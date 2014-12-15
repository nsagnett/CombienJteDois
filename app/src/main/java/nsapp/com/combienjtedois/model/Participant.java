package nsapp.com.combienjtedois.model;

import java.io.Serializable;

public class Participant implements Serializable {

    private final int id;
    private final String name;
    private final String phoneNumber;
    private final String budget;
    private final boolean isPaid;

    public Participant(int id, String name, String phoneNumber, String budget, boolean isPaid) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.budget = budget;
        this.isPaid = isPaid;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getBudget() {
        return budget;
    }

    public boolean isPaid() {
        return isPaid;
    }
}
