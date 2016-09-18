package com.batterymentor.sensors.power;

import com.batterymentor.device.Device;
import com.batterymentor.sensors.Sensor;

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
        if (Device.getInstance().isBatteryPowerEstimated())
            return POWER_ESTIMATION_SENSOR.measure();
        else
            return CURRENT.measure() * VOLTAGE.measure();
    }

    public double measureForBatterySupport() {
        return CURRENT.measure() * VOLTAGE.measure();
    }
}
