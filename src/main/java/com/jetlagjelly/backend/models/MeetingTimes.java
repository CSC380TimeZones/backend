package com.jetlagjelly.backend.models;

import java.util.ArrayList;

public class MeetingTimes {

    public ArrayList<Long> startTimes = new ArrayList<>();
    public ArrayList<Long> endTimes = new ArrayList<>();
    public ArrayList<Long> subStartTimes = new ArrayList<>();
    public ArrayList<Long> subEndTimes = new ArrayList<>();

    public void setStartTimes(Long startTime) {
        startTimes.add(startTime);
    }

    public void setEndTimes(Long endTime) {
        endTimes.add(endTime);
    }

    public void setSubStartTimes(Long subStartTime) {
        subStartTimes.add(subStartTime);
    }

    public void setSubEndTimes(Long subEndTime) {
        subEndTimes.add(subEndTime);
    }
}
