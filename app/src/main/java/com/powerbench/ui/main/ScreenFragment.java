package com.powerbench.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.powerbench.R;
import com.powerbench.constants.BenchmarkConstants;
import com.powerbench.model.BatteryModel;
import com.powerbench.model.ModelManager;
import com.powerbench.ui.benchmark.ScreenTestActivity;
import com.powerbench.ui.theme.Theme;

/**
 * Fragment for showing the screen gadget.
 */
public class ScreenFragment extends TabFragment {

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
     * The icon for running the screen test.
     */
    private ImageView mScreenTestIcon;

    /**
     * The button for running the screen test.
     */
    private Button mScreenTestButton;

    /**
     * The seek bar for controlling the brightness.
     */
    private SeekBar mScreenBrightnessSeekBar;

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
        mScreenTestButton = (Button) view.findViewById(R.id.button_screen_test);
        mScreenTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ScreenTestActivity.class));
            }
        });
        mScreenBrightnessSeekBar = (SeekBar) view.findViewById(R.id.gadget_screen_brightness_slider);
        int brightness = BenchmarkConstants.MAX_BRIGHTNESS;
        try {
            brightness = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
        }
        mScreenBrightnessSeekBar.setProgress(brightness);
        mBatteryModel.setScreenBrightness(brightness);
        mScreenBrightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int brightness, boolean fromUser) {
                Settings.System.putInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
                mBatteryModel.setScreenBrightness(brightness);
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
        if (mScreenTestButton != null) {
            mScreenTestButton.setBackgroundResource(mTheme.getButtonResource());
        }
    }

    @Override
    public void refresh() {
        if (getActivity() == null)
            return;

        if (mBatteryModel == null) {
            mBatteryModel = ModelManager.getInstance().getBatteryModel(getActivity());
        }

        if (mBatteryModel.getScreenModel() == null) {
            mWelcomeContainer.setVisibility(View.VISIBLE);
            mGadgetContainer.setVisibility(View.GONE);
        } else {
            mWelcomeContainer.setVisibility(View.GONE);
            mGadgetContainer.setVisibility(View.VISIBLE);
        }
    }
}