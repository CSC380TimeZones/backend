package com.jetlagjelly.backend.controllers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseManager {
    public static void main(String[] args) {

        MongoClient client = MongoClients.create("mongodb+srv://bmclean2:bmclean03@clusterjlj.jottkkm.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = client.getDatabase("JetLagJelly");
        MongoCollection collection = db.getCollection("users");
        String[] cid = new String[]{"calendar_id"};
        String[] dy = new String[]{"days"};
        int[] st = new int[]{6,3};
        int[] en = new int[]{8,4};
        List<String> sca = new ArrayList<>();
        sca.add("sc");
        List<String> cida = new ArrayList<>();
        cida.add("calendar_id");
        List<String> dya = new ArrayList<>();
        dya.add("days");
        List<Integer> sta = new ArrayList<>();
        sta.add(3);
        List<Integer> ena = new ArrayList<>();
        ena.add(4);


        Document document;
        document = newUser("bmclean2@oswego.edu", "at", "rt", 6,sca, "tt", 8, cida, sta, ena, dya);


    }

    public static Document newUser(String email, String access_token, String refresh_token, int expires_at, List<String> scope, String token_type, int timezone, List<String> calendar_id, List<Integer> start, List<Integer> end, List<String> days) {
        Document tr = new Document().append("start", start).append("end", end).append("days", days);
        Document sampleDoc = new Document("email", email).append("access_token", access_token).append("refresh_token", refresh_token).append("expires_at", expires_at).append("scope", scope).append("token_type", token_type).append("timezone", timezone).append("calendar_id", calendar_id).append("timerange", tr);
        return sampleDoc;
    }
}
