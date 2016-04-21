package com.powerbench.model;

import android.content.Context;

import com.powerbench.constants.Constants;
import com.powerbench.constants.DeviceConstants;
import com.powerbench.constants.ModelConstants;
import com.powerbench.constants.SensorConstants;
import com.powerbench.device.Device;

import java.util.HashSet;
import java.util.Set;

/**
 * Class that represents a battery model.
 */
public class BatteryModel {

    /**
     * The predicted battery life.
     */
    private Double mBatteryLife;

    /**
     * The last measured absolute power.
     */
    private Double mPower;

    /**
     * The base power that is not covered by any of the models.
     */
    private Double mBasePower;

    /**
     * The battery capacity;
     */
    private double mBatteryCapacity = DeviceConstants.BATTERY_CAPACITY_DEFAULT;

    /**
     * The current battery level.
     */
    private int mBatteryLevel = SensorConstants.BATTERY_LEVEL_FULL;

    /**
     * The screen brightness model.
     */
    private Model mScreenModel;

    /**
     * The cpu model.
     */
    private Model mCpuModel;

    /**
     * The screen brightness.
     */
    private Integer mScreenBrightness;

    /**
     * The device cpu.
     */
    private Double mCpu;

    /**
     * Flag indicating whether the device is charging.
     */
    private boolean mCharging = false;

    /**
     * The set of charger listeners.
     */
    private Set<OnModelChangedListener> mModelChangedListeners = new HashSet<OnModelChangedListener>();

    public BatteryModel(Context context) {
        mBatteryCapacity = Device.getInstance().getBatteryCapacity(context);
    }

    public double getBatteryLife() {
        return mBatteryLife;
    }

    public void updateModel() {
        double sensorBasePower = (mPower != null) ? mPower : Constants.INVALID_VALUE;
        double modelBasePower = 0;
        int numModels = 0;
        if (mScreenModel != null) {
            double screenBasePower = mScreenModel.getIntercept();
            if (mScreenBrightness != null) {
                sensorBasePower -= mScreenModel.getY(mScreenBrightness);
            }
            modelBasePower += screenBasePower;
            numModels++;
        }
        if (mCpuModel != null) {
            double cpuBasePower = mScreenModel.getIntercept();
            if (mCpu != null) {
                sensorBasePower -= mCpuModel.getY(mCpu);
            }
            modelBasePower += cpuBasePower;
            numModels++;
        }
        if (numModels > 0 && !mCharging) {
            modelBasePower /= numModels;
            if (mPower == null) {
                mBasePower = modelBasePower;
            } else {
                if (mBasePower == null) {
                    double stableThreshold = sensorBasePower * ModelConstants.STABLE_THRESHOLD;
                    if (Math.abs(sensorBasePower - modelBasePower) > stableThreshold) {
                        mBasePower = sensorBasePower;
                    } else {
                        mBasePower = modelBasePower;
                    }
                } else {
                    double stableThreshold = mBasePower * ModelConstants.STABLE_THRESHOLD;
                    if (Math.abs(sensorBasePower - mBasePower) > stableThreshold) {
                        mBasePower = sensorBasePower;
                    }
                }
            }
        } else if (mBasePower == null) {
            mBasePower = sensorBasePower;
        } else {
            double stableThreshold = mBasePower * ModelConstants.STABLE_THRESHOLD;
            if (Math.abs(sensorBasePower - mBasePower) > stableThreshold) {
                mBasePower = sensorBasePower;
            }
        }

        int batteryLevel = (!mCharging) ? mBatteryLevel : SensorConstants.BATTERY_LEVEL_FULL - mBatteryLevel;
        double power = mBasePower;
        if (mScreenModel != null) {
            if (!mCharging && mScreenBrightness != null) {
                power += mScreenModel.getY(mScreenBrightness);
            }
        }
        mBatteryLife = (mBatteryCapacity * batteryLevel) / (SensorConstants.BATTERY_LEVEL_FULL * power);
        notifyAllListenersOfModelChanged();
    }

    public void setPower(double power) {
        mPower = power;
        updateModel();
    }

    public void setBatteryLevel(int batteryLevel) {
        mBatteryLevel = batteryLevel;
        updateModel();
    }

    public void setScreenModel(Model screenModel) {
        mScreenModel = screenModel;
        mBasePower = null;
        updateModel();
    }

    public void setCpuModel(Model cpuModel) {
        mCpuModel = cpuModel;
        mBasePower = null;
        updateModel();
    }

    public Model getScreenModel() {
        return mScreenModel;
    }

    public Model getCpuModel() {
        return mCpuModel;
    }

    public void setScreenBrightness(int screenBrightness) {
        mScreenBrightness = screenBrightness;
        updateModel();
    }

    public void setCharging(boolean isCharging) {
        mCharging = isCharging;
        updateModel();
    }

    /**
     * Register a {@link OnModelChangedListener} to listen to charger events.
     *
     * @param onModelChangedListener the listener to register.
     */
    public void registerOnModelChangedListener(OnModelChangedListener onModelChangedListener) {
        if (onModelChangedListener == null)
            return;

        synchronized (mModelChangedListeners) {
            mModelChangedListeners.add(onModelChangedListener);
        }
    }

    /**
     * Unregister a {@link OnModelChangedListener} to listen to charger events.
     *
     * @param onModelChangedListener the listener to unregister.
     */
    public void unregisterOnModelChangedListener(OnModelChangedListener onModelChangedListener) {
        if (onModelChangedListener == null)
            return;

        synchronized (mModelChangedListeners) {
            mModelChangedListeners.remove(onModelChangedListener);
        }
    }

    /**
     * Notify all registered listeners of a model changed event.
     */
    public void notifyAllListenersOfModelChanged() {
        synchronized (mModelChangedListeners) {
            for (OnModelChangedListener onModelChangedListener : mModelChangedListeners) {
                onModelChangedListener.onModelChanged();
            }
        }
    }

    /**
     * Interface used to notify listeners when this model changes.
     */
    public interface OnModelChangedListener {

        /**
         * Used to notify listeners when this model changes.
         */
        void onModelChanged();
    }
}
