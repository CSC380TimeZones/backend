package com.jetlagjelly.backend.controllers;

import com.jetlagjelly.backend.models.*;

import java.io.IOException;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;

/**
 * An easy to use request manager, which contains all of the
 * Non-Authorization-related API Calls
 */
public class GoogleAPIManager {

  private GoogleCredential credential;
  private HttpTransport httpTransport;
  private HttpRequestFactory requestFactory;

  /**
   * Creates a GoogleAPIManager from a String that is the users email
   * 
   * @param email
   */
  GoogleAPIManager(DatabaseManager db, String email) {
    httpTransport = new NetHttpTransport();
    User user = db.fetchUserAsUserObject(email, true);

    credential = AuthorizationManager.getCredential()
        .setAccessToken(user.access_token);

    requestFactory = httpTransport.createRequestFactory(credential);
  }

  /**
   * Creates a GoogleAPIManager from a GoogleTokenResponse
   * 
   * @param db
   * @param tokenResponse
   */
  GoogleAPIManager(DatabaseManager db, GoogleTokenResponse tokenResponse) {
    httpTransport = new NetHttpTransport();

    credential = AuthorizationManager.getCredential()
        .setAccessToken(tokenResponse.getAccessToken());

    requestFactory = httpTransport.createRequestFactory(credential);
  }

  /**
   * Get's a users email given the credentials of the class
   * 
   * @return
   * @throws IOException
   */
  public Payload getUserEmail() throws IOException {
    GenericUrl url = new GenericUrl(
        "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" +
            credential.getAccessToken());
    HttpRequest request = requestFactory.buildGetRequest(url);
    HttpResponse response = request.execute();

    Payload payload = new Gson().fromJson(response.parseAsString(), Payload.class);

    return payload;
  }
}
