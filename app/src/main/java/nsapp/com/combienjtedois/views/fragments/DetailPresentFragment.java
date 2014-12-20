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
import android.widget.Toast;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.listeners.SwipeDismissListViewTouchListener;
import nsapp.com.combienjtedois.model.DBManager;
import nsapp.com.combienjtedois.model.Participant;
import nsapp.com.combienjtedois.model.Present;
import nsapp.com.combienjtedois.model.Utils;
import nsapp.com.combienjtedois.views.ViewCreator;
import nsapp.com.combienjtedois.views.adapters.ParticipantListAdapter;

public class DetailPresentFragment extends AbstractFragment {

    private Present selectedPresent;

    private ArrayList<Participant> participants = new ArrayList<Participant>();

    public static DetailPresentFragment newInstance(Present present) {
        DetailPresentFragment fragment = new DetailPresentFragment();
        Bundle args = new Bundle();
        args.putSerializable(Utils.PRESENT_KEY, present);
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

        launchActivity.supportInvalidateOptionsMenu();

        selectedPresent = (Present) getArguments().getSerializable(Utils.PRESENT_KEY);
        view.findViewById(R.id.headerLayout).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.headerNameView)).setText(selectedPresent.getPresent());
        ((ImageView) view.findViewById(R.id.headerProfileView)).setImageResource(R.drawable.presents);
        ((TextView) view.findViewById(R.id.valueTextView)).setText(String.format(getString(R.string.value_format), selectedPresent.getValue() + getString(R.string.euro)));

        view.findViewById(R.id.smsView).setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        launchActivity.updateActionBarTitle(String.format(getString(R.string.consignee_format), selectedPresent.getConsignee()));
        SwipeDismissListViewTouchListener swipeDismissListViewTouchListener = new SwipeDismissListViewTouchListener(listView, new SwipeDismissListViewTouchListener.OnDismissCallback() {
            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                for (final int position : reverseSortedPositions) {
                    final AlertDialog alert = ViewCreator.createCustomConfirmDialogBox(launchActivity, R.string.message_delete_person_text);
                    alert.show();
                    alert.findViewById(R.id.positiveView).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                            Utils.dbManager.deleteParticipant(participants.get(position).getId());
                            Utils.dbManager.updateParticipantNumber(selectedPresent.getIdPresent(), Integer.parseInt(selectedPresent.getParticipantNumber()) - 1);
                            notifyChanges();
                        }
                    });
                    alert.findViewById(R.id.negativeView).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                        }
                    });
                }
            }
        });
        listView.setOnTouchListener(swipeDismissListViewTouchListener);
        listView.setOnScrollListener(swipeDismissListViewTouchListener.makeScrollListener());
        notifyChanges();
    }

    private void notifyChanges() {
        Cursor c = Utils.dbManager.fetchAllParticipants(selectedPresent.getIdPresent());
        participants = new ArrayList<Participant>();

        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex(DBManager.NAME_KEY));
            String budget = c.getString(c.getColumnIndex(DBManager.BUDGET_KEY));
            String phoneNumber = c.getString(c.getColumnIndex(DBManager.PHONE_NUMBER_KEY));
            int paid = Integer.parseInt(c.getString(c.getColumnIndex(DBManager.PAID_KEY)));

            int id = Utils.dbManager.fetchIdParticipant(selectedPresent.getIdPresent(), name);
            participants.add(new Participant(id, name, phoneNumber, budget, paid == 1));
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
            }
        } else {
            listView.removeFooterView(footerView);
        }

        ParticipantListAdapter participantListAdapter = new ParticipantListAdapter(launchActivity, participants, isEditingView);
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

                checkView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.paid, 0, 0, 0);
                uncheckView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.clock, 0, 0, 0);

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
        final EditText budgetEditView = (EditText) alert.findViewById(R.id.budgetEditView);
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
                if (checkPersonForm(namePersonView, budgetEditView)) {
                    alert.dismiss();
                    Utils.dbManager.createParticipant(selectedPresent.getIdPresent(), namePersonView.getText().toString(), phoneNumberEditView.getText().toString(), budgetEditView.getText().toString());
                    Utils.dbManager.updateParticipantNumber(selectedPresent.getIdPresent(), Integer.parseInt(selectedPresent.getParticipantNumber()) + 1);
                    notifyChanges();
                }
            }
        });
    }

    boolean checkPersonForm(EditText namePersonView, EditText budgetEditView) {
        if (namePersonView.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.name)));
            return false;
        } else if (budgetEditView.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.budget)));
            return false;
        } else if (Integer.parseInt(budgetEditView.getText().toString()) >= Integer.parseInt(selectedPresent.getValue())) {
            Toast.makeText(launchActivity, getString(R.string.impossible_budget), Toast.LENGTH_SHORT).show();
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
