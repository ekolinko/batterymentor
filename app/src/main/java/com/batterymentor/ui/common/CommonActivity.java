package com.batterymentor.ui.common;

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
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.batterymentor.BatteryMentorService;
import com.batterymentor.R;
import com.batterymentor.constants.Constants;
import com.batterymentor.constants.DeviceConstants;
import com.batterymentor.constants.UIConstants;
import com.batterymentor.sensors.ChargerManager;
import com.batterymentor.ui.theme.Theme;
import com.batterymentor.ui.theme.ThemeManager;
import com.google.android.gms.ads.MobileAds;

import java.text.DecimalFormat;

/**
 * The common activity that all other activities in the application inherit from.
 */
public abstract class CommonActivity extends AppCompatActivity implements ChargerManager.ChargerListener {

    /**
     * The powerbench service.
     */
    private BatteryMentorService mService;

    /**
     * The current theme.
     */
    private Theme mTheme = Theme.BATTERY_THEME;

    /**
     * Flag indicating whether the powerbench service has been bound.
     */
    private boolean mServiceBound = false;

    /**
     * The formatter used to format power values.
     */
    private DecimalFormat mPowerFormatter;

    /**
     * The battery level.
     */
    private int mBatteryLevel = 0;

    /**
     * The handler used to measure the UI.
     */
    private Handler mHandler;

    /**
     * Flag indicating whether the charger is connected.
     */
    private boolean mChargerConnected;

    /**
     * The ad view.
     */
    private AdView mAdView;

    /**
     * The thread responsible for refreshing ads.
     */
    private AdRefreshThread mAdRefreshThread;

    /**
     * The theme manager.
     */
    private ThemeManager mThemeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.batterymentor.settings.Settings.getInstance().setContext(this);
        if (!com.batterymentor.settings.Settings.getInstance().isProVersion()) {
            MobileAds.initialize(getApplicationContext(), getString(R.string.advertising_id));
        }
        ChargerManager.getInstance().initialize(this);
        mHandler = new Handler();
        mThemeManager = ThemeManager.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("tstatic","onStart");
        Intent intent = new Intent(this, BatteryMentorService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Initialize the common UI elements across all activities. Should be called after the call to
     * {@link android.app.Activity#setContentView(int)}}.
     */
    protected void initialize() {
        mPowerFormatter = new DecimalFormat(getString(R.string.format_power));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        mAdView = (AdView) findViewById(R.id.powerbench_ad);
        if (com.batterymentor.settings.Settings.getInstance().isProVersion()) {
            mAdView.setVisibility(View.GONE);
        } else {
            mAdView.setVisibility(View.VISIBLE);
        }
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
        if (!com.batterymentor.settings.Settings.getInstance().isProVersion()) {
            if (mAdRefreshThread != null) {
                mHandler.removeCallbacks(mAdRefreshThread);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChargerManager.getInstance().registerChargerListener(this, this);
        if (!com.batterymentor.settings.Settings.getInstance().isProVersion()) {
            if (mAdRefreshThread == null) {
                mAdRefreshThread = new AdRefreshThread();
                refreshAd();
                mHandler.post(mAdRefreshThread);
            }
        }
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
                    com.batterymentor.settings.Settings.getInstance().setShowTutorial(CommonActivity.this, false);
                    refreshAd();
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
     * Refresh the ad.
     */
    private void refreshAd() {
        if (mAdView != null) {
            if (com.batterymentor.settings.Settings.getInstance().isProVersion()) {
                mAdView.setVisibility(View.GONE);
            } else {
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
                mAdView.setVisibility(View.VISIBLE);
            }
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
    public BatteryMentorService getService() {
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
    public void onChargerConnected() {
        mChargerConnected = true;
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            onChargerConnectedUIThread();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onChargerConnectedUIThread();
                }
            });
        }
    }

    /**
     * Method that indicates to all activities that inherit from this class that a charger has been
     * disconnected.
     */
    public void onChargerDisconnected() {
        mChargerConnected = false;
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            onChargerDisconnectedUIThread();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onChargerDisconnectedUIThread();
                }
            });
        }
    }

    /**
     * Method that indicates to all activities that inherit from this class that a charger has been
     * connected and is guaranteed to run on the main UI thread.
     */
    public void onChargerConnectedUIThread() {
        Theme theme = Theme.CHARGER_THEME;
        mThemeManager.setCurrentTheme(this, theme);
        applyTheme(theme);
    }

    /**
     * Method that indicates to all activities that inherit from this class that a charger has been
     * disconnected and is guaranteed to run on the main UI thread.
     */
    public void onChargerDisconnectedUIThread() {
        Theme theme = Theme.BATTERY_THEME;
        mThemeManager.setCurrentTheme(this, theme);
        applyTheme(theme);
    }

    /**
     * Apply the theme to this activity.
     */
    protected void applyTheme(Theme theme) {
        mTheme = theme;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, theme.getActionBarColorResource()));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, theme.getActionBarColorResource())));
    }

    /**
     * Method that indicates to all activities that inherit form this class that the battery level
     * has changed.
     *
     * @param level the new battery level.
     */
    @Override
    public void onBatteryLevelChanged(int level) {
        mBatteryLevel = level;
    }

    protected int getBatteryLevel() {
        return mBatteryLevel;
    }

    protected Handler getHandler() {
        return mHandler;
    }

    protected ThemeManager getThemeManager() {
        return mThemeManager;
    }

    protected DecimalFormat getPowerFormatter() {
        return mPowerFormatter;
    }

    protected boolean isChargerConnected() {
        return mChargerConnected;
    }

    protected Theme getAppTheme() {
        return mTheme;
    }

    /**
     * Connection used to bind to {@link BatteryMentorService}.
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            BatteryMentorService.BatteryMentorBinder binder = (BatteryMentorService.BatteryMentorBinder) service;
            mService = binder.getService();
            mServiceBound = true;
            CommonActivity.this.onServiceBound();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mServiceBound = false;
        }
    };

    /**
     * The thread responsible for refreshing the ad on the screen.
     */
    private class AdRefreshThread implements Runnable {

        @Override
        public void run() {
            mHandler.postDelayed(this, Constants.AD_REFRESH_INTERVAL);
            refreshAd();
        }
    }
}
