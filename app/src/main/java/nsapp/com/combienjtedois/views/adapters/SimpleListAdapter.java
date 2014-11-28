package nsapp.com.combienjtedois.views.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import nsapp.com.combienjtedois.R;

public class SimpleListAdapter extends BaseAdapter {
    private Context context;
    private String[] items;

    public SimpleListAdapter(Context context, String[] items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = new TextView(context);
        }

        TextView titleView = ((TextView) convertView);
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        titleView.setPadding(0, 10, 0, 10);
        titleView.setTextSize(17);
        titleView.setText(items[position]);

        if (position == 0) {
            titleView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera, 0, 0, 0);
        } else if (position == 1) {
            titleView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gallery, 0, 0, 0);
        } else {
            titleView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.trash, 0, 0, 0);
        }

        return convertView;
    }
}
