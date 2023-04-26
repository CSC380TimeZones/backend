package com.jetlagjelly.backend;

//import static com.jetlagjelly.backend.controllers.AuthorizationManager.exchangeCode;
import static com.jetlagjelly.backend.models.MeetingTimes.setEndTimes;
import static com.jetlagjelly.backend.models.MeetingTimes.setStartTimes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jetlagjelly.backend.controllers.DatabaseManager;
import com.jetlagjelly.backend.controllers.MeetingManager;
import com.jetlagjelly.backend.models.MeetingContraint;
import com.jetlagjelly.backend.models.MeetingTimes;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Updates;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier.Builder;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;



@RestController
public class Endpoints {
  private static final String CLIENT_ID = "1018210986187-ve886ig30rfadhe5ahrmu2tg391ohq8s.apps.googleusercontent.com";
  private static final String CLIENT_SECRET = "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";
  private static final String REDIRECT_URI = "https://localhost";

  public static MeetingContraint mc = new MeetingContraint();

  public static MongoCollection collection() {
    MongoClient client = MongoClients.create("mongodb://localhost:27017/");
    MongoDatabase db = client.getDatabase("JetLagJelly");
    MongoCollection collection = db.getCollection("users");
    return collection;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/email")
  public static MeetingTimes getMeetingConstraints(
      @RequestParam(value = "email",
                    defaultValue = "No email found!") String email,
      @RequestParam(value = "mtngLength", defaultValue = "60") int mtngLength,
      @RequestParam(value = "startDay",
                    defaultValue = "100000000000") Long startDay,
      @RequestParam(value = "endDay",
                    defaultValue = "1000000000") Long endDay) {

    mc.setEmail(email);
    mc.setMtngLength(mtngLength);
    mc.setStartDay(startDay);
    mc.setEndDay(endDay);

    MeetingManager mm = new MeetingManager();
    String ls = mc.getEmail();
    ArrayList<String> emailList = new ArrayList<>();
    String[] emailArray = ls.split(" ");
    Collections.addAll(emailList, emailArray);
    ArrayList<ArrayList<Long>> a = new ArrayList<>();

    for (String s : emailList) {
      Document d = DatabaseManager.fetchUser(collection(), s);
      Document pt = (Document)d.get("preferred_timerange");
      Document st = (Document)d.get("suboptimal_timerange");
      DatabaseManager.User user = new DatabaseManager.User(
          s, d.getString("access_token"), d.getString("refresh_token"),
          d.getInteger("expires_at"), (List<String>)d.get("scope"),
          d.getString("token_type"), d.getString("timezone"),
          (List<String>)d.get("calendar_id"), (List<Integer>)pt.get("start"),
          (List<Integer>)pt.get("end"), (List<Integer>)pt.get("days"),
          (List<Integer>)st.get("suboptimal_start"),
          (List<Integer>)st.get("suboptimal_end"),
          (List<Integer>)st.get("suboptimal_days"));
      a.add((ArrayList<Long>)DatabaseManager.concreteTime(user, mc));
    }
    // System.out.println(a);
    ArrayList<Long> p = mm.intersectMany(a);
    // System.out.println(p);
    MeetingTimes mt = new MeetingTimes();
    for (int i = 0; i < p.size(); i++) {
      if (i % 2 == 1) {
        setEndTimes(p.get(i));
      } else if (i % 2 == 0) {
        setStartTimes(p.get(i));
      }
    }

    System.out.println("startTimes:  " + mt.startTimes);
    System.out.println("endTimes:  " + mt.endTimes);

    return mt;
  }

  @GetMapping("/oauth")
  public String handleCallback(@RequestParam("code") String authorizationCode) throws IOException, GeneralSecurityException {
    // Exchange the authorization code for an access token and ID token
    NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    GsonFactory gsonFactory = new GsonFactory();
    GoogleAuthorizationCodeTokenRequest request = new GoogleAuthorizationCodeTokenRequest(
            httpTransport,
            gsonFactory,
            CLIENT_ID,
            CLIENT_SECRET,
            authorizationCode,
            REDIRECT_URI
    );
    request.setGrantType("authorization_code");
    GoogleTokenResponse tokenResponse = request.execute();


    // Extract the ID token from the token response
    String idTokenString = tokenResponse.getIdToken();

    // Get the access token from the token response
    String accessToken = tokenResponse.getAccessToken();

    // Verify the ID token using the GoogleIdTokenVerifier
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, gsonFactory)
            .setAudience(Collections.singletonList(CLIENT_ID))
            .build();
    GoogleIdToken idToken = verifier.verify(idTokenString);

    if (idToken != null) {
      // The ID token is valid
      String userId = idToken.getPayload().getSubject();
      String email = idToken.getPayload().getEmail();
      String name = (String) idToken.getPayload().get("name");
      return "User ID: " + userId + "\n" + "Email: " + email + "\n" + "Name: " + name;
    } else {
      // The ID token is invalid
      return "Invalid ID token.";
    }
  }


  @PutMapping("/timezone")
  public ResponseEntity<String> setTimezone(
          @RequestParam(value = "email") String email,
          @RequestParam(value = "timezone") String timezone) {
    //set timezone in db
    Document query = new Document("email", email);
    Document update = new Document("$set", new Document("timezone", timezone));
    collection().updateOne(query,update);
    return ResponseEntity.ok("Timezone set!");
  }

  @PostMapping("/timerange")
  public ResponseEntity<String> addTimeRange(
          @RequestParam(value = "email") String email,
          @RequestParam(value = "start") int start,
          @RequestParam(value = "end") int end,
          @RequestParam(value = "days") List<Integer> days) {
    //add time range for user in db
    Document query =  new Document("email", email);
    Document update = new Document("$push", new Document("preferred_timerange.start", start)
            .append("preferred_timerange.end", end)
            .append("preferred_timerange.days", days));
    collection().updateOne(query, update);
    return ResponseEntity.ok("Time range added!");
  }
  @PatchMapping("/timerange")
  public ResponseEntity<String> updateTimeRange(
          //@RequestParam(value = "timerange") String timerange,
          @RequestParam(value = "email") String email,
          @RequestParam(value = "start") int start,
          @RequestParam(value = "end") int end,
          @RequestParam(value = "days") List<Integer> days) {
    //update time range for user in db
    Document query =  new Document("email", email);
    Document update = new Document("$push", new Document("preferred_timerange.start", start)
            .append("preferred_timerange.end", end)
            .append("preferred_timerange.days", days));
    collection().updateOne(query, update);
    return ResponseEntity.ok("Time range updated!");
  }

//specify which timerange to delete, inside array and which index
  @DeleteMapping("/timerange")
  public ResponseEntity<String> removeTimeRange(
          @RequestParam(value = "email") String email,
          @RequestParam(value = "start") int start,
          @RequestParam(value = "end") int end,
          @RequestParam(value = "days") List<Integer> days) {
    //remove time range  for user in db
    Document query = new Document("email", email);
    Document update = new Document("$pull", new Document("preferred_timerange.start", start)
            .append("preferred_timerange.end", end)
            .append("preffered_timerange.days", days));
    collection().updateOne(query, update);
    return ResponseEntity.ok("Time range removed!");
  }

}
