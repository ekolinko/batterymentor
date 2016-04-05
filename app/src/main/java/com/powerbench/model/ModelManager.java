package com.powerbench.model;

import android.content.Context;

import com.powerbench.collectionmanager.CollectionManager;
import com.powerbench.collectionmanager.CollectionTask;
import com.powerbench.datamanager.Point;
import com.powerbench.datamanager.Statistics;
import com.powerbench.sensors.ChargerManager;

/**
 * Class representing the manager that maintains all the global models used by the application.
 */
public class ModelManager implements ChargerManager.ChargerListener {

    /**
     * The battery model.
     */
    private BatteryModel mBatteryModel;

    private static class SingletonHolder {
        private static final ModelManager INSTANCE = new ModelManager();
    }

    public static ModelManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private ModelManager() {
    }

    public BatteryModel getBatteryModel(Context context) {
        if (mBatteryModel == null) {
            mBatteryModel = new BatteryModel(context);
            ChargerManager.getInstance().registerChargerListener(context, this);
            CollectionTask powerCollectionTask = CollectionManager.getInstance().getPowerCollectionTask();
            final Statistics statistics = powerCollectionTask.getStatistics();
            powerCollectionTask.registerMeasurementListener(new CollectionTask.MeasurementListener() {
                @Override
                public void onMeasurementReceived(Point point) {
                    mBatteryModel.setPower(statistics.getAverage());
                }
            });
        }
        return mBatteryModel;
    }

    @Override
    public void onChargerConnected() {
        if (mBatteryModel != null) {
            mBatteryModel.setCharging(true);
        }
    }

    @Override
    public void onChargerDisconnected() {
        if (mBatteryModel != null) {
            mBatteryModel.setCharging(false);
        }
    }

    @Override
    public void onBatteryLevelChanged(int batteryLevel) {
        if (mBatteryModel != null) {
            mBatteryModel.setBatteryLevel(batteryLevel);
        }
    }
}
