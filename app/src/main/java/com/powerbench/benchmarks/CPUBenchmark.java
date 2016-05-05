package com.powerbench.benchmarks;

import android.content.Context;

import com.powerbench.collectionmanager.CollectionTask;
import com.powerbench.constants.BenchmarkConstants;
import com.powerbench.constants.Constants;
import com.powerbench.datamanager.Point;
import com.powerbench.device.Device;
import com.powerbench.model.LinearModel;
import com.powerbench.model.Model;
import com.powerbench.sensors.Sensor;

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
     * The model used to predict the power used the brightness as the parameter.
     */
    private Model mCpuToPowerModel;

    /**
     * The benchmark data at various cpu frequency levels used to construct the cpu model.
     */
    private ArrayList<Point> mCpuFrequencyData = new ArrayList<Point>();

    /**
     * The benchmark data at various cpu load levels used to construct the cpu model.
     */
    private ArrayList<Point> mPowerData = new ArrayList<Point>();

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
        return mCpuToPowerModel;
    }

    /**
     * The thread responsible for running through different cpu load settings.
     */
    private class CpuThread implements Runnable {

        /**
         * Flag indicating that the thread is stopped.
         */
        private boolean mStopped = false;

        @Override
        public void run() {
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
                try {
                    Thread.sleep(BenchmarkConstants.CPU_CHANGE_SETTLE_DURATION);
                } catch (InterruptedException e) {
                }
                CollectionTask powerCollectionTask = new CollectionTask(getContext(), Sensor.POWER);
                CollectionTask loadCollectionTask = new CollectionTask(getContext(), Sensor.LOAD_SENSOR);
                CollectionTask frequencyCollectionTask = new CollectionTask(getContext(), Sensor.FREQUENCY_SENSOR);
                powerCollectionTask.start();
                loadCollectionTask.start();
                frequencyCollectionTask.start();
                try {
                    Thread.sleep(mDurationStep);
                } catch (InterruptedException e) {
                }
                frequencyCollectionTask.stop();
                loadCollectionTask.stop();
                powerCollectionTask.stop();
                lockData();
                double cpuAverage = loadCollectionTask.getAverage();
                mCpuFrequencyData.add(new Point(cpuAverage, frequencyCollectionTask.getAverage()));
                mPowerData.add(new Point(cpuAverage, powerCollectionTask.getAverage()));
                unlockData();
                notifyListenersOfBenchmarkProgress(load);
                load += mCpuStep;
            }
            if (!mStopped) {
                lockData();
                mCpuToPowerModel = new LinearModel(mPowerData);
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
            mStopped = true;
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
