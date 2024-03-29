package com.batterymentor.benchmarks;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import com.batterymentor.collectionmanager.CollectionTask;
import com.batterymentor.constants.BenchmarkConstants;
import com.batterymentor.constants.Constants;
import com.batterymentor.datamanager.Point;
import com.batterymentor.model.LinearModel;
import com.batterymentor.sensors.Sensor;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class that represents a brightness benchmark. A brightness benchmark toggles through various
 * brightness settings and collects the power data for each one.
 */
public class BrightnessBenchmark extends Benchmark {

    /**
     * The content resolver used for changing the screen brightness.
     */
    private ContentResolver mContentResolver;

    /**
     * The time to collect the data at each brightness step.
     */
    private long mDurationStep;

    /**
     * The amount to increase the brightness at each step.
     */
    private double mBrightnessStep;

    /**
     * The thread used for running the benchmark.
     */
    private BrightnessThread mBrightnessThread;

    /**
     * Flag indicating that the benchmark is running.
     */
    private boolean mRunning = false;

    /**
     * The model used to predict the power used the brightness as the parameter.
     */
    private LinearModel mBrightnessToPowerModel;

    /**
     * The benchmark data at various brightness levels used to construct the brightness model.
     */
    private ArrayList<Point> mBrightnessData = new ArrayList<Point>();

    /**
     * The lock for the benchmark data.
     */
    private Lock mLock = new ReentrantLock();

    /**
     * Create a new brightness benchmark for the specified duration and brightness steps. This
     * benchmark will increase the brightness based on the specified brightness step and collect
     * data for the time specified by the duration step.
     *
     * @param durationStep the time to collect the data at each brightness step.
     * @param brightnessStep the amount to increase the brightness at each step.
     */
    public BrightnessBenchmark(Context context, long durationStep, double brightnessStep) {
        super(context, (durationStep + BenchmarkConstants.BRIGHTNESS_CHANGE_SETTLE_DURATION) * (int)(1 + (BenchmarkConstants.MAX_BRIGHTNESS - BenchmarkConstants.MIN_BRIGHTNESS) / brightnessStep));
        mDurationStep = durationStep;
        mBrightnessStep = brightnessStep;
        mContentResolver = context.getContentResolver();
    }

    public void start() {
        super.start();
        if (mBrightnessThread == null) {
            mBrightnessThread = new BrightnessThread();
            new Thread(mBrightnessThread).start();
        }
    }

    public void stop() {
        super.stop();
        if (mBrightnessThread != null) {
            mBrightnessThread.stop();
            mBrightnessThread = null;
        }
    }

    public void lockData() {
        mLock.lock();
    }

    public void unlockData() {
        mLock.unlock();
    }

    public ArrayList<Point> getBrightnessData() {
        return mBrightnessData;
    }

    public LinearModel getModel() {
        return mBrightnessToPowerModel;
    }

    /**
     * The thread responsible for running through different brightness settings.
     */
    private class BrightnessThread implements Runnable {

        /**
         * The initial brightness.
         */
        private int mInitialBrightness;

        /**
         * The initial value for adaptive brightness.
         */
        private int mInitialAdaptiveBrightness;

        /**
         * Flag indicating that the benchmark is running.
         */
        private boolean mBenchmarkRunning = false;

        /**
         * Flag indicating that the thread should keep running.
         */
        private boolean mStopped = false;

        /**
         * The collection task associated with this benchmark.
         */
        private CollectionTask mCollectionTask;

        @Override
        public void run() {
            int brightness = BenchmarkConstants.MIN_BRIGHTNESS;
            try {
                mInitialBrightness = Settings.System.getInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS);
                mInitialAdaptiveBrightness = Settings.System.getInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
            } catch (Settings.SettingNotFoundException e) {
            }
            mBenchmarkRunning = true;
            Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            double preciseBrightness = brightness;
            while (!mStopped && brightness <= BenchmarkConstants.MAX_BRIGHTNESS) {
                Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
                int level = (int)Math.round((brightness * Constants.PERCENT) / (double)(BenchmarkConstants.MAX_BRIGHTNESS - BenchmarkConstants.MIN_BRIGHTNESS));
                notifyListenersOfBenchmarkLevel(level);
                sleep(BenchmarkConstants.BRIGHTNESS_CHANGE_SETTLE_DURATION);
                if (!mStopped) {
                    mCollectionTask = new CollectionTask(getContext(), Sensor.POWER);
                    mCollectionTask.start();
                    sleep(mDurationStep);
                    if (!mStopped) {
                        mCollectionTask.stop();
                        lockData();
                        mBrightnessData.add(new Point(brightness, mCollectionTask.getAbsoluteAverage()));
                        unlockData();
                        preciseBrightness += mBrightnessStep;
                        brightness = (int) Math.round(preciseBrightness);
                    }
                }
            }
            Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS, mInitialBrightness);
            Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, mInitialAdaptiveBrightness);
            if (!mStopped) {
                lockData();
                mBrightnessToPowerModel = new LinearModel(mBrightnessData);
                unlockData();
                notifyListenersOfBenchmarkComplete();
                BrightnessBenchmark.this.stop();
            }
            mRunning = false;
        }

        public void stop() {
            if (mBenchmarkRunning) {
                Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS, mInitialBrightness);
            }
            if (mCollectionTask != null) {
                mCollectionTask.stop();
            }
            mStopped = true;
        }
    }
}
