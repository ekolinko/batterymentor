package com.powerbench.model;

import android.content.Context;

import com.powerbench.constants.Constants;
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
    private double mBatteryLife = Constants.INVALID_VALUE;

    /**
     * The last measured absolute power.
     */
    private double mPower;

    /**
     * The base power that is not covered by any of the models.
     */
    private double mBasePower;

    /**
     * The battery capacity;
     */
    private double mBatteryCapacity;

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
    private int mScreenBrightness;

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
        mBasePower = mPower;
        int batteryLevel = (!mCharging) ? mBatteryLevel : SensorConstants.BATTERY_LEVEL_FULL - mBatteryLevel;
        double power = mBasePower;
        if (mScreenModel != null) {
            if (!mCharging) {
                power = mScreenModel.getIntercept() + mScreenModel.getY(mScreenBrightness);
            } else {
                power = mBasePower;
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
        updateModel();
    }

    public void setCpuModel(Model cpuModel) {
        mCpuModel = cpuModel;
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
