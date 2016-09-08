package com.powerbench.ui.theme;

import com.powerbench.R;

/**
 * The classic blue battery theme for this application.
 */
public class BatteryTheme extends Theme {

    @Override
    public int initializeStyleResource() {
        return R.style.battery_style;
    }

    @Override
    public int initializeDialogStyleResource() {
        return R.style.battery_style_alert_dialog;
    }

    @Override
    public int initializeColorResource() {
        return R.color.material_blue;
    }

    @Override
    public int initializeActionBarColorResource() {
        return R.color.material_blue_dark;
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
        return R.drawable.phone_blue;
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

    @Override
    public int initializeSemitransparentColorResource() {
        return R.color.material_blue_semitransparent;
    }

    @Override
    public int initializeProgressResource() {
        return R.drawable.progress_battery;
    }
}
