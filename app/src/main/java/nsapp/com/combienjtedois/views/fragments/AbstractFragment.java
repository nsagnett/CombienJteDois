package nsapp.com.combienjtedois.views.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;

public class AbstractFragment extends Fragment {


    protected static final String ARG_SECTION_NUMBER = "section_number";
    protected static final String PERSON_KEY = "person";

    public enum listWantedType {PERSON, DEBT}

    protected boolean isDeletingView;
    protected boolean isEditingView;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((LaunchActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

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
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_in_down, R.anim.slide_out_down);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
