package com.powerbench.constants;

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
     * The default brightness step.
     */
    public static final int BRIGHTNESS_STEP = 50;

    /**
     * The minimum brightness supported by the device.
     */
    public static final int MIN_BRIGHTNESS = 0;

    /**
     * The maximum brightness supported by the device.
     */
    public static final int MAX_BRIGHTNESS = 255;
}
