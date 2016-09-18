package com.batterymentor.constants;

/**
 * Class containing different constants used by the brightness benchmark.
 */
public class BenchmarkConstants {

    /**
     * The default brightness duration step in milliseconds.
     */
    public static final long BRIGHTNESS_DURATION_STEP = 20 * Constants.SECOND;

    /**
     * The amount of time to wait for the brightness change to settle before starting the
     * measurement.
     */
    public static final long BRIGHTNESS_CHANGE_SETTLE_DURATION = 3 * Constants.SECOND;

    /**
     * The minimum brightness supported by the device.
     */
    public static final int MIN_BRIGHTNESS = 0;

    /**
     * The maximum brightness supported by the device.
     */
    public static final int MAX_BRIGHTNESS = 255;

    /**
     * The default brightness step.
     */
    public static final double BRIGHTNESS_STEP = (0.25)*(MAX_BRIGHTNESS - MIN_BRIGHTNESS);

    /**
     * The default cpu duration step in milliseconds.
     */
    public static final long CPU_DURATION_STEP = 20 * Constants.SECOND;

    /**
     * The amount of time to wait for the cpu change to settle before starting the measurement.
     */
    public static final long CPU_CHANGE_SETTLE_DURATION = 3 * Constants.SECOND;

    /**
     * The default cpu step.
     */
    public static final int CPU_STEP = 25;

    /**
     * The minimum cpu used by the benchmark.
     */
    public static final int MIN_CPU = 25;

    /**
     * The maximum cpu used by the benchmark.
     */
    public static final int MAX_CPU = 100;

    /**
     * The base duration for a benchmark that is added to the estimated duration.
     */
    public static final long BASE_DURATION = Constants.SECOND;

    /**
     * The default update interval of the benchmark countdown timer in milliseconds.
     */
    public static final long COUNTDOWN_TIMER_UPDATE_INTERVAL = 100;
}
