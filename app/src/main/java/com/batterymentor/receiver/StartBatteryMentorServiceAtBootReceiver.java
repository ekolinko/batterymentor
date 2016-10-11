package com.batterymentor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.batterymentor.BatteryMentorService;
import com.batterymentor.settings.Settings;

/**
 * Receiver that's responsible for starting this app when the device is booted.
 */
public class StartBatteryMentorServiceAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) && Settings.getInstance().getStartupWithDevice(context)) {
            Intent serviceIntent = new Intent(context, BatteryMentorService.class);
            context.startService(serviceIntent);
        }
    }
}
