package com.zjw.apps3pluspro.module.home.heart

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
import com.zjw.apps3pluspro.view.dialog.WaitDialog
import com.zjw.apps3pluspro.view.mycalendar.MyCalendarUtils
import kotlinx.android.synthetic.main.continuous_heart_history_activity.*
import kotlinx.android.synthetic.main.public_head_white_text.*
import org.json.JSONObject

class ContinuousHeartHistoryActivity : BaseActivity() {
    private val TAG = ContinuousHeartHistoryActivity::class.java.simpleName
    private var registTime: String? = null
    private var selectionDate: String? = null
    private var waitDialog: WaitDialog? = null
    private val mHeartInfoUtils = BaseApplication.getHeartInfoUtils()
    override fun setLayoutId(): Int {
        return R.layout.continuous_heart_history_activity
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
    fun updateUi(mHeartInfo: HeartInfo?) {
        val mHeartModel = HeartModel(mHeartInfo)
        val day_max = mHeartModel.heartDayMax
        val day_min = mHeartModel.heartDayMin
        val day_avg = mHeartModel.heartDayAverage
        val sleep_avg = mHeartModel.heartSleepAverage
        val wo_heart_data = mHeartModel.heartData
        val last_heart = mHeartModel.lastHeart

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
            val heartDataList: Array<String> = mHeartModel.heartData.split(",").toTypedArray()
            MyChartUtils.showDayWoHearBarChart(this, heartLineChart, heartDataList, true)
            layoutNoData.visibility = View.GONE
            layoutData.visibility = View.VISIBLE
        } else {
            layoutNoData.visibility = View.VISIBLE
            layoutData.visibility = View.GONE
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
        ivTitleType.background = ContextCompat.getDrawable(this@ContinuousHeartHistoryActivity, R.mipmap.title_heart_icon)
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
                        getWoHeartDay(true)
                    } else {
                        AppUtils.showToast(this@ContinuousHeartHistoryActivity, R.string.calendar_no_touchou)
                    }
                }
            }
        })
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
        getWoHeartDay(true)
    }

    fun getWoHeartDay(is_cycle: Boolean) {
        MyLog.i(TAG, "getWoHeartDay()")
        val mHeartInfo = mHeartInfoUtils.MyQueryToDate(BaseApplication.getUserId(), selectionDate, "1")
        if (mHeartInfo != null) {
            MyLog.i(TAG, "mHeartInfo = $mHeartInfo")
            updateUi(mHeartInfo)
        } else {
            MyLog.i(TAG, "mHeartInfo = null")
            if (is_cycle) {
                requestWoheartData(selectionDate!!)
            }
        }
    }

    private fun requestWoheartData(date: String) {
        waitDialog!!.show(getString(R.string.loading0))
        val mRequestInfo = RequestJson.getWoListData(date, date)
        MyLog.i(TAG, "请求接口-获取连续心率数据 mRequestInfo = $mRequestInfo")
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                object : VolleyInterface(this, mListener, mErrorListener) {
                    override fun onMySuccess(result: JSONObject) { // TODO Auto-generated method stub
                        waitDialog!!.close()
                        MyLog.i(TAG, "请求接口-获取连续心率数据 请求成功 = result = $result")
                        val mHeartBean = ResultJson.HeartBean(result)
                        //请求成功
                        if (mHeartBean.isRequestSuccess) {
                            if (mHeartBean.isGetHeartSuccess == 1) {
                                MyLog.i(TAG, "请求接口-获取连续心率数据 成功")
                                ResultWoHeartDataParsing(mHeartBean, date)
                            } else if (mHeartBean.isGetHeartSuccess == 0) {
                                MyLog.i(TAG, "请求接口-获取连续心率数据 失败")
                                AppUtils.showToast(mContext, R.string.data_try_again_code1)
                            } else if (mHeartBean.isGetHeartSuccess == 2) {
                                MyLog.i(TAG, "请求接口-获取连续心率数据 无数据")
                                HeartBean.insertWoNullData(mHeartInfoUtils, date)
                                getWoHeartDay(false)
                            } else {
                                MyLog.i(TAG, "请求接口-获取连续心率数据 请求异常(1)")
                                AppUtils.showToast(mContext, R.string.data_try_again_code1)
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取连续心率数据 请求异常(0)")
                            AppUtils.showToast(mContext, R.string.server_try_again_code0)
                        }
                    }

                    override fun onMyError(arg0: VolleyError) { // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-获取连续心率数据 请求失败 = message = " + arg0.message)
                        waitDialog!!.close()
                        AppUtils.showToast(mContext, R.string.net_worse_try_again)
                        return
                    }
                })
    }

    /**
     * 解析数据
     */
    private fun ResultWoHeartDataParsing(mHeartBean: HeartBean, date: String) {
        MyLog.i(TAG, "解析 = mWoHeartBean = $mHeartBean")
        if (mHeartBean.data.size >= 1) {
            val mHeartInfo = HeartBean.getHeartInfo(mHeartBean.data[0])
            MyLog.i(TAG, "请求接口-获取连续心率数据 mHeartInfo = $mHeartInfo")
            val isSuccess = mHeartInfoUtils.MyUpdateData(mHeartInfo)
            if (isSuccess) {
                MyLog.i(TAG, "插入连续心率表成功！")
            } else {
                MyLog.i(TAG, "插入连续心率失败！")
            }
        } else {
            HeartBean.insertWoNullData(mHeartInfoUtils, date)
        }
        getWoHeartDay(false)
    }
}
