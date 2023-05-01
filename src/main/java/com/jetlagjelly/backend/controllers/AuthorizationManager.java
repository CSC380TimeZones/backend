package com.jetlagjelly.backend.controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.github.cdimascio.dotenv.Dotenv;

public class AuthorizationManager {
  private static Dotenv dotenv = Dotenv.load();

  /**
   * Creates the URL used to login using Google Oauth
   */
  public static String getAuthorizationUrl() {
    JsonObject credentials = getCredentials();
    String REDIRECT_URL = dotenv.get("REDIRECT_URL");

    HttpTransport httpTransport = new NetHttpTransport();
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport,
        new GsonFactory(),
        credentials.get("client_id").getAsString(),
        credentials.get("client_secret").getAsString(),
        Arrays.asList("https://www.googleapis.com/auth/userinfo.email", "https://www.googleapis.com/auth/calendar"))
        .build();

    GoogleAuthorizationCodeRequestUrl url = flow
        .newAuthorizationUrl()
        .setRedirectUri(REDIRECT_URL)
        .setAccessType("offline");

    return url.toString();
  }

  /**
   * Takes a code from a URL and exchanges it for an access & refresh token
   * 
   * @param authorizationCode
   * @return
   * @throws IOException
   */
  public static GoogleTokenResponse getTokenFromCode(String authorizationCode) throws IOException {
    JsonObject credentials = getCredentials();
    String REDIRECT_URL = dotenv.get("REDIRECT_URL");

    HttpTransport httpTransport = new NetHttpTransport();
    GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
        httpTransport,
        new GsonFactory(),
        credentials.get("client_id").getAsString(),
        credentials.get("client_secret").getAsString(),
        authorizationCode, REDIRECT_URL)
        .execute();
    return tokenResponse;
  }

  /**
   * Takes a refresh token and exchanges it for a new access & refresh token
   * 
   * @param refreshToken
   * @return
   * @throws IOException
   */
  public static GoogleTokenResponse refreshToken(String refreshToken) throws IOException {
    JsonObject credentials = getCredentials();

    HttpTransport httpTransport = new NetHttpTransport();
    GoogleRefreshTokenRequest refreshTokenRequest = new GoogleRefreshTokenRequest(
        httpTransport,
        new GsonFactory(),
        refreshToken,
        credentials.get("client_secret").getAsString(),
        credentials.get("client_id").getAsString());

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

  /**
   * Returns a Google Credential object with the client parameters, which used for
   * authorizing web requests
   */
  public static GoogleCredential getCredential() {
    JsonObject credentials = getCredentials();

    HttpTransport httpTransport = new NetHttpTransport();
    GoogleCredential credential = new GoogleCredential.Builder()
        .setTransport(httpTransport)
        .setJsonFactory(new GsonFactory())
        .setClientSecrets(

            credentials.get("client_id").getAsString(),
            credentials.get("client_secret").getAsString())
        .build();
    return credential;
  }

  /**
   * Returns the web property of a credential.json file stored at
   * src/main/resources/credentials.json
   */
  private static JsonObject getCredentials() {
    String credentialsPath = System.getProperty("user.dir") + "/src/main/resources/credentials.json";

    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(credentialsPath));
    } catch (FileNotFoundException e) {
      System.out.println("Cannot find file: " + credentialsPath);
      return null;
    }

    Gson gson = new Gson();
    JsonObject json = gson.fromJson(reader, JsonObject.class);

    return (JsonObject) json.get("web");
  }
}
