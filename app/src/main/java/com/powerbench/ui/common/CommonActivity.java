package com.powerbench.ui.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.WindowManager;

import com.powerbench.PowerBenchService;
import com.powerbench.R;
import com.powerbench.constants.DeviceConstants;
import com.powerbench.constants.UIConstants;
import com.powerbench.sensors.ChargerManager;
import com.powerbench.ui.theme.Theme;
import com.powerbench.ui.theme.ThemeManager;

import java.text.DecimalFormat;

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

    /**
     * The formatter used to format power values.
     */
    private DecimalFormat mPowerFormatter;

    /**
     * The handler used to measure the UI.
     */
    private Handler mHandler;

    /**
     * Flag indicating whether the charger is connected.
     */
    private boolean mChargerConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, PowerBenchService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Initialize the common UI elements across all activities. Should be called after the call to
     * {@link android.app.Activity#setContentView(int)}}.
     */
    protected void initialize() {
        mHandler = new Handler();
        mPowerFormatter = new DecimalFormat(getString(R.string.format_power));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DeviceConstants.PERMISSIONS_WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(this)) {
                    onPermissionGranted(DeviceConstants.PERMISSIONS_WRITE_SETTINGS);
                } else {
                    onPermissionDenied(DeviceConstants.PERMISSIONS_WRITE_SETTINGS);
                }
            }
        }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UIConstants.TUTORIAL_REQUEST_CODE:
                    if (data.getBooleanExtra(UIConstants.CLOSE_APP, false)) {
                        finish();
                    }
                    break;
                case UIConstants.TUTORIAL_REFRESH_REQUEST_CODE:
                    com.powerbench.settings.Settings.getInstance().setShowTutorial(CommonActivity.this, false);;
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case DeviceConstants.PERMISSIONS_WRITE_SETTINGS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionGranted(requestCode);
                } else {
                    onPermissionDenied(requestCode);
                }
                break;
        }
    }

    /**
     * Show a dialog with the specified title and message.
     *
     * @param title the dialog title.
     * @param message the dialog message.
     */
    protected void showPermissionDialog(String title, String message) {

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
     * Method that indicates all activities that inherit from this class that a permission has been
     * granted.
     */
    protected void onPermissionGranted(int permission) {
    }

    /**
     * Method that indicates all activities that inherit from this class that a permission has been
     * rejected.
     */
    protected void onPermissionDenied(int permission) {
    }

    /**
     * Method that indicates to all activities that inherit from this class that the service has
     * been bound.
     */
    protected void onServiceBound() {
    }

    /**
     * Method that indicates to all activities that inherit from this class that a charger has been
     * connected.
     */
    @Override
    public void onChargerConnected() {
        mChargerConnected = true;
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Theme theme = ThemeManager.getInstance().getCurrentTheme(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(ContextCompat.getColor(this, theme.getActionBarColorResource()));
            }
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, theme.getActionBarColorResource())));
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Theme theme = ThemeManager.getInstance().getCurrentTheme(CommonActivity.this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        getWindow().setStatusBarColor(ContextCompat.getColor(CommonActivity.this, theme.getActionBarColorResource()));
                    }
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(CommonActivity.this, theme.getActionBarColorResource())));
                }
            });
        }
    }

    /**
     * Method that indicates to all activities that inherit from this class that a charger has been
     * disconnected.
     */
    @Override
    public void onChargerDisconnected() {
        mChargerConnected = false;
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Theme theme = ThemeManager.getInstance().getCurrentTheme(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(ContextCompat.getColor(this, theme.getActionBarColorResource()));
            }
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, theme.getActionBarColorResource())));
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Theme theme = ThemeManager.getInstance().getCurrentTheme(CommonActivity.this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        getWindow().setStatusBarColor(ContextCompat.getColor(CommonActivity.this, theme.getActionBarColorResource()));
                    }
                }
            });
        }
    }

    /**
     * Method that indicates to all activities that inherit form this class that the battery level
     * has changed.
     *
     * @param level the new battery level.
     */
    @Override
    public void onBatteryLevelChanged(int level) {
    }

    protected Handler getHandler() {
        return mHandler;
    }

    protected DecimalFormat getPowerFormatter() {
        return mPowerFormatter;
    }

    protected boolean isChargerConnected() {
        return mChargerConnected;
    }

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
