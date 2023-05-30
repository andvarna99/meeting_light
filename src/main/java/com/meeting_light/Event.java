package com.meeting_light;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Event {
    private ZonedDateTime startDateTime;
    private ZonedDateTime endDateTime;
}
