package nsapp.com.combienjtedois.views.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.LoanObject;
import nsapp.com.combienjtedois.utils.Utils;
import nsapp.com.combienjtedois.utils.ViewCreator;

public class LoanObjectAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<LoanObject> loanObjects = new ArrayList<>();

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
        final LoanObject loanObject = loanObjects.get(position);

        ((TextView) convertView.findViewById(R.id.nameView)).setText(String.format(context.getString(R.string.name_with_points), loanObject.getNamePerson()));
        ((TextView) convertView.findViewById(R.id.categoryView)).setText(context.getString(R.string.category_object_with_points) + loanObject.getCategory());
        ((TextView) convertView.findViewById(R.id.nameObjectView)).setText(context.getString(R.string.object_description_with_points) + loanObject.getNameObject());
        ((TextView) convertView.findViewById(R.id.typeLoanView)).setText(context.getString(R.string.type_with_points) + loanObject.getType());
        TextView dateView = ((TextView) convertView.findViewById(R.id.subTitleView));
        dateView.setText(String.format(context.getString(R.string.add_date_format), loanObject.getDate()));
        dateView.setText(dateView.getText() + String.format(context.getString(R.string.lifetime_format), Utils.convertLifeTime(context, loanObject.getDate())));

        return convertView;
    }
}
