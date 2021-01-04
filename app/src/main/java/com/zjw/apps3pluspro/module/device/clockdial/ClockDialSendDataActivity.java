package com.zjw.apps3pluspro.module.device.clockdial;

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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lidroid.xutils.BitmapUtils;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.device.entity.ClockDialCustomModel;
import com.zjw.apps3pluspro.module.device.entity.ThemeModle;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.AuthorityManagement;
import com.zjw.apps3pluspro.utils.BmpUtils;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.ThemeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.ColorPickerView;
import com.zjw.apps3pluspro.view.ColorRoundView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 表盘发送数据
 */
public class ClockDialSendDataActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = ClockDialSendDataActivity.class.getSimpleName();
    private Context mContext;
    private MyActivityManager manager;
    //轻量级存储
    public BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    private WaitDialog waitDialog;

    public int deviceWidth = 0;
    public int deviceHeight = 0;
    public int deviceShape = 0;
    public int ClockDialDataSize = 0;
    public boolean deviceIsHeart;
    public boolean deviceScanfTypeIsVer = false;
    public int UiType = 2;//1=长方形3列/=2正方形2列


    //======表盘传输相关===========
    private ThemeModle mThemeModle;
    private boolean is_send_data = false;
    private boolean is_send_fial = false;
    //模拟丢包
    private int testPassDataValue = 0;


    private byte[] Bytes;
    private int IntentType = 0;

    private boolean isCustom = false;

    //表盘市场相关===============================
    private String nowImgUrl = "";

    //自定义市场相关===============================
    private LinearLayout llCustomView;

    private String customClockDialBinName;

    private Bitmap NowBgBitmap;
    private Bitmap defuleBgBitmap;

    private Bitmap NowTextBitmap;
    private Bitmap defuleTextBitmap;

    private ImageView ivEffectBg;
    private ImageView ivEffectText, ivCover;
    private ColorRoundView crv11, crv12, crv13, crv14, crv15, crv16, crv21, crv22, crv23, crv24, crv25;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_clock_dial_send_data;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = ClockDialSendDataActivity.this;
        imageUri = Uri.parse(Constants.IMAGE_FILE_LOCATION);
        waitDialog = new WaitDialog(mContext);
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        initBroadcast();
        initView();
        initData();
        enableNotifacationThemeRead();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {
            }
        }

        if (TimerTwoHandler != null) {
            TimerTwoHandler.removeCallbacksAndMessages(null);
        }

        if (TimerThreeHandler != null) {
            TimerThreeHandler.removeCallbacksAndMessages(null);
        }

        if (TimerFourHandler != null) {
            TimerFourHandler.removeCallbacksAndMessages(null);
        }

        if (TimerFiveHandler != null) {
            TimerFiveHandler.removeCallbacksAndMessages(null);
        }

        super.onDestroy();
    }


    private void initView() {

        findViewById(R.id.public_head_back).setOnClickListener(this);

        crv11 = findViewById(R.id.crv11);
        crv12 = findViewById(R.id.crv12);
        crv13 = findViewById(R.id.crv13);
        crv14 = findViewById(R.id.crv14);
        crv15 = findViewById(R.id.crv15);
        crv16 = findViewById(R.id.crv16);
        crv21 = findViewById(R.id.crv21);
        crv22 = findViewById(R.id.crv22);
        crv23 = findViewById(R.id.crv23);
        crv24 = findViewById(R.id.crv24);
        crv25 = findViewById(R.id.crv25);

        findViewById(R.id.public_head_back).setOnClickListener(this);
        findViewById(R.id.tvSelectPicture).setOnClickListener(this);
        findViewById(R.id.sendData).setOnClickListener(this);
        findViewById(R.id.ivSelectColor).setOnClickListener(this);
        findViewById(R.id.crv11).setOnClickListener(this);
        findViewById(R.id.crv12).setOnClickListener(this);
        findViewById(R.id.crv13).setOnClickListener(this);
        findViewById(R.id.crv14).setOnClickListener(this);
        findViewById(R.id.crv15).setOnClickListener(this);
        findViewById(R.id.crv16).setOnClickListener(this);
        findViewById(R.id.crv21).setOnClickListener(this);
        findViewById(R.id.crv22).setOnClickListener(this);
        findViewById(R.id.crv23).setOnClickListener(this);
        findViewById(R.id.crv24).setOnClickListener(this);
        findViewById(R.id.crv25).setOnClickListener(this);

        ivEffectBg = (ImageView) findViewById(R.id.ivEffectBg);
        ivCover = (ImageView) findViewById(R.id.ivCover);
        ivEffectText = (ImageView) findViewById(R.id.ivEffectText);
        llCustomView = (LinearLayout) findViewById(R.id.llCustomView);

        if (mBleDeviceTools.get_device_theme_shape() == 2) {
            ivCover.setVisibility(View.VISIBLE);
        } else {
            ivCover.setVisibility(View.GONE);
        }
    }


    private void initData() {

        try {
            Intent intent = this.getIntent();

            deviceWidth = intent.getIntExtra(IntentConstants.ClockDialWidth, 0);
            deviceHeight = intent.getIntExtra(IntentConstants.ClockDialHeight, 0);
            deviceShape = intent.getIntExtra(IntentConstants.ClockDialShape, 0);
            ClockDialDataSize = intent.getIntExtra(IntentConstants.ClockDialDataSize, 0);
            deviceIsHeart = intent.getBooleanExtra(IntentConstants.ClockDialIsHeart, false);
            deviceScanfTypeIsVer = intent.getBooleanExtra(IntentConstants.ClockDialisScanfTypeVer, false);
            UiType = intent.getIntExtra(IntentConstants.ClockDialisUiType, 0);

            IntentType = intent.getIntExtra(IntentConstants.IntentClockDialSendDataType, 0);

            isCustom = (IntentType == IntentConstants.ClockDialSendDataType2) ? true : false;


            switch (IntentType) {
                case IntentConstants.ClockDialSendDataType1:
                    try {
                        if (ClockDialActivity.clockDailData != null && ClockDialActivity.clockDailData.length != 0) {
                            Bytes = ClockDialActivity.clockDailData;
                            nowImgUrl = intent.getStringExtra(IntentConstants.ClockDialImgUrl);
                            new BitmapUtils(mContext).display(ivEffectBg, nowImgUrl);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case IntentConstants.ClockDialSendDataType2:
                    ClockDialCustomModel mClockDialCustomModel = intent.getParcelableExtra(IntentConstants.ClockDialCustomModel);

                    if (mClockDialCustomModel != null) {
                        String custom_dial_bin_name = mClockDialCustomModel.getBinName();
                        String custom_dial_img_bg = mClockDialCustomModel.getBgName();
                        String custom_dial_img_text = mClockDialCustomModel.getTextName();

                        MyLog.i(TAG, "custom_dial_bin_name = " + custom_dial_bin_name);
                        MyLog.i(TAG, "custom_dial_img_bg = " + custom_dial_img_bg);
                        MyLog.i(TAG, "custom_dial_img_text = " + custom_dial_img_text);

                        customClockDialBinName = custom_dial_bin_name;

                        defuleBgBitmap = CustomClockDialUtils.getAssetBitmap(mContext, Constants.ASSETS_CUSTOM_DIAL_DIR + custom_dial_img_bg);//filePath
                        defuleTextBitmap = CustomClockDialUtils.getAssetBitmap(mContext, Constants.ASSETS_CUSTOM_DIAL_DIR + custom_dial_img_text);//filePath

                        NowBgBitmap = defuleBgBitmap;
                        NowTextBitmap = defuleTextBitmap;

                        MyLog.i(TAG, "NowBgBitmap width = " + NowBgBitmap.getWidth());
                        MyLog.i(TAG, "NowBgBitmap height = " + NowBgBitmap.getHeight());

                        MyLog.i(TAG, "NowTextBitmap width = " + NowTextBitmap.getWidth());
                        MyLog.i(TAG, "NowTextBitmap height = " + NowTextBitmap.getHeight());

                        ivEffectBg.setImageBitmap(NowBgBitmap);
                        ivEffectText.setImageBitmap(NowTextBitmap);
                        setColor(255, 255, 255);

                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isCustom) {
            ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.custom_title));
            llCustomView.setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.my_theme_title));
            llCustomView.setVisibility(View.GONE);
        }


    }

    private int color_r = 0;
    private int color_g = 0;
    private int color_b = 0;

    private void setColor(int r, int g, int b) {
        color_r = r;
        color_g = g;
        color_b = b;
        NowTextBitmap = CustomClockDialUtils.getNewTextBitmap(defuleTextBitmap, r, g, b);
        ivEffectText.setImageBitmap(NowTextBitmap);
    }

    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.public_head_back:
                finish();
                break;

            case R.id.tvSelectPicture:
                if (AuthorityManagement.verifyStoragePermissions(ClockDialSendDataActivity.this)) {
                    MyLog.i(TAG, "SD卡权限 已获取");
                    showImgDialog();
                } else {
                    MyLog.i(TAG, "SD卡权限 未获取");
                }
                break;
            case R.id.crv11:
                initRoundView(crv11);
                break;
            case R.id.crv12:
                initRoundView(crv12);
                break;
            case R.id.crv13:
                initRoundView(crv13);
                break;
            case R.id.crv14:
                initRoundView(crv14);
                break;
            case R.id.crv15:
                initRoundView(crv15);
                break;
            case R.id.crv16:
                initRoundView(crv16);
                break;
            case R.id.crv21:
                initRoundView(crv21);
                break;
            case R.id.crv22:
                initRoundView(crv22);
                break;
            case R.id.crv23:
                initRoundView(crv23);
                break;
            case R.id.crv24:
                initRoundView(crv24);
                break;
            case R.id.crv25:
                initRoundView(crv25);
                break;
            case R.id.ivSelectColor:
                showSelectColor();
                break;
            case R.id.sendData:
                if (isCustom) {
                    Bytes = CustomClockDialUtils.getCustonClockDialData(ClockDialSendDataActivity.this, customClockDialBinName, color_r, color_g, color_b, NowBgBitmap);
                }
                startSendThemeData(Bytes);
                break;

        }
    }

    private void initRoundView(ColorRoundView view) {
        crv11.setClick(false);
        crv12.setClick(false);
        crv13.setClick(false);
        crv14.setClick(false);
        crv15.setClick(false);
        crv16.setClick(false);
        crv21.setClick(false);
        crv22.setClick(false);
        crv23.setClick(false);
        crv24.setClick(false);
        crv25.setClick(false);
        view.setClick(true);
        int color = view.getcolor();
//        int red = (color & 0xff0000) >> 16;
//        int green = (color & 0x00ff00) >> 8;
//        int blue = (color & 0x0000ff);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        setColor(red, green, blue);
    }


    // 弹框的信息
    private Dialog dialog;

    private void showSelectColor() {
        // TODO Auto-generated method stub
        View view = getLayoutInflater().inflate(R.layout.select_color_layout, null);
        dialog = new Dialog(mContext, R.style.transparentdialog);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        ColorPickerView colorPickerView = view.findViewById(R.id.colorPickerView);
        colorPickerView.setOnColorChangedListenner(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color, int originalColor, float saturation) {
//                int red = (color & 0xff0000) >> 16;
//                int green = (color & 0x00ff00) >> 8;
//                int blue = (color & 0x0000ff);

                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                setColor(red, green, blue);

            }
        });
        view.findViewById(R.id.ivCancel).setOnClickListener(new View.OnClickListener() {
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


    //拍照相关
    private Uri imageUri;
    private File tempFile;

    /**
     * 弹出拍照-相册-对话框
     */
    private void showImgDialog() {
        // TODO Auto-generated method stub
        View view = getLayoutInflater().inflate(R.layout.photo_choose_dialog, null);
        dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
                dialog.dismiss();
                if (AuthorityManagement.verifyStoragePermissions(ClockDialSendDataActivity.this)) {
                    MyLog.i(TAG, "SD卡权限 已获取");
                } else {
                    MyLog.i(TAG, "SD卡权限 未获取");
                }

                if (AuthorityManagement.verifyPhotogrAuthority(ClockDialSendDataActivity.this)) {
                    MyLog.i(TAG, "拍照权限 已获取");
                    TakingPictures();
                } else {
                    MyLog.i(TAG, "拍照权限 未获取");
                }

            }
        });
        view.findViewById(R.id.albums).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                PhotoAlbum();
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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Constants.HEAD_IMG, "head.png")));
        startActivityForResult(intent, Constants.TakingTag);// 采用ForResult打开
    }

    /**
     * 相册
     */
    void PhotoAlbum() {
        Intent intent2 = new Intent(Intent.ACTION_PICK, null);
        intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent2, Constants.PhotoTag);
    }

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
//                    setPicToView(bitmap);// 保存在SD卡中
                        handleBitmap(bitmap);
                    }
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setPicToView(Bitmap mBitmap) {

        String sdStatus = Environment.getExternalStorageState();
        String fileName = "";
        // 检测sd是否可用
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            AppUtils.showToast(mContext, R.string.sd_card);
            return;
        }

        // 创建
        SysUtils.makeRootDirectory(Constants.CUSTOM_IMG);

        FileOutputStream b = null;
        fileName = Constants.CUSTOM_IMG + "custom_" + BaseApplication.getUserId() + ".png";// 图片名字
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        intent.putExtra("aspectX", deviceWidth);
        intent.putExtra("aspectY", deviceHeight);
        intent.putExtra("outputX", deviceWidth);
        intent.putExtra("outputY", deviceHeight);
        intent.putExtra("return-data", false);
        // 上面设为false的时候将MediaStore.EXTRA_OUTPUT关联一个Uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        intent.putExtra("noFaceDetection", true);
        intent.putExtra("circleCrop", false);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
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

        if (my_bitmap.getWidth() == deviceWidth && my_bitmap.getHeight() == deviceHeight) {
            NowBgBitmap = my_bitmap;
            ivEffectBg.setImageBitmap(NowBgBitmap);// 用ImageView显示出来
        } else {
            NowBgBitmap = defuleBgBitmap;
            ivEffectBg.setImageBitmap(NowBgBitmap);
            AppUtils.showToast(mContext, R.string.sned_img_size_error);
        }
    }

    /**
     * 初始化广播
     */
    private void initBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastTools.ACTION_GATT_DISCONNECTED);
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_READY);
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_BLOCK_END);
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_REPAIR_HEAD);
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_RESPONSE_SN);
        filter.addAction(BroadcastTools.ACTION_THEME_THEME_RESULT_SUCCESS_SN);
        filter.addAction(BroadcastTools.ACTION_THEME_SUSPENSION_FAIL);
        filter.addAction(BroadcastTools.ACTION_THEME_SUSPENSION_INTERVAL);
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

                case BroadcastTools.ACTION_GATT_DISCONNECTED:
                    MyLog.i(TAG, "主题数据 蓝牙断开了");
                    handFailState(true);
                    break;

                //准备就绪
                case BroadcastTools.ACTION_THEME_RECEIVE_READY:
                    MyLog.i(TAG, "主题数据 接收广播 准备就绪");
                    BleReady();
                    break;

                case BroadcastTools.ACTION_THEME_RECEIVE_BLOCK_END:
                    MyLog.i(TAG, "主题数据 接收广播 = 当前块结束");
                    FourNowCount = 0;
                    sendDataToNor();
                    break;

                case BroadcastTools.ACTION_THEME_RECEIVE_REPAIR_HEAD:
                    MyLog.i(TAG, "主题数据 接收广播 = 补发头");
                    break;

                case BroadcastTools.ACTION_THEME_RECEIVE_RESPONSE_SN:
                    MyLog.i(TAG, "主题数据 接收广播 = 补发数据");

                    Bundle budle = intent.getExtras();
                    replacmentSnList1 = budle.getIntegerArrayList(BroadcastTools.INTENT_PUT_THEME_REPONSE_SN_NUM_LIST);
                    replacmentSnList2 = new ArrayList<>(replacmentSnList1);
                    MyLog.i(TAG, "主题数据 接收广播 = 补发数据 replacmentSnList1 = " + replacmentSnList1);

                    FourNowCount = 0;

                    TimerThreeStart();

                    break;

                case BroadcastTools.ACTION_THEME_THEME_RESULT_SUCCESS_SN:
                    MyLog.i(TAG, "主题数据 接收广播 = 补发完成的SN");
                    Bundle budle2 = intent.getExtras();
                    int reslt_sn = budle2.getInt(BroadcastTools.INTENT_PUT_THEME_RESULT_SUCCESS_SN);
                    replacmentSnList2.remove((Object) (reslt_sn));

                    MyLog.i(TAG, "主题数据 接收广播 = 补发完成的SN reslt_sn = " + reslt_sn);
                    MyLog.i(TAG, "主题数据 接收广播 = 补发完成的SN replacmentSnList2 = " + replacmentSnList2.toString());

                    int FiveNowCount = 0;

                    //不需要第二轮补发提前结束
                    if (replacmentSnList2.size() == 0) {
                        MyLog.i(TAG, "主题数据 传输 不需要第二轮补发");
                        MyLog.i(TAG, "补发2 传输状态 = " + is_send_data);
                        if (is_send_data) {
                            sendBlockVerfication();
                        }
                        TimerFiveStop();
                    } else {
                        MyLog.i(TAG, "主题数据 传输 需要补发=等待定时器执行");
                    }
                    break;

                case BroadcastTools.ACTION_THEME_SUSPENSION_FAIL:
                    MyLog.i(TAG, "主题数据 接收广播 发送失败-中断");

                    Bundle budle3 = intent.getExtras();
                    int fail_code = budle3.getInt(BroadcastTools.INTENT_PUT_SUSPENSION_FAIL_FIAL_CODE);

                    MyLog.i(TAG, "主题数据 接收广播 发送失败-中断 fail_code = " + fail_code);
                    handFailStateFialCode(fail_code);
                    break;


                case BroadcastTools.ACTION_THEME_SUSPENSION_INTERVAL:
                    MyLog.i(TAG, "主题数据 接收广播 连接间隔");

                    Bundle budle4 = intent.getExtras();
                    int interval_code = budle4.getInt(BroadcastTools.INTENT_PUT_SUSPENSION_INTERVAL_INTERVAL_CODE);

                    MyLog.i(TAG, "主题数据 接收广播 连接间隔 interval_code = " + interval_code);

                    if (interval_code == 1) {
                        handFailState(false);
                    } else if (interval_code == 2) {
                        FourNowCount = 0;
                    }

                    break;

            }
        }
    };


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if(todayFragment != null && todayFragment.updateInfoService != null){
//            todayFragment.updateInfoService.handlePermissionsResult(requestCode, grantResults);
//        }
        switch (requestCode) {
            case AuthorityManagement.REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.i(TAG, "SD卡权限 回调允许");
                } else {
                    MyLog.i(TAG, "SD卡权限 回调拒绝");
                    showSettingDialog(getString(R.string.setting_dialog_storage));
                }
            }
            break;
            case AuthorityManagement.REQUEST_EXTERNAL_CALL_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.i(TAG, "拍照权限 回调允许");
//                    TakingPictures();
                } else {
                    MyLog.i(TAG, "拍照权限 回调拒绝");
                    showSettingDialog(getString(R.string.setting_dialog_call_camera));
                }
                break;
            case AuthorityManagement.REQUEST_EXTERNAL_MAIL_LIST:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //成功，开启摄像头
//                    MyLog.i(TAG, "读取联系人 回调允许");
//                    PhoneUtil.contacNameByNumber(this, "123456");
//                } else {
//                    MyLog.i(TAG, "读取联系人 回调拒绝");
//                    //授权失败
//                }
                break;
            case AuthorityManagement.REQUEST_EXTERNAL_PHONE_STATE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //成功，开启摄像头
//                    MyLog.i(TAG, "读取来电状态 回调允许");
//                } else {
//                    MyLog.i(TAG, "读取来电状态 回调拒绝");
//                    //授权失败
//                }
                break;
            case AuthorityManagement.REQUEST_EXTERNAL_ALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.i(TAG, "所有权限 回调允许");
                } else {
                    MyLog.i(TAG, "所有权限 回调拒绝");
//                    showSettingDialog(getString(R.string.setting_dialog_storage));
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void showSettingDialog(String title) {
        new AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.dialog_prompt))
                .setMessage(title)
                .setPositiveButton(getString(R.string.setting_dialog_setting), new DialogInterface.OnClickListener() {//添加确定按钮

                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
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


    //===============传输相关====================================================

    public void startSendThemeData(byte[] bytes) {


        if (mBleDeviceTools.get_device_is_theme_transmission()) {
            if (HomeActivity.ISBlueToothConnect()) {

                if (mBleDeviceTools.get_ble_device_power() >= 50) {
                    sendDataToBle(bytes);
                } else {
                    AppUtils.showToast(mContext, R.string.send_imge_error_low_power);
                }

            } else {
                AppUtils.showToast(mContext, R.string.no_connection_notification);
            }
        }

    }


    /**
     * 发送蓝牙数据
     */
    private void sendDataToBle(byte[] bytes) {

        MyLog.i(TAG, "主题传输发送 MTU = " + mBleDeviceTools.get_device_mtu_num());
        mThemeModle = new ThemeModle(bytes, mBleDeviceTools.get_device_mtu_num());


        MyLog.i(TAG, "发送数据 服务不为空");

        waitDialog.show(getString(R.string.loading0));

        startSendData();
        AppsendHead();
        TimerFourStart();
//            TimerOneStart();
    }

    //当前传输的块
    private int NowBlock = 0;
    //当前块的第几个包？
    private int NowPage = 0;
    private int SnNum = 0;
    //当前包的大小
    private int NowPageNumber = 0;

    //补发总包数
    private int ReplacementMax = 0;
    //当前补发的
    private int NowReplacement = 0;


    //当前是否是补发状态
    private boolean isReplacement = false;

    //开始传输
    private void startSendData() {
        NowBlock = 1;
        NowPage = 0;
        SnNum = 0;
        NowPageNumber = 0;
        NowReplacement = 0;
        isReplacement = false;
        is_send_fial = false;
    }


    //设备准备就绪
    private void BleReady() {
        is_send_data = true;
        is_send_fial = false;
        waitDialog.close();
        initLoadingdialog();
        TimerTwoStart();
        FourNowCount = 0;
    }

    private void sendDataToNor() {

        MyLog.i(TAG, "正常发1 传输状态 = " + is_send_data);

        if (is_send_data) {

            //不补发
            isReplacement = false;

            //传输结束
            if (mThemeModle.isLastBlock(NowBlock)) {
                MyLog.i(TAG, "传输结束");
                handSuccessState();
            }
            //响应不补发-需要发送下一块
            else {
                MyLog.i(TAG, "响应不补发-需要发送下一块");
                NowBlock++;
                NowPage = 0;
                TimerTwoStart();
            }
        }


    }


    //======== 第1个定时器====================
    //发送头
    private void AppsendHead() {
        MyLog.i(TAG, "发送头");
        MyLog.i(TAG, "发送头 getPageDataMax = " + mThemeModle.getPageDataMax());
        MyLog.i(TAG, "发送头 getTotalPageSize = " + mThemeModle.getTotalPageSize());

        //模拟丢包，清零
        testPassDataValue = 0;

        sendThemeHead(mThemeModle);
    }

    //======== 第2个定时器====================发送数据-正常数据
//    int CheckDelayTime = 1000*10;
//    int SendDelayTime = 1000*10;
    private int CheckDelayTime = 10;
    private int CheckDelayTimeTwo = 50;
    private int CheckDelayTimeThree = 100;
    private int SendDelayTime = 10;

    private void sendNorDataToBle() {


        byte[] send_data = mThemeModle.getSendData(NowBlock, NowPage, SnNum);
//            MyLog.i(TAG, "发送数据 正常 = " + ThemeUtils.bytes2HexString3(send_data));

//=====================================模拟丢包==============
//            if (testPassDataValue == 20 || testPassDataValue == 30) {
//                if (send_data != null) {
//                    MyLog.i(TAG, "发送数据 模拟丢包 = " + testPassDataValue);
//                    MyLog.i(TAG, "发送数据 正常 = 编码 = NowBlock " + NowBlock + " NowPage =" + NowPage + " SnNum = " + SnNum);
//                    MyLog.i(TAG, "发送数据 正常 = " + ThemeUtils.bytes2HexString3(send_data));
//                } else {
//                    MyLog.i(TAG, "发送数据 数据为空 = " + testPassDataValue);
//                }
//            } else {
//                if (send_data != null) {
//                    mService.send_theme_data(SnNum, send_data);
//                } else {
//                    MyLog.i(TAG, "发送数据 数据为空 = " + testPassDataValue);
//                }
//            }
//=====================================模拟丢包==============


        //正常发送数据
        if (send_data != null) {
            sendThemeData(SnNum, send_data);
        } else {
            MyLog.i(TAG, "发送数据 数据为空 = " + testPassDataValue);
        }

    }

    //发送块校验
    private void sendBlockVerfication() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyLog.i(TAG, "主题数据 传输 发送块校验1");
                sendThemeBlockVerfication();
                FourNowCount = 0;
            }
        }, CheckDelayTime);
    }


    //发送正常数据
    private void sendNormalData() {

        testPassDataValue++;
        FourNowCount = 0;
        NowReplacement = 0;
        NowPage++;
        SnNum++;

        //最后一个包
        if (mThemeModle.isLastPage(SnNum)) {
//            MyLog.i(TAG, "主题数据 传输 是最后的包 NowPage = " + NowPage + " SnNum = " + SnNum);

            //最后一个正常数据发送完毕
            TimerTwoStop();
            sendNorDataToBle();
            //发送块校验
            sendBlockVerfication();

        } else {
//            MyLog.i(TAG, "主题数据 传输 不是最后的包 NowPage = " + NowPage + " SnNum = " + SnNum);

            //最后一个正常数据发送完毕
            if (mThemeModle.isNowBlockLastPage(NowPage)) {
                TimerTwoStop();
            }

            sendNorDataToBle();
            //最后一个正常数据发送完毕
            if (mThemeModle.isNowBlockLastPage(NowPage)) {
                sendBlockVerfication();
            }

        }

        send_loading_progressbar.setProgress(SnNum);

        String por_str = ThemeUtils.getPercentageStr(send_loading_progressbar.getMax(), SnNum);

//        MyLog.i(TAG, "表盘传输进度条 = por_str = " + por_str);

        end_loading_text.setText(por_str);
    }

    private boolean TimerTwoIsStop = false;
    private Handler TimerTwoHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

//                    MyLog.i(TAG, "定时器2=TimerTwoIsStop = " + TimerTwoIsStop);

                    // 添加更新ui的代码
                    if (!TimerTwoIsStop) {
                        sendNormalData();
                        TimerTwoHandler.sendEmptyMessageDelayed(1, SendDelayTime);
                    }
                    break;
                case 0:
                    break;
            }
        }
    };

    private void TimerTwoStart() {
        MyLog.i(TAG, "定时器2=打开");
        TimerTwoHandler.sendEmptyMessage(1);
        TimerTwoIsStop = false;
    }

    private void TimerTwoStop() {
        MyLog.i(TAG, "定时器2=关闭");
        TimerTwoHandler.sendEmptyMessage(0);
        TimerTwoIsStop = true;
    }

    //======== 第3个定时器====================

    private ArrayList<Integer> replacmentSnList1;
    private ArrayList<Integer> replacmentSnList2;

    //补发数据间隔
    private int ReplacementDelayTime = 10;

    private void sendRepDataToBle(int sn_number) {

        MyLog.i(TAG, "发送数据 补发 = 编码 = sn_number " + sn_number + " getPageDataMax =" + mThemeModle.getPageDataMax());


        int now_page = sn_number % mThemeModle.getPageDataMax();

        if (now_page <= 0) {
            now_page = mThemeModle.getPageDataMax();
        }


        MyLog.i(TAG, "发送数据 补发 = 编码 = NowBlock " + NowBlock + " now_page =" + now_page + " sn_number = " + sn_number);

        byte[] send_data = mThemeModle.getSendData(NowBlock, now_page, sn_number);

        if (send_data != null) {
            MyLog.i(TAG, "发送数据 补发 = " + ThemeUtils.bytes2HexString3(send_data));
            sendThemeData(sn_number, send_data);
        } else {
            MyLog.i(TAG, "发送数据 数据为空 = " + testPassDataValue);
        }
    }


    //发送补发数据
    private void sendReplacment() {

        MyLog.i(TAG, "补发1 传输状态 = " + is_send_data);

        if (is_send_data) {

            NowReplacement++;

            MyLog.i(TAG, "主题数据 传输 发送补发数据 NowReplacement = " + NowReplacement + "  data_sn = " + replacmentSnList1.get(NowReplacement - 1));

            sendRepDataToBle(replacmentSnList1.get(NowReplacement - 1));


            //最后一个数据
            if (NowReplacement >= replacmentSnList1.size()) {

                MyLog.i(TAG, "主题数据 传输 发送补发数据  最后一个数据");

                TimerThreeStop();
                TimerFiveStart();


            }
        } else {
            TimerFiveStop();
        }

    }

    private void checkReplacmentData() {

        if (replacmentSnList2.size() == 0) {
            MyLog.i(TAG, "主题数据 传输 不需要第二轮补发");
            MyLog.i(TAG, "补发2 传输状态 = " + is_send_data);
            if (is_send_data) {
                sendBlockVerfication();
            }
        } else {
            MyLog.i(TAG, "补发3 传输状态 = " + is_send_data);
            if (is_send_data) {
                MyLog.i(TAG, "主题数据 传输 需要第二轮补发！！");
                replacmentSnList1 = new ArrayList<>(replacmentSnList2);
                TimerThreeStart();
            }
        }
    }

    private boolean TimerThreeIsStop = false;
    private Handler TimerThreeHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

                    MyLog.i(TAG, "定时器3=TimerThreeIsStop = " + TimerThreeIsStop);

                    // 添加更新ui的代码
                    if (!TimerThreeIsStop) {
                        sendReplacment();
                        TimerThreeHandler.sendEmptyMessageDelayed(1, ReplacementDelayTime);
                    }
                    break;
                case 0:
                    break;
            }
        }
    };

    private void TimerThreeStart() {
        isReplacement = true;
        NowReplacement = 0;
        MyLog.i(TAG, "定时器3=打开");
        TimerThreeHandler.sendEmptyMessage(1);
        TimerThreeIsStop = false;
    }

    private void TimerThreeStop() {
        MyLog.i(TAG, "定时器3=关闭");
        TimerThreeHandler.sendEmptyMessage(0);
        TimerThreeIsStop = true;
    }


    //======== 第4个定时器====================判断超时-总超时
    private int HeadDelayFourTime = 1000;
    private int FourNowCount = 0;
    private int FourMaxCount = 10;

    private boolean TimerFourIsStop = false;
    private Handler TimerFourHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

                    MyLog.i(TAG, "定时器4=TimerFourIsStop = " + TimerFourIsStop);

                    // 添加更新ui的代码
                    if (!TimerFourIsStop) {

                        MyLog.i(TAG, "定时器4 FourNowCount = " + FourNowCount);

                        if (FourNowCount > FourMaxCount) {
                            TimerTwoStop();
                            TimerFourStop();
                            TimerThreeStop();
                            handFailState(false);
                        }
                        FourNowCount++;
                        TimerFourHandler.sendEmptyMessageDelayed(1, HeadDelayFourTime);
                    }
                    break;
                case 0:
                    break;
            }
        }
    };

    private void TimerFourStart() {
        MyLog.i(TAG, "定时器4=打开");
        FourNowCount = 0;
        TimerFourIsStop = false;
        TimerFourHandler.sendEmptyMessage(1);
    }

    private void TimerFourStop() {
        MyLog.i(TAG, "定时器4=关闭");
        FourNowCount = 0;
        TimerFourIsStop = true;
        TimerFourHandler.sendEmptyMessage(0);
    }


    //======== 第5个定时器====================判断超时-补发定时器
    private int HeadDelayFiveTime = 500;
    private int FiveNowCount = 0;
    private int FiveMaxCount = 4;

    private boolean TimerFiveIsStop = false;
    private Handler TimerFiveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

                    MyLog.i(TAG, "定时器5=TimerFiveIsStop = " + TimerFiveIsStop);

                    // 添加更新ui的代码
                    if (!TimerFiveIsStop) {

                        MyLog.i(TAG, "定时器5 FiveNowCount = " + FiveNowCount);

                        if (FiveNowCount > FiveMaxCount) {
                            TimerFiveStop();
                            MyLog.i(TAG, "定时器5 检查补发数据是否发完");
                            checkReplacmentData();
                        }
                        FiveNowCount++;
                        TimerFiveHandler.sendEmptyMessageDelayed(1, HeadDelayFiveTime);
                    }
                    break;
                case 0:
                    break;
            }
        }
    };

    private void TimerFiveStart() {
        MyLog.i(TAG, "定时器5=打开");
        FiveNowCount = 0;
        TimerFiveIsStop = false;
        TimerFiveHandler.sendEmptyMessage(1);
    }

    private void TimerFiveStop() {
        MyLog.i(TAG, "定时器5=关闭");
        FiveNowCount = 0;
        TimerFiveIsStop = true;
        TimerFiveHandler.sendEmptyMessage(0);
    }


    private AlertDialog loading_dialog;
    private ProgressBar send_loading_progressbar;
    private RelativeLayout rl_theme_type1;
    private RelativeLayout rl_theme_type2;
    private ImageView send_loading_img_bg_type1, send_loading_img_text_type1;
    private ImageView send_loading_img_bg_type2, send_loading_img_text_type2, send_loading_cover_img_type2;
    private TextView end_loading_text;


    //发送数据提示框
    private void initLoadingdialog() {
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
        send_loading_cover_img_type2 = (ImageView) view.findViewById(R.id.send_loading_cover_img_type2);
        send_loading_progressbar.setMax(mThemeModle.getTotalPageSize());

        if (mBleDeviceTools.get_device_theme_shape() == 2) {
            send_loading_cover_img_type2.setVisibility(View.VISIBLE);
        } else {
            send_loading_cover_img_type2.setVisibility(View.GONE);
        }

        if (UiType == 1) {
            rl_theme_type1.setVisibility(View.VISIBLE);
            rl_theme_type2.setVisibility(View.GONE);
            if (isCustom) {
                send_loading_img_bg_type1.setImageBitmap(NowBgBitmap);
                send_loading_img_text_type1.setImageBitmap(NowTextBitmap);
            } else {
                new BitmapUtils(mContext).display(send_loading_img_bg_type1, nowImgUrl);
            }
        } else {
            rl_theme_type1.setVisibility(View.GONE);
            rl_theme_type2.setVisibility(View.VISIBLE);
            if (isCustom) {
                send_loading_img_bg_type2.setImageBitmap(NowBgBitmap);
                send_loading_img_text_type2.setImageBitmap(NowTextBitmap);
            } else {
                new BitmapUtils(mContext).display(send_loading_img_bg_type2, nowImgUrl);
            }
        }

        builder.setView(view);
//        builder.setTitle(getString(R.string.send_loading));
        loading_dialog = builder.show();
        loading_dialog.setCancelable(false);

    }


    //==================成功处理
    private void handSuccessState() {

        is_send_data = false;

        TimerTwoStop();
        TimerThreeStop();
        TimerFourStop();
        TimerFiveStop();
        if (loading_dialog != null && loading_dialog.isShowing()) {
            loading_dialog.dismiss();
            Toast.makeText(ClockDialSendDataActivity.this, getText(R.string.send_success), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    //==================失败处理
    private void handFailState(boolean is_finish) {

        is_send_data = false;

        waitDialog.close();
        TimerTwoStop();
        TimerThreeStop();
        TimerFourStop();
        TimerFiveStop();
        if (is_finish) {
            AppUtils.showToast(mContext, R.string.no_connection_notification);
            if (loading_dialog != null && loading_dialog.isShowing()) {
                loading_dialog.dismiss();
            }
            finish();
        } else {
            AppUtils.showToast(mContext, R.string.send_fail);
            if (loading_dialog != null && loading_dialog.isShowing()) {
                loading_dialog.dismiss();
            }
        }
    }

    //==================失败处理
    private void handFailStateFialCode(int fail_code) {
        is_send_data = false;
        waitDialog.close();
        TimerTwoStop();
        TimerThreeStop();
        TimerFourStop();
        TimerFiveStop();


        if (loading_dialog != null && loading_dialog.isShowing()) {
            loading_dialog.dismiss();
        }

        if (!is_send_fial) {
            AppUtils.showToast(mContext, R.string.send_fail);
        }

        is_send_fial = true;
    }


}
