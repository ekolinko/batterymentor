package com.batterymentor.ui.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.batterymentor.R;
import com.batterymentor.constants.Constants;
import com.batterymentor.constants.DeviceConstants;
import com.batterymentor.constants.UIConstants;
import com.batterymentor.device.Permissions;
import com.batterymentor.settings.Settings;
import com.batterymentor.ui.common.CommonActivity;
import com.batterymentor.ui.common.CommonFragment;
import com.batterymentor.ui.theme.Theme;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity responsible for showing swipeable tutorial pages.
 */
public class TutorialActivity extends CommonActivity {

    /**
     * The array of tutorial fragments.
     */
    private TutorialFragment[] mTutorialFragments;

    /**
     * The view pager.
     */
    private ViewPager mViewPager;

    /**
     * The pager adapter
     */
    private PagerAdapter mPagerAdapter;

    /**
     * Flag indicating that the permission pages are shown.
     */
    private boolean mPermissionPagesShown;

    /**
     * The skip button.
     */
    private Button mSkipButton;

    /**
     * The next button.
     */
    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        initialize();
        mTutorialFragments = initializeTutorialFragments();
        mPagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager(), mTutorialFragments);
        mViewPager = (ViewPager) findViewById(R.id.powerbench_pager);
        mViewPager.setAdapter(mPagerAdapter);
        final int numPages = mTutorialFragments.length;
        mSkipButton = (Button) findViewById(R.id.powerbench_tutorial_button_skip);
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPermissionPagesShown) {
                    mViewPager.setCurrentItem(numPages - 1);
                } else {
                    finishAndSetShowTutorialFalse();
                }
            }
        });
        if (numPages <= 1) {
            mSkipButton.setVisibility(View.INVISIBLE);
        }
        mNextButton = (Button) findViewById(R.id.powerbench_tutorial_button_next);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = mViewPager.getCurrentItem();
                if (currentItem < numPages - 1) {
                    mViewPager.setCurrentItem(currentItem + 1);
                } else if (Permissions.getInstance().requestSettingsPermission(TutorialActivity.this)) {
                    finishAndSetShowTutorialFalse();
                }
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // There are two pages at the end of the carousel that cannot be skipped
                // when permissions need to be set
                int numNoSkipPages = 1;
                if (mPermissionPagesShown) {
                    numNoSkipPages = 1;
                }
                if (position >= numPages - numNoSkipPages) {
                    mSkipButton.setVisibility(View.INVISIBLE);
                } else {
                    mSkipButton.setVisibility(View.VISIBLE);
                }
                if (!mPermissionPagesShown && position == numPages - 1) {
                    mNextButton.setText(getString(R.string.finish));
                } else {
                    mNextButton.setText(getString(R.string.next));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (numPages == 0)
            finishAndSetShowTutorialFalse();
    }

    /**
     * Finish the activity and set the flag to show the tutorial to false in settings.
     */
    private void finishAndSetShowTutorialFalse() {
        Settings.getInstance().setShowTutorial(this, false);
        finish();
    }

    /**
     * Initialize the pages that are shown as part of the tutorial.
     */
    private TutorialFragment[] initializeTutorialFragments() {
        List<TutorialFragment> tutorialFragments = new ArrayList<TutorialFragment>();
        boolean showTutorial = Settings.getInstance().getShowTutorial(this);
        if (showTutorial) {
            TutorialFragment welcomeFragment = new TutorialFragment();
            welcomeFragment.setArguments(bundleArguments(R.string.tutorial_welcome_title, R.drawable.tutorial_welcome, R.string.tutorial_welcome_text));
            TutorialFragment realtimePowerFragment = new TutorialFragment();
            realtimePowerFragment.setArguments(bundleArguments(R.string.tutorial_power_tab_title, R.drawable.tutorial_battery_tab, R.string.tutorial_power_tab_text));
            TutorialFragment brightnessFragment = new TutorialFragment();
            brightnessFragment.setArguments(bundleArguments(R.string.tutorial_screen_title, R.drawable.tutorial_screen_tab, R.string.tutorial_screen_text));
            TutorialFragment tipsFragment = new TutorialFragment();
            tipsFragment.setArguments(bundleArguments(R.string.tutorial_battery_tips_title, R.drawable.tutorial_battery_tips, R.string.tutorial_battery_tips_text));
            TutorialFragment chargerFragment = new TutorialFragment();
            chargerFragment.setArguments(bundleArguments(R.string.tutorial_charger_title, R.drawable.tutorial_charger, R.string.tutorial_charger_text));
            tutorialFragments.add(welcomeFragment);
            tutorialFragments.add(realtimePowerFragment);
            tutorialFragments.add(brightnessFragment);
            tutorialFragments.add(tipsFragment);
            tutorialFragments.add(chargerFragment);
        }
        mPermissionPagesShown = !Permissions.getInstance().isSettingsPermissionGranted(this);
        if (mPermissionPagesShown) {
            TutorialFragment settingsFragment = new TutorialFragment();
            settingsFragment.setArguments(bundleArguments(R.string.tutorial_settings_title, R.drawable.tutorial_modify_system_settings, R.string.tutorial_settings_text));
            tutorialFragments.add(settingsFragment);
        }

        return tutorialFragments.toArray(new TutorialFragment[0]);
    }

    /**
     * Bundle the specified title resource id, image resource id, and text resource id into a
     * bundle resource.
     */
    private Bundle bundleArguments(int titleId, int imageId, int textId) {
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.argument_tutorial_title_res_id), titleId);
        bundle.putInt(getString(R.string.argument_tutorial_image_res_id), imageId);
        bundle.putInt(getString(R.string.argument_tutorial_text_res_id), textId);
        return bundle;
    }

    protected void onPermissionGranted(int permission) {
        if (permission == DeviceConstants.PERMISSIONS_WRITE_SETTINGS) {
            finishAndSetShowTutorialFalse();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(UIConstants.CLOSE_APP, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void applyTheme(Theme theme) {
//        super.applyTheme(theme);
//        if (mNextButton != null) {
//            mNextButton.setTextColor(ContextCompat.getColor(this, theme.getColorResource()));
//        }
//        if (mSkipButton != null) {
//            mSkipButton.setTextColor(ContextCompat.getColor(this, theme.getColorResource()));
//        }
//        for (TutorialFragment tutorialFragment : mTutorialFragments) {
//            tutorialFragment.applyTheme(theme);
//        }
    }

    /**
     * The pager adapter for showing the tutorial pages.
     */
    public class TutorialPagerAdapter extends FragmentPagerAdapter {

        /**
         * The array of fragments supported by this pager adapter.
         */
        private CommonFragment[] mFragments;

        public TutorialPagerAdapter(FragmentManager fm, CommonFragment[] fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Constants.EMPTY_STRING;
        }
    }

}
