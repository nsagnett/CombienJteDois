package nsapp.com.combienjtedois.model;

import java.io.Serializable;

public class Debt implements Serializable {

    private final int id;
    private String amount;
    private final String date;
    private final String reason;

    public Debt(int id, String amount, String reason, String date) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public String getAmount() {
        return amount;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
