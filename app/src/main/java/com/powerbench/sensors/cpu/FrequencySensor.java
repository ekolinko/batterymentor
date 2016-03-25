package com.powerbench.sensors.cpu;

import com.powerbench.constants.SensorConstants;
import com.powerbench.device.Device;
import com.powerbench.sensors.Sensor;

/**
 * The sensor responsible for reading cpu frequency from the system.
 */
public class FrequencySensor extends Sensor {

    @Override
    public String initFilename() {
        return String.format(SensorConstants.SENSOR_CPU_FREQUENCY_TEMPLATE, 0);
    }

    public double measure() {
        double sum = 0;
        int numCores = Device.getInstance().getNumCores();
        for (int i = 0; i < Device.getInstance().getNumCores(); i++) {
            String filename = String.format(SensorConstants.SENSOR_CPU_FREQUENCY_TEMPLATE, i);
            double frequency =  measureValue(filename, SensorConstants.KILOHERTZ_IN_MEGAHERTZ);
            sum += frequency;
        }
        return (numCores > 0) ? sum / numCores : 0;
    }
}
