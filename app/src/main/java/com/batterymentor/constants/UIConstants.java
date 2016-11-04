package com.batterymentor.constants;

/**
 * Class containing different constants used by the UI.
 */
public class UIConstants {

    /**
     * The threshold at which the hundreds rounding factor will take effect.
     */
    public static final int NOTIFICATION_ROUNDING_THRESHOLD_HUNDREDS = 1000;

    /**
     * The notification rounding factor for less than 1,000.
     */
    public static final int NOTIFICATION_ROUNDING_FACTOR_HUNDREDS = 10;

    /**
     * The threshold at which the thousands rounding factor will take effect.
     */
    public static final int NOTIFICATION_ROUNDING_THRESHOLD_THOUSANDS = 10000;

    /**
     * The notification rounding factor for less than 10,000.
     */
    public static final int NOTIFICATION_ROUNDING_FACTOR_THOUSANDS = 100;

    /**
     * The notification rounding factor for more than 10,000.
     */
    public static final int NOTIFICATION_ROUNDING_FACTOR_TENS_OF_THOUSANDS = 100;

    /**
     * The minimum power that is shown in the notification.
     */
    public static final int NOTIFICATION_MIN_POWER = 10;

    /**
     * The minimum current that is shown in the notification.
     */
    public static final int NOTIFICATION_MIN_CURRENT = 10;

    /**
     * The minimum battery life that is shown in the notification.
     */
    public static final int NOTIFICATION_MIN_BATTERY_LIFE = 1;

    /**
     * The maximum battery life that is shown in the notification.
     */
    public static final int NOTIFICATION_MAX_BATTERY_LIFE = 100;

    /**
     * The threshold at which the LED is shown as red.
     */
    public static final double LED_RED_THRESHOLD = 2;

    /**
     * The threshold at which the LED is shown as yellow.
     */
    public static final double LED_YELLOW_THRESHOLD = 1.5;

    /**
     * The label of the progress animation operation.
     */
    public static final String BENCHMARK_PROGRESS_ANIMATION_FIELD = "progress";

    /**
     * The notification scaling factor for battery life.
     */
    public static int NOTIFICATION_BATTERY_LIFE_SCALING_FACTOR = 6;

    /**
     * The key indicating that the modify system settings activity was closed.
     */
    public static final String MODIFY_SYSTEM_SETTINGS_CLOSED = "com.powerbench.modifySystemSettingsClosed";


    /**
     * The key indicating that the app should close.
     */
    public static final String CLOSE_APP = "com.powerbench.closeApp";

    /**
     * The sun view origin angle.
     */
    public static final int SUN_VIEW_ORIGIN_ANGLE = -90;

    /**
     * The sun view offset angle.
     */
    public static final int SUN_VIEW_OFFSET_ANGLE = 25;

    /**
     * The maximum threshold of the sun view max angle.
     */
    public static final int SUN_VIEW_MAX_ANGLE_THRESHOLD = (int)(UIConstants.SUN_VIEW_ORIGIN_ANGLE + Constants.CIRCLE_MAX_ANGLE);

    /**
     * The tutorial request code.
     */
    public static final int TUTORIAL_REQUEST_CODE = 1;

    /**
     * The tutorial refresh request code.
     */
    public static final int TUTORIAL_REFRESH_REQUEST_CODE = 2;

    /**
     * The show screen tab request code.
     */
    public static final int TAB_REQUEST_CODE = 3;

    /**
     * The number of histogram points used for drawing purposes.
     */
    public static final int HISTOGRAM_NUM_POINTS_FOR_DRAW = 80;

    /**
     * The buffer for the y-axis maximum that ensures that the data is not cut off.
     */
    public static final float HISTOGRAM_Y_AXIS_MAX_BUFFER = 1.25f;

    /**
     * The interval at which lifetime data should be updated.
     */
    public static final long LIFETIME_UPDATE_INTERVAL = 10*Constants.SECOND;

    /**
     * The maximum battery life / until fully charger to show in minutes
     */
    public static final double MAX_BATTERY_LIFE = 20*Constants.MINUTES_IN_HOUR;

    /**
     * The maximum battery life / until fully charger to show for the notification.
     */
    public static final double MAX_BATTERY_LIFE_NOTIFICATION = 10000;

    /**
     * The index of the power tab.
     */
    public static final int POWER_TAB_INDEX = 0;

    /**
     * The index of the screen tab.
     */
    public static final int SCREEN_TAB_INDEX = 1;

    /**
     * The power tab.
     */
    public static final String POWER_TAB = "power_tab";

    /**
     * The screen tab.
     */
    public static final String SCREEN_TAB = "screen_tab";
}
