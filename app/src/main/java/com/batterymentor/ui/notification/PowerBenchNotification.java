package com.batterymentor.ui.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.batterymentor.R;
import com.batterymentor.constants.Constants;
import com.batterymentor.constants.FlavorConstants;
import com.batterymentor.constants.UIConstants;
import com.batterymentor.device.Device;
import com.batterymentor.model.ModelManager;
import com.batterymentor.sensors.ChargerManager;
import com.batterymentor.sensors.Sensor;
import com.batterymentor.settings.Settings;
import com.batterymentor.ui.main.BatteryMentorActivity;
import com.batterymentor.utils.Utils;

import java.text.DecimalFormat;

/**
 * Class used for creating and updating a notification.
 */
public class PowerBenchNotification {

    /**
     * The value drawable generator for this notification.
     */
    private NotificationDrawableCache mResourceCache = new NotificationDrawableCache();

    /**
     * The battery life formatter.
     */
    private DecimalFormat mBatteryLifeFormatter;

    private static class SingletonHolder {
        private static final PowerBenchNotification INSTANCE = new PowerBenchNotification();
    }

    public static PowerBenchNotification getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private PowerBenchNotification() {
    }

    /**
     * Create the powerbench notification.
     *
     * @param context the context of the application.
     * @return the updated notification.
     */
    public Notification createNotification(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(context.getString(R.string.notification_measuring))
                        .setDeleteIntent(createOnDismissedIntent(context, FlavorConstants.NOTIFICATION_ID));
        mBatteryLifeFormatter = new DecimalFormat(context.getString(R.string.format_battery_life));
        Intent resultIntent = new Intent(context, BatteryMentorActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(BatteryMentorActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        Intent intent = new Intent(context, BatteryMentorActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        return mBuilder.build();
    }

    /**
     * Update the notification with the specified power value.
     *
     * @param context the context of the application.
     * @param value the value with which to measure the notification.
     * @return the updated notification.
     */
    public Notification updateNotification(Context context, double value) {
        String message;
        int resourceId;
        if (Settings.getInstance().getStatusBarUnits(context).equals(context.getString(R.string.mA))) {
            value = value / Sensor.VOLTAGE.measure();
            int roundedValue = (int) Math.round(value);

            // Rounding code
            if (value < UIConstants.NOTIFICATION_ROUNDING_THRESHOLD_HUNDREDS) {
                roundedValue = ((roundedValue + UIConstants.NOTIFICATION_ROUNDING_FACTOR_HUNDREDS / 2) /
                        UIConstants.NOTIFICATION_ROUNDING_FACTOR_HUNDREDS) * UIConstants.NOTIFICATION_ROUNDING_FACTOR_HUNDREDS;
            } else if (value < UIConstants.NOTIFICATION_ROUNDING_THRESHOLD_THOUSANDS) {
                roundedValue = ((roundedValue + UIConstants.NOTIFICATION_ROUNDING_FACTOR_THOUSANDS / 2) /
                        UIConstants.NOTIFICATION_ROUNDING_FACTOR_THOUSANDS) * UIConstants.NOTIFICATION_ROUNDING_FACTOR_THOUSANDS;
            } else {
                roundedValue = ((roundedValue + UIConstants.NOTIFICATION_ROUNDING_FACTOR_TENS_OF_THOUSANDS / 2) /
                        UIConstants.NOTIFICATION_ROUNDING_FACTOR_TENS_OF_THOUSANDS) * UIConstants.NOTIFICATION_ROUNDING_FACTOR_TENS_OF_THOUSANDS;
            }

            ChargerManager chargerManager = ChargerManager.getInstance();
            boolean isChargerConnected = chargerManager.isCharging();
            if (Device.getInstance().isBatteryPowerEstimated() && isChargerConnected) {
                message = context.getString(R.string.charging);
                resourceId = R.drawable.notification_battery_life_charging;
            } else if (roundedValue <= 0 && isChargerConnected) {
                message = context.getString(R.string.not_charging);
                resourceId = R.drawable.notification_battery_life_not_charging;
            } else {
                message = String.format(context.getString(R.string.notification_current_template), roundedValue);
                resourceId = mResourceCache.getCurrentResourceForValue(roundedValue);
            }
        } else if (Settings.getInstance().getStatusBarUnits(context).equals(context.getString(R.string.hours))) {
            double batteryLife = ModelManager.getInstance().getBatteryModel(context).getBatteryLife();
            int batteryLifeRounded = (int) Math.round((batteryLife / UIConstants.NOTIFICATION_BATTERY_LIFE_SCALING_FACTOR));

            ChargerManager chargerManager = ChargerManager.getInstance();
            int batteryLevel = chargerManager.getBatteryLevel();
            boolean isChargerConnected = chargerManager.isCharging();
            if (batteryLevel == Constants.INT_PERCENT && isChargerConnected) {
                resourceId = R.drawable.notification_is_full;
            } else if (Device.getInstance().isBatteryPowerEstimated() && isChargerConnected) {
                resourceId = R.drawable.notification_battery_life_charging;
            } else if (batteryLife <= 0 && isChargerConnected) {
                resourceId = R.drawable.notification_battery_life_not_charging;
            } else if (batteryLife >= UIConstants.MAX_BATTERY_LIFE) {
                resourceId = R.drawable.notification_battery_life_max;
            } else {
                resourceId = mResourceCache.getBatteryLifeResourceForValue(batteryLifeRounded);
            }

            message = Utils.convertBatteryLifeToSimpleString(context, batteryLife);
            if (isChargerConnected) {
                message += Constants.SPACE + context.getString(R.string.battery_life_until_full).toLowerCase();
            } else {
                message += Constants.SPACE + context.getString(R.string.remaining).toLowerCase();
            }
            if (batteryLevel == Constants.INT_PERCENT && isChargerConnected) {
                message = context.getString(R.string.fully_charged);
            }  else if (Device.getInstance().isBatteryPowerEstimated() && isChargerConnected) {
                message = context.getString(R.string.charging);
            } else if (batteryLife <= 0 && isChargerConnected) {
                message = context.getString(R.string.not_charging);
            } else if (batteryLife >= UIConstants.MAX_BATTERY_LIFE) {
                message = String.format(context.getString(R.string.value_units_template), context.getString(R.string.max_battery_life), context.getString(R.string.hours));
            } else if (Double.isInfinite(batteryLife)) {
                message = String.format(context.getString(R.string.value_units_template), context.getString(R.string.invalid_value), context.getString(R.string.hours));
            }
        } else {
            int roundedValue = (int) Math.round(value);

            // Rounding code
            if (value < UIConstants.NOTIFICATION_ROUNDING_THRESHOLD_HUNDREDS) {
                roundedValue = ((roundedValue + UIConstants.NOTIFICATION_ROUNDING_FACTOR_HUNDREDS / 2) /
                        UIConstants.NOTIFICATION_ROUNDING_FACTOR_HUNDREDS) * UIConstants.NOTIFICATION_ROUNDING_FACTOR_HUNDREDS;
            } else if (value < UIConstants.NOTIFICATION_ROUNDING_THRESHOLD_THOUSANDS) {
                roundedValue = ((roundedValue + UIConstants.NOTIFICATION_ROUNDING_FACTOR_THOUSANDS / 2) /
                        UIConstants.NOTIFICATION_ROUNDING_FACTOR_THOUSANDS) * UIConstants.NOTIFICATION_ROUNDING_FACTOR_THOUSANDS;
            } else {
                roundedValue = ((roundedValue + UIConstants.NOTIFICATION_ROUNDING_FACTOR_TENS_OF_THOUSANDS / 2) /
                        UIConstants.NOTIFICATION_ROUNDING_FACTOR_TENS_OF_THOUSANDS) * UIConstants.NOTIFICATION_ROUNDING_FACTOR_TENS_OF_THOUSANDS;
            }

            ChargerManager chargerManager = ChargerManager.getInstance();
            boolean isChargerConnected = chargerManager.isCharging();
            if (Device.getInstance().isBatteryPowerEstimated() && isChargerConnected) {
                message = context.getString(R.string.charging);
                resourceId = R.drawable.notification_battery_life_charging;
            } else if (roundedValue <= 0 && isChargerConnected) {
                message = context.getString(R.string.not_charging);
                resourceId = R.drawable.notification_battery_life_not_charging;
            } else {
                message = String.format(context.getString(R.string.notification_power_template), roundedValue);
                resourceId = mResourceCache.getPowerResourceForValue(roundedValue);
            }
        }

        Intent intent = new Intent(context, BatteryMentorActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(resourceId)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(message)
                        .setContentIntent(pendingIntent)
                        .setDeleteIntent(createOnDismissedIntent(context, FlavorConstants.NOTIFICATION_ID));
        return mBuilder.build();
    }

    /**
     * Create an intent that is triggered when the notification is dismissed.
     *
     * @param context the application context.
     * @param notificationId the notification id.
     * @return the intent that is triggered when the notification is dismissed.
     */
    private PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent();
        intent.setAction(Constants.NOTIFICATION_ACTION);
        intent.putExtra(FlavorConstants.NOTIFICATION_ID_KEY, notificationId);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        notificationId, intent, 0);
        return pendingIntent;
    }
}
