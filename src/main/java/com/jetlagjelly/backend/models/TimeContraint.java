package com.jetlagjelly.backend.models;

import com.jetlagjelly.backend.controllers.DatabaseManager;
import com.jetlagjelly.backend.controllers.MeetingManager;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jetlagjelly.backend.controllers.DatabaseManager.collection;
import static com.jetlagjelly.backend.controllers.DatabaseManager.fetchUser;

public class TimeContraint {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        getMeetingConstraints(
                "bmclean2@oswego.edu JetLagJellyFan@gmail.com", 120, 1682913600000L,
                1683345540000L);

    }



    public static MeetingTimes getMeetingConstraints(
             String email,
             int mtngLength,
            Long startDay,
            Long endDay)
            throws GeneralSecurityException, IOException {
        MeetingContraint mc = new MeetingContraint();

        MongoClient client = MongoClients.create("mongodb://localhost:27017/");
        MongoDatabase db = client.getDatabase("JetLagJelly");
        MongoCollection collections = db.getCollection("users");

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
        ArrayList<DatabaseManager.User> notFound = new ArrayList<>();

        for (String s : emailList) {
            Document d = fetchUser(collections, s);
            Document pt = (Document) d.get("preferred_timerange");
            Document st = (Document) d.get("suboptimal_timerange");
            DatabaseManager.User user = new DatabaseManager.User(
                    s, d.getString("access_token"), d.getString("refresh_token"),
                    d.getLong("expires_at"), (List<String>) d.get("scope"),
                    d.getString("token_type"), Double.valueOf(d.getString("timezone")),
                    (List<String>) d.get("calendar_id"), (List<Double>) pt.get("start"),
                    (List<Double>) pt.get("end"), (List<List<Boolean>>) pt.get("days"),
                    (List<Double>) st.get("suboptimal_start"),
                    (List<Double>) st.get("suboptimal_end"),
                    (List<List<Boolean>>) st.get("suboptimal_days"));
            a.add((ArrayList<Long>) DatabaseManager.concreteTime(user, mc));
            //b.add((ArrayList<Long>) DatabaseManager.concreteSubTime(user, mc));
        }
        if (notFound.size() < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "profile not found for:  " + notFound);
        }
        System.out.println(a);
        ArrayList<Long> p = mm.intersectMany(a);
        // System.out.println(p);

        // System.out.println("Events list " +
        // events("ya29.a0Ael9sCOxyvQXDCeCYvs52eS13MnXiYHouO_imWwnQYKioVyT2TciADhRzIoRz4SYTi3XnUE0ioq7JBFqyrovUKKCIuSNuB6q-ixspwB0U6ycNZXNZoMTYA03Z6WDK4SAh03L9kvQO3K51DjvBNbGXktv4R1GgJAaCgYKAcASARESFQF4udJhSW9tE-VC6NAixQ_c4Lx8Dg0166",
        // "bmclean426@gmail.com"));
        MeetingTimes mt = new MeetingTimes();
        for (int i = 0; i < p.size(); i++) {
            if (i % 2 == 1) {
                mt.setEndTimes(p.get(i));
            } else if (i % 2 == 0) {
                mt.setStartTimes(p.get(i));
            }
        }
        // System.out.println(b);

        System.out.println("startTimes:  " + mt.startTimes);
        System.out.println("endTimes:  " + mt.endTimes);

        return mt;
    }
}
