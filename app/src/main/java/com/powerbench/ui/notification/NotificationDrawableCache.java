package com.powerbench.ui.notification;

import android.util.SparseArray;

import com.powerbench.R;

/**
 * The class used that contains the cache of notification drawables.
 */
public class NotificationDrawableCache {

    private SparseArray<Integer> mCache = new SparseArray<Integer>();

    public NotificationDrawableCache() {
        initialize();
    }

    /**
     * Initialize the values for this generator.
     */
    private void initialize() {
        mCache.put(100, R.drawable.notification_100);
        mCache.put(110, R.drawable.notification_110);
        mCache.put(120, R.drawable.notification_120);
        mCache.put(130, R.drawable.notification_130);
        mCache.put(140, R.drawable.notification_140);
        mCache.put(150, R.drawable.notification_150);
        mCache.put(160, R.drawable.notification_160);
        mCache.put(170, R.drawable.notification_170);
        mCache.put(180, R.drawable.notification_180);
        mCache.put(190, R.drawable.notification_190);
        mCache.put(200, R.drawable.notification_200);
        mCache.put(210, R.drawable.notification_210);
        mCache.put(220, R.drawable.notification_220);
        mCache.put(230, R.drawable.notification_230);
        mCache.put(240, R.drawable.notification_240);
        mCache.put(250, R.drawable.notification_250);
        mCache.put(260, R.drawable.notification_260);
        mCache.put(270, R.drawable.notification_270);
        mCache.put(280, R.drawable.notification_280);
        mCache.put(290, R.drawable.notification_290);
        mCache.put(300, R.drawable.notification_300);
        mCache.put(310, R.drawable.notification_310);
        mCache.put(320, R.drawable.notification_320);
        mCache.put(330, R.drawable.notification_330);
        mCache.put(340, R.drawable.notification_340);
        mCache.put(350, R.drawable.notification_350);
        mCache.put(360, R.drawable.notification_360);
        mCache.put(370, R.drawable.notification_370);
        mCache.put(380, R.drawable.notification_380);
        mCache.put(390, R.drawable.notification_390);
        mCache.put(400, R.drawable.notification_400);
        mCache.put(410, R.drawable.notification_410);
        mCache.put(420, R.drawable.notification_420);
        mCache.put(430, R.drawable.notification_430);
        mCache.put(440, R.drawable.notification_440);
        mCache.put(450, R.drawable.notification_450);
        mCache.put(460, R.drawable.notification_460);
        mCache.put(470, R.drawable.notification_470);
        mCache.put(480, R.drawable.notification_480);
        mCache.put(490, R.drawable.notification_490);
        mCache.put(500, R.drawable.notification_500);
        mCache.put(510, R.drawable.notification_510);
        mCache.put(520, R.drawable.notification_520);
        mCache.put(530, R.drawable.notification_530);
        mCache.put(540, R.drawable.notification_540);
        mCache.put(550, R.drawable.notification_550);
        mCache.put(560, R.drawable.notification_560);
        mCache.put(570, R.drawable.notification_570);
        mCache.put(580, R.drawable.notification_580);
        mCache.put(590, R.drawable.notification_590);
        mCache.put(600, R.drawable.notification_600);
        mCache.put(610, R.drawable.notification_610);
        mCache.put(620, R.drawable.notification_620);
        mCache.put(630, R.drawable.notification_630);
        mCache.put(640, R.drawable.notification_640);
        mCache.put(650, R.drawable.notification_650);
        mCache.put(660, R.drawable.notification_660);
        mCache.put(670, R.drawable.notification_670);
        mCache.put(680, R.drawable.notification_680);
        mCache.put(690, R.drawable.notification_690);
        mCache.put(700, R.drawable.notification_700);
        mCache.put(710, R.drawable.notification_710);
        mCache.put(720, R.drawable.notification_720);
        mCache.put(730, R.drawable.notification_730);
        mCache.put(740, R.drawable.notification_740);
        mCache.put(750, R.drawable.notification_750);
        mCache.put(760, R.drawable.notification_760);
        mCache.put(770, R.drawable.notification_770);
        mCache.put(780, R.drawable.notification_780);
        mCache.put(790, R.drawable.notification_790);
        mCache.put(800, R.drawable.notification_800);
        mCache.put(810, R.drawable.notification_810);
        mCache.put(820, R.drawable.notification_820);
        mCache.put(830, R.drawable.notification_830);
        mCache.put(840, R.drawable.notification_840);
        mCache.put(850, R.drawable.notification_850);
        mCache.put(860, R.drawable.notification_860);
        mCache.put(870, R.drawable.notification_870);
        mCache.put(880, R.drawable.notification_880);
        mCache.put(890, R.drawable.notification_890);
        mCache.put(900, R.drawable.notification_900);
        mCache.put(910, R.drawable.notification_910);
        mCache.put(920, R.drawable.notification_920);
        mCache.put(930, R.drawable.notification_930);
        mCache.put(940, R.drawable.notification_940);
        mCache.put(950, R.drawable.notification_950);
        mCache.put(960, R.drawable.notification_960);
        mCache.put(970, R.drawable.notification_970);
        mCache.put(980, R.drawable.notification_980);
        mCache.put(990, R.drawable.notification_990);
    }

    /**
     * Get the drawable resource associated with the specified value.
     *
     * @param value the value for which to look up the associated resources.
     * @return the associated resource.
     */
    public int getResourceForValue(int value) {
        Integer resourceId;
        if ((resourceId = mCache.get(value)) == null) {
            resourceId = R.drawable.notification_990;
        }
        return resourceId;
    }
}
