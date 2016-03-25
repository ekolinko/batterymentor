package com.powerbench.sensors.power;

import com.powerbench.constants.SensorConstants;
import com.powerbench.sensors.Sensor;

import java.io.File;

/**
 * The sensor responsible or reading current from the system.
 */
public class CurrentSensor extends Sensor {

    @Override
    public String initFilename() {
        return (new File(SensorConstants.SENSOR_CURRENT_NOW).exists()) ?
                SensorConstants.SENSOR_CURRENT_NOW : null;
    }

    public double measure() {
        return measureValue(SensorConstants.MILLIAMPS_IN_MICROAMP);
    }
}
