package com.zjw.apps3pluspro.module.home.exercise

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import butterknife.OnClick
import com.android.volley.VolleyError
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView.OnCalendarSelectListener
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.application.BaseApplication
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.bleservice.BleTools
import com.zjw.apps3pluspro.module.home.DataManager
import com.zjw.apps3pluspro.network.NewVolleyRequest
import com.zjw.apps3pluspro.network.RequestJson
import com.zjw.apps3pluspro.network.ResultJson
import com.zjw.apps3pluspro.network.VolleyInterface
import com.zjw.apps3pluspro.network.javabean.SportBean
import com.zjw.apps3pluspro.sql.entity.MovementInfo
import com.zjw.apps3pluspro.utils.*
import com.zjw.apps3pluspro.utils.log.MyLog
import com.zjw.apps3pluspro.view.dialog.WaitDialog
import com.zjw.apps3pluspro.view.mycalendar.MyCalendarUtils
import kotlinx.android.synthetic.main.public_head_white_text.*
import kotlinx.android.synthetic.main.step_history_activity.*
import kotlinx.android.synthetic.main.step_history_layout.*
import org.json.JSONObject
import java.util.*

class StepHistoryActivity : BaseActivity() {
    private var registTime: String? = null
    private var selectionDate: String? = null
    private val mMovementInfoUtils = BaseApplication.getMovementInfoUtils()

    //加载相关
    private var waitDialog: WaitDialog? = null
    private val TAG = StepHistoryActivity::class.java.simpleName
    override fun setLayoutId(): Int {
        return R.layout.step_history_activity
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

    @SuppressLint("SetTextI18n")
    override fun initViews() {
        super.initViews()
        public_head_title.text = resources.getString(R.string.page_exercise_title)
        ivTitleType.background = ContextCompat.getDrawable(this@StepHistoryActivity, R.mipmap.title_step_icon)
        waitDialog = WaitDialog(this)

        calendarView.setOnYearChangeListener {
        }
        calendarView.setOnMonthChangeListener { year, month ->
            val date = NewTimeUtils.FormatDateYYYYMM(year, month)
            public_head_title.text = date
        }
        calendarView.setOnCalendarSelectListener(object : OnCalendarSelectListener {
            override fun onCalendarOutOfRange(calendar: Calendar) {}
            override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
                val date = NewTimeUtils.FormatDateYYYYMMDD(calendar)
                if (isClick) {
                    //如果等于加入的数据，类型等于 ONE_TYPE,则查询数据
                    if (calendar.scheme != null && calendar.scheme != ""
                            && calendar.scheme == MyCalendarUtils.ONE_TYPE) {
                        selectionDate = date
                        public_head_title.setText(selectionDate)
                        calendarLayout.shrink()
                        getSportData(false)
                    } else {
                        AppUtils.showToast(context, R.string.calendar_no_touchou)
                    }
                }
            }
        })
        val target = mUserSetTools._user_exercise_target
        tvTarget.text = "($target)"

        tvSlidingValue.visibility = View.INVISIBLE
        stepHistogramView.setOnSlidingListener { data: Float, index: Int, time: Int, step: Int ->
            if (index != -1) {
                var timeFormat: String
                if (index < 10) {
                    timeFormat = "0$index:00  "
                } else {
                    timeFormat = "$index:00  "
                }


                tvSlidingValue.text = timeFormat + "$step" + " " + resources.getString(R.string.steps)
            } else {
                tvSlidingValue.visibility = View.INVISIBLE
            }
        }
        stepHistogramView.setOnTouchListener { _, event ->
            val eventX = event.x
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    tvSlidingValue.visibility = View.VISIBLE
                    stepHistogramView.setTouchPos(eventX)
                }
                MotionEvent.ACTION_MOVE -> stepHistogramView.setTouchPos(eventX)
                MotionEvent.ACTION_UP -> {
                    tvSlidingValue.visibility = View.INVISIBLE
                }
            }
            stepHistogramView.invalidate()
            true
        }
    }

    private val mBleDeviceTools = BaseApplication.getBleDeviceTools()
    private val mUserSetTools = BaseApplication.getUserSetTools()
    fun updateUi(mMovementInfo: MovementInfo) {
        val steps = mMovementInfo.total_step
        val calory = mMovementInfo.calorie
        var distance = mMovementInfo.disance
        val sport_data = mMovementInfo.data
        if (!JavaUtil.checkIsNull(sport_data) && !JavaUtil.checkIsNull(steps)) {
            val steps24 = mMovementInfo.data.split(",").toTypedArray()
            var max = 0
            val progress = FloatArray(24)
            val progressStep = IntArray(24)
            for (i in 0..23) {
                progress[i] = steps24[i].toFloat()
                progressStep[i] = steps24[i].toInt()
                if (progress[i] > max) {
                    max = progress[i].toInt()
                }
            }
            max = max / 100 * 100 + 100
            stepHistogramView.start(progress, max.toFloat(), 1, null, progressStep, false)
            tvExerciseStep.text = steps

            if (mBleDeviceTools.get_device_unit() == 1) { // 公制
                distance = AppUtils.GetTwoFormat(java.lang.Float.valueOf(distance))
                tvDistanceUnit.text = resources.getString(R.string.sport_distance_unit)
            } else {
                distance = AppUtils.GetTwoFormat(java.lang.Float.valueOf(BleTools.getBritishSystem(steps)))
                tvDistanceUnit.text = resources.getString(R.string.unit_mi)
            }
            tvExerciseDistance.text = distance
            tvExerciseCal.text = calory
            tvComplete.text = AnalyticalUtils.getCompletionRate(mUserSetTools, steps) + "%"

            val target = mUserSetTools._user_exercise_target

            tvStepProgress.text = AnalyticalUtils.getCompletionRate(mUserSetTools, steps) + "%"
            roundViewStep.setProgress(0f, steps.toInt() / target.toFloat(), 0f)

            targetProgress.progress = Integer.parseInt(AnalyticalUtils.getCompletionRate(mUserSetTools, steps))

            layoutData.visibility = View.VISIBLE
            layoutNoData.visibility = View.GONE
        } else {
            noData()
        }
    }

    private fun noData() {
        val progress = FloatArray(24)
        stepHistogramView.start(progress, 100f, 1, null, null, false)

        tvExerciseStep.text = resources.getString(R.string.sleep_gang)
        tvExerciseDistance.text = resources.getString(R.string.sleep_gang)
        tvExerciseCal.text = resources.getString(R.string.sleep_gang)
        tvComplete.text = resources.getString(R.string.sleep_gang) + "%"
        targetProgress.progress = 0;

        layoutNoData.visibility = View.VISIBLE
        layoutData.visibility = View.GONE
    }

    override fun initDatas() {
        super.initDatas()
        registTime = BaseApplication.getRegisterTime()
        if (JavaUtil.checkIsNull(registTime)) {
            registTime = DefaultVale.USER_DEFULT_REGISTER_TIME
        } else {
            val xxx = registTime
            registTime = xxx?.split(" ")?.toTypedArray()?.get(0)
        }
        selectionDate = MyTime.getTime()
//        public_head_title.setText(selectionDate)
        calendarView.setSchemeDate(NewTimeUtils.getCycData(registTime))
        getSportData(true)

    }

    fun getSportData(init: Boolean) {
        MyLog.i(TAG, "getSportData()")
        try {
            val mMovementInfo = mMovementInfoUtils.MyQueryToDate(BaseApplication.getUserId(), selectionDate)
            if (mMovementInfo != null) {
                updateUi(mMovementInfo)
            } else {
                waitDialog!!.show(getString(R.string.loading0))
                DataManager.getInstance().getSportDay(context, true, selectionDate) { movementInfo ->
                    if (init) {
                        waitDialog!!.close()
                    } else {
                        waitDialog!!.close(150)
                    }
                    if (movementInfo != null) {
                        MyLog.i(TAG, "movementInfo = $movementInfo")
                        updateUi(movementInfo as MovementInfo)
                    } else {
                        noData()
                    }
                }
            }
        } catch (e: Exception) {
            waitDialog!!.close()
            noData()
        }
    }
}
