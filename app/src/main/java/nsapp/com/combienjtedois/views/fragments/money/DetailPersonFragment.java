package nsapp.com.combienjtedois.views.fragments.money;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.listeners.SwipeDismissListViewTouchListener;
import nsapp.com.combienjtedois.model.Debt;
import nsapp.com.combienjtedois.model.Person;
import nsapp.com.combienjtedois.model.Utils;
import nsapp.com.combienjtedois.views.ViewCreator;
import nsapp.com.combienjtedois.views.activities.EditTextAmountActivity;
import nsapp.com.combienjtedois.views.activities.FullScreenImageActivity;
import nsapp.com.combienjtedois.views.adapters.SimpleListAdapter;

public class DetailPersonFragment extends AbstractMoneyFragment implements View.OnClickListener {

    private static final String RESULT_KEY = "result";
    private static final String OPERATION = "operation";
    private static final String TYPE = "type";
    private static final String DEBT_EXTRA = "debt";
    private Debt debtExtra;

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
        updateProfileView();
        SwipeDismissListViewTouchListener swipeDismissListViewTouchListener = new SwipeDismissListViewTouchListener(listView, new SwipeDismissListViewTouchListener.OnDismissCallback() {
            @Override
            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                for (final int position : reverseSortedPositions) {
                    final AlertDialog alert = ViewCreator.createCustomConfirmDialogBox(getActivity(), R.string.message_delete_element);
                    alert.show();
                    alert.findViewById(R.id.positiveView).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                            Utils.dbManager.setModificationDatePerson(selectedPerson.getId(), (String) DateFormat.format(Utils.PATTERN_DATE, new Date().getTime()));
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
    public void onClick(View v) {
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

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        if (!debtArrayList.isEmpty() && isEditingView) {
            modifyItem(debtArrayList.get(position));
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
                    Utils.dbManager.setModificationDatePerson(selectedPerson.getId(), (String) DateFormat.format(Utils.PATTERN_DATE, new Date().getTime()));
                    Utils.dbManager.createDebt(selectedPerson.getId(), sign + countEditText.getText().toString(), reasonEditText.getText().toString(), (String) DateFormat.format(Utils.PATTERN_DATE, new Date().getTime()));
                    notifyChanges();
                }
            }
        });
    }

    public void modifyItem(final Debt debt) {
        final AlertDialog alert = ViewCreator.createCustomModifyDebtDialogBox(launchActivity);
        alert.show();

        final TextView increaseTextView = ((TextView) alert.findViewById(R.id.addTextView));
        final TextView reduceTextView = ((TextView) alert.findViewById(R.id.subtractTextView));
        final String type;
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
                Utils.dbManager.modifyDebt(debt.getId(), finalSign + amountInteger.toString(), (String) DateFormat.format(Utils.PATTERN_DATE, new Date().getTime()));
                Utils.dbManager.setModificationDatePerson(selectedPerson.getId(), (String) DateFormat.format(Utils.PATTERN_DATE, new Date().getTime()));
                notifyChanges();
            }
        });
    }

    protected boolean checkAddDebtForm(EditText reasonEditText, EditText countEditText, String sign) {
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

    public Debt getDebtExtra() {
        return debtExtra;
    }

    public void setDebtExtra(Debt debtExtra) {
        this.debtExtra = debtExtra;
    }

}
