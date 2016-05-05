package com.powerbench.ui.common;

import android.support.v4.app.Fragment;

import com.powerbench.ui.theme.Theme;

/**
 * Common fragment that is used by different parts of the app.
 */
public abstract class CommonFragment extends Fragment {

    /**
     * Flag indicating whether a charger is connected.
     */
    private boolean mChargerConnected = false;

    /**
     * Apply the specified theme to this fragment.
     *
     * @param theme the theme to apply to this fragment.
     */
    public void applyTheme(Theme theme) {
    }

    /**
     * Refresh this fragment.
     */
    public void refresh() {
    }

    /**
     * Notify the fragment of a connected charger.
     */
    public void onChargerConnected() {
        mChargerConnected = true;
        refresh();
    }

    /**
     * Notify the fragment of a disconnected charger.
     */
    public void onChargerDisconnected() {
        mChargerConnected = false;
        refresh();
    }

    /**
     * Return true if a charger is connected, false otherwise.
     *
     * @return true if a charger is connected, false otherwise.
     */
    public boolean isChargerConnected() {
        return mChargerConnected;
    }
}
