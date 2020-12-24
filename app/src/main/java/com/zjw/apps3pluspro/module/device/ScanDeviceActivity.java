package com.zjw.apps3pluspro.module.device;import android.Manifest;import android.app.Dialog;import android.bluetooth.BluetoothAdapter;import android.bluetooth.BluetoothDevice;import android.content.Context;import android.content.DialogInterface;import android.content.Intent;import android.content.pm.PackageManager;import android.location.LocationManager;import android.net.Uri;import android.os.Handler;import android.provider.Settings;import android.view.View;import android.view.animation.Animation;import android.view.animation.LinearInterpolator;import android.view.animation.RotateAnimation;import android.widget.AdapterView;import android.widget.AdapterView.OnItemClickListener;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.ListView;import android.widget.RadioGroup;import android.widget.RelativeLayout;import android.widget.TextView;import androidx.core.app.ActivityCompat;import androidx.core.content.ContextCompat;import com.zjw.apps3pluspro.R;import com.zjw.apps3pluspro.adapter.LeDeviceListAdapter;import com.zjw.apps3pluspro.application.BaseApplication;import com.zjw.apps3pluspro.base.BaseActivity;import com.zjw.apps3pluspro.bleservice.BleService;import com.zjw.apps3pluspro.bleservice.BroadcastTools;import com.zjw.apps3pluspro.eventbus.BlueToothStateEvent;import com.zjw.apps3pluspro.eventbus.BluetoothAdapterStateEvent;import com.zjw.apps3pluspro.eventbus.tools.EventTools;import com.zjw.apps3pluspro.module.device.entity.DeviceModel;import com.zjw.apps3pluspro.bleservice.BleConstant;import com.zjw.apps3pluspro.bleservice.scan.BaseBleScanner;import com.zjw.apps3pluspro.bleservice.scan.NordicsemiBleScanner;import com.zjw.apps3pluspro.bleservice.scan.MyBleScanState;import com.zjw.apps3pluspro.bleservice.scan.SimpleScanCallback;import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;import com.zjw.apps3pluspro.utils.AuthorityManagement;import com.zjw.apps3pluspro.utils.Constants;import com.zjw.apps3pluspro.utils.MyUtils;import com.zjw.apps3pluspro.utils.PageManager;import com.zjw.apps3pluspro.utils.SysUtils;import com.zjw.apps3pluspro.utils.log.MyLog;import org.greenrobot.eventbus.Subscribe;import org.greenrobot.eventbus.ThreadMode;import java.util.List;import butterknife.OnClick;import no.nordicsemi.android.support.v18.scanner.ScanResult;/** * 搜索蓝牙类 */public class ScanDeviceActivity extends BaseActivity {    private final String TAG = ScanDeviceActivity.class.getSimpleName();    //列表    private ListView bleconnect_device_list;    private LeDeviceListAdapter mLeDeviceListAdapter;    private boolean isBLEScanning = false;    private Handler ScanfHandler;    private Handler dialogHandler;    //适配器    public BluetoothAdapter mBluetoothAdapter = null;    private ImageView pro_scanf_device_state;    private TextView tv_scarch_title, tv_scarch_content;    private LinearLayout ll_scarch_help, layoutQrCode, layoutScan;    private RelativeLayout rl_search_no_device;    private Dialog progressDialog;    private TextView tvLeft, tvRight;    private View viewRight, viewLeft;    @Override    protected int setLayoutId() {        return R.layout.activity_search_device;    }    @Override    protected void initViews() {        super.initViews();        setTvTitle(R.string.add_device);        EventTools.SafeRegisterEventBus(this);        ScanfHandler = new Handler();        dialogHandler = new Handler();        initView();        initSetAdapter();        if (!this.mBluetoothAdapter.isEnabled()) {            this.mBluetoothAdapter.enable();        }        baseBleScanner = new NordicsemiBleScanner(this, simpleScanCallback);        newStartDeviceSanf(false);        initScanAnimation();//        tvLeft = (TextView) findViewById(R.id.tvLeft);//        tvLeft.setText(getResources().getString(R.string.qr_code_title));//        tvRight = (TextView) findViewById(R.id.tvRight);//        tvRight.setText(getResources().getString(R.string.qr_code_manual));////        layoutQrCode = findViewById(R.id.layoutQrCode);//        layoutScan = findViewById(R.id.layoutScan);////        viewLeft = findViewById(R.id.viewLeft);//        viewRight = findViewById(R.id.viewRight);//        RadioGroup rgTitle = findViewById(R.id.rgTitle);//        rgTitle.setOnCheckedChangeListener((group, checkedId) -> {//            switch (checkedId) {//                case R.id.rbLeft://                    updateUi(0);//                    break;//                case R.id.rbRight://                    updateUi(1);//                    break;//            }//        });//        updateUi(0);    }    private void updateUi(int item) {        tvLeft.setTextColor(this.getResources().getColor(R.color.white_50));        tvRight.setTextColor(this.getResources().getColor(R.color.white_50));        viewLeft.setVisibility(View.INVISIBLE);        viewRight.setVisibility(View.INVISIBLE);        layoutScan.setVisibility(View.GONE);        layoutQrCode.setVisibility(View.GONE);        switch (item) {            case 0:                tvLeft.setTextColor(this.getResources().getColor(R.color.white));                viewLeft.setBackground(getResources().getDrawable(R.mipmap.clock_index_bg));                viewLeft.setVisibility(View.VISIBLE);                layoutQrCode.setVisibility(View.VISIBLE);                break;            case 1:                tvRight.setTextColor(this.getResources().getColor(R.color.white));                viewRight.setBackground(getResources().getDrawable(R.mipmap.clock_index_bg));                viewRight.setVisibility(View.VISIBLE);                layoutScan.setVisibility(View.VISIBLE);                break;        }    }    private void initScanAnimation() {        pro_scanf_device_state.setBackground(getResources().getDrawable(R.mipmap.sync_image_white));        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);        LinearInterpolator lin = new LinearInterpolator();        rotate.setInterpolator(lin);        rotate.setDuration(2000);// 设置动画持续周期        rotate.setRepeatCount(-1);// 设置重复次数        rotate.setFillAfter(true);// 动画执行完后是否停留在执行完的状态        rotate.setStartOffset(10);// 执行前的等待时间        pro_scanf_device_state.setAnimation(rotate);    }    void initView() {        bleconnect_device_list = (ListView) findViewById(R.id.bleconnect_device_list);        // 蓝牙适配器        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();        pro_scanf_device_state = findViewById(R.id.pro_scanf_device_state);        tv_scarch_title = (TextView) findViewById(R.id.tv_scarch_title);        tv_scarch_content = (TextView) findViewById(R.id.tv_scarch_content);        ll_scarch_help = (LinearLayout) findViewById(R.id.ll_scarch_help);        rl_search_no_device = (RelativeLayout) findViewById(R.id.rl_search_no_device);    }    @OnClick({R.id.pro_scanf_device_state, R.id.llScanfDevice, R.id.btn_restart, R.id.btn_nobing, R.id.ll_scarch_help, R.id.btNext})    public void onViewClicked(View view) {        switch (view.getId()) {            case R.id.pro_scanf_device_state:            case R.id.llScanfDevice:            case R.id.btn_restart:                if (isBLEScanning) {                    SysUtils.logContentI(TAG, " scanning and  return");                    return;                }                newStartDeviceSanf(true);                break;            case R.id.btn_nobing:                finish();                break;            //连接问题            case R.id.ll_scarch_help:                Intent intent = new Intent(context, BleConnectProblemActivity.class);                startActivity(intent);                break;            case R.id.btNext:                //动态权限申请                if (ContextCompat.checkSelfPermission(ScanDeviceActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {                    ActivityCompat.requestPermissions(ScanDeviceActivity.this, new String[]{Manifest.permission.CAMERA}, 1);                } else {                    //扫码                    startActivityForResult(new Intent(context, ScanQrCodeActivity.class), 0x01);                }                break;        }    }    private void initSetAdapter() {        mLeDeviceListAdapter = new LeDeviceListAdapter(context);        bleconnect_device_list.setAdapter(mLeDeviceListAdapter);        bleconnect_device_list.setOnItemClickListener(new OnItemClickListener() {            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {                scanLeDevice(false);                DeviceModel device = mLeDeviceListAdapter.getDevice(arg2);                BleService.curBleAddress = device.address;                BleService.curBleName = device.name;                SysUtils.logContentI(TAG, "onItemClick = BleService.curBleAddress = " + BleService.curBleAddress);                SysUtils.logContentI(TAG, "onItemClick = BleService.curBleName = " + BleService.curBleName);                Intent intent = new Intent();                intent.setAction(BroadcastTools.ACTION_RESULT_BLE_DEVICE_ACTION);                intent.putExtra(BroadcastTools.BLE_DEVICE, device);                ScanDeviceActivity.this.sendBroadcast(intent);                showDialog();                isConnect = true;            }        });    }    private boolean isConnect = false;    @Subscribe(threadMode = ThreadMode.MAIN)    public void bluetoothAdapterStateEvent(BluetoothAdapterStateEvent event) {        if (event.state == BluetoothAdapter.STATE_ON) {            newStartDeviceSanf(true);        }    }    @Subscribe(threadMode = ThreadMode.MAIN)    public void blueToothStateEvent(BlueToothStateEvent event) {        if (!isConnect) return;        if (!progressDialog.isShowing()) {            showDialog();        }        switch (event.state) {            case BleConstant.STATE_CONNECTING:                msg.setText(getResources().getString(R.string.scan_binging));                break;            case BleConstant.STATE_DISCONNECTED://                msg.setText(getResources().getString(R.string.scan_bing_fail));//                dialogHandler.postDelayed(new Runnable() {//                    @Override//                    public void run() {//                        if (progressDialog.isShowing()) {//                            progressDialog.dismiss();//                        }//                    }//                }, 1000);                break;            case BleConstant.STATE_CONNECTED_TIMEOUT:                if (ivSyncWhite != null) {                    ivSyncWhite.clearAnimation();                }                msg.setText(getResources().getString(R.string.scan_bing_fail));                ivSyncWhite.setBackground(getResources().getDrawable(R.mipmap.bind_error));                dialogHandler.postDelayed(new Runnable() {                    @Override                    public void run() {                        if (progressDialog.isShowing()) {                            progressDialog.dismiss();                        }                    }                }, 2000);                break;            case BleConstant.STATE_CONNECTED:                msg.setText(getResources().getString(R.string.scan_bing_success));                if (ivSyncWhite != null) {                    ivSyncWhite.clearAnimation();                }                ivSyncWhite.setBackground(getResources().getDrawable(R.mipmap.bind_success));                PageManager.getInstance().cleanList();                dialogHandler.postDelayed(new Runnable() {                    @Override                    public void run() {                        if (progressDialog.isShowing()) {                            progressDialog.dismiss();                        }                        finish();                    }                }, 1000);                break;            default:                break;        }    }    TextView msg;    ImageView ivSyncWhite;    private void showDialog() {        progressDialog = new Dialog(ScanDeviceActivity.this, R.style.progress_dialog);        progressDialog.setContentView(R.layout.connect_layout);        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);        msg = (TextView) progressDialog.findViewById(R.id.tvLoading);        ivSyncWhite = progressDialog.findViewById(R.id.ivSyncWhite);        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);        LinearInterpolator lin = new LinearInterpolator();        rotate.setInterpolator(lin);        rotate.setDuration(2000);// 设置动画持续周期        rotate.setRepeatCount(-1);// 设置重复次数        rotate.setFillAfter(true);// 动画执行完后是否停留在执行完的状态        rotate.setStartOffset(10);// 执行前的等待时间        ivSyncWhite.setAnimation(rotate);        msg.setText(getResources().getString(R.string.scan_binging));        progressDialog.show();        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {            @Override            public void onDismiss(DialogInterface dialog) {                isConnect = false;            }        });    }    private SimpleScanCallback simpleScanCallback = new SimpleScanCallback() {        @Override        public void onBleScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {        }        @Override        public void onBleScan(List<ScanResult> results) {            MyLog.i(TAG, "onBleScan results size = " + results.size());            if (isBLEScanning) {                for (final ScanResult result : results) {                    final DeviceModel device = new DeviceModel(result);                    String deviceName = device.name;                    if (deviceName != null && deviceName.length() > 0) {                        if (MyUtils.checkBleName(deviceName)) {                            if (deviceName.startsWith(Constants.REDMI_BAND)                                    || deviceName.startsWith(Constants.ZI_LONG)                                    || deviceName.startsWith(Constants.REDMI_SMART_BAND)                                    || deviceName.startsWith(Constants.MI_SMART_BAND)                                    || deviceName.startsWith(Constants.zzLong_Band)                                    || deviceName.startsWith(Constants.redmi1)                                    || deviceName.startsWith(Constants.Redmi2)                                    || deviceName.startsWith(Constants.Redmi3)                            ) {                                continue;                            }                            mLeDeviceListAdapter.addDevice(device);                        }                    }                }                if (mLeDeviceListAdapter != null) {                    mLeDeviceListAdapter.MyDevicesort();                    mLeDeviceListAdapter.notifyDataSetChanged();                }            }        }        @Override        public void onBleScanStop(MyBleScanState scanState) {//扫描异常停止        }    };    private BaseBleScanner baseBleScanner;    private void scanLeDevice(final boolean enable) {        // 扫描5秒后停止        if (enable) {            if (!this.mBluetoothAdapter.isEnabled()) {                this.mBluetoothAdapter.enable();            } else {                ScanfHandler.removeCallbacksAndMessages(null);                // Stops scanning after a pre-defined scan period.                ScanfHandler.postDelayed(new Runnable() {                    @Override                    public void run() {                        MyLog.i(TAG, "停止扫描=");                        stopSCanf();                        baseBleScanner.onStopBleScan();                        // return ;                    }                }, Constants.SCAN_PERIOD_SEARCH_DEVICE_ACTIVITY);                // 正在扫描                startSCanf();                initScanAnimation();                baseBleScanner.onStartBleScan();            }        } else {            stopSCanf();            baseBleScanner.onStopBleScan();        }    }    /**     * 开始搜索     */    void startSCanf() {        mLeDeviceListAdapter.clear();        mLeDeviceListAdapter.notifyDataSetChanged();        isBLEScanning = true;        tv_scarch_title.setText(getText(R.string.device_scanf_loading));        tv_scarch_content.setText(getText(R.string.device_scanf_near));        ll_scarch_help.setVisibility(View.GONE);        rl_search_no_device.setVisibility(View.GONE);    }    /**     * 停止搜索     */    void stopSCanf() {        isBLEScanning = false;        pro_scanf_device_state.clearAnimation();        pro_scanf_device_state.setBackground(getResources().getDrawable(R.mipmap.icon_complete));        if (mLeDeviceListAdapter.getCount() > 0) {            tv_scarch_title.setText(getText(R.string.device_scanf_end));            tv_scarch_content.setText(getText(R.string.device_scanf_touch_connect));            ll_scarch_help.setVisibility(View.GONE);            rl_search_no_device.setVisibility(View.GONE);        } else {            tv_scarch_title.setText(getText(R.string.device_scanf_no));            tv_scarch_content.setText(getText(R.string.device_scanf_touch_connect));            ll_scarch_help.setVisibility(View.VISIBLE);            rl_search_no_device.setVisibility(View.VISIBLE);        }    }    @Override    protected void onStart() {        // TODO Auto-generated method stub        super.onStart();    }    @Override    protected void onDestroy() {        EventTools.SafeUnregisterEventBus(this);        scanLeDevice(false);        if (ScanfHandler != null) {            ScanfHandler.removeCallbacksAndMessages(null);        }        if (dialogHandler != null) {            dialogHandler.removeCallbacksAndMessages(null);        }        clearConnectInfo();        super.onDestroy();    }    // gps是否可用    public static boolean isGpsEnable(final Context context) {        LocationManager locationManager                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);        if (gps || network) {            return true;        }        return false;    }    @Override    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {        switch (requestCode) {            case AuthorityManagement.REQUEST_EXTERNAL_LOCATION:                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {                    MyLog.i(TAG, "获取定位权限 回调允许");//                    newStartDeviceSanf(true);                    if (MyUtils.isGPSOpen(ScanDeviceActivity.this)) {                        scanLeDevice(true);                    } else {                        showSettingGps();                    }                } else {                    MyLog.i(TAG, "获取定位权限 回调拒绝");                    showSettingDialog(getString(R.string.setting_dialog_location));                }                break;        }    }    void showSettingDialog(String title) {        new android.app.AlertDialog.Builder(context)                .setTitle(getString(R.string.dialog_prompt))//设置对话框标题                .setMessage(title)//设置显示的内容                .setPositiveButton(getString(R.string.setting_dialog_setting), new DialogInterface.OnClickListener() {//添加确定按钮                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件                        Intent intent = new Intent();                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);                        Uri uri = Uri.fromParts("package", getPackageName(), null);                        intent.setData(uri);                        startActivity(intent);                    }                }).setNegativeButton(getString(R.string.setting_dialog_cancel), new DialogInterface.OnClickListener() {//添加返回按钮            @Override            public void onClick(DialogInterface dialog, int which) {//响应事件                // TODO Auto-generated method stub            }        }).show();//在按键响应事件中显示此对话框    }    /**     * 显示设置Gps的对话框     */    private void showSettingGps() {        new android.app.AlertDialog.Builder(ScanDeviceActivity.this)                .setTitle(getString(R.string.dialog_prompt))//设置对话框标题                .setMessage(getString(R.string.open_gps))//设置显示的内容                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {//添加确定按钮                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);                        startActivity(intent); // 设置完成后返回到原来的界面                    }                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {//添加返回按钮            @Override            public void onClick(DialogInterface dialog, int which) {//响应事件                // TODO Auto-generated method stub            }        }).show();//在按键响应事件中显示此对话框    }    void newStartDeviceSanf(boolean isStop) {        if (isStop) {            scanLeDevice(false);        }        if (AuthorityManagement.verifyLocation(ScanDeviceActivity.this)) {            if (MyUtils.isGPSOpen(ScanDeviceActivity.this)) {                scanLeDevice(true);            } else {                showSettingGps();            }        } else {            MyLog.i(TAG, "获取定位权限 未授权");        }    }}