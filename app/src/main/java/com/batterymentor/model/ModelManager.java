package com.batterymentor.model;

import android.content.Context;

import com.batterymentor.collectionmanager.CollectionManager;
import com.batterymentor.collectionmanager.CollectionTask;
import com.batterymentor.collectionmanager.LifetimeCollectionTask;
import com.batterymentor.constants.ModelConstants;
import com.batterymentor.datamanager.Point;
import com.batterymentor.sensors.ChargerManager;

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

    /**
     * The cpu frequency model.
     */
    private Model mCpuFrequencyModel;

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
        Model model = loadModelFromStorage(context, ModelConstants.SCREEN_MODEL_FILENAME);
        if (model instanceof LinearModel) {
            LinearModel screenModel = (LinearModel) model;
            if (screenModel != null) {
                mScreenModel = screenModel;
                getBatteryModel(context).setScreenModel(screenModel);
            }
        }
        model = loadModelFromStorage(context, ModelConstants.CPU_MODEL_FILENAME);
        if (model instanceof LinearModel) {
            LinearModel cpuModel = (LinearModel) model;
            if (cpuModel != null) {
                mCpuModel = cpuModel;
                getBatteryModel(context).setCpuModel(cpuModel);
            }
        }
        model = loadModelFromStorage(context, ModelConstants.CPU_FREQUENCY_MODEL_FILENAME);
        if (model instanceof QuadraticModel) {
            QuadraticModel frequencyModel = (QuadraticModel) model;
            if (frequencyModel != null) {
                mCpuFrequencyModel = frequencyModel;
                getBatteryModel(context).setCpuFrequencyModel(frequencyModel);
            }
        }
        ChargerManager.getInstance().registerChargerListener(context, this);
    }

    public BatteryModel getBatteryModel(Context context) {
        if (mBatteryModel == null) {
            mBatteryModel = new BatteryModel(context);
            LifetimeCollectionTask powerCollectionTask = CollectionManager.getInstance().getPowerCollectionTask(context);
            final double average = powerCollectionTask.getRealtimeStatistics().getAverage();
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
        getBatteryModel(context).setCpuModel(cpuModel);
        saveModelToStorage(context, ModelConstants.CPU_MODEL_FILENAME, cpuModel);
    }

    /**
     * Set the cpu model. Assign this cpu model to the battery model and write it to storage.
     *
     * @param context the context of the application.
     * @param frequencyModel the cpu frequency model to set.
     */
    public void setCpuFrequencyModel(Context context, Model frequencyModel) {
        mCpuFrequencyModel = frequencyModel;
        getBatteryModel(context).setCpuFrequencyModel(mCpuFrequencyModel);
        saveModelToStorage(context, ModelConstants.CPU_FREQUENCY_MODEL_FILENAME, mCpuFrequencyModel);
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
            } else if (model instanceof QuadraticModel) {
                QuadraticModel quadraticModel = (QuadraticModel) model;
                objectOutputStream.writeObject(quadraticModel);
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
    public Model loadModelFromStorage(Context context, String filename) {
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        Model model = null;
        try {
            fileInputStream = context.openFileInput(filename);
            objectInputStream = new ObjectInputStream(fileInputStream);
            Object object = objectInputStream.readObject();
            if (object instanceof Model) {
                model = (Model) object;
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
        return model;
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
