package nsapp.com.combienjtedois.views.fragments.money;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Debt;
import nsapp.com.combienjtedois.model.Person;
import nsapp.com.combienjtedois.model.Utils;
import nsapp.com.combienjtedois.model.ViewCreator;
import nsapp.com.combienjtedois.views.activities.EditTextAmountActivity;
import nsapp.com.combienjtedois.views.activities.FullScreenImageActivity;
import nsapp.com.combienjtedois.views.adapters.SimpleListAdapter;

import static nsapp.com.combienjtedois.model.ViewCreator.TYPE_SWITCH.TYPE_DEBT;

public class DetailPersonFragment extends AbstractMoneyFragment {

    private static final String RESULT_KEY = "result";
    private static final String OPERATION = "operation";
    private static final String TYPE = "type";
    private static final String DEBT_EXTRA = "debt";
    private Debt debtExtra;
    private int typeDebt = -1;

    public static DetailPersonFragment newInstance(Person person) {
        DetailPersonFragment fragment = new DetailPersonFragment();
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
        listType = listWantedType.DEBT;
        view.findViewById(R.id.headerLayout).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.headerNameView)).setText(selectedPerson.getName());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProfileView();
        notifyChanges();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.footerView) {
            addItem(null, null);
        } else {
            ListView listView = new ListView(launchActivity);
            String[] items = new String[]{getString(R.string.camera), getString(R.string.gallery), getString(R.string.fullscreen), getString(R.string.nothing)};
            SimpleListAdapter adapter = new SimpleListAdapter(launchActivity, items);
            listView.setAdapter(adapter);
            final AlertDialog dialog = ViewCreator.createListViewDialogBox(launchActivity, listView);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();
                    switch (position) {
                        case 0:
                            String fileName = "temp.jpg";
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, fileName);
                            setCapturedImageURI(launchActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values));

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, getCapturedImageURI());
                            startActivityForResult(intent, Utils.TAKE_PICTURE_FOR_PERSON);
                            break;
                        case 1:
                            Intent galleryPicture = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryPicture, Utils.IMPORT_PERSON_IMAGE_CODE);
                            break;
                        case 2:
                            String path = selectedPerson.getImageProfileUrl();
                            if (path != null && !path.isEmpty()) {
                                Intent fullScreenIntent = new Intent(launchActivity, FullScreenImageActivity.class);
                                fullScreenIntent.putExtra(Utils.PATH_KEY, path);
                                startActivity(fullScreenIntent);
                            } else {
                                Toast.makeText(launchActivity, getString(R.string.missing_picture), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 3:
                            Utils.dbManager.setImageProfileUrlPerson(selectedPerson.getId(), "");
                            selectedPerson.setImageProfileUrl("");
                            updateProfileView();
                            break;
                        default:
                            break;
                    }
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        if (!debtArrayList.isEmpty()) {
            selectedDebt = debtArrayList.get(position);
            if (isDeletingView) {
                deleteDebt(parent, position, selectedDebt);
            } else if (isEditingView) {
                modifyItem(selectedDebt);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Utils.IMPORT_DEBT_IMAGE_CODE: {
                if (data != null) {
                    Debt debt = getDebtExtra();
                    Utils.dbManager.setImageProfileUrlDebt(debt.getId(), Utils.getPathImage(launchActivity, data.getData()));
                    notifyChanges();
                }
            }
            break;
            case Utils.TAKE_PICTURE_FOR_DEBT: {
                Debt debt = getDebtExtra();
                Utils.dbManager.setImageProfileUrlDebt(debt.getId(), Utils.getPathImage(launchActivity, capturedImageURI));
                notifyChanges();
            }
            break;
            case Utils.IMPORT_PERSON_IMAGE_CODE:
                if (data != null) {
                    String path = Utils.getPathImage(launchActivity, data.getData());
                    Utils.dbManager.setImageProfileUrlPerson(selectedPerson.getId(), path);
                    selectedPerson.setImageProfileUrl(path);
                    updateProfileView();
                }
                break;
            case Utils.TAKE_PICTURE_FOR_PERSON: {
                String path = Utils.getPathImage(launchActivity, capturedImageURI);
                Utils.dbManager.setImageProfileUrlPerson(selectedPerson.getId(), path);
                selectedPerson.setImageProfileUrl(path);
                updateProfileView();
            }
            break;
            case Utils.UPDATE_DEBT_COUNT:
                if (data != null) {
                    updateCountView(data.getStringExtra(RESULT_KEY));
                } else {
                    updateCountView(selectedDebt.getAmount());
                }
                break;
            default:
                break;
        }
    }

    private void updateCountView(String amount) {
        View view = getView();
        if (view != null) {
            selectedDebt.setAmount(amount);
            modifyItem(selectedDebt);
        }
    }

    private void updateProfileView() {
        View view = getView();
        Bitmap imageFromPath = Utils.getImageFromPath(selectedPerson.getImageProfileUrl());
        if (view != null) {
            if (imageFromPath != null) {
                Bitmap profileImage = ViewCreator.getRoundedShape(imageFromPath);
                Drawable drawable = new BitmapDrawable(getResources(), profileImage);
                ((ImageView) view.findViewById(R.id.headerProfileView)).setImageDrawable(drawable);
            } else {
                ((ImageView) view.findViewById(R.id.headerProfileView)).setImageResource(R.drawable.profile);

            }
            view.findViewById(R.id.headerProfileView).setOnClickListener(this);
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
        ViewCreator.switchView(getActivity(), positiveDebtView, negativeDebtView, this, TYPE_DEBT);

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
                if (checkAddDebtForm(reasonEditText, countEditText, sign)) {
                    alert.dismiss();
                    Utils.dbManager.createDebt(selectedPerson.getId(), sign + countEditText.getText().toString(), reasonEditText.getText().toString());
                    Toast.makeText(getActivity(), getString(R.string.toast_add_debt), Toast.LENGTH_SHORT).show();
                    notifyChanges();
                }
            }
        });
    }

    public void modifyItem(final Debt debt) {
        final AlertDialog alert = ViewCreator.createCustomModifyDebtDialogBox(getActivity(), R.string.modify_debt, R.drawable.edit, R.string.validate);
        alert.show();

        final TextView positiveDebtView = (TextView) alert.findViewById(R.id.positiveDebtView);
        final TextView negativeDebtView = (TextView) alert.findViewById(R.id.negativeDebtView);
        ((TextView) alert.findViewById(R.id.typeDebtView)).setText(R.string.type);
        ((TextView) alert.findViewById(R.id.reasonTextView)).setText(R.string.object);
        final EditText reasonEditText = ((EditText) alert.findViewById(R.id.reasonEditText));
        final TextView addTextView = ((TextView) alert.findViewById(R.id.addTextView));
        final TextView subtractTextView = ((TextView) alert.findViewById(R.id.subtractTextView));

        ViewCreator.switchView(getActivity(), positiveDebtView, negativeDebtView, this, TYPE_DEBT);

        Double amount = Double.parseDouble(debt.getAmount());

        if (amount >= 0) {
            typeDebt = 0;
            positiveDebtView.setSelected(true);
            positiveDebtView.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            typeDebt = 1;
            negativeDebtView.setSelected(true);
            negativeDebtView.setTextColor(getResources().getColor(android.R.color.white));
        }

        final Integer amountInteger = (int) Math.abs(amount);
        String amountString = getString(R.string.amount) + String.format(getString(R.string.money_format), amountInteger.toString());
        ((TextView) alert.findViewById(R.id.countTextView)).setText(amountString);
        reasonEditText.setText(debt.getReason());

        addTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                Intent intent = new Intent(launchActivity, EditTextAmountActivity.class);
                intent.putExtra(OPERATION, 0);
                intent.putExtra(TYPE, typeDebt);
                intent.putExtra(DEBT_EXTRA, selectedDebt);
                startActivityForResult(intent, Utils.UPDATE_DEBT_COUNT);
            }
        });

        subtractTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                Intent intent = new Intent(launchActivity, EditTextAmountActivity.class);
                intent.putExtra(OPERATION, 1);
                intent.putExtra(TYPE, typeDebt);
                intent.putExtra(DEBT_EXTRA, selectedDebt);
                startActivityForResult(intent, Utils.UPDATE_DEBT_COUNT);
            }
        });

        alert.findViewById(R.id.neutralTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sign = null;
                if (positiveDebtView.isSelected()) {
                    sign = "";
                } else if (negativeDebtView.isSelected()) {
                    sign = "-";
                }
                if (reasonEditText.getText().length() == 0) {
                    ViewCreator.showCustomAlertDialogBox(getActivity(),
                            R.string.warning_text,
                            R.drawable.warning,
                            String.format(getString(R.string.empty_field_format), getString(R.string.object)));
                }
                if (checkModifyDebtForm(reasonEditText, sign)) {
                    alert.dismiss();
                    Utils.dbManager.modifyDebt(debt.getId(), sign + amountInteger.toString(), reasonEditText.getText().toString());
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
                final int idPerson = (int) selectedPerson.getId();
                ScaleAnimation anim = new ScaleAnimation(1, 0, 1, 0);
                anim.setDuration(Utils.ANIMATION_DURATION);
                parent.getChildAt(position).startAnimation(anim);
                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        Utils.dbManager.deleteDebt(idDebt, idPerson);
                        notifyChanges();
                    }

                }, Utils.ANIMATION_DURATION);
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

    protected boolean checkAddDebtForm(EditText reasonEditText, EditText countEditText, String sign) {
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

    protected boolean checkModifyDebtForm(EditText reasonEditText, String sign) {
        if (sign != null) {
            if (reasonEditText.getText().length() == 0) {
                ViewCreator.showCustomAlertDialogBox(getActivity(),
                        R.string.warning_text,
                        R.drawable.warning,
                        String.format(getString(R.string.empty_field_format), getString(R.string.object)));
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
