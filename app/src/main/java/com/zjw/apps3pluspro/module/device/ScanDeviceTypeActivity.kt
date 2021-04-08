package com.zjw.apps3pluspro.module.device

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.view.View
import android.widget.TextView
import butterknife.OnClick
import com.zjw.apps3pluspro.HomeActivity
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.bleservice.BleConstant
import com.zjw.apps3pluspro.utils.BluetoothUtil
import com.zjw.apps3pluspro.utils.SysUtils

class ScanDeviceTypeActivity : BaseActivity() {
    override fun setLayoutId(): Int {
        return R.layout.scan_device_type_activity
    }

    //适配器
    var mBluetoothAdapter: BluetoothAdapter? = null

    override fun initDatas() {
        super.initDatas()
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    }

    @OnClick(R.id.layoutType1, R.id.layoutType2)
    fun viewOnClick(view: View) {
        when (view.id) {
            R.id.layoutType1 -> {
                if (isOpenBluetooth()) {
                    val mIntent = Intent(this, ScanDeviceNoBindActivity::class.java)
                    mIntent.putExtra("type", BleConstant.PLUS_HR)
                    startActivity(mIntent)
                    finish()
                    SysUtils.logAppRunning("ScanDeviceTypeActivity.class", "scan = " + BleConstant.PLUS_HR)
                } else {
                    BluetoothUtil.enableBluetooth(this@ScanDeviceTypeActivity, HomeActivity.BleStateResult)
                }
            }
            R.id.layoutType2 -> {
                if (isOpenBluetooth()) {
                    val mIntent = Intent(this, ScanDeviceActivity::class.java)
                    mIntent.putExtra("type", BleConstant.PLUS_Vibe)
                    startActivity(mIntent)
                    finish()
                    SysUtils.logAppRunning("ScanDeviceTypeActivity.class", "scan = " + BleConstant.PLUS_Vibe)
                } else {
                    BluetoothUtil.enableBluetooth(this@ScanDeviceTypeActivity, HomeActivity.BleStateResult)
                }
            }
        }
    }

    private fun isOpenBluetooth(): Boolean {
        return mBluetoothAdapter?.isEnabled!!
    }


    override fun initViews() {
        super.initViews()
        (findViewById<View>(R.id.public_head_title) as TextView).setText(R.string.add_device)
    }

}
