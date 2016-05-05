package com.powerbench.model;

import android.content.Context;

import com.powerbench.collectionmanager.CollectionManager;
import com.powerbench.collectionmanager.CollectionTask;
import com.powerbench.collectionmanager.LifetimeCollectionTask;
import com.powerbench.constants.ModelConstants;
import com.powerbench.datamanager.Point;
import com.powerbench.datamanager.RealtimeStatistics;
import com.powerbench.sensors.ChargerManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class representing the manager that maintains all the global models used by the application.
 */
public class ModelManager implements ChargerManager.ChargerListener {

    /**
     * The battery model.
     */
    private BatteryModel mBatteryModel;

    /**
     * The screen brightness model.
     */
    private Model mScreenModel;

    /**
     * The cpu model.
     */
    private Model mCpuModel;

    private static class SingletonHolder {
        private static final ModelManager INSTANCE = new ModelManager();
    }

    public static ModelManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private ModelManager() {
    }

    /**
     * Initialize the model manager. Load in the screen and brightness models from storage.
     *
     * @param context the context of the application.
     */
    public void initialize(Context context) {
        LinearModel screenModel = loadModelFromStorage(context, ModelConstants.SCREEN_MODEL_FILENAME);
        if (screenModel != null) {
            mScreenModel = screenModel;
            getBatteryModel(context).setScreenModel(screenModel);
        }
        LinearModel cpuModel = loadModelFromStorage(context, ModelConstants.CPU_MODEL_FILENAME);
        if (cpuModel != null) {
            mCpuModel = cpuModel;
            getBatteryModel(context).setCpuModel(cpuModel);
        }
    }

    public BatteryModel getBatteryModel(Context context) {
        if (mBatteryModel == null) {
            mBatteryModel = new BatteryModel(context);
            ChargerManager.getInstance().registerChargerListener(context, this);
            LifetimeCollectionTask powerCollectionTask = CollectionManager.getInstance().getPowerCollectionTask(context);
            final double average = powerCollectionTask.getStatistics().getAverage();
            powerCollectionTask.registerMeasurementListener(new CollectionTask.MeasurementListener() {
                @Override
                public void onMeasurementReceived(Point point) {
                    mBatteryModel.setPower(average);
                }
            });
        }
        return mBatteryModel;
    }

    /**
     * Set the screen model. Assign this screen model to the battery model and write it to storage.
     *
     * @param context the context of the application.
     * @param screenModel the screen model to set.
     */
    public void setScreenModel(Context context, Model screenModel) {
        mScreenModel = screenModel;
        getBatteryModel(context).setScreenModel(screenModel);
        saveModelToStorage(context, ModelConstants.SCREEN_MODEL_FILENAME, screenModel);
    }

    /**
     * Set the cpu model. Assign this cpu model to the battery model and write it to storage.
     *
     * @param context the context of the application.
     * @param cpuModel the cpu model to set.
     */
    public void setCpuModel(Context context, Model cpuModel) {
        mCpuModel = cpuModel;
        getBatteryModel(context).setScreenModel(cpuModel);
        saveModelToStorage(context, ModelConstants.CPU_MODEL_FILENAME, cpuModel);
    }

    /**
     * Save a model to the specified file in storage.
     */
    public void saveModelToStorage(Context context, String filename, Model model) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            if (model instanceof LinearModel) {
                LinearModel linearModel = (LinearModel) model;
                objectOutputStream.writeObject(linearModel);
            }
        } catch (IOException e) {
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                }
            }
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Load a linear model from the specified file in storage.
     */
    public LinearModel loadModelFromStorage(Context context, String filename) {
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        LinearModel linearModel = null;
        try {
            fileInputStream = context.openFileInput(filename);
            objectInputStream = new ObjectInputStream(fileInputStream);
            Object object = objectInputStream.readObject();
            if (object instanceof LinearModel) {
                linearModel = (LinearModel) object;
            }
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                }
            }
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return linearModel;
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
