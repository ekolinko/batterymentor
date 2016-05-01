package com.powerbench.ui.common.listeners;

import android.view.View;
import android.widget.TextView;

import com.powerbench.settings.Settings;

/**
 * Listener that handles a toggle setting.
 */
public class ToggleSettingListener implements View.OnClickListener {

    /**
     * The text view that contains the current setting.
     */
    private TextView mCurrentSettingTextView;

    /**
     * The list of options supported by the setting.
     */
    private String[] mSettingOptions;

    /**
     * The setting selected listener.
     */
    private SettingSelectedListener mSettingSelectedListener;

    public ToggleSettingListener(TextView currentSettingTextView, String[] settingOptions, SettingSelectedListener settingSelectedListener) {
        mCurrentSettingTextView = currentSettingTextView;
        mSettingOptions = settingOptions;
        mSettingSelectedListener = settingSelectedListener;
    }

    @Override
    public void onClick(View v) {
        int index = 0;
        String currentSetting = mCurrentSettingTextView.getText().toString();
        for (int i = 0; i < mSettingOptions.length; i++) {
            if (mSettingOptions[i].equals(currentSetting)) {
                index = i;
                break;
            }
        }
        if (++index >= mSettingOptions.length) {
            index = 0;
        }
        String newSetting = mSettingOptions[index];
        mCurrentSettingTextView.setText(newSetting);
        mSettingSelectedListener.onSettingSelected(newSetting);
    }

    /**
     * The listener that gets fired when a setting is selected.
     */
    public interface SettingSelectedListener {
        void onSettingSelected(String setting);
    }
}
