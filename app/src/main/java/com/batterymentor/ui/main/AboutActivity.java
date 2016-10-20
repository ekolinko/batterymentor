package com.batterymentor.ui.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.batterymentor.R;
import com.batterymentor.constants.Constants;
import com.batterymentor.constants.FlavorConstants;
import com.batterymentor.device.Device;
import com.batterymentor.ui.common.CommonActivity;

/**
 * The about activity that shows information about this app.
 */
public class AboutActivity extends CommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initialize();
        TextView versionView = (TextView)findViewById(R.id.about_version);
        versionView.setText(getString(R.string.version) + Constants.SPACE + Device.getInstance().getAppVersion(this));
        LinearLayout promoContainer = (LinearLayout)findViewById(R.id.promo_container);
        if (promoContainer != null) {
            if (!FlavorConstants.VERSION_PRO) {
                promoContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_play_link_battery_mentor_pro))));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.google_play_web_link_battery_mentor_pro))));
                        }
                    }
                });
            } else {
                promoContainer.setVisibility(View.GONE);
            }
        }
    }
}
