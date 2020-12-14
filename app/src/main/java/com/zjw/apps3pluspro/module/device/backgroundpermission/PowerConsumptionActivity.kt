package com.zjw.apps3pluspro.module.device.backgroundpermission

import android.content.Intent
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
import kotlinx.android.synthetic.main.power_consumption_activity.*
import kotlinx.android.synthetic.main.public_head.*
import org.json.JSONObject

class PowerConsumptionActivity : BaseActivity() {
    private val TAG = PowerConsumptionActivity::class.java.simpleName
    override fun setLayoutId(): Int {
        return R.layout.power_consumption_activity
    }

    override fun initViews() {
        super.initViews()
        public_head_title.text = resources.getString(R.string.running_permission_title)

        layoutOppo.visibility = View.GONE
        layoutVivo.visibility = View.GONE

        var curValue = intent.getIntExtra("type", 0)
        when (curValue) {
            2 -> layoutOppo.visibility = View.VISIBLE
            3 -> layoutVivo.visibility = View.VISIBLE
        }

        val mRequestInfo = RequestJson.getSystemPermissionImageUrl("6", ((curValue + 1).toString()))
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
                startActivity(Intent(Settings.ACTION_SETTINGS));
            }
        }
    }

}
