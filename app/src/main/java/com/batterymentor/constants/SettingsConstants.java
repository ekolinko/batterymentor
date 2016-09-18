package com.batterymentor.constants;

/**
 * Class containing the constants used by the settings module.
 */
public class SettingsConstants {

    /**
     * The show tutorial setting.
     */
    public static final String SHOW_TUTORIAL = "showTutorial";

    /**
     * The milliwatt unit.
     */
    public static final String UNITS_MILLIWATT = "mW";

    /**
     * The milliamp unit.
     */
    public static final String UNITS_MILLIAMP = "mA";

    /**
     * The battery life unit.
     */
    public static final String UNITS_BATTERY_LIFE = "h";

    /**
     * The default show tutorial value.
     */
    public static boolean SHOW_TUTORIAL_DEFAULT_VALUE = true;

    /**
     * The list of power tab units.
     */
    public static String[] STATUS_BAR_UNITS_OPTIONS = new String[] { UNITS_MILLIWATT, UNITS_MILLIAMP, UNITS_BATTERY_LIFE };

    /**
     * The list of power tab units.
     */
    public static String[] POWER_TAB_UNITS_OPTIONS = new String[] { UNITS_MILLIWATT, UNITS_MILLIAMP };

}
