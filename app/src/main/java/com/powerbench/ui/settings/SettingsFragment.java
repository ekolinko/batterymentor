package com.powerbench.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Contacts;
import android.support.v4.app.Fragment;

import com.powerbench.R;
import com.powerbench.constants.Constants;
import com.powerbench.constants.UIConstants;
import com.powerbench.model.Model;
import com.powerbench.model.ModelManager;
import com.powerbench.settings.Settings;
import com.powerbench.ui.benchmark.ScreenTestActivity;

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
        final Preference rerunScreenTestPreference = (Preference) findPreference(getString(R.string.settings_rerun_screen_test));
        Model screenModel = ModelManager.getInstance().getBatteryModel(getActivity()).getScreenModel();
        if (screenModel != null) {
            if (rerunScreenTestPreference != null) {
                rerunScreenTestPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent data = new Intent();
                        data.setData(Uri.parse(UIConstants.SCREEN_TAB));
                        getActivity().setResult(getActivity().RESULT_OK, data);
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), ScreenTestActivity.class));
                        return false;
                    }
                });
            }
        } else {
            preferenceScreen.removePreference(rerunScreenTestPreference);
        }
    }
}
