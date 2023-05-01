package com.jetlagjelly.backend;

import static com.jetlagjelly.backend.CalendarQuickstart.events;
import static com.jetlagjelly.backend.controllers.DatabaseManager.*;
import com.jetlagjelly.backend.models.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;
import com.jetlagjelly.backend.controllers.AuthorizationManager;
import com.jetlagjelly.backend.controllers.DatabaseManager;
import com.jetlagjelly.backend.controllers.MeetingManager;
import com.jetlagjelly.backend.models.MeetingContraint;
import com.jetlagjelly.backend.models.MeetingTimes;
import com.mongodb.client.MongoCollection;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class Endpoints {

  public static DatabaseManager db = new DatabaseManager();
  public static Dotenv dotenv = Dotenv.load();

  @RequestMapping(method = RequestMethod.GET, value = "/email")
  public static MeetingTimes getMeetingConstraints(
      @RequestParam(value = "email", defaultValue = "No email found!") String email,
      @RequestParam(value = "mtngLength", defaultValue = "60") int mtngLength,
      @RequestParam(value = "startDay", defaultValue = "100000000000") Long startDay,
      @RequestParam(value = "endDay", defaultValue = "1000000000") Long endDay)
      throws GeneralSecurityException, IOException {

    MeetingContraint mc = new MeetingContraint();

    mc.setEmail(email);
    mc.setMtngLength(mtngLength);
    mc.setStartDay(startDay);
    mc.setEndDay(endDay);

    LocalDateTime startdate = LocalDateTime.ofInstant(Instant.ofEpochMilli(startDay), TimeZone
        .getDefault().toZoneId());

    LocalDateTime enddate = LocalDateTime.ofInstant(Instant.ofEpochMilli(endDay), TimeZone
        .getDefault().toZoneId());
    int j = 0;
    for (int i = 0; i < 52; i++) {
      if (startdate.plusWeeks(i).isAfter(enddate)) {
        j = i;
      }
    }

    MeetingManager mm = new MeetingManager();
    String ls = mc.getEmail();
    ArrayList<String> emailList = new ArrayList<>();
    String[] emailArray = ls.split(" ");
    Collections.addAll(emailList, emailArray);
    ArrayList<String> notFound = new ArrayList<>();
    MeetingTimes mt = new MeetingTimes();

    for (String s : emailList) {
      Document d = db.fetchUser(s);

      // Add user to not found list if email is not in database, and skip email
      if (d == null) {
        notFound.add(s);
        continue;
      }
      Document pt = (Document) d.get("preferred_timerange");
      Document st = (Document) d.get("suboptimal_timerange");
      User user = new User(
          s, d.getString("access_token"), d.getString("refresh_token"),
          d.getLong("expires_at"), (List<String>) d.get("scope"),
          d.getString("token_type"), Double.valueOf(d.getString("timezone")),
          (List<String>) d.get("calendar_id"), (List<Double>) pt.get("start"),
          (List<Double>) pt.get("end"), (List<List<Boolean>>) pt.get("days"),
          (List<Double>) st.get("suboptimal_start"),
          (List<Double>) st.get("suboptimal_end"),
          (List<List<Boolean>>) st.get("suboptimal_days"));
      for (int c = 0; c < j; c++) {
        ArrayList<ArrayList<Long>> a = new ArrayList<>();
        ArrayList<ArrayList<Long>> b = new ArrayList<>();
        a.add((ArrayList<Long>) concreteTime(user, mc, "preferred", c));
        b.add((ArrayList<Long>) concreteTime(user, mc, "suboptimal", c));
        a.add(CalendarQuickstart.events(user.access_token, (ArrayList<String>) user.calendar_id, mc));
        b.add(CalendarQuickstart.events(user.access_token, (ArrayList<String>) user.calendar_id, mc));

        ArrayList<Long> p = mm.intersectMany(a);
        // System.out.println(p);

        for (int i = 0; i < p.size(); i++) {
          if (i % 2 == 1) {
            mt.setEndTimes(p.get(i));
          } else if (i % 2 == 0) {
            mt.setStartTimes(p.get(i));
          }
        }
        // System.out.println(b);

        ArrayList<Long> l = mm.intersectMany(b);
        for (int i = 0; i < l.size(); i++) {
          if (i % 2 == 1) {
            mt.setSubEndTimes(l.get(i));
          } else if (i % 2 == 0) {
            mt.setSubStartTimes(l.get(i));
          }
        }
      }
    }

    if (notFound.size() > 0) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "profile not found for:  " + notFound);
    }
    // System.out.println(a);
    // Remove times that are shorter than specified meeting length
    for (int i = 0; i < mt.startTimes.size(); i++) {
      long difference = mt.endTimes.get(i) - mt.startTimes.get(i);

      if (difference < 1000 * 60 * mc.getMtngLength()) {
        mt.startTimes.remove(i);
        mt.endTimes.remove(i);
      }
    }

    for (int i = 0; i < mt.subStartTimes.size(); i++) {
      long difference = mt.subEndTimes.get(i) - mt.subStartTimes.get(i);

      if (difference > 1000 * 60 * mc.getMtngLength()) {
        mt.subStartTimes.remove(i);
        mt.subEndTimes.remove(i);
      }
    }

    System.out.println("startTimes:  " + mt.startTimes);
    System.out.println("endTimes:  " + mt.endTimes);

    System.out.println("substartTimes:  " + mt.subStartTimes);
    System.out.println("subendTimes:  " + mt.subEndTimes);

    return mt;
  }

  @GetMapping("/login")
  public RedirectView login() {
    String url = AuthorizationManager.getAuthorizationUrl();
    return new RedirectView(url);
  }

  @GetMapping("/oauth")
  public String handleCallback(@RequestParam(value = "code") String authorizationCode) throws IOException {
    GoogleTokenResponse tokenResponse = AuthorizationManager.getTokenFromCode(authorizationCode);

    GoogleCredential credential = AuthorizationManager.getCredential();
    credential.setAccessToken(tokenResponse.getAccessToken());

    HttpTransport httpTransport = new NetHttpTransport();
    HttpRequestFactory requestFactory = httpTransport.createRequestFactory(credential);
    GenericUrl url = new GenericUrl(
        "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" +
            credential.getAccessToken());
    HttpRequest request = requestFactory.buildGetRequest(url);
    HttpResponse response = request.execute();

    Payload payload = new Gson().fromJson(response.parseAsString(), Payload.class);

    // Default Configuration
    List<String> scope = Arrays.asList("https://www.googleapis.com/auth/userinfo.email",
        "https://www.googleapis.com/auth/calendar");
    List<Double> p_start = Arrays.asList(9d);
    List<Double> p_end = Arrays.asList(17d);
    List<List<Boolean>> p_days = Arrays.asList(
        Arrays.asList(false, true, true, true, true, true, false));
    List<Double> s_start = Arrays.asList(16d);
    List<Double> s_end = Arrays.asList(19d);
    List<List<Boolean>> s_days = Arrays.asList(
        Arrays.asList(true, false, false, false, false, false, true));
    String email = payload.getEmail();
    Document newUser = new Document("email", email)
        .append("access_token", tokenResponse.getAccessToken())
        .append("refresh_token", tokenResponse.getRefreshToken())
        .append("expires_at", tokenResponse.getExpiresInSeconds())
        .append("scope", scope)
        .append("token_type", tokenResponse.getTokenType())
        .append("timezone", "0")
        .append("calendar_id", Arrays.asList(email))
        .append("preferred_timerange", new Document("start", p_start)
            .append("end", p_end)
            .append("days", p_days))
        .append("suboptimal_timerange",
            new Document("suboptimal_start", s_start)
                .append("suboptimal_end", s_end)
                .append("suboptimal_days", s_days));

    Document user = db.fetchUser(email);

    if (user == null) {
      collection.insertOne(newUser);
    } else {
      collection.updateOne(user, new Document("$set", newUser));
    }
    return "Authorization Success! You may now close this window.";

    // make an account with the email, add it to the db
    // try to make email and access token a string to just pass into the mongo
    // db make a new mongo db user once they are authenticated. create a new
    // user in db with id email access token and stuff, default values

    // return payload.getEmail();
    // return tokenResponse.getAccessToken();
  }

  @PutMapping("/timezone")
  public static ResponseEntity<String> setTimezone(@RequestParam(value = "email") String email,
      @RequestParam(value = "timezone") String timezone) {
    // set timezone in db
    Document query = new Document("email", email);
    Document update = new Document("$set", new Document("timezone", timezone));
    collection.updateOne(query, update);
    return ResponseEntity.ok("Timezone set!");
  }

  @PostMapping("/timerange")
  public static ResponseEntity<String> addTimeRange(@RequestParam(value = "email") String email,
      @RequestParam(value = "type") String type,
      @RequestParam(value = "start") double start,
      @RequestParam(value = "end") double end,
      @RequestParam(value = "days") List<Boolean> days) {
    // add time range for user in db
    Document query = new Document("email", email);
    String whichRange = type + "_timerange";
    Document update = new Document();
    if (type.equals("suboptimal")) {
      update = new Document("$push", new Document(whichRange + "." + type + "_start", start)
          .append(whichRange + "." + type + "_end", end)
          .append(whichRange + "." + type + "_days", days));
    } else {
      update = new Document("$push", new Document(whichRange + ".start", start)
          .append(whichRange + ".end", end)
          .append(whichRange + ".days", days));
    }
    collection.updateOne(query, update);
    return ResponseEntity.ok("Time range added!");
  }

  @PatchMapping("/timerange")
  public ResponseEntity<String> updateTimeRange(@RequestParam(value = "email") String email,
      @RequestParam(value = "type") String type,
      @RequestParam(value = "index") int index,
      @RequestParam(value = "start") double start,
      @RequestParam(value = "end") double end,
      @RequestParam(value = "days") List<Boolean> days) {
    // update time range for user in db
    String whichRange = type + "_timerange";

    Document query = new Document("email", email);
    Document update = new Document();
    if (type.equals("suboptimal")) {
      update = new Document("$set", new Document(whichRange + "." + type + "_start"
          + "." + index,
          start)
          .append(whichRange + "." + type + "_end"
              + "." + index,
              end)
          .append(whichRange + "." + type + "_days"
              + "." + index,
              days));
    } else {
      update = new Document("$set", new Document(whichRange + ".start"
          + "." + index,
          start)
          .append(whichRange + ".end"
              + "." + index,
              end)
          .append(whichRange + ".days"
              + "." + index,
              days));
    }
    collection.updateOne(query, update);
    return ResponseEntity.ok("Time range updated!");
  }

  @DeleteMapping("/timerange")
  public ResponseEntity<String> removeTimeRange(@RequestParam(value = "email") String email,
      @RequestParam(value = "type") String type,
      @RequestParam(value = "index") int index) {
    // remove time range for user in db
    String whichRange = type + "_timerange";

    if (type.equals("suboptimal")) {
      Document query = new Document("email", email);
      Document updatestart = new Document(
          "$unset", new Document(whichRange + "." + type + "_start." + index, null));
      Document updateend = new Document(
          "$unset", new Document(whichRange + "." + type + "_end." + index, null));
      Document updatedays = new Document(
          "$unset", new Document(whichRange + "." + type + "_days." + index, null));
      collection.updateOne(query, updatestart);
      collection.updateOne(query, updateend);
      collection.updateOne(query, updatedays);

      Document removeNullStart = new Document("$pull", new Document(whichRange + "." + type + "_start", null));
      Document removeNullEnd = new Document("$pull", new Document(whichRange + "." + type + "_end", null));
      Document removeNullDays = new Document("$pull", new Document(whichRange + "." + type + "_days", null));
      collection.updateOne(query, removeNullStart);
      collection.updateOne(query, removeNullEnd);
      collection.updateOne(query, removeNullDays);
    } else {
      Document query = new Document("email", email);
      Document updatestart = new Document(
          "$unset", new Document(whichRange + ".start." + index, null));
      Document updateend = new Document(
          "$unset", new Document(whichRange + ".end." + index, null));
      Document updatedays = new Document(
          "$unset", new Document(whichRange + ".days." + index, null));
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
    return ResponseEntity.ok("Time range removed!");
  }

  @RequestMapping(method = RequestMethod.GET, value = "/newUser")
  public static void addNewUser(
      @RequestParam(value = "email") String email,
      @RequestParam(value = "timezone") double timezone,
      @RequestParam(value = "calendar_id") List<String> calendar_id,
      @RequestParam(value = "preferred_start") List<Double> preferred_start,
      @RequestParam(value = "preferred_end") List<Double> preferred_end,
      @RequestParam(value = "preferred_day") List<List<Boolean>> preferred_day,
      @RequestParam(value = "suboptimal_start") List<Double> suboptimal_start,
      @RequestParam(value = "suboptimal_end") List<Double> suboptimal_end,
      @RequestParam(value = "suboptimal_day") List<List<Boolean>> suboptimal_day) {

    DatabaseManager.currentUser user = new DatabaseManager.currentUser(
        email, timezone, calendar_id, preferred_start, preferred_end,
        preferred_day, suboptimal_start, suboptimal_end, suboptimal_day);

    Document document;
    document = newcurrentUser(user);
    collection.insertOne(document);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/currentUser")
  public static DatabaseManager.currentUser currentUser(@RequestParam(value = "email") String email) {
    Document d = db.fetchUser(email);

    if (d == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    Document pt = (Document) d.get("preferred_timerange");
    Document st = (Document) d.get("suboptimal_timerange");
    DatabaseManager.currentUser user = new DatabaseManager.currentUser(
        email, Double.valueOf(d.getString("timezone")),
        (List<String>) d.get("calendar_id"), (List<Double>) pt.get("start"),
        (List<Double>) pt.get("end"), (List<List<Boolean>>) pt.get("days"),
        (List<Double>) st.get("suboptimal_start"),
        (List<Double>) st.get("suboptimal_end"),
        (List<List<Boolean>>) st.get("suboptimal_days"));

    return user;
  }

  @PutMapping("/calendar")
  public static void calendar(@RequestParam(value = "email") String email,
      @RequestParam(value = "calendar_id") String calendar_id,
      @RequestParam(value = "used") Boolean used) {
    Document query = new Document("email", email);
    Document update = new Document();
    if (used) {
      update = new Document("$push", new Document("calendar_id", calendar_id));
    } else {
      update = new Document("$pull", new Document("calendar_id", calendar_id));
    }
    collection.updateOne(query, update);
  }
}
