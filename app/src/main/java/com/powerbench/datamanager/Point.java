package com.powerbench.datamanager;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Class representing a point with a timestamp and a value.
 */
public class Point implements Serializable, Comparable<Point> {

    /**
     * The timestamp of the point.
     */
    private double mTimestamp;

    /**
     * The value of the point.
     */
    private double mValue;

    public Point(double timestamp, double value) {
        mTimestamp = timestamp;
        mValue = value;
    }

    public double getTimestamp() {
        return mTimestamp;
    }

    public double getValue() {
        return mValue;
    }

    @Override
    public int compareTo(Point another) {
        if (another == null)
            return -1;

        double otherValue = another.getValue();
        if (mValue < otherValue)
            return -1;
        if (mValue > otherValue)
            return 1;

        long thisBits = Double.doubleToLongBits(mValue);
        long otherBits = Double.doubleToLongBits(otherValue);

        return (thisBits == otherBits ?  0 : (thisBits < otherBits ? -1 : 1));
    }
}
