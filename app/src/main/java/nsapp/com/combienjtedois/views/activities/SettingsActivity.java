package nsapp.com.combienjtedois.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import nsapp.com.combienjtedois.R;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        finish();
    }
}
