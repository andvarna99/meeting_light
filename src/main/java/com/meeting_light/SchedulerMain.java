package com.meeting_light;

import java.util.Timer;

public class SchedulerMain {
    public static void main(String[] args) {
        Timer time = new Timer(); // Instantiate Timer Object
        CalendarInfo ci = new CalendarInfo(); // Instantiate SheduledTask class
        time.schedule(ci, 0, 300000); //create task for every 5 mins
    }
}
