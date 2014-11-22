package nsapp.com.combienjtedois.model;

import java.io.Serializable;

public class Person implements Serializable {

    private long id;
    private String phoneNumber;
    private String name;
    private String totalAmount;

    public Person(long id, String name, String totalAmount, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.totalAmount = totalAmount;
    }

    public long getId() {
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

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
