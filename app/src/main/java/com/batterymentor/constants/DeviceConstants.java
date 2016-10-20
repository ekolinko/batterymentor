package com.batterymentor.constants;

/**
 * Class containing different constants used by the device module.
 */
public class DeviceConstants {

    /**
     * The permissions to write to the device settings.
     */
    public static final int PERMISSIONS_WRITE_SETTINGS = 1;

    /**
     * The power profile class.
     */
    public static final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

    /**
     * The get average power method.
     */
    public static final String GET_AVERAGE_POWER_METHOD = "getAveragePower";

    /**
     * The battery capacity field.
     */
    public static final String BATTERY_CAPACITY_FIELD = "battery.capacity";

    /**
     * The default battery capacity.
     */
    public static final double BATTERY_CAPACITY_DEFAULT = 2400;

    /**
     * The number of readings used to determine if the battery sensor provides valid data.
     */
    public static final int BATTERY_TEST_NUM_POINTS = 10;

    /**
     * The number of readings included in the device information.
     */
    public static final int DEVICE_INFORMATION_BATTERY_TEST_NUM_POINTS = 5;

    /**
     * The amount of time between battery readings used to determine if the battery reading is
     * valid.
     */
    public static final int BATTERY_TEST_INTERVAL = 10;

    /**
     * The minimum threshold to check for when a device is charging.
     */
    public static final double BATTERY_TEST_CHARGING_MIN = -5000;

    /**
     * The maximum threshold to check for when a device is charging.
     */
    public static final double BATTERY_TEST_CHARGING_MAX = 10000;

    /**
     * The minimum threshold to check for when a device is charging.
     */
    public static final double BATTERY_TEST_BATTERY_MIN = 200;

    /**
     * The maximum threshold to check for when a device is charging.
     */
    public static final double BATTERY_TEST_BATTERY_MAX = 20000;

    /**
     * The string for samsung devices.
     */
    public static final String SAMSUNG = "Samsung";

    /**
     * The set of device models used to configure this app.
     */
    public static class Model {
        public static final String NEXUS_6 = "Nexus 6";
        public static final String NEXUS_7 = "Nexus 7";
    }

    /**
     * The set of power constants used for converting raw power numbers from the sensor to milliwatts.
     */
    public static class PowerFactor {
        public static final double DEFAULT = 1;
        public static final double NEXUS_6 = -1;
        public static final double NEXUS_7 = -0.001;
    }

}
