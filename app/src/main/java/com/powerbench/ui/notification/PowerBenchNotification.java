package com.powerbench.ui.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.powerbench.R;
import com.powerbench.constants.UIConstants;
import com.powerbench.ui.main.PowerBenchActivity;

/**
 * Class used for creating and updating a notification.
 */
public class PowerBenchNotification {

    /**
     * The value drawable generator for this notification.
     */
    private NotificationDrawableCache mResourceCache = new NotificationDrawableCache();

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
                        .setSmallIcon(R.drawable.remove_app_icon)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(context.getString(R.string.notification_measuring));
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
     * @param value the value with which to update the notification.
     * @return the updated notification.
     */
    public Notification updateNotification(Context context, double value) {
        int roundedValue = (int) value;

        // Round to the nearest tenth
        roundedValue = ((roundedValue + UIConstants.NOTIFICATION_ROUNDING_FACTOR / 2) /
                UIConstants.NOTIFICATION_ROUNDING_FACTOR) * UIConstants.NOTIFICATION_ROUNDING_FACTOR;

        int resourceId = mResourceCache.getResourceForValue(roundedValue);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(resourceId)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(String.format(context.getString(R.string.notification_power_template), roundedValue));
        return mBuilder.build();
    }
}
