package nsapp.com.combienjtedois.views.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
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
    public void onResume() {
        super.onResume();
        ((LaunchActivity) getActivity()).updateActionBarTitle(getString(R.string.title_section1));
        notifyChanges(listWantedType.PERSON);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isDeletingView) {
            final int idPerson = (int) personArrayList.get(position).getId();
            ScaleAnimation anim = new ScaleAnimation(1, 0, 1, 0);
            anim.setDuration(Tools.ANIMATION_DURATION);
            parent.getChildAt(position).startAnimation(anim);
            new Handler().postDelayed(new Runnable() {

                public void run() {
                    Tools.dbManager.deletePerson(idPerson);
                    notifyChanges(listWantedType.PERSON);
                }

            }, Tools.ANIMATION_DURATION);
        } else {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_in_down, R.anim.slide_out_down);
            transaction.replace(R.id.container, DetailFragment.newInstance(personArrayList.get(position)));
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
