package com.powerbench.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.powerbench.constants.SettingsConstants;

/**
 * Singleton class used for storing and accessing the app settings.
 */
public class Settings {

    private static class SingletonHolder {
        private static final Settings INSTANCE = new Settings();
    }

    public static Settings getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Settings() {
    }

    /**
     * Set the show tutorial flag in shared preferences.
     *
     * @param context the application context.
     * @param showTutorial the flag to show the tutorial.
     */
    public void setShowTutorial(Context context, boolean showTutorial) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(SettingsConstants.SHOW_TUTORIAL, showTutorial).apply();
    }

    /**
     * Return true if the tutorial should be shown, false otherwise.
     *
     * @param context the application context
     * @return true if the tutorial should be shown, false otherwise.
     */
    public boolean getShowTutorial(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(SettingsConstants.SHOW_TUTORIAL, SettingsConstants.SHOW_TUTORIAL_DEFAULT_VALUE);
    }

    /**
     * Set the flag to launch the powerbench service on device bootup.
     *
     * @param context the application context.
     * @param launchOnDeviceBootup the flag to launch the powerbench service on device boot-up.
     */
    public void setLaunchOnDeviceBootup(Context context, boolean launchOnDeviceBootup) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(SettingsConstants.LAUNCH_ON_DEVICE_BOOTUP, launchOnDeviceBootup).apply();
    }

    /**
     * Return true if the powerbench should launch on device bootup, false otherwise.
     *
     * @param context the application context
     * @return true if the powerbench should launch on device bootup, false otherwise.
     */
    public boolean getLaunchOnDeviceBootup(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(SettingsConstants.LAUNCH_ON_DEVICE_BOOTUP, SettingsConstants.LAUNCH_ON_DEVICE_BOOTUP_DEFAULT_VALUE);
    }

    /**
     * Set the units shown in the status bar.
     *
     * @param context the application context.
     * @param statusBarUnits the units shown in the status bar.
     */
    public void setStatusBarUnits(Context context, String statusBarUnits) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(SettingsConstants.STATUS_BAR_UNITS, statusBarUnits).apply();
    }

    /**
     * Return the units shown in the status bar.
     *
     * @param context the application context
     * @return the units shown in the status bar.
     */
    public String getStatusBarUnits(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(SettingsConstants.STATUS_BAR_UNITS, SettingsConstants.STATUS_BAR_UNITS_DEFAULT);
    }

    /**
     * Set the units shown in the power tab.
     *
     * @param context the application context.
     * @param statusBarUnits the units shown in the power tab.
     */
    public void setPowerTabUnits(Context context, String statusBarUnits) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(SettingsConstants.POWER_TAB_UNITS, statusBarUnits).apply();
    }

    /**
     * Return the units shown in the power tab.
     *
     * @param context the application context
     * @return the units shown in the power tab.
     */
    public String getPowerTabUnits(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(SettingsConstants.POWER_TAB_UNITS, SettingsConstants.POWER_TAB_UNITS_DEFAULT);
    }
}
