package nsapp.com.combienjtedois.views.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Date;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.DBManager;
import nsapp.com.combienjtedois.model.Person;
import nsapp.com.combienjtedois.model.Preferences;
import nsapp.com.combienjtedois.model.Utils;
import nsapp.com.combienjtedois.views.ViewCreator;
import nsapp.com.combienjtedois.views.adapters.PersonListAdapter;

public class PersonListForMoneyFragment extends AbstractFragment {

    private boolean isCreatingPerson;

    public static PersonListForMoneyFragment newInstance(int sectionNumber) {
        PersonListForMoneyFragment fragment = new PersonListForMoneyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        launchActivity.updateActionBarTitle(getString(R.string.title_section1));
        View view = getView();
        if (view != null) {
            view.findViewById(R.id.headerSeparator).setVisibility(View.GONE);
        }
        notifyChanges();
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        if (!personArrayList.isEmpty()) {
            Person person = personArrayList.get(position);
            if (isEditingView) {
                modifyPerson(personArrayList.get(position), null, null);
            } else {
                prepareOnReplaceTransaction(DetailPersonForMoneyFragment.newInstance(person));
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.IMPORT_CONTACT_CODE && resultCode == Activity.RESULT_OK) {
            importContact(data);
        }
    }

    @Override
    public void addItem(String importName, String importPhoneNumber) {
        isCreatingPerson = true;
        final AlertDialog alert = ViewCreator.createCustomPersonDialogBox(launchActivity, R.string.add_person, R.drawable.add);
        alert.show();
        final EditText nameEditView = ((EditText) alert.findViewById(R.id.namePersonEditView));
        final EditText phoneNumberView = ((EditText) alert.findViewById(R.id.phoneNumberEditView));
        final ImageView importContactView = ((ImageView) alert.findViewById(R.id.importContactView));

        nameEditView.setText(importName);
        phoneNumberView.setText(importPhoneNumber);

        importContactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), Utils.IMPORT_CONTACT_CODE);
                alert.dismiss();
            }
        });

        alert.findViewById(R.id.neutralTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPersonForm(nameEditView)) {
                    alert.dismiss();
                    Utils.dbManager.createPerson(nameEditView.getText().toString(), phoneNumberView.getText().toString(), (String) DateFormat.format(Utils.SPECIFIC_PATTERN_DATE, new Date().getTime()));
                    notifyChanges();
                }
            }
        });
    }

    private void modifyPerson(final Person person, String name, String phoneNumber) {
        isCreatingPerson = false;
        selectedPerson = person;
        final AlertDialog alert = ViewCreator.createCustomPersonDialogBox(launchActivity, R.string.modify_person, R.drawable.edit);
        alert.show();
        final EditText nameView = ((EditText) alert.findViewById(R.id.namePersonEditView));
        final EditText phoneNumberView = ((EditText) alert.findViewById(R.id.phoneNumberEditView));
        final ImageView importView = (ImageView) alert.findViewById(R.id.importContactView);

        importView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), Utils.IMPORT_CONTACT_CODE);
                alert.dismiss();
            }
        });

        if (name == null) {
            nameView.setText(person.getName());
        } else {
            nameView.setText(name);
        }
        if (phoneNumber == null) {
            phoneNumberView.setText(person.getPhoneNumber());
        } else {
            phoneNumberView.setText(phoneNumber);
        }
        alert.findViewById(R.id.neutralTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPersonForm(nameView)) {
                    alert.dismiss();
                    Utils.dbManager.modifyPerson(person.getId(), nameView.getText().toString(), phoneNumberView.getText().toString(), (String) DateFormat.format(Utils.SPECIFIC_PATTERN_DATE, new Date().getTime()));
                    notifyChanges();
                }
            }
        });
    }

    @Override
    public void deleteItem(final int position) {
        if (confirmDismiss) {
            final AlertDialog alert = ViewCreator.createCustomConfirmDialogBox(launchActivity, R.string.message_delete_person_text);
            alert.show();
            alert.findViewById(R.id.positiveView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    Utils.dbManager.deletePerson(personArrayList.get(position).getId());
                    notifyChanges();
                }
            });
            alert.findViewById(R.id.negativeView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
        } else {
            Utils.dbManager.deletePerson(personArrayList.get(position).getId());
            notifyChanges();
        }
    }

    private void notifyChanges() {
        Cursor c = Utils.dbManager.fetchAllPersons();
        personArrayList = new ArrayList<>();
        float positiveTotal = 0.f;
        float negativeTotal = 0.f;

        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex(DBManager.NAME_KEY));
            String phoneNumber = c.getString(c.getColumnIndex(DBManager.PHONE_NUMBER_KEY));
            String date = c.getString(c.getColumnIndex(DBManager.DATE_KEY));

            int id = Utils.dbManager.fetchIdPerson(name);
            String totalPerson = Utils.dbManager.getTotalCount(id);
            personArrayList.add(new Person(id, name, totalPerson, phoneNumber, date));
            if (Float.parseFloat(totalPerson) > 0) {
                positiveTotal += Float.parseFloat(totalPerson);
            } else {
                negativeTotal += Float.parseFloat(totalPerson);
            }
        }



        preferences.edit().putFloat(Preferences.AMOUNT_CREDENCE_MONEY, positiveTotal).apply();
        preferences.edit().putFloat(Preferences.AMOUNT_DEBT_MONEY, negativeTotal).apply();

        if (personArrayList.isEmpty()) {
            isEditingView = false;
            if (listView.getFooterViewsCount() == 0) {
                footerView.setText(R.string.add_person);
                footerView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.dark_add, 0, 0);
                footerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addItem(null, null);
                    }
                });
                listView.addFooterView(footerView);
                launchActivity.setListEmpty(true);
                launchActivity.supportInvalidateOptionsMenu();
            }
        } else {
            listView.removeFooterView(footerView);
            launchActivity.setListEmpty(false);
            launchActivity.supportInvalidateOptionsMenu();
        }

        PersonListAdapter personListAdapter = new PersonListAdapter(launchActivity, personArrayList, isEditingView);
        listView.setAdapter(personListAdapter);
    }

    private void importContact(Intent data) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri dataUri = data.getData();
        Cursor c = contentResolver.query(dataUri, null, null, null, null);
        String importName = null;
        String importPhoneNumber = null;
        if (c.getCount() > 0) {
            if (c.moveToFirst()) {
                String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    phones.moveToFirst();
                    importName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    importPhoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phones.close();
                }
            }
        }
        c.close();
        if (isCreatingPerson) {
            addItem(importName, importPhoneNumber);
        } else {
            modifyPerson(selectedPerson, importName, importPhoneNumber);
        }
    }

    boolean checkPersonForm(EditText editText) {
        if (editText.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.name)));
            return false;
        }
        return true;
    }
}
