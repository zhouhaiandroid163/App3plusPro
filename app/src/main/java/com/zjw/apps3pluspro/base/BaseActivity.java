package com.zjw.apps3pluspro.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.bleservice.BleService;
import com.zjw.apps3pluspro.module.device.entity.ThemeModle;
import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.location.ForegroundLocationService;
import com.zjw.ffitsdk.BleProtocol;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends FragmentActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private Unbinder bind;
    public Context context;
    BleProtocol mBleProtocol;

    public boolean isTextDark = false;
    public int bgColor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = setLayoutId();
        if (bgColor != 0) {
            AppUtils.setStatusBarMode(this, isTextDark, bgColor);
        } else {
            AppUtils.setStatusBarMode(this, isTextDark, R.color.base_activity_bg);
        }
        View v = getLayoutInflater().inflate(id, null);
        setContentView(v);
        context = this;
        initTitleBar(v);

        bind = ButterKnife.bind(this);
        mBleProtocol = new BleProtocol(this);

        initViews();
        initDatas();
    }

    TextView tvTitle;

    private void initTitleBar(View v) {
        tvTitle = v.findViewById(R.id.public_head_title);
        LinearLayout public_head_back = v.findViewById(R.id.public_head_back);
        if (public_head_back != null) {
            public_head_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finishListener != null) {
                        finishListener.onFinish();
                    }
                    finish();
                }
            });
        }
    }

    protected void setTvTitle(int title) {
        if (tvTitle != null) {
            tvTitle.setText(getResources().getString(title));
        }
    }

    protected abstract int setLayoutId();

    protected void initViews() {
    }

    protected void initDatas() {
    }

    @Override
    protected void onDestroy() {
        if (bind != null) {
            bind.unbind();
        }
        super.onDestroy();
    }

    public interface FinishListener {
        void onFinish();
    }

    private FinishListener finishListener;

    public void setFinishListener(FinishListener finishListener) {
        this.finishListener = finishListener;
    }

    private void startService(Bundle bundle) {
        Intent gattServiceIntent = new Intent(this, BleService.class);
        gattServiceIntent.putExtras(bundle);
        gattServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName name = startService(gattServiceIntent);
    }

    public void reconnectDevice() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "reconnectDevice");
        startService(bundle);
    }

    public void setDeviceErrorLog() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "setDeviceErrorLog");
        startService(bundle);
    }

    public void getDeviceErrorLog() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "getDeviceErrorLog");
        startService(bundle);
    }

    public void bindDeviceConnect(String address) {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "bindDeviceConnect");
        bundle.putString("address", address);
        startService(bundle);
    }

    public void createBond() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "createBond");
        startService(bundle);
    }

    public void closePhoto() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "closePhoto");
        startService(bundle);
    }

    public void writeRXCharacteristic(byte[] params) {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "writeRXCharacteristic");
        bundle.putByteArray("params", params);
        startService(bundle);
    }

    public void disconnect() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "disconnect");
        startService(bundle);
    }

    public void endSleepTag() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "endSleepTag");
        startService(bundle);
    }

    public void syncTime() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "syncTime");
        startService(bundle);
    }

    public void openPhoto() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "openPhoto");
        startService(bundle);
    }

    public void clearConnectInfo() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "clearConnectInfo");
        startService(bundle);
    }

    public void enableNotifacationThemeRead() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "enableNotifacationThemeRead");
        startService(bundle);
    }


    public void sendThemeData(int SnNum, byte[] send_data) {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "sendThemeData");
        bundle.putInt("SnNum", SnNum);
        bundle.putByteArray("send_data", send_data);
        startService(bundle);
    }

    public void sendThemeBlockVerfication() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "sendThemeBlockVerfication");
        startService(bundle);
    }

    public void cleanMailList() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "cleanMailList");
        startService(bundle);
    }

    public double handlerEcg(Handler mHandler, int end, boolean isLeft) {
        return mBleProtocol.HandlerEcg(end, mHandler, isLeft);
    }


    public void sendMusicHead(ThemeModle mThemeModle, String fileName) {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "sendMusicHead");
        bundle.putInt("total_page", mThemeModle.getData().length);
        bundle.putInt("mtu", mThemeModle.getMTU());
        bundle.putInt("block_size", mThemeModle.getBlockSize());
        bundle.putString("fileName", fileName);
        startService(bundle);
    }

    public void sendThemeHead(ThemeModle mThemeModle) {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "sendThemeHead");
        bundle.putInt("total_page", mThemeModle.getData().length);
        bundle.putInt("mtu", mThemeModle.getMTU());
        bundle.putInt("block_size", mThemeModle.getBlockSize());
        startService(bundle);
    }

    public void sendMailListHead(ThemeModle mThemeModle, int crc) {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "sendMailListHead");
        bundle.putInt("total_page", mThemeModle.getData().length);
        bundle.putInt("mtu", mThemeModle.getMTU());
        bundle.putInt("block_size", mThemeModle.getBlockSize());
        bundle.putInt("crc", crc);
        startService(bundle);
    }

    public void setDeviceScreensaverInfo() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "setDeviceScreensaverInfo");
        startService(bundle);
    }

    public void getDeviceScreensaverInfo() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "getDeviceScreensaverInfo");
        startService(bundle);
    }

    public void sendImageHead(int OutputX, int OutputY, byte crc) {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "sendImageHead");
        bundle.putInt("OutputX", OutputX);
        bundle.putInt("OutputY", OutputY);
        bundle.putByte("crc", crc);
        startService(bundle);
    }

    public void send_image_data(byte[] crc) {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "send_image_data");
        bundle.putByteArray("crc", crc);
        startService(bundle);
    }

    public void enableNotifacationEcgRead() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "enableNotifacationEcgRead");
        startService(bundle);
    }

    public void enableNotifacationPpgRead() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "enableNotifacationPpgRead");
        startService(bundle);
    }

    public void setEcgMeasure(boolean is_ecg_measure) {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "setEcgMeasure");
        bundle.putBoolean("is_ecg_measure", is_ecg_measure);
        startService(bundle);
    }

    public double HandlerPpg(int end, boolean isLeft) {
        return mBleProtocol.HandlerPpg(end, isLeft);
    }

    public void initEcgData() {
        mBleProtocol.initEcgData();
    }

    public void initPpgData() {
        mBleProtocol.initPpgData();
    }

    public void openEcg() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "openEcg");
        startService(bundle);
    }

    public void closeEcg() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "closeEcg");
        startService(bundle);
    }

    public int getEcgHr() {
        return mBleProtocol.getEcgHr();
    }

    public void ResultCalibrationHeart() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "ResultCalibrationHeart");
        startService(bundle);
    }

    public int getEcgPtpAvg() {
        return mBleProtocol.getEcgPtpAvg();
    }

    //上传健康数据
    public void ResultMeasureHeart(HealthInfo mHealthInfo) {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "ResultMeasureHeart");
        bundle.putParcelable("HealthInfo", mHealthInfo);
        startService(bundle);
    }

    public void getDeviceInfo() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "getDeviceInfo");
        startService(bundle);
    }

    public void restore_factory() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "restore_factory");
        startService(bundle);
    }

    public void setDeviceCycleInfo() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "setDeviceCycleInfo");
        startService(bundle);
    }

    public void getConnectStatus() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "getConnectStatus");
        startService(bundle);
    }

    public void sendData(byte[] params) {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "send");
        bundle.putByteArray("params", params);
        startService(bundle);
    }

    public void sendAppStart(byte[] params) {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "proto");
        bundle.putByteArray("params", params);
        startService(bundle);
    }

    public void sendProtoUpdateData(byte[] params) {
//        Bundle bundle = new Bundle();
//        bundle.putString("cmd", "sendProtoUpdateData");
//        bundle.putByteArray("params", params);
//        startService(bundle);

        BleService bleService = BleService.bluetoothLeService;
        bleService.writeCharacteristicProto4(params);
    }

    public void startInitDeviceCmd() {
        Bundle bundle = new Bundle();
        bundle.putString("cmd", "initDeviceCmd");
        startService(bundle);
    }

    public void startLocationService() {
        Intent intent = new Intent(this, ForegroundLocationService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    /**
     * 重写 getResource 方法，防止系统字体影响
     */
    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        if (resources != null && resources.getConfiguration().fontScale != 1) {
            Configuration configuration = resources.getConfiguration();
            configuration.fontScale = 1;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return resources;
    }
}
