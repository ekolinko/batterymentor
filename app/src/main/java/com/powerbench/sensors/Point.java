package com.powerbench.sensors;

import java.io.Serializable;

/**
 * Class representing a point with a timestamp and a value.
 */
public class Point implements Serializable {

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
}
