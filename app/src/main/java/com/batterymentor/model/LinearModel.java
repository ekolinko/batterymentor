package com.batterymentor.model;

import com.batterymentor.datamanager.Point;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that represents a linear model.
 */
public class LinearModel extends Model implements Serializable {

    /**
     * The slope of the line in the model.
     */
    protected double mSlope;

    /**
     * The intercept of the line in the model.
     */
    protected double mIntercept;

    public LinearModel(double slope, double intercept) {
        mSlope = slope;
        mIntercept = intercept;
    }

    public LinearModel(ArrayList<Point> data) {
        super(data);
    }

    /**
     * Create a linear model using the simple linear regression formula on the specified data.
     *
     * @param data the data to use to create the model.
     */
    public void createModel(ArrayList<Point> data) {
        int N = data.size();
        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
        for (Point point : data) {
            double x = point.getX();
            double y = point.getY();
            sumx += x;
            sumx2 += x*x;
            sumy += y;
        }
        double xbar = sumx / N;
        double ybar = sumy / N;
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (Point point : data) {
            double x = point.getX();
            double y = point.getY();
            xxbar += (x - xbar) * (x - xbar);
            yybar += (y - ybar) * (y - ybar);
            xybar += (x - xbar) * (y - ybar);
        }
        mSlope  = xybar / xxbar;
        mIntercept = ybar - mSlope * xbar;
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

    /**
     * Return the y-intercept for this model.
     *
     * @return the y-intercept for this model.
     */
    public double getIntercept() {
        return mIntercept;
    }

    /**
     * Return the primary coefficient for this model.
     *
     * @return the primary coefficient for this model.
     */
    public double getFirstCoefficient() {
        return mSlope;
    }

    public double getSecondCoefficient() {
        return 0;
    }
}
