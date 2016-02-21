package com.powerbench.ui.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.powerbench.PowerBenchService;
import com.powerbench.sensors.ChargerManager;

/**
 * The common activity that all other activities in the application inherit from.
 */
public abstract class CommonActivity extends AppCompatActivity implements ChargerManager.ChargerListener {

    /**
     * The powerbench service.
     */
    private PowerBenchService mService;

    /**
     * Flag indicating whether the powerbench service has been bound.
     */
    private boolean mServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, PowerBenchService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mServiceBound) {
            unbindService(mConnection);
            mServiceBound = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ChargerManager.getInstance().unregisterChargerListener(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChargerManager.getInstance().registerChargerListener(this, this);
    }

    /**
     * Return the powerbench service. If the service has not yet been bound, wait until it has
     * been bound.
     */
    public PowerBenchService getService() {
        return mService;
    }

    /**
     * Return true if the powerbench service has been bound, false otherwise.
     *
     * @return true if the powerbench service has been bound, false otherwise.
     */
    public boolean isServiceBound() {
        return mServiceBound;
    }

    /**
     * Method that indicates to all activities that inherit from this class that the service has
     * been bound.
     */
    protected abstract void onServiceBound();

    /**
     * Method that indicates to all activities that inherit from this class that a charger has been
     * connected.
     */
    @Override
    public abstract void onChargerConnected();

    /**
     * Method that indicates to all activities that inherit from this class that a charger has been
     * disconnected.
     */
    @Override
    public abstract void onChargerDisconnected();

    /**
     * Connection used to bind to {@link PowerBenchService}.
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            PowerBenchService.PowerBenchBinder binder = (PowerBenchService.PowerBenchBinder) service;
            mService = binder.getService();
            mServiceBound = true;
            CommonActivity.this.onServiceBound();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mServiceBound = false;
        }
    };
}
