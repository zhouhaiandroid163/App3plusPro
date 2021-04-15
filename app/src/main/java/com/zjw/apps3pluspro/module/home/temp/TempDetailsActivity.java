package com.zjw.apps3pluspro.module.home.temp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.github.mikephil.charting.charts.LineChart;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.home.entity.ContinuityTempModel;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.ContinuityTempListBean;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sql.dbmanager.ContinuityTempInfoUtils;
import com.zjw.apps3pluspro.sql.entity.ContinuityTempInfo;
import com.zjw.apps3pluspro.view.CurveChartView;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.view.mycalendar.MyCalendarUtils;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyChartUtils;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.NewTimeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;


/**
 * 连续体温历史
 */
public class TempDetailsActivity extends BaseActivity implements
        View.OnClickListener,
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnMonthChangeListener,
        CalendarView.OnYearChangeListener {
    private final String TAG = TempDetailsActivity.class.getSimpleName();

    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    private Context mContext;
    //数据库存储
    private ContinuityTempInfoUtils mContinuityTempInfoUtils = BaseApplication.getContinuityTempInfoUtils();

    //标题
    private TextView public_head_title;

    //日历
    CalendarLayout mCalendarLayout;
    CalendarView mCalendarView;

    // 图表
    private LinearLayout temp_details_lin_data_no, temp_details_lin_data_yes;

    //体温
    private TextView tv_temp_body_last_value, tv_temp_body_last_unit;

    private TextView tv_temp_body_max_value, tv_temp_body_max_unit;
    private TextView tv_temp_body_min_value, tv_temp_body_min_unit;


    //日历相关
    private String registTime;
    private String selectionDate;

    //加载相关
    private WaitDialog waitDialog;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_temp_details;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = TempDetailsActivity.this;
        waitDialog = new WaitDialog(mContext);
        initView();
        //模拟 清空连续体温
//        mContinuityTempInfoUtils.deleteAllData();
        loadData();
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
        waitDialog.dismiss();
        super.onDestroy();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        public_head_title = (TextView) findViewById(R.id.public_head_title);
        public_head_title.setText(getResources().getString(R.string.temp_body));

        //日历
        mCalendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mCalendarView.setOnCalendarSelectListener(this);
        findViewById(R.id.layoutCalendar).setOnClickListener(this);

        tv_temp_body_last_value = (TextView) findViewById(R.id.tv_temp_body_last_value);
        tv_temp_body_last_unit = (TextView) findViewById(R.id.tv_temp_body_last_unit);

        tv_temp_body_max_value = (TextView) findViewById(R.id.tvTempMax);
        tv_temp_body_max_unit = (TextView) findViewById(R.id.tvUnit1);

        tv_temp_body_min_value = (TextView) findViewById(R.id.tvTempMin);
        tv_temp_body_min_unit = (TextView) findViewById(R.id.tvUnit2);

        temp_details_lin_data_no = (LinearLayout) findViewById(R.id.layoutNoData);
        temp_details_lin_data_yes = (LinearLayout) findViewById(R.id.layoutData);

        findViewById(R.id.rl_temp_measure_history).setOnClickListener(this);

        LinearLayout rl_temp_measure_history = findViewById(R.id.rl_temp_measure_history);
        if (mBleDeviceTools.get_is_support_temp() && mBleDeviceTools.getSupportOfflineTemp()) {
            rl_temp_measure_history.setVisibility(View.VISIBLE);
        } else {
            rl_temp_measure_history.setVisibility(View.GONE);
        }
        mCurveChartView = findViewById(R.id.mCurveChartView);
        tvSlidingValue = findViewById(R.id.tvSlidingValue);
        tvSlidingValue.setVisibility(View.INVISIBLE);
        mCurveChartView.setOnSlidingListener((data, index) -> {
            if (index != -1) {
                tvSlidingValue.setVisibility(View.VISIBLE);
                tvSlidingValue.setText(data + "  " + MyTime.getSleepTime_H(String.valueOf(index * 5), "00")
                        + ":" + MyTime.getSleepTime_M(String.valueOf(index * 5), "00")
                );
            } else {
                tvSlidingValue.setVisibility(View.INVISIBLE);
            }
        });
        mCurveChartView.setOnTouchListener((v, event) -> {
            float eventX = event.getX();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    mCurveChartView.setTouchPos(eventX);
                    break;
                case MotionEvent.ACTION_UP:
                    mCurveChartView.setTouchPos(-1);
                    break;
            }
            mCurveChartView.invalidate();
            return true;
        });
    }

    private void loadData() {

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

        updateUiType();
        getContinuityTempDay(true);
    }

    TextView tvSlidingValue;
    CurveChartView mCurveChartView;

    void updateUi(ContinuityTempInfo mContinuityTempInfo) {

        ContinuityTempModel mContinuityTempModel = new ContinuityTempModel(mContinuityTempInfo);

        String day_body_max = mContinuityTempModel.getContinuityTempDayBodyMax();
        String day_body_min = mContinuityTempModel.getContinuityTempDayBodyMin();
        String data = mContinuityTempModel.getBodyListData();
        //最近一数据
        String last_body_value = !JavaUtil.checkIsNull(mContinuityTempModel.getLastBodyValue()) ? mContinuityTempModel.getLastBodyValue() : "0";
//        wo_heart_data = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,10,20,30,40,50,60,70,90,100,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,80,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,80,90,100,120,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";

        MyLog.i(TAG, "data = " + data);


        if (!JavaUtil.checkIsNull(day_body_max)) {
            tv_temp_body_max_value.setText(day_body_max);
        } else {
            tv_temp_body_max_value.setText(R.string.sleep_gang);
        }

        if (!JavaUtil.checkIsNull(day_body_min)) {
            tv_temp_body_min_value.setText(day_body_min);
        } else {
            tv_temp_body_min_value.setText(R.string.sleep_gang);
        }


        if (!JavaUtil.checkIsNull(last_body_value)) {
            tv_temp_body_last_value.setText(last_body_value);
        } else {
            tv_temp_body_last_value.setText(R.string.sleep_gang);
        }


        if (!JavaUtil.checkIsNull(last_body_value) && !JavaUtil.checkIsNull(data) && !JavaUtil.checkIsNull(data)) {

            String[] body_list = data.split(",");
            MyLog.i(TAG, "updateUi data = " + data);

            //图表
            MyLog.i(TAG, "图表有数据");
            temp_details_lin_data_yes.setVisibility(View.VISIBLE);
            temp_details_lin_data_no.setVisibility(View.GONE);

            String standard = "36";

            if (mBleDeviceTools.getTemperatureType() == 1) {
                standard = "90";
            }

            mCurveChartView.setParameter(body_list, standard);

//            mCurveChartView.setParameter(new String[]{"50", "60", "50", "20", "50", "10", "50", "35", "50", "25", "50", "65"}, "36.5");
        } else {
            temp_details_lin_data_yes.setVisibility(View.GONE);
            temp_details_lin_data_no.setVisibility(View.VISIBLE);
            MyLog.i(TAG, "图表无数据");
        }

    }

    void updateUiType() {
        //华氏度
        if (mBleDeviceTools.getTemperatureType() == 1) {
            tv_temp_body_max_unit.setText(getString(R.string.fahrenheit_degree));
            tv_temp_body_min_unit.setText(getString(R.string.fahrenheit_degree));
            tv_temp_body_last_unit.setText(getString(R.string.fahrenheit_degree));

        }
        //摄氏度
        else {
            tv_temp_body_max_unit.setText(getString(R.string.centigrade));
            tv_temp_body_min_unit.setText(getString(R.string.centigrade));
            tv_temp_body_last_unit.setText(getString(R.string.centigrade));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layoutCalendar:
                if (mCalendarLayout.isExpand()) {
                    mCalendarLayout.shrink();
                } else {
                    mCalendarLayout.expand();
                }

                break;

            case R.id.rl_temp_measure_history:
                Intent intent = new Intent(TempDetailsActivity.this, TempHistoryActivity.class);
                startActivity(intent);
                break;

        }

    }


    /**
     * @param is_cycle
     */
    void getContinuityTempDay(boolean is_cycle) {


        MyLog.i(TAG, "getContinuityTempDay()");

        ContinuityTempInfo mContinuityTempInfo = mContinuityTempInfoUtils.MyQueryToDate(BaseApplication.getUserId(), selectionDate);

        if (mContinuityTempInfo != null) {
            MyLog.i(TAG, "mContinuityTempInfo = " + mContinuityTempInfo.toString());
            updateUi(mContinuityTempInfo);
        } else {
            MyLog.i(TAG, "mContinuityTempInfo = null");
            if (is_cycle) {
                requestContinuityTempData(selectionDate);
            }
        }
    }

    private void requestContinuityTempData(final String date) {

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.getContinuityTempListData(date);

        MyLog.i(TAG, "请求接口-获取连续体温数据 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-获取连续体温数据 请求成功 = result = " + result.toString());

                        ContinuityTempListBean mContinuityTempListBean = ResultJson.ContinuityTempListBean(result);

                        //请求成功
                        if (mContinuityTempListBean.isRequestSuccess()) {
                            if (mContinuityTempListBean.isGetSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取连续体温数据 成功");
                                ResultTemptDataParsing(mContinuityTempListBean, date);
                            } else if (mContinuityTempListBean.isGetSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取连续体温数据 失败");
                            } else if (mContinuityTempListBean.isGetSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取连续体温数据 无数据");
                                ContinuityTempListBean.insertNullData(mContinuityTempInfoUtils, date);
                                getContinuityTempDay(false);

                            } else {
                                MyLog.i(TAG, "请求接口-获取连续体温数据 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }

                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取连续体温数据 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }


                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-获取连续体温数据 请求失败 = message = " + arg0.getMessage());
                        waitDialog.close();
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);
                        return;
                    }
                });
    }

    /**
     * 解析数据
     */
    private void ResultTemptDataParsing(ContinuityTempListBean mContinuityTempListBean, String date) {

        MyLog.i(TAG, "请求接口-获取连续体温数据 size = " + mContinuityTempListBean.getData().size());

        if (mContinuityTempListBean.getData().size() > 0) {
            ContinuityTempInfo mContinuityTempInfo = mContinuityTempListBean.getContinuityTempInfo(mContinuityTempListBean.getData().get(0));
            boolean isSuccess = mContinuityTempInfoUtils.MyUpdateData(mContinuityTempInfo);
            if (isSuccess) {
                MyLog.i(TAG, "插入连续体温表成功！");
            } else {
                MyLog.i(TAG, "插入连续体温表失败！");
            }
        } else {
            ContinuityTempListBean.insertNullData(mContinuityTempInfoUtils, date);
        }

        getContinuityTempDay(false);

    }


    @Override
    public void onCalendarSelect(com.haibin.calendarview.Calendar calendar, boolean isClick) {

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

                getContinuityTempDay(true);
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


    @Override
    public void onCalendarOutOfRange(com.haibin.calendarview.Calendar calendar) {
        MyLog.i(TAG, "onCalendarOutOfRange calendar = " + calendar.toString());
    }


    @Override
    public void onYearChange(int year) {
        MyLog.i(TAG, "onYearChange year = " + year);
    }


}
