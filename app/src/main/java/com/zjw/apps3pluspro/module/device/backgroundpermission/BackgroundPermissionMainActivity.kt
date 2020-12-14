package com.zjw.apps3pluspro.module.device.backgroundpermission

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import butterknife.OnClick
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.view.PickerView
import kotlinx.android.synthetic.main.background_permission_main_activity.*
import kotlinx.android.synthetic.main.public_head.*
import java.util.*

class BackgroundPermissionMainActivity : BaseActivity() {

    override fun setLayoutId(): Int {
        return R.layout.background_permission_main_activity
    }

    override fun initViews() {
        super.initViews()
        public_head_title.text = resources.getString(R.string.running_permission_title)
        var mtype = Build.BRAND
        mtype = mtype.toLowerCase(Locale.ROOT)
        if (mtype.startsWith("redmi") || mtype.startsWith("mi") || mtype.startsWith("xiaomi")) {
            curValue = 0
        } else if (mtype.startsWith("huawei") || mtype.startsWith("honor")) {
            curValue = 1
        } else if (mtype.startsWith("oppo")) {
            curValue = 2
        } else if (mtype.startsWith("vivo")) {
            curValue = 3
        } else if (mtype.startsWith("zte")) {
            curValue = 4
        } else if (mtype.startsWith("f")) {
            curValue = 4
        } else {
            curValue = 4
        }
        showText()
    }

    @OnClick(R.id.layoutPhoneType, R.id.layoutPowerSaving, R.id.layoutPermissionUnlimited, R.id.layoutBootCompleted, R.id.layoutLocking,
            R.id.layoutPowerManagement, R.id.layoutPowerConsumption, R.id.layoutOther)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.layoutPhoneType -> showPhoneTypeDialog()
            R.id.layoutPowerSaving -> startActivity(Intent(this, PowerSavingActivity::class.java))
            R.id.layoutPermissionUnlimited -> startActivity(Intent(this, UnlimitedActivity::class.java))
            R.id.layoutBootCompleted -> {
                val intent = Intent(this, BootCompletedActivity::class.java)
                intent.putExtra("type", curValue)
                startActivity(intent)
            }

            R.id.layoutLocking -> {
                val intent = Intent(this, LockingActivity::class.java)
                intent.putExtra("type", curValue)
                startActivity(intent)
            }
            R.id.layoutPowerManagement -> startActivity(Intent(this, PowerManagementActivity::class.java))
            R.id.layoutPowerConsumption -> {
                val intent = Intent(this, PowerConsumptionActivity::class.java)
                intent.putExtra("type", curValue)
                startActivity(intent)
            }

            R.id.layoutOther -> startActivity(Intent(this, PhoneOtherActivity::class.java))
        }
    }

    private var curValue: Int = 0
    private fun showPhoneTypeDialog() {
        // TODO Auto-generated method stub
        val view = layoutInflater.inflate(R.layout.phone_type_layout, null)
        var dialog = Dialog(this, R.style.shareStyle)
        dialog.setContentView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT))
        val window: Window? = dialog.window
        window?.setWindowAnimations(R.style.main_menu_animstyle)
        val wl = window?.attributes
        if (wl != null) {
            wl.x = 0
        }
        if (wl != null) {
            wl.y = windowManager.defaultDisplay.height
        }
        if (wl != null) {
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }

        val pvResources = view.findViewById<View>(R.id.pvResources) as PickerView

        val dataSex: MutableList<String> = ArrayList()
        dataSex.add(getString(R.string.running_permission_xiaomi))
        dataSex.add(getString(R.string.running_permission_huawei))
        dataSex.add(getString(R.string.running_permission_oppo))
        dataSex.add(getString(R.string.running_permission_vivo))
        dataSex.add(getString(R.string.running_permission_other_phone))


        when (curValue) {
            0 -> pvResources.setData(dataSex, 0)
            1 -> pvResources.setData(dataSex, 1)
            2 -> pvResources.setData(dataSex, 2)
            3 -> pvResources.setData(dataSex, 3)
            4 -> pvResources.setData(dataSex, 4)
        }

        pvResources.setOnSelectListener { text ->
            when (text) {
                resources.getString(R.string.running_permission_xiaomi) -> curValue = 0
                resources.getString(R.string.running_permission_huawei) -> curValue = 1
                resources.getString(R.string.running_permission_oppo) -> curValue = 2
                resources.getString(R.string.running_permission_vivo) -> curValue = 3
                resources.getString(R.string.running_permission_other_phone) -> curValue = 4
            }
        }

        view.findViewById<View>(R.id.tvCancel).setOnClickListener { dialog.cancel() }
        view.findViewById<View>(R.id.tvOk).setOnClickListener {
            showText()
            dialog.cancel()
        }
        dialog.onWindowAttributesChanged(wl)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    private fun showText() {
        layoutPhone.visibility = View.VISIBLE
        layoutOther.visibility = View.GONE

        layoutPowerSaving.visibility = View.GONE
        layoutPowerSavingIndex.visibility = View.GONE
        layoutPermissionUnlimited.visibility = View.GONE
        layoutPermissionUnlimitedIndex.visibility = View.GONE
        layoutBootCompleted.visibility = View.GONE
        layoutBootCompletedIndex.visibility = View.GONE
        layoutLocking.visibility = View.GONE
        layoutLockingIndex.visibility = View.GONE
        layoutPowerManagement.visibility = View.GONE
        layoutPowerManagementIndex.visibility = View.GONE
        layoutPowerConsumption.visibility = View.GONE
        when (curValue) {
            0 -> {
                tvPhoneType.text = resources.getString(R.string.running_permission_xiaomi)
                tvPhoneType2.text = resources.getString(R.string.running_permission_xiaomi)
                layoutPowerSaving.visibility = View.VISIBLE
                layoutPowerSavingIndex.visibility = View.VISIBLE
                layoutPermissionUnlimited.visibility = View.VISIBLE
                layoutPermissionUnlimitedIndex.visibility = View.VISIBLE
                layoutBootCompleted.visibility = View.VISIBLE
                layoutBootCompletedIndex.visibility = View.VISIBLE
                layoutLocking.visibility = View.VISIBLE
            }
            1 -> {
                tvPhoneType.text = resources.getString(R.string.running_permission_huawei)
                tvPhoneType2.text = resources.getString(R.string.running_permission_huawei)
                layoutBootCompleted.visibility = View.VISIBLE
                layoutBootCompletedIndex.visibility = View.VISIBLE
                layoutLocking.visibility = View.VISIBLE
                layoutLockingIndex.visibility = View.VISIBLE
                layoutPowerManagement.visibility = View.VISIBLE
            }
            2 -> {
                tvPhoneType.text = resources.getString(R.string.running_permission_oppo)
                tvPhoneType2.text = resources.getString(R.string.running_permission_oppo)
                layoutBootCompleted.visibility = View.VISIBLE
                layoutBootCompletedIndex.visibility = View.VISIBLE
                layoutLocking.visibility = View.VISIBLE
                layoutLockingIndex.visibility = View.VISIBLE
                layoutPowerConsumption.visibility = View.VISIBLE
            }
            3 -> {
                tvPhoneType.text = resources.getString(R.string.running_permission_vivo)
                tvPhoneType2.text = resources.getString(R.string.running_permission_vivo)
                layoutBootCompleted.visibility = View.VISIBLE
                layoutBootCompletedIndex.visibility = View.VISIBLE
                layoutLocking.visibility = View.VISIBLE
                layoutLockingIndex.visibility = View.VISIBLE
                layoutPowerConsumption.visibility = View.VISIBLE
            }
            4 -> {
                tvPhoneType.text = resources.getString(R.string.running_permission_other_phone)
                tvPhoneType2.text = resources.getString(R.string.running_permission_other_phone)
                layoutOther.visibility = View.VISIBLE
                layoutPhone.visibility = View.GONE
            }
        }
    }
}
