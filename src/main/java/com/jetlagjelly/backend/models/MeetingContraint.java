package com.jetlagjelly.backend.models;

public class MeetingContraint {

    private String email;
    private int mtngLength;
    private int daysInAdv;

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

    public int getDaysInAdv() {
        return daysInAdv;
    }

    public void setDaysInAdv(int daysInAdv) {
        this.daysInAdv = daysInAdv;
    }
}
