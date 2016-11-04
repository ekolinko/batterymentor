package com.batterymentor.collectionmanager;

import android.content.Context;
import android.os.Handler;

import com.batterymentor.constants.SensorConstants;
import com.batterymentor.datamanager.Point;
import com.batterymentor.datamanager.Statistics;
import com.batterymentor.sensors.Sensor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class responsible for performing data collection for a specified sensor that periodically saves
 * data to persistent storage.
 */
public class LifetimeCollectionTask extends CollectionTask {

    /**
     * The handler used for scheduling tasks.
     */
    private Handler mHandler;

    /**
     * The name of the battery file in persistent storage.
     */
    private String mBatteryFilename;

    /**
     * The name of the charger file in persistent storage.
     */
    private String mChargerFilename;

    /**
     * The thread used for saving data to persistent storage.
     */
    private SaverThread mSaverThread;

    /**
     * The lifetime statistics associated with this task.
     */
    private Statistics mLifetimeStatistics;

    /**
     * The lifetime battery statistics associated with this task.
     */
    private Statistics mLifetimeBatteryStatistics;

    /**
     * The lifetime charger statistics associated with this task.
     */
    private Statistics mLifetimeChargerStatistics;

    /**
     * Create a new collection task with the specified sensor, context, and filename where the
     * persistent data is stored.
     *
     * @param context             the context of the application.
     * @param sensor              the sensor that is measured during data collection.
     * @param batteryFilename     the location where to save the battery file in persistent storage.
     * @param chargerFilename     the location where to save the charger file in persistent storage.
     */
    public LifetimeCollectionTask(Context context, Sensor sensor, String batteryFilename, String chargerFilename) {
        super(context, sensor);
        mBatteryFilename = batteryFilename;
        mChargerFilename = chargerFilename;
        mHandler = new Handler();
        mLifetimeBatteryStatistics = loadLifetimeStatisticsFromStorage(context, batteryFilename);
        if (mLifetimeBatteryStatistics == null)
            mLifetimeBatteryStatistics = new Statistics(false);
        getBatteryStatistics().setLifetimeStatistics(mLifetimeBatteryStatistics);
        mLifetimeChargerStatistics = loadLifetimeStatisticsFromStorage(context, chargerFilename);
        if (mLifetimeChargerStatistics == null)
            mLifetimeChargerStatistics = new Statistics(true);
        mLifetimeStatistics = mLifetimeBatteryStatistics;
        getChargerStatistics().setLifetimeStatistics(mLifetimeChargerStatistics);
    }

    /**
     * Start the data collection task.
     */
    public void start() {
        super.start();
        mSaverThread = new SaverThread();
        mHandler.post(mSaverThread);
    }

    /**
     * Stop the data collection task.
     */
    public void stop() {
        super.stop();
        mHandler.removeCallbacks(mSaverThread);
    }

    /**
     * Measure the sensor and add it to the usage data.
     */
    public Point measureSensor() {
        Point point = super.measureSensor();
        if (mLifetimeStatistics != null) {
            mLifetimeStatistics.addPoint(point);
        }
        return point;
    }

    /**
     * Save persistent battery lifetime statistics to storage.
     */
    public void saveBatteryLifetimeStatisticsToStorage() {
        saveLifetimeStatisticsToStorage(getContext(), mBatteryFilename, mLifetimeBatteryStatistics);
    }

    /**
     * Save persistent charger lifetime statistics to storage.
     */
    public void saveChargerLifetimeStatisticsToStorage() {
        saveLifetimeStatisticsToStorage(getContext(), mChargerFilename, mLifetimeChargerStatistics);
    }

    /**
     * Save persistent lifetime statistics to the specified file in storage.
     */
    public void saveLifetimeStatisticsToStorage(Context context, String filename, Statistics statistics) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(statistics);
        } catch (IOException e) {
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                }
            }
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Load the lifetime statistics from the specified file in storage.
     */
    public Statistics loadLifetimeStatisticsFromStorage(Context context, String filename) {
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        Statistics statistics = null;
        try {
            fileInputStream = context.openFileInput(filename);
            objectInputStream = new ObjectInputStream(fileInputStream);
            Object object = objectInputStream.readObject();
            if (object instanceof Statistics) {
                statistics = (Statistics) object;
            }
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                }
            }
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return statistics;
    }

    /**
     * Return the usage data.
     *
     * @return the usage data.
     */
    public Statistics getLifetimeStatistics() {
        return mLifetimeStatistics;
    }

    /**
     * Return the average the usage data.
     *
     * @return the average usage data.
     */
    public double getAverage() {
        if (mLifetimeStatistics != null)
            return mLifetimeStatistics.getAverage();

        return 0;
    }

    @Override
    protected void onChargerConnected() {
        mLifetimeStatistics = mLifetimeChargerStatistics;
        super.onChargerConnected();
    }

    @Override
    protected void onChargerDisconnected() {
        mLifetimeStatistics = mLifetimeBatteryStatistics;
        super.onChargerDisconnected();
    }

    public Statistics getBatteryLifetimeStatistics() {
        return mLifetimeBatteryStatistics;
    }

    public Statistics getChargerLifetimeStatistics() {
        return mLifetimeChargerStatistics;
    }

    /**
     * The thread used to save data to persistent storage.
     */
    class SaverThread implements Runnable {
        @Override
        public void run() {
            saveBatteryLifetimeStatisticsToStorage();
            saveChargerLifetimeStatisticsToStorage();
            mHandler.postDelayed(mSaverThread, SensorConstants.PERSISTENT_SAVE_INTERVAL);
        }
    }
}
