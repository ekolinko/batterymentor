package com.batterymentor.datamanager;

/**
 * Class representing a point with minimum and maximum x values and a y value.
 */
public class HistogramPoint extends Point {

    /**
     * The minimum x-value of the range.
     */
    private double mMinX;

    /**
     * The maximum x-value of the range.
     */
    private double mMaxX;

    public HistogramPoint(double minX, double maxX, double value) {
        super(minX, value);
        mMinX = minX;
        mMaxX = maxX;
    }

    public double getMinX() {
        return mMinX;
    }

    public double getMaxX() {
        return mMaxX;
    }
}
