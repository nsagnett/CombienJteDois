package nsapp.com.combienjtedois.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Preferences;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;

public class AutoSendNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

            sendNotification(context, preferences);
        }
    }

    private NotificationCompat.Builder createDefaultNotification(Context context) {
        Intent resultIntent = new Intent(context, LaunchActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        return new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
    }

    private void sendNotification(Context context, SharedPreferences preferences) {
        if (preferences.getBoolean(Preferences.ENABLED_NOTIFICATION_MONEY, false)) {
            NotificationCompat.Builder builder = createDefaultNotification(context);
            float positiveTotal = preferences.getFloat(Preferences.AMOUNT_CREDENCE_MONEY, 0);
            float negativeTotal = preferences.getFloat(Preferences.AMOUNT_DEBT_MONEY, 0);

            builder.setContentText(String.format(context.getString(R.string.notification_money_text_format), positiveTotal, negativeTotal));

            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(0, builder.build());
        }

        if (preferences.getBoolean(Preferences.ENABLED_NOTIFICATION_LOAN, false)) {
            NotificationCompat.Builder builder = createDefaultNotification(context);
            int lendingObjectsCount = preferences.getInt(Preferences.COUNT_LENDING_OBJECTS, 0);
            int loanObjectsCount = preferences.getInt(Preferences.COUNT_LOAN_OBJECTS, 0);

            builder.setContentText(String.format(context.getString(R.string.notification_loan_text_format), loanObjectsCount + lendingObjectsCount));

            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(1, builder.build());
        }
    }
}
