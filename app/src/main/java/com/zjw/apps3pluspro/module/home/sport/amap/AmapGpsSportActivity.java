package com.zjw.apps3pluspro.module.home.sport.amap;


import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.module.home.sport.DeviceSportManager;
import com.zjw.apps3pluspro.module.home.sport.MoreSportActivity;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.module.home.sport.SportModleUtils;
import com.zjw.apps3pluspro.sql.dbmanager.SportModleInfoUtils;
import com.zjw.apps3pluspro.sql.entity.SportModleInfo;
import com.zjw.apps3pluspro.view.seekbar.CircularSeekBar;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.FontsUtils;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 高德运动
 */
public class AmapGpsSportActivity extends Activity implements LocationSource, View.OnClickListener {
    private final String TAG = AmapGpsSportActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    //数据库存储
    private SportModleInfoUtils mSportModleInfoUtils = BaseApplication.getSportModleInfoUtils();

    private TextView yundong_time, map_duration;
    private TextView yundong_distance, map_distance, tvDistanceUnit;
    private TextView yundong_unit;
    private TextView yundong_velocity, map_speed;

    private AMapLocationClient mlocationClient;
    private OnLocationChangedListener mListener;
    private AMapLocationClientOption mLocationOption;

    private MapView yundong_map;
    private AMap mAMap;

    private PolylineOptions mPolyoptions;

    //以前的定位点
    private LatLng oldLatLng;

    //是否是第一次定位
    public boolean isFirst = true;

    private Button yundong_pause;
    private Button yundong_stop;
    private RelativeLayout gps_sport_mapview;

    private LinearLayout gps_sport_bootom_lin1, gps_sport_bootom_lin2;
    private TextView gps_sport_long;

    private LinearLayout measure_daojishi_line;
    private TextView measure_daojishi_text;
    List<LatLng> latLngList = new ArrayList<>();
    private float sport_distance = 0;

    private long old_long_time = 0;
    private LinearLayout gps_sport_lin;
    private ImageView gps_sport_img;
    private ImageView gps_sport_signa1, gps_sport_signa2, gps_sport_signa3;
    private ImageView gps_sport_signa;

    CircularSeekBar gps_sport_seekbar;
    private int SeekBarMax = 50;
    private int SeekBarTime = 20;
    private View gps_sport_view1;

    // 语音合成对象
//    private SpeechSynthesizer mTts;

    private String start_sport_time = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setStatusBarMode(this, false, R.color.title_bg_gps);
        setContentView(R.layout.activity_gps_sport);
        mContext = AmapGpsSportActivity.this;
        SysUtils.logAmapGpsI(TAG, "onCreate");
        //设置参数
//        setParam();
        initView();
        yundong_map = (MapView) findViewById(R.id.yundong_map);
        yundong_map.onCreate(savedInstanceState);
        initMap();
        initLocation();
        initpolyline();
        DJSStart();
    }


    void initView() {
        yundong_time = (TextView) findViewById(R.id.yundong_time);
        map_duration = (TextView) findViewById(R.id.map_duration);
        yundong_distance = (TextView) findViewById(R.id.yundong_distance);
        map_distance = (TextView) findViewById(R.id.map_distance);
        map_distance = (TextView) findViewById(R.id.map_distance);
        yundong_unit = (TextView) findViewById(R.id.yundong_unit);
        tvDistanceUnit = (TextView) findViewById(R.id.tvDistanceUnit);
        yundong_velocity = (TextView) findViewById(R.id.yundong_velocity);
        map_speed = (TextView) findViewById(R.id.map_speed);

        ivShowMap = findViewById(R.id.ivShowMap);
        ivShowMap.setOnClickListener(this);

        yundong_pause = (Button) findViewById(R.id.yundong_pause);
        yundong_pause.setOnClickListener(this);

        yundong_stop = (Button) findViewById(R.id.yundong_stop);
        yundong_stop.setOnClickListener(this);


        measure_daojishi_line = (LinearLayout) findViewById(R.id.measure_daojishi_line);
        measure_daojishi_text = (TextView) findViewById(R.id.measure_daojishi_text);
        measure_daojishi_text.setTypeface(FontsUtils.modefyNumber(this));

        gps_sport_lin = (LinearLayout) findViewById(R.id.gps_sport_lin);
        gps_sport_img = (ImageView) findViewById(R.id.gps_sport_img);
        gps_sport_img.setOnClickListener(this);

        gps_sport_signa = (ImageView) findViewById(R.id.gps_sport_signa);
        gps_sport_signa1 = (ImageView) findViewById(R.id.gps_sport_signa1);
        gps_sport_signa2 = (ImageView) findViewById(R.id.gps_sport_signa2);
        gps_sport_signa3 = (ImageView) findViewById(R.id.gps_sport_signa3);
        gps_sport_signa3 = (ImageView) findViewById(R.id.gps_sport_signa3);


        gps_sport_mapview = (RelativeLayout) findViewById(R.id.gps_sport_mapview);
        findViewById(R.id.gps_sport_lo).setOnClickListener(this);

        ButtonListener b = new ButtonListener();
        yundong_pause.setOnClickListener(b);
        yundong_pause.setOnTouchListener(b);


        gps_sport_seekbar = (CircularSeekBar) findViewById(R.id.gps_sport_seekbar);
        gps_sport_seekbar.setMax(SeekBarMax);
        gps_sport_seekbar.setProgress(0);
        gps_sport_seekbar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        gps_sport_view1 = (View) findViewById(R.id.gps_sport_view1);

        gps_sport_bootom_lin1 = (LinearLayout) findViewById(R.id.gps_sport_bootom_lin1);
        gps_sport_bootom_lin2 = (LinearLayout) findViewById(R.id.gps_sport_bootom_lin2);
        gps_sport_long = (TextView) findViewById(R.id.gps_sport_long);
        findViewById(R.id.yundong_continue).setOnClickListener(this);

        if (mBleDeviceTools.get_device_unit() == 1) {
            yundong_unit.setText(getString(R.string.sport_distance_unit));
            tvDistanceUnit.setText(getString(R.string.sport_distance_unit));
        } else {
            yundong_unit.setText(getString(R.string.unit_mi));
            tvDistanceUnit.setText(getString(R.string.unit_mi));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yundong_pause:
                break;
            case R.id.yundong_stop:
                showQuitGpsSport();
                break;
            case R.id.gps_sport_img:
                ChangeMapUi(false);
                break;
            case R.id.ivShowMap:
                ChangeMapUi(true);
                break;
            case R.id.gps_sport_lo:
                if (oldLatLng != null) {
                    mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(oldLatLng.latitude, oldLatLng.longitude), 17), 1000, null);
                }
                break;
            case R.id.yundong_continue:
                YDStart();
                gps_sport_bootom_lin1.setVisibility(View.VISIBLE);
                gps_sport_long.setVisibility(View.VISIBLE);
                gps_sport_bootom_lin2.setVisibility(View.GONE);
                break;
        }
    }

    private void initMap() {
        //实例化地图类
        if (mAMap == null) {
            mAMap = yundong_map.getMap();
            UiSettings mUiSettings;
            mUiSettings = mAMap.getUiSettings();//实例化UiSettings类
            mUiSettings.setRotateGesturesEnabled(false);
            mUiSettings.setRotateGesturesEnabled(true);//是否允许旋转
//            mAMap.moveCamera(CameraUpdateFactory.zoomBy(16));
            mAMap.moveCamera(CameraUpdateFactory.zoomTo(17));//将地图的缩放级别调整到17级
            setUpMap();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        mAMap.getUiSettings().setZoomControlsEnabled(false);
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(
                BitmapDescriptorFactory.fromResource(R.drawable.my_img_map_stud));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        // 自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(0);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        // 将自定义的 myLocationStyle 对象添加到地图上
        mAMap.setMyLocationStyle(myLocationStyle);
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    private void initpolyline() {
        mPolyoptions = new PolylineOptions();
        mPolyoptions.width(10f);
//        mPolyoptions.geodesic(true); //是否绘制大地曲线
        mPolyoptions.color(Color.parseColor("#ff0000"));
    }

    private void setpolyline(LatLng oldData, LatLng newData) {
        if (!mPolyoptions.getPoints().isEmpty()) {
            mPolyoptions.getPoints().clear();
        }
        mAMap.addPolyline(mPolyoptions.add(oldData, newData));
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (location == null) {
                SysUtils.logAmapGpsE(TAG, "location == null");
                return;
            }
            //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
            if (location.getErrorCode() != 0) {
                SysUtils.logAmapGpsE(TAG, "errorcode =" + location.getErrorCode() + " info=" + location.getErrorInfo() + " detail=" + location.getLocationDetail());
                AppUtils.showToast(mContext, R.string.sport_no_gps);
                return;
            }
            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            boolean isChange = false;
            switch (location.getLocationType()) {
                case AMapLocation.LOCATION_TYPE_GPS:
                    isChange = true;
                    break;
                case AMapLocation.LOCATION_TYPE_SAME_REQ:
                case AMapLocation.LOCATION_TYPE_FAST:
                case AMapLocation.LOCATION_TYPE_FIX_CACHE:
                case AMapLocation.LOCATION_TYPE_WIFI:
                case AMapLocation.LOCATION_TYPE_CELL:
                case AMapLocation.LOCATION_TYPE_AMAP:
                case AMapLocation.LOCATION_TYPE_OFFLINE:
                case AMapLocation.LOCATION_TYPE_LAST_LOCATION_CACHE:
                    break;
                default:
                    break;
            }

            updateSignalUi(location.getSatellites());

            SysUtils.logAmapGpsI(TAG, " loc type=" + location.getLocationType() + "  lon=" + location.getLongitude() + "  lat=" + location.getLatitude()
                    + "  accuracy=" + location.getAccuracy() + "m  speed=" + location.getSpeed() + "m/s  beaing=" + location.getBearing()
                    + "  Satellites=" + location.getSatellites() + "  adress=" + location.getAddress());
            if (isChange) {
                if (newLatLng.latitude == 0.0 && newLatLng.longitude == 0.0) {
                    return;
                }
                long nowTime = System.currentTimeMillis();
                if (oldLatLng != null) {
                    long xSeconds = (nowTime - old_long_time) / 1000;
                    float newDistance = AMapUtils.calculateLineDistance(oldLatLng, newLatLng);
                    if (newDistance < Constants.GPS_OFFSET_DISTANCE_MIN || (newDistance / xSeconds) > Constants.GPS_OFFSET_DISTANCE_MAX)
                        return;
                    if (oldLatLng != newLatLng) {
                        mListener.onLocationChanged(location);// 显示系统小蓝点
                        setDistance(oldLatLng, newLatLng, old_long_time, nowTime);
                        setpolyline(oldLatLng, newLatLng);
                    }
                }
                latLngList.add(newLatLng);
                oldLatLng = newLatLng;
                old_long_time = nowTime;
            }
        }
    };


    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        //初始化client
        mlocationClient = new AMapLocationClient(this.getApplicationContext());
        mLocationOption = getDefaultOption();
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 设置定位监听
        mlocationClient.setLocationListener(locationListener);
    }

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
        //根据控件的选择，重新设置定位参数
//        resetOption();
        // 设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 启动定位
        mlocationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        mlocationClient.stopLocation();
    }


    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
//        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);//仅设备模式。
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(1000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true

        return mOption;
    }


    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;

    }


    public void deactivate() {

    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        yundong_map.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        yundong_map.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        yundong_map.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        yundong_map.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }

        DJSHandler.removeCallbacksAndMessages(null);
        YDHaSndler.removeCallbacksAndMessages(null);
        MySeekBarHandler.removeCallbacksAndMessages(null);

//        if (null != mTts) {
//            mTts.stopSpeaking();
//            mTts.destroy();
//        }
//        SpeechUtility.getUtility().destroy();

    }


    // 开始的5秒倒计时
    private boolean DJSIsStop = false;
    private int DJSdown = 3;
    private Handler DJSHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 添加更新ui的代码
                    if (!DJSIsStop) {


                        if (DJSdown > 0) {
                            measure_daojishi_text.setText(String.valueOf(DJSdown));
                            DJSHandler.sendEmptyMessageDelayed(1, 1000);
                            //                            mTts.startSpeaking(String.valueOf(DJSdown), mTtsListener);


                        } else if (DJSdown == 0) {
                            measure_daojishi_text.setText("Go");
                            //                            mTts.startSpeaking("开始", mTtsListener);
                            DJSHandler.sendEmptyMessageDelayed(1, 1000);
                        } else {
                            DJSStop();
                        }
                        DJSdown -= 1;

                    }
                    break;
                case 0:
                    break;
            }
        }

    };

    private void DJSStart() {
        DJSdown = 3;
        DJSHandler.removeMessages(1);
        DJSHandler.sendEmptyMessage(1);
        measure_daojishi_line.setVisibility(View.VISIBLE);
        DJSIsStop = false;

    }

    private void DJSStop() {
        DJSHandler.sendEmptyMessage(0);
        measure_daojishi_line.setVisibility(View.GONE);
        DJSIsStop = true;
        yundong_pause.setVisibility(View.VISIBLE);
        YDStart();

    }


    // 开始的5秒倒计时
    private boolean YDIsStop = false;
    private int YDdown = 0;
    private Handler YDHaSndler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 添加更新ui的代码
                    if (!YDIsStop) {


                        YDdown += 1;
                        YDHaSndler.sendEmptyMessageDelayed(1, 1000);


                        String sport_time_used = SportModleUtils.getcueDate(YDdown * 1000);


                        yundong_time.setText(sport_time_used);
                        map_duration.setText(sport_time_used);

                    }
                    break;
                case 0:
                    break;
            }
        }

    };

    private void YDStart() {
        startLocation();
        gps_sport_img.setVisibility(View.VISIBLE);
        YDHaSndler.removeMessages(1);
        YDHaSndler.sendEmptyMessage(1);
        YDIsStop = false;
        start_sport_time = MyTime.getAllTime();

    }

    private void YDStop() {
        stopLocation();
        YDHaSndler.sendEmptyMessage(0);
        YDIsStop = true;

    }


    void setDistance(LatLng oldLatLng, LatLng newLatLng, long old_time, long new_time) {

        float now_distance = AMapUtils.calculateLineDistance(oldLatLng, newLatLng);

//
//        {
//        if(now_distance<10)
//            return;
//        }

        sport_distance += now_distance;

//        yundong_distance.setText(String.valueOf(df.format(sport_distance / 1000)) + "/" + now_distance);

        //        yundong_distance.setText(String.valueOf(df.format(sport_distance / 1000)));

        if (mBleDeviceTools.get_device_unit() == 1) {
            yundong_distance.setText(AppUtils.GetFormat(2, Float.valueOf((sport_distance / 1000))));
            map_distance.setText(AppUtils.GetFormat(2, Float.valueOf((sport_distance / 1000))));
        } else {
            yundong_distance.setText(AppUtils.GetFormat(2, Float.valueOf((sport_distance / 1000 / 1.61f))));
            map_distance.setText(AppUtils.GetFormat(2, Float.valueOf((sport_distance / 1000 / 1.61f))));
        }


        if (sport_distance != 0f) {

//            yundong_velocity.setText(getPeiSu(new_time, old_time, now_distance));
            yundong_velocity.setText(getPeiSuAll(YDdown, sport_distance));
            map_speed.setText(getPeiSuAll(YDdown, sport_distance));


        }
    }


    public void onBackPressed() {
//        if (null != mTts) {
//            mTts.stopSpeaking();
//            mTts.destroy();
//        }
    }

//    private void setParam() {
//        // TODO Auto-generated method stub
//        //2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
//        //设置讯飞
//        SpeechUtility.createUtility(AmapGpsSportActivity.this, "appid=" + getString(R.string.xunfei_id));
//        //语音
//        mTts = SpeechSynthesizer.createSynthesizer(AmapGpsSportActivity.this, null);
//        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
//        mTts.setParameter(SpeechConstant.SPEED, "60");//设置语速
//        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
//        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
//    }


    /**
     * 显示设置Gps的对话框
     */
    private void showQuitGpsSport() {
        int sport_calory = 0;
        StringBuffer buffer = new StringBuffer();
        if (latLngList != null && latLngList.size() >= 2) {
            for (int i = 0; i < latLngList.size(); i++) {
                if (i < latLngList.size() - 1) {
                    buffer.append(latLngList.get(i).longitude + "," + latLngList.get(i).latitude + ";");
                } else {
                    buffer.append(latLngList.get(i).longitude + "," + latLngList.get(i).latitude);
                }
            }
            sport_calory = SportModleUtils.getCalory(sport_distance);
        }
        final int finalSport_calory = sport_calory;
        final StringBuffer finalBuffer = buffer;

        String message = "";
        if (sport_calory == 0) {
            message = getString(R.string.no_Track) + "," + getString(R.string.dialog_quit_sport_content);
        }

        if (finalSport_calory > 0) {
            SportModleInfo mSportModleInfo = new SportModleInfo();
            mSportModleInfo.setUser_id(BaseApplication.getUserId());
            mSportModleInfo.setTime(start_sport_time);
            mSportModleInfo.setSport_duration(String.valueOf(YDdown));
            mSportModleInfo.setSport_type("100");
            mSportModleInfo.setUi_type("100");
            mSportModleInfo.setSpeed(yundong_velocity.getText().toString());
            mSportModleInfo.setDisance(String.valueOf((int) sport_distance));
            mSportModleInfo.setMap_data(finalBuffer.toString());
            mSportModleInfo.setCalorie(String.valueOf(finalSport_calory));
            boolean isSuccess = mSportModleInfoUtils.MyUpdateData(mSportModleInfo);
            DeviceSportManager.Companion.getInstance().uploadMoreSportData();
            MyLog.i(TAG, "GPS isSuccess = " + isSuccess);
            Intent intent = new Intent(mContext, AmapLocusActivity.class);
            MoreSportActivity.Companion.setSportModleInfo(mSportModleInfo);
//            intent.putExtra(IntentConstants.SportModleInfo, mSportModleInfo);
            startActivity(intent);
            setResult(Activity.RESULT_OK);
        }
        finish();

//        new AlertDialog.Builder(this)
//                .setTitle(getString(R.string.dialog_quit_sport_title))//设置对话框标题
//                .setMessage(message)//设置显示的内容
//                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {//添加确定按钮
//                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
//                    }
//                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
//            @Override
//
//            public void onClick(DialogInterface dialog, int which) {
//            }
//
//        }).show();
    }


    String getPeiSu(long new_time, long old_time, float distance) {

        String result = "";

        float miao = (new_time - old_time) / 1000f;
        float km = distance / 1000;
        int result_fen = (int) (miao / km) / 60;
        int result_miao = (int) (miao / km) % 60;


        result = String.valueOf(result_fen) + "`" + String.valueOf(result_miao) + "``";

        return result;
//        System.out.println("定位测试 配速 miao = " + miao);

    }


    String getPeiSuAll(long miao, float distance) {

        String result = "";

        float km = distance / 1000f;
        int result_fen = (int) (miao / km) / 60;
        int result_miao = (int) (miao / km) % 60;


        result = String.valueOf(result_fen) + "`" + String.valueOf(result_miao) + "``";

        return result;

    }

    void updateSignalUi(int signa) {


        gps_sport_signa.setBackgroundResource(R.drawable.my_gps_signal_0);
        gps_sport_signa1.setBackgroundResource(R.drawable.my_gps_sport_two_yuan);
        gps_sport_signa2.setBackgroundResource(R.drawable.my_gps_sport_two_yuan);
        gps_sport_signa3.setBackgroundResource(R.drawable.my_gps_sport_two_yuan);


        if (signa >= 1 && signa < 3) {
            gps_sport_signa.setBackgroundResource(R.drawable.my_gps_signal_1);
            gps_sport_signa1.setBackgroundResource(R.drawable.my_gps_sport_three_yuan);

        } else if (signa >= 3 && signa < 6) {
            gps_sport_signa.setBackgroundResource(R.drawable.my_gps_signal_2);
            gps_sport_signa1.setBackgroundResource(R.drawable.my_gps_sport_three_yuan);
            gps_sport_signa2.setBackgroundResource(R.drawable.my_gps_sport_three_yuan);

        } else if (signa >= 6) {
            gps_sport_signa.setBackgroundResource(R.drawable.my_gps_signal_3);
            gps_sport_signa1.setBackgroundResource(R.drawable.my_gps_sport_three_yuan);
            gps_sport_signa2.setBackgroundResource(R.drawable.my_gps_sport_three_yuan);
            gps_sport_signa3.setBackgroundResource(R.drawable.my_gps_sport_three_yuan);

        }

    }


    class ButtonListener implements View.OnClickListener, View.OnTouchListener {

        public void onClick(View v) {
        }

        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    MyLog.i(TAG, "定位测试 = 按下去");

                    MySeekBarStart();
                    break;
                case MotionEvent.ACTION_UP:

                    MyLog.i(TAG, "定位测试 = 抬起来");
                    MySeekBarStop();
                    break;
            }
            return false;
        }

    }


    // 长按加载圈
    private boolean MySeekBarIsStop = false;
    private int MySeekBARdown = 0;
    private Handler MySeekBarHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 添加更新ui的代码
                    if (!MySeekBarIsStop) {

                        if (MySeekBARdown >= SeekBarMax) {

                            MySeekBarStop();

                            YDStop();
                            gps_sport_bootom_lin1.setVisibility(View.GONE);
                            gps_sport_long.setVisibility(View.GONE);
                            gps_sport_bootom_lin2.setVisibility(View.VISIBLE);


                        } else {

                            gps_sport_seekbar.setProgress(MySeekBARdown);
                            MySeekBarHandler.sendEmptyMessageDelayed(1, SeekBarTime);
                        }

                        MySeekBARdown++;

                    }
                    break;
                case 0:
                    break;
            }
        }

    };

    private void MySeekBarStart() {
        gps_sport_seekbar.setVisibility(View.VISIBLE);
        MySeekBARdown = 0;
        MySeekBarHandler.removeMessages(1);
        MySeekBarHandler.sendEmptyMessage(1);
        MySeekBarIsStop = false;

    }

    private void MySeekBarStop() {
        gps_sport_seekbar.setVisibility(View.GONE);
        MySeekBarHandler.sendEmptyMessage(0);
        gps_sport_seekbar.setProgress(0);
        MySeekBarIsStop = true;

    }

    private ImageView ivShowMap;

    void ChangeMapUi(boolean isV) {
        int x = ivShowMap.getLeft() + ivShowMap.getWidth() / 2;
        int y = ((View) ivShowMap.getParent()).getBottom() - ivShowMap.getWidth() / 2;
        int AnimartorDuration = 500;
        if (isV) {
            if (Build.VERSION.SDK_INT >= 21) {
                Animator anim = ViewAnimationUtils.createCircularReveal(gps_sport_mapview, x, y, 0, y);
                anim.setDuration(AnimartorDuration);
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        gps_sport_img.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                gps_sport_mapview.setVisibility(View.VISIBLE);
                anim.start();
            } else {
                gps_sport_mapview.setVisibility(View.VISIBLE);
                gps_sport_img.setVisibility(View.VISIBLE);
            }

        } else {

            if (Build.VERSION.SDK_INT >= 21) {
                gps_sport_img.setVisibility(View.GONE);
                Animator anim = ViewAnimationUtils.createCircularReveal(gps_sport_mapview, x, y, y, 0);
                anim.setDuration(AnimartorDuration);
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        gps_sport_mapview.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
                anim.start();
            } else {
                gps_sport_mapview.setVisibility(View.GONE);
                gps_sport_img.setVisibility(View.GONE);
            }

        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


}
