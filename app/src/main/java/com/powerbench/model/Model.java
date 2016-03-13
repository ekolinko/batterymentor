package com.powerbench.model;

import com.powerbench.datamanager.Point;

import java.util.ArrayList;

/**
 * Abstract class that creates a model using the specified data and can be used to predict unknown
 * behavior using the built model.
 */
public abstract class Model {

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

}
