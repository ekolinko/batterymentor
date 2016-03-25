package com.powerbench.ui.prototype;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import com.powerbench.R;
import com.powerbench.collectionmanager.ApplicationCollectionTask;
import com.powerbench.sensors.app.Process;
import com.powerbench.ui.app.ProcessAdapter;
import com.powerbench.ui.common.CommonActivity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The main powerbench activity that allows a user to view battery power consumption and charging
 * rate in realtime.
 */
public class RunningApplicationsActivity extends CommonActivity {

    /**
     * The primary application collection task.
     */
    private ApplicationCollectionTask mApplicationCollectionTask;

    /**
     * The measurement listener.
     */
    private ApplicationCollectionTask.MeasurementListener mMeasurementListener;

    /**
     * The handler used to measure the UI.
     */
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_list);
        initialize();
        getSupportActionBar().setTitle(getString(R.string.cpu_benchmark));
        mHandler = new Handler();
        mApplicationCollectionTask = new ApplicationCollectionTask(this);
        final ArrayList<Process> processes = mApplicationCollectionTask.getProcesses();
        final ProcessAdapter processAdapter = new ProcessAdapter(this, mApplicationCollectionTask, processes);
        mMeasurementListener = new ApplicationCollectionTask.MeasurementListener() {
            @Override
            public void onMeasurementReceived() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mApplicationCollectionTask.updateProcesses();
                            mApplicationCollectionTask.lock();
                            Collections.sort(processes);
                            mApplicationCollectionTask.unlock();
                            processAdapter.notifyDataSetChanged();
                        }
                    });
            }
        };
        ListView processList = (ListView) findViewById(R.id.process_list);
        processList.setAdapter(processAdapter);
        mApplicationCollectionTask.registerMeasurementListener(mMeasurementListener);
        mApplicationCollectionTask.start();
    }


    @Override
    protected void onServiceBound() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApplicationCollectionTask.registerMeasurementListener(mMeasurementListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mApplicationCollectionTask.unregisterMeasurementListener(mMeasurementListener);
    }

    @Override
    protected void onDestroy() {
        mApplicationCollectionTask.stop();
        super.onDestroy();
    }

    @Override
    public void onChargerConnected() {
    }

    @Override
    public void onChargerDisconnected() {
    }
}
