package com.jetlagjelly.backend;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Setting;
import com.jetlagjelly.backend.models.MeetingContraint;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

/* class to demonstarte use of Calendar events list API */
public class CalendarQuickstart {
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "JetLagJelly";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Directory to store authorization tokens for this application.
     */

    public static ArrayList<Long> events(String token, ArrayList<String> requiredCalendarIDs, MeetingContraint mc)
            throws IOException, GeneralSecurityException {
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(token);
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        // hash map that stores <key:calendarID, value:calendarName>
        HashMap<String, String> calendarsListHT = new HashMap<>();
        // Initialize Calendar service with valid OAuth credentials
        Calendar calendarService = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("applicationName").build();

        // Iterate through entries in calendar list and store them in hash map
        String pageToken = null;
        do {
            CalendarList calendarList = calendarService
                    .calendarList()
                    .list()
                    .setPageToken(pageToken)
                    .execute();
            List<CalendarListEntry> calendarItems = calendarList.getItems();

            for (CalendarListEntry calendarListEntry : calendarItems) {
                calendarsListHT.put(calendarListEntry.getId(), calendarListEntry.getSummary());
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        // print the name of all the calendars
        System.out.println(calendarsListHT);

        ArrayList<Long> eventsList = new ArrayList<>();

        // iterate through hash table and events for the required calendar ID
        eventsList.add(mc.getStartDay());
        eventsList.add(mc.getEndDay());
        for (String requiredCalendarID : requiredCalendarIDs) {
            for (String calendarID : calendarsListHT.keySet()) {
                if (calendarID.equals(requiredCalendarID)) {
                    Date date = new Date(mc.getStartDay());
                    DateTime st = new DateTime(date);
                    Date endate = new Date(mc.getEndDay());
                    DateTime en = new DateTime(endate);
                    Events events = service.events().list(calendarID)
                            .setTimeMin(st)
                            .setTimeMax(en)
                            .setOrderBy("startTime")
                            .setSingleEvents(true)
                            .execute();
                    List<Event> items = events.getItems();
                    if (items.isEmpty()) {
                    } else {
                        for (Event event : items) {
                            DateTime start = event.getStart().getDateTime();
                            DateTime end = event.getEnd().getDateTime();
                            if (start == null) {
                                start = event.getStart().getDate();
                            }
                            if (end == null) {
                                end = event.getEnd().getDate();
                            }
                            Long unixStart = start.getValue();
                            Long unixEnd = end.getValue();
                            eventsList.add(unixStart);
                            eventsList.add(unixEnd);
                        }
                    }
                }
            }
        }
        Collections.sort(eventsList);
        return eventsList;
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        String accessToken = "ya29.a0Ael9sCOLafLREphMeAFWD9aDISbPaTtCHqbOWdadQkP1qlDRONLDJPQi5uDNBTy-sy7f6Mq-QeyOtFJT9X3-ENUqIJpxxUTTlLPP8lCXW-IAeCf1B7bdlafVYVhErVr_K7P34oYwLocJkRrdByJcc7fMjrMdaCgYKAT0SARMSFQF4udJh4h7Paod_XjK1FcrjIEo9bw0163";
        DateTime start = new DateTime(System.currentTimeMillis());
        ;
        DateTime end = DateTime.parseRfc3339("2023-05-13T00:00:00.000-04:00");
        ArrayList<String> calendarID = new ArrayList<>();
        calendarID.add("en.np#holiday@group.v.calendar.google.com");
        calendarID.add("c_pcc74igoo9tcautmfo7vm8pst4@group.calendar.google.com");
        calendarID.add("c_afa653dae9d504dcd2d794c8b230a56752a33d429b24683dff98d5dbdc417d61@group.calendar.google.com");
        getCalendarData(accessToken, calendarID, start, end);
    }

    public static void getCalendarData(String token, ArrayList<String> requiredCalendarIDs, DateTime startTime,
            DateTime endTime) throws IOException, GeneralSecurityException {
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(token);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        // hash map that stores <key:calendarID, value:calendarName>
        HashMap<String, String> calendarsListHT = new HashMap<>();
        // Initialize Calendar service with valid OAuth credentials
        Calendar calendarService = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("applicationName").build();

        // Iterate through entries in calendar list and store them in hash map
        String pageToken = null;
        do {
            CalendarList calendarList = calendarService.calendarList().list().setPageToken(pageToken).execute();
            List<CalendarListEntry> calendarItems = calendarList.getItems();

            for (CalendarListEntry calendarListEntry : calendarItems) {
                calendarsListHT.put(calendarListEntry.getId(), calendarListEntry.getSummary());
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        // print the name of all the calendars
        // System.out.println(calendarsListHT);
        System.out.println(calendarsListHT.values());

        // Retrieve a single user timezone
        Setting setting = service.settings().get("timezone").execute();
        System.out.println(setting.getId() + ": " + setting.getValue());

        // iterate through hash table and events for the required calendar ID
        for (String requiredCalendarID : requiredCalendarIDs) {
            for (String calendarID : calendarsListHT.keySet()) {
                if (calendarID.equals(requiredCalendarID)) {
                    System.out.println("\n" + calendarsListHT.get(calendarID));
                    Events events = service.events().list(calendarID)
                            .setTimeMax(endTime)
                            .setTimeMin(startTime)
                            .setOrderBy("startTime")
                            .setSingleEvents(true)
                            .execute();
                    List<Event> items = events.getItems();
                    if (items.isEmpty()) {
                        System.out.println("No upcoming events found.");
                    } else {
                        System.out.println("Upcoming events");
                        for (Event event : items) {
                            DateTime start = event.getStart().getDateTime();
                            DateTime end = event.getEnd().getDateTime();
                            if (start == null) {
                                start = event.getStart().getDate();
                            }
                            if (end == null) {
                                end = event.getEnd().getDate();
                            }
                            System.out.printf("event: %s, start: (%s), end: (%s)\n", event.getSummary(), start, end);
                        }
                    }
                }
            }
        }
    }
}
