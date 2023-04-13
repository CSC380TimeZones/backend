package com.jetlagjelly.backend.controllers;

import com.jetlagjelly.backend.Endpoints;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import static com.jetlagjelly.backend.Endpoints.mc;
import static com.jetlagjelly.backend.controllers.DatabaseManager.deleteUser;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {
    public static DatabaseManager.User constants() {
        List<String> sca = new ArrayList<>();
        sca.add("create");
        List<String> cida = new ArrayList<>();
        cida.add("Phases of the Moon");
        List<Integer> dya = new ArrayList<>();
        dya.add(1);
        dya.add(5);
        List<Integer> sd = new ArrayList<>();
        sd.add(2);
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

        return user;
    }

    @Test
    void newUser() {
        DatabaseManager.User user = constants();
        MongoCollection collection = Endpoints.collection();
        Document document;
        document = DatabaseManager.newUser(user);
        collection.insertOne(document);
    }

    @Test
    void meetingMgr() {
        DatabaseManager.User user = constants();
        MongoCollection collection = Endpoints.collection();
        Document document;

        document = DatabaseManager.meetingMgr(collection, user);

        assertEquals("America/New_York", document.get("timezone"));
    }

    @Test
    void fetchUser() {

        DatabaseManager.User user = constants();
        MongoCollection collection = Endpoints.collection();
        Document document;

        document = DatabaseManager.fetchUser(collection, "bmclean2@oswego.edu");

        assertEquals(user.calendar_id, document.get("calendar_id"));
    }

    @Test
    void tokens() {

        DatabaseManager.User user = constants();
        MongoCollection collection = Endpoints.collection();
        Document document;

        document = DatabaseManager.tokens(user);

        assertEquals(user.access_token, document.get("token"));

    }

    @Test
    void setTimezone() {

        DatabaseManager.User user = constants();
        MongoCollection collection = Endpoints.collection();
        Document document;

        DatabaseManager.setTimezone(user, "Europe/Malta");
        document = DatabaseManager.newUser(user);
        deleteUser(collection, user);
        collection.insertOne(document);

        assertEquals("Europe/Malta", user.timezone);
    }

    @Test
    void concreteUser() {
        DatabaseManager.User user = constants();
        MongoCollection collection = Endpoints.collection();

        System.out.println(DatabaseManager.concreteTime(user, mc));
    }
}