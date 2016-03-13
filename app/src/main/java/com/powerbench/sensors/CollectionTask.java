package com.powerbench.sensors;

import android.util.Log;

import com.powerbench.constants.SensorConstants;
import com.powerbench.datamanager.Point;
import com.powerbench.datamanager.Statistics;

import java.util.HashSet;
import java.util.Set;

/**
 * Class responsible for performing data collection for a specified sensor.
 */
public class CollectionTask {

    /**
     * The sensor associated with this collection task.
     */
    private final Sensor mSensor;

    /**
     * The sensor measurement task.
     */
    private SensorMeasurementTask mSensorMeasurementTask;

    /**
     * The data collection interval.
     */
    private long mCollectionInterval;

    /**
     * The statistics associated with this task.
     */
    private Statistics mStatistics;

    /**
     * The current point associated with this task.
     */
    private Point mPoint;

    /**
     * The set of listeners that listen to updates to this task.
     */
    private Set<MeasurementListener> mMeasurementListeners = new HashSet<MeasurementListener>();

    /**
     * Create a new collection task with the specified sensor and the default collection interval.
     *
     * @param sensor the sensor that is measured during data collection.
     */
    public CollectionTask(Sensor sensor) {
        this(sensor, SensorConstants.DEFAULT_COLLECTION_INTERVAL);
    }

    /**
     * Create a new collection task with the specified sensor and the specified collection interval.
     *
     * @param sensor             the sensor that is measured during data collection.
     * @param collectionInterval the data collection interval.
     */
    public CollectionTask(Sensor sensor, long collectionInterval) {
        this(sensor, collectionInterval, null);
    }

    /**
     * Create a new collection task with the specified sensor. Register the specified measurement
     * listener with this task.
     *
     * @param sensor              the sensor that is measured during data collection.
     * @param measurementListener a listener to register with this task.
     */
    public CollectionTask(Sensor sensor, MeasurementListener measurementListener) {
        this(sensor, SensorConstants.DEFAULT_COLLECTION_INTERVAL, measurementListener);
    }

    /**
     * Create a new collection task with the specified sensor and the specified collection interval.
     * Register the specified measurement listener with this task.
     *
     * @param sensor              the sensor that is measured during data collection.
     * @param collectionInterval  the data collection interval.
     * @param measurementListener a listener to register with this task.
     */
    public CollectionTask(Sensor sensor, long collectionInterval, MeasurementListener measurementListener) {
        mSensor = sensor;
        mCollectionInterval = collectionInterval;
        registerMeasurementListener(measurementListener);
        mStatistics = new Statistics();
    }

    /**
     * Start the data collection task.
     */
    public void start() {
        if (mSensorMeasurementTask == null) {
            mSensorMeasurementTask = new SensorMeasurementTask();
            new Thread(mSensorMeasurementTask).start();
        }
    }

    /**
     * Stop the data collection task.
     */
    public void stop() {
        if (mSensorMeasurementTask != null) {
            mSensorMeasurementTask.stop();
            mSensorMeasurementTask = null;
        }
    }

    public Statistics getStatistics() {
        return mStatistics;
    }

    public double getMedian() {
        return mStatistics.getMedian();
    }

    public double getAverage() {
        return mStatistics.getAverage();
    }

    public double getValue() {
        return mStatistics.getValue();
    }

    /**
     * Register a {@link MeasurementListener} to listen to measurements in this task.
     *
     * @param measurementListener the listener to register.
     */
    public void registerMeasurementListener(MeasurementListener measurementListener) {
        if (measurementListener == null)
            return;

        synchronized (mMeasurementListeners) {
            mMeasurementListeners.add(measurementListener);
        }
    }

    /**
     * Unregister a {@link MeasurementListener} from listening to measurements in this task.
     *
     * @param measurementListener the listener to unregister.
     */
    public void unregisterMeasurementListener(MeasurementListener measurementListener) {
        if (measurementListener == null)
            return;

        synchronized (mMeasurementListeners) {
            mMeasurementListeners.remove(measurementListener);
        }
    }

    /**
     * Notify all registered listeners of a measurement event.
     *
     * @param point the point measurement to notify listeners of.
     */
    public void notifyAllListenersOfMeasurement(Point point) {
        synchronized (mMeasurementListeners) {
            for (MeasurementListener measurementListener : mMeasurementListeners) {
                measurementListener.onMeasurementReceived(point);
            }
        }
    }

    /**
     * Measure the sensor immediately and notify all the associated listeners of the measurement.
     */
    public void measureSensor() {
        mPoint = mSensor.measurePoint();
        mStatistics.addPoint(mPoint);
        notifyAllListenersOfMeasurement(mPoint);
    }

    /**
     * Task used for measuring the sensor associated with this collection task.
     */
    class SensorMeasurementTask implements Runnable {

        /**
         * Flag indicating whether this task has been cancelled.
         */
        private boolean mCancelled = false;

        public void run() {
            while (!mCancelled) {
                measureSensor();
                try {
                    Thread.sleep(mCollectionInterval);
                } catch (InterruptedException e) {
                }
            }
        }

        public void stop() {
            mCancelled = true;
        }
    }

    /**
     * Interface used to listen to measurements in this collection task.
     */
    public interface MeasurementListener {
        void onMeasurementReceived(Point point);
    }
}
