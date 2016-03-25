package com.powerbench.datamanager;

import com.powerbench.constants.Constants;

/**
 * Class that represents a core load measurement, containing cpu usage and a total.
 */
public class LoadMeasurement {
    /**
     * The previous idle time.
     */
    private int mUsage = Constants.INVALID_VALUE;

    /**
     * The previous total.
     */
    private int mTotal = Constants.INVALID_VALUE;

    /**
     * Return true if the usage and total measurements are valid, false otherwise.
     *
     * @return true if the usage and total measurements are valid, false otherwise.
     */
    public boolean isValid() {
        return mUsage != Constants.INVALID_VALUE && mTotal != Constants.INVALID_VALUE;
    }

    public void setMeasurements(int usage, int total) {
        mUsage = usage;
        mTotal = total;
    }

    public void setUsage(int usage) {
        mUsage = usage;
    }

    public void setTotal(int total) {
        mTotal = total;
    }

    public int getUsage() {
        return mUsage;
    }

    public int getTotal() {
        return mTotal;
    }
}
