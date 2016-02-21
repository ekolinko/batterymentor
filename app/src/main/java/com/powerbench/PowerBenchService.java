package com.powerbench;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.powerbench.sensors.CollectionTask;

import java.util.HashSet;
import java.util.Set;

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
     * The set of collection tasks that are collecting data in the background.
     */
    private Set<CollectionTask> mCollectionTasks = new HashSet<CollectionTask>();

    /**
     * Create a new powerbench service. Instantiate and initialize all the core modules.
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Add a collection task to the service. The specified collection task will continue to collect
     * data when the UI is put into the background.
     */
    public void addCollectionTask(CollectionTask collectionTask) {
        synchronized (mCollectionTasks) {
            mCollectionTasks.add(collectionTask);
        }
    }

    /**
     * Remove a collection task from the service. The specified collection task will no longer
     * collect data when the UI is put into the background.
     */
    public void removeCollectionTask(CollectionTask collectionTask) {
        synchronized (mCollectionTasks) {
            mCollectionTasks.add(collectionTask);
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
}
