package com.zjw.apps3pluspro.module.device.backgroundpermission

import android.util.Log
import android.view.View
import android.widget.ImageView
import com.android.volley.VolleyError
import com.lidroid.xutils.BitmapUtils
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.network.NewVolleyRequest
import com.zjw.apps3pluspro.network.RequestJson
import com.zjw.apps3pluspro.network.ResultJson
import com.zjw.apps3pluspro.network.VolleyInterface
import kotlinx.android.synthetic.main.locking_activity.ivPicture
import kotlinx.android.synthetic.main.locking_activity.layoutXiaomi
import kotlinx.android.synthetic.main.locking_activity.layouthuawei
import kotlinx.android.synthetic.main.locking_activity.layoutoppo
import kotlinx.android.synthetic.main.locking_activity.layoutvivo
import kotlinx.android.synthetic.main.public_head.*
import org.json.JSONObject

class LockingActivity : BaseActivity() {
    private val TAG = LockingActivity::class.java.simpleName
    override fun setLayoutId(): Int {
        return R.layout.locking_activity
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

        val mRequestInfo = RequestJson.getSystemPermissionImageUrl("4", ((curValue + 1).toString()))
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
}
