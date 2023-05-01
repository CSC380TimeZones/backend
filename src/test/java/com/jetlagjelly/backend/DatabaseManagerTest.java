package com.jetlagjelly.backend;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.jetlagjelly.backend.Endpoints.mc;
import static com.jetlagjelly.backend.controllers.DatabaseManager.collection;
import static com.jetlagjelly.backend.controllers.DatabaseManager.deleteUser;
import com.jetlagjelly.backend.controllers.DatabaseManager;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {
    public static DatabaseManager.User constants() {
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
        dyya.add(dya);
        dyya.add(dyaa);

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

        DatabaseManager.User user = new DatabaseManager.User("bmclean2@oswego.edu",
                "MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3",
                "IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk", 3600L, sca, "Bearer",
                -5, cida, sta, ena, dyya, ss,
                se, sda);

        return user;
    }

    @Test
    void newUser() {
        DatabaseManager.User user = constants();
        Document document;
        document = DatabaseManager.newUser(user);
        collection.insertOne(document);
    }

    @Test
    void meetingMgr() {
        DatabaseManager.User user = constants();
        Document document;

        document = DatabaseManager.meetingMgr(collection, user);

        assertEquals("America/New_York", document.get("timezone"));
    }

    @Test
    void fetchUser() {

        DatabaseManager.User user = constants();
        Document document;

        document = DatabaseManager.fetchUser(collection, "bmclean2@oswego.edu");

        //assertEquals(user.calendar_id, document.get("calendar_id"));
    }

    @Test
    void tokens() {

        DatabaseManager.User user = constants();
        Document document;

        document = DatabaseManager.tokens(user);

        assertEquals(user.access_token, document.get("token"));

    }

    @Test
    void setTimezone() {

        DatabaseManager.User user = constants();
        Document document;

        DatabaseManager.setTimezone(user, -4);
        document = DatabaseManager.newUser(user);
        deleteUser(collection, user);
        collection.insertOne(document);

        assertEquals("Europe/Malta", user.timezone);
    }

    @Test
    void concreteUser() {
        DatabaseManager.User user = constants();

        System.out.println(DatabaseManager.concreteTime(user, mc, "preferred"));
    }
}