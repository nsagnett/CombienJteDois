package nsapp.com.combienjtedois.views.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
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
    protected TextView headerPersonView;
    protected TextView footerView;

    protected TextView positiveSort;
    protected TextView negativeSort;

    protected int sortIndex;
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

        headerPersonView = (TextView) inflater.inflate(R.layout.header_listview, null, false);
        footerView = (TextView) inflater.inflate(R.layout.footer_listview, null, false);

        negativeSort = (TextView) view.findViewById(R.id.negativeSort);
        positiveSort = (TextView) view.findViewById(R.id.positiveSort);

        sortIndex = -1;

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((LaunchActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onResume() {
        super.onResume();
        positiveSort.setTextColor(getResources().getColor(R.color.green));
        negativeSort.setTextColor(getResources().getColor(R.color.green));
        positiveSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!positiveSort.isSelected()) {
                    sortIndex = 0;
                    positiveSort.setSelected(true);
                    negativeSort.setSelected(false);
                    positiveSort.setTextColor(getResources().getColor(android.R.color.white));
                    negativeSort.setTextColor(getResources().getColor(R.color.green));
                    notifyChanges();
                }
            }
        });
        negativeSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!negativeSort.isSelected()) {
                    sortIndex = 1;
                    negativeSort.setSelected(true);
                    positiveSort.setSelected(false);
                    negativeSort.setTextColor(getResources().getColor(android.R.color.white));
                    positiveSort.setTextColor(getResources().getColor(R.color.green));
                    notifyChanges();
                }
            }
        });
    }

    public void notifyChanges() {
        Cursor c;
        switch (listType) {
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
                    isDeletingView = false;
                    isEditingView = false;
                    if (listView.getFooterViewsCount() == 0) {
                        footerView.setText(R.string.empty_footer_person);
                        footerView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_add, 0, 0);
                        footerView.setOnClickListener(this);
                        listView.addFooterView(footerView);
                    }
                } else {
                    listView.removeFooterView(footerView);
                    if (sortIndex == 0) {
                        personArrayList = Tools.decroissantSort(personArrayList);
                    } else if (sortIndex == 1) {
                        personArrayList = Tools.croissantSort(personArrayList);
                    }
                }

                PersonListAdapter personListAdapter = new PersonListAdapter(getActivity(), personArrayList, isDeletingView, isEditingView);
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
                    isDeletingView = false;
                    isEditingView = false;
                    if (listView.getFooterViewsCount() == 0) {
                        footerView.setText(R.string.empty_footer_money);
                        footerView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_add, 0, 0);
                        footerView.setOnClickListener(this);
                        listView.addFooterView(footerView);
                    }
                } else {
                    listView.removeFooterView(footerView);
                }

                DebtListAdapter debtListAdapter = new DebtListAdapter(getActivity(), debtArrayList, isDeletingView, isEditingView);
                listView.setAdapter(debtListAdapter);

                Double total = Double.parseDouble(Tools.dbManager.getTotalCount(person.getId()));

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

    public void addPerson() {
        final AlertDialog alert = Tools.createCustomAddPersonDialogBox(getActivity(), R.string.add_person, R.drawable.add, R.string.validate);
        alert.show();
        final EditText editText = ((EditText) alert.findViewById(R.id.editTextView));
        editText.requestFocus();
        alert.findViewById(R.id.neutralTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() == 0) {
                    Tools.showCustomAlertDialogBox(getActivity(),
                            R.string.warning_text,
                            R.drawable.warning,
                            String.format(getString(R.string.empty_edittext_format), getString(R.string.add_person_name)),
                            R.string.validate);
                } else {
                    alert.dismiss();
                    Tools.dbManager.createPerson(editText.getText().toString());
                    notifyChanges();
                }
            }
        });
    }

    public void addDebt() {
        final AlertDialog alert = Tools.createCustomAddDebtDialogBox(getActivity(), R.string.add_debt, R.drawable.add, R.string.validate);
        alert.show();
        final TextView positiveDebtView = (TextView) alert.findViewById(R.id.positiveDebtView);
        final TextView negativeDebtView = (TextView) alert.findViewById(R.id.negativeDebtView);
        TextView deviseView = (TextView) alert.findViewById(R.id.deviseView);
        deviseView.setVisibility(View.VISIBLE);
        deviseView.setText(R.string.euro);

        positiveDebtView.setSelected(true);
        positiveDebtView.setTextColor(getResources().getColor(android.R.color.white));
        negativeDebtView.setTextColor(getResources().getColor(R.color.green));

        positiveDebtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!positiveDebtView.isSelected()) {
                    negativeDebtView.setSelected(false);
                    positiveDebtView.setSelected(true);
                    positiveDebtView.setTextColor(getResources().getColor(android.R.color.white));
                    negativeDebtView.setTextColor(getResources().getColor(R.color.green));
                }
            }
        });
        negativeDebtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!negativeDebtView.isSelected()) {
                    positiveDebtView.setSelected(false);
                    negativeDebtView.setSelected(true);
                    negativeDebtView.setTextColor(getResources().getColor(android.R.color.white));
                    positiveDebtView.setTextColor(getResources().getColor(R.color.green));
                }
            }
        });

        ((TextView) alert.findViewById(R.id.reasonTextView)).setText(R.string.add_debt_reason);
        ((TextView) alert.findViewById(R.id.countTextView)).setText(R.string.add_debt_amount);
        final EditText reasonEditText = ((EditText) alert.findViewById(R.id.reasonEditText));
        final EditText countEditText = ((EditText) alert.findViewById(R.id.countEditText));
        reasonEditText.requestFocus();
        alert.findViewById(R.id.neutralTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reasonEditText.getText().length() == 0) {
                    Tools.showCustomAlertDialogBox(getActivity(),
                            R.string.warning_text,
                            R.drawable.warning,
                            String.format(getString(R.string.empty_edittext_format), getString(R.string.add_debt_reason)),
                            R.string.validate);
                } else if (countEditText.getText().length() == 0) {
                    Tools.showCustomAlertDialogBox(getActivity(),
                            R.string.warning_text,
                            R.drawable.warning,
                            String.format(getString(R.string.empty_edittext_format), getString(R.string.add_debt_amount)),
                            R.string.validate);
                } else {
                    alert.dismiss();
                    if (positiveDebtView.isSelected()) {
                        Tools.dbManager.createDebt(person.getId(), countEditText.getText().toString(), reasonEditText.getText().toString());
                    } else {
                        Tools.dbManager.createDebt(person.getId(), "-" + countEditText.getText().toString(), reasonEditText.getText().toString());
                    }
                    notifyChanges();
                }
            }
        });
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
