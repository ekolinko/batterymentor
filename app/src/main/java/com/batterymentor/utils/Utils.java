package com.batterymentor.utils;

import android.content.Context;

import com.batterymentor.R;
import com.batterymentor.constants.Constants;

import java.text.DecimalFormat;

/**
 * Class that contains utility functions used by other parts of the application.
 */
public class Utils {

    /**
     * Convert time remaining to a simple string with units.
     *
     * @param context the application context
     * @param timeRemaining the time remaining to convert to a simple string in seconds
     */
    public static String convertTimeRemainingToSimpleString(Context context, double timeRemaining) {
        String timeRemainingString;
        if (timeRemaining > Constants.SECONDS_IN_MINUTE) {
            int minutesRemaining = (int)Math.ceil(timeRemaining / Constants.SECONDS_IN_MINUTE);
            if (minutesRemaining == 1) {
                timeRemainingString = String.format(context.getString(R.string.minute_remaining_template), Integer.toString(minutesRemaining));
            } else {
                timeRemainingString = String.format(context.getString(R.string.minutes_remaining_template), Integer.toString(minutesRemaining));
            }
        } else {
            int secondsRemaining = (int)Math.ceil(timeRemaining);
            if (secondsRemaining == 1) {
                timeRemainingString = String.format(context.getString(R.string.second_remaining_template), Integer.toString(secondsRemaining));
            } else {
                timeRemainingString = String.format(context.getString(R.string.seconds_remaining_template), Integer.toString(secondsRemaining));
            }
        }
        return timeRemainingString;
    }

    /**
     * Convert the specified battery life to a simple string with units.
     *
     * @param context the application context
     * @param batteryLife the battery life to convert to a simple string in minutes
     */
    public static String convertBatteryLifeToSimpleString(Context context, double batteryLife) {
        String batteryLifeString;
        if (batteryLife > Constants.MINUTES_IN_HOUR) {
            DecimalFormat batteryLifeFormatter = new DecimalFormat(context.getString(R.string.format_battery_life));
            batteryLifeString = String.format(context.getString(R.string.value_units_template), batteryLifeFormatter.format(batteryLife / Constants.MINUTES_IN_HOUR), context.getString(R.string.hours));
        } else if (batteryLife > 1){
            batteryLifeString = String.format(context.getString(R.string.value_units_template), Integer.toString((int)batteryLife), context.getString(R.string.minutes));
        } else {
            batteryLifeString = context.getString(R.string.less_than_a_minute);
        }
        return batteryLifeString;
    }
}
