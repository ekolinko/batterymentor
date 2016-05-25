package com.powerbench.ui.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.powerbench.R;
import com.powerbench.constants.Constants;
import com.powerbench.constants.SettingsConstants;
import com.powerbench.constants.UIConstants;
import com.powerbench.model.ModelManager;
import com.powerbench.sensors.Sensor;
import com.powerbench.settings.Settings;
import com.powerbench.ui.main.PowerBenchActivity;

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
                        .setSmallIcon(R.drawable.powerbench)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(context.getString(R.string.notification_measuring))
                        .setDeleteIntent(createOnDismissedIntent(context, Constants.NOTIFICATION_ID));
        mBatteryLifeFormatter = new DecimalFormat(context.getString(R.string.format_battery_life));
        Intent resultIntent = new Intent(context, PowerBenchActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(PowerBenchActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
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
        if (Settings.getInstance().getStatusBarUnits(context).equals(context.getString(R.string.milliamps))) {
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

            resourceId = mResourceCache.getCurrentResourceForValue(roundedValue);
            message = String.format(context.getString(R.string.notification_current_template), roundedValue);
        } else if (Settings.getInstance().getStatusBarUnits(context).equals(context.getString(R.string.hr))) {
            double batteryLife = ModelManager.getInstance().getBatteryModel(context).getBatteryLife();
            int roundedValue = (int) Math.round((batteryLife * UIConstants.NOTIFICATION_BATTERY_LIFE_SCALING_FACTOR));
            resourceId = mResourceCache.getBatteryLifeResourceForValue(roundedValue);
            message = String.format(context.getString(R.string.notification_battery_life_template), mBatteryLifeFormatter.format(batteryLife));
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

            resourceId = mResourceCache.getPowerResourceForValue(roundedValue);
            message = String.format(context.getString(R.string.notification_power_template), roundedValue);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(resourceId)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(message)
                        .setDeleteIntent(createOnDismissedIntent(context, Constants.NOTIFICATION_ID));
        return mBuilder.build();
    }

    /**
     * Update the notification with the current value.
     *
     * @param context the context of the application.
     * @return the updated notification.
     */


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
        intent.putExtra(UIConstants.NOTIFICATION_ID_KEY, notificationId);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        notificationId, intent, 0);
        return pendingIntent;
    }
}
