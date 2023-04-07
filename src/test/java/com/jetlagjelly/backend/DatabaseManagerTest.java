package com.jetlagjelly.backend.controllers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import static com.jetlagjelly.backend.controllers.DatabaseManager.deleteUser;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    @Test
    void newUser() {
        MongoClient client = MongoClients.create("mongodb://localhost:27017/");
        MongoDatabase db = client.getDatabase("JetLagJelly");
        MongoCollection collection = db.getCollection("users");


        List<String> sca = new ArrayList<>();
        sca.add("create");
        List<String> cida = new ArrayList<>();
        cida.add("Phases of the Moon");
        List<DayOfWeek> dya = new ArrayList<>();
        dya.add(DayOfWeek.MONDAY);
        dya.add(DayOfWeek.FRIDAY);
        List<DayOfWeek> sd = new ArrayList<>();
        sd.add(DayOfWeek.TUESDAY);
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

        DatabaseManager.User user = new DatabaseManager.User("bmclean2@oswego.edu", "MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3", "IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk", 3600,sca, "Bearer", "America/New_York", cida, sta, ena, dya, ss, se, sd);


        Document document;
        document = DatabaseManager.newUser(user);
        collection.insertOne(document);
    }

    @Test
    void meetingMgr() {
        MongoClient client = MongoClients.create("mongodb://localhost:27017/");
        MongoDatabase db = client.getDatabase("JetLagJelly");
        MongoCollection collection = db.getCollection("users");


        List<String> sca = new ArrayList<>();
        sca.add("create");
        List<String> cida = new ArrayList<>();
        cida.add("Phases of the Moon");
        List<DayOfWeek> dya = new ArrayList<>();
        dya.add(DayOfWeek.MONDAY);
        dya.add(DayOfWeek.FRIDAY);
        List<DayOfWeek> sd = new ArrayList<>();
        sd.add(DayOfWeek.TUESDAY);
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

        DatabaseManager.User user = new DatabaseManager.User("bmclean2@oswego.edu", "MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3", "IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk", 3600,sca, "Bearer", "America/New_York", cida, sta, ena, dya, ss, se, sd);

        Document document;

        document = DatabaseManager.meetingMgr(collection, user);

        assertEquals("America/New_York", document.get("timezone"));
    }

    @Test
    void fetchUser() {

        MongoClient client = MongoClients.create("mongodb://localhost:27017/");
        MongoDatabase db = client.getDatabase("JetLagJelly");
        MongoCollection collection = db.getCollection("users");


        List<String> sca = new ArrayList<>();
        sca.add("create");
        List<String> cida = new ArrayList<>();
        cida.add("Phases of the Moon");
        List<DayOfWeek> dya = new ArrayList<>();
        dya.add(DayOfWeek.MONDAY);
        dya.add(DayOfWeek.FRIDAY);
        List<DayOfWeek> sd = new ArrayList<>();
        sd.add(DayOfWeek.TUESDAY);
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

        DatabaseManager.User user = new DatabaseManager.User("bmclean2@oswego.edu", "MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3", "IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk", 3600,sca, "Bearer", "America/New_York", cida, sta, ena, dya, ss, se, sd);

        Document document;

        document = DatabaseManager.fetchUser(collection, "bmclean2@oswego.edu");

        assertEquals(user.calendar_id, document.get("calendar_id"));
    }

    @Test
    void tokens() {

        MongoClient client = MongoClients.create("mongodb://localhost:27017/");
        MongoDatabase db = client.getDatabase("JetLagJelly");
        MongoCollection collection = db.getCollection("users");


        List<String> sca = new ArrayList<>();
        sca.add("create");
        List<String> cida = new ArrayList<>();
        cida.add("Phases of the Moon");
        List<DayOfWeek> dya = new ArrayList<>();
        dya.add(DayOfWeek.MONDAY);
        dya.add(DayOfWeek.FRIDAY);
        List<DayOfWeek> sd = new ArrayList<>();
        sd.add(DayOfWeek.TUESDAY);
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

        DatabaseManager.User user = new DatabaseManager.User("bmclean2@oswego.edu", "MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3", "IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk", 3600,sca, "Bearer", "America/New_York", cida, sta, ena, dya, ss, se, sd);

        Document document;

        document = DatabaseManager.tokens(user);

        assertEquals(user.access_token, document.get("token"));

    }

    @Test
    void setTimezone() {

        MongoClient client = MongoClients.create("mongodb://localhost:27017/");
        MongoDatabase db = client.getDatabase("JetLagJelly");
        MongoCollection collection = db.getCollection("users");


        List<String> sca = new ArrayList<>();
        sca.add("create");
        List<String> cida = new ArrayList<>();
        cida.add("Phases of the Moon");
        List<DayOfWeek> dya = new ArrayList<>();
        dya.add(DayOfWeek.MONDAY);
        dya.add(DayOfWeek.FRIDAY);
        List<DayOfWeek> sd = new ArrayList<>();
        sd.add(DayOfWeek.TUESDAY);
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

        DatabaseManager.User user = new DatabaseManager.User("bmclean2@oswego.edu", "MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3", "IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk", 3600,sca, "Bearer", "America/New_York", cida, sta, ena, dya, ss, se, sd);

        Document document;

        DatabaseManager.setTimezone(user, "Europe/Malta");
        document = DatabaseManager.newUser(user);
        deleteUser(collection, user);
        collection.insertOne(document);

        assertEquals("Europe/Malta", user.timezone);
    }

    @Test
    void concreteUser() {
        MongoClient client = MongoClients.create("mongodb://localhost:27017/");
        MongoDatabase db = client.getDatabase("JetLagJelly");
        MongoCollection collection = db.getCollection("users");


        List<String> sca = new ArrayList<>();
        sca.add("create");
        List<String> cida = new ArrayList<>();
        cida.add("Phases of the Moon");
        List<DayOfWeek> dya = new ArrayList<>();
        dya.add(DayOfWeek.MONDAY);
        dya.add(DayOfWeek.FRIDAY);
        List<DayOfWeek> sd = new ArrayList<>();
        sd.add(DayOfWeek.TUESDAY);
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

        DatabaseManager.User user = new DatabaseManager.User("bmclean2@oswego.edu", "MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3", "IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk", 3600,sca, "Bearer", "America/New_York", cida, sta, ena, dya, ss, se, sd);


        System.out.println(DatabaseManager.concreteTime(user));
    }
}