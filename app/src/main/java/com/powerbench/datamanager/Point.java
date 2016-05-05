package com.powerbench.datamanager;

import java.io.Serializable;

/**
 * Class representing a point with x and y values.
 */
public class Point implements Serializable, Comparable<Point> {

    /**
     * The x-value of the point.
     */
    public double x;

    /**
     * The value of the point.
     */
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public int compareTo(Point another) {
        if (another == null)
            return -1;

        double y = Math.abs(this.y);
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
