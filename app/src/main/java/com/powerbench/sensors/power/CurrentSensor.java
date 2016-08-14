package com.powerbench.sensors.power;

import com.powerbench.constants.SensorConstants;
import com.powerbench.device.Device;
import com.powerbench.sensors.ChargerManager;
import com.powerbench.sensors.Sensor;

import java.io.File;

/**
 * The sensor responsible or reading current from the system.
 */
public class CurrentSensor extends Sensor {

    /**
     * The conversion factor for this device.
     */
    private double mConversionFactor;

    /**
     * Initialize the sensor.
     */
    public void initialize() {
        mConversionFactor = Device.getInstance().getCurrentConversionFactor();
    }

    @Override
    public String initFilename() {
        return (new File(SensorConstants.SENSOR_CURRENT_NOW).exists()) ?
                SensorConstants.SENSOR_CURRENT_NOW : null;
    }

    public double measure() {
        return measureValue(mConversionFactor);
    }

    public double measureValueAlternate() {
        return ChargerManager.getInstance().getBatteryCurrentNow() / mConversionFactor;
    }
}
