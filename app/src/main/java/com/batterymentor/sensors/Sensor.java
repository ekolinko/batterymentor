package com.batterymentor.sensors;

import com.batterymentor.constants.SensorConstants;
import com.batterymentor.datamanager.Point;
import com.batterymentor.debug.Debug;
import com.batterymentor.sensors.cpu.FrequencySensor;
import com.batterymentor.sensors.cpu.LoadSensor;
import com.batterymentor.sensors.power.CurrentSensor;
import com.batterymentor.sensors.power.PowerEstimationSensor;
import com.batterymentor.sensors.power.PowerSensor;
import com.batterymentor.sensors.power.VoltageSensor;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * The set of sensors responsible for reading values from the filesystem.
 */
public abstract class Sensor {
    public static final CurrentSensor CURRENT = new CurrentSensor();
    public static final VoltageSensor VOLTAGE = new VoltageSensor();
    public static final PowerSensor POWER = new PowerSensor();
    public static final FrequencySensor FREQUENCY_SENSOR = new FrequencySensor();
    public static final LoadSensor LOAD_SENSOR = new LoadSensor();
    public static final PowerEstimationSensor POWER_ESTIMATION_SENSOR = new PowerEstimationSensor();

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
     * Create an initialize the sensor.
     * @return
     */
    public Sensor() {
        initialize();
    }

    /**
     * Initialize the sensor.
     */
    public void initialize() {
    }

    /**
     * Return the filename associated with this sensor. Set the filename if it is null.
     */
    public String getFilename() {
        if (mFilename == null) {
            mFilename = initFilename();
        }
        return mFilename;
    }

    /**
     * Initialize the filename for this specific sensor.
     *
     * @return the filename associated with the sensor or null if it doesn't exist.
     */
    public abstract String initFilename();

    /**
     * Return true if this sensor is supported, false otherwise.
     *
     * @return true if this sensor is supported, false otherwise.
     */
    public boolean isSupported() {
        return getFilename() != null;
    }

    /**
     * Read a value measurement from this sensor and scale it by the specified conversion factor.
     *
     * @return a scaled value measurement from this sensor.
     */
    public abstract double measure();

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
                mValue = measure();
                point = new Point(mTimestamp, mValue);
            }
        }
        return point;
    }

    /**
     * Read a value measurement from this sensor and scale it by the specified conversion factor.
     *
     * @param conversionFactor the factor by which to scale the measurement.
     * @return a scaled value measurement from this sensor.
     */
    public double measureValue(double conversionFactor) {
        return measureValue(getFilename(), conversionFactor);
    }

    /**
     * Read a value measurement from this sensor using an alternate measurement system and scale it
     * by the specified conversions factor.
     *
     * @return a scaled value measurement from this sensor.
     */
    public double measureValueAlternate() {
        return 0;
    }

    /**
     * Read a value measurement from this sensor and scale it by the specified conversion factor.
     *
     * @param filename the name of the file to measure from.
     * @param conversionFactor the factor by which to scale the measurement.
     * @return a scaled value measurement from this sensor.
     */
    public double measureValue(String filename, double conversionFactor) {
        double value = 0;
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(filename, SensorConstants.MODE_READ);
            value = Double.parseDouble(file.readLine()) / conversionFactor;
        } catch (Exception e) {
            value = measureValueAlternate();
            if (Debug.isCollectionManagerLoggingEnabled())
                Debug.printDebug(e);
        } finally {
            try {
                if (file != null)
                    file.close();
            } catch (IOException e) {
            }
        }
        return value;
    }
}
