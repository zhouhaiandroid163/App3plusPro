package com.zjw.apps3pluspro.module.device.backgroundpermission

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import butterknife.OnClick
import com.android.volley.VolleyError
import com.lidroid.xutils.BitmapUtils
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.network.*
import kotlinx.android.synthetic.main.power_saving_activity.*
import kotlinx.android.synthetic.main.public_head.*
import org.json.JSONObject


class PowerSavingActivity : BaseActivity() {
    private val TAG = PowerSavingActivity::class.java.simpleName
    override fun setLayoutId(): Int {
        return R.layout.power_saving_activity
    }

    override fun initViews() {
        super.initViews()
        public_head_title.text = resources.getString(R.string.running_permission_title)

        val mRequestInfo = RequestJson.getSystemPermissionImageUrl("1", "1")
        Log.i(TAG, "getSystemPermissionImageUrl=$mRequestInfo")
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, object : VolleyInterface(this, mListener, mErrorListener) {
            override fun onMySuccess(result: JSONObject) {
                val bitmapUtils = BitmapUtils(mContext)
                try {
                    Log.i(TAG, "getHtmlUrl=$result")
                    val resultString = result.optString("code")
                    if (resultString.equals(ResultJson.Code_operation_success, ignoreCase = true)) {
                        val jsonobject = result.optJSONObject("data")
                        val jsonArray = jsonobject.getJSONArray("imgUrls")
                        bitmapUtils.display<ImageView>(ivPicture, jsonArray[0].toString())
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            override fun onMyError(arg0: VolleyError) {

            }
        })


    }

    @OnClick(R.id.tvSetting)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.tvSetting -> {
                val mtype = Build.BRAND // 手机品牌
                val intent = Intent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                var componentName: ComponentName? = null
                if (mtype.startsWith("Redmi") || mtype.startsWith("MI")) {
                    componentName = ComponentName("com.miui.securitycenter", "com.miui.powercenter.PowerMainActivity")
                }
                intent.component = componentName
                try {
                    context.startActivity(intent)
                } catch (e: Exception) { //抛出异常就直接打开设置页面
                    startActivity(Intent(Settings.ACTION_SETTINGS));
                }
            }
        }
    }

}
