package com.batterymentor.settings;

import android.content.Context;

import com.batterymentor.R;

/**
 * Singleton class used for storing the settings that are personalized to the user's personal tastes.
 */
public class Personalization {

    private static class SingletonHolder {
        private static final Personalization INSTANCE = new Personalization();
    }

    public static Personalization getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Personalization() {
    }

    public int getBatteryThemeColor(Context context) {
        return context.getResources().getColor(R.color.personalization_battery_default);
    }

    public int getChargerThemeColor(Context context) {
        return context.getResources().getColor(R.color.personalization_charger_default);
    }
}
