package nsapp.com.combienjtedois.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.listeners.SwipeListener;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;

public abstract class AbstractFragment extends Fragment implements AdapterView.OnItemClickListener {


    protected static final String ARG_SECTION_NUMBER = "section_number";

    public enum listWantedType {PERSON, DEBT}

    protected boolean isDeletingView;
    protected boolean isEditingView;

    protected LaunchActivity launchActivity;

    protected SwipeListener swipeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        launchActivity = ((LaunchActivity) getActivity());
        swipeListener = new SwipeListener();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((LaunchActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public abstract void addItem(String importName, String importPhone);

    public boolean isDeletingView() {
        return isDeletingView;
    }

    public boolean isEditingView() {
        return isEditingView;
    }

    public void setDeletingView(boolean isDeletingView) {
        this.isDeletingView = isDeletingView;
    }

    public void setEditingView(boolean isEditingView) {
        this.isEditingView = isEditingView;
    }

    protected void prepareOnReplaceTransaction(Fragment fragment) {
        isDeletingView = false;
        isEditingView = false;
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_in_down, R.anim.slide_out_down);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
