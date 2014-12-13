package nsapp.com.combienjtedois.model;

public class Present {

    private final int idPresent;
    private final String consignee;
    private final String participantNumber;
    private final String present;
    private final String value;
    private final String date;

    public Present(int idPresent, String consignee, String participantNumber, String present, String value, String date) {
        this.idPresent = idPresent;
        this.consignee = consignee;
        this.participantNumber = participantNumber;
        this.present = present;
        this.value = value;
        this.date = date;
    }

    public int getIdPresent() {
        return idPresent;
    }

    public String getConsignee() {
        return consignee;
    }

    public String getParticipantNumber() {
        return participantNumber;
    }

    public String getPresent() {
        return present;
    }

    public String getValue() {
        return value;
    }

    public String getDate() {
        return date;
    }
}