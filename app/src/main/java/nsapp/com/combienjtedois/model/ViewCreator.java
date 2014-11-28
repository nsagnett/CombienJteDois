package nsapp.com.combienjtedois.model;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.views.fragments.AbstractMoneyFragment;

public class ViewCreator {

    public static View getCustomTitleDialogBox(Context context, int resTitleID, int resDrawableTitleID) {
        TextView titleView = new TextView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        titleView.setLayoutParams(params);
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        titleView.setText(resTitleID);
        titleView.setTextSize(17);
        titleView.setTextColor(context.getResources().getColor(android.R.color.white));
        titleView.setBackgroundResource(R.color.dark_blue);
        titleView.setCompoundDrawablesWithIntrinsicBounds(resDrawableTitleID, 0, 0, 0);

        return titleView;
    }

    public static void showCustomAlertDialogBox(Context context, int resTitleID, int resDrawableTitleID, String resMessageID) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_layout, null);

        builder.setCustomTitle(getCustomTitleDialogBox(context, resTitleID, resDrawableTitleID));

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

    public static void switchView(final Context context, final TextView viewOne, final TextView viewTwo, final AbstractMoneyFragment abstractMoneyFragment) {
        final int green = context.getResources().getColor(R.color.dark_blue);
        final int white = context.getResources().getColor(android.R.color.white);

        viewOne.setTextColor(green);
        viewTwo.setTextColor(green);

        viewOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewOne.isSelected()) {
                    viewTwo.setSelected(false);
                    viewOne.setSelected(true);
                    viewOne.setTextColor(white);
                    viewTwo.setTextColor(green);
                    abstractMoneyFragment.setSortIndex(0);
                    abstractMoneyFragment.notifyChanges();
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
                    viewOne.setTextColor(green);
                    abstractMoneyFragment.setSortIndex(1);
                    abstractMoneyFragment.notifyChanges();
                }
            }
        });
    }

    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 50;
        int targetHeight = 50;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        canvas.drawBitmap(scaleBitmapImage,
                new Rect(0, 0, scaleBitmapImage.getWidth(),
                        scaleBitmapImage.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    public static AlertDialog createListViewDialogBox(Context context, ListView listView){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCustomTitle(getCustomTitleDialogBox(context, R.string.choose, R.drawable.question));
        builder.setView(listView);

        return builder.create();
    }
}
