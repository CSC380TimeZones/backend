package com.jetlagjelly.backend.models;

import com.jetlagjelly.backend.controllers.DatabaseManager;
import com.jetlagjelly.backend.controllers.MeetingManager;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jetlagjelly.backend.CalendarQuickstart.events;
import static com.jetlagjelly.backend.controllers.DatabaseManager.collection;
import static com.jetlagjelly.backend.controllers.DatabaseManager.fetchUser;

public class TimeContraint {
    public static void main(String[] args) throws GeneralSecurityException, IOException {

    }
}
