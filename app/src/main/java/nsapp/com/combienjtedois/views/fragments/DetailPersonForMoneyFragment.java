package nsapp.com.combienjtedois.views.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.listeners.SwipeDismissListViewTouchListener;
import nsapp.com.combienjtedois.model.DBManager;
import nsapp.com.combienjtedois.model.Debt;
import nsapp.com.combienjtedois.model.Person;
import nsapp.com.combienjtedois.model.Utils;
import nsapp.com.combienjtedois.views.ViewCreator;
import nsapp.com.combienjtedois.views.activities.EditTextAmountActivity;
import nsapp.com.combienjtedois.views.adapters.DebtListAdapter;

public class DetailPersonForMoneyFragment extends AbstractFragment {

    private String type;

    public static DetailPersonForMoneyFragment newInstance(Person person) {
        DetailPersonForMoneyFragment fragment = new DetailPersonForMoneyFragment();
        Bundle args = new Bundle();
        args.putSerializable(Utils.PERSON_KEY, person);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        selectedPerson = (Person) getArguments().getSerializable(Utils.PERSON_KEY);
        launchActivity.updateActionBarTitle(selectedPerson.getName());
        view.findViewById(R.id.headerLayout).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.headerNameView)).setText(selectedPerson.getName());
        ((ImageView) view.findViewById(R.id.headerProfileView)).setImageResource(R.drawable.profile);

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
        SwipeDismissListViewTouchListener swipeDismissListViewTouchListener = new SwipeDismissListViewTouchListener(listView, new SwipeDismissListViewTouchListener.OnDismissCallback() {
            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                for (final int position : reverseSortedPositions) {
                    final AlertDialog alert = ViewCreator.createCustomConfirmDialogBox(getActivity(), R.string.message_delete_element);
                    alert.show();
                    alert.findViewById(R.id.positiveView).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                            Utils.dbManager.setModificationDatePerson(selectedPerson.getId(), (String) DateFormat.format(Utils.SPECIFIC_PATTERN_DATE, new Date().getTime()));
                            final int idDebt = debtArrayList.get(position).getId();
                            final int idPerson = selectedPerson.getId();
                            Utils.dbManager.deleteDebt(idDebt, idPerson);
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

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        if (!debtArrayList.isEmpty() && isEditingView) {
            selectedDebt = debtArrayList.get(position);
            modifyItem(selectedDebt);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.UPDATE_DEBT_COUNT) {
            if (data != null) {
                updateCountView(data.getStringExtra(RESULT_KEY));
            } else {
                updateCountView(selectedDebt.getAmount());
            }
        }
    }

    private void updateCountView(String amount) {
        View view = getView();
        if (view != null) {
            if (type.equals(getString(R.string.credence))) {
                selectedDebt.setAmount(amount);
            } else {
                selectedDebt.setAmount("-" + amount);
            }
            modifyItem(selectedDebt);
        }
    }

    @Override
    public void addItem(String importName, String importPhone) {
        final AlertDialog alert = ViewCreator.createCustomAddDebtDialogBox(getActivity());
        alert.show();
        TextView deviseView = (TextView) alert.findViewById(R.id.deviseView);
        deviseView.setVisibility(View.VISIBLE);
        deviseView.setText(R.string.euro);

        final TextView positiveDebtView = (TextView) alert.findViewById(R.id.positiveDebtView);
        final TextView negativeDebtView = (TextView) alert.findViewById(R.id.negativeDebtView);
        ViewCreator.switchView(launchActivity, positiveDebtView, negativeDebtView);

        ((TextView) alert.findViewById(R.id.typeDebtView)).setText(R.string.type_with_points);
        ((TextView) alert.findViewById(R.id.reasonTextView)).setText(R.string.object_with_points);
        ((TextView) alert.findViewById(R.id.countTextView)).setText(R.string.amount_with_points);
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
                if (checkAddDebtForm(reasonEditText, countEditText, sign)) {
                    alert.dismiss();
                    Utils.dbManager.setModificationDatePerson(selectedPerson.getId(), (String) DateFormat.format(Utils.SPECIFIC_PATTERN_DATE, new Date().getTime()));
                    Utils.dbManager.createDebt(selectedPerson.getId(), sign + countEditText.getText().toString(), reasonEditText.getText().toString(), (String) DateFormat.format(Utils.SPECIFIC_PATTERN_DATE, new Date().getTime()));
                    notifyChanges();
                }
            }
        });
    }

    private void notifyChanges() {
        int idPerson = selectedPerson.getId();
        Cursor c = Utils.dbManager.fetchAllDebt(idPerson);
        debtArrayList = new ArrayList<Debt>();

        while (c.moveToNext()) {
            String reason = c.getString(c.getColumnIndex(DBManager.REASON_KEY));
            String date = c.getString(c.getColumnIndex(DBManager.DATE_KEY));

            int id = Utils.dbManager.fetchIdDebt(idPerson, reason);
            String amount = Utils.dbManager.getCount(id);
            debtArrayList.add(new Debt(id, amount, reason, date));
        }

        if (debtArrayList.isEmpty()) {
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

        DebtListAdapter debtListAdapter = new DebtListAdapter(launchActivity, debtArrayList, isEditingView);
        listView.setAdapter(debtListAdapter);

        Double total = Double.parseDouble(Utils.dbManager.getTotalCount(selectedPerson.getId()));

        if (total >= 0) {
            headerCountView.setTextColor(getResources().getColor(R.color.green));
        } else {
            headerCountView.setTextColor(getResources().getColor(R.color.red));
        }

        headerCountView.setText(String.format(getString(R.string.money_format), total.toString()));
    }

    void modifyItem(final Debt debt) {
        final AlertDialog alert = ViewCreator.createCustomModifyDebtDialogBox(launchActivity);
        alert.show();

        final TextView increaseTextView = ((TextView) alert.findViewById(R.id.addTextView));
        final TextView reduceTextView = ((TextView) alert.findViewById(R.id.subtractTextView));
        String sign = "";

        Double amount = Double.parseDouble(debt.getAmount());
        if (amount >= 0) {
            type = getString(R.string.credence);
            increaseTextView.setTextColor(getResources().getColor(R.color.green));
            reduceTextView.setTextColor(getResources().getColor(R.color.red));
        } else {
            type = getString(R.string.debt);
            reduceTextView.setTextColor(getResources().getColor(R.color.green));
            increaseTextView.setTextColor(getResources().getColor(R.color.red));
            sign = "-";
        }

        ((TextView) alert.findViewById(R.id.typeDebtView)).setText(getString(R.string.type_with_points) + type);
        ((TextView) alert.findViewById(R.id.reasonTextView)).setText(getString(R.string.object_with_points) + debt.getReason());

        final Integer amountInteger = (int) Math.abs(amount);
        String amountString = getString(R.string.amount_with_points) + String.format(getString(R.string.money_format), amountInteger.toString());
        ((TextView) alert.findViewById(R.id.countTextView)).setText(amountString);

        increaseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                Intent intent = new Intent(launchActivity, EditTextAmountActivity.class);
                intent.putExtra(OPERATION, 0);
                intent.putExtra(TYPE, type);
                intent.putExtra(DEBT_EXTRA, selectedDebt);
                startActivityForResult(intent, Utils.UPDATE_DEBT_COUNT);
            }
        });

        reduceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                Intent intent = new Intent(launchActivity, EditTextAmountActivity.class);
                intent.putExtra(OPERATION, 1);
                intent.putExtra(TYPE, type);
                intent.putExtra(DEBT_EXTRA, selectedDebt);
                startActivityForResult(intent, Utils.UPDATE_DEBT_COUNT);
            }
        });

        final String finalSign = sign;
        alert.findViewById(R.id.neutralTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                Utils.dbManager.modifyDebt(debt.getId(), finalSign + amountInteger.toString(), (String) DateFormat.format(Utils.SPECIFIC_PATTERN_DATE, new Date().getTime()));
                Utils.dbManager.setModificationDatePerson(selectedPerson.getId(), (String) DateFormat.format(Utils.SPECIFIC_PATTERN_DATE, new Date().getTime()));
                notifyChanges();
            }
        });
    }

    boolean checkAddDebtForm(EditText reasonEditText, EditText countEditText, String sign) {
        if (sign != null) {
            if (reasonEditText.getText().length() == 0) {
                ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.object)));
                return false;
            } else if (countEditText.getText().length() == 0) {
                ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.amount)));
                return false;
            }
        } else {
            ViewCreator.showCustomAlertDialogBox(launchActivity, String.format(getString(R.string.empty_field_format), getString(R.string.type)));
            return false;
        }
        return true;
    }
}
