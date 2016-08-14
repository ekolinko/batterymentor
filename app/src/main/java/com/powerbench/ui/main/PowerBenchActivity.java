package com.powerbench.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.powerbench.R;
import com.powerbench.collectionmanager.ApplicationCollectionTask;
import com.powerbench.collectionmanager.CollectionManager;
import com.powerbench.collectionmanager.CollectionTask;
import com.powerbench.constants.Constants;
import com.powerbench.constants.UIConstants;
import com.powerbench.datamanager.Point;
import com.powerbench.device.Device;
import com.powerbench.device.Permissions;
import com.powerbench.model.BatteryModel;
import com.powerbench.model.ModelManager;
import com.powerbench.sensors.ChargerManager;
import com.powerbench.settings.Settings;
import com.powerbench.ui.common.CommonActivity;
import com.powerbench.ui.common.CommonFragment;
import com.powerbench.ui.settings.SettingsActivity;
import com.powerbench.ui.theme.Theme;
import com.powerbench.ui.tutorial.TutorialActivity;
import com.powerbench.utils.Utils;

import java.text.DecimalFormat;

/**
 * The main powerbench activity that allows a user to view battery power consumption and charging
 * rate in realtime.
 */
public class PowerBenchActivity extends CommonActivity {

    /**
     * The primary battery collection task.
     */
    private CollectionTask mPowerCollectionTask;

    /**
     * The drawer layout.
     */
    private DrawerLayout mDrawerLayout;

    /**
     * The drawer toggle for the action bar.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     * The view pager.
     */
    private ViewPager mViewPager;

    /**
     * The pager adapter
     */
    private PagerAdapter mPagerAdapter;

    /**
     * The list of tab fragments.
     */
    private CommonFragment[] mTabFragments;

    /**
     * The power tab fragment.
     */
    private PowerFragment mPowerFragment;

    /**
     * The screen tab fragment.
     */
    private ScreenFragment mScreenFragment;

    /**
     * The apps tab fragment.
     */
    private AppsFragment mAppsFragment;

    /**
     * The list of buttons representing the pager adapter tabs.
     */
    private Button[] mPagerTabs;

    /**
     * The battery life container.
     */
    private View mBatteryLifeContainer;

    /**
     * The battery life view.
     */
    private TextView mBatteryLife;

    /**
     * The battery life remaining label.
     */
    private TextView mBatteryLifeLabel;

    /**
     * The battery status menu item.
     */
    private MenuItem mBatteryStatusMenuItem;

    /**
     * The measurement listener.
     */
    private CollectionTask.MeasurementListener mMeasurementListener;

    /**
     * The primary application collection task.
     */
    private ApplicationCollectionTask mApplicationCollectionTask;

    /**
     * The handler used to measure the UI.
     */
    private Handler mHandler;

    /**
     * The current median.
     */
    private double mPower;

    /**
     * The battery model;
     */
    private BatteryModel mBatteryModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(getApplicationContext(), getString(R.string.advertising_id));
        boolean showTutorialActivity = Settings.getInstance().getShowTutorial(this) || !Permissions.getInstance().isSettingsPermissionGranted(this);
        if (showTutorialActivity) {
            startActivityForResult(new Intent(this, TutorialActivity.class), UIConstants.TUTORIAL_REQUEST_CODE);
        }
        setContentView(R.layout.activity_main);
        setupNavigationDrawer();
        initialize();
        String version = Constants.EMPTY_STRING;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        getSupportActionBar().setTitle(getString(R.string.app_name) + Constants.SPACE + version);
        mHandler = new Handler();
        mPowerFragment = new PowerFragment();
        mScreenFragment = new ScreenFragment();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            mAppsFragment = new AppsFragment();
            mTabFragments = new CommonFragment[] { mPowerFragment, mScreenFragment, mAppsFragment };
        } else {
            mTabFragments = new CommonFragment[] { mPowerFragment, mScreenFragment };
        }

        mPagerAdapter = new PowerbenchPagerAdapter(getSupportFragmentManager(), mTabFragments);
        mViewPager = (ViewPager) findViewById(R.id.powerbench_pager);
        mViewPager.setAdapter(mPagerAdapter);
        mBatteryLifeContainer = findViewById(R.id.battery_life_container);
        mBatteryLife = (TextView) findViewById(R.id.battery_life);
        mBatteryLifeLabel = (TextView) findViewById(R.id.battery_life_label);
        setupTabs();
        applyTheme(getThemeManager().getCurrentTheme(this));
        ModelManager.getInstance().initialize(this);
        mBatteryModel = ModelManager.getInstance().getBatteryModel(this);
        mBatteryModel.registerOnModelChangedListener(new BatteryModel.OnModelChangedListener() {
            @Override
            public void onModelChanged() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateBatteryLife();
                    }
                });
            }
        });
        mMeasurementListener = new CollectionTask.MeasurementListener() {
            @Override
            public void onMeasurementReceived(final Point point) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mPowerFragment.updatePowerViews(false);
                    }
                });
            }
        };

        mPowerCollectionTask = CollectionManager.getInstance().getPowerCollectionTask(this);
        mApplicationCollectionTask = CollectionManager.getInstance().getApplicationCollectionTask(this);
        mPowerCollectionTask.start();
        mApplicationCollectionTask.start();
        Device.getInstance().getBatteryCapacity(this);
    }

    /**
     * Setup the navigation drawer.
     */
    private void setupNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final FrameLayout contentFrame = (FrameLayout)findViewById(R.id.powerbench_power_container);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.app_name,
                R.string.app_name) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                contentFrame.setTranslationX(slideOffset * drawerView.getWidth());
                mDrawerLayout.bringChildToFront(drawerView);
                mDrawerLayout.requestLayout();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_menu);

        Button tutorialButton = (Button) findViewById(R.id.button_tutorial);
        if (tutorialButton != null) {
            tutorialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Settings.getInstance().setShowTutorial(PowerBenchActivity.this, true);
                    startActivityForResult(new Intent(PowerBenchActivity.this, TutorialActivity.class), UIConstants.TUTORIAL_REFRESH_REQUEST_CODE);
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
            });
        }

        Button settingsButton = (Button) findViewById(R.id.button_settings);
        if (settingsButton != null) {
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PowerBenchActivity.this, SettingsActivity.class));
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
            });
        }

        Button exitButton = (Button) findViewById(R.id.button_exit);
        if (exitButton != null) {
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    /**
     * Setup the tabs.
     */
    protected void setupTabs() {
        Button powerTab = (Button) findViewById(R.id.powerbench_tab_power);
        Button displayTab = (Button) findViewById(R.id.powerbench_tab_display);
        Button appsTab = (Button) findViewById(R.id.powerbench_tab_apps);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            mPagerTabs = new Button[] { powerTab, displayTab, appsTab };
        } else {
            appsTab.setVisibility(View.GONE);
            mPagerTabs = new Button[] { powerTab, displayTab };
        }
        for (int position = 0; position < mPagerTabs.length; position++) {
            Button button = mPagerTabs[position];
            final int item = position;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(item);
                    updateTabs(item);
                }
            });
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        updateTabs(0);
    }

    /**
     * Update the tabs with the specified position as the selected item.
     */
    protected void updateTabs(int item) {
        for (int position = 0; position < mPagerTabs.length; position++) {
            Button button = mPagerTabs[position];
            button.setEnabled(position != item);
        }
    }

    /**
     * Update the remaining battery life.
     */
    protected void updateBatteryLife() {
        double batteryLife = mBatteryModel.getBatteryLife();
        String value = Utils.convertBatteryLifeToSimpleString(this, batteryLife);
        if (getBatteryLevel() == Constants.INT_PERCENT && isChargerConnected()) {
            value = getString(R.string.fully_charged);
            mBatteryLifeLabel.setVisibility(View.GONE);
        } else if (batteryLife <= 0 && isChargerConnected()) {
            value = getString(R.string.not_charging);
            mBatteryLifeLabel.setVisibility(View.GONE);
        } else if (batteryLife >= UIConstants.MAX_BATTERY_LIFE) {
            value = String.format(getString(R.string.value_units_template), getString(R.string.max_battery_life), getString(R.string.hours));
            mBatteryLifeLabel.setVisibility(View.VISIBLE);
        } else if (Double.isInfinite(batteryLife)) {
            value = String.format(getString(R.string.value_units_template), getString(R.string.invalid_value), getString(R.string.hours));
            mBatteryLifeLabel.setVisibility(View.GONE);
        }
        mBatteryLife.setText(value);
    }

    /**
     * Apply the theme to this activity.
     */
    @Override
    protected void applyTheme(Theme theme) {
        super.applyTheme(theme);

        // Apply the theme to the battery life container.
        if (mBatteryLifeContainer != null) {
            mBatteryLifeContainer.setBackgroundResource(theme.getColorResource());
        }
        // Apply the theme to the tabs
        if (mPagerTabs != null) {
            for (Button button : mPagerTabs) {
                button.setTextColor(ContextCompat.getColorStateList(this, theme.getTabTextColorResource()));
                button.setBackgroundResource(theme.getTabDrawableResource());
            }
        }
        // Apply the theme to the fragments
        if (mTabFragments != null) {
            for (CommonFragment tabFragment : mTabFragments) {
                tabFragment.applyTheme(theme);
            }
        }
    }

    /**
     * Refresh all the tab fragments that are part of this view.
     */
    private void refreshFragments() {
        for (CommonFragment tabFragment : mTabFragments) {
            tabFragment.refresh();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId() == R.id.menu_battery_status) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, getAppTheme().getDialogStyleResource()).setTitle(getString(R.string.more_details)).
                    setMessage(R.string.test_screen_more_details)
                    .setPositiveButton(R.string.close, null);
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onServiceBound() {
        getService().startNotification();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mBatteryStatusMenuItem = menu.findItem(R.id.menu_battery_status);
        if (mBatteryStatusMenuItem != null) {
            setTitle(String.format(getString(R.string.value_percent_template), Integer.toString(getBatteryLevel())));
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPowerCollectionTask.registerMeasurementListener(mMeasurementListener);
        ChargerManager.getInstance().registerChargerListener(this, ModelManager.getInstance());
        refreshFragments();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPowerCollectionTask.unregisterMeasurementListener(mMeasurementListener);
        ChargerManager.getInstance().unregisterChargerListener(this, ModelManager.getInstance());
    }

    @Override
    public void onDestroy() {
        mApplicationCollectionTask.stop();
        getService().cancelNotification();
        super.onDestroy();
    }

    @Override
    public void onChargerConnectedUIThread() {
        super.onChargerConnectedUIThread();
        for (CommonFragment tabFragment : mTabFragments) {
            tabFragment.onChargerConnected();
        }
        if (mBatteryLifeLabel != null) {
            mBatteryLifeLabel.setText(getString(R.string.battery_life_until_full));
            if (getBatteryLevel() == Constants.INT_PERCENT)
                mBatteryLifeLabel.setVisibility(View.GONE);
        }
        if (mBatteryStatusMenuItem != null) {
            mBatteryStatusMenuItem.setIcon(R.drawable.battery_charging);
        }
    }

    @Override
    public void onChargerDisconnectedUIThread() {
        super.onChargerDisconnectedUIThread();
        for (CommonFragment tabFragment : mTabFragments) {
            tabFragment.onChargerDisconnected();
        }
        if (mBatteryLifeLabel != null) {
            mBatteryLifeLabel.setText(getString(R.string.battery_life_remaining));
            mBatteryLifeLabel.setVisibility(View.VISIBLE);
        }
        if (mBatteryStatusMenuItem != null) {
            mBatteryStatusMenuItem.setIcon(R.drawable.battery_discharging);
        }
    }

    @Override
    public void onBatteryLevelChanged(int level) {
        super.onBatteryLevelChanged(level);
        if (mBatteryStatusMenuItem != null) {
            mBatteryStatusMenuItem.setTitle(String.format(getString(R.string.value_percent_template), Integer.toString(level)));
        }
    }

    /**
     * The pager adapter for showing the various modes of powerbench.
     */
    public class PowerbenchPagerAdapter extends FragmentPagerAdapter {

        /**
         * The array of fragments supported by this pager adapter.
         */
        private CommonFragment[] mFragments;

        public PowerbenchPagerAdapter(FragmentManager fm, CommonFragment[] fragments) {
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
