package com.powerbench.collectionmanager;

import android.content.Context;

import com.powerbench.constants.SensorConstants;
import com.powerbench.datamanager.Point;
import com.powerbench.datamanager.RealtimeStatistics;
import com.powerbench.sensors.ChargerManager;
import com.powerbench.sensors.Sensor;

import java.util.HashSet;
import java.util.Set;

/**
 * Class responsible for performing data collection for a specified sensor.
 */
public class CollectionTask {

    /**
     * The context of the application.
     */
    private final Context mContext;

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
    private RealtimeStatistics mStatistics;

    /**
     * The battery statistics associated with this task.
     */
    private RealtimeStatistics mBatteryStatistics;

    /**
     * The charger statistics associated with this task.
     */
    private RealtimeStatistics mChargerStatistics;

    /**
     * The current point associated with this task.
     */
    private Point mPoint;

    /**
     * The set of listeners that listen to updates to this task.
     */
    private Set<MeasurementListener> mMeasurementListeners = new HashSet<MeasurementListener>();

    /**
     * The charger listener responsible for listening to
     */
    private ChargerManager.ChargerListener mChargerListener;

    /**
     * Create a new collection task with the specified sensor and the default collection interval.
     *
     * @param sensor the sensor that is measured during data collection.
     */
    public CollectionTask(Context context, Sensor sensor) {
        this(context, sensor, SensorConstants.DEFAULT_COLLECTION_INTERVAL);
    }

    /**
     * Create a new collection task with the specified sensor and the specified collection interval.
     *
     * @param sensor             the sensor that is measured during data collection.
     * @param collectionInterval the data collection interval.
     */
    public CollectionTask(Context context, Sensor sensor, long collectionInterval) {
        this(context, sensor, collectionInterval, null);
    }

    /**
     * Create a new collection task with the specified sensor. Register the specified measurement
     * listener with this task.
     *
     * @param sensor              the sensor that is measured during data collection.
     * @param measurementListener a listener to register with this task.
     */
    public CollectionTask(Context context, Sensor sensor, MeasurementListener measurementListener) {
        this(context, sensor, SensorConstants.DEFAULT_COLLECTION_INTERVAL, measurementListener);
    }

    /**
     * Create a new collection task with the specified sensor and the specified collection interval.
     * Register the specified measurement listener with this task.
     *
     * @param sensor              the sensor that is measured during data collection.
     * @param collectionInterval  the data collection interval.
     * @param measurementListener a listener to register with this task.
     */
    public CollectionTask(Context context, Sensor sensor, long collectionInterval, MeasurementListener measurementListener) {
        mContext = context;
        mSensor = sensor;
        mCollectionInterval = collectionInterval;
        registerMeasurementListener(measurementListener);
        mBatteryStatistics = new RealtimeStatistics(false);
        mChargerStatistics = new RealtimeStatistics(true);
        mStatistics = mBatteryStatistics;
        mChargerListener = new ChargerManager.ChargerListener() {
            @Override
            public void onChargerConnected() {
                CollectionTask.this.onChargerConnected();
            }

            @Override
            public void onChargerDisconnected() {
                CollectionTask.this.onChargerDisconnected();
            }

            @Override
            public void onBatteryLevelChanged(int level) {

            }
        };
    }

    /**
     * Start the data collection task.
     */
    public void start() {
        if (mSensorMeasurementTask == null) {
            mSensorMeasurementTask = new SensorMeasurementTask();
            new Thread(mSensorMeasurementTask).start();
            ChargerManager.getInstance().registerChargerListener(getContext(), mChargerListener);
        }
    }

    /**
     * Stop the data collection task.
     */
    public void stop() {
        if (mSensorMeasurementTask != null) {
            mSensorMeasurementTask.stop();
            mSensorMeasurementTask = null;
            ChargerManager.getInstance().unregisterChargerListener(getContext(), mChargerListener);
        }
    }

    /**
     * Method that is called when a charger is connected. This is only called when the collection
     * task is running.
     */
    protected void onChargerConnected() {
        mStatistics = mChargerStatistics;
        if (mStatistics.getNumPoints() == 0) {
            measureSensor();
        }
    }

    /**
     * Method that is called when a charger is disconnected. This is only called when the collection
     * task is running.
     */
    protected void onChargerDisconnected() {
        mStatistics = mBatteryStatistics;
        if (mStatistics.getNumPoints() == 0) {
            measureSensor();
        }
    }

    public Context getContext() {
        return mContext;
    }

    public RealtimeStatistics getRealtimeStatistics() {
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
    public Point measureSensor() {
        mPoint = mSensor.measurePoint();
        mStatistics.addPoint(mPoint);
        notifyAllListenersOfMeasurement(mPoint);
        return mPoint;
    }

    protected RealtimeStatistics getBatteryStatistics() {
        return mBatteryStatistics;
    }

    protected RealtimeStatistics getChargerStatistics() {
        return mChargerStatistics;
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
