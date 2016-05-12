package com.powerbench.ui.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBarActivity;

import com.powerbench.R;
import com.powerbench.ui.common.CommonActivity;

/**
 * Activity that allows user to change persistent settings.
 */
public class SettingsActivity extends CommonActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initialize();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
    }
}
