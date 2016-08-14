package com.powerbench.ui.benchmark;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.powerbench.R;
import com.powerbench.benchmarks.Benchmark;
import com.powerbench.benchmarks.CpuBenchmark;
import com.powerbench.collectionmanager.ApplicationCollectionTask;
import com.powerbench.constants.BenchmarkConstants;
import com.powerbench.constants.DeviceConstants;
import com.powerbench.datamanager.Point;
import com.powerbench.device.Permissions;
import com.powerbench.model.ModelManager;
import com.powerbench.ui.common.CommonFragment;
import com.powerbench.ui.theme.Theme;
import com.powerbench.ui.theme.ThemeManager;

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
