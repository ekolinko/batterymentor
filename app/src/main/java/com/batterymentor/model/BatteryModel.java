package com.batterymentor.model;

import android.content.Context;

import com.batterymentor.collectionmanager.CollectionManager;
import com.batterymentor.collectionmanager.LifetimeCollectionTask;
import com.batterymentor.constants.Constants;
import com.batterymentor.constants.ModelConstants;
import com.batterymentor.constants.SensorConstants;
import com.batterymentor.datamanager.RealtimeStatistics;
import com.batterymentor.datamanager.Statistics;
import com.batterymentor.device.Device;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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
     * The cpu frequency model.
     */
    private Model mCpuFrequencyModel;

    /**
     * The screen brightness.
     */
    private Integer mScreenBrightness;

    /**
     * Flag indicating whether the device is charging.
     */
    private boolean mCharging = false;

    /**
     * Flag indicating whether an update is required.
     */
    private boolean mForceUpdate = false;

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
     * The timer used to schedule update operations.
     */
    private Timer mTimer;

    /**
     * The set of charger listeners.
     */
    private Set<OnModelChangedListener> mModelChangedListeners = new HashSet<OnModelChangedListener>();

    public BatteryModel(Context context) {
        mContext = context;
    }

    public double getBatteryLife() {
        if (mBatteryLife == null)
            return Double.POSITIVE_INFINITY;

        return mBatteryLife;
    }

    /**
     * Check the timestamp to see if the model needs an update.
     *
     * @return true if the model needs updated, false otherwise.
     */
    public boolean needsUpdate() {
        return System.currentTimeMillis() > mNextUpdateTime;
    }

    public void updateModel() {
        if (needsUpdate() || mForceUpdate) {
            LifetimeCollectionTask powerCollectionTask = CollectionManager.getInstance().getPowerCollectionTask(mContext);
            Statistics lifetimeStatistics = powerCollectionTask.getLifetimeStatistics();
            mLifetimePower = lifetimeStatistics.getAverage();
            RealtimeStatistics realtimeStatistics = powerCollectionTask.getRealtimeStatistics();
            mRealtimePower = realtimeStatistics.getAverage();
            mRealtimeWeight = realtimeStatistics.getWeight();
            mRealtimeCounterweight = realtimeStatistics.getCounterweight();
            mLifetimeBatteryAverage = powerCollectionTask.getBatteryLifetimeStatistics().getAverage();
            mLifetimeChargerAverage = powerCollectionTask.getChargerLifetimeStatistics().getAverage();
            mForceUpdate = false;
            mNextUpdateTime = System.currentTimeMillis() + ((mCharging) ? ModelConstants.CHARGING_MODEL_UPDATE_INTERVAL : ModelConstants.BATTERY_MODEL_UPDATE_INTERVAL);
        }

        double power;
        if (mCharging) {
            power = mRealtimePower;
        } else {
            power = mRealtimePower * mRealtimeWeight + mLifetimePower * mRealtimeCounterweight;
        }
        Model screenModel = getScreenModel();
        if (screenModel != null && mScreenBrightness != null) {
            if (mCharging) {
                double screenBasePower = -(screenModel.getY(mScreenBrightness) - (mLifetimeBatteryAverage + mLifetimeChargerAverage)) + 0.0;
                double screenPower = screenModel.getY(mScreenBrightness);
                double realtimeBasePower = mRealtimePower;
                power = realtimeBasePower * mRealtimeWeight + screenBasePower * mRealtimeCounterweight - screenPower;
            } else {
                double screenBasePower = screenModel.getIntercept();
                double screenPower = screenModel.getY(mScreenBrightness);
                double realtimeBasePower = mRealtimePower - screenPower;
                power = realtimeBasePower * mRealtimeWeight + screenBasePower * mRealtimeCounterweight + screenPower;
            }
        }
        int batteryLevel = (!mCharging) ? mBatteryLevel : SensorConstants.BATTERY_LEVEL_FULL - mBatteryLevel;
        mBatteryLife = (Device.getInstance().getBatteryCapacity(mContext) * Constants.MINUTES_IN_HOUR * batteryLevel) / (SensorConstants.BATTERY_LEVEL_FULL * power);
        if (mBatteryLife.isInfinite())
            mNextUpdateTime = 0;
        if (mCharging && mRealtimePower < 0) {
            mBatteryLife = -1d;
        }
        notifyAllListenersOfModelChanged();
    }

    public void setPower(double power) {
        if (needsUpdate()) {
            updateModel();
        }
    }

    public void setBatteryLevel(int batteryLevel) {
        mBatteryLevel = batteryLevel;
        mForceUpdate = true;
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

    public void setCpuFrequencyModel(Model cpuFrequencyModel) {
        mCpuFrequencyModel = cpuFrequencyModel;
        updateModel();
    }

    public Model getScreenModel() {
        if (Device.getInstance().isBatteryPowerEstimated())
            return Model.ESTIMATED_SCREEN_MODEL;

        return mScreenModel;
    }

    public Model getCpuModel() {
        return mCpuModel;
    }

    public Model getCpuFrequencyModel() {
        return mCpuFrequencyModel;
    }

    public void setScreenBrightness(int screenBrightness) {
        mScreenBrightness = screenBrightness;
        updateModel();
    }

    public void setCharging(boolean isCharging) {
        mCharging = isCharging;
        mForceUpdate = true;
        updateModel();
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mTimer = null;
                mForceUpdate = true;
                updateModel();
            }
        }, ModelConstants.BATTERY_MODEL_CHARGER_STATUS_CHANGE_UPDATE_INTERVAL);
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
