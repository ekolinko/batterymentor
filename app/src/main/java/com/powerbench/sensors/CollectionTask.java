package com.powerbench.sensors;

import com.powerbench.constants.SensorConstants;
import com.powerbench.datamanager.Point;
import com.powerbench.datamanager.Statistics;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class responsible for performing data collection for a specified sensor.
 */
public class CollectionTask {

    /**
     * The sensor associated with this collection task.
     */
    private final Sensor mSensor;

    /**
     * The timer used for data collection.
     */
    private Timer mTimer;

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
     * @param sensor the sensor that is measured during data collection.
     * @param collectionInterval the data collection interval.
     */
    public CollectionTask(Sensor sensor, long collectionInterval) {
        this(sensor, collectionInterval, null);
    }

    /**
     * Create a new collection task with the specified sensor. Register the specified measurement
     * listener with this task.
     *
     * @param sensor the sensor that is measured during data collection.
     * @param measurementListener a listener to register with this task.
     */
    public CollectionTask(Sensor sensor, MeasurementListener measurementListener) {
        this(sensor, SensorConstants.DEFAULT_COLLECTION_INTERVAL, measurementListener);
    }

    /**
     * Create a new collection task with the specified sensor and the specified collection interval.
     * Register the specified measurement listener with this task.
     *
     * @param sensor the sensor that is measured during data collection.
     * @param collectionInterval the data collection interval.
     * @param measurementListener a listener to register with this task.
     */
    public CollectionTask(Sensor sensor, long collectionInterval, MeasurementListener measurementListener) {
        mSensor = sensor;
        mCollectionInterval = collectionInterval;
        registerMeasurementListener(measurementListener);
        mTimer = new Timer();
        mStatistics = new Statistics();
    }

    /**
     * Start the data collection task.
     */
    public void start() {
        mTimer.scheduleAtFixedRate(new SensorMeasurementTask(), new Date(), mCollectionInterval);
    }

    /**
     * Stop the data collection task.
     */
    public void stop() {
        mTimer.cancel();
    }

    public Statistics getStatistics() {
        return mStatistics;
    }

    public double getMedian() {
        return mStatistics.getMedian();
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
    class SensorMeasurementTask extends TimerTask {
        public void run() {
            measureSensor();
        }
    }

    /**
     * Interface used to listen to measurements in this collection task.
     */
    public interface MeasurementListener {
        void onMeasurementReceived(Point point);
    }
}
