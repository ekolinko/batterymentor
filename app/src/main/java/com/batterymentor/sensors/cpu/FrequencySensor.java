package com.batterymentor.sensors.cpu;

import com.batterymentor.constants.SensorConstants;
import com.batterymentor.device.Device;
import com.batterymentor.sensors.Sensor;

/**
 * The sensor responsible for reading cpu frequency from the system.
 */
public class FrequencySensor extends Sensor {

    @Override
    public String initFilename() {
        return String.format(SensorConstants.SENSOR_CPU_FREQUENCY_TEMPLATE, 0);
    }

    /**
     * Measure the frequency sensor. Calculate the frequency for each core and return the average
     * frequency across all cores.
     *
     * @return the average frequency across all four cores.
     */
    public double measure() {
        double sum = 0;
        int numCores = Device.getInstance().getNumCores();
        for (int i = 0; i < numCores; i++) {
            double frequency = measureCore(i);
            sum += frequency;
        }
        return (numCores > 0) ? sum / numCores : 0;
    }

    /**
     * Measure the frequency for the specified core.
     *
     * @param core the core to measure.
     * @return the frequency for the specified core.
     */
    public double measureCore(int core) {
        String filename = String.format(SensorConstants.SENSOR_CPU_FREQUENCY_TEMPLATE, core);
        double frequency = measureValue(filename, SensorConstants.KILOHERTZ_IN_MEGAHERTZ);
        return frequency;
    }
}
