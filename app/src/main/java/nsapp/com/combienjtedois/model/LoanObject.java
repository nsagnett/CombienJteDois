package nsapp.com.combienjtedois.model;

public class LoanObject {

    private long idLoanObject;
    private String namePerson;
    private String category;
    private String nameObject;
    private String type;
    private String date;

    public LoanObject(long idLoanObject, String namePerson, String category, String nameObject, String type, String date) {
        this.idLoanObject = idLoanObject;
        this.namePerson = namePerson;
        this.category = category;
        this.nameObject = nameObject;
        this.type = type;
        this.date = date;
    }

    public long getIdLoanObject() {
        return idLoanObject;
    }

    public String getNamePerson() {
        return namePerson;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getNameObject() {
        return nameObject;
    }

    public String getDate() {
        return date;
    }
}