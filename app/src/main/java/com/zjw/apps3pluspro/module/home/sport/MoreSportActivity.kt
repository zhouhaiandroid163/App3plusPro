package com.zjw.apps3pluspro.module.home.sport

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.OnClick
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.adapter.MoreSportRecyclerAdapter
import com.zjw.apps3pluspro.application.BaseApplication
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.module.home.sport.DeviceSportManager.Companion.instance
import com.zjw.apps3pluspro.module.home.sport.amap.AmapGpsSportActivity
import com.zjw.apps3pluspro.module.home.sport.amap.AmapLocusActivity
import com.zjw.apps3pluspro.module.home.sport.google.GoogleGpsSportActivity
import com.zjw.apps3pluspro.module.home.sport.google.GoogleLocusActivity
import com.zjw.apps3pluspro.sql.entity.SportModleInfo
import com.zjw.apps3pluspro.utils.*
import com.zjw.apps3pluspro.view.dialog.WaitDialog
import com.zjw.apps3pluspro.view.mycalendar.MyCalendarUtils
import kotlinx.android.synthetic.main.more_sport_activity.*
import kotlinx.android.synthetic.main.public_head_white_text.*

class MoreSportActivity : BaseActivity() {
    companion object {
        var sportModleInfo: SportModleInfo? = null
    }

    private lateinit var pageManageRecyclerAdapter: MoreSportRecyclerAdapter
    //加载相关
    private var waitDialog: WaitDialog? = null
    private val TAG = MoreSportActivity::class.java.simpleName
    private var selectionDate: String? = null
    private var registTime: String? = null
    private var sportModleInfos = ArrayList<SportModleInfo>()
    private val mSportModleInfoUtils = BaseApplication.getSportModleInfoUtils()

    override fun setLayoutId(): Int {
        return R.layout.more_sport_activity
    }

    override fun initViews() {
        super.initViews()
        public_head_title.text = resources.getString(R.string.movement_history)
        ivTitleType.background = ContextCompat.getDrawable(this@MoreSportActivity, R.mipmap.title_gps_icon)
        waitDialog = WaitDialog(this)

        recyclerView.layoutManager = LinearLayoutManager(this);

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
                if (isClick) {
                    //如果等于加入的数据，类型等于 ONE_TYPE,则查询数据
                    if (calendar.scheme != null && calendar.scheme != ""
                            && calendar.scheme == MyCalendarUtils.ONE_TYPE) {
                        selectionDate = date
                        public_head_title.text = selectionDate
                        calendarLayout.shrink()

                        queryData()
                    } else {
                        AppUtils.showToast(context, R.string.calendar_no_touchou)
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        waitDialog!!.dismiss()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
//        queryData()
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
        calendarView.setSchemeDate(NewTimeUtils.getCycData(registTime))

        queryData()
    }

    fun queryData() {
        val startTime = NewTimeUtils.getLongTime(selectionDate, NewTimeUtils.TIME_YYYY_MM_DD)
        val endTime = startTime + 24 * 3600 * 1000L - 1
        sportModleInfos = mSportModleInfoUtils.queryByTime(startTime, endTime) as ArrayList<SportModleInfo>

        if (sportModleInfos.size == 0) {
            pageManageRecyclerAdapter = MoreSportRecyclerAdapter(context, sportModleInfos)
            recyclerView.adapter = pageManageRecyclerAdapter
            pageManageRecyclerAdapter.notifyDataSetChanged()

            instance.getMoreSportData(selectionDate.toString(), object : DeviceSportManager.GetDataSuccess {
                override fun onSuccess() {
                    sportModleInfos = mSportModleInfoUtils.queryByTime(startTime, endTime) as ArrayList<SportModleInfo>
                    initNotify()
                }

                override fun onError() {
                    initNotify()
                }
            })
        } else {
            initNotify()
        }
    }

    fun initNotify() {
        if (sportModleInfos.size == 0) {
            layoutNoData.visibility = View.VISIBLE
            layoutData.visibility = View.GONE
        } else {
            layoutNoData.visibility = View.GONE
            layoutData.visibility = View.VISIBLE
        }
        pageManageRecyclerAdapter = MoreSportRecyclerAdapter(context, sportModleInfos)
        recyclerView.adapter = pageManageRecyclerAdapter
        pageManageRecyclerAdapter.setmCallback { v, position ->
            sportModleInfo = sportModleInfos[position]

            if (sportModleInfo != null) {
                if (sportModleInfo!!.dataSourceType == 0) {
                    if (sportModleInfo!!.getUi_type() == "100") {
                        if (MyUtils.isGoogle(this)) { //谷歌
                            val intent = Intent(this, GoogleLocusActivity::class.java)
//                            intent.putExtra(IntentConstants.SportModleInfo, sportModleInfo)
                            startActivity(intent)
                        } else { //高德
                            val intent = Intent(this, AmapLocusActivity::class.java)
//                            intent.putExtra(IntentConstants.SportModleInfo, sportModleInfo)
                            startActivity(intent)
                        }
                    } else {
//                        val intent = Intent(this, SportPatternDetailsActivity::class.java)
////                        intent.putExtra(IntentConstants.SportModleInfo, sportModleInfo)
//                        startActivity(intent)
                    }
                } else {
                    val intent = Intent(this, DeviceSportDetailsActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        pageManageRecyclerAdapter.notifyDataSetChanged()
    }

    @OnClick(R.id.layoutCalendar, R.id.start_sport)
    fun viewOnClick(view: View) {
        when (view.id) {
            R.id.layoutCalendar -> {
                if (calendarLayout.isExpand) {
                    calendarLayout.shrink()
                } else {
                    calendarLayout.expand()
                }
            }
            R.id.start_sport -> {
                startGpsSport();
            }
        }
    }

    private fun startGpsSport() {
        if (MyUtils.isGPSOpen(this)) {
            val intent: Intent
            if (MyUtils.isGoogle(this@MoreSportActivity)) { //谷歌
                intent = Intent(this, GoogleGpsSportActivity::class.java)
                startActivityForResult(intent, sportStartCode)
            } else { //高德
                intent = Intent(this, AmapGpsSportActivity::class.java)
                startActivityForResult(intent, sportStartCode)
            }
        } else {
            showSettingGps()
        }
    }

    private val sportStartCode = 1
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == sportStartCode) {
            queryData()
        }
    }

    private fun showSettingGps() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_prompt)) //设置对话框标题
                .setMessage(getString(R.string.open_gps)) //设置显示的内容
                .setPositiveButton(getString(R.string.dialog_yes)) { dialog, which ->
                    val intent = Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent) // 设置完成后返回到原来的界面
                }.setNegativeButton(getString(R.string.dialog_no)) { dialog, which ->
                }.show() //在按键响应事件中显示此对话框
    }


}
