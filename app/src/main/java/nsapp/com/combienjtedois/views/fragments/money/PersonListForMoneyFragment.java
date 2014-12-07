package nsapp.com.combienjtedois.views.fragments.money;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.listeners.SwipeListener;
import nsapp.com.combienjtedois.model.Person;
import nsapp.com.combienjtedois.model.Utils;
import nsapp.com.combienjtedois.model.ViewCreator;

public class PersonListForMoneyFragment extends AbstractMoneyFragment {

    public static PersonListForMoneyFragment newInstance(int sectionNumber) {
        PersonListForMoneyFragment fragment = new PersonListForMoneyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        listType = listWantedType.PERSON;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        launchActivity.updateActionBarTitle(getString(R.string.title_section1));
        View view = getView();
        if (view != null) {
            view.findViewById(R.id.headerSeparator).setVisibility(View.GONE);
        }
        notifyChanges();
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        if (!personArrayList.isEmpty()) {
            Person person = personArrayList.get(position);
            if (swipeListener.swipeDetected()) {
                if (swipeListener.getAction() == SwipeListener.Action.RL || swipeListener.getAction() == SwipeListener.Action.LR) {
                    deletePerson(parent, position, (int) person.getId(), swipeListener.getAction());
                }
            } else {
                if (isDeletingView) {
                    deletePerson(parent, position, (int) person.getId(), SwipeListener.Action.None);
                } else if (isEditingView) {
                    modifyPerson(person);
                } else if (!personArrayList.isEmpty()) {
                    prepareOnReplaceTransaction(DetailPersonFragment.newInstance(person));
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Utils.IMPORT_CONTACT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    importContact(data);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void addItem(String importName, String importPhoneNumber) {
        final AlertDialog alert = ViewCreator.createCustomAddPersonDialogBox(getActivity(), R.string.add_person, R.drawable.add, R.string.validate);
        alert.show();
        final EditText nameEditView = ((EditText) alert.findViewById(R.id.namePersonEditView));
        final EditText phoneNumberView = ((EditText) alert.findViewById(R.id.phoneNumberEditView));
        final TextView importContactView = ((TextView) alert.findViewById(R.id.importContactView));

        nameEditView.setText(importName);
        phoneNumberView.setText(importPhoneNumber);

        importContactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), Utils.IMPORT_CONTACT_CODE);
                alert.dismiss();
            }
        });

        alert.findViewById(R.id.neutralTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPersonForm(nameEditView)) {
                    alert.dismiss();
                    Utils.dbManager.createPerson(nameEditView.getText().toString(), importContactView.getText().toString(), (String) DateFormat.format(Utils.PATTERN_DATE, new Date().getTime()));
                    notifyChanges();
                    Toast.makeText(getActivity(), getString(R.string.toast_add_person), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void modifyPerson(final Person person) {
        final AlertDialog alert = ViewCreator.createCustomAddPersonDialogBox(getActivity(), R.string.modify_person, R.drawable.edit, R.string.validate);
        alert.show();
        final EditText nameView = ((EditText) alert.findViewById(R.id.namePersonEditView));
        final EditText phoneNumberView = ((EditText) alert.findViewById(R.id.phoneNumberEditView));
        nameView.setText(person.getName());
        phoneNumberView.setText(person.getPhoneNumber());
        alert.findViewById(R.id.neutralTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPersonForm(nameView)) {
                    alert.dismiss();
                    Utils.dbManager.modifyPerson(person.getId(), nameView.getText().toString(), person.getTotalAmount(), person.getPhoneNumber(), (String) DateFormat.format(Utils.PATTERN_DATE, new Date().getTime()));
                    notifyChanges();
                    Toast.makeText(getActivity(), getString(R.string.toast_modify), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deletePerson(final AdapterView<?> parent, final int position, final int idPerson, final SwipeListener.Action action) {
        final AlertDialog alert = ViewCreator.createCustomConfirmDialogBox(getActivity(), R.string.warning_text, R.drawable.warning, R.string.message_delete_person_text, R.string.positive_text, R.string.negative_text);
        alert.show();
        alert.findViewById(R.id.positiveView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                TranslateAnimation anim;
                if (action == SwipeListener.Action.LR || action == SwipeListener.Action.None) {
                    anim = new TranslateAnimation(0, Utils.getScreenWidth(launchActivity), 0, 0);
                } else {
                    anim = new TranslateAnimation(0, -Utils.getScreenWidth(launchActivity), 0, 0);
                }
                anim.setDuration(Utils.ANIMATION_DURATION);

                parent.getChildAt(position).startAnimation(anim);
                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        Utils.dbManager.deletePerson(idPerson);
                        notifyChanges();
                    }

                }, Utils.ANIMATION_DURATION);
                Toast.makeText(getActivity(), getString(R.string.toast_delete_person), Toast.LENGTH_SHORT).show();
            }
        });
        alert.findViewById(R.id.negativeView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
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

    protected boolean checkPersonForm(EditText editText) {
        if (editText.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(getActivity(),
                    R.string.warning_text,
                    R.drawable.warning,
                    String.format(getString(R.string.empty_field_format), getString(R.string.name)));
            return false;
        }
        return true;
    }
}
