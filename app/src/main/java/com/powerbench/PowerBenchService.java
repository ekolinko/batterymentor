package com.powerbench;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.powerbench.collectionmanager.CollectionManager;
import com.powerbench.constants.Constants;
import com.powerbench.datamanager.Point;
import com.powerbench.sensors.CollectionTask;
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
     * Create a new powerbench service. Instantiate and initialize all the core modules.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mNotification = PowerBenchNotification.getInstance().createNotification(this);
        startForeground(Constants.NOTIFICATION_ID, mNotification);
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        CollectionManager.getInstance().getPowerCollectionTask().registerMeasurementListener(new CollectionTask.MeasurementListener() {
            @Override
            public void onMeasurementReceived(Point point) {
                mNotification = PowerBenchNotification.getInstance().updateNotification(PowerBenchService.this, Math.abs(point.getValue()));
                notificationManager.notify(Constants.NOTIFICATION_ID, mNotification);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * The binder that clients use to bind to this service.
     */
    public class PowerBenchBinder extends Binder {
        public PowerBenchService getService() {
            return PowerBenchService.this;
        }
    }
}
