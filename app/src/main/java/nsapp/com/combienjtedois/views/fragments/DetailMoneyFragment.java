package nsapp.com.combienjtedois.views.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Person;
import nsapp.com.combienjtedois.model.Tools;

public class DetailMoneyFragment extends AbstractMoneyFragment {

    public static DetailMoneyFragment newInstance(Person person) {
        DetailMoneyFragment fragment = new DetailMoneyFragment();
        Bundle args = new Bundle();
        args.putSerializable(PERSON_KEY, person);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        person = (Person) getArguments().getSerializable(PERSON_KEY);

        listType = listWantedType.DEBT;

        listView.addHeaderView(headerPersonView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        launchActivity.updateActionBarTitle(person.getName());
        notifyChanges();
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        if (!debtArrayList.isEmpty()) {
            if (isDeletingView) {
                final AlertDialog alert = Tools.createCustomConfirmDialogBox(getActivity(), R.string.warning_text, R.drawable.warning, R.string.message_delete_debt_text, R.string.positive_text, R.string.negative_text);
                alert.show();
                alert.findViewById(R.id.positiveView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                        deleteDebt(parent, position);
                        Toast.makeText(getActivity(), getString(R.string.toast_delete_debt), Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void addItem(String importName, String importPhone) {
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

    private void deleteDebt(AdapterView<?> parent, int position) {
        final int idDebt = (int) debtArrayList.get(position - 1).getId();
        final int idPerson = (int) person.getId();
        ScaleAnimation anim = new ScaleAnimation(1, 0, 1, 0);
        anim.setDuration(Tools.ANIMATION_DURATION);
        parent.getChildAt(position).startAnimation(anim);
        new Handler().postDelayed(new Runnable() {

            public void run() {
                Tools.dbManager.deleteDebt(idDebt, idPerson);
                notifyChanges();
            }

        }, Tools.ANIMATION_DURATION);
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
}
