package com.zjw.apps3pluspro.module.device;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.bleservice.BtSerializeation;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.AuthorityManagement;
import com.zjw.apps3pluspro.utils.BmpUtils;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.FileUtil;
import com.zjw.apps3pluspro.utils.FontsUtils;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;


/**
 * 屏保页面
 */

public class SendBleActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = SendBleActivity.class.getSimpleName();
    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    private Context mContext;


    private SwitchCompat sb_screensaver;
    private TextView send_text1, send_text2, send_text3;


    //配置
    private int OutputX = 240;//宽度
    private int OutputY = 240;//高度

    private final int AspectX = 1;//比例
    private final int AspectY = 1;//比例
    private final int DelayTime1 = 150;//发送数据头和数据直接的延迟时间
    private final int DelayTime2 = 3000;//发送数据头和数据直接的延迟时间
    private final boolean IsAllSleep = true;//是否睡眠
    //    private final int AllSleepTime = 20;//睡眠时间
    private int AllSleepTime = 20;//睡眠时间

    private final boolean IsReplacement = true;//是否补发
    private final boolean IsReplaSleep = true;//补发是否睡眠
    //    private final int ReplaSleepTime = 20;//补发时间
    private int ReplaSleepTime = 20;//补发时间

    private final int ReplaMaxCount = 200;//单次补发最大次数

    final int send_level[] = {R.string.dialog_send_lev0, R.string.dialog_send_lev1, R.string.dialog_send_lev2, R.string.dialog_send_lev3, R.string.dialog_send_lev4};
    final int sleep_time[] = {40, 30, 25, 20, 10};


    private Dialog dialog;
    private File tempFile;
    private Uri imageUri;


    UpdateTextTask updateTextTask;
    //    ProgressDialog progressDialog;
    private WaitDialog waitDialog;
    byte[] bitMapData;


    private Handler myHandler;

    //锁屏
    PowerManager.WakeLock wakeLock = null;


    //UI控件
    private ImageView custom_img_square, custom_img_circular;
    private ImageView custom_bg0, custom_bg1, custom_bg2;
    private RelativeLayout custom_rela;

    private TextView text_time, my_text, send_state;
    private Button button_send_data;


    int ColorARGB = 0;

    int transmissionProgress = 0;


//    MoveLayout ml_main;
//    TextView iv_active;
//    TextView tv;

    private boolean isChange = false;

    private Bitmap Nowbitmap;


    int item = 0;

    private Bitmap defuleBitmap;

    private RelativeLayout r_custom_pos;
    private View v_custom_pos;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_send_ble;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = SendBleActivity.this;
        myHandler = new Handler();
        waitDialog = new WaitDialog(mContext);
        imageUri = Uri.parse(Constants.IMAGE_FILE_LOCATION);
        initBroadcast();
        initView();
        initData();
        // 创建
        SysUtils.makeRootDirectory(Constants.HEAD_IMG);
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onResume() {
        super.onResume();
        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ON_AFTER_RELEASE, TAG);
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wakeLock != null) {
            wakeLock.release();
        }
    }

    private boolean isOnDestroy = false;
    @Override
    public void onDestroy() {
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
        }


        if (broadcastReceiver != null) {
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {
            }
        }

        isOnDestroy = true;
        super.onDestroy();
    }

    /**
     * 初始化广播
     */
    private void initBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastTools.ACTION_GATT_CONNECTED);
        filter.addAction(BroadcastTools.ACTION_GATT_DISCONNECTED);
        filter.addAction(BroadcastTools.ACTION_GATT_SET_SCREENSAVER_SUCCESS);
        filter.addAction(BroadcastTools.ACTION_GATT_SET_SCREENSAVER_FAIL);
        filter.setPriority(1000);
        registerReceiver(broadcastReceiver, filter);
    }

    /**
     * 广播监听
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @SuppressWarnings({"unused", "unused"})
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                //设备已连接
                case BroadcastTools.ACTION_GATT_CONNECTED:
                    MyLog.i(TAG, "设备已连接");
                    break;

                //设备已断开
                case BroadcastTools.ACTION_GATT_DISCONNECTED:
                    MyLog.i(TAG, "设备已断开");
                    handSendImageFail();
                    break;

                //设置屏保成功
                case BroadcastTools.ACTION_GATT_SET_SCREENSAVER_SUCCESS:
                    MyLog.i(TAG, "设置屏保成功");
                    AppUtils.showToast(mContext, R.string.send_success);
                    if (Nowbitmap != null) {
                        String baset64 = FileUtil.bitmapToBase64(Nowbitmap);
                        mBleDeviceTools.set_screensaver_bast64(baset64);
                        isChange = false;
                        myHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                saveColor();
                            }
                        }, DelayTime1);
                    }
                    break;

                //设置屏保失败
                case BroadcastTools.ACTION_GATT_SET_SCREENSAVER_FAIL:
                    MyLog.i(TAG, "设置屏保失败");
                    handSendImageFail();
                    break;
            }
        }
    };


    void initView() {

        findViewById(R.id.public_no_bg_head_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.public_no_bg_head_title)).setText(getString(R.string.screensaver_title));


        send_text1 = (TextView) findViewById(R.id.send_text1);
        send_text2 = (TextView) findViewById(R.id.send_text2);
        send_text3 = (TextView) findViewById(R.id.send_text3);
        text_time = (TextView) findViewById(R.id.text_time);
        text_time.setTypeface(FontsUtils.modefyEscape(mContext));
        text_time.setText(MyTime.getMeasure());
        my_text = (TextView) findViewById(R.id.my_text);
        send_state = (TextView) findViewById(R.id.send_state);
        custom_img_square = (ImageView) findViewById(R.id.custom_img_square);
        custom_img_circular = (ImageView) findViewById(R.id.custom_img_circular);
        custom_bg0 = (ImageView) findViewById(R.id.custom_bg0);
        custom_bg1 = (ImageView) findViewById(R.id.custom_bg1);
        custom_bg2 = (ImageView) findViewById(R.id.custom_bg2);
        custom_rela = (RelativeLayout) findViewById(R.id.custom_rela);
        button_send_data = (Button) findViewById(R.id.button_send_data);
        button_send_data.setOnClickListener(this);

        sb_screensaver = (SwitchCompat) findViewById(R.id.sb_screensaver);
        sb_screensaver.setChecked(mBleDeviceTools.get_device_screensaver());
        updateUi(mBleDeviceTools.get_device_screensaver());

        sb_screensaver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                mBleDeviceTools.set_device_screensaverb(isChecked);
                updateUi(mBleDeviceTools.get_device_screensaver());
                if (HomeActivity.ISBlueToothConnect()) {
                    savePos();
                    setDeviceScreensaverInfo();
                } else {
                    AppUtils.showToast(mContext, R.string.no_connection_notification);
                }
            }
        });

        r_custom_pos = (RelativeLayout) findViewById(R.id.r_custom_pos);
        v_custom_pos = (View) findViewById(R.id.v_custom_pos);
        r_custom_pos.setOnClickListener(this);

        findViewById(R.id.r_custom_picture).setOnClickListener(this);
        findViewById(R.id.r_custom_color).setOnClickListener(this);
        findViewById(R.id.r_custom_pos).setOnClickListener(this);
        findViewById(R.id.find_device).setOnClickListener(this);
        findViewById(R.id.set_device_info).setOnClickListener(this);
        findViewById(R.id.get_device_info).setOnClickListener(this);
        findViewById(R.id.device_type0).setOnClickListener(this);
        findViewById(R.id.device_type1).setOnClickListener(this);
        findViewById(R.id.device_type2).setOnClickListener(this);


        findViewById(R.id.time_0).setOnClickListener(this);
        findViewById(R.id.time_1).setOnClickListener(this);

        findViewById(R.id.date_0).setOnClickListener(this);
        findViewById(R.id.date_1).setOnClickListener(this);

        findViewById(R.id.user_img_0).setOnClickListener(this);
        findViewById(R.id.user_img_1).setOnClickListener(this);

        findViewById(R.id.is_img_0).setOnClickListener(this);
        findViewById(R.id.is_img_1).setOnClickListener(this);


//        ml_main = (MoveLayout) findViewById(R.id.ml_main);
//        iv_active = (TextView) findViewById(R.id.iv_active);
//        tv = (TextView) findViewById(R.id.textView1);
//
//        //主动移动view
//        ml_main.setActiveMoveItem(iv_active, 0, 0, false);
//
//        MyLog.i(TAG, "iv_active x111 = " +  mBleDeviceTools.get_screensaver_x_coordinate());
//        MyLog.i(TAG, "iv_active y222 = " + mBleDeviceTools.get_screensaver_y_coordinate());
//        MyUtils.setMarginsTextView(iv_active, mBleDeviceTools.get_screensaver_x_coordinate(), mBleDeviceTools.get_screensaver_y_coordinate());
//        //移动区域
//        ml_main.setActiveMoveArea(true, 0, 400, 0, 400);
//        //移动中监听
//        ml_main.setOnMovingListener(new MoveLayout.OnMovingListener() {
//
//            @Override
//            public void onMoving(double movingPrecent) {
//                // TODO 自动生成的方法存根
//                tv.setText((int) (movingPrecent * 100) + "%");
//
//                MyLog.i(TAG, "movingPrecent = " + (int) (movingPrecent * 100) + "%");
//
////                MyLog.i(TAG, "ml_main h = " + ml_main.getHeight());
////                MyLog.i(TAG, "ml_main h = " + ml_main.getWidth());
////                MyLog.i(TAG, "iv_active h = " + iv_active.getHeight());
////                MyLog.i(TAG, "iv_active h = " + iv_active.getWidth());
//                MyLog.i(TAG, "iv_active x = " + iv_active.getX());
//                MyLog.i(TAG, "iv_active y = " + iv_active.getY());
//
//                mBleDeviceTools.set_screensaver_x_coordinate((int) iv_active.getX());
//                mBleDeviceTools.set_screensaver_y_coordinate((int) iv_active.getY());
//
//                //移动区域
//                ml_main.setActiveMoveArea(true, 0, ml_main.getWidth() - iv_active.getWidth(), 0, ml_main.getHeight() - iv_active.getHeight());
//
//            }
//
//        });

    }


    int screensaverWidth;
    int screensaverHeight;
    int timeWidth;
    int timeHeight;

    void initData() {

        item = mBleDeviceTools.get_screen_shape();


        if (mBleDeviceTools.get_screensaver_resolution_width() != 0) {
            OutputX = mBleDeviceTools.get_screensaver_resolution_width();//宽度
        }
        if (mBleDeviceTools.get_screensaver_resolution_height() != 0) {
            OutputY = mBleDeviceTools.get_screensaver_resolution_height();//高度
        }


        String str =
                "屏幕形状 = " + mBleDeviceTools.get_screen_shape()
                        + "\n" + "屏幕宽度 = " + mBleDeviceTools.get_screensaver_resolution_width()
                        + "  屏幕高度 = " + mBleDeviceTools.get_screensaver_resolution_height()
                        + "\n" + "时间宽度 = " + mBleDeviceTools.get_screensaver_time_width()
                        + "  时间高度 = " + mBleDeviceTools.get_screensaver_time_height()
                        + "\n" + "是否支持 = " + mBleDeviceTools.get_is_screen_saver() + "  Flash是否有图片 = " + mBleDeviceTools.get_screensaver_falsh();


        screensaverWidth = mBleDeviceTools.get_screensaver_resolution_width();
        screensaverHeight = mBleDeviceTools.get_screensaver_resolution_height();
        timeWidth = mBleDeviceTools.get_screensaver_time_width();
        timeHeight = mBleDeviceTools.get_screensaver_time_height();

        System.out.println("screensaverWidth = " + screensaverWidth);
        System.out.println("screensaverHeight = " + screensaverHeight);
        System.out.println("timeWidth = " + timeWidth);
        System.out.println("timeHeight = " + timeHeight);


        my_text.setText(str);

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                savePos();
            }
        }, 100);


        ColorARGB = mBleDeviceTools.get_screensaver_color();
        ColorARGB = MyUtils.getNoTransparentColor(ColorARGB);

        System.out.println("get_screensaver_color = " + mBleDeviceTools.get_screensaver_color());


        Resources res = mContext.getResources();
        defuleBitmap = BitmapFactory.decodeResource(res, R.drawable.default_user_image);

        if (!JavaUtil.checkIsNull(mBleDeviceTools.get_screensaver_bast64())) {
            MyLog.i(TAG, "显示头像 Bast64 ");
            Bitmap bitmap = FileUtil.base64ToBitmap(mBleDeviceTools.get_screensaver_bast64());
            custom_img_square.setImageBitmap(bitmap);
            custom_img_circular.setImageBitmap(bitmap);
            bitMapData = BmpUtils.getbitmapByte(bitmap);
        } else {
//            custom_img_square.setBackgroundResource(R.drawable.default_user_image);
//            custom_img_circular.setBackgroundResource(R.drawable.default_user_image);
            custom_img_square.setImageBitmap(defuleBitmap);
            custom_img_circular.setImageBitmap(defuleBitmap);
        }


        updateScreenShape();
        updateColor();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.public_no_bg_head_back:
                finish();
                break;

            case R.id.r_custom_picture:

                if (mBleDeviceTools.get_device_screensaver()) {
//                    showHeadDialog();
                    if (AuthorityManagement.verifyStoragePermissions(SendBleActivity.this)) {
                        MyLog.i(TAG, "SD卡权限 已获取");
                        PhotoAlbum();
                    } else {
                        MyLog.i(TAG, "SD卡权限 未获取");
                        showSettingDialog(getString(R.string.setting_dialog_storage));

                    }
                }


                break;
            case R.id.r_custom_color:

                if (mBleDeviceTools.get_device_screensaver()) {
                    showColorDialog();
                }

                break;

            case R.id.r_custom_pos:

                if (mBleDeviceTools.get_device_screensaver()) {
                    if (item == 1) {
                        showPosDialogType1();
                    } else {
                        showPosDialogType0();
                    }
                }

                break;


            case R.id.find_device:


                if (HomeActivity.ISBlueToothConnect()) {
                    writeRXCharacteristic(BtSerializeation.findDeviceInstra());
                } else {

                    AppUtils.showToast(mContext, R.string.no_connection_notification);
                }


//               MyLog.i(TAG, "ml_main h = " + ml_main.getHeight());
//               MyLog.i(TAG, "ml_main h = " + ml_main.getWidth());
//
//               MyLog.i(TAG, "iv_active h = " + iv_active.getHeight());
//               MyLog.i(TAG, "iv_active h = " + iv_active.getWidth());
//
//               MyLog.i(TAG, "iv_active x = " + iv_active.getX());
//               MyLog.i(TAG, "iv_active y = " + iv_active.getY());


                break;
            case R.id.get_device_info:


                if (HomeActivity.ISBlueToothConnect()) {
                    getDeviceScreensaverInfo();
                } else {
                    AppUtils.showToast(mContext, R.string.no_connection_notification);
                }


                break;
            case R.id.set_device_info:


                if (HomeActivity.ISBlueToothConnect()) {
                    setDeviceScreensaverInfo();
                } else {
                    AppUtils.showToast(mContext, R.string.no_connection_notification);
                }


                break;


            case R.id.button_send_data:

                if (mBleDeviceTools.get_device_screensaver()) {
                    if (HomeActivity.ISBlueToothConnect()) {
                        if (isChange) {
                            if (mBleDeviceTools.get_ble_device_power() >= 50) {
                                SendImageDialog();
                            } else {
                                AppUtils.showToast(mContext, R.string.send_imge_error_low_power);
                            }

                        } else {

                            if (checkContinuousClick()) {
                                saveColor();
                                AppUtils.showToast(mContext, R.string.send_success);
                            }

                        }
                    } else {
                        AppUtils.showToast(mContext, R.string.no_connection_notification);
                    }
                }


                break;

            case R.id.device_type0:

                item = 0;

                updateScreenShape();

                break;
            case R.id.device_type1:

                item = 1;

                updateScreenShape();

                break;

            case R.id.device_type2:

                item = 2;

                updateScreenShape();

                break;

            case R.id.time_0:
                mBleDeviceTools.set_screensaver_is_show_time(0);

                break;
            case R.id.time_1:
                mBleDeviceTools.set_screensaver_is_show_time(1);
//                custom_img_square.setBackgroundResource(R.drawable.default_user_image);
//                custom_img_circular.setBackgroundResource(R.drawable.default_user_image);
                custom_img_square.setImageBitmap(defuleBitmap);
                custom_img_circular.setImageBitmap(defuleBitmap);
                break;

            case R.id.date_0:
                mBleDeviceTools.set_screensaver_is_show_date(0);
                break;
            case R.id.date_1:
                mBleDeviceTools.set_screensaver_is_show_date(1);
                break;
            case R.id.user_img_0:
                mBleDeviceTools.set_screensaver_is_user_imager(0);
                break;
            case R.id.user_img_1:
                mBleDeviceTools.set_screensaver_is_user_imager(1);
                break;
            case R.id.is_img_0:
                mBleDeviceTools.set_device_screensaverb(false);
                break;
            case R.id.is_img_1:
                mBleDeviceTools.set_device_screensaverb(true);
                break;


        }

    }


    /**
     * 换头像的Dialog
     */
    private void showHeadDialog() {

        // TODO Auto-generated method stub

        View view = getLayoutInflater().inflate(
                R.layout.photo_choose_dialog, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;


        view.findViewById(R.id.photograph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (AuthorityManagement.verifyStoragePermissions(SendBleActivity.this)) {
                    MyLog.i(TAG, "SD卡权限 已获取");
                } else {
                    MyLog.i(TAG, "SD卡权限 未获取");
//                    showSettingDialog(getString(R.string.setting_dialog_storage));
                }

                if (AuthorityManagement.verifyPhotogrAuthority(SendBleActivity.this)) {
                    MyLog.i(TAG, "拍照权限 已获取");
                    TakingPictures();
                } else {
                    MyLog.i(TAG, "拍照权限 未获取");
//                    showSettingDialog(getString(R.string.setting_dialog_storage));
                }

            }
        });
        view.findViewById(R.id.albums).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AuthorityManagement.verifyStoragePermissions(SendBleActivity.this)) {
                    MyLog.i(TAG, "SD卡权限 已获取");
                    PhotoAlbum();
                } else {
                    MyLog.i(TAG, "SD卡权限 未获取");
                    showSettingDialog(getString(R.string.setting_dialog_storage));

                }


            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });


        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * 拍照
     */
    void TakingPictures() {

        MyLog.i(TAG, "拍照");
        dialog.dismiss();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Constants.HEAD_IMG, "head.png")));
        startActivityForResult(intent, Constants.TakingTag);// 采用ForResult打开
    }

    /**
     * 相册
     */
    void PhotoAlbum() {
        MyLog.i(TAG, "相册");
//        dialog.dismiss();
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, Constants.PhotoTag);
    }


    /**
     * 拍照回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.PhotoTag:

                MyLog.i(TAG, "回调 裁剪图片");

                if (resultCode == RESULT_OK) {

                    try {
                        startCropIntent(data.getData());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }


                break;
            case Constants.TakingTag:

                MyLog.i(TAG, "回调 拍摄照片");

                if (resultCode == RESULT_OK) {
                    tempFile = new File(Constants.HEAD_IMG + "head.png");
                    try {
                        startCropIntent(Uri.fromFile(tempFile));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }

                break;

            case Constants.TailoringResult:

                MyLog.i(TAG, "回调 裁剪完成 imageUri = " + imageUri);
                if(resultCode == Activity.RESULT_OK){
                    if (imageUri != null) {
                        Bitmap bitmap = BmpUtils.decodeUriAsBitmap(mContext, imageUri);
//
//                    custom_black_bg.setDrawingCacheEnabled(true);
//                    custom_black_bg.buildDrawingCache();
//                    Bitmap dstbmp0 = custom_black_bg.getDrawingCache(); // 获取图片
//
//                    if(dstbmp0!=null)
//                    {
//                       MyLog.i(TAG,"处理图片 = width2222 = " + dstbmp0.getWidth());
//                       MyLog.i(TAG,"处理图片 = height222 = " + dstbmp0.getHeight());
//
//                        Bitmap end_bitmap = BmpUtils.combineBitmap(bitmap,dstbmp0,0,0);
//
//                        handleBitmap(end_bitmap);
//                    }
//                    else
//                    {
//                       MyLog.i(TAG,"处理图片 = dstbmp0 = null");
//                    }

                        handleBitmap(bitmap);
                    }
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片
     *
     * @param uri
     * @throws FileNotFoundException
     */
    private void startCropIntent(Uri uri) throws FileNotFoundException {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", OutputX);
        intent.putExtra("aspectY", OutputY);
        intent.putExtra("outputX", OutputX);
        intent.putExtra("outputY", OutputY);
        intent.putExtra("return-data", false);
        // 上面设为false的时候将MediaStore.EXTRA_OUTPUT关联一个Uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, Constants.TailoringResult);
    }


    /**
     * 处理图片
     *
     * @param my_bitmap
     */
    void handleBitmap(Bitmap my_bitmap) {

        if (my_bitmap == null) {
            MyLog.i(TAG, "处理图片 my_bitmap  = null");
            return;
        } else {
            MyLog.i(TAG, "处理图片 my_bitmap  = " + my_bitmap);
        }


        MyLog.i(TAG, "Bitmap 宽度 = " + my_bitmap.getWidth());
        MyLog.i(TAG, "Bitmap 高度 = " + my_bitmap.getHeight());


        if (my_bitmap.getWidth() == OutputX && my_bitmap.getHeight() == OutputY) {

            Nowbitmap = my_bitmap;

//            String FileName = "jw_" + MyTime.getAllTime();
//           MyLog.i(TAG,"Bitmap FileName = " + FileName);

//        String baset64 = FileUtil.bitmapToBase64(my_bitmap);
//        mBleDeviceTools.set_screensaver_bast64(baset64);


            custom_img_square.setImageBitmap(my_bitmap);// 用ImageView显示出来
            custom_img_circular.setImageBitmap(my_bitmap);// 用ImageView显示出来

            if (mBleDeviceTools.get_is_scanf_type()) {
                bitMapData = BmpUtils.getbitmapNewByte(my_bitmap);
            } else {
                bitMapData = BmpUtils.getbitmapByte(my_bitmap);
            }

            isChange = true;

            // 保存在SD卡中
//        BmpUtils.setPicToView(my_bitmap, FileName);
            //保存到十六进制文件
//        BmpUtils.saveBitmapTrace(my_bitmap, FileName);

//        my_text.setText(
////                file_name + ".png"
////                + "\n" + file_name + ".txt"
////                + "\n" +
//                "宽度 = " + my_bitmap.getWidth() + "  " + "高度 = " + my_bitmap.getHeight()
//                        + "\n" + "数据长度 = " + my_data.length
//        );
        } else {

            isChange = false;

            if (!JavaUtil.checkIsNull(mBleDeviceTools.get_screensaver_bast64())) {
                MyLog.i(TAG, "显示头像 Bast64 ");
                Bitmap bitmap = FileUtil.base64ToBitmap(mBleDeviceTools.get_screensaver_bast64());
                custom_img_square.setImageBitmap(bitmap);
                custom_img_circular.setImageBitmap(bitmap);
                bitMapData = BmpUtils.getbitmapByte(bitmap);
            } else {
//            custom_img_square.setBackgroundResource(R.drawable.default_user_image);


//            custom_img_circular.setBackgroundResource(R.drawable.default_user_image);
                custom_img_square.setImageBitmap(defuleBitmap);
                custom_img_circular.setImageBitmap(defuleBitmap);
            }


            AppUtils.showToast(mContext, R.string.sned_img_size_error);

        }


    }


    //发送数据提示框
    void SendImageDialog() {


        View view = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_send_tip_view, null);

        final TextView send_speed_text = (TextView) view.findViewById(R.id.send_speed_text);
        SeekBar send_speed_seekbar = (SeekBar) view.findViewById(R.id.send_speed_seekbar);


        send_speed_seekbar.setMax(4);
        int leve = setSendSpeed(send_speed_text);
        send_speed_seekbar.setProgress(leve);

        send_speed_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mBleDeviceTools.set_screensaver_speed_level(progress);
                setSendSpeed(send_speed_text);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.dialog_send_title))//设置对话框标题
                .setView(view)
                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {//添加确定按钮

                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                        sendDataToBle();

                    }

                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {//添加返回按钮


            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件


            }

        }).show();//在按键响应事件中显示此对话框

    }

    int setSendSpeed(TextView textview) {

        int level = mBleDeviceTools.get_screensaver_speed_level();

        if (level < 0) {
            level = 0;
        } else if (level > 4) {
            level = 4;
        }

        textview.setText(getString(send_level[level]));

        AllSleepTime = sleep_time[level];
        ReplaSleepTime = sleep_time[level];

        return level;

    }


    /**
     * 发送蓝牙数据
     */
    void sendDataToBle() {

        if (bitMapData != null && bitMapData.length > 0) {

            MyLog.i(TAG, "发送数据 服务不为空");


            final byte crc = getCrc(bitMapData);

            MyLog.i(TAG, "crc = " + (crc & 0xff));

//                saveColor();

            waitDialog.show(getString(R.string.loading0));

            mBleDeviceTools.set_screensaver_bast64("");
            mBleDeviceTools.set_screensaver_color(-1);

            MyLog.i(TAG, "屏保 = OutputX = " + OutputX);
            MyLog.i(TAG, "屏保 = OutputY = " + OutputY);

            sendImageHead(OutputX, OutputY, crc);

            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    update();

                }
            }, DelayTime2);

        } else {
            MyLog.i(TAG, "发送数据 数据为空");
            AppUtils.showToast(mContext, R.string.send_fail);
        }

    }


    /**
     * 更新数据
     */
    void update() {

        waitDialog.close();
        transmissionProgress = 0;
//        progressDialog = new ProgressDialog(mContext);
//        progressDialog.setIcon(R.drawable.ic_launcher);
//        progressDialog.setTitle(getString(R.string.send_loading));
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置进度条对话框//样式（水平，旋转）
//        progressDialog.setMax(BmpUtils.getSendPageLenght(bitMapData));
//        // 是否可以按回退键取消
//        progressDialog.setCancelable(false);

        initLoadingdialog();
        updateTextTask = new UpdateTextTask(mContext);
        updateTextTask.execute();

    }


    class UpdateTextTask extends AsyncTask<Void, Integer, Integer> {
        private Context context;

        UpdateTextTask(Context context) {
            this.context = context;
        }

        /**
         * 运行在UI线程中，在调用doInBackground()之前执行
         */
        @Override
        protected void onPreExecute() {
//            progressDialog.show();
            loading_dialog.show();


        }


        long start_time;
        long end_time;
        String send_state_str = "";

        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        @Override
        protected Integer doInBackground(Void... params) {


            //数据长度
            int data_lenght = BmpUtils.getSendPageLenght(bitMapData);
            //余数
            int data_remainder = BmpUtils.getSendPageRemainder(bitMapData);

            MyLog.i(TAG, "发送数据 data_lenght = " + data_lenght);
            MyLog.i(TAG, "发送数据 data_remainder = " + data_remainder);

            send_state_str = "";

            int is_send_success = 0;

            //总补发次数
            int bufaCount = 0;
            //当次补发次数
            int bufaOneCount = 0;

            for (int i = 0; i < data_lenght; i++) {

                if (i == 0) {
                    start_time = System.currentTimeMillis();
                }


                byte[] val = null;

                if (i == data_lenght - 1) {

                    if (data_remainder > 0) {
                        val = new byte[data_remainder];

                        for (int j = 0; j < data_remainder; j++) {

                            val[j] = bitMapData[(i * 20 + j)];

                        }

                        send_image_data(val);
                        is_send_success++;
                        bufaOneCount = 0;
                        publishProgress(i);
//                        boolean is_state = send_image_data(val);
//
//                        if (is_state) {
//                            is_send_success++;
//                            bufaOneCount = 0;
//                            publishProgress(i);
//                        } else if (IsReplacement) {
//                            bufaCount++;
//                            bufaOneCount++;
//                            i--;
//                            ReplaSleep();
//                        }
                    }


                } else {
                    val = new byte[20];
                    for (int j = 0; j < 20; j++) {

                        val[j] = bitMapData[(i * 20 + j)];

                    }

                    send_image_data(val);
                    is_send_success++;
                    bufaOneCount = 0;
                    publishProgress(i);
//                    boolean is_state = mService.send_image_data(val);
//
////                       MyLog.i(TAG, "发送数据 i = " + i + " data = " + BmpUtils.bytes2HexString16TXT(val) + "  state = " + is_state);
////                       MyLog.i(TAG, "发送数据 i = " + i + "  state = " + is_state);
//
//                    if (is_state) {
//                        is_send_success++;
//                        bufaOneCount = 0;
//                        publishProgress(i);
//                    } else if (IsReplacement) {
//                        bufaCount++;
//                        bufaOneCount++;
//                        i--;
//                        ReplaSleep();
//                    }

                    AllSleep();


                }

//               MyLog.i(TAG, "发送数据 i = " + i + "  bufaOneCount = " + bufaOneCount);

                if (bufaOneCount > ReplaMaxCount) {
                    MyLog.i(TAG, "发送数据 i = " + i + "  失败 =");
                    break;

                }
            }


            int countSize = data_lenght;

            if (data_remainder == 0) {
                countSize -= 1;
            }


            end_time = System.currentTimeMillis();

            MyLog.i(TAG, "耗时11 = " + ((float) (end_time - start_time) / (float) 1000) + "秒");
            MyLog.i(TAG, "耗时22 = " + (end_time - start_time) + "毫秒");
            MyLog.i(TAG, "耗时33 = 成功包数 = " + is_send_success);
            MyLog.i(TAG, "耗时44 = 总数" + countSize);
            MyLog.i(TAG, "耗时44 = 成功率" + (int) ((float) is_send_success / (float) countSize * 100) + "%");
            transmissionProgress = (int) ((float) is_send_success / (float) countSize * 100);
            MyLog.i(TAG, "耗时44 = 补发次数" + bufaCount);
            send_state_str = "耗时 = " + ((float) (end_time - start_time) / (float) 1000) + "秒" + "\n"
                    + "成功率" + (int) ((float) is_send_success / (float) countSize * 100) + "%";

            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {
            if(isOnDestroy){
                return;
            }

            send_state.setText(send_state_str);


            if (transmissionProgress >= 100) {
//                Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(context, "发送失败！", Toast.LENGTH_SHORT).show();
            }

            MyLog.i(TAG, "onPostExecute transmissionProgress = " + transmissionProgress);
            MyLog.i(TAG, "已发送 = " + transmissionProgress);

//            progressDialog.dismiss();
            loading_dialog.dismiss();


        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {

//            progressDialog.setProgress(values[0] + 1);

            send_loading_progressbar.setProgress(values[0] + 1);

            float bili = (float) (values[0] + 1) / (float) send_loading_progressbar.getMax() * 100;
//            end_loading_text.setText(String.valueOf(bili));


            String str = String.valueOf(bili);

            if (bili > 10) {
                if (str.length() >= 5) {
                    str = str.substring(0, 5);
                }
            } else {
                if (str.length() >= 4) {
                    str = str.substring(0, 4);
                }
            }


            end_loading_text.setText(str);
//            end_loading_text.setText(BengXinUtils.GetFormat(1, bili));


        }
    }

    /**
     * 每次发送的睡眠时间
     */
    void AllSleep() {
        if (IsAllSleep) {
            try {
                Thread.sleep(AllSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 补发是的睡眠时间
     */
    void ReplaSleep() {
        if (IsReplaSleep) {
            try {
                Thread.sleep(ReplaSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


    /**
     * 选择颜色
     */
    private void showColorDialog() {
        // TODO Auto-generated method stub
        View view = getLayoutInflater().inflate(R.layout.dialog_color, null);
        dialog = new Dialog(mContext, R.style.shareStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;


//        //颜色选择器
        final ColorPicker picker = (ColorPicker) view.findViewById(R.id.picker);
        SVBar svBar = (SVBar) view.findViewById(R.id.svbar);
        OpacityBar opacityBar = (OpacityBar) view.findViewById(R.id.opacitybar);
        SaturationBar saturationBar = (SaturationBar) view.findViewById(R.id.saturationbar);
        ValueBar valueBar = (ValueBar) view.findViewById(R.id.valuebar);


        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        picker.addSaturationBar(saturationBar);
        picker.addValueBar(valueBar);


        //获得颜色.
//        picker.getColor();
        picker.setOldCenterColor(ColorARGB);
        picker.setColor(ColorARGB);

        svBar.setSaturation(1f);


        //设置旧的选择颜色u可以这样做
//        picker.setOldCenterColor(picker.getColor());

        // 将侦听器添加到实现的颜色选择器中
        //in the activity
        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {

//                设置旧的选择颜色u可以这样做
                picker.setOldCenterColor(picker.getColor());

                ColorARGB = color;


            }
        });


        //to turn of showing the old color 转旧显色
        picker.setShowOldCenterColor(true);


        view.findViewById(R.id.tv_color_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        view.findViewById(R.id.tv_color_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ColorARGB = MyUtils.getNoTransparentColor(ColorARGB);
                MyLog.i(TAG, "Set_screensaver_color = " + ColorARGB);
                dialog.cancel();
                updateColor();

            }
        });


        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    void updateColor() {


        int a = Color.alpha(ColorARGB);
        int r = Color.red(ColorARGB);
        int g = Color.green(ColorARGB);
        int b = Color.blue(ColorARGB);
        MyLog.i(TAG, "color = " + r + "," + g + "," + b + "  a = " + a);
        text_time.setTextColor(Color.argb(a, r, g, b));
//        iv_active.setTextColor(Color.argb(a, r, g, b));

    }


    /**
     * 初始化广的Action
     *
     * @return
     */
    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastTools.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BroadcastTools.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BroadcastTools.ACTION_GATT_SET_SCREENSAVER_SUCCESS);
        intentFilter.addAction(BroadcastTools.ACTION_GATT_SET_SCREENSAVER_FAIL);
        return intentFilter;
    }


    void updateScreenShape() {


        custom_img_square.setVisibility(View.GONE);
        custom_img_circular.setVisibility(View.GONE);
        custom_bg0.setVisibility(View.GONE);
        custom_bg1.setVisibility(View.GONE);
        custom_bg2.setVisibility(View.GONE);
        r_custom_pos.setVisibility(View.GONE);
        v_custom_pos.setVisibility(View.GONE);


        switch (item) {
            case 0:
                custom_img_square.setVisibility(View.VISIBLE);
                custom_bg0.setVisibility(View.VISIBLE);
                r_custom_pos.setVisibility(View.VISIBLE);
                v_custom_pos.setVisibility(View.VISIBLE);
                break;
            case 1:
                custom_img_square.setVisibility(View.VISIBLE);
                custom_bg1.setVisibility(View.VISIBLE);
                mBleDeviceTools.set_screensaver_post_time(1);
                break;
            case 2:
                custom_img_circular.setVisibility(View.VISIBLE);
                custom_bg2.setVisibility(View.VISIBLE);
                mBleDeviceTools.set_screensaver_post_time(1);

                break;
        }

    }


    void handSendImageFail() {


        if (updateTextTask != null && !updateTextTask.isCancelled() && updateTextTask.getStatus() == AsyncTask.Status.RUNNING) {
            updateTextTask.cancel(true);
            updateTextTask = null;
            AppUtils.showToast(mContext, R.string.send_fail);

            MyLog.i(TAG, "onPostExecute transmissionProgress = " + transmissionProgress);
            MyLog.i(TAG, "已发送 = " + transmissionProgress);

            isChange = false;
            custom_img_square.setImageBitmap(defuleBitmap);
            custom_img_circular.setImageBitmap(defuleBitmap);

            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    saveColor();
                    if (loading_dialog != null && loading_dialog.isShowing()) {
                        loading_dialog.dismiss();
                    }


                }
            }, DelayTime1);


        }
    }

    void saveColor() {

        mBleDeviceTools.set_device_screensaverb(true);
        mBleDeviceTools.set_screensaver_color(ColorARGB);
        if (HomeActivity.ISBlueToothConnect()) {
            setDeviceScreensaverInfo();
        }
    }

    void savePos() {


        int bianju_width = 4;
        int bianju_height = 8;

        MyLog.i(TAG, "屏保 screensaverWidth = " + screensaverWidth);
        MyLog.i(TAG, "屏保 screensaverHeight = " + screensaverHeight);
        MyLog.i(TAG, "屏保 timeWidth = " + timeWidth);
        MyLog.i(TAG, "屏保 timeHeight = " + timeHeight);

        int coordinate[] = MyUtils.getSendBleCoordinate(mBleDeviceTools, bianju_width, bianju_height);

        int xCordinate = coordinate[0];
        int yCordinate = coordinate[1];


        mBleDeviceTools.set_screensaver_x_time(xCordinate);
        mBleDeviceTools.set_screensaver_y_time(yCordinate);


        //居中
        if (mBleDeviceTools.get_screensaver_post_time() == 0) {

            text_time.setGravity(Gravity.CENTER);

            if (item == 1) {
                int height = dip2px(mContext, 140);
                int top = height / (240 / 35);
                text_time.setPadding(0, top, 0, 0);
            }

            //上
        } else if (mBleDeviceTools.get_screensaver_post_time() == 1) {
            text_time.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            if (item == 1) {
                int height = dip2px(mContext, 140);
                int top = height / (240 / 45);
                text_time.setPadding(0, top, 0, 0);
            }
            //下
        } else if (mBleDeviceTools.get_screensaver_post_time() == 2) {
            text_time.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            if (item == 1) {
                int height = dip2px(mContext, 140);
                int bootom = height / (240 / 18);
                text_time.setPadding(0, 0, 0, bootom);
            }
            //左上
        } else if (mBleDeviceTools.get_screensaver_post_time() == 3) {
            text_time.setGravity(Gravity.LEFT);
            //右上
        } else if (mBleDeviceTools.get_screensaver_post_time() == 4) {
            text_time.setGravity(Gravity.RIGHT);
            //左下
        } else if (mBleDeviceTools.get_screensaver_post_time() == 5) {
            text_time.setGravity(Gravity.LEFT | Gravity.BOTTOM);
            //右下
        } else if (mBleDeviceTools.get_screensaver_post_time() == 6) {
            text_time.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
            //左
        } else if (mBleDeviceTools.get_screensaver_post_time() == 7) {


            text_time.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

            if (item == 1) {

                int width = dip2px(mContext, 140);
                int left = width / (240 / 18);

                int height = dip2px(mContext, 140);
                int top = height / (240 / 35);
                text_time.setPadding(left, top, 0, 0);
            }


            //右
        } else if (mBleDeviceTools.get_screensaver_post_time() == 8) {


            text_time.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

            if (item == 1) {
                int width = dip2px(mContext, 140);
                int right = width / (240 / 18);

                int height = dip2px(mContext, 140);
                int top = height / (240 / 35);
                text_time.setPadding(0, top, right, 0);
            }


        } else {


            text_time.setGravity(Gravity.CENTER);
        }

        mBleDeviceTools.set_screensaver_is_show_time(1);
        mBleDeviceTools.set_screensaver_is_show_date(1);
//                    mBleDeviceTools.set_screensaver_is_user_imager(1);

//        text_time.setX(mBleDeviceTools.get_screensaver_x_time());
//        text_time.setY(mBleDeviceTools.get_screensaver_y_time());
    }


    private AlertDialog loading_dialog;
    private ProgressBar send_loading_progressbar;
    private RelativeLayout rl_theme_type1;
    private RelativeLayout rl_theme_type2;
    private ImageView send_loading_img_bg_type1, send_loading_img_text_type1;
    private ImageView send_loading_img_bg_type2, send_loading_img_text_type2;
    private TextView end_loading_text;


    //发送数据提示框
    void initLoadingdialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_send_loading_view, null);
        send_loading_progressbar = (ProgressBar) view.findViewById(R.id.send_loading_progressbar);
        end_loading_text = (TextView) view.findViewById(R.id.end_loading_text);
        rl_theme_type1 = (RelativeLayout) view.findViewById(R.id.rl_theme_type1);
        rl_theme_type2 = (RelativeLayout) view.findViewById(R.id.rl_theme_type2);
        send_loading_img_bg_type1 = (ImageView) view.findViewById(R.id.send_loading_img_bg_type1);
        send_loading_img_text_type1 = (ImageView) view.findViewById(R.id.send_loading_img_text_type1);
        send_loading_img_bg_type2 = (ImageView) view.findViewById(R.id.send_loading_img_bg_type2);
        send_loading_img_text_type2 = (ImageView) view.findViewById(R.id.send_loading_img_text_type2);
        send_loading_progressbar.setMax(BmpUtils.getSendPageLenght(bitMapData));

        rl_theme_type1.setVisibility(View.GONE);
        rl_theme_type2.setVisibility(View.GONE);


        builder.setView(view);
        builder.setTitle(getString(R.string.send_loading));
        loading_dialog = builder.show();
        loading_dialog.setCancelable(false);
    }


    /**
     * 校验收到的完整数据包是否正确
     *
     * @param mBtRecData
     * @return
     */
    public byte getCrc(byte[] mBtRecData) {
        byte crc = 0;
        int length = mBtRecData.length;
        for (int i = 0; i < length; i++) {
            crc = getCheckNum(mBtRecData[i], crc);
        }
        return crc;

    }

    /**
     * 根据校验规则，得到一个校验码
     *
     * @param value
     * @param crc
     * @return
     */
    private byte getCheckNum(byte value, byte crc) {
        byte polynomial = (byte) 0x97;
        crc ^= value;

        for (int i = 0; i < 8; i++) {
            if ((crc & 0x80) != 0) {
                crc <<= 1;
                crc ^= polynomial;
            } else {
                crc <<= 1;
            }
        }
        return crc;
    }


    long t1 = 0;

    boolean checkContinuousClick() {

        boolean rsult = true;

        if (t1 == 0) {//第一次单击，初始化为本次单击的时间
            t1 = (new Date()).getTime();
        } else {
            long curTime = (new Date()).getTime();//本地单击的时间

            if (curTime - t1 > 500) {
                //间隔5秒允许点击，可以根据需要修改间隔时间
                t1 = curTime;//当前单击事件变为上次时间
                MyLog.i(TAG, "离线血压 == 111");
                rsult = true;
            } else {
                MyLog.i(TAG, "离线血压 == 222");
                rsult = false;
            }

        }
        return rsult;
    }

    void updateUi(boolean isOpen) {

        if (isOpen) {
            send_text1.setTextColor(this.getResources().getColor(
                    R.color.public_text_color1));

            send_text2.setTextColor(this.getResources().getColor(
                    R.color.public_text_color1));

            send_text3.setTextColor(this.getResources().getColor(
                    R.color.public_text_color1));


            button_send_data.setBackgroundResource(R.drawable.my_button1_selector);

        } else {

            send_text1.setTextColor(this.getResources().getColor(
                    R.color.public_unenable_text_color1));

            send_text2.setTextColor(this.getResources().getColor(
                    R.color.public_unenable_text_color1));

            send_text3.setTextColor(this.getResources().getColor(
                    R.color.public_unenable_text_color1));


//            button_send_data.setBackgroundResource(R.drawable.clock_yuan_off);

        }

    }

    int now_cycle = 1;

    /**
     * 方屏-选择位置
     */
    void showPosDialogType0() {

        final String[] BloodGradeText = {
                getString(R.string.dialog_send_pos3), getString(R.string.dialog_send_pos4), getString(R.string.dialog_send_pos0), getString(R.string.dialog_send_pos5), getString(R.string.dialog_send_pos6)
        };

        now_cycle = mBleDeviceTools.get_screensaver_post_time();

        AlertDialog.Builder builder = new AlertDialog.Builder(SendBleActivity.this);
        builder.setTitle(getString(R.string.time_dialog_title));


        //    设置一个单项选择下拉框
        /**
         * 第一个参数指定我们要显示的一组下拉单选框的数据集合
         * 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认'女' 会被勾选上
         * 第三个参数给每一个单选项绑定一个监听器
         */

        int cc = 2;


        switch (now_cycle) {
            case 3:
                cc = 0;
                break;
            case 4:
                cc = 1;
                break;
            case 0:
                cc = 2;
                break;
            case 5:
                cc = 3;
                break;
            case 6:
                cc = 4;
                break;
        }


        builder.setSingleChoiceItems(BloodGradeText, cc, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                switch (which) {
                    case 0:
                        now_cycle = 3;
                        break;
                    case 1:
                        now_cycle = 4;
                        break;
                    case 2:
                        now_cycle = 0;
                        break;
                    case 3:
                        now_cycle = 5;
                        break;
                    case 4:
                        now_cycle = 6;
                        break;
                }


            }
        });

        builder.setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                MyLog.i(TAG, "输出002 = " + now_cycle);
                mBleDeviceTools.set_screensaver_post_time(now_cycle);
                savePos();
                setDeviceScreensaverInfo();


            }
        });
        builder.setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

    }

    /**
     * 球拍屏-选择位置
     */
    void showPosDialogType1() {

        final String[] BloodGradeText = {
                getString(R.string.dialog_send_pos0), getString(R.string.dialog_send_pos1), getString(R.string.dialog_send_pos2),
                getString(R.string.dialog_send_pos7), getString(R.string.dialog_send_pos8)
        };

        now_cycle = mBleDeviceTools.get_screensaver_post_time();

        AlertDialog.Builder builder = new AlertDialog.Builder(SendBleActivity.this);
        builder.setTitle(getString(R.string.time_dialog_title));


        //    设置一个单项选择下拉框
        /**
         * 第一个参数指定我们要显示的一组下拉单选框的数据集合
         * 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认'女' 会被勾选上
         * 第三个参数给每一个单选项绑定一个监听器
         */

        int cc = 0;


        switch (now_cycle) {
            case 0:
                cc = 0;
                break;
            case 1:
                cc = 1;
                break;
            case 2:
                cc = 2;
                break;
            case 7:
                cc = 3;
                break;
            case 8:
                cc = 4;
                break;
        }


        builder.setSingleChoiceItems(BloodGradeText, cc, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                switch (which) {
                    case 0:
                        now_cycle = 0;
                        break;
                    case 1:
                        now_cycle = 1;
                        break;
                    case 2:
                        now_cycle = 2;
                        break;
                    case 3:
                        now_cycle = 7;
                        break;
                    case 4:
                        now_cycle = 8;
                        break;
                }


            }
        });

        builder.setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                MyLog.i(TAG, "输出002 = " + now_cycle);
                mBleDeviceTools.set_screensaver_post_time(now_cycle);
                savePos();
                setDeviceScreensaverInfo();
            }
        });
        builder.setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

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
        new AlertDialog.Builder(mContext)
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

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
