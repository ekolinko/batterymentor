package com.powerbench.ui.benchmark;

import android.widget.TextView;

import com.powerbench.R;
import com.powerbench.constants.Constants;
import com.powerbench.ui.common.CommonActivity;

import java.text.DecimalFormat;

/**
 * The benchmark activity that all other benchmark activities in the application inherit from.
 */
public abstract class BenchmarkActivity extends CommonActivity {

    /**
     * The duration text view.
     */
    private TextView mDurationTextView;

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
     * Initialize the benchmark activity
     */
    @Override
    protected void initialize() {
        super.initialize();
        mDurationTextView = (TextView)findViewById(R.id.benchmark_duration);
        mDurationFormatter = new DecimalFormat(getString(R.string.format_duration));
    }

    /**
     * Update the duration text view with the specified duration.
     */
    protected void updateDuration(long millisUntilFinished) {
        if (mDurationTextView != null) {
            final double duration = millisUntilFinished / ((double) Constants.SECOND);
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    mDurationTextView.setText(String.format(getString(R.string.value_units_template), mDurationFormatter.format(duration), getString(R.string.seconds)));
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
                    mDurationTextView.setText(getString(R.string.benchmark_completing));
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
                    mDurationTextView.setText(getString(R.string.benchmark_complete));
                }
            });
            return true;
        }
        return false;
    }

    /**
     * Called when the benchmark results should be called.
     */
    protected abstract void showBenchmarkResults();
}
