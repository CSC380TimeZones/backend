package com.jetlagjelly.backend;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.jetlagjelly.backend.controllers.DatabaseManager;
import com.jetlagjelly.backend.controllers.MeetingManager;
import com.jetlagjelly.backend.models.MeetingContraint;
import com.jetlagjelly.backend.models.MeetingTimes;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jetlagjelly.backend.CalendarQuickstart.events;
import static com.jetlagjelly.backend.controllers.DatabaseManager.newUser;
import static com.jetlagjelly.backend.models.MeetingTimes.*;

@RestController
public class Endpoints {

    private String CLIENT_ID = "1018210986187-ve886ig30rfadhe5ahrmu2tg391ohq8s.apps.googleusercontent.com";
    private String CLIENT_SECRET = "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";
    private String REDIRECT_URI = "http://localhost:8080/oauth";
    public static MeetingContraint mc = new MeetingContraint();
    public static MongoCollection collection = new DatabaseManager().collection;

    @RequestMapping(method = RequestMethod.GET, value = "/email")
    public static MeetingTimes getMeetingConstraints(
            @RequestParam(value = "email", defaultValue = "No email found!") String email,
            @RequestParam(value = "mtngLength", defaultValue = "60") int mtngLength,
            @RequestParam(value = "startDay", defaultValue = "100000000000") Long startDay,
            @RequestParam(value = "endDay", defaultValue = "1000000000") Long endDay)
            throws GeneralSecurityException, IOException {

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
        ArrayList<ArrayList<Long>> b = new ArrayList<>();

        for (String s : emailList) {
            Document d = DatabaseManager.fetchUser(collection, s);
            Document pt = (Document) d.get("preferred_timerange");
            Document st = (Document) d.get("suboptimal_timerange");
            DatabaseManager.User user = new DatabaseManager.User(s, d.getString("access_token"),
                    d.getString("refresh_token"), d.getLong("expires_at"), (List<String>) d.get("scope"),
                    d.getString("token_type"), d.getString("timezone"), (List<String>) d.get("calendar_id"),
                    (List<Double>) pt.get("start"), (List<Double>) pt.get("end"), (List<List<Boolean>>) pt.get("days"),
                    (List<Double>) st.get("suboptimal_start"), (List<Double>) st.get("suboptimal_end"),
                    (List<List<Boolean>>) st.get("suboptimal_days"));
            a.add((ArrayList<Long>) DatabaseManager.concreteTime(user, mc));
            b.add((ArrayList<Long>) DatabaseManager.concreteSubTime(user, mc));
        }
         System.out.println(a);
        ArrayList<Long> p = mm.intersectMany(a);
        // System.out.println(p);

       // System.out.println("Events list    " + events("ya29.a0Ael9sCOxyvQXDCeCYvs52eS13MnXiYHouO_imWwnQYKioVyT2TciADhRzIoRz4SYTi3XnUE0ioq7JBFqyrovUKKCIuSNuB6q-ixspwB0U6ycNZXNZoMTYA03Z6WDK4SAh03L9kvQO3K51DjvBNbGXktv4R1GgJAaCgYKAcASARESFQF4udJhSW9tE-VC6NAixQ_c4Lx8Dg0166",
       //         "bmclean426@gmail.com"));
        MeetingTimes mt = new MeetingTimes();
        for (int i = 0; i < p.size(); i++) {
            if (i % 2 == 1) {
                setEndTimes(p.get(i));
            } else if (i % 2 == 0) {
                setStartTimes(p.get(i));
            }
        }
        //System.out.println(b);

        ArrayList<Long> l = mm.intersectMany(b);
        for (int i = 0; i < l.size(); i++) {
            if (i % 2 == 1) {
                setSubEndTimes(l.get(i));
            } else if (i % 2 == 0) {
                setSubStartTimes(l.get(i));
            }
        }

        System.out.println("startTimes:  " + mt.startTimes);
        System.out.println("endTimes:  " + mt.endTimes);

        System.out.println("sub-times");

        System.out.println("substartTimes:  " + mt.subStartTimes);
        System.out.println("subendTimes:  " + mt.subEndTimes);

        return mt;
    }

    @GetMapping("/login")
    public RedirectView login()  throws IOException, GeneralSecurityException {

        //this is the only one that doesn't give an error
        HttpTransport httpTransport = new NetHttpTransport();
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport,
                new GsonFactory(),
                CLIENT_ID,
                CLIENT_SECRET,
                Collections.singleton("https://www.googleapis.com/auth/calendar")).build();

        GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI);

        return new RedirectView(url.toString());
    }

    @GetMapping("/oauth")
    public String handleCallback(@RequestParam(value = "code") String authorizationCode) throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = new NetHttpTransport();
        GoogleTokenResponse tokenResponse =  new GoogleAuthorizationCodeTokenRequest(
                httpTransport,
                new GsonFactory(),
                CLIENT_ID,
                CLIENT_SECRET,
                authorizationCode,
                REDIRECT_URI).execute();

        return tokenResponse.getAccessToken();
    }

    @PutMapping("/timezone")
    public static ResponseEntity<String> setTimezone(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "timezone") String timezone) {
        // set timezone in db
        Document query = new Document("email", email);
        Document update = new Document("$set", new Document("timezone", timezone));
        collection.updateOne(query, update);
        return ResponseEntity.ok("Timezone set!");
    }

    @PostMapping("/timerange")
    public static ResponseEntity<String> addTimeRange(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "start") int start,
            @RequestParam(value = "end") int end,
            @RequestParam(value = "days") int days) {
        // add time range for user in db
        Document query = new Document("email", email);
        Document update = new Document("$push", new Document("preferred_timerange.start", start)
                .append("preferred_timerange.end", end)
                .append("preferred_timerange.days", days));
        collection.updateOne(query, update);
        return ResponseEntity.ok("Time range added!");
    }

    @PatchMapping("/timerange")
    public ResponseEntity<String> updateTimeRange(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "index") int index,
            @RequestParam(value = "start") int start,
            @RequestParam(value = "end") int end,
            @RequestParam(value = "days") List<Boolean> days) {
        // update time range for user in db
        String whichRange = type + "_timerange";
        //String whichIndex = whichRange + "." + index;

        Document query = new Document("email", email);
        Document update = new Document("$set", new Document(whichRange + ".start" + "." + index, start)
                .append(whichRange + ".end" + "." + index, end)
                .append(whichRange + ".days" + "." + index, days));
        collection.updateOne(query, update);
        return ResponseEntity.ok("Time range updated!");
    }

    @DeleteMapping("/timerange")
    public ResponseEntity<String> removeTimeRange(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "type") int type,
            @RequestParam(value = "index") int index) {
        // remove time range for user in db
        String whichRange = type + "_timerange";
        //String whichIndex = whichRange + "." + index;

        Document query = new Document("email", email);
        Document update = new Document("$unset", new Document(whichRange + "." + index, null));
        collection.updateOne(query, update);

        update = new Document("$pull", new Document(whichRange, null));
        collection.updateOne(query, update);

        return ResponseEntity.ok("Time range removed!");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/newUser")
    public static void addNewUser(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "access_token") String access_token,
            @RequestParam(value = "refresh_token") String refresh_token,
            @RequestParam(value = "expires_at") Long expires_at,
            @RequestParam(value = "scope") List<String> scope,
            @RequestParam(value = "token_type") String token_type,
            @RequestParam(value = "timezone") String timezone,
            @RequestParam(value = "calendar_id") List<String> calendar_id,
            @RequestParam(value = "preferred_start") List<Double> preferred_start,
            @RequestParam(value = "preferred_end") List<Double> preferred_end,
            @RequestParam(value = "preferred_day") List<List<Boolean>> preferred_day,
            @RequestParam(value = "suboptimal_start") List<Double> suboptimal_start,
            @RequestParam(value = "suboptimal_end") List<Double> suboptimal_end,
            @RequestParam(value = "suboptimal_day") List<List<Boolean>> suboptimal_day ) {

        DatabaseManager.User user = new DatabaseManager.User(email, access_token,
                refresh_token, expires_at, scope, token_type, timezone, calendar_id, preferred_start,
                preferred_end, preferred_day, suboptimal_start, suboptimal_end, suboptimal_day);

        Document document;
        document = newUser(user);
        collection.insertOne(document);
    }
}