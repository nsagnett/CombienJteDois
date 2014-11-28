package nsapp.com.combienjtedois.views.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.AllDatas;
import nsapp.com.combienjtedois.model.DBManager;
import nsapp.com.combienjtedois.model.Debt;
import nsapp.com.combienjtedois.model.Person;
import nsapp.com.combienjtedois.model.SortClass;
import nsapp.com.combienjtedois.model.ViewCreator;
import nsapp.com.combienjtedois.views.adapters.DebtListAdapter;
import nsapp.com.combienjtedois.views.adapters.PersonListAdapter;

public abstract class AbstractMoneyFragment extends AbstractFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    protected ArrayList<Person> personArrayList = new ArrayList<Person>();
    protected ArrayList<Debt> debtArrayList = new ArrayList<Debt>();

    protected ListView listView;
    protected TextView headerPersonView;
    protected TextView footerView;

    protected TextView positiveSort;
    protected TextView negativeSort;

    protected int sortIndex;
    protected Person person;

    protected listWantedType listType;

    protected Uri capturedImageURI;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_listview, container, false);

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        headerPersonView = (TextView) inflater.inflate(R.layout.header_listview, null, false);
        footerView = (TextView) inflater.inflate(R.layout.footer_listview, null, false);

        negativeSort = (TextView) view.findViewById(R.id.negativeSort);
        positiveSort = (TextView) view.findViewById(R.id.positiveSort);

        sortIndex = -1;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewCreator.switchView(getActivity(), positiveSort, negativeSort, this);
    }

    @Override
    public void onClick(View v) {
        addItem(null, null);
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public void notifyChanges() {
        Cursor c;
        switch (listType) {
            case PERSON:
                c = AllDatas.dbManager.fetchAllPersons();
                personArrayList = new ArrayList<Person>();

                while (c.moveToNext()) {
                    String name = c.getString(c.getColumnIndex(DBManager.NAME_PERSON_KEY));
                    String phoneNumber = c.getString(c.getColumnIndex(DBManager.PHONE_NUMBER_KEY));
                    String imageUrl = c.getString(c.getColumnIndex(DBManager.IMAGE_PROFILE));

                    long id = AllDatas.dbManager.fetchIdPerson(name);
                    String total = AllDatas.dbManager.getTotalCount(id);
                    personArrayList.add(new Person(id, name, total, phoneNumber, imageUrl));
                }

                if (personArrayList.isEmpty()) {
                    isDeletingView = false;
                    isEditingView = false;
                    if (listView.getFooterViewsCount() == 0) {
                        footerView.setText(R.string.add_person);
                        footerView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.dark_add, 0, 0);
                        footerView.setOnClickListener(this);
                        listView.addFooterView(footerView);
                    }
                } else {
                    listView.removeFooterView(footerView);

                    if (sortIndex == 0) {
                        personArrayList = SortClass.decreasingOrderPersonAmountSort(personArrayList);
                    } else if (sortIndex == 1) {
                        personArrayList = SortClass.increasingOrderPersonAmountSort(personArrayList);
                    }
                }

                PersonListAdapter personListAdapter = new PersonListAdapter(launchActivity, (MoneyFragment) launchActivity.getCurrentFragment(), personArrayList, isDeletingView, isEditingView);
                listView.setAdapter(personListAdapter);

                break;
            case DEBT:
                long idPerson = person.getId();
                c = AllDatas.dbManager.fetchAllDebt(idPerson);
                debtArrayList = new ArrayList<Debt>();

                while (c.moveToNext()) {
                    String reason = c.getString(c.getColumnIndex(DBManager.REASON_KEY));
                    String profileImage = c.getString(c.getColumnIndex(DBManager.IMAGE_PROFILE));

                    long id = AllDatas.dbManager.fetchIdDebt(idPerson, reason);
                    String amount = AllDatas.dbManager.getCount(id);
                    debtArrayList.add(new Debt(id, amount, reason, profileImage));
                }

                if (debtArrayList.isEmpty()) {
                    isDeletingView = false;
                    isEditingView = false;
                    if (listView.getFooterViewsCount() == 0) {
                        footerView.setText(R.string.add_debt);
                        footerView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.dark_add, 0, 0);
                        footerView.setOnClickListener(this);
                        listView.addFooterView(footerView);
                    }
                } else {
                    listView.removeFooterView(footerView);

                    if (sortIndex == 0) {
                        debtArrayList = SortClass.decreasingOrderDebtAmountSort(debtArrayList);
                    } else if (sortIndex == 1) {
                        debtArrayList = SortClass.increasingOrderDebtAmountSort(debtArrayList);
                    }
                }

                DebtListAdapter debtListAdapter = new DebtListAdapter(launchActivity, (DetailMoneyFragment) launchActivity.getCurrentFragment(), debtArrayList, isDeletingView, isEditingView);
                listView.setAdapter(debtListAdapter);

                Double total = Double.parseDouble(AllDatas.dbManager.getTotalCount(person.getId()));

                if (total >= 0) {
                    headerPersonView.setTextColor(getResources().getColor(R.color.green));
                } else {
                    headerPersonView.setTextColor(getResources().getColor(R.color.red));
                }

                headerPersonView.setText(String.format(getString(R.string.person_info_format), person.getName(),
                        String.format(getString(R.string.money_format), total)));
                break;
            default:
                break;
        }
    }

    public void setCapturedImageURI(Uri capturedImageURI) {
        this.capturedImageURI = capturedImageURI;
    }

    public Uri getCapturedImageURI() {
        return capturedImageURI;
    }
}
