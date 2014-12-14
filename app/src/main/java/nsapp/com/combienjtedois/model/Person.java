package nsapp.com.combienjtedois.model;

import java.io.Serializable;

public class Person implements Serializable {

    private final int id;
    private final String phoneNumber;
    private final String name;
    private final String modificationDate;
    private final String totalAmount;

    public Person(int id, String name, String totalAmount, String phoneNumber, String modificationDate) {
        this.id = id;
        this.name = name;
        this.modificationDate = modificationDate;
        this.phoneNumber = phoneNumber;
        this.totalAmount = totalAmount;
    }

    public int getId() {
        return id;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getModificationDate() {
        return modificationDate;
    }
}
