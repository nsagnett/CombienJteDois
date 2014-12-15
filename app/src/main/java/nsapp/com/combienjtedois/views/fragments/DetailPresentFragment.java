package nsapp.com.combienjtedois.views.fragments;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

        view.findViewById(R.id.smsView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SMS TASK
            }
        });

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
        listView.setOnItemClickListener(this);
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
                launchActivity.setListEmpty(true);
                launchActivity.supportInvalidateOptionsMenu();
            }
        } else {
            listView.removeFooterView(footerView);
            launchActivity.setListEmpty(false);
            launchActivity.supportInvalidateOptionsMenu();
        }

        ParticipantListAdapter participantListAdapter = new ParticipantListAdapter(launchActivity, participants, isEditingView);
        listView.setAdapter(participantListAdapter);
    }


    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
    }

    @Override
    public void addItem(String importName, String importPhone) {
    }
}
