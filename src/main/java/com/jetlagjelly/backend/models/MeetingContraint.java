package com.jetlagjelly.backend.models;

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

    public MeetingContraint setEmail(String email) {
        this.email = email;
        return this;
    }

    public int getMtngLength() {
        return mtngLength;
    }

    public MeetingContraint setMtngLength(int mtngLength) {
        this.mtngLength = mtngLength;
        return this;
    }

    public Long getStartDay() {
        return startDay;
    }

    public MeetingContraint setStartDay(Long startDay) {
        this.startDay = startDay;
        return this;
    }

    public Long getEndDay() {
        return endDay;
    }

    public MeetingContraint setEndDay(Long endDay) {
        this.endDay = endDay;
        return this;
    }

    public Long getSubStartDay() {
        return subStartDay;
    }

    public MeetingContraint setSubStartDay(Long subStartDay) {
        this.subStartDay = subStartDay;
        return this;
    }

    public Long getSubEndDay() {
        return subEndDay;
    }

    public MeetingContraint setSubEndDay(Long subEndDay) {
        this.subEndDay = subEndDay;
        return this;
    }
}
