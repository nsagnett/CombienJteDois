package nsapp.com.combienjtedois.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Debt;

public class DebtListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Debt> debtArrayList = new ArrayList<Debt>();

    public DebtListAdapter(Context context, ArrayList<Debt> debtArrayList) {
        this.context = context;
        this.debtArrayList.addAll(debtArrayList);
    }

    @Override
    public int getCount() {
        return debtArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.linear_container, null);
        }

        Debt debt = debtArrayList.get(position);

        TextView nameView = (TextView) convertView.findViewById(R.id.nameView);

        nameView.setText(debt.getReason());
        ((TextView) convertView.findViewById(R.id.countView)).setText(String.format(context.getString(R.string.money_format), debt.getAmount()));

        return convertView;
    }
}
