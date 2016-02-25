package com.powerbench.sensors;

import com.powerbench.constants.SensorConstants;
import com.powerbench.datamanager.Point;
import com.powerbench.debug.Debug;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * The set of sensors responsible for reading power values from the filesystem.
 */
public enum Sensor {
    CURRENT,
    VOLTAGE,
    POWER;

    /**
     * The name of the file used for reading the sensor measurement.
     */
    private String mFilename;

    /**
     * The timestamp of the last measurement.
     */
    private double mTimestamp;

    /**
     * The value of the last measurement.
     */
    private double mValue;


    /**
     * Return the filename associated with this sensor. Set the filename if it is null.
     */
    private String getFilename() {
        if (mFilename == null) {
            switch (this) {
                case CURRENT:
                    if (new File(SensorConstants.SENSOR_CURRENT_NOW).exists()) {
                        mFilename = SensorConstants.SENSOR_CURRENT_NOW;
                    }
                    break;
                case VOLTAGE:
                    if (new File(SensorConstants.SENSOR_VOLTAGE_NOW).exists()) {
                        mFilename = SensorConstants.SENSOR_VOLTAGE_NOW;
                    }
                    break;
                case POWER:
                    break;
            }
        }
        return mFilename;
    }

    /**
     * Return true if this sensor is supported, false otherwise.
     *
     * @return true if this sensor is supported, false otherwise.
     */
    public boolean isSupported() {
        switch (this) {
            case CURRENT:
            case VOLTAGE:
                return getFilename() != null;
            case POWER:
                return CURRENT.isSupported() && VOLTAGE.isSupported();
        }
        return false;
    }

    /**
     * Read a point measurement from this sensor.
     *
     * @return a point measurement from this sensor.
     */
    public synchronized Point measurePoint() {
        Point point = null;
        if (isSupported()) {
            double timestamp = System.currentTimeMillis();
            if (Math.abs(timestamp - mTimestamp) < SensorConstants.SENSOR_MINIMUM_MEASUREMENT_INTERVAL) {
                point = new Point(timestamp, mValue);
            } else {
                mTimestamp = timestamp;
                mValue = measureValue();
                point = new Point(mTimestamp, mValue);
            }
        }
        return point;
    }

    /**
     * Read a value measurement from this sensor and scale it by the specified conversion factor.
     *
     * @return a scaled value measurement from this sensor.
     */
    public double measureValue() {
        double conversionFactor = SensorConstants.DEFAULT_CONVERSION_FACTOR;
        switch (this) {
            case CURRENT:
                conversionFactor = SensorConstants.MILLIAMPS_IN_MICROAMP;
                break;
            case VOLTAGE:
                conversionFactor = SensorConstants.VOLTS_IN_MICROWATT;
                break;
            case POWER:
                return CURRENT.measureValue() * VOLTAGE.measureValue();
        }
        return measureValue(conversionFactor);
    }

    /**
     * Read a value measurement from this sensor and scale it by the specified conversion factor.
     *
     * @return a scaled value measurement from this sensor.
     */
    public double measureValue(double conversionFactor) {
        double value = 0;
        try {
            RandomAccessFile file = new RandomAccessFile(getFilename(), SensorConstants.MODE_READ);
            value = Double.parseDouble(file.readLine()) / conversionFactor;
        } catch (IOException e) {
            if (Debug.isCollectionManagerLoggingEnabled())
                Debug.printDebug(e);
        }
        return value;
    }
}
