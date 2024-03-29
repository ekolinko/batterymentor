package com.batterymentor.ui.main;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.batterymentor.R;
import com.batterymentor.constants.BenchmarkConstants;
import com.batterymentor.constants.Constants;
import com.batterymentor.constants.UIConstants;
import com.batterymentor.device.Device;
import com.batterymentor.model.BatteryModel;
import com.batterymentor.model.Model;
import com.batterymentor.model.ModelManager;
import com.batterymentor.ui.benchmark.ScreenTestActivity;
import com.batterymentor.ui.common.CommonFragment;
import com.batterymentor.ui.common.SunView;
import com.batterymentor.ui.theme.Theme;
import com.batterymentor.ui.tips.BatteryTipsActivity;
import com.batterymentor.ui.tips.ChargingTipsActivity;

import java.text.DecimalFormat;

/**
 * Fragment for showing the screen gadget.
 */
public class ScreenFragment extends CommonFragment {

    /**
     * The welcome container.
     */
    private View mWelcomeContainer;

    /**
     * The gadget container.
     */
    private View mGadgetContainer;

    /**
     * The battery model.
     */
    private BatteryModel mBatteryModel;

    /**
     * The screen power value.
     */
    private TextView mScreenPowerValue;

    /**
     * The screen power label.
     */
    private TextView mScreenPowerLabel;

    /**
     * The power estimation indicator
     */
    private TextView mPowerEstimationIndicator;

    /**
     * The icon that shows on the run screen test tab.
     */
    private ImageView mScreenTestIcon;

    /**
     * The button for reading more details.
     */
    private Button mMoreDetailsButton;

    /**
     * The button for running the screen test.
     */
    private Button mScreenTestButton;

    /**
     * The button for viewing the screen details.
     */
    private Button mScreenDetailsButton;

    /**
     * The button for viewing the battery tips.
     */
    private Button mBatteryTipsButton;

    /**
     * The seek bar for controlling the brightness.
     */
    private SeekBar mScreenBrightnessSeekBar;

    /**
     * The sun view.
     */
    private SunView mSunView;

    /**
     * The current theme.
     */
    private Theme mTheme;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen, container, false);
        mBatteryModel = ModelManager.getInstance().getBatteryModel(getActivity());
        mWelcomeContainer = view.findViewById(R.id.screen_test_welcome_container);
        mGadgetContainer = view.findViewById(R.id.screen_test_gadget_container);
        mScreenPowerValue = (TextView) view.findViewById(R.id.battery_mentor_screen_power);
        mScreenPowerLabel = (TextView) view.findViewById(R.id.battery_mentor_screen_power_label);
        mPowerEstimationIndicator = (TextView) view.findViewById(R.id.powerbench_power_estimation_indicator);
        mScreenTestIcon = (ImageView) view.findViewById(R.id.icon_screen_test);
        mMoreDetailsButton = (Button) view.findViewById(R.id.button_more_details);
        mMoreDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), mTheme.getDialogStyleResource()).setTitle(getString(R.string.screen_test)).
                setMessage(R.string.test_screen_more_details)
                .setPositiveButton(R.string.test_screen_run, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getContext(), ScreenTestActivity.class));
                    }
                })
                        .setNegativeButton(R.string.cancel, null);
                builder.show();
            }
        });
        mBatteryTipsButton = (Button) view.findViewById(R.id.button_battery_tips);
        if (mBatteryTipsButton != null) {
            mBatteryTipsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), isChargerConnected() ? ChargingTipsActivity.class : BatteryTipsActivity.class));
                }
            });
        }
        mScreenDetailsButton = (Button) view.findViewById(R.id.button_screen_details);
        mScreenDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScreenDetailsDialog();
            }
        });
        mScreenTestButton = (Button) view.findViewById(R.id.button_screen_test);
        mScreenTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ScreenTestActivity.class));
            }
        });
        mSunView = (SunView) view.findViewById(R.id.gadget_screen_sun_view);
//            mSunView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    String message = Constants.EMPTY_STRING;
//                    Model screenModel = mBatteryModel.getScreenModel();
//                    if (screenModel != null) {
//                        DecimalFormat coefficientFormat = new DecimalFormat(getString(R.string.format_model_coefficient));
//                        String slope = coefficientFormat.format(screenModel.getFirstCoefficient());
//                        String intercept = coefficientFormat.format(screenModel.getIntercept());
//                        message += String.format(getString(R.string.model_screen_template), slope, intercept) + Constants.NEWLINE;
//                    }
//                    Model cpuModel = mBatteryModel.getCpuModel();
//                    if (cpuModel != null) {
//                        DecimalFormat coefficientFormat = new DecimalFormat(getString(R.string.format_model_coefficient));
//                        String slope = coefficientFormat.format(cpuModel.getFirstCoefficient());
//                        String intercept = coefficientFormat.format(cpuModel.getIntercept());
//                        message += String.format(getString(R.string.model_cpu_template), slope, intercept) + Constants.NEWLINE;
//                    }
//                    Model frequencyModel = mBatteryModel.getCpuFrequencyModel();
//                    if (frequencyModel != null) {
//                        DecimalFormat coefficientFormat = new DecimalFormat(getString(R.string.format_model_coefficient));
//                        String a = coefficientFormat.format(frequencyModel.getFirstCoefficient());
//                        String b = coefficientFormat.format(frequencyModel.getSecondCoefficient());
//                        String c = coefficientFormat.format(frequencyModel.getIntercept());
//                        message += String.format(getString(R.string.model_cpu_frequency_template), a, b, c);
//                    }
//                    if (!message.equals(Constants.EMPTY_STRING)) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), mTheme.getDialogStyleResource()).setTitle(getString(R.string.model_dialog_title)).
//                                setMessage(message)
//                                .setPositiveButton(R.string.ok, null);
//                        builder.show();
//                    }
//                    return true;
//                }
//            });
        mScreenBrightnessSeekBar = (SeekBar) view.findViewById(R.id.gadget_screen_brightness_slider);
        int brightness = BenchmarkConstants.MAX_BRIGHTNESS;
        try {
            brightness = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
        }
        mScreenBrightnessSeekBar.setProgress(brightness);
        mSunView.setBrightness(brightness * Constants.PERCENT / BenchmarkConstants.MAX_BRIGHTNESS);
        mBatteryModel.setScreenBrightness(brightness);
        mScreenBrightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int brightness, boolean fromUser) {
                if (brightness < BenchmarkConstants.MIN_VISUAL_BRIGHTNESS)
                    brightness = BenchmarkConstants.MIN_VISUAL_BRIGHTNESS;
                Settings.System.putInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                Settings.System.putInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
                updateScreenPower();
                mBatteryModel.setScreenBrightness(brightness);
                mSunView.setBrightness(brightness * Constants.PERCENT / BenchmarkConstants.MAX_BRIGHTNESS);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        refresh();
        applyTheme(mTheme);
        setRetainInstance(true);
        return view;
    }

    protected Theme getAppTheme() {
        return mTheme;
    }

    /**
     * Apply the specified theme to this fragment.
     *
     * @param theme the theme to apply to this fragment.
     */
    @Override
    public void applyTheme(Theme theme) {
        mTheme = theme;
        if (mTheme != null) {
            if (mScreenPowerValue != null) {
                mScreenPowerValue.setTextColor(ContextCompat.getColor(getContext(), mTheme.getColorResource()));
            }
            if (mScreenTestIcon != null) {
                mScreenTestIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), mTheme.getScreenTestIconResource()));
            }
            if (mMoreDetailsButton != null) {
                mMoreDetailsButton.setTextColor(ContextCompat.getColor(getContext(), mTheme.getColorResource()));
            }
            if (mScreenTestButton != null) {
                mScreenTestButton.setTextColor(ContextCompat.getColor(getContext(), mTheme.getColorResource()));
            }
            if (mScreenDetailsButton != null) {
                mScreenDetailsButton.setTextColor(ContextCompat.getColor(getContext(), mTheme.getColorResource()));
            }
            if (mBatteryTipsButton != null) {
                mBatteryTipsButton.setTextColor(ContextCompat.getColor(getContext(), mTheme.getColorResource()));
                mBatteryTipsButton.setText(isChargerConnected() ? R.string.charger_tips_short : R.string.battery_tips_short);
            }
            if (mSunView != null) {
                mSunView.applyTheme(theme);
            }
            if (mScreenBrightnessSeekBar != null) {
                mScreenBrightnessSeekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getContext(), mTheme.getColorResource()), PorterDuff.Mode.MULTIPLY));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mScreenBrightnessSeekBar.getThumb().setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getContext(), mTheme.getColorResource()), PorterDuff.Mode.SRC_IN));
                }
            }
        }
    }

    @Override
    public void refresh() {
        if (getActivity() == null)
            return;

        if (mBatteryModel.getScreenModel() == null) {
            mWelcomeContainer.setVisibility(View.VISIBLE);
            mGadgetContainer.setVisibility(View.GONE);
            mBatteryTipsButton.setVisibility(View.GONE);
            mScreenDetailsButton.setVisibility(View.GONE);
            mScreenPowerValue.setVisibility(View.GONE);
            mScreenPowerLabel.setVisibility(View.GONE);
            mPowerEstimationIndicator.setVisibility(View.GONE);
            mScreenTestButton.setVisibility(View.VISIBLE);
            mMoreDetailsButton.setVisibility(View.VISIBLE);
        } else {
            mWelcomeContainer.setVisibility(View.GONE);
            mGadgetContainer.setVisibility(View.VISIBLE);
            mBatteryTipsButton.setVisibility(View.VISIBLE);
            mScreenDetailsButton.setVisibility(View.VISIBLE);
            mScreenPowerValue.setVisibility(View.VISIBLE);
            mScreenPowerLabel.setVisibility(View.VISIBLE);
            mPowerEstimationIndicator.setVisibility(Device.getInstance().isBatteryPowerEstimated() ? View.VISIBLE : View.GONE);
            mScreenTestButton.setVisibility(View.GONE);
            mMoreDetailsButton.setVisibility(View.GONE);
            if (mBatteryModel.getScreenModel() != null) {
                mSunView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showScreenDetailsDialog();
                    }
                });
            }
        }
        if (mBatteryTipsButton != null) {
            mBatteryTipsButton.setText(isChargerConnected() ? R.string.charger_tips_short : R.string.battery_tips_short);
        }
        updateScreenPower();
    }

    /**
     * Update the screen power value.
     */
    private void updateScreenPower() {
        if (mScreenPowerValue != null) {
            mScreenPowerValue.setText(getScreenPowerValueAsString());
        }
    }

    /**
     * Return the screen power value as a string.
     *
     * @return the screen power value as a string.
     */
    private String getScreenPowerValueAsString() {
        if (mBatteryModel != null && mBatteryModel.getScreenModel() != null) {
            Model screenModel = mBatteryModel.getScreenModel();
            int screenBrightness = getScreenBrightnessAsPercent();
            int screenPower = 0;
            if (screenBrightness != 0) {
                screenPower = (int)(Math.round(screenModel.getFirstCoefficient() * BenchmarkConstants.MAX_BRIGHTNESS * getScreenBrightnessAsPercent() / Constants.PERCENT));
            }
            return String.format(getString(R.string.value_units_template), Integer.toString(screenPower), getString(R.string.mW));
        } else {
            return getString(R.string.invalid_value);
        }
    }

    /**
     * Return the screen brightness as a percent.
     *
     * @return the screen brightness as a percent.
     */
    private int getScreenBrightnessAsPercent() {
        int brightness = BenchmarkConstants.MAX_BRIGHTNESS;
        try {
            brightness = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
        }
        return (int)((brightness * Constants.PERCENT) / BenchmarkConstants.MAX_BRIGHTNESS);
    }

    /**
     * Show the battery status dialog in the current theme.
     */
    private boolean showScreenDetailsDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View screenDetailsView = inflater.inflate(R.layout.dialog_screen_details, null);

        TextView screenPowerLabel = (TextView) screenDetailsView.findViewById(R.id.label_screen_power);
        if (screenPowerLabel != null) {
            screenPowerLabel.setTextColor(ContextCompat.getColor(getActivity(), getAppTheme().getColorResource()));
        }

        TextView brightnessLabel = (TextView) screenDetailsView.findViewById(R.id.label_brightness);
        if (brightnessLabel != null) {
            brightnessLabel.setTextColor(ContextCompat.getColor(getActivity(), getAppTheme().getColorResource()));
        }

        TextView screenSizeLabel = (TextView) screenDetailsView.findViewById(R.id.label_screen_size);
        if (screenSizeLabel != null) {
            screenSizeLabel.setTextColor(ContextCompat.getColor(getActivity(), getAppTheme().getColorResource()));
        }

        TextView screenResolutionLabel = (TextView) screenDetailsView.findViewById(R.id.label_screen_resolution);
        if (screenResolutionLabel != null) {
            screenResolutionLabel.setTextColor(ContextCompat.getColor(getActivity(), getAppTheme().getColorResource()));
        }

        TextView screenDensityLabel = (TextView) screenDetailsView.findViewById(R.id.label_screen_density);
        if (screenDensityLabel != null) {
            screenDensityLabel.setTextColor(ContextCompat.getColor(getActivity(), getAppTheme().getColorResource()));
        }

        TextView powerPerBrightnessLabel = (TextView) screenDetailsView.findViewById(R.id.label_power_per_brightness);
        if (powerPerBrightnessLabel != null) {
            powerPerBrightnessLabel.setTextColor(ContextCompat.getColor(getActivity(), getAppTheme().getColorResource()));
        }

        TextView maxPowerPerPixelLabel = (TextView) screenDetailsView.findViewById(R.id.label_power_per_pixel);
        if (maxPowerPerPixelLabel != null) {
            maxPowerPerPixelLabel.setTextColor(ContextCompat.getColor(getActivity(), getAppTheme().getColorResource()));
        }

        TextView screenPowerDetails = (TextView) screenDetailsView.findViewById(R.id.value_screen_power);
        if (screenPowerDetails != null) {
            screenPowerDetails.setText(getScreenPowerValueAsString());
            if (Device.getInstance().isBatteryPowerEstimated()) {
                screenPowerDetails.setText(getString(R.string.power_estimation_indicator) + screenPowerDetails.getText());
            }
            screenPowerDetails.setTextColor(ContextCompat.getColor(getActivity(), getAppTheme().getColorResource()));
        }

        TextView brightnessDetails = (TextView) screenDetailsView.findViewById(R.id.value_brightness);
        if (brightnessDetails != null) {
            brightnessDetails.setText(String.format(getString(R.string.value_percent_template), Integer.toString(getScreenBrightnessAsPercent())));
            brightnessDetails.setTextColor(ContextCompat.getColor(getActivity(), getAppTheme().getColorResource()));
        }

        TextView screenSizeDetails = (TextView) screenDetailsView.findViewById(R.id.value_screen_size);
        if (screenSizeDetails != null) {
            screenSizeDetails.setText(Device.getInstance().getScreenSize(getActivity()));
            screenSizeDetails.setTextColor(ContextCompat.getColor(getActivity(), getAppTheme().getColorResource()));
        }

        TextView screenDimensionsDetails = (TextView) screenDetailsView.findViewById(R.id.value_screen_dimensions);
        if (screenDimensionsDetails != null) {
            screenDimensionsDetails.setText(Device.getInstance().getScreenDimensions(getActivity()));
            screenDimensionsDetails.setTextColor(ContextCompat.getColor(getActivity(), getAppTheme().getColorResource()));
        }

        TextView screenDensityDetails = (TextView) screenDetailsView.findViewById(R.id.value_screen_density);
        if (screenDensityDetails != null) {
            screenDensityDetails.setText(Device.getInstance().getScreenDensity(getActivity()));
            screenDensityDetails.setTextColor(ContextCompat.getColor(getActivity(), getAppTheme().getColorResource()));
        }

        TextView powerPerBrightnessDetails = (TextView) screenDetailsView.findViewById(R.id.value_power_per_brightness);
        if (powerPerBrightnessDetails != null) {
            if (mBatteryModel != null && mBatteryModel.getScreenModel() != null) {
                Model screenModel = mBatteryModel.getScreenModel();
                double powerPerBrightness = (screenModel.getFirstCoefficient() * BenchmarkConstants.MAX_BRIGHTNESS / (double) Constants.PERCENT);
                DecimalFormat coefficientFormat = new DecimalFormat(getString(R.string.format_screen_coefficient));
                powerPerBrightnessDetails.setText(String.format(getString(R.string.value_units_template), coefficientFormat.format(powerPerBrightness), getString(R.string.mW_per_percent)));
            } else
                powerPerBrightnessDetails.setText(R.string.invalid_value);
            if (Device.getInstance().isBatteryPowerEstimated()) {
                powerPerBrightnessDetails.setText(getString(R.string.power_estimation_indicator) + powerPerBrightnessDetails.getText());
            }
            powerPerBrightnessDetails.setTextColor(ContextCompat.getColor(getActivity(), getAppTheme().getColorResource()));
        }

        TextView maxPowerPerPixelDetails = (TextView) screenDetailsView.findViewById(R.id.value_max_power_per_pixel);
        if (maxPowerPerPixelDetails != null) {
            if (mBatteryModel != null && mBatteryModel.getScreenModel() != null) {
                Model screenModel = mBatteryModel.getScreenModel();
                double maxPowerPerPixel = (screenModel.getFirstCoefficient() * BenchmarkConstants.MAX_BRIGHTNESS) / (double) Device.getInstance().getTotalScreenPixels(getActivity());
                DecimalFormat coefficientFormat = new DecimalFormat(getString(R.string.format_screen_coefficient_long));
                maxPowerPerPixelDetails.setText(String.format(getString(R.string.value_units_template), coefficientFormat.format(maxPowerPerPixel), getString(R.string.mW_per_pixel)));
            } else
                maxPowerPerPixelDetails.setText(R.string.invalid_value);
            if (Device.getInstance().isBatteryPowerEstimated()) {
                maxPowerPerPixelDetails.setText(getString(R.string.power_estimation_indicator) + maxPowerPerPixelDetails.getText());
            }
            maxPowerPerPixelDetails.setTextColor(ContextCompat.getColor(getActivity(), getAppTheme().getColorResource()));
        }

        final boolean showChargingTips = isChargerConnected();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getAppTheme().getDialogStyleResource()).setTitle(getString(R.string.screen_details))
                .setView(screenDetailsView)
                .setNegativeButton(R.string.close, null)
                .setPositiveButton(showChargingTips ? R.string.charger_tips : R.string.battery_tips, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(getActivity(), showChargingTips ? ChargingTipsActivity.class : BatteryTipsActivity.class), UIConstants.TAB_REQUEST_CODE);
                    }
                });
        builder.show();
        return true;
    }
}