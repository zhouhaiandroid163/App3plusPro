package com.zjw.apps3pluspro.module.home.ppg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.adapter.MesureHistoryListAdapter;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.HealthBean;
import com.zjw.apps3pluspro.bleservice.BleService;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.dbmanager.HealthInfoUtils;
import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.view.mycalendar.MyCalendarUtils;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.CalibrationUtils;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.NewTimeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.utils.MyTime;

import org.json.JSONObject;

import java.util.List;


/**
 * PPG测量历史
 */
public class PpgMesureHistoryActivity extends BaseActivity implements View.OnClickListener,
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnMonthChangeListener,
        CalendarView.OnYearChangeListener {
    private final String TAG = PpgMesureHistoryActivity.class.getSimpleName();
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    //数据库存储
    private HealthInfoUtils mHealthInfoUtils = BaseApplication.getHealthInfoUtils();
    private Context mContext;

    //日期相关
    private String registTime;
    private String selectionDate;

    //列表
    private ListView ppg_mesure_history_list;
    private MesureHistoryListAdapter mEcgMesureHistoryListAdapter;

    //标题
    private TextView public_head_title;

    //日历
    private LinearLayout public_new_calendar_top_lin;
    CalendarLayout mCalendarLayout;
    CalendarView mCalendarView;

    //日历
    private LinearLayout layoutData, layoutNoData;


    //加载相关
    private WaitDialog waitDialog;

    @Override
    protected int setLayoutId() {
        isTextDark = false;
        bgColor = R.color.title_bg_ecg;
        return R.layout.activity_ppg_mesure_history;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = PpgMesureHistoryActivity.this;
        waitDialog = new WaitDialog(mContext);
        initView();
        initSetAdapter();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getHealthtDay(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
    }


    private void initView() {

        public_head_title = (TextView) findViewById(R.id.public_head_title);
        public_head_title.setText(getResources().getString(R.string.blood_pressure));
        findViewById(R.id.layoutTitle).setBackground(ContextCompat.getDrawable(this, R.color.title_bg_ecg));

        findViewById(R.id.layoutCalendar).setOnClickListener(this);

        //日历
        layoutData = (LinearLayout) findViewById(R.id.layoutData);
        layoutNoData = (LinearLayout) findViewById(R.id.layoutNoData);
        mCalendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mCalendarView.setOnCalendarSelectListener(this);


        ppg_mesure_history_list = (ListView) findViewById(R.id.ppg_mesure_history_list);


        findViewById(R.id.ppg_history_to_measure).setOnClickListener(this);


    }

    void initSetAdapter() {


        mEcgMesureHistoryListAdapter = new MesureHistoryListAdapter(mContext, MesureHistoryListAdapter.TYPE_PPG);
        ppg_mesure_history_list.setAdapter(mEcgMesureHistoryListAdapter);
        ppg_mesure_history_list
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                        HealthInfo mHealthInfo = mEcgMesureHistoryListAdapter.getDevice(arg2);

                        if (mHealthInfo != null) {
                            Intent intent = new Intent(mContext, PpgMesureDetailsActivity.class);
                            intent.putExtra(IntentConstants.HealthInfo, mHealthInfo);
                            startActivity(intent);
                        }
                    }
                });

        //listView长按事件
        ppg_mesure_history_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                showDeleteHistoryData(position);
                return true;
            }
        });


    }

    void initData() {


        MyLog.i(TAG, "initData");

        registTime = BaseApplication.getRegisterTime();

        if (JavaUtil.checkIsNull(registTime)) {
            registTime = DefaultVale.USER_DEFULT_REGISTER_TIME;
        } else {
            registTime = registTime.split(" ")[0];
        }
        //模拟数据
//        registTime = "2019-05-09";

        MyLog.i(TAG, "注册日期为 = " + registTime);

        selectionDate = MyTime.getTime();
        mCalendarView.setSchemeDate(NewTimeUtils.getCycData(registTime));

//        //模拟数据
//        List<HealthInfo> health_list = new ArrayList<>();
//        HealthInfo mHealthInfo1 = new HealthInfo();
//        mHealthInfo1.setMeasure_time("2019-03-30 09:25:21");
//        mHealthInfo1.setPpg_data(SimulationPpgData.ppg_data);
//        mHealthInfo1.setEcg_data(SimulationEcgData.ecg_data);
//        mHealthInfo1.setHealth_heart("70");
//        mHealthInfo1.setHealth_systolic("125");
//        mHealthInfo1.setHealth_diastolic("72");
//        mHealthInfo1.setIndex_health_index("79");
//        mHealthInfo1.setIndex_fatigue_index("81");
//        mHealthInfo1.setIndex_cardiac_function("72");
//        mHealthInfo1.setIndex_body_quality("91");
//        mHealthInfo1.setIndex_body_load("85");
//        health_list.add(mHealthInfo1);
//
//
//        HealthInfo mHealthInfo2 = new HealthInfo();
//        mHealthInfo2.setMeasure_time("2019-03-30 10:15:11");
//        mHealthInfo2.setPpg_data(SimulationPpgData.ppg_data);
//        mHealthInfo2.setEcg_data(SimulationEcgData.ecg_data);
//        mHealthInfo2.setHealth_heart("72");
//        mHealthInfo2.setHealth_systolic("130");
//        mHealthInfo2.setHealth_diastolic("75");
//        mHealthInfo2.setIndex_health_index("81");
//        mHealthInfo2.setIndex_fatigue_index("83");
//        mHealthInfo2.setIndex_cardiac_function("70");
//        mHealthInfo2.setIndex_body_quality("81");
//        mHealthInfo2.setIndex_body_load("91");
//        health_list.add(mHealthInfo2);
//
//        HealthInfo mHealthInfo3 = new HealthInfo();
//        mHealthInfo3.setMeasure_time("2019-03-30 10:20:37");
//        mHealthInfo3.setPpg_data(SimulationPpgData.ppg_data);
//        mHealthInfo3.setEcg_data(SimulationEcgData.ecg_data);
//        mHealthInfo3.setHealth_heart("65");
//        mHealthInfo3.setHealth_systolic("120");
//        mHealthInfo3.setHealth_diastolic("67");
//        mHealthInfo3.setIndex_health_index("89");
//        mHealthInfo3.setIndex_health_index("85");
//        mHealthInfo3.setIndex_fatigue_index("82");
//        mHealthInfo3.setIndex_cardiac_function("80");
//        mHealthInfo3.setIndex_body_quality("85");
//        mHealthInfo3.setIndex_body_load("89");
//        health_list.add(mHealthInfo3);
//
//        HealthInfo mHealthInfo4 = new HealthInfo();
//        mHealthInfo4.setMeasure_time("2019-03-30 11:35:18");
//        mHealthInfo4.setPpg_data(SimulationPpgData.ppg_data);
//        mHealthInfo4.setEcg_data(SimulationEcgData.ecg_data);
//        mHealthInfo4.setHealth_heart("67");
//        mHealthInfo4.setHealth_systolic("121");
//        mHealthInfo4.setHealth_diastolic("68");
//        mHealthInfo4.setIndex_health_index("91");
//        mHealthInfo4.setIndex_health_index("92");
//        mHealthInfo4.setIndex_fatigue_index("89");
//        mHealthInfo4.setIndex_cardiac_function("95");
//        mHealthInfo4.setIndex_body_quality("87");
//        mHealthInfo4.setIndex_body_load("90");
//        health_list.add(mHealthInfo4);
//
//
//        if (health_list != null && health_list.size() > 0) {
//            MyLog.i(TAG, "health_list = " + health_list.toString());
//            updateUi(health_list);
//        }


    }

    /**
     * 展示当前用户UI
     *
     * @param my_health_list
     */
    void updateUi(List<HealthInfo> my_health_list) {

        MyLog.i(TAG, "过滤前 size = " + my_health_list.size());
        MyLog.i(TAG, "过滤前 mHealthInfo = " + my_health_list.toString());

        if (my_health_list.size() > 0) {
            my_health_list = HealthInfo.HandleNoData(my_health_list);
        }

        MyLog.i(TAG, "过滤后 size = " + my_health_list.size());
        MyLog.i(TAG, "过滤后 mHealthInfo = " + my_health_list.toString());

        if (my_health_list.size() > 0) {
            layoutData.setVisibility(View.VISIBLE);
            layoutNoData.setVisibility(View.GONE);

            mEcgMesureHistoryListAdapter.clear();
            mEcgMesureHistoryListAdapter.setDeviceList(my_health_list);
            mEcgMesureHistoryListAdapter.notifyDataSetChanged();
        } else {
            showNoData();
        }


    }

    void showNoData() {
        layoutData.setVisibility(View.GONE);
        layoutNoData.setVisibility(View.VISIBLE);

    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            //展开日历
            case R.id.layoutCalendar:
                if (mCalendarLayout.isExpand()) {
                    mCalendarLayout.shrink();
                } else {
                    mCalendarLayout.expand();
                }
                break;


            case R.id.ppg_history_to_measure:


                if (HomeActivity.ISBlueToothConnect()) {
                    if (BleService.OffLineOverIsStop) {
                        //如果没校准过
                        if (mUserSetTools.get_is_par() == 0) {
                            showCalibrationdialog();
                        }
                        //校准过
                        else {
                            gotoPpgMeasure(true);
                        }
                    } else {
                        AppUtils.showToast(mContext, R.string.synchronizing_offline_data);
                    }
                } else {
                    AppUtils.showToast(mContext, R.string.no_connection_notification);
                }

                break;


        }
    }


    /**
     * 获取当天健康数据
     *
     * @param is_cycle 是否循环，主要是为了，防止死循环用的
     */
    void getHealthtDay(boolean is_cycle) {

        List<HealthInfo> health_list = mHealthInfoUtils.queryToDate(BaseApplication.getUserId(), selectionDate, false);

        if (health_list != null && health_list.size() > 0) {
            MyLog.i(TAG, "health_list = " + health_list.toString());
            updateUi(health_list);
        } else {
            MyLog.i(TAG, "health_list = null");
            if (is_cycle) {
                requestHealthData(selectionDate);
            } else {
                showNoData();
            }
        }
    }

    /**
     * 请求当前用户健康数据
     */
    private void requestHealthData(final String date) {

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.getdHealthData(date, date);

        MyLog.i(TAG, "请求接口-获取健康数据" + mRequestInfo.getRequestJson());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-获取健康数据 result = " + result);

                        HealthBean mHealthBean = ResultJson.HealthBean(result);

                        //请求成功
                        if (mHealthBean.isRequestSuccess()) {
                            if (mHealthBean.isGetHealthSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取健康数据 成功");
                                ResultDataParsing(mHealthBean, date);
                            } else if (mHealthBean.isGetHealthSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取健康数据 失败");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else if (mHealthBean.isGetHealthSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取健康数据 无数据");
                                HealthBean.insertNullHealth(mHealthInfoUtils, date, false);
                                getHealthtDay(false);
                            } else {
                                MyLog.i(TAG, "请求接口-获取健康数据 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-上传健康数据 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }


                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "请求接口-获取健康数据 请求失败 = message = " + arg0.getMessage());
                        waitDialog.close();
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                    }
                });


    }


    /**
     * 解析当前用户数据
     *
     * @param mHealthBean
     */
    private void ResultDataParsing(HealthBean mHealthBean, String date) {


        MyLog.i(TAG, "请求接口-获取健康数据 总大小size = " + mHealthBean.getData().getPageCount());
        MyLog.i(TAG, "请求接口-获取健康数据 当前页size = " + mHealthBean.getData().getHealthList().size());

        if (mHealthBean.getData().getHealthList().size() >= 1) {

            MyLog.i(TAG, "请求接口-获取健康数据 getHealthMeasuringTime = " + mHealthBean.getData().getHealthList().get(0).getHealthMeasuringTime());

            List<HealthInfo> health_list = mHealthBean.getHealthInfoList(mHealthBean.getData().getHealthList());
            //由于可能这条数据是心电的，导致没有存储到运动的数据，过滤之后，运动的数据一直没有，会一直获取。
            health_list.add(HealthBean.getPpgHealthNullInfo(date));

            MyLog.i(TAG, "请求接口-获取健康数据 health_list = " + health_list.toString());

            boolean isSuccess = mHealthInfoUtils.insertInfoList(health_list);
            if (isSuccess) {
                MyLog.i(TAG, "插入健康表成功！");
            } else {
                MyLog.i(TAG, "插入健康失败！");
            }


        } else {

            HealthBean.insertNullHealth(mHealthInfoUtils, date, false);

        }

        getHealthtDay(false);


    }


//    /**
//     * 删除数据对话框
//     *
//     * @param position
//     */
//    void showDeleteHistoryData(final int position) {
//
//        new android.app.AlertDialog.Builder(mContext)
//                .setTitle(getString(R.string.dialog_prompt))//设置对话框标题
//
//                .setMessage(getString(R.string.delete_history_tip))//设置显示的内容
//
//                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {//添加确定按钮
//
//                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
//
//
//                        DeleteHealthyData(mEcgMesureHistoryListAdapter.getDevice(position));
//
//
//                    }
//
//                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {//添加返回按钮
//
//
//            @Override
//
//            public void onClick(DialogInterface dialog, int which) {//响应事件
//
//                // TODO Auto-generated method stub
//
//
//            }
//
//        }).show();//在按键响应事件中显示此对话框
//    }
//
//    /**
//     * 删除数据
//     *
//     * @param mHealthInfo
//     */
//    void DeleteHealthyData(HealthInfo mHealthInfo) {
//
//
//        if (mHealthInfo != null) {
//
//            if (!JavaUtil.checkIsNull(mHealthInfo.getData_id()) && Integer.valueOf(mHealthInfo.getData_id()) > 0) {
//
//                questDataHealth(mHealthInfo);
//
//            } else {
//
//                boolean is_sucdess = mHealthInfoUtils.deleteData(mHealthInfo);
//
//                if (is_sucdess) {
//                    MyLog.i(TAG, "数据库-删除数据 成功");
//                } else {
//                    MyLog.i(TAG, "数据库-删除数据 失败");
//                }
////                getHealthtDay(getDate(), true);
//
//
//            }
//
//        }
//
//
//    }
//
//    /**
//     * 请求删除健康数据
//     *
//     * @param mHealthInfo
//     */
//    void questDataHealth(final HealthInfo mHealthInfo) {
//
//        RequestInfo mRequestInfo = RequestJson.deleteHealthData(mHealthInfo.getData_id());
//
//        MyLog.i(TAG, "请求接口-删除数据 mRequestInfo = " + mRequestInfo.toString());
//
//        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
//                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
//
//                    @Override
//                    public void onMySuccess(JSONObject result) {
//                        // TODO Auto-generated method stub
//
//
//                        MyLog.i(TAG, "请求接口-删除数据 result = " + result);
//
//                        HealthBean mHealthBean = new Gson().fromJson(result.toString(), HealthBean.class);
//
//                        MyLog.i(TAG, "请求接口-删除数据 result = " + mHealthBean.getResult());
//                        MyLog.i(TAG, "请求接口-删除数据 result = " + mHealthBean.getMsg());
//
//
//                        if (mHealthBean.getResult() == 1) {
//
//                            MyLog.i(TAG, "请求接口-删除数据 成功");
//
//                        } else {
//
//                            MyLog.i(TAG, "请求接口-删除数据 失败");
//
//                        }
//
//                        boolean is_sucdess = mHealthInfoUtils.deleteData(mHealthInfo);
//
//                        if (is_sucdess) {
//                            MyLog.i(TAG, "数据库-删除数据 成功");
//                        } else {
//                            MyLog.i(TAG, "数据库-删除数据 失败");
//                        }
////                        getHealthtDay(getDate(), true);
//
//                    }
//
//                    @Override
//                    public void onMyError(VolleyError arg0) {
//                        // TODO Auto-generated method stub
//
//
//                        MyLog.i(TAG, "请求接口-删除数据 请求失败 = message = " + arg0.getMessage());
//
//                    }
//                });
//    }


    //日历实现方法
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {

        String date = NewTimeUtils.FormatDateYYYYMMDD(calendar);
        MyLog.i(TAG, "onCalendarSelect date = " + date + "  isClick = " + isClick);

        if (isClick) {

            MyLog.i(TAG, "calendar = " + calendar.toString());
            MyLog.i(TAG, "getScheme = " + calendar.getScheme());

            //如果等于加入的数据，类型等于 ONE_TYPE,则查询数据
            if (calendar.getScheme() != null
                    && !calendar.getScheme().equals("")
                    && calendar.getScheme().equals(MyCalendarUtils.ONE_TYPE)) {

                selectionDate = date;
                public_head_title.setText(selectionDate);

                mCalendarLayout.shrink();

                getHealthtDay(true);
            }
            //否则提示无数据
            else {


                AppUtils.showToast(mContext, R.string.calendar_no_touchou);
            }

        }


    }

    @Override
    public void onMonthChange(int year, int month) {
        String date = NewTimeUtils.FormatDateYYYYMM(year, month);
        MyLog.i(TAG, "onMonthChange date = " + date);
        public_head_title.setText(date);
    }

    //日历实现方法
    @Override
    public void onCalendarOutOfRange(Calendar calendar) {
        MyLog.i(TAG, "onCalendarOutOfRange calendar = " + calendar.toString());
    }

    //日历实现方法
    @Override
    public void onYearChange(int year) {
        MyLog.i(TAG, "onYearChange year = " + year);
    }

    //===============校准相关=======================

    boolean IsCalibrationGrade = true;
    int CalibrationGrade = 2;


    /**
     * 测量校准对话框
     */
    void showCalibrationdialog() {


        CalibrationGrade = mUserSetTools.get_blood_grade();


        int systilic = mUserSetTools.get_calibration_systolic();
        int diastolic = mUserSetTools.get_calibration_diastolic();
//
        final LayoutInflater factory = LayoutInflater.from(mContext);
        final View textEntryView = factory.inflate(R.layout.dialog_calibration, null);


        final TextView dialog_calibration_0_max = (TextView) textEntryView.findViewById(R.id.dialog_calibration_0_max);
        final TextView dialog_calibration_0_min = (TextView) textEntryView.findViewById(R.id.dialog_calibration_0_min);
        final TextView dialog_calibration_1_max = (TextView) textEntryView.findViewById(R.id.dialog_calibration_1_max);
        final TextView dialog_calibration_1_min = (TextView) textEntryView.findViewById(R.id.dialog_calibration_1_min);
        final TextView dialog_calibration_2_max = (TextView) textEntryView.findViewById(R.id.dialog_calibration_2_max);
        final TextView dialog_calibration_2_min = (TextView) textEntryView.findViewById(R.id.dialog_calibration_2_min);
        final TextView dialog_calibration_3_max = (TextView) textEntryView.findViewById(R.id.dialog_calibration_3_max);
        final TextView dialog_calibration_3_min = (TextView) textEntryView.findViewById(R.id.dialog_calibration_3_min);
        final TextView dialog_calibration_4_max = (TextView) textEntryView.findViewById(R.id.dialog_calibration_4_max);
        final TextView dialog_calibration_4_min = (TextView) textEntryView.findViewById(R.id.dialog_calibration_4_min);

        final Button dialog_btn_calibration_grade = (Button) textEntryView.findViewById(R.id.dialog_btn_calibration_grade);
        final Button dialog_btn_calibration_value = (Button) textEntryView.findViewById(R.id.dialog_btn_calibration_value);


        final LinearLayout dialog_ll_calibration_grade = (LinearLayout) textEntryView.findViewById(R.id.dialog_ll_calibration_grade);
        final LinearLayout dialog_ll_calibration_value = (LinearLayout) textEntryView.findViewById(R.id.dialog_ll_calibration_value);

        final EditText dialog_edit_calibration_sbp = (EditText) textEntryView.findViewById(R.id.dialog_edit_calibration_sbp);
        final EditText dialog_edit_calibration_dbp = (EditText) textEntryView.findViewById(R.id.dialog_edit_calibration_dbp);

        final SeekBar dialog_calibration_grade_seekbar = (SeekBar) textEntryView.findViewById(R.id.dialog_calibration_grade_seekbar);


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


        final AlertDialog dialog = new AlertDialog.Builder(mContext).setView(textEntryView).
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
                    gotoPpgMeasure(false);

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


    void handleCalibrationDialogValue(String calibration_sbp, String calibration_dbp) {


        MyLog.i(TAG, "测量校准 = 精准值校准 = calibration_sbp = " + calibration_sbp);
        MyLog.i(TAG, "测量校准 = 精准值校准 = calibration_dbp = " + calibration_dbp);


        if (TextUtils.isEmpty(calibration_sbp) || TextUtils.isEmpty(calibration_dbp)) {
            AppUtils.showToast(mContext, R.string.jiaozhun_dailog_error);
            return;
        } else if (MyUtils.checkInputNumber(calibration_sbp) && MyUtils.checkInputNumber(calibration_dbp)) {

            int sbp = Integer.valueOf(calibration_sbp);
            int dbp = Integer.valueOf(calibration_dbp);

            if (sbp < DefaultVale.USER_SYSTOLIC_MIN || sbp > DefaultVale.USER_SYSTOLIC_MAX
                    || dbp < DefaultVale.USER_DIASTOLIC_MIN || dbp > DefaultVale.USER_DIASTOLIC_MAX) {
                AppUtils.showToast(mContext, R.string.jiaozhun_dailog_error);
                return;
            } else {
                mUserSetTools.set_calibration_systolic(sbp);
                mUserSetTools.set_calibration_diastolic(dbp);
                mUserSetTools.set_blood_grade(-1);
                gotoPpgMeasure(false);
            }
        } else {
            AppUtils.showToast(mContext, R.string.jiaozhun_dailog_error);
            return;
        }
    }


    /**
     * 是否校准过
     *
     * @param isCalibration
     */
    void gotoPpgMeasure(boolean isCalibration) {

        Intent intent = null;
        //校准过直接进入测量
        if (isCalibration) {
            intent = new Intent(mContext, PpgMeasureActivity.class);
            intent.putExtra(IntentConstants.MesureType, IntentConstants.MesureType_Measure);
            startActivity(intent);
        }
        //没校准过，进行校准
        else {
            intent = new Intent(mContext, PpgMeasureActivity.class);
            intent.putExtra(IntentConstants.MesureType, IntentConstants.MesureType_Calibration);
            startActivity(intent);
        }


    }


}
