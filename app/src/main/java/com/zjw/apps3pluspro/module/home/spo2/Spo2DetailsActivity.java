package com.zjw.apps3pluspro.module.home.spo2;

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
import com.zjw.apps3pluspro.module.home.entity.ContinuitySpo2Model;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.ContinuitySpo2ListBean;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sql.dbmanager.ContinuitySpo2InfoUtils;
import com.zjw.apps3pluspro.sql.entity.ContinuitySpo2Info;
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
 * 连续血氧历史
 */
public class Spo2DetailsActivity extends BaseActivity implements
        View.OnClickListener,
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnMonthChangeListener,
        CalendarView.OnYearChangeListener {
    private final String TAG = Spo2DetailsActivity.class.getSimpleName();
    private Context mContext;
    //数据库存储
    private ContinuitySpo2InfoUtils mContinuitySpo2InfoUtils = BaseApplication.getContinuitySpo2InfoUtils();

    //标题
    private TextView public_head_title;
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    //日历
    CalendarLayout mCalendarLayout;
    CalendarView mCalendarView;

    // 图表
    LineChart spo2_record_barchart;
    private LinearLayout spo2_details_lin_data_no, spo2_details_lin_data_yes;

    //血氧
    private TextView tv_spo2_last_value, tv_spo2_max_value, tv_spo2_min_value;

    //日历相关
    private String registTime;
    private String selectionDate;

    //加载相关
    private WaitDialog waitDialog;

    @Override
    protected int setLayoutId() {
        isTextDark = false;
        bgColor = R.color.title_bg_spo2;
        return R.layout.activity_spo2_details;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = Spo2DetailsActivity.this;
        waitDialog = new WaitDialog(mContext);
        initView();
        //模拟 清空连续血氧
//        mContinuitySpo2InfoUtils.deleteAllData();
        loadData();
    }

    @Override
    protected void onDestroy() {
        waitDialog.dismiss();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        public_head_title = (TextView) findViewById(R.id.public_head_title);
        public_head_title.setText(getResources().getString(R.string.spo2_str));
        findViewById(R.id.layoutTitle).setBackground(ContextCompat.getDrawable(this, R.color.title_bg_spo2));
        findViewById(R.id.layoutCalendar).setOnClickListener(this);
        spo2_details_lin_data_no = (LinearLayout) findViewById(R.id.layoutNoData);
        spo2_details_lin_data_yes = (LinearLayout) findViewById(R.id.layoutData);
        //日历
        mCalendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mCalendarView.setOnCalendarSelectListener(this);

        tv_spo2_last_value = (TextView) findViewById(R.id.tv_spo2_last_value);
        tv_spo2_max_value = (TextView) findViewById(R.id.tv_spo2_max_value);
        tv_spo2_min_value = (TextView) findViewById(R.id.tv_spo2_min_value);

        spo2_record_barchart = (LineChart) findViewById(R.id.spo2_record_barchart);

        findViewById(R.id.rl_spo2_measure_history).setOnClickListener(this);

        LinearLayout rl_spo2_measure_history = findViewById(R.id.rl_spo2_measure_history);

        if (mBleDeviceTools.get_is_support_spo2() && mBleDeviceTools.getSupportOfflineBloodOxygen()) {
            rl_spo2_measure_history.setVisibility(View.VISIBLE);
        } else {
            rl_spo2_measure_history.setVisibility(View.GONE);
        }

        mCurveChartView = findViewById(R.id.mCurveChartView);
        tvSlidingValue = findViewById(R.id.tvSlidingValue);
        tvSlidingValue.setVisibility(View.INVISIBLE);
        mCurveChartView.setOnSlidingListener(new CurveChartView.OnSlidingListener() {
            @Override
            public void SlidingDisOver(@NotNull String data, int index) {
                if (index != -1) {
                    tvSlidingValue.setVisibility(View.VISIBLE);
                    tvSlidingValue.setText(data + "  " + MyTime.getSleepTime_H(String.valueOf(index * 5), "00")
                            + ":" + MyTime.getSleepTime_M(String.valueOf(index * 5), "00")
                    );
                } else {
                    tvSlidingValue.setVisibility(View.INVISIBLE);
                }
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

    TextView tvSlidingValue;
    CurveChartView mCurveChartView;

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
//        public_head_title.setText(MyTime.getTime());
        mCalendarView.setSchemeDate(NewTimeUtils.getCycData(registTime));
        getContinuitySpo2Day(true);
    }

    void updateUi(ContinuitySpo2Info mContinuitySpo2Info) {

        ContinuitySpo2Model mContinuitySpo2Model = new ContinuitySpo2Model(mContinuitySpo2Info);

        String day_max = mContinuitySpo2Model.getContinuitySpo2DayMax();
        String day_min = mContinuitySpo2Model.getContinuitySpo2DayMin();
        String data = mContinuitySpo2Model.getContinuitySpo2Data();
        //最近一次数据
        String last_value = mContinuitySpo2Model.getLastValue();

//        wo_heart_data = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,10,20,30,40,50,60,70,90,100,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,80,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,80,90,100,120,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";

        MyLog.i(TAG, "data = " + data);


        if (!JavaUtil.checkIsNull(day_max)) {
            tv_spo2_max_value.setText(day_max);
        } else {
            tv_spo2_max_value.setText(R.string.sleep_gang);
        }

        if (!JavaUtil.checkIsNull(day_min)) {
            tv_spo2_min_value.setText(day_min);
        } else {
            tv_spo2_min_value.setText(R.string.sleep_gang);
        }


        if (!JavaUtil.checkIsNull(last_value)) {
            tv_spo2_last_value.setText(last_value);
        } else {
            tv_spo2_last_value.setText(R.string.sleep_gang);
        }


        if (!JavaUtil.checkIsNull(last_value) && !JavaUtil.checkIsNull(data) && !JavaUtil.checkIsNull(data)) {

            String[] spo2_list = data.split(",");
            mCurveChartView.setParameter(spo2_list, "95");
            //图表
            MyChartUtils.showDayContinuitySpo2BarChart(Spo2DetailsActivity.this, spo2_record_barchart, spo2_list, true);

            MyLog.i(TAG, "图表有数据");

            spo2_details_lin_data_yes.setVisibility(View.VISIBLE);
            spo2_details_lin_data_no.setVisibility(View.GONE);

        } else {

            spo2_details_lin_data_yes.setVisibility(View.GONE);
            spo2_details_lin_data_no.setVisibility(View.VISIBLE);

            MyLog.i(TAG, "图表无数据");

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

            case R.id.rl_spo2_measure_history:
                Intent intent = new Intent(Spo2DetailsActivity.this, Spo2MesureHistoryActivity.class);
                startActivity(intent);
                break;

        }

    }


    /**
     * @param is_cycle
     */
    void getContinuitySpo2Day(boolean is_cycle) {

        MyLog.i(TAG, "getContinuitySpo2Day() selectionDate = " + selectionDate);

        ContinuitySpo2Info mContinuitySpo2Info = mContinuitySpo2InfoUtils.MyQueryToDate(BaseApplication.getUserId(), selectionDate);

        if (mContinuitySpo2Info != null) {
            MyLog.i(TAG, "mContinuitySpo2Info = " + mContinuitySpo2Info.toString());
            updateUi(mContinuitySpo2Info);
        } else {
            MyLog.i(TAG, "mContinuitySpo2Info = null");
            if (is_cycle) {
                requestContinuitySpo2Data(selectionDate);
            }

        }

    }

    private void requestContinuitySpo2Data(final String date) {

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.getContinuitySpo2ListData(date);

        MyLog.i(TAG, "请求接口-获取连续血氧数据 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-获取连续血氧数据 请求成功 = result = " + result.toString());
                        ContinuitySpo2ListBean mContinuitySpo2ListBean = ResultJson.ContinuitySpo2ListBean(result);

                        //请求成功
                        if (mContinuitySpo2ListBean.isRequestSuccess()) {

                            if (mContinuitySpo2ListBean.isGetSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取连续血氧数据 成功");
                                ResultSpo2tDataParsing(mContinuitySpo2ListBean, date);
                            } else if (mContinuitySpo2ListBean.isGetSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取连续血氧数据 失败");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else if (mContinuitySpo2ListBean.isGetSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-获取连续血氧数据 无数据");
                                mContinuitySpo2ListBean.insertNullData(mContinuitySpo2InfoUtils, date);
                                getContinuitySpo2Day(false);
                            } else {
                                MyLog.i(TAG, "请求接口-获取连续血氧数据 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }

                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取连续血氧数据 请求异常(0)");

                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-获取连续血氧数据 请求失败 = message = " + arg0.getMessage());
                        waitDialog.close();
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);
                        return;
                    }
                });
    }

    /**
     * 解析数据
     */
    private void ResultSpo2tDataParsing(ContinuitySpo2ListBean mContinuitySpo2ListBean, String date) {

        MyLog.i(TAG, "请求接口-获取连续血氧数据 size = " + mContinuitySpo2ListBean.getData().size());

        if (mContinuitySpo2ListBean.getData().size() > 0) {
            ContinuitySpo2Info mContinuitySpo2Info = mContinuitySpo2ListBean.getContinuitySpo2Info(mContinuitySpo2ListBean.getData().get(0));
            boolean isSuccess = mContinuitySpo2InfoUtils.MyUpdateData(mContinuitySpo2Info);
            if (isSuccess) {
                MyLog.i(TAG, "插入连续血氧表成功！");
            } else {
                MyLog.i(TAG, "插入连续血氧表失败！");
            }
        } else {
            ContinuitySpo2ListBean.insertNullData(mContinuitySpo2InfoUtils, date);
        }

        getContinuitySpo2Day(false);

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

                getContinuitySpo2Day(true);
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
