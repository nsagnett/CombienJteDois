package nsapp.com.combienjtedois.views.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Preferences;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;

public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private LaunchActivity launchActivity;

    private SharedPreferences prefs;

    private CheckBox confirmDismissCheckbox;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.preferences_layout, container, false);
        launchActivity = (LaunchActivity) getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(launchActivity);

        confirmDismissCheckbox = (CheckBox) view.findViewById(R.id.confirmDismissCheckbox);
        confirmDismissCheckbox.setChecked(prefs.getBoolean(Preferences.CONFIRM_DISMISS_KEY, true));
        confirmDismissCheckbox.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = launchActivity.getSupportActionBar();
        if (actionBar != null) {
            launchActivity.updateActionBarTitle(getString(R.string.settings));
            launchActivity.supportInvalidateOptionsMenu();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.confirmDismissCheckbox:
                prefs.edit().putBoolean(Preferences.CONFIRM_DISMISS_KEY, isChecked).apply();
                break;
        }
    }
}