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
import nsapp.com.combienjtedois.model.Participant;

public class ParticipantListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Participant> participants = new ArrayList<>();

    private final boolean isEditingView;

    public ParticipantListAdapter(Context context, ArrayList<Participant> participants, boolean isEditingView) {
        this.context = context;
        this.participants.addAll(participants);
        this.isEditingView = isEditingView;
    }

    @Override
    public int getCount() {
        return participants.size();
    }

    @Override
    public Participant getItem(int position) {
        return participants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.participant_holder, null);
        }

        Participant participant = participants.get(position);
        String name = participant.getName();

        ((TextView) convertView.findViewById(R.id.nameView)).setText(name);
        TextView subTitleView = ((TextView) convertView.findViewById(R.id.subTitleView));
        subTitleView.setText(String.format(context.getString(R.string.budget_format), participant.getBudget() + context.getString(R.string.euro)));
        ImageView smsView = (ImageView) convertView.findViewById(R.id.smsView);
        smsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO sms task
            }
        });

        TextView rightView = ((TextView) convertView.findViewById(R.id.rightView));

        if (participant.isPaid()) {
            rightView.setText(context.getString(R.string.check));
            rightView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.paid, 0, 0, 0);
            smsView.setVisibility(View.GONE);
        } else {
            rightView.setText(context.getString(R.string.waiting));
            rightView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.clock, 0, 0, 0);
        }


        if (isEditingView) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.otherView);
            imageView.setImageResource(R.drawable.dark_edit);
            imageView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
