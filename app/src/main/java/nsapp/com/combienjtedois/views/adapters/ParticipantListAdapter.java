package nsapp.com.combienjtedois.views.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Event;
import nsapp.com.combienjtedois.model.Participant;
import nsapp.com.combienjtedois.services.SendSmsTask;
import nsapp.com.combienjtedois.utils.ViewCreator;

public class ParticipantListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Participant> participants = new ArrayList<>();
    private final Event event;

    private final boolean isEditingView;

    public ParticipantListAdapter(Context context, ArrayList<Participant> participants, boolean isEditingView, Event event) {
        this.context = context;
        this.participants.addAll(participants);
        this.isEditingView = isEditingView;
        this.event = event;
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

        final Participant participant = participants.get(position);
        String name = participant.getName();

        ((TextView) convertView.findViewById(R.id.nameView)).setText(name);
        TextView subTitleView = ((TextView) convertView.findViewById(R.id.subTitleView));
        subTitleView.setText(String.format(context.getString(R.string.budget_format), participant.getBudget() + context.getString(R.string.euro)));
        ImageView smsView = (ImageView) convertView.findViewById(R.id.smsView);
        smsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alert = ViewCreator.createSendMessageDialogBox(context);
                alert.show();

                final EditText messageView = (EditText) alert.findViewById(R.id.messageView);
                final EditText phoneView = (EditText) alert.findViewById(R.id.phoneNumberEditView);

                messageView.setText(String.format(context.getString(R.string.message_text_event_format), participant.getBudget(), event.getSubject()));
                phoneView.setText(participant.getPhoneNumber());

                alert.findViewById(R.id.neutralTextView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkSendForm(messageView, phoneView)) {
                            alert.dismiss();
                            new SendSmsTask(context).execute(messageView.getText().toString(), phoneView.getText().toString());
                        }
                    }
                });
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

    private boolean checkSendForm(EditText messageView, EditText phoneNumberView) {
        if (messageView.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(context, String.format(context.getString(R.string.empty_field_format), context.getString(R.string.message)));
            return false;
        } else if (phoneNumberView.getText().length() == 0) {
            ViewCreator.showCustomAlertDialogBox(context, String.format(context.getString(R.string.empty_field_format), context.getString(R.string.phone_number)));
            return false;
        }
        return true;
    }
}