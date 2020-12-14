package com.zjw.apps3pluspro.module.device.weather

import android.content.Intent
import android.provider.Settings
import android.view.View
import butterknife.OnClick
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.utils.SysUtils


class WeatherHelpActivity : BaseActivity() {
    override fun setLayoutId(): Int {
        return R.layout.weather_help_activity
    }

    override fun initViews() {
        super.initViews()
        setTvTitle(R.string.weather_help)
    }

    @OnClick(R.id.tvAppSet, R.id.tvSystemSet)
    fun viewOnClick(view: View) {
        when (view.id) {
            R.id.tvAppSet -> {
                startActivity(SysUtils.getAppDetailSettingIntent(this))
            }
            R.id.tvSystemSet -> {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
        }
    }
}
