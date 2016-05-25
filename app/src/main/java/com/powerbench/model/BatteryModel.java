package com.powerbench.model;

import android.content.Context;
import android.util.Log;

import com.powerbench.collectionmanager.CollectionManager;
import com.powerbench.collectionmanager.LifetimeCollectionTask;
import com.powerbench.constants.DeviceConstants;
import com.powerbench.constants.ModelConstants;
import com.powerbench.constants.SensorConstants;
import com.powerbench.datamanager.RealtimeStatistics;
import com.powerbench.datamanager.Statistics;
import com.powerbench.device.Device;

import java.util.HashSet;
import java.util.Set;

/**
 * Class that represents a battery model.
 */
public class BatteryModel {

    /**
     * The context associated with the app.
     */
    private Context mContext;

    /**
     * The predicted battery life.
     */
    private Double mBatteryLife;

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
     * The time at which the next model update can come.
     */
    private long mNextUpdateTime;

    /**
     * The power to be used for estimating battery life.
     */
    private double mLifetimePower;

    /**
     * The realtime power to be used for estimating battery life.
     */
    private double mRealtimePower;

    /**
     * The realtime average to be used for estimating battery life.
     */
    private double mRealtimeAverage;

    /**
     * The realtime weight to be used for estimating battery life.
     */
    private double mRealtimeWeight;

    /**
     * The realtime counterweight to be used for estimating battery life.
     */
    private double mRealtimeCounterweight;

    /**
     * The lifetime battery average used for estimating battery life.
     */
    private double mLifetimeBatteryAverage;

    /**
     * The realtime battery average used for estimating battery life.
     */
    private double mLifetimeChargerAverage;

    /**
     * The set of charger listeners.
     */
    private Set<OnModelChangedListener> mModelChangedListeners = new HashSet<OnModelChangedListener>();

    public BatteryModel(Context context) {
        mContext = context;
        mBatteryCapacity = Device.getInstance().getBatteryCapacity(context);
    }

    public double getBatteryLife() {
        return mBatteryLife;
    }

    /**
     * Check the timestamp to see if the model needs an update.
     *
     * @return true if the model needs updated, false otherwise.
     */
    public boolean needsUpdate() {
        long currentTime = System.currentTimeMillis();
        if (currentTime > mNextUpdateTime) {
            mNextUpdateTime = currentTime + ModelConstants.BATTERY_MODEL_UPDATE_INTERVAL;
            return true;
        }
        return false;
    }

    public void updateModel() {
        if (needsUpdate()) {
            LifetimeCollectionTask powerCollectionTask = CollectionManager.getInstance().getPowerCollectionTask(mContext);
            Statistics lifetimeStatistics = powerCollectionTask.getLifetimeStatistics();
            mLifetimePower = lifetimeStatistics.getAverage();
            RealtimeStatistics realtimeStatistics = powerCollectionTask.getRealtimeStatistics();
            mRealtimePower = realtimeStatistics.getAverage();
            mRealtimeWeight = realtimeStatistics.getWeight();
            mRealtimeCounterweight = realtimeStatistics.getCounterweight();
            mLifetimeBatteryAverage = powerCollectionTask.getBatteryLifetimeStatistics().getAverage();
            mLifetimeChargerAverage = powerCollectionTask.getChargerLifetimeStatistics().getAverage();
        }

        double power = mRealtimePower*mRealtimeWeight + mLifetimePower*mRealtimeCounterweight;
        if (mScreenModel != null && mScreenBrightness != null) {
            if (mCharging) {
                double screenBasePower = -(mScreenModel.getY(mScreenBrightness) - (mLifetimeBatteryAverage + mLifetimeChargerAverage)) + 0.0;
                double screenPower = mScreenModel.getY(mScreenBrightness);
                double realtimeBasePower = mRealtimePower;
                power = realtimeBasePower*mRealtimeWeight + screenBasePower*mRealtimeCounterweight - screenPower;
//                Log.d("tstatic","\t screenPower = " + screenPower);
//                Log.d("tstatic","\t screenBasePower = " + screenBasePower);
//                Log.d("tstatic","\t screenBaseWeight = " + realtimeCounterweight);
//                Log.d("tstatic","\t realtimeBasePower = " + realtimeBasePower);
//                Log.d("tstatic","\t realtimeWeight = " + realtimeWeight);
            } else {
                double screenBasePower = mScreenModel.getIntercept();
                double screenPower = mScreenModel.getY(mScreenBrightness);
                double realtimeBasePower = mRealtimePower - screenPower;
                power = realtimeBasePower*mRealtimeWeight + screenBasePower*mRealtimeCounterweight + screenPower;
            }
        }
        int batteryLevel = (!mCharging) ? mBatteryLevel : SensorConstants.BATTERY_LEVEL_FULL - mBatteryLevel;
        mBatteryLife = (mBatteryCapacity * batteryLevel) / (SensorConstants.BATTERY_LEVEL_FULL * power);
        if (mBatteryLife.isInfinite())
            mNextUpdateTime = 0;
        notifyAllListenersOfModelChanged();
    }

    public void setPower(double power) {
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
