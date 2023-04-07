package com.jetlagjelly.backend.models;

import java.time.DayOfWeek;

public class MeetingContraint {

    private String email;
    private int mtngLength;
    private DayOfWeek startDay;
    private DayOfWeek endDay;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getMtngLength() {
        return mtngLength;
    }

    public void setMtngLength(int mtngLength) {
        this.mtngLength = mtngLength;
    }


    public DayOfWeek getStartDay() {
        return startDay;
    }

    public void setStartDay(DayOfWeek startDay) {
        this.startDay = startDay;
    }

    public DayOfWeek getEndDay() {
        return endDay;
    }

    public void setEndDay(DayOfWeek endDay) {
        this.endDay = endDay;
    }
}
