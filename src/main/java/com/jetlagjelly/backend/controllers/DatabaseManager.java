package com.jetlagjelly.backend.controllers;

import static com.mongodb.client.model.Filters.eq;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.jetlagjelly.backend.models.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import io.github.cdimascio.dotenv.Dotenv;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import org.bson.Document;
import org.bson.conversions.Bson;

public class DatabaseManager {

  static Dotenv dotenv = Dotenv.load();
  public static String MONGODB_USER;
  public static String MONGODB_PASSWORD;
  public static String MONGODB_DATABASE;
  public static String MONGODB_LOCAL_PORT;
  public static String MONGODB_HOSTNAME;

  public static String DB_URL;
  public static MongoClient client;
  public static MongoDatabase db;
  public static MongoCollection<Document> collection;

  public DatabaseManager() {
    MONGODB_USER = dotenv.get("MONGODB_USER");
    MONGODB_PASSWORD = dotenv.get("MONGODB_PASSWORD");
    MONGODB_DATABASE = dotenv.get("MONGODB_DATABASE");
    MONGODB_LOCAL_PORT = dotenv.get("MONGODB_LOCAL_PORT");
    MONGODB_HOSTNAME = dotenv.get("MONGODB_HOSTNAME");

    DB_URL = "mongodb://" + MONGODB_USER + ":" + MONGODB_PASSWORD + "@" +
        MONGODB_HOSTNAME + ":" + MONGODB_LOCAL_PORT + "/";

    client = MongoClients.create(DB_URL);
    db = client.getDatabase(MONGODB_DATABASE);
    collection = db.getCollection("users");
  }

  public Document newUser(String email, GoogleTokenResponse tokenResponse) {

    // Default Configuration
    String scopeUrl = "https://www.googleapis.com/auth/";
    List<String> scope = Arrays.asList(scopeUrl + "userinfo.email", scopeUrl + "calendar");
    List<Double> p_start = Arrays.asList(9d);
    List<Double> p_end = Arrays.asList(17d);
    List<List<Boolean>> p_days = Arrays.asList(Arrays.asList(false, true, true, true, true, true, false));
    List<Double> s_start = Arrays.asList(16d);
    List<Double> s_end = Arrays.asList(19d);
    List<List<Boolean>> s_days = Arrays.asList(Arrays.asList(true, false, false, false, false, false, true));

    Document newUser = new Document("email", email)
        .append("access_token", tokenResponse.getAccessToken())
        .append("refresh_token", tokenResponse.getRefreshToken())
        .append("expires_at", tokenResponse.getExpiresInSeconds())
        .append("scope", scope)
        .append("token_type", tokenResponse.getTokenType()).append("timezone", "0")
        .append("calendar_id", Arrays.asList(email))
        .append("preferred_timerange", new Document("start", p_start)
            .append("end", p_end)
            .append("days", p_days))
        .append("suboptimal_timerange",
            new Document("start", s_start)
                .append("end", s_end)
                .append("days", s_days));
    collection.insertOne(newUser);
    return newUser;
  }

  /**
   * Updates user token using User email
   * 
   * @param user
   * @param tokenResponse
   */
  public void updateUserToken(String email, GoogleTokenResponse tokenResponse) {
    Document user = fetchUser(email, true);

    String scopeUrl = "https://www.googleapis.com/auth/";
    List<String> scope = Arrays.asList(scopeUrl + "userinfo.email", scopeUrl + "calendar");

    Document update = new Document("email", user.get("email"))
        .append("access_token", tokenResponse.getAccessToken())
        .append("expires_at", System.currentTimeMillis() + tokenResponse.getExpiresInSeconds() * 1000)
        .append("scope", scope)
        .append("token_type", tokenResponse.getTokenType());

    collection.updateOne(user, new Document("$set", update));
  }

  public Document fetchUser(String email, boolean includeOauth) {
    ArrayList<String> fieldsList = new ArrayList<String>(
        Arrays.asList("email", "timezone", "calendar_id", "preferred_timerange",
            "start", "end", "days", "suboptimal_timerange"));

    if (includeOauth) {
      List<String> oauthList = Arrays.asList("access_token", "refresh_token", "expires_at", "scope",
          "token_type");
      fieldsList.addAll(oauthList);
    }

    Bson projectionFields = Projections.fields(
        Projections.include(fieldsList),
        Projections.excludeId());
    Document doc = (Document) collection.find(eq("email", email))
        .projection(projectionFields)
        .first();
    return doc;
  }

  /**
   * Grabs a user from the databse and returns it as an object of class User
   * 
   * @param email
   * @param includeOauth
   * @return
   */
  public User fetchUserAsUserObject(String email, boolean includeOauth) {
    Document user = fetchUser(email, includeOauth);

    if (user == null)
      return null;

    Document pt = user.get("preferred_timerange", Document.class);
    Document st = user.get("suboptimal_timerange", Document.class);

    List<String> scope = user.getList("scope", String.class);
    List<String> calendarId = user.getList("calendar_id", String.class);

    List<Double> pStart = pt.getList("start", Double.class);
    List<Double> pEnd = pt.getList("end", Double.class);
    List<List<Boolean>> pDays = (List<List<Boolean>>) pt.get("days");

    List<Double> sStart = st.getList("start", Double.class);
    List<Double> sEnd = st.getList("end", Double.class);
    List<List<Boolean>> sDays = (List<List<Boolean>>) pt.get("days");

    User userObject = new User(
        email,
        user.getString("access_token"), user.getString("refresh_token"),
        user.getLong("expires_at"), scope, user.getString("token_type"),
        Double.valueOf(user.getString("timezone")), calendarId,
        pStart, pEnd, pDays,
        sStart, sEnd, sDays);

    return userObject;
  }

  public void deleteUser(User user) {
    Bson query = eq("email", user.email);
    collection.deleteOne(query);
  }

  public void setTimezone(String email, String tz) {
    Document query = new Document("email", email);
    Document update = new Document("$set", new Document("timezone", tz));
    collection.updateOne(query, update);
  }

  /**
   * Adds a new calendar to the array if used is true, otherwise removes it
   * 
   * @param email
   * @param calendar_id
   * @param used
   */
  public void toggleCalendar(String email, String calendar_id, boolean used) {

    Document query = new Document("email", email);
    Document update = new Document();

    if (used) {
      update = new Document("$push", new Document("calendar_id", calendar_id));
    } else {
      update = new Document("$pull", new Document("calendar_id", calendar_id));
    }

    collection.updateOne(query, update);
  }

  public void addTimeRange(
      String email,
      String type,
      double start,
      double end,
      List<Boolean> days) {
    Document query = new Document("email", email);
    String whichRange = type + "_timerange";
    Document update = new Document("$push", new Document(whichRange + ".start", start)
        .append(whichRange + ".end", end)
        .append(whichRange + ".days", days));
    collection.updateOne(query, update);
  }

  public void updateTimeRange(
      String email,
      String type,
      int index,
      double start,
      double end,
      List<Boolean> days) {
    String whichRange = type + "_timerange";

    Document query = new Document("email", email);
    Document update = new Document("$set",
        new Document(whichRange + ".start" + "." + index, start)
            .append(whichRange + ".end" + "." + index, end)
            .append(whichRange + ".days" + "." + index, days));
    collection.updateOne(query, update);
  }

  public void deleteTimeRange(
      String email,
      String type,
      int index) {

    String whichRange = type + "_timerange";

    Document query = new Document("email", email);
    Document updatestart = new Document("$unset", new Document(whichRange + ".start." + index, null));
    Document updateend = new Document("$unset", new Document(whichRange + ".end." + index, null));
    Document updatedays = new Document("$unset", new Document(whichRange + ".days." + index, null));
    collection.updateOne(query, updatestart);
    collection.updateOne(query, updateend);
    collection.updateOne(query, updatedays);

    Document removeNullStart = new Document("$pull", new Document(whichRange + ".start", null));
    Document removeNullEnd = new Document("$pull", new Document(whichRange + ".end", null));
    Document removeNullDays = new Document("$pull", new Document(whichRange + ".days", null));
    collection.updateOne(query, removeNullStart);
    collection.updateOne(query, removeNullEnd);
    collection.updateOne(query, removeNullDays);
  }

  public static void sendEmail(String recipient) {

    final String username = "jetlagjelly@gmail.com";
    final String password = System.getenv("ACCOUNT_PASSWORD");

    Properties prop = new Properties();
    prop.put("mail.smtp.host", "smtp.gmail.com");
    prop.put("mail.smtp.port", "587");
    prop.put("mail.smtp.auth", "true");
    prop.put("mail.smtp.starttls.enable", "true");

    Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });

    try {

      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(username));
      message.setRecipients(Message.RecipientType.TO,
          InternetAddress.parse(recipient));
      message.setSubject("You Have Been Invited To Join JetLagJelly!");
      message.setText("Click this link to join JLJ today:"
          + "\n\n https://github.com/CSC380TimeZones/backend"
          + "\n\n From,"
          + "\n The JetLagJelly Team");

      Transport.send(message);

      System.out.println("Done");

    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }

  public static List<Long> concreteTime(User user, MeetingContraint mc, String type, int weekAdvance) {
    List<Long> ranges = new ArrayList<>();
    List<DayOfWeek> day = new ArrayList<>();
    List<DayOfWeek> dbDay = new ArrayList<>();
    List<Integer> usedDays = new ArrayList<>();
    if (type.equals("preferred")) {
      for (int j = 0; j < user.days.size(); j++) {
        usedDays = getTimeRangeDays(user.days.get(j));
        for (int i = 0; i < usedDays.size(); i++) {
          for (int y = 0; y < weekAdvance; y++) {
            day.add(DayOfWeek.of(usedDays.get(i)));
            dbDay.add(DayOfWeek.of(usedDays.get(i)));
            LocalDateTime start = getNextClosestDateTime(dbDay, user.start.get(j),
                mc.getStartDay(), user, y);
            LocalDateTime end = getNextClosestDateTime(dbDay, user.end.get(j),
                mc.getStartDay(), user, y);
            ZonedDateTime zdtstart = ZonedDateTime.of(
                start, ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds((int) (user.timezone * 3600))));
            long startTime = zdtstart.toInstant().toEpochMilli();

            ZonedDateTime zdtend = ZonedDateTime.of(
                end, ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds((int) (user.timezone * 3600))));
            long endTime = zdtend.toInstant().toEpochMilli();

            ranges.add(startTime);
            ranges.add(endTime);
            dbDay.remove(DayOfWeek.of(usedDays.get(i)));
          }
        }
      }
    } else {
      for (int j = 0; j < user.subdays.size(); j++) {
        usedDays = getTimeRangeDays(user.subdays.get(j));
        for (int i = 0; i < usedDays.size(); i++) {
          for (int y = 0; y < weekAdvance; y++) {

            day.add(DayOfWeek.of(usedDays.get(i)));
            dbDay.add(DayOfWeek.of(usedDays.get(i)));
            LocalDateTime start = getNextClosestDateTime(
                dbDay, user.substart.get(j), mc.getStartDay(), user, y);
            LocalDateTime end = getNextClosestDateTime(dbDay, user.subend.get(j),
                mc.getStartDay(), user, y);
            ZonedDateTime zdtstart = ZonedDateTime.of(
                start, ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds((int) (user.timezone * 3600))));
            long startTime = zdtstart.toInstant().toEpochMilli();

            ZonedDateTime zdtend = ZonedDateTime.of(
                end, ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds((int) (user.timezone * 3600))));
            long endTime = zdtend.toInstant().toEpochMilli();

            ranges.add(startTime);
            ranges.add(endTime);
            dbDay.remove(DayOfWeek.of(usedDays.get(i)));
          }
        }
      }
    }
    ArrayList<Interval> intervals = new ArrayList<>();
    ArrayList<Long> blockRanges = new ArrayList<>();

    for (int i = 0; i < ranges.size(); i = i + 2) {
      Long a = ranges.get(i);
      Long b = ranges.get(i + 1);
      intervals.add(new Interval(a, b));
    }
    intervals = merge(intervals);

    for (Interval i : intervals) {
      blockRanges.add(i.getStart());
      blockRanges.add(i.getEnd());
    }

    Collections.sort(blockRanges);
    return blockRanges;
  }

  private static List<Integer> getTimeRangeDays(List<Boolean> tr) {
    List<Integer> usedDays = new ArrayList<>();
    for (int i = 0; i < tr.size(); i++) {
      if (tr.get(i).equals(true)) {
        usedDays.add(((i + 6) % 7) + 1);
      }
    }
    return usedDays;
  }

  private static ArrayList<Interval> merge(ArrayList<Interval> intervals) {

    if (intervals.size() == 0 || intervals.size() == 1)
      return intervals;

    Collections.sort(intervals, new IntervalComparator());

    Interval first = intervals.get(0);
    Long start = first.getStart();
    Long end = first.getEnd();

    ArrayList<Interval> result = new ArrayList<Interval>();

    for (int i = 1; i < intervals.size(); i++) {
      Interval current = intervals.get(i);
      if (current.getStart() <= end) {
        end = Math.max(current.getEnd(), end);
      } else {
        result.add(new Interval(start, end));
        start = current.getStart();
        end = current.getEnd();
      }
    }

    result.add(new Interval(start, end));
    return result;
  }

  private static class Interval {
    private Long start;
    private Long end;

    Interval(Long s, Long e) {
      start = s;
      end = e;
    }

    public Long getStart() {
      return start;
    }

    public Long getEnd() {
      return end;
    }
  }

  static class IntervalComparator implements Comparator<Interval> {
    public int compare(Interval i1, Interval i2) {
      return (int) (i1.getStart() - i2.getStart());
    }
  }

  public static LocalDateTime getNextClosestDateTime(List<DayOfWeek> daysOfWeek, double hour,
      long meetingStartTime, User user, int weekAdvance)
      throws IllegalArgumentException {
    if (daysOfWeek.isEmpty()) {
      throw new IllegalArgumentException("daysOfWeek should not be empty.");
    }

    String hours = Double.toString(hour);

    int timeHours = Integer.parseInt(hours.substring(0, hours.indexOf(".")));
    double mins = Double.parseDouble(hours.substring(hours.indexOf(".")));
    int minutes = (int) (mins * 60);

    final LocalDateTime dateNow = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(meetingStartTime),
        ZoneId.ofOffset("UTC",
            ZoneOffset.ofTotalSeconds((int) (user.timezone * 3600))));
    final LocalDateTime dateNowWithDifferentTime = dateNow.withHour(timeHours).withMinute(minutes).withSecond(0)
        .withNano(0).plusWeeks(weekAdvance);

    return daysOfWeek.stream()
        .map(
            d -> dateNowWithDifferentTime.with(TemporalAdjusters.nextOrSame(d)))
        .filter(d -> d.isAfter(dateNow))
        .min(Comparator.naturalOrder())
        .orElse(dateNowWithDifferentTime.with(
            TemporalAdjusters.next(daysOfWeek.get(0))));
  }
}
