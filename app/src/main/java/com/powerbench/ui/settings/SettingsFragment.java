package com.powerbench.ui.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Contacts;
import android.support.v4.app.Fragment;

import com.powerbench.R;
import com.powerbench.settings.Settings;

/**
 * Fragment that allows user to change persistent settings.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        final ListPreference statusBarUnitsPreference = (ListPreference)findPreference(getString(R.string.settings_status_bar_units_key));
        if (statusBarUnitsPreference != null) {
            statusBarUnitsPreference.setSummary(Settings.getInstance().getStatusBarUnits(getActivity()));
            statusBarUnitsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    ((SettingsActivity)getActivity()).getService().updateNotification();
                    statusBarUnitsPreference.setSummary((String)newValue);
                    return true;
                }
            });
        }
        final ListPreference powerTabUnitsPreference = (ListPreference)findPreference(getString(R.string.settings_power_tab_units_key));
        if (powerTabUnitsPreference != null) {
            powerTabUnitsPreference.setSummary(Settings.getInstance().getPowerTabUnits(getActivity()));
            powerTabUnitsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    powerTabUnitsPreference.setSummary((String)newValue);
                    return true;
                }
            });
        }
    }
}
