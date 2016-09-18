package com.batterymentor.ui.settings;

import android.os.Bundle;

import com.batterymentor.R;
import com.batterymentor.ui.common.CommonActivity;

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
