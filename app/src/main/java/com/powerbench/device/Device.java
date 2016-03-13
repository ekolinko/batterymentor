package com.powerbench.device;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.powerbench.constants.Constants;
import com.powerbench.constants.DeviceConstants;
import com.powerbench.sensors.Sensor;
import com.powerbench.ui.common.CommonActivity;

/**
 * Class that handles device information.
 */
public class Device {

    /**
     * The battery capacity.
     */
    private double mBatteryCapacity = Constants.INVALID_VALUE;

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
                mBatteryCapacity = capacityInMah * Sensor.VOLTAGE.measureValue();
            } catch (Exception e) {
                mBatteryCapacity = 0;
            }
        }
        return mBatteryCapacity;
    }
}
