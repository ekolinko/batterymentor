package com.powerbench.model;

import com.powerbench.datamanager.Point;

import java.util.ArrayList;

/**
 * Abstract class that represents a linear model that intercepts the origin.
 */
public class LinearModelAtOrigin extends LinearModel {

    public LinearModelAtOrigin(ArrayList<Point> data) {
        super(data);
    }

    /**
     * Return the modeled y-value for the specified x-value. Currently, the y-intercept is not
     * used for prediction.
     *
     * @param x the x-value for which to get the y-value.
     * @return the modeled y-value for the specified x-value.
     */
    public double getY(double x) {
        return mSlope*x;
    }
}
