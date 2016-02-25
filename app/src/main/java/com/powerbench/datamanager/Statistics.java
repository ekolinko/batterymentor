package com.powerbench.datamanager;

import com.powerbench.constants.DataConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Class representing statistics for a specified set of data. Manages the mean, median, spike
 * elimination, and other statistical aspects of the data.
 */
public class Statistics {

    /**
     * The global average of the data.
     */
    private double mAverage;

    /**
     * The global median of the data.
     */
    private double mMedian;

    /**
     * Flag indicating that the median has already been calculated and no data has come in to
     * change it.
     */
    private boolean mMedianCalculated = false;

    /**
     * The linked list of the most recent data points used for statistic calculation.
     */
    private LinkedList<Point> mRecentData = new LinkedList<Point>();

    /**
     * Add a point to the statistics. Remove the earliest point if the size of the queue
     * is about the threshold.
     *
     * @param point the point to add to the statistics.
     */
    public void addPoint(Point point) {
        synchronized (mRecentData) {
            mRecentData.add(point);
            if (mRecentData.size() > DataConstants.STATISTICS_RECENT_DATA_MAX_QUEUE_SIZE) {
                mRecentData.removeFirst();
            }
            mMedianCalculated = false;
        }
    }

    /**
     * Calculate the median of the recent data.
     *
     * @return the median of the recent data.
     */
    public double getMedian() {
        synchronized (mRecentData) {
            if (!mMedianCalculated) {
                ArrayList<Point> sortedRecentData = new ArrayList<Point>(mRecentData);
                Collections.sort(sortedRecentData);
                int size = sortedRecentData.size();
                if (size > 0) {
                    int nonSpikeMidpoint = (int) (size * (1 - DataConstants.STATISTICS_PERCENT_OF_SPIKES_TO_DROP) / 2);
                    mMedian = sortedRecentData.get(nonSpikeMidpoint).getValue();
                } else {
                    mMedian = 0;
                }
                mMedianCalculated = true;
            }
        }
        return mMedian;
    }

    /**
     * Clear the recent data.
     */
    public void clearRecentData() {
        synchronized (mRecentData) {
            mRecentData.clear();
        }
    }
}
