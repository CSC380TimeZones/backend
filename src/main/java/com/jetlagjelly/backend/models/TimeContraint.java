package com.jetlagjelly.backend.models;

import com.jetlagjelly.backend.CalendarQuickstart;
import com.jetlagjelly.backend.controllers.DatabaseManager;
import com.jetlagjelly.backend.controllers.MeetingManager;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.DotenvException;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

public class TimeContraint {
    public static MongoCollection<Document> collection = new DatabaseManager().collection;

    public static void main(String[] args)
            throws GeneralSecurityException, IOException, DotenvException, ExceptionInInitializerError {

        MongoClient client = MongoClients.create("mongodb://localhost:27017/");
        MongoDatabase db = client.getDatabase("JetLagJelly");
        MongoCollection collections = db.getCollection("users");

        String email = "bmclean2@oswego.edu";
        int mtngLength = 60;
        Long startDay = 1682913600000L;
        Long endDay = 1683545540000L;

        MeetingContraint mc = new MeetingContraint();
        mc.setEmail(email);
        mc.setMtngLength(mtngLength);
        mc.setStartDay(startDay);
        mc.setEndDay(endDay);

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

        LocalDateTime startdate = LocalDateTime.ofInstant(Instant.ofEpochMilli(startDay), TimeZone
                .getDefault().toZoneId());

        LocalDateTime enddate = LocalDateTime.ofInstant(Instant.ofEpochMilli(endDay), TimeZone
                .getDefault().toZoneId());
        int j = 0;
        for (int i = 0; i < 52; i++) {
            if (startdate.plusWeeks(i).isAfter(enddate)) {
                j = i;
                break;
            }
        }

        MeetingManager mm = new MeetingManager();
        String ls = mc.getEmail();
        ArrayList<String> emailList = new ArrayList<>();
        String[] emailArray = ls.split(" ");
        Collections.addAll(emailList, emailArray);
        ArrayList<ArrayList<Long>> a = new ArrayList<>();
        ArrayList<ArrayList<Long>> b = new ArrayList<>();
        ArrayList<String> notFound = new ArrayList<>();

        for (int c = 0; c < j; c++) {
            a.add((ArrayList<Long>) DatabaseManager.concreteTime(user, mc, "preferred", c));
        }
        System.out.println(a);

    }
}
