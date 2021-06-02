package com.zjw.apps3pluspro.utils.location;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by android
 * on 2021/5/14
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {
    private static final String TAG = JobSchedulerService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.w(TAG, "onStartJob");
        doJob(params);
        SchedulerManager.getInstance().createJob(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobFinished(params, true);
        } else {
            jobFinished(params, false);
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("stop service", "stop job service");
        return false;
    }

    public PowerManager.WakeLock wakeLock = null;
    public PowerManager powerManager = null;

    @SuppressLint("InvalidWakeLockTag")
    private void doJob(JobParameters params) {
        Log.i(TAG, "do job service");
        powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(1, "ffit-Runner");
            if (wakeLock != null) {
                wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
                wakeLock.release();
                Log.e(TAG, "acquire");
            }
        }
    }

    @Override
    public void onDestroy() {
        if (wakeLock != null) {
            if (wakeLock.isHeld())
                wakeLock.release();
            wakeLock = null;
        }
        super.onDestroy();
    }
}
