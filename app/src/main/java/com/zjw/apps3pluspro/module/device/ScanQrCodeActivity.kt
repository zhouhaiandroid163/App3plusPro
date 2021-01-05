package com.zjw.apps3pluspro.module.device

import android.app.Activity
import android.content.Intent
import com.zjw.apps3pluspro.utils.SysUtils
import com.zjw.apps3pluspro.zxing.activity.CaptureActivity

class ScanQrCodeActivity : CaptureActivity() {
    private val TAG = ScanQrCodeActivity::class.java.simpleName
    override fun onScanResultIntent(resultIntent: Intent) {
        //        super.onScanResultIntent(resultIntent);
        val content = resultIntent.getStringExtra("result")
        SysUtils.logContentI(TAG, " content = $content")
        val data = Intent()
        data.putExtra("content", content)
        setResult(Activity.RESULT_OK, data)
        finish()
    }
}
