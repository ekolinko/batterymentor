package com.powerbench.datamanager;

import com.powerbench.constants.CollectionConstants;
import com.powerbench.constants.DataConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Class representing statistics for a specified set of data. Manages the mean, median, spike
 * elimination, and other statistical aspects of the data.
 */
public class RealtimeStatistics extends Statistics {

    /**
     * The current value of the data.
     */
    private double mValue;

    /**
     * The global median of the data.
     */
    private double mMedian;

    /**
     * The global average of the data.
     */
    private double mAverage;

    /**
     * Flag indicating that the median has already been calculated and no data has come in to
     * change it.
     */
    private boolean mMedianCalculated = false;

    /**
     * Flag indicating that the adjusted average has already been calculated and no data has come
     * int to change it.
     */
    private boolean mAverageCalculated = false;

    /**
     * The linked list of the most recent data points used for statistic calculation.
     */
    private LinkedList<Point> mRecentData = new LinkedList<Point>();

    /**
     * The lifetime statistics associated with this object.
     */
    public Statistics mLifetimeStatistics;

    public RealtimeStatistics() {
        super();
    }

    /**
     * Add a point to the statistics. Remove the earliest point if the size of the queue
     * is about the threshold.
     *
     * @param point the point to add to the statistics.
     */
    public void addPoint(Point point) {
        if (point == null)
            return;

        synchronized (mRecentData) {
            mRecentData.add(point);

            if (mRecentData.size() > DataConstants.STATISTICS_RECENT_DATA_MAX_SIZE) {
                removePointFromHistogram(mRecentData.removeFirst());
            }
            mMedianCalculated = false;
            mAverageCalculated = false;
            mValue = point.getY();
            super.addPoint(point);
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
                    mMedian = sortedRecentData.get(nonSpikeMidpoint).getY();
                    StringBuilder sb = new StringBuilder();
                    for (Point point : sortedRecentData) {
                        sb.append(Math.abs(point.getY()) + " ");
                    }
                } else {
                    mMedian = 0;
                }
                mMedianCalculated = true;
            }
        }
        return mMedian;
    }

    /**
     * Calculate the average of the recent data.
     *
     * @return the average of the recent data.
     */
    public double getAverage() {
        if (!isRealtimeDataReady() && mLifetimeStatistics != null)
            return mLifetimeStatistics.getAverage();

        synchronized (mRecentData) {
            if (!mAverageCalculated) {
                mAverage = super.getAverage();
                int recentSize = DataConstants.STATISTICS_RECENT_DATA_SIZE;
                if (mRecentData.size() > recentSize + 1) {
                    double previousShortAverage = 0;
                    for (int i = mRecentData.size() - 2; i > mRecentData.size() - recentSize - 2; i--) {
                        previousShortAverage += Math.abs(mRecentData.get(i).getY());
                    }
                    previousShortAverage /= recentSize;
                    double currentShortAverage = 0;
                    for (int i = mRecentData.size() - 1; i > mRecentData.size() - recentSize - 1; i--) {
                        currentShortAverage += Math.abs(mRecentData.get(i).getY());
                    }
                    currentShortAverage /= recentSize;
                    if (currentShortAverage > previousShortAverage) {
                        double variance = 0;
                        for (int i = mRecentData.size() - 1; i > mRecentData.size() - recentSize - 1; i--) {
                            double value = Math.abs(mRecentData.get(i).getY());
                            variance += Math.pow(value - currentShortAverage, 2);
                        }
                        double standardDeviation = Math.sqrt(variance / recentSize);
                        mAverage += standardDeviation / 2;
                    }
                    mAverageCalculated = true;
                }
            }
        }
        return mAverage;
    }

    /**
     * Return true if the realtime data is ready, false otherwise. Realtime data is deemed to be
     * ready if the number of points is above a certain threshold.
     *
     * @return true if the realtime data is ready, false otherwise
     */
    private boolean isRealtimeDataReady() {
        return getNumPoints() > CollectionConstants.REALTIME_STATISTICS_VALID_POINT_THRESHOLD;
    }


    /**
     * Return the size of the recent data.
     *
     * @return the size of the recent data.
     */
    public int getSize() {
        int size;
        synchronized (mRecentData) {
            size = mRecentData.size();
        }
        return size;
    }

    /**
     * Return the maximum size of the recent data.
     *
     * @return the maximum size of the recent data.
     */
    public int getMaximumSize() {
        return DataConstants.STATISTICS_RECENT_DATA_MAX_SIZE;
    }

    /**
     * Return the weight of this statistics instance. The weight is a number between 0 and 1 that
     * represents how much calculations should rely on this statistics instance and is dependent on
     * the size of the recent data.
     *
     * @return the weight of this statistics instance.
     */
    public double getWeight() {
        if (!isRealtimeDataReady())
            return 0d;

        return (getSize() / (double)(getMaximumSize()) * DataConstants.REALTIME_STATISTICS_MAX_WEIGHT);
    }

    public void setLifetimeStatistics(Statistics lifetimeStatistics) {
        mLifetimeStatistics = lifetimeStatistics;
    }

    public Statistics getLifetimeStatistics() {
        return mLifetimeStatistics;
    }

    public double getValue() {
        return mValue;
    }
}
