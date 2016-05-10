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
        LifetimeCollectionTask powerCollectionTask = CollectionManager.getInstance().getPowerCollectionTask(mContext);
        Statistics lifetimeStatistics = powerCollectionTask.getLifetimeStatistics();
        double lifetimePower = lifetimeStatistics.getAverage();
        double lifetimeWeight = lifetimeStatistics.getWeight();
        double lifetimeCounterweight = lifetimeStatistics.getCounterweight();
        RealtimeStatistics realtimeStatistics = powerCollectionTask.getRealtimeStatistics();
        double realtimePower = realtimeStatistics.getAverage();
        double realtimeWeight = realtimeStatistics.getWeight();
        double realtimeCounterweight = realtimeStatistics.getCounterweight();

        double power = realtimePower*realtimeWeight + lifetimePower*realtimeCounterweight;
        if (mScreenModel != null && mScreenBrightness != null) {
            if (mCharging) {
                double screenBasePower = Math.abs(mScreenModel.getY(mScreenBrightness) - (powerCollectionTask.getBatteryLifetimeStatistics().getAverage() + powerCollectionTask.getChargerLifetimeStatistics().getAverage()));
                double screenPower = mScreenModel.getY(mScreenBrightness);
                double realtimeBasePower = realtimePower;
                power = realtimeBasePower*realtimeWeight + screenBasePower*realtimeCounterweight - screenPower;
//                Log.d("tstatic","\t screenPower = " + screenPower);
//                Log.d("tstatic","\t screenBasePower = " + screenBasePower);
//                Log.d("tstatic","\t screenBaseWeight = " + realtimeCounterweight);
//                Log.d("tstatic","\t realtimeBasePower = " + realtimeBasePower);
//                Log.d("tstatic","\t realtimeWeight = " + realtimeWeight);
            } else {
                double screenBasePower = mScreenModel.getIntercept();
                double screenPower = mScreenModel.getY(mScreenBrightness);
                double realtimeBasePower = realtimePower - screenPower;
                power = realtimeBasePower*realtimeWeight + screenBasePower*realtimeCounterweight + screenPower;
            }
        }
        int batteryLevel = (!mCharging) ? mBatteryLevel : SensorConstants.BATTERY_LEVEL_FULL - mBatteryLevel;
        mBatteryLife = (mBatteryCapacity * batteryLevel) / (SensorConstants.BATTERY_LEVEL_FULL * power);
        notifyAllListenersOfModelChanged();
    }

    public void setPower(double power) {
        if (needsUpdate())
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
