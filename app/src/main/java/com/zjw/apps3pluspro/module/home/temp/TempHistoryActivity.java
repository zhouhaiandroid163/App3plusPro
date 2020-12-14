package com.zjw.apps3pluspro.module.home.temp;

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
import com.zjw.apps3pluspro.adapter.MesureTempHistoryListAdapter;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.MeasureTempListBean;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.dbmanager.MeasureTempInfoUtils;
import com.zjw.apps3pluspro.sql.entity.MeasureTempInfo;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.NewTimeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.view.mycalendar.MyCalendarUtils;

import org.json.JSONObject;

import java.util.List;


/**
 * 体温测量历史
 */
public class TempHistoryActivity extends BaseActivity implements View.OnClickListener,
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnMonthChangeListener,
        CalendarView.OnYearChangeListener {
    private final String TAG = TempHistoryActivity.class.getSimpleName();
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    //数据库存储
    private MeasureTempInfoUtils mMeasureTempInfoUtils = BaseApplication.getMeasureTempInfoUtils();
    private Context mContext;

    //日期相关
    private String registTime;
    private String selectionDate;

    //列表
    private ListView temp_mesure_history_list;
    private MesureTempHistoryListAdapter mMesureTempHistoryListAdapter;

    //标题
    private TextView public_details_date;

    //日历
    CalendarLayout mCalendarLayout;
    CalendarView mCalendarView;

    //加载相关
    private WaitDialog waitDialog;
    private LinearLayout layoutNoData;
    private LinearLayout layoutData;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_temp_mesure_history;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = TempHistoryActivity.this;
        waitDialog = new WaitDialog(mContext);
        initView();
        initSetAdapter();
        //模拟，清除所有数据
//        mMeasureTempInfoUtils.deleteAllData();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMeasureTempDay(true);
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

        public_details_date = (TextView) findViewById(R.id.public_head_title);
        public_details_date.setText(getResources().getString(R.string.measure_record));

        layoutNoData = (LinearLayout) findViewById(R.id.layoutNoData);
        layoutData = (LinearLayout) findViewById(R.id.layoutData);
        //日历
        mCalendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);
        findViewById(R.id.layoutCalendar).setOnClickListener(this);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mCalendarView.setOnCalendarSelectListener(this);

        temp_mesure_history_list = (ListView) findViewById(R.id.temp_mesure_history_list);

    }

    void initSetAdapter() {
        mMesureTempHistoryListAdapter = new MesureTempHistoryListAdapter(mContext);
        temp_mesure_history_list.setAdapter(mMesureTempHistoryListAdapter);
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
//        public_details_date.setText(MyTime.getTime());
        mCalendarView.setSchemeDate(NewTimeUtils.getCycData(registTime));
    }

    /**
     * 展示当前用户UI
     *
     * @param data_list
     */
    void updateUi(List<MeasureTempInfo> data_list) {

        MyLog.i(TAG, "过滤前 size = " + data_list.size());
        MyLog.i(TAG, "过滤前 mHealthInfo = " + data_list.toString());

        if (data_list.size() > 0) {
            data_list = MeasureTempInfo.HandleNoData(data_list);
        }

        MyLog.i(TAG, "过滤后 size = " + data_list.size());
        MyLog.i(TAG, "过滤后 mHealthInfo = " + data_list.toString());

        if (data_list.size() > 0) {

            layoutNoData.setVisibility(View.GONE);
            layoutData.setVisibility(View.VISIBLE);

            mMesureTempHistoryListAdapter.clear();
            mMesureTempHistoryListAdapter.setDeviceList(data_list);
            mMesureTempHistoryListAdapter.notifyDataSetChanged();
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
    void getMeasureTempDay(boolean is_cycle) {

        List<MeasureTempInfo> data_list = mMeasureTempInfoUtils.queryToDate(BaseApplication.getUserId(), selectionDate);

        if (data_list != null && data_list.size() > 0) {
            MyLog.i(TAG, "data_list = " + data_list.toString());
            updateUi(data_list);
        } else {
            MyLog.i(TAG, "data_list = null");
            if (is_cycle) {
                requestMeasureTempData(selectionDate);
            } else {
                showNoData();
            }
        }
    }


    /**
     * 请求当前用户健康数据
     */
    private void requestMeasureTempData(final String date) {

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.getMeasureTempListData(date);

        MyLog.i(TAG, "请求接口-获取测量体温数据" + mRequestInfo.getRequestJson());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-获取测量体温数据 result = " + result);

                        MeasureTempListBean mMeasureTempListBean = ResultJson.MeasureTempListBean(result);

                        //请求成功
                        if (mMeasureTempListBean.isRequestSuccess()) {
                            if (mMeasureTempListBean.isGetSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取测量体温数据 成功");
                                ResultDataParsing(mMeasureTempListBean, date);
                            } else if (mMeasureTempListBean.isGetSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取测量体温数据 失败");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else if (mMeasureTempListBean.isGetSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取测量体温数据 无数据");
                                MeasureTempListBean.insertNullData(mMeasureTempInfoUtils, date);
                                getMeasureTempDay(false);
                            } else {
                                MyLog.i(TAG, "请求接口-获取测量体温数据 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取测量体温数据 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "请求接口-获取测量体温数据 请求失败 = message = " + arg0.getMessage());
                        waitDialog.close();
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                    }
                });


    }


    /**
     * 解析当前用户数据
     *
     * @param mMeasureTempListBean
     */
    private void ResultDataParsing(MeasureTempListBean mMeasureTempListBean, String date) {


        MyLog.i(TAG, "请求接口-获取测量体温数据 总大小size = " + mMeasureTempListBean.getData().getPageCount());
        MyLog.i(TAG, "请求接口-获取测量体温数据 当前页size = " + mMeasureTempListBean.getData().getTemperatureMeasureList().size());

        if (mMeasureTempListBean.getData().getTemperatureMeasureList().size() >= 1) {

            MyLog.i(TAG, "请求接口-获取测量体温数据 getHealthMeasuringTime = " + mMeasureTempListBean.getData().getTemperatureMeasureList().get(0).getTemperatureMeasureTime());

            List<MeasureTempInfo> data_list = mMeasureTempListBean.getInfoList(mMeasureTempListBean.getData().getTemperatureMeasureList());

            //由于可能这条数据是心电的，导致没有存储到运动的数据，过滤之后，运动的数据一直没有，会一直获取。
//            data_list.add(HealthBean.getEcgHealthNullInfo(date));

            MyLog.i(TAG, "请求接口-获取测量体温数据 data_list = " + data_list.toString());

            boolean isSuccess = mMeasureTempInfoUtils.insertInfoList(data_list);
            if (isSuccess) {
                MyLog.i(TAG, "插入测量体温表成功！");
            } else {
                MyLog.i(TAG, "插入测量体温失败！");
            }


        } else {
            MeasureTempListBean.insertNullData(mMeasureTempInfoUtils, date);
        }
        getMeasureTempDay(false);


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
                public_details_date.setText(selectionDate);

                mCalendarLayout.shrink();

                getMeasureTempDay(true);
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
        public_details_date.setText(date);
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
