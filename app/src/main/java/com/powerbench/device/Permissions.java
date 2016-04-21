package com.powerbench.device;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.powerbench.R;
import com.powerbench.constants.DeviceConstants;
import com.powerbench.ui.common.CommonActivity;
import com.powerbench.ui.tutorial.TutorialActivity;

/**
 * Class that handles device interactions, such as permission requests.
 */
public class Permissions {


    private static class SingletonHolder {
        private static final Permissions INSTANCE = new Permissions();
    }

    public static Permissions getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Permissions() {
    }

    /**
     * Check if the permission to write settings is granted.
     *
     * @param activity the common activity requesting the permission.
     * @return true if the permission to write settings is granted, false otherwise.
     */
    public boolean isSettingsPermissionGranted(CommonActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.System.canWrite(activity);
        }

        return true;
    }

    /**
     * Request the permission to write to settings.
     *
     * @param activity the common activity requesting the permission.
     */
    public boolean requestSettingsPermission(CommonActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.System.canWrite(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                Uri uri = Uri.fromParts(activity.getString(R.string.uri_package), activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivityForResult(intent, DeviceConstants.PERMISSIONS_WRITE_SETTINGS);
                return false;
            }
        }
        return true;
//        if (ContextCompat.checkSelfPermission(activity,
//                Manifest.permission.WRITE_SETTINGS)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
//                    Manifest.permission.READ_CONTACTS)) {
//
//            } else {
//                ActivityCompat.requestPermissions(activity,
//                        new String[]{ Manifest.permission.WRITE_SETTINGS },
//                        DeviceConstants.PERMISSIONS_WRITE_SETTINGS);
//            }
//        }
    }
}
