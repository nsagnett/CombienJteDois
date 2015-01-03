package nsapp.com.combienjtedois.services;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.widget.Toast;

import nsapp.com.combienjtedois.R;

public class SendSmsTask extends AsyncTask<String, Void, Void> {

    private Context context;
    private boolean failure;

    public SendSmsTask(Context context) {
        this.context = context;
        failure = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context, context.getString(R.string.impossible_send), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(String... params) {
        SmsManager manager = SmsManager.getDefault();
        if (params[0] != null && params[1] != null) {
            manager.sendTextMessage(params[1], null, params[0], null, null);
        } else {
            Toast.makeText(context, context.getString(R.string.impossible_send), Toast.LENGTH_SHORT).show();
            failure = true;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (failure) {
            Toast.makeText(context, context.getString(R.string.impossible_send), Toast.LENGTH_SHORT).show();
        }
    }
}
