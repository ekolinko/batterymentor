package com.powerbench.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.powerbench.R;
import com.powerbench.constants.Constants;
import com.powerbench.sensors.CollectionTask;
import com.powerbench.sensors.Point;
import com.powerbench.sensors.Sensor;
import com.powerbench.ui.common.CommonActivity;

import java.text.DecimalFormat;

/**
 * The main powerbench activity that allows a user to view battery power consumption and charging
 * rate in realtime.
 */
public class PowerBenchActivity extends CommonActivity {

    /**
     * The battery collection task.
     */
    private CollectionTask mBatteryCollectionTask;

    /**
     * The handler used to update the UI.
     */
    private Handler mHandler;

    /**
     * The last point that was received.
     */
    private Point mLastPoint;

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
        mBatteryFragment = new BatteryFragment();
        mChargerFragment = new ChargerFragment();
        mHandler = new Handler();
        mBatteryCollectionTask = new CollectionTask(Sensor.POWER, new CollectionTask.MeasurementListener() {
            @Override
            public void onMeasurementReceived(final Point point) {
                mLastPoint = point;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mFragment != null) {
                            mFragment.updatePowerValue(point);
                        }
                    }
                });
            }
        });
        mBatteryCollectionTask.start();
    }

    @Override
    protected void onServiceBound() {
        if (isServiceBound()) {
            getService().addCollectionTask(mBatteryCollectionTask);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isServiceBound()) {
            getService().addCollectionTask(mBatteryCollectionTask);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isServiceBound()) {
            getService().removeCollectionTask(mBatteryCollectionTask);
        }
    }

    @Override
    public void onChargerConnected() {
        showFragment(mChargerFragment);
    }

    @Override
    public void onChargerDisconnected() {
        showFragment(mBatteryFragment);
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
            args.putSerializable(Constants.BUNDLE_KEY_POINT, mLastPoint);
            if (mFragment.getArguments() == null) {
                mFragment.setArguments(args);
            } else {
                mFragment.getArguments().putAll(args);
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, mFragment);
            transaction.addToBackStack(null);
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

        public abstract TextView getPowerValue();

        /**
         * Update the power value from the fragment arguments.
         */
        public void updatePowerValueFromArguments() {
            Point point = (Point) getArguments().getSerializable(Constants.BUNDLE_KEY_POINT);
            if (point != null) {
                updatePowerValue(point);
            }
        }

        /**
         * Update the power value using the specified point.
         *
         * @param point the point to use to update the arguments.
         */
        public void updatePowerValue(Point point) {
            if (point == null)
                return;

            if (mPowerFormatter == null)
                mPowerFormatter = new DecimalFormat(getString(R.string.format_power));

            TextView powerValue = getPowerValue();
            if (powerValue != null) {
                String value = String.format(getString(R.string.value_units_template), mPowerFormatter.format(point.getValue()), getString(R.string.milliwatts));
                powerValue.setText(value);
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
            updatePowerValueFromArguments();
            setRetainInstance(true);
            return view;
        }

        @Override
        public TextView getPowerValue() {
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
            updatePowerValueFromArguments();
            setRetainInstance(true);
            return view;
        }

        @Override
        public TextView getPowerValue() {
            return mChargerPowerValue;
        }
    }
}
