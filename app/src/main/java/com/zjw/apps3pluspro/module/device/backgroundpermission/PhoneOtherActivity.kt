package com.zjw.apps3pluspro.module.device.backgroundpermission

import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.base.BaseActivity
import kotlinx.android.synthetic.main.public_head.*

class PhoneOtherActivity : BaseActivity() {

    override fun setLayoutId(): Int {
        return R.layout.phone_other_activity
    }

    override fun initViews() {
        super.initViews()
        public_head_title.text = resources.getString(R.string.running_permission_title)
    }

}
