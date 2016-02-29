package com.powerbench.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Singleton class used for notifying listeners when a charger is connected or disconnected.
 */
public class ChargerManager {

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

    private static class SingletonHolder {
        private static final ChargerManager INSTANCE = new ChargerManager();
    }

    public static ChargerManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private ChargerManager() {
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
            mChargerListeners.add(chargerListener);
        }

        if (!mChargerReceiverRegistered) {
            IntentFilter batteryChangedFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            mChargerReceiver = new ChargerReceiver();
            Intent batteryStatusIntent = context.registerReceiver(mChargerReceiver, batteryChangedFilter);
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
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        if (isCharging) {
            notifyAllListenersOfChargerConnected();
        } else {
            notifyAllListenersOfChargerDisconnected();
        }
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
     * Notify all registered listeners of a charger discconnected event.
     */
    public void notifyAllListenersOfChargerDisconnected() {
        synchronized (mChargerListeners) {
            for (ChargerListener ChargerListener : mChargerListeners) {
                ChargerListener.onChargerDisconnected();
            }
        }
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
