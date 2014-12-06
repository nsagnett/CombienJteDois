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

        int drawableID = 0;
        switch (position) {
            case 0:
                drawableID = R.drawable.camera;
                break;
            case 1:
                drawableID = R.drawable.gallery;
                break;
            case 2:
                drawableID = R.drawable.zoom;
                break;
            case 3:
                drawableID = R.drawable.trash;
                break;
            default:
                break;
        }

        titleView.setCompoundDrawablesWithIntrinsicBounds(drawableID, 0, 0, 0);

        return convertView;
    }
}
