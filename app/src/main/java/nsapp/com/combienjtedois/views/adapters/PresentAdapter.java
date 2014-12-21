package nsapp.com.combienjtedois.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Present;
import nsapp.com.combienjtedois.model.Utils;

public class PresentAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Present> presentArrayList = new ArrayList<Present>();

    public PresentAdapter(Context context, ArrayList<Present> presentArrayList) {
        this.context = context;
        this.presentArrayList.addAll(presentArrayList);
    }

    @Override
    public int getCount() {
        return presentArrayList.size();
    }

    @Override
    public Present getItem(int position) {
        return presentArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.present_holder, null);
        }
        Present present = presentArrayList.get(position);

        ((TextView) convertView.findViewById(R.id.nameView)).setText(String.format(context.getString(R.string.consignee_format), present.getConsignee()));
        ((TextView) convertView.findViewById(R.id.presentView)).setText(String.format(context.getString(R.string.present_format), present.getPresent()));
        ((TextView) convertView.findViewById(R.id.participantNumberView)).setText(String.format(context.getString(R.string.participant_number_format), present.getParticipantNumber()));
        ((TextView) convertView.findViewById(R.id.valueView)).setText(String.format(context.getString(R.string.value_format), present.getValue() + context.getString(R.string.euro)));

        long time = Utils.getTimeBeforeEvent(present.getDate());

        if (time <= 0) {
            ((TextView) convertView.findViewById(R.id.subTitleView)).setText(context.getString(R.string.finish));
        } else {
            ((TextView) convertView.findViewById(R.id.subTitleView)).setText(String.format(context.getString(R.string.event_date_format), present.getDate(), Utils.convertLifeTimeFromMillis(context, Utils.getTimeBeforeEvent(present.getDate()))));
        }

        return convertView;
    }
}
