package nsapp.com.combienjtedois.model;

public class Debt {

    private long id;
    private long idPerson;
    private String amount;
    private String reason;

    public Debt(long id, long idPerson, String amount, String reason){
        this.id = id;
        this.idPerson = idPerson;
        this.amount = amount;
        this.reason = reason;
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
}
