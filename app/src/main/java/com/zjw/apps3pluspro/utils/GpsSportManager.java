package com.zjw.apps3pluspro.utils;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.android.volley.VolleyError;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.eventbus.ShowDialogEvent;
import com.zjw.apps3pluspro.module.device.ScanDeviceActivity;
import com.zjw.apps3pluspro.module.device.weather.WeatherCityEntity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by android
 * on 2020/11/10
 */
public class GpsSportManager {
    private static final String TAG = GpsSportManager.class.getSimpleName();
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    private static GpsSportManager gpsSportManager;

    public static GpsSportManager getInstance() {
        if (gpsSportManager == null) {
            gpsSportManager = new GpsSportManager();
        }
        return gpsSportManager;
    }

    public GpsSportManager() {
    }

    private int type = 0; // 1 长时间定位
    private Notification notification;
    private int id;

    public GpsSportManager(int type, int id, Notification notification) {
        this.type = type;
        this.notification = notification;
        this.id = id;
    }

    public void stopGps(Context context) {
        try {
            locationListener = null;
            if (MyUtils.isGoogle(context)) {
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, googleLocationListener);
                    mGoogleApiClient = null;
                }
            } else {
                if (null != mlocationClient) {
                    mlocationClient.disableBackgroundLocation(true);
                    mlocationClient.onDestroy();
                    mlocationClient = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int m = 0;
    private Handler xaaaaaa = new Handler();
    Runnable xxx = new Runnable() {
        @Override
        public void run() {
            GpsInfo gpsInfo = new GpsInfo();
            gpsInfo.latitude = 22.628843315972222 + m * 0.00001;
            gpsInfo.longitude = 113.83821750217014 + m * 0.00001;
            gpsInfo.altitude = 1;
            if (locationListener != null) {
                locationListener.onLocationChanged(gpsInfo);
            }
            xaaaaaa.postDelayed(xxx, 3000);
            m++;
        }
    };

    public void getLatLon(Context context, LocationListener locationListener) {
        try {
            this.locationListener = locationListener;
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                EventBus.getDefault().post(new ShowDialogEvent(0));
                return;
            }
            if (!MyUtils.isGPSOpen(context)) {
                EventBus.getDefault().post(new ShowDialogEvent(1));
            }
            if (MyUtils.isGoogle(context)) {
                SysUtils.logAppRunning(TAG, "is google gps");
                //加载google 定位
                initGoogleMap(context);
            } else {
                SysUtils.logAppRunning(TAG, "is amap gps");
                initAMap(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private com.google.android.gms.maps.model.LatLng oldGoogleLatLng;
    private LocationRequest mLocationRequest;

    private com.google.android.gms.location.LocationListener googleLocationListener = this::onLocationChanged;

    private void onLocationChanged(Location location) {
        com.google.android.gms.maps.model.LatLng newLatLng = new com.google.android.gms.maps.model.LatLng(location.getLatitude(), location.getLongitude());

        if (oldGoogleLatLng != newLatLng) {
            if (newLatLng.latitude == 0.0 && newLatLng.longitude == 0.0) {
                return;
            }
            GpsInfo gpsInfo = new GpsInfo();
            gpsInfo.latitude = location.getLatitude();
            gpsInfo.longitude = location.getLongitude();
            gpsInfo.altitude = location.getAltitude();


            Log.i(TAG, "  google Accuracy=" + location.getAccuracy());

            int satellite = (int) location.getAccuracy();
            if (satellite >= 1 && satellite < 3) {
                gpsInfo.gpsAccuracy = GpsInfo.GPS_LOW;
            } else if (satellite >= 3 && satellite < 6) {
                gpsInfo.gpsAccuracy = GpsInfo.GPS_MEDIUM;
            } else if (satellite >= 6) {
                gpsInfo.gpsAccuracy = GpsInfo.GPS_HIGH;
            }

            mBleDeviceTools.setWeatherGps(gpsInfo.longitude + "," + gpsInfo.latitude);
            if (locationListener != null) {
                locationListener.onLocationChanged(gpsInfo);
            }
        }
        oldGoogleLatLng = newLatLng;
    }

    private void initGoogleMap(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        try {
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                            if (mLastLocation != null) {
                                oldGoogleLatLng = new com.google.android.gms.maps.model.LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            }

                            mLocationRequest = LocationRequest.create();
                            mLocationRequest.setInterval(5 * 1000); //5 seconds //间隔
                            mLocationRequest.setFastestInterval(3 * 1000); //3 seconds
                            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);//定位模式
                            mLocationRequest.setSmallestDisplacement(5F); //定位精度
                            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, googleLocationListener);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(connectionResult -> {

                })
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private AMapLocationClient mlocationClient;

    private void initAMap(Context context) {

        AMapLocationClientOption mLocationOption;

        mlocationClient = new AMapLocationClient(context.getApplicationContext());
        mLocationOption = getDefaultOption();
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        if(notification != null){
            mlocationClient.enableBackgroundLocation(id, notification);
        }

        mlocationClient.startLocation();
        // 设置定位监听
        mlocationClient.setLocationListener(location -> {
            if (location.getErrorCode() != 0) {
                SysUtils.logContentI(TAG, "errorcode =" + location.getErrorCode() + " info=" + location.getErrorInfo() + " detail=" + location.getLocationDetail());
                return;
            }
            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            if (newLatLng.latitude == 0.0 && newLatLng.longitude == 0.0) {
                return;
            }
            SysUtils.logContentI(TAG, "  new LatLng" + " lat = " + newLatLng.latitude + " lon = " + newLatLng.longitude);

            long nowTime = System.currentTimeMillis();
            if (oldLatLng != null) {
                long xSeconds = (nowTime - old_long_time) / 1000;
                float newDistance = AMapUtils.calculateLineDistance(oldLatLng, newLatLng);
                if (/*newDistance < Constants.GPS_OFFSET_DISTANCE_MIN ||*/ (newDistance / xSeconds) > Constants.GPS_OFFSET_DISTANCE_MAX)
                    return;
                if (oldLatLng != newLatLng) {
                    sendLocationChanged(location);
                }
            } else {
                sendLocationChanged(location);
            }
            oldLatLng = newLatLng;
        });
    }

    private void sendLocationChanged(AMapLocation location) {
        GpsInfo gpsInfo = new GpsInfo();

        double[] gps84 = GPSUtil.gcj02_To_Gps84(location.getLatitude(), location.getLongitude());
        gpsInfo.latitude = gps84[0];
        gpsInfo.longitude = gps84[1];
        gpsInfo.altitude = location.getAltitude();

        int satellite = location.getSatellites();
        if (satellite >= 1 && satellite < 3) {
            gpsInfo.gpsAccuracy = GpsInfo.GPS_LOW;
        } else if (satellite >= 3 && satellite < 6) {
            gpsInfo.gpsAccuracy = GpsInfo.GPS_MEDIUM;
        } else if (satellite >= 6) {
            gpsInfo.gpsAccuracy = GpsInfo.GPS_HIGH;
        }
        mBleDeviceTools.setWeatherGps(gpsInfo.longitude + "," + gpsInfo.latitude);

        if (locationListener != null) {
            locationListener.onLocationChanged(gpsInfo);
        }
    }

    private LatLng oldLatLng;
    private long old_long_time = 0;

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        if (type == 1) {
            mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);//仅设备模式。
            mOption.setMockEnable(true);
        } else {
//            mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
            mOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        }
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setInterval(1000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(false);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);

        return mOption;
    }

    public static class GpsInfo {
        public static final int GPS_LOW = 0x00;
        public static final int GPS_MEDIUM = 0x01;
        public static final int GPS_HIGH = 0x02;
        public static final int GPS_UNKNOWN = 0x0A; // no satellite

        public int gpsAccuracy;
        public long timestamp;
        public double longitude;
        public double latitude;
        public double altitude;
        public float speed;
        public float bearing;
        public float horizontal_accuracy;
        public float vertical_accuracy;
    }

    private LocationListener locationListener;

    public interface LocationListener {
        void onLocationChanged(GpsInfo gpsInfo);
    }


    public interface onWeatherListener {
        void onSuccess();
    }

    public interface onWeatherCityListener {
        void onSuccess();
    }

    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();

    public void getWeatherCity(Context context, onWeatherCityListener onWeatherCityListener) {
        RequestInfo mRequestInfo = RequestJson.getWeatherCity(context);
        MyLog.i(TAG, "getWeatherCity = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(BaseApplication.getmContext(), VolleyInterface.mListener, VolleyInterface.mErrorListener) {
            @Override
            public void onMySuccess(JSONObject result) {
                MyLog.i(TAG, "getWeatherCity result = " + result);
                try {
                    String code = result.optString("code");
                    if (code.equalsIgnoreCase(ResultJson.Code_operation_success)) {
                        JSONObject dataJson = result.optJSONObject("data");
                        JSONArray location = dataJson.optJSONArray("location");
                        JSONObject locationItem = location.getJSONObject(0);
                        mBleDeviceTools.setWeatherCity(locationItem.optString("adm2"));
                        mBleDeviceTools.setWeatherCityID(locationItem.optString("id"));
                        onWeatherCityListener.onSuccess();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyError(VolleyError arg0) {
                MyLog.i(TAG, "getWeatherCity arg0 = " + arg0);
            }
        });
    }

    public interface onWeatherCitySearchListener {
        void onSuccess(ArrayList<WeatherCityEntity> list);

        void onError();
    }

    public void getWeatherCityBySearch(Context context, onWeatherCitySearchListener onWeatherCitySearchListener, String city) {
        RequestInfo mRequestInfo = RequestJson.getWeatherCityBySearch(context, city);
        MyLog.i(TAG, "getWeatherCityBySearch = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(BaseApplication.getmContext(), VolleyInterface.mListener, VolleyInterface.mErrorListener) {
            @Override
            public void onMySuccess(JSONObject result) {
                MyLog.i(TAG, "getWeatherCityBySearch result = " + result);
                try {
                    String code = result.optString("code");
                    if (code.equalsIgnoreCase(ResultJson.Code_operation_success)) {
                        JSONObject dataJson = result.optJSONObject("data");
                        JSONArray location = dataJson.optJSONArray("location");

                        ArrayList<WeatherCityEntity> list = new ArrayList<>();
                        for (int i = 0; i < location.length(); i++) {
                            JSONObject locationItem = location.getJSONObject(i);
                            WeatherCityEntity weatherCityEntity = new WeatherCityEntity();
                            weatherCityEntity.name = locationItem.optString("name");
                            weatherCityEntity.id = locationItem.optString("id");
                            list.add(weatherCityEntity);
                        }
                        onWeatherCitySearchListener.onSuccess(list);
                    } else {
                        onWeatherCitySearchListener.onSuccess(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onWeatherCitySearchListener.onSuccess(null);
                }
            }

            @Override
            public void onMyError(VolleyError arg0) {
                MyLog.i(TAG, "getWeatherCityBySearch arg0 = " + arg0);
                onWeatherCitySearchListener.onError();
            }
        });
    }

    public void getWeatherArea(Context context, onWeatherListener onWeatherListener) {
        RequestInfo mRequestInfo = RequestJson.getWeatherArea(context);
        MyLog.i(TAG, "getWeatherArea = " + mRequestInfo.toString());
        SysUtils.logAppRunning(TAG, "getWeatherArea = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(BaseApplication.getmContext(), VolleyInterface.mListener, VolleyInterface.mErrorListener) {
            @Override
            public void onMySuccess(JSONObject result) {
                MyLog.i(TAG, "getWeatherArea result = " + result);
                SysUtils.logAppRunning(TAG, "getWeatherArea result = " + result);
                try {
                    String code = result.optString("code");
                    if (code.equalsIgnoreCase(ResultJson.Code_operation_success)) {
                        mUserSetTools.set_weather_now_data(result.toString());
                        getWeatherForecast(context, onWeatherListener);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyError(VolleyError arg0) {
                MyLog.i(TAG, "getWeatherArea arg0 = " + arg0);
                SysUtils.logAppRunning(TAG, "getWeatherArea arg0 = " + arg0);
            }
        });
    }

    private void getWeatherForecast(Context context, onWeatherListener onWeatherListener) {
        RequestInfo mRequestInfo = RequestJson.getWeatherForecast(context);
        MyLog.i(TAG, "getWeatherArea = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(BaseApplication.getmContext(), VolleyInterface.mListener, VolleyInterface.mErrorListener) {
            @Override
            public void onMySuccess(JSONObject result) {
                MyLog.i(TAG, "getWeatherForecast result = " + result);
                try {
                    String code = result.optString("code");
                    if (code.equalsIgnoreCase(ResultJson.Code_operation_success)) {
                        mUserSetTools.set_weather_his_data(result.toString());
                        mBleDeviceTools.setWeatherSyncTime(System.currentTimeMillis());
                        onWeatherListener.onSuccess();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyError(VolleyError arg0) {
                MyLog.i(TAG, "getWeatherForecast arg0 = " + arg0);
            }
        });
    }

    public double getDistance(double startLatitude, double startlongitude, double endlatitude, double endlongitude) {
        double lat1 = (Math.PI / 180) * startLatitude;
        double lat2 = (Math.PI / 180) * endlatitude;

        double lon1 = (Math.PI / 180) * startlongitude;
        double lon2 = (Math.PI / 180) * endlongitude;

//      double Lat1r = (Math.PI/180)*(gp1.getLatitudeE6()/1E6);
//      double Lat2r = (Math.PI/180)*(gp2.getLatitudeE6()/1E6);
//      double Lon1r = (Math.PI/180)*(gp1.getLongitudeE6()/1E6);
//      double Lon2r = (Math.PI/180)*(gp2.getLongitudeE6()/1E6);

        //地球半径
        double R = 6371;

        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;

        return d * 1000;
    }

    private PowerManager.WakeLock wakeLock = null;

    private void acquireWakeLock(Context context) {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, getClass()
                    .getCanonicalName());
            if (null != wakeLock) {
                //   Log.i(TAG, "call acquireWakeLock");
                wakeLock.acquire();
            }
        }
    }

    private void releaseWakeLock() {
        if (null != wakeLock && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
