package com.batterymentor.ui.app;


import android.app.ActivityManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.batterymentor.R;
import com.batterymentor.collectionmanager.ApplicationCollectionTask;
import com.batterymentor.constants.Constants;
import com.batterymentor.model.Model;
import com.batterymentor.sensors.app.Application;
import com.batterymentor.sensors.app.Process;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * The adapter that generates process rows.
 */
public class ProcessAdapter extends ArrayAdapter<Process> {

    /**
     * The context associated with the application.
     */
    private Context mContext;

    /**
     * The layout inflater.
     */
    private LayoutInflater mLayoutInflater;

    /**
     * The list of running processes.
     */
    public final ArrayList<Process> mProcesses;

    /**
     * The power formatter.
     */
    private DecimalFormat mPowerFormatter;

    /**
     * The cpu load formatter.
     */
    private DecimalFormat mCpuLoadFormatter;

    /**
     * The activity manager.
     */
    public final ActivityManager mActivityManager;

    /**
     * The application collection task.
     */
    private ApplicationCollectionTask mApplicationCollectionTask;

    /**
     * The model associated with this process adapter.
     */
    private Model mModel;

    public ProcessAdapter(Context context, ApplicationCollectionTask applicationCollectionTask, ArrayList<Process> values) {
        this(context, applicationCollectionTask, values, null);
    }

    public ProcessAdapter(Context context, ApplicationCollectionTask applicationCollectionTask, ArrayList<Process> values, Model model) {
        super(context, R.layout.process_list_item, values);
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mProcesses = values;
        mPowerFormatter = new DecimalFormat(context.getString(R.string.format_power));
        mCpuLoadFormatter = new DecimalFormat(context.getString(R.string.format_cpu));
        mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        mApplicationCollectionTask = applicationCollectionTask;
        mModel = model;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.process_list_item, null);
            ImageView icon = (ImageView) convertView.findViewById(R.id.process_icon);
            TextView name = (TextView) convertView.findViewById(R.id.process_name);
            TextView cpu = (TextView) convertView.findViewById(R.id.process_cpu);
            TextView power = (TextView) convertView.findViewById(R.id.process_power);
            Button stopButton = (Button) convertView.findViewById(R.id.process_button_stop);
            viewHolder = new ViewHolder();
            viewHolder.icon = icon;
            viewHolder.name = name;
            viewHolder.cpu = cpu;
            viewHolder.power = power;
            viewHolder.stopButton = stopButton;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Process process;
        mApplicationCollectionTask.lock();
        process = mProcesses.get(position);
        mApplicationCollectionTask.unlock();
        double cpuLoad = process.getCpuLoad();
        viewHolder.icon.setImageDrawable(process.getIcon());
        viewHolder.name.setText(process.getName());
        viewHolder.cpu.setText(String.format(mContext.getString(R.string.value_units_condensed_template), mCpuLoadFormatter.format(cpuLoad), mContext.getString(R.string.cpu_load)));
        if (mModel != null) {
            double power = mModel.getY(process.getCpuLoad());
            viewHolder.power.setText(String.format(mContext.getString(R.string.value_units_template), mPowerFormatter.format(power), mContext.getString(R.string.milliwatts)));
        } else {
            viewHolder.power.setText(Constants.EMPTY_STRING);
        }
        if (process.isApplication()) {
            final Application application = (Application)process;
            viewHolder.stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivityManager.killBackgroundProcesses(application.getPackageName());
                    mApplicationCollectionTask.killProcess(application);
                }
            });
            viewHolder.stopButton.setVisibility(View.VISIBLE);
        } else {
            viewHolder.stopButton.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void setModel(Model model) {
        mModel = model;
    }

    /**
     * Holder of the various list views for smoother scrolling.
     */
    static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView cpu;
        TextView power;
        Button stopButton;
    }
}
