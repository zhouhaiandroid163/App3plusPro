package com.zjw.apps3pluspro.module.device.clockdial

import androidx.recyclerview.widget.LinearLayoutManager
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.adapter.ThemeMarketRecyclerAdapter
import com.zjw.apps3pluspro.application.BaseApplication
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.module.device.entity.ThemeMarketItem
import com.zjw.apps3pluspro.utils.AppUtils
import com.zjw.apps3pluspro.utils.DialMarketManager
import kotlinx.android.synthetic.main.public_head.*
import kotlinx.android.synthetic.main.theme_market_activity.*

class ThemeMarketActivity : BaseActivity() {
    private val mBleDeviceTools = BaseApplication.getBleDeviceTools()
    private lateinit var recyclerAdapter: ThemeMarketRecyclerAdapter
    private var themeMarketItems = ArrayList<ThemeMarketItem>()

    override fun setLayoutId(): Int {
        return R.layout.theme_market_activity
    }

    override fun initViews() {
        super.initViews()
        public_head_title.text = resources.getText(R.string.my_theme_title)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun initDatas() {
        super.initDatas()
//        mBleDeviceTools.set_device_theme_shape(0)
//        mBleDeviceTools._device_theme_resolving_power_width = 128
//        mBleDeviceTools._device_theme_resolving_power_height = 220
//        mBleDeviceTools.setClockDialDataFormat(1);
//        mBleDeviceTools.setClockDialGenerationMode(1);

        DialMarketManager.getInstance().getMainDialList(object : DialMarketManager.GetListOnFinishListen {
            override fun success() {
                loadData()
            }

            override fun fail() {
                AppUtils.showToast(context, R.string.server_try_again_code0)
            }

            override fun error() {
                AppUtils.showToast(context, R.string.net_worse_try_again)
            }
        })
//        if (DialMarketManager.getInstance().themeMarketItems.size > 0) {
//            loadData()
//        } else {
//            DialMarketManager.getInstance().getMainDialList {
//                loadData()
//            }
//        }
    }

    private fun loadData() {
        themeMarketItems = DialMarketManager.getInstance().themeMarketItems
        recyclerAdapter = ThemeMarketRecyclerAdapter(context, themeMarketItems)
        recyclerView.adapter = recyclerAdapter
        recyclerAdapter.notifyDataSetChanged()
    }

}
