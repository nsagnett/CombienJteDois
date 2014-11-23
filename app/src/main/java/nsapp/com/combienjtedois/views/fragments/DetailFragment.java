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

import nsapp.com.combienjtedois.R;
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

        view.findViewById(R.id.switchViewHeader).setVisibility(View.GONE);
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
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        if (!debtArrayList.isEmpty()) {
            if (isDeletingView) {
                final AlertDialog alert = Tools.createCustomConfirmDialogBox(getActivity(), R.string.warning_text, R.drawable.warning, R.string.message_delete_person_text, R.string.positive_text, R.string.negative_text);
                alert.show();
                alert.findViewById(R.id.positiveView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
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
}
