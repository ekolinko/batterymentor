package com.batterymentor.benchmarks;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import com.batterymentor.collectionmanager.CollectionTask;
import com.batterymentor.constants.BenchmarkConstants;
import com.batterymentor.constants.Constants;
import com.batterymentor.datamanager.Point;
import com.batterymentor.device.Device;
import com.batterymentor.model.LinearModel;
import com.batterymentor.model.Model;
import com.batterymentor.model.QuadraticModel;
import com.batterymentor.sensors.Sensor;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class that represents a cpu benchmark. A cpu benchmark runs through a specified set of loads
 * on each of the cores and collects power data for each one.
 */
public class CpuBenchmark extends Benchmark {

    /**
     * The content resolver used for changing the screen brightness.
     */
    private ContentResolver mContentResolver;

    /**
     * The time to collect the data at each brightness step.
     */
    private long mDurationStep;

    /**
     * The amount to increase the cpu at each step.
     */
    private int mCpuStep;

    /**
     * The thread used for running the benchmark.
     */
    private CpuThread mCpuThread;

    /**
     * Flag indicating that the benchmark is running.
     */
    private boolean mRunning = false;

    /**
     * Flag indicating that the benchmark is running.
     */
    private boolean mBenchmarkRunning = false;

    /**
     * The model used to predict the power using the cpu load as the parameter.
     */
    private Model mCpuLoadToPowerModel;

    /**
     * The model used to predict the power using the cpu frequency as the parameter.
     */
    private Model mCpuFrequencyToPowerModel;

    /**
     * The benchmark data at various cpu frequency levels used to construct the cpu model.
     */
    private ArrayList<Point> mCpuFrequencyData = new ArrayList<Point>();

    /**
     * The benchmark data at various cpu load levels used to construct the cpu model.
     */
    private ArrayList<Point> mPowerData = new ArrayList<Point>();

    /**
     * The benchmark data at various cpu frequency levels used to construct the frequency power model.
     */
    private ArrayList<Point> mFrequencyPowerData = new ArrayList<Point>();

    /**
     * The lock for the benchmark data.
     */
    private Lock mLock = new ReentrantLock();

    /**
     * Create a new brightness benchmark for the specified duration and cpu load steps. This
     * benchmark will increase the cpu load on each core based on the specified cpu step and
     * collect the frequency and load data for the time specified by the duration step.
     *
     * @param durationStep the time to collect the data at each brightness step.
     * @param cpuStep      the amount to increase the cpu load at each step.
     */
    public CpuBenchmark(Context context, long durationStep, int cpuStep) {
        super(context, (durationStep + BenchmarkConstants.CPU_CHANGE_SETTLE_DURATION) * (1 + ((BenchmarkConstants.MAX_CPU - BenchmarkConstants.MIN_CPU) / cpuStep)));
        mDurationStep = durationStep;
        mCpuStep = cpuStep;
        mContentResolver = context.getContentResolver();
    }

    public void start() {
        super.start();
        if (mCpuThread == null) {
            mCpuThread = new CpuThread();
            new Thread(mCpuThread).start();
        }
    }

    public void stop() {
        super.stop();
        if (mCpuThread != null) {
            mCpuThread.stop();
            mCpuThread = null;
        }
    }

    public void lockData() {
        mLock.lock();
    }

    public void unlockData() {
        mLock.unlock();
    }

    public ArrayList<Point> getPowerData() {
        return mPowerData;
    }

    public ArrayList<Point> getCpuFrequencyData() {
        return mCpuFrequencyData;
    }

    public Model getModel() {
        return mCpuLoadToPowerModel;
    }

    public Model getFrequencyModel() {
        return mCpuFrequencyToPowerModel;
    }

    /**
     * The thread responsible for running through different cpu load settings.
     */
    private class CpuThread implements Runnable {

        /**
         * Flag indicating that the thread is stopped.
         */
        private boolean mStopped = false;

        /**
         * The initial brightness.
         */
        private int mInitialBrightness;

        /**
         * The collection task responsible for measuring power.
         */
        CollectionTask mPowerCollectionTask;

        /**
         * The collection task responsible for measuring load.
         */
        CollectionTask mLoadCollectionTask;

        /**
         * The collection task responsible for measuring frequency.
         */
        CollectionTask mFrequencyCollectionTask;

        @Override
        public void run() {
            try {
                mInitialBrightness = Settings.System.getInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
            }
            mBenchmarkRunning = true;
            Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS, BenchmarkConstants.MIN_BRIGHTNESS);
            int load = BenchmarkConstants.MIN_CPU;
            CoreThread[] coreThreads = new CoreThread[Device.getInstance().getNumCores()];
            for (int i = 0; i < coreThreads.length; i++) {
                coreThreads[i] = new CoreThread(load);
                new Thread(coreThreads[i]).start();
            }
            while (!mStopped && load <= BenchmarkConstants.MAX_CPU) {
                for (CoreThread coreThread : coreThreads) {
                    coreThread.setLoad(load);
                }
                notifyListenersOfBenchmarkLevel(load);
                sleep(BenchmarkConstants.CPU_CHANGE_SETTLE_DURATION);
                if (!mStopped) {
                    mPowerCollectionTask = new CollectionTask(getContext(), Sensor.POWER);
                    mLoadCollectionTask = new CollectionTask(getContext(), Sensor.LOAD_SENSOR);
                    mFrequencyCollectionTask = new CollectionTask(getContext(), Sensor.FREQUENCY_SENSOR);
                    mPowerCollectionTask.start();
                    mLoadCollectionTask.start();
                    mFrequencyCollectionTask.start();
                    sleep(mDurationStep);
                    if (!mStopped) {
                        mFrequencyCollectionTask.stop();
                        mLoadCollectionTask.stop();
                        mPowerCollectionTask.stop();
                        lockData();
                        double cpuAverage = mLoadCollectionTask.getAverage();
                        double frequencyAverage = mFrequencyCollectionTask.getAverage();
                        mCpuFrequencyData.add(new Point(cpuAverage, mFrequencyCollectionTask.getAverage()));
                        mPowerData.add(new Point(cpuAverage, mPowerCollectionTask.getAverage()));
                        mFrequencyPowerData.add(new Point(frequencyAverage, mPowerCollectionTask.getAverage()));
                        unlockData();
                        load += mCpuStep;
                    }
                }
            }
            Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS, mInitialBrightness);
            if (!mStopped) {
                lockData();
                mCpuLoadToPowerModel = new LinearModel(mPowerData);
                mCpuFrequencyToPowerModel = new QuadraticModel(mFrequencyPowerData);
                unlockData();
                notifyListenersOfBenchmarkComplete();
                CpuBenchmark.this.stop();
            }
            for (CoreThread coreThread : coreThreads) {
                coreThread.stop();
            }
            mRunning = false;
        }

        public void stop() {
            if (mBenchmarkRunning) {
                Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS, mInitialBrightness);
            }
            mStopped = true;
            if (mPowerCollectionTask != null)
                mPowerCollectionTask.stop();
            if (mLoadCollectionTask != null)
                mLoadCollectionTask.stop();
            if (mFrequencyCollectionTask != null)
                mFrequencyCollectionTask.stop();
        }
    }

    /**
     * The thread responsible for loading a core by the specified amount.
     */
    private class CoreThread implements Runnable {

        /**
         * The core lock.
         */
        private Lock mLock = new ReentrantLock();

        /**
         * The condition indicating that the load has changed.
         */
        private Condition mLoadChangedCondition = mLock.newCondition();

        /**
         * Flag indicating that the load has changed.
         */
        private boolean mLoadChanged = false;

        /**
         * The amount to load the core.
         */
        private int mLoad;

        /**
         * Flag indicating that the thread is stopped.
         */
        private boolean mStopped = false;

        public CoreThread(int load) {
            mLoad = load;
        }

        @Override
        public void run() {
            while (!mStopped) {
                mLoadChanged = false;
                int idle = BenchmarkConstants.MAX_CPU - mLoad;
                spin(mLoad);
                if (!mLoadChanged) {
                    mLock.lock();
                    try {
                        mLoadChangedCondition.await(idle, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                    }
                    mLock.unlock();
                }
            }
        }

        private double spin(int millis) {
            long sleepTime = millis * Constants.NANOSECONDS_IN_MILLISECOND;
            long startTime = System.nanoTime();
            double sum = 0;
            int i = 0;
            while ((System.nanoTime() - startTime) < sleepTime && !mLoadChanged) {
                sum += (i % 2 == 0) ? -1 / (2 * i - 1) : 1 / (2 * i - 1);
                i++;
            }
            return sum;
        }

        public void setLoad(int load) {
            mLock.lock();
            mLoad = load;
            mLoadChanged = true;
            mLoadChangedCondition.signal();
            mLock.unlock();
        }

        public void stop() {
            mStopped = true;
        }
    }
}
