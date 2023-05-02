package com.jetlagjelly.backend;

import static com.jetlagjelly.backend.controllers.MeetingManager.intersectMany;
import static com.jetlagjelly.backend.controllers.DatabaseManager.concreteTime;
import com.jetlagjelly.backend.models.*;

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
  public static MeetingTimes getMeetingConstraints(
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
    for (int i = 0; i < 52; i++) {
      if (startdate.plusWeeks(i).isAfter(enddate)) {
        j = i;
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

      // Add user to not found list if email is not in database, and skip email
      if (user == null) {
        notFound.add(s);
        continue;
      }
      a.add((ArrayList<Long>) concreteTime(user, mc, "preferred", j));
      b.add((ArrayList<Long>) concreteTime(user, mc, "suboptimal", j));
      a.add(CalendarQuickstart.events(user.access_token, (ArrayList<String>) user.calendar_id, mc));
      b.add(CalendarQuickstart.events(user.access_token, (ArrayList<String>) user.calendar_id, mc));
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
