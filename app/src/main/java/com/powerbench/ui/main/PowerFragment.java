package com.powerbench.ui.main;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.powerbench.R;
import com.powerbench.constants.Constants;
import com.powerbench.ui.common.CommonFragment;
import com.powerbench.ui.theme.Theme;

import java.text.DecimalFormat;

/**
 * Fragment for showing the realtime battery power measurements.
 */
public class PowerFragment extends CommonFragment {

    /**
     * The power formatter.
     */
    private DecimalFormat mPowerFormatter;

    /**
     * The battery power value associated with this fragment.
     */
    private TextView mBatteryPowerValue;

    /**
     * The power value.
     */
    private double mPowerValue = Constants.INVALID_VALUE;

    /**
     * The current theme.
     */
    private Theme mTheme;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_realtime_battery, container, false);
        mBatteryPowerValue = (TextView) view.findViewById(R.id.powerbench_power_value);
        if (mTheme != null) {
            applyTheme(mTheme);
        }
        if (mPowerValue > 0) {
            updatePowerValue(mPowerValue);
        }
        setRetainInstance(true);
        return view;
    }

    /**
     * Update the power value using the specified value
     *
     * @param powerValue the value to use to measure the arguments.
     */
    public void updatePowerValue(double powerValue) {
        mPowerValue = powerValue;
        if (mBatteryPowerValue != null) {
            if (mPowerFormatter == null)
                mPowerFormatter = new DecimalFormat(getString(R.string.format_power));

            String value = String.format(getString(R.string.value_units_template), mPowerFormatter.format(powerValue), getString(R.string.milliwatts));
            mBatteryPowerValue.setText(value);
        }
    }

    /**
     * Apply the specified theme to this fragment.
     *
     * @param theme the theme to apply to this fragment.
     */
    @Override
    public void applyTheme(Theme theme) {
        mTheme = theme;
        if (mBatteryPowerValue != null) {
            mBatteryPowerValue.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
        }
    }
}