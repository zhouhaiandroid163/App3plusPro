package com.zjw.apps3pluspro.module.device.backgroundpermission

import android.content.ComponentName
import android.content.Context
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
import com.zjw.apps3pluspro.network.NewVolleyRequest
import com.zjw.apps3pluspro.network.RequestJson
import com.zjw.apps3pluspro.network.ResultJson
import com.zjw.apps3pluspro.network.VolleyInterface
import kotlinx.android.synthetic.main.boot_completed_activity.*
import kotlinx.android.synthetic.main.boot_completed_activity.ivPicture
import kotlinx.android.synthetic.main.public_head.*
import org.json.JSONObject


class BootCompletedActivity : BaseActivity() {
    private val TAG = BootCompletedActivity::class.java.simpleName
    override fun setLayoutId(): Int {
        return R.layout.boot_completed_activity
    }


    override fun initViews() {
        super.initViews()
        public_head_title.text = resources.getString(R.string.running_permission_title)

        layoutXiaomi.visibility = View.GONE
        layouthuawei.visibility = View.GONE
        layoutoppo.visibility = View.GONE
        layoutvivo.visibility = View.GONE

        var curValue = intent.getIntExtra("type", 0)
        when (curValue) {
            0 -> layoutXiaomi.visibility = View.VISIBLE
            1 -> layouthuawei.visibility = View.VISIBLE
            2 -> layoutoppo.visibility = View.VISIBLE
            3 -> layoutvivo.visibility = View.VISIBLE
        }

        val mRequestInfo = RequestJson.getSystemPermissionImageUrl("3", ((curValue + 1).toString()))
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
                selfStartManagerSettingIntent(context);
            }
        }
    }

    private fun selfStartManagerSettingIntent(context: Context) {
        val mtype = Build.BRAND // 手机品牌
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        var componentName: ComponentName? = null
        if (mtype.startsWith("Redmi") || mtype.startsWith("MI")) {
            componentName = ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
        } else if (mtype.startsWith("HUAWEI") || mtype.startsWith("HONOR")) {
            componentName = ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")
        } else if (mtype.startsWith("vivo")) {
            componentName = ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")
        } else if (mtype.startsWith("ZTE")) {
            componentName = ComponentName("com.zte.heartyservice", "com.zte.heartyservice.autorun.AppAutoRunManager")
        } else if (mtype.startsWith("F")) {
            componentName = ComponentName("com.gionee.softmanager", "com.gionee.softmanager.oneclean.AutoStartMrgActivity")
        } else if (mtype.startsWith("oppo")) {
            componentName = ComponentName("oppo com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")
        }
        intent.component = componentName
        try {
            context.startActivity(intent)
        } catch (e: Exception) { //抛出异常就直接打开设置页面
            startActivity(Intent(Settings.ACTION_SETTINGS));
        }
    }
}
