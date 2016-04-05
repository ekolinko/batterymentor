package com.powerbench.collectionmanager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.powerbench.constants.SensorConstants;
import com.powerbench.debug.Debug;
import com.powerbench.sensors.app.Application;
import com.powerbench.sensors.app.Process;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class responsible for performing application data collection.
 */
public class ApplicationCollectionTask {

    /**
     * The application context.
     */
    private final Context mContext;

    /**
     * The package manager.
     */
    private final PackageManager mPackageManager;

    /**
     * The directory containing the list running applications.
     */
    private final File mRunningAppsDirectory;

    /**
     * The sensor measurement task.
     */
    private ApplicationMeasurementTask mApplicationMeasurementTask;

    /**
     * The data collection interval.
     */
    private long mCollectionInterval;

    /**
     * The set of listeners that listen to updates to this task.
     */
    private Set<MeasurementListener> mMeasurementListeners = new HashSet<MeasurementListener>();

    /**
     * The list of running applications.
     */
    private ArrayList<Process> mProcesses = new ArrayList<Process>();

    /**
     * The map of running applications.
     */
    private HashMap<String, Application> mApplicationMap = new HashMap<String, Application>();

    /**
     * The map of running system processes.
     */
    private HashMap<String, Process> mProcessMap = new HashMap<String, Process>();

    /**
     * The set of process information objects for running apps.
     */
    private HashMap<Integer,ProcessInfo> mProcessInfoCache = new HashMap<Integer, ProcessInfo>();

    /**
     * The lock for the benchmark data.
     */
    private Lock mLock = new ReentrantLock();

    /**
     * Create a new application collection task with the specified context.
     *
     * @param context the application context.
     */
    public ApplicationCollectionTask(Context context) {
        this(context, SensorConstants.DEFAULT_COLLECTION_INTERVAL, null);
    }

    /**
     * Create a new application collection task with the specified context and collection interval.
     *
     * @param context the application context.
     * @param collectionInterval the data collection interval.
     */
    public ApplicationCollectionTask(Context context, long collectionInterval) {
        this(context, collectionInterval, null);
    }

    /**
     * Create a new application collection task with the specified context. Register the specified
     * measurement listener with this task.
     *
     * @param context the application context.
     * @param measurementListener a listener to register with this task.
     */
    public ApplicationCollectionTask(Context context, MeasurementListener measurementListener) {
        this(context, SensorConstants.DEFAULT_COLLECTION_INTERVAL, measurementListener);
    }

    /**
     * Create a new collection task with the specified context and collection interval. Register the
     * specified measurement listener with this task.
     *
     * @param context the application context.
     * @param collectionInterval  the data collection interval.
     * @param measurementListener a listener to register with this task.
     */
    public ApplicationCollectionTask(Context context, long collectionInterval, MeasurementListener measurementListener) {
        mContext = context;
        mPackageManager = mContext.getPackageManager();
        mCollectionInterval = collectionInterval;
        registerMeasurementListener(measurementListener);
        mRunningAppsDirectory = new File(SensorConstants.RUNNING_APPLICATIONS);
    }

    /**
     * Start the data collection task.
     */
    public void start() {
        if (mApplicationMeasurementTask == null) {
            mApplicationMeasurementTask = new ApplicationMeasurementTask();
            new Thread(mApplicationMeasurementTask).start();
        }
    }

    /**
     * Stop the data collection task.
     */
    public void stop() {
        if (mApplicationMeasurementTask != null) {
            mApplicationMeasurementTask.stop();
            mApplicationMeasurementTask = null;
        }
    }

    /**
     * Lock this collection task.
     */
    public void lock() {
        mLock.lock();
    }

    /**
     * Unlock this collection task.
     */
    public void unlock() {
        mLock.unlock();
    }

    /**
     * Register a {@link MeasurementListener} to listen to measurements in this task.
     *
     * @param measurementListener the listener to register.
     */
    public void registerMeasurementListener(MeasurementListener measurementListener) {
        if (measurementListener == null)
            return;

        synchronized (mMeasurementListeners) {
            mMeasurementListeners.add(measurementListener);
        }
    }

    /**
     * Unregister a {@link MeasurementListener} from listening to measurements in this task.
     *
     * @param measurementListener the listener to unregister.
     */
    public void unregisterMeasurementListener(MeasurementListener measurementListener) {
        if (measurementListener == null)
            return;

        synchronized (mMeasurementListeners) {
            mMeasurementListeners.remove(measurementListener);
        }
    }

    /**
     * Notify all registered listeners of a measurement event.
     */
    public void notifyAllListenersOfMeasurement() {
        synchronized (mMeasurementListeners) {
            for (MeasurementListener measurementListener : mMeasurementListeners) {
                measurementListener.onMeasurementReceived();
            }
        }
    }

    /**
     * Mark all the process information objects in the cache as invalid.
     */
    private void markAllProcessesInvalid() {
        lock();
        for (ProcessInfo processInfo : mProcessInfoCache.values()) {
            processInfo.markInvalid();
        }
        unlock();
    }

    /**
     * Clear out all the invalid processes information objects in the cache as invalid.
     */
    private void removeInvalidProcesses() {
        lock();
        LinkedList<ProcessInfo> invalidProcesses = new LinkedList<ProcessInfo>();
        for (ProcessInfo processInfo : mProcessInfoCache.values()) {
            if (!processInfo.isValid()) {
                invalidProcesses.add(processInfo);
            }
        }
        for (ProcessInfo processInfo : invalidProcesses) {
            mProcessInfoCache.remove(processInfo);
            String name = processInfo.getName();
            Application application = mApplicationMap.get(name);
            if (application != null) {
                application.removePid(processInfo.getPid());
                if (!application.hasPids())
                    mApplicationMap.remove(application.getPackageName());
            }
        }
        unlock();
    }

    /**
     * Read the process name for the specified pid.
     *
     * @param pid the process id of the process for which to read the name.
     */
    private String readProcessName(int pid) {
        String processName = null;
        RandomAccessFile file = null;
        try {
            String filename = String.format(SensorConstants.SENSOR_PROCESS_PACKAGE_NAME_TEMPLATE, pid);
            if (new File(filename).exists()) {
                file = new RandomAccessFile(filename, SensorConstants.MODE_READ);
                processName = file.readLine();
                if (processName != null)
                    processName = processName.trim();
            }
        } catch (Exception e) {
            if (Debug.isCollectionManagerLoggingEnabled())
                Debug.printDebug(e);
        } finally {
            try {
                if (file != null)
                    file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return processName;
    }

    /**
     * Update the list of running apps and processes.
     */
    public void updateMapOfRunningProcesses() {
        File[] files;
        lock();
        if ((files = mRunningAppsDirectory.listFiles()) != null) {
            for (File file : files) {
                String filename = file.getName();
                if (TextUtils.isDigitsOnly(filename)) {
                    int pid = Integer.parseInt(filename);
                    String processName = readProcessName(pid);
                    if (processName != null && !processName.equals(mContext.getPackageName())) {
                        ProcessInfo processInfo = mProcessInfoCache.get(pid);
                        if (processInfo == null) {
                            String name;
                            Drawable icon;
                            boolean isApplication;
                            try {
                                name = (String) mPackageManager.getApplicationLabel(mPackageManager.getApplicationInfo(processName, PackageManager.GET_META_DATA));
                                icon = mPackageManager.getApplicationIcon(processName);
                                isApplication = true;
                            } catch (PackageManager.NameNotFoundException e) {
                                name = processName;
                                icon = null;
                                isApplication = false;
                            }
                                mProcessInfoCache.put(pid, new ProcessInfo(pid, processName));
                            if (isApplication) {
                                Application application = mApplicationMap.get(processName);
                                if (application == null) {
                                    mApplicationMap.put(processName, new Application(mContext, pid, name, processName, icon));
                                } else {
                                    application.addPid(pid);
                                }
                            } else {
                                Process process = mProcessMap.get(processName);
                                if (process == null) {
                                    mProcessMap.put(processName, new Process(mContext, pid, name));
                                } else {
                                    process.addPid(pid);
                                }
                            }
                        } else {
                            processInfo.markValid();
                        }
                    }
                }
            }
        }
        unlock();
    }

    /**
     * Measure the data for each of the apps that are currently running in the system.
     */
    public void measureApplications() {
        lock();
        for (Application application : mApplicationMap.values()) {
            application.measure();
        }
        unlock();
    }

    /**
     * Measure the data for each of the processes that are currently running in the system.
     */
    public void measureProcesses() {
        lock();
        for (Process process : mProcessMap.values()) {
            process.measure();
        }
        unlock();
    }

    /**
     * Update the list of running processes.
     */
    public void updateProcesses() {
        lock();
        mProcesses.clear();
        for (Process process : mApplicationMap.values()) {
            if (process.isRunning())
                mProcesses.add(process);
        }
        unlock();
    }

    /**
     * Kill the specified process.
     *
     * @param process the process to kill.
     */
    public void killProcess(Process process) {
        lock();
        if (process.isApplication()) {
            mApplicationMap.remove(((Application) process).getPackageName());
        } else {
            mProcessMap.remove(process.getName());
        }

        for (Integer pid : process.getPids()) {
            mProcessInfoCache.remove(pid);
            android.os.Process.sendSignal(pid, android.os.Process.SIGNAL_KILL);
        }

        mProcesses.remove(process);
        unlock();
        notifyAllListenersOfMeasurement();
    }

    public ArrayList<Process> getProcesses() {
        return mProcesses;
    }

    public HashMap<String, Application> getApplicationMap() {
        return mApplicationMap;
    }

    public HashMap<String,Process> getProcessMap() {
        return mProcessMap;
    }

    /**
     * Task used for measuring the list of running apps.
     */
    class ApplicationMeasurementTask implements Runnable {

        /**
         * Flag indicating whether this task has been cancelled.
         */
        private boolean mCancelled = false;

        public void run() {
            while (!mCancelled) {
                markAllProcessesInvalid();
                updateMapOfRunningProcesses();
                removeInvalidProcesses();
                measureApplications();
                notifyAllListenersOfMeasurement();
                try {
                    Thread.sleep(mCollectionInterval);
                } catch (InterruptedException e) {
                }
            }
            lock();
            mApplicationMap.clear();
            mProcessMap.clear();
            mProcessInfoCache.clear();
            unlock();
        }

        public void stop() {
            mCancelled = true;
        }
    }

    /**
     * Class containing the information for a process.
     */
    class ProcessInfo {

        /**
         * The process id.
         */
        private final int mPid;

        /**
         * The process name.
         */
        private final String mName;

        /**
         * Flag indicating that this process is valid (it's recognized as running in the system).
         */
        private boolean mValid = true;

        public ProcessInfo(int pid, String name) {
            mPid = pid;
            mName = name;
        }

        public int getPid() {
            return mPid;
        }

        public String getName() {
            return mName;
        }

        public void markValid() {
            mValid = true;
        }

        public void markInvalid() {
            mValid = false;
        }

        public boolean isValid() {
            return mValid;
        }
    }

    /**
     * Interface used to listen to measurements in this collection task.
     */
    public interface MeasurementListener {
        void onMeasurementReceived();
    }
}
