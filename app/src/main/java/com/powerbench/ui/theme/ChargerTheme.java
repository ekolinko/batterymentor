package com.powerbench.ui.theme;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.powerbench.R;

/**
 * The classic blue battery theme for this application.
 */
public class ChargerTheme extends Theme {

    @Override
    public int initializeColorResource() {
        return R.color.powerbench_red;
    }

    @Override
    public int initializeActionBarColorResource() {
        return R.color.powerbench_red_dark;
    }

    @Override
    public int initializeTabDrawableResource() {
        return R.drawable.tab_material_red;
    }

    @Override
    public int initializeTabTextColorResource() {
        return R.color.tab_material_red;
    }

    @Override
    public int initializeScreenTestResource() {
        return R.drawable.remove_icon_screen_test_red;
    }

    public int initializeScreenSunResource() {
        return R.drawable.screen_sun_red;
    }

    public int initializeScreenRaysResource() {
        return R.drawable.screen_rays_red;
    }

    @Override
    public int initializeButtonResource() {
        return R.drawable.button_material_red;
    }
}
