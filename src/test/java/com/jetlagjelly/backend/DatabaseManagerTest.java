package com.jetlagjelly.backend;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import com.jetlagjelly.backend.controllers.DatabaseManager;
import com.jetlagjelly.backend.models.*;

class DatabaseManagerTest {
    public static DatabaseManager db = new DatabaseManager();

    public static User constants() {
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

        User user = new User("bmclean2@oswego.edu",
                "MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3",
                "IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk", 3600L, sca, "Bearer",
                -5, cida, sta, ena, dyya, ss,
                se, sda);

        return user;
    }

    @Test
    void getUser() {
        Document user = db.fetchUser("bmclean426@gmail.com", false);
        assertNotNull(user);
        assertNull(user.get("access_token"));
        assertNotNull(user.get("timezone"));
        assertEquals(user.get("email"), "bmclean426@gmail.com");
    }

    @Test
    void fetchUser() {
        db.fetchUser("bmclean2@oswego.edu", false);
        // assertEquals(user.calendar_id, document.get("calendar_id"));
    }

    @Test
    void concreteUser() {
        User user = constants();
        MeetingContraint mc = new MeetingContraint()
                .setEmail(user.email)
                .setStartDay(1682918097289l)
                .setEndDay(1683522897291l)
                .setMtngLength(60)
                .setSubEndDay(1682918097289l)
                .setSubEndDay(1683522897291l);

        System.out.println(DatabaseManager.concreteTime(user, mc, "preferred", 0));
    }
}