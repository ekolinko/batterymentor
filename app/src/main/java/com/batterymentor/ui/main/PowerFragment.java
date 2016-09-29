package com.batterymentor.ui.main;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.batterymentor.R;
import com.batterymentor.collectionmanager.CollectionManager;
import com.batterymentor.collectionmanager.LifetimeCollectionTask;
import com.batterymentor.constants.UIConstants;
import com.batterymentor.datamanager.Histogram;
import com.batterymentor.datamanager.RealtimeStatistics;
import com.batterymentor.datamanager.Statistics;
import com.batterymentor.device.Device;
import com.batterymentor.sensors.Sensor;
import com.batterymentor.settings.Settings;
import com.batterymentor.ui.common.CommonFragment;
import com.batterymentor.ui.common.HistogramView;
import com.batterymentor.ui.theme.Theme;
import com.batterymentor.ui.tips.BatteryTipsActivity;

import java.text.DecimalFormat;

/**
 * Fragment for showing the realtime battery power measurements.
 */
public class PowerFragment extends CommonFragment {

    /**
     * Flag indicating that the fragment has been created.
     */
    private boolean mCreated = false;

    /**
     * The collection task associated with this fragment.
     */
    private LifetimeCollectionTask mCollectionTask;

    /**
     * The power formatter.
     */
    private DecimalFormat mPowerFormatter;

    /**
     * The flag that indicates that the lifetime histogram should be shown. Otherwise, the
     * instantaneous histogram will be shown.
     */
    private boolean mShowLifetimeData = false;

    /**
     * The histogram that contains the data that is shown on this screen.
     */
    private Histogram mHistogram;

    /**
     * The title view associated with this fragment.
     */
    private TextView mTitleView;

    /**
     * The hint view associated with this fragment.
     */
    private TextView mHintView;

    /**
     * The power view associated with this fragment.
     */
    private TextView mPowerView;

    /**
     * The power estimation indicator
     */
    private TextView mPowerEstimationIndicator;

    /**
     * The power LED associated with this fragment.
     */
    private ImageView mPowerLed;

    /**
     * The histogram view associated with this fragment.
     */
    private HistogramView mHistogramView;

    /**
     * The value label view associated with this fragment.
     */
    private TextView mValueLabel;

    /**
     * The minimum value view associated with this fragment.
     */
    private TextView mMinValue;

    /**
     * The maximum value view associated with this fragment.
     */
    private TextView mMaxValue;

    /**
     * The button used to reset the statistics.
     */
    private ImageView mButtonResetStatistics;

    /**
     * The current theme.
     */
    private Theme mTheme;

    /**
     * The time at which the next update should come.
     */
    private long mNextUpdateTime;

    /**
     * The dialog that shows that estimated power is not supported while the device is charging.
     */
    private Dialog mEstimatedPowerNotSupportedDialog;

    /**
     * Flag indicating whether the estimated power not supported dialog is dismissed.
     */
    private boolean mEstimatedPowerNotSupportedDialogDismissed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_power, container, false);
        mCreated = true;
        mCollectionTask = CollectionManager.getInstance().getPowerCollectionTask(getContext());
        mTitleView = (TextView) view.findViewById(R.id.powerbench_power_title);
        mHintView = (TextView) view.findViewById(R.id.powerbench_power_hint);
        mPowerView = (TextView) view.findViewById(R.id.powerbench_power_value);
        mPowerLed = (ImageView) view.findViewById(R.id.powerbench_power_led);
        mPowerEstimationIndicator = (TextView) view.findViewById(R.id.powerbench_power_estimation_indicator);
        mHistogramView = (HistogramView) view.findViewById(R.id.powerbench_power_histogram);
        mValueLabel = (TextView) view.findViewById(R.id.powerbench_power_value_label);
        mMinValue = (TextView) view.findViewById(R.id.powerbench_power_min);
        mMaxValue = (TextView) view.findViewById(R.id.powerbench_power_max);
        mButtonResetStatistics = (ImageView) view.findViewById(R.id.battery_mentor_button_reset);
        mButtonResetStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHistogram != null) {
                    if (mShowLifetimeData) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), mTheme.getDialogStyleResource());
                        builder.setTitle(R.string.dialog_reset_lifetime_statistics_title).
                                setMessage(R.string.dialog_reset_lifetime_statistics_message)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Statistics lifetimeStatistics = mCollectionTask.getLifetimeStatistics();
                                        lifetimeStatistics.reset();
                                        updatePowerViews(true);
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null);
                        builder.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), mTheme.getDialogStyleResource());
                        builder.setTitle(R.string.dialog_reset_realtime_statistics_title).
                                setMessage(R.string.dialog_reset_realtime_statistics_message)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        RealtimeStatistics realtimeStatistics = mCollectionTask.getRealtimeStatistics();
                                        realtimeStatistics.reset();
                                        updatePowerViews(true);
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null);
                        builder.show();
                    }
                }
            }
        });
        if (mHistogramView != null) {
            mHistogramView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mShowLifetimeData = !mShowLifetimeData;
                    refresh();
                }
            });
        }
        if (mTheme != null) {
            applyTheme(mTheme);
        }
        refresh();
        setRetainInstance(true);
        return view;
    }

    /**
     * Update the power value using the specified value
     */
    public void updatePowerViews(boolean forceRefresh) {
        if (needsUpdate() || forceRefresh) {
            if (mHistogram != null) {
                double voltage = Sensor.VOLTAGE.measure();
                if (mPowerView != null) {
                    if (mPowerFormatter == null)
                        mPowerFormatter = new DecimalFormat(getString(R.string.format_power));

                    double powerValue = mHistogram.getAverage();
                    String value;
                    if (powerValue <= 0 && isChargerConnected()) {
                        value = getString(R.string.not_charging);
                    } else {
                        if (Settings.getInstance().getPowerTabUnits(getContext()).equals(getString(R.string.milliamps))) {
                            value = String.format(getString(R.string.value_units_template), mPowerFormatter.format(powerValue / voltage), getString(R.string.milliamps));
                        } else {
                            value = String.format(getString(R.string.value_units_template), mPowerFormatter.format(powerValue), getString(R.string.milliwatts));
                        }
                    }

                    if (mPowerLed != null && mPowerLed.getVisibility() == View.VISIBLE) {
                        double lifetimeAverage = mCollectionTask.getLifetimeStatistics().getAverage();
                        double fraction = powerValue / lifetimeAverage;
                        if (fraction >= UIConstants.LED_RED_THRESHOLD) {
                            mPowerLed.setImageResource(R.drawable.led_red);
                        } else if (fraction >= UIConstants.LED_YELLOW_THRESHOLD) {
                            mPowerLed.setImageResource(R.drawable.led_yellow);
                        } else {
                            mPowerLed.setImageResource(R.drawable.led_green);
                        }
                    }

                    if (mPowerEstimationIndicator != null) {
                        mPowerEstimationIndicator.setVisibility(Device.getInstance().isBatteryPowerEstimated() ? View.VISIBLE : View.GONE);
                    }

                    mPowerView.setText(value);
                    mHistogramView.setHistogram(mHistogram);
                    mHistogramView.postInvalidate();
                }

                if (mMinValue != null && mMaxValue != null) {
                    double minValue = mHistogram.getMin();
                    double maxValue = mHistogram.getMax();
                    if (isChargerConnected()) {
                        double temp = -maxValue + 0.0;
                        maxValue = -minValue + 0.0;
                        minValue = temp;
                    }
                    if (Settings.getInstance().getPowerTabUnits(getContext()).equals(getString(R.string.milliamps))) {
                        String min = Double.isInfinite(minValue) ? getString(R.string.invalid_value) : mPowerFormatter.format(minValue / voltage);
                        String max = Double.isInfinite(maxValue) ? getString(R.string.invalid_value) : mPowerFormatter.format(maxValue / voltage);
                        mMinValue.setText(String.format(getString(R.string.current_template), min));
                        mMaxValue.setText(String.format(getString(R.string.current_template), max));
                    } else {
                        String min = Double.isInfinite(minValue) ? getString(R.string.invalid_value) : mPowerFormatter.format(minValue);
                        String max = Double.isInfinite(maxValue) ? getString(R.string.invalid_value) : mPowerFormatter.format(maxValue);
                        mMinValue.setText(String.format(getString(R.string.power_template), min));
                        mMaxValue.setText(String.format(getString(R.string.power_template), max));
                    }
                }
            } else if (mPowerView != null) {
                mPowerView.setText(getString(R.string.invalid_value));
            }
        }
    }

    /**
     * Refresh all the views of this fragment.
     */
    public void refresh() {
        if (mCreated) {
            if (isChargerConnected()) {
                if (mShowLifetimeData) {
                    mHistogram = mCollectionTask.getLifetimeStatistics();
                    if (mTitleView != null)
                        mTitleView.setText(R.string.power_title_lifetime_charger_speed);
                    if (mHintView != null)
                        mHintView.setText(R.string.power_hint_lifetime_charger_speed);
                    if (mValueLabel != null)
                        mValueLabel.setText(R.string.lifetime);
                } else {
                    mHistogram = mCollectionTask.getRealtimeStatistics();
                    if (mTitleView != null)
                        mTitleView.setText(R.string.power_title_realtime_charger_speed);
                    if (mHintView != null)
                        mHintView.setText(R.string.power_hint_realtime_charger_speed);
                    if (mValueLabel != null)
                        mValueLabel.setText(R.string.realtime);
                }
                if (mPowerLed != null)
                    mPowerLed.setVisibility(View.GONE);
            } else {
                if (mShowLifetimeData) {
                    mHistogram = mCollectionTask.getLifetimeStatistics();
                    if (mTitleView != null)
                        mTitleView.setText(R.string.power_title_lifetime_battery_usage);
                    if (mHintView != null)
                        mHintView.setText(R.string.power_hint_lifetime_battery_usage);
                    if (mPowerLed != null)
                        mPowerLed.setVisibility(View.GONE);
                    if (mValueLabel != null)
                        mValueLabel.setText(R.string.lifetime);
                } else {
                    mHistogram = mCollectionTask.getRealtimeStatistics();
                    if (mTitleView != null)
                        mTitleView.setText(R.string.power_title_realtime_battery_usage);
                    if (mHintView != null)
                        mHintView.setText(R.string.power_hint_realtime_battery_usage);
                    if (mPowerLed != null)
                        mPowerLed.setVisibility(View.VISIBLE);
                    if (mValueLabel != null)
                        mValueLabel.setText(R.string.realtime);
                }
            }
        }
        updatePowerViews(true);
    }

    /**
     * Apply the specified theme to this fragment.
     *
     * @param theme the theme to apply to this fragment.
     */
    @Override
    public void applyTheme(Theme theme) {
        mTheme = theme;
        if (mPowerView != null) {
            mPowerView.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
        if (mTitleView != null) {
            mTitleView.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
        if (mHistogramView != null) {
            mHistogramView.applyTheme(theme);
            mHistogramView.postInvalidate();
        }
        if (mMinValue != null) {
            mMinValue.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
        if (mMaxValue != null) {
            mMaxValue.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
    }

    /**
     * Check the timestamp to see if the model needs an update.
     *
     * @return true if the model needs updated, false otherwise.
     */
    public boolean needsUpdate() {
        if (!mShowLifetimeData)
            return true;

        long currentTime = System.currentTimeMillis();
        if (currentTime > mNextUpdateTime) {
            mNextUpdateTime = currentTime + UIConstants.LIFETIME_UPDATE_INTERVAL;
            return true;
        }
        return false;
    }

    /**
     * Notify the fragment of a connected charger.
     */
    public void onChargerConnected() {
        super.onChargerConnected();
        if (mCreated && Device.getInstance().isBatteryPowerEstimated() && !mEstimatedPowerNotSupportedDialogDismissed) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), mTheme.getDialogStyleResource()).setTitle(getString(R.string.battery_details))
                    .setTitle(R.string.charger_readings_not_supported_with_estimated_power_title)
                    .setMessage(R.string.charger_readings_not_supported_with_estimated_power_summary)
                    .setPositiveButton(R.string.charger_tips, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(new Intent(getActivity(), BatteryTipsActivity.class), UIConstants.TAB_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton(R.string.close, null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                        }
                    });
            mEstimatedPowerNotSupportedDialog = builder.create();
            mEstimatedPowerNotSupportedDialog.show();
            mEstimatedPowerNotSupportedDialogDismissed = true;
        }
    }

    /**
     * Notify the fragment of a disconnected charger.
     */
    public void onChargerDisconnected() {
        super.onChargerDisconnected();
        if (mEstimatedPowerNotSupportedDialog != null && mEstimatedPowerNotSupportedDialog.isShowing()) {
            mEstimatedPowerNotSupportedDialog.dismiss();
            mEstimatedPowerNotSupportedDialog = null;
        }
        mEstimatedPowerNotSupportedDialogDismissed = false;
    }
}