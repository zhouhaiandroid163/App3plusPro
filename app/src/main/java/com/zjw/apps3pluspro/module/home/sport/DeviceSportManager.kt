package com.zjw.apps3pluspro.module.home.sport

import android.text.TextUtils
import android.util.Log
import com.android.volley.VolleyError
import com.zjw.apps3pluspro.application.BaseApplication
import com.zjw.apps3pluspro.module.home.entity.DeviceSportEntity
import com.zjw.apps3pluspro.network.NewVolleyRequest
import com.zjw.apps3pluspro.network.RequestJson
import com.zjw.apps3pluspro.network.ResultJson
import com.zjw.apps3pluspro.network.VolleyInterface
import com.zjw.apps3pluspro.sql.entity.SportModleInfo
import com.zjw.apps3pluspro.utils.NewTimeUtils
import com.zjw.apps3pluspro.utils.network.AESUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import java.util.zip.GZIPInputStream

class DeviceSportManager private constructor() {
    private val TAG: String = DeviceSportManager::class.java.simpleName
    private val mSportModleInfoUtils = BaseApplication.getSportModleInfoUtils()

    companion object {
        val instance: DeviceSportManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DeviceSportManager()
        }
    }

    fun parsingFitness(sportType: Int, sportData: List<String>): ArrayList<DeviceSportEntity> {
        val deviceSportList = ArrayList<DeviceSportEntity>()
        if (sportType == 1 || sportType == 2 || sportType == 4 || sportType == 5) {
            var i = 0
            while (i < sportData.size) {
                val altitude = (sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i]).toLong(16)
                i += 4
                val dataNumber = (sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i]).toLong(16)
                i += 4
                val time = (sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i]).toLong(16)
                i += 4
                for (j in 0 until dataNumber) {
                    val byte1 = sportData[i].toInt(16)
                    val byte2 = sportData[i + 1].toInt(16)
                    val byte3 = sportData[i + 2].toInt(16)
                    val byte4 = sportData[i + 3].toInt(16)
                    i += 4
                    val cal = byte1 shr 4
                    val step = byte1 and 0x0f
                    val heart = byte2
                    val isFullKilometer = byte3 shr 7 == 1
                    val heightType = byte3 shr 6 and 0x01 //0=下降，1=上升
                    val height = (byte3 and 0x1f) / 10.0 // 实际数据精确到0.1，但是打点存储×10后保存为整数
                    val distance = byte4 / 10.0

                    var deviceSportEntity = DeviceSportEntity()
                    deviceSportEntity.cal = cal
                    deviceSportEntity.step = step
                    deviceSportEntity.heart = heart
                    deviceSportEntity.distance = distance.toDouble()
                    if (heightType == 0) {
                        deviceSportEntity.height = -height
                    } else {
                        deviceSportEntity.height = height
                    }
                    deviceSportList.add(deviceSportEntity)
                }
            }
        } else if (sportType == 3) {
            // At last 4 byte is check crc32
            var i = 0
            while (i < sportData.size) {
                val dataNumber = (sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i]).toLong(16)
                i += 4
                val time = (sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i]).toLong(16)
                i += 4
                for (j in 0 until dataNumber) {
                    val byte1 = sportData[i].toInt(16)
                    val byte2 = sportData[i + 1].toInt(16)
                    val byte3 = sportData[i + 2].toInt(16)
                    i += 3
                    val cal = byte1 shr 4
                    val step = byte1 and 0x0f
                    val heart = byte2
                    val distance = byte3 / 10.0

                    var deviceSportEntity = DeviceSportEntity()
                    deviceSportEntity.cal = cal
                    deviceSportEntity.step = step
                    deviceSportEntity.heart = heart
                    deviceSportEntity.distance = distance.toDouble()
                    deviceSportList.add(deviceSportEntity)
                }
            }
        } else if (sportType == 6) {
            var i = 0
            while (i < sportData.size) {
                val altitude = (sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i]).toLong(16)
                i += 4
                val dataNumber = (sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i]).toLong(16)
                i += 4
                val time = (sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i]).toLong(16)
                i += 4
                for (j in 0 until dataNumber) {
                    val byte1 = sportData[i].toInt(16)
                    val byte2 = sportData[i + 1].toInt(16)
                    val byte3 = sportData[i + 2].toInt(16)
                    i += 3
                    val cal = byte1
                    val heart = byte2
                    val isFullKilometer = byte3 shr 7 == 1
                    val heightType = byte3 shr 6 and 0x01 //0=下降，1=上升
                    val height = (byte3 and 0x1f) / 10.0 // 实际数据精确到0.1，但是打点存储×10后保存为整数

                    var deviceSportEntity = DeviceSportEntity()
                    deviceSportEntity.cal = cal
                    deviceSportEntity.heart = heart
                    if (heightType == 0) {
                        deviceSportEntity.height = -height
                    } else {
                        deviceSportEntity.height = height
                    }
                    deviceSportList.add(deviceSportEntity)
                }
            }
        } else if (sportType == 7 || sportType == 8 || sportType == 9 || sportType == 10 || sportType == 11 || sportType == 12) {
            // At last 4 byte is check crc32
            var i = 0
            while (i < sportData.size) {
                val dataNumber = (sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i]).toLong(16)
                i += 4
                val time = (sportData[i + 3] + sportData[i + 2] + sportData[i + 1] + sportData[i]).toLong(16)
                i += 4
                for (j in 0 until dataNumber) {
                    val byte1 = sportData[i].toInt(16)
                    val byte2 = sportData[i + 1].toInt(16)
                    i += 2
                    val heart = byte1
                    val cal = byte2

                    var deviceSportEntity = DeviceSportEntity()
                    deviceSportEntity.cal = cal
                    deviceSportEntity.heart = heart
                    deviceSportList.add(deviceSportEntity)
                }
            }
        }
        return deviceSportList
    }

    fun uploadMoreSportData() {
        Thread(Runnable {
            val noUploadData0 = mSportModleInfoUtils.queryNoUploadData(0)
            val noUploadData1 = mSportModleInfoUtils.queryNoUploadData(1)

            Log.i(TAG, "uploadMoreSportData=${noUploadData0.size}")
            Log.i(TAG, "uploadMoreSportData=${noUploadData1.size}")

            if (noUploadData0.size > 0 && isUploadGps == "0") {
                for (i in 0 until noUploadData0.size) {
                    val mSportModleInfo = noUploadData0[i]
                    if (mSportModleInfo.recordPointIdTime == 0L) {
                        mSportModleInfo.recordPointIdTime = NewTimeUtils.getLongTime(mSportModleInfo.getTime(), NewTimeUtils.TIME_YYYY_MM_DD_HHMMSS)
                        val timeZone = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (3600 * 1000)
                        mSportModleInfo.recordPointTimeZone = timeZone
                    }
                }
                val mRequestInfo = RequestJson.postGpsSport(noUploadData0)
                NewVolleyRequest.RequestPost(mRequestInfo, TAG, object : VolleyInterface(BaseApplication.getmContext(), mListener, mErrorListener) {
                    override fun onMySuccess(result: JSONObject) {
                        try {
                            Log.i(TAG, "postGpsSport=$result")
                            val resultString = result.optString("code")
                            if (resultString.equals(ResultJson.Code_operation_success, ignoreCase = true)) {
                                mSportModleInfoUtils.updateNoUploadData(noUploadData0)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onMyError(arg0: VolleyError) {}
                })
            }

            if (noUploadData1.size > 0 && isUploadDeviceSport == "0") {
                val mRequestInfo = RequestJson.postRecordPointList(noUploadData1)
                if(mRequestInfo == null){
                    mSportModleInfoUtils.updateNoUploadData(noUploadData1)
                } else{
                    NewVolleyRequest.RequestPost(mRequestInfo, TAG, object : VolleyInterface(BaseApplication.getmContext(), mListener, mErrorListener) {
                        override fun onMySuccess(result: JSONObject) {
                            try {
                                Log.i(TAG, "postRecordPointList=$result")
                                val resultString = result.optString("code")
                                if (resultString.equals(ResultJson.Code_operation_success, ignoreCase = true)) {
                                    mSportModleInfoUtils.updateNoUploadData(noUploadData1)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }

                        override fun onMyError(arg0: VolleyError) {}
                    })
                }
            }
        }).start()
    }

    private val mBleDeviceTools = BaseApplication.getBleDeviceTools()
    fun getMoreSportData(date: String, getDataSuccess: GetDataSuccess) {
        val mRequestInfo = RequestJson.getMoreSportData(date)
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, object : VolleyInterface(BaseApplication.getmContext(), mListener, mErrorListener) {
            override fun onMySuccess(result: JSONObject) {
                mBleDeviceTools.lastRequestServiceTime = System.currentTimeMillis()
                try {
                    Log.i(TAG, "getMoreSportData=$result")
                    val resultString = result.optString("code")
                    if (resultString.equals(ResultJson.Code_operation_success, ignoreCase = true)) {

                        val jsonobject = result.optJSONObject("data")
                        val jsonArray = jsonobject.getJSONArray("list")
                        if (jsonArray.length() > 0) {
                            for (i in 0 until jsonArray.length()) {
                                val jsonobject = jsonArray[i] as JSONObject
                                if (NewTimeUtils.getStringDate(jsonobject.optLong("queryUnixTime"), NewTimeUtils.TIME_YYYY_MM_DD) == date) {
                                    val sportModleInfo = SportModleInfo()
                                    sportModleInfo.user_id = BaseApplication.getUserId()
                                    sportModleInfo.recordPointIdTime = jsonobject.optLong("queryUnixTime")
                                    sportModleInfo.recordPointTimeZone = jsonobject.optString("timeZone").toInt()
                                    sportModleInfo.serviceId = jsonobject.optLong("id")
                                    sportModleInfo.sync_state = "1"
                                    if (jsonobject.optInt("dataType") == 1) {
                                        sportModleInfo.dataSourceType = 0
                                        sportModleInfo.sport_type = jsonobject.optInt("sportType").toString()
                                        sportModleInfo.calorie = jsonobject.optInt("calorie").toString()
                                        sportModleInfo.disance = jsonobject.optInt("distance").toString()
                                        sportModleInfo.total_step = jsonobject.optInt("totalStep").toString()
                                        sportModleInfo.ui_type = jsonobject.optInt("uiType").toString()
                                        sportModleInfo.sport_duration = jsonobject.optInt("sportDuration").toString()
                                        sportModleInfo.time = NewTimeUtils.getStringDate(jsonobject.optLong("queryUnixTime"), NewTimeUtils.TIME_YYYY_MM_DD_HHMMSS);
                                    } else if (jsonobject.optInt("dataType") == 2) {
                                        sportModleInfo.dataSourceType = 1
                                        sportModleInfo.recordPointSportType = jsonobject.optInt("sportType")
                                        sportModleInfo.reportCal = jsonobject.optInt("calorie").toLong()
                                        sportModleInfo.reportTotalStep = jsonobject.optInt("totalStep").toLong()
                                        sportModleInfo.reportDuration = jsonobject.optInt("sportDuration").toLong()
                                        sportModleInfo.reportSportStartTime = jsonobject.optLong("sportBeginUnixTime")
                                        sportModleInfo.reportSportEndTime = jsonobject.optLong("sportEndUnixTime")
                                        sportModleInfo.reportDistance = jsonobject.optLong("distance")
                                    }
                                    val querySportModleInfos = mSportModleInfoUtils.queryByRecordPointIdTime(BaseApplication.getUserId(), sportModleInfo.recordPointIdTime)
                                    if (querySportModleInfos != null && querySportModleInfos.size > 0) {
//                                        sportModleInfo._id = querySportModleInfos[0]._id
//                                        mSportModleInfoUtils.updateData(sportModleInfo)
                                    } else {
                                        mSportModleInfoUtils.insertData(sportModleInfo)
                                    }

                                } else {
                                    Log.i(TAG, "data time is error")
                                }
                            }
                        }
                        getDataSuccess.onSuccess()
                    } else {
                        getDataSuccess.onError()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onMyError(arg0: VolleyError) {
                getDataSuccess.onError()
            }
        })

    }

    interface GetDataSuccess {
        fun onSuccess()
        fun onError()
    }

    @Throws(IOException::class)
    fun unCompress(str: String?): String? {
        if (null == str || str.length <= 0) {
            return str
        }
        // 创建一个新的输出流
        val out = ByteArrayOutputStream()
        // 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲区数组
        val `in` = ByteArrayInputStream(str.toByteArray(charset("ISO-8859-1")))
        // 使用默认缓冲区大小创建新的输入流
        val gzip = GZIPInputStream(`in`)
        val buffer = ByteArray(256)
        var n = 0
        // 将未压缩数据读入字节数组
        while (gzip.read(buffer).also { n = it } >= 0) {
            out.write(buffer, 0, n)
        }
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        return out.toString("utf-8")
    }

    fun getMoreSportDetail(dataType: Int, serviceId: Long, getDataSuccess: GetDataSuccess) {
        val mRequestInfo = RequestJson.getMoreSportDataDetail(dataType, serviceId)
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, object : VolleyInterface(BaseApplication.getmContext(), mListener, mErrorListener) {
            override fun onMySuccess(result: JSONObject) {
                try {
                    Log.i(TAG, "getMoreSportDataDetail=$result")
                    val resultString = result.optString("code")
                    if (resultString.equals(ResultJson.Code_operation_success, ignoreCase = true)) {
                        val jsonobject = result.optJSONObject("data")
                        val sportModleInfos = mSportModleInfoUtils.queryByServerId(serviceId)
                        val sportModleInfo = sportModleInfos[0]
                        if (dataType == 0) { // gps
                            val gpsInfo: JSONObject = jsonobject.getJSONObject("gpsInfo")
                            sportModleInfo.dataSourceType = 0

                            var mapData = gpsInfo.optString("mapData")
                            if (TextUtils.isEmpty(mapData)) {
                                sportModleInfo.map_data = mapData
                            } else {
                                sportModleInfo.map_data = unCompress(AESUtils.decrypt(mapData, "wo.szzhkjyxgs.20"))
                            }

                            sportModleInfo.calorie = gpsInfo.optInt("calorie").toString()
                            sportModleInfo.disance = gpsInfo.optInt("distance").toString()
                            sportModleInfo.total_step = gpsInfo.optInt("totalStep").toString()
                            sportModleInfo.sport_duration = gpsInfo.optInt("sportDuration").toString()
                            sportModleInfo.speed = gpsInfo.optString("speed")
                            sportModleInfo.heart = gpsInfo.optInt("heart").toString()
                            sportModleInfo.sport_type = gpsInfo.optInt("sportType").toString()
                            sportModleInfo.ui_type = gpsInfo.optInt("uiType").toString()
                            sportModleInfo.map_type = gpsInfo.optInt("mapType").toString()
                            sportModleInfo.reportSportStartTime = gpsInfo.optLong("sportBeginUnixTime")
                            sportModleInfo.reportSportEndTime = gpsInfo.optLong("sportEndUnixTime")
                            sportModleInfo.recordPointIdTime = gpsInfo.optLong("queryUnixTime")
                            sportModleInfo.recordPointTimeZone = gpsInfo.optInt("timeZone")
                            sportModleInfo.time = NewTimeUtils.getStringDate(sportModleInfo.recordPointIdTime, NewTimeUtils.TIME_YYYY_MM_DD_HHMMSS)
                        } else if (dataType == 1) { // device sport
                            val recordPointInfo: JSONObject = jsonobject.getJSONObject("recordPointInfo")
                            sportModleInfo.dataSourceType = 1
                            sportModleInfo.recordPointDataId = recordPointInfo.optString("recordPointDataId")
                            sportModleInfo.recordPointVersion = recordPointInfo.optString("recordPointVersion").toInt()
                            sportModleInfo.recordPointTypeDescription = recordPointInfo.optInt("recordPointTypeDescription")
                            sportModleInfo.recordPointSportType = recordPointInfo.optInt("recordPointSportType")
                            sportModleInfo.recordPointEncryption = recordPointInfo.optInt("recordPointEncryption")
                            sportModleInfo.recordPointDataValid1 = recordPointInfo.optInt("recordPointDataValid1")
                            sportModleInfo.recordPointDataValid2 = recordPointInfo.optInt("recordPointDataValid2")

                            var recordPointSportData = recordPointInfo.optString("recordPointSportData")
                            if (TextUtils.isEmpty(recordPointSportData)) {
                                sportModleInfo.recordPointSportData = recordPointSportData
                            } else {
                                sportModleInfo.recordPointSportData = unCompress(AESUtils.decrypt(recordPointSportData, "wo.szzhkjyxgs.20"))
                            }

                            var mapData = recordPointInfo.optString("mapData")
                            if (TextUtils.isEmpty(mapData)) {
                                sportModleInfo.map_data = mapData
                            } else {
                                sportModleInfo.map_data = unCompress(AESUtils.decrypt(mapData, "wo.szzhkjyxgs.20"))
                            }

                            sportModleInfo.reportEncryption = recordPointInfo.optInt("reportEncryption")
                            sportModleInfo.reportDataValid1 = recordPointInfo.optInt("reportDataValid1")
                            sportModleInfo.reportDataValid2 = recordPointInfo.optInt("reportDataValid2")
                            sportModleInfo.reportDataValid3 = recordPointInfo.optInt("reportDataValid3")
                            sportModleInfo.reportDataValid4 = recordPointInfo.optInt("reportDataValid4")
                            sportModleInfo.reportSportStartTime = recordPointInfo.optLong("reportSportStartTime")
                            sportModleInfo.reportSportEndTime = recordPointInfo.optLong("reportSportEndTime")
                            sportModleInfo.reportDuration = recordPointInfo.optLong("reportDuration")
                            sportModleInfo.reportDistance = recordPointInfo.optLong("reportDistance")
                            sportModleInfo.reportCal = recordPointInfo.optLong("reportCal")
                            sportModleInfo.reportFastPace = recordPointInfo.optLong("reportFastPace")
                            sportModleInfo.reportSlowestPace = recordPointInfo.optLong("reportSlowestPace")
                            sportModleInfo.reportFastSpeed = recordPointInfo.optDouble("reportFastSpeed").toFloat()
                            sportModleInfo.reportTotalStep = recordPointInfo.optInt("reportTotalStep").toLong()
                            sportModleInfo.reportMaxStepSpeed = recordPointInfo.optInt("reportMaxStepSpeed")
                            sportModleInfo.reportAvgHeart = recordPointInfo.optInt("reportAvgHeart")
                            sportModleInfo.reportMaxHeart = recordPointInfo.optInt("reportMaxHeart")
                            sportModleInfo.reportMinHeart = recordPointInfo.optInt("reportMinHeart")
                            sportModleInfo.reportCumulativeRise = recordPointInfo.optDouble("reportCumulativeRise").toFloat()
                            sportModleInfo.reportCumulativeDecline = recordPointInfo.optDouble("reportCumulativeDecline").toFloat()
                            sportModleInfo.reportAvgHeight = recordPointInfo.optDouble("reportAvgHeight").toFloat()
                            sportModleInfo.reportMaxHeight = recordPointInfo.optDouble("reportMaxHeight").toFloat()
                            sportModleInfo.reportMinHeight = recordPointInfo.optDouble("reportMinHeight").toFloat()
                            sportModleInfo.reportTrainingEffect = recordPointInfo.optDouble("reportTrainingEffect").toFloat()
                            sportModleInfo.reportMaxOxygenIntake = recordPointInfo.optInt("reportMaxOxygenIntake")
                            sportModleInfo.reportEnergyConsumption = recordPointInfo.optInt("reportEnergyConsumption")
                            sportModleInfo.reportRecoveryTime = recordPointInfo.optLong("reportRecoveryTime")
                            sportModleInfo.reportHeartLimitTime = recordPointInfo.optLong("reportHeartLimitTime")
                            sportModleInfo.reportHeartAnaerobic = recordPointInfo.optLong("reportHeartAnaerobic")
                            sportModleInfo.reportHeartAerobic = recordPointInfo.optLong("reportHeartAerobic")
                            sportModleInfo.reportHeartFatBurning = recordPointInfo.optLong("reportHeartFatBurning")
                            sportModleInfo.reportHeartWarmUp = recordPointInfo.optLong("reportHeartWarmUp")
                            sportModleInfo.recordPointIdTime = recordPointInfo.optLong("queryUnixTime")
                            sportModleInfo.recordPointTimeZone = recordPointInfo.optInt("timeZone")
                            sportModleInfo.reportGpsValid1 = recordPointInfo.optInt("gpsDataValid1")
                            sportModleInfo.reportGpsEncryption = recordPointInfo.optInt("gpsEncryption")
//                            sportModleInfo.recordGpsTime = recordPointInfo.optString("gpsUnixDatas")
                        }
                        mSportModleInfoUtils.updateData(sportModleInfo)

                        getDataSuccess.onSuccess()
                    } else {
                        getDataSuccess.onError()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onMyError(arg0: VolleyError) {
                getDataSuccess.onError()
            }
        })
    }

    var isUploadGps: String = "1"
    var isUploadDeviceSport: String = "1"
    fun getIsUploadMoreSport(type: Int) {
        val mRequestInfo = RequestJson.getIsUploadMoreSport(type)
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, object : VolleyInterface(BaseApplication.getmContext(), mListener, mErrorListener) {
            override fun onMySuccess(result: JSONObject) {
                Log.i(TAG, "getIsUploadMoreSport=$result")
                val resultString = result.optString("code")
                if (resultString.equals(ResultJson.Code_operation_success, ignoreCase = true)) {
                    val jsonobject = result.optJSONObject("data")
                    if (type == 1) {
                        isUploadGps = jsonobject.optString("value")
                    } else {
                        isUploadDeviceSport = jsonobject.optString("value")
                    }
                }
            }

            override fun onMyError(arg0: VolleyError) {
            }
        })
    }

}



