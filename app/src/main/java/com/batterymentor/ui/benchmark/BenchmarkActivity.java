package com.batterymentor.ui.benchmark;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.batterymentor.R;
import com.batterymentor.benchmarks.Benchmark;
import com.batterymentor.constants.Constants;
import com.batterymentor.constants.DeviceConstants;
import com.batterymentor.device.Permissions;
import com.batterymentor.ui.common.CommonActivity;
import com.batterymentor.ui.theme.Theme;
import com.batterymentor.utils.Utils;

import java.text.DecimalFormat;

/**
 * The benchmark activity that all other benchmark activities in the application inherit from.
 */
public abstract class BenchmarkActivity extends CommonActivity {

    /**
     * The progress label.
     */
    private TextView mProgressLabelTextView;

    /**
     * The duration text view.
     */
    private TextView mProgressDurationTextView;

    /**
     * The progress view.
     */
    private ProgressBar mProgressView;

    /**
     * The progress error text view.
     */
    private TextView mProgressErrorTextView;

//    /**
//     * The progress text view.
//     */
//    private TextView mProgressTextView;

    /**
     * The button to stop the benchmark.
     */
    private Button mStopButton;

    /**
     * The benchmark associated with this view.
     */
    private Benchmark mBenchmark;

    /**
     * The power formatter.
     */
    private DecimalFormat mDurationFormatter;

    /**
     * Flag indicating whether the countdown timer has completed.
     */
    private boolean mCountdownTimerComplete = false;

    /**
     * Flag indicating whether the benchmark has completed.
     */
    private boolean mBenchmarkComplete = false;

    /**
     * The progress listener.
     */
    private Benchmark.ProgressListener mBenchmarkProgressListener;

    /**
     * Flag indicating that the progress animation is running.
     */
    private boolean mProgressAnimationRunning = false;

    /**
     * The current playtime of the progress animation.
     */
    private long mProgressAnimationPlaytime = 0;

    /**
     * Initialize the benchmark activity
     */
    @Override
    protected void initialize() {
        super.initialize();
        mProgressLabelTextView = (TextView)findViewById(R.id.benchmark_progress_label);
        mProgressDurationTextView = (TextView)findViewById(R.id.benchmark_progress_duration);
        mProgressView = (ProgressBar)findViewById(R.id.benchmark_progress_bar);
        mProgressErrorTextView = (TextView)findViewById(R.id.benchmark_progress_error);
        mStopButton = (Button) findViewById(R.id.button_stop);
        if (mStopButton != null) {
            mStopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBenchmark.stop();
                    finish();
                }
            });
        }
    }

    @Override
    protected void onPermissionGranted(int permission) {
        if (permission == DeviceConstants.PERMISSIONS_WRITE_SETTINGS) {
            startBenchmark(mBenchmark);
        }
    }

    @Override
    protected void onPermissionDenied(int permission) {
        if (permission == DeviceConstants.PERMISSIONS_WRITE_SETTINGS) {
            finish();
        }
    }

    @Override
    protected void onServiceBound() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBenchmark != null) {
            mBenchmark.resume();
            mBenchmark.registerProgressListener(mBenchmarkProgressListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBenchmark != null) {
            mBenchmark.pause();
            mBenchmark.unregisterProgressListener(mBenchmarkProgressListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBenchmark != null) {
            mBenchmark.stop();
            onBenchmarkStopped();
        }
    }

    /**
     * Start the benchmark.
     */
    protected void startBenchmark(Benchmark benchmark) {
        mBenchmark = benchmark;
        if (Permissions.getInstance().requestSettingsPermission(this)) {
            startProgress(mBenchmark.getDuration());
            mBenchmarkProgressListener = new Benchmark.ProgressListener() {
                @Override
                public void onTick(long millisUntilFinished) {
                    updateDuration(millisUntilFinished);
                }

                @Override
                public void onTimerComplete() {
                    onCountdownTimerComplete();
                }

                @Override
                public void onProgress(final int progress) {
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress(progress);
                        }
                    });
                }

                @Override
                public void onLevel(final int level) {
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            updateLevel(level);
                        }
                    });
                }

                @Override
                public void onComplete() {
                    onBenchmarkComplete();
                }
            };
            mBenchmark.start();
            mBenchmark.registerProgressListener(mBenchmarkProgressListener);
            onBenchmarkStarted();
        }
    }

    /**
     * Start the benchmark progress timer.
     *
     * @param duration the duration of the timer, in milliseconds.
     */
    protected void startProgress(long duration) {
//        if (mProgressTextView != null) {
//            mProgressTextView.setText(getString(R.string.benchmark_getting_ready));
//        }
    }

    /**
     * Update the progress of the benchmark.
     */
    protected void updateProgress(int progress) {
        if (mProgressView != null) {
            mProgressView.setProgress(progress);
        }

    }

    /**
     * Update the level of the benchmark.
     */
    protected void updateLevel(int level) {
//        if (mProgressTextView != null) {
//            mProgressTextView.setText(String.format(getString(R.string.value_percent_template), level));
//        }
    }

    /**
     * Update the duration text view with the specified duration.
     */
    protected void updateDuration(long millisUntilFinished) {
        if (mProgressDurationTextView != null) {
            final double duration = millisUntilFinished / ((double) Constants.SECOND);
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    mProgressDurationTextView.setText(Utils.convertTimeRemainingToSimpleString(BenchmarkActivity.this, duration));
                }
            });
        }
    }

    /**
     * Called when the countdown timer is complete.
     */
    protected void onCountdownTimerComplete() {
        mCountdownTimerComplete = true;
        if (!checkShowBenchmarkResults()) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    mProgressDurationTextView.setText(getString(R.string.test_completing));
                }
            });
        }
    }

    /**
     * Called when the benchmark starts.
     */
    protected void onBenchmarkStarted() {
        mCountdownTimerComplete = false;
        mBenchmarkComplete = false;
    }

    /**
     * Called when the benchmark is stopped by the user.
     */
    protected void onBenchmarkStopped() {
        mCountdownTimerComplete = true;
        mBenchmarkComplete = true;
    }

    /**
     * Called when the benchmark completes.
     */
    protected void onBenchmarkComplete() {
        mBenchmarkComplete = true;
        checkShowBenchmarkResults();
    }

    @Override
    protected void applyTheme(Theme theme) {
        super.applyTheme(theme);
        if (mProgressLabelTextView != null) {
            mProgressLabelTextView.setTextColor(ContextCompat.getColor(this, theme.getColorResource()));
        }
        if (mProgressDurationTextView != null) {
            mProgressDurationTextView.setTextColor(ContextCompat.getColor(this, theme.getColorResource()));
        }
        if (mProgressView != null) {
            int progress = mProgressView.getProgress();
            mProgressView.setProgressDrawable(ContextCompat.getDrawable(this, theme.getProgressResource()));
            mProgressView.setProgress(0);
            mProgressView.setProgress(progress);
        }
//        if (mProgressTextView != null) {
//            mProgressTextView.setTextColor(ContextCompat.getColor(this, theme.getColorResource()));
//        }
        if (mProgressErrorTextView != null) {
            mProgressErrorTextView.setTextColor(ContextCompat.getColor(this, theme.getColorResource()));
        }
        if (mStopButton != null) {
            mStopButton.setTextColor(ContextCompat.getColor(this, theme.getColorResource()));
        }
    }

    /**
     * Check if the benchmark results should be shown. Notify all activities that inherit this
     * activity to show the benchmark results when both the benchmark and countdown timer are
     * complete.
     */
    private synchronized boolean checkShowBenchmarkResults() {
        if (mBenchmarkComplete && mCountdownTimerComplete) {
            showBenchmarkResults();
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    mProgressDurationTextView.setText(getString(R.string.test_complete));
                }
            });
            return true;
        }
        return false;
    }

    /**
     * Called when the benchmark results should be called.
     */
    protected void showBenchmarkResults() {
        finish();
    }

    @Override
    public void onChargerConnectedUIThread() {
        super.onChargerConnectedUIThread();
        if (mBenchmark != null) {
            mBenchmark.onChargerConnected();
        }
        if (mProgressLabelTextView != null) {
            mProgressLabelTextView.setText(R.string.test_paused);
        }
        if (mProgressErrorTextView != null) {
            mProgressErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onChargerDisconnectedUIThread() {
        super.onChargerDisconnectedUIThread();
        if (mBenchmark != null) {
            mBenchmark.onChargerDisconnected();
        }
        if (mProgressLabelTextView != null) {
            mProgressLabelTextView.setText(R.string.test_in_progress);
        }
        if (mProgressErrorTextView != null) {
            mProgressErrorTextView.setVisibility(View.GONE);
        }
    }
}
