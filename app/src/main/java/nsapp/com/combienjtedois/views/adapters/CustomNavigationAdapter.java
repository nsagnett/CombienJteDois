package nsapp.com.combienjtedois.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import nsapp.com.combienjtedois.R;

public class CustomNavigationAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<String> items;

    public CustomNavigationAdapter(Context context, String[] items) {
        this.context = context;
        this.items = new ArrayList<>(Arrays.asList(items));
    }

    @Override
    public int getCount() {
        return items.size();
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
            convertView = inflater.inflate(R.layout.navigation_layout, null);
        }
        switch (position) {
            case 0:
                ((ImageView) convertView.findViewById(R.id.navigationItemImage)).setImageResource(R.drawable.money);
                break;
            case 1:
                ((ImageView) convertView.findViewById(R.id.navigationItemImage)).setImageResource(R.drawable.objects);
                break;
            case 2:
                ((ImageView) convertView.findViewById(R.id.navigationItemImage)).setImageResource(R.drawable.event);
                break;
            case 3:
                ((ImageView) convertView.findViewById(R.id.navigationItemImage)).setImageResource(R.drawable.donation);
                break;
            default:
                break;
        }

        ((TextView) convertView.findViewById(R.id.navigationItemText)).setText(items.get(position));

        return convertView;
    }
}
