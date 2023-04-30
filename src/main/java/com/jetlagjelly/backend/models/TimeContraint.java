package com.jetlagjelly.backend.models;

import com.jetlagjelly.backend.controllers.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class TimeContraint {
    public static void main(String[] args) {
        List<String> sca = new ArrayList<>();
        sca.add("create");
        List<String> cida = new ArrayList<>();
        cida.add("Phases of the Moon");
        List<List<Boolean>> dyya = new ArrayList<>();
        List<Boolean> dya = new ArrayList<>();
        dya.add(0, true);
        dya.add(1, true);
        dya.add(2, true);
        dya.add(3, true);
        dya.add(4, false);
        dya.add(5, false);
        dya.add(6, false);
        List<Boolean> dyaa = new ArrayList<>();
        dyaa.add(0, false);
        dyaa.add(1, false);
        dyaa.add(2, false);
        dyaa.add(3, false);
        dyaa.add(4, true);
        dyaa.add(5, false);
        dyaa.add(6, false);
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
        sta.add(6.00);
        sta.add(12.00);
        List<Double> ena = new ArrayList<>();
        ena.add(13.00);
        ena.add(20.00);
        List<Double> ss = new ArrayList<>();
        ss.add(2.00);
        List<Double> se = new ArrayList<>();
        se.add(8.00);

        DatabaseManager.User user = new DatabaseManager.User("bmclean2@oswego.edu", "MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3",
                "IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk", 3600L, sca, "Bearer", -4,
                cida, sta, ena, dyya, ss, se, sda);
        MeetingContraint mc = new MeetingContraint();
        mc.setStartDay(1682913600000L);
        mc.setEndDay(1683342000000L);
        System.out.println(DatabaseManager.concreteTime(user, mc));

    }
}
