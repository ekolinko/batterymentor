package com.powerbench.sensors.power;

import com.powerbench.sensors.Sensor;

/**
 * The sensor responsible for estimating the system power.
 */
public class PowerEstimationSensor extends Sensor {
    @Override
    public String initFilename() {
        return null;
    }

    @Override
    public boolean isSupported() {
        return LOAD_SENSOR.isSupported();
    }

    public double measure() {
        return CURRENT.measure() * VOLTAGE.measure();
    }
}
