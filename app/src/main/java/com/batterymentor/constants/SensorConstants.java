package com.batterymentor.constants;

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
     * The number of hertz in a megahertz.
     */
    public static final double KILOHERTZ_IN_MEGAHERTZ = 1000;

    /**
     * The number of volts in a microwatt.
     */
    public static final double MICROVOLTS_IN_VOLT = 1000000;

    /**
     * The number of volts in a milliwatt.
     */
    public static final double MILLIVOLTS_IN_VOLT = 1000;

    /**
     * The minimum length of time between measurements of the same sensor. If two measurement
     * requests are made within this interval, the sensor should return a cached value instead of
     * reading from the file.
     */
    public static final double SENSOR_MINIMUM_MEASUREMENT_INTERVAL = 50;

    /**
     * The path to the list of running applications.
     */
    public static final String RUNNING_APPLICATIONS = "/proc/";

    /**
     * The path to the current sensor.
     */
    public static final String SENSOR_CURRENT_NOW = "/sys/class/power_supply/battery/current_now";

    /**
     * The path to the voltage sensor.
     */
    public static final String SENSOR_VOLTAGE_NOW = "/sys/class/power_supply/battery/voltage_now";

    /**
     * The path template to the cpu.
     */
    public static final String SENSOR_CPU_TEMPLATE = "/sys/devices/system/cpu/cpu%d";

    /**
     * The path template to the cpu frequency sensor.
     */
    public static final String SENSOR_CPU_FREQUENCY_TEMPLATE = "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq";

    /**
     * The path template to the cpu load sensor.
     */
    public static final String SENSOR_CPU_LOAD = "/proc/stat";

    /**
     * The path template to the app package name.
     */
    public static final String SENSOR_PROCESS_PACKAGE_NAME_TEMPLATE = "/proc/%d/cmdline";

    /**
     * The path template to the app cpu load sensor.
     */
    public static final String SENSOR_PROCESS_CPU_LOAD_TEMPLATE = "/proc/%d/stat";

    /**
     * The number of tokens to ignore for the process sensor during measurement.
     */
    public static final int SENSOR_PROCESS_NUM_TOKENS_TO_IGNORE = 13;

    /**
     * The number of tokens to read for the process sensor during measurement.
     */
    public static final int SENSOR_PROCESS_NUM_TOKENS_TO_READ = 4;

    /**
     * The percentage threshold at which an application is considered running.
     */
    public static final double APPLICATION_RUNNING_CPU_LOAD_THRESHOLD = 0.1;

    /**
     * The constant for a full battery level.
     */
    public static final int BATTERY_LEVEL_FULL = 100;

    /**
     * The filename where persistent battery lifetime statistics are saved.
     */
    public static final String LIFETIME_STATISTICS_BATTERY_FILENAME = "powerbench_battery_lifetime_statistics";

    /**
     * The filename where persistent charger lifetime statistics are saved.
     */
    public static final String LIFETIME_STATISTICS_CHARGER_FILENAME = "powerbench_charger_lifetime_statistics";

    /**
     * The interval at which data gets saved in a persistent collection task.
     */
    public static final long PERSISTENT_SAVE_INTERVAL = 30 * Constants.SECOND;

    /**
     * The conversion factor to convert the battery temperature from tenths of a celsius to a celsius.
     */
    public static final float BATTERY_TEMPERATURE_CONVERSION_FACTOR = 10;

    /**
     * The minimum power that can measured by the battery power sensor;
     */
    public static final double BATTERY_POWER_MIN = 300;
}
