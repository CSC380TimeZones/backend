package com.jetlagjelly.backend;

import static com.jetlagjelly.backend.controllers.MeetingManager.intersectMany;
import static com.jetlagjelly.backend.controllers.DatabaseManager.concreteTime;
import com.jetlagjelly.backend.models.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.jetlagjelly.backend.controllers.AuthorizationManager;
import com.jetlagjelly.backend.controllers.DatabaseManager;
import com.jetlagjelly.backend.controllers.MeetingManager;
import com.jetlagjelly.backend.models.MeetingContraint;
import com.jetlagjelly.backend.models.MeetingTimes;
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
  public static MeetingManager mm = new MeetingManager();
  public static Dotenv dotenv = Dotenv.load();

  @RequestMapping(method = RequestMethod.GET, value = "/email")
  public static Document getMeetingConstraints(
      @RequestParam(value = "email", defaultValue = "No email found!") String email,
      @RequestParam(value = "mtngLength", defaultValue = "60") int mtngLength,
      @RequestParam(value = "startDay", defaultValue = "100000000000") Long startDay,
      @RequestParam(value = "endDay", defaultValue = "1000000000") Long endDay)
      throws GeneralSecurityException, IOException {

    MeetingContraint mc = new MeetingContraint()
        .setEmail(email)
        .setMtngLength(mtngLength)
        .setStartDay(startDay)
        .setEndDay(endDay);

    LocalDateTime startdate = LocalDateTime.ofInstant(Instant.ofEpochMilli(startDay), TimeZone
        .getDefault().toZoneId());

    LocalDateTime enddate = LocalDateTime.ofInstant(Instant.ofEpochMilli(endDay), TimeZone
        .getDefault().toZoneId());
    int j = 0;
    if (startdate.plusWeeks(1).isEqual(enddate)) {
      j = 0;
    } else {
      for (int i = 0; i < 52; i++) {
        if (startdate.plusWeeks(i).isAfter(enddate)) {
          j = i;
        }
      }
    }

    String ls = mc.getEmail();
    ArrayList<String> emailList = new ArrayList<>();
    String[] emailArray = ls.split(" ");
    Collections.addAll(emailList, emailArray);
    ArrayList<String> notFound = new ArrayList<>();
    MeetingTimes mt = new MeetingTimes();
    ArrayList<ArrayList<Long>> a = new ArrayList<>();
    ArrayList<ArrayList<Long>> b = new ArrayList<>();

    for (String s : emailList) {
      // Get user object from database
      User user = db.fetchUserAsUserObject(s, true);

      // Add user to not found list if email is not in database, and skip all time
      // checks
      if (user == null) {
        notFound.add(s);
        continue;
      } else if (!notFound.isEmpty())
        continue;

      if (user.expires_at <= System.currentTimeMillis()) {
        GoogleTokenResponse tokenResponse = AuthorizationManager.refreshToken(user.refresh_token);
        db.updateUserToken(user.email, tokenResponse);
        user.updateOauthProperties(tokenResponse);
      }

      List<Long> preferredConcreteTimes = concreteTime(user, mc, "preferred", j);
      List<Long> suboptimalConcreteTimes = concreteTime(user, mc, "suboptimal", j);
      ArrayList<Long> events = CalendarQuickstart.events(user.access_token, (ArrayList<String>) user.calendar_id, mc);
      a.add((ArrayList<Long>) preferredConcreteTimes);
      b.add((ArrayList<Long>) suboptimalConcreteTimes);
      b.add((ArrayList<Long>) preferredConcreteTimes);
      a.add(events);
      b.add(events);
    }

    if (notFound.size() > 0) {
      Document response = new Document()
          .append("error", "unrecognized_emails")
          .append("emails", notFound);

      return response;
    }

    ArrayList<Long> p = intersectMany(a);
    // System.out.println(p);

    for (int i = 0; i < p.size(); i++) {
      if (i % 2 == 1) {
        mt.setEndTimes(p.get(i));
      } else if (i % 2 == 0) {
        mt.setStartTimes(p.get(i));
      }
    }
    // System.out.println(b);

    ArrayList<Long> l = intersectMany(b);
    for (int i = 0; i < l.size(); i++) {
      if (i % 2 == 1) {
        mt.setSubEndTimes(l.get(i));
      } else if (i % 2 == 0) {
        mt.setSubStartTimes(l.get(i));
      }
    }

    for (int i = 0; i < p.size() / 2; i++) {
      for (int k = 0; k < l.size() / 2; k++) {
        if (mt.endTimes.get(i) < mt.subEndTimes.get(k)
            && mt.subEndTimes.get(k) < mt.subStartTimes.get(k)
            && mt.subStartTimes.get(k) < mt.startTimes.get(i)) {
          mt.subStartTimes.remove(k);
          mt.subEndTimes.remove(k);
        } else if (mt.subEndTimes.get(k).equals(mt.endTimes.get(i))) {
          mt.subStartTimes.remove(k);
          mt.subEndTimes.remove(k);
        } else if (mt.subStartTimes.get(k).equals(mt.startTimes.get(i))) {
          mt.subStartTimes.remove(k);
          mt.subEndTimes.remove(k);
        }
      }
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

      if (difference < 1000 * 60 * mc.getMtngLength()) {
        mt.subStartTimes.remove(i);
        mt.subEndTimes.remove(i);
      }
    }

    System.out.println("startTimes:  " + mt.startTimes);
    System.out.println("endTimes:  " + mt.endTimes);

    System.out.println("substartTimes:  " + mt.subStartTimes);
    System.out.println("subendTimes:  " + mt.subEndTimes);

    // Generate response object
    Document response = new Document()
        .append("startTimes", mt.startTimes)
        .append("endTimes", mt.endTimes)
        .append("subStartTimes", mt.subStartTimes)
        .append("subEndTimes", mt.subEndTimes);

    return response;
  }

  @GetMapping("/send")
  public Document sendEmail(@RequestParam(value = "email") String email) {
    db.sendEmail(email);
    Document response = new Document("email_sent", true);
    return response;
  }

  @GetMapping("/login")
  public RedirectView login() {

    String url = AuthorizationManager.getAuthorizationUrl();
    return new RedirectView(url);
  }

  @GetMapping("/oauth")
  public String handleCallback(@RequestParam(value = "code") String authorizationCode) throws IOException {

    AuthorizationManager.handleOauthCallback(db, authorizationCode);
    return "Authorization Success! You may now close this window.";
  }

  @PutMapping("/timezone")
  public static ResponseEntity<String> setTimezone(
      @RequestParam(value = "email") String email,
      @RequestParam(value = "timezone") String timezone) {

    db.setTimezone(email, timezone);
    return ResponseEntity.ok("Timezone set!");
  }

  @PostMapping("/timerange")
  public static ResponseEntity<String> addTimeRange(@RequestParam(value = "email") String email,
      @RequestParam(value = "type") String type,
      @RequestParam(value = "start") double start,
      @RequestParam(value = "end") double end,
      @RequestParam(value = "days") List<Boolean> days) {

    db.addTimeRange(email, type, start, end, days);
    return ResponseEntity.ok("Time range added!");
  }

  @PatchMapping("/timerange")
  public ResponseEntity<String> updateTimeRange(@RequestParam(value = "email") String email,
      @RequestParam(value = "type") String type,
      @RequestParam(value = "index") int index,
      @RequestParam(value = "start") double start,
      @RequestParam(value = "end") double end,
      @RequestParam(value = "days") List<Boolean> days) {

    db.updateTimeRange(email, type, index, start, end, days);
    return ResponseEntity.ok("Time range updated!");
  }

  @DeleteMapping("/timerange")
  public ResponseEntity<String> removeTimeRange(@RequestParam(value = "email") String email,
      @RequestParam(value = "type") String type,
      @RequestParam(value = "index") int index) {

    db.deleteTimeRange(email, type, index);
    return ResponseEntity.ok("Time range removed!");
  }

  @GetMapping("/currentUser")
  public static Document currentUser(@RequestParam(value = "email") String email) {

    Document user = db.fetchUser(email, false);
    if (user == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    return user;
  }

  @PutMapping("/calendar")
  public static void calendar(@RequestParam(value = "email") String email,
      @RequestParam(value = "calendar_id") String calendar_id,
      @RequestParam(value = "used") Boolean used) {

    db.toggleCalendar(email, calendar_id, used);
  }
}
