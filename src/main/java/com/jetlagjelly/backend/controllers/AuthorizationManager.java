package com.jetlagjelly.backend.controllers;

import java.io.IOException;
import java.util.Arrays;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import io.github.cdimascio.dotenv.Dotenv;

public class AuthorizationManager {
  private static Dotenv dotenv = Dotenv.load();

  public static String getAuthorizationUrl() {
    final String CLIENT_ID = dotenv.get("GOOGLE_CLIENT_ID");
    final String CLIENT_SECRET = dotenv.get("GOOGLE_CLIENT_SECRET");
    final String REDIRECT_URL = dotenv.get("REDIRECT_URL", "http://localhost:8080/oauth");

    HttpTransport httpTransport = new NetHttpTransport();
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, new GsonFactory(), CLIENT_ID, CLIENT_SECRET,
        Arrays.asList("https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/calendar"))
        .build();

    GoogleAuthorizationCodeRequestUrl url = flow
        .newAuthorizationUrl()
        .setRedirectUri(REDIRECT_URL)
        .setAccessType("offline");

    return url.toString();
  }

  public static GoogleTokenResponse tradeCodeForAccessToken(String authorizationCode) throws IOException {
    final String CLIENT_ID = dotenv.get("GOOGLE_CLIENT_ID");
    final String CLIENT_SECRET = dotenv.get("GOOGLE_CLIENT_SECRET");
    final String REDIRECT_URL = dotenv.get("REDIRECT_URL", "http://localhost:8080/oauth");

    HttpTransport httpTransport = new NetHttpTransport();
    GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
        httpTransport, new GsonFactory(), CLIENT_ID, CLIENT_SECRET,
        authorizationCode, REDIRECT_URL)
        .execute();
    return tokenResponse;
  }

  public static GoogleTokenResponse refreshToken(String refreshToken) throws IOException {
    final String CLIENT_ID = dotenv.get("GOOGLE_CLIENT_ID");
    final String CLIENT_SECRET = dotenv.get("GOOGLE_CLIENT_SECRET");

    HttpTransport httpTransport = new NetHttpTransport();
    GoogleRefreshTokenRequest refreshTokenRequest = new GoogleRefreshTokenRequest(
        httpTransport,
        new GsonFactory(),
        refreshToken,
        CLIENT_SECRET,
        CLIENT_ID);
    try {
      GoogleTokenResponse tokenResponse = refreshTokenRequest.execute();
      return tokenResponse;
    } catch (TokenResponseException e) {
      if (e.getDetails() != null) {
        System.err.println("Error: " + e.getDetails().getError());
        if (e.getDetails().getErrorDescription() != null) {
          System.err.println(e.getDetails().getErrorDescription());
        }
        if (e.getDetails().getErrorUri() != null) {
          System.err.println(e.getDetails().getErrorUri());
        }
      } else {
        System.err.println(e.getMessage());
      }
    }
    return null;
  }

}
