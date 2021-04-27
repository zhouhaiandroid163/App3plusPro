package com.zjw.apps3pluspro.module.device.weather

import android.app.Activity
import android.content.Context
import android.os.Handler
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
import butterknife.OnClick
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.application.BaseApplication
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.utils.GpsSportManager
import kotlinx.android.synthetic.main.weather_city_search_activity.*
import java.util.ArrayList


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
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(getLocationTimeOut, 10 * 1000)

        isStartLocation = true
        isLocationSuccess = false
        tvCityName.text = resources.getString(R.string.weather_location_ing)
        GpsSportManager.getInstance().getLatLon(this) { gpsInfo: GpsSportManager.GpsInfo ->
            GpsSportManager.getInstance().stopGps(this)
            GpsSportManager.getInstance().getWeatherCity(this) {
                handler.removeCallbacksAndMessages(null)
                isStartLocation = false
                isLocationSuccess = true
                tvCityName.text = mBleDeviceTools.weatherCity
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

}
