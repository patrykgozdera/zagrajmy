package com.example.patryk.zagrajmy;

public class LP_Util {

    public static String getEventInfo(EventData eventData) {
        String evInfo = eventData.getEventType();
        evInfo += " • " + eventData.getEventLocation();
        evInfo += " • " + eventData.getEventParticipants().size() + "/" + eventData.getNbOfPeople() + " osób";

        return evInfo;
    }
}
