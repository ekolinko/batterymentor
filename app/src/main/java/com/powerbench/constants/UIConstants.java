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
     * The notification id key.
     */
    public static final String NOTIFICATION_ID_KEY = "com.powerbench.notificationId";
}
