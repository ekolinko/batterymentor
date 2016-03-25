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
     * The global median of the data.
     */
    private double mMedian;

    /**
     * The global average of the data.
     */
    private double mAverage;

    /**
     * The total sum of the data.
     */
    private double mTotal;

    /**
     * The number of points.
     */
    private double mNumPoints;

    /**
     * The last value received by the statistics.
     */
    private double mValue;

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
            if (mRecentData.size() > DataConstants.STATISTICS_RECENT_DATA_MAX_QUEUE_SIZE) {
                mRecentData.removeFirst();
            }
            mMedianCalculated = false;
            mAverageCalculated = false;
            mValue = point.getY();
            mTotal += Math.abs(mValue);
            mNumPoints++;
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
        synchronized (mRecentData) {
            if (!mAverageCalculated) {
                mAverage = (mNumPoints > 0) ? mTotal / mNumPoints : 0;
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

    public double getValue() {
        return mValue;
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
