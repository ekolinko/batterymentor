package com.powerbench.sensors.cpu;

import com.powerbench.constants.Constants;
import com.powerbench.constants.SensorConstants;
import com.powerbench.datamanager.LoadMeasurement;
import com.powerbench.debug.Debug;
import com.powerbench.device.Device;
import com.powerbench.sensors.Sensor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * The sensor responsible for reading cpu load from the system.
 */
public class LoadSensor extends Sensor {

    /**
     * The number of cores the device has.
     */
    private final int mNumCores = Device.getInstance().getNumCores();

    /**
     * The cache of previous measurements for each cpu.
     */
    private final LoadMeasurement[] mUsageCache = new LoadMeasurement[mNumCores];

    public LoadSensor() {
        for (int i = 0; i < mUsageCache.length; i++) {
            mUsageCache[i] = new LoadMeasurement();
        }
    }

    @Override
    public String initFilename() {
        return (new File(SensorConstants.SENSOR_CPU_LOAD).exists()) ?
                SensorConstants.SENSOR_CPU_LOAD : null;
    }

    @Override
    public boolean isSupported() {
        return getFilename() != null;
    }

    @Override
    public double measure() {
        double cpuLoad = Constants.INVALID_VALUE;
        try {
            RandomAccessFile file = new RandomAccessFile(getFilename(), SensorConstants.MODE_READ);
            file.readLine();
            for (int i = 0; i < mNumCores; i++) {
                double coreLoad = 0;
                LoadMeasurement previous = mUsageCache[i];
                String line = file.readLine();
                StringTokenizer stringTokenizer = new StringTokenizer(line);
                stringTokenizer.nextToken();
                ArrayList<Integer> values = new ArrayList<Integer>();
                int total = 0;
                while (stringTokenizer.hasMoreTokens()) {
                    int value = Integer.parseInt(stringTokenizer.nextToken());
                    values.add(value);
                    total += value;
                }
                int usage = total - values.get(3);
                if (previous.isValid()) {
                    int diffTotal = total - previous.getTotal();
                    if (diffTotal > 0) {
                        coreLoad = (usage - previous.getUsage()) / (double) diffTotal;
                    }
                    cpuLoad = (cpuLoad == Constants.INVALID_VALUE) ? coreLoad : cpuLoad + coreLoad;
                }
                previous.setMeasurements(usage, total);
            }
        } catch (Exception e) {
            if (Debug.isCollectionManagerLoggingEnabled())
                Debug.printDebug(e);
        }
        if (cpuLoad != Constants.INVALID_VALUE)
            cpuLoad = cpuLoad * Constants.PERCENT / mNumCores;
        return cpuLoad;
    }

    public double measureCore(int core) {
        double cpuLoad = Constants.INVALID_VALUE;
        try {
            RandomAccessFile file = new RandomAccessFile(getFilename(), SensorConstants.MODE_READ);
            file.readLine();
            for (int i = 0; i < core; i++) {
                file.readLine();
            }
            double coreLoad = 0;
            LoadMeasurement previous = mUsageCache[core];
            String line = file.readLine();
            StringTokenizer stringTokenizer = new StringTokenizer(line);
            stringTokenizer.nextToken();
            ArrayList<Integer> values = new ArrayList<Integer>();
            int total = 0;
            while (stringTokenizer.hasMoreTokens()) {
                int value = Integer.parseInt(stringTokenizer.nextToken());
                values.add(value);
                total += value;
            }
            int usage = total - values.get(3);
            if (previous.isValid()) {
                int diffTotal = total - previous.getTotal();
                if (diffTotal > 0) {
                    coreLoad = (usage - previous.getUsage()) / (double) diffTotal;
                }
                cpuLoad = (cpuLoad == Constants.INVALID_VALUE) ? coreLoad : cpuLoad + coreLoad;
            }
            previous.setMeasurements(usage, total);
        } catch (Exception e) {
            if (Debug.isCollectionManagerLoggingEnabled())
                Debug.printDebug(e);
        }
        return cpuLoad;
    }

    /**
     * Return the total time the cpu was online since boot.
     *
     * @return the total time the cpu was online since boot.
     */
    public int measureTotal() {
        int total = 0;
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(getFilename(), SensorConstants.MODE_READ);
            file.readLine();
            for (int i = 0; i < mNumCores; i++) {
                StringTokenizer stringTokenizer = new StringTokenizer(file.readLine());
                stringTokenizer.nextToken();
                ArrayList<Integer> values = new ArrayList<Integer>();
                while (stringTokenizer.hasMoreTokens()) {
                    int value = Integer.parseInt(stringTokenizer.nextToken());
                    values.add(value);
                    total += value;
                }
            }
        } catch (Exception e) {
            if (Debug.isCollectionManagerLoggingEnabled())
                Debug.printDebug(e);
        } finally {
            try {
                if (file != null)
                    file.close();
            } catch (IOException e) {
            }
        }
        return total;
    }
}
