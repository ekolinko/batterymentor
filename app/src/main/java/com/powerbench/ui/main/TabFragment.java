package com.powerbench.ui.main;

import android.support.v4.app.Fragment;

import com.powerbench.ui.theme.Theme;

/**
 * Fragment that can be used within a tab context.
 */
public abstract class TabFragment extends Fragment {

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
