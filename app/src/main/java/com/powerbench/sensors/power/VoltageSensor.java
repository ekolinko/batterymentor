package com.powerbench.sensors.power;

import com.powerbench.constants.SensorConstants;
import com.powerbench.sensors.ChargerManager;
import com.powerbench.sensors.Sensor;

import java.io.File;

/**
 * The sensor responsible or reading voltage from the system.
 */
public class VoltageSensor extends Sensor {
    @Override
     public String initFilename() {
        return (new File(SensorConstants.SENSOR_VOLTAGE_NOW).exists()) ?
                SensorConstants.SENSOR_VOLTAGE_NOW : null;
    }

    @Override
    public boolean isSupported() {
        return getFilename() != null;
    }

    public double measure() {
        return measureValue(SensorConstants.VOLTS_IN_MICROWATT);
    }

    public double measureValueAlternate() {
        return ChargerManager.getInstance().getBatteryVoltage() / SensorConstants.VOLTS_IN_MILLIWATT;
    }
}
