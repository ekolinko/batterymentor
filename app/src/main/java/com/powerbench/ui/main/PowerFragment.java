package com.powerbench.ui.main;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.powerbench.R;
import com.powerbench.collectionmanager.CollectionManager;
import com.powerbench.collectionmanager.LifetimeCollectionTask;
import com.powerbench.constants.SettingsConstants;
import com.powerbench.datamanager.Histogram;
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
    private boolean mShowLifetimeHistogram = false;

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
     * The histogram view associated with this fragment.
     */
    private HistogramView mHistogramView;

    /**
     * The current theme.
     */
    private Theme mTheme;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_realtime_battery, container, false);
        mCollectionTask = CollectionManager.getInstance().getPowerCollectionTask(getContext());
        mTitleView = (TextView) view.findViewById(R.id.powerbench_power_title);
        mHintView = (TextView) view.findViewById(R.id.powerbench_power_hint);
        mPowerView = (TextView) view.findViewById(R.id.powerbench_power_value);
        mHistogramView = (HistogramView) view.findViewById(R.id.powerbench_power_histogram);
        RelativeLayout powerContainer = (RelativeLayout) view.findViewById(R.id.powerbench_power_container);
        if (powerContainer != null) {
            powerContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mShowLifetimeHistogram = !mShowLifetimeHistogram;
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
    public void updatePowerViews() {
        if (mPowerView != null && mHistogram != null) {
            if (mPowerFormatter == null)
                mPowerFormatter = new DecimalFormat(getString(R.string.format_power));

            if (mHistogram != null) {
                double powerValue = mHistogram.getAverage();

                String value;
                if (Settings.getInstance().getPowerTabUnits(getContext()) == SettingsConstants.UNITS_MILLIAMP) {
                    value = String.format(getString(R.string.value_units_template), mPowerFormatter.format(powerValue / Sensor.VOLTAGE.measure()), getString(R.string.milliamps));
                } else {
                    value = String.format(getString(R.string.value_units_template), mPowerFormatter.format(powerValue), getString(R.string.milliwatts));
                }

                mPowerView.setText(value);
                mHistogramView.setHistogram(mHistogram);
                mHistogramView.postInvalidate();
            } else {
                mPowerView.setText(getString(R.string.invalid_value));
            }
        }
    }

    /**
     * Refresh all the views of this fragment.
     */
    @Override
    public void refresh() {
        if (mCollectionTask != null) {
            if (isChargerConnected()) {
                if (mShowLifetimeHistogram) {
                    mHistogram = mCollectionTask.getLifetimeStatistics();
                    if (mTitleView != null)
                        mTitleView.setText(R.string.power_title_lifetime_charger_speed);
                    if (mHintView != null)
                        mHintView.setText(R.string.power_hint_lifetime_charger_speed);
                } else {
                    mHistogram = mCollectionTask.getRealtimeStatistics();
                    if (mTitleView != null)
                        mTitleView.setText(R.string.power_title_realtime_charger_speed);
                    if (mHintView != null)
                        mHintView.setText(R.string.power_hint_realtime_charger_speed);
                }
            } else {
                if (mShowLifetimeHistogram) {
                    mHistogram = mCollectionTask.getLifetimeStatistics();
                    if (mTitleView != null)
                        mTitleView.setText(R.string.power_title_lifetime_battery_usage);
                    if (mHintView != null)
                        mHintView.setText(R.string.power_hint_lifetime_battery_usage);
                } else {
                    mHistogram = mCollectionTask.getRealtimeStatistics();
                    if (mTitleView != null)
                        mTitleView.setText(R.string.power_title_realtime_battery_usage);
                    if (mHintView != null)
                        mHintView.setText(R.string.power_hint_realtime_battery_usage);
                }
            }
        }
        updatePowerViews();
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
    }
}