package com.powerbench.ui.benchmark;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.powerbench.R;
import com.powerbench.benchmarks.Benchmark;
import com.powerbench.benchmarks.BrightnessBenchmark;
import com.powerbench.constants.BenchmarkConstants;
import com.powerbench.constants.Constants;
import com.powerbench.constants.DeviceConstants;
import com.powerbench.datamanager.Point;
import com.powerbench.device.Device;
import com.powerbench.device.Permissions;
import com.powerbench.model.Model;

import java.text.DecimalFormat;

/**
 * The main powerbench activity that allows a user to view battery power consumption and charging
 * rate in realtime.
 */
public class BrightnessBenchmarkActivity extends BenchmarkActivity {

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
        mBrightnessDataTextView = (TextView) findViewById(R.id.benchmark_brightness_text_view);
        mBrightnessGadgetContainer = (LinearLayout) findViewById(R.id.brightness_gadget_container);
        mBrightnessGadgetPowerValue = (TextView) findViewById(R.id.brightness_gadget_power_value);
        mBrightnessGadgetSeekBar = (SeekBar) findViewById(R.id.brightness_gadget_slider);
        mBatteryLifeFormatter = new DecimalFormat(getString(R.string.format_battery_life));
        if (Permissions.getInstance().requestSettingsPermission(this)) {
            startBenchmark();
        }
    }

    protected void startBenchmark() {
        mBrightnessBenchmark = new BrightnessBenchmark(this, BenchmarkConstants.BRIGHTNESS_DURATION_STEP, BenchmarkConstants.BRIGHTNESS_STEP);
        mBrightnessProgressListener = new Benchmark.ProgressListener() {
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
                        if (mBrightnessDataTextView != null) {
                            String benchmarkText = getString(R.string.benchmark_in_progress);
                            mBrightnessBenchmark.lockData();
                            for (Point point : mBrightnessBenchmark.getBrightnessData()) {
                                int brightness = (int)point.getX();
                                int percent = (int)Math.round((brightness * Constants.PERCENT) / (double)(BenchmarkConstants.MAX_BRIGHTNESS - BenchmarkConstants.MIN_BRIGHTNESS));
                                benchmarkText += String.format(getString(R.string.brightness_data_template), percent, getPowerFormatter().format(point.getY()));
                            }
                            mBrightnessBenchmark.unlockData();
                            mBrightnessDataTextView.setText(benchmarkText);
                        }
                    }
                });
            }

            @Override
            public void onComplete() {
                onBenchmarkComplete();
            }
        };
        mBrightnessBenchmark.start();
        mBrightnessBenchmark.registerProgressListener(mBrightnessProgressListener);
        onBenchmarkStarted();
    }

    @Override
    protected void showBenchmarkResults() {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (mBrightnessDataTextView != null) {
                    String benchmarkText = getString(R.string.benchmark_complete);
                    mBrightnessBenchmark.lockData();
                    for (Point point : mBrightnessBenchmark.getBrightnessData()) {
                        int brightness = (int)point.getX();
                        int percent = (int)Math.round((brightness * Constants.PERCENT) / (double)(BenchmarkConstants.MAX_BRIGHTNESS - BenchmarkConstants.MIN_BRIGHTNESS));
                        benchmarkText += String.format(getString(R.string.brightness_data_template), percent, getPowerFormatter().format(point.getY()));
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
        if (mBrightnessBenchmark != null) {
            mBrightnessBenchmark.registerProgressListener(mBrightnessProgressListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBrightnessBenchmark != null) {
            mBrightnessBenchmark.unregisterProgressListener(mBrightnessProgressListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBrightnessBenchmark != null) {
            mBrightnessBenchmark.stop();
            onBenchmarkStopped();
        }
    }
}
