package com.jetlagjelly.backend.models;

import java.util.ArrayList;

public class MeetingTimes {

    public static ArrayList<Long> startTimes = new ArrayList<>();
    public static ArrayList<Long> endTimes = new ArrayList<>();

    public static void setStartTimes(Long startTime) {
        startTimes.add(startTime);
    }

    public static void setEndTimes(Long endTime) {
        endTimes.add(endTime);
    }
}
