package com.powerbench.ui.main;

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

import com.powerbench.R;
import com.powerbench.constants.BenchmarkConstants;
import com.powerbench.constants.Constants;
import com.powerbench.model.BatteryModel;
import com.powerbench.model.Model;
import com.powerbench.model.ModelManager;
import com.powerbench.ui.benchmark.ScreenTestActivity;
import com.powerbench.ui.common.CommonFragment;
import com.powerbench.ui.common.SunView;
import com.powerbench.ui.theme.Theme;
import com.powerbench.ui.tips.BatteryTipsActivity;

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
        mScreenTestIcon = (ImageView) view.findViewById(R.id.icon_screen_test);
        mMoreDetailsButton = (Button) view.findViewById(R.id.button_more_details);
        mMoreDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), mTheme.getDialogStyleResource()).setTitle(getString(R.string.screen_test)).
                setMessage(R.string.test_screen_more_details)
                .setPositiveButton(mBatteryModel == null ? R.string.test_screen_run : R.string.test_screen_rerun, new OnClickListener() {
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
                    startActivity(new Intent(getContext(), BatteryTipsActivity.class));
                }
            });
        }
        mScreenTestButton = (Button) view.findViewById(R.id.button_screen_test);
        if (mBatteryModel != null && mBatteryModel.getScreenModel() != null) {
            mScreenTestButton.setText(R.string.test_screen_rerun);
            mScreenTestButton.setVisibility(View.GONE);
            mMoreDetailsButton.setVisibility(View.GONE);
        } else {
            mBatteryTipsButton.setVisibility(View.GONE);
        }
        mScreenTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ScreenTestActivity.class));
            }
        });
        mSunView = (SunView) view.findViewById(R.id.gadget_screen_sun_view);
        if (mBatteryModel != null && (mBatteryModel.getScreenModel() != null || mBatteryModel.getCpuModel() != null || mBatteryModel.getCpuFrequencyModel() != null)) {
            mSunView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    String message = Constants.EMPTY_STRING;
                    Model screenModel = mBatteryModel.getScreenModel();
                    if (screenModel != null) {
                        DecimalFormat coefficientFormat = new DecimalFormat(getString(R.string.format_model_coefficient));
                        String slope = coefficientFormat.format(screenModel.getFirstCoefficient());
                        String intercept = coefficientFormat.format(screenModel.getIntercept());
                        message += String.format(getString(R.string.model_screen_template), slope, intercept) + Constants.NEWLINE;
                    }
                    Model cpuModel = mBatteryModel.getCpuModel();
                    if (cpuModel != null) {
                        DecimalFormat coefficientFormat = new DecimalFormat(getString(R.string.format_model_coefficient));
                        String slope = coefficientFormat.format(cpuModel.getFirstCoefficient());
                        String intercept = coefficientFormat.format(cpuModel.getIntercept());
                        message += String.format(getString(R.string.model_cpu_template), slope, intercept) + Constants.NEWLINE;
                    }
                    Model frequencyModel = mBatteryModel.getCpuFrequencyModel();
                    if (frequencyModel != null) {
                        DecimalFormat coefficientFormat = new DecimalFormat(getString(R.string.format_model_coefficient));
                        String a = coefficientFormat.format(frequencyModel.getFirstCoefficient());
                        String b = coefficientFormat.format(frequencyModel.getSecondCoefficient());
                        String c = coefficientFormat.format(frequencyModel.getIntercept());
                        message += String.format(getString(R.string.model_cpu_frequency_template), a, b, c);
                    }
                    if (!message.equals(Constants.EMPTY_STRING)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), mTheme.getDialogStyleResource()).setTitle(getString(R.string.model_dialog_title)).
                                setMessage(message)
                                .setPositiveButton(R.string.ok, null);
                        builder.show();
                    }
                    return true;
                }
            });
        }
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
                Settings.System.putInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
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

    /**
     * Apply the specified theme to this fragment.
     *
     * @param theme the theme to apply to this fragment.
     */
    @Override
    public void applyTheme(Theme theme) {
        mTheme = theme;
        if (mScreenTestIcon != null) {
            mScreenTestIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), mTheme.getScreenTestIconResource()));
        }
        if (mMoreDetailsButton != null) {
            mMoreDetailsButton.setTextColor(ContextCompat.getColor(getContext(), mTheme.getColorResource()));
        }
        if (mScreenTestButton != null) {
            mScreenTestButton.setTextColor(ContextCompat.getColor(getContext(), mTheme.getColorResource()));
        }
        if (mBatteryTipsButton != null) {
            mBatteryTipsButton.setTextColor(ContextCompat.getColor(getContext(), mTheme.getColorResource()));
            mBatteryTipsButton.setText(isChargerConnected() ? R.string.charger_tips : R.string.battery_tips);
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

    @Override
    public void refresh() {
        if (getActivity() == null)
            return;

//        if (mBatteryModel == null) {
//            mBatteryModel = ModelManager.getInstance().getBatteryModel(getActivity());
//            if (mBatteryModel != null && (mBatteryModel.getScreenModel() != null || mBatteryModel.getCpuModel() != null || mBatteryModel.getCpuFrequencyModel() != null)) {
//                mSunView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View view) {
//                        String message = Constants.EMPTY_STRING;
//                        Model screenModel = mBatteryModel.getScreenModel();
//                        if (screenModel != null) {
//                            DecimalFormat coefficientFormat = new DecimalFormat(getString(R.string.format_model_coefficient));
//                            String slope = coefficientFormat.format(screenModel.getFirstCoefficient());
//                            String intercept = coefficientFormat.format(screenModel.getIntercept());
//                            message += String.format(getString(R.string.model_screen_template), slope, intercept) + Constants.NEWLINE;
//                        }
//                        Model cpuModel = mBatteryModel.getCpuModel();
//                        if (cpuModel != null) {
//                            DecimalFormat coefficientFormat = new DecimalFormat(getString(R.string.format_model_coefficient));
//                            String slope = coefficientFormat.format(cpuModel.getFirstCoefficient());
//                            String intercept = coefficientFormat.format(cpuModel.getIntercept());
//                            message += String.format(getString(R.string.model_cpu_template), slope, intercept) + Constants.NEWLINE;
//                        }
//                        Model frequencyModel = mBatteryModel.getCpuFrequencyModel();
//                        if (frequencyModel != null) {
//                            DecimalFormat coefficientFormat = new DecimalFormat(getString(R.string.format_model_coefficient));
//                            String a = coefficientFormat.format(frequencyModel.getFirstCoefficient());
//                            String b = coefficientFormat.format(frequencyModel.getSecondCoefficient());
//                            String c = coefficientFormat.format(frequencyModel.getIntercept());
//                            message += String.format(getString(R.string.model_cpu_frequency_template), a, b, c);
//                        }
//                        if (!message.equals(Constants.EMPTY_STRING)) {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), mTheme.getDialogStyleResource()).setTitle(getString(R.string.model_dialog_title)).
//                                    setMessage(message)
//                                    .setPositiveButton(R.string.ok, null);
//                            builder.show();
//                        }
//                        return true;
//                    }
//                });
//            }
//        }

        if (mBatteryModel.getScreenModel() == null) {
            mWelcomeContainer.setVisibility(View.VISIBLE);
            mGadgetContainer.setVisibility(View.GONE);
        } else {
            mWelcomeContainer.setVisibility(View.GONE);
            mGadgetContainer.setVisibility(View.VISIBLE);
        }
    }
}