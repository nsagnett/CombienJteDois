package nsapp.com.combienjtedois.model;

import java.util.ArrayList;

public class SortClass {

    public static ArrayList<Person> increasingOrderPersonAmountSort(ArrayList<Person> persons) {
        ArrayList<Person> personsList = new ArrayList<Person>();
        do {
            double min = Integer.MAX_VALUE;
            int index = 0;
            for (int i = 0; i < persons.size(); i++) {
                double amount = Double.parseDouble(persons.get(i).getTotalAmount());
                if (amount < min) {
                    min = amount;
                    index = i;
                }
            }
            personsList.add(persons.get(index));
            persons.remove(index);
        } while (!persons.isEmpty());

        return personsList;
    }

    public static ArrayList<Person> decreasingOrderPersonAmountSort(ArrayList<Person> persons) {
        ArrayList<Person> personsList = new ArrayList<Person>();
        do {
            double max = Integer.MIN_VALUE;
            int index = 0;
            for (int i = 0; i < persons.size(); i++) {
                double amount = Double.parseDouble(persons.get(i).getTotalAmount());
                if (amount > max) {
                    max = amount;
                    index = i;
                }
            }
            personsList.add(persons.get(index));
            persons.remove(index);
        } while (!persons.isEmpty());

        return personsList;
    }

    public static ArrayList<Debt> increasingOrderDebtAmountSort(ArrayList<Debt> debts) {
        ArrayList<Debt> debtArrayList = new ArrayList<Debt>();
        do {
            double min = Integer.MAX_VALUE;
            int index = 0;
            for (int i = 0; i < debts.size(); i++) {
                double amount = Double.parseDouble(debts.get(i).getAmount());
                if (amount < min) {
                    min = amount;
                    index = i;
                }
            }
            debtArrayList.add(debts.get(index));
            debts.remove(index);
        } while (!debts.isEmpty());

        return debtArrayList;
    }

    public static ArrayList<Debt> decreasingOrderDebtAmountSort(ArrayList<Debt> debts) {
        ArrayList<Debt> debtArrayList = new ArrayList<Debt>();
        do {
            double max = Integer.MIN_VALUE;
            int index = 0;
            for (int i = 0; i < debts.size(); i++) {
                double amount = Double.parseDouble(debts.get(i).getAmount());
                if (amount > max) {
                    max = amount;
                    index = i;
                }
            }
            debtArrayList.add(debts.get(index));
            debts.remove(index);
        } while (!debts.isEmpty());

        return debtArrayList;
    }
}
