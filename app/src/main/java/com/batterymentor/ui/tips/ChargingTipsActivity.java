package com.batterymentor.ui.tips;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.batterymentor.R;
import com.batterymentor.constants.UIConstants;
import com.batterymentor.device.Device;
import com.batterymentor.ui.common.CommonActivity;

/**
 * The battery tips activity that shows tips for improving the battery life.
 */
public class ChargingTipsActivity extends CommonActivity {

    /**
     * The try different chargers button.
     */
    private RelativeLayout mTryDifferentChargersButton;

    /**
     * The turn off screen button.
     */
    private RelativeLayout mTurnOffScreenButton;

    /**
     * The turn on airplane mode button.
     */
    private RelativeLayout mTurnOnAirplaneModeButton;

    /**
     * The plug into outlet button.
     */
    private RelativeLayout mPlugIntoOutletButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_tips);
        initialize();
        getSupportActionBar().setTitle(R.string.charger_tips);
        setupButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Setup the buttons that take the user to various tools that can be used to improve battery
     * life.
     */
    protected void setupButtons() {
        mTryDifferentChargersButton = (RelativeLayout)findViewById(R.id.charger_tip_try_different_chargers);
        if (mTryDifferentChargersButton != null) {
            TextView summaryView = (TextView) mTryDifferentChargersButton.findViewById(R.id.battery_tip_summary);
            String summary = getResources().getString(R.string.charger_tip_title_try_different_chargers_summary);
            if (!Device.getInstance().isBatteryPowerEstimated()) {
                summary += getResources().getString(R.string.charger_tip_title_try_different_chargers_summary_extra);
                summaryView.setText(summary);
                mTryDifferentChargersButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent data = new Intent();
                        data.setData(Uri.parse(UIConstants.POWER_TAB));
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
            }
        }
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
}
