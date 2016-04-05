package com.powerbench.ui.main;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.powerbench.R;
import com.powerbench.collectionmanager.ApplicationCollectionTask;
import com.powerbench.collectionmanager.CollectionManager;
import com.powerbench.constants.Constants;
import com.powerbench.datamanager.Statistics;
import com.powerbench.device.Device;
import com.powerbench.collectionmanager.CollectionTask;
import com.powerbench.datamanager.Point;
import com.powerbench.model.BatteryModel;
import com.powerbench.model.ModelManager;
import com.powerbench.ui.common.CommonActivity;
import com.powerbench.ui.theme.Theme;
import com.powerbench.ui.theme.ThemeManager;

import java.text.DecimalFormat;

/**
 * The main powerbench activity that allows a user to view battery power consumption and charging
 * rate in realtime.
 */
public class PowerBenchActivity extends CommonActivity {

    /**
     * The theme manager.
     */
    private ThemeManager mThemeManager = ThemeManager.getInstance();

    /**
     * The primary battery collection task.
     */
    private CollectionTask mPowerCollectionTask;

    /**
     * The statistics associated with the battery collection task.
     */
    private Statistics mBatteryStatistics;

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
    private TabFragment[] mTabFragments;

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
     * The measurement listener.
     */
    private CollectionTask.MeasurementListener mMeasurementListener;

    /**
     * The primary application collection task.
     */
    private ApplicationCollectionTask mApplicationCollectionTask;

    /**
     * The battery life formatter.
     */
    private DecimalFormat mBatteryLifeFormatter;

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
        mAppsFragment = new AppsFragment();
        mTabFragments = new TabFragment[] { mPowerFragment, mScreenFragment, mAppsFragment };
        mPagerAdapter = new PowerbenchPagerAdapter(getSupportFragmentManager(), mTabFragments);
        mViewPager = (ViewPager) findViewById(R.id.powerbench_pager);
        mViewPager.setAdapter(mPagerAdapter);
        mBatteryLifeContainer = findViewById(R.id.battery_life_container);
        mBatteryLife = (TextView) findViewById(R.id.battery_life);
        mBatteryLifeLabel = (TextView) findViewById(R.id.battery_life_label);
        setupTabs();
        applyTheme();
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
        mBatteryLifeFormatter = new DecimalFormat(getString(R.string.format_battery_life));
        mMeasurementListener = new CollectionTask.MeasurementListener() {
            @Override
            public void onMeasurementReceived(final Point point) {
                if (mBatteryStatistics != null) {
                    mPower = Math.abs(mBatteryStatistics.getAverage());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPowerFragment.updatePowerValue(mPower);
                        }
                    });
                }
            }
        };

        mPowerCollectionTask = CollectionManager.getInstance().getPowerCollectionTask();
        mApplicationCollectionTask = CollectionManager.getInstance().getApplicationCollectionTask(this);
        mBatteryStatistics = mPowerCollectionTask.getStatistics();
        mPowerCollectionTask.start();
        mApplicationCollectionTask.start();
        Device.getInstance().getBatteryCapacity(this);
    }

    /**
     * Setup the navigation drawer.
     */
    private void setupNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final FrameLayout contentFrame = (FrameLayout)findViewById(R.id.powerbench_container);
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
        mPagerTabs = new Button[] { powerTab, displayTab, appsTab };
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
        String value = String.format(getString(R.string.value_units_template), mBatteryLifeFormatter.format(batteryLife), getString(R.string.hours));
        mBatteryLife.setText(value);
    }

    /**
     * Apply the theme to this activity.
     */
    protected void applyTheme() {
        Theme theme = mThemeManager.getCurrentTheme(this);
        // Apply the theme to the action bar.
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, theme.getActionBarColorResource())));
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
            for (TabFragment tabFragment : mTabFragments) {
                tabFragment.applyTheme(theme);
            }
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onServiceBound() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPowerCollectionTask.registerMeasurementListener(mMeasurementListener);
        for (TabFragment tabFragment : mTabFragments) {
            tabFragment.refresh();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPowerCollectionTask.unregisterMeasurementListener(mMeasurementListener);
    }

    @Override
    public void onDestroy() {
        mApplicationCollectionTask.stop();
        super.onDestroy();
    }

    @Override
    public void onChargerConnected() {
        mThemeManager.setCurrentTheme(this, Theme.CHARGER_THEME);
        applyTheme();
        mBatteryStatistics.clearRecentData();
        if (mBatteryLifeLabel != null)
            mBatteryLifeLabel.setText(getString(R.string.battery_life_until_full));

    }

    @Override
    public void onChargerDisconnected() {
        mThemeManager.setCurrentTheme(this, Theme.BATTERY_THEME);
        applyTheme();
        mBatteryStatistics.clearRecentData();
        if (mBatteryLifeLabel != null)
            mBatteryLifeLabel.setText(getString(R.string.battery_life_remaining));
    }

    /**
     * The pager adapter for showing the various modes of powerbench.
     */
    public class PowerbenchPagerAdapter extends FragmentPagerAdapter {

        /**
         * The array of fragments supported by this pager adapter.
         */
        private TabFragment[] mFragments;

        public PowerbenchPagerAdapter(FragmentManager fm, TabFragment[] fragments) {
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
