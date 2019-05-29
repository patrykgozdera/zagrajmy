package com.example.patryk.zagrajmy;

import java.util.List;

public class EventData {

    private String eventType;
    private Integer nbOfPeople;
    private String eventLocation;
    private String eventDate;
    private String eventDescription;
    private Double eventLat;
    private Double eventLong;
    private List<String> eventParticipants;
    private List<String> eventDeclines;

    public EventData() {}

    public EventData(String eventType, Integer nbOfPeople, String eventLocation, String eventDate, String eventDescription,
                     Double eventLat, Double eventLong, List<String> eventParticipants, List<String> eventDeclines) {
        this.eventType = eventType;
        this.nbOfPeople = nbOfPeople;
        this.eventLocation = eventLocation;
        this.eventDate = eventDate;
        this.eventDescription = eventDescription;
        this.eventLat = eventLat;
        this.eventLong = eventLong;
        this.eventParticipants = eventParticipants;
        this.eventDeclines = eventDeclines;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Integer getNbOfPeople() {
        return nbOfPeople;
    }

    public void setNbOfPeople(Integer nbOfPeople) {
        this.nbOfPeople = nbOfPeople;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public List<String> getEventParticipants() {
        return eventParticipants;
    }

    public void setEventParticipants(List<String> eventParticipants) {
        this.eventParticipants = eventParticipants;
    }

    public List<String> getEventDeclines() {
        return eventDeclines;
    }

    public void setEventDeclines(List<String> eventDeclines) {
        this.eventDeclines = eventDeclines;
    }

    public Double getEventLat() {
        return eventLat;
    }

    public void setEventLat(Double eventLat) {
        this.eventLat = eventLat;
    }

    public Double getEventLong() {
        return eventLong;
    }

    public void setEventLong(Double eventLong) {
        this.eventLong = eventLong;
    }
}
