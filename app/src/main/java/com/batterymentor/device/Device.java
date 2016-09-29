package com.batterymentor.device;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.batterymentor.constants.Constants;
import com.batterymentor.constants.DeviceConstants;
import com.batterymentor.constants.SensorConstants;
import com.batterymentor.sensors.ChargerManager;
import com.batterymentor.sensors.Sensor;

import java.io.File;

/**
 * Class that handles device information.
 */
public class Device {

    public enum BatterySupport {
        DEVICE, ESTIMATE, UNKNOWN;
    }

    /**
     * The battery capacity.
     */
    private double mBatteryCapacity = Constants.INVALID_VALUE;

    /**
     * The number of cores.
     */
    public int mNumCores = Constants.INVALID_VALUE;

    /**
     * Flag indicating what type of battery support is provided by the device.
     */
    private BatterySupport mBatterySupport = BatterySupport.UNKNOWN;

    private static class SingletonHolder {
        private static final Device INSTANCE = new Device();
    }

    public static Device getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Device() {
    }

    /**
     * Return the version of this app.
     *
     * @return the version of this app.
     */
    public String getAppVersion(Context context) {
        String version = Constants.EMPTY_STRING;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return version;
    }

    /**
     * Return true if the power supported by the device is an estimate, false otherwise.
     *
     * @return true if the power supported by the device is an estimate.
     */
    public boolean isBatteryPowerEstimated() {
        if (mBatterySupport == BatterySupport.UNKNOWN) {
            mBatterySupport = checkBatterySupport();
        }

        return mBatterySupport == BatterySupport.ESTIMATE;
    }

    /**
     * Return the battery measurement that is supported on this device.
     *
     * @return the battery measurement that is supported on this device.
     */
    public BatterySupport checkBatterySupport() {
        String manufacturer = android.os.Build.MANUFACTURER;
        if (manufacturer.equalsIgnoreCase(DeviceConstants.SAMSUNG)) {
            return BatterySupport.ESTIMATE;
        }

        if (!Sensor.POWER.isSupported()) {
            return BatterySupport.ESTIMATE;
        }

        double total = 0;
        for (int i = 0; i < DeviceConstants.BATTERY_TEST_NUM_POINTS; i++) {
            total += Sensor.POWER.measureForBatterySupport();
            try {
                Thread.sleep(DeviceConstants.BATTERY_TEST_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        double average = total / DeviceConstants.BATTERY_TEST_NUM_POINTS;
        if (ChargerManager.getInstance().isCharging()) {
            return (average >= DeviceConstants.BATTERY_TEST_CHARGING_MIN && average <= DeviceConstants.BATTERY_TEST_CHARGING_MAX) ? BatterySupport.DEVICE : BatterySupport.ESTIMATE;
        } else {
            return (average >= DeviceConstants.BATTERY_TEST_BATTERY_MIN && average <= DeviceConstants.BATTERY_TEST_BATTERY_MAX) ? BatterySupport.DEVICE : BatterySupport.ESTIMATE;
        }
    }

    /**
     * Get the battery capacity of the device in milliwatt hours. Measure this value if it is
     * invalid.
     *
     * @param context the application context.
     * @return the battery capacity of the device in milliwatt hours.
     */
    public double getBatteryCapacity(Context context) {
        if (mBatteryCapacity < 0) {
            Object mPowerProfile = null;
            try {
                mPowerProfile = Class.forName(DeviceConstants.POWER_PROFILE_CLASS)
                        .getConstructor(Context.class).newInstance(context);
            } catch (Exception e) {
                mBatteryCapacity = 0;
            }
            try {
                double capacityInMah = (Double) Class
                        .forName(DeviceConstants.POWER_PROFILE_CLASS)
                        .getMethod(DeviceConstants.GET_AVERAGE_POWER_METHOD, java.lang.String.class)
                        .invoke(mPowerProfile, DeviceConstants.BATTERY_CAPACITY_FIELD);
                mBatteryCapacity = capacityInMah * Sensor.VOLTAGE.measure();
            } catch (Exception e) {
                mBatteryCapacity = 0;
            }
        }
        return mBatteryCapacity;
    }

    /**
     * Get the number of CPU cores.
     *
     * @return the number of CPU cores.
     */
    public int getNumCores() {
        if (mNumCores == Constants.INVALID_VALUE) {
            mNumCores = 0;
            while (new File(String.format(SensorConstants.SENSOR_CPU_TEMPLATE, mNumCores)).exists()) {
                mNumCores++;
            }
        }
        return mNumCores;
    }

    /**
     * Get the factor that should be used to convert the power reading from the sensor to milliwatts.
     */
    public double getCurrentConversionFactor() {
        double conversionFactor = DeviceConstants.PowerFactor.DEFAULT;

        if (Build.MODEL.equals(DeviceConstants.Model.NEXUS_6)) {
            conversionFactor = DeviceConstants.PowerFactor.NEXUS_6;
        }

        return conversionFactor * SensorConstants.MILLIAMPS_IN_MICROAMP;
    }
}
