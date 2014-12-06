package nsapp.com.combienjtedois.model;

import java.io.Serializable;

public class Person implements Serializable {

    private long id;
    private String phoneNumber;
    private String name;
    private String modificationDate;
    private String totalAmount;
    private String imageProfileUrl;

    public Person(long id, String name, String totalAmount, String phoneNumber, String imageProfileUrl, String modificationDate) {
        this.id = id;
        this.name = name;
        this.modificationDate = modificationDate;
        this.phoneNumber = phoneNumber;
        this.totalAmount = totalAmount;
        this.imageProfileUrl = imageProfileUrl;
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

    public String getImageProfileUrl() {
        return imageProfileUrl;
    }

    public void setImageProfileUrl(String imageProfileUrl) {
        this.imageProfileUrl = imageProfileUrl;
    }

    public String getModificationDate() {
        return modificationDate;
    }
}
