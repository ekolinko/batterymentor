package com.powerbench.ui.theme;

import android.content.ContentResolver;
import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.powerbench.R;

/**
 * The classic blue battery theme for this application.
 */
public class BatteryTheme extends Theme {

    @Override
    public int initializeColorResource() {
        return R.color.powerbench_blue;
    }

    @Override
    public int initializeActionBarColorResource() {
        return R.color.powerbench_blue_dark;
    }

    @Override
    public int initializeTabDrawableResource() {
        return R.drawable.tab_material_blue;
    }

    @Override
    public int initializeTabTextColorResource() {
        return R.color.tab_material_blue;
    }

    @Override
    public int initializeScreenTestResource() {
        return R.drawable.remove_icon_screen_test_blue;
    }

    public int initializeScreenSunResource() {
        return R.drawable.screen_sun_blue;
    }

    public int initializeScreenRaysResource() {
        return R.drawable.screen_rays_blue;
    }

    @Override
    public int initializeButtonResource() {
        return R.drawable.button_material_blue;
    }
}
