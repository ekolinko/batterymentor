package com.batterymentor.benchmarks;

import android.content.Context;
import android.os.CountDownTimer;

import com.batterymentor.constants.BenchmarkConstants;
import com.batterymentor.constants.Constants;
import com.batterymentor.model.Model;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract class that represents a benchmark that can be run on the user's device. A benchmark
 * contains a workload and a set of data associated with running that workload.
 */
public abstract class Benchmark {

    /**
     * The application context.
     */
    private Context mContext;

    /**
     * The set of listeners that listen to benchmark progress.
     */
    private HashSet<ProgressListener> mProgressListeners = new HashSet<ProgressListener>();

    /**
     * The duration of the benchmark in milliseconds.
     */
    private long mDuration;

    /**
     * The countdown timer associated with this benchmark.
     */
    private BenchmarkTimer mBenchmarkTimer;

    /**
     * The lock associated with this benchmark.
     */
    private Lock mLock = new ReentrantLock();

    /**
     * The condition indicating that the charger has been connected.
     */
    private Condition mChargerConnectedCondition = mLock.newCondition();

    /**
     * The condition indicating that the charger has been disconnected.
     */
    private Condition mChargerDisconnectedCondition = mLock.newCondition();

    /**
     * Flag indicating whether a charger is connected.
     */
    private boolean mChargerConnected = false;

    public Benchmark(Context context, long duration) {
        mContext = context;
        mDuration = duration;
        mBenchmarkTimer = new BenchmarkTimer(mDuration + BenchmarkConstants.BASE_DURATION);
    }

    /**
     * Start running the benchmark.
     */
    public void start() {
        mBenchmarkTimer.start();
    }

    /**
     * Stop running the benchmark.
     */
    public void stop() {
    }

    /**
     * Resume the benchmark.
     */
    public void resume() {
        mBenchmarkTimer.resume();
    }

    /**
     * Pause the benchmark.
     */
    public void pause() {
        mBenchmarkTimer.pause();
    }

    /**
     * Called when a charger is connected.
     */
    public void onChargerConnected() {
        mLock.lock();
        mChargerConnected = true;
        mChargerConnectedCondition.signal();
        mBenchmarkTimer.pause();
        mLock.unlock();;
    }

    /**
     * Called when a charger is disconnected.
     */
    public void onChargerDisconnected() {
        mLock.lock();
        mChargerConnected = false;
        mChargerDisconnectedCondition.signal();
        mBenchmarkTimer.resume();
        mLock.unlock();
    }

    /**
     * Sleep for the specified amount of time in milliseconds, but interrupt early if a charger is
     * detected.
     */
    public void sleep(long milliseconds) {
        mLock.lock();
        long remaining = milliseconds;
        while (remaining > 0) {
            if (mChargerConnected) {
                try {
                    mChargerDisconnectedCondition.await();
                } catch (InterruptedException e) {
                    resume();
                }
            }

            long startTime = System.currentTimeMillis();
            try {
                if (mChargerConnectedCondition.await(remaining, TimeUnit.MILLISECONDS)) {
                    long endTime = System.currentTimeMillis();
                    remaining = remaining - (endTime - startTime);
                    if (remaining > 0) {
                        pause();
                    }
                } else {
                    remaining = 0;
                }
            } catch (InterruptedException e) {

            }
        }
        mLock.unlock();
    }

    public Context getContext() {
        return mContext;
    }

    public long getDuration() {
        return mDuration;
    }

    /**
     * Register a completion listener to listen to benchmark progress.
     *
     * @param progressListener the progress listener to register.
     */
    public void registerProgressListener(ProgressListener progressListener) {
        synchronized (mProgressListeners) {
            mProgressListeners.add(progressListener);
        }
    }

    /**
     * Unregister a completion listener from listening to benchmark progress.
     *
     * @param progressListener the completion listener to unregister.
     */
    public void unregisterProgressListener(ProgressListener progressListener) {
        synchronized (mProgressListeners) {
            mProgressListeners.remove(progressListener);
        }
    }

    /**
     * Notify all registered progress listeners of a timer tick.
     */
    public void notifyListenersOfBenchmarkTick(long millisUntilFinished) {
        synchronized (mProgressListeners) {
            for (ProgressListener completionListener : mProgressListeners) {
                completionListener.onTick(millisUntilFinished);
            }
        }
    }

    /**
     * Notify all registered progress listeners when the timer completes.
     */
    public void notifyListenersOfBenchmarkTimerComplete() {
        synchronized (mProgressListeners) {
            for (ProgressListener completionListener : mProgressListeners) {
                completionListener.onTimerComplete();
            }
        }
    }

    /**
     * Notify all registered progress listeners of progress changes.
     */
    public void notifyListenersOfBenchmarkProgress(int progress) {
        synchronized (mProgressListeners) {
            for (ProgressListener completionListener : mProgressListeners) {
                completionListener.onProgress(progress);
            }
        }
    }

    /**
     * Notify all registered progress listeners of level changes.
     */
    public void notifyListenersOfBenchmarkLevel(int level) {
        synchronized (mProgressListeners) {
            for (ProgressListener completionListener : mProgressListeners) {
                completionListener.onLevel(level);
            }
        }
    }

    /**
     * Notify all registered progress listeners that this benchmark has completed.
     */
    public void notifyListenersOfBenchmarkComplete() {
        synchronized (mProgressListeners) {
            for (ProgressListener completionListener : mProgressListeners) {
                completionListener.onComplete();
            }
        }
    }

    /**
     * Return the model associated with this benchmark.
     *
     * @return the model associated with this benchmark.
     */
    public abstract Model getModel();

    /**
     * The benchmark timer.
     */
    class BenchmarkTimer {

        /**
         * Flag indicating this timer is paused.
         */
        private boolean mPaused = true;

        /**
         * The duration of the timer.
         */
        private long mDuration;

        /**
         * The number of milliseconds remaining for a paused timer.
         */
        private long mMillisUntilFinished;

        /**
         * The countdown timer.
         */
        private CountDownTimer mCountDownTimer;

        public BenchmarkTimer(long millisInFuture) {
            mDuration = millisInFuture;
            mMillisUntilFinished = millisInFuture;
        }

        public void start() {
            resume();
        }

        public void pause() {
            if (!mPaused) {
                mCountDownTimer.cancel();
                mPaused = true;
            }
        }

        public void resume() {
            if (mPaused) {
                mCountDownTimer = new CountDownTimer(mMillisUntilFinished, BenchmarkConstants.COUNTDOWN_TIMER_UPDATE_INTERVAL) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        mMillisUntilFinished = millisUntilFinished;
                        notifyListenersOfBenchmarkTick(millisUntilFinished);
                        notifyListenersOfBenchmarkProgress((int)(((mDuration - millisUntilFinished) * (long) Constants.PERCENT) / mDuration));
                    }

                    @Override
                    public void onFinish() {
                        notifyListenersOfBenchmarkTimerComplete();
                    }
                };
                mCountDownTimer.start();
                mPaused = false;
            }
        }
    };
    /**
     * The interface used for listening for benchmark progress.
     */
    public interface ProgressListener {

        /**
         * Called at a regular interval when the timer changes.
         */
        void onTick(long millisUntilFinished);

        /**
         * Called when the timer is complete.
         */
        void onTimerComplete();

        /**
         * Called when the benchmark changes in progress.
         *
         * @param progress the progress of the benchmark.
         */
        void onProgress(int progress);

        /**
         * Called when the benchmark level changes.
         *
         * @param level the level of the benchmark.
         */
        void onLevel(int level);

        /**
         * Called when the benchmark has completed.
         */
        void onComplete();
    }
}
