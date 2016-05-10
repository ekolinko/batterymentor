package com.powerbench.constants;

/**
 * Class containing different constants used by the data manager module.
 */
public class DataConstants {

    /**
     * The size of the statistics queue that contains the most recent data.
     */
    public static final int STATISTICS_RECENT_DATA_MAX_SIZE = 50;

    /**
     * The percent of the spikes to drop. (0.10 = drop 10% of spikes, 0.20 = drop 20% of spikes,
     * etc).
     */
    public static final double STATISTICS_PERCENT_OF_SPIKES_TO_DROP = 0.10;

    /**
     * The number of points to be considered as part of the short term calculation.
     */
    public static final int STATISTICS_RECENT_DATA_SIZE = 5;

    /**
     * The number of buckets used for collecting histogram points.
     */
    public static final int HISTOGRAM_NUM_BUCKETS = 10;

    /**
     * The minimum power used for constructing the histogram.
     */
    public static final double HISTOGRAM_MIN_POWER = 0;

    /**
     * The maximum power used for constructing the histogram.
     */
    public static final double HISTOGRAM_MAX_POWER = 5000;

    /**
     * The range of each bucket in the histogram.
     */
    public static final double HISTOGRAM_BUCKET_RANGE = (HISTOGRAM_MAX_POWER - HISTOGRAM_MIN_POWER) / HISTOGRAM_NUM_BUCKETS;

    /**
     * The maximum weight of the realtime statistics. The weight is a number between 0 and 1 that
     * represents how much calculations should rely on the realtime statistics.
     */
    public static final double REALTIME_STATISTICS_MAX_WEIGHT = 0.25d;

    /**
     * The maximum weight of the lifetime statistics.  The weight is a number between 0 and 1 that
     * represents how much calculations should rely on the lifetime statistics.
     */
    public static final double LIFETIME_STATISTICS_MAX_WEIGHT = 1d;

    /**
     * The threshold of lifetime points at which maximum weight is achieved.
     */
    public static final double LIFETIME_NUM_POINTS_MAX_WEIGHT_THRESHOLD = 100;
}
