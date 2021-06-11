package com.zjw.apps3pluspro.module.device.weather

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import butterknife.OnClick
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.application.BaseApplication
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.bleservice.BleConstant
import com.zjw.apps3pluspro.bleservice.BleService
import com.zjw.apps3pluspro.bleservice.BleTools
import com.zjw.apps3pluspro.bleservice.BtSerializeation
import com.zjw.apps3pluspro.eventbus.BlueToothStateEvent
import com.zjw.apps3pluspro.eventbus.SendOpenWeatherDataEvent
import com.zjw.apps3pluspro.eventbus.tools.EventTools
import com.zjw.apps3pluspro.module.device.weather.openweather.WeatherManager
import com.zjw.apps3pluspro.utils.*
import com.zjw.apps3pluspro.utils.DialogUtils.DialogClickListener
import kotlinx.android.synthetic.main.weather_main_activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class WeatherMainActivity : BaseActivity() {
    private val TAG = WeatherMainActivity::class.java.simpleName
    private val mBleDeviceTools = BaseApplication.getBleDeviceTools()
    override fun setLayoutId(): Int {
        return R.layout.weather_main_activity
    }

    override fun initViews() {
        super.initViews()
        EventTools.SafeRegisterEventBus(this)
        setTvTitle(R.string.weather_title)
        handler = Handler()
    }

    @OnClick(R.id.tvHelp, R.id.switchCompat, R.id.layoutCity)
    fun viewOnClick(view: View) {
        when (view.id) {
            R.id.tvHelp -> {
                startActivity(Intent(this, WeatherHelpActivity::class.java))
            }
            R.id.switchCompat -> {
                initSwitch()
            }
            R.id.layoutCity -> {
                startActivityForResult(Intent(this, WeatherCitySearchActivity::class.java), 0x01)
            }
        }
    }

    override fun onDestroy() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
        EventTools.SafeUnregisterEventBus(this)
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0x01 && resultCode == Activity.RESULT_OK) {

            if (mBleDeviceTools.weatherSwitch) {
                showDialog()
                initTvLatLon()

                if (mBleDeviceTools.weatherMode == 3) {
                    getOpenWeather(false)
                } else {
                    startRequestWeather()
                }
            }
        }
    }

    override fun initDatas() {
        super.initDatas()
        switchCompat.isChecked = mBleDeviceTools.weatherSwitch
        initTvLatLon()
    }

    private fun initTvLatLon() {
        if (switchCompat.isChecked) {
            layoutCity.visibility = View.VISIBLE
        } else {
            layoutCity.visibility = View.GONE
        }
        if (mBleDeviceTools.weatherCity.isNotEmpty()) {
//            tvLatLon.visibility = View.VISIBLE
            tvLatLon.text = mBleDeviceTools.weatherGps

            tvCityName.text = mBleDeviceTools.weatherCity
            tvCityName.visibility = View.VISIBLE

        } else {
//            tvLatLon.visibility = View.GONE
            tvCityName.text = resources.getText(R.string.no_data_default)
        }
    }

    var getLocationTimeOut = Runnable {
        GpsSportManager.getInstance().stopGps(this)
        msg?.text = resources.getString(R.string.weather_location_error)
        SysUtils.logAppRunning(TAG, "weather_location_error")

        val handler = Handler()
        handler.postDelayed({
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        }, 1500)
    }
    private var handler: Handler = Handler()

    @SuppressLint("ShowToast")
    private fun initSwitch() {
        if (switchCompat.isChecked) {
            SysUtils.logAmapGpsE(TAG, "switchCompat is checked")
            SysUtils.logAppRunning(TAG, "switchCompat is checked")

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                switchCompat.isChecked = false
                AuthorityManagement.verifyLocation(this)
                return
            }
            if (!MyUtils.isGPSOpen(context)) {
                switchCompat.isChecked = false
                DialogUtils.showSettingGps(this)
                return
            }

            layoutCity.visibility = View.VISIBLE
//            tvLatLon.visibility = View.VISIBLE
            showDialog()
            mBleDeviceTools.weatherSwitch = true
            mBleDeviceTools.weatherCity = ""
            mBleDeviceTools.weatherGps = ""
            initTvLatLon()

            if (mBleDeviceTools.weatherMode == 3) {
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(getLocationTimeOut, 15 * 1000)
                SysUtils.logAmapGpsE(TAG, "getLatLon")
                SysUtils.logAppRunning(TAG, "getLatLon")
                GpsSportManager.getInstance().getLatLon(this) { gpsInfo: GpsSportManager.GpsInfo ->
                    SysUtils.logAmapGpsE(TAG, "getLatLon success and stopGps getWeatherCity")
                    SysUtils.logAppRunning(TAG, "getLatLon success and stopGps getWeatherCity")
                    GpsSportManager.getInstance().stopGps(this)

                    getOpenWeather(true)
                }
            } else {
                if (mBleDeviceTools.weatherCity.isNotEmpty()) {
                    initTvLatLon()
                    startRequestWeather()
                } else {
//                mBleDeviceTools.weatherGps = "113.83315192897955,22.631363436442037"
//                GpsSportManager.getInstance().getWeatherCity(this) {
//                    initTvLatLon()
//                    startRequestWeather()
//                }
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed(getLocationTimeOut, 10 * 1000)
                    SysUtils.logAmapGpsE(TAG, "getLatLon")
                    SysUtils.logAppRunning(TAG, "getLatLon")
                    GpsSportManager.getInstance().getLatLon(this) { gpsInfo: GpsSportManager.GpsInfo ->
                        SysUtils.logAmapGpsE(TAG, "getLatLon success and stopGps getWeatherCity")
                        SysUtils.logAppRunning(TAG, "getLatLon success and stopGps getWeatherCity")
                        GpsSportManager.getInstance().stopGps(this)

                        GpsSportManager.getInstance().getWeatherCity(this) {
                            SysUtils.logAmapGpsE(TAG, "getWeatherCity success and startRequestWeather")
                            handler.removeCallbacksAndMessages(null)
                            initTvLatLon()
                            startRequestWeather()
                        }
                    }
                }
            }
        } else {
            mBleDeviceTools.weatherSwitch = false
            layoutCity.visibility = View.GONE
//            tvLatLon.visibility = View.GONE
        }
    }

    private fun startRequestWeather() {
        GpsSportManager.getInstance().getWeatherArea(this) {

            val myWeatherModle = WeatherBean.getHisData(this@WeatherMainActivity)
            if (myWeatherModle != null) {
                println("请求天气 = 历史 解析2 $myWeatherModle")

                val t2: ByteArray? = WeatherBean.getWaeatherListData(myWeatherModle)
                System.out.println("请求天气  t2 = " + BleTools.printHexString(t2))

                var atmosphericPressure = 0f
                try {
                    atmosphericPressure = myWeatherModle[0].getPressure().toFloat()
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
                val t3: ByteArray? = BtSerializeation.setWeather(atmosphericPressure, t2)
                System.out.println("请求天气  t3 = " + BleTools.printHexString(t3))

                sendData(t3)
            }

            sendSuccess()
        }
    }

    private fun sendSuccess() {
        msg?.text = resources.getString(R.string.weather_send_over)
        handler.removeCallbacksAndMessages(null)

        val handler = Handler()
        handler.postDelayed({
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        }, 2000)
    }

    private var progressDialog: Dialog? = null
    var msg: TextView? = null
    var ivSyncWhite: ImageView? = null
    private fun showDialog() {
        progressDialog = Dialog(this@WeatherMainActivity, R.style.progress_dialog)
        progressDialog!!.setContentView(R.layout.connect_layout)
        progressDialog!!.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        msg = progressDialog!!.findViewById(R.id.tvLoading) as TextView
        ivSyncWhite = progressDialog!!.findViewById(R.id.ivSyncWhite)
        val rotate = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        val lin = LinearInterpolator()
        rotate.interpolator = lin
        rotate.duration = 2000 // 设置动画持续周期
        rotate.repeatCount = -1 // 设置重复次数
        rotate.fillAfter = true // 动画执行完后是否停留在执行完的状态
        rotate.startOffset = 10 // 执行前的等待时间
        ivSyncWhite?.animation = rotate
        msg!!.setText(resources.getString(R.string.weather_positioning))
        progressDialog!!.show()
        progressDialog!!.setOnDismissListener({ })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun blueToothStateEvent(event: BlueToothStateEvent) {
        when (event.state) {
            BleConstant.STATE_CONNECTING -> {
            }
            BleConstant.STATE_DISCONNECTED -> {
                AppUtils.showToast(context, R.string.no_connection_notification)
                finish()
            }
            BleConstant.STATE_CONNECTED_TIMEOUT -> {
            }
            BleConstant.STATE_CONNECTED -> {
            }
            else -> {
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            AuthorityManagement.REQUEST_EXTERNAL_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    showSettingDialog(getString(R.string.setting_dialog_location))
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun showSettingDialog(title: String?) {
        DialogUtils.BaseDialog(context,
                context.resources.getString(R.string.dialog_prompt),
                title,
                context.getDrawable(R.drawable.black_corner_bg),
                object : DialogClickListener {
                    override fun OnOK() {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", context.getPackageName(), null)
                        intent.data = uri
                        startActivity(intent)
                    }

                    override fun OnCancel() {}
                }, getString(R.string.setting_dialog_setting))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun sendOpenWeatherDataEvent(event: SendOpenWeatherDataEvent?) {
        if (event!!.status == 1) {
            sendSuccess()
        }
    }

    private fun getOpenWeather(isSaveName: Boolean) {
        val gps = mBleDeviceTools.weatherGps.split(",")
        if (gps.size > 1) {
            WeatherManager.getInstance().getCurrentWeather(false, isSaveName, gps[1].toDouble(), gps[0].toDouble(), object : WeatherManager.GetOpenWeatherListener {
                override fun onSuccess() {
                    EventBus.getDefault().post(SendOpenWeatherDataEvent(0))
                    initTvLatLon()
                }

                override fun onFail() {
                }
            })
        } else {
            SysUtils.logAppRunning(TAG, "gps.size is error = " + mBleDeviceTools.weatherGps)
        }
    }
}
