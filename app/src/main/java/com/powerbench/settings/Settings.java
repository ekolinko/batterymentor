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
}
