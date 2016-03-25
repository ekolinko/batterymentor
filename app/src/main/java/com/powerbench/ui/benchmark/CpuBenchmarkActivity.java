package com.powerbench.ui.benchmark;

import android.os.Bundle;
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
import com.powerbench.model.Model;
import com.powerbench.sensors.app.Process;
import com.powerbench.ui.app.ProcessAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The main powerbench activity that allows a user to view battery power consumption and charging
 * rate in realtime.
 */
public class CpuBenchmarkActivity extends BenchmarkActivity {

    /**
     * The cpu data text view.
     */
    private TextView mCpuDataTextView;

    /**
     * The cpu gadget container.
     */
    private ListView mCpuGadgetContainer;

    /**
     * The cpu benchmark.
     */
    private CpuBenchmark mCpuBenchmark;

    /**
     * The button to stop the benchmark.
     */
    private Button mStopButton;

    /**
     * The cpu progress listener.
     */
    private Benchmark.ProgressListener mCpuProgressListener;

    /**
     * The primary application collection task.
     */
    private ApplicationCollectionTask mApplicationCollectionTask;

    /**
     * The measurement listener.
     */
    private ApplicationCollectionTask.MeasurementListener mApplicationMeasurementListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benchmark_cpu);
        initialize();
        setupButtons();
        getSupportActionBar().setTitle(getString(R.string.cpu_benchmark));
        mCpuDataTextView = (TextView) findViewById(R.id.benchmark_cpu_text_view);
        mCpuGadgetContainer = (ListView) findViewById(R.id.process_list);
        View header = getLayoutInflater().inflate(R.layout.gadget_cpu_header, null);
        mCpuGadgetContainer.addHeaderView(header);
        if (Permissions.getInstance().requestSettingsPermission(this)) {
            startBenchmark();
        }
    }

    protected void startBenchmark() {
        mCpuBenchmark = new CpuBenchmark(this, BenchmarkConstants.CPU_DURATION_STEP, BenchmarkConstants.CPU_STEP);
        mCpuProgressListener = new Benchmark.ProgressListener() {
            @Override
            public void onTick(long millisUntilFinished) {
                updateDuration(millisUntilFinished);
            }

            @Override
            public void onTimerComplete() {
                onCountdownTimerComplete();
            }

            @Override
            public void onProgress(int progress) {
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCpuDataTextView != null) {
                            String benchmarkText = getString(R.string.benchmark_in_progress);
                            mCpuBenchmark.lockData();
                            for (Point point : mCpuBenchmark.getPowerData()) {
                                benchmarkText += String.format(getString(R.string.cpu_load_data_template), (int) point.getX(), getPowerFormatter().format(point.getY()));
                            }
                            mCpuBenchmark.unlockData();
                            mCpuDataTextView.setText(benchmarkText);
                        }
                    }
                });
            }

            @Override
            public void onComplete() {
                onBenchmarkComplete();
            }
        };
        mCpuBenchmark.start();
        mCpuBenchmark.registerProgressListener(mCpuProgressListener);
        onBenchmarkStarted();
    }

    @Override
    protected void showBenchmarkResults() {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (mCpuDataTextView != null) {
                    String benchmarkText = getString(R.string.benchmark_complete);
                    mCpuBenchmark.lockData();
                    for (Point point : mCpuBenchmark.getPowerData()) {
                        benchmarkText += String.format(getString(R.string.cpu_load_data_template), (int) point.getX(), getPowerFormatter().format(point.getY()));
                    }
//                            for (Point point : mCpuBenchmark.getCpuFrequencyData()) {
//                                benchmarkText += String.format(getString(R.string.cpu_frequency_data_template), (int)point.getX(), mDurationFormatter.format(point.getY()));
//                            }
                    mCpuBenchmark.unlockData();
                    mCpuDataTextView.setText(benchmarkText);
                }
                final Model model = mCpuBenchmark.getModel();
                if (model != null) {
                    mApplicationCollectionTask = new ApplicationCollectionTask(CpuBenchmarkActivity.this);
                    final ArrayList<Process> processes = mApplicationCollectionTask.getProcesses();
                    final ProcessAdapter processAdapter = new ProcessAdapter(CpuBenchmarkActivity.this, mApplicationCollectionTask, processes, model);
                    mApplicationMeasurementListener = new ApplicationCollectionTask.MeasurementListener() {
                        @Override
                        public void onMeasurementReceived() {
                            getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    mApplicationCollectionTask.updateProcesses();
                                    mApplicationCollectionTask.lock();
                                    Collections.sort(processes);
                                    mApplicationCollectionTask.unlock();
                                    processAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    };
                    ListView processList = (ListView) findViewById(R.id.process_list);
                    processList.setAdapter(processAdapter);
                    mApplicationCollectionTask.registerMeasurementListener(mApplicationMeasurementListener);
                    mApplicationCollectionTask.start();
                    mCpuGadgetContainer.setVisibility(View.VISIBLE);
                }
                if (mStopButton != null) {
                    mStopButton.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onPermissionGranted(int permission) {
        if (permission == DeviceConstants.PERMISSIONS_WRITE_SETTINGS) {
            startBenchmark();
        }
    }

    @Override
    protected void onPermissionDenied(int permission) {
        if (permission == DeviceConstants.PERMISSIONS_WRITE_SETTINGS) {
            finish();
        }
    }

    /**
     * Setup the buttons that are used in this activity.
     */
    protected void setupButtons() {
        mStopButton = (Button) findViewById(R.id.button_stop);
        if (mStopButton != null) {
            mStopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onServiceBound() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCpuBenchmark != null) {
            mCpuBenchmark.registerProgressListener(mCpuProgressListener);
        }
        if (mApplicationCollectionTask != null) {
            mApplicationCollectionTask.registerMeasurementListener(mApplicationMeasurementListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCpuBenchmark != null) {
            mCpuBenchmark.unregisterProgressListener(mCpuProgressListener);
        }
        if (mApplicationCollectionTask != null) {
            mApplicationCollectionTask.unregisterMeasurementListener(mApplicationMeasurementListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCpuBenchmark != null) {
            mCpuBenchmark.stop();
        }
        onBenchmarkStopped();
    }
}
