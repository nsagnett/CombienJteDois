package nsapp.com.combienjtedois.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Debt;

public class DebtListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Debt> debtArrayList = new ArrayList<Debt>();
    private final boolean isDeletingView;
    private final boolean isEditingView;

    public DebtListAdapter(Context context, ArrayList<Debt> debtArrayList, boolean isDeletingView, boolean isEditingView) {
        this.context = context;
        this.debtArrayList.addAll(debtArrayList);
        this.isDeletingView = isDeletingView;
        this.isEditingView = isEditingView;
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
        String reason = debt.getReason();
        Double amount = Double.parseDouble(debt.getAmount());

        TextView reasonView = (TextView) convertView.findViewById(R.id.nameView);
        reasonView.setText(reason);

        TextView amountView = ((TextView) convertView.findViewById(R.id.countView));

        amountView.setText(String.format(context.getString(R.string.money_format), amount));
        amountView.setTextColor(amount >= 0 ? context.getResources().getColor(R.color.green) : context.getResources().getColor(R.color.red));

        if (isDeletingView) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.otherView);
            imageView.setImageResource(R.drawable.delete);
            imageView.setVisibility(View.VISIBLE);
        } else if (isEditingView) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.otherView);
            imageView.setImageResource(R.drawable.edit);
            imageView.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
