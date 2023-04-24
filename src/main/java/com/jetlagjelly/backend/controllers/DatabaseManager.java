package com.jetlagjelly.backend.controllers;

import static com.mongodb.client.model.Filters.eq;

import com.jetlagjelly.backend.models.MeetingContraint;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

        DB_URL = "mongodb://" + MONGODB_USER + ":" + MONGODB_PASSWORD + "@" + MONGODB_HOSTNAME + ":"
                + MONGODB_LOCAL_PORT + "/";

        client = MongoClients.create(DB_URL);
        db = client.getDatabase(MONGODB_DATABASE);
        collection = db.getCollection("users");
    }

    public static void main(String[] args) {

        List<String> sca = new ArrayList<>();
        sca.add("create");
        List<String> cida = new ArrayList<>();
        cida.add("Phases of the Moon");
        List<Integer> dya = new ArrayList<>();
        dya.add(1);
        dya.add(5);
        List<Integer> sd = new ArrayList<>();
        sd.add(2);
        List<Integer> sta = new ArrayList<>();
        sta.add(300);
        sta.add(1200);
        List<Integer> ena = new ArrayList<>();
        ena.add(400);
        ena.add(2000);
        List<Integer> ss = new ArrayList<>();
        ss.add(200);
        List<Integer> se = new ArrayList<>();
        se.add(800);

        User user = new User("bmclean2@oswego.edu", "MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3",
                "IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk", 3600, sca, "Bearer", "America/New_York", cida, sta, ena, dya, ss,
                se, sd);

        Document document;
        // document = newUser(user);
        // collection.insertOne(document);

        // document = fetchUser(collection, "bmclean2@oswego.edu");
        // System.out.println(document.get("calendar_id"));

        // document = meetingMgr(collection, user);
        // System.out.println(document);

        // deleteUser(collection, user);

        // setTimezone(user, "Europe/Malta");
        // document = newUser(user);
        // deleteUser(collection, user);
        // collection.insertOne(document);

        // System.out.println(concreteTime(user, mc));

        System.out.println(DB_URL);
    }

    public static final class User {
        public String email;
        public String access_token;
        public String refresh_token;
        public int expires_at;
        public List<String> scope;
        public String token_type;
        public String timezone;
        public List<String> calendar_id;
        public List<Integer> start;
        public List<Integer> end;
        public List<Integer> days;
        public List<Integer> substart;
        public List<Integer> subend;
        public List<Integer> subdays;

        public User(String em, String a, String r, int ex, List<String> sc, String tt, String t, List<String> cid,
                List<Integer> s, List<Integer> e, List<Integer> d, List<Integer> ss, List<Integer> se,
                List<Integer> sd) {
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

    public static Document newUser(User user) {
        Document tr = new Document().append("start", user.start).append("end", user.end).append("days", user.days);
        Document st = new Document().append("suboptimal_start", user.substart).append("suboptimal_end", user.subend)
                .append("suboptimal_days", user.subdays);
        Document sampleDoc = new Document("email", user.email).append("access_token", user.access_token)
                .append("refresh_token", user.refresh_token).append("expires_at", user.expires_at)
                .append("scope", user.scope).append("token_type", user.token_type).append("timezone", user.timezone)
                .append("calendar_id", user.calendar_id).append("preferred_timerange", tr)
                .append("suboptimal_timerange", st);
        return sampleDoc;
    }

    public static Document meetingMgr(MongoCollection collection, User user) {

        Bson projectionFields = Projections.fields(
                Projections.include("email", "timezone", "calendar_id", "preferred_timerange", "start", "end", "days",
                        "suboptimal_timerange", "suboptimal_start", "suboptimal_end", "suboptimal_days"),
                Projections.excludeId());
        Document doc = (Document) collection.find(eq("email", user.email)).projection(projectionFields).first();
        return doc;

    }

    public static Document fetchUser(MongoCollection collection, String email) {

        Bson projectionFields = Projections.fields(
                Projections.include("email", "access_token", "refresh_token", "expires_at", "scope", "token_type",
                        "timezone", "calendar_id", "preferred_timerange", "start", "end", "days",
                        "suboptimal_timerange", "suboptimal_start", "suboptimal_end", "suboptimal_days"),
                Projections.excludeId());
        Document doc = (Document) collection.find(eq("email", email)).projection(projectionFields).first();
        if (doc == null) {
            sendEmail(email);
        }
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

    public static void setTimezone(User user, String tz) {
        user.timezone = tz;
    }

    public static void updateTokens(User user, String access_token, int expires_at, String refresh_token,
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

    public static void addTimeRange(User user, Integer start, Integer end, Integer day) { // need parameter for day of
                                                                                          // the week (slot in the list
                                                                                          // (or rather array[7]?)?)?
        user.start.add(start);
        user.end.add(end);
        user.days.add(day);
    }

    public static void addSuboptimalTimes(User user, Integer start, Integer end, Integer day) {
        user.substart.add(start);
        user.subend.add(end);
        user.subdays.add(day);
    }

    public static void sendEmail(String recipient) {

        final String username = "jetlagjelly@gmail.com";
        final String password = "cyorbvwieztktuly";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
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

    public static List<Long> concreteTime(User user, MeetingContraint mc) {
        List<Long> ranges = new ArrayList<>();
        List<DayOfWeek> day = new ArrayList<>();
        List<DayOfWeek> unusedDay = new ArrayList<>();
        List<DayOfWeek> dbDay = new ArrayList<>();

        for (int i = 0; i < user.days.size(); i++) {
            day.add(DayOfWeek.of(user.days.get(i)));
            dbDay.add(DayOfWeek.of(user.days.get(i)));
            LocalDateTime start = getNextClosestDateTime(dbDay, user.start.get(i), mc.getStartDay(), user);
            LocalDateTime end = getNextClosestDateTime(dbDay, user.end.get(i), mc.getStartDay(), user);
            ZonedDateTime zdtstart = ZonedDateTime.of(start, ZoneId.of(user.timezone));
            long startTime = zdtstart.toInstant().toEpochMilli();

            ZonedDateTime zdtend = ZonedDateTime.of(end, ZoneId.of(user.timezone));
            long endTime = zdtend.toInstant().toEpochMilli();

            ranges.add(startTime);
            ranges.add(endTime);
            dbDay.remove(DayOfWeek.of(user.days.get(i)));
        }

        for (int i = 1; i <= 5; i++) {
            if (!day.contains(DayOfWeek.of(i))) {
                day.add(DayOfWeek.of(i));
                unusedDay.add(DayOfWeek.of(i));
                LocalDateTime start = getNextClosestDateTime(unusedDay, 900, mc.getStartDay(), user);
                LocalDateTime end = getNextClosestDateTime(unusedDay, 1700, mc.getStartDay(), user);
                ZonedDateTime zdtstart = ZonedDateTime.of(start, ZoneId.of(user.timezone));
                long startTime = zdtstart.toInstant().toEpochMilli();

                ZonedDateTime zdtend = ZonedDateTime.of(end, ZoneId.of(user.timezone));
                long endTime = zdtend.toInstant().toEpochMilli();

                ranges.add(startTime);
                ranges.add(endTime);
                unusedDay.remove(DayOfWeek.of(i));
            }
        }

        Collections.sort(ranges);
        return ranges;
    }

    public static List<Long> concreteSubTime(User user, MeetingContraint mc) {
        List<Long> subranges = new ArrayList<>();
        List<DayOfWeek> subday = new ArrayList<>();
        List<DayOfWeek> subunusedDay = new ArrayList<>();
        List<DayOfWeek> subdbDay = new ArrayList<>();

        for (int i = 0; i < user.subdays.size(); i++) {
            subday.add(DayOfWeek.of(user.subdays.get(i)));
            subdbDay.add(DayOfWeek.of(user.subdays.get(i)));
            LocalDateTime start = getNextClosestDateTime(subdbDay, user.substart.get(i), mc.getStartDay(), user);
            LocalDateTime end = getNextClosestDateTime(subdbDay, user.subend.get(i), mc.getStartDay(), user);
            ZonedDateTime zdtstart = ZonedDateTime.of(start, ZoneId.of(user.timezone));
            long startTime = zdtstart.toInstant().toEpochMilli();

            ZonedDateTime zdtend = ZonedDateTime.of(end, ZoneId.of(user.timezone));
            long endTime = zdtend.toInstant().toEpochMilli();

            subranges.add(startTime);
            subranges.add(endTime);
            subdbDay.remove(DayOfWeek.of(user.subdays.get(i)));
        }

        for (int i = 1; i <= 5; i++) {
            if (!subday.contains(DayOfWeek.of(i))) {
                subday.add(DayOfWeek.of(i));
                subunusedDay.add(DayOfWeek.of(i));
                LocalDateTime start = getNextClosestDateTime(subunusedDay, 900, mc.getStartDay(), user);
                LocalDateTime end = getNextClosestDateTime(subunusedDay, 1700, mc.getStartDay(), user);
                ZonedDateTime zdtstart = ZonedDateTime.of(start, ZoneId.of(user.timezone));
                long startTime = zdtstart.toInstant().toEpochMilli();

                ZonedDateTime zdtend = ZonedDateTime.of(end, ZoneId.of(user.timezone));
                long endTime = zdtend.toInstant().toEpochMilli();

                subranges.add(startTime);
                subranges.add(endTime);
                subunusedDay.remove(DayOfWeek.of(i));
            }
        }
        Collections.sort(subranges);
        return subranges;
    }

    public static LocalDateTime getNextClosestDateTime(
            List<DayOfWeek> daysOfWeek, int hour, long meetingStartTime, User user)
            throws IllegalArgumentException {
        if (daysOfWeek.isEmpty()) {
            throw new IllegalArgumentException("daysOfWeek should not be empty.");
        }

        String hours = Integer.toString(hour);
        int timeHours = 0;
        int mins = 0;
        if (hours.length() == 4) {
            timeHours = Integer.parseInt(hours.substring(0, 2));
            mins = Integer.parseInt(hours.substring(2));
        } else {
            timeHours = Integer.parseInt(hours.substring(0, 1));
            mins = Integer.parseInt(hours.substring(1));
        }

        final LocalDateTime dateNow = LocalDateTime.ofInstant(Instant.ofEpochMilli(meetingStartTime),
                ZoneId.of(user.timezone));
        final LocalDateTime dateNowWithDifferentTime = dateNow.withHour(timeHours).withMinute(mins).withSecond(0)
                .withNano(0);

        return daysOfWeek
                .stream()
                .map(
                        d -> dateNowWithDifferentTime.with(TemporalAdjusters.nextOrSame(d)))
                .filter(d -> d.isAfter(dateNow))
                .min(Comparator.naturalOrder())
                .orElse(
                        dateNowWithDifferentTime.with(TemporalAdjusters.next(daysOfWeek
                                .get(0))));
    }
}
