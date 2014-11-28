package nsapp.com.combienjtedois.views.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import nsapp.com.combienjtedois.model.AllDatas;
import nsapp.com.combienjtedois.model.Debt;
import nsapp.com.combienjtedois.model.Person;
import nsapp.com.combienjtedois.model.ViewCreator;

public class DetailMoneyFragment extends AbstractMoneyFragment {

    private Debt debtExtra;

    public static DetailMoneyFragment newInstance(Person person) {
        DetailMoneyFragment fragment = new DetailMoneyFragment();
        Bundle args = new Bundle();
        args.putSerializable(AllDatas.PERSON_KEY, person);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        person = (Person) getArguments().getSerializable(AllDatas.PERSON_KEY);

        listType = listWantedType.DEBT;

        Bitmap imageFromPath = AllDatas.getImageFromPath(person.getImageProfileUrl());
        if (imageFromPath != null) {
            Bitmap bmp = Bitmap.createScaledBitmap(imageFromPath, AllDatas.SIZE_IMAGE, AllDatas.SIZE_IMAGE, true);
            Bitmap profileImage = ViewCreator.getRoundedShape(bmp);
            Drawable drawable = new BitmapDrawable(getResources(), profileImage);
            headerPersonView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
        } else {
            headerPersonView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.profile);
        }
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
            Debt debt = debtArrayList.get(position - 1);
            if (isDeletingView) {
                deleteDebt(parent, position, debt);
            } else if (isEditingView) {
                modifyItem(debt);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AllDatas.IMPORT_DEBT_IMAGE_CODE: {
                if (data != null) {
                    Debt debt = getDebtExtra();
                    AllDatas.dbManager.setImageProfileUrlDebt(debt.getId(), AllDatas.getPathImage(launchActivity, data.getData()));
                    notifyChanges();
                }
            }
            break;
            case AllDatas.TAKE_PICTURE_FOR_DEBT: {
                Debt debt = getDebtExtra();
                AllDatas.dbManager.setImageProfileUrlDebt(debt.getId(), AllDatas.getPathImage(launchActivity, capturedImageURI));
                notifyChanges();
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void addItem(String importName, String importPhone) {
        final AlertDialog alert = ViewCreator.createCustomAddDebtDialogBox(getActivity(), R.string.add_debt, R.drawable.add, R.string.validate);
        alert.show();
        TextView deviseView = (TextView) alert.findViewById(R.id.deviseView);
        deviseView.setVisibility(View.VISIBLE);
        deviseView.setText(R.string.euro);

        final TextView positiveDebtView = (TextView) alert.findViewById(R.id.positiveDebtView);
        final TextView negativeDebtView = (TextView) alert.findViewById(R.id.negativeDebtView);
        ViewCreator.switchView(getActivity(), positiveDebtView, negativeDebtView, this);

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
                    AllDatas.dbManager.createDebt(person.getId(), sign + countEditText.getText().toString(), reasonEditText.getText().toString());
                    Toast.makeText(getActivity(), getString(R.string.toast_add_debt), Toast.LENGTH_SHORT).show();
                    notifyChanges();
                }
            }
        });
    }

    public void modifyItem(final Debt debt) {
        final AlertDialog alert = ViewCreator.createCustomAddDebtDialogBox(getActivity(), R.string.add_debt, R.drawable.add, R.string.validate);
        alert.show();
        TextView deviseView = (TextView) alert.findViewById(R.id.deviseView);
        deviseView.setVisibility(View.VISIBLE);
        deviseView.setText(R.string.euro);

        final TextView positiveDebtView = (TextView) alert.findViewById(R.id.positiveDebtView);
        final TextView negativeDebtView = (TextView) alert.findViewById(R.id.negativeDebtView);
        ViewCreator.switchView(getActivity(), positiveDebtView, negativeDebtView, this);

        ((TextView) alert.findViewById(R.id.typeDebtView)).setText(R.string.type);
        ((TextView) alert.findViewById(R.id.reasonTextView)).setText(R.string.object);
        ((TextView) alert.findViewById(R.id.countTextView)).setText(R.string.amount);
        final EditText reasonEditText = ((EditText) alert.findViewById(R.id.reasonEditText));
        final EditText countEditText = ((EditText) alert.findViewById(R.id.countEditText));

        countEditText.setText(debt.getAmount());
        reasonEditText.setText(debt.getReason());

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
                    AllDatas.dbManager.modifyDebt(debt.getId(), sign + countEditText.getText().toString(), reasonEditText.getText().toString());
                    Toast.makeText(getActivity(), getString(R.string.toast_modify), Toast.LENGTH_SHORT).show();
                    notifyChanges();
                }
            }
        });
    }

    private void deleteDebt(final AdapterView<?> parent, final int position, final Debt debt) {
        final AlertDialog alert = ViewCreator.createCustomConfirmDialogBox(getActivity(), R.string.warning_text, R.drawable.warning, R.string.message_delete_debt_text, R.string.positive_text, R.string.negative_text);
        alert.show();
        alert.findViewById(R.id.positiveView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                final int idDebt = (int) debt.getId();
                final int idPerson = (int) person.getId();
                ScaleAnimation anim = new ScaleAnimation(1, 0, 1, 0);
                anim.setDuration(AllDatas.ANIMATION_DURATION);
                parent.getChildAt(position).startAnimation(anim);
                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        AllDatas.dbManager.deleteDebt(idDebt, idPerson);
                        notifyChanges();
                    }

                }, AllDatas.ANIMATION_DURATION);
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

    protected boolean checkDebtForm(EditText reasonEditText, EditText countEditText, String sign) {
        if (sign != null) {
            if (reasonEditText.getText().length() == 0) {
                ViewCreator.showCustomAlertDialogBox(getActivity(),
                        R.string.warning_text,
                        R.drawable.warning,
                        String.format(getString(R.string.empty_field_format), getString(R.string.object)));
                return false;
            } else if (countEditText.getText().length() == 0) {
                ViewCreator.showCustomAlertDialogBox(getActivity(),
                        R.string.warning_text,
                        R.drawable.warning,
                        String.format(getString(R.string.empty_field_format), getString(R.string.amount)));
                return false;
            }
        } else {
            ViewCreator.showCustomAlertDialogBox(getActivity(),
                    R.string.warning_text,
                    R.drawable.warning,
                    String.format(getString(R.string.empty_field_format), getString(R.string.type)));
            return false;
        }
        return true;
    }

    public Debt getDebtExtra() {
        return debtExtra;
    }

    public void setDebtExtra(Debt debtExtra) {
        this.debtExtra = debtExtra;
    }
}
