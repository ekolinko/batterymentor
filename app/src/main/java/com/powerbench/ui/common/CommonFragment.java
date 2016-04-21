package com.powerbench.ui.common;

import android.support.v4.app.Fragment;

import com.powerbench.ui.theme.Theme;

/**
 * Common fragment that is used by different parts of the app.
 */
public abstract class CommonFragment extends Fragment {

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
}
