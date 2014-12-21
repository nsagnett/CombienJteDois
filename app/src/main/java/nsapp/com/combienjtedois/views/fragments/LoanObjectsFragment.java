package nsapp.com.combienjtedois.views.fragments;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.DBManager;
import nsapp.com.combienjtedois.model.LoanObject;
import nsapp.com.combienjtedois.model.Preferences;
import nsapp.com.combienjtedois.model.Utils;
import nsapp.com.combienjtedois.views.ViewCreator;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;
import nsapp.com.combienjtedois.views.adapters.LoanObjectAdapter;

public class LoanObjectsFragment extends AbstractFragment {

    private ArrayList<LoanObject> loanObjects = new ArrayList<LoanObject>();

    public static LoanObjectsFragment newInstance(int sectionNumber) {
        LoanObjectsFragment fragment = new LoanObjectsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.findViewById(R.id.headerSeparator).setVisibility(View.GONE);
        launchActivity.supportInvalidateOptionsMenu();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LaunchActivity) getActivity()).updateActionBarTitle(getString(R.string.title_section2));
        notifyChanges();
    }

    @Override
    public void addItem(String importName, String importPhone) {
        final AlertDialog alert = ViewCreator.createCustomLoanObjectDialogBox(launchActivity);
        alert.show();
        final EditText namePersonView = (EditText) alert.findViewById(R.id.namePersonEditView);
        final Spinner categories = (Spinner) alert.findViewById(R.id.categorySpinnerView);
        final TextView positiveLoanView = (TextView) alert.findViewById(R.id.positiveLoanView);
        final TextView negativeLoanView = (TextView) alert.findViewById(R.id.negativeLoanView);
        final EditText descriptionView = (EditText) alert.findViewById(R.id.objectDescriptionView);
        final TextView validateView = (TextView) alert.findViewById(R.id.neutralTextView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(launchActivity, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.objectsArray));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(adapter);

        ViewCreator.switchView(launchActivity, positiveLoanView, negativeLoanView);

        validateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAddLoanForm(namePersonView, categories, positiveLoanView, negativeLoanView, descriptionView)) {
                    alert.dismiss();
                    Utils.dbManager.createObject(namePersonView.getText().toString(),
                            categories.getSelectedItem().toString(),
                            descriptionView.getText().toString(),
                            positiveLoanView.isSelected() ? positiveLoanView.getText().toString() : negativeLoanView.getText().toString(),
                            (String) DateFormat.format(Utils.SPECIFIC_PATTERN_DATE, new Date().getTime()));
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
                    Utils.dbManager.deleteObject(loanObjects.get(position).getIdLoanObject());
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
            Utils.dbManager.deleteObject(loanObjects.get(position).getIdLoanObject());
            notifyChanges();
        }
    }

    private void notifyChanges() {
        Cursor c = Utils.dbManager.fetchAllObjects();
        loanObjects = new ArrayList<LoanObject>();

        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex(DBManager.NAME_KEY));
            String nameObject = c.getString(c.getColumnIndex(DBManager.NAME_OBJECT_KEY));
            String category = c.getString(c.getColumnIndex(DBManager.CATEGORY_OBJECT_KEY));
            String type = c.getString(c.getColumnIndex(DBManager.TYPE_OBJECT_KEY));
            String date = c.getString(c.getColumnIndex(DBManager.DATE_KEY));

            int id = Utils.dbManager.fetchIdObject(name, date);

            loanObjects.add(new LoanObject(id, name, category, nameObject, type, date));
        }

        if (loanObjects.isEmpty()) {
            isEditingView = false;
            if (listView.getFooterViewsCount() == 0) {
                footerView.setText(R.string.add_element);
                footerView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.dark_add, 0, 0);
                footerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addItem(null, null);
                    }
                });
                listView.addFooterView(footerView);
            }
        } else {
            listView.removeFooterView(footerView);
        }

        LoanObjectAdapter loanObjectAdapter = new LoanObjectAdapter(launchActivity, loanObjects);
        listView.setAdapter(loanObjectAdapter);
    }

    private boolean checkAddLoanForm(EditText namePersonView, Spinner categories, TextView positiveLoanView, TextView negativeLoanView, EditText descriptionView) {
        if (namePersonView.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.person_name)));
            return false;
        } else if (categories.getSelectedItemPosition() == 0) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.category_object)));
            return false;
        } else if (!positiveLoanView.isSelected() && !negativeLoanView.isSelected()) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.type)));
            return false;
        } else if (descriptionView.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.object_description)));
            return false;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Required implementation
    }
}
