package nsapp.com.combienjtedois.model;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;

import static android.view.ViewGroup.LayoutParams;

public class Tools {

    public static DBManager dbManager = null;

    public static final int ANIMATION_DURATION = 400;

    public static String camelCase(String s) {
        String result = "";
        s = s.toLowerCase();
        int length = s.length();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                result += String.valueOf(s.charAt(i)).toUpperCase();
            } else {
                result += String.valueOf(s.charAt(i));
            }
        }
        return result;
    }

    public static ArrayList<Person> croissantSort(ArrayList<Person> persons) {
        ArrayList<Person> personsList = new ArrayList<Person>();
        do{
            double min = Integer.MAX_VALUE;
            int index = 0;
            for (int i = 0; i < persons.size(); i++) {
                double amount = Double.parseDouble(persons.get(i).getTotalAmount());
                if(amount < min){
                    min = amount;
                    index = i;
                }
            }
            personsList.add(persons.get(index));
            persons.remove(index);
        }while (!persons.isEmpty());

        return personsList;
    }

    public static ArrayList<Person> decroissantSort(ArrayList<Person> persons) {
        ArrayList<Person> personsList = new ArrayList<Person>();
        do{
            double max = Integer.MIN_VALUE;
            int index = 0;
            for (int i = 0; i < persons.size(); i++) {
                double amount = Double.parseDouble(persons.get(i).getTotalAmount());
                if(amount > max){
                    max = amount;
                    index = i;
                }
            }
            personsList.add(persons.get(index));
            persons.remove(index);
        }while (!persons.isEmpty());

        return personsList;
    }

    public static View getCustomTitleDialogBox(Context context, int resTitleID, int resDrawableTitleID) {
        TextView titleView = new TextView(context);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        titleView.setLayoutParams(params);
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        titleView.setText(resTitleID);
        titleView.setTextSize(17);
        titleView.setBackgroundResource(android.R.color.white);
        titleView.setCompoundDrawablesWithIntrinsicBounds(resDrawableTitleID, 0, 0, 0);

        return titleView;
    }

    public static void showCustomAlertDialogBox(Context context, int resTitleID, int resDrawableTitleID, String resMessageID, int resNeutralID) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_layout, null);

        builder.setCustomTitle(getCustomTitleDialogBox(context, resTitleID, resDrawableTitleID));

        TextView ok = (TextView) view.findViewById(R.id.neutralTextView);
        ok.setText(resNeutralID);

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

    public static AlertDialog createCustomConfirmDialogBox(Context context, int resTitleID, int resDrawableTitleID, int resMessageID, int resYesID, int resNoID) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.confirm_dialog_layout, null);

        builder.setCustomTitle(getCustomTitleDialogBox(context, resTitleID, resDrawableTitleID));

        ((TextView) view.findViewById(R.id.messageAlert)).setText(resMessageID);
        ((TextView) view.findViewById(R.id.positiveView)).setText(resYesID);
        ((TextView) view.findViewById(R.id.negativeView)).setText(resNoID);

        builder.setView(view);
        return builder.create();
    }

    public static AlertDialog createCustomAddPersonDialogBox(Context context, int resTitleID, int resDrawableTitleID, int resNeutralID) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.add_person_dialog_layout, null);

        builder.setCustomTitle(getCustomTitleDialogBox(context, resTitleID, resDrawableTitleID));

        ((TextView) view.findViewById(R.id.neutralTextView)).setText(resNeutralID);

        builder.setView(view);
        return builder.create();
    }

    public static AlertDialog createCustomAddDebtDialogBox(Context context, int resTitleID, int resDrawableTitleID, int resNeutralID) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.add_object_dialog_layout, null);

        builder.setCustomTitle(getCustomTitleDialogBox(context, resTitleID, resDrawableTitleID));

        ((TextView) view.findViewById(R.id.neutralTextView)).setText(resNeutralID);

        builder.setView(view);
        return builder.create();
    }
}
