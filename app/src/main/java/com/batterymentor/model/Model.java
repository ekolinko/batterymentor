package com.batterymentor.model;

import com.batterymentor.datamanager.Point;

import java.util.ArrayList;

/**
 * Abstract class that creates a model using the specified data and can be used to predict unknown
 * behavior using the built model.
 */
public abstract class Model {

    /**
     * The estimated display model.
     */
    public static final LinearModel ESTIMATED_SCREEN_MODEL = new LinearModel(3.3343d, 1000.5571d);

    /**
     * Create a new model.
     */
    public Model() {
    }

    /**
     * Create a new model using the specified data.
     */
    public Model(ArrayList<Point> data) {
        createModel(data);
    }

    /**
     * Create a model using the specified data.
     *
     * @param data the data to use to create the model.
     */
    public abstract void createModel(ArrayList<Point> data);

    /**
     * Use the model to get the y-value for the specified x-value.
     *
     * @param x the x-value for which to get the y-value.
     */
    public abstract double getY(double x);

    /**
     * Return the y-intercept for this model.
     *
     * @return the y-intercept for this model.
     */
    public abstract double getIntercept();

    /**
     * Return the first coefficient for this model.
     *
     * @return the first coefficient for this model.
     */
    public abstract double getFirstCoefficient();

    /**
     * Return the second coefficient for this model.
     *
     * @return the second coefficient for this model.
     */
    public abstract double getSecondCoefficient();
}
