package com.powerbench.ui.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.powerbench.R;
import com.powerbench.collectionmanager.CollectionManager;
import com.powerbench.collectionmanager.LifetimeCollectionTask;
import com.powerbench.constants.UIConstants;
import com.powerbench.datamanager.Histogram;
import com.powerbench.datamanager.RealtimeStatistics;
import com.powerbench.sensors.ChargerManager;
import com.powerbench.sensors.Sensor;
import com.powerbench.settings.Settings;
import com.powerbench.ui.common.CommonFragment;
import com.powerbench.ui.common.HistogramView;
import com.powerbench.ui.theme.Theme;

import java.text.DecimalFormat;

/**
 * Fragment for showing the realtime battery power measurements.
 */
public class PowerFragment extends CommonFragment {

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
     * The hint view assocaited with this fragment.
     */
    private TextView mHintView;

    /**
     * The power view associated with this fragment.
     */
    private TextView mPowerView;

    /**
     * The power LED associated with this fragment.
     */
    private ImageView mPowerLed;

    /**
     * The histogram view associated with this fragment.
     */
    private HistogramView mHistogramView;

    /**
     * The realtime min/max container view associated with this fragment.
     */
    private LinearLayout mRealtimeMinMaxContainerView;

    /**
     * The battery level header view associated with this fragment.
     */
    private TextView mBatteryLevelHeaderView;

    /**
     * The charging status header view associated with this fragment.
     */
    private TextView mChargingStatusHeaderView;

    /**
     * The battery temperature header view associated with this fragment.
     */
    private TextView mBatteryTemperatureHeaderView;

    /**
     * The realtime min/max header view associated with this fragment.
     */
    private TextView mRealtimeMinMaxHeaderView;

    /**
     * The charging rate header view associated with this fragment.
     */
    private TextView mChargingRateHeaderView;

    /**
     * The battery level view associated with this fragment.
     */
    private TextView mBatteryLevelView;

    /**
     * The charging status view associated with this fragment.
     */
    private TextView mChargingStatusView;

    /**
     * The battery temperature view associated with this fragment.
     */
    private TextView mBatteryTemperatureView;

    /**
     * The realtime min/max view associated with this fragment.
     */
    private TextView mRealtimeMinMaxView;

    /**
     * The charging rate view associated with this fragment.
     */
    private TextView mChargingRateView;

    /**
     * The value label view associated with this fragment.
     */
    private TextView mValueLabel;

    /**
     * The minimum value view associated with this fragment.
     */
    private TextView mMinValue;

    /**
     * The minimum label view associated with this fragment.
     */
    private TextView mMinLabel;

    /**
     * The maximum value view associated with this fragment.
     */
    private TextView mMaxValue;

    /**
     * The maximum label view associated with this fragment.
     */
    private TextView mMaxLabel;

    /**
     * The current theme.
     */
    private Theme mTheme;

    /**
     * The time at which the next update should come.
     */
    private long mNextUpdateTime;

    /**
     * The power details container divider.
     */
    private View mDivider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_power, container, false);
        mCollectionTask = CollectionManager.getInstance().getPowerCollectionTask(getContext());
        mTitleView = (TextView) view.findViewById(R.id.powerbench_power_title);
        mHintView = (TextView) view.findViewById(R.id.powerbench_power_hint);
        mPowerView = (TextView) view.findViewById(R.id.powerbench_power_value);
        mPowerLed = (ImageView) view.findViewById(R.id.powerbench_power_led);
        mHistogramView = (HistogramView) view.findViewById(R.id.powerbench_power_histogram);
        mRealtimeMinMaxContainerView = (LinearLayout) view.findViewById(R.id.powerbench_power_battery_min_max_container);
        mBatteryLevelHeaderView = (TextView) view.findViewById(R.id.powerbench_power_battery_level_header);
        mChargingStatusHeaderView = (TextView) view.findViewById(R.id.powerbench_power_charging_status_header);
        mBatteryTemperatureHeaderView = (TextView) view.findViewById(R.id.powerbench_power_battery_temperature_header);
        mRealtimeMinMaxHeaderView = (TextView) view.findViewById(R.id.powerbench_power_battery_min_max_header);
        mChargingRateHeaderView = (TextView) view.findViewById(R.id.powerbench_power_charging_rate_header);
        mBatteryLevelView = (TextView) view.findViewById(R.id.powerbench_power_battery_level);
        mChargingStatusView = (TextView) view.findViewById(R.id.powerbench_power_charging_status);
        mBatteryTemperatureView = (TextView) view.findViewById(R.id.powerbench_power_battery_temperature);
        mRealtimeMinMaxView = (TextView) view.findViewById(R.id.powerbench_power_battery_min_max);
        mChargingRateView = (TextView) view.findViewById(R.id.powerbench_power_charging_rate);
        mValueLabel = (TextView) view.findViewById(R.id.powerbench_power_value_label);
        mMinValue = (TextView) view.findViewById(R.id.powerbench_power_min);
        mMinLabel = (TextView) view.findViewById(R.id.powerbench_power_min_label);
        mMaxValue = (TextView) view.findViewById(R.id.powerbench_power_max);
        mMaxLabel = (TextView) view.findViewById(R.id.powerbench_power_max_label);
        mDivider = view.findViewById(R.id.divider_power_details_container);
        RelativeLayout powerContainer = (RelativeLayout) view.findViewById(R.id.powerbench_power_container);
        if (powerContainer != null) {
            powerContainer.setOnClickListener(new View.OnClickListener() {
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
            if (mPowerView != null && mHistogram != null) {
                if (mPowerFormatter == null)
                    mPowerFormatter = new DecimalFormat(getString(R.string.format_power));

                if (mHistogram != null) {
                    double powerValue = mHistogram.getAverage();

                    String value;
                    if (powerValue <= 0 && isChargerConnected()) {
                        value = getString(R.string.not_charging);
                    } else {
                        if (Settings.getInstance().getPowerTabUnits(getContext()).equals(getString(R.string.milliamps))) {
                            value = String.format(getString(R.string.value_units_template), mPowerFormatter.format(powerValue / Sensor.VOLTAGE.measure()), getString(R.string.milliamps));
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

                    mPowerView.setText(value);
                    mHistogramView.setHistogram(mHistogram);
                    mHistogramView.postInvalidate();
                } else {
                    mPowerView.setText(getString(R.string.invalid_value));
                }
            }

            if (mCollectionTask != null) {
                if (mRealtimeMinMaxView != null) {
                    double minValue = mCollectionTask.getRealtimeStatistics().getMin();
                    double maxValue = mCollectionTask.getRealtimeStatistics().getMax();
                    if (isChargerConnected()) {
                        double temp = -maxValue + 0.0;
                        maxValue = -minValue + 0.0;
                        minValue = temp;
                    }

                    String min = Double.isInfinite(minValue) ? getString(R.string.invalid_value) : mPowerFormatter.format(minValue);
                    String max = Double.isInfinite(minValue) ? getString(R.string.invalid_value) : mPowerFormatter.format(maxValue);
                    mRealtimeMinMaxView.setText(String.format(getString(R.string.value_min_max_template), min, max));
                }
                if (mMinValue != null && mMaxValue != null) {
                    double minValue = mCollectionTask.getRealtimeStatistics().getMin();
                    double maxValue = mCollectionTask.getRealtimeStatistics().getMax();
                    if (isChargerConnected()) {
                        double temp = -maxValue + 0.0;
                        maxValue = -minValue + 0.0;
                        minValue = temp;
                    }

                    String min = Double.isInfinite(minValue) ? getString(R.string.invalid_value) : mPowerFormatter.format(minValue);
                    String max = Double.isInfinite(minValue) ? getString(R.string.invalid_value) : mPowerFormatter.format(maxValue);
                    mMinValue.setText(String.format(getString(R.string.power_template), min));
                    mMaxValue.setText(String.format(getString(R.string.power_template), max));
                }
            }

            ChargerManager chargerManager = ChargerManager.getInstance();
            if (mBatteryLevelView != null) {
                mBatteryLevelView.setText(String.format(getString(R.string.value_percent_template), chargerManager.getBatteryLevel()));
            }
            if (mChargingStatusView != null) {
                mChargingStatusView.setText(chargerManager.getChargingStatus());
            }
            if (mBatteryTemperatureView != null) {
                mBatteryTemperatureView.setText(String.format(getString(R.string.value_celsius_template), chargerManager.getBatteryTemperature()));
            }
            if (mChargingRateHeaderView != null) {
                if (isChargerConnected()) {
                    mChargingRateHeaderView.setText(R.string.power_charging_rate);
                } else {
                    mChargingRateHeaderView.setText(R.string.power_discharging_rate);
                }
            }
            if (mChargingRateView != null) {
                mChargingRateView.setText("N/A");
            }
        }
    }

    /**
     * Refresh all the views of this fragment.
     */
    public void refresh() {
        if (mCollectionTask != null) {
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
            if (mRealtimeMinMaxContainerView != null) {
                mRealtimeMinMaxContainerView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.dialog_reset_min_max_title).
                                setMessage(R.string.dialog_reset_min_max_message)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        RealtimeStatistics realtimeStatistics = mCollectionTask.getRealtimeStatistics();
                                        realtimeStatistics.resetMin();
                                        realtimeStatistics.resetMax();
                                        updatePowerViews(true);
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null);
                        builder.show();
                        return true;
                    }
                });
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
        if (mBatteryLevelHeaderView != null) {
            mBatteryLevelHeaderView.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
        if (mBatteryLevelView != null) {
            mBatteryLevelView.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
        if (mChargingStatusHeaderView != null) {
            mChargingStatusHeaderView.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
        if (mChargingStatusView != null) {
            mChargingStatusView.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
        if (mBatteryTemperatureHeaderView != null) {
            mBatteryTemperatureHeaderView.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
        if (mBatteryTemperatureView != null) {
            mBatteryTemperatureView.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
        if (mRealtimeMinMaxHeaderView != null) {
            mRealtimeMinMaxHeaderView.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
        if (mRealtimeMinMaxView != null) {
            mRealtimeMinMaxView.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
        if (mChargingRateHeaderView != null) {
            mChargingRateHeaderView.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
        if (mChargingRateView != null) {
            mChargingRateView.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
        if (mDivider != null) {
            mDivider.setBackgroundColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
//        if (mValueLabel != null) {
//            mValueLabel.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
//        }
        if (mMinValue != null) {
            mMinValue.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
//        if (mMinLabel != null) {
//            mMinLabel.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
//        }
        if (mMaxValue != null) {
            mMaxValue.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
//        if (mMaxLabel != null) {
//            mMaxLabel.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
//        }
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
}