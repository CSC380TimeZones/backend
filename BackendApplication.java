package com.jetlagjelly.backend;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

@SpringBootApplication
public class BackendApplication {
	private static final String CLIENT_ID = System.getenv("CLIENT_ID"); //"1018210986187-ve886ig30rfadhe5ahrmu2tg391ohq8s.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = System.getenv("CLIENT_SECRET");//"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";
	private static final String REDIRECT_URI = System.getenv("REDIRECT_URI"); //"http://localhost:7000/oauth2callback";

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);


	}



	public String exchangeCode(String code) throws IOException, GeneralSecurityException {
		//Set up the Google API client library
		NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

		//Set up the Google Authorization Code Flow
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET,
				Arrays.asList("openid", "email", "profile", "https://www.googleapis.com/auth/calendar"))
				.setAccessType("offline")
				.setApprovalPrompt("force")
				.build();

		////// Generate the authorization URL and redirect the user to it
		////String authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
		////System.out.println("Go to the following link in your browser:");
		////System.out.println(authorizationUrl);

		///// Wait for the user to grant permission and enter the authorization code
		////System.out.println("Enter the authorization code:");
		////Scanner scanner = new Scanner(System.in);
		////String authorizationCode = scanner.nextLine();

		// Exchange the authorization code for an access token and refresh token
		GoogleAuthorizationCodeTokenRequest tokenRequest = flow.newTokenRequest(code);
		tokenRequest.setRedirectUri(REDIRECT_URI);
		GoogleTokenResponse tokenResponse;
		try {
			tokenResponse = tokenRequest.execute();
		} catch (TokenResponseException e) {
			System.err.println("Error exchanging authorization code: " + e.getMessage());
			return null;
		}

		// Create a new Credential object using the access token and refresh token
		Credential credential = flow.createAndStoreCredential(tokenResponse, null);
		//System.out.println(credential.getAccessToken());
		//return access token
		return credential.getAccessToken();

	}
}
