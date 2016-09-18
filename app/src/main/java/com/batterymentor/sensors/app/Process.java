package com.batterymentor.sensors.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;

import com.batterymentor.R;
import com.batterymentor.constants.Constants;
import com.batterymentor.constants.SensorConstants;
import com.batterymentor.datamanager.Point;
import com.batterymentor.datamanager.RealtimeStatistics;
import com.batterymentor.debug.Debug;
import com.batterymentor.sensors.Sensor;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * Class representing a running process.
 */
public class Process implements Comparable<Process> {

    /**
     * The name of the process..
     */
    private String mName;

    /**
     * The icon associated with the application.
     */
    private final Drawable mIcon;

    /**
     * The cpu load of the process.
     */
    private double mCpuLoad = Constants.INVALID_VALUE;

    /**
     * The list of process ids associated with this sensor.
     */
    private HashSet<Integer> mPids = new HashSet<Integer>();

    /**
     * The previous cpu load total.
     */
    private int mPreviousTotal = Constants.INVALID_VALUE;

    /**
     * The cache of previous measurements for each pid.
     */
    private final SparseArray<Integer> mUsageCache = new SparseArray<Integer>();

    /**
     * The statistics associated with this process.
     */
    private final RealtimeStatistics mStatistics = new RealtimeStatistics(false);

    public Process(Context context, int pid, String name) {
        this(context, pid, name, context.getResources().getDrawable(R.drawable.battery_mentor));
    }

    public Process(Context context, int pid, String name, Drawable icon) {
        mName = name;
        mIcon = icon;
        addPid(pid);
    }

    /**
     * Update the information for this process.
     */
    public void measure() {
        double cpuLoad = Constants.INVALID_VALUE;
        int usage = 0;
        synchronized (mPids) {
            for (int pid : mPids) {
                String filename = String.format(SensorConstants.SENSOR_PROCESS_CPU_LOAD_TEMPLATE, pid);
                int diffPidUsage = 0;
                RandomAccessFile file = null;
                try {
                    int pidUsage = 0;
                    file = new RandomAccessFile(filename, SensorConstants.MODE_READ);
                    StringTokenizer stringTokenizer = new StringTokenizer(file.readLine());
                    for (int i = 0; i < SensorConstants.SENSOR_PROCESS_NUM_TOKENS_TO_IGNORE; i++) {
                        stringTokenizer.nextToken();
                    }
                    for (int i = 0; i < SensorConstants.SENSOR_PROCESS_NUM_TOKENS_TO_READ; i++) {
                        pidUsage += Integer.parseInt(stringTokenizer.nextToken());
                    }
                    Integer previousPidUsage = mUsageCache.get(pid);
                    if (previousPidUsage != Constants.INVALID_VALUE) {
                        diffPidUsage = pidUsage - previousPidUsage;
                    }
                    mUsageCache.put(pid, pidUsage);
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
                usage += diffPidUsage;
            }
        }
        int total = Sensor.LOAD_SENSOR.measureTotal();
        if (mPreviousTotal != Constants.INVALID_VALUE) {
            int diffTotal = total - mPreviousTotal;
            if (diffTotal > 0) {
                cpuLoad = usage * Constants.PERCENT / (double) diffTotal;
            }
            mStatistics.addPoint(new Point(System.currentTimeMillis(), cpuLoad));
        }
        mPreviousTotal = total;
        mCpuLoad = cpuLoad;
    }

    /**
     * Return true if this application has pids, false otherwise.
     *
     * @return true if this application has pids, false otherwise.
     */
    public boolean hasPids() {
        boolean hasPids;
        synchronized (mPids) {
            hasPids = !mPids.isEmpty();
        }
        return hasPids;
    }

    /**
     * Add a process id to the list of pids of this application sensor.
     *
     * @param pid the pid to add.
     */
    public void addPid(int pid) {
        synchronized (mPids) {
            mPids.add(pid);
            mUsageCache.put(pid, Constants.INVALID_VALUE);
        }
    }

    /**
     * Remove a process id from the list of pids of this application sensor.
     *
     * @param pid the pid to remove
     */
    public void removePid(int pid) {
        synchronized (mPids) {
            mPids.remove(pid);
            mUsageCache.remove(pid);
        }
    }

    public boolean isRunning() {
        return getCpuLoad() >= SensorConstants.APPLICATION_RUNNING_CPU_LOAD_THRESHOLD;
    }

    public String getName() {
        return mName;
    }

    public double getCpuLoad() {
        return mStatistics.getAverage();
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public HashSet<Integer> getPids() {
        return mPids;
    }

    public boolean isApplication() {
        return false;
    }

    @Override
    public int compareTo(Process another) {
        double d1 = getCpuLoad();
        double d2 = another.getCpuLoad();
        if (d1 < d2)
            return 1;
        if (d1 > d2)
            return -1;

        long thisBits = Double.doubleToLongBits(d1);
        long anotherBits = Double.doubleToLongBits(d2);

        return (thisBits == anotherBits ? 0 :
                (thisBits < anotherBits ? 1 :
                        -1));
    }

}