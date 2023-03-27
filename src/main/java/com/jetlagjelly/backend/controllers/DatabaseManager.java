package com.jetlagjelly.backend.controllers;

import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatabaseManager {
    public static void main(String[] args) {

        MongoClient client = MongoClients.create("mongodb://localhost:27017/");
        MongoDatabase db = client.getDatabase("JetLagJelly");
        MongoCollection collection = db.getCollection("users");


        List<String> sca = new ArrayList<>();
        sca.add("sc");
        List<String> cida = new ArrayList<>();
        cida.add("calendar_id");
        List<String> dya = new ArrayList<>();
        dya.add("days");
        List<String> sd = new ArrayList<>();
        dya.add("sub-optimal days");
        List<Integer> sta = new ArrayList<>();
        sta.add(3);
        List<Integer> ena = new ArrayList<>();
        ena.add(4);
        List<Integer> ss = new ArrayList<>();
        sta.add(2);
        List<Integer> se = new ArrayList<>();
        ena.add(8);

        User user = new User("bmclean2@oswego.edu", "at", "rt", 6,sca, "tt", 8, cida, sta, ena, dya, ss, se, sd);


        Document document;
        //document = newUser(user);
        //collection.insertOne(document);


        //document = fetchUser(collection, "bmclean2@oswego.edu");
        //System.out.println(document.get("access_token"));

        //document = meetingMgr(collection, user);
        //System.out.println(document);

        //deleteUser(collection, user);

        //setTimezone(user, 13);
        //document = newUser(user);
        //deleteUser(collection, user);
        //collection.insertOne(document);

    }

    static final class User {
        String email;
        String access_token;
        String refresh_token;
        int expires_at;
        List<String> scope;
        String token_type;
        int timezone;
        List<String> calendar_id;
        List<Integer> start;
        List<Integer> end;
        List<String> days;
        List<Integer> substart;
        List<Integer> subend;
        List<String> subdays;
        User(String em, String a, String r, int ex, List<String> sc, String tt, int t, List<String> cid, List<Integer> s, List<Integer> e, List<String> d, List<Integer> ss, List<Integer> se, List<String> sd) {email = em; access_token = a; refresh_token = r; expires_at = ex; scope = sc; token_type = tt; timezone = t; calendar_id= cid; start = s; end = e; days = d; substart = ss; subend = se; subdays = sd;}
    }

    public static Document newUser(User user) {
        Document tr = new Document().append("start", user.start).append("end", user.end).append("days", user.days);
        Document st = new Document().append("suboptimal_start", user.substart).append("suboptimal_end", user.subend).append("suboptimal_days", user.subdays);
        Document sampleDoc = new Document("email", user.email).append("access_token", user.access_token).append("refresh_token", user.refresh_token).append("expires_at", user.expires_at).append("scope", user.scope).append("token_type", user.token_type).append("timezone", user.timezone).append("calendar_id", user.calendar_id).append("preferred_timerange", tr).append("suboptimal_timerange", st);
        return sampleDoc;
    }

    public static Document meetingMgr(MongoCollection collection, User user) {

        Bson projectionFields = Projections.fields(
                Projections.include( "email", "timezone", "calendar_id", "preferred_timerange", "start", "end", "days", "suboptimal_timerange", "suboptimal_start", "suboptimal_end", "suboptimal_days"),
                Projections.excludeId());
        Document doc = (Document) collection.find(eq("email", user.email)).projection(projectionFields).first();
        return doc;

    }

    public static Document fetchUser(MongoCollection collection, String email) {

        Bson projectionFields = Projections.fields(
                Projections.include("email", "access_token", "refresh_token", "expires_at", "scope", "token_type", "timezone", "calendar_id", "preferred_timerange", "start", "end", "days", "suboptimal_timerange", "suboptimal_start", "suboptimal_end", "suboptimal_days"),
                Projections.excludeId());
        Document doc = (Document) collection.find(eq("email", email)).projection(projectionFields).first();
        return doc;
    }

    public static void deleteUser(MongoCollection collection, User user) {

        Bson query = eq("email", user.email);
        collection.deleteOne(query);

    }

    public static Document tokens(User user){
        Document accessToken = new Document()
                .append("token", user.access_token)
                .append("type", user.token_type)
                .append("expires_at", user.expires_at)
                .append("scope", user.scope)
                .append("refreshToken", user.refresh_token);

        return accessToken;
    }

    public static void setTimezone(DatabaseManager.User user, int tz) {
        user.timezone = tz;
    }
}
