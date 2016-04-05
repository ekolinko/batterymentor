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
    public int initializeScreenTestIconResource() {
        return R.drawable.remove_icon_screen_test_red;
    }

    @Override
    public int initializeButtonResource() {
        return R.drawable.button_material_red;
    }
}
