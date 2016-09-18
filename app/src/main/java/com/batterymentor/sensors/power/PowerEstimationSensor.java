package com.batterymentor.sensors.power;

import android.content.Context;
import android.provider.Settings;

import com.batterymentor.constants.Constants;
import com.batterymentor.device.Device;
import com.batterymentor.sensors.Sensor;

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
        return LOAD_SENSOR.isSupported() && FREQUENCY_SENSOR.isSupported() && VOLTAGE.isSupported();
    }

    /**
     * Estimate the total power by estimating the power consumed by the screen and the CPU and
     * and adding a base component.
     *
     * @return the estimated power consumed by the device.
     */
    public double measure() {
        double screenPower = estimateScreenPower();
        if (screenPower < 0) {
            screenPower = 0;
        }
        double cpuPower = estimateCpuPower();
        if (cpuPower < 0) {
            cpuPower = 0;
        }
        double basePower = 500;
        return screenPower + cpuPower + basePower;
    }

    /**
     * Estimate the power consumed by the screen.
     *
     * @return the power consumed by the screen.
     */
    public double estimateScreenPower() {
        int brightness = Constants.INVALID_VALUE;
        Context context = com.batterymentor.settings.Settings.getInstance().getContext();
        if (context != null) {
            try {
                brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
            }
        }
        return 3.0054*brightness;
    }

    /**
     * Estimate the power consumed by the CPU.
     *
     * @return the power consumed by the CPU.
     */
    public double estimateCpuPower() {
        double cpuPower = Constants.INVALID_VALUE;
        int numCores = Device.getInstance().getNumCores();
        for (int core = 0; core < numCores; core++) {
            double frequency = FREQUENCY_SENSOR.measureCore(core);
            double load = LOAD_SENSOR.measureCore(core);
            cpuPower += (0.0003*frequency*frequency + 0.113*frequency)*load;
        }
        return cpuPower;
    }
}
