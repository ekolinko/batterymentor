package com.powerbench.constants;

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
     * The set of device models used to configure this app.
     */
    public static class Model {
        public static final String NEXUS_6 = "Nexus 6";
    }

    /**
     * The set of power constants used for converting raw power numbers from the sensor to milliwatts.
     */
    public static class PowerFactor {
        public static final double DEFAULT = 1;
        public static final double NEXUS_6 = -1;
    }

}
