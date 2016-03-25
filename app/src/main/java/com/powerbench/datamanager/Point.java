package com.powerbench.datamanager;

import java.io.Serializable;

/**
 * Class representing a point with a timestamp and a value.
 */
public class Point implements Serializable, Comparable<Point> {

    /**
     * The timestamp of the point.
     */
    private double mX;

    /**
     * The value of the point.
     */
    private double mY;

    public Point(double timestamp, double value) {
        mX = timestamp;
        mY = value;
    }

    public void setX(double x) {
        mX = x;
    }

    public void setY(double y) {
        mY = y;
    }

    public double getX() {
        return mX;
    }

    public double getY() {
        return mY;
    }

    @Override
    public int compareTo(Point another) {
        if (another == null)
            return -1;

        double y = Math.abs(mY);
        double otherY = Math.abs(another.getY());
        if (y < otherY)
            return -1;
        if (y > otherY)
            return 1;

        long thisBits = Double.doubleToLongBits(y);
        long otherBits = Double.doubleToLongBits(otherY);

        return (thisBits == otherBits ?  0 : (thisBits < otherBits ? -1 : 1));
    }
}
