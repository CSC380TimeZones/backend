package com.jetlagjelly.backend.models;

import java.time.DayOfWeek;

public class MeetingContraint {

    private String email;
    private int mtngLength;
    private Long startDay;
    private Long endDay;

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


    public Long getStartDay() {
        return startDay;
    }

    public void setStartDay(Long startDay) {
        this.startDay = startDay;
    }

    public Long getEndDay() {
        return endDay;
    }

    public void setEndDay(Long endDay) {
        this.endDay = endDay;
    }
}
