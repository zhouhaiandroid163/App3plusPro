package com.zjw.apps3pluspro.module.device.clockdial

import android.content.Intent
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.adapter.ThemeMarketStyleAdapter
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.utils.AutoLoadListener
import com.zjw.apps3pluspro.utils.AutoLoadListener.AutoLoadCallBack
import com.zjw.apps3pluspro.utils.DialMarketManager
import kotlinx.android.synthetic.main.public_head.*
import kotlinx.android.synthetic.main.theme_market_style_activity.*


class ThemeMarketStyleActivity : BaseActivity() {
    override fun setLayoutId(): Int {
        return R.layout.theme_market_style_activity
    }

    private var pageNum = 1
    private var dialTypeId = 1

    override fun initViews() {
        super.initViews()

        dialTypeId = intent.getIntExtra("dialTypeId", 0)
        val dialTypeName = intent.getStringExtra("dialTypeName")
        public_head_title.text = dialTypeName

        val autoLoadListener = AutoLoadListener(callBack)
        gridView.setOnScrollListener(autoLoadListener)
    }


    override fun initDatas() {
        super.initDatas()

        DialMarketManager.getInstance().getMoreDialPageList({
            loadData()
        },pageNum, dialTypeId)
    }

    private fun loadData() {
        var adapter = ThemeMarketStyleAdapter(this, DialMarketManager.getInstance().dialInfos)
        gridView.adapter = adapter
        adapter.notifyDataSetChanged()

        gridView.setOnItemClickListener { arg0, _, arg2, arg3 ->
            val dialInfo  = DialMarketManager.getInstance().dialInfos[arg2]
            val mIntent = Intent(context, ThemeUploadActivity::class.java)
            mIntent.putExtra("DialInfo", dialInfo)
            startActivity(mIntent)
        }
    }

    var callBack = AutoLoadCallBack {
        //            Utils.showToast("已经拖动至底部");
//        loadSpareItems(currentPage + 1) //这段代码是用来请求下一页数据的
    }


}
