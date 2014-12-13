package nsapp.com.combienjtedois.views.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import nsapp.com.combienjtedois.model.Debt;
import nsapp.com.combienjtedois.model.Person;
import nsapp.com.combienjtedois.model.Present;
import nsapp.com.combienjtedois.model.Utils;

public class DetailPresentFragment extends AbstractFragment {

    public static DetailPresentFragment newInstance(Present present) {
        DetailPresentFragment fragment = new DetailPresentFragment();
        Bundle args = new Bundle();
        args.putSerializable(Utils.PRESENT_KEY, present);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
    }

    @Override
    public void addItem(String importName, String importPhone) {
    }
}
