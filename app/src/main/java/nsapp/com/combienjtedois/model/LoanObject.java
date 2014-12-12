package nsapp.com.combienjtedois.model;

public class LoanObject {

    private final int idLoanObject;
    private final String namePerson;
    private final String category;
    private final String nameObject;
    private final String type;
    private final String date;

    public LoanObject(int idLoanObject, String namePerson, String category, String nameObject, String type, String date) {
        this.idLoanObject = idLoanObject;
        this.namePerson = namePerson;
        this.category = category;
        this.nameObject = nameObject;
        this.type = type;
        this.date = date;
    }

    public int getIdLoanObject() {
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