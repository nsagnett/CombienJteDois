package nsapp.com.combienjtedois.views.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.listeners.SwipeListener;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;

public class PresentFragment extends AbstractFragment {

    public static PresentFragment newInstance(int sectionNumber) {
        PresentFragment fragment = new PresentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LaunchActivity) getActivity()).updateActionBarTitle(getString(R.string.title_section3));
    }

    @Override
    public void addItem(String importName, String importPhone) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (swipeListener.swipeDetected()) {
            if (swipeListener.getAction() == SwipeListener.Action.RL || swipeListener.getAction() == SwipeListener.Action.LR) {
                // delete present
            }
        } else if (isDeletingView) {
            // delete present
        }
    }
}
