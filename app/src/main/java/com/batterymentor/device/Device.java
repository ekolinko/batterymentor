package com.batterymentor.device;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;

import com.batterymentor.R;
import com.batterymentor.constants.Constants;
import com.batterymentor.constants.DeviceConstants;
import com.batterymentor.constants.SensorConstants;
import com.batterymentor.sensors.ChargerManager;
import com.batterymentor.sensors.Sensor;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Class that handles device information.
 */
public class Device {

    public enum BatterySupport {
        DEVICE, ESTIMATE, UNKNOWN;
    }

    /**
     * The battery capacity.
     */
    private double mBatteryCapacity = Constants.INVALID_VALUE;

    /**
     * The number of cores.
     */
    public int mNumCores = Constants.INVALID_VALUE;

    /**
     * Flag indicating what type of battery support is provided by the device.
     */
    private BatterySupport mBatterySupport = BatterySupport.UNKNOWN;

    private static class SingletonHolder {
        private static final Device INSTANCE = new Device();
    }

    public static Device getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Device() {
    }

    /**
     * Return the version of this app.
     *
     * @return the version of this app.
     */
    public String getAppVersion(Context context) {
        String version = Constants.EMPTY_STRING;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return version;
    }

    /**
     * Return true if the power supported by the device is an estimate, false otherwise.
     *
     * @return true if the power supported by the device is an estimate.
     */
    public boolean isBatteryPowerEstimated() {
        if (mBatterySupport == BatterySupport.UNKNOWN) {
            mBatterySupport = checkBatterySupport();
        }

        return mBatterySupport == BatterySupport.ESTIMATE;
    }

    /**
     * Return the battery measurement that is supported on this device.
     *
     * @return the battery measurement that is supported on this device.
     */
    public BatterySupport checkBatterySupport() {
        String manufacturer = android.os.Build.MANUFACTURER;
        if (manufacturer.equalsIgnoreCase(DeviceConstants.SAMSUNG)) {
            return BatterySupport.ESTIMATE;
        }

        if (!Sensor.POWER.isSupported()) {
            return BatterySupport.ESTIMATE;
        }

        double total = 0;
        double[] testPoints = getBatteryTestPoints(DeviceConstants.BATTERY_TEST_NUM_POINTS);
        for (double testPoint : testPoints) {
            total += testPoint;
        }
        double average = total / DeviceConstants.BATTERY_TEST_NUM_POINTS;
        if (ChargerManager.getInstance().isCharging()) {
            return (average >= DeviceConstants.BATTERY_TEST_CHARGING_MIN && average <= DeviceConstants.BATTERY_TEST_CHARGING_MAX) ? BatterySupport.DEVICE : BatterySupport.ESTIMATE;
        } else {
            return (average >= DeviceConstants.BATTERY_TEST_BATTERY_MIN && average <= DeviceConstants.BATTERY_TEST_BATTERY_MAX) ? BatterySupport.DEVICE : BatterySupport.ESTIMATE;
        }
    }

    /**
     * Return the specified number of test measurements from the battery.
     *
     * @return the specified number of test measurements from the battery.
     */
    public double[] getBatteryTestPoints(int numPoints) {
        double[] testPoints = new double[numPoints];
        for (int i = 0; i < numPoints; i++) {
            testPoints[i] = Sensor.POWER.measureForBatterySupport();
            try {
                Thread.sleep(DeviceConstants.BATTERY_TEST_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return testPoints;
    }

    /**
     * Get the battery capacity of the device in milliwatt hours. Measure this value if it is
     * invalid.
     *
     * @param context the application context.
     * @return the battery capacity of the device in milliwatt hours.
     */
    public double getBatteryCapacity(Context context) {
        if (mBatteryCapacity < 0) {
            Object mPowerProfile = null;
            try {
                mPowerProfile = Class.forName(DeviceConstants.POWER_PROFILE_CLASS)
                        .getConstructor(Context.class).newInstance(context);
            } catch (Exception e) {
                mBatteryCapacity = 0;
            }
            try {
                double capacityInMah = (Double) Class
                        .forName(DeviceConstants.POWER_PROFILE_CLASS)
                        .getMethod(DeviceConstants.GET_AVERAGE_POWER_METHOD, java.lang.String.class)
                        .invoke(mPowerProfile, DeviceConstants.BATTERY_CAPACITY_FIELD);
                mBatteryCapacity = capacityInMah * Sensor.VOLTAGE.measure();
            } catch (Exception e) {
                mBatteryCapacity = 0;
            }
        }
        return mBatteryCapacity;
    }

    /**
     * Get the number of CPU cores.
     *
     * @return the number of CPU cores.
     */
    public int getNumCores() {
        if (mNumCores == Constants.INVALID_VALUE) {
            mNumCores = 0;
            while (new File(String.format(SensorConstants.SENSOR_CPU_TEMPLATE, mNumCores)).exists()) {
                mNumCores++;
            }
        }
        return mNumCores;
    }

    /**
     * Get the factor that should be used to convert the power reading from the sensor to milliwatts.
     */
    public double getCurrentConversionFactor() {
        double conversionFactor = DeviceConstants.PowerFactor.DEFAULT;

        if (Build.MODEL.equals(DeviceConstants.Model.NEXUS_6)) {
            conversionFactor = DeviceConstants.PowerFactor.NEXUS_6;
        } else if (Build.MODEL.equals(DeviceConstants.Model.NEXUS_7)) {
            conversionFactor = DeviceConstants.PowerFactor.NEXUS_7;
        }

        return conversionFactor * SensorConstants.MILLIAMPS_IN_MICROAMP;
    }

    /**
     * Return information about the device including the device manufacturer, model, OS version, and
     * realtime power metrics.
     *
     * @return information about the device including the device manufacturer, model, OS version, and
     * realtime power metrics.
     */
    public String getDeviceInformation(Context context) {
        String deviceInformation = String.format(context.getString(R.string.device_manufacturer_template), Build.MANUFACTURER) + Constants.NEWLINE;
        deviceInformation += String.format(context.getString(R.string.device_model_template), Build.MODEL) + Constants.NEWLINE;
        deviceInformation += String.format(context.getString(R.string.device_operating_system_template), Build.VERSION.RELEASE) + Constants.NEWLINE + Constants.NEWLINE;
//        DecimalFormat testPointFormatter = new DecimalFormat("#.##");
//        StringBuilder testPointBuilder = new StringBuilder();
//        for (double testPoint : getBatteryTestPoints(DeviceConstants.DEVICE_INFORMATION_BATTERY_TEST_NUM_POINTS)) {
//            testPointBuilder.append(testPointFormatter.format(testPoint) + Constants.SPACE);
//        }
//        deviceInformation += String.format(context.getString(R.string.device_sample_data_template), testPointBuilder.toString());
        return deviceInformation;
    }

    /**
     * Return the size of the screen in inches.
     *
     * @param activity the application activity.
     * @return the size of the screen in inches.
     */
    public String getScreenSize(Activity activity) {
        DisplayMetrics dm = getRealDisplayMetrics(activity);
        double x = Math.pow(dm.widthPixels/dm.xdpi,2);
        double y = Math.pow(dm.heightPixels/dm.ydpi,2);
        double inches = Math.sqrt(x+y);
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return String.format(activity.getString(R.string.value_units_template), decimalFormat.format(inches), activity.getString(R.string.inches));
    }

    /**
     * Return the screen dimensions as a string.
     *
     * @param activity the activity.
     * @return the screen dimensions as a string.
     */
    public String getScreenDimensions(Activity activity) {
        DisplayMetrics dm = getRealDisplayMetrics(activity);
        return String.format(activity.getString(R.string.screen_dimensions_template), dm.widthPixels, dm.heightPixels);
    }

    /**
     * Return the screen density as a string.
     *
     * @param activity the application activity.
     * @return the screen density as a string.
     */
    public String getScreenDensity(Activity activity) {
        DisplayMetrics dm = getRealDisplayMetrics(activity);
        return String.format(activity.getString(R.string.value_units_template), Integer.toString((int)(dm.density*160)), activity.getString(R.string.dpi));
    }

    /**
     * Return the total number of pixels for this device.
     *
     * @param activity the current activity.
     * @return the total number of pixels for this device.
     */
    public int getTotalScreenPixels(Activity activity) {
        DisplayMetrics dm = getRealDisplayMetrics(activity);
        return dm.widthPixels * dm.heightPixels;
    }

    /**
     * Get the real display metrics for this device.
     *
     * @param activity the activity for which to get the metrics.
     * @return the real display metrics.
     */
    public DisplayMetrics getRealDisplayMetrics(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = activity.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 17){
            display.getRealMetrics(displayMetrics);
        } else  {
            display.getMetrics(displayMetrics);
        }
        return displayMetrics;
    }
}
