package com.batterymentor.collectionmanager;

import android.content.Context;

import com.batterymentor.constants.SensorConstants;
import com.batterymentor.sensors.Sensor;

import java.util.HashSet;
import java.util.Set;

/**
 * Singleton class that manages all of the active collection tasks.
 */
public class CollectionManager {

    /**
     * The primary battery collection task.
     */
    private LifetimeCollectionTask mPowerCollectionTask;

    /**
     * The primary application collection task.
     */
    private ApplicationCollectionTask mApplicationCollectionTask;

    /**
     * The set of collection tasks that are collecting data in the background.
     */
    private Set<CollectionTask> mCollectionTasks = new HashSet<CollectionTask>();

    private static class SingletonHolder {
        private static final CollectionManager INSTANCE = new CollectionManager();
    }

    public static CollectionManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private CollectionManager() {
    }

    public LifetimeCollectionTask getPowerCollectionTask(Context context) {
        if (mPowerCollectionTask == null) {
            mPowerCollectionTask = new LifetimeCollectionTask(context, Sensor.POWER, SensorConstants.LIFETIME_STATISTICS_BATTERY_FILENAME, SensorConstants.LIFETIME_STATISTICS_CHARGER_FILENAME);
        }
        return mPowerCollectionTask;
    }

    public ApplicationCollectionTask getApplicationCollectionTask(Context context) {
        if (mApplicationCollectionTask == null) {
            mApplicationCollectionTask = new ApplicationCollectionTask(context);
        }
        return mApplicationCollectionTask;
    }

    /**
     * Add a collection task to the manager. The specified collection task will continue to collect
     * data when the UI is put into the background.
     */
    public void addCollectionTask(CollectionTask collectionTask) {
        synchronized (mCollectionTasks) {
            mCollectionTasks.add(collectionTask);
        }
    }

    /**
     * Remove a collection task from the manager. The specified collection task will no longer
     * collect data when the UI is put into the background.
     */
    public void removeCollectionTask(CollectionTask collectionTask) {
        synchronized (mCollectionTasks) {
            mCollectionTasks.add(collectionTask);
        }
    }
}
