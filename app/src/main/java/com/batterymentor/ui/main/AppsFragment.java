package com.batterymentor.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.batterymentor.R;
import com.batterymentor.collectionmanager.ApplicationCollectionTask;
import com.batterymentor.collectionmanager.CollectionManager;
import com.batterymentor.model.BatteryModel;
import com.batterymentor.model.ModelManager;
import com.batterymentor.sensors.app.Process;
import com.batterymentor.ui.app.ProcessAdapter;
import com.batterymentor.ui.benchmark.CpuTestActivity;
import com.batterymentor.ui.common.CommonFragment;
import com.batterymentor.ui.theme.Theme;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Fragment for showing the apps gadget.
 */
public class AppsFragment extends CommonFragment {

    /**
     * The battery model.
     */
    private BatteryModel mBatteryModel;

    /**
     * The button for running the screen test.
     */
    private Button mAppsTestButton;

    /**
     * The handler used to measure the UI.
     */
    private Handler mHandler;

    /**
     * The current theme.
     */
    private Theme mTheme;

    /**
     * The primary application collection task.
     */
    private ApplicationCollectionTask mApplicationCollectionTask;

    /**
     * The measurement listener.
     */
    private ApplicationCollectionTask.MeasurementListener mMeasurementListener;

    /**
     * The process adapter.
     */
    private ProcessAdapter mProcessAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);
        mHandler = new Handler();
        mBatteryModel = ModelManager.getInstance().getBatteryModel(getActivity());
        mAppsTestButton = (Button) view.findViewById(R.id.button_apps_test);
        mAppsTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CpuTestActivity.class));
            }
        });
        mApplicationCollectionTask = CollectionManager.getInstance().getApplicationCollectionTask(getContext());
        mApplicationCollectionTask.lock();
        final ArrayList<Process> processes = mApplicationCollectionTask.getProcesses();
        mProcessAdapter = new ProcessAdapter(getContext(), mApplicationCollectionTask, processes);
        if (mBatteryModel.getCpuModel() != null) {
            mProcessAdapter.setModel(mBatteryModel.getCpuModel());
        }
        mApplicationCollectionTask.unlock();
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
                        mProcessAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        ListView processList = (ListView) view.findViewById(R.id.process_list);
        processList.setAdapter(mProcessAdapter);
        mApplicationCollectionTask.registerMeasurementListener(mMeasurementListener);
        if (mTheme != null) {
            applyTheme(mTheme);
        }
        setRetainInstance(true);
        return view;
    }

    /**
     * Apply the specified theme to this fragment.
     *
     * @param theme the theme to apply to this fragment.
     */
    @Override
    public void applyTheme(Theme theme) {
        mTheme = theme;
        if (mAppsTestButton != null) {
            mAppsTestButton.setBackgroundResource(mTheme.getButtonResource());
        }
    }

    @Override
    public void refresh() {
        if (mBatteryModel == null) {
            mBatteryModel = ModelManager.getInstance().getBatteryModel(getActivity());
        }

        if (mProcessAdapter != null && mBatteryModel.getCpuModel() != null) {
            mProcessAdapter.setModel(mBatteryModel.getCpuModel());
            mProcessAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mApplicationCollectionTask.registerMeasurementListener(mMeasurementListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mApplicationCollectionTask.unregisterMeasurementListener(mMeasurementListener);
    }
}