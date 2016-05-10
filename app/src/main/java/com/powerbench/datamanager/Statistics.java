package com.powerbench.datamanager;

import com.powerbench.constants.Constants;
import com.powerbench.constants.DataConstants;

import java.io.Serializable;

/**
 * Class that contains the lifetime statistics about the application such as the average.
 */
public class Statistics implements Serializable, Histogram {

    /**
     * The total sum of the lifetime statistics. Used for calculating the average.
     */
    private double mTotal;

    /**
     * The number of points that have been collected. Used for calculating the average.
     */
    private double mNumPoints;

    /**
     * The histogram points associated with the statistics.
     */
    private HistogramPoint[] mHistogramData;

    public Statistics() {
        mTotal = 0;
        mNumPoints = 0;
        mHistogramData = new HistogramPoint[DataConstants.HISTOGRAM_NUM_BUCKETS - 1];
        for (int i = 0; i < mHistogramData.length - 1; i++) {
            double minX = DataConstants.HISTOGRAM_MIN_POWER + i * DataConstants.HISTOGRAM_BUCKET_RANGE;
            double maxX = minX + DataConstants.HISTOGRAM_BUCKET_RANGE;
            mHistogramData[i] = new HistogramPoint(minX, maxX, 0);
        }
        double maxX = DataConstants.HISTOGRAM_MIN_POWER;
        mHistogramData[mHistogramData.length - 1] = new HistogramPoint(maxX, maxX, 0);
    }

    /**
     * Add a point to the lifetime statistics. Add the y-value to the total and increment the number of
     * points.
     *
     * @param point the point to add to the usage data.
     */
    public void addPoint(Point point) {
        if (point == null)
            return;

        double value = point.getY();
        mTotal += Math.abs(value);
        mNumPoints++;

        addPointToHistogram(point);
    }

    /**
     * Return the lifetime average.
     *
     * @return the lifetime average.
     */
    public double getAverage() {
        if (mNumPoints > 0) {
            return mTotal / mNumPoints;
        }

        return 0;
    }

    /**
     * Add a point to the histogram.
     */
    public void addPointToHistogram(Point point) {
        int index = (int)((Math.abs(point.getY()) - DataConstants.HISTOGRAM_MIN_POWER) / DataConstants.HISTOGRAM_BUCKET_RANGE);
        if (index < 0)
            index = 0;
        else if (index >= mHistogramData.length)
            index = mHistogramData.length - 1;

        mHistogramData[index].y++;
    }

    /**
     * Remove a point from the histogram.
     */
    public void removePointFromHistogram(Point point) {
        int index = (int)((Math.abs(point.getY()) - DataConstants.HISTOGRAM_MIN_POWER) / DataConstants.HISTOGRAM_BUCKET_RANGE);
        if (index < 0)
            index = 0;
        else if (index >= mHistogramData.length)
            index = mHistogramData.length - 1;

        mHistogramData[index].y--;
    }

    /**
     * Return the weight of this statistics instance. The weight is a number between 0 and 1 that
     * represents how much calculations should rely on this statistics instance and is dependent on
     * the size of the recent data.
     *
     * @return the weight of this statistics instance.
     */
    public double getWeight() {
        double numPoints = getNumPoints();
        if (numPoints > DataConstants.LIFETIME_NUM_POINTS_MAX_WEIGHT_THRESHOLD)
            return DataConstants.LIFETIME_STATISTICS_MAX_WEIGHT;

        return (numPoints / DataConstants.LIFETIME_NUM_POINTS_MAX_WEIGHT_THRESHOLD) * DataConstants.LIFETIME_STATISTICS_MAX_WEIGHT;
    }

    /**
     * Return the counterweight of this statistics instance. The counterweight is a number between
     * 0 and 1 that represents how much calculations should rely on statistics other than these
     * realtime statistics.
     *
     * @return the counterweight of this statistics instance.
     */
    public double getCounterweight() {
        return 1d - getWeight();
    }

    @Override
    public HistogramPoint[] getHistogramData() {
        return mHistogramData;
    }

    public void setTotal(double total) {
        mTotal = total;
    }

    public void setNumPoints(double numPoints) {
        mNumPoints = numPoints;
    }

    public double getNumPoints() {
        return mNumPoints;
    }
}
