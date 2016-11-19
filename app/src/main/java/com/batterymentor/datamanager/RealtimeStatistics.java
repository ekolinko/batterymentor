package com.batterymentor.datamanager;

import com.batterymentor.constants.CollectionConstants;
import com.batterymentor.constants.DataConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Class representing statistics for a specified set of data. Manages the mean, median, spike
 * elimination, and other statistical aspects of the data.
 */
public class RealtimeStatistics extends Statistics {

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

    public RealtimeStatistics(boolean chargerStatistics) {
        super(chargerStatistics);
    }

    /**
     * Add a point to the statistics. Remove the earliest point if the size of the queue
     * is about the threshold.
     *
     * @param point the point to add to the statistics.
     */
    public void addPoint(Point point) {
        if (point == null || Double.isInfinite(point.getY()))
            return;

        synchronized (mRecentData) {
            mRecentData.add(point);
            if (mRecentData.size() > DataConstants.STATISTICS_RECENT_DATA_MAX_SIZE) {
                mRecentData.removeFirst();
            }
            mMedianCalculated = false;
            mAverageCalculated = false;
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
                    mMedian = convertValue(sortedRecentData.get(nonSpikeMidpoint).getY());
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
        if (!mChargerStatistics) {
            if (!isRealtimeDataReady() && mLifetimeStatistics != null)
                return mLifetimeStatistics.getAverage();

            synchronized (mRecentData) {
                if (!mAverageCalculated) {
                    mAverage = getFullRecentAverage();
                    mAverageCalculated = true;
                }
            }
        } else {
            mAverage = getShortRecentAverage();
        }
        return mAverage;
    }

    /**
     * Return the average using a short portion of recent data.
     */
    private double getShortRecentAverage() {
        if (mRecentData.isEmpty())
            return Double.POSITIVE_INFINITY;

        double recentAverage = 0;
        synchronized (mRecentData) {
            if (mRecentData.size() > 0) {
                int recentSize = Math.min(mRecentData.size(), DataConstants.STATISTICS_RECENT_DATA_SIZE) ;
                for (int i = mRecentData.size() - 1; i > mRecentData.size() - recentSize - 1; i--) {
                    double value = convertValue(mRecentData.get(i).getY());
                    recentAverage += value;
                }
                recentAverage = recentAverage / recentSize;
            }
        }
        return recentAverage;
    }

    /**
     * Return the average using a full portion of recent data.
     */
    public double getFullRecentAverage() {
        if (mRecentData.isEmpty())
            return Double.POSITIVE_INFINITY;

        double recentAverage = 0;
        synchronized (mRecentData) {
            if (mRecentData.size() > 0) {
                for (Point point : mRecentData) {
                    recentAverage += convertValue(point.getY());
                }
                recentAverage = recentAverage / mRecentData.size();
            }
        }
        return recentAverage;
    }

    /**
     * Return the absolute average over the entire statistics.
     *
     * @return the absolute average over the entire statistcs.
     */
    public double getAbsoluteAverage() {
        return super.getAverage();
    }

    /**
     * Return true if the realtime data is ready, false otherwise. Realtime data is deemed to be
     * ready if the number of points is above a certain threshold.
     *
     * @return true if the realtime data is ready, false otherwise
     */
    private boolean isRealtimeDataReady() {
        return getSize() > CollectionConstants.REALTIME_STATISTICS_VALID_POINT_THRESHOLD;
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

    @Override
    public HistogramPoint[] getHistogramData() {
        HistogramPoint[] histogramData = new HistogramPoint[DataConstants.HISTOGRAM_NUM_BUCKETS - 1];
        for (int i = 0; i < histogramData.length - 1; i++) {
            double minX = DataConstants.HISTOGRAM_MIN_POWER + i * DataConstants.HISTOGRAM_BUCKET_RANGE;
            double maxX = minX + DataConstants.HISTOGRAM_BUCKET_RANGE;
            histogramData[i] = new HistogramPoint(minX, maxX, 0);
        }
        double maxX = DataConstants.HISTOGRAM_MAX_POWER;
        histogramData[histogramData.length - 1] = new HistogramPoint(maxX, maxX, 0);
        synchronized (mRecentData) {
            for (Point point : mRecentData) {
                double value = convertValue(point.getY());
                int index = (int)((value - DataConstants.HISTOGRAM_MIN_POWER) / DataConstants.HISTOGRAM_BUCKET_RANGE);
                if (index < 0)
                    index = 0;
                else if (index >= histogramData.length)
                    index = histogramData.length - 1;

                histogramData[index].y++;
            }
        }
        return histogramData;
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

    @Override
    public void reset() {
        mAverageCalculated = false;
        mMedianCalculated = false;
        synchronized (mRecentData) {
            mRecentData.clear();
        }
        super.reset();
    }

    public void setLifetimeStatistics(Statistics lifetimeStatistics) {
        mLifetimeStatistics = lifetimeStatistics;
    }

    public Statistics getLifetimeStatistics() {
        return mLifetimeStatistics;
    }
}
