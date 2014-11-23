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
import android.widget.Toast;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Person;
import nsapp.com.combienjtedois.model.Tools;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;

public class MoneyFragment extends AbstractMoneyFragment {

    public static MoneyFragment newInstance(int sectionNumber) {
        MoneyFragment fragment = new MoneyFragment();
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
        ((LaunchActivity) getActivity()).updateActionBarTitle(getString(R.string.title_section1));
        notifyChanges();
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        Person person = personArrayList.get(position);
        if (isDeletingView) {
            deletePerson(parent, position, (int) person.getId());
        } else if (isEditingView) {
            modifyPerson(person);
        } else if (!personArrayList.isEmpty()) {
            prepareOnReplaceTransaction(DetailMoneyFragment.newInstance(person));
        }
    }

    private void modifyPerson(final Person person) {
        final AlertDialog alert = Tools.createCustomAddPersonDialogBox(getActivity(), R.string.modify_person, R.drawable.edit, R.string.validate);
        alert.show();
        final EditText editText = ((EditText) alert.findViewById(R.id.editTextView));
        alert.findViewById(R.id.neutralTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPersonForm(editText)) {
                    alert.dismiss();
                    Tools.dbManager.modifyPerson(person.getId(), editText.getText().toString(), person.getTotalAmount(), person.getPhoneNumber());
                    notifyChanges();
                    Toast.makeText(getActivity(), getString(R.string.toast_modify), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deletePerson(final AdapterView<?> parent, final int position, final int idPerson) {
        final AlertDialog alert = Tools.createCustomConfirmDialogBox(getActivity(), R.string.warning_text, R.drawable.warning, R.string.message_delete_person_text, R.string.positive_text, R.string.negative_text);
        alert.show();
        alert.findViewById(R.id.positiveView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                ScaleAnimation anim = new ScaleAnimation(1, 0, 1, 0);
                anim.setDuration(Tools.ANIMATION_DURATION);
                parent.getChildAt(position).startAnimation(anim);
                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        Tools.dbManager.deletePerson(idPerson);
                        notifyChanges();
                    }

                }, Tools.ANIMATION_DURATION);
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
}
