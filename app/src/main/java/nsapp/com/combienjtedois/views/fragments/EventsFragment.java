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

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.utils.DBManager;
import nsapp.com.combienjtedois.model.Event;
import nsapp.com.combienjtedois.utils.Utils;
import nsapp.com.combienjtedois.utils.ViewCreator;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;
import nsapp.com.combienjtedois.views.adapters.EventAdapter;

import static android.os.Build.VERSION_CODES.HONEYCOMB;

public class EventsFragment extends AbstractFragment {

    private ArrayList<Event> presentsArray = new ArrayList<>();

    public static EventsFragment newInstance(int sectionNumber) {
        EventsFragment fragment = new EventsFragment();
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
        Cursor c = Utils.dbManager.fetchAllEvents();
        presentsArray = new ArrayList<>();

        while (c.moveToNext()) {
            String consignee = c.getString(c.getColumnIndex(DBManager.CONSIGNEE_KEY));
            int participantNumber = c.getInt(c.getColumnIndex(DBManager.PARTICIPANT_NUMBER_KEY));
            String present = c.getString(c.getColumnIndex(DBManager.SUBJECT_KEY));
            String value = c.getString(c.getColumnIndex(DBManager.VALUE_KEY));
            String date = c.getString(c.getColumnIndex(DBManager.DATE_KEY));

            int id = Utils.dbManager.fetchIdEvent(present, date);
            presentsArray.add(new Event(id, consignee, participantNumber, present, value, date));
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

        EventAdapter eventAdapter = new EventAdapter(launchActivity, presentsArray);
        listView.setAdapter(eventAdapter);
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
                String date = Utils.formattingDate(datePickerEventView);
                long beforeEventTime = Utils.getTimeBeforeEvent(date);
                if (checkAddForm(namePersonView, presentView, valueView, beforeEventTime)) {
                    alert.dismiss();
                    Utils.dbManager.createEvent(namePersonView.getText().toString(), presentView.getText().toString(), valueView.getText().toString(), date);
                    notifyChanges();
                }
            }
        });
    }

    void modifyPresent(final Event event) {
        final AlertDialog alert = ViewCreator.modifyCustomPresentDialogBox(launchActivity);
        alert.show();
        final EditText namePersonView = (EditText) alert.findViewById(R.id.namePersonEditView);
        final EditText valueView = (EditText) alert.findViewById(R.id.valueView);
        final EditText presentView = (EditText) alert.findViewById(R.id.presentView);
        final TextView validateView = (TextView) alert.findViewById(R.id.neutralTextView);

        namePersonView.setText(event.getConsignee());
        valueView.setText(event.getValue());
        presentView.setText(event.getSubject());

        validateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkModifyForm(namePersonView, presentView, valueView)) {
                    alert.dismiss();
                    Utils.dbManager.modifyEvent(event.getIdEvent(), namePersonView.getText().toString(), presentView.getText().toString(), valueView.getText().toString());
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
                    Utils.dbManager.deleteEvent(presentsArray.get(position).getIdEvent());
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
            Utils.dbManager.deleteEvent(presentsArray.get(position).getIdEvent());
            notifyChanges();
        }
    }

    private boolean checkAddForm(EditText namePersonView, EditText presentView, EditText valueView, long beforeEventTime) {
        if (namePersonView.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.person_name)));
            return false;
        } else if (presentView.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.subject)));
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

    private boolean checkModifyForm(EditText namePersonView, EditText presentView, EditText valueView) {
        if (namePersonView.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.person_name)));
            return false;
        } else if (presentView.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.subject)));
            return false;
        } else if (valueView.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.value)));
            return false;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!presentsArray.isEmpty()) {
            Event event = presentsArray.get(position);
            if (isEditingView) {
                modifyPresent(presentsArray.get(position));
            } else {
                prepareOnReplaceTransaction(EventParticipantsFragment.newInstance(event));
            }
        }
    }
}
