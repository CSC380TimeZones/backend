package com.jetlagjelly.backend;

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
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.jetlagjelly.backend.models.MeetingTimes.*;

@RestController
public class Endpoints {

    public static MeetingContraint mc = new MeetingContraint();

    public static MongoCollection collection() {
        MongoClient client = MongoClients.create("mongodb://localhost:27017/");
        MongoDatabase db = client.getDatabase("JetLagJelly");
        MongoCollection collection = db.getCollection("users");
        return collection;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/email")
    public static MeetingTimes getMeetingConstraints(@RequestParam(value = "email", defaultValue = "No email found!") String email, @RequestParam(value = "mtngLength", defaultValue = "60") int mtngLength, @RequestParam(value = "startDay", defaultValue = "100000000000") Long startDay, @RequestParam(value = "endDay", defaultValue = "1000000000") Long endDay) {

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
            Document d = DatabaseManager.fetchUser(collection(), s);
            Document pt = (Document) d.get("preferred_timerange");
            Document st = (Document) d.get("suboptimal_timerange");
            DatabaseManager.User user = new DatabaseManager.User(s, d.getString("access_token"), d.getString("refresh_token"), d.getInteger("expires_at"), (List<String>) d.get("scope"), d.getString("token_type"), d.getString("timezone"), (List<String>) d.get("calendar_id"), (List<Integer>) pt.get("start"), (List<Integer>) pt.get("end"), (List<Integer>) pt.get("days"), (List<Integer>) st.get("suboptimal_start"), (List<Integer>) st.get("suboptimal_end"), (List<Integer>) st.get("suboptimal_days"));
            a.add((ArrayList<Long>) DatabaseManager.concreteTime(user, mc));
            b.add((ArrayList<Long>) DatabaseManager.concreteSubTime(user, mc));
        }
        System.out.println(a);
        ArrayList<Long> p = mm.intersectMany(a);
        //System.out.println(p);
        MeetingTimes mt = new MeetingTimes();
        for(int i = 0; i < p.size(); i++) {
            if (i % 2 == 1) {
                setEndTimes(p.get(i));
            } else if (i % 2 == 0) {
                setStartTimes(p.get(i));
            }
        }
        System.out.println(b);

        ArrayList<Long> l = mm.intersectMany(b);
        for(int i = 0; i < l.size(); i++) {
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
}
