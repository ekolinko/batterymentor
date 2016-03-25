package com.powerbench.device;

import android.content.Context;

import com.powerbench.constants.Constants;
import com.powerbench.constants.DeviceConstants;
import com.powerbench.constants.SensorConstants;
import com.powerbench.sensors.Sensor;
import com.powerbench.ui.common.CommonActivity;

import java.io.File;

/**
 * Class that handles device information.
 */
public class Device {

    /**
     * The battery capacity.
     */
    private double mBatteryCapacity = Constants.INVALID_VALUE;

    /**
     * The number of cores.
     */
    public int mNumCores = Constants.INVALID_VALUE;

    private static class SingletonHolder {
        private static final Device INSTANCE = new Device();
    }

    public static Device getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Device() {
    }

    /**
     * Get the battery capacity of the device in milliwatt hours. Measure this value if it is
     * invalid.
     *
     * @param activity the activity containing the application context.
     * @return the battery capacity of the device in milliwatt hours.
     */
    public double getBatteryCapacity(CommonActivity activity) {
        if (mBatteryCapacity < 0) {
            Object mPowerProfile = null;
            try {
                mPowerProfile = Class.forName(DeviceConstants.POWER_PROFILE_CLASS)
                        .getConstructor(Context.class).newInstance(activity);
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
}
