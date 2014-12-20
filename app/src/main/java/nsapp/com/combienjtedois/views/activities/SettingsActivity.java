package nsapp.com.combienjtedois.views.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import nsapp.com.combienjtedois.R;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
