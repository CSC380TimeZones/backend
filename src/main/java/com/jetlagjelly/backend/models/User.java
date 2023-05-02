package com.jetlagjelly.backend.models;

import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

public class User {
  public String email;
  public String access_token;
  public String refresh_token;
  public Long expires_at;
  public List<String> scope;
  public String token_type;
  public double timezone;
  public List<String> calendar_id;
  public List<Double> start;
  public List<Double> end;
  public List<List<Boolean>> days;
  public List<Double> substart;
  public List<Double> subend;
  public List<List<Boolean>> subdays;

  public User(String em, String a, String r, Long ex, List<String> sc,
      String tt, double t, List<String> cid, List<Double> s,
      List<Double> e, List<List<Boolean>> d, List<Double> ss,
      List<Double> se, List<List<Boolean>> sd) {
    email = em;
    access_token = a;
    refresh_token = r;
    expires_at = ex;
    scope = sc;
    token_type = tt;
    timezone = t;
    calendar_id = cid;
    start = s;
    end = e;
    days = d;
    substart = ss;
    subend = se;
    subdays = sd;
  }

  public User updateOauthProperties(GoogleTokenResponse tokenResponse) {
    access_token = tokenResponse.getAccessToken();
    refresh_token = tokenResponse.getRefreshToken();
    expires_at = System.currentTimeMillis() + tokenResponse.getExpiresInSeconds() * 1000;
    token_type = tokenResponse.getTokenType();

    return this;
  }
}