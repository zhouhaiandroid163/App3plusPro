package com.zjw.apps3pluspro.module.device

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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

    @OnClick(R.id.layoutType1, R.id.layoutType2, R.id.layoutType3, R.id.tvCallNumber)
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
            R.id.layoutType3 -> {
                if (isOpenBluetooth()) {
                    val mIntent = Intent(this, ScanDeviceActivity::class.java)
                    mIntent.putExtra("type", BleConstant.PLUS_Vibe_Pro)
                    startActivity(mIntent)
                    finish()
                } else {
                    BluetoothUtil.enableBluetooth(this@ScanDeviceTypeActivity, HomeActivity.BleStateResult)
                }
            }
            R.id.tvCallNumber -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    val REQUEST_CODE_CONTACT = 101
                    val permissions = arrayOf<String>(Manifest.permission.CALL_PHONE)
                    //验证是否许可权限
                    for (str in permissions) {
                        if (checkSelfPermission(str) !== PackageManager.PERMISSION_GRANTED) { //申请权限
                            requestPermissions(permissions, REQUEST_CODE_CONTACT)
                            return
                        }
                    }
                }
                //如果需要手动拨号将Intent.ACTION_CALL改为Intent.ACTION_DIAL（跳转到拨号界面，用户手动点击拨打）
                val intent = Intent(Intent.ACTION_DIAL)
                val data: Uri = Uri.parse("tel:" + resources.getString(R.string.call_us4))
                intent.data = data
                startActivity(intent)
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
