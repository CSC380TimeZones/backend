package com.jetlagjelly.backend.models;

import java.time.DayOfWeek;

public class MeetingContraint {

    private String email;
    private int mtngLength;
    private Long startDay;
    private Long endDay;
    private Long subStartDay;
    private Long subEndDay;

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

    public Long getSubStartDay() {
        return subStartDay;
    }

    public void setSubStartDay(Long subStartDay) {
        this.subStartDay = subStartDay;
    }

    public Long getSubEndDay() {
        return subEndDay;
    }

    public void setSubEndDay(Long subEndDay) {
        this.subEndDay = subEndDay;
    }
}
