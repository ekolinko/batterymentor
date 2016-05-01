package com.powerbench.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.powerbench.PowerBenchService;
import com.powerbench.settings.Settings;

/**
 * Receiver that's responsible for starting this app when the device is booted.
 */
public class StartPowerBenchServiceAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) && Settings.getInstance().getLaunchOnDeviceBootup(context)) {
            Intent serviceIntent = new Intent(context, PowerBenchService.class);
            context.startService(serviceIntent);
        }
    }
}
