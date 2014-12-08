package nsapp.com.combienjtedois.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.LoanObject;

public class LoanObjectAdapter extends BaseAdapter {

    private final Context context;
    private ArrayList<LoanObject> loanObjects = new ArrayList<LoanObject>();

    public LoanObjectAdapter(Context context, ArrayList<LoanObject> loanObjects) {
        this.context = context;
        this.loanObjects.addAll(loanObjects);
    }

    @Override
    public int getCount() {
        return loanObjects.size();
    }

    @Override
    public LoanObject getItem(int position) {
        return loanObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.loan_object_holder, null);
        }
        LoanObject loanObject = loanObjects.get(position);

        ((TextView) convertView.findViewById(R.id.nameView)).setText(loanObject.getNamePerson());
        ((TextView) convertView.findViewById(R.id.categoryView)).setText(loanObject.getCategory());
        ((TextView) convertView.findViewById(R.id.nameObjectView)).setText(loanObject.getNameObject());
        ((TextView) convertView.findViewById(R.id.typeLoanView)).setText(loanObject.getType());
        ((TextView) convertView.findViewById(R.id.dateView)).setText(String.format(context.getString(R.string.add_date_format), loanObject.getDate()));

        return convertView;
    }
}
