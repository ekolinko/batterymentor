package com.powerbench.constants;

/**
 * Class containing different constants used by the data manager module.
 */
public class DataConstants {

    /**
     * The size of the statistics queue that contains the most recent data.
     */
    public static final int STATISTICS_RECENT_DATA_MAX_QUEUE_SIZE = 50;

    /**
     * The percent of the spikes to drop. (0.10 = drop 10% of spikes, 0.20 = drop 20% of spikes,
     * etc).
     */
    public static final double STATISTICS_PERCENT_OF_SPIKES_TO_DROP = 0.10;

}
