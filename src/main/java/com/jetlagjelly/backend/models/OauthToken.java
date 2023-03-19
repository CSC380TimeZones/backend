package com.jetlagjelly.backend.models;

import java.util.Date;

public class OauthToken {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String[] scope;
    private long expiresAt;

    public OauthToken(String a, String r, String t, String[] s, int expiresIn) {
        accessToken = a;
        refreshToken = r;
        tokenType = t;
        scope = s;
        expiresAt = new Date().getTime() + expiresIn;
    }

    public String getAuthorizationHeader() {
        return tokenType + " " + accessToken;
    }
}
