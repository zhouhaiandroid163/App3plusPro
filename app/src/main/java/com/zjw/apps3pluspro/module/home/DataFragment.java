package com.zjw.apps3pluspro.module.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseFragment;
import com.zjw.apps3pluspro.bleservice.BleConstant;
import com.zjw.apps3pluspro.bleservice.BleService;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.bleservice.BtSerializeation;
import com.zjw.apps3pluspro.eventbus.BlueToothStateEvent;
import com.zjw.apps3pluspro.eventbus.BluetoothAdapterStateEvent;
import com.zjw.apps3pluspro.eventbus.DataSyncCompleteEvent;
import com.zjw.apps3pluspro.eventbus.DeviceInfoEvent;
import com.zjw.apps3pluspro.eventbus.DeviceSportStatusEvent;
import com.zjw.apps3pluspro.eventbus.DeviceToAppSportStateEvent;
import com.zjw.apps3pluspro.eventbus.GpsSportDeviceStartEvent;
import com.zjw.apps3pluspro.eventbus.OffEcgSyncStateEvent;
import com.zjw.apps3pluspro.eventbus.SyncDeviceSportEvent;
import com.zjw.apps3pluspro.eventbus.SyncTimeLoadingEvent;
import com.zjw.apps3pluspro.eventbus.SyncTimeOutEvent;
import com.zjw.apps3pluspro.eventbus.tools.EventTools;
import com.zjw.apps3pluspro.module.device.ScanDeviceActivity;
import com.zjw.apps3pluspro.module.home.cycle.CycleActivity;
import com.zjw.apps3pluspro.module.home.cycle.CycleInitActivity;
import com.zjw.apps3pluspro.module.home.cycle.utils.MyCalendarUtils;
import com.zjw.apps3pluspro.module.home.ecg.EcgMeasureActivity;
import com.zjw.apps3pluspro.module.home.ecg.EcgMesureHistoryActivity;
import com.zjw.apps3pluspro.module.home.entity.ContinuitySpo2Model;
import com.zjw.apps3pluspro.module.home.entity.ContinuityTempModel;
import com.zjw.apps3pluspro.module.home.entity.HeartModel;
import com.zjw.apps3pluspro.module.home.entity.MeasureTempModel;
import com.zjw.apps3pluspro.module.home.entity.PageItem;
import com.zjw.apps3pluspro.module.home.entity.SleepModel;
import com.zjw.apps3pluspro.module.home.exercise.StepHistoryActivity;
import com.zjw.apps3pluspro.module.home.heart.ContinuousHeartHistoryActivity;
import com.zjw.apps3pluspro.module.home.heart.PerHourOneHeartHistoryActivity;
import com.zjw.apps3pluspro.module.home.ppg.PpgMeasureActivity;
import com.zjw.apps3pluspro.module.home.ppg.PpgMesureHistoryActivity;
import com.zjw.apps3pluspro.module.home.sleep.SleepHistoryActivity;
import com.zjw.apps3pluspro.module.home.spo2.Spo2DetailsActivity;
import com.zjw.apps3pluspro.module.home.spo2.Spo2MesureHistoryActivity;
import com.zjw.apps3pluspro.module.home.spo2.Spo2OfflineDataDetailsActivity;
import com.zjw.apps3pluspro.module.home.sport.MoreSportActivity;
import com.zjw.apps3pluspro.module.home.sport.SportModleUtils;
import com.zjw.apps3pluspro.module.home.temp.TempDetailsActivity;
import com.zjw.apps3pluspro.module.home.temp.TempHistoryActivity;
import com.zjw.apps3pluspro.module.mine.user.TargetSettingActivity;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.dbmanager.ContinuitySpo2InfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.ContinuityTempInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.HealthInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.HeartInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.MovementInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.SleepInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.SportModleInfoUtils;
import com.zjw.apps3pluspro.sql.entity.ContinuitySpo2Info;
import com.zjw.apps3pluspro.sql.entity.ContinuityTempInfo;
import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.sql.entity.HeartInfo;
import com.zjw.apps3pluspro.sql.entity.MeasureSpo2Info;
import com.zjw.apps3pluspro.sql.entity.MeasureTempInfo;
import com.zjw.apps3pluspro.sql.entity.MovementInfo;
import com.zjw.apps3pluspro.sql.entity.SleepInfo;
import com.zjw.apps3pluspro.sql.entity.SportModleInfo;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.CalibrationUtils;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.GoogleFitManager;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyChartUtils;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.NewTimeUtils;
import com.zjw.apps3pluspro.utils.PageManager;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.HeartChartView;
import com.zjw.apps3pluspro.view.MultiProgressView;
import com.zjw.apps3pluspro.view.StepHistogramView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by android
 * on 2020/5/11.
 */
public class DataFragment extends BaseFragment {

    private static final String TAG = DataFragment.class.getSimpleName();
    private static final int SORT_CODE = 0x01;
    @BindView(R.id.layoutAddDevice)
    LinearLayout layoutAddDevice;
    @BindView(R.id.layoutNoDevice)
    LinearLayout layoutNoDevice;
    @BindView(R.id.ivDeviceIcon)
    ImageView ivDeviceIcon;
    @BindView(R.id.tvDeviceName)
    TextView tvDeviceName;
    @BindView(R.id.ivBattery)
    ImageView ivBattery;
    @BindView(R.id.tvSyncState)
    TextView tvSyncState;
    @BindView(R.id.ivSync)
    ImageView ivSync;
    @BindView(R.id.layoutParent)
    LinearLayout layoutParent;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layoutCurDevice)
    ConstraintLayout layoutCurDevice;
    @BindView(R.id.tvDataCardSorting)
    TextView tvDataCardSorting;
    @BindView(R.id.tvDataTitle)
    TextView tvDataTitle;
    @BindView(R.id.layoutNoData)
    LinearLayout layoutNoData;
    @BindView(R.id.layoutPen)
    LinearLayout layoutPen;


    private HomeActivity homeActivity;
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private MovementInfoUtils mMovementInfoUtils = BaseApplication.getMovementInfoUtils();
    private SleepInfoUtils mSleepInfoUtils = BaseApplication.getSleepInfoUtils();
    private HeartInfoUtils mHeartInfoUtils = BaseApplication.getHeartInfoUtils();
    private HealthInfoUtils mHealthInfoUtils = BaseApplication.getHealthInfoUtils();
    private SportModleInfoUtils mSportModleInfoUtils = BaseApplication.getSportModleInfoUtils();

    private ContinuitySpo2InfoUtils mContinuitySpo2InfoUtils = BaseApplication.getContinuitySpo2InfoUtils();
    private ContinuityTempInfoUtils mContinuityTempInfoUtils = BaseApplication.getContinuityTempInfoUtils();

    private Handler mHandler = new Handler();

    @Override
    public void onDestroy() {
        EventTools.SafeUnregisterEventBus(this);
        super.onDestroy();
    }

    private EditText test_input;

    @Override
    public View initView() {
        EventTools.SafeRegisterEventBus(this);
        homeActivity = (HomeActivity) this.getActivity();
        view = View.inflate(context, R.layout.data_fragment, null);
        view.findViewById(R.id.test_01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationSuccess();
            }
        });
        view.findViewById(R.id.test_02).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationFail();
            }
        });
        view.findViewById(R.id.test_03).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSportSate();
            }
        });
        view.findViewById(R.id.test_04).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float value = Float.valueOf(test_input.getText().toString().trim());
                uploadSportDistance(value);
            }
        });
        test_input = (EditText) view.findViewById(R.id.test_input);
        return view;
    }

    @BindView(R.id.layoutSync)
    LinearLayout layoutSync;

    @Override
    public void initData() {
        layoutSync.setVisibility(View.VISIBLE);
        layoutPen.setVisibility(View.VISIBLE);
//        layoutNoData.setVisibility(View.GONE);
        tvDataTitle.setText(AppUtils.StringData(context));
        updateTagUi();
        mLayountInflater = LayoutInflater.from(context);
        // loading data
        swipeRefreshLayout.setOnRefreshListener(() -> {
            closeSwipeRefresh();
            if (BleService.syncState) {
                return;
            }
            if (HomeActivity.isSyncSportData) {
                return;
            }
            if (HomeActivity.ISBlueToothConnect()) {
                homeActivity.syncData();
                closeSwipeRefresh();
            } else {
                getAllData();
            }
        });

        refreshView();
        homeActivity.setGpsAccuracy(ivGpsStatus);
        layoutConnectState.setOnClickListener(v -> {
//            startActivity(new Intent(context, CommonProblemActivity.class));
        });
    }

    private void syncAnimation() {
        ivSync.clearAnimation();
        ivSync.setBackground(getResources().getDrawable(R.mipmap.sync_image_white));
        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(2000);// 设置动画持续周期
        rotate.setRepeatCount(-1);// 设置重复次数
        rotate.setFillAfter(true);// 动画执行完后是否停留在执行完的状态
        rotate.setStartOffset(10);// 执行前的等待时间
        ivSync.setAnimation(rotate);
    }

    @OnClick({R.id.layoutSync, R.id.tvDataCardSorting, R.id.layoutAddDevice, R.id.layoutPen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layoutPen:
                startActivity(new Intent(context, TargetSettingActivity.class));
                break;
            case R.id.tvDataCardSorting:
                Intent intent = new Intent(context, PageManagementActivity.class);
                intent.putExtra(PageManagementActivity.PAGE_TYPE, PageManagementActivity.PAGE_APP);
                startActivity(new Intent(context, PageManagementActivity.class));
                break;
            case R.id.layoutSync:
                if (BleService.syncState) {
                    return;
                }
                if (HomeActivity.isSyncSportData) {
                    return;
                }

                homeActivity.syncData();

                break;
            case R.id.layoutAddDevice:
                //蓝牙不等于空
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    return;
                }
                // 如果本地蓝牙没有开启，则开启
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, HomeActivity.BleStateResult);
                } else {
                    startActivity(new Intent(homeActivity, ScanDeviceActivity.class));
                }

                break;
        }
    }

    private LayoutInflater mLayountInflater;

    private void getAllData() {
        try {
            DataManager.getInstance().getSportDay(homeActivity, true, new DataManager.GetDataSuccess() {
                @Override
                public void onSuccess(Object movementInfo) {
                    closeSwipeRefresh();
                    if (movementInfo != null) {
                        MyLog.i(TAG, "getAllData movementInfo = " + movementInfo);
                        initExerciseData((MovementInfo) movementInfo);
                    }
                }
            });
            DataManager.getInstance().getSleepDay(homeActivity, true, new DataManager.GetDataSuccess() {
                @Override
                public void onSuccess(Object object) {
                    if (object != null) {
                        initSleepData((SleepInfo) object);
                    }
                }
            });
            if (mBleDeviceTools.get_is_support_persist_heart() == 1) {
                DataManager.getInstance().getContinuousHeart(homeActivity, true, new DataManager.GetDataSuccess() {
                    @Override
                    public void onSuccess(Object object) {
                        if (object == null) {
                            return;
                        }
                        initHeartData((HeartInfo) object);
                    }
                });
            } else {
                DataManager.getInstance().getWholeSnackHeart(homeActivity, true, new DataManager.GetDataSuccess() {
                    @Override
                    public void onSuccess(Object object) {
                        if (object == null) {
                            return;
                        }
                        initHeartData((HeartInfo) object);
                    }
                });
            }
            DataManager.getInstance().getHealthDay(homeActivity, true, new DataManager.GetDataSuccess() {
                @Override
                public void onSuccess(Object object) {
                    initHealthData((HealthInfo) object);
                }
            });

            if (mBleDeviceTools.get_is_support_spo2()) {
                if (mBleDeviceTools.getSupportContinuousBloodOxygen()) {
                    DataManager.getInstance().getContinuitySpo2(homeActivity, true, new DataManager.GetDataSuccess() {
                        @Override
                        public void onSuccess(Object object) {
                            initContinuitySpo2Info((ContinuitySpo2Info) object);
                        }
                    });
                } else if (mBleDeviceTools.getSupportOfflineBloodOxygen()) {
                    DataManager.getInstance().getOfflineSpo2(homeActivity, true, new DataManager.GetDataSuccess() {
                        @Override
                        public void onSuccess(Object object) {
                            initMeasureSpo2Info((MeasureSpo2Info) object);
                        }
                    });
                }
            }

            if (mBleDeviceTools.get_is_support_temp()) {
                if (mBleDeviceTools.getSupportContinuousTemp()) {
                    DataManager.getInstance().getContinuityTempDay(homeActivity, true, new DataManager.GetDataSuccess() {
                        @Override
                        public void onSuccess(Object object) {
                            initContinuityTempDay((ContinuityTempInfo) object);
                        }
                    });
                } else if (mBleDeviceTools.getSupportOfflineTemp()) {
                    DataManager.getInstance().getOfflineTempDay(homeActivity, true, new DataManager.GetDataSuccess() {
                        @Override
                        public void onSuccess(Object object) {
                            initOffineTempDay((MeasureTempInfo) object);
                        }
                    });
                }
            }
            DataManager.getInstance().getSportModleData(object -> initSport((SportModleInfo) object));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initViewState();
        refreshView();
//        updateUi();
        getAllData();
        if (HomeActivity.currentGpsSportState != -1 && HomeActivity.currentGpsSportState != GpsSportDeviceStartEvent.SPORT_STATE_STOP) {
            gpsSportDeviceStartEvent(new GpsSportDeviceStartEvent(HomeActivity.currentGpsSportState));
        }
    }

    @SuppressLint("InflateParams")
    private void refreshView() {
        layoutParent.removeAllViews();
        for (int i = 0; i < PageManager.getInstance().getPageAppList().size(); i++) {
            PageItem pageItem = PageManager.getInstance().getPageAppList().get(i);
            if (pageItem.isMark) {
                break;
            }
            int index = pageItem.index;
            LinearLayout mLinearLayout;
            switch (index) {
                case PageManager.PAGE_APP_ECG:
                    mLinearLayout = (LinearLayout) mLayountInflater.inflate(R.layout.page_ecg_layout, null);
                    findEcgId(mLinearLayout);
                    break;
                case PageManager.PAGE_APP_EXERCISE:
                    mLinearLayout = (LinearLayout) mLayountInflater.inflate(R.layout.page_exercise_layout, null);
                    findExerciseId(mLinearLayout);
                    break;
                case PageManager.PAGE_APP_HEART:
                    mLinearLayout = (LinearLayout) mLayountInflater.inflate(R.layout.page_heart_layout, null);
                    findHeartId(mLinearLayout);
                    break;
                case PageManager.PAGE_APP_SLEEP:
                    mLinearLayout = (LinearLayout) mLayountInflater.inflate(R.layout.page_sleep_layout, null);
                    findSleepId(mLinearLayout);
                    break;
                case PageManager.PAGE_APP_GPS_SPORT:
                    mLinearLayout = (LinearLayout) mLayountInflater.inflate(R.layout.page_gps_sport_layout, null);
                    findGpsId(mLinearLayout);
                    break;
                case PageManager.PAGE_APP_BLOOD_PRESSURE:
                    mLinearLayout = (LinearLayout) mLayountInflater.inflate(R.layout.page_blood_pressure_layout, null);
                    findBloodPressureId(mLinearLayout);
                    break;
                case PageManager.PAGE_APP_MENSTRUAL_PERIOD:
                    mLinearLayout = (LinearLayout) mLayountInflater.inflate(R.layout.page_menstrual_period_layout, null);
                    findMenstrualPeriodId(mLinearLayout);
                    break;
                case PageManager.PAGE_APP_BLOOD_OXYGEN:
                    mLinearLayout = (LinearLayout) mLayountInflater.inflate(R.layout.page_blood_oxygen_layout, null);
                    findBloodOxygenId(mLinearLayout);
                    break;
                case PageManager.PAGE_APP_TEMPERATURE:
                    mLinearLayout = (LinearLayout) mLayountInflater.inflate(R.layout.page_temperature_layout, null);
                    findTemperatureId(mLinearLayout);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + index);
            }
            layoutParent.addView(mLinearLayout);
        }
    }

    private boolean isFirstSync = true;

    @BindView(R.id.layoutConnectState)
    ConstraintLayout layoutConnectState;
    @BindView(R.id.tvConnectState)
    TextView tvConnectState;

    private void initBleState(int state) {
        switch (state) {
            case BleConstant.STATE_DISCONNECTED:
                MyLog.i(TAG, "initBleState() state = STATE_DISCONNECTED");
                ivSync.setBackground(getResources().getDrawable(R.mipmap.sync_image_white));
                tvSyncState.setText(getResources().getString(R.string.index_tip_no_connect1));
                tvConnectState.setText(getResources().getString(R.string.index_tip_no_connect1));

                layoutConnectState.setVisibility(View.VISIBLE);
                layoutDeviceGps.setVisibility(View.GONE);
                break;
            case BleConstant.STATE_CONNECTING:
                MyLog.i(TAG, "initBleState() state = STATE_CONNECTING");
                isFirstSync = true;
                tvSyncState.setText(getResources().getString(R.string.loading3));
                tvConnectState.setText(getResources().getString(R.string.loading3));
                syncAnimation();
                layoutConnectState.setVisibility(View.VISIBLE);
                break;
            case BleConstant.STATE_CONNECTED:
                MyLog.i(TAG, "initBleState() state = STATE_CONNECTED");
                isFirstSync = false;
                tvSyncState.setText(getResources().getString(R.string.already_connect_bracelet));
//                tvSyncState.setText(getResources().getString(R.string.sync_data_ing));
                tvDeviceName.setText(MyUtils.getNewBleName(mBleDeviceTools));
                layoutConnectState.setVisibility(View.GONE);
                break;
            case BleConstant.STATE_CONNECTED_TIMEOUT:
                MyLog.i(TAG, "initBleState() state = STATE_CONNECTED_TIMEOUT");
                tvSyncState.setText(getResources().getString(R.string.connect_timeout));
                tvConnectState.setText(getResources().getString(R.string.connect_timeout));
                layoutConnectState.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void syncTimeLoadingEventEvent(SyncTimeLoadingEvent event) {
        syncAnimation();
        tvSyncState.setText(getResources().getString(R.string.sync_data_ing));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void syncTimeOutEventEvent(SyncTimeOutEvent event) {
        ivSync.clearAnimation();
        ivSync.setBackground(getResources().getDrawable(R.mipmap.icon_complete));
        tvSyncState.setText(getResources().getString(R.string.sync_time_out));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void blueToothStateEvent(BlueToothStateEvent event) {
        setBattery(event.state);
        initBleState(event.state);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deviceInfoComplete(DeviceInfoEvent event) {
        setBattery(HomeActivity.getBlueToothStatus());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dataSyncSuccess(DataSyncCompleteEvent event) {
        //更新 选项卡支持哪些，更新当前的数据
        updateTagUi();
        updateUi();
        refreshView();
        getAllData();

        ivSync.clearAnimation();
        ivSync.setBackground(getResources().getDrawable(R.mipmap.icon_complete));
        tvSyncState.setText(getResources().getString(R.string.sync_data_success));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OffEcgSyncStateEvent(OffEcgSyncStateEvent event) {
        if (event.state == OffEcgSyncStateEvent.OFF_ECG_SYNC_STATE_END) {
            //支持ECG-显示健康布局
            HealthInfo mHealthInfo;
            if (mBleDeviceTools.get_is_support_ecg() == 1) {
                mHealthInfo = mHealthInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime(), true);
            } else {
                mHealthInfo = mHealthInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime(), false);
            }
            initHealthData(mHealthInfo);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void syncDeviceSportEvent(SyncDeviceSportEvent event) {
        DataManager.getInstance().getSportModleData(object -> initSport((SportModleInfo) object));
    }

    private void setBattery(int status) {
        if (status == BleConstant.STATE_CONNECTED) {
            MyLog.i(TAG, "setBattery() state = STATE_CONNECTED");
            int power = mBleDeviceTools.get_ble_device_power();
            MyLog.i(TAG, "电量 power = " + power);
            if (power >= 90) {
                ivBattery.setBackgroundResource(R.mipmap.electricity_100);
            } else if (power >= 75) {
                ivBattery.setBackgroundResource(R.mipmap.electricity_75);
            } else if (power >= 50) {
                ivBattery.setBackgroundResource(R.mipmap.electricity_50);
            } else if (power >= 25) {
                ivBattery.setBackgroundResource(R.mipmap.electricity_25);
            } else if (power >= 0) {
                ivBattery.setBackgroundResource(R.mipmap.electricity_0);
            }
        } else {
            ivBattery.setBackgroundResource(R.mipmap.electricity_0);
        }
    }


    @SuppressLint("StringFormatInvalid")
    private void initViewState() {
        if (JavaUtil.checkIsNull(mBleDeviceTools.get_ble_mac())) {
            layoutNoDevice.setVisibility(View.GONE);
            layoutCurDevice.setVisibility(View.VISIBLE);
        } else {
            layoutNoDevice.setVisibility(View.GONE);
            layoutCurDevice.setVisibility(View.VISIBLE);
            tvDeviceName.setText(MyUtils.getNewBleName(mBleDeviceTools));
        }

        setBattery(HomeActivity.getBlueToothStatus());
        initBleState(HomeActivity.getBlueToothStatus());

        if (BleService.syncState) {
            if (HomeActivity.ISBlueToothConnect()) {
                syncAnimation();
                tvSyncState.setText(getResources().getString(R.string.sync_data_ing));
            }
        } else {
            if (HomeActivity.ISBlueToothConnect()) {
                ivSync.clearAnimation();
                ivSync.setBackground(getResources().getDrawable(R.mipmap.icon_complete));
                long lastSyncTime = mBleDeviceTools.getLastSyncTime();
                MyLog.i(TAG, "initViewState lastSyncTime = " + lastSyncTime);
                if (lastSyncTime > 0) {
                    long time = (System.currentTimeMillis() - lastSyncTime) / (1000 * 60);
                    if (time > 0 && time <= 30) {
                        tvSyncState.setText(String.format(getResources().getString(R.string.sync_data_time), String.valueOf(time)));
                    }
                    if (time > 30 && HomeActivity.ISBlueToothConnect()) {
                        homeActivity.syncData();
                    }
                    if (time == 0) {
                        tvSyncState.setText(getResources().getString(R.string.sync_data_success));
                    }
                } else {
                    tvSyncState.setText(getResources().getString(R.string.no_data_default));
                }
            }
        }
    }

    private void closeSwipeRefresh() {
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private StepHistogramView stepHistogramView;
    private TextView tvExerciseStep, tvExerciseDistance, tvDistanceUnit, tvExerciseCal;
    private LinearLayout layoutExerciseShowData, layoutExerciseNoData;

    private void findExerciseId(LinearLayout mLinearLayout) {
        tvExerciseStep = mLinearLayout.findViewById(R.id.tvExerciseStep);
        tvExerciseDistance = mLinearLayout.findViewById(R.id.tvExerciseDistance);
        tvDistanceUnit = mLinearLayout.findViewById(R.id.tvDistanceUnit);
        tvExerciseCal = mLinearLayout.findViewById(R.id.tvExerciseCal);

        stepHistogramView = mLinearLayout.findViewById(R.id.stepHistogramView);

        layoutExerciseShowData = mLinearLayout.findViewById(R.id.layoutExerciseShowData);
        layoutExerciseNoData = mLinearLayout.findViewById(R.id.layoutExerciseNoData);

        mLinearLayout.findViewById(R.id.layoutExercise).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeActivity, StepHistoryActivity.class));
            }
        });
    }

    public void initExerciseData(MovementInfo mMovementInfo) {
        if (tvExerciseStep == null) {
            return;
        }
        String steps = !JavaUtil.checkIsNull(mMovementInfo.getTotal_step()) ? mMovementInfo.getTotal_step() : "0";
        String calory = !JavaUtil.checkIsNull(mMovementInfo.getCalorie()) ? mMovementInfo.getCalorie() : "0";
        String distance = !JavaUtil.checkIsNull(mMovementInfo.getDisance()) ? mMovementInfo.getDisance() : "0";
        distance = distance.replace(",", ".");

        //模拟数据
//        steps = "2290";

        if (mBleDeviceTools.get_device_unit() == 1) { // 公制
            distance = AppUtils.GetTwoFormat(Float.valueOf(distance));
            tvDistanceUnit.setText(getResources().getString(R.string.sport_distance_unit));
        } else {
            distance = AppUtils.GetTwoFormat(Float.valueOf(BleTools.getBritishSystem(steps)));
            tvDistanceUnit.setText(getResources().getString(R.string.unit_mi));
        }

        if (!JavaUtil.checkIsNull(steps)) {
            layoutExerciseShowData.setVisibility(View.VISIBLE);
            layoutExerciseNoData.setVisibility(View.GONE);
            mUserSetTools.set_user_stpe(Integer.parseInt(steps));
        } else {
            tvExerciseStep.setText("0");
            tvExerciseDistance.setText(context.getString(R.string.no_data_default));
            tvExerciseCal.setText(context.getString(R.string.no_data_default));
            layoutExerciseShowData.setVisibility(View.GONE);
            layoutExerciseNoData.setVisibility(View.VISIBLE);
            mUserSetTools.set_user_stpe(0);
            return;
        }

        tvExerciseStep.setText(steps);
        tvExerciseDistance.setText(distance);
        tvExerciseCal.setText(calory);

        String[] steps24 = mMovementInfo.getData().split(",");
        int max = 0;
        final float[] progress = new float[24];
        for (int i = 0; i < 24; i++) {
            progress[i] = Float.parseFloat(steps24[i]);
            if (progress[i] > max) {
                max = (int) progress[i];
            }
        }
        max = (max / 100) * 100 + 100;
        stepHistogramView.start(progress, max, 1, null, null, false);
    }

    private TextView tvSleepTitle, tvDeepSleep, tvLightSleep, tvWokeSleep, tvDeepSleepText;
    private MultiProgressView multiProgressView;
    private LinearLayout layoutDeep, layoutWoke, layoutLight, layoutSleepNoData, layoutSleepData;

    private void findSleepId(LinearLayout mLinearLayout) {
        tvSleepTitle = mLinearLayout.findViewById(R.id.tvSleepTitle);
        multiProgressView = mLinearLayout.findViewById(R.id.multiProgressView);
        tvDeepSleepText = mLinearLayout.findViewById(R.id.tvDeepSleepText);
        tvDeepSleep = mLinearLayout.findViewById(R.id.tvDeepSleep);
        tvLightSleep = mLinearLayout.findViewById(R.id.tvlightSleep);
        tvWokeSleep = mLinearLayout.findViewById(R.id.tvWokeSleep);
        layoutSleepNoData = mLinearLayout.findViewById(R.id.layoutSleepNoData);
        layoutSleepData = mLinearLayout.findViewById(R.id.layoutSleepData);
        layoutDeep = mLinearLayout.findViewById(R.id.layoutDeep);
        layoutLight = mLinearLayout.findViewById(R.id.layoutLight);
        layoutWoke = mLinearLayout.findViewById(R.id.layoutWoke);
        mLinearLayout.findViewById(R.id.layoutSleep).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeActivity, SleepHistoryActivity.class));
            }
        });
    }

    public void initSleepData(SleepInfo mSleepInfo) {
        if (tvSleepTitle == null) {
            return;
        }
        tvSleepTitle.setText(getResources().getString(R.string.title_sleep));
        String targetSleep = mUserSetTools.get_user_sleep_target();
        //睡眠目标
        if (!JavaUtil.checkIsNull(mSleepInfo.getData())) {
            SleepModel mSleepModel = new SleepModel(mSleepInfo);
            if (!JavaUtil.checkIsNull(mSleepModel.getSleepSleepTime())) {
                String sleep_time = mSleepModel.getSleepSleepTime();

                tvSleepTitle.setText(getResources().getString(R.string.sleep_length_time) + " " + MyTime.getSleepTime_H(sleep_time, getResources().getString(R.string.sleep_gang))+ " "
                        + getResources().getString(R.string.hour) + " " + MyTime.getSleepTime_M(sleep_time, getResources().getString(R.string.sleep_gang))+ " "
                        + getResources().getString(R.string.minute));
            }
            int progress1 = Integer.parseInt(mSleepModel.getSleepDeep());
            int progress2 = Integer.parseInt(mSleepModel.getSleepLight());
            int progress3 = Integer.parseInt(mSleepModel.getSleepSoberTime());

            int deepProportion = 0;
            int lightProportion = 0;
            int wokeProportion = 0;

            int total = progress1 + progress2 + progress3;

            deepProportion = progress1 * 100 / total;

            tvDeepSleepText.setText(getResources().getString(R.string.deep_sleep) + " " + MyTime.getSleepTime_H(String.valueOf(progress1), getResources().getString(R.string.sleep_gang)) + " "
                    + getResources().getString(R.string.hour) + " " + MyTime.getSleepTime_M(String.valueOf(progress1), getResources().getString(R.string.sleep_gang)) + " "
                    + getResources().getString(R.string.minute));

            if (progress3 == 0) {
                lightProportion = 100 - progress1 * 100 / total;
                wokeProportion = 0;
            } else if (progress3 * 100 / total == 0) {
                lightProportion = 100 - progress1 * 100 / total - 1;
                wokeProportion = 1;
            } else {
                lightProportion = 100 - progress1 * 100 / total - progress3 * 100 / total;
                wokeProportion = progress3 * 100 / total;
            }

            tvDeepSleep.setText(getResources().getString(R.string.deep_sleep) + deepProportion + "%");
            tvLightSleep.setText(getResources().getString(R.string.light_sleep) + lightProportion + "%");
            tvWokeSleep.setText(getResources().getString(R.string.sober) + wokeProportion + "%");

            multiProgressView.start(deepProportion, lightProportion, wokeProportion);
            layoutSleepNoData.setVisibility(View.GONE);
            layoutSleepData.setVisibility(View.VISIBLE);
            layoutDeep.setVisibility(View.VISIBLE);
            layoutLight.setVisibility(View.VISIBLE);
            layoutWoke.setVisibility(View.VISIBLE);
        } else {
            multiProgressView.start(0, 0, 0);
            tvDeepSleep.setText("0%");
            tvLightSleep.setText("0%");
            tvWokeSleep.setText("0%");
            layoutSleepNoData.setVisibility(View.VISIBLE);
            layoutSleepData.setVisibility(View.GONE);
            layoutDeep.setVisibility(View.GONE);
            layoutLight.setVisibility(View.GONE);
            layoutWoke.setVisibility(View.GONE);
        }

    }

    private TextView tvAvg, tvMax, tvMin, tvHeartTitle;
    private LineChart lcHeart;
    private LinearLayout layoutHeartValue, layoutHeartNoData, layout1, layout2;
    private HeartChartView heartLineChart;

    private void findHeartId(LinearLayout mLinearLayout) {
        tvAvg = mLinearLayout.findViewById(R.id.tvAvg);
        tvMax = mLinearLayout.findViewById(R.id.tvMax);
        tvMin = mLinearLayout.findViewById(R.id.tvMin);
        lcHeart = mLinearLayout.findViewById(R.id.lcHeart);
        tvHeartTitle = mLinearLayout.findViewById(R.id.tvHeartTitle);
        layout1 = mLinearLayout.findViewById(R.id.layout1);
        layout2 = mLinearLayout.findViewById(R.id.layout2);
        heartLineChart = mLinearLayout.findViewById(R.id.heartLineChart);

        layoutHeartNoData = mLinearLayout.findViewById(R.id.layoutHeartNoData);
        layoutHeartValue = mLinearLayout.findViewById(R.id.layoutHeartValue);

        mLinearLayout.findViewById(R.id.layoutText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBleDeviceTools.get_is_support_persist_heart() == 1) {
                    MyLog.i(TAG, "跳转到连续心率界面");
                    startActivity(new Intent(homeActivity, ContinuousHeartHistoryActivity.class));
                } else {
                    MyLog.i(TAG, "跳转到整点心率界面");
                    startActivity(new Intent(homeActivity, PerHourOneHeartHistoryActivity.class));
                }
            }
        });
    }

    private void initHeartData(HeartInfo mHeartInfo) {
        if (tvAvg == null) {
            return;
        }
        HeartModel mHeartModel = new HeartModel(mHeartInfo);
        //最近一次心率
        String last_heart = !JavaUtil.checkIsNull(mHeartModel.getLastHeart()) ? mHeartModel.getLastHeart() : "0";
        String avg = !JavaUtil.checkIsNull(mHeartModel.getHeartDayAverage()) ? mHeartModel.getHeartDayAverage() : "0";
        String max = !JavaUtil.checkIsNull(mHeartModel.getHeartDayMax()) ? mHeartModel.getHeartDayMax() : "0";
        String min = !JavaUtil.checkIsNull(mHeartModel.getHeartDayMin()) ? mHeartModel.getHeartDayMin() : "0";

        int heartTime = mHeartModel.getLastHeartIndex();
        if (heartTime != -1) {
            int length = mHeartModel.getHeartData().split(",").length;
            long dataTime = NewTimeUtils.getLongTime(mHeartModel.getHeartDate(), NewTimeUtils.TIME_YYYY_MM_DD);
            GoogleFitManager.getInstance().updateHeart(getActivity(), Long.parseLong(last_heart), (long) (dataTime + 24 * 3600 * 1000l * (heartTime * 1.0f / length)));
        }

        if (!JavaUtil.checkIsNull(avg)) {
            tvAvg.setText(avg);
            tvMax.setText(" " + max + " ");
            tvMin.setText(" " + min + " ");
            tvHeartTitle.setText(getResources().getString(R.string.heart) + " " + last_heart + " " + getResources().getString(R.string.bpm));
        } else {
            tvAvg.setText(context.getString(R.string.sleep_gang));
            tvMax.setText(context.getString(R.string.sleep_gang));
            tvMin.setText(context.getString(R.string.sleep_gang));
            tvHeartTitle.setText(getResources().getText(R.string.heart));
        }
        String[] heartData = mHeartModel.getHeartData().split(",");
        if (Integer.parseInt(avg) == 0) {
            layoutHeartNoData.setVisibility(View.VISIBLE);
            layoutHeartValue.setVisibility(View.GONE);
        } else {
            layoutHeartNoData.setVisibility(View.GONE);
            layoutHeartValue.setVisibility(View.VISIBLE);
            // 1 连续心率  0 整点心率
            if (mBleDeviceTools.get_is_support_persist_heart() == 1) {
                MyChartUtils.showDayWoHearBarChart(context, lcHeart, heartData, false);
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
            } else {
                layout2.setVisibility(View.VISIBLE);
                layout1.setVisibility(View.GONE);
                heartLineChart.setParameter(heartData, "65", false);
            }
        }
    }

    private TextView tvPPGBloodPressure, tvPpgTitle;
    private LinearLayout layoutBloodNoData, layoutBloodShowData;

    private void findBloodPressureId(LinearLayout mLinearLayout) {
        tvPPGBloodPressure = mLinearLayout.findViewById(R.id.tvPPGBloodPressure);
        tvPPGBloodPressure = mLinearLayout.findViewById(R.id.tvPPGBloodPressure);
        layoutBloodShowData = mLinearLayout.findViewById(R.id.layoutBloodShowData);
        layoutBloodNoData = mLinearLayout.findViewById(R.id.layoutBloodNoData);
        tvPpgTitle = mLinearLayout.findViewById(R.id.tvPpgTitle);
        mLinearLayout.findViewById(R.id.layoutBloodPressureCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeActivity, PpgMesureHistoryActivity.class));
            }
        });

    }

    private TextView tvEcgBloodPressure, tvEcgTitle;
    private ConstraintLayout layoutEcgShowData;
    private LinearLayout layoutEcgNoData;

    private void findEcgId(LinearLayout mLinearLayout) {
        tvEcgTitle = mLinearLayout.findViewById(R.id.tvEcgTitle);
        tvEcgBloodPressure = mLinearLayout.findViewById(R.id.tvEcgBloodPressure);
        layoutEcgShowData = mLinearLayout.findViewById(R.id.layoutEcgShowData);
        layoutEcgNoData = mLinearLayout.findViewById(R.id.layoutEcgNoData);

        mLinearLayout.findViewById(R.id.layoutEcgCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeActivity, EcgMesureHistoryActivity.class));
            }
        });
    }

    public void initHealthData(HealthInfo mHealthInfo) {
        if (mBleDeviceTools.get_is_support_ecg() == 1) {
            if (tvEcgTitle == null) {
                return;
            }
        } else {
            if (tvPpgTitle == null) {
                return;
            }
        }

        if (mHealthInfo != null) {
            MyLog.i(TAG, " mHealthInfo = " + mHealthInfo.toString());
            String s_heart = !JavaUtil.checkIsNull(mHealthInfo.getHealth_heart()) ? mHealthInfo.getHealth_heart() : "0";
            String s_systolic = !JavaUtil.checkIsNull(mHealthInfo.getHealth_systolic()) ? mHealthInfo.getHealth_systolic() : "0";
            String s_diastole = !JavaUtil.checkIsNull(mHealthInfo.getHealth_diastolic()) ? mHealthInfo.getHealth_diastolic() : "0";
            String s_index = !JavaUtil.checkIsNull(mHealthInfo.getIndex_health_index()) ? mHealthInfo.getIndex_health_index() : "0";
            String s_time = !JavaUtil.checkIsNull(mHealthInfo.getMeasure_time()) ? mHealthInfo.getMeasure_time() : "";

            //心电手环-ECG测量
            if (mBleDeviceTools.get_is_support_ecg() == 1) {
                if (!JavaUtil.checkIsNull(s_heart)) {
                    tvEcgTitle.setText(getResources().getString(R.string.ecg_heart) + s_heart + getResources().getString(R.string.bpm));
                    tvEcgBloodPressure.setText(s_systolic + "/" + s_diastole);
                    layoutEcgNoData.setVisibility(View.GONE);
                    layoutEcgShowData.setVisibility(View.VISIBLE);
                } else {
                    tvEcgTitle.setText(getResources().getString(R.string.ecg_measure_ecg));
                    tvEcgBloodPressure.setText(context.getString(R.string.sleep_gang));
                    layoutEcgNoData.setVisibility(View.VISIBLE);
                    layoutEcgShowData.setVisibility(View.GONE);
                }
            } else {
                if (!JavaUtil.checkIsNull(s_heart)) {
                    tvPpgTitle.setText(getResources().getString(R.string.ppg_heart) + s_heart + getResources().getString(R.string.bpm));
                    tvPPGBloodPressure.setText(s_systolic + "/" + s_diastole);
                    layoutBloodNoData.setVisibility(View.GONE);
                    layoutBloodShowData.setVisibility(View.VISIBLE);
                } else {
                    tvPpgTitle.setText(getResources().getString(R.string.blood_pressure));
                    tvPPGBloodPressure.setText(context.getString(R.string.sleep_gang));
                    layoutBloodNoData.setVisibility(View.VISIBLE);
                    layoutBloodShowData.setVisibility(View.GONE);
                }
            }
        } else {
            if (mBleDeviceTools.get_is_support_ecg() == 1) {
                tvEcgTitle.setText(getResources().getString(R.string.ecg_measure_ecg));
                layoutEcgNoData.setVisibility(View.VISIBLE);
                layoutEcgShowData.setVisibility(View.GONE);
            } else {
                layoutBloodNoData.setVisibility(View.VISIBLE);
                layoutBloodShowData.setVisibility(View.GONE);
            }
        }

    }

    private ImageView ivGpsSportTitle;
    private TextView tvGpsSportTitle, tvUpdateTime, tvValue0, tvValue1;
    private LinearLayout layoutGpsSportShowData, layoutGpsSportNoData, layoutGpsSport0, layoutGpsSport1;

    private void findGpsId(LinearLayout mLinearLayout) {
        ivGpsSportTitle = mLinearLayout.findViewById(R.id.ivGpsSportTitle);
        tvGpsSportTitle = mLinearLayout.findViewById(R.id.tvGpsSportTitle);

        tvUpdateTime = mLinearLayout.findViewById(R.id.tvUpdateTime);
        tvValue0 = mLinearLayout.findViewById(R.id.tvValue0);
        tvValue1 = mLinearLayout.findViewById(R.id.tvValue1);

        layoutGpsSportShowData = mLinearLayout.findViewById(R.id.layoutGpsSportShowData);
        layoutGpsSportNoData = mLinearLayout.findViewById(R.id.layoutGpsSportNoData);
        layoutGpsSport0 = mLinearLayout.findViewById(R.id.layoutGpsSport0);
        layoutGpsSport1 = mLinearLayout.findViewById(R.id.layoutGpsSport1);

        mLinearLayout.findViewById(R.id.layoutSportCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MoreSportActivity.class));
            }
        });
    }

    private void initSport(SportModleInfo mSportModleInfo) {
        if (ivGpsSportTitle == null) {
            return;
        }

        if (mSportModleInfo != null) {
            layoutGpsSportShowData.setVisibility(View.VISIBLE);
            layoutGpsSportNoData.setVisibility(View.GONE);
            if (mSportModleInfo.getDataSourceType() == 0) {
                layoutGpsSport0.setVisibility(View.VISIBLE);
                layoutGpsSport1.setVisibility(View.GONE);

                tvGpsSportTitle.setText(SportModleUtils.getSportTypeStr(context, mSportModleInfo.getSport_type()));
                Drawable imageDrawable = SportModleUtils.getSportTypeImg(context, mSportModleInfo.getSport_type());
                ivGpsSportTitle.setBackgroundDrawable(imageDrawable);
                tvUpdateTime.setText(mSportModleInfo.getTime());
                if (mSportModleInfo.getUi_type().equals("0")) {
                    tvValue0.setText(mSportModleInfo.getTotal_step() + " " + getResources().getString(R.string.steps) + "  " + getResources().getString(R.string.sport_time) + " " +
                            NewTimeUtils.getTimeString(Integer.valueOf(mSportModleInfo.getSport_duration())));
                } else if (mSportModleInfo.getUi_type().equals("1")) {
                    tvValue0.setText(getResources().getString(R.string.consume) + " " + mSportModleInfo.getCalorie() + " " + getResources().getString(R.string.big_calory) + "  " + getResources().getString(R.string.sport_time) + " " +
                            NewTimeUtils.getTimeString(Integer.valueOf(mSportModleInfo.getSport_duration())));
                } else if (mSportModleInfo.getUi_type().equals("2")) {
                    tvValue0.setText(mSportModleInfo.getTotal_step() + " " + getResources().getString(R.string.steps) + "  " + getResources().getString(R.string.sport_time) + " " +
                            NewTimeUtils.getTimeString(Integer.valueOf(mSportModleInfo.getSport_duration())));
                } else {
                    tvValue0.setText(getResources().getString(R.string.consume) + " " + mSportModleInfo.getCalorie() + getResources().getString(R.string.big_calory) + "  " + getResources().getString(R.string.sport_time) + " " +
                            NewTimeUtils.getTimeString(Integer.valueOf(mSportModleInfo.getSport_duration())));
                }

            } else {
                layoutGpsSport0.setVisibility(View.GONE);
                layoutGpsSport1.setVisibility(View.VISIBLE);

                tvGpsSportTitle.setText(SportModleUtils.getDeviceSportTypeStr(context, mSportModleInfo.getRecordPointSportType()));
                Drawable imageDrawable = SportModleUtils.getDeviceSportTypeImg(context, mSportModleInfo.getRecordPointSportType());
                ivGpsSportTitle.setBackgroundDrawable(imageDrawable);

                switch (mSportModleInfo.getRecordPointSportType()) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        tvValue1.setText(mSportModleInfo.getReportTotalStep() + " " + getResources().getString(R.string.steps) + "  " + getResources().getString(R.string.sport_time) + " " + NewTimeUtils.getTimeString(mSportModleInfo.getReportDuration()));
                        break;
                    default:
                        tvValue1.setText(getResources().getString(R.string.consume) + mSportModleInfo.getReportCal() + " " + getResources().getString(R.string.big_calory) + "  " + getResources().getString(R.string.sport_time) + " " + NewTimeUtils.getTimeString(mSportModleInfo.getReportDuration()));
                        break;
                }
            }

        } else {
            layoutGpsSportShowData.setVisibility(View.GONE);
            layoutGpsSportNoData.setVisibility(View.VISIBLE);
            tvGpsSportTitle.setText(context.getString(R.string.data_sport));
            ivGpsSportTitle.setBackgroundDrawable(SportModleUtils.getSportTypeDefaultImg(context));
            return;
        }


    }

    private void updateUi() {
        MyLog.i(TAG, "updateUi()");
        MovementInfo mMovementInfo = mMovementInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime());
        SleepInfo mSleepInfo = mSleepInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime());
        HeartInfo mPoHeartInfo = mHeartInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime(), "0");
        HeartInfo mWoHeartInfo = mHeartInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime(), "1");
        HealthInfo mHealthInfo = null;

        if (mMovementInfo != null) {
            initExerciseData(mMovementInfo);
        }

        if (mSleepInfo != null) {
            MyLog.i(TAG, "mSleepInfo = " + mSleepInfo.toString());
            initSleepData(mSleepInfo);
        } else {
            MyLog.i(TAG, "mSleepInfo = null");
        }

        //是否支持连续心率
        //连续心率
        if (mBleDeviceTools.get_is_support_persist_heart() == 1) {
            if (mWoHeartInfo != null) {
                initHeartData(mWoHeartInfo);
            }
        }//整点心率
        else {
            if (mPoHeartInfo != null) {
                initHeartData(mPoHeartInfo);
            }
        }
        //支持ECG-显示健康布局
        if (mBleDeviceTools.get_is_support_ecg() == 1) {
            mHealthInfo = mHealthInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime(), true);
        } else {
            mHealthInfo = mHealthInfoUtils.MyQueryToDate(BaseApplication.getUserId(), MyTime.getTime(), false);
        }
        initHealthData(mHealthInfo);
    }

    private void updateTagUi() {
        //模拟数据-标志位
//        mBleDeviceTools.set_is_support_blood(1);
//        mBleDeviceTools.set_is_support_ppg(0);
//        mBleDeviceTools.set_device_is_cycle(true);

        if (!hasAppointCard(PageManager.PAGE_APP_EXERCISE)) {
            PageManager.getInstance().addPage(new PageItem(PageManager.PAGE_APP_EXERCISE, false));
        }

        if (!hasAppointCard(PageManager.PAGE_APP_SLEEP)) {
            PageManager.getInstance().addPage(new PageItem(PageManager.PAGE_APP_SLEEP, false));
        }

        if (!hasAppointCard(PageManager.PAGE_APP_HEART)) {
            //
            if (mBleDeviceTools.get_is_support_ecg() == 1) {
                PageManager.getInstance().addPage(new PageItem(PageManager.PAGE_APP_HEART, false));
            } else {
                if (mBleDeviceTools.get_is_support_ppg() == 1) {
                    PageManager.getInstance().addPage(new PageItem(PageManager.PAGE_APP_HEART, false));
                }
            }
        } else {
            //不支持心率
            if (mBleDeviceTools.get_is_support_ppg() != 1) {
                PageManager.getInstance().removePage(PageManager.PAGE_APP_HEART);
            }
        }

        if (!hasAppointCard(PageManager.PAGE_APP_ECG)) {
            if (mBleDeviceTools.get_is_support_ecg() != 0) {
                PageManager.getInstance().addPage(new PageItem(PageManager.PAGE_APP_ECG, false));
            }
        } else {
            if (mBleDeviceTools.get_is_support_ecg() == 0) {
                PageManager.getInstance().removePage(PageManager.PAGE_APP_ECG);
            }
        }

        if (!hasAppointCard(PageManager.PAGE_APP_BLOOD_PRESSURE)) {
            //不支持ECG，支持PPG，且，支持血压
            if (mBleDeviceTools.get_is_support_ecg() != 1 && mBleDeviceTools.get_is_support_ppg() == 1 && mBleDeviceTools.get_is_support_blood() != 1) {
                PageManager.getInstance().addPage(new PageItem(PageManager.PAGE_APP_BLOOD_PRESSURE, false));
            }
        } else {
            //支持ECG，不支持PPG，或者，不支持血压
            if (mBleDeviceTools.get_is_support_ecg() == 1 || mBleDeviceTools.get_is_support_ppg() != 1 || mBleDeviceTools.get_is_support_blood() == 1) {
                PageManager.getInstance().removePage(PageManager.PAGE_APP_BLOOD_PRESSURE);
            }
        }
        if (!hasAppointCard(PageManager.PAGE_APP_BLOOD_OXYGEN)) {
            if ((mBleDeviceTools.get_is_support_spo2() && mBleDeviceTools.getSupportContinuousBloodOxygen())
                    || mBleDeviceTools.get_is_support_spo2() && mBleDeviceTools.getSupportOfflineBloodOxygen()
            ) {
                PageManager.getInstance().addPage(new PageItem(PageManager.PAGE_APP_BLOOD_OXYGEN, false));
            }
        } else {
            if (!mBleDeviceTools.get_is_support_spo2()) {
                PageManager.getInstance().removePage(PageManager.PAGE_APP_BLOOD_OXYGEN);
            }
        }

        if (!hasAppointCard(PageManager.PAGE_APP_TEMPERATURE)) {
            if ((mBleDeviceTools.get_is_support_temp() && mBleDeviceTools.getSupportContinuousTemp()) ||
                    mBleDeviceTools.get_is_support_temp() && mBleDeviceTools.getSupportOfflineTemp()
            ) {
                PageManager.getInstance().addPage(new PageItem(PageManager.PAGE_APP_TEMPERATURE, false));
            }
        } else {
            if (!mBleDeviceTools.get_is_support_temp()) {
                PageManager.getInstance().removePage(PageManager.PAGE_APP_TEMPERATURE);
            }
        }

        if (!hasAppointCard(PageManager.PAGE_APP_MENSTRUAL_PERIOD)) {
            if (mUserSetTools.get_user_sex() == 1 && mBleDeviceTools.get_device_is_cycle()) {
                PageManager.getInstance().addPage(new PageItem(PageManager.PAGE_APP_MENSTRUAL_PERIOD, false));
            }
        } else {
            if (mUserSetTools.get_user_sex() != 1 || !mBleDeviceTools.get_device_is_cycle()) {
                PageManager.getInstance().removePage(PageManager.PAGE_APP_MENSTRUAL_PERIOD);
            }
        }

        if (!hasAppointCard(PageManager.PAGE_APP_GPS_SPORT)) {
            PageManager.getInstance().addPage(new PageItem(PageManager.PAGE_APP_GPS_SPORT, false));
        }
        if (!hasAppointCard(-1)) {
            PageManager.getInstance().addPage(new PageItem(PageManager.PAGE_HIDE, true));
        }
        PageManager.getInstance().setCardJson();
    }

    private boolean hasAppointCard(int index) {
        ArrayList<PageItem> pagelist = PageManager.getInstance().getPageAppList();
        for (int i = 0; i < pagelist.size(); i++) {
            if (index == pagelist.get(i).index) {
                return true;
            }
        }
        return false;
    }


    private LinearLayout cycleview_lin;
    private TextView tv_cycle_target, tvLength;
    private TextView tvMenstualPeriodTitle, tvState1, tvState2, tvState3, tvState4;

    private void findMenstrualPeriodId(LinearLayout mLinearLayout) {
        LinearLayout ll_index_cycle_no_set = mLinearLayout.findViewById(R.id.ll_index_cycle_no_set);
        LinearLayout ll_index_cycle_yes_set = mLinearLayout.findViewById(R.id.ll_index_cycle_yes_set);
        cycleview_lin = mLinearLayout.findViewById(R.id.cycleview_lin);
        tv_cycle_target = mLinearLayout.findViewById(R.id.tv_cycle_target);
        tvMenstualPeriodTitle = mLinearLayout.findViewById(R.id.tvMenstualPeriodTitle);

        tvState1 = mLinearLayout.findViewById(R.id.tvState1);
        tvState2 = mLinearLayout.findViewById(R.id.tvState2);
        tvState3 = mLinearLayout.findViewById(R.id.tvState3);
        tvState4 = mLinearLayout.findViewById(R.id.tvState4);

        tvLength = mLinearLayout.findViewById(R.id.tvLength);

        mLinearLayout.findViewById(R.id.layoutMenstualPeriod).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (mUserSetTools.get_device_is_one_cycle()) {
                    intent = new Intent(context, CycleInitActivity.class);
                    intent.putExtra(CycleInitActivity.CYCLE_INIT_TAG_TYPE, CycleInitActivity.CYCLE_INIT_TAG_A);
                    startActivity(intent);

                } else if (mUserSetTools.get_nv_start_date().equals("")) {
                    intent = new Intent(context, CycleInitActivity.class);
                    intent.putExtra(CycleInitActivity.CYCLE_INIT_TAG_TYPE, CycleInitActivity.CYCLE_INIT_TAG_A);
                    startActivity(intent);
                } else {
                    intent = new Intent(context, CycleActivity.class);
                    intent.putExtra(CycleInitActivity.CYCLE_INIT_TAG_TYPE, CycleInitActivity.CYCLE_INIT_TAG_B);
                    startActivity(intent);
                }
            }
        });

        //第一次使用，需要保存之后跳转到生理周期页面，并关闭当前页面
        if (mUserSetTools.get_device_is_one_cycle()) {
            ll_index_cycle_no_set.setVisibility(View.VISIBLE);
            ll_index_cycle_yes_set.setVisibility(View.GONE);
        }
        //第一次使用，需要保存之后跳转到生理周期页面，并关闭当前页面
        else if (mUserSetTools.get_nv_start_date().equals("")) {
            ll_index_cycle_no_set.setVisibility(View.VISIBLE);
            ll_index_cycle_yes_set.setVisibility(View.GONE);
        } else {
            ll_index_cycle_no_set.setVisibility(View.GONE);
            ll_index_cycle_yes_set.setVisibility(View.VISIBLE);
            initCycle();
        }
    }

    private void initCycle() {
        //不是第一次,需要保存后，关闭当前页面就行了
        boolean is_one_cycle = !mUserSetTools.get_device_is_one_cycle();
        boolean nv_start_date = !mUserSetTools.get_nv_start_date().equals("");
        boolean nv_cycle = mUserSetTools.get_nv_cycle() > 0;
        boolean jingqi = mBleDeviceTools.get_device_cycle_jingqi() > 0;
//        boolean anqunqiyi = mBleDeviceTools.get_device_cycle_anqunqiyi() > 0;
        boolean weixianqi = mBleDeviceTools.get_device_cycle_weixianqi() > 0;
//        boolean anquanqier = mBleDeviceTools.get_device_cycle_anquanqier() > 0;

        if (is_one_cycle && nv_start_date && nv_cycle && jingqi && weixianqi) {
            int state = MyCalendarUtils.getCycleState(mBleDeviceTools, mUserSetTools, MyTime.getTime());
            String state_str = MyCalendarUtils.getCycleStateStr(context, state);
            int state_color = MyCalendarUtils.getCycleStateColor(state);

            tv_cycle_target.setText(String.valueOf(mUserSetTools.get_nv_cycle()));
            tvLength.setText(getResources().getString(R.string.cycle_tile) + mUserSetTools.get_nv_cycle());

            if (state >= 1 && state <= 4) {
                switch (state) {
                    case 1:
                        tvState1.setBackground(getResources().getDrawable(R.mipmap.cycle_select_bg));
                        tvMenstualPeriodTitle.setText(getResources().getString(R.string.cycle_period));
                        break;
                    case 2:
                        tvState2.setBackground(getResources().getDrawable(R.mipmap.cycle_select_bg));
                        tvMenstualPeriodTitle.setText(getResources().getString(R.string.cycle_security));
                        break;
                    case 3:
                        tvState3.setBackground(getResources().getDrawable(R.mipmap.cycle_select_bg));
                        tvMenstualPeriodTitle.setText(getResources().getString(R.string.cycle_danger));
                        break;
                    case 4:
                        tvState4.setBackground(getResources().getDrawable(R.mipmap.cycle_select_bg));
                        tvMenstualPeriodTitle.setText(getResources().getString(R.string.cycle_security));
                        break;
                }
            }
        }

    }

    private TextView tvTemperatureTitle;
    private LinearLayout layoutTemperatureNoData, layoutTemperatureShowData;

    private void findTemperatureId(LinearLayout mLinearLayout) {
        tvTemperatureTitle = mLinearLayout.findViewById(R.id.tvTemperatureTitle);
        layoutTemperatureNoData = mLinearLayout.findViewById(R.id.layoutTemperatureNoData);
        layoutTemperatureShowData = mLinearLayout.findViewById(R.id.layoutTemperatureShowData);
        mLinearLayout.findViewById(R.id.layoutTemperatureCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBleDeviceTools.getSupportContinuousTemp()) {
                    startActivity(new Intent(homeActivity, TempDetailsActivity.class));
                } else if (mBleDeviceTools.getSupportOfflineTemp()) {
                    startActivity(new Intent(homeActivity, TempHistoryActivity.class));
                }

            }
        });
    }

    private void initContinuityTempDay(ContinuityTempInfo mContinuityTempInfo) {
        if (tvTemperatureTitle == null) {
            return;
        }
        if (mContinuityTempInfo != null) {
            ContinuityTempModel mContinuityTempModel = new ContinuityTempModel(mContinuityTempInfo);
            String day_body_max = mContinuityTempModel.getContinuityTempDayBodyMax();
            String day_body_min = mContinuityTempModel.getContinuityTempDayBodyMin();
            String data = mContinuityTempModel.getBodyListData();

            String unit;
            //华氏度
            if (mBleDeviceTools.getTemperatureType() == 1) {
                unit = getString(R.string.fahrenheit_degree);
            } else {
                unit = getString(R.string.centigrade);
            }
            //最近一数据
            String last_body_value = !JavaUtil.checkIsNull(mContinuityTempModel.getLastBodyValue()) ? mContinuityTempModel.getLastBodyValue() : "0";
            if (last_body_value.equals("0")) {
                layoutTemperatureNoData.setVisibility(View.VISIBLE);
                layoutTemperatureShowData.setVisibility(View.GONE);
            } else {
                layoutTemperatureNoData.setVisibility(View.GONE);
                layoutTemperatureShowData.setVisibility(View.VISIBLE);
                tvTemperatureTitle.setText(getResources().getString(R.string.temp_body) + " " + last_body_value + unit);
            }
        } else {
            layoutTemperatureNoData.setVisibility(View.VISIBLE);
            layoutTemperatureShowData.setVisibility(View.GONE);
        }
    }

    private void initOffineTempDay(MeasureTempInfo mContinuityTempInfo) {
        if (tvTemperatureTitle == null) {
            return;
        }
        if (mContinuityTempInfo != null) {
            MeasureTempModel mMeasureTempModel = new MeasureTempModel(mContinuityTempInfo);

            String unit;
            //华氏度
            if (mBleDeviceTools.getTemperatureType() == 1) {
                unit = getString(R.string.fahrenheit_degree);
            } else {
                unit = getString(R.string.centigrade);
            }
            //最近一数据
            String last_body_value = !JavaUtil.checkIsNull(mMeasureTempModel.getMeasureTempDayBody()) ? mMeasureTempModel.getMeasureTempDayBody() : "0";
            if (last_body_value.equals("0")) {
                layoutTemperatureNoData.setVisibility(View.VISIBLE);
                layoutTemperatureShowData.setVisibility(View.GONE);
            } else {
                layoutTemperatureNoData.setVisibility(View.GONE);
                layoutTemperatureShowData.setVisibility(View.VISIBLE);
                tvTemperatureTitle.setText(getResources().getString(R.string.temp_body) + last_body_value + unit);
            }
        } else {
            layoutTemperatureNoData.setVisibility(View.VISIBLE);
            layoutTemperatureShowData.setVisibility(View.GONE);
        }
    }


    private TextView tvBloodOxygenTitle;
    private LinearLayout layoutBloodOxygenNoData, layoutBloodOxygenShowData;

    private void findBloodOxygenId(LinearLayout mLinearLayout) {
        tvBloodOxygenTitle = mLinearLayout.findViewById(R.id.tvBloodOxygenTitle);
        layoutBloodOxygenShowData = mLinearLayout.findViewById(R.id.layoutBloodOxygenShowData);
        layoutBloodOxygenNoData = mLinearLayout.findViewById(R.id.layoutBloodOxygenNoData);

        mLinearLayout.findViewById(R.id.layoutBloodOxygenCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(homeActivity, Spo2OfflineDataDetailsActivity.class));
//                if (mBleDeviceTools.getSupportContinuousBloodOxygen()) {
//                    startActivity(new Intent(homeActivity, Spo2DetailsActivity.class));
//                } else if (mBleDeviceTools.getSupportOfflineBloodOxygen()) {
//                    startActivity(new Intent(homeActivity, Spo2MesureHistoryActivity.class));
//                }
            }
        });
    }

    private void initContinuitySpo2Info(ContinuitySpo2Info object) {
        if (tvBloodOxygenTitle == null) {
            return;
        }
        if (object != null) {
            ContinuitySpo2Model mContinuitySpo2Model = new ContinuitySpo2Model(object);
            String day_max = mContinuitySpo2Model.getContinuitySpo2DayMax();
            String day_min = mContinuitySpo2Model.getContinuitySpo2DayMin();
            String data = mContinuitySpo2Model.getContinuitySpo2Data();
            //最近一次数据
            String last_value = mContinuitySpo2Model.getLastValue();
            if (!JavaUtil.checkIsNull(last_value)) {
                tvBloodOxygenTitle.setText(getResources().getString(R.string.spo2_str) + " " + last_value + "%");
                layoutBloodOxygenNoData.setVisibility(View.GONE);
                layoutBloodOxygenShowData.setVisibility(View.VISIBLE);
            } else {
                layoutBloodOxygenNoData.setVisibility(View.VISIBLE);
                layoutBloodOxygenShowData.setVisibility(View.GONE);
            }
        } else {
            layoutBloodOxygenNoData.setVisibility(View.VISIBLE);
            layoutBloodOxygenShowData.setVisibility(View.GONE);
        }
    }

    private void initMeasureSpo2Info(MeasureSpo2Info object) {
        if (tvBloodOxygenTitle == null) {
            return;
        }
        if (object != null) {
            String last_value = object.getMeasure_spo2();
            if (!JavaUtil.checkIsNull(last_value)) {
                tvBloodOxygenTitle.setText(getResources().getString(R.string.spo2_str) + " " + last_value + "%");
                layoutBloodOxygenNoData.setVisibility(View.GONE);
                layoutBloodOxygenShowData.setVisibility(View.VISIBLE);
            } else {
                layoutBloodOxygenNoData.setVisibility(View.VISIBLE);
                layoutBloodOxygenShowData.setVisibility(View.GONE);
            }
        } else {
            layoutBloodOxygenNoData.setVisibility(View.VISIBLE);
            layoutBloodOxygenShowData.setVisibility(View.GONE);
        }
    }


    int CalibrationGrade = 2;
    boolean IsCalibrationGrade = true;

    private void showCalibrationdialog() {
        CalibrationGrade = mUserSetTools.get_blood_grade();
        int systilic = mUserSetTools.get_calibration_systolic();
        int diastolic = mUserSetTools.get_calibration_diastolic();
        final LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.dialog_calibration, null);


        final TextView dialog_calibration_0_max = textEntryView.findViewById(R.id.dialog_calibration_0_max);
        final TextView dialog_calibration_0_min = textEntryView.findViewById(R.id.dialog_calibration_0_min);
        final TextView dialog_calibration_1_max = textEntryView.findViewById(R.id.dialog_calibration_1_max);
        final TextView dialog_calibration_1_min = textEntryView.findViewById(R.id.dialog_calibration_1_min);
        final TextView dialog_calibration_2_max = textEntryView.findViewById(R.id.dialog_calibration_2_max);
        final TextView dialog_calibration_2_min = textEntryView.findViewById(R.id.dialog_calibration_2_min);
        final TextView dialog_calibration_3_max = textEntryView.findViewById(R.id.dialog_calibration_3_max);
        final TextView dialog_calibration_3_min = textEntryView.findViewById(R.id.dialog_calibration_3_min);
        final TextView dialog_calibration_4_max = textEntryView.findViewById(R.id.dialog_calibration_4_max);
        final TextView dialog_calibration_4_min = textEntryView.findViewById(R.id.dialog_calibration_4_min);

        final Button dialog_btn_calibration_grade = textEntryView.findViewById(R.id.dialog_btn_calibration_grade);
        final Button dialog_btn_calibration_value = textEntryView.findViewById(R.id.dialog_btn_calibration_value);


        final LinearLayout dialog_ll_calibration_grade = textEntryView.findViewById(R.id.dialog_ll_calibration_grade);
        final LinearLayout dialog_ll_calibration_value = textEntryView.findViewById(R.id.dialog_ll_calibration_value);

        final EditText dialog_edit_calibration_sbp = textEntryView.findViewById(R.id.dialog_edit_calibration_sbp);
        final EditText dialog_edit_calibration_dbp = textEntryView.findViewById(R.id.dialog_edit_calibration_dbp);

        final SeekBar dialog_calibration_grade_seekbar = textEntryView.findViewById(R.id.dialog_calibration_grade_seekbar);


        //等级校准
        if (CalibrationGrade < 0 || CalibrationGrade > 4) {
            CalibrationGrade = 2;

            IsCalibrationGrade = false;
            dialog_ll_calibration_grade.setVisibility(View.GONE);
            dialog_ll_calibration_value.setVisibility(View.VISIBLE);

            dialog_btn_calibration_grade.setBackgroundResource(R.drawable.dailog_calibration_choice_off);
            dialog_btn_calibration_grade.setTextColor(getResources().getColor(R.color.public_text_color1));
            dialog_btn_calibration_value.setBackgroundResource(R.drawable.dailog_calibration_choice_on);
            dialog_btn_calibration_value.setTextColor(getResources().getColor(R.color.public_text_color_white));
        }
        //精准值校准
        else {

            IsCalibrationGrade = true;
            dialog_ll_calibration_grade.setVisibility(View.VISIBLE);
            dialog_ll_calibration_value.setVisibility(View.GONE);

            dialog_btn_calibration_grade.setBackgroundResource(R.drawable.dailog_calibration_choice_on);
            dialog_btn_calibration_grade.setTextColor(getResources().getColor(R.color.public_text_color_white));
            dialog_btn_calibration_value.setBackgroundResource(R.drawable.dailog_calibration_choice_off);
            dialog_btn_calibration_value.setTextColor(getResources().getColor(R.color.public_text_color1));

        }


        if (systilic < DefaultVale.USER_SYSTOLIC_MIN || systilic > DefaultVale.USER_SYSTOLIC_MAX) {
            systilic = DefaultVale.USER_SYSTOLIC;
        }

        if (diastolic < DefaultVale.USER_DIASTOLIC_MIN || diastolic > DefaultVale.USER_DIASTOLIC_MAX) {
            diastolic = DefaultVale.USER_DIASTOLIC;
        }

        dialog_edit_calibration_sbp.setText(String.valueOf(systilic));
        dialog_edit_calibration_dbp.setText(String.valueOf(diastolic));


        //等级校准
        textEntryView.findViewById(R.id.dialog_btn_calibration_grade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IsCalibrationGrade = true;
                dialog_ll_calibration_grade.setVisibility(View.VISIBLE);
                dialog_ll_calibration_value.setVisibility(View.GONE);

                dialog_btn_calibration_grade.setBackgroundResource(R.drawable.dailog_calibration_choice_on);
                dialog_btn_calibration_grade.setTextColor(getResources().getColor(R.color.public_text_color_white));
                dialog_btn_calibration_value.setBackgroundResource(R.drawable.dailog_calibration_choice_off);
                dialog_btn_calibration_value.setTextColor(getResources().getColor(R.color.public_text_color1));

            }
        });


        //精准值校准
        textEntryView.findViewById(R.id.dialog_btn_calibration_value).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IsCalibrationGrade = false;
                dialog_ll_calibration_grade.setVisibility(View.GONE);
                dialog_ll_calibration_value.setVisibility(View.VISIBLE);

                dialog_btn_calibration_grade.setBackgroundResource(R.drawable.dailog_calibration_choice_off);
                dialog_btn_calibration_grade.setTextColor(getResources().getColor(R.color.public_text_color1));
                dialog_btn_calibration_value.setBackgroundResource(R.drawable.dailog_calibration_choice_on);
                dialog_btn_calibration_value.setTextColor(getResources().getColor(R.color.public_text_color_white));

            }
        });


        dialog_calibration_0_max.setVisibility(View.GONE);
        dialog_calibration_1_max.setVisibility(View.GONE);
        dialog_calibration_2_max.setVisibility(View.GONE);
        dialog_calibration_3_max.setVisibility(View.GONE);
        dialog_calibration_4_max.setVisibility(View.GONE);

        dialog_calibration_0_min.setVisibility(View.VISIBLE);
        dialog_calibration_1_min.setVisibility(View.VISIBLE);
        dialog_calibration_2_min.setVisibility(View.VISIBLE);
        dialog_calibration_3_min.setVisibility(View.VISIBLE);
        dialog_calibration_4_min.setVisibility(View.VISIBLE);


        switch (CalibrationGrade) {
            case 0:
                dialog_calibration_0_max.setVisibility(View.VISIBLE);
                dialog_calibration_0_min.setVisibility(View.GONE);
                break;
            case 1:
                dialog_calibration_1_max.setVisibility(View.VISIBLE);
                dialog_calibration_1_min.setVisibility(View.GONE);
                break;
            case 2:
                dialog_calibration_2_max.setVisibility(View.VISIBLE);
                dialog_calibration_2_min.setVisibility(View.GONE);
                break;
            case 3:
                dialog_calibration_3_max.setVisibility(View.VISIBLE);
                dialog_calibration_3_min.setVisibility(View.GONE);
                break;
            case 4:
                dialog_calibration_4_max.setVisibility(View.VISIBLE);
                dialog_calibration_4_min.setVisibility(View.GONE);
                break;
        }


        dialog_calibration_grade_seekbar.setProgress(CalibrationGrade);

        dialog_calibration_grade_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                CalibrationGrade = progress;

                dialog_calibration_0_max.setVisibility(View.GONE);
                dialog_calibration_1_max.setVisibility(View.GONE);
                dialog_calibration_2_max.setVisibility(View.GONE);
                dialog_calibration_3_max.setVisibility(View.GONE);
                dialog_calibration_4_max.setVisibility(View.GONE);

                dialog_calibration_0_min.setVisibility(View.VISIBLE);
                dialog_calibration_1_min.setVisibility(View.VISIBLE);
                dialog_calibration_2_min.setVisibility(View.VISIBLE);
                dialog_calibration_3_min.setVisibility(View.VISIBLE);
                dialog_calibration_4_min.setVisibility(View.VISIBLE);

                switch (CalibrationGrade) {
                    case 0:
                        dialog_calibration_0_max.setVisibility(View.VISIBLE);
                        dialog_calibration_0_min.setVisibility(View.GONE);
                        break;
                    case 1:
                        dialog_calibration_1_max.setVisibility(View.VISIBLE);
                        dialog_calibration_1_min.setVisibility(View.GONE);
                        break;
                    case 2:
                        dialog_calibration_2_max.setVisibility(View.VISIBLE);
                        dialog_calibration_2_min.setVisibility(View.GONE);
                        break;
                    case 3:
                        dialog_calibration_3_max.setVisibility(View.VISIBLE);
                        dialog_calibration_3_min.setVisibility(View.GONE);
                        break;
                    case 4:
                        dialog_calibration_4_max.setVisibility(View.VISIBLE);
                        dialog_calibration_4_min.setVisibility(View.GONE);
                        break;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        final AlertDialog dialog = new AlertDialog.Builder(context).setView(textEntryView).
                setPositiveButton(getString(R.string.dialog_yes), null)
                .setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.setTitle(getString(R.string.dialog_prompt));
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyLog.i(TAG, "测量校准 = CalibrationGrade = " + CalibrationGrade);


                if (IsCalibrationGrade) {

                    mUserSetTools.set_blood_grade(CalibrationGrade);
                    CalibrationUtils.handleCalibrationDialogGrade(TAG, mUserSetTools, CalibrationGrade);
                    gotoMeasure(false);

                } else {
                    mUserSetTools.set_blood_grade(DefaultVale.USER_BP_LEVEL);
                    String calibration_sbp = dialog_edit_calibration_sbp.getText().toString().trim();
                    String calibration_dbp = dialog_edit_calibration_dbp.getText().toString().trim();

                    handleCalibrationDialogValue(calibration_sbp, calibration_dbp);
                }
                dialog.dismiss();
            }
        });
    }

    private void gotoMeasure(boolean isCalibration) {
        //支持ECG-显示健康布局
        if (mBleDeviceTools.get_is_support_ecg() == 1) {
            Intent intent = null;
            //校准过直接进入测量
            if (isCalibration) {
                intent = new Intent(context, EcgMeasureActivity.class);
                intent.putExtra(IntentConstants.MesureType, IntentConstants.MesureType_Measure);
                startActivity(intent);
            }
            //没校准过，进行校准
            else {
                intent = new Intent(context, EcgMeasureActivity.class);
                intent.putExtra(IntentConstants.MesureType, IntentConstants.MesureType_Calibration);
                startActivity(intent);
            }

            //不支持ECG-显示运动布局
        } else {
            Intent intent = null;
            //校准过直接进入测量
            if (isCalibration) {
                intent = new Intent(context, PpgMeasureActivity.class);
                intent.putExtra(IntentConstants.MesureType, IntentConstants.MesureType_Measure);
                startActivity(intent);
            }
            //没校准过，进行校准
            else {
                intent = new Intent(context, PpgMeasureActivity.class);
                intent.putExtra(IntentConstants.MesureType, IntentConstants.MesureType_Calibration);
                startActivity(intent);
            }

        }
    }

    private void handleCalibrationDialogValue(String calibration_sbp, String calibration_dbp) {
        MyLog.i(TAG, "测量校准 = 精准值校准 = calibration_sbp = " + calibration_sbp);
        MyLog.i(TAG, "测量校准 = 精准值校准 = calibration_dbp = " + calibration_dbp);
        if (TextUtils.isEmpty(calibration_sbp) || TextUtils.isEmpty(calibration_dbp)) {
            AppUtils.showToast(context, R.string.jiaozhun_dailog_error);
            return;
        } else if (MyUtils.checkInputNumber(calibration_sbp) && MyUtils.checkInputNumber(calibration_dbp)) {

            int sbp = Integer.valueOf(calibration_sbp);
            int dbp = Integer.valueOf(calibration_dbp);

            if (sbp < DefaultVale.USER_SYSTOLIC_MIN || sbp > DefaultVale.USER_SYSTOLIC_MAX
                    || dbp < DefaultVale.USER_DIASTOLIC_MIN || dbp > DefaultVale.USER_DIASTOLIC_MAX) {
                AppUtils.showToast(context, R.string.jiaozhun_dailog_error);
                return;
            } else {
                mUserSetTools.set_calibration_systolic(sbp);
                mUserSetTools.set_calibration_diastolic(dbp);
                mUserSetTools.set_blood_grade(-1);
                gotoMeasure(false);
            }
        } else {
            AppUtils.showToast(context, R.string.jiaozhun_dailog_error);
            return;
        }
    }

    @BindView(R.id.layoutDeviceGps)
    LinearLayout layoutDeviceGps;
    @BindView(R.id.tvDeviceGpsSport)
    TextView tvDeviceGpsSport;
    @BindView(R.id.ivGpsStatus)
    ImageView ivGpsStatus;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deviceSportStatusEvent(DeviceSportStatusEvent event) {
        if (event.sportType != 0) {
            layoutDeviceGps.setVisibility(View.VISIBLE);
            if (event.paused) {
                tvDeviceGpsSport.setText(context.getResources().getString(R.string.device_sport_paused));
            } else {
                tvDeviceGpsSport.setText(context.getResources().getString(R.string.device_sport_runing));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void gpsSportDeviceStartEvent(GpsSportDeviceStartEvent event) {
        layoutDeviceGps.setVisibility(View.VISIBLE);
        switch (event.state) {
            case GpsSportDeviceStartEvent.SPORT_STATE_START:
                tvDeviceGpsSport.setText(context.getResources().getString(R.string.device_sport_runing));
                break;
            case GpsSportDeviceStartEvent.SPORT_STATE_PAUSE:
                tvDeviceGpsSport.setText(context.getResources().getString(R.string.device_sport_paused));
                break;
            case GpsSportDeviceStartEvent.SPORT_STATE_RESUME:
                mHandler.postDelayed(() -> {
                    tvDeviceGpsSport.setText(context.getResources().getString(R.string.device_sport_runing));
                }, 0);
                break;
            case GpsSportDeviceStartEvent.SPORT_STATE_STOP:
                tvDeviceGpsSport.setText(context.getResources().getString(R.string.device_sport_over));
                mHandler.postDelayed(() -> {
                    layoutDeviceGps.setVisibility(View.GONE);
                }, 1000);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void DeviceToAppSportStateEvent(DeviceToAppSportStateEvent event) {
        layoutDeviceGps.setVisibility(View.VISIBLE);
        switch (event.state) {
            case BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_START: // 发起运动
                tvDeviceGpsSport.setText(context.getResources().getString(R.string.device_sport_runing));
                break;
            case BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_PAUSE: // 运动已暂停
                tvDeviceGpsSport.setText(context.getResources().getString(R.string.device_sport_paused));
                break;
            case BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_RESUME: // 运动继续
                mHandler.postDelayed(() -> {
                    tvDeviceGpsSport.setText(context.getResources().getString(R.string.device_sport_runing));
                }, 0);
                break;
            case BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_STOP: // 结束运动
                tvDeviceGpsSport.setText(context.getResources().getString(R.string.device_sport_over));
                mHandler.postDelayed(() -> {
                    layoutDeviceGps.setVisibility(View.GONE);
                }, 1000);
                break;
            case BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_RESULT_YES: // 正在运动中…
                tvDeviceGpsSport.setText(context.getResources().getString(R.string.device_sport_runing));
                break;
            case BroadcastTools.TAG_DEVICE_TO_APP_SPORT_STATE_RESULT_NO: // 非运动状态…
                layoutDeviceGps.setVisibility(View.GONE);
                break;
        }
    }

    private void locationFail() {
        homeActivity.writeRXCharacteristic(BtSerializeation.sendSportState(0));
    }

    private void locationSuccess() {
        homeActivity.writeRXCharacteristic(BtSerializeation.sendSportState(1));
    }

    private void requestSportSate() {
        homeActivity.writeRXCharacteristic(BtSerializeation.sendSportState(2));
    }

    private void uploadSportDistance(float distance) {
//        homeActivity.writeRXCharacteristic(BtSerializeation.sendSportData(distance));
    }

}
