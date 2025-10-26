package com.coderisle.residentisle.models;

import java.util.Date;
import java.util.List;

public class EventModel {
    private String eventId;
    private String title;
    private String description;
    private String location;
    private Date date;
    private List<String> participants;
    private Long pointsReward;

    public EventModel() {} // Firestore requires empty constructor

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public Date getDate() { return date; }
    public List<String> getParticipants() { return participants; }
    public Long getPointsReward() { return pointsReward; }
}

