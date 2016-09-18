package com.batterymentor.datamanager;

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
     * Return the minimum of the data.
     *
     * @return the minimum of the data.
     */
    public double getMin();

    /**
     * Return the maximum of the data.
     *
     * @return the maximum of the data.
     */
    public double getMax();

    /**
     * Return the histogram data. Histogram data is a set of points with ranges associated with
     *
     * @return the histogram data.
     */
    public HistogramPoint[] getHistogramData();
}
