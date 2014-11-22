package nsapp.com.combienjtedois.views.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Tools;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;

public class MoneyFragment extends AbstractFragment {

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
        notifyChanges(listType);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        if (isDeletingView) {
            final AlertDialog alert = Tools.createCustomConfirmDialogBox(getActivity(), R.string.warning_text, R.drawable.warning, R.string.message_delete_text, R.string.positive_text, R.string.negative_text);
            alert.show();
            alert.findViewById(R.id.positiveView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    final int idPerson = (int) personArrayList.get(position).getId();
                    ScaleAnimation anim = new ScaleAnimation(1, 0, 1, 0);
                    anim.setDuration(Tools.ANIMATION_DURATION);
                    parent.getChildAt(position).startAnimation(anim);
                    new Handler().postDelayed(new Runnable() {

                        public void run() {
                            Tools.dbManager.deletePerson(idPerson);
                            notifyChanges(listType);
                        }

                    }, Tools.ANIMATION_DURATION);
                }
            });
            alert.findViewById(R.id.negativeView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
        } else if(!personArrayList.isEmpty()){
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_in_down, R.anim.slide_out_down);
            transaction.replace(R.id.container, DetailFragment.newInstance(personArrayList.get(position)));
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
