package nsapp.com.combienjtedois.views.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.listeners.SwipeDismissListViewTouchListener;
import nsapp.com.combienjtedois.model.Debt;
import nsapp.com.combienjtedois.model.Person;
import nsapp.com.combienjtedois.model.Preferences;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;

public abstract class AbstractFragment extends Fragment implements AdapterView.OnItemClickListener {


    static final String ARG_SECTION_NUMBER = "section_number";
    static final String RESULT_KEY = "result";
    static final String OPERATION = "operation";
    static final String TYPE = "type";
    static final String DEBT_EXTRA = "debt";

    LaunchActivity launchActivity;

    ArrayList<Person> personArrayList = new ArrayList<Person>();
    ArrayList<Debt> debtArrayList = new ArrayList<Debt>();

    ListView listView;
    TextView footerView;
    TextView headerCountView;
    boolean isEditingView;

    SharedPreferences preferences;
    boolean confirmDismiss;

    Person selectedPerson;
    Debt selectedDebt;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_listview, container, false);
        launchActivity = ((LaunchActivity) getActivity());
        footerView = (TextView) inflater.inflate(R.layout.footer_listview, null, false);

        listView = (ListView) view.findViewById(R.id.listView);
        headerCountView = (TextView) view.findViewById(R.id.headerCountView);

        listView.setOnItemClickListener(this);
        launchActivity.supportInvalidateOptionsMenu();

        preferences = PreferenceManager.getDefaultSharedPreferences(launchActivity);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((LaunchActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onResume() {
        super.onResume();
        SwipeDismissListViewTouchListener swipeDismissListViewTouchListener = new SwipeDismissListViewTouchListener(listView, new SwipeDismissListViewTouchListener.OnDismissCallback() {
            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                for (final int position : reverseSortedPositions) {
                    deleteItem(position);
                }
            }
        });
        listView.setOnTouchListener(swipeDismissListViewTouchListener);
        listView.setOnScrollListener(swipeDismissListViewTouchListener.makeScrollListener());
        updatePreferences();
    }

    private void updatePreferences() {
        confirmDismiss = preferences.getBoolean(Preferences.CONFIRM_DISMISS_KEY, true);
    }

    public abstract void addItem(String importName, String importPhone);

    public abstract void deleteItem(final int position);

    public boolean isEditingView() {
        return isEditingView;
    }

    public void setEditingView(boolean isEditingView) {
        this.isEditingView = isEditingView;
    }

    public void prepareOnReplaceTransaction(Fragment fragment) {
        isEditingView = false;
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_in_down, R.anim.slide_out_down);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
