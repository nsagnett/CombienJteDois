package nsapp.com.combienjtedois.model;

public class Debt {

    private long id;
    private String amount;
    private String reason;
    private String profileImageUrl;

    public Debt(long id, String amount, String reason, String profileImageUrl){
        this.id = id;
        this.amount = amount;
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
}
