package com.powerbench.utils;

import android.content.Context;

import com.powerbench.R;
import com.powerbench.constants.Constants;

import java.text.DecimalFormat;

/**
 * Class that contains utility functions used by other parts of the application.
 */
public class Utils {

    /**
     * Convert the specified battery life to a simple string with units.
     *
     * @param context the application context
     * @param batteryLife the battery life to convert to a simple string
     */
    public static String convertBatteryLifeToSimpleString(Context context, double batteryLife) {
        String batteryLifeString;
        if (batteryLife > Constants.MINUTES_IN_HOUR) {
            DecimalFormat batteryLifeFormatter = new DecimalFormat(context.getString(R.string.format_battery_life));
            batteryLifeString = String.format(context.getString(R.string.value_units_template), batteryLifeFormatter.format(batteryLife / Constants.MINUTES_IN_HOUR), context.getString(R.string.hours));
        } else {
            batteryLifeString = String.format(context.getString(R.string.value_units_template), Integer.toString((int)batteryLife), context.getString(R.string.minutes));
        }
        return batteryLifeString;
    }
}
