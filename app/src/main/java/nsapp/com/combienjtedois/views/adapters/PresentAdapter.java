package nsapp.com.combienjtedois.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Event;
import nsapp.com.combienjtedois.model.Utils;

public class PresentAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Event> eventArrayList = new ArrayList<>();

    public PresentAdapter(Context context, ArrayList<Event> eventArrayList) {
        this.context = context;
        this.eventArrayList.addAll(eventArrayList);
    }

    @Override
    public int getCount() {
        return eventArrayList.size();
    }

    @Override
    public Event getItem(int position) {
        return eventArrayList.get(position);
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
        Event event = eventArrayList.get(position);

        ((TextView) convertView.findViewById(R.id.nameView)).setText(String.format(context.getString(R.string.consignee_format), event.getConsignee()));
        ((TextView) convertView.findViewById(R.id.presentView)).setText(String.format(context.getString(R.string.event_format), event.getSubject()));
        ((TextView) convertView.findViewById(R.id.participantNumberView)).setText(String.format(context.getString(R.string.participant_number_format), event.getParticipantNumber()));
        ((TextView) convertView.findViewById(R.id.valueView)).setText(String.format(context.getString(R.string.value_format), event.getValue() + context.getString(R.string.euro)));

        long time = Utils.getTimeBeforeEvent(event.getDate());

        if (time <= 0) {
            ((TextView) convertView.findViewById(R.id.subTitleView)).setText(context.getString(R.string.finish));
        } else {
            ((TextView) convertView.findViewById(R.id.subTitleView)).setText(String.format(context.getString(R.string.event_date_format), event.getDate(), Utils.convertLifeTimeFromMillis(context, Utils.getTimeBeforeEvent(event.getDate()))));
        }

        return convertView;
    }
}
