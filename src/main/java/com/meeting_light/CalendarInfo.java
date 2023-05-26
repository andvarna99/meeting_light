package com.meeting_light;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class CalendarInfo {
    public static void main(String[] args) throws Exception {

        String timeZone = "America/Chicago";

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(timeZone));

        ZonedDateTime startOfToday = now.toLocalDate().atStartOfDay(now.getZone());

        ZonedDateTime startOfTomorrow = startOfToday.plusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        String timeMin = URLEncoder.encode(formatter.format(startOfToday), StandardCharsets.UTF_8);
        String timeMax = URLEncoder.encode(formatter.format(startOfTomorrow), StandardCharsets.UTF_8);

        String urlTemplate = "https://clients6.google.com/calendar/v3/calendars/andrea.h.varnado@gmail.com/events?calendarId=andrea.h.varnado%40gmail.com&singleEvents=true&timeZone={timeZone}&maxAttendees=1&maxResults=250&sanitizeHtml=true&timeMin={timeMin}&timeMax={timeMax}&key=AIzaSyBNlYH01_9Hc5S1J9vuFmu2nUqBZJNAXxs";

        String url = urlTemplate
                .replace("{timeZone}", URLEncoder.encode(timeZone, StandardCharsets.UTF_8))
                .replace("{timeMin}", timeMin)
                .replace("{timeMax}", timeMax);

        System.out.println(url);



//        URL url = new URL("https://clients6.google.com/calendar/v3/calendars/andrea.h.varnado@gmail.com/events?calendarId=andrea.h.varnado%40gmail.com&singleEvents=true&timeZone=America%2FChicago&maxAttendees=1&maxResults=250&sanitizeHtml=true&timeMin=2023-04-30T00%3A00%3A00-05%3A00&timeMax=2023-06-04T00%3A00%3A00-05%3A00&key=AIzaSyBNlYH01_9Hc5S1J9vuFmu2nUqBZJNAXxs");
        URL newUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) newUrl.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");

//      reads data from url connection bytes = text
        InputStreamReader in = new InputStreamReader(con.getInputStream());
//      reads text line by line,
        BufferedReader br = new BufferedReader(in);
//        System.out.println(br);
        String output;
        StringBuilder responseBuilder = new StringBuilder();


        while((output = br.readLine()) != null){
//            System.out.println(output);
            responseBuilder.append(output);
        }

        String fullResponse = responseBuilder.toString();
        JSONObject jsonObj = new JSONObject(fullResponse);
        JSONArray jsonArr = jsonObj.getJSONArray("items");

        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject item = jsonArr.getJSONObject(i);
            String startDateTime = item.getJSONObject("start").getString("dateTime");
            String endDateTime = item.getJSONObject("end").getString("dateTime");
            System.out.println("Start: " + startDateTime);
            System.out.println("End: " + endDateTime);
        }


        con.disconnect();
    }
}
