package nsapp.com.combienjtedois.views;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nsapp.com.combienjtedois.R;

public class ViewCreator {

    private static View getCustomTitleDialogBox(Context context, int resTitleID, int resDrawableTitleID) {
        TextView titleView = new TextView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        titleView.setLayoutParams(params);
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        titleView.setText(resTitleID);
        titleView.setTextSize(17);
        titleView.setTextColor(context.getResources().getColor(android.R.color.white));
        titleView.setBackgroundResource(R.color.light_grey);
        titleView.setCompoundDrawablesWithIntrinsicBounds(resDrawableTitleID, 0, 0, 0);

        return titleView;
    }

    public static void showCustomAlertDialogBox(Context context, String resMessageID) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_layout, null);

        builder.setCustomTitle(getCustomTitleDialogBox(context, R.string.warning_text, R.drawable.warning));

        TextView ok = (TextView) view.findViewById(R.id.neutralTextView);
        ok.setText(R.string.ok);

        ((TextView) view.findViewById(R.id.messageAlertText)).setText(resMessageID);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static AlertDialog createCustomConfirmDialogBox(Context context, int resMessageID) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.confirm_dialog_layout, null);

        builder.setCustomTitle(getCustomTitleDialogBox(context, R.string.warning_text, R.drawable.warning));

        ((TextView) view.findViewById(R.id.messageAlert)).setText(resMessageID);
        ((TextView) view.findViewById(R.id.positiveView)).setText(R.string.positive_text);
        ((TextView) view.findViewById(R.id.negativeView)).setText(R.string.negative_text);

        builder.setView(view);
        return builder.create();
    }

    public static AlertDialog createCustomPersonDialogBox(Context context, int resTitleID, int resDrawableTitleID) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.add_person_dialog_layout, null);

        builder.setCustomTitle(getCustomTitleDialogBox(context, resTitleID, resDrawableTitleID));

        ((TextView) view.findViewById(R.id.neutralTextView)).setText(R.string.validate);

        builder.setView(view);
        return builder.create();
    }

    public static AlertDialog createCustomAddDebtDialogBox(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.add_debt_dialog_layout, null);

        builder.setCustomTitle(getCustomTitleDialogBox(context, R.string.add_element, R.drawable.add));

        ((TextView) view.findViewById(R.id.neutralTextView)).setText(R.string.validate);

        builder.setView(view);


        return builder.create();
    }

    public static AlertDialog createCustomModifyDebtDialogBox(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.modify_money_dialog_layout, null);

        builder.setCustomTitle(getCustomTitleDialogBox(context, R.string.modify_element, R.drawable.edit));

        ((TextView) view.findViewById(R.id.neutralTextView)).setText(R.string.validate);

        builder.setView(view);


        return builder.create();
    }

    public static void switchView(final Context context, final TextView viewOne, final TextView viewTwo) {
        final int dark_blue = context.getResources().getColor(R.color.dark_blue);
        final int white = context.getResources().getColor(android.R.color.white);

        viewOne.setTextColor(dark_blue);
        viewTwo.setTextColor(dark_blue);

        viewOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewOne.isSelected()) {
                    viewTwo.setSelected(false);
                    viewOne.setSelected(true);
                    viewOne.setTextColor(white);
                    viewTwo.setTextColor(dark_blue);
                }
            }
        });
        viewTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewTwo.isSelected()) {
                    viewOne.setSelected(false);
                    viewTwo.setSelected(true);
                    viewTwo.setTextColor(white);
                    viewOne.setTextColor(dark_blue);
                }
            }
        });
    }

    public static AlertDialog createCustomLoanObjectDialogBox(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.add_loan_dialog_layout, null);

        builder.setCustomTitle(getCustomTitleDialogBox(context, R.string.add_element, R.drawable.add));

        ((TextView) view.findViewById(R.id.neutralTextView)).setText(R.string.validate);

        builder.setView(view);
        return builder.create();
    }

    public static AlertDialog createCustomPresentDialogBox(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.add_present_dialog_layout, null);

        builder.setCustomTitle(getCustomTitleDialogBox(context, R.string.add_present, R.drawable.add));

        ((TextView) view.findViewById(R.id.neutralTextView)).setText(R.string.validate);

        builder.setView(view);
        return builder.create();
    }

    public static AlertDialog createCustomParticipantDialogBox(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.add_participant_dialog_layout, null);

        builder.setCustomTitle(getCustomTitleDialogBox(context, R.string.add_participant, R.drawable.add));

        ((TextView) view.findViewById(R.id.neutralTextView)).setText(R.string.validate);

        builder.setView(view);
        return builder.create();
    }

    public static AlertDialog createCustomUpdatePaymentDialogBox(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.change_payment_present_dialog_layout, null);

        builder.setCustomTitle(getCustomTitleDialogBox(context, R.string.update_payment, R.drawable.edit));

        builder.setView(view);
        return builder.create();
    }
}
