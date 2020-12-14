package com.zjw.apps3pluspro.module.device.clockdial;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.adapter.HomePagerAdapter;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.device.entity.ClockDialCustomModel;
import com.zjw.apps3pluspro.network.javabean.ThemeFileBean;
import com.zjw.apps3pluspro.utils.DialogUtils;
import com.zjw.apps3pluspro.view.pager.LazyViewPager;
import com.zjw.apps3pluspro.view.pager.MyViewPager;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.bleservice.UpdateInfoService;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.AuthorityManagement;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.ThemeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页home界面
 */
public class ClockDialActivity extends BaseActivity {
    private final String TAG = ClockDialActivity.class.getSimpleName();
    private Context mContext;
    private MyActivityManager manager;
    //轻量级存储
    public BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    public int deviceWidth = 0;
    public int deviceHeight = 0;
    public int deviceShape = 0;
    public int deviceClockDialDataSize = 0;
    public boolean deviceIsHeart;
    public boolean deviceScanfTypeIsVer = false;
    public int UiType = 2;//1=长方形3列/=2正方形2列

    private MyViewPager vpMenu;
    private RadioGroup rgClockDial;
    private TextView tvMenuMarket, tvMenuCustom;
    private View viewRight, viewLeft;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_clock_dial;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = ClockDialActivity.this;
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        initBroadcast();
        initThemeSet();
        initView();
        initData();
        enableNotifacationThemeRead();
        ((TextView) (findViewById(R.id.public_head_title))).setText(getText(R.string.device_dial_center_title));
    }

    private void initThemeSet() {
        deviceWidth = mBleDeviceTools.get_device_theme_resolving_power_width();
        deviceHeight = mBleDeviceTools.get_device_theme_resolving_power_height();
        deviceShape = mBleDeviceTools.get_device_theme_shape();
        deviceClockDialDataSize = mBleDeviceTools.get_device_theme_available_space();
        deviceIsHeart = mBleDeviceTools.get_device_theme_is_support_heart();
        deviceScanfTypeIsVer = mBleDeviceTools.get_device_theme_scanning_mode();

        //模拟测试
//        device_width = 128;
//        device_height = 220;
//        device_shape = 0;
//        it_bin_size = 800;
//        device_is_heart = false;

//        device_width = 240;
//        device_height = 240;
//        device_shape = 2;
//        it_bin_size = 800;
//        device_is_heart = true;

        UiType = (deviceWidth == 128 && deviceHeight == 220) ? 1 : 2;
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
        super.onDestroy();
    }


    private void initView() {

        tvMenuMarket = (TextView) findViewById(R.id.tvMenuMarket);
        tvMenuCustom = (TextView) findViewById(R.id.tvMenuCustom);

        viewLeft = findViewById(R.id.viewLeft);
        viewRight = findViewById(R.id.viewRight);

        rgClockDial = (RadioGroup) findViewById(R.id.rgClockDial);
        rgClockDial.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    //表盘市场
                    case R.id.rbMarket:
                        vpMenu.setCurrentItem(0, false);
                        updateUi(0);
                        break;
                    //自定义表盘
                    case R.id.rbCustom:
                        vpMenu.setCurrentItem(1, false);
                        updateUi(1);
                        break;
                }
            }
        });
        vpMenu = (MyViewPager) findViewById(R.id.vpMenu);
    }


    private void initData() {
        mClockDialMarketFragment = new ClockDialMarketFragment();
        mClockDialCustomFragment = new ClockDialCustomFragment();

        List<Fragment> fragLists = new ArrayList<Fragment>();
        fragLists.add(mClockDialMarketFragment);
        fragLists.add(mClockDialCustomFragment);

        HomePagerAdapter mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), fragLists);

        vpMenu.setAdapter(mHomePagerAdapter);
        vpMenu.setOnPageChangeListener(new LazyViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                MyLog.i(TAG, "onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                MyLog.i(TAG, "onPageSelected");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                MyLog.i(TAG, "onPageScrollStateChanged");
            }
        });


        vpMenu.setCurrentItem(0, false);

        rgClockDial.check(R.id.rbMarket);

        updateUi(0);


    }


    private ClockDialMarketFragment mClockDialMarketFragment;
    private ClockDialCustomFragment mClockDialCustomFragment;

    /**
     * 初始化广播
     */
    private void initBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastTools.ACTION_GATT_DISCONNECTED);
        filter.addAction(BroadcastTools.ACTION_DOWN_CLOCK_FILE_STATE_SUCCESS);
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
                    finish();
                    break;

                case BroadcastTools.ACTION_DOWN_CLOCK_FILE_STATE_SUCCESS:
                    if (!ThemeUtils.checkFileExistenceByMd5(Constants.DOWN_THEME_FILE, CLOCK_FILE_NAME, CLOCK_THEME_MD5)) {
                        MyLog.i(TAG, "数据大小 Bytes = 文件不存在");
                    } else {
                        marketToClockDialSendActivity(ThemeUtils.getBytes(Constants.DOWN_THEME_FILE + CLOCK_FILE_NAME));
                    }
                    break;
            }
        }
    };

    private void updateUi(int item) {
        tvMenuMarket.setTextColor(this.getResources().getColor(R.color.white_50));
        tvMenuCustom.setTextColor(this.getResources().getColor(R.color.white_50));
        viewLeft.setVisibility(View.INVISIBLE);
        viewRight.setVisibility(View.INVISIBLE);
        switch (item) {
            case 0:
                tvMenuMarket.setTextColor(this.getResources().getColor(R.color.white));
                viewLeft.setBackground(getResources().getDrawable(R.mipmap.clock_index_bg));
                viewLeft.setVisibility(View.VISIBLE);
                break;
            case 1:
                tvMenuCustom.setTextColor(this.getResources().getColor(R.color.white));
                viewRight.setBackground(getResources().getDrawable(R.mipmap.clock_index_bg));
                viewRight.setVisibility(View.VISIBLE);
                break;
        }
    }


    //=================下载文件相关=====================

    private String CLOCK_FILE_NAME = "";
    private String CLOCK_THEME_MD5 = "";
    private String CLOCK_FILE_URL = "";

    public void downThemeFile(ThemeFileBean.DataBean mDataBean) {
        int theme_id = mDataBean.getThemeId();
        CLOCK_THEME_MD5 = mDataBean.getMd5Value();
        CLOCK_FILE_NAME = String.valueOf(theme_id) + "_" + CLOCK_THEME_MD5;
        CLOCK_FILE_URL = mDataBean.getThemeFileUrl();

        MyLog.i(TAG, "下载表盘文件 CLOCK_FILE_URL = " + CLOCK_FILE_URL);
        MyLog.i(TAG, "下载表盘文件 CLOCK_THEME_MD5 = " + CLOCK_THEME_MD5);
        MyLog.i(TAG, "下载表盘文件 CLOCK_FILE_NAME = " + CLOCK_FILE_NAME);

        if (AuthorityManagement.verifyStoragePermissions(ClockDialActivity.this)) {
            MyLog.i(TAG, "SD卡权限 已获取");

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                MyLog.i(TAG, "SD卡权限 支持");

                if (!ThemeUtils.checkFileExistenceByMd5(Constants.DOWN_THEME_FILE, CLOCK_FILE_NAME, CLOCK_THEME_MD5)) {
                    MyLog.i(TAG, "数据大小 Bytes = 文件不存在");
                    Dialog progressDialogDownFile;
                    progressDialogDownFile = DialogUtils.BaseDialogShowProgress(context,
                            context.getResources().getString(R.string.download_title),
                            context.getResources().getString(R.string.loading0),
                            context.getDrawable(R.drawable.black_corner_bg)
                    );
                    new UpdateInfoService(ClockDialActivity.this).downLoadNewFile(CLOCK_FILE_URL, CLOCK_FILE_NAME, Constants.DOWN_THEME_FILE, progressDialogDownFile);
                } else {
                    marketToClockDialSendActivity(ThemeUtils.getBytes(Constants.DOWN_THEME_FILE + CLOCK_FILE_NAME));
                }


            } else {
                MyLog.i(TAG, "SD卡权限 不支持");
                AppUtils.showToast(ClockDialActivity.this, R.string.sd_card);
            }

        } else {
            MyLog.i(TAG, "SD卡权限 未获取");
            AppUtils.showToast(ClockDialActivity.this, R.string.sd_card);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    /**
     * 权限-跳转设置对话框
     *
     * @param title
     */
    private void showSettingDialog(String title) {
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

    public static byte[] clockDailData = null;

    public void marketToClockDialSendActivity(byte[] bytes) {

        clockDailData = bytes;
        Intent intent = new Intent(mContext, ClockDialSendDataActivity.class);

        intent.putExtra(IntentConstants.ClockDialWidth, deviceWidth);
        intent.putExtra(IntentConstants.ClockDialHeight, deviceHeight);
        intent.putExtra(IntentConstants.ClockDialShape, deviceShape);
        intent.putExtra(IntentConstants.ClockDialDataSize, deviceClockDialDataSize);
        intent.putExtra(IntentConstants.ClockDialIsHeart, deviceIsHeart);
        intent.putExtra(IntentConstants.ClockDialisScanfTypeVer, deviceScanfTypeIsVer);
        intent.putExtra(IntentConstants.ClockDialisUiType, UiType);

        intent.putExtra(IntentConstants.IntentClockDialSendDataType, IntentConstants.ClockDialSendDataType1);
//        intent.putExtra(IntentConstants.ClockDialBinData, bytes);
        intent.putExtra(IntentConstants.ClockDialImgUrl, mClockDialMarketFragment.nowImgUrl);
        startActivity(intent);
    }


    public void customToClockDialSendActivity(ClockDialCustomModel mClockDialCustomModel) {
        Intent intent = new Intent(mContext, ClockDialSendDataActivity.class);

        intent.putExtra(IntentConstants.ClockDialWidth, deviceWidth);
        intent.putExtra(IntentConstants.ClockDialHeight, deviceHeight);
        intent.putExtra(IntentConstants.ClockDialShape, deviceShape);
        intent.putExtra(IntentConstants.ClockDialDataSize, deviceClockDialDataSize);
        intent.putExtra(IntentConstants.ClockDialIsHeart, deviceIsHeart);
        intent.putExtra(IntentConstants.ClockDialisScanfTypeVer, deviceScanfTypeIsVer);
        intent.putExtra(IntentConstants.ClockDialisUiType, UiType);

        intent.putExtra(IntentConstants.IntentClockDialSendDataType, IntentConstants.ClockDialSendDataType2);
        intent.putExtra(IntentConstants.ClockDialCustomModel, mClockDialCustomModel);
        startActivity(intent);
    }
}
