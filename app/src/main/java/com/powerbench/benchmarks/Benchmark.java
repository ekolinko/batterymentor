package com.powerbench.benchmarks;

import android.content.Context;
import android.os.CountDownTimer;

import com.powerbench.constants.BenchmarkConstants;
import com.powerbench.constants.Constants;

import java.util.HashSet;

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
    private CountDownTimer mCountdownTimer;

    public Benchmark(Context context, long duration) {
        mContext = context;
        mDuration = duration;
        mCountdownTimer = new CountDownTimer(mDuration + BenchmarkConstants.BASE_DURATION, BenchmarkConstants.COUNTDOWN_TIMER_UPDATE_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                notifyListenersOfBenchmarkTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                notifyListenersOfBenchmarkTimerComplete();
            }
        };
    }

    /**
     * Start running the benchmark.
     */
    public void start() {
        mCountdownTimer.start();
    }

    /**
     * Stop running the benchmark.
     */
    public void stop() {
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
     * Notify all registered progress listeners of progress.
     */
    public void notifyListenersOfBenchmarkProgress(int progress) {
        synchronized (mProgressListeners) {
            for (ProgressListener completionListener : mProgressListeners) {
                completionListener.onProgress(progress);
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
         * Called when the benchmark has completed.
         */
        void onComplete();
    }
}
