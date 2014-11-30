package nsapp.com.combienjtedois.model;

public class Debt {

    private long id;
    private String amount;
    private String date;
    private String reason;
    private String profileImageUrl;

    public Debt(long id, String amount, String reason, String profileImageUrl, String date){
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.reason = reason;
        this.profileImageUrl = profileImageUrl;
    }

    public String getReason() {
        return reason;
    }

    public String getAmount() {
        return amount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
