package com.zjw.apps3pluspro.module.home.sleep

import android.annotation.SuppressLint
import android.view.View
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import butterknife.OnClick
import com.android.volley.VolleyError
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.application.BaseApplication
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.module.home.entity.SleepData
import com.zjw.apps3pluspro.module.home.entity.SleepModel
import com.zjw.apps3pluspro.network.NewVolleyRequest
import com.zjw.apps3pluspro.network.RequestJson
import com.zjw.apps3pluspro.network.ResultJson
import com.zjw.apps3pluspro.network.VolleyInterface
import com.zjw.apps3pluspro.network.javabean.SleepBean
import com.zjw.apps3pluspro.sql.entity.SleepInfo
import com.zjw.apps3pluspro.utils.*
import com.zjw.apps3pluspro.utils.log.MyLog
import com.zjw.apps3pluspro.view.dialog.WaitDialog
import com.zjw.apps3pluspro.view.mycalendar.MyCalendarUtils
import kotlinx.android.synthetic.main.public_head_white_text.*
import kotlinx.android.synthetic.main.sleep_history_activity.*
import kotlinx.android.synthetic.main.sleep_history_layout.*
import org.json.JSONObject
import java.util.*

class SleepHistoryActivity : BaseActivity() {
    private val mUserSetTools = BaseApplication.getUserSetTools()
    private val mSleepInfoUtils = BaseApplication.getSleepInfoUtils()
    private var waitDialog: WaitDialog? = null
    private val TAG = SleepHistoryActivity::class.java.simpleName
    private var registTime: String? = null
    private var selectionDate: String? = null
    override fun setLayoutId(): Int {
        return R.layout.sleep_history_activity
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

    override fun initViews() {
        super.initViews()
        public_head_title.text = resources.getString(R.string.title_sleep)
        ivTitleType.background = ContextCompat.getDrawable(this@SleepHistoryActivity, R.mipmap.title_sleep_icon)
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
                        getSleepWeek(true)
                    } else {
                        AppUtils.showToast(this@SleepHistoryActivity, R.string.calendar_no_touchou)
                    }
                }
            }
        })

        tvTarget.setText("(" + MyTime.getSleepTime_H(mUserSetTools._user_sleep_target, resources.getString(R.string.sleep_gang))
                + resources.getString(R.string.hour) + MyTime.getSleepTime_M(mUserSetTools._user_sleep_target, resources.getString(R.string.sleep_gang))
                + resources.getString(R.string.minute) + ")")

        try {
            targetProgress.max = mUserSetTools._user_sleep_target.toInt()
        } catch (e: Exception) {
            targetProgress.max = DefaultVale.USER_SLEEP_TARGET.toString().toInt()
        }

        sleep_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                MyLog.i(TAG, "拖动睡眠 = 拖动停止")
                ll_sleep_state.visibility = View.INVISIBLE
                //图表
                if (sleep_list_data != null && sleep_list_data!!.size != 0) {
                    MyChartUtils.showDaySleepView(this@SleepHistoryActivity, sleep_list_data, rl_sleep_details_chart_view1, rl_sleep_details_chart_view2, rl_sleep_details_chart_view3)
                }
                sleep_seek_bar.thumb = resources.getDrawable(R.drawable.ecg_seekbar_bg)
            }

            /**
             * 拖动条开始拖动的时候调用
             */
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                ll_sleep_state.visibility = View.VISIBLE
                MyLog.i(TAG, "拖动睡眠 = 开始拖动 = ")
                sleep_seek_bar.thumb = resources.getDrawable(R.drawable.thumb_image)
            }

            /**
             * 拖动条进度改变的时候调用
             */
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                getSleepTimeSlot(progress)
            }
        })
    }

    var sleep_list_data: List<SleepData>? = null
    @SuppressLint("SetTextI18n")
    private fun updateUi(sleepinfoList: List<SleepInfo>) {
        val mSleepInfo = mSleepInfoUtils.MyQueryToDate(BaseApplication.getUserId(), selectionDate)
        if (mSleepInfo != null && !JavaUtil.checkIsNull(mSleepInfo.data)) {
            MyLog.i(TAG, "mSleepInfo = $mSleepInfo")
            val mSleepModel = SleepModel(mSleepInfo)
            MyLog.i(TAG, "mSleepModel = $mSleepModel")
            val start_time = mSleepModel.sleepStartTime //入睡时间
            val end_time = mSleepModel.sleepEndTime //醒来时间
            val total_time = mSleepModel.sleepTotalTime //总时间
            val sleep_total_time = mSleepModel.sleepSleepTime //睡眠总时间
            val deep_time = mSleepModel.sleepDeep //深睡时间
            val light_time = mSleepModel.sleepLight //浅睡时间
            val stayup_time = mSleepModel.sleepStayUpTime //熬夜
            val sober_time = mSleepModel.sleepSoberTime //清醒
            val woke_count = mSleepModel.sleepWoke //醒来次数
            sleep_list_data = mSleepModel.sleepListData //睡眠数据
            val deep_proportion: Int
            val light_proportion: Int
            val sober_proportion: Int
            val progress1 = mSleepModel.sleepDeep.toInt()
            val progress2 = mSleepModel.sleepLight.toInt()
            val progress3 = mSleepModel.sleepSoberTime.toInt()
            val total = progress1 + progress2 + progress3
            deep_proportion = progress1 * 100 / total
            if (progress3 == 0) {
                light_proportion = 100 - progress1 * 100 / total
                sober_proportion = 0
            } else if (progress3 * 100 / total == 0) {
                light_proportion = 100 - progress1 * 100 / total - 1
                sober_proportion = 1
            } else {
                light_proportion = 100 - progress1 * 100 / total - progress3 * 100 / total
                sober_proportion = progress3 * 100 / total
            }
            targetProgress.progress = sleep_total_time.toInt()
//            tvSleepHour.text = MyTime.getSleepTime_H(sleep_total_time, resources.getString(R.string.sleep_gang))
            tvSleepHour.text = MyTime.getHoursInt(sleep_total_time)
            tvSleepMinute.text = MyTime.getSleepTime_M(sleep_total_time, resources.getString(R.string.sleep_gang))

//            tvDeepSleep.text = MyTime.getSleepTime_H(deep_time, resources.getString(R.string.sleep_gang)) + resources.getString(R.string.hour) + MyTime.getSleepTime_M(deep_time, resources.getString(R.string.sleep_gang)) + resources.getString(R.string.minute)
//            tvLightSleep.text = MyTime.getSleepTime_H(light_time, resources.getString(R.string.sleep_gang)) + resources.getString(R.string.hour) + MyTime.getSleepTime_M(light_time, resources.getString(R.string.sleep_gang)) + resources.getString(R.string.minute)
//            tvAwakeSleep.text = MyTime.getSleepTime_H(sober_time, resources.getString(R.string.sleep_gang)) + resources.getString(R.string.hour) + MyTime.getSleepTime_M(sober_time, resources.getString(R.string.sleep_gang)) + resources.getString(R.string.minute)
            tvDeepSleep.text = MyTime.getHoursInt(deep_time)
            tvLightSleep.text = MyTime.getHoursInt(light_time)
            tvAwakeSleep.text = MyTime.getHoursInt(sober_time)

//            tvDeepSleepProgress.text = "$deep_proportion%"
//            tvLightSleepProgress.text = "$light_proportion%"
//            tvAwakeSleepProgress.text = "$sober_proportion%"

            multiProgressView.start(progress1, progress2, progress3)


            val target = mUserSetTools._user_sleep_target.toFloat()

            roundViewSleep.setProgress((progress3 + progress2 + progress1) / target, (progress2 + progress1) / target, progress1 / target)

            tv_sleep_start_time.text = start_time
            tv_sleep_end_time.text = end_time
            //睡眠目标
            tvComplete.text = """${AnalyticalUtils.getCompletionRateSleep(mUserSetTools, sleep_total_time)}%"""
            tvSleepProgress.text = """${AnalyticalUtils.getCompletionRateSleep(mUserSetTools, sleep_total_time)}%"""

            if (!JavaUtil.checkIsNull(sleep_total_time) && sleep_list_data?.size != 0) {
                sleep_details_lin_data_yes.visibility = View.VISIBLE
                sleep_details_seek_data_yes.visibility = View.VISIBLE
                MyChartUtils.showDaySleepView(this, sleep_list_data, rl_sleep_details_chart_view1, rl_sleep_details_chart_view2, rl_sleep_details_chart_view3)
                val totalMin = MyTime.TotalTime2(start_time, end_time)
                sleep_seek_bar.max = Integer.valueOf(totalMin)
                sleep_seek_bar.progress = 0
            } else {
                sleep_details_lin_data_yes.visibility = View.GONE
                sleep_details_seek_data_yes.visibility = View.GONE
            }
            layoutNoData.visibility = View.GONE
            layoutData.visibility = View.VISIBLE

        } else {
            sleep_details_lin_data_yes.visibility = View.GONE
            sleep_details_seek_data_yes.visibility = View.GONE

            layoutNoData.visibility = View.VISIBLE
            layoutData.visibility = View.GONE

            targetProgress.progress = 0
            tvSleepHour.text = resources.getString(R.string.sleep_gang)
            tvSleepMinute.text = resources.getString(R.string.sleep_gang)

            tv_sleep_start_time.text = resources.getString(R.string.sleep_gang)
            tv_sleep_end_time.text = resources.getString(R.string.sleep_gang)

//            tvDeepSleep.text = resources.getString(R.string.sleep_gang) + resources.getString(R.string.hour) + resources.getString(R.string.sleep_gang) + resources.getString(R.string.minute)
//            tvLightSleep.text = resources.getString(R.string.sleep_gang) + resources.getString(R.string.hour) + resources.getString(R.string.sleep_gang) + resources.getString(R.string.minute)
//            tvAwakeSleep.text = resources.getString(R.string.sleep_gang) + resources.getString(R.string.hour) + resources.getString(R.string.sleep_gang) + resources.getString(R.string.minute)
            tvDeepSleep.text = resources.getString(R.string.sleep_gang) + resources.getString(R.string.hour)
            tvLightSleep.text = resources.getString(R.string.sleep_gang) + resources.getString(R.string.hour)
            tvAwakeSleep.text = resources.getString(R.string.sleep_gang) + resources.getString(R.string.hour)

//            tvDeepSleepProgress.text = """${resources.getString(R.string.sleep_gang)}%"""
//            tvLightSleepProgress.text = """${resources.getString(R.string.sleep_gang)}%"""
//            tvAwakeSleepProgress.text = """${resources.getString(R.string.sleep_gang)}%"""

            tvComplete.text = """${resources.getString(R.string.sleep_gang)}%"""
            tvSleepProgress.text = """${resources.getString(R.string.sleep_gang)}%"""

            multiProgressView.start(0, 0, 0)
        }
    }

    override fun initDatas() {
        super.initDatas()
        registTime = BaseApplication.getRegisterTime()

        if (JavaUtil.checkIsNull(registTime)) {
            registTime = DefaultVale.USER_DEFULT_REGISTER_TIME
        } else {
            registTime = registTime?.split(" ")?.toTypedArray()?.get(0)
        }
        MyLog.i(TAG, "注册日期为 = $registTime")

        selectionDate = MyTime.getTime()
//        public_head_title.text = MyTime.getTime()
        calendarView.setSchemeDate(NewTimeUtils.getCycData(registTime))
        getSleepWeek(true)
    }

    fun getSleepWeek(is_cycle: Boolean) {
        MyLog.i(TAG, "getSleepWeek()")
        val week_list = NewTimeUtils.GetLastWeektDate(registTime, selectionDate)
        val start_date = week_list[0]
        val end_date = week_list[week_list.size - 1]
        MyLog.i(TAG, "待处理 开始时间 = $start_date")
        MyLog.i(TAG, "待处理 结束时间 = $end_date")
        val sleepInfo_list: List<SleepInfo> = mSleepInfoUtils.MyQueryToPeriodTime(BaseApplication.getUserId(), start_date, end_date)
        if (sleepInfo_list.size >= week_list.size) {
            MyLog.i(TAG, "待处理 数据够了 = 更新UI")
            updateUi(sleepInfo_list)
        } else {
            MyLog.i(TAG, "待处理 数据不够 = 获取数据")
            if (is_cycle) {
                requestSleepData(week_list, start_date, end_date)
            }
        }
    }

    private fun requestSleepData(my_data_list: ArrayList<String>, begin_time: String, end_time: String) {
        waitDialog!!.show(getString(R.string.loading0))
        val mRequestInfo = RequestJson.getSleepListData(begin_time, end_time)
        MyLog.i(TAG, "请求接口-获取睡眠数据 mRequestInfo = $mRequestInfo")
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                object : VolleyInterface(this, mListener, mErrorListener) {
                    override fun onMySuccess(result: JSONObject) { // TODO Auto-generated method stub
                        waitDialog!!.close()
                        MyLog.i(TAG, "请求接口-获取睡眠数据 请求成功 = result = $result")
                        val mSleepBean = ResultJson.SleepBean(result)
                        //请求成功
                        if (mSleepBean.isRequestSuccess) {
                            if (mSleepBean.isGetSleepSuccess == 1) {
                                MyLog.i(TAG, "请求接口-获取睡眠数据 成功")
                                ResultSleepDataParsing(my_data_list, mSleepBean)
                            } else if (mSleepBean.isGetSleepSuccess == 0) {
                                MyLog.i(TAG, "请求接口-获取睡眠数据 失败")
                                AppUtils.showToast(mContext, R.string.data_try_again_code1)
                            } else if (mSleepBean.isGetSleepSuccess == 2) {
                                MyLog.i(TAG, "请求接口-获取睡眠数据 无数据")
                                SleepBean.insertNullListData(mSleepInfoUtils, my_data_list)
                                getSleepWeek(false)
                            } else {
                                AppUtils.showToast(mContext, R.string.data_try_again_code1)
                            }
                            //请求失败
                        } else {
                            AppUtils.showToast(mContext, R.string.server_try_again_code0)
                        }
                    }

                    override fun onMyError(arg0: VolleyError) { // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-获取睡眠数据 请求失败 = message = " + arg0.message)
                        waitDialog!!.close()
                        AppUtils.showToast(mContext, R.string.net_worse_try_again)
                        return
                    }
                })
    }

    /**
     * 解析数据
     */
    private fun ResultSleepDataParsing(my_date_list: ArrayList<String>, mSleepBean: SleepBean) {
        MyLog.i(TAG, "待处理 日期数组 = $my_date_list")
        MyLog.i(TAG, "请求接口-获取睡眠数据 size = " + mSleepBean.data.size)
        val sleepInfo_list = mSleepBean.getSleepList(my_date_list, mSleepBean.data)
        for (mSleepInfo in sleepInfo_list) {
            MyLog.i(TAG, "解析数组 = sleepInfo_list = $mSleepInfo")
        }
        val isSuccess: Boolean = mSleepInfoUtils.insertInfoList(sleepInfo_list)
        if (isSuccess) {
            MyLog.i(TAG, "插入多条睡眠表成功！")
        } else {
            MyLog.i(TAG, "插入多条睡眠表失败！")
        }
        getSleepWeek(false)
    }

    fun getSleepTimeSlot(progress: Int) { //图表
        if (sleep_list_data != null && sleep_list_data!!.size != 0) {
            MyChartUtils.showDaySleepView(this, sleep_list_data, rl_sleep_details_chart_view1, rl_sleep_details_chart_view2, rl_sleep_details_chart_view3)
            val now = progress + Integer.valueOf(sleep_list_data!![0].startTimeMin) - 1
            for (i in sleep_list_data!!.indices) {
                if (now < Integer.valueOf(sleep_list_data!![i + 1].startTimeMin)) {
                    tv_sleep_state_start_time.text = sleep_list_data!![i].startTime
                    tv_sleep_state_end_time.text = sleep_list_data!![i + 1].startTime
                    tv_sleep_state.text = sleep_list_data!![i].getSleepState(this)
                    break
                }
            }
        }
    }
}
