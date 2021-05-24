package com.zjw.apps3pluspro.module.device;import android.Manifest;import android.app.Dialog;import android.bluetooth.BluetoothAdapter;import android.bluetooth.BluetoothDevice;import android.content.BroadcastReceiver;import android.content.Context;import android.content.DialogInterface;import android.content.Intent;import android.content.IntentFilter;import android.content.pm.PackageManager;import android.content.res.Resources;import android.location.LocationManager;import android.net.Uri;import android.os.Handler;import android.provider.Settings;import android.util.Log;import android.view.View;import android.view.animation.Animation;import android.view.animation.LinearInterpolator;import android.view.animation.RotateAnimation;import android.widget.AdapterView;import android.widget.AdapterView.OnItemClickListener;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.ListView;import android.widget.RadioGroup;import android.widget.RelativeLayout;import android.widget.TextView;import androidx.annotation.Nullable;import androidx.core.app.ActivityCompat;import androidx.core.content.ContextCompat;import com.zjw.apps3pluspro.R;import com.zjw.apps3pluspro.adapter.LeDeviceListAdapter;import com.zjw.apps3pluspro.application.BaseApplication;import com.zjw.apps3pluspro.base.BaseActivity;import com.zjw.apps3pluspro.bleservice.BleService;import com.zjw.apps3pluspro.bleservice.BleTools;import com.zjw.apps3pluspro.bleservice.BroadcastTools;import com.zjw.apps3pluspro.bleservice.BtSerializeation;import com.zjw.apps3pluspro.eventbus.BlueToothStateEvent;import com.zjw.apps3pluspro.eventbus.BluetoothAdapterStateEvent;import com.zjw.apps3pluspro.eventbus.tools.EventTools;import com.zjw.apps3pluspro.module.device.entity.DeviceModel;import com.zjw.apps3pluspro.bleservice.BleConstant;import com.zjw.apps3pluspro.bleservice.scan.BaseBleScanner;import com.zjw.apps3pluspro.bleservice.scan.NordicsemiBleScanner;import com.zjw.apps3pluspro.bleservice.scan.MyBleScanState;import com.zjw.apps3pluspro.bleservice.scan.SimpleScanCallback;import com.zjw.apps3pluspro.utils.AppUtils;import com.zjw.apps3pluspro.utils.AuthorityManagement;import com.zjw.apps3pluspro.utils.Constants;import com.zjw.apps3pluspro.utils.DialogUtils;import com.zjw.apps3pluspro.utils.MyUtils;import com.zjw.apps3pluspro.utils.PageManager;import com.zjw.apps3pluspro.utils.SysUtils;import com.zjw.apps3pluspro.utils.log.MyLog;import org.greenrobot.eventbus.Subscribe;import org.greenrobot.eventbus.ThreadMode;import java.util.List;import butterknife.OnClick;import no.nordicsemi.android.support.v18.scanner.ScanResult;/** * 搜索蓝牙类 */public class ScanDeviceActivity extends BaseActivity {    private final String TAG = ScanDeviceActivity.class.getSimpleName();    //列表    private ListView bleconnect_device_list;    private LeDeviceListAdapter mLeDeviceListAdapter;    private boolean isBLEScanning = false;    private Handler ScanfHandler;    private Handler dialogHandler;    private Handler mHandler;    //适配器    public BluetoothAdapter mBluetoothAdapter = null;    private ImageView pro_scanf_device_state;    private TextView tv_scarch_title, tv_scarch_content;    private LinearLayout ll_scarch_help, layoutQrCode, layoutScan;    private RelativeLayout rl_search_no_device;    private Dialog progressDialog;    private TextView tvLeft, tvRight;    private View viewRight, viewLeft;    private ImageView ivE08, ivW004;    @Override    protected int setLayoutId() {        return R.layout.activity_search_device;    }    @Override    protected void initViews() {        super.initViews();        setTvTitle(R.string.add_device);        EventTools.SafeRegisterEventBus(this);        BaseApplication.isScanActivity = true;        ScanfHandler = new Handler();        dialogHandler = new Handler();        mHandler = new Handler();        ivE08 = (ImageView) findViewById(R.id.ivE08);        ivW004 = (ImageView) findViewById(R.id.ivW004);        ivE08.setVisibility(View.GONE);        ivW004.setVisibility(View.GONE);        filterName = getIntent().getStringExtra("type");        if (filterName == null || filterName.equalsIgnoreCase("")) {            filterName = BleConstant.PLUS_HR;        }        switch (filterName) {            case BleConstant.PLUS_HR:                filterType = BleConstant.E07;                break;            case BleConstant.PLUS_Vibe:                filterType = BleConstant.E08;                ivE08.setVisibility(View.VISIBLE);                break;            case BleConstant.PLUS_Vibe_Pro:                filterType = BleConstant.W004;                ivW004.setVisibility(View.VISIBLE);                break;            default:                filterName = BleConstant.PLUS_HR;                filterType = BleConstant.E07;                break;        }        initView();        initSetAdapter();        baseBleScanner = new NordicsemiBleScanner(this, simpleScanCallback);        tvLeft = (TextView) findViewById(R.id.tvLeft);        tvLeft.setText(getResources().getString(R.string.qr_code_title));        tvRight = (TextView) findViewById(R.id.tvRight);        tvRight.setText(getResources().getString(R.string.qr_code_manual));        layoutQrCode = findViewById(R.id.layoutQrCode);        layoutScan = findViewById(R.id.layoutScan);        viewLeft = findViewById(R.id.viewLeft);        viewRight = findViewById(R.id.viewRight);        RadioGroup rgTitle = findViewById(R.id.rgTitle);        rgTitle.setOnCheckedChangeListener((group, checkedId) -> {            switch (checkedId) {                case R.id.rbLeft:                    updateUi(0);                    scanLeDevice(false);                    break;                case R.id.rbRight:                    updateUi(1);                    scanQRMac = "";                    newStartDeviceSanf(false);                    break;            }        });        updateUi(0);        rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);        LinearInterpolator lin = new LinearInterpolator();        rotate.setInterpolator(lin);        rotate.setDuration(2000);// 设置动画持续周期        rotate.setRepeatCount(-1);// 设置重复次数        rotate.setFillAfter(true);// 动画执行完后是否停留在执行完的状态        rotate.setStartOffset(10);// 执行前的等待时间    }    RotateAnimation rotate;    @Override    protected void initDatas() {        super.initDatas();        IntentFilter intentFilter = new IntentFilter();        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);        registerReceiver(mBroadcastReceiver, intentFilter);    }    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {        @Override        public void onReceive(Context context, Intent intent) {            final String action = intent.getAction();            if (action == null) {                return;            }            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {                    finish();                }            }        }    };    private int curUi = 0;    private void updateUi(int item) {        curUi = item;        tvLeft.setTextColor(this.getResources().getColor(R.color.white_50));        tvRight.setTextColor(this.getResources().getColor(R.color.white_50));        viewLeft.setVisibility(View.INVISIBLE);        viewRight.setVisibility(View.INVISIBLE);        layoutScan.setVisibility(View.GONE);        layoutQrCode.setVisibility(View.GONE);        switch (item) {            case 0:                tvLeft.setTextColor(this.getResources().getColor(R.color.white));                viewLeft.setBackground(getResources().getDrawable(R.mipmap.clock_index_bg));                viewLeft.setVisibility(View.VISIBLE);                layoutQrCode.setVisibility(View.VISIBLE);                break;            case 1:                tvRight.setTextColor(this.getResources().getColor(R.color.white));                viewRight.setBackground(getResources().getDrawable(R.mipmap.clock_index_bg));                viewRight.setVisibility(View.VISIBLE);                layoutScan.setVisibility(View.VISIBLE);                break;        }    }    private void startAnimation() {        pro_scanf_device_state.setBackground(getResources().getDrawable(R.mipmap.sync_image_white));        pro_scanf_device_state.startAnimation(rotate);    }    private void initView() {        bleconnect_device_list = (ListView) findViewById(R.id.bleconnect_device_list);        // 蓝牙适配器        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();        pro_scanf_device_state = findViewById(R.id.pro_scanf_device_state);        tv_scarch_title = (TextView) findViewById(R.id.tv_scarch_title);        tv_scarch_content = (TextView) findViewById(R.id.tv_scarch_content);        ll_scarch_help = (LinearLayout) findViewById(R.id.ll_scarch_help);        rl_search_no_device = (RelativeLayout) findViewById(R.id.rl_search_no_device);    }    @OnClick({R.id.pro_scanf_device_state, R.id.llScanfDevice, R.id.btn_restart, R.id.btn_nobing, R.id.ll_scarch_help, R.id.btNext})    public void onViewClicked(View view) {        switch (view.getId()) {            case R.id.pro_scanf_device_state:            case R.id.llScanfDevice:            case R.id.btn_restart:                if (isBLEScanning) {                    SysUtils.logContentI(TAG, " scanning and  return");                    return;                }                newStartDeviceSanf(true);                break;            case R.id.btn_nobing:                finish();                break;            //连接问题            case R.id.ll_scarch_help:                Intent intent = new Intent(context, BleConnectProblemActivity.class);                startActivity(intent);                break;            case R.id.btNext:                //动态权限申请                if (ContextCompat.checkSelfPermission(ScanDeviceActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {                    ActivityCompat.requestPermissions(ScanDeviceActivity.this, new String[]{Manifest.permission.CAMERA}, 1);                    SysUtils.logAppRunning(TAG, "requestPermissions permission.CAMERA");                } else {                    scanQRMac = "";                    if (isStartScanQrCode) {                        return;                    }                    isStartScanQrCode = true;                    startActivityForResult(new Intent(context, ScanQrCodeActivity.class), 0x01);                }                break;        }    }    private boolean isStartScanQrCode = false;    @Override    protected void onResume() {        super.onResume();        isStartScanQrCode = false;    }    private void initSetAdapter() {        mLeDeviceListAdapter = new LeDeviceListAdapter(context);        bleconnect_device_list.setAdapter(mLeDeviceListAdapter);        bleconnect_device_list.setOnItemClickListener(new OnItemClickListener() {            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {                DeviceModel device = mLeDeviceListAdapter.getDevice(arg2);                connectDevice(device);                showDialog();            }        });    }    private void connectDevice(DeviceModel deviceModel) {        if (isConnect) {            return;        }        SysUtils.logAppRunning(TAG, "scan size = " + mLeDeviceListAdapter.getCount());        SysUtils.logAppRunning(TAG, "connectDevice = " + " address = " + deviceModel.address + " name = " + deviceModel.name);        scanLeDevice(false);        isBindTimeOut = false;        device = deviceModel;        BleService.curBleAddress = deviceModel.address;        BleService.curBleName = deviceModel.name;        SysUtils.logContentI(TAG, "onItemClick = BleService.curBleAddress = " + BleService.curBleAddress);        SysUtils.logContentI(TAG, "onItemClick = BleService.curBleName = " + BleService.curBleName);        Intent intent = new Intent();        intent.setAction(BroadcastTools.ACTION_RESULT_BLE_DEVICE_ACTION);        intent.putExtra(BroadcastTools.BLE_DEVICE, deviceModel);        ScanDeviceActivity.this.sendBroadcast(intent);        isConnect = true;    }    private boolean isConnect = false;    @Subscribe(threadMode = ThreadMode.MAIN)    public void bluetoothAdapterStateEvent(BluetoothAdapterStateEvent event) {        if (event.state == BluetoothAdapter.STATE_ON) {            newStartDeviceSanf(true);        }    }    @Subscribe(threadMode = ThreadMode.MAIN)    public void blueToothStateEvent(BlueToothStateEvent event) {        if (isBindTimeOut) return;        if (!isConnect) return;        if (!progressDialog.isShowing()) {            showDialog();        }        switch (event.state) {            case BleConstant.STATE_CONNECTING:                msg.setText(getResources().getString(R.string.scan_binging));                break;            case BleConstant.STATE_DISCONNECTED://                msg.setText(getResources().getString(R.string.scan_bing_fail));//                dialogHandler.postDelayed(new Runnable() {//                    @Override//                    public void run() {//                        if (progressDialog.isShowing()) {//                            progressDialog.dismiss();//                        }//                    }//                }, 1000);                break;            case BleConstant.STATE_CONNECTED_TIMEOUT:            case BleConstant.STATE_BIND_ERROR:                mHandler.removeCallbacksAndMessages(null);                bindError();                break;            case BleConstant.STATE_CONNECTED:                try {                    if (MyUtils.BinstrToIntArray(BleTools.hexString2bytes(device.serviceDataString)[3])[1] == 1) { // 有绑定功能                        mHandler.postDelayed(runnableTime, timeOut);                    }                } catch (NumberFormatException e) {                    e.printStackTrace();                }                break;            case BleConstant.STATE_DISCOVER_SERVICES:                try {                    if (device.serviceDataString != null) {                        byte[] data = BleTools.hexString2bytes(device.serviceDataString);                        int[] deviceParams = MyUtils.BinstrToIntArray(data[3]);                        if (deviceParams[1] == 1) { // 有绑定功能                            //                    mHandler.postDelayed(runnableTime, timeOut);                            sendData(BtSerializeation.bindDevice(0));                        } else {                            initDeviceCmd();                        }                    } else {                        initDeviceCmd();                    }                } catch (Exception e) {                    e.printStackTrace();                }                break;            case BleConstant.STATE_BIND_SUCCESS:                mHandler.removeCallbacksAndMessages(null);                initDeviceCmd();                SysUtils.logAppRunning(TAG, "bindDevice success");                break;            default:                break;        }    }    private void bindError() {        try {            isConnect = false;            if (ivSyncWhite != null) {                ivSyncWhite.clearAnimation();            }            msg.setText(getResources().getString(R.string.scan_bing_fail));            ivSyncWhite.setBackground(getResources().getDrawable(R.mipmap.bind_error));            dialogHandler.postDelayed(() -> {                if (progressDialog.isShowing()) {                    progressDialog.dismiss();                }            }, 2000);        } catch (Resources.NotFoundException e) {            e.printStackTrace();        }    }    private boolean isBindTimeOut = false;    private int timeOut = 20 * 1000;    Runnable runnableTime = () -> {        SysUtils.logAppRunning(TAG, "bindDevice time out");        Log.w("ble", " bindDevice time out");        mHandler.removeCallbacksAndMessages(null);        isBindTimeOut = true;        disconnect();        DeviceManager.getInstance().unBind(this, null);        bindError();    };    private void initDeviceCmd() {        try {            startInitDeviceCmd();            msg.setText(getResources().getString(R.string.scan_bing_success));            if (ivSyncWhite != null) {                ivSyncWhite.clearAnimation();            }            ivSyncWhite.setBackground(getResources().getDrawable(R.mipmap.bind_success));            PageManager.getInstance().cleanList();            dialogHandler.postDelayed(() -> {                if (progressDialog.isShowing()) {                    progressDialog.dismiss();                }                isBindSuccess = true;                finish();            }, 1000);        } catch (Resources.NotFoundException e) {            e.printStackTrace();        }    }    TextView msg;    ImageView ivSyncWhite;    private void showDialog() {        progressDialog = new Dialog(ScanDeviceActivity.this, R.style.progress_dialog);        progressDialog.setContentView(R.layout.connect_layout);        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);        msg = (TextView) progressDialog.findViewById(R.id.tvLoading);        ivSyncWhite = progressDialog.findViewById(R.id.ivSyncWhite);        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);        LinearInterpolator lin = new LinearInterpolator();        rotate.setInterpolator(lin);        rotate.setDuration(2000);// 设置动画持续周期        rotate.setRepeatCount(-1);// 设置重复次数        rotate.setFillAfter(true);// 动画执行完后是否停留在执行完的状态        rotate.setStartOffset(10);// 执行前的等待时间        ivSyncWhite.setAnimation(rotate);        msg.setText(getResources().getString(R.string.scan_binging));        progressDialog.setCancelable(false);        progressDialog.show();        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {            @Override            public void onDismiss(DialogInterface dialog) {                isConnect = false;            }        });    }    @Override    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {        super.onActivityResult(requestCode, resultCode, data);        if (requestCode == 0x01 && resultCode == RESULT_OK) {            try {                String content = data.getStringExtra("content");                SysUtils.logAppRunning(TAG, "onActivityResult scanQR = " + content);                scanQRMac = content.replace(filterType, "");                boolean isTrue = true;                switch (filterType) {                    case BleConstant.E07:                        if (!filterName.equalsIgnoreCase(BleConstant.PLUS_HR)) {                            AppUtils.showToast(context, R.string.scan_bing_fail);                            isTrue = false;                        }                        break;                    case BleConstant.E08:                        if (!filterName.equalsIgnoreCase(BleConstant.PLUS_Vibe)) {                            AppUtils.showToast(context, R.string.scan_bing_fail);                            isTrue = false;                        }                        break;                    case BleConstant.W004:                        if (!filterName.equalsIgnoreCase(BleConstant.PLUS_Vibe_Pro)) {                            AppUtils.showToast(context, R.string.scan_bing_fail);                            isTrue = false;                        }                        break;                }                if (isTrue) {                    showDialog();                    if(progressDialog != null){                        progressDialog.setCancelable(true);                    }                    newStartDeviceSanf(false);                }            } catch (Exception e) {                e.printStackTrace();                AppUtils.showToast(context, R.string.scan_bing_fail);            }        }    }    private String filterName = "";    private String filterType = "";    private String scanQRMac = "";    private DeviceModel device;    private SimpleScanCallback simpleScanCallback = new SimpleScanCallback() {        @Override        public void onBleScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {        }        @Override        public void onBleScan(List<ScanResult> results) {            MyLog.i(TAG, "onBleScan results size = " + results.size());            if (isBLEScanning) {                for (final ScanResult result : results) {                    final DeviceModel device = new DeviceModel(result);                    String deviceName = device.name;                    if (deviceName != null && deviceName.length() > 0) {                        if (MyUtils.checkBleName(deviceName)) {                            if (deviceName.startsWith(Constants.REDMI_BAND)                                    || deviceName.startsWith(Constants.ZI_LONG)                                    || deviceName.startsWith(Constants.REDMI_SMART_BAND)                                    || deviceName.startsWith(Constants.MI_SMART_BAND)                                    || deviceName.startsWith(Constants.zzLong_Band)                                    || deviceName.startsWith(Constants.redmi1)                                    || deviceName.startsWith(Constants.Redmi2)                                    || deviceName.startsWith(Constants.Redmi3)                            ) {                                continue;                            }                            if(deviceName.length() > 5){                                deviceName = deviceName.substring(0, deviceName.length() - 5);                            }                            if (deviceName.equals(filterName)) {                                MyLog.i(TAG, "onBleScan results getServiceDataString = " + device.serviceDataString);                                if (device.serviceDataString != null) {                                    byte[] data = BleTools.hexString2bytes(device.serviceDataString);                                    int[] deviceParams = MyUtils.BinstrToIntArray(data[3]);                                    if (deviceParams[0] == 0) { // 未绑定                                        mLeDeviceListAdapter.addDevice(device);                                        if (device.address.replace(":", "").equalsIgnoreCase(scanQRMac)) {                                            connectDevice(device);                                        }                                    }                                }                            }                        }                    }                }                if (mLeDeviceListAdapter != null) {                    mLeDeviceListAdapter.MyDevicesort();                    mLeDeviceListAdapter.notifyDataSetChanged();                }            }        }        @Override        public void onBleScanStop(MyBleScanState scanState) {//扫描异常停止        }    };    private BaseBleScanner baseBleScanner;    private void scanLeDevice(final boolean enable) {        if (enable) {            if (!this.mBluetoothAdapter.isEnabled()) {                this.mBluetoothAdapter.enable();            } else {                ScanfHandler.removeCallbacksAndMessages(null);                // Stops scanning after a pre-defined scan period.                ScanfHandler.postDelayed(new Runnable() {                    @Override                    public void run() {                        MyLog.i(TAG, "停止扫描=");                        stopSCanf();                        baseBleScanner.onStopBleScan();                        if (curUi == 0) {                            if (!mBluetoothAdapter.isEnabled()) {                                mBluetoothAdapter.enable();                            }                            newStartDeviceSanf(false);                        }                    }                }, Constants.SCAN_PERIOD_SEARCH_DEVICE_ACTIVITY);                // 正在扫描                startSCanf();                startAnimation();                baseBleScanner.onStartBleScan();            }        } else {            ScanfHandler.removeCallbacksAndMessages(null);            stopSCanf();            baseBleScanner.onStopBleScan();        }    }    /**     * 开始搜索     */    void startSCanf() {        mLeDeviceListAdapter.clear();        mLeDeviceListAdapter.notifyDataSetChanged();        isBLEScanning = true;        tv_scarch_title.setText(getText(R.string.device_scanf_loading));        tv_scarch_content.setText(getText(R.string.device_scanf_near));        ll_scarch_help.setVisibility(View.GONE);        rl_search_no_device.setVisibility(View.GONE);    }    /**     * 停止搜索     */    void stopSCanf() {        isBLEScanning = false;        pro_scanf_device_state.clearAnimation();        pro_scanf_device_state.setBackground(getResources().getDrawable(R.mipmap.icon_complete));        if (mLeDeviceListAdapter.getCount() > 0) {            tv_scarch_title.setText(getText(R.string.device_scanf_end));            tv_scarch_content.setText(getText(R.string.device_scanf_touch_connect));            ll_scarch_help.setVisibility(View.GONE);            rl_search_no_device.setVisibility(View.GONE);        } else {            tv_scarch_title.setText(getText(R.string.device_scanf_no));            tv_scarch_content.setText(getText(R.string.device_scanf_touch_connect));            ll_scarch_help.setVisibility(View.VISIBLE);            rl_search_no_device.setVisibility(View.VISIBLE);        }    }    @Override    protected void onStart() {        // TODO Auto-generated method stub        super.onStart();    }    boolean isBindSuccess = false;    @Override    protected void onDestroy() {        EventTools.SafeUnregisterEventBus(this);        unregisterReceiver(mBroadcastReceiver);        scanLeDevice(false);        if (ScanfHandler != null) {            ScanfHandler.removeCallbacksAndMessages(null);        }        if (dialogHandler != null) {            dialogHandler.removeCallbacksAndMessages(null);        }        if (mHandler != null) {            mHandler.removeCallbacksAndMessages(null);        }        clearConnectInfo();        BaseApplication.isScanActivity = false;        if (!isBindSuccess) {            disconnect();            DeviceManager.getInstance().unBind(this, null);        }        super.onDestroy();    }    // gps是否可用    public static boolean isGpsEnable(final Context context) {        LocationManager locationManager                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);        if (gps || network) {            return true;        }        return false;    }    @Override    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {        switch (requestCode) {            case AuthorityManagement.REQUEST_EXTERNAL_LOCATION:                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {                    MyLog.i(TAG, "获取定位权限 回调允许");//                    newStartDeviceSanf(true);                    if (MyUtils.isGPSOpen(ScanDeviceActivity.this)) {                        scanLeDevice(true);                    } else {                        DialogUtils.showSettingGps(this);                    }                } else {                    MyLog.i(TAG, "获取定位权限 回调拒绝");                    showSettingDialog(getString(R.string.setting_dialog_location));                }                break;        }    }    void showSettingDialog(String content) {        DialogUtils.BaseDialog(context,                context.getResources().getString(R.string.dialog_prompt),                content,                context.getDrawable(R.drawable.black_corner_bg),                new DialogUtils.DialogClickListener() {                    @Override                    public void OnOK() {                        Intent intent = new Intent();                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);                        Uri uri = Uri.fromParts("package", getPackageName(), null);                        intent.setData(uri);                        startActivity(intent);                    }                    @Override                    public void OnCancel() {                    }                }                , getString(R.string.setting_dialog_setting));    }    void newStartDeviceSanf(boolean isStop) {        if (isStop) {            scanLeDevice(false);        }        if (AuthorityManagement.verifyLocation(ScanDeviceActivity.this)) {            if (MyUtils.isGPSOpen(ScanDeviceActivity.this)) {                scanLeDevice(true);            } else {                DialogUtils.showSettingGps(this);            }        } else {            MyLog.i(TAG, "获取定位权限 未授权");        }    }}