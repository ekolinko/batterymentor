package com.batterymentor.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

import com.batterymentor.R;
import com.batterymentor.constants.Constants;
import com.batterymentor.constants.SensorConstants;

import java.util.HashSet;
import java.util.Set;

/**
 * Singleton class used for notifying listeners when a charger is connected or disconnected.
 */
public class ChargerManager {

    /**
     * The context associated with this charger manager.
     */
    private Context mContext;

    /**
     * The set of charger listeners.
     */
    private Set<ChargerListener> mChargerListeners = new HashSet<ChargerListener>();

    /**
     * The charger receiver.
     */
    private ChargerReceiver mChargerReceiver;

    /**
     * Flag indicating whether the charger receiver has been registered with android.
     */
    private boolean mChargerReceiverRegistered = false;

    /**
     * The battery service.
     */
    private BatteryManager mBatteryManager;

    /**
     * The current battery level.
     */
    private int mBatteryLevel = Constants.INVALID_VALUE;

    /**
     * The current battery voltage.
     */
    private int mBatteryVoltage = Constants.INVALID_VALUE;

    /**
     * Flag indicating whether the device is charging.
     */
    private boolean mCharging = false;

    /**
     * The current charging status as a string.
     */
    private String mChargingStatus;

    /**
     * The current battery temperature in celsius.
     */
    private float mBatteryTemperature;



    private static class SingletonHolder {
        private static final ChargerManager INSTANCE = new ChargerManager();
    }

    public static ChargerManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private ChargerManager() {
    }

    /**
     * Initialize the charger manager.
     */
    public void initialize(Context context) {
        if (!mChargerReceiverRegistered) {
            mContext = context;
            mBatteryManager = (BatteryManager) mContext.getSystemService(Context.BATTERY_SERVICE);
            IntentFilter batteryChangedFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            mChargerReceiver = new ChargerReceiver();
            Intent batteryStatusIntent = mContext.registerReceiver(mChargerReceiver, batteryChangedFilter);
            handleBatteryStatusIntent(context, batteryStatusIntent);
            mChargerReceiverRegistered = true;
        }
    }

    /**
     * Register a {@link ChargerListener} to listen to charger events.
     *
     * @param chargerListener the listener to register.
     */
    public void registerChargerListener(Context context, ChargerListener chargerListener) {
        if (chargerListener == null)
            return;

        synchronized (mChargerListeners) {
            if (!mChargerListeners.contains(chargerListener)) {
                mChargerListeners.add(chargerListener);
            }
        }

        if (!mChargerReceiverRegistered) {
            mContext = context;
            mBatteryManager = (BatteryManager) mContext.getSystemService(Context.BATTERY_SERVICE);
            IntentFilter batteryChangedFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            mChargerReceiver = new ChargerReceiver();
            Intent batteryStatusIntent = mContext.registerReceiver(mChargerReceiver, batteryChangedFilter);
            handleBatteryStatusIntent(context, batteryStatusIntent);
        }
    }

    /**
     * Unregister a {@link ChargerListener} from listening to charger events.
     *
     * @param chargerListener the listener to unregister.
     */
    public void unregisterChargerListener(Context context, ChargerListener chargerListener) {
        if (chargerListener == null)
            return;

        synchronized (mChargerListeners) {
            mChargerListeners.remove(chargerListener);
        }

        if (mChargerReceiverRegistered && mChargerListeners.isEmpty()) {
            context.unregisterReceiver(mChargerReceiver);
            mChargerReceiverRegistered = false;
        }
    }

    /**
     * Unregister all charger listeners and unregister the charger receiver.
     */
    public void unregisterAllChargerListeners(Context context) {
        synchronized (mChargerListeners) {
            mChargerListeners.clear();
        }

        if (mChargerReceiverRegistered) {
            context.unregisterReceiver(mChargerReceiver);
            mChargerReceiverRegistered = false;
        }
    }

    /**
     * Handle a battery status intent. Notify listeners if a charger is connected or disconnected.
     *
     * @param context the context of the application.
     * @param batteryStatusIntent the battery status intent to handle.
     */
    public void handleBatteryStatusIntent(Context context, Intent batteryStatusIntent) {
        int status = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        mBatteryLevel = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        mBatteryVoltage = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        mCharging = isCharging;
        if (isCharging) {
            notifyAllListenersOfChargerConnected();
            mChargingStatus = mContext.getString(R.string.charging);
        } else {
            notifyAllListenersOfChargerDisconnected();
            mChargingStatus = mContext.getString(R.string.discharging);
        }
        mBatteryTemperature = ((float)batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / SensorConstants.BATTERY_TEMPERATURE_CONVERSION_FACTOR;
        notifyAllListenersOfBatteryLevelChanged(mBatteryLevel);
    }

    /**
     * Notify all registered listeners of a charger connected event.
     */
    public void notifyAllListenersOfChargerConnected() {
        synchronized (mChargerListeners) {
            for (ChargerListener ChargerListener : mChargerListeners) {
                ChargerListener.onChargerConnected();
            }
        }
    }

    /**
     * Notify all registered listeners of a charger disconnected event.
     */
    public void notifyAllListenersOfChargerDisconnected() {
        synchronized (mChargerListeners) {
            for (ChargerListener ChargerListener : mChargerListeners) {
                ChargerListener.onChargerDisconnected();
            }
        }
    }

    /**
     * Notify all registered listeners of a battery level changed event.
     */
    public void notifyAllListenersOfBatteryLevelChanged(int level) {
        synchronized (mChargerListeners) {
            for (ChargerListener ChargerListener : mChargerListeners) {
                ChargerListener.onBatteryLevelChanged(level);
            }
        }
    }

    public int getBatteryLevel() {
        return mBatteryLevel;
    }

    public int getBatteryCurrentNow() {
        if (mBatteryManager != null && Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            return mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
        }
        return Constants.INVALID_VALUE;
    }

    public int getBatteryVoltage() {
        return mBatteryVoltage;
    }

    public boolean isCharging() {
        return mCharging;
    }

    public String getChargingStatus() {
        return mChargingStatus;
    }

    public float getBatteryTemperature() {
        return mBatteryTemperature;
    }

    /**
     * Interface used to notify listeners when a charger is connected or disconnected.
     */
    public interface ChargerListener {

        /**
         * Used to notify listeners when the charger is connected.
         */
        public void onChargerConnected();

        /**
         * Used to notify listeners when a charger is disconnected.
         */
        public void onChargerDisconnected();

        /**
         * Used ot notify listeners when the battery level changes.
         */
        public void onBatteryLevelChanged(int level);
    }

    /**
     * Receiver used to listen for charger connected and disconnected events.
     */
    public class ChargerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleBatteryStatusIntent(context, intent);
        }
    }
}
