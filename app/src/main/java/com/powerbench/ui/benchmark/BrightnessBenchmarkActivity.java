package com.powerbench.ui.benchmark;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.powerbench.R;
import com.powerbench.benchmarks.Benchmark;
import com.powerbench.benchmarks.BrightnessBenchmark;
import com.powerbench.collectionmanager.CollectionManager;
import com.powerbench.constants.BenchmarkConstants;
import com.powerbench.constants.DeviceConstants;
import com.powerbench.datamanager.Point;
import com.powerbench.datamanager.Statistics;
import com.powerbench.device.Device;
import com.powerbench.device.Permissions;
import com.powerbench.model.Model;
import com.powerbench.sensors.CollectionTask;
import com.powerbench.ui.common.CommonActivity;

import java.text.DecimalFormat;

/**
 * The main powerbench activity that allows a user to view battery power consumption and charging
 * rate in realtime.
 */
public class BrightnessBenchmarkActivity extends CommonActivity {

    /**
     * The power value text view.
     */
    private TextView mPowerTextView;

    /**
     * The brightness data text view.
     */
    private TextView mBrightnessDataTextView;

    /**
     * The brightness gadget container.
     */
    private LinearLayout mBrightnessGadgetContainer;

    /**
     * The brightness gadget power value.
     */
    private TextView mBrightnessGadgetPowerValue;

    /**
     * The brightness gadget slider.
     */
    private SeekBar mBrightnessGadgetSeekBar;

    /**
     * The brightness benchmark.
     */
    private BrightnessBenchmark mBrightnessBenchmark;

    /**
     * The button to stop the benchmark.
     */
    private Button mStopButton;

    /**
     * The brightness progress listener.
     */
    private Benchmark.ProgressListener mBrightnessProgressListener;

    /**
     * The primary battery collection task.
     */
    private CollectionTask mPowerCollectionTask;

    /**
     * The statistics associated with the battery collection task.
     */
    private Statistics mBatteryStatistics;

    /**
     * The measurement listener.
     */
    private CollectionTask.MeasurementListener mMeasurementListener;

    /**
     * The handler used to update the UI.
     */
    private Handler mHandler;

    /**
     * The current value.
     */
    private double mValue;

    /**
     * The power formatter.
     */
    private DecimalFormat mPowerFormatter;

    /**
     * The battery life formatter.
     */
    private DecimalFormat mBatteryLifeFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benchmark_brightness);
        initialize();
        setupButtons();
        getSupportActionBar().setTitle(getString(R.string.brightness_benchmark));
        mHandler = new Handler();
        mPowerTextView = (TextView) findViewById(R.id.powerbench_power_value);
        mBrightnessDataTextView = (TextView) findViewById(R.id.benchmark_brightness_text_view);
        mBrightnessGadgetContainer = (LinearLayout) findViewById(R.id.brightness_gadget_container);
        mBrightnessGadgetPowerValue = (TextView) findViewById(R.id.brightness_gadget_power_value);
        mBrightnessGadgetSeekBar = (SeekBar) findViewById(R.id.brightness_gadget_slider);
        mPowerFormatter = new DecimalFormat(getString(R.string.format_power));
        mBatteryLifeFormatter = new DecimalFormat(getString(R.string.format_battery_life));
        mMeasurementListener = new CollectionTask.MeasurementListener() {
            @Override
            public void onMeasurementReceived(final Point point) {
                if (mBatteryStatistics != null) {
                    mValue = Math.abs(mBatteryStatistics.getAverage());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mPowerTextView != null) {
                                String value = String.format(getString(R.string.value_units_template), mPowerFormatter.format(mValue), getString(R.string.milliwatts));
                                mPowerTextView.setText(value);
                            }
                        }
                    });
                }
            }
        };
        mPowerCollectionTask = CollectionManager.getInstance().getPowerCollectionTask();
        mBatteryStatistics = mPowerCollectionTask.getStatistics();
        mPowerCollectionTask.start();
        if (Permissions.getInstance().requestSettingsPermission(this)) {
            startBenchmark();
        }
    }

    protected void startBenchmark() {
        mBrightnessBenchmark = new BrightnessBenchmark(this, BenchmarkConstants.BRIGHTNESS_DURATION_STEP, BenchmarkConstants.BRIGHTNESS_STEP);
        mBrightnessProgressListener = new Benchmark.ProgressListener() {
            @Override
            public void onProgress(int progress) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mBrightnessDataTextView != null) {
                            String benchmarkText = getString(R.string.benchmark_in_progress);
                            mBrightnessBenchmark.lockData();
                            for (Point point : mBrightnessBenchmark.getBrightnessData()) {
                                benchmarkText += String.format(getString(R.string.brightness_data_template), (int)point.getX(), mPowerFormatter.format(point.getY()));
                            }
                            mBrightnessBenchmark.unlockData();
                            mBrightnessDataTextView.setText(benchmarkText);
                        }
                    }
                });
            }

            @Override
            public void onComplete() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mBrightnessDataTextView != null) {
                            String benchmarkText = getString(R.string.benchmark_complete);
                            mBrightnessBenchmark.lockData();
                            for (Point point : mBrightnessBenchmark.getBrightnessData()) {
                                benchmarkText += String.format(getString(R.string.brightness_data_template), (int)point.getX(), mPowerFormatter.format(point.getY()));
                            }
                            mBrightnessBenchmark.unlockData();
                            mBrightnessDataTextView.setText(benchmarkText);
                        }
                        final Model model = mBrightnessBenchmark.getModel();
                        if (model != null) {
                            int brightness = BenchmarkConstants.MAX_BRIGHTNESS;
                            try {
                                brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                            } catch (Settings.SettingNotFoundException e) {
                            }
                            mBrightnessGadgetSeekBar.setProgress(brightness);
                            final double batteryCapacity = Device.getInstance().getBatteryCapacity(BrightnessBenchmarkActivity.this);
                            double power = model.getY(brightness);
                            double batteryLife = batteryCapacity / power;
                            String value = String.format(getString(R.string.value_units_template), mBatteryLifeFormatter.format(batteryLife), getString(R.string.hours));
                            mBrightnessGadgetPowerValue.setText(value);
                            mBrightnessGadgetSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int brightness, boolean fromUser) {
                                    Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
                                    double power = model.getY(brightness);
                                    double batteryLife = batteryCapacity / power;
                                    String value = String.format(getString(R.string.value_units_template), mBatteryLifeFormatter.format(batteryLife), getString(R.string.hours));
                                    mBrightnessGadgetPowerValue.setText(value);
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                }
                            });
                            mBrightnessGadgetContainer.setVisibility(View.VISIBLE);
                        }
                        if (mStopButton != null) {
                            mStopButton.setVisibility(View.GONE);
                        }
                    }
                });
            }
        };
        mBrightnessBenchmark.start();
        mBrightnessBenchmark.registerProgressListener(mBrightnessProgressListener);
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
        mPowerCollectionTask.registerMeasurementListener(mMeasurementListener);
        if (mBrightnessBenchmark != null) {
            mBrightnessBenchmark.registerProgressListener(mBrightnessProgressListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPowerCollectionTask.unregisterMeasurementListener(mMeasurementListener);
        if (mBrightnessBenchmark != null) {
            mBrightnessBenchmark.unregisterProgressListener(mBrightnessProgressListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBrightnessBenchmark != null) {
            mBrightnessBenchmark.stop();
        }
    }

    @Override
    public void onChargerConnected() {
        mBatteryStatistics.clearRecentData();
    }

    @Override
    public void onChargerDisconnected() {
        mBatteryStatistics.clearRecentData();
    }
}
