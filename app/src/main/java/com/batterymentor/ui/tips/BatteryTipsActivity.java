package com.batterymentor.ui.tips;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.RelativeLayout;

import com.batterymentor.R;
import com.batterymentor.constants.UIConstants;
import com.batterymentor.model.Model;
import com.batterymentor.model.ModelManager;
import com.batterymentor.ui.benchmark.ScreenTestActivity;
import com.batterymentor.ui.common.CommonActivity;

/**
 * The battery tips activity that shows tips for improving the battery life.
 */
public class BatteryTipsActivity extends CommonActivity {

    /**
     * The run screen test button.
     */
    private RelativeLayout mRunScreenTestButton;

    /**
     * The reduce screen brightness button.
     */
    private RelativeLayout mReduceScreenBrightnessButton;

    /**
     * The force stop unused apps button.
     */
    private RelativeLayout mForceStopUnusedApps;

    /**
     * The use wifi button.
     */
    private RelativeLayout mUseWifiButton;

    /**
     * The disable location services button.
     */
    private RelativeLayout mDisableLocationServicesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_tips);
        initialize();
        getSupportActionBar().setTitle(R.string.battery_tips);
        setupButtons();
        update();
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    /**
     * Setup the buttons that take the user to various tools that can be used to improve battery
     * life.
     */
    protected void setupButtons() {
        mRunScreenTestButton = (RelativeLayout) findViewById(R.id.battery_tip_screen_test);
        if (mRunScreenTestButton != null) {
            mRunScreenTestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(BatteryTipsActivity.this, ScreenTestActivity.class));
                }
            });
        }
        mReduceScreenBrightnessButton = (RelativeLayout) findViewById(R.id.battery_tip_screen_brightness);
        if (mReduceScreenBrightnessButton != null) {
            mReduceScreenBrightnessButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent data = new Intent();
                    data.setData(Uri.parse(UIConstants.SCREEN_TAB));
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
        }
        mForceStopUnusedApps = (RelativeLayout) findViewById(R.id.battery_tip_force_stop_unused_apps);
        if (mForceStopUnusedApps != null) {
            mForceStopUnusedApps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_POWER_USAGE_SUMMARY));
                }
            });
        }
        mUseWifiButton = (RelativeLayout) findViewById(R.id.battery_tip_use_wifi);
        if (mUseWifiButton != null) {
            mUseWifiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
        }
        mDisableLocationServicesButton = (RelativeLayout) findViewById(R.id.battery_tip_disable_location_services);
        if (mDisableLocationServicesButton != null) {
            mDisableLocationServicesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
        }
    }

    /**
     * Refresh the buttons based on the current state of the app.
     */
    protected void update() {
        Model screenModel = ModelManager.getInstance().getBatteryModel(this).getScreenModel();
        if (mRunScreenTestButton != null)
            mRunScreenTestButton.setVisibility((screenModel == null) ? View.VISIBLE : View.GONE);
        if (mReduceScreenBrightnessButton != null)
            mReduceScreenBrightnessButton.setVisibility((screenModel != null) ? View.VISIBLE : View.GONE);
    }

    public void onChargerConnectedUIThread() {
        super.onChargerConnectedUIThread();
        update();
    }

    public void onChargerDisconnectedUIThread() {
        super.onChargerDisconnectedUIThread();
        update();
    }
}
