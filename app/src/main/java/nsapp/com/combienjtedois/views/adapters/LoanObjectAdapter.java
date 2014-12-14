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
import nsapp.com.combienjtedois.model.Utils;

public class LoanObjectAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<LoanObject> loanObjects = new ArrayList<LoanObject>();

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

        ((TextView) convertView.findViewById(R.id.nameView)).setText(String.format(context.getString(R.string.name_with_points), loanObject.getNamePerson()));
        ((TextView) convertView.findViewById(R.id.categoryView)).setText(context.getString(R.string.category_object_with_points) + loanObject.getCategory());
        ((TextView) convertView.findViewById(R.id.nameObjectView)).setText(context.getString(R.string.object_description_with_points) + loanObject.getNameObject());
        ((TextView) convertView.findViewById(R.id.typeLoanView)).setText(context.getString(R.string.type_with_points) + loanObject.getType());
        TextView dateView = ((TextView) convertView.findViewById(R.id.dateView));
        dateView.setText(String.format(context.getString(R.string.add_date_format), loanObject.getDate()));
        dateView.setText(dateView.getText() + String.format(context.getString(R.string.lifetime_format), Utils.convertLifeTime(context, loanObject.getDate())));
        if (loanObject.getType().equals(context.getString(R.string.negative_loan))) {
            convertView.findViewById(R.id.smsView).setVisibility(View.GONE);
        } else {
            convertView.findViewById(R.id.smsView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        return convertView;
    }
}
