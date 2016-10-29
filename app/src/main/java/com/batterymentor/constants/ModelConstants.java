package com.batterymentor.constants;

/**
 * Class containing different constants used by the model module.
 */
public class ModelConstants {

    /**
     * The filename of the screen model.
     */
    public static final String SCREEN_MODEL_FILENAME = "powerbench_screen_model";

    /**
     * The filename of the cpu model.
     */
    public static final String CPU_MODEL_FILENAME = "powerbench_cpu_model";

    /**
     * The filename of the cpu frequency model.
     */
    public static final String CPU_FREQUENCY_MODEL_FILENAME = "powerbench_cpu_frequency_model";

    /**
     * The threshold at which the difference between a newly measuring base power and the current
     * base power will not cause the current base power to change. If the newly measured base power
     * exceeds this threshold, the estimated base power will change the battery power estimation
     * will be affected.
     */
    public static final double STABLE_THRESHOLD = 0.20;

    /**
     * Interval at which the battery model needs to be updated.
     */
    public static final long BATTERY_MODEL_UPDATE_INTERVAL = 60*Constants.SECOND;

    /**
     * Interval at which the battery model should to be updated while charging.
     */
    public static final long CHARGING_MODEL_UPDATE_INTERVAL = 30*Constants.SECOND;

    /**
     * Interval at which the battery model needs to be updated when the charger status changes.
     */
    public static final long BATTERY_MODEL_CHARGER_STATUS_CHANGE_UPDATE_INTERVAL = 10*Constants.SECOND;
}
