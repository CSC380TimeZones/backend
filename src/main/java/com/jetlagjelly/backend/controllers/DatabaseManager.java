package com.jetlagjelly.backend.controllers;

import static com.mongodb.client.model.Filters.eq;

import com.jetlagjelly.backend.models.MeetingContraint;
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
  public static MongoCollection collection;

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

  public static void main(String[] args) {

    List<String> sca = new ArrayList<>();
    sca.add("create");
    List<String> cida = new ArrayList<>();
    cida.add("Phases of the Moon");
    List<List<Boolean>> dyya = new ArrayList<>();
    List<Boolean> dya = new ArrayList<>();
    dya.add(0, true);
    dya.add(1, false);
    dya.add(2, false);
    dya.add(3, false);
    dya.add(4, false);
    dya.add(5, false);
    dya.add(6, false);
    List<Boolean> dyaa = new ArrayList<>();
    dya.add(0, false);
    dya.add(1, false);
    dya.add(2, false);
    dya.add(3, false);
    dya.add(4, true);
    dya.add(5, false);
    dya.add(6, false);
    dyya.add(0, dya);
    dyya.add(1, dyaa);

    List<List<Boolean>> sda = new ArrayList<>();
    List<Boolean> sd = new ArrayList<>();
    sd.add(0, false);
    sd.add(1, true);
    sd.add(2, false);
    sd.add(3, false);
    sd.add(4, false);
    sd.add(5, false);
    sd.add(6, false);
    sda.add(sd);
    List<Double> sta = new ArrayList<>();
    sta.add(3.00);
    sta.add(12.00);
    List<Double> ena = new ArrayList<>();
    ena.add(4.00);
    ena.add(20.00);
    List<Double> ss = new ArrayList<>();
    ss.add(2.00);
    List<Double> se = new ArrayList<>();
    se.add(8.00);

    User user = new User("bmclean2@oswego.edu", "MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3",
        "IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk", 3600L, sca, "Bearer", -5,
        cida, sta, ena, dyya, ss, se, sda);

    // Document document;
    // document = newUser(user);
    // collection.insertOne(document);

    // document = fetchUser(collection, "bmclean2@oswego.edu");
    // System.out.println(document.get("calendar_id"));

    // document = meetingMgr(collection, user);
    // System.out.println(document);

    // deleteUser(collection, user);

    // setTimezone(user, "-8");
    // document = newUser(user);
    // deleteUser(collection, user);
    // collection.insertOne(document);

    // System.out.println(concreteTime(user, mc));

    // System.out.println(DB_URL);
  }

  public static final class User {
    public String email;
    public String access_token;
    public String refresh_token;
    public Long expires_at;
    public List<String> scope;
    public String token_type;
    public double timezone;
    public List<String> calendar_id;
    public List<Double> start;
    public List<Double> end;
    public List<List<Boolean>> days;
    public List<Double> substart;
    public List<Double> subend;
    public List<List<Boolean>> subdays;

    public User(String em, String a, String r, Long ex, List<String> sc,
        String tt, double t, List<String> cid, List<Double> s,
        List<Double> e, List<List<Boolean>> d, List<Double> ss,
        List<Double> se, List<List<Boolean>> sd) {
      email = em;
      access_token = a;
      refresh_token = r;
      expires_at = ex;
      scope = sc;
      token_type = tt;
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

  public static Document newUser(User user) {
    Document tr = new Document()
        .append("start", user.start)
        .append("end", user.end)
        .append("days", user.days);
    Document st = new Document()
        .append("suboptimal_start", user.substart)
        .append("suboptimal_end", user.subend)
        .append("suboptimal_days", user.subdays);
    Document sampleDoc = new Document("email", user.email)
        .append("access_token", user.access_token)
        .append("refresh_token", user.refresh_token)
        .append("expires_at", user.expires_at)
        .append("scope", user.scope)
        .append("token_type", user.token_type)
        .append("timezone", user.timezone)
        .append("calendar_id", user.calendar_id)
        .append("preferred_timerange", tr)
        .append("suboptimal_timerange", st);
    return sampleDoc;
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

  public static Document meetingMgr(MongoCollection collection, User user) {

    Bson projectionFields = Projections.fields(
        Projections.include("email", "timezone", "calendar_id",
            "preferred_timerange", "start", "end", "days",
            "suboptimal_timerange", "suboptimal_start",
            "suboptimal_end", "suboptimal_days"),
        Projections.excludeId());
    Document doc = (Document) collection.find(eq("email", user.email))
        .projection(projectionFields)
        .first();
    return doc;
  }

  public static Document fetchUser(MongoCollection collection, String email) {

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

  public static void deleteUser(MongoCollection collection, User user) {

    Bson query = eq("email", user.email);
    collection.deleteOne(query);
  }

  public static Document tokens(User user) {
    Document accessToken = new Document()
        .append("token", user.access_token)
        .append("type", user.token_type)
        .append("expires_at", user.expires_at)
        .append("scope", user.scope)
        .append("refreshToken", user.refresh_token);

    return accessToken;
  }

  public static void setTimezone(User user, double tz) {
    user.timezone = tz;
  }

  public static void updateTokens(User user, String access_token,
      Long expires_at, String refresh_token,
      List<String> scope, String token_type) {
    user.access_token = access_token;
    user.expires_at = expires_at;
    user.refresh_token = refresh_token;
    user.scope = scope;
    user.token_type = token_type;
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
          day.add(DayOfWeek.of(usedDays.get(i)));
          dbDay.add(DayOfWeek.of(usedDays.get(i)));
          LocalDateTime start = getNextClosestDateTime(dbDay, user.start.get(j),
              mc.getStartDay(), user, weekAdvance);
          LocalDateTime end = getNextClosestDateTime(dbDay, user.end.get(j),
              mc.getStartDay(), user, weekAdvance);
          ZonedDateTime zdtstart = ZonedDateTime.of(
              start, ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds((int) (user.timezone * 360))));
          long startTime = zdtstart.toInstant().toEpochMilli();

          ZonedDateTime zdtend = ZonedDateTime.of(
              end, ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds((int) (user.timezone * 360))));
          long endTime = zdtend.toInstant().toEpochMilli();

          ranges.add(startTime);
          ranges.add(endTime);
          dbDay.remove(DayOfWeek.of(usedDays.get(i)));
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
              start, ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds((int) (user.timezone * 360))));
          long startTime = zdtstart.toInstant().toEpochMilli();

          ZonedDateTime zdtend = ZonedDateTime.of(
              end, ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds((int) (user.timezone * 360))));
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
            ZoneOffset.ofTotalSeconds((int) (user.timezone * 360))));
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
