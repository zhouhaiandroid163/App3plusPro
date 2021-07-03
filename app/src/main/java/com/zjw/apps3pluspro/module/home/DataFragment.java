package com.zjw.apps3pluspro.module.home;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.zjw.apps3pluspro.eventbus.DataSyncCompleteEvent;
import com.zjw.apps3pluspro.eventbus.DeviceInfoEvent;
import com.zjw.apps3pluspro.eventbus.DeviceSportStatusEvent;
import com.zjw.apps3pluspro.eventbus.DeviceToAppSportStateEvent;
import com.zjw.apps3pluspro.eventbus.GpsSportDeviceStartEvent;
import com.zjw.apps3pluspro.eventbus.RefreshGpsInfoEvent;
import com.zjw.apps3pluspro.eventbus.SyncDeviceSportEvent;
import com.zjw.apps3pluspro.eventbus.SyncTimeLoadingEvent;
import com.zjw.apps3pluspro.eventbus.SyncTimeOutEvent;
import com.zjw.apps3pluspro.eventbus.tools.EventTools;
import com.zjw.apps3pluspro.module.device.ScanDeviceActivity;
import com.zjw.apps3pluspro.module.home.cycle.CycleActivity;
import com.zjw.apps3pluspro.module.home.cycle.CycleInitActivity;
import com.zjw.apps3pluspro.module.home.cycle.utils.MyCalendarUtils;
import com.zjw.apps3pluspro.module.home.entity.ContinuitySpo2Model;
import com.zjw.apps3pluspro.module.home.entity.ContinuityTempModel;
import com.zjw.apps3pluspro.module.home.entity.HeartModel;
import com.zjw.apps3pluspro.module.home.entity.MeasureTempModel;
import com.zjw.apps3pluspro.module.home.entity.SleepModel;
import com.zjw.apps3pluspro.module.home.exercise.StepHistoryActivity;
import com.zjw.apps3pluspro.module.home.heart.ContinuousHeartHistoryActivity;
import com.zjw.apps3pluspro.module.home.heart.PerHourOneHeartHistoryActivity;
import com.zjw.apps3pluspro.module.home.ppg.PpgMesureHistoryActivity;
import com.zjw.apps3pluspro.module.home.sleep.SleepHistoryActivity;
import com.zjw.apps3pluspro.module.home.spo2.Spo2OfflineDataDetailsActivity;
import com.zjw.apps3pluspro.module.home.sport.MoreSportActivity;
import com.zjw.apps3pluspro.module.home.sport.SportModleUtils;
import com.zjw.apps3pluspro.module.home.temp.TempDetailsActivity;
import com.zjw.apps3pluspro.module.home.temp.TempHistoryActivity;
import com.zjw.apps3pluspro.module.mine.user.TargetSettingActivity;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.dbmanager.HealthInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.HeartInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.MovementInfoUtils;
import com.zjw.apps3pluspro.sql.dbmanager.SleepInfoUtils;
import com.zjw.apps3pluspro.sql.entity.ContinuitySpo2Info;
import com.zjw.apps3pluspro.sql.entity.ContinuityTempInfo;
import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.sql.entity.HeartInfo;
import com.zjw.apps3pluspro.sql.entity.MeasureSpo2Info;
import com.zjw.apps3pluspro.sql.entity.MeasureTempInfo;
import com.zjw.apps3pluspro.sql.entity.MovementInfo;
import com.zjw.apps3pluspro.sql.entity.SleepInfo;
import com.zjw.apps3pluspro.sql.entity.SportModleInfo;
import com.zjw.apps3pluspro.utils.AnalyticalUtils;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.GpsSportManager;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.NewTimeUtils;
import com.zjw.apps3pluspro.utils.PageManager;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.RoundProgress2View;
import com.zjw.apps3pluspro.view.StepHistogramView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by android
 * on 2020/5/11.
 */
@SuppressLint("UseCompatLoadingForDrawables")
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
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.layoutCurDevice)
    ConstraintLayout layoutCurDevice;
    @BindView(R.id.layoutNoData)
    LinearLayout layoutNoData;
    @BindView(R.id.layoutPen)
    LinearLayout layoutPen;

    private HomeActivity homeActivity;
    private final BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    private final UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private final MovementInfoUtils mMovementInfoUtils = BaseApplication.getMovementInfoUtils();
    private final SleepInfoUtils mSleepInfoUtils = BaseApplication.getSleepInfoUtils();
    private final HeartInfoUtils mHeartInfoUtils = BaseApplication.getHeartInfoUtils();
    private final HealthInfoUtils mHealthInfoUtils = BaseApplication.getHealthInfoUtils();

    private final Handler mHandler = new Handler();

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
    @BindView(R.id.tvBodyWeight)
    TextView tvBodyWeight;
    @BindView(R.id.tvBodyWeightUnit)
    TextView tvBodyWeightUnit;
    @BindView(R.id.roundViewDistance)
    RoundProgress2View roundViewDistance;
    @BindView(R.id.roundViewStep)
    RoundProgress2View roundViewStep;
    @BindView(R.id.roundViewCal)
    RoundProgress2View roundViewCal;
    @BindView(R.id.ivActivitiesTopDistance)
    ImageView ivActivitiesTopDistance;
    @BindView(R.id.ivActivitiesTopStep)
    ImageView ivActivitiesTopStep;
    @BindView(R.id.ivActivitiesTopCal)
    ImageView ivActivitiesTopCal;
    @BindView(R.id.layoutMainView)
    RelativeLayout layoutMainView;

    @Override
    public void initData() {
        layoutMainView.setOnClickListener(v -> {
            startActivity(new Intent(homeActivity, StepHistoryActivity.class));
        });

        String weightValue = String.valueOf(mUserSetTools.get_user_weight());
        if (!JavaUtil.checkIsNull(weightValue)) {
            if (mUserSetTools.get_user_unit_type()) {
                tvBodyWeight.setText(weightValue);
                tvBodyWeightUnit.setText(getString(R.string.kg));
            } else {
                tvBodyWeight.setText(MyUtils.KGToLBString(weightValue, context));
                tvBodyWeightUnit.setText(getString(R.string.unit_lb));
            }
        }
        layoutSync.setVisibility(View.VISIBLE);
        layoutPen.setVisibility(View.VISIBLE);
//        layoutNoData.setVisibility(View.GONE);
        updateTagUi();
        mLayoutInflater = LayoutInflater.from(context);
        // loading data
        swipeRefreshLayout.setOnRefreshListener(() -> {
            closeSwipeRefresh();
            if (BleService.syncState) {
                return;
            }
            if (BleService.isSyncSportData) {
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

    @OnClick({R.id.layoutSync, R.id.layoutAddDevice, R.id.layoutPen, R.id.layoutMoreSport, R.id.layoutHeart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layoutHeart:
                if (mBleDeviceTools.get_is_support_persist_heart() == 1) {
                    MyLog.i(TAG, "跳转到连续心率界面");
                    startActivity(new Intent(homeActivity, ContinuousHeartHistoryActivity.class));
                } else {
                    MyLog.i(TAG, "跳转到整点心率界面");
                    startActivity(new Intent(homeActivity, PerHourOneHeartHistoryActivity.class));
                }
                break;
            case R.id.layoutPen:
                startActivity(new Intent(context, TargetSettingActivity.class));
                break;
            case R.id.layoutSync:
                if (BleService.syncState) {
                    return;
                }
                if (BleService.isSyncSportData) {
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
            case R.id.layoutMoreSport:
                startActivity(new Intent(context, MoreSportActivity.class));
                break;
        }
    }

    private LayoutInflater mLayoutInflater;

    private void getAllData() {
        try {
            DataManager.getInstance().getSportDay(homeActivity, true, MyTime.getTime(), new DataManager.GetDataSuccess() {
                @Override
                public void onSuccess(Object movementInfo) {
                    closeSwipeRefresh();
                    if (movementInfo != null) {
                        MyLog.i(TAG, "getAllData movementInfo = " + movementInfo);
                        initExerciseData((MovementInfo) movementInfo);
                    }
                }
            });
            DataManager.getInstance().getSleepDay(homeActivity, true, MyTime.getTime(), new DataManager.GetDataSuccess() {
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

            if (mBleDeviceTools.get_is_support_spo2()) {
                if (mBleDeviceTools.getSupportContinuousBloodOxygen()) {
                    DataManager.getInstance().getContinuitySpo2(homeActivity, true, new DataManager.GetDataSuccess() {
                        @Override
                        public void onSuccess(Object object) {
                            if (object == null) {
                                initSpo2Info("");
                            } else {
                                ContinuitySpo2Model mContinuitySpo2Model = new ContinuitySpo2Model((ContinuitySpo2Info) object);
                                initSpo2Info(mContinuitySpo2Model.getLastValue());
                            }
                        }
                    });
                } else if (mBleDeviceTools.getSupportOfflineBloodOxygen()) {
                    DataManager.getInstance().getOfflineSpo2(homeActivity, true, new DataManager.GetDataSuccess() {
                        @Override
                        public void onSuccess(Object object) {
                            if (object == null) {
                                initSpo2Info("");
                            } else {
                                MeasureSpo2Info measureSpo2Info = (MeasureSpo2Info) object;
                                initSpo2Info(measureSpo2Info.getMeasure_spo2());
                            }
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void initSpo2Info(String lastValue) {
        if (tvValue_blood_oxygen == null) {
            return;
        }
        if (!TextUtils.isEmpty(lastValue)) {
            tvValue_blood_oxygen.setText(lastValue + "%");
            mRoundProgress2View_blood_oxygen.setFullProgress(getResources().getColor(R.color.color_CC6761));
        } else {
            tvValue_blood_oxygen.setText(getResources().getString(R.string.no_data_default) + "%");
            mRoundProgress2View_blood_oxygen.setFullProgress(getResources().getColor(R.color.transparent));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initViewState();
        refreshView();
//        updateUi();
        getAllData();
        if (BleService.currentGpsSportState != -1 && BleService.currentGpsSportState != GpsSportDeviceStartEvent.SPORT_STATE_STOP) {
            gpsSportDeviceStartEvent(new GpsSportDeviceStartEvent(BleService.currentGpsSportState));
        }
    }

    @BindView(R.id.layoutLeft)
    LinearLayout layoutLeft;
    @BindView(R.id.layoutRight)
    LinearLayout layoutRight;

    @SuppressLint("InflateParams")
    private void refreshView() {
        layoutLeft.removeAllViews();
        layoutRight.removeAllViews();
        for (int i = 0; i < PageManager.getInstance().pageApp2List.size(); i++) {
            LinearLayout mLinearLayout;
            int type = PageManager.getInstance().pageApp2List.get(i);
            switch (type) {
                case PageManager.PAGE_APP2_DISTANCE:
                    mLinearLayout = (LinearLayout) mLayoutInflater.inflate(R.layout.activities_page1_view, null);
                    findDistance(mLinearLayout);
                    break;
                case PageManager.PAGE_APP2_STEPS:
                    mLinearLayout = (LinearLayout) mLayoutInflater.inflate(R.layout.activities_page1_view, null);
                    findSteps(mLinearLayout);
                    break;
                case PageManager.PAGE_APP2_CAL:
                    mLinearLayout = (LinearLayout) mLayoutInflater.inflate(R.layout.activities_page1_view, null);
                    findCal(mLinearLayout);
                    break;
                case PageManager.PAGE_APP2_WATER:
                    mLinearLayout = (LinearLayout) mLayoutInflater.inflate(R.layout.activities_page1_view, null);
                    break;
                case PageManager.PAGE_APP2_SLEEP:
                    mLinearLayout = (LinearLayout) mLayoutInflater.inflate(R.layout.activities_page1_view, null);
                    findSleep(mLinearLayout);
                    break;
                case PageManager.PAGE_APP2_TEMPERATURE:
                    mLinearLayout = (LinearLayout) mLayoutInflater.inflate(R.layout.activities_page2_view, null);
                    findTemp(mLinearLayout);
                    break;
                case PageManager.PAGE_APP2_BLOOD_OXYGEN:
                    mLinearLayout = (LinearLayout) mLayoutInflater.inflate(R.layout.activities_page2_view, null);
                    findBloodOxygen(mLinearLayout);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }
            if (i % 2 == 0) {
                layoutLeft.addView(mLinearLayout);
            } else {
                layoutRight.addView(mLinearLayout);
            }
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
    public void syncDeviceSportEvent(SyncDeviceSportEvent event) {
//        DataManager.getInstance().getSportModleData(object -> initSport((SportModleInfo) object));
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


    @SuppressLint({"StringFormatInvalid", "StringFormatMatches"})
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
                        tvSyncState.setText(String.format(getResources().getString(R.string.sync_data_time), time));
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

    TextView tvActivitiesPageTitle_distance, tvProgress_distance, tvValue_distance, tvValueUnit_distance, tvGoal_distance;
    RoundProgress2View mRoundProgress2View_distance;
    TextView tvActivitiesPageTitle_step, tvProgress_step, tvValue_step, tvValueUnit_step, tvGoal_step;
    RoundProgress2View mRoundProgress2View_step;
    TextView tvActivitiesPageTitle_cal, tvProgress_cal, tvValue_cal, tvValueUnit_cal, tvGoal_cal;
    RoundProgress2View mRoundProgress2View_cal;

    private void findDistance(LinearLayout mLinearLayout) {
        tvActivitiesPageTitle_distance = mLinearLayout.findViewById(R.id.tvActivitiesPageTitle);
        tvProgress_distance = mLinearLayout.findViewById(R.id.tvProgress);
        tvValue_distance = mLinearLayout.findViewById(R.id.tvValue);
        tvValueUnit_distance = mLinearLayout.findViewById(R.id.tvValueUnit);
        tvGoal_distance = mLinearLayout.findViewById(R.id.tvGoal);
        mRoundProgress2View_distance = mLinearLayout.findViewById(R.id.mRoundProgress2View);
        mRoundProgress2View_distance.setFillingColor(getResources().getColor(R.color.color_FFA557));

        tvActivitiesPageTitle_distance.setText(getResources().getString(R.string.sport_distance));
        mLinearLayout.findViewById(R.id.ivIcon).setBackground(getResources().getDrawable(R.mipmap.page_app2_distance));
        mLinearLayout.setOnClickListener(v -> {
            startActivity(new Intent(homeActivity, StepHistoryActivity.class));
        });
    }

    private void findSteps(LinearLayout mLinearLayout) {
        tvActivitiesPageTitle_step = mLinearLayout.findViewById(R.id.tvActivitiesPageTitle);
        tvProgress_step = mLinearLayout.findViewById(R.id.tvProgress);
        tvValue_step = mLinearLayout.findViewById(R.id.tvValue);
        tvValueUnit_step = mLinearLayout.findViewById(R.id.tvValueUnit);
        tvGoal_step = mLinearLayout.findViewById(R.id.tvGoal);
        mRoundProgress2View_step = mLinearLayout.findViewById(R.id.mRoundProgress2View);
        mRoundProgress2View_step.setFillingColor(getResources().getColor(R.color.color_57FFB5));

        tvActivitiesPageTitle_step.setText(getResources().getString(R.string.steps));
        mLinearLayout.findViewById(R.id.ivIcon).setBackground(getResources().getDrawable(R.mipmap.page_app2_step));
        mLinearLayout.setOnClickListener(v -> {
            startActivity(new Intent(homeActivity, StepHistoryActivity.class));
        });
    }

    private void findCal(LinearLayout mLinearLayout) {
        tvActivitiesPageTitle_cal = mLinearLayout.findViewById(R.id.tvActivitiesPageTitle);
        tvProgress_cal = mLinearLayout.findViewById(R.id.tvProgress);
        tvValue_cal = mLinearLayout.findViewById(R.id.tvValue);
        tvValueUnit_cal = mLinearLayout.findViewById(R.id.tvValueUnit);
        tvGoal_cal = mLinearLayout.findViewById(R.id.tvGoal);
        mRoundProgress2View_cal = mLinearLayout.findViewById(R.id.mRoundProgress2View);
        mRoundProgress2View_cal.setFillingColor(getResources().getColor(R.color.color_FFD857));

        tvActivitiesPageTitle_cal.setText(getResources().getString(R.string.sport_calorie));
        mLinearLayout.findViewById(R.id.ivIcon).setBackground(getResources().getDrawable(R.mipmap.page_app2_cal));
        mLinearLayout.setOnClickListener(v -> {
            startActivity(new Intent(homeActivity, StepHistoryActivity.class));
        });
    }

    @SuppressLint("SetTextI18n")
    public void initExerciseData(MovementInfo mMovementInfo) {
        if (tvActivitiesPageTitle_distance == null) {
            return;
        }
        String steps = !JavaUtil.checkIsNull(mMovementInfo.getTotal_step()) ? mMovementInfo.getTotal_step() : "0";
        String cal = !JavaUtil.checkIsNull(mMovementInfo.getCalorie()) ? mMovementInfo.getCalorie() : "0";
        String distance = !JavaUtil.checkIsNull(mMovementInfo.getDisance()) ? mMovementInfo.getDisance() : "0";
        distance = distance.replace(",", ".");

        String distanceGoal = mUserSetTools.get_user_distance_target();
        String stepGoal = mUserSetTools.get_user_exercise_target();
        String calGoal = mUserSetTools.get_user_cal_target();

        float distanceProgress;
        if (mBleDeviceTools.get_device_unit() == 1) { // 公制
            distance = AppUtils.GetTwoFormat(Float.parseFloat(distance));
            tvValueUnit_distance.setText(getResources().getString(R.string.sport_distance_unit));
            distanceProgress = Float.parseFloat(distance) / Float.parseFloat(distanceGoal);
            tvGoal_distance.setText(getResources().getString(R.string.goal) + distanceGoal + getResources().getString(R.string.sport_distance_unit));
        } else {
            distance = AppUtils.GetTwoFormat(Float.parseFloat(BleTools.getBritishSystem(steps)));
            tvValueUnit_distance.setText(getResources().getString(R.string.unit_mi));
            distanceProgress = Float.parseFloat(BleTools.getBritishSystem(steps)) / Float.parseFloat(distanceGoal);
            tvGoal_distance.setText(getResources().getString(R.string.goal) + distanceGoal + getResources().getString(R.string.unit_mi));
        }

        tvGoal_step.setText(getResources().getString(R.string.goal) + Integer.parseInt(stepGoal) / 1000 + "k");

        int calString = Integer.parseInt(calGoal);
        if (calString < 1000) {
            tvGoal_cal.setText(getResources().getString(R.string.goal) + calGoal);
        } else {
            tvGoal_cal.setText(getResources().getString(R.string.goal) + calString / 1000.0 + "k");
        }

        tvValueUnit_step.setText("s");
        tvValueUnit_cal.setText(getResources().getString(R.string.big_calory));

        if (!JavaUtil.checkIsNull(steps)) {
            mUserSetTools.set_user_stpe(Integer.parseInt(steps));
        } else {
            mUserSetTools.set_user_stpe(0);
            tvValue_step.setText("0");
            tvValue_distance.setText("0");
            tvValue_cal.setText("0");
            mRoundProgress2View_distance.setProgress(0, 0, 0);
            mRoundProgress2View_step.setProgress(0, 0, 0);
            mRoundProgress2View_cal.setProgress(0, 0, 0);
            tvProgress_distance.setText("0%");
            tvProgress_step.setText("0%");
            tvProgress_cal.setText("0%");
            tvProgress_distance.setTextColor(getResources().getColor(R.color.white));
            tvProgress_step.setTextColor(getResources().getColor(R.color.white));
            tvProgress_cal.setTextColor(getResources().getColor(R.color.white));
            ivActivitiesTopDistance.setBackground(getResources().getDrawable(R.mipmap.activities_top_distance));
            ivActivitiesTopStep.setBackground(getResources().getDrawable(R.mipmap.activities_top_step));
            ivActivitiesTopCal.setBackground(getResources().getDrawable(R.mipmap.activities_top_cal));
            roundViewDistance.setProgress(0, 0, 0);
            roundViewStep.setProgress(0, 0, 0);
            roundViewCal.setProgress(0, 0, 0);
            return;
        }
        tvProgress_distance.setTextColor(getResources().getColor(R.color.color_313841));
        tvProgress_step.setTextColor(getResources().getColor(R.color.color_313841));
        tvProgress_cal.setTextColor(getResources().getColor(R.color.color_313841));

        tvValue_distance.setText(distance);
        tvProgress_distance.setText((int) (distanceProgress * 100) + "%");
        if (distanceProgress > 0 && (int) (distanceProgress * 100) == 0) {
            tvProgress_distance.setText("1%");
        }
        if (distanceProgress > 100) {
            tvProgress_distance.setText("100%");
        }
        mRoundProgress2View_distance.setProgress(0, distanceProgress, 0);

        tvValue_step.setText(steps);
        float stepProgress = (Integer.parseInt(steps) * 1f / Integer.parseInt(stepGoal));
        tvProgress_step.setText((int) (stepProgress * 100) + "%");
        if (stepProgress > 0 && (int) (stepProgress * 100) == 0) {
            tvProgress_step.setText("1%");
        }
        if (stepProgress > 100) {
            tvProgress_step.setText("100%");
        }
        mRoundProgress2View_step.setProgress(0, stepProgress, 0);

        tvValue_cal.setText(cal);
        float calProgress = Integer.parseInt(cal) * 1f / Integer.parseInt(calGoal);
        tvProgress_cal.setText((int) (calProgress * 100) + "%");
        if (calProgress > 0 && (int) (calProgress * 100) == 0) {
            tvProgress_cal.setText("1%");
        }
        if (calProgress > 100) {
            tvProgress_cal.setText("100%");
        }
        mRoundProgress2View_cal.setProgress(0, calProgress, 0);

        ivActivitiesTopDistance.setBackground(getResources().getDrawable(R.mipmap.activities_top_distance_1));
        ivActivitiesTopStep.setBackground(getResources().getDrawable(R.mipmap.activities_top_step_1));
        ivActivitiesTopCal.setBackground(getResources().getDrawable(R.mipmap.activities_top_cal_1));
        roundViewDistance.setProgress(0, distanceProgress, 0);
        roundViewStep.setProgress(0, stepProgress, 0);
        roundViewCal.setProgress(0, calProgress, 0);
    }

    TextView tvActivitiesPageTitle_sleep, tvProgress_sleep, tvValue_sleep, tvValueUnit_sleep, tvGoal_sleep, tvSleepHour, tvSleepMinute;
    RoundProgress2View mRoundProgress2View_sleep;

    private void findSleep(LinearLayout mLinearLayout) {
        tvActivitiesPageTitle_sleep = mLinearLayout.findViewById(R.id.tvActivitiesPageTitle);
        tvProgress_sleep = mLinearLayout.findViewById(R.id.tvProgress);
        tvValue_sleep = mLinearLayout.findViewById(R.id.tvValue);
        tvValueUnit_sleep = mLinearLayout.findViewById(R.id.tvValueUnit);
        tvGoal_sleep = mLinearLayout.findViewById(R.id.tvGoal);
        mRoundProgress2View_sleep = mLinearLayout.findViewById(R.id.mRoundProgress2View);
        mRoundProgress2View_sleep.setFillingColor(getResources().getColor(R.color.color_57ABFF));

        mLinearLayout.findViewById(R.id.layoutSleep).setVisibility(View.VISIBLE);
        mLinearLayout.findViewById(R.id.tvValue).setVisibility(View.GONE);
        mLinearLayout.findViewById(R.id.tvValueUnit).setVisibility(View.GONE);
        tvSleepHour = mLinearLayout.findViewById(R.id.tvSleepHour);
        tvSleepMinute = mLinearLayout.findViewById(R.id.tvSleepMinute);

        mLinearLayout.setOnClickListener(v -> startActivity(new Intent(homeActivity, SleepHistoryActivity.class)));

        tvActivitiesPageTitle_sleep.setText(getResources().getString(R.string.title_sleep));
        mLinearLayout.findViewById(R.id.ivIcon).setBackground(getResources().getDrawable(R.mipmap.page_app2_sleep));
    }

    @SuppressLint("SetTextI18n")
    public void initSleepData(SleepInfo mSleepInfo) {
        if (tvActivitiesPageTitle_sleep == null) {
            return;
        }
        String targetSleep = mUserSetTools.get_user_sleep_target();
        if (!JavaUtil.checkIsNull(mSleepInfo.getData())) {
            SleepModel mSleepModel = new SleepModel(mSleepInfo);
            String sleep_time = mSleepModel.getSleepSleepTime();

            tvProgress_sleep.setText(AnalyticalUtils.getCompletionRateSleep(mUserSetTools, sleep_time) + "%");
            tvProgress_sleep.setTextColor(getResources().getColor(R.color.color_313841));
            tvSleepHour.setText(MyTime.getSleepTime_H(sleep_time, "0"));
            tvSleepMinute.setText(MyTime.getSleepTime_H(sleep_time, "0"));
            mRoundProgress2View_sleep.setProgress(0, Integer.parseInt(sleep_time) * 1f / Integer.parseInt(targetSleep), 0);
        } else {
            tvSleepHour.setText("0");
            tvSleepMinute.setText("0");
            tvProgress_sleep.setText("0%");
            tvProgress_sleep.setTextColor(getResources().getColor(R.color.white));
            mRoundProgress2View_sleep.setProgress(0, 0, 0);
        }
        double hour = Integer.parseInt(targetSleep) / 60.0;
        tvGoal_sleep.setText(getResources().getString(R.string.goal) + hour + getResources().getString(R.string.hour));
    }

    TextView tvActivitiesPageTitle_temp, tvValue_temp;
    RoundProgress2View mRoundProgress2View_temp;
    ImageView ivIcon_temp;

    TextView tvActivitiesPageTitle_blood_oxygen, tvValue_blood_oxygen;
    RoundProgress2View mRoundProgress2View_blood_oxygen;
    ImageView ivIcon_blood_oxygen;

    private void findTemp(LinearLayout mLinearLayout) {
        tvActivitiesPageTitle_temp = mLinearLayout.findViewById(R.id.tvActivitiesPageTitle);
        tvValue_temp = mLinearLayout.findViewById(R.id.tvValue);
        ivIcon_temp = mLinearLayout.findViewById(R.id.ivIcon);
        mRoundProgress2View_temp = mLinearLayout.findViewById(R.id.mRoundProgress2View);

        tvActivitiesPageTitle_temp.setText(getResources().getString(R.string.temp_body));
        ivIcon_temp.setBackground(getResources().getDrawable(R.mipmap.page_app2_temperture));

        mLinearLayout.setOnClickListener(v -> {
            if (mBleDeviceTools.getSupportContinuousTemp()) {
                startActivity(new Intent(homeActivity, TempDetailsActivity.class));
            } else if (mBleDeviceTools.getSupportOfflineTemp()) {
                startActivity(new Intent(homeActivity, TempHistoryActivity.class));
            }
        });
    }

    private void findBloodOxygen(LinearLayout mLinearLayout) {
        tvActivitiesPageTitle_blood_oxygen = mLinearLayout.findViewById(R.id.tvActivitiesPageTitle);
        tvValue_blood_oxygen = mLinearLayout.findViewById(R.id.tvValue);
        ivIcon_blood_oxygen = mLinearLayout.findViewById(R.id.ivIcon);
        mRoundProgress2View_blood_oxygen = mLinearLayout.findViewById(R.id.mRoundProgress2View);

        tvActivitiesPageTitle_blood_oxygen.setText(getResources().getString(R.string.spo2_str));
        ivIcon_blood_oxygen.setBackground(getResources().getDrawable(R.mipmap.page_app2_blood_oxygen));

        mLinearLayout.setOnClickListener(v -> startActivity(new Intent(homeActivity, Spo2OfflineDataDetailsActivity.class)));
    }

    @BindView(R.id.tvHeart)
    TextView tvHeart;

    private void initHeartData(HeartInfo mHeartInfo) {
        if (tvHeart == null) {
            return;
        }
        HeartModel mHeartModel = new HeartModel(mHeartInfo);
        String lastHeart = !JavaUtil.checkIsNull(mHeartModel.getLastHeart()) ? mHeartModel.getLastHeart() : "0";
        if (!JavaUtil.checkIsNull(lastHeart)) {
            tvHeart.setText(lastHeart);
        } else {
            tvHeart.setText(getResources().getString(R.string.no_data_default));
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
    }

    private void updateTagUi() {
        PageManager.getInstance().pageApp2List.clear();
        PageManager.getInstance().pageApp2List.add(PageManager.PAGE_APP2_DISTANCE);
        PageManager.getInstance().pageApp2List.add(PageManager.PAGE_APP2_STEPS);
        PageManager.getInstance().pageApp2List.add(PageManager.PAGE_APP2_CAL);
        PageManager.getInstance().pageApp2List.add(PageManager.PAGE_APP2_SLEEP);

        if ((mBleDeviceTools.get_is_support_temp() && mBleDeviceTools.getSupportContinuousTemp()) ||
                mBleDeviceTools.get_is_support_temp() && mBleDeviceTools.getSupportOfflineTemp()) {
            PageManager.getInstance().pageApp2List.add(PageManager.PAGE_APP2_TEMPERATURE);
        }

        if ((mBleDeviceTools.get_is_support_spo2() && mBleDeviceTools.getSupportContinuousBloodOxygen())
                || mBleDeviceTools.get_is_support_spo2() && mBleDeviceTools.getSupportOfflineBloodOxygen()) {
            PageManager.getInstance().pageApp2List.add(PageManager.PAGE_APP2_BLOOD_OXYGEN);
        }
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

    @SuppressLint("SetTextI18n")
    private void initContinuityTempDay(ContinuityTempInfo mContinuityTempInfo) {
        if (tvActivitiesPageTitle_temp == null) {
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
                tvValue_temp.setText(getResources().getString(R.string.no_data_default));
                mRoundProgress2View_temp.setFullProgress(getResources().getColor(R.color.transparent));
            } else {
                tvValue_temp.setText(last_body_value + unit);
                mRoundProgress2View_temp.setFullProgress(getResources().getColor(R.color.color_CA9B71));
            }
        } else {
            tvValue_temp.setText(getResources().getString(R.string.no_data_default));
            mRoundProgress2View_temp.setFullProgress(getResources().getColor(R.color.transparent));
        }
    }

    @SuppressLint("SetTextI18n")
    private void initOffineTempDay(MeasureTempInfo mContinuityTempInfo) {
        if (tvActivitiesPageTitle_temp == null) {
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
                tvValue_temp.setText(getResources().getString(R.string.no_data_default));
            } else {
                tvValue_temp.setText(last_body_value + unit);
            }
        } else {
            tvValue_temp.setText(getResources().getString(R.string.no_data_default));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshGpsInfoEvent(RefreshGpsInfoEvent event) {
        if (ivGpsStatus != null) {
            switch (event.gpsInfo.gpsAccuracy) {
                case GpsSportManager.GpsInfo.GPS_LOW:
                    ivGpsStatus.setBackground(getResources().getDrawable(R.mipmap.gps_low_bg));
                    break;
                case GpsSportManager.GpsInfo.GPS_MEDIUM:
                    ivGpsStatus.setBackground(getResources().getDrawable(R.mipmap.gps_medium_bg));
                    break;
                case GpsSportManager.GpsInfo.GPS_HIGH:
                    ivGpsStatus.setBackground(getResources().getDrawable(R.mipmap.gps_high_bg));
                    break;
            }
        }
    }

}
