package com.powerbench.ui.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.powerbench.R;
import com.powerbench.collectionmanager.CollectionManager;
import com.powerbench.constants.Constants;
import com.powerbench.datamanager.Statistics;
import com.powerbench.device.Device;
import com.powerbench.collectionmanager.CollectionTask;
import com.powerbench.datamanager.Point;
import com.powerbench.ui.benchmark.BrightnessBenchmarkActivity;
import com.powerbench.ui.benchmark.CpuBenchmarkActivity;
import com.powerbench.ui.common.CommonActivity;
import com.powerbench.ui.prototype.RunningApplicationsActivity;

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
     * The measurement listener.
     */
    private CollectionTask.MeasurementListener mMeasurementListener;

    /**
     * The handler used to measure the UI.
     */
    private Handler mHandler;

    /**
     * The current median.
     */
    private double mValue;

    /**
     * The current realtime fragment being shown on the screen.
     */
    private RealtimeFragment mFragment;

    /**
     * The fragment for showing realtime battery power measurements.
     */
    private BatteryFragment mBatteryFragment;

    /**
     * The fragment for showing realtime charger power measurements.
     */
    private ChargerFragment mChargerFragment;

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
        mBatteryFragment = new BatteryFragment();
        mChargerFragment = new ChargerFragment();
        mHandler = new Handler();
        mMeasurementListener = new CollectionTask.MeasurementListener() {
            @Override
            public void onMeasurementReceived(final Point point) {
                if (mBatteryStatistics != null) {
                    mValue = Math.abs(mBatteryStatistics.getAverage());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mFragment.updatePowerValue(mValue);
                        }
                    });
                }
            }
        };
        mPowerCollectionTask = CollectionManager.getInstance().getPowerCollectionTask();
        mBatteryStatistics = mPowerCollectionTask.getStatistics();
        mPowerCollectionTask.start();
        Device.getInstance().getBatteryCapacity(this);
    }

    /**
     * Setup the navigation drawer.
     */
    private void setupNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final FrameLayout contentFrame = (FrameLayout)findViewById(R.id.fragment_container);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPowerCollectionTask.unregisterMeasurementListener(mMeasurementListener);
    }

    @Override
    public void onChargerConnected() {
        showFragment(mChargerFragment);
        mBatteryStatistics.clearRecentData();
    }

    @Override
    public void onChargerDisconnected() {
        showFragment(mBatteryFragment);
        mBatteryStatistics.clearRecentData();
    }

    /**
     * Show the specified fragment as the main screen of this activity.
     */
    public void showFragment(RealtimeFragment fragment) {
        if (fragment == null)
            return;

        if (mFragment != fragment) {
            mFragment = fragment;
            Bundle args = new Bundle();
            args.putDouble(Constants.BUNDLE_KEY_VALUE, mValue);
            if (mFragment.getArguments() == null) {
                mFragment.setArguments(args);
            } else {
                mFragment.getArguments().putAll(args);
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, mFragment);
            transaction.commit();
        }
    }

    /**
     * Fragment for showing realtime measurements.
     */
    public abstract static class RealtimeFragment extends  Fragment {
        /**
         * The power formatter.
         */
        private DecimalFormat mPowerFormatter;

        public abstract TextView getPowerValueTextView();

        /**
         * Update the power value from the fragment arguments.
         */
        public void updatePowerValueFromArguments() {
            double value = getArguments().getDouble(Constants.BUNDLE_KEY_VALUE);
            updatePowerValue(value);
        }

        /**
         * Update the power value using the specified point.
         *
         * @param point the point to use to measure the arguments.
         */
        public void updatePowerValue(Point point) {
            if (point == null)
                return;

            updatePowerValue(point.getY());
        }

        /**
         * Update the power value using the specified value
         *
         * @param powerValue the value to use to measure the arguments.
         */
        public void updatePowerValue(double powerValue) {
            if (mPowerFormatter == null)
                mPowerFormatter = new DecimalFormat(getString(R.string.format_power));

            TextView powerValueTextView = getPowerValueTextView();
            if (powerValueTextView != null) {
                String value = String.format(getString(R.string.value_units_template), mPowerFormatter.format(powerValue), getString(R.string.milliwatts));
                powerValueTextView.setText(value);
            }
        }

    }

    /**
     * Fragment for showing the realtime battery measurements.
     */
    public static class BatteryFragment extends RealtimeFragment {

        /**
         * The battery power value associated with this fragment.
         */
        private TextView mBatteryPowerValue;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_realtime_battery, container, false);
            mBatteryPowerValue = (TextView) view.findViewById(R.id.powerbench_power_value);
            Button brightnessBenchmark = (Button) view.findViewById(R.id.button_brightness_benchmark);
            if (brightnessBenchmark != null) {
                brightnessBenchmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), BrightnessBenchmarkActivity.class));
                    }
                });
            }
            Button cpuBenchmark = (Button) view.findViewById(R.id.button_cpu_benchmark);
            if (cpuBenchmark != null) {
                cpuBenchmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), CpuBenchmarkActivity.class));
                    }
                });
            }
            updatePowerValueFromArguments();
            setRetainInstance(true);
            return view;
        }

        @Override
        public TextView getPowerValueTextView() {
            return mBatteryPowerValue;
        }
    }

    /**
     * Fragment for showing the realtime charger measurements.
     */
    public static  class ChargerFragment extends RealtimeFragment {
        /**
         * The battery power value associated with this fragment.
         */
        private TextView mChargerPowerValue;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_realtime_charger, container, false);
            mChargerPowerValue = (TextView) view.findViewById(R.id.powerbench_power_value);
            Button runningApplications = (Button) view.findViewById(R.id.button_running_applications);
            if (runningApplications != null) {
                runningApplications.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), RunningApplicationsActivity.class));
                    }
                });
            }
            updatePowerValueFromArguments();
            setRetainInstance(true);
            return view;
        }

        @Override
        public TextView getPowerValueTextView() {
            return mChargerPowerValue;
        }
    }
}
