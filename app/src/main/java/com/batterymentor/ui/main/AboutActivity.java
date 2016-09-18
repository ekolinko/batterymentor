package com.batterymentor.ui.main;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.batterymentor.R;
import com.batterymentor.constants.Constants;
import com.batterymentor.device.Device;
import com.batterymentor.ui.common.CommonActivity;

/**
 * The about activity that shows information about this app.
 */
public class AboutActivity extends CommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initialize();
        TextView versionView = (TextView)findViewById(R.id.about_version);
        versionView.setText(getString(R.string.version) + Constants.SPACE + Device.getInstance().getAppVersion(this));
    }
}
