package com.zjw.apps3pluspro.module.device.clockdial

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.content.ContextCompat
import butterknife.OnClick
import com.lidroid.xutils.BitmapUtils
import com.zjw.apps3pluspro.HomeActivity
import com.zjw.apps3pluspro.R
import com.zjw.apps3pluspro.application.BaseApplication
import com.zjw.apps3pluspro.base.BaseActivity
import com.zjw.apps3pluspro.bleservice.BleConstant
import com.zjw.apps3pluspro.bleservice.BleService
import com.zjw.apps3pluspro.bleservice.BroadcastTools
import com.zjw.apps3pluspro.eventbus.GetDeviceProtoWatchFacePrepareStatusEvent
import com.zjw.apps3pluspro.eventbus.GetDeviceProtoWatchFacePrepareStatusSuccessEvent
import com.zjw.apps3pluspro.eventbus.UploadThemeStateEvent
import com.zjw.apps3pluspro.eventbus.tools.EventTools
import com.zjw.apps3pluspro.module.device.clockdial.custom.CustomClockDialNewUtils
import com.zjw.apps3pluspro.module.device.clockdial.custom.MyCustomClockUtils
import com.zjw.apps3pluspro.module.device.entity.ThemeDetails
import com.zjw.apps3pluspro.module.device.entity.ThemeMarketItem
import com.zjw.apps3pluspro.module.device.entity.ThemeModle
import com.zjw.apps3pluspro.network.okhttp.MyOkHttpClient
import com.zjw.apps3pluspro.utils.*
import com.zjw.apps3pluspro.utils.DialMarketManager.GetDialDetailsListen
import com.zjw.apps3pluspro.utils.DialogUtils.DialogClickListener
import com.zjw.apps3pluspro.utils.log.MyLog
import com.zjw.apps3pluspro.view.ColorPickerView
import com.zjw.apps3pluspro.view.ColorRoundView
import com.zjw.apps3pluspro.view.dialog.WaitDialog
import kotlinx.android.synthetic.main.public_head.*
import kotlinx.android.synthetic.main.theme_upload_activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class ThemeUploadActivity : BaseActivity() {
    private lateinit var protoHandler: Handler
    private val TAG = ThemeUploadActivity::class.java.simpleName
    var mBleDeviceTools = BaseApplication.getBleDeviceTools()
    override fun setLayoutId(): Int {
        return R.layout.theme_upload_activity;
    }

    lateinit var layountInflater: LayoutInflater
    private val colorList = intArrayOf(
            R.color.theme_color1,
            R.color.theme_color2,
            R.color.theme_color3,
            R.color.theme_color4,
            R.color.theme_color5,
            R.color.theme_color6,
            R.color.theme_color7,
            R.color.theme_color8,
            R.color.theme_color9,
            R.color.theme_color10)

    lateinit var handler: Handler
    override fun initViews() {
        super.initViews()
        EventTools.SafeRegisterEventBus(this)
        SysUtils.makeRootDirectory(Constants.HEAD_IMG)
        public_head_title.text = resources.getText(R.string.custom_sync)
        layountInflater = LayoutInflater.from(context)
        handler = Handler()
        protoHandler = Handler()
        waitDialog = WaitDialog(context)
        val filter = IntentFilter()
        filter.addAction(BroadcastTools.ACTION_DOWN_CLOCK_FILE_STATE_SUCCESS)
        filter.addAction(BroadcastTools.ACTION_DOWN_CLOCK_FILE_STATE_ERROR)
        filter.priority = 1000
        registerReceiver(broadcastReceiver, filter)

        initColorLayout()
    }

    private fun initColorLayout() {
        layoutColor.removeAllViews()
        for (i in colorList.indices) {
            val mLinearLayout = layountInflater.inflate(R.layout.theme_color_layout, null) as LinearLayout

            val colorRoundView = mLinearLayout.findViewById<ColorRoundView>(R.id.colorRoundView)
            val ivColorBg = mLinearLayout.findViewById<ImageView>(R.id.ivColorBg)
            colorRoundView.setBgColor(colorList[i], colorList[i])
            colorRoundView.setOnClickListener {
                for (i in colorList.indices) {
                    val childView = layoutColor.getChildAt(i)
                    val childViewColorBg = childView.findViewById<ImageView>(R.id.ivColorBg)
                    childViewColorBg.background = ContextCompat.getDrawable(this, R.color.transparent)
                }
                ivColorBg.background = ContextCompat.getDrawable(this, R.mipmap.theme_color_bg)

                val color: Int = colorRoundView.getcolor()
                setColor(Color.red(color), Color.green(color), Color.blue(color))
            }
            layoutColor.addView(mLinearLayout)
        }
    }


    lateinit var bitmapUtils: BitmapUtils
    lateinit var dialInfo: ThemeMarketItem.DialInfo
    override fun initDatas() {
        super.initDatas()
        initBroadcast()
        initTheme()
    }

    private fun initTheme() {
        var deviceWidth = mBleDeviceTools._device_theme_resolving_power_width
        var deviceHeight = mBleDeviceTools._device_theme_resolving_power_height
        UiType = if (deviceWidth == 128 && deviceHeight == 220) 1 else 2
        bitmapUtils = BitmapUtils(context)

        dialInfo = intent.getSerializableExtra("DialInfo") as ThemeMarketItem.DialInfo
        bitmapUtils.display(ivThemeMain, dialInfo.effectImgUrl)
        tvThemeName.text = dialInfo.dialName

        waitDialog?.show(getString(R.string.loading0))
        DialMarketManager.getInstance().getDialDetails(dialInfo.dialId, object : GetDialDetailsListen {
            override fun success(themeDetails: ThemeDetails) {
                waitDialog?.close()
                updateUi(themeDetails)
            }

            override fun error(code: Int) {
                waitDialog?.close()
                AppUtils.showToast(context, R.string.data_try_again_code1)
                finish()
            }
        })
    }

    lateinit var themeDetails: ThemeDetails
    private var hasData: Boolean = false
    private var horizontalScanUrl: String? = null
    private var horizontalScanMd5Value: String? = null
    private var verticalScanUrl: String? = null
    private var verticalScanMd5Value: String? = null

    private var isCustom = false
    private var customTextUrl: String = ""
    private var customBgUrl: String = ""


    @SuppressLint("SetTextI18n")
    private fun updateUi(themeDetails: ThemeDetails) {
        this.themeDetails = themeDetails
        for (i in 0 until themeDetails.dialFileList.size) {
            when (themeDetails.dialFileList[i].dialFileType) {
                1 -> {
                    horizontalScanUrl = themeDetails.dialFileList[i].dialFileUrl
                    horizontalScanMd5Value = themeDetails.dialFileList[i].md5Value
                }
                2 -> {
                    verticalScanUrl = themeDetails.dialFileList[i].dialFileUrl
                    verticalScanMd5Value = themeDetails.dialFileList[i].md5Value
                }
                3 -> {
                }
                4 -> {
                }
                5 -> {
                    customBgUrl = themeDetails.dialFileList[i].dialFileUrl
                }
                6 -> {
                    customTextUrl = themeDetails.dialFileList[i].dialFileUrl
                }
            }
        }
        if (themeDetails.clockDialType == 1) {
            isCustom = true
            layoutCustomize.visibility = View.VISIBLE
            Thread(Runnable {
                oldBgBitmap = GlideUtils.getBitMapByService(this, customBgUrl, mBleDeviceTools._device_theme_resolving_power_width, mBleDeviceTools._device_theme_resolving_power_height)
                newBgBitmap = oldBgBitmap
//                handler.post { ivThemeMain.setImageBitmap(newBgBitmap) }
            }).start()
            Thread(Runnable {
                oldTextBitmap = GlideUtils.getBitMapByService(this, customTextUrl, mBleDeviceTools._device_theme_resolving_power_width, mBleDeviceTools._device_theme_resolving_power_height)
                newTextBitmap = oldTextBitmap
//                handler.post { ivThemeText.setImageBitmap(newTextBitmap) }
            }).start()
        } else {
            layoutCustomize.visibility = View.GONE
        }

        tvSummaryContent.text = themeDetails.dialDescribe

        val size: Int = themeDetails.binSize
        tvThemeSize.text = size.toString() + "KB | " + themeDetails.downNum + resources.getString(R.string.sport_frequency)
        if (hasData) {
            return
        }
        hasData = true

        if (themeDetails.groupDialList.size <= 1) {
            tvTitle.visibility = View.GONE
            horizontalScrollView.visibility = View.GONE
        }

        layoutThemeCandidate.removeAllViews()
        for (i in 0 until themeDetails.groupDialList.size) {
            val mLinearLayout = layountInflater.inflate(R.layout.theme_details_item, null) as LinearLayout

            val ivTheme = mLinearLayout.findViewById<ImageView>(R.id.ivTheme)
            val ivThemeBg = mLinearLayout.findViewById<ImageView>(R.id.ivThemeBg)
//            bitmapUtils.display(ivTheme, themeDetails.groupDialList[i].thumbnailUrl)
            var childBitmap: Bitmap? = null
            Thread(Runnable {
                childBitmap = GlideUtils.getBitMapByService(this, themeDetails.groupDialList[i].effectImgUrl, mBleDeviceTools._device_theme_resolving_power_width, mBleDeviceTools._device_theme_resolving_power_height)
                handler.post { ivTheme.setImageBitmap(childBitmap) }
            }).start()
            ivTheme.setOnClickListener {
                if (childBitmap == null) {
                    return@setOnClickListener
                }

                waitDialog!!.show(getString(R.string.loading0))

                DialMarketManager.getInstance().getDialDetails(themeDetails.groupDialList[i].dialId, object : GetDialDetailsListen {
                    override fun success(themeDetails: ThemeDetails) {
                        waitDialog!!.close()
                        for (i in 0 until themeDetails.groupDialList.size) {
                            val childView = layoutThemeCandidate.getChildAt(i)
                            val childViewThemeBg = childView.findViewById<ImageView>(R.id.ivThemeBg)
                            childViewThemeBg.background = ContextCompat.getDrawable(this@ThemeUploadActivity, R.color.transparent)
                        }
                        ivThemeBg.background = ContextCompat.getDrawable(this@ThemeUploadActivity, R.mipmap.theme_select_bg)

                        ivThemeText.setImageBitmap(null)
                        ivThemeMain.setImageBitmap(childBitmap)
                        mainBitmap = childBitmap
//                bitmapUtils.display(ivThemeMain, themeDetails.groupDialList[i].effectImgUrl)
                        initColorLayout()
                        updateUi(themeDetails)
                    }

                    override fun error(code: Int) {
                        waitDialog!!.close()
                        AppUtils.showToast(context, R.string.data_try_again_code1)
                    }
                })

            }
            layoutThemeCandidate.addView(mLinearLayout)
        }
    }

    private var mainBitmap: Bitmap? = null

    private var waitDialog: WaitDialog? = null

    @OnClick(R.id.ivCustomizeColor, R.id.tvThemeUpload, R.id.layoutSelectPicture)
    fun viewOnClick(view: View) {
        when (view.id) {
            R.id.ivCustomizeColor -> {
                showSelectColor()
            }
            R.id.tvThemeUpload -> {
                try {
                    if (MyOkHttpClient.getInstance().isConnect(context)) {
                        if (HomeActivity.getBlueToothStatus() == BleConstant.STATE_CONNECTED) {
                            uploadTheme()
                        } else {
                            waitDialog!!.show(getString(R.string.index_tip_no_connect1))

                            val handle = Handler()
                            handle.postDelayed({
                                waitDialog?.close()
                                finish()
                            }, 2000)
                        }
                    } else {
                        AppUtils.showToast(context, R.string.net_worse_try_again)
                    }
                } catch (e: Exception) {
                    waitDialog!!.show(getString(R.string.device_prepare2))

                    val handle = Handler()
                    handle.postDelayed({
                        waitDialog?.close()
                        finish()
                    }, 2000)
                }

            }
            R.id.layoutSelectPicture -> {
                if (AuthorityManagement.verifyStoragePermissions(this)) {
                    MyLog.i(TAG, "SD卡权限 已获取")
                    showImgDialog()
                } else {
                    MyLog.i(TAG, "SD卡权限 未获取")
                    SysUtils.logAppRunning(TAG, "SD卡权限 未获取 ")
                }
            }
        }
    }

    private var curThemeName: String = ""
    private var curThemeMd5: String? = null
    private fun uploadTheme() {
        var url: String? = null
        if (mBleDeviceTools._device_theme_scanning_mode) {
            url = verticalScanUrl
            curThemeMd5 = verticalScanMd5Value
        } else {
            url = horizontalScanUrl
            curThemeMd5 = horizontalScanMd5Value
        }
        curThemeName = themeDetails.dialId.toString() + "_" + curThemeMd5
        if (!ThemeUtils.checkFileExistenceByMd5(Constants.DOWN_THEME_FILE, curThemeName, curThemeMd5)) {
            DialMarketManager.getInstance().uploadDialDownloadRecording(themeDetails.dialId, DialMarketManager.uploadDialDownloadRecordingType1_downloading, this)
            DialMarketManager.getInstance().downLoadThemeFile(this, curThemeName, url)
        } else {
            initSendData()
        }
    }

    private fun startSendThemeData(byte: ByteArray) {
        if (mBleDeviceTools._device_is_theme_transmission) {
            if (HomeActivity.ISBlueToothConnect()) {
                if (mBleDeviceTools._ble_device_power >= 50) {
                    DialMarketManager.getInstance().uploadDialDownloadRecording(themeDetails.dialId, DialMarketManager.uploadDialDownloadRecordingType2_transport, this)
                    sendDataToBle(byte)
                } else {
                    AppUtils.showToast(this, R.string.send_imge_error_low_power)
                }
            } else {
                AppUtils.showToast(this, R.string.no_connection_notification)
            }
        }
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("NewApi")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BroadcastTools.ACTION_DOWN_CLOCK_FILE_STATE_SUCCESS ->
                    if (!ThemeUtils.checkFileExistenceByMd5(Constants.DOWN_THEME_FILE, curThemeName, curThemeMd5)) {
                        MyLog.i(TAG, "数据大小 Bytes = 文件不存在")
                    } else {
                        initSendData()
                    }
                BroadcastTools.ACTION_DOWN_CLOCK_FILE_STATE_ERROR -> {
                    AppUtils.showToast(context, R.string.net_worse_try_again)
                    SysUtils.logAppRunning(TAG, "ACTION_DOWN_CLOCK_FILE_STATE_ERROR ")
                }
            }
        }
    }

    lateinit var byteTheme: ByteArray
    private fun initSendData() {
        protoHandler = Handler()

        if (isCustom) {
//            byte = CustomClockDialUtils.getCustonClockDialDataByFile(this@ThemeUploadActivity, curThemeName, color_r, color_g, color_b, newBgBitmap)
            if (newBgBitmap == null || newTextBitmap == null) {
                initTheme()
                return
            }
            byteTheme = CustomClockDialNewUtils.getNewCustomClockDialData(curThemeName, color_r, color_g, color_b, newBgBitmap, newTextBitmap)
        } else {
            byteTheme = ThemeUtils.getBytes(Constants.DOWN_THEME_FILE + curThemeName)
        }
        MyLog.i(TAG, "数据大小 Bytes = 文件已存在 Bytes len = " + byteTheme.size)
        if (mBleDeviceTools.isSupportProtobuf && mBleDeviceTools.deviceUpdateType) {
            //检测设备状态状态
            if (mBleDeviceTools.isSupportGetDeviceProtoStatus) {
                waitDialog!!.show(getString(R.string.ignored))
                protoHandler?.removeCallbacksAndMessages(null)
                protoHandler?.postDelayed(getDeviceStatusTimeOut, 10 * 1000)
                EventBus.getDefault().post(GetDeviceProtoWatchFacePrepareStatusEvent(themeDetails.dialId.toString(), byteTheme.size))
            } else {
                startSendThemeDataByProto(byteTheme)
            }
        } else {
            startSendThemeData(byteTheme)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getDeviceProtoWatchFacePrepareStatusSuccessEvent(event: GetDeviceProtoWatchFacePrepareStatusSuccessEvent) {
        protoHandler?.removeCallbacksAndMessages(null)
        SysUtils.logAppRunning(TAG, "GetDeviceProtoWatchFacePrepareStatusSuccessEvent = " + event.status)
        when (event.status) {
            0 -> {
                waitDialog?.close()
                startSendThemeDataByProto(byteTheme)
            }
            1 -> {
                finishActivity(resources.getString(R.string.device_prepare1))
            }
            2 -> {
                finishActivity(resources.getString(R.string.device_prepare2))
            }
            3 -> {
                finishActivity(resources.getString(R.string.device_prepare2))
            }
            4 -> {
                finishActivity(resources.getString(R.string.device_prepare4))
            }
            5 -> {
                finishActivity(resources.getString(R.string.device_prepare2))
            }
        }
    }

    fun finishActivity(text: String) {
        waitDialog!!.show(text)
        val handle = Handler()
        handle.postDelayed({
            waitDialog?.close()
            finish()
        }, Constants.FINISH_ACTIVITY_DELAY_TIME.toLong())
    }

    private var getDeviceStatusTimeOut = Runnable {
        Log.w("ble", " getDeviceStatusTimeOut Time Out")
        protoHandler?.removeCallbacksAndMessages(null)
        finishActivity(resources.getString(R.string.device_prepare2))
    }

    // 弹框的信息
    private lateinit var dialog: Dialog

    private fun showSelectColor() { // TODO Auto-generated method stub
        val view = layoutInflater.inflate(R.layout.select_color_layout, null)
        dialog = Dialog(context, R.style.transparentdialog)
        dialog.setContentView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        val window: Window = dialog.window!!
        window.setWindowAnimations(R.style.main_menu_animstyle)
        val wl = window.attributes
        wl.x = 0
        wl.y = windowManager.defaultDisplay.height
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT
        val colorPickerView: ColorPickerView = view.findViewById(R.id.colorPickerView)
        colorPickerView.setOnColorChangedListenner { color, originalColor, saturation ->
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            setColor(red, green, blue)
        }
        view.findViewById<View>(R.id.ivCancel).setOnClickListener { dialog.dismiss() }
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl)
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    private var color_r = 255
    private var color_g = 255
    private var color_b = 255

    private var oldTextBitmap: Bitmap? = null
    private var newTextBitmap: Bitmap? = null

    private var oldBgBitmap: Bitmap? = null
    private var newBgBitmap: Bitmap? = null
    private fun setColor(r: Int, g: Int, b: Int) {
        color_r = r
        color_g = g
        color_b = b
        if (oldTextBitmap == null) {
            return
        }
//        newTextBitmap = CustomClockDialUtils.getNewTextBitmap(oldTextBitmap, r, g, b)
        newTextBitmap = MyCustomClockUtils.getNewTextBitmap(oldTextBitmap, r, g, b)
        ivThemeMain.setImageBitmap(newBgBitmap)
        ivThemeText.setImageBitmap(newTextBitmap)
    }

    // *****************************   send theme to device by proto *******************

    private fun startSendThemeDataByProto(byte: ByteArray?) {
        DialMarketManager.getInstance().uploadDialDownloadRecording(themeDetails.dialId, DialMarketManager.uploadDialDownloadRecordingType2_transport, this)
        BleService.bluetoothLeService.sendTheme("watch", byte)
        initLoadingdialog()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun UploadThemeStateEvent(event: UploadThemeStateEvent) {
        when (event.state) {
            1 -> {
                Log.w("ble", " uploadProtoTheme Time Out")
                Toast.makeText(this@ThemeUploadActivity, resources.getText(R.string.send_fail), Toast.LENGTH_SHORT).show()
                if (loading_dialog != null && loading_dialog!!.isShowing) {
                    loading_dialog!!.dismiss()
                }
                finish()
            }
            2 -> {
                send_loading_progressbar!!.max = 100
                send_loading_progressbar!!.progress = event.progress
                end_loading_text!!.text = (event.progress).toString()
            }
            3 -> {
                DialMarketManager.getInstance().uploadDialDownloadRecording(themeDetails.dialId, DialMarketManager.uploadDialDownloadRecordingType3_success, this@ThemeUploadActivity)
                Toast.makeText(this@ThemeUploadActivity, resources.getText(R.string.send_success), Toast.LENGTH_SHORT).show()
                if (loading_dialog != null && loading_dialog!!.isShowing) {
                    loading_dialog!!.dismiss()
                }
                finish()
            }
        }
    }

    // *****************************   send theme to device *******************

    /**
     * 发送蓝牙数据
     */
    private lateinit var mThemeModle: ThemeModle

    private fun sendDataToBle(bytes: ByteArray) {
        MyLog.i(TAG, "主题传输发送 MTU = " + mBleDeviceTools._device_mtu_num)
        mThemeModle = ThemeModle(bytes, mBleDeviceTools._device_mtu_num)
        MyLog.i(TAG, "发送数据 服务不为空")
        waitDialog!!.show(getString(R.string.loading0))
        startSendData()
        AppsendHead()
        TimerFourStart()
        //            TimerOneStart();
    }

    //当前传输的块
    private var NowBlock = 0

    //当前块的第几个包？
    private var NowPage = 0
    private var SnNum = 0

    //当前包的大小
    private var NowPageNumber = 0

    //补发总包数
    private val ReplacementMax = 0

    //当前补发的
    private var NowReplacement = 0


    //当前是否是补发状态
    private var isReplacement = false

    private var is_send_fial = false

    //开始传输
    private fun startSendData() {
        NowBlock = 1
        NowPage = 0
        SnNum = 0
        NowPageNumber = 0
        NowReplacement = 0
        isReplacement = false
        is_send_fial = false
    }

    private var is_send_data = false

    //设备准备就绪
    private fun BleReady() {
        is_send_data = true
        is_send_fial = false
        waitDialog!!.close()
        initLoadingdialog()
        TimerTwoStart()
        FourNowCount = 0
    }

    private fun sendDataToNor() {
        MyLog.i(TAG, "正常发1 传输状态 = $is_send_data")
        if (is_send_data) { //不补发
            isReplacement = false
            //传输结束
            if (mThemeModle.isLastBlock(NowBlock)) {
                MyLog.i(TAG, "传输结束")
                handSuccessState()
            } else {
                MyLog.i(TAG, "响应不补发-需要发送下一块")
                NowBlock++
                NowPage = 0
                TimerTwoStart()
            }
        }
    }

    //模拟丢包
    private var testPassDataValue = 0

    //======== 第1个定时器====================
    //发送头
    private fun AppsendHead() {
        MyLog.i(TAG, "发送头")
        MyLog.i(TAG, "发送头 getPageDataMax = " + mThemeModle.getPageDataMax())
        MyLog.i(TAG, "发送头 getTotalPageSize = " + mThemeModle.getTotalPageSize())
        //模拟丢包，清零
        testPassDataValue = 0
        sendThemeHead(mThemeModle)
    }

    //======== 第2个定时器====================发送数据-正常数据
    //    int CheckDelayTime = 1000*10;
    //    int SendDelayTime = 1000*10;
    private val CheckDelayTime = 10
    private val CheckDelayTimeTwo = 50
    private val CheckDelayTimeThree = 100
    private val SendDelayTime = 10

    private fun sendNorDataToBle() {
        val send_data: ByteArray = mThemeModle.getSendData(NowBlock, NowPage, SnNum)
        //            MyLog.i(TAG, "发送数据 正常 = " + ThemeUtils.bytes2HexString3(send_data));
//=====================================模拟丢包==============
//            if (testPassDataValue == 20 || testPassDataValue == 30) {
//                if (send_data != null) {
//                    MyLog.i(TAG, "发送数据 模拟丢包 = " + testPassDataValue);
//                    MyLog.i(TAG, "发送数据 正常 = 编码 = NowBlock " + NowBlock + " NowPage =" + NowPage + " SnNum = " + SnNum);
//                    MyLog.i(TAG, "发送数据 正常 = " + ThemeUtils.bytes2HexString3(send_data));
//                } else {
//                    MyLog.i(TAG, "发送数据 数据为空 = " + testPassDataValue);
//                }
//            } else {
//                if (send_data != null) {
//                    mService.send_theme_data(SnNum, send_data);
//                } else {
//                    MyLog.i(TAG, "发送数据 数据为空 = " + testPassDataValue);
//                }
//            }
//=====================================模拟丢包==============
//正常发送数据
        if (send_data != null) {
            sendThemeData(SnNum, send_data)
        } else {
            MyLog.i(TAG, "发送数据 数据为空 = $testPassDataValue")
        }
    }

    //发送块校验
    private fun sendBlockVerfication() {
        Handler().postDelayed({
            MyLog.i(TAG, "主题数据 传输 发送块校验1")
            sendThemeBlockVerfication()
            FourNowCount = 0
        }, CheckDelayTime.toLong())
    }


    //发送正常数据
    private fun sendNormalData() {
        testPassDataValue++
        FourNowCount = 0
        NowReplacement = 0
        NowPage++
        SnNum++
        //最后一个包
        if (mThemeModle.isLastPage(SnNum)) { //            MyLog.i(TAG, "主题数据 传输 是最后的包 NowPage = " + NowPage + " SnNum = " + SnNum);
//最后一个正常数据发送完毕
            TimerTwoStop()
            sendNorDataToBle()
            //发送块校验
            sendBlockVerfication()
        } else { //            MyLog.i(TAG, "主题数据 传输 不是最后的包 NowPage = " + NowPage + " SnNum = " + SnNum);
//最后一个正常数据发送完毕
            if (mThemeModle.isNowBlockLastPage(NowPage)) {
                TimerTwoStop()
            }
            sendNorDataToBle()
            //最后一个正常数据发送完毕
            if (mThemeModle.isNowBlockLastPage(NowPage)) {
                sendBlockVerfication()
            }
        }
        send_loading_progressbar!!.progress = SnNum
        val por_str = ThemeUtils.getPercentageStr(send_loading_progressbar!!.max, SnNum)
        //        MyLog.i(TAG, "表盘传输进度条 = por_str = " + por_str);
        end_loading_text!!.text = por_str
    }

    private var TimerTwoIsStop = false
    private val TimerTwoHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) { // TODO Auto-generated method stub
            super.handleMessage(msg)
            when (msg.what) {
                1 ->  //                    MyLog.i(TAG, "定时器2=TimerTwoIsStop = " + TimerTwoIsStop);
// 添加更新ui的代码
                    if (!TimerTwoIsStop) {
                        sendNormalData()
                        sendEmptyMessageDelayed(1, SendDelayTime.toLong())
                    }
                0 -> {
                }
            }
        }
    }

    private fun TimerTwoStart() {
        MyLog.i(TAG, "定时器2=打开")
        TimerTwoHandler.sendEmptyMessage(1)
        TimerTwoIsStop = false
    }

    private fun TimerTwoStop() {
        MyLog.i(TAG, "定时器2=关闭")
        TimerTwoHandler.sendEmptyMessage(0)
        TimerTwoIsStop = true
    }

    //======== 第3个定时器====================

    //======== 第3个定时器====================
    private var replacmentSnList1: ArrayList<Int>? = null
    private var replacmentSnList2: ArrayList<Int>? = null

    //补发数据间隔
    private val ReplacementDelayTime = 10

    private fun sendRepDataToBle(sn_number: Int) {
        MyLog.i(TAG, "发送数据 补发 = 编码 = sn_number " + sn_number + " getPageDataMax =" + mThemeModle.getPageDataMax())
        var now_page: Int = sn_number % mThemeModle.getPageDataMax()
        if (now_page <= 0) {
            now_page = mThemeModle.getPageDataMax()
        }
        MyLog.i(TAG, "发送数据 补发 = 编码 = NowBlock $NowBlock now_page =$now_page sn_number = $sn_number")
        val send_data: ByteArray = mThemeModle.getSendData(NowBlock, now_page, sn_number)
        if (send_data != null) {
            MyLog.i(TAG, "发送数据 补发 = " + ThemeUtils.bytes2HexString3(send_data))
            sendThemeData(sn_number, send_data)
        } else {
            MyLog.i(TAG, "发送数据 数据为空 = $testPassDataValue")
        }
    }


    //发送补发数据
    private fun sendReplacment() {
        MyLog.i(TAG, "补发1 传输状态 = $is_send_data")
        if (is_send_data) {
            NowReplacement++
            MyLog.i(TAG, "主题数据 传输 发送补发数据 NowReplacement = " + NowReplacement + "  data_sn = " + replacmentSnList1!![NowReplacement - 1])
            sendRepDataToBle(replacmentSnList1!![NowReplacement - 1])
            //最后一个数据
            if (NowReplacement >= replacmentSnList1!!.size) {
                MyLog.i(TAG, "主题数据 传输 发送补发数据  最后一个数据")
                TimerThreeStop()
                TimerFiveStart()
            }
        } else {
            TimerFiveStop()
        }
    }

    private fun checkReplacmentData() {
        if (replacmentSnList2!!.size == 0) {
            MyLog.i(TAG, "主题数据 传输 不需要第二轮补发")
            MyLog.i(TAG, "补发2 传输状态 = $is_send_data")
            if (is_send_data) {
                sendBlockVerfication()
            }
        } else {
            MyLog.i(TAG, "补发3 传输状态 = $is_send_data")
            if (is_send_data) {
                MyLog.i(TAG, "主题数据 传输 需要第二轮补发！！")
                replacmentSnList1 = ArrayList(replacmentSnList2!!)
                TimerThreeStart()
            }
        }
    }

    private var TimerThreeIsStop = false
    private val TimerThreeHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) { // TODO Auto-generated method stub
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    MyLog.i(TAG, "定时器3=TimerThreeIsStop = $TimerThreeIsStop")
                    // 添加更新ui的代码
                    if (!TimerThreeIsStop) {
                        sendReplacment()
                        sendEmptyMessageDelayed(1, ReplacementDelayTime.toLong())
                    }
                }
                0 -> {
                }
            }
        }
    }

    private fun TimerThreeStart() {
        isReplacement = true
        NowReplacement = 0
        MyLog.i(TAG, "定时器3=打开")
        TimerThreeHandler.sendEmptyMessage(1)
        TimerThreeIsStop = false
    }

    private fun TimerThreeStop() {
        MyLog.i(TAG, "定时器3=关闭")
        TimerThreeHandler.sendEmptyMessage(0)
        TimerThreeIsStop = true
    }


    //======== 第4个定时器====================判断超时-总超时
    private val HeadDelayFourTime = 1000
    private var FourNowCount = 0
    private val FourMaxCount = 10

    private var TimerFourIsStop = false
    private val TimerFourHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) { // TODO Auto-generated method stub
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    MyLog.i(TAG, "定时器4=TimerFourIsStop = $TimerFourIsStop")
                    // 添加更新ui的代码
                    if (!TimerFourIsStop) {
                        MyLog.i(TAG, "定时器4 FourNowCount = $FourNowCount")
                        if (FourNowCount > FourMaxCount) {
                            TimerTwoStop()
                            TimerFourStop()
                            TimerThreeStop()
                            DialMarketManager.getInstance().uploadDialDownloadRecording(themeDetails.dialId, DialMarketManager.uploadDialDownloadRecordingType4_error, context)
                            handFailState(false)
                        }
                        FourNowCount++
                        sendEmptyMessageDelayed(1, HeadDelayFourTime.toLong())
                    }
                }
                0 -> {
                }
            }
        }
    }

    private fun TimerFourStart() {
        MyLog.i(TAG, "定时器4=打开")
        FourNowCount = 0
        TimerFourIsStop = false
        TimerFourHandler.sendEmptyMessage(1)
    }

    private fun TimerFourStop() {
        MyLog.i(TAG, "定时器4=关闭")
        FourNowCount = 0
        TimerFourIsStop = true
        TimerFourHandler.sendEmptyMessage(0)
    }


    //======== 第5个定时器====================判断超时-补发定时器
    private val HeadDelayFiveTime = 500
    private var FiveNowCount = 0
    private val FiveMaxCount = 4

    private var TimerFiveIsStop = false
    private val TimerFiveHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) { // TODO Auto-generated method stub
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    MyLog.i(TAG, "定时器5=TimerFiveIsStop = $TimerFiveIsStop")
                    // 添加更新ui的代码
                    if (!TimerFiveIsStop) {
                        MyLog.i(TAG, "定时器5 FiveNowCount = $FiveNowCount")
                        if (FiveNowCount > FiveMaxCount) {
                            TimerFiveStop()
                            MyLog.i(TAG, "定时器5 检查补发数据是否发完")
                            checkReplacmentData()
                        }
                        FiveNowCount++
                        sendEmptyMessageDelayed(1, HeadDelayFiveTime.toLong())
                    }
                }
                0 -> {
                }
            }
        }
    }

    private fun TimerFiveStart() {
        MyLog.i(TAG, "定时器5=打开")
        FiveNowCount = 0
        TimerFiveIsStop = false
        TimerFiveHandler.sendEmptyMessage(1)
    }

    private fun TimerFiveStop() {
        MyLog.i(TAG, "定时器5=关闭")
        FiveNowCount = 0
        TimerFiveIsStop = true
        TimerFiveHandler.sendEmptyMessage(0)
    }


    private var loading_dialog: AlertDialog? = null
    private var send_loading_progressbar: ProgressBar? = null
    private var rl_theme_type1: RelativeLayout? = null
    private var rl_theme_type2: RelativeLayout? = null
    private var send_loading_img_bg_type1: ImageView? = null
    private lateinit var send_loading_img_text_type1: android.widget.ImageView
    private var send_loading_img_bg_type2: ImageView? = null
    private lateinit var send_loading_img_text_type2: android.widget.ImageView
    private lateinit var send_loading_cover_img_type2: android.widget.ImageView
    private var end_loading_text: TextView? = null

    var UiType = 2 //1=长方形3列/=2正方形2列

    //发送数据提示框
    private fun initLoadingdialog() {
        val builder = AlertDialog.Builder(this)
        val view: View = layoutInflater.inflate(R.layout.dialog_send_loading_view, null) as LinearLayout
        send_loading_progressbar = view.findViewById<View>(R.id.send_loading_progressbar) as ProgressBar
        end_loading_text = view.findViewById<View>(R.id.end_loading_text) as TextView
        rl_theme_type1 = view.findViewById<View>(R.id.rl_theme_type1) as RelativeLayout
        rl_theme_type2 = view.findViewById<View>(R.id.rl_theme_type2) as RelativeLayout
        send_loading_img_bg_type1 = view.findViewById<View>(R.id.send_loading_img_bg_type1) as ImageView
        send_loading_img_text_type1 = view.findViewById<View>(R.id.send_loading_img_text_type1) as ImageView
        send_loading_img_bg_type2 = view.findViewById<View>(R.id.send_loading_img_bg_type2) as ImageView
        send_loading_img_text_type2 = view.findViewById<View>(R.id.send_loading_img_text_type2) as ImageView
        send_loading_cover_img_type2 = view.findViewById<View>(R.id.send_loading_cover_img_type2) as ImageView
        if (mBleDeviceTools.isSupportProtobuf && mBleDeviceTools.deviceUpdateType) {
        } else {
            send_loading_progressbar!!.max = mThemeModle.getTotalPageSize()
        }
        if (mBleDeviceTools._device_theme_shape == 2) {
//            send_loading_cover_img_type2.visibility = View.VISIBLE
        } else {
            send_loading_cover_img_type2.visibility = View.GONE
        }
        if (UiType == 1) {
            rl_theme_type1!!.visibility = View.VISIBLE
            rl_theme_type2!!.visibility = View.GONE
            if (isCustom) {
                send_loading_img_bg_type1!!.setImageBitmap(newBgBitmap)
                send_loading_img_text_type1.setImageBitmap(newTextBitmap)
            } else {
                if (mainBitmap == null) {
                    bitmapUtils.display(send_loading_img_bg_type1, dialInfo.effectImgUrl)
                } else {
                    send_loading_img_bg_type1!!.setImageBitmap(mainBitmap)
                }
            }
        } else {
            rl_theme_type1!!.visibility = View.GONE
            rl_theme_type2!!.visibility = View.VISIBLE
            if (isCustom) {
                send_loading_img_bg_type2!!.setImageBitmap(newBgBitmap)
                send_loading_img_text_type2.setImageBitmap(newTextBitmap)
            } else {
                if (mainBitmap == null) {
                    bitmapUtils.display(send_loading_img_bg_type2, dialInfo.effectImgUrl)
                } else {
                    send_loading_img_bg_type2!!.setImageBitmap(mainBitmap)
                }
            }
        }
        builder.setView(view)
//        builder.setTitle(getString(R.string.send_loading))
        loading_dialog = builder.show()
        loading_dialog?.setCancelable(false)
    }

    //==================成功处理
    private fun handSuccessState() {
        is_send_data = false
        TimerTwoStop()
        TimerThreeStop()
        TimerFourStop()
        TimerFiveStop()
        if (loading_dialog != null && loading_dialog!!.isShowing) {
            loading_dialog!!.dismiss()
            Toast.makeText(this, getText(R.string.send_success), Toast.LENGTH_SHORT).show()
        }
        DialMarketManager.getInstance().uploadDialDownloadRecording(themeDetails.dialId, DialMarketManager.uploadDialDownloadRecordingType3_success, this)
        finish()
    }

    //==================失败处理
    private fun handFailState(is_finish: Boolean) {
        is_send_data = false
        waitDialog!!.close()
        TimerTwoStop()
        TimerThreeStop()
        TimerFourStop()
        TimerFiveStop()
        if (is_finish) {
            AppUtils.showToast(context, R.string.no_connection_notification)
            if (loading_dialog != null && loading_dialog!!.isShowing) {
                loading_dialog!!.dismiss()
            }
            finish()
        } else {
            AppUtils.showToast(context, R.string.send_fail)
            if (loading_dialog != null && loading_dialog!!.isShowing) {
                loading_dialog!!.dismiss()
            }
        }
    }

    //==================失败处理
    private fun handFailStateFialCode(fail_code: Int) {
        is_send_data = false
        waitDialog!!.close()
        TimerTwoStop()
        TimerThreeStop()
        TimerFourStop()
        TimerFiveStop()
        if (loading_dialog != null && loading_dialog!!.isShowing) {
            loading_dialog!!.dismiss()
        }
        if (!is_send_fial) {
            if (fail_code == 1) {
                AppUtils.showToast(context, R.string.send_imge_error_low_power)
            } else {
                AppUtils.showToast(context, R.string.send_fail)
            }
        }
        is_send_fial = true
    }

    private fun initBroadcast() {
        val filter = IntentFilter()
        filter.addAction(BroadcastTools.ACTION_GATT_DISCONNECTED)
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_READY)
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_BLOCK_END)
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_REPAIR_HEAD)
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_RESPONSE_SN)
        filter.addAction(BroadcastTools.ACTION_THEME_THEME_RESULT_SUCCESS_SN)
        filter.addAction(BroadcastTools.ACTION_THEME_SUSPENSION_FAIL)
        filter.addAction(BroadcastTools.ACTION_THEME_SUSPENSION_INTERVAL)
        filter.priority = 1000
        registerReceiver(broadcastReceiverTheme, filter)

    }

    /**
     * 广播监听
     */
    private val broadcastReceiverTheme: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("NewApi")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BroadcastTools.ACTION_GATT_DISCONNECTED -> {
                    MyLog.i(TAG, "主题数据 蓝牙断开了")
                    handFailState(true)
                }
                BroadcastTools.ACTION_THEME_RECEIVE_READY -> {
                    MyLog.i(TAG, "主题数据 接收广播 准备就绪")
                    BleReady()
                }
                BroadcastTools.ACTION_THEME_RECEIVE_BLOCK_END -> {
                    MyLog.i(TAG, "主题数据 接收广播 = 当前块结束")
                    FourNowCount = 0
                    sendDataToNor()
                }
                BroadcastTools.ACTION_THEME_RECEIVE_REPAIR_HEAD -> MyLog.i(TAG, "主题数据 接收广播 = 补发头")
                BroadcastTools.ACTION_THEME_RECEIVE_RESPONSE_SN -> {
                    MyLog.i(TAG, "主题数据 接收广播 = 补发数据")
                    val budle = intent.extras
                    replacmentSnList1 = budle!!.getIntegerArrayList(BroadcastTools.INTENT_PUT_THEME_REPONSE_SN_NUM_LIST)
                    replacmentSnList2 = ArrayList(replacmentSnList1!!)
                    MyLog.i(TAG, "主题数据 接收广播 = 补发数据 replacmentSnList1 = $replacmentSnList1")
                    FourNowCount = 0
                    TimerThreeStart()
                }
                BroadcastTools.ACTION_THEME_THEME_RESULT_SUCCESS_SN -> {
                    MyLog.i(TAG, "主题数据 接收广播 = 补发完成的SN")
                    val budle2 = intent.extras
                    val reslt_sn = budle2!!.getInt(BroadcastTools.INTENT_PUT_THEME_RESULT_SUCCESS_SN)
                    replacmentSnList2!!.remove(reslt_sn as Any)
                    MyLog.i(TAG, "主题数据 接收广播 = 补发完成的SN reslt_sn = $reslt_sn")
                    MyLog.i(TAG, "主题数据 接收广播 = 补发完成的SN replacmentSnList2 = " + replacmentSnList2.toString())
                    val FiveNowCount = 0
                    //不需要第二轮补发提前结束
                    if (replacmentSnList2!!.size == 0) {
                        MyLog.i(TAG, "主题数据 传输 不需要第二轮补发")
                        MyLog.i(TAG, "补发2 传输状态 = $is_send_data")
                        if (is_send_data) {
                            sendBlockVerfication()
                        }
                        TimerFiveStop()
                    } else {
                        MyLog.i(TAG, "主题数据 传输 需要补发=等待定时器执行")
                    }
                }
                BroadcastTools.ACTION_THEME_SUSPENSION_FAIL -> {
                    MyLog.i(TAG, "主题数据 接收广播 发送失败-中断")
                    val budle3 = intent.extras
                    val fail_code = budle3!!.getInt(BroadcastTools.INTENT_PUT_SUSPENSION_FAIL_FIAL_CODE)
                    MyLog.i(TAG, "主题数据 接收广播 发送失败-中断 fail_code = $fail_code")
                    handFailStateFialCode(fail_code)
                }
                BroadcastTools.ACTION_THEME_SUSPENSION_INTERVAL -> {
                    MyLog.i(TAG, "主题数据 接收广播 连接间隔")
                    val budle4 = intent.extras
                    val interval_code = budle4!!.getInt(BroadcastTools.INTENT_PUT_SUSPENSION_INTERVAL_INTERVAL_CODE)
                    MyLog.i(TAG, "主题数据 接收广播 连接间隔 interval_code = $interval_code")
                    if (interval_code == 1) {
                        DialMarketManager.getInstance().uploadDialDownloadRecording(themeDetails.dialId, DialMarketManager.uploadDialDownloadRecordingType4_error, context)
                        handFailState(false)
                    } else if (interval_code == 2) {
                        FourNowCount = 0
                    }
                }
            }
        }
    }

    private fun showImgDialog() {
        val view = layoutInflater.inflate(R.layout.photo_choose_dialog, null)
        dialog = Dialog(context, R.style.transparentFrameWindowStyle)
        dialog.setContentView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        val window = dialog.window
        // 设置显示动画
        window!!.setWindowAnimations(R.style.main_menu_animstyle)
        val wl = window.attributes
        wl.x = 0
        wl.y = windowManager.defaultDisplay.height
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT
        view.findViewById<View>(R.id.photograph).setOnClickListener {
            dialog.dismiss()
            if (AuthorityManagement.verifyStoragePermissions(this)) {
                MyLog.i(TAG, "SD卡权限 已获取")
            } else {
                MyLog.i(TAG, "SD卡权限 未获取")
            }
            if (AuthorityManagement.verifyPhotogrAuthority(this)) {
                MyLog.i(TAG, "拍照权限 已获取")
                takingPictures()
            } else {
                MyLog.i(TAG, "拍照权限 未获取")
            }
        }
        view.findViewById<View>(R.id.albums).setOnClickListener {
            dialog.dismiss()
            PhotoAlbum()
        }
        view.findViewById<View>(R.id.cancel).setOnClickListener { dialog.dismiss() }
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl)
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    fun takingPictures() {
        val mIntent = Intent()
        mIntent.action = MediaStore.ACTION_IMAGE_CAPTURE
        mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(Constants.HEAD_IMG, imageName)))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0)
        }
        startActivityForResult(mIntent, Constants.TakingTag)
    }

    /**
     * 相册
     */
    fun PhotoAlbum() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(intent, Constants.PhotoTag)
    }

    //拍照相关
    private var imageUri: Uri? = null
    private var tempFile: File? = null
    private val imageName = "theme.png"
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constants.PhotoTag -> {
                MyLog.i(TAG, "回调 裁剪图片")
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        val fileSrc: String?
                        if ("file" == data!!.data!!.scheme) { // 有些低版本机型返回的Uri模式为file
                            fileSrc = data.data!!.path
                        } else { // Uri模型为content
                            val proj = arrayOf(MediaStore.Images.Media.DATA)
                            val cursor = contentResolver.query(data.data!!, proj, null, null, null)
                            cursor!!.moveToFirst()
                            val idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                            fileSrc = cursor.getString(idx)
                            cursor.close()
                        }
                        // 跳转到图片裁剪页面
                        startCropIntent(Uri.fromFile(File(fileSrc)))
                    } catch (e: Exception) {
                        startCropIntent(data!!.data!!)
                    }
                }
            }
            Constants.TakingTag -> {
                MyLog.i(TAG, "回调 拍摄照片")
                if (resultCode == Activity.RESULT_OK) {
                    tempFile = File(Constants.HEAD_IMG + imageName)
                    try {
                        startCropIntent(Uri.fromFile(tempFile))
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
            Constants.TailoringResult -> {
                MyLog.i(TAG, "回调 裁剪完成 imageUri = $imageUri")
                if (resultCode == Activity.RESULT_OK) {
                    val themePicture = File(Constants.HEAD_IMG + imageName)
                    if (themePicture.exists()) {
                        themePicture.delete()
                    }
                    if (imageUri != null) {
                        val bitmap = BmpUtils.decodeUriAsBitmap(context, imageUri)
                        //                    setPicToView(bitmap);// 保存在SD卡中
                        handleBitmap(bitmap)
                    }
                }
                try {
                    val themeTemp = File(Constants.IMAGE_FILE_LOCATION + File.separator + Constants.IMAGE_FILE_LOCATION_TEMP)
                    themeTemp.delete()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 裁剪图片
     *
     * @param uri
     * @throws FileNotFoundException
     */
    private fun startCropIntent(uri: Uri) {
        SysUtils.makeFilePath(Constants.IMAGE_FILE_LOCATION, Constants.IMAGE_FILE_LOCATION_TEMP)
        imageUri = Uri.fromFile(File(Constants.IMAGE_FILE_LOCATION + File.separator + Constants.IMAGE_FILE_LOCATION_TEMP))
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")
        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", mBleDeviceTools._device_theme_resolving_power_width)
        intent.putExtra("aspectY", mBleDeviceTools._device_theme_resolving_power_height)
        intent.putExtra("outputX", mBleDeviceTools._device_theme_resolving_power_width)
        intent.putExtra("outputY", mBleDeviceTools._device_theme_resolving_power_height)
        intent.putExtra("return-data", false)
        // 上面设为false的时候将MediaStore.EXTRA_OUTPUT关联一个Uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        //        intent.putExtra("noFaceDetection", true);
        intent.putExtra("circleCrop", false)
        //        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString())
        startActivityForResult(intent, Constants.TailoringResult)
    }

    fun handleBitmap(my_bitmap: Bitmap?) {
        if (my_bitmap == null) {
            MyLog.i(TAG, "处理图片 my_bitmap  = null")
            return
        } else {
            MyLog.i(TAG, "处理图片 my_bitmap  = $my_bitmap")
        }
        MyLog.i(TAG, "Bitmap 宽度 = " + my_bitmap.width)
        MyLog.i(TAG, "Bitmap 高度 = " + my_bitmap.height)
        if (my_bitmap.width == mBleDeviceTools._device_theme_resolving_power_width && my_bitmap.height == mBleDeviceTools._device_theme_resolving_power_height) {
            newBgBitmap = my_bitmap
            ivThemeMain.setImageBitmap(newBgBitmap) // 用ImageView显示出来
            setColor(color_r, color_g, color_b)
        } else {
            newBgBitmap = oldBgBitmap
            ivThemeMain.setImageBitmap(newBgBitmap)
            AppUtils.showToast(context, R.string.sned_img_size_error)
        }
    }

    override fun onDestroy() {
        waitDialog!!.dismiss()
        EventTools.SafeUnregisterEventBus(this)
        unregisterReceiver(broadcastReceiver)
        unregisterReceiver(broadcastReceiverTheme)
        TimerTwoHandler?.removeCallbacksAndMessages(null)
        TimerThreeHandler?.removeCallbacksAndMessages(null)
        TimerFourHandler?.removeCallbacksAndMessages(null)
        TimerFiveHandler?.removeCallbacksAndMessages(null)
        protoHandler?.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            AuthorityManagement.REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.i(TAG, "SD卡权限 回调允许")
                } else {
                    MyLog.i(TAG, "SD卡权限 回调拒绝")
                    showSettingDialog(getString(R.string.setting_dialog_storage))
                }
            }
            AuthorityManagement.REQUEST_EXTERNAL_CALL_CAMERA -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MyLog.i(TAG, "拍照权限 回调允许")
                //                    TakingPictures();
            } else {
                MyLog.i(TAG, "拍照权限 回调拒绝")
                showSettingDialog(getString(R.string.setting_dialog_call_camera))
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun showSettingDialog(title: String?) {
        DialogUtils.showBaseDialog(this, this.getResources().getString(R.string.dialog_prompt), title, this.getDrawable(R.drawable.black_corner_bg), object : DialogClickListener {
            override fun OnOK() {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }

            override fun OnCancel() {}
        }, true, false, resources.getString(R.string.setting_dialog_setting))
    }
}
