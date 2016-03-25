package com.powerbench.sensors.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;

import com.powerbench.R;
import com.powerbench.constants.Constants;
import com.powerbench.constants.SensorConstants;
import com.powerbench.debug.Debug;
import com.powerbench.sensors.Sensor;

import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * Class representing a running application.
 */
public class Application extends Process {

    /**
     * The package name of the application.
     */
    private String mPackageName;

    public Application(Context context, int pid, String name, String packageName, Drawable icon) {
        super(context, pid, name, icon);
        mPackageName = packageName;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public boolean isApplication() {
        return true;
    }

}