package com.powerbench.datamanager;

import java.util.ArrayList;

/**
 * Class that contains information that is used to draw a histogram. Also provides an interface
 * for accessing the average of the data.
 */
public interface Histogram {

    /**
     * Return the average of the data.
     *
     * @return the average of the data.
     */
    public double getAverage();

    /**
     * Return the histogram data. Histogram data is a set of points with ranges associated with
     *
     * @return the histogram data.
     */
    public HistogramPoint[] getHistogramData();
}
