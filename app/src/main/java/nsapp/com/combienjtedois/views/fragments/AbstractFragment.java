package nsapp.com.combienjtedois.views.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.DBManager;
import nsapp.com.combienjtedois.model.Debt;
import nsapp.com.combienjtedois.model.Person;
import nsapp.com.combienjtedois.model.Tools;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;
import nsapp.com.combienjtedois.views.adapters.DebtListAdapter;
import nsapp.com.combienjtedois.views.adapters.PersonListAdapter;

public abstract class AbstractFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    protected static final String ARG_SECTION_NUMBER = "section_number";
    protected static final String PERSON_KEY = "person";

    protected ArrayList<Person> personArrayList = new ArrayList<Person>();
    protected ArrayList<Debt> debtArrayList = new ArrayList<Debt>();

    protected ListView listView;
    protected TextView headerView;
    protected TextView footerView;

    protected Person person;

    protected boolean isDeletingView;
    protected boolean isEditingView;

    public enum listWantedType {PERSON, DEBT}

    protected listWantedType listType;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_listview, container, false);

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        headerView = (TextView) inflater.inflate(R.layout.header_listview, null, false);
        footerView = (TextView) inflater.inflate(R.layout.footer_listview, null, false);
        getActivity().supportInvalidateOptionsMenu();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((LaunchActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public void notifyChanges(listWantedType type) {
        Cursor c;
        listType = type;
        switch (type) {
            case PERSON:
                c = Tools.dbManager.fetchAllPersons();
                personArrayList = new ArrayList<Person>();

                while (c.moveToNext()) {
                    String name = c.getString(c.getColumnIndex(DBManager.NAME_PERSON_KEY));
                    String phoneNumber = c.getString(c.getColumnIndex(DBManager.PHONE_NUMBER_KEY));

                    long id = Tools.dbManager.fetchIdPerson(name);
                    String total = Tools.dbManager.getTotalCount(id);
                    personArrayList.add(new Person(id, name, total, phoneNumber));
                }

                if (personArrayList.isEmpty()) {
                    footerView.setText(R.string.empty_footer_person);
                    footerView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_add, 0, 0);
                    footerView.setOnClickListener(this);
                    listView.addFooterView(footerView);
                } else {
                    listView.removeFooterView(footerView);
                }

                PersonListAdapter personListAdapter = new PersonListAdapter(getActivity(), personArrayList);
                listView.setAdapter(personListAdapter);

                break;
            case DEBT:
                long idPerson = person.getId();
                c = Tools.dbManager.fetchAllDebt(idPerson);
                debtArrayList = new ArrayList<Debt>();

                while (c.moveToNext()) {
                    String reason = c.getString(c.getColumnIndex(DBManager.REASON_KEY));

                    long id = Tools.dbManager.fetchIdDebt(idPerson, reason);
                    String amount = Tools.dbManager.getCount(id);
                    debtArrayList.add(new Debt(id, idPerson, amount, reason));
                }

                if (debtArrayList.isEmpty()) {
                    footerView.setText(R.string.empty_footer_money);
                    footerView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_add, 0, 0);
                    footerView.setOnClickListener(this);
                    listView.addFooterView(footerView);
                } else {
                    listView.removeFooterView(footerView);
                }

                DebtListAdapter debtListAdapter = new DebtListAdapter(getActivity(), debtArrayList);
                listView.setAdapter(debtListAdapter);

                headerView.setText(String.format(getString(R.string.person_info_format), person.getName(),
                        String.format(getString(R.string.money_format), Tools.dbManager.getTotalCount(person.getId()))));

                break;
            default:
                break;
        }
    }

    public void addPerson() {
        Tools.dbManager.createPerson("TEST");
        notifyChanges(listWantedType.PERSON);
    }

    public void addDebt() {
        Tools.dbManager.createDebt(person.getId(), "90", "DETTE");
        notifyChanges(listWantedType.DEBT);
    }


    @Override
    public void onClick(View v) {
        switch (listType) {
            case PERSON:
                addPerson();
                break;
            case DEBT:
                addDebt();
                break;
            default:
                break;
        }
    }

    public boolean isDeletingView() {
        return isDeletingView;
    }
    public boolean isEditingView() {
        return isEditingView;
    }

    public void setDeletingView(boolean isDeletingView) {
        this.isDeletingView = isDeletingView;
    }
    public void setEditingView(boolean isEditingView) {
        this.isEditingView = isEditingView;
    }
}
