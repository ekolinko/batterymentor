package com.powerbench.sensors.power;

import com.powerbench.constants.SensorConstants;
import com.powerbench.sensors.Sensor;

import java.io.File;

/**
 * The sensor responsible or reading power from the system.
 */
public class PowerSensor extends Sensor {
    @Override
    public String initFilename() {
        return null;
    }

    @Override
    public boolean isSupported() {
        return CURRENT.isSupported() && VOLTAGE.isSupported();
    }

    public double measure() {
        return CURRENT.measure() * VOLTAGE.measure();
    }
}
