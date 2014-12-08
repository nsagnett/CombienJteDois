package nsapp.com.combienjtedois.model;

import java.io.Serializable;

public class Debt implements Serializable{

    private final long id;
    private String amount;
    private final String date;
    private final String reason;
    private final String profileImageUrl;

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

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getDate() {
        return date;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
