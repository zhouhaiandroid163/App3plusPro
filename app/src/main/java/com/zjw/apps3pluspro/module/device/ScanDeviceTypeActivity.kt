package com.zjw.apps3pluspro.module.device

import android.content.Intent
import android.view.View
import android.widget.TextView
import butterknife.OnClick
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.bleservice.BleConstant

class ScanDeviceTypeActivity : BaseActivity() {
    override fun setLayoutId(): Int {
        return R.layout.scan_device_type_activity
    }

    @OnClick(R.id.layoutType1, R.id.layoutType2)
    fun viewOnClick(view: View) {
        when (view.id) {
            R.id.layoutType1 -> {
                val mIntent = Intent(this, ScanDeviceNoBindActivity::class.java)
                mIntent.putExtra("type", BleConstant.PLUS_HR)
                startActivity(mIntent)
                finish()
            }
            R.id.layoutType2 -> {
                val mIntent = Intent(this, ScanDeviceActivity::class.java)
                mIntent.putExtra("type", BleConstant.PLUS_Vibe)
                startActivity(mIntent)
                finish()
            }
        }
    }

    override fun initViews() {
        super.initViews()
        (findViewById<View>(R.id.public_head_title) as TextView).setText(R.string.add_device)
    }

}
