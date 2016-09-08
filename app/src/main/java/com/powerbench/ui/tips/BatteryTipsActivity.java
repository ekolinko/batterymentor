package com.powerbench.ui.tips;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.RelativeLayout;

import com.powerbench.R;
import com.powerbench.constants.Constants;
import com.powerbench.constants.UIConstants;
import com.powerbench.model.Model;
import com.powerbench.model.ModelManager;
import com.powerbench.ui.benchmark.ScreenTestActivity;
import com.powerbench.ui.common.CommonActivity;
import com.powerbench.ui.theme.Theme;

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

    /**
     * The disable location services button.
     */
    private RelativeLayout mTryDifferentChargersButton;

    /**
     * The disable location services button.
     */
    private RelativeLayout mTurnOffScreenButton;

    /**
     * The disable location services button.
     */
    private RelativeLayout mTurnOnAirplaneModeButton;

    /**
     * The disable location services button.
     */
    private RelativeLayout mPlugIntoOutletButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_tips);
        initialize();
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
        mRunScreenTestButton = (RelativeLayout)findViewById(R.id.battery_tip_screen_test);
        if (mRunScreenTestButton != null) {
            mRunScreenTestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(BatteryTipsActivity.this, ScreenTestActivity.class));
                }
            });
        }
        mReduceScreenBrightnessButton = (RelativeLayout)findViewById(R.id.battery_tip_screen_brightness);
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
        mForceStopUnusedApps = (RelativeLayout)findViewById(R.id.battery_tip_force_stop_unused_apps);
        if (mForceStopUnusedApps != null) {
            mForceStopUnusedApps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_POWER_USAGE_SUMMARY));
                }
            });
        }
        mUseWifiButton = (RelativeLayout)findViewById(R.id.battery_tip_use_wifi);
        if (mUseWifiButton != null) {
            mUseWifiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
        }
        mDisableLocationServicesButton = (RelativeLayout)findViewById(R.id.battery_tip_disable_location_services);
        if (mDisableLocationServicesButton != null) {
            mDisableLocationServicesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
        }
        mTryDifferentChargersButton = (RelativeLayout)findViewById(R.id.charger_tip_try_different_chargers);
        mTryDifferentChargersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.setData(Uri.parse(UIConstants.POWER_TAB));
                setResult(RESULT_OK, data);
                finish();
            }
        });
        mTurnOffScreenButton = (RelativeLayout)findViewById(R.id.charger_tip_turn_off_screen);
        mTurnOnAirplaneModeButton = (RelativeLayout)findViewById(R.id.charger_tip_turn_on_airplane_mode);
        if (mTurnOnAirplaneModeButton != null) {
            mTurnOnAirplaneModeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));
                }
            });
        }
        mPlugIntoOutletButton = (RelativeLayout)findViewById(R.id.charger_tip_plug_into_an_outlet);
    }

    /**
     * Refresh the buttons based on the current state of the app.
     */
    protected void update() {
        if (isChargerConnected()) {
            if (mRunScreenTestButton != null)
                mRunScreenTestButton.setVisibility(View.GONE);
            if (mReduceScreenBrightnessButton != null)
                mReduceScreenBrightnessButton.setVisibility(View.GONE);
            if (mForceStopUnusedApps != null)
                mForceStopUnusedApps.setVisibility(View.GONE);
            if (mUseWifiButton != null)
                mUseWifiButton.setVisibility(View.GONE);
            if (mDisableLocationServicesButton != null)
                mDisableLocationServicesButton.setVisibility(View.GONE);
            if (mTryDifferentChargersButton != null)
                mTryDifferentChargersButton.setVisibility(View.VISIBLE);
            if (mTurnOffScreenButton != null)
                mTurnOffScreenButton.setVisibility(View.VISIBLE);
            if (mTurnOnAirplaneModeButton != null)
                mTurnOnAirplaneModeButton.setVisibility(View.VISIBLE);
            if (mPlugIntoOutletButton != null)
                mPlugIntoOutletButton.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(R.string.charger_tips);
        } else {
            Model screenModel = ModelManager.getInstance().getBatteryModel(this).getScreenModel();
            if (mRunScreenTestButton != null)
                mRunScreenTestButton.setVisibility((screenModel == null) ? View.VISIBLE : View.GONE);
            if (mReduceScreenBrightnessButton != null)
                mReduceScreenBrightnessButton.setVisibility((screenModel != null) ? View.VISIBLE : View.GONE);
            if (mForceStopUnusedApps != null)
                mForceStopUnusedApps.setVisibility(View.VISIBLE);
            if (mUseWifiButton != null)
                mUseWifiButton.setVisibility(View.VISIBLE);
            if (mDisableLocationServicesButton != null)
                mDisableLocationServicesButton.setVisibility(View.VISIBLE);
            if (mTryDifferentChargersButton != null)
                mTryDifferentChargersButton.setVisibility(View.GONE);
            if (mTurnOffScreenButton != null)
                mTurnOffScreenButton.setVisibility(View.GONE);
            if (mTurnOnAirplaneModeButton != null)
                mTurnOnAirplaneModeButton.setVisibility(View.GONE);
            if (mPlugIntoOutletButton != null)
                mPlugIntoOutletButton.setVisibility(View.GONE);
            getSupportActionBar().setTitle(R.string.battery_tips);
        }
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
