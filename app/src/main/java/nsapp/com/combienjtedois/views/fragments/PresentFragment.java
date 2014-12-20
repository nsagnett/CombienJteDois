package nsapp.com.combienjtedois.views.fragments;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.DBManager;
import nsapp.com.combienjtedois.model.Preferences;
import nsapp.com.combienjtedois.model.Present;
import nsapp.com.combienjtedois.model.Utils;
import nsapp.com.combienjtedois.views.ViewCreator;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;
import nsapp.com.combienjtedois.views.adapters.PresentAdapter;

import static android.os.Build.VERSION_CODES.HONEYCOMB;

public class PresentFragment extends AbstractFragment {

    private ArrayList<Present> presentsArray = new ArrayList<Present>();

    public static PresentFragment newInstance(int sectionNumber) {
        PresentFragment fragment = new PresentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.findViewById(R.id.headerSeparator).setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LaunchActivity) getActivity()).updateActionBarTitle(getString(R.string.title_section3));
        notifyChanges();
    }

    private void notifyChanges() {
        Cursor c = Utils.dbManager.fetchAllPresents();
        presentsArray = new ArrayList<Present>();

        while (c.moveToNext()) {
            String consignee = c.getString(c.getColumnIndex(DBManager.CONSIGNEE_KEY));
            String participantNumber = c.getString(c.getColumnIndex(DBManager.PARTICIPANT_NUMBER_KEY));
            String present = c.getString(c.getColumnIndex(DBManager.PRESENT_KEY));
            String value = c.getString(c.getColumnIndex(DBManager.VALUE_KEY));
            String date = c.getString(c.getColumnIndex(DBManager.DATE_KEY));

            int id = Utils.dbManager.fetchIdPresent(present, date);
            presentsArray.add(new Present(id, consignee, participantNumber, present, value, date));
        }

        if (presentsArray.isEmpty()) {
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
                launchActivity.setListEmpty(true);
                launchActivity.supportInvalidateOptionsMenu();
            }
        } else {
            listView.removeFooterView(footerView);
            launchActivity.setListEmpty(false);
            launchActivity.supportInvalidateOptionsMenu();
        }

        PresentAdapter presentAdapter = new PresentAdapter(launchActivity, presentsArray);
        listView.setAdapter(presentAdapter);
    }


    @Override
    public void addItem(String importName, String importPhone) {
        final AlertDialog alert = ViewCreator.createCustomPresentDialogBox(launchActivity);
        alert.show();
        final EditText namePersonView = (EditText) alert.findViewById(R.id.namePersonEditView);
        final EditText valueView = (EditText) alert.findViewById(R.id.valueView);
        final EditText presentView = (EditText) alert.findViewById(R.id.presentView);
        final TextView validateView = (TextView) alert.findViewById(R.id.neutralTextView);
        final DatePicker datePickerEventView = (DatePicker) alert.findViewById(R.id.datePickerEventView);

        if (Build.VERSION.SDK_INT >= HONEYCOMB) {
            datePickerEventView.setCalendarViewShown(false);
        }

        validateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = formattingDate(datePickerEventView);
                long beforeEventTime = getTimeBeforeEvent(date);
                if (checkAddForm(namePersonView, presentView, valueView, beforeEventTime)) {
                    alert.dismiss();
                    Utils.dbManager.createPresent(namePersonView.getText().toString(), presentView.getText().toString(), valueView.getText().toString(), date);
                    notifyChanges();
                }
            }
        });
    }

    @Override
    public void deleteItem(final int position) {
        if (preferences.getBoolean(Preferences.CONFIRM_DISMISS_KEY, true)) {
            final AlertDialog alert = ViewCreator.createCustomConfirmDialogBox(launchActivity, R.string.message_delete_person_text);
            alert.show();
            alert.findViewById(R.id.positiveView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    Utils.dbManager.deletePresent(presentsArray.get(position).getIdPresent());
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
            Utils.dbManager.deletePresent(presentsArray.get(position).getIdPresent());
            notifyChanges();
        }
    }

    private boolean checkAddForm(EditText namePersonView, EditText presentView, EditText valueView, long beforeEventTime) {
        if (namePersonView.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.person_name)));
            return false;
        } else if (presentView.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.present)));
            return false;
        } else if (valueView.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.value)));
            return false;
        } else if (beforeEventTime < 0) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, getString(R.string.impossible_date));
            return false;
        }
        return true;
    }

    private String formattingDate(DatePicker datePickerEventView) {
        int days = datePickerEventView.getDayOfMonth();
        int month = datePickerEventView.getMonth() + 1;
        int year = datePickerEventView.getYear();
        String date = "";
        if (days < 10) {
            date += "0" + days;
        } else {
            date += days;
        }
        if (month < 10) {
            date += "-0" + month + "-" + year;
        } else {
            date += "-" + month + "-" + year;
        }

        return date;
    }

    private long getTimeBeforeEvent(String date) {
        long beforeEventTime = -1;
        try {
            beforeEventTime = new SimpleDateFormat(Utils.EVENT_PATTERN_DATE).parse(date).getTime() - new Date().getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return beforeEventTime;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!presentsArray.isEmpty()) {
            Present present = presentsArray.get(position);
            if (isEditingView) {
                // MODIFY PRESENT
            } else {
                prepareOnReplaceTransaction(DetailPresentFragment.newInstance(present));
            }
        }
    }
}
