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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Preferences;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;

public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    private LaunchActivity launchActivity;

    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.preferences_layout, container, false);
        launchActivity = (LaunchActivity) getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(launchActivity);

        CheckBox confirmDismissCheckbox = (CheckBox) view.findViewById(R.id.confirmDismissCheckbox);
        confirmDismissCheckbox.setChecked(prefs.getBoolean(Preferences.CONFIRM_DISMISS_KEY, true));
        confirmDismissCheckbox.setOnCheckedChangeListener(this);

        CheckBox moneyAlertCheckbox = (CheckBox) view.findViewById(R.id.moneyAlertCheckbox);
        moneyAlertCheckbox.setChecked(prefs.getBoolean(Preferences.ENABLED_NOTIFICATION_MONEY_KEY, false));
        moneyAlertCheckbox.setOnCheckedChangeListener(this);

        CheckBox loanAlertCheckbox = (CheckBox) view.findViewById(R.id.loanAlertCheckbox);
        loanAlertCheckbox.setChecked(prefs.getBoolean(Preferences.ENABLED_NOTIFICATION_LOAN_KEY, false));
        loanAlertCheckbox.setOnCheckedChangeListener(this);

        CheckBox presentAlertCheckbox = (CheckBox) view.findViewById(R.id.presentAlertCheckbox);
        presentAlertCheckbox.setChecked(prefs.getBoolean(Preferences.ENABLED_NOTIFICATION_PRESENT_KEY, false));
        presentAlertCheckbox.setOnCheckedChangeListener(this);

        Spinner frequencySpinner = (Spinner) view.findViewById(R.id.notificationFrequencySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(launchActivity, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.frequencyArray));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(adapter);
        frequencySpinner.setSelection(prefs.getInt(Preferences.FREQUENCY_NOTIFICATION_KEY, 0));
        frequencySpinner.setOnItemSelectedListener(this);
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
            case R.id.moneyAlertCheckbox:
                prefs.edit().putBoolean(Preferences.ENABLED_NOTIFICATION_MONEY_KEY, isChecked).apply();
                break;
            case R.id.loanAlertCheckbox:
                prefs.edit().putBoolean(Preferences.ENABLED_NOTIFICATION_LOAN_KEY, isChecked).apply();
                break;
            case R.id.presentAlertCheckbox:
                prefs.edit().putBoolean(Preferences.ENABLED_NOTIFICATION_PRESENT_KEY, isChecked).apply();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        prefs.edit().putInt(Preferences.FREQUENCY_NOTIFICATION_KEY, position).apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Required implementation
    }
}