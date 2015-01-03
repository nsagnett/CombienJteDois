package nsapp.com.combienjtedois.views.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.utils.DBManager;
import nsapp.com.combienjtedois.model.Event;
import nsapp.com.combienjtedois.model.Participant;
import nsapp.com.combienjtedois.utils.Utils;
import nsapp.com.combienjtedois.utils.ViewCreator;
import nsapp.com.combienjtedois.views.adapters.ParticipantListAdapter;

public class EventParticipantsFragment extends AbstractFragment {

    private Event selectedEvent;

    private ArrayList<Participant> participants = new ArrayList<>();

    public static EventParticipantsFragment newInstance(Event event) {
        EventParticipantsFragment fragment = new EventParticipantsFragment();
        Bundle args = new Bundle();
        args.putSerializable(Utils.EVENT_KEY, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_listview, container, false);

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        footerView = (TextView) inflater.inflate(R.layout.footer_listview, null, false);

        selectedEvent = (Event) getArguments().getSerializable(Utils.EVENT_KEY);
        view.findViewById(R.id.headerLayout).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.headerNameView)).setText(selectedEvent.getSubject());
        ((ImageView) view.findViewById(R.id.headerProfileView)).setImageResource(R.drawable.event);
        ((TextView) view.findViewById(R.id.valueTextView)).setText(String.format(getString(R.string.value_format), selectedEvent.getValue() + getString(R.string.euro)));

        view.findViewById(R.id.smsView).setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        launchActivity.updateActionBarTitle(String.format(getString(R.string.consignee_format), selectedEvent.getConsignee()));
        notifyChanges();
    }

    private void notifyChanges() {
        Cursor c = Utils.dbManager.fetchAllParticipants(selectedEvent.getIdEvent());
        participants = new ArrayList<>();

        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex(DBManager.NAME_KEY));
            String phoneNumber = c.getString(c.getColumnIndex(DBManager.PHONE_NUMBER_KEY));
            int paid = Integer.parseInt(c.getString(c.getColumnIndex(DBManager.PAID_KEY)));
            int value = Integer.parseInt(selectedEvent.getValue());
            int numberParticipant = selectedEvent.getParticipantNumber();

            int id = Utils.dbManager.fetchIdParticipant(selectedEvent.getIdEvent(), name);
            participants.add(new Participant(id, name, phoneNumber, String.valueOf(value / numberParticipant), paid == 1));
        }

        if (participants.isEmpty()) {
            isEditingView = false;
            if (listView.getFooterViewsCount() == 0) {
                footerView.setText(R.string.add_participant);
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

        ParticipantListAdapter participantListAdapter = new ParticipantListAdapter(launchActivity, participants, isEditingView, selectedEvent);
        listView.setAdapter(participantListAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.IMPORT_CONTACT_CODE && resultCode == Activity.RESULT_OK) {
            importContact(data);
        }
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        if (isEditingView) {
            if (!participants.isEmpty()) {
                final Participant participant = participants.get(position);
                final AlertDialog alert = ViewCreator.createCustomUpdatePaymentDialogBox(launchActivity);
                alert.show();
                final TextView checkView = (TextView) alert.findViewById(R.id.checkView);
                final TextView uncheckView = (TextView) alert.findViewById(R.id.uncheckView);

                checkView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                        Utils.dbManager.updateParticipantPaid(participant.getId(), 1);
                        notifyChanges();
                    }
                });

                uncheckView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                        Utils.dbManager.updateParticipantPaid(participant.getId(), 0);
                        notifyChanges();
                    }
                });
            }
        }
    }

    @Override
    public void addItem(String importName, String importPhone) {
        final AlertDialog alert = ViewCreator.createCustomParticipantDialogBox(launchActivity);
        alert.show();
        final EditText namePersonView = (EditText) alert.findViewById(R.id.namePersonEditView);
        final EditText phoneNumberEditView = (EditText) alert.findViewById(R.id.phoneNumberEditView);
        final ImageView importContactView = (ImageView) alert.findViewById(R.id.importContactView);
        final TextView validateView = (TextView) alert.findViewById(R.id.neutralTextView);

        namePersonView.setText(importName);
        phoneNumberEditView.setText(importPhone);

        importContactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), Utils.IMPORT_CONTACT_CODE);
                alert.dismiss();
            }
        });


        validateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPersonForm(namePersonView)) {
                    alert.dismiss();
                    Utils.dbManager.createParticipant(selectedEvent.getIdEvent(), namePersonView.getText().toString(), phoneNumberEditView.getText().toString());
                    Utils.dbManager.updateParticipantNumber(selectedEvent.getIdEvent(), selectedEvent.getParticipantNumber() + 1);
                    selectedEvent.setParticipantNumber(selectedEvent.getParticipantNumber() + 1);
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
                    Utils.dbManager.deleteParticipant(participants.get(position).getId());
                    Utils.dbManager.updateParticipantNumber(selectedEvent.getIdEvent(), selectedEvent.getParticipantNumber() - 1);
                    selectedEvent.setParticipantNumber(selectedEvent.getParticipantNumber() - 1);
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
            Utils.dbManager.deleteParticipant(participants.get(position).getId());
            Utils.dbManager.updateParticipantNumber(selectedEvent.getIdEvent(), selectedEvent.getParticipantNumber() - 1);
            selectedEvent.setParticipantNumber(selectedEvent.getParticipantNumber() - 1);
            notifyChanges();
        }
    }

    boolean checkPersonForm(EditText namePersonView) {
        if (namePersonView.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.name)));
            return false;
        }
        return true;
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
        addItem(importName, importPhoneNumber);
    }
}
