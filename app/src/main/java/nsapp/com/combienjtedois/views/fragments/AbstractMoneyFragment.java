package nsapp.com.combienjtedois.views.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.DBManager;
import nsapp.com.combienjtedois.model.Debt;
import nsapp.com.combienjtedois.model.Person;
import nsapp.com.combienjtedois.model.Tools;
import nsapp.com.combienjtedois.views.adapters.DebtListAdapter;
import nsapp.com.combienjtedois.views.adapters.PersonListAdapter;

import static android.provider.ContactsContract.CommonDataKinds.Phone;
import static android.provider.ContactsContract.Contacts;

public abstract class AbstractMoneyFragment extends AbstractFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final int IMPORT_CODE = 1;
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
        Tools.switchView(getActivity(), positiveSort, negativeSort, this);
    }

    @Override
    public void onClick(View v) {
        switch (listType) {
            case PERSON:
                addPerson(null, null);
                break;
            case DEBT:
                addDebt();
                break;
            default:
                break;
        }
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
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
                        footerView.setText(R.string.add_person);
                        footerView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_add, 0, 0);
                        footerView.setOnClickListener(this);
                        listView.addFooterView(footerView);
                    }
                } else {
                    listView.removeFooterView(footerView);

                    if (sortIndex == 0) {
                        personArrayList = Tools.decreasingOrderPersonAmountSort(personArrayList);
                    } else if (sortIndex == 1) {
                        personArrayList = Tools.increasingOrderPersonAmountSort(personArrayList);
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
                        footerView.setText(R.string.add_debt);
                        footerView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_add, 0, 0);
                        footerView.setOnClickListener(this);
                        listView.addFooterView(footerView);
                    }
                } else {
                    listView.removeFooterView(footerView);

                    if (sortIndex == 0) {
                        debtArrayList = Tools.decreasingOrderDebtAmountSort(debtArrayList);
                    } else if (sortIndex == 1) {
                        debtArrayList = Tools.increasingOrderDebtAmountSort(debtArrayList);
                    }
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

    public void addPerson(String importName, String importPhoneNumber) {
        final AlertDialog alert = Tools.createCustomAddPersonDialogBox(getActivity(), R.string.add_person, R.drawable.add, R.string.validate);
        alert.show();
        final EditText nameEditView = ((EditText) alert.findViewById(R.id.namePersonEditView));
        final EditText phoneNumberView = ((EditText) alert.findViewById(R.id.phoneNumberEditView));
        final TextView importContactView = ((TextView) alert.findViewById(R.id.importContactView));

        nameEditView.setText(importName);
        phoneNumberView.setText(importPhoneNumber);

        importContactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI), IMPORT_CODE);
                alert.dismiss();
            }
        });

        alert.findViewById(R.id.neutralTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPersonForm(nameEditView)) {
                    alert.dismiss();
                    Tools.dbManager.createPerson(nameEditView.getText().toString(), importContactView.getText().toString());
                    notifyChanges();
                    Toast.makeText(getActivity(), getString(R.string.toast_add_person), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addDebt() {
        final AlertDialog alert = Tools.createCustomAddDebtDialogBox(getActivity(), R.string.add_debt, R.drawable.add, R.string.validate);
        alert.show();
        TextView deviseView = (TextView) alert.findViewById(R.id.deviseView);
        deviseView.setVisibility(View.VISIBLE);
        deviseView.setText(R.string.euro);

        final TextView positiveDebtView = (TextView) alert.findViewById(R.id.positiveDebtView);
        final TextView negativeDebtView = (TextView) alert.findViewById(R.id.negativeDebtView);
        Tools.switchView(getActivity(), positiveDebtView, negativeDebtView, this);

        ((TextView) alert.findViewById(R.id.typeDebtView)).setText(R.string.type);
        ((TextView) alert.findViewById(R.id.reasonTextView)).setText(R.string.object);
        ((TextView) alert.findViewById(R.id.countTextView)).setText(R.string.amount);
        final EditText reasonEditText = ((EditText) alert.findViewById(R.id.reasonEditText));
        final EditText countEditText = ((EditText) alert.findViewById(R.id.countEditText));

        alert.findViewById(R.id.neutralTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sign = null;
                if (positiveDebtView.isSelected()) {
                    sign = "";
                } else if (negativeDebtView.isSelected()) {
                    sign = "-";
                }
                if (checkDebtForm(reasonEditText, countEditText, sign)) {
                    alert.dismiss();
                    Tools.dbManager.createDebt(person.getId(), sign + countEditText.getText().toString(), reasonEditText.getText().toString());
                    Toast.makeText(getActivity(), getString(R.string.toast_add_debt), Toast.LENGTH_SHORT).show();
                    notifyChanges();
                }
            }
        });
    }

    protected boolean checkDebtForm(EditText reasonEditText, EditText countEditText, String sign) {
        if (sign != null) {
            if (reasonEditText.getText().length() == 0) {
                Tools.showCustomAlertDialogBox(getActivity(),
                        R.string.warning_text,
                        R.drawable.warning,
                        String.format(getString(R.string.empty_field_format), getString(R.string.object)));
                return false;
            } else if (countEditText.getText().length() == 0) {
                Tools.showCustomAlertDialogBox(getActivity(),
                        R.string.warning_text,
                        R.drawable.warning,
                        String.format(getString(R.string.empty_field_format), getString(R.string.amount)));
                return false;
            }
        } else {
            Tools.showCustomAlertDialogBox(getActivity(),
                    R.string.warning_text,
                    R.drawable.warning,
                    String.format(getString(R.string.empty_field_format), getString(R.string.type)));
            return false;
        }
        return true;
    }

    protected boolean checkPersonForm(EditText editText) {
        if (editText.getText().length() == 0) {
            Tools.showCustomAlertDialogBox(getActivity(),
                    R.string.warning_text,
                    R.drawable.warning,
                    String.format(getString(R.string.empty_field_format), getString(R.string.name)));
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMPORT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                ContentResolver contentResolver = getActivity().getContentResolver();
                Uri dataUri = data.getData();
                Cursor c = contentResolver.query(dataUri, null, null, null, null);
                String importName = null;
                String importPhoneNumber = null;
                if (c.getCount() > 0) {
                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndex(Contacts._ID));
                        c.getString(c.getColumnIndex(Contacts.DISPLAY_NAME));
                        if (Integer.parseInt(c.getString(c.getColumnIndex(Contacts.HAS_PHONE_NUMBER))) > 0) {
                            Cursor phones = contentResolver.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = ?",
                                    new String[]{id}, null);
                            phones.moveToFirst();
                            importName = c.getString(c.getColumnIndex(Phone.DISPLAY_NAME));
                            importPhoneNumber = phones.getString(phones.getColumnIndex(Phone.NUMBER));
                            phones.close();
                        }
                    }
                }
                c.close();
                addPerson(importName, importPhoneNumber);
            }
        }
    }

}
