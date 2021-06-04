package com.zjw.apps3pluspro.module.device.weather

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import butterknife.OnClick
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.application.BaseApplication
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.bleservice.BleTools
import com.zjw.apps3pluspro.eventbus.SendOpenWeatherDataEvent
import com.zjw.apps3pluspro.module.device.weather.openweather.WeatherFind
import com.zjw.apps3pluspro.module.device.weather.openweather.WeatherManager
import com.zjw.apps3pluspro.utils.*
import kotlinx.android.synthetic.main.weather_city_search_activity.*
import org.greenrobot.eventbus.EventBus
import java.util.*


class WeatherCitySearchActivity : BaseActivity() {
    override fun setLayoutId(): Int {
        return R.layout.weather_city_search_activity
    }

    override fun initViews() {
        super.initViews()
        setTvTitle(R.string.weather_city)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    @OnClick(R.id.layoutResetLocation, R.id.tvCityName, R.id.layoutSearch)
    fun viewOnClick(view: View) {
        when (view.id) {
            R.id.layoutResetLocation -> {
                if (!isStartLocation) {
                    startLocation()
                }
            }
            R.id.tvCityName -> {
                if (isLocationSuccess) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
            R.id.layoutSearch -> {
                requestCityList(etSearchCity.text.toString())
            }
        }
    }

    var getLocationTimeOut = Runnable {
        GpsSportManager.getInstance().stopGps(this)
        tvCityName.text = resources.getString(R.string.weather_location_error)
    }
    private var handler: Handler = Handler()
    private var isStartLocation: Boolean = false
    private var isLocationSuccess: Boolean = false
    private fun startLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AuthorityManagement.verifyLocation(this)
            return
        }
        if (!MyUtils.isGPSOpen(context)) {
            DialogUtils.showSettingGps(this)
            return
        }

        handler.removeCallbacksAndMessages(null)
        if (mBleDeviceTools.weatherMode == 3) {
            handler.postDelayed(getLocationTimeOut, 15 * 1000)
        } else {
            handler.postDelayed(getLocationTimeOut, 10 * 1000)
        }

        isStartLocation = true
        isLocationSuccess = false
        tvCityName.text = resources.getString(R.string.weather_location_ing)
        GpsSportManager.getInstance().getLatLon(this) { gpsInfo: GpsSportManager.GpsInfo ->
            GpsSportManager.getInstance().stopGps(this)

            if (mBleDeviceTools.weatherMode == 3) {
                val gps = mBleDeviceTools.weatherGps.split(",")
                if (gps.size > 1) {
                    WeatherManager.getInstance().getCurrentWeather(true, true, gps[1].toDouble(), gps[0].toDouble(), object : WeatherManager.GetOpenWeatherListener {
                        override fun onSuccess() {
                            handler.removeCallbacksAndMessages(null)
                            isStartLocation = false
                            isLocationSuccess = true
                            tvCityName.text = mBleDeviceTools.weatherCity
                        }

                        override fun onFail() {
                        }
                    })
                }

            } else {
                GpsSportManager.getInstance().getWeatherCity(this) {
                    handler.removeCallbacksAndMessages(null)
                    isStartLocation = false
                    isLocationSuccess = true
                    tvCityName.text = mBleDeviceTools.weatherCity
                }
            }
        }
    }

    override fun initDatas() {
        super.initDatas()
        mLayoutInflater = LayoutInflater.from(this)
        handler = Handler()

        etSearchCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                requestCityList(s.toString())
                if (TextUtils.isEmpty(s.toString())) {
                    layoutSearch.visibility = View.GONE
                } else {
                    layoutSearch.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        etSearchCity.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { // 先隐藏键盘
                (etSearchCity.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(this@WeatherCitySearchActivity.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                requestCityList(etSearchCity.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
    }

    private var mLayoutInflater: LayoutInflater? = null
    private fun requestCityList(city: String) {
        if (mBleDeviceTools.weatherMode == 3) {
            WeatherManager.getInstance().getWeatherFindBySearch(city, object : WeatherManager.GetOpenWeatherListener {
                override fun onSuccess() {
                    layoutParent.removeAllViews()
                    if (WeatherManager.getInstance().weatherFind.list == null) {
                        Toast.makeText(context, resources.getText(R.string.no_data), Toast.LENGTH_SHORT).show()
                    }

                    for (i in 0 until WeatherManager.getInstance().weatherFind.list.size) {
                        val mLinearLayout = mLayoutInflater?.inflate(R.layout.city_open_weather_item_layout, null) as LinearLayout
                        initOpenWeatherData(mLinearLayout, i, WeatherManager.getInstance().weatherFind.list[i])
                        layoutParent.addView(mLinearLayout)
                    }
                }

                override fun onFail() {
                    Toast.makeText(context, resources.getText(R.string.net_worse_try_again), Toast.LENGTH_SHORT).show()
                }
            })

        } else {
            GpsSportManager.getInstance().getWeatherCityBySearch(this, object : GpsSportManager.onWeatherCitySearchListener {
                override fun onError() {
                    Toast.makeText(context, resources.getText(R.string.net_worse_try_again), Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(list: ArrayList<WeatherCityEntity>?) {
                    layoutParent.removeAllViews()
                    if (list == null) {
                        Toast.makeText(context, resources.getText(R.string.no_data), Toast.LENGTH_SHORT).show()
                    } else {
                        for (i in 0 until list.size) {
                            val mLinearLayout = mLayoutInflater?.inflate(R.layout.city_item_layout, null) as LinearLayout
                            findViewById(mLinearLayout, i, list[i])
                            layoutParent.addView(mLinearLayout)
                        }
                    }
                }
            }, city)
        }
    }

    private val mBleDeviceTools = BaseApplication.getBleDeviceTools()
    private fun findViewById(mLinearLayout: LinearLayout, i: Int, entity: WeatherCityEntity) {
        val tvItemName = mLinearLayout.findViewById<TextView>(R.id.tvCityName)
        val layoutCity = mLinearLayout.findViewById<LinearLayout>(R.id.layoutCity)
        tvItemName.text = entity.name
        layoutCity.setOnClickListener { v: View? ->
            mBleDeviceTools.weatherCity = entity.name
            mBleDeviceTools.weatherCityID = entity.id
            setResult(Activity.RESULT_OK)
            finish()
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
                object : DialogUtils.DialogClickListener {
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

    private fun initOpenWeatherData(mLinearLayout: LinearLayout, i: Int, weatherFindItem: WeatherFind.WeatherFindItem?) {
        val tvName = mLinearLayout.findViewById<TextView>(R.id.tvName)
        val tvMain = mLinearLayout.findViewById<TextView>(R.id.tvMain)
        val tvTemp = mLinearLayout.findViewById<TextView>(R.id.tvTemp)
        val tvTempMaxMin = mLinearLayout.findViewById<TextView>(R.id.tvTempMaxMin)
        val layoutCity = mLinearLayout.findViewById<LinearLayout>(R.id.layoutCity)

        tvName.text = weatherFindItem?.name + " " + weatherFindItem?.sys!!.country
        tvMain.text = weatherFindItem.weather[0].main

        if (mBleDeviceTools.temperatureType == 1) {
            tvTemp.text = BleTools.getFahrenheit(weatherFindItem.main.temp.toFloat().toInt()).toString() + "℉"
            tvTempMaxMin.text = BleTools.getFahrenheit(weatherFindItem.main.temp_max.toFloat().toInt()).toString() + "℉" + "/" + BleTools.getFahrenheit(weatherFindItem.main.temp_min.toFloat().toInt()).toString() + "℉"
        } else {
            tvTemp.text = weatherFindItem.main.temp.toFloat().toInt().toString() + "℃"
            tvTempMaxMin.text = weatherFindItem.main.temp_max.toFloat().toInt().toString() + "℃" + "/" + weatherFindItem.main.temp_min.toFloat().toInt().toString() + "℃"
        }
        layoutCity.setOnClickListener { v: View? ->
            mBleDeviceTools.weatherCity = weatherFindItem.name
            mBleDeviceTools.weatherCityID = weatherFindItem.id
            mBleDeviceTools.weatherGps = weatherFindItem.coord.lon.toString() + "," + weatherFindItem.coord.lat.toString()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

}
