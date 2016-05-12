package com.powerbench;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import com.powerbench.collectionmanager.CollectionManager;
import com.powerbench.collectionmanager.LifetimeCollectionTask;
import com.powerbench.constants.Constants;
import com.powerbench.constants.UIConstants;
import com.powerbench.datamanager.Point;
import com.powerbench.collectionmanager.CollectionTask;
import com.powerbench.ui.notification.PowerBenchNotification;

/**
 * This class is a service that performs data collection and benchmark tasks in the background. This
 * service also provides access to all the core modules for the UI, manages a list of all
 * collection tasks and benchmarks, and makes notification updates in realtime.
 */
public class PowerBenchService extends Service {

    /**
     * Interface that allows clients to bind to the servic.e
     */
    private IBinder mBinder = new PowerBenchBinder();

    /**
     * The notification associated with this service.
     */
    private Notification mNotification;

    /**
     * The notification manager service.
     */
    private NotificationManager mNotificationManager;

    /**
     * The power collection task.
     */
    private LifetimeCollectionTask mPowerCollectionTask;

    /**
     * The measurement listener.
     */
    private CollectionTask.MeasurementListener mMeasurementListener;

    /**
     * The notification dismissed broadcast receiver.
     */
    private NotificationDismissedReceiver mNotificationDismissedReceiver;

    /**
     * Flag indicating whether the notification should be shown.
     */
    private boolean mShowNotification = true;

    /**
     * Create a new powerbench service. Instantiate and initialize all the core modules.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mNotification = PowerBenchNotification.getInstance().createNotification(this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Constants.NOTIFICATION_ID, mNotification);
        mPowerCollectionTask = CollectionManager.getInstance().getPowerCollectionTask(this);
        mPowerCollectionTask.start();
        mMeasurementListener = new CollectionTask.MeasurementListener() {
            @Override
            public void onMeasurementReceived(Point point) {
                updateNotification();
            }
        };
        mPowerCollectionTask.registerMeasurementListener(mMeasurementListener);
        mNotificationDismissedReceiver = new NotificationDismissedReceiver();
        registerReceiver(mNotificationDismissedReceiver, new IntentFilter(Constants.NOTIFICATION_ACTION));
    }

    /**
     * Update the notification using the current settings.
     */
    public void updateNotification() {
        if (mShowNotification) {
            double average = mPowerCollectionTask.getRealtimeStatistics().getAverage();
            mNotification = PowerBenchNotification.getInstance().updateNotification(PowerBenchService.this, Math.abs(average));
            mNotificationManager.notify(Constants.NOTIFICATION_ID, mNotification);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Start the notification.
     */
    public void startNotification() {
        mShowNotification = true;
        updateNotification();
    }

    /**
     * Cancel the notification in the notificaiton manager and unregister this from listening to the
     * power collection task.
     */
    public void cancelNotification() {
        mPowerCollectionTask.unregisterMeasurementListener(mMeasurementListener);
        mShowNotification = false;
        if (mNotificationManager != null) {
            mNotificationManager.cancel(Constants.NOTIFICATION_ID);
        }
    }



    /**
     * The binder that clients use to bind to this service.
     */
    public class PowerBenchBinder extends Binder {
        public PowerBenchService getService() {
            return PowerBenchService.this;
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mNotificationDismissedReceiver);
    }

    /**
     * Class used for detecting when the notification has been swiped away.
     */
    public class NotificationDismissedReceiver extends BroadcastReceiver {
        public NotificationDismissedReceiver(){

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int notificationId = intent.getExtras().getInt(UIConstants.NOTIFICATION_ID_KEY);
            if (notificationId == Constants.NOTIFICATION_ID && mMeasurementListener != null && mShowNotification) {
                cancelNotification();
            }
        }
    }
}
