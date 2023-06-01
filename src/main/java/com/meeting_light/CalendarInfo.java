package com.meeting_light;


import com.meeting_light.model.Event;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TimerTask;


@SpringBootApplication
public class CalendarInfo extends TimerTask {

    static Properties props = new Properties();

    public static InputStream is;

    static {
        try {
            is = new FileInputStream("config.properties");
            props.load(is);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String plugIP = props.getProperty("IP_ADDRESS");
    public static int plugPort = 9999;

    public static net.insxnity.hs100.HS100 plug = new net.insxnity.hs100.HS100(plugIP, plugPort);

    public static String timeZone = "America/Chicago";

    public static ZonedDateTime now = ZonedDateTime.now(ZoneId.of(timeZone));

    public  void run(){

        String url = generateURL(timeZone);

        String fullResponse = null;
        try {
            fullResponse = sendGetRequest(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Event> events = printEvents(fullResponse);

        System.out.println(events.toString());

        System.out.println(checkIfBusy(events, now));

        if(checkIfBusy(events, now)){
            try {
                turnLightOn(plug);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            try {
                turnLightOff(plug);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String generateURL(String timeZone){

        ZonedDateTime startOfToday = now.toLocalDate().atStartOfDay(now.getZone());

        ZonedDateTime startOfTomorrow = startOfToday.plusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        String timeMin = URLEncoder.encode(formatter.format(startOfToday), StandardCharsets.UTF_8);
        String timeMax = URLEncoder.encode(formatter.format(startOfTomorrow), StandardCharsets.UTF_8);

        String urlTemplate = props.getProperty("URL_TEMPLATE");

        return urlTemplate
                .replace("{timeZone}", URLEncoder.encode(timeZone, StandardCharsets.UTF_8))
                .replace("{timeMin}", timeMin)
                .replace("{timeMax}", timeMax);
    }

    public static String sendGetRequest(String url)throws Exception{
        URL newUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) newUrl.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");

//      reads data from url connection bytes = text
        InputStreamReader in = new InputStreamReader(con.getInputStream());
//      reads text line by line,
        BufferedReader br = new BufferedReader(in);
        String output;
        StringBuilder responseBuilder = new StringBuilder();


        while((output = br.readLine()) != null){
            responseBuilder.append(output);
        }

        con.disconnect();

        return responseBuilder.toString();
    }

    public static List<Event> printEvents(String fullResponse){
        List<Event> events = new ArrayList<>();
        JSONObject jsonObj = new JSONObject(fullResponse);
        JSONArray jsonArr = jsonObj.getJSONArray("items");

        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject item = jsonArr.getJSONObject(i);
            String startDateTime = item.getJSONObject("start").getString("dateTime");
            String endDateTime = item.getJSONObject("end").getString("dateTime");
            ZonedDateTime zonedStartDateTime = ZonedDateTime.parse(startDateTime);
            ZonedDateTime zonedEndDateTime = ZonedDateTime.parse(endDateTime);
            events.add(new Event(zonedStartDateTime,zonedEndDateTime));
        }
        return events;
    }
    public static void turnLightOn(net.insxnity.hs100.HS100 hs100) throws IOException {
        hs100.switchOn();
    }

    private static void turnLightOff(net.insxnity.hs100.HS100 hs100) throws IOException {
        hs100.switchOff();
    }

    public static boolean checkIfBusy(List<Event> events, ZonedDateTime timeToCheck){
        for (Event event: events) {
            if(!timeToCheck.isBefore(event.getStartDateTime()) && !timeToCheck.isAfter(event.getEndDateTime())){
                return true;
            }
        }
        return false;
    }
}
