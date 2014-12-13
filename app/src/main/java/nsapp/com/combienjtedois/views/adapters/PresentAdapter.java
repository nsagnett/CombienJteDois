package nsapp.com.combienjtedois.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        ((TextView) convertView.findViewById(R.id.valueView)).setText(String.format(context.getString(R.string.value_format), present.getValue()));
        ((TextView) convertView.findViewById(R.id.dateView)).setText(String.format(context.getString(R.string.event_date_format), present.getDate(), Utils.convertLifeTimeFromMillis(context, getTimeBeforeEvent(present))));
        convertView.findViewById(R.id.smsView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }

    private long getTimeBeforeEvent(Present present) {
        try {
            long eventDate = new SimpleDateFormat(Utils.EVENT_PATTERN_DATE).parse(present.getDate()).getTime();
            return eventDate - (new Date().getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}