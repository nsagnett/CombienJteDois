package nsapp.com.combienjtedois.views.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;

import nsapp.com.combienjtedois.model.Person;
import nsapp.com.combienjtedois.model.Tools;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;

public class DetailFragment extends AbstractFragment {

    public static DetailFragment newInstance(Person person) {
        DetailFragment fragment = new DetailFragment();
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
        ((LaunchActivity) getActivity()).updateActionBarTitle(person.getName());
        notifyChanges();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isDeletingView && !debtArrayList.isEmpty()) {
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
    }
}
