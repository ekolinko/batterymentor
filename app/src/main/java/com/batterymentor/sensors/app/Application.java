package com.batterymentor.sensors.app;

import android.content.Context;
import android.graphics.drawable.Drawable;

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