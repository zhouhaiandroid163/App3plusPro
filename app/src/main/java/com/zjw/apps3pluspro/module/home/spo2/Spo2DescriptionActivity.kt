package com.zjw.apps3pluspro.module.home.spo2

import android.view.View
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.base.BaseActivity
import kotlinx.android.synthetic.main.public_head_white_text.*

/**
 * Created by android
 * on 2021/4/13
 */
class Spo2DescriptionActivity : BaseActivity() {
    override fun setLayoutId(): Int {
        return R.layout.spo2_description_activity
    }

    override fun initViews() {
        super.initViews()
        ivTitleType.background = resources.getDrawable(R.mipmap.title_spo2_icon)
        layoutCalendar.visibility = View.GONE
        public_head_title.text = resources.getString(R.string.spo2_description_title)
    }
}