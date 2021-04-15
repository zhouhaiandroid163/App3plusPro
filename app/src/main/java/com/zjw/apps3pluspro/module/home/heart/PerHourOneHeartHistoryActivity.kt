package com.zjw.apps3pluspro.module.home.heart

import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import butterknife.OnClick
import com.android.volley.VolleyError
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.application.BaseApplication
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.module.home.entity.HeartModel
import com.zjw.apps3pluspro.network.NewVolleyRequest
import com.zjw.apps3pluspro.network.RequestJson
import com.zjw.apps3pluspro.network.ResultJson
import com.zjw.apps3pluspro.network.VolleyInterface
import com.zjw.apps3pluspro.network.javabean.HeartBean
import com.zjw.apps3pluspro.sql.entity.HeartInfo
import com.zjw.apps3pluspro.utils.*
import com.zjw.apps3pluspro.utils.log.MyLog
import com.zjw.apps3pluspro.view.HeartChartView
import com.zjw.apps3pluspro.view.dialog.WaitDialog
import com.zjw.apps3pluspro.view.mycalendar.MyCalendarUtils
import kotlinx.android.synthetic.main.continuous_heart_history_activity.layoutData
import kotlinx.android.synthetic.main.continuous_heart_history_activity.layoutNoData
import kotlinx.android.synthetic.main.per_hour_one_heart_history_activity.*
import kotlinx.android.synthetic.main.per_hour_one_heart_history_activity.calendarLayout
import kotlinx.android.synthetic.main.per_hour_one_heart_history_activity.calendarView
import kotlinx.android.synthetic.main.per_hour_one_heart_history_activity.heartLineChart
import kotlinx.android.synthetic.main.per_hour_one_heart_history_activity.tvAvgHeart
import kotlinx.android.synthetic.main.per_hour_one_heart_history_activity.tvLastHeart
import kotlinx.android.synthetic.main.per_hour_one_heart_history_activity.tvMaxHeart
import kotlinx.android.synthetic.main.per_hour_one_heart_history_activity.tvMinHeart
import kotlinx.android.synthetic.main.public_head_white_text.*
import org.json.JSONObject

class PerHourOneHeartHistoryActivity : BaseActivity() {
    private val TAG = PerHourOneHeartHistoryActivity::class.java.simpleName
    private var registTime: String? = null
    private var selectionDate: String? = null
    private var waitDialog: WaitDialog? = null
    private val mHeartInfoUtils = BaseApplication.getHeartInfoUtils()
    override fun setLayoutId(): Int {
        return R.layout.per_hour_one_heart_history_activity
    }

    @OnClick(R.id.layoutCalendar)
    fun viewOnClick(view: View) {
        when (view.id) {
            R.id.layoutCalendar -> {
                if (calendarLayout.isExpand) {
                    calendarLayout.shrink()
                } else {
                    calendarLayout.expand()
                }
            }
        }
    }

    override fun onDestroy() {
        waitDialog!!.dismiss()
        super.onDestroy()
    }

    override fun initViews() {
        super.initViews()
        tvAvgHeart.text = resources.getString(R.string.average_heart) + "  " + resources.getString(R.string.no_data_default) + resources.getString(R.string.bpm)
        public_head_title.text = resources.getString(R.string.heart)
        ivTitleType.background = ContextCompat.getDrawable(this@PerHourOneHeartHistoryActivity, R.mipmap.title_heart_icon)
        waitDialog = WaitDialog(this)

        calendarView.setOnYearChangeListener {
        }
        calendarView.setOnMonthChangeListener { year, month ->
            val date = NewTimeUtils.FormatDateYYYYMM(year, month)
            public_head_title.text = date
        }
        calendarView.setOnCalendarSelectListener(object : CalendarView.OnCalendarSelectListener {
            override fun onCalendarOutOfRange(calendar: Calendar) {}
            override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
                val date = NewTimeUtils.FormatDateYYYYMMDD(calendar)
                MyLog.i(TAG, "onCalendarSelect date = $date  isClick = $isClick")

                if (isClick) {
                    MyLog.i(TAG, "calendar = $calendar")
                    MyLog.i(TAG, "getScheme = " + calendar.scheme)
                    //如果等于加入的数据，类型等于 ONE_TYPE,则查询数据
                    if (calendar.scheme != null && calendar.scheme != ""
                            && calendar.scheme == MyCalendarUtils.ONE_TYPE) {
                        selectionDate = date
                        public_head_title.text = selectionDate
                        calendarLayout.shrink()
                        getPoHeartDay(true)
                    } else {
                        AppUtils.showToast(this@PerHourOneHeartHistoryActivity, R.string.calendar_no_touchou)
                    }
                }
            }
        })

        heartLineChart.setOnSlidingListener(object : HeartChartView.OnSlidingListener {
            override fun SlidingDisOver(data: String, index: Int) {
                if (index != -1) {
                    tvSlidingValue.visibility = View.VISIBLE
                    tvSlidingValue.text = (data + "  " + MyTime.getSleepTime_H((index * 60).toString(), "00")
                            + ":" + MyTime.getSleepTime_M((0 * 5).toString(), "00"))
                } else {
                    tvSlidingValue.visibility = View.INVISIBLE
                }
            }
        })
        heartLineChart.setOnTouchListener(View.OnTouchListener { v: View?, event: MotionEvent ->
            val eventX = event.x
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> heartLineChart.setTouchPos(eventX)
                MotionEvent.ACTION_UP -> heartLineChart.setTouchPos(-1f)
            }
            heartLineChart.invalidate()
            true
        })

    }

    fun updateUi(mHeartInfo: HeartInfo?) {
        val mHeartModel = HeartModel(mHeartInfo)

        if (!JavaUtil.checkIsNull(mHeartModel.lastHeart)) {
            tvLastHeart.text = mHeartModel.lastHeart
        } else {
            tvLastHeart.text = resources.getText(R.string.no_data_default)
        }

        if (!JavaUtil.checkIsNull(mHeartModel.heartDayAverage)) {
            tvAvgHeart.text = resources.getString(R.string.average_heart) + "  " + mHeartModel.heartDayAverage + resources.getString(R.string.bpm)
        } else {
            tvAvgHeart.text = resources.getString(R.string.average_heart) + "  " + resources.getString(R.string.no_data_default) + resources.getString(R.string.bpm)
        }

        if (!JavaUtil.checkIsNull(mHeartModel.heartDayMax)) {
            tvMaxHeart.text = mHeartModel.heartDayMax
        } else {
            tvMaxHeart.text = resources.getText(R.string.no_data_default)
        }

        if (!JavaUtil.checkIsNull(mHeartModel.heartDayMin)) {
            tvMinHeart.text = mHeartModel.heartDayMin
        } else {
            tvMinHeart.text = resources.getText(R.string.no_data_default)
        }

        if (!JavaUtil.checkIsNull(mHeartModel.heartData) && !JavaUtil.checkIsNull(mHeartModel.heartDayAverage)) {
            val po_heart_list: Array<String> = mHeartModel.heartData.split(",").toTypedArray()
//            MyChartUtils.showDayPoHearBarChart(this, heartLineChart, po_heart_list, true)
            heartLineChart.setParameter(po_heart_list, "65", true)

            layoutNoData.visibility = View.GONE
            layoutData.visibility = View.VISIBLE
        } else {
            layoutNoData.visibility = View.VISIBLE
            layoutData.visibility = View.GONE
        }
    }

    override fun initDatas() {
        super.initDatas()
        registTime = BaseApplication.getRegisterTime()

        registTime = if (JavaUtil.checkIsNull(registTime)) {
            DefaultVale.USER_DEFULT_REGISTER_TIME
        } else {
            registTime?.split(" ")?.toTypedArray()?.get(0)
        }
        MyLog.i(TAG, "注册日期为 = $registTime")

        selectionDate = MyTime.getTime()
        calendarView.setSchemeDate(NewTimeUtils.getCycData(registTime))
        getPoHeartDay(true)
    }

    fun getPoHeartDay(is_cycle: Boolean) {
        MyLog.i(TAG, "getPoHeartDay()")
        val mPoHeartInfo = if (mHeartInfoUtils.MyQueryToDate(BaseApplication.getUserId(), selectionDate, "0") == null) null else mHeartInfoUtils.MyQueryToDate(BaseApplication.getUserId(), selectionDate, "0")
        if (mPoHeartInfo != null) {
            MyLog.i(TAG, "mPoHeartInfo = $mPoHeartInfo")
            updateUi(mPoHeartInfo)
        } else {
            MyLog.i(TAG, "mPoHeartInfo = null")
            if (is_cycle) {
                requestPoheartData(selectionDate!!)
            }
        }
    }

    private fun requestPoheartData(date: String) {
        waitDialog?.show(getString(R.string.loading0))
        val mRequestInfo = RequestJson.getPoListData(date, date)
        MyLog.i(TAG, "请求接口-获取整点心率数据 mRequestInfo = $mRequestInfo")
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                object : VolleyInterface(this, mListener, mErrorListener) {
                    override fun onMySuccess(result: JSONObject) { // TODO Auto-generated method stub
                        waitDialog?.close()
                        MyLog.i(TAG, "请求接口-获取整点心率数据 请求成功 = result = $result")
                        val mHeartBean = ResultJson.HeartBean(result)
                        //请求成功
                        if (mHeartBean.isRequestSuccess) {
                            if (mHeartBean.isGetHeartSuccess == 1) {
                                MyLog.i(TAG, "请求接口-获取整点心率数据 成功")
                                ResultPoHeartDataParsing(mHeartBean, date)
                            } else if (mHeartBean.isGetHeartSuccess == 0) {
                                MyLog.i(TAG, "请求接口-获取整点心率数据 失败")
                                AppUtils.showToast(mContext, R.string.data_try_again_code1)
                            } else if (mHeartBean.isGetHeartSuccess == 2) {
                                MyLog.i(TAG, "请求接口-获取整点心率数据 无数据")
                                HeartBean.insertPoNullData(mHeartInfoUtils, date)
                                getPoHeartDay(false)
                            } else {
                                MyLog.i(TAG, "请求接口-获取整点心率数据 请求异常(1)")
                                AppUtils.showToast(mContext, R.string.data_try_again_code1)
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取整点心率数据 请求异常(0)")
                            AppUtils.showToast(mContext, R.string.server_try_again_code0)
                        }
                    }

                    override fun onMyError(arg0: VolleyError) { // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-获取整点心率数据 请求失败 = message = " + arg0.message)
                        waitDialog?.close()
                        AppUtils.showToast(mContext, R.string.net_worse_try_again)
                        return
                    }
                })
    }

    /**
     * 解析数据
     */
    private fun ResultPoHeartDataParsing(mHeartBean: HeartBean, date: String) {
        MyLog.i(TAG, "解析 = mHeartBean = $mHeartBean")
        if (mHeartBean.data.size > 0) {
            val mHeartInfo = HeartBean.getHeartInfo(mHeartBean.data[0])
            MyLog.i(TAG, "请求接口-获取整点心率数据 mHeartInfo = $mHeartInfo")
            val isSuccess: Boolean = mHeartInfoUtils.MyUpdateData(mHeartInfo)
            if (isSuccess) {
                MyLog.i(TAG, "插入整点心率表成功！")
            } else {
                MyLog.i(TAG, "插入整点心率失败！")
            }
        } else {
            HeartBean.insertPoNullData(mHeartInfoUtils, date)
        }
        getPoHeartDay(false)
    }
}
