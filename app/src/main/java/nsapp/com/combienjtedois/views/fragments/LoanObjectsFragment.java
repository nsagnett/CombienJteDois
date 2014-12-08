package nsapp.com.combienjtedois.views.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.DBManager;
import nsapp.com.combienjtedois.model.LoanObject;
import nsapp.com.combienjtedois.model.Utils;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;
import nsapp.com.combienjtedois.views.adapters.LoanObjectAdapter;

public class LoanObjectsFragment extends AbstractFragment {

    private TextView footerView;
    private ListView listView;

    public static LoanObjectsFragment newInstance(int sectionNumber) {
        LoanObjectsFragment fragment = new LoanObjectsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_listview, container, false);

        listView = (ListView) view.findViewById(R.id.listView);
        footerView = (TextView) inflater.inflate(R.layout.footer_listview, null, false);

        view.findViewById(R.id.headerSeparator).setVisibility(View.GONE);
        launchActivity.supportInvalidateOptionsMenu();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LaunchActivity) getActivity()).updateActionBarTitle(getString(R.string.title_section2));
        notifyChanges();
    }

    @Override
    public void addItem(String importName, String importPhone) {

    }

    private void notifyChanges() {
        Cursor c = Utils.dbManager.fetchAllObjects();
        ArrayList<LoanObject> loanObjects = new ArrayList<LoanObject>();

        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex(DBManager.NAME_PERSON_KEY));
            String nameObject = c.getString(c.getColumnIndex(DBManager.NAME_OBJECT_KEY));
            String category = c.getString(c.getColumnIndex(DBManager.CATEGORY_OBJECT_KEY));
            String type = c.getString(c.getColumnIndex(DBManager.TYPE_OBJECT_KEY));
            String date = c.getString(c.getColumnIndex(DBManager.DATE_KEY));

            long id = Utils.dbManager.fetchIdObject(name, date);
            loanObjects.add(new LoanObject(id, name, category, nameObject, type, date));
        }

        if (loanObjects.isEmpty()) {
            isEditingView = false;
            if (listView.getFooterViewsCount() == 0) {
                footerView.setText(R.string.add_element);
                footerView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.dark_add, 0, 0);
                footerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addItem(null, null);
                    }
                });
                listView.addFooterView(footerView);
            }
        } else {
            listView.removeFooterView(footerView);
        }

        LoanObjectAdapter loanObjectAdapter = new LoanObjectAdapter(launchActivity, loanObjects);
        listView.setAdapter(loanObjectAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }
}
