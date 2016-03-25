package com.powerbench.collectionmanager;

import com.powerbench.sensors.Sensor;

import java.util.HashSet;
import java.util.Set;

/**
 * Singleton class that manages all of the active collection tasks.
 */
public class CollectionManager {

    /**
     * The primary battery collection task.
     */
    private CollectionTask mPowerCollectionTask;

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

    public CollectionTask getPowerCollectionTask() {
        if (mPowerCollectionTask == null) {
            mPowerCollectionTask = new CollectionTask(Sensor.POWER);
        }
        return mPowerCollectionTask;
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
