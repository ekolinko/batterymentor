package com.batterymentor.ui.tutorial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.batterymentor.R;
import com.batterymentor.constants.DeviceConstants;
import com.batterymentor.constants.UIConstants;
import com.batterymentor.device.Permissions;
import com.batterymentor.ui.common.CommonActivity;
import com.batterymentor.ui.theme.Theme;

/**
 * Activity responsible for showing the screen that shows a tutorial on how to enable system
 * permissions.
 */
public class SystemPermissionsActivity extends CommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_settings);
        initialize();
        Button buttonCancel = (Button) findViewById(R.id.powerbench_button_cancel);
        if (buttonCancel != null) {
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
        Button buttonOk = (Button) findViewById(R.id.powerbench_button_ok);
        if (buttonOk != null) {
            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Permissions.getInstance().requestSettingsPermission(SystemPermissionsActivity.this);
                }
            });
        }
    }

    protected void onPermissionGranted(int permission) {
        if (permission != DeviceConstants.PERMISSIONS_WRITE_SETTINGS) {
            Intent data = new Intent();
            data.setData(Uri.parse(UIConstants.POWER_TAB));
            setResult(RESULT_OK, data);
        } else {
            Intent data = new Intent();
            data.setData(Uri.parse(UIConstants.SCREEN_TAB));
            setResult(RESULT_OK, data);
        }
        finish();
    }

    @Override
    protected void applyTheme(Theme theme) {
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.setData(Uri.parse(UIConstants.POWER_TAB));
        setResult(RESULT_OK, data);
        finish();
    }
}
