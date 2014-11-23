package nsapp.com.combienjtedois.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;

public class PresentMoneyFragment extends AbstractMoneyFragment {

    public static PresentMoneyFragment newInstance(int sectionNumber) {
        PresentMoneyFragment fragment = new PresentMoneyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  super.onCreateView(inflater, container, savedInstanceState);

        // TMP
        view.findViewById(R.id.switchViewHeader).setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LaunchActivity) getActivity()).updateActionBarTitle(getString(R.string.title_section3));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
