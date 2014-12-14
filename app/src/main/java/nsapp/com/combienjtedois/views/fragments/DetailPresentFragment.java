package nsapp.com.combienjtedois.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Present;
import nsapp.com.combienjtedois.model.Utils;

public class DetailPresentFragment extends AbstractFragment {

    private Present selectedPresent;

    public static DetailPresentFragment newInstance(Present present) {
        DetailPresentFragment fragment = new DetailPresentFragment();
        Bundle args = new Bundle();
        args.putSerializable(Utils.PRESENT_KEY, present);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_listview, container, false);

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        footerView = (TextView) inflater.inflate(R.layout.footer_listview, null, false);

        launchActivity.supportInvalidateOptionsMenu();

        selectedPresent = (Present) getArguments().getSerializable(Utils.PRESENT_KEY);
        view.findViewById(R.id.headerLayout).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.headerNameView)).setText(selectedPresent.getPresent());
        ((ImageView) view.findViewById(R.id.headerProfileView)).setImageResource(R.drawable.presents);
        ((TextView) view.findViewById(R.id.valueTextView)).setText(String.format(getString(R.string.value_format), selectedPresent.getValue()));

        view.findViewById(R.id.smsView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SMS TASK
            }
        });

        return view;
    }

    private void notifyChanges() {

    }

    @Override
    public void onResume() {
        super.onResume();
        launchActivity.updateActionBarTitle(String.format(getString(R.string.consignee_format), selectedPresent.getConsignee()));
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
    }

    @Override
    public void addItem(String importName, String importPhone) {
    }
}
