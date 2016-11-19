package com.batterymentor.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.batterymentor.R;
import com.batterymentor.constants.UIConstants;
import com.batterymentor.device.Device;
import com.batterymentor.model.Model;
import com.batterymentor.model.ModelManager;
import com.batterymentor.settings.Settings;
import com.batterymentor.ui.benchmark.ScreenTestActivity;

/**
 * Fragment that allows user to change persistent settings.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        final PreferenceScreen preferenceScreen = (PreferenceScreen)findPreference(getString(R.string.settings_screen));
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
        final ListPreference rerunScreenTestPreference = (ListPreference) findPreference(getString(R.string.settings_rerun_screen_test));
        if (!Device.getInstance().isBatteryPowerEstimated()) {
            if (rerunScreenTestPreference != null) {
                if (ModelManager.getInstance().getBatteryModel(getActivity()).getScreenModel() == null)
                    rerunScreenTestPreference.setTitle(R.string.test_screen_run);
                rerunScreenTestPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Intent data = new Intent();
                        data.setData(Uri.parse(UIConstants.SCREEN_TAB));
                        getActivity().setResult(getActivity().RESULT_OK, data);
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), ScreenTestActivity.class);
                        intent.putExtra(UIConstants.BRIGHTNESS_DURATION_STEP, Long.parseLong((String)newValue));
                        startActivity(intent);
                        return false;
                    }
                });
            }
        } else {
            preferenceScreen.removePreference(rerunScreenTestPreference);
        }
    }
}
