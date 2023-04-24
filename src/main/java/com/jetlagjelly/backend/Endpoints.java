package com.jetlagjelly.backend;

import com.jetlagjelly.backend.controllers.DatabaseManager;
import com.jetlagjelly.backend.controllers.MeetingManager;
import com.jetlagjelly.backend.models.MeetingContraint;
import com.jetlagjelly.backend.models.MeetingTimes;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jetlagjelly.backend.CalendarQuickstart.events;
import static com.jetlagjelly.backend.models.MeetingTimes.*;

@RestController
public class Endpoints {

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
                    d.getString("refresh_token"), d.getInteger("expires_at"), (List<String>) d.get("scope"),
                    d.getString("token_type"), d.getString("timezone"), (List<String>) d.get("calendar_id"),
                    (List<Integer>) pt.get("start"), (List<Integer>) pt.get("end"), (List<Integer>) pt.get("days"),
                    (List<Integer>) st.get("suboptimal_start"), (List<Integer>) st.get("suboptimal_end"),
                    (List<Integer>) st.get("suboptimal_days"));
            a.add((ArrayList<Long>) DatabaseManager.concreteTime(user, mc));
            b.add((ArrayList<Long>) DatabaseManager.concreteSubTime(user, mc));
        }
        // System.out.println(a);
        ArrayList<Long> p = mm.intersectMany(a);
        // System.out.println(p);

        System.out.println(events());
        MeetingTimes mt = new MeetingTimes();
        for (int i = 0; i < p.size(); i++) {
            if (i % 2 == 1) {
                setEndTimes(p.get(i));
            } else if (i % 2 == 0) {
                setStartTimes(p.get(i));
            }
        }
        System.out.println(b);

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
    public ResponseEntity<String> addTimeRange(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "start") int start,
            @RequestParam(value = "end") int end,
            @RequestParam(value = "days") List<Integer> days) {
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
            @RequestParam(value = "start") int start,
            @RequestParam(value = "end") int end,
            @RequestParam(value = "days") List<Integer> days) {
        // update time range for user in db
        Document query = new Document("email", email);
        Document update = new Document("$push", new Document("preferred_timerange.start", start)
                .append("preferred_timerange.end", end)
                .append("preferred_timerange.days", days));
        collection.updateOne(query, update);
        return ResponseEntity.ok("Time range updated!");
    }

    @DeleteMapping("/timerange")
    public ResponseEntity<String> removeTimeRange(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "start") int start,
            @RequestParam(value = "end") int end,
            @RequestParam(value = "days") List<Integer> days) {
        // remove time range for user in db
        Document query = new Document("email", email);
        Document update = new Document("$pull", new Document("preferred_timerange.start", start)
                .append("preferred_timerange.end", end)
                .append("preferred_timerange.days", days));
        collection.updateOne(query, update);
        return ResponseEntity.ok("Time range removed!");
    }
}
