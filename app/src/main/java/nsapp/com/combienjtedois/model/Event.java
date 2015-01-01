package nsapp.com.combienjtedois.model;

import java.io.Serializable;

public class Event implements Serializable{

    private final int idEvent;
    private final String consignee;
    private final String participantNumber;
    private final String subject;
    private final String value;
    private final String date;

    public Event(int idEvent, String consignee, String participantNumber, String present, String value, String date) {
        this.idEvent = idEvent;
        this.consignee = consignee;
        this.participantNumber = participantNumber;
        this.subject = present;
        this.value = value;
        this.date = date;
    }

    public int getIdEvent() {
        return idEvent;
    }

    public String getConsignee() {
        return consignee;
    }

    public String getParticipantNumber() {
        return participantNumber;
    }

    public String getSubject() {
        return subject;
    }

    public String getValue() {
        return value;
    }

    public String getDate() {
        return date;
    }
}