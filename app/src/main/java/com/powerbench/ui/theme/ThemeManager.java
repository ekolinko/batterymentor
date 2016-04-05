package com.powerbench.ui.theme;

import android.content.Context;

/**
 * Class used for to manager the current theme and all other themes available to this application.
 */
public class ThemeManager {

    /**
     * The current theme.
     */
    private Theme mTheme = Theme.BATTERY_THEME;

    private static class SingletonHolder {
        private static final ThemeManager INSTANCE = new ThemeManager();
    }

    public static ThemeManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private ThemeManager() {
    }

    /**
     * Get the current application theme.
     *
     * @param context the context of the application.
     * @return the current application theme.
     */
    public Theme getCurrentTheme(Context context) {
        return mTheme;
    }

    /**
     * Set the current application theme.
     *
     * @param context the context of the application.
     * @param theme the theme to set as the current theme.
     */
    public void setCurrentTheme(Context context, Theme theme) {
        if (theme != null) {
            mTheme = theme;
        }
    }
}
