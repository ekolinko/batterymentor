package com.powerbench.ui.theme;

import android.content.Context;

import com.powerbench.R;

/**
 * Class representing an abstract color theme.
 */
public abstract class Theme {

    /**
     * The list of available themes.
     */
    public final static Theme BATTERY_THEME = new BatteryTheme();
    public final static Theme CHARGER_THEME = new ChargerTheme();

    /**
     * The main color resource.
     */
    private final int mColorResource;

    /**
     * The color resource of the action bar.
     */
    private final int mActionBarColorResource;

    /**
     * The drawable resource for the tabs.
     */
    private final int mTabDrawableResource;

    /**
     * The tab text color resource.
     */
    private final int mTabTextColorResource;

    /**
     * The screen test icon resource.
     */
    private final int mScreenTestIconResource;

    /**
     * The button resource.
     */
    private final int mButtonResource;

    public Theme() {
        mColorResource = initializeColorResource();
        mActionBarColorResource = initializeActionBarColorResource();
        mTabDrawableResource = initializeTabDrawableResource();
        mTabTextColorResource = initializeTabTextColorResource();
        mScreenTestIconResource = initializeScreenTestIconResource();
        mButtonResource = initializeButtonResource();
    }

    /**
     * Initialize the primary color of this theme.
     *
     * @return the primary color of this theme.
     */
    public int initializeColorResource() {
        return R.color.powerbench_blue;
    }

    /**
     * Initialize the action bar color of this theme.
     *
     * @return the action bar color of this theme.
     */
    public int initializeActionBarColorResource() {
        return R.color.powerbench_blue_dark;
    }

    /**
     * Initialize the tab drawable resource of this theme.
     *
     * @return the tab drawable resource of this theme.
     */
    public int initializeTabDrawableResource() {
        return R.drawable.tab_material_blue;
    }

    /**
     * Initialize the tab text color resource.
     *
     * @return the tab text color resource.
     */
    public int initializeTabTextColorResource() {
        return R.drawable.tab_material_blue;
    }

    /**
     * Initialize the screen test icon resource.
     *
     * @return the screen test icon resource.
     */
    public int initializeScreenTestIconResource() {
        return R.drawable.remove_icon_screen_test_blue;
    }

    /**
     * Initialize the button resource.
     *
     * @return the button resource.
     */
    public int initializeButtonResource() {
        return R.drawable.button_material_blue;
    }

    public int getColorResource() {
        return mColorResource;
    }

    public int getActionBarColorResource() {
        return mActionBarColorResource;
    }

    public int getTabDrawableResource() {
        return mTabDrawableResource;
    }

    public int getTabTextColorResource() {
        return mTabTextColorResource;
    }

    public int getScreenTestIconResource() {
        return mScreenTestIconResource;
    }

    public int getButtonResource() {
        return mButtonResource;
    }
}