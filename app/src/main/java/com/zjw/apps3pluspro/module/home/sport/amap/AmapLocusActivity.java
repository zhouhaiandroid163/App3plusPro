package com.zjw.apps3pluspro.module.home.sport.amap;

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
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.lidroid.xutils.BitmapUtils;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.module.home.sport.DeviceSportManager;
import com.zjw.apps3pluspro.module.home.sport.MoreSportActivity;
import com.zjw.apps3pluspro.network.okhttp.MyOkHttpClient;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.module.home.sport.AmapGpsUtils;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.dbmanager.SportModleInfoUtils;
import com.zjw.apps3pluspro.sql.entity.SportModleInfo;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.AuthorityManagement;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.FileUtil;
import com.zjw.apps3pluspro.utils.ImageUtil;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.NewTimeUtils;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.CircleImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 高德轨迹
 */
public class AmapLocusActivity extends Activity implements View.OnClickListener {
    private final String TAG = AmapLocusActivity.class.getSimpleName();
    private Context mContext;
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    private SportModleInfoUtils mSportModleInfoUtils = BaseApplication.getSportModleInfoUtils();
    private MapView loucs_amap_map;
    private AMap mAMap;
    private ArrayList<LatLng> latLngList;
    private PolylineOptions mPolyoptions;

    private ConstraintLayout locus_amap_share_view;
    private TextView locus_amap_date, loucs_amap_duration, loucs_amap_speed, loucs_amap_distance, loucs_amap_distance_unit, loucs_amap_calory;


    SportModleInfo mSportModleInfo = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setStatusBarMode(this, false, R.color.base_activity_bg);
        setContentView(R.layout.activity_amap_locus);
        SysUtils.makeRootDirectory(Constants.P_GPS_LOG);
        mContext = AmapLocusActivity.this;
        loucs_amap_map = (MapView) findViewById(R.id.loucs_amap_map);
        loucs_amap_map.onCreate(savedInstanceState);
        initView();
        initpolyline();
        initMap();
        Intent intent = getIntent();
//        mSportModleInfo = intent.getParcelableExtra(IntentConstants.SportModleInfo);
        mSportModleInfo = MoreSportActivity.Companion.getSportModleInfo();
        try {
            if (TextUtils.isEmpty(mSportModleInfo.getMap_data())) {
                if (MyOkHttpClient.getInstance().isConnect(this)) {
                    DeviceSportManager.Companion.getInstance().getMoreSportDetail(mSportModleInfo.getRecordPointSportType(), mSportModleInfo.getServiceId(), new DeviceSportManager.GetDataSuccess() {
                        @Override
                        public void onSuccess() {
                            List<SportModleInfo> SportModleInfos = mSportModleInfoUtils.queryByServerId(mSportModleInfo.getServiceId());
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
                shareAmapLocus();
                break;
        }
    }

    void initView() {
        locus_amap_share_view = findViewById(R.id.locus_amap_share_view);
        locus_amap_date = (TextView) findViewById(R.id.locus_amap_date);
        loucs_amap_duration = (TextView) findViewById(R.id.loucs_amap_duration);
        loucs_amap_speed = (TextView) findViewById(R.id.loucs_amap_speed);
        loucs_amap_calory = (TextView) findViewById(R.id.loucs_amap_calory);
        loucs_amap_distance = (TextView) findViewById(R.id.loucs_amap_distance);
        loucs_amap_distance_unit = (TextView) findViewById(R.id.loucs_amap_distance_unit);

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

    String MypointData = "";

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
            MypointData = mSportModleInfo.getMap_data();

//            String sport_time_used = SportModleUtils.getcueDate(Integer.valueOf(sport_duration) * 1000);
            String sport_time_used = NewTimeUtils.getTimeString(Integer.parseInt(sport_duration));

            locus_amap_date.setText(date);//时间


            double pace = Integer.parseInt(sport_duration) / (Integer.parseInt(sport_distance) / 1000.0);
            String paceString = String.format("%1$02d'%2$02d\"", (int) (pace / 60), (int) (pace % 60));

            loucs_amap_duration.setText(sport_time_used);//时长
//            loucs_amap_speed.setText(sport_speed);//配速
            loucs_amap_speed.setText(paceString);//配速
            loucs_amap_calory.setText(sport_calory);//卡路里

            sport_distance = sport_distance.replace(",", ".");

            if (mBleDeviceTools.get_device_unit() == 1) {
                loucs_amap_distance.setText(AppUtils.GetFormat(2, (Float.valueOf(sport_distance) / 1000)));
                loucs_amap_distance_unit.setText(getText(R.string.sport_distance_unit));
            } else {
                loucs_amap_distance.setText(AppUtils.GetFormat(2, (Float.valueOf(sport_distance) / 1000 / 1.61f)));
                loucs_amap_distance_unit.setText(getText(R.string.unit_mi));
            }

//            GpsUtils.init();
            AmapGpsUtils.init();

            if (!TextUtils.isEmpty(pointData)) {
                String[] pointDataArray = pointData.split(";");
                for (int i = 0; i < pointDataArray.length; i++) {
                    LatLng latlng = new LatLng(Float.parseFloat(pointDataArray[i].split(",")[1]), Float.parseFloat(pointDataArray[i].split(",")[0]));
                    latLngList.add(latlng);

                }
            }

            //展示地图
            if (!latLngList.isEmpty()) {
                mAMap.moveCamera(CameraUpdateFactory.changeLatLng(latLngList.get(0)));
                mAMap.moveCamera(CameraUpdateFactory.zoomTo(16));

                PathSmoothTool mpathSmoothTool = new PathSmoothTool();
                mpathSmoothTool.setIntensity(4);
                List<LatLng> pathoptimizeList = mpathSmoothTool.pathOptimize(latLngList);
                mPolyoptions.addAll(pathoptimizeList);

                if (pathoptimizeList != null && pathoptimizeList.size() > 0) {
                    mAMap.addPolyline(mPolyoptions);
                    mAMap.moveCamera(CameraUpdateFactory.newLatLngBoundsRect(getBounds(pathoptimizeList), ImageUtil.dp2px(mContext, 50),         //left padding
                            ImageUtil.dp2px(mContext, 50),         //right padding
                            ImageUtil.dp2px(mContext, 50),         //top padding
                            ImageUtil.dp2px(mContext, 130)));
                }

                mAMap.addMarker(new MarkerOptions().position(latLngList.get(0)).icon(BitmapDescriptorFactory.fromResource(R.drawable.my_sport_start)));
                mAMap.addMarker(new MarkerOptions().position(latLngList.get(latLngList.size() - 1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.my_sport_end)));
            }
        }

        Handler handler = new Handler();
        findViewById(R.id.btShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AmapLocusActivity.this, "开始生成文件...", Toast.LENGTH_LONG).show();
                File mFile = new File(Constants.P_GPS_LOG + mSportModleInfo.getTime() + " gps.log");
                if (mFile.exists()) {
                    mFile.delete();
                }
                if (!latLngList.isEmpty()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < latLngList.size(); i++) {
                                SysUtils.writeTxtToFile2(latLngList.get(i).latitude + "," + latLngList.get(i).longitude, Constants.P_GPS_LOG, mSportModleInfo.getTime() + " gps.log");
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AmapLocusActivity.this, "生成完成", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).start();

                } else {
                    Toast.makeText(AmapLocusActivity.this, "无效数据", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private LatLngBounds getBounds(List<LatLng> pointlist) {
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (pointlist == null) {
            return b.build();
        }
        for (int i = 0; i < pointlist.size(); i++) {
            b.include(pointlist.get(i));
        }
        return b.build();

    }


    private void initMap() {
        //实例化地图类
        if (mAMap == null) {
            mAMap = loucs_amap_map.getMap();
            mAMap.getUiSettings().setRotateGesturesEnabled(false);
            mAMap.moveCamera(CameraUpdateFactory.zoomBy(6));
            mAMap.getUiSettings().setZoomControlsEnabled(false);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        loucs_amap_map.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        loucs_amap_map.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        loucs_amap_map.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        loucs_amap_map.onDestroy();
    }


    private void initpolyline() {
        mPolyoptions = new PolylineOptions();
        mPolyoptions.width(10f);
//        mPolyoptions.geodesic(true); //是否绘制大地曲线
        mPolyoptions.color(Color.parseColor("#00ff00"));
    }

    void shareAmapLocus() {
        mAMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(Bitmap bitmap) {
            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int status) {
                if (null == bitmap) {
                    return;
                }
                if (AuthorityManagement.verifyStoragePermissions(AmapLocusActivity.this)) {
                    MyLog.i(TAG, "SD卡权限 已获取");
                    Bitmap screenBitmap = MyUtils.getViewBitmap(locus_amap_share_view);
                    Bitmap shareBitmap = ImageUtil.createWaterMaskLeftBottom(mContext, bitmap, screenBitmap, 0, 0);
                    MyUtils.SharePhoto(shareBitmap, AmapLocusActivity.this);
                } else {
                    MyLog.i(TAG, "SD卡权限 未获取");
                    showSettingDialog(getString(R.string.setting_dialog_storage));
                }
            }
        });
    }


    //================权限相关==========================
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode) {
            case AuthorityManagement.REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.i(TAG, "SD卡权限 回调允许");
                } else {
                    MyLog.i(TAG, "SD卡权限 回调拒绝");
                }
            }
            break;
            case AuthorityManagement.REQUEST_EXTERNAL_CALL_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.i(TAG, "拍照权限 回调允许");
                } else {
                    MyLog.i(TAG, "拍照权限 回调拒绝");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }


    /**
     * 弹出设置对话框
     *
     * @param title
     */
    void showSettingDialog(String title) {
        new android.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_prompt))
                .setMessage(title)
                .setPositiveButton(getString(R.string.setting_dialog_setting), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }

                }).setNegativeButton(getString(R.string.setting_dialog_cancel), new DialogInterface.OnClickListener() {


            @Override

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }

        }).show();

    }


}
