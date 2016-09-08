package com.powerbench.ui.theme;

import com.powerbench.R;

/**
 * The classic blue battery theme for this application.
 */
public class ChargerTheme extends Theme {

    @Override
    public int initializeStyleResource() {
        return R.style.charger_style;
    }

    @Override
    public int initializeDialogStyleResource() {
        return R.style.charger_style_alert_dialog;
    }

    @Override
    public int initializeColorResource() {
        return R.color.material_red;
    }

    @Override
    public int initializeActionBarColorResource() {
        return R.color.material_red_dark;
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
        return R.drawable.phone_red;
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

    @Override
    public int initializeSemitransparentColorResource() {
        return R.color.material_red_semitransparent;
    }

    @Override
    public int initializeProgressResource() {
        return R.drawable.progress_charger;
    }
}
