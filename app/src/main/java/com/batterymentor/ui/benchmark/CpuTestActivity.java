package com.batterymentor.ui.benchmark;

import android.os.Bundle;

import com.batterymentor.R;
import com.batterymentor.benchmarks.CpuBenchmark;
import com.batterymentor.constants.BenchmarkConstants;
import com.batterymentor.model.ModelManager;

/**
 * The main powerbench activity that allows a user to view battery power consumption and charging
 * rate in realtime.
 */
public class CpuTestActivity extends BenchmarkActivity {

    /**
     * The cpu benchmark.
     */
    private CpuBenchmark mCpuBenchmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benchmark);
        initialize();
        getSupportActionBar().setTitle(getString(R.string.cpu_test));
        mCpuBenchmark = new CpuBenchmark(this, BenchmarkConstants.CPU_DURATION_STEP, BenchmarkConstants.CPU_STEP);
        startBenchmark(mCpuBenchmark);
    }

    @Override
    protected void showBenchmarkResults() {
        if (mCpuBenchmark != null) {
            ModelManager.getInstance().setCpuModel(this, mCpuBenchmark.getModel());
            ModelManager.getInstance().setCpuFrequencyModel(this, mCpuBenchmark.getFrequencyModel());
        }
        finish();
    }
}
