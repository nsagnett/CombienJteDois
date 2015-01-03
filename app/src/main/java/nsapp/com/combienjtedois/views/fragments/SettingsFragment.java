package nsapp.com.combienjtedois.views.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.utils.Preferences;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;

public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private LaunchActivity launchActivity;

    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.preferences_layout, container, false);
        launchActivity = (LaunchActivity) getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(launchActivity);

        CheckBox confirmDismissCheckbox = (CheckBox) view.findViewById(R.id.confirmDismissCheckbox);
        confirmDismissCheckbox.setChecked(prefs.getBoolean(Preferences.CONFIRM_DISMISS, true));
        confirmDismissCheckbox.setOnCheckedChangeListener(this);

        CheckBox moneyAlertCheckbox = (CheckBox) view.findViewById(R.id.moneyAlertCheckbox);
        moneyAlertCheckbox.setChecked(prefs.getBoolean(Preferences.ENABLED_NOTIFICATION_MONEY, false));
        moneyAlertCheckbox.setOnCheckedChangeListener(this);

        CheckBox loanAlertCheckbox = (CheckBox) view.findViewById(R.id.loanAlertCheckbox);
        loanAlertCheckbox.setChecked(prefs.getBoolean(Preferences.ENABLED_NOTIFICATION_LOAN, false));
        loanAlertCheckbox.setOnCheckedChangeListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        launchActivity.updateActionBarTitle(getString(R.string.settings));
        launchActivity.supportInvalidateOptionsMenu();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.confirmDismissCheckbox:
                prefs.edit().putBoolean(Preferences.CONFIRM_DISMISS, isChecked).apply();
                break;
            case R.id.moneyAlertCheckbox:
                prefs.edit().putBoolean(Preferences.ENABLED_NOTIFICATION_MONEY, isChecked).apply();
                break;
            case R.id.loanAlertCheckbox:
                prefs.edit().putBoolean(Preferences.ENABLED_NOTIFICATION_LOAN, isChecked).apply();
                break;
        }
    }
}