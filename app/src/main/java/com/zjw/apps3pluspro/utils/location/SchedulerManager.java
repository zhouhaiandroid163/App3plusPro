package com.zjw.apps3pluspro.utils.location;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

/**
 * Created by android
 * on 2021/5/14
 */
public class SchedulerManager {
    private static SchedulerManager schedulerManager;

    public static SchedulerManager getInstance() {
        if (schedulerManager == null) {
            schedulerManager = new SchedulerManager();
        }
        return schedulerManager;
    }

    private JobScheduler scheduler;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void createJob(Context context) {
        Log.i("JobSchedulerService", "createJob");
        scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(context, com.zjw.apps3pluspro.utils.location.JobSchedulerService.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
            builder.setOverrideDeadline(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS);
            builder.setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS, JobInfo.BACKOFF_POLICY_LINEAR);//线性重试方案
        } else {
            builder.setPeriodic(10 * 1000);
        }
//        builder.setPersisted(true);
        scheduler.schedule(builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void cancelJob() {
        scheduler.cancelAll();
    }
}
