package com.zjw.apps3pluspro.module.home.sport.google;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.lidroid.xutils.BitmapUtils;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.module.home.sport.DeviceSportManager;
import com.zjw.apps3pluspro.module.home.sport.MoreSportActivity;
import com.zjw.apps3pluspro.network.okhttp.MyOkHttpClient;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.dbmanager.SportModleInfoUtils;
import com.zjw.apps3pluspro.sql.entity.SportModleInfo;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.AuthorityManagement;
import com.zjw.apps3pluspro.utils.FileUtil;
import com.zjw.apps3pluspro.utils.ImageUtil;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.NewTimeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 谷歌轨迹
 */
public class GoogleLocusActivity extends Activity implements View.OnClickListener, OnMapReadyCallback {
    private final String TAG = GoogleLocusActivity.class.getSimpleName();
    private Context mContext;
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    private SportModleInfoUtils mSportModleInfoUtils = BaseApplication.getSportModleInfoUtils();
    private ArrayList<LatLng> latLngList;

    //Google地图
    GoogleMap mGoogleMap;
    MapFragment mapFragment;
    private Polyline polyline;
    private PolylineOptions polylineOptions;


    private ConstraintLayout locus_share_view;
    private TextView locus_google_date, loucs_google_duration, loucs_google_speed, loucs_google_distance, loucs_google_distance_unit, loucs_google_calory;

    SportModleInfo mSportModleInfo = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setStatusBarMode(this, true, R.color.base_activity_bg);
        setContentView(R.layout.activity_google_locus);
        mContext = GoogleLocusActivity.this;
        polylineOptions = new PolylineOptions();
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initView();
        initpolyline();
        initMap();

        Intent intent = getIntent();
//        mSportModleInfo = intent.getParcelableExtra(IntentConstants.SportModleInfo);
        mSportModleInfo = MoreSportActivity.Companion.getSportModleInfo();
        try {
            if(TextUtils.isEmpty(mSportModleInfo.getMap_data())){
                if(MyOkHttpClient.getInstance().isConnect(this)){
                    DeviceSportManager.Companion.getInstance().getMoreSportDetail(mSportModleInfo.getRecordPointSportType(), mSportModleInfo.getServiceId(), new DeviceSportManager.GetDataSuccess() {
                        @Override
                        public void onSuccess() {
                            List<SportModleInfo> SportModleInfos  = mSportModleInfoUtils.queryByServerId(mSportModleInfo.getServiceId());
                            mSportModleInfo = SportModleInfos.get(0);
                            initData();
                        }
                        @Override
                        public void onError() {
                        }
                    });
                } else {
                    AppUtils.showToast(this, R.string.no_net_work);
                }
            } else {
                initData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((TextView) findViewById(R.id.public_head_title)).setText(R.string.sport_modle_type100);
        findViewById(R.id.layoutRight).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutRight).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutRight:
                shareGoogleMapLocus();
                break;
        }
    }

    void initView() {
        locus_share_view = findViewById(R.id.locus_share_view);

        locus_google_date = (TextView) findViewById(R.id.locus_google_date);
        loucs_google_duration = (TextView) findViewById(R.id.loucs_google_duration);
        loucs_google_speed = (TextView) findViewById(R.id.loucs_google_speed);
        loucs_google_distance = (TextView) findViewById(R.id.loucs_google_distance);
        loucs_google_distance_unit = (TextView) findViewById(R.id.loucs_google_distance_unit);
        loucs_google_calory = (TextView) findViewById(R.id.loucs_google_calory);

        TextView tvUserName = findViewById(R.id.tvUserName);
        CircleImageView ivMyHead = findViewById(R.id.ivMyHead);
        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
        tvUserName.setText(mUserSetTools.get_user_nickname());
        if (!JavaUtil.checkIsNull(mUserSetTools.get_uesr_head_bast64())) {
            MyLog.i(TAG, "显示头像 Bast64 ");
            Bitmap bitmap = FileUtil.base64ToBitmap(mUserSetTools.get_uesr_head_bast64());
            ivMyHead.setImageBitmap(bitmap);
        } else if (!JavaUtil.checkIsNull(mUserSetTools.get_user_head_url())) {
            MyLog.i(TAG, "显示头像 url ");
            String head_url = mUserSetTools.get_user_head_url();
            BitmapUtils bitmapUtils = new BitmapUtils(this);
            bitmapUtils.display(ivMyHead, head_url);
        } else {
            MyLog.i(TAG, "显示头像 url ");
            ivMyHead.setImageResource(R.drawable.default_header);
        }
    }

    void initData() {
        latLngList = new ArrayList<LatLng>();
        String sport_distance = "";
        String sport_calory = "";
        String sport_speed = "";
        String sport_duration = "";
        String date = "";
        String pointData = "";


        if (mSportModleInfo != null) {

            MyLog.i(TAG, "mSportModleInfo = " + mSportModleInfo.toString());
            sport_distance = mSportModleInfo.getDisance();
            sport_calory = mSportModleInfo.getCalorie();
            sport_speed = mSportModleInfo.getSpeed();
            sport_duration = mSportModleInfo.getSport_duration();
            date = mSportModleInfo.getTime();
            pointData = mSportModleInfo.getMap_data();
//            String sport_time_used = SportModleUtils.getcueDate(Integer.valueOf(sport_duration) * 1000);
            String sport_time_used = NewTimeUtils.getTimeString(Integer.valueOf(sport_duration));

            double pace = Integer.parseInt(sport_duration) / (Integer.parseInt(sport_distance) / 1000.0);
            String paceString = String.format("%1$02d'%2$02d\"", (int) (pace / 60), (int) (pace % 60));

            locus_google_date.setText(date);
            loucs_google_duration.setText(sport_time_used);
//            loucs_google_speed.setText(String.valueOf(sport_speed));
            loucs_google_speed.setText(paceString);
            loucs_google_calory.setText(String.valueOf(sport_calory));

            sport_distance = sport_distance.replace(",", ".");

            if (mBleDeviceTools.get_device_unit() == 1) {
                loucs_google_distance.setText(AppUtils.GetFormat(2, (Float.valueOf(sport_distance) / 1000)));
                loucs_google_distance_unit.setText(getText(R.string.sport_distance_unit));
            } else {
                loucs_google_distance.setText(AppUtils.GetFormat(2, (Float.valueOf(sport_distance) / 1000 / 1.61f)));
                loucs_google_distance_unit.setText(getText(R.string.unit_mi));
            }


            if (!TextUtils.isEmpty(pointData)) {
                String[] pointDataArray = pointData.split(";");
                for (int i = 0; i < pointDataArray.length; i++) {

                    LatLng latlng = new LatLng(Float.parseFloat(pointDataArray[i].split(",")[1]),
                            Float.parseFloat(pointDataArray[i].split(",")[0]));
                    latLngList.add(latlng);
                }
            }

            //展示地图
            if (!latLngList.isEmpty()) {
                handler.sendEmptyMessageDelayed(0, 1000);

            }
        }


    }

    private Handler handler = new Handler() {


        public void handleMessage(Message message) {

            try {
                for (LatLng latLng : latLngList) {
                    MyLog.i(TAG, "运动轨迹 = X = " + latLng.latitude + "  Y = " + latLng.longitude);
                    polylineOptions.add(latLng);

                }
                LatLng southwest = latLngList.get(0);
                LatLng northeast = latLngList.get(latLngList.size() - 1);
                MarkerOptions markerOptions1 = new MarkerOptions();
                markerOptions1.position(southwest);
                markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_sport_start));
                mGoogleMap.addMarker(markerOptions1);
                MarkerOptions markerOptions2 = new MarkerOptions();
                markerOptions2.position(northeast);
                markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_sport_end));
                mGoogleMap.addMarker(markerOptions2);
                if (mGoogleMap != null) {
                    polyline = mGoogleMap.addPolyline(polylineOptions);
    //                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(0), 16.0f));

                    LatLng myLatLng = new LatLng(((southwest.latitude + northeast.latitude) / 2), ((southwest.longitude + northeast.longitude) / 2));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 16.0f));

    //                MyLog.i(TAG,"运动轨迹 = southwest X = " + southwest.latitude + "  Y = " + southwest.longitude);
    //                MyLog.i(TAG,"运动轨迹 = northeast X = " + northeast.latitude + "  Y = " + northeast.longitude);
    //                MyLog.i(TAG,"运动轨迹 = myLatLng X = " + myLatLng.latitude + "  Y = " + myLatLng.longitude);

                    MyLog.i(TAG, "运动轨迹 不等于空");

                } else {
                    MyLog.i(TAG, "运动轨迹 等于空了");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private void initMap() {
        //实例化地图类
//        if (mAMap == null) {
//            mAMap = loucs_map.getMap();
//            mAMap.getUiSettings().setRotateGesturesEnabled(false);
//            mAMap.moveCamera(CameraUpdateFactory.zoomBy(6));
//            mAMap.getUiSettings().setZoomControlsEnabled(false);
//        }


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
    protected void onPause() {
        super.onPause();
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    private void initpolyline() {

        polylineOptions.width(10f).color(Color.parseColor("#7C7DF4"));
    }

    //dip--->px, 1dp = 2px,定义一个控件的宽高  layoutParams(w,h)
    public int dip2px(int dip) {
        //获取dp和px的转换关系的变量
        float density = getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5);
    }

    void shareGoogleMapLocus() {
        mGoogleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {


                if (AuthorityManagement.verifyStoragePermissions(GoogleLocusActivity.this)) {
                    MyLog.i(TAG, "SD卡权限 已获取");
                    Bitmap screenBitmap = MyUtils.getViewBitmap(locus_share_view);
                    Bitmap shareBitmap = ImageUtil.createWaterMaskLeftBottom(mContext, bitmap, screenBitmap, 0, 0);
                    MyUtils.SharePhoto(shareBitmap, GoogleLocusActivity.this);
                } else {
                    MyLog.i(TAG, "SD卡权限 未获取");
                    showSettingDialog(getString(R.string.setting_dialog_storage));

                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MyLog.i(TAG, "运动轨迹 onMapReady()");
        mGoogleMap = googleMap;


    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode) {
            case AuthorityManagement.REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.i(TAG, "SD卡权限 回调允许");
                } else {
                    MyLog.i(TAG, "SD卡权限 回调拒绝");
//                    showSettingDialog(getString(R.string.setting_dialog_storage));
                }
            }
            break;
            case AuthorityManagement.REQUEST_EXTERNAL_CALL_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.i(TAG, "拍照权限 回调允许");
//                    TakingPictures();
                } else {
                    MyLog.i(TAG, "拍照权限 回调拒绝");
//                    showSettingDialog(getString(R.string.setting_dialog_call_camera));
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }


    void showSettingDialog(String title) {
        new android.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_prompt))//设置对话框标题
                .setMessage(title)//设置显示的内容
                .setPositiveButton(getString(R.string.setting_dialog_setting), new DialogInterface.OnClickListener() {//添加确定按钮

                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);

                    }

                }).setNegativeButton(getString(R.string.setting_dialog_cancel), new DialogInterface.OnClickListener() {//添加返回按钮


            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件

                // TODO Auto-generated method stub


            }

        }).show();//在按键响应事件中显示此对话框

    }
}
