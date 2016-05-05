package com.powerbench.constants;

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
     * The notification scaling factor for battery life.
     */
    public static int NOTIFICATION_BATTERY_LIFE_SCALING_FACTOR = 10;

    /**
     * The notification id key.
     */
    public static final String NOTIFICATION_ID_KEY = "com.powerbench.notificationId";

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
     * The number of histogram points used for drawing purposes.
     */
    public static final int HISTOGRAM_NUM_POINTS_FOR_DRAW = 80;

    /**
     * The buffer for the y-axis maximum that ensures that the data is not cut off.
     */
    public static final float HISTOGRAM_Y_AXIS_MAX_BUFFER = 1.25f;
}
