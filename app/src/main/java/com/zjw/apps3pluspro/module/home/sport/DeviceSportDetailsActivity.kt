package com.zjw.apps3pluspro.module.home.sport

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import butterknife.OnClick
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.LatLngBounds
import com.amap.api.maps.model.MarkerOptions
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.PolylineOptions
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.application.BaseApplication
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.bleservice.BleTools
import com.zjw.apps3pluspro.module.home.entity.DeviceSportEntity
import com.zjw.apps3pluspro.module.home.entity.DeviceSportSwimEntity
import com.zjw.apps3pluspro.module.home.sport.amap.PathSmoothTool
import com.zjw.apps3pluspro.sql.entity.SportModleInfo
import com.zjw.apps3pluspro.utils.*
import com.zjw.apps3pluspro.utils.log.MyLog
import kotlinx.android.synthetic.main.device_sport_cal.*
import kotlinx.android.synthetic.main.device_sport_details_activity.*
import kotlinx.android.synthetic.main.device_sport_effect.*
import kotlinx.android.synthetic.main.device_sport_heart.*
import kotlinx.android.synthetic.main.device_sport_height.*
import kotlinx.android.synthetic.main.device_sport_height.mHeightCurveChartView
import kotlinx.android.synthetic.main.device_sport_height.tvCumulativeDecline
import kotlinx.android.synthetic.main.device_sport_height.tvCumulativeRise
import kotlinx.android.synthetic.main.device_sport_map.*
import kotlinx.android.synthetic.main.device_sport_pace.*
import kotlinx.android.synthetic.main.device_sport_speed.*
import kotlinx.android.synthetic.main.device_sport_step_speed.*
import kotlinx.android.synthetic.main.device_sport_step_speed.tvAvgStepLength
import kotlinx.android.synthetic.main.device_sport_step_speed.tvMaxStepSpeed
import kotlinx.android.synthetic.main.device_sport_swim_frequency.*
import kotlinx.android.synthetic.main.device_sport_swim_swolf.*
import kotlinx.android.synthetic.main.public_head_white_text.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

@Suppress("DEPRECATION")
class DeviceSportDetailsActivity : BaseActivity(), OnMapReadyCallback {
    private var unitType: Int = 0
    private val TAG = DeviceSportDetailsActivity::class.java.simpleName
    private val mBleDeviceTools = BaseApplication.getBleDeviceTools()
    private var sportType: Int = 0
    var sportModleInfo: SportModleInfo? = null
    var caloriesFmt = DecimalFormat(",##0.00")
    override fun setLayoutId(): Int {
        isTextDark = false
        return R.layout.device_sport_details_activity
    }

    private val mSportModleInfoUtils = BaseApplication.getSportModleInfoUtils()

    override fun initViews() {
        super.initViews()
        caloriesFmt.roundingMode = RoundingMode.DOWN
        unitType = mBleDeviceTools._device_unit
        sportModleInfo = MoreSportActivity.sportModleInfo
        ivRight.background = ContextCompat.getDrawable(this, R.mipmap.device_sport_share)
        ivTitleType.visibility = View.GONE
        layoutCalendar.visibility = View.GONE
        ivRight.visibility = View.GONE
        sportType = sportModleInfo?.recordPointSportType!!
        public_head_title.text = SportModleUtils.getDeviceSportTypeStr(context, sportType)

        if (sportModleInfo?.recordPointSportData == null) {
            // 后台获取
            DeviceSportManager.instance.getMoreSportDetail(sportModleInfo?.dataSourceType!!, sportModleInfo?.serviceId!!, object : DeviceSportManager.GetDataSuccess {
                override fun onSuccess() {
                    val sportModleInfos = mSportModleInfoUtils.queryByServerId(sportModleInfo?.serviceId!!)
                    sportModleInfo = sportModleInfos[0]
                    viewGone()
                    initViewData()
                }

                override fun onError() {
                    viewGone()
                    initViewData()
                }
            })
        } else {
            viewGone()
        }
    }

    override fun initDatas() {
        super.initDatas()
        initViewData()
    }


    @SuppressLint("SetTextI18n")
    fun initViewData() {
        try {
            if (sportModleInfo?.reportDuration!! == 0L || sportModleInfo?.recordPointSportData == null) {
                layoutData.visibility = View.GONE
                layoutNoData.visibility = View.VISIBLE
                return
            }

            loadMap()

            layoutData.visibility = View.VISIBLE
            layoutNoData.visibility = View.GONE

            val avgPaceString: String
            var avgPace: Double = 0.0
            if (sportModleInfo?.reportDistance!! != 0L) {
                if (isUserM(sportType)) {
                    avgPace = sportModleInfo?.reportDuration!! / ((sportModleInfo?.reportDistance!! / 100.0))
                } else {
                    avgPace = sportModleInfo?.reportDuration!! / ((sportModleInfo?.reportDistance!! / 1000.0))
                }
            }
            val minute = (avgPace / 60).toInt()
            val second = (avgPace % 60).toInt()
            if (SysUtils.isShow00Pace(minute, second)) {
                avgPaceString = String.format("%1$02d'%2$02d\"", 0, 0)
            } else {
                if (mBleDeviceTools._device_unit == 1) {
                    avgPaceString = String.format("%1$02d'%2$02d\"", (avgPace / 60).toInt(), (avgPace % 60).toInt())
                } else {
                    if (isUserM(sportType)) {
                        avgPace = sportModleInfo?.reportDuration!! / ((sportModleInfo?.reportDistance!! * 3.28 / 100))
                    } else {
                        avgPace = sportModleInfo?.reportDuration!! / ((sportModleInfo?.reportDistance!! / 1000.0 / 1.61f))
                    }
                    avgPaceString = String.format("%1$02d'%2$02d\"", (avgPace / 60).toInt(), (avgPace % 60).toInt())
                }
            }

            tvTitleValue1.text = NewTimeUtils.getTimeString(sportModleInfo?.reportDuration!!)
            tvTitleValue2.text = NewTimeUtils.getStringDate(sportModleInfo?.reportSportStartTime!!, NewTimeUtils.HHMMSS) + " - " + NewTimeUtils.getStringDate(sportModleInfo?.reportSportEndTime!!, NewTimeUtils.HHMMSS)

            if (isUserM(sportType)) {
                tvTitleValue3.text = sportModleInfo?.reportDistance.toString() + " " + resources.getString(R.string.device_sport_unit)
            } else {
                tvTitleValue3.text = caloriesFmt.format(sportModleInfo?.reportDistance!! / 1000.0) + " " + resources.getString(R.string.sport_distance_unit)
            }

            tvTitleValue4.text = sportModleInfo?.reportCal.toString() + " " + resources.getString(R.string.big_calory)
            tvTitleValue5.text = avgPaceString
            tvTitleValue6.text = caloriesFmt.format(((sportModleInfo?.reportDistance!! / 1000.0) / (sportModleInfo?.reportDuration!! / 3600.0))).toString() + " " + resources.getString(R.string.speed_unit)
            tvTitleValue7.text = sportModleInfo?.reportAvgHeart!!.toString() + " " + resources.getString(R.string.bpm)
            tvTitleValue8.text = sportModleInfo?.reportMaxHeart!!.toString() + " " + resources.getString(R.string.bpm)
            tvTitleValue9.text = sportModleInfo?.reportTotalStep!!.toString() + " " + resources.getString(R.string.steps)
            tvTitleValue10.text = (sportModleInfo?.reportTotalStep!! / (sportModleInfo?.reportDuration!! / 60.0)).toInt().toString() + " " + resources.getString(R.string.device_sport_step_speed_unit)
            tvTitleValue11.text = sportModleInfo?.reportCumulativeRise!!.toString() + " " + resources.getString(R.string.device_sport_unit)
            tvTitleValue12.text = sportModleInfo?.reportCumulativeDecline!!.toString() + " " + resources.getString(R.string.device_sport_unit)
            tvAvgHeightValue.text = sportModleInfo?.reportAvgHeight!!.toString() + " " + resources.getString(R.string.device_sport_unit)
            tvTitleValue13.text = DeviceSportManager.instance.getSwimStyle(sportModleInfo?.reportSwimStyle, context)


            if (unitType != 1) {
                if (isUserM(sportType)) {
                    tvTitleValue3.text = caloriesFmt.format(sportModleInfo?.reportDistance!! * 3.28f) + " " + resources.getString(R.string.unit_ft)
                } else {
                    tvTitleValue3.text = caloriesFmt.format(sportModleInfo?.reportDistance!! / 1000.0 / 1.61f) + " " + resources.getString(R.string.unit_mi)
                }

                tvTitleValue6.text = caloriesFmt.format(((sportModleInfo?.reportDistance!! / 1000.0 / 1.61f) / (sportModleInfo?.reportDuration!! / 3600.0))).toString() + " " + resources.getString(R.string.speed_unit_mi)
                tvTitleValue11.text = caloriesFmt.format(sportModleInfo?.reportCumulativeRise!! * 3.28f) + " " + resources.getString(R.string.unit_ft)
                tvTitleValue12.text = caloriesFmt.format(sportModleInfo?.reportCumulativeDecline!! * 3.28f) + " " + resources.getString(R.string.unit_ft)
                tvAvgHeightValue.text = caloriesFmt.format(sportModleInfo?.reportAvgHeight!! * 3.28f) + " " + resources.getString(R.string.unit_ft)
            }

            tvTrainingEffectScore.text = sportModleInfo?.reportTrainingEffect!!.toString()
            tvMaxOxygenIntakeTitleValue.text = sportModleInfo?.reportMaxOxygenIntake!!.toString() + " " + resources.getString(R.string.device_sport_maxOxygenIntake_unit)
            tvRecoveryTimeValue.text = sportModleInfo?.reportRecoveryTime!!.toString() + " " + resources.getString(R.string.hour)

            tvHeartAvgHeart.text = sportModleInfo?.reportAvgHeart!!.toString()
            tvHeartMaxHeart.text = sportModleInfo?.reportMaxHeart!!.toString()
            tvHeartMinHeart.text = sportModleInfo?.reportMinHeart!!.toString()
            tvDeviceSportHeartLimit.text = NewTimeUtils.getTimeString(sportModleInfo?.reportHeartLimitTime!!)
            tvDeviceSportHeartAnaerobic.text = NewTimeUtils.getTimeString(sportModleInfo?.reportHeartAnaerobic!!)
            tvDeviceSportHeartAerobic.text = NewTimeUtils.getTimeString(sportModleInfo?.reportHeartAerobic!!)
            tvDeviceSportHeartFatBurning.text = NewTimeUtils.getTimeString(sportModleInfo?.reportHeartFatBurning!!)
            tvDeviceSportHeartWarmUp.text = NewTimeUtils.getTimeString(sportModleInfo?.reportHeartWarmUp!!)
            var durationTime = sportModleInfo?.reportHeartLimitTime!! + sportModleInfo?.reportHeartAnaerobic!! + sportModleInfo?.reportHeartAerobic!! + sportModleInfo?.reportHeartFatBurning!! + sportModleInfo?.reportHeartWarmUp!!;
            if (durationTime != 0L) {
                var progress1: Long = sportModleInfo?.reportHeartLimitTime!! * 100 / durationTime
                var progress2: Long = sportModleInfo?.reportHeartAnaerobic!! * 100 / durationTime
                var progress3: Long = sportModleInfo?.reportHeartAerobic!! * 100 / durationTime
                var progress4: Long = sportModleInfo?.reportHeartFatBurning!! * 100 / durationTime
                if (sportModleInfo?.reportHeartLimitTime!! > 0 && progress1 == 0L) {
                    progress1 = 1
                }
                if (sportModleInfo?.reportHeartAnaerobic!! > 0 && progress2 == 0L) {
                    progress2 = 1
                }
                if (sportModleInfo?.reportHeartAerobic!! > 0 && progress3 == 0L) {
                    progress3 = 1
                }
                if (sportModleInfo?.reportHeartFatBurning!! > 0 && progress4 == 0L) {
                    progress4 = 1
                }

                val progress5: Long = 100 - progress1 - progress2 - progress3 - progress4

                tvDeviceSportHeartLimitProgress.text = "$progress1%"
                tvDeviceSportHeartAnaerobicProgress.text = "$progress2%"
                tvDeviceSportHeartAerobicProgress.text = "$progress3%"
                tvDeviceSportHeartFatBurningProgress.text = "$progress4%"
                tvDeviceSportHeartWarmUpProgress.text = "$progress5%"
                heartDistributedView.start(progress1.toInt(), progress2.toInt(), progress3.toInt(), progress4.toInt(), progress5.toInt())
            }

            tvAvgPace.text = avgPaceString
            val maxPace: Long = sportModleInfo?.reportFastPace!!
            if (SysUtils.isShow00Pace(maxPace.toInt())) {
                tvMaxPace.text = String.format("%1$02d'%2$02d\"", 0, 0)
            } else {
                if (unitType == 1) {
                    tvMaxPace.text = String.format("%1$02d'%2$02d\"", (maxPace / 60).toInt(), (maxPace % 60).toInt())
                } else {
                    tvMaxPace.text = String.format("%1$02d'%2$02d\"", (maxPace * 1.61f / 60).toInt(), (maxPace * 1.61f % 60).toInt())
                }
            }

            val minPace: Long = sportModleInfo?.reportSlowestPace!!
            if (SysUtils.isShow00Pace(minPace.toInt())) {
                tvMinPace.text = String.format("%1$02d'%2$02d\"", 0, 0)
            } else {
                if (unitType == 1) {
                    tvMinPace.text = String.format("%1$02d'%2$02d\"", (minPace / 60).toInt(), (minPace % 60).toInt())
                } else {
                    tvMinPace.text = String.format("%1$02d'%2$02d\"", (minPace * 1.61f / 60).toInt(), (minPace * 1.61f % 60).toInt())
                }
            }

            tvAvgStepSpeed.text = (sportModleInfo?.reportTotalStep!! / (sportModleInfo?.reportDuration!! / 60.0)).toInt().toString()
            tvMaxStepSpeed.text = (sportModleInfo?.reportMaxStepSpeed!!).toString()

            if (sportModleInfo?.reportTotalStep!! == 0L) {
                tvAvgStepLength.text = "0"
                if (unitType != 1) {
                    tvAvgStepLength.text = "0"
                    tvAvgStepLengthUnit.text = resources.getText(R.string.unit_in)
                }
            } else {
                tvAvgStepLength.text = (sportModleInfo?.reportDistance!! * 100 / sportModleInfo?.reportTotalStep!!).toString()
                if (unitType != 1) {
                    tvAvgStepLength.text = (sportModleInfo?.reportDistance!! * 100 * 0.393 / sportModleInfo?.reportTotalStep!!).toInt().toString()
                    tvAvgStepLengthUnit.text = resources.getText(R.string.unit_in)
                }
            }

            tvAvgSpeed.text = caloriesFmt.format((sportModleInfo?.reportDistance!! / 1000.0 / (sportModleInfo?.reportDuration!! / 3600.0))).toString()
            if (unitType != 1) {
                tvAvgSpeed.text = caloriesFmt.format((sportModleInfo?.reportDistance!! / 1000.0 / 1.61f / (sportModleInfo?.reportDuration!! / 3600.0))).toString()
                tvAvgSpeedUnit.text = resources.getText(R.string.speed_unit_mi)
            }

            tvMaxSpeed.text = caloriesFmt.format(sportModleInfo?.reportFastSpeed!!)
            tvTitleMaxSpeedValue.text = caloriesFmt.format(sportModleInfo?.reportFastSpeed!!)  + " " + resources.getString(R.string.speed_unit)
            if (unitType != 1) {
                tvMaxSpeed.text = caloriesFmt.format(sportModleInfo?.reportFastSpeed!! / 1.61f)
                tvMaxSpeedUnit.text = resources.getText(R.string.speed_unit_mi)
                tvTitleMaxSpeedValue.text = caloriesFmt.format(sportModleInfo?.reportFastSpeed!! / 1.61f)  + " " + resources.getText(R.string.speed_unit_mi)
            }

            tvTotalCal.text = sportModleInfo?.reportCal!!.toString()
            tvAvgCal.text = caloriesFmt.format((sportModleInfo?.reportCal!! / (sportModleInfo?.reportDuration!! / 60.0))).toString()

            tvCumulativeRise.text = sportModleInfo?.reportCumulativeRise!!.toString()
            tvCumulativeDecline.text = sportModleInfo?.reportCumulativeDecline!!.toString()
            if (unitType != 1) {
                tvCumulativeRise.text = caloriesFmt.format(sportModleInfo?.reportCumulativeRise!! * 3.28f)
                tvCumulativeDecline.text = caloriesFmt.format(sportModleInfo?.reportCumulativeDecline!! * 3.28f)
                tvHeightUnit1.text = resources.getString(R.string.unit_ft)
                tvHeightUnit2.text = resources.getString(R.string.unit_ft)
            }

            tvAvgSwimFrequency.text = (sportModleInfo!!.reportTotalSwimNum / (sportModleInfo?.reportDuration!! / 60.0)).toInt().toString()
            tvMaxSwimFrequency.text = sportModleInfo?.reportMaxSwimFrequency.toString()
            tvTotalSwimNum.text = sportModleInfo?.reportTotalSwimNum.toString()
            tvAvgSwolf.text = sportModleInfo?.reportAvgSwolf.toString()
            tvOptimalSwolf.text = sportModleInfo?.reportOptimalSwolf.toString()

            mPaceCurveChartView.setType(1)
            mHeartCurveChartView.setType(2)
            mHeightCurveChartView.setType(3)

            var sportDataString = sportModleInfo?.recordPointSportData
            val sportData = sportDataString?.split("-")

            if (sportType == 200 || sportType == 201) {
                var deviceSportList = ArrayList<DeviceSportSwimEntity>()
                if (sportData != null) {
                    deviceSportList = DeviceSportManager.instance.parsingFitnessNew(sportType, sportData)
                }
                tvTitleValue14.text = deviceSportList.size.toString()

                var xTime = sportModleInfo!!.reportDuration / 60.0 / 16.0
                if (deviceSportList.size > 0) {
                    xTime = (deviceSportList[deviceSportList.size - 1].endTime - deviceSportList[0].startTime) / 1000 / 60.0 / 16.0
                }

                val xData = ArrayList<Double>()
                for (i in 0 until 16) {
                    xData.add((xTime * (i + 1)))
                }

                mSwimFrequencyChartView.setParameter(deviceSportList, xData, 1)
                mSwimSwolfChartView.setParameter(deviceSportList, xData, 2)

                return
            }

            var deviceSportList = ArrayList<DeviceSportEntity>()
            if (sportData != null) {
                deviceSportList = DeviceSportManager.instance.parsingFitness(sportType, sportData)
            }

            if (deviceSportList.size > 16) {
                val oneGroup = deviceSportList.size / 16
                val lastData = deviceSportList.size % 16
                if (lastData * 1f / oneGroup < 0.1) {
                    var xTime = oneGroup * 1f / 60
                    val xData = ArrayList<Double>()
                    for (i in 0 until 16) {
                        xData.add((xTime * (i + 1)).toDouble())
                    }

                    val yHeartData = ArrayList<Double>()
                    val yPaceData = ArrayList<Double>()
                    val yStepSpeedData = ArrayList<Double>()
                    val ySpeedData = ArrayList<Double>()
                    val yCalData = ArrayList<Double>()
                    val yHeightData = ArrayList<Double>()

                    for (i in 0..15) {
                        var heart: Double = 0.0
                        var distance: Double = 0.0
                        var height: Double = 0.0
                        var step: Int = 0
                        var cal: Double = 0.0

                        var heartIndex: Int = 0

                        for (j in i * oneGroup until (i + 1) * oneGroup) {
                            var deviceSportEntity = deviceSportList[j];
                            heart += deviceSportEntity.heart
                            distance += deviceSportEntity.distance
                            height += deviceSportEntity.height
                            step += deviceSportEntity.step
                            cal += deviceSportEntity.cal

                            if (deviceSportEntity.heart != 0) {
                                heartIndex++
                            }
                        }

                        if (heartIndex == 0) {
                            heartIndex = 1
                        }

                        yHeartData.add(heart / heartIndex)
                        if (distance == 0.0) {
                            yPaceData.add(0.0)
                        } else {
                            var yMinute = oneGroup * 1000 / (60.0 * distance)
                            if (SysUtils.isShow00Pace((yMinute * 60).toInt())) {
                                yPaceData.add(0.0)
                            } else {
                                if (unitType == 1) {
                                    yPaceData.add(oneGroup * 1000 / (60.0 * distance))
                                } else {
                                    yPaceData.add(oneGroup * 1000 / (60.0 * distance / 1.61f))
                                }
                            }
                        }

                        if (unitType == 1) {
                            ySpeedData.add(distance * 3600 / (oneGroup * 1000))
                            yHeightData.add(height)
                        } else {
                            ySpeedData.add(distance * 3600 / (oneGroup * 1000 * 1.61f))
                            yHeightData.add(height * 3.28f)
                        }

                        yStepSpeedData.add(step * 60.0 / oneGroup)
                        yCalData.add(cal)
                    }
                    mHeartCurveChartView.setParameter(xData, yHeartData)
                    mPaceCurveChartView.setParameter(xData, yPaceData)
                    mStepSpeedCurveChartView.setParameter(xData, yStepSpeedData)
                    mSpeedCurveChartView.setParameter(xData, ySpeedData)
                    mCalCurveChartView.setParameter(xData, yCalData)
                    mHeightCurveChartView.setParameter(xData, yHeightData)

                } else {
                    // 17 段数据
                    var xTime = oneGroup * 1f / 60
                    val xData = ArrayList<Double>()
                    for (i in 0 until 16) {
                        xData.add((xTime * (i + 1)).toDouble())
                    }
                    xData.add(lastData * 1.0 / 60 + xTime * 16)

                    val yHeartData = ArrayList<Double>()
                    val yPaceData = ArrayList<Double>()
                    val yStepSpeedData = ArrayList<Double>()
                    val ySpeedData = ArrayList<Double>()
                    val yCalData = ArrayList<Double>()
                    val yHeightData = ArrayList<Double>()

                    for (i in 0..15) {
                        var heart: Double = 0.0
                        var distance: Double = 0.0
                        var height: Double = 0.0
                        var step: Int = 0
                        var cal: Double = 0.0

                        var heartIndex: Int = 0

                        for (j in i * oneGroup until (i + 1) * oneGroup) {
                            var deviceSportEntity = deviceSportList[j];
                            heart += deviceSportEntity.heart
                            distance += deviceSportEntity.distance
                            height += deviceSportEntity.height
                            step += deviceSportEntity.step
                            cal += deviceSportEntity.cal

                            if (deviceSportEntity.heart != 0) {
                                heartIndex++
                            }
                        }

                        if (heartIndex == 0) {
                            heartIndex = 1
                        }

                        yHeartData.add(heart / heartIndex)
                        if (distance == 0.0) {
                            yPaceData.add(0.0)
                        } else {
                            val yMinute = oneGroup * 1000 / (60.0 * distance)
                            if (SysUtils.isShow00Pace((yMinute * 60).toInt())) {
                                yPaceData.add(0.0)
                            } else {
                                if (unitType == 1) {
                                    yPaceData.add(oneGroup * 1000 / (60.0 * distance))
                                } else {
                                    yPaceData.add(oneGroup * 1000 / (60.0 * distance / 1.61f))
                                }
                            }
                        }

                        if (unitType == 1) {
                            ySpeedData.add(distance * 3600 / (oneGroup * 1000))
                            yHeightData.add(height)
                        } else {
                            ySpeedData.add(distance * 3600 / (oneGroup * 1000 * 1.61f))
                            yHeightData.add(height * 3.28f)
                        }
                        yStepSpeedData.add(step * 60.0 / oneGroup)
                        yCalData.add(cal)
                    }

                    // 第十七段数据
                    var heart: Double = 0.0
                    var distance: Double = 0.0
                    var height: Double = 0.0
                    var step: Int = 0
                    var cal: Double = 0.0

                    var heartIndex: Int = 0

                    for (j in 16 * oneGroup until 16 * oneGroup + lastData) {
                        var deviceSportEntity = deviceSportList[j];
                        heart += deviceSportEntity.heart
                        distance += deviceSportEntity.distance
                        height += deviceSportEntity.height
                        step += deviceSportEntity.step
                        cal += deviceSportEntity.cal

                        if (deviceSportEntity.heart != 0) {
                            heartIndex++
                        }
                    }

                    if (heartIndex == 0) {
                        heartIndex = 1
                    }

                    yHeartData.add(heart / heartIndex)
                    if (distance == 0.0) {
                        yPaceData.add(0.0)
                    } else {
                        val yMinute = lastData * 1000 / (60.0 * distance)
                        if (SysUtils.isShow00Pace((yMinute * 60).toInt())) {
                            yPaceData.add(0.0)
                        } else {
                            if (unitType == 1) {
                                yPaceData.add(lastData * 1000 / (60.0 * distance))
                            } else {
                                yPaceData.add(lastData * 1000 / (60.0 * distance / 1.61f))
                            }
                        }
                    }

                    yStepSpeedData.add(step * 60.0 / lastData)
                    yCalData.add(cal)

                    if (unitType == 1) {
                        ySpeedData.add(distance * 3600 / (lastData * 1000))
                        yHeightData.add(height)
                    } else {
                        ySpeedData.add(distance * 3600 / (lastData * 1000 * 1.61f))
                        yHeightData.add(height * 3.28f)
                    }

                    mHeartCurveChartView.setParameter(xData, yHeartData)
                    mPaceCurveChartView.setParameter(xData, yPaceData)
                    mStepSpeedCurveChartView.setParameter(xData, yStepSpeedData)
                    mSpeedCurveChartView.setParameter(xData, ySpeedData)
                    mCalCurveChartView.setParameter(xData, yCalData)
                    mHeightCurveChartView.setParameter(xData, yHeightData)
                }

            } else {
                val xData = ArrayList<Double>()
                val yData = ArrayList<Double>()
                for (i in 0 until 17) {
                    if (i == 16) {
                        xData.add(0.8f + 16 * 1.3)
                    } else {
                        xData.add(1.3 * (i + 1))
                    }
                }
                for (i in 0 until 17) {
                    if (i == 16) {
                        yData.add(21.0)
                    } else {
                        if (i % 2 == 0) {
                            yData.add(65.0 / 2)
                        } else {
                            yData.add(65.0)
                        }
                    }
                }
                mHeartCurveChartView.setParameter(xData, yData)
                mPaceCurveChartView.setParameter(xData, yData)
                mStepSpeedCurveChartView.setParameter(xData, yData)
                mSpeedCurveChartView.setParameter(xData, yData)
                mCalCurveChartView.setParameter(xData, yData)
                mHeightCurveChartView.setParameter(xData, yData)
            }
        } catch (e: Exception) {
            Log.e("Exception", "Exception = $e")
        }
    }

    fun viewGone() {
        val recordPointDataValid1 = BleTools.toBinary(sportModleInfo?.recordPointDataValid1!!, 8);
        val recordPointDataValid2 = BleTools.toBinary(sportModleInfo?.recordPointDataValid2!!, 8);

        tvTitleMaxSpeed.visibility = View.GONE
        tvTitleMaxSpeedValue.visibility = View.GONE

        when (sportType) {
            1 -> goneType1(recordPointDataValid1, recordPointDataValid2)
            2 -> goneType1(recordPointDataValid1, recordPointDataValid2)
            4 -> goneType1(recordPointDataValid1, recordPointDataValid2)
            5 -> goneType1(recordPointDataValid1, recordPointDataValid2)

            3 -> goneType2(recordPointDataValid1, recordPointDataValid2)

            6 -> goneType3(recordPointDataValid1, recordPointDataValid2)

            7 -> goneType4(recordPointDataValid1, recordPointDataValid2)
            8 -> goneType4(recordPointDataValid1, recordPointDataValid2)
            9 -> goneType4(recordPointDataValid1, recordPointDataValid2)
            10 -> goneType4(recordPointDataValid1, recordPointDataValid2)
            11 -> goneType4(recordPointDataValid1, recordPointDataValid2)
            12 -> goneType4(recordPointDataValid1, recordPointDataValid2)
            27 -> goneType4(recordPointDataValid1, recordPointDataValid2)
            39 -> goneType4(recordPointDataValid1, recordPointDataValid2)

            200 -> goneType5(recordPointDataValid1, recordPointDataValid2)
            201 -> goneType5(recordPointDataValid1, recordPointDataValid2)
        }
    }

    private fun goneType5(recordPointDataValid1: String?, recordPointDataValid2: String?) {
        goneStep()
        goneSpeed()
        goneHeight()
        goneHeart()
        layoutPace.visibility = View.GONE
        layoutCal.visibility = View.GONE

        tvTitle3.visibility = View.VISIBLE
        tvTitleValue3.visibility = View.VISIBLE
    }

    private fun goneType4(recordPointDataValid1: String, recordPointDataValid2: String) {
        goneSwim()
        goneStep()
        goneSpeed()
        gonePace()
        goneHeight()
        if (recordPointDataValid1.substring(0, 1) == "0") {
            goneHeart()
        }
        if (recordPointDataValid1.substring(1, 2) == "0") {
            goneCal()
        }
    }

    private fun goneType3(recordPointDataValid1: String, recordPointDataValid2: String) {
        goneSwim()
        goneStep()
        goneSpeed()
        gonePace()
        if (recordPointDataValid1.substring(0, 1) == "1") {
            if (recordPointDataValid1.substring(1, 2) == "0") {
                goneCal()
            }
        } else {
            goneCal()
        }
        if (recordPointDataValid1.substring(4, 5) == "1") {
            if (recordPointDataValid1.substring(5, 6) == "0") {
                goneHeart()
            }
        } else {
            goneHeart()
        }
        //2
        if (recordPointDataValid2.substring(0, 1) == "1") {
            if (recordPointDataValid2.substring(1, 2) == "0") {
                // 是否整公里
            }
            if (recordPointDataValid2.substring(2, 3) == "0") {
            }
            if (recordPointDataValid2.substring(3, 4) == "0") {
                goneHeight()
            }

        } else {
            goneHeight()
        }

        tvTitle3.visibility = View.VISIBLE
        tvTitleValue3.visibility = View.VISIBLE
        tvTitle5.visibility = View.VISIBLE
        tvTitleValue5.visibility = View.VISIBLE
        tvTitle6.visibility = View.VISIBLE
        tvTitleValue6.visibility = View.VISIBLE
        tvTitleMaxSpeed.visibility = View.VISIBLE
        tvTitleMaxSpeedValue.visibility = View.VISIBLE
    }

    private fun goneType2(recordPointDataValid1: String, recordPointDataValid2: String) {
        goneSwim()
        goneHeight()
        if (recordPointDataValid1.substring(0, 1) == "1") {
            if (recordPointDataValid1.substring(1, 2) == "0") {
                goneCal()
            }
            if (recordPointDataValid1.substring(2, 3) == "0") {
                goneStep()
            }
            if (recordPointDataValid1.substring(3, 4) == "0") {
            }
        } else {
            goneCal()
            goneStep()
        }
        if (recordPointDataValid1.substring(4, 5) == "1") {
            if (recordPointDataValid1.substring(5, 6) == "0") {
                goneHeart()
            }
        } else {
            goneHeart()
        }

        if (recordPointDataValid2.substring(0, 1) == "1") {
            if (recordPointDataValid2.substring(1, 2) == "0") {
                gonePace()
                goneSpeed()
            }
        } else {
            gonePace()
            goneSpeed()
        }

    }

    private fun goneType1(recordPointDataValid1: String, recordPointDataValid2: String) {
        goneSwim()
        if (recordPointDataValid1.substring(0, 1) == "1") {
            if (recordPointDataValid1.substring(1, 2) == "0") {
                goneCal()
            }
            if (recordPointDataValid1.substring(2, 3) == "0") {
                goneStep()
            }
            if (recordPointDataValid1.substring(3, 4) == "0") {
            }
        } else {
            goneCal()
            goneStep()
        }
        if (recordPointDataValid1.substring(4, 5) == "1") {
            if (recordPointDataValid1.substring(5, 6) == "0") {
                goneHeart()
            }
        } else {
            goneHeart()
        }

        // 2
        if (recordPointDataValid2.substring(0, 1) == "1") {
            if (recordPointDataValid2.substring(1, 2) == "0") {
                // 是否整公里
            }
            if (recordPointDataValid2.substring(2, 3) == "0") {
            }
            if (recordPointDataValid2.substring(3, 4) == "0") {
                goneHeight()
            }

        } else {
            goneHeight()
        }
        if (recordPointDataValid2.substring(4, 5) == "1") {
            if (recordPointDataValid2.substring(5, 6) == "0") {
                // 新增距离
                gonePace()
            }
        } else {
            gonePace()
        }
    }

    private fun gonePace() {
        layoutPace.visibility = View.GONE
        tvTitle5.visibility = View.GONE
        tvTitleValue5.visibility = View.GONE
    }

    private fun goneHeight() {
        layoutHeight.visibility = View.GONE
        tvTitle11.visibility = View.GONE
        tvTitleValue11.visibility = View.GONE
        tvTitle12.visibility = View.GONE
        tvTitleValue12.visibility = View.GONE
        tvAvgHeight.visibility = View.GONE
        tvAvgHeightValue.visibility = View.GONE
    }

    private fun goneHeart() {
        layoutHeartParent.visibility = View.GONE
        tvTitle7.visibility = View.GONE
        tvTitleValue7.visibility = View.GONE
        tvTitle8.visibility = View.GONE
        tvTitleValue8.visibility = View.GONE
    }

    private fun goneSpeed() {
        layoutSpeed.visibility = View.GONE
        tvTitle3.visibility = View.GONE
        tvTitleValue3.visibility = View.GONE
        tvTitle6.visibility = View.GONE
        tvTitleValue6.visibility = View.GONE
    }

    private fun goneCal() {
        layoutCal.visibility = View.GONE
        tvTitle4.visibility = View.GONE
        tvTitleValue4.visibility = View.GONE
    }

    private fun goneStep() {
        layoutStepSpeed.visibility = View.GONE
        tvTitle9.visibility = View.GONE
        tvTitleValue9.visibility = View.GONE
        tvTitle10.visibility = View.GONE
        tvTitleValue10.visibility = View.GONE
    }

    private fun goneSwim() {
        tvTitle13.visibility = View.GONE
        tvTitleValue13.visibility = View.GONE
        tvTitle14.visibility = View.GONE
        tvTitleValue14.visibility = View.GONE
        layoutSwimFrequency.visibility = View.GONE
        layoutSwimSwolf.visibility = View.GONE
    }

    @OnClick(R.id.layoutMapTitle)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.layoutMapTitle -> {
                if (lat == 0.0 && lon == 0.0) {
                    return
                }
                if (MyUtils.isGoogle(context)) {
                    val xxx = com.google.android.gms.maps.model.LatLng(lat, lon)
                    val markerOptions1 = com.google.android.gms.maps.model.MarkerOptions()
                    markerOptions1.position(xxx)
                    mGoogleMap!!.addMarker(markerOptions1)
                } else {
                    val latlng = LatLng(lat, lon)
                    mAMap.moveCamera(CameraUpdateFactory.changeLatLng(latlng))
                }
            }
        }
    }

    var lat: Double = 0.0
    var lon: Double = 0.0

    private fun loadMap() {
//        sportModleInfo?.reportGpsValid1 = 1
//        sportModleInfo?.map_data = "113.828761,22.653576;113.829565,22.653664;113.829812,22.653314;113.830248,22.652784;113.830248,22.652784;113.829727,22.652017;113.829727,22.652017"

        if (sportModleInfo?.reportGpsValid1 == 0) {
            layoutMap.visibility = View.GONE
        } else {
            if (sportModleInfo?.map_data == null || sportModleInfo?.map_data == "") {
                layoutNoLocus.visibility = View.VISIBLE
                mvAmap.visibility = View.GONE
                layoutGoogleMap.visibility = View.GONE
            } else {
                layoutNoLocus.visibility = View.GONE
                if (MyUtils.isGoogle(context)) {
                    layoutGoogleMap.visibility = View.VISIBLE
                    mvAmap.visibility = View.GONE
                    loadGoogleMap()
                } else {
                    layoutGoogleMap.visibility = View.GONE
                    mvAmap.visibility = View.VISIBLE
                    loadAMap()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mvAmap.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        mvAmap.onDestroy()
        super.onDestroy()
    }

    lateinit var mAMap: AMap
    private fun loadAMap() {

        val mPolyoptions = com.amap.api.maps.model.PolylineOptions()
        mPolyoptions.width(10f)
        @Suppress("DEPRECATION")
        mPolyoptions.color(resources.getColor(R.color.color_278DFD))

        mAMap = mvAmap.map
        mAMap.uiSettings.isRotateGesturesEnabled = false
        mAMap.moveCamera(CameraUpdateFactory.zoomBy(6f))
        mAMap.uiSettings.isZoomControlsEnabled = false
        mAMap.setMapLanguage("en")

        AmapGpsUtils.init()

        var pointData: String? = sportModleInfo?.map_data

        val latLngList = ArrayList<LatLng>()
        if (!TextUtils.isEmpty(pointData)) {

            if (pointData?.substring(pointData.length - 1).equals(";")) {
                pointData = pointData?.substring(0, pointData.length - 2)
            }

            val pointDataArray = pointData!!.split(";").toTypedArray()
            for (i in pointDataArray.indices) {
                val lat = pointDataArray[i].split(",").toTypedArray()[1].toFloat().toDouble()
                val lon = pointDataArray[i].split(",").toTypedArray()[0].toFloat().toDouble()
                val latlon02 = GPSUtil.gps84_To_Gcj02(lat, lon)
                val latlng = LatLng(latlon02[0], latlon02[1])
                latLngList.add(latlng)
            }
        }
        //展示地图
        if (!latLngList.isEmpty()) {
            lat = latLngList[0].latitude
            lon = latLngList[0].longitude
            mAMap.moveCamera(CameraUpdateFactory.changeLatLng(latLngList[0]))
            mAMap.moveCamera(CameraUpdateFactory.zoomTo(16f))
            val mpathSmoothTool = PathSmoothTool()
            mpathSmoothTool.intensity = 4
            val pathoptimizeList = mpathSmoothTool.pathOptimize(latLngList)
            mPolyoptions.addAll(pathoptimizeList)
            if (pathoptimizeList != null && pathoptimizeList.size > 0) {
                mAMap.addPolyline(mPolyoptions)
                mAMap.moveCamera(CameraUpdateFactory.newLatLngBoundsRect(getBounds(pathoptimizeList), ImageUtil.dp2px(this, 50f),  //left padding
                        ImageUtil.dp2px(this, 50f),  //right padding
                        ImageUtil.dp2px(this, 50f),  //top padding
                        ImageUtil.dp2px(this, 130f)))
            }
            mAMap.addMarker(MarkerOptions().position(latLngList[0]).icon(BitmapDescriptorFactory.fromResource(R.drawable.my_sport_start)))
            mAMap.addMarker(MarkerOptions().position(latLngList[latLngList.size - 1]).icon(BitmapDescriptorFactory.fromResource(R.drawable.my_sport_end)))
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        mGoogleMap = p0

        val polylineOptions = PolylineOptions()
        polylineOptions.width(10f).color(Color.parseColor("#7C7DF4"))

        val pointData: String? = sportModleInfo?.map_data
        val latLngList = ArrayList<com.google.android.gms.maps.model.LatLng>()
        if (!TextUtils.isEmpty(pointData)) {
            val pointDataArray = pointData!!.split(";").toTypedArray()
            for (i in pointDataArray.indices) {
                val latlng = com.google.android.gms.maps.model.LatLng(pointDataArray[i].split(",").toTypedArray()[1].toFloat().toDouble(), pointDataArray[i].split(",").toTypedArray()[0].toFloat().toDouble())
                latLngList.add(latlng)
            }
        }
        try {
            for (latLng in latLngList) {
                MyLog.i(TAG, "运动轨迹 = X = " + latLng.latitude + "  Y = " + latLng.longitude)
                polylineOptions.add(latLng)
            }
            lat = latLngList[0].latitude
            lon = latLngList[0].longitude

            val southwest = latLngList[0]
            val northeast = latLngList[latLngList.size - 1]
            val markerOptions1 = com.google.android.gms.maps.model.MarkerOptions()
            markerOptions1.position(southwest)
            markerOptions1.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.my_sport_start))
            mGoogleMap!!.addMarker(markerOptions1)

            val markerOptions2 = com.google.android.gms.maps.model.MarkerOptions()
            markerOptions2.position(northeast)
            markerOptions2.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.my_sport_end))
            mGoogleMap!!.addMarker(markerOptions2)

            if (mGoogleMap != null) {
                mGoogleMap!!.addPolyline(polylineOptions)
                //                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(0), 16.0f));
                val myLatLng = com.google.android.gms.maps.model.LatLng((southwest.latitude + northeast.latitude) / 2, (southwest.longitude + northeast.longitude) / 2)
                mGoogleMap!!.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(myLatLng, 16.0f))
                //                MyLog.i(TAG,"运动轨迹 = southwest X = " + southwest.latitude + "  Y = " + southwest.longitude);
//                MyLog.i(TAG,"运动轨迹 = northeast X = " + northeast.latitude + "  Y = " + northeast.longitude);
//                MyLog.i(TAG,"运动轨迹 = myLatLng X = " + myLatLng.latitude + "  Y = " + myLatLng.longitude);
                MyLog.i(TAG, "运动轨迹 不等于空")
            } else {
                MyLog.i(TAG, "运动轨迹 等于空了")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var mapFragment: MapFragment? = null
    var mGoogleMap: GoogleMap? = null
    private fun loadGoogleMap() {
        mapFragment = fragmentManager.findFragmentById(R.id.mapGoogle) as MapFragment
        mapFragment!!.getMapAsync(this)
    }

    private fun getBounds(pointlist: List<LatLng>?): LatLngBounds? {
        val b = LatLngBounds.builder()
        if (pointlist == null) {
            return b.build()
        }
        for (i in pointlist.indices) {
            b.include(pointlist[i])
        }
        return b.build()
    }

    private fun isUserM(sportType: Int): Boolean {
        return sportType == 200 || sportType == 201
    }

}
