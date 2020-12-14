package com.zjw.apps3pluspro.module.home.sport.google;


import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
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
import com.zjw.apps3pluspro.utils.FontsUtils;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;


public class GoogleGpsSportActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnCameraMoveListener,
        OnMapReadyCallback, View.OnClickListener {
    private final String TAG = GoogleGpsSportActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    //数据库存储
    private SportModleInfoUtils mSportModleInfoUtils = BaseApplication.getSportModleInfoUtils();

    private TextView tvResult;
    private TextView yundong_time, map_duration;
    private TextView yundong_distance, map_distance;
    private TextView yundong_unit, tvDistanceUnit;
    private TextView yundong_velocity, map_speed;


    //是否是第一次定位
    public boolean isFirst = true;

    private Button yundong_pause;
    private Button yundong_stop;
    private RelativeLayout gps_sport_mapview;

    private LinearLayout gps_sport_bootom_lin1, gps_sport_bootom_lin2;

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


    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    LatLng oldLatLng;
    GoogleMap mGoogleMap;
    Marker mCurrLocation;

    MapFragment mapFragment;
    private PolylineOptions polylineOptions;

    private String start_sport_time = "";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setStatusBarMode(this, false, R.color.title_bg_gps);
        setContentView(R.layout.activity_google_gps_sport);
        mContext = GoogleGpsSportActivity.this;
        initView();
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        polylineOptions = new PolylineOptions();
        initpolyline();
        DJSStart();

    }

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {

        mapFragment.getMapAsync(this);
    }


    void initView() {
        tvResult = (TextView) findViewById(R.id.tvResult);
        yundong_time = (TextView) findViewById(R.id.yundong_time);
        map_duration = (TextView) findViewById(R.id.map_duration);
        yundong_distance = (TextView) findViewById(R.id.yundong_distance);
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
                if (mLastLocation != null) {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(oldLatLng, 15.0f));
                }
                break;

            case R.id.yundong_continue:

                YDStart();

                gps_sport_bootom_lin1.setVisibility(View.VISIBLE);
                gps_sport_bootom_lin2.setVisibility(View.GONE);

                break;


        }
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {


        super.onResume();

        if (mapFragment != null) {
            try {
                mapFragment.onResume();
            } catch (Exception e) {
            }
        }


    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        DJSHandler.removeCallbacksAndMessages(null);
        YDHaSndler.removeCallbacksAndMessages(null);
        MySeekBarHandler.removeCallbacksAndMessages(null);

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        if (mapFragment != null) {
            try {
                mapFragment.onDestroy();
            } catch (Exception e) {
            }
        }

    }


    private void initpolyline() {
        polylineOptions.width(10f).color(Color.parseColor("#7C7DF4"));
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

        YDHaSndler.sendEmptyMessage(0);
        YDIsStop = true;

    }

    void setDistance(LatLng oldLatLng, LatLng newLatLng, long old_time, long new_time) {

//        DecimalFormat df;
//        df = new DecimalFormat("######0.00");


//        float now_distance = AMapUtils.calculateLineDistance(oldLatLng, newLatLng);
        float now_distance = (float) (getDistance(oldLatLng, newLatLng));

        sport_distance += now_distance;
//        yundong_distance.setText(String.valueOf(df.format(sport_distance / 1000)));
        if (mBleDeviceTools.get_device_unit() == 1) {
            yundong_distance.setText(AppUtils.GetFormat(2, Float.valueOf((sport_distance / 1000))));
            map_distance.setText(AppUtils.GetFormat(2, Float.valueOf((sport_distance / 1000))));
        } else {
            yundong_distance.setText(AppUtils.GetFormat(2, Float.valueOf((sport_distance / 1000 / 1.61f))));
            map_distance.setText(AppUtils.GetFormat(2, Float.valueOf((sport_distance / 1000 / 1.61f))));
        }

        if (sport_distance != 0f) {

            yundong_velocity.setText(getPeiSuAll(YDdown, sport_distance));
            map_speed.setText(getPeiSuAll(YDdown, sport_distance));


        }
    }


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
        if(sport_calory == 0){
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
            MyLog.i(TAG, "插入多运动-GPS isSuccess = " + isSuccess);
            DeviceSportManager.Companion.getInstance().uploadMoreSportData();

            Intent intent = new Intent(mContext, GoogleLocusActivity.class);
            MoreSportActivity.Companion.setSportModleInfo(mSportModleInfo);
//            intent.putExtra(IntentConstants.SportModleInfo, mSportModleInfo);
            startActivity(intent);
            setResult(Activity.RESULT_OK);
        }
        finish();
//        new android.app.AlertDialog.Builder(this)
//                .setTitle(getString(R.string.dialog_quit_sport_title))//设置对话框标题
//                .setMessage(message)//设置显示的内容
//                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {//添加确定按钮
//
//                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
//                    }
//
//                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {//添加返回按钮
//
//
//            @Override
//
//            public void onClick(DialogInterface dialog, int which) {//响应事件
//            }
//
//        }).show();//在按键响应事件中显示此对话框
    }

    String getPeiSu(long new_time, long old_time, float distance) {

        String result = "";

        float miao = (new_time - old_time) / 1000f;
        float km = distance / 1000;
        int result_fen = (int) (miao / km) / 60;
        int result_miao = (int) (miao / km) % 60;


        result = String.valueOf(result_fen) + "`" + String.valueOf(result_miao) + "``";

        return result;
//      MyLog.i(TAG,"定位测试 配速 miao = " + miao);

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


    @Override
    public void onMapReady(GoogleMap googleMap) {


        mGoogleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        if (mGoogleMap != null) {
            mGoogleMap.setOnCameraMoveListener(this);
            mGoogleMap.setMyLocationEnabled(true);
            buildGoogleApiClient();
            mGoogleApiClient.connect();

        }

    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        //Unregister for location callbacks:

    }


    protected synchronized void buildGoogleApiClient() {
//        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    Location mLastLocation;

    public void onConnected(Bundle bundle) {

        MyLog.i(TAG, "GPS运动 = onConnected ");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        MyLog.i(TAG, "GPS运动 = 位置111 = " + LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient) + "");


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        MyLog.i(TAG, "GPS运动 = 位置222 = " + mLastLocation + "1111111");
        if (mLastLocation != null) {
            //place marker at current position
            mGoogleMap.clear();
            oldLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(latLng);
//            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_img_map_stud));
//            mCurrLocation = mGoogleMap.addMarker(markerOptions);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15));

            com.google.android.gms.maps.UiSettings mUiSettings;
            mUiSettings = mGoogleMap.getUiSettings();

        }

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5 * 1000); //5 seconds //间隔
        mLocationRequest.setFastestInterval(3 * 1000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);//定位模式
        mLocationRequest.setSmallestDisplacement(5F); //定位精度

//        mLocationRequest.setExpirationTime(30*1000);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

        MyLog.i(TAG, "GPS运动 = onConnectionSuspended ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        MyLog.i(TAG, "GPS运动 = Override ");

    }

    /**
     * 当位置发生改变
     **/
    @Override
    public void onLocationChanged(Location location) {

        try {
            //remove previous current location marker and add new one at current position
            if (mCurrLocation != null) {
                mCurrLocation.remove();
            }


            tvResult.setText("X = " + location.getLatitude() + "\n"
                    + "Y = " + location.getLongitude() + "\n"
                    + "类型 = " + location.getProvider() + "\n"
                    + "精度 = " + location.getAccuracy() + "\n"
                    + "速度 = " + location.getSpeed() + "\n"

            );

            MyLog.i(TAG, "GPS运动 = 位置............ = " + location + "");

            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());


            if (oldLatLng != newLatLng) {

                long new_long_time = System.currentTimeMillis();

                latLngList.add(newLatLng);

                if(oldLatLng != null){
                    setDistance(oldLatLng, newLatLng, old_long_time, new_long_time);
                    setpolyline(oldLatLng, newLatLng);
                }
                old_long_time = new_long_time;
            }
            oldLatLng = newLatLng;


//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_img_map_stud));
//        mCurrLocation = mGoogleMap.addMarker(markerOptions);

            //摄像头移动到坐标上
//        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));


//        Toast.makeText(this, "Location Changed", Toast.LENGTH_SHORT).show();

            //If you only need one location, unregister the listener
            //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setpolyline(LatLng oldData, LatLng newData) {
        if (!polylineOptions.getPoints().isEmpty()) {
            polylineOptions.getPoints().clear();
        }

        mGoogleMap.addPolyline(polylineOptions.add(oldData, newData));


    }


    /**
     * google地图移动回调
     */
    public void onCameraMove() {
        CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
//        longitude = cameraPosition.target.longitude;
//        latitude = cameraPosition.target.latitude;
//        zoom = cameraPosition.zoom;
//      MyLog.i(TAG,"GPS运动 移动了222");
    }

    /**
     * 计算两点之间距离
     *
     * @param start
     * @param end
     * @return 米
     */
    public double getDistance(LatLng start, LatLng end) {
        double lat1 = (Math.PI / 180) * start.latitude;
        double lat2 = (Math.PI / 180) * end.latitude;

        double lon1 = (Math.PI / 180) * start.longitude;
        double lon2 = (Math.PI / 180) * end.longitude;

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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


}
