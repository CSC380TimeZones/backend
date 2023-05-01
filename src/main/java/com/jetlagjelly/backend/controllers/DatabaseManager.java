package com.jetlagjelly.backend.controllers;

import static com.mongodb.client.model.Filters.eq;

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

  public static final class currentUser {
    public String email;
    public double timezone;
    public List<String> calendar_id;
    public List<Double> start;
    public List<Double> end;
    public List<List<Boolean>> days;
    public List<Double> substart;
    public List<Double> subend;
    public List<List<Boolean>> subdays;

    public currentUser(String em, double t, List<String> cid, List<Double> s,
        List<Double> e, List<List<Boolean>> d, List<Double> ss,
        List<Double> se, List<List<Boolean>> sd) {
      email = em;
      timezone = t;
      calendar_id = cid;
      start = s;
      end = e;
      days = d;
      substart = ss;
      subend = se;
      subdays = sd;
    }
  }

  public Document newUser(User user) {
    Document times = new Document()
        .append("start", user.start)
        .append("end", user.end)
        .append("days", user.days);
    Document subtimes = new Document()
        .append("suboptimal_start", user.substart)
        .append("suboptimal_end", user.subend)
        .append("suboptimal_days", user.subdays);
    Document userDoc = new Document("email", user.email)
        .append("access_token", user.access_token)
        .append("refresh_token", user.refresh_token)
        .append("expires_at", user.expires_at)
        .append("scope", user.scope)
        .append("token_type", user.token_type)
        .append("timezone", user.timezone)
        .append("calendar_id", user.calendar_id)
        .append("preferred_timerange", times)
        .append("suboptimal_timerange", subtimes);

    collection.insertOne(userDoc);
    return userDoc;
  }

  public static Document newcurrentUser(currentUser user) {
    Document tr = new Document()
        .append("start", user.start)
        .append("end", user.end)
        .append("days", user.days);
    Document st = new Document()
        .append("suboptimal_start", user.substart)
        .append("suboptimal_end", user.subend)
        .append("suboptimal_days", user.subdays);
    Document sampleDoc = new Document("email", user.email)
        .append("timezone", user.timezone)
        .append("calendar_id", user.calendar_id)
        .append("preferred_timerange", tr)
        .append("suboptimal_timerange", st);
    return sampleDoc;
  }

  public Document fetchUser(String email) {

    Bson projectionFields = Projections.fields(
        Projections.include(
            "email", "access_token", "refresh_token", "expires_at", "scope",
            "token_type", "timezone", "calendar_id", "preferred_timerange",
            "start", "end", "days", "suboptimal_timerange", "suboptimal_start",
            "suboptimal_end", "suboptimal_days"),
        Projections.excludeId());
    Document doc = (Document) collection.find(eq("email", email))
        .projection(projectionFields)
        .first();
    if (doc == null) {
      sendEmail(email);
    }
    return doc;
  }

  public static Document fetchCurrentUser(MongoCollection collection,
      String email) {
    Bson projectionFields = Projections.fields(
        Projections.include("email", "timezone", "calendar_id",
            "preferred_timerange", "start", "end", "days",
            "suboptimal_timerange", "suboptimal_start",
            "suboptimal_end", "suboptimal_days"),
        Projections.excludeId());
    Document doc = (Document) collection.find(eq("email", email))
        .projection(projectionFields)
        .first();
    return doc;
  }

  public void deleteUser(User user) {
    Bson query = eq("email", user.email);
    collection.deleteOne(query);
  }

  public void setTimezone(String email, double tz) {
    Document query = new Document("email", email);
    Document update = new Document("$set", new Document("timezone", tz));
    collection.updateOne(query, update);
  }

  public static void addCalendar(User user, String id) {
    user.calendar_id.add(id);
  }

  public static void addTimeRange(User user, double start, double end,
      List<Boolean> day) { // need parameter for day of
    // the week (slot in the list
    // (or rather array[7]?)?)?
    user.start.add(start);
    user.end.add(end);
    user.days.add(day);
  }

  public static void addSuboptimalTimes(User user, double start, double end,
      List<Boolean> day) {
    user.substart.add(start);
    user.subend.add(end);
    user.subdays.add(day);
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

  public static List<Integer> getTimeRangeDays(List<Boolean> tr) {
    List<Integer> usedDays = new ArrayList<>();
    for (int i = 0; i < tr.size(); i++) {
      if (tr.get(i).equals(true)) {
        usedDays.add(((i + 6) % 7) + 1);
      }
    }
    return usedDays;
  }

  public static List<Long> concreteTime(User user, MeetingContraint mc, String type, int weekAdvance) {
    List<Long> ranges = new ArrayList<>();
    List<DayOfWeek> day = new ArrayList<>();
    List<DayOfWeek> unusedDay = new ArrayList<>();
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
          day.add(DayOfWeek.of(usedDays.get(i)));
          dbDay.add(DayOfWeek.of(usedDays.get(i)));
          LocalDateTime start = getNextClosestDateTime(
              dbDay, user.substart.get(j), mc.getStartDay(), user, weekAdvance);
          LocalDateTime end = getNextClosestDateTime(dbDay, user.subend.get(j),
              mc.getStartDay(), user, weekAdvance);
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

  public static ArrayList<Interval> merge(ArrayList<Interval> intervals) {

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

  static class Interval {
    private Long start;
    private Long end;

    Interval() {
      start = 0L;
      end = 0L;
    }

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
