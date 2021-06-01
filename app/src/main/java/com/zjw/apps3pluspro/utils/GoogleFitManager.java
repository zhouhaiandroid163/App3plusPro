package com.zjw.apps3pluspro.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sql.entity.MovementInfo;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.concurrent.TimeUnit;

import static com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent;
import static com.google.android.gms.auth.api.signin.internal.zzh.getSignInResultFromIntent;

public class GoogleFitManager {

    private static final String TAG = GoogleFitManager.class.getCanonicalName();
    private static final int GOOGLE_SIGN_IN = 0x10;
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 0x20;
    private static final int REQUEST_OAUTH = 0x30;

    private static GoogleFitManager googleFitManager;
    private GoogleSignInAccount account;

    public static GoogleFitManager getInstance() {
        if (googleFitManager == null) {
            googleFitManager = new GoogleFitManager();
        }
        return googleFitManager;
    }

    private GoogleFitManager() {
    }

    FitnessOptions fitnessOptions;

    public void setDataType() {
        fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
//                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_WRITE)
                .build();
    }

    public void connectGoogle(Activity activity, SunbscreribListener sunbscreribListener) {
        this.sunbscreribListener = sunbscreribListener;
        try {
            //如果已经登录过google账号。则可以拿到账号
            account = GoogleSignIn.getLastSignedInAccount(activity);
            if (account == null) {
                //没有。则要登录
                MyLog.e(TAG, "没有。则要登录");
                signIn(activity);
            } else {
                //有，则要订阅
                MyLog.e(TAG, "//有，则要订阅");
                sunbscrerib(activity, sunbscreribListener);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void signIn(Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    /**
     * 在activity的onActivityResult方法里面调用，回调是否登录账号成功。
     *
     * @param data
     */
    public void handleSignInResult(int requestCode, int resultCode, Intent data, Activity activity) {
        switch (requestCode) {
            case GOOGLE_SIGN_IN:
                try {
                    GoogleSignInResult result = getSignInResultFromIntent(data);
                    if (result.isSuccess()) {
                        account = getSignedInAccountFromIntent(data).getResult(ApiException.class);
                        sunbscrerib(activity, sunbscreribListener);
                    } else
                        sunbscreribListener.onFail(null);
                } catch (ApiException e) {
                    MyLog.e(TAG, "signInResult:failed code=" + e.getStatusCode());
                    sunbscreribListener.onFail(null);
                }
                break;
            case GOOGLE_FIT_PERMISSIONS_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    MyLog.e(TAG, "allow Sunbscrerib");
                    subscriptionData(activity, sunbscreribListener);
                } else {
                    MyLog.e(TAG, "not allow sunbscrerib");
                    sunbscreribListener.onFail(null);
                }
                break;

            case REQUEST_OAUTH:
                authInProgress = false;
                if (resultCode == Activity.RESULT_OK) {
                    if (mApiClient == null) return;
                    if (!mApiClient.isConnecting() && !mApiClient.isConnected()) {
                        mApiClient.connect();
                        Log.e(TAG, "connect");
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e(TAG, "RESULT_CANCELED");
                } else {
                    Log.e(TAG, "requestCode NOT request_oauth");
                }
                break;
        }

    }

    public interface SunbscreribListener {
        void onSuccess(DataType datatype);

        void onFail(DataType datatype);
    }

    private SunbscreribListener sunbscreribListener;

    private void sunbscrerib(Activity activity, SunbscreribListener sunbscreribListener) {
        account = GoogleSignIn.getLastSignedInAccount(activity);
        if (account == null) {
            MyLog.e(TAG, "sunbscrerib account is null");
            return;
        }
        //判断是否有写入数据的权限，这个会弹出授权写入数据的弹框
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    activity, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    account,
                    fitnessOptions);
        } else {
            subscriptionData(activity, sunbscreribListener);
        }
    }

    private void subscriptionData(Activity activity, SunbscreribListener sunbscreribListener) {
        subscribe(DataType.TYPE_STEP_COUNT_DELTA, activity, sunbscreribListener);// 订阅步数
//        subscribe(DataType.TYPE_DISTANCE_DELTA, activity, sunbscreribListener);// 距离
    }

    // 订阅
    private void subscribe(final DataType dataType, Activity activity, final SunbscreribListener sunbscreribListener) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if (account == null) {
            return;
        }
        Fitness.getRecordingClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .subscribe(dataType)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //每订阅成功一个，会回调这个方法
                        MyLog.e(TAG, "sunbscrerib onSuccess =" + dataType);
                        sunbscreribListener.onSuccess(dataType);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sunbscreribListener.onFail(dataType);
                        MyLog.e(TAG, "sunbscrerib onFail =" + e);

                    }
                });
    }


    //    DataSet stepDataSet = createDataForRequest(DataType.TYPE_STEP_COUNT_DELTA, Field.FIELD_STEPS, stepAllCount, lastUploadTime, currentUploadTime);// 步数;
//    DataSet distanceDataSet = createDataForRequest(DataType.TYPE_DISTANCE_DELTA, Field.FIELD_DISTANCE, allDistance, lastUploadTime, currentUploadTime);// 距离;
    private void upStep(int step, long startTime, long endTime) {
        DataSet dataSet = createDataForRequest(DataType.TYPE_STEP_COUNT_DELTA, Field.FIELD_STEPS,
                step, startTime, endTime, HomeActivity.homeActivity);// 步数;
        upLoadGoogleFitData(HomeActivity.homeActivity, dataSet);
    }

    public DataSet createDataForRequest(DataType dataType, Field field, Object values, long startTime, long endTime, Activity activity) {
        DataSource dataSource =
                new DataSource.Builder()
                        .setAppPackageName(activity)
                        .setDataType(dataType)
                        .setStreamName("streamName")
                        .setType(DataSource.TYPE_RAW)
                        .build();
        DataSet dataSet = DataSet.create(dataSource);
        DataPoint dataPoint =
                dataSet.createDataPoint().setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        if (dataType == DataType.TYPE_CALORIES_EXPENDED || dataType == DataType.TYPE_HEART_RATE_BPM) {
            dataPoint.getValue(field).setFloat((Float) values);
        } else {
            //如果是float类型则要调用setFloagValues
            if (values instanceof Integer) {
                dataPoint.setIntValues((Integer) values);
            } else {
                dataPoint = dataPoint.setFloatValues((Float) values);
            }
        }
        dataSet.add(dataPoint);
        return dataSet;
    }

    public void upLoadGoogleFitData(final Activity activity, final DataSet stepDataSet) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Task<Void> responseStep = Fitness.getHistoryClient(activity, GoogleSignIn.getLastSignedInAccount(activity)).insertData(stepDataSet);
                MyLog.e(TAG, "upload google fit start");
            }
        }).start();
    }

    public void postGooglefitData(MovementInfo mMotionInfo) {
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        if (!mBleDeviceTools.getIsOpenGooglefit()) {
            MyLog.e(TAG, "google fit is close");
            return;
        }
        long lastTime = mBleDeviceTools.getGooglefitSyncLastTime();
        long dataTime = NewTimeUtils.getLongTime(mMotionInfo.getDate(), NewTimeUtils.TIME_YYYY_MM_DD);

        long oneDayTime = 24 * 3600 * 1000L;
        long oneHourTime = 3600 * 1000L;
        String data = mMotionInfo.getData();
        if (data == null) {
            return;
        }
        String[] steps = mMotionInfo.getData().split(",");
//        steps = new String[]{"100", "500", "100", "500", "100",
//                "100", "500", "100", "500", "100",
//                "100", "500", "100", "500", "100",
//                "100", "500", "100", "500", "100",
//                "100", "500", "100", "500"};

        String currentTime = NewTimeUtils.getStringDate(System.currentTimeMillis(), NewTimeUtils.TIME_YYYY_MM_DD_HHMMSS);
        String HHMMSS = currentTime.split(" ")[1];
        int currentHour = Integer.parseInt(HHMMSS.split(":")[0]);
        int currentMinute = Integer.parseInt(HHMMSS.split(":")[1]);
        MyLog.w(TAG, "upload google fit start HH:MM:SS = " + HHMMSS);

        int totalStep = 0;
        for (int i = 0; i < steps.length; i++) {
            int step = Integer.parseInt(steps[i]);
            long startTime = dataTime + oneHourTime * i;
            if (i < currentHour) {
                MyLog.e(TAG, "upload google fit start num mMotionInfo " + mMotionInfo.getDate() + " " + i + " step=" + step);
                GoogleFitManager.getInstance().UpLoadGooglefitStep(step, startTime, 3600 * 1000 - 1, HomeActivity.homeActivity);
                totalStep = totalStep + step;
            } else if (i == currentHour) {
                int timeMode = 60 * 1000;
                if (currentMinute > 5) {
                    timeMode = (currentMinute - 5) * 60 * 1000;
                }
                MyLog.e(TAG, "upload google fit start num mMotionInfo " + mMotionInfo.getDate() + " " + i + " step=" + step);
                GoogleFitManager.getInstance().UpLoadGooglefitStep(step, startTime, timeMode, HomeActivity.homeActivity);
                totalStep = totalStep + step;
            } else {
                break;
            }
        }
        MyLog.w(TAG, "upload google fit end totalStep = " + totalStep);
    }

    private GoogleApiClient mApiClient;
    private boolean authInProgress = false;

    public void OpenGoogleFit(Context context) {
        mApiClient = new GoogleApiClient.Builder(context)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.RECORDING_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.e(TAG, "mApiClient onConnected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        if (!authInProgress) {
                            try {
                                authInProgress = true;
                                connectionResult.startResolutionForResult(HomeActivity.homeActivity, REQUEST_OAUTH);
                            } catch (IntentSender.SendIntentException e) {

                            }
                        } else {
                            Log.e(TAG, "authInProgress");
                        }
                    }
                })
                .build();
        mApiClient.connect();

    }

    public void UpLoadGooglefitStep(final int steps, final long timemillis, final int timeMode, final Activity activity) {
        if (mApiClient == null) {
            if (BaseApplication.getBleDeviceTools().getIsOpenGooglefit()) {
                OpenGoogleFit(HomeActivity.homeActivity);
            } else {
                return;
            }
        }
        if (mApiClient == null) {
            return;
        }
//        if (steps == 0) return;
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DataSource stepSource = new DataSource.Builder()
                            .setAppPackageName(activity.getApplicationContext().getPackageName())
                            .setStreamName("Googlefit" + " - step count")
                            .setDataType(DataType.AGGREGATE_STEP_COUNT_DELTA)
                            .setType(DataSource.TYPE_RAW)
                            .build();
                    DataSet dataSet = DataSet.create(stepSource);
                    DataPoint stepDataPoint = dataSet.createDataPoint();
                    stepDataPoint.setTimeInterval(timemillis, timemillis + timeMode, TimeUnit.MILLISECONDS);
                    stepDataPoint.getValue(Field.FIELD_STEPS).setInt(steps);
                    dataSet.add(stepDataPoint);
                    com.google.android.gms.common.api.Status insertStatus = Fitness.HistoryApi.insertData(mApiClient, dataSet).await(1, TimeUnit.MINUTES);
                    if (!insertStatus.isSuccess()) {
                        Log.i(TAG, "There was a problem inserting the dataset.");
                    }
                    Log.i(TAG, "step insert was successful!" + steps);

                    DataSource distanceSource = new DataSource.Builder()
                            .setAppPackageName(activity.getApplicationContext().getPackageName())
                            .setStreamName("Googlefit" + " - distance count")
                            .setDataType(DataType.AGGREGATE_DISTANCE_DELTA)
                            .setType(DataSource.TYPE_RAW)
                            .build();
                    DataSet distanceSet = DataSet.create(distanceSource);
                    DataPoint distanceDataPoint = distanceSet.createDataPoint();
                    distanceDataPoint.setTimeInterval(timemillis, timemillis + timeMode, TimeUnit.MILLISECONDS);
                    float distance = Float.parseFloat(AppUtils.getDistance(steps));
                    distanceDataPoint.getValue(Field.FIELD_DISTANCE).setFloat(distance);
                    distanceSet.add(distanceDataPoint);

                    com.google.android.gms.common.api.Status StatusDistance = Fitness.HistoryApi.insertData(mApiClient, distanceSet).await(1, TimeUnit.MINUTES);
                    if (!StatusDistance.isSuccess()) {
                        Log.i(TAG, "There was a problem inserting the distanceSet.");
                    }
                    Log.i(TAG, "distance insert was successful!" + distance);

                    DataSource calDataSource = new DataSource.Builder()
                            .setAppPackageName(activity.getApplicationContext().getPackageName())
                            .setStreamName("Googlefit" + " - cal count")
                            .setDataType(DataType.AGGREGATE_CALORIES_EXPENDED)
                            .setType(DataSource.TYPE_RAW)
                            .build();
                    DataSet calSet = DataSet.create(calDataSource);
                    DataPoint calDataPoint = calSet.createDataPoint();
                    calDataPoint.setTimeInterval(timemillis, timemillis + timeMode, TimeUnit.MILLISECONDS);
                    float calorie = Float.parseFloat(AppUtils.getCalory(steps));
                    calDataPoint.getValue(Field.FIELD_CALORIES).setFloat(calorie);
                    calSet.add(calDataPoint);

                    com.google.android.gms.common.api.Status StatusCal = Fitness.HistoryApi.insertData(mApiClient, calSet).await(1, TimeUnit.MINUTES);
                    if (!StatusCal.isSuccess()) {
                        Log.i(TAG, "There was a problem inserting the calSet.");
                    }
                    Log.i(TAG, "cal insert was successful!" + calorie);
                }
            }).start();

        } catch (Exception e) {
            Log.i(TAG, "Data insert Exception!" + e);
        }
    }

    public void updateHeart(final Activity activity, final float bmp, final long timemillis) {
        if (mApiClient == null) {
            if (BaseApplication.getBleDeviceTools().getIsOpenGooglefit()) {
                OpenGoogleFit(HomeActivity.homeActivity);
            } else {
                return;
            }
        }
        if (mApiClient == null) {
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                DataSource heartSource = new DataSource.Builder()
                        .setAppPackageName(activity.getApplicationContext().getPackageName())
                        .setStreamName("Googlefit" + " - heart rate")
                        .setDataType(DataType.TYPE_HEART_RATE_BPM)
                        .setType(DataSource.TYPE_RAW)
                        .build();
                DataSet heartdataSet = DataSet.create(heartSource);
                DataPoint heartDataPoint = heartdataSet.createDataPoint();
                heartDataPoint.setTimestamp(timemillis / 60000, TimeUnit.MINUTES);
                heartDataPoint.getValue(Field.FIELD_BPM).setFloat(bmp);
                heartdataSet.add(heartDataPoint);
                com.google.android.gms.common.api.Status StatusCal = Fitness.HistoryApi.insertData(mApiClient, heartdataSet).await(1, TimeUnit.MINUTES);
                if (!StatusCal.isSuccess()) {
                    Log.i(TAG, "There was a problem inserting the heartdataSet.");
                }
                Log.i(TAG, "heartdataSet insert was successful!");
            }
        }).start();
    }


}
