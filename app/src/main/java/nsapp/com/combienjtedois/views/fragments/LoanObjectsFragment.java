package nsapp.com.combienjtedois.views.fragments;

import android.os.Bundle;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;

public class LoanObjectsFragment extends AbstractFragment {

    public static LoanObjectsFragment newInstance(int sectionNumber) {
        LoanObjectsFragment fragment = new LoanObjectsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LaunchActivity) getActivity()).updateActionBarTitle(getString(R.string.title_section2));
    }

    @Override
    public void addItem(String importName, String importPhone) {

    }
}
