package com.powerbench.constants;

/**
 * Class containing different constants used by the sensors module.
 */
public class SensorConstants {

    /**
     * The parameter indicating that a file should be open in read mode.
     */
    public static final String MODE_READ = "r";

    /**
     * The default conversion factor.
     */
    public static final double DEFAULT_CONVERSION_FACTOR = 1;

    /**
     * The default rate at which data is collected in milliseconds.
     */
    public static final long DEFAULT_COLLECTION_INTERVAL = 1000;

    /**
     * The number of milliamps in a microamp.
     */
    public static final double MILLIAMPS_IN_MICROAMP = 1000;

    /**
     * The number of volts in a microwatt.
     */
    public static final double VOLTS_IN_MICROWATT = 1000000;

    /**
     * The minimum length of time between measurements of the same sensor. If two measurement
     * requests are made within this interval, the sensor should return a cached value instead of
     * reading from the file.
     */
    public static final double SENSOR_MINIMUM_MEASUREMENT_INTERVAL = 50;

    /**
     * The path to the current sensor.
     */
    public static final String SENSOR_CURRENT_NOW = "/sys/class/power_supply/battery/current_now";

    /**
     * The path to the voltage sensor
     */
    public static final String SENSOR_VOLTAGE_NOW = "/sys/class/power_supply/battery/voltage_now";
}
