package com.zjw.apps3pluspro.module.home.spo2;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.adapter.MesureSpo2HistoryListAdapter;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.MeasureSpo2ListBean;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.dbmanager.MeasureSpo2InfoUtils;
import com.zjw.apps3pluspro.sql.entity.MeasureSpo2Info;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.view.mycalendar.MyCalendarUtils;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.NewTimeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

import java.util.List;


/**
 * PPG测量历史
 */
public class Spo2MesureHistoryActivity extends BaseActivity implements View.OnClickListener,
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnMonthChangeListener,
        CalendarView.OnYearChangeListener {
    private final String TAG = Spo2MesureHistoryActivity.class.getSimpleName();
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    //数据库存储
    private MeasureSpo2InfoUtils mMeasureSpo2InfoUtils = BaseApplication.getMeasureSpo2InfoUtils();
    private Context mContext;

    //日期相关
    private String registTime;
    private String selectionDate;
    //列表
    private ListView spo2_mesure_history_list;
    private MesureSpo2HistoryListAdapter mMesureSpo2HistoryListAdapter;

    //标题
    private TextView public_head_title;

    //日历
    CalendarLayout mCalendarLayout;
    CalendarView mCalendarView;



    //加载相关
    private WaitDialog waitDialog;
    private LinearLayout layoutNoData;
    private LinearLayout layoutData;

    @Override
    protected int setLayoutId() {
        isTextDark = false;
        bgColor = R.color.title_bg_spo2;
        return R.layout.activity_spo2_mesure_history;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = Spo2MesureHistoryActivity.this;
        waitDialog = new WaitDialog(mContext);
        initView();
        initSetAdapter();
        //        模拟，清除所有数据
//        mMeasureSpo2InfoUtils.deleteAllData();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMeasureSpo2Day(true);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }

    private void initView() {
        public_head_title = (TextView) findViewById(R.id.public_head_title);
        public_head_title.setText(getResources().getString(R.string.measure_record));
        findViewById(R.id.layoutTitle) .setBackground(ContextCompat.getDrawable(this, R.color.title_bg_spo2));

        layoutNoData = (LinearLayout) findViewById(R.id.layoutNoData);
        layoutData = (LinearLayout) findViewById(R.id.layoutData);
        findViewById(R.id.layoutCalendar).setOnClickListener(this);
        //日历
        mCalendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mCalendarView.setOnCalendarSelectListener(this);

        spo2_mesure_history_list = (ListView) findViewById(R.id.spo2_mesure_history_list);

    }

    void initSetAdapter() {
        mMesureSpo2HistoryListAdapter = new MesureSpo2HistoryListAdapter(mContext);
        spo2_mesure_history_list.setAdapter(mMesureSpo2HistoryListAdapter);
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
//        public_head_title.setText(MyTime.getTime());
        mCalendarView.setSchemeDate(NewTimeUtils.getCycData(registTime));


    }

    /**
     * 展示当前用户UI
     *
     * @param data_list
     */
    void updateUi(List<MeasureSpo2Info> data_list) {

        MyLog.i(TAG, "过滤前 size = " + data_list.size());
        MyLog.i(TAG, "过滤前 mHealthInfo = " + data_list.toString());

        if (data_list.size() > 0) {
            data_list = MeasureSpo2Info.HandleNoData(data_list);
        }

        MyLog.i(TAG, "过滤后 size = " + data_list.size());
        MyLog.i(TAG, "过滤后 mHealthInfo = " + data_list.toString());

        if (data_list.size() > 0) {
            layoutNoData.setVisibility(View.GONE);
            layoutData.setVisibility(View.VISIBLE);

            mMesureSpo2HistoryListAdapter.clear();
            mMesureSpo2HistoryListAdapter.setDeviceList(data_list);
            mMesureSpo2HistoryListAdapter.notifyDataSetChanged();
        } else {
            showNoData();
        }


    }

    void showNoData() {
        layoutNoData.setVisibility(View.VISIBLE);
        layoutData.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.public_head_back:
                finish();
                break;
            case R.id.layoutCalendar:
                if(mCalendarLayout.isExpand()){
                    mCalendarLayout.shrink();
                } else {
                    mCalendarLayout.expand();
                }
                break;
        }
    }


    /**
     * 获取当天健康数据
     *
     * @param is_cycle 是否循环，主要是为了，防止死循环用的
     */
    void getMeasureSpo2Day(boolean is_cycle) {

        List<MeasureSpo2Info> data_list = mMeasureSpo2InfoUtils.queryToDate(BaseApplication.getUserId(), selectionDate);

        if (data_list != null && data_list.size() > 0) {
            MyLog.i(TAG, "data_list = " + data_list.toString());
            updateUi(data_list);
        } else {
            MyLog.i(TAG, "data_list = null");
            if (is_cycle) {
                requestMeasureSpo2Data(selectionDate);
            } else {
                showNoData();
            }
        }
    }

    /**
     * 请求当前用户健康数据
     */
    private void requestMeasureSpo2Data(final String date) {

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.getMeasureSpo2ListData(date);

        MyLog.i(TAG, "请求接口-获取测量血氧数据" + mRequestInfo.getRequestJson());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-获取测量血氧数据 result = " + result);

                        MeasureSpo2ListBean mMeasureSpo2ListBean = ResultJson.MeasureSpo2ListBean(result);

                        //请求成功
                        if (mMeasureSpo2ListBean.isRequestSuccess()) {
                            if (mMeasureSpo2ListBean.isGetSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取测量血氧数据 成功");
                                ResultDataParsing(mMeasureSpo2ListBean, date);
                            } else if (mMeasureSpo2ListBean.isGetSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取测量血氧数据 失败");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else if (mMeasureSpo2ListBean.isGetSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取测量血氧数据 无数据");
                                MeasureSpo2ListBean.insertNullData(mMeasureSpo2InfoUtils, date);
                                getMeasureSpo2Day(false);
                            } else {
                                MyLog.i(TAG, "请求接口-获取测量血氧数据 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取测量血氧数据 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }


                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "请求接口-获取测量血氧数据 请求失败 = message = " + arg0.getMessage());
                        waitDialog.close();
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                    }
                });


    }


    /**
     * 解析当前用户数据
     *
     * @param mMeasureSpo2ListBean
     */
    private void ResultDataParsing(MeasureSpo2ListBean mMeasureSpo2ListBean, String date) {


        MyLog.i(TAG, "请求接口-获取测量血氧数据 总大小size = " + mMeasureSpo2ListBean.getData().getPageCount());
        MyLog.i(TAG, "请求接口-获取测量血氧数据 当前页size = " + mMeasureSpo2ListBean.getData().getSpoMeasureList().size());

        if (mMeasureSpo2ListBean.getData().getSpoMeasureList().size() >= 1) {

            MyLog.i(TAG, "请求接口-获取测量血氧数据 getHealthMeasuringTime = " + mMeasureSpo2ListBean.getData().getSpoMeasureList().get(0).getSpoMeasureTime());

            List<MeasureSpo2Info> data_list = mMeasureSpo2ListBean.getInfoList(mMeasureSpo2ListBean.getData().getSpoMeasureList());

            //由于可能这条数据是心电的，导致没有存储到运动的数据，过滤之后，运动的数据一直没有，会一直获取。
//            data_list.add(HealthBean.getEcgHealthNullInfo(date));

            MyLog.i(TAG, "请求接口-获取测量血氧数据 data_list = " + data_list.toString());

            boolean isSuccess = mMeasureSpo2InfoUtils.insertInfoList(data_list);
            if (isSuccess) {
                MyLog.i(TAG, "插入测量血氧表成功！");
            } else {
                MyLog.i(TAG, "插入测量血氧失败！");
            }


        } else {
            MeasureSpo2ListBean.insertNullData(mMeasureSpo2InfoUtils, date);
        }
        getMeasureSpo2Day(false);


    }


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

                getMeasureSpo2Day(true);
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


}
