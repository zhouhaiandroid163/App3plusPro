package com.zjw.apps3pluspro.module.device.clockdial;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.lidroid.xutils.BitmapUtils;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.adapter.ClockDialMarketListAdapter;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.device.entity.ThemeModle;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.ThemeBean;
import com.zjw.apps3pluspro.network.javabean.ThemeFileBean;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.bleservice.UpdateInfoService;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.DialogUtils;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.AuthorityManagement;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.ThemeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 表盘市场
 */

public class MyThemeActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = MyThemeActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    private WaitDialog waitDialog;
    private GridView gr_my_theme;
    private ClockDialMarketListAdapter mClockDialMarketListAdapter;
    //    private Bitmap nowImgBitmap = null;
    private String nowImgUrl = "";
    private Handler myHandler;
    private ThemeModle mThemeModle;
    //        private int UiType = 1;//长条3列
//    private int UiType = 2;//正方2列
    private int UiType = 2;

    private String CLOCK_FILE_NAME = "";
    private String CLOCK_THEME_MD5 = "";
    private String CLOCK_FILE_URL = "";

    int device_width = 0;
    int device_height = 0;
    int device_shape = 0;
    int it_bin_size = 0;
    boolean device_is_heart;
    boolean is_scanf_type_is_ver = false;

    boolean is_send_data = false;
    boolean is_send_fial = false;

    //模拟丢包
    int testPassDataValue = 0;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_my_theme;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = MyThemeActivity.this;
        myHandler = new Handler();
        waitDialog = new WaitDialog(mContext);
        initBroadcast();
        initThemeSet();
        initView();
        initSetAdapter();
        updateUI();
        getPageList();
        enableNotifacationThemeRead();
    }

    void initThemeSet() {
        device_width = mBleDeviceTools.get_device_theme_resolving_power_width();
        device_height = mBleDeviceTools.get_device_theme_resolving_power_height();
        device_shape = mBleDeviceTools.get_device_theme_shape();
        it_bin_size = mBleDeviceTools.get_device_theme_available_space();
        device_is_heart = mBleDeviceTools.get_device_theme_is_support_heart();
        is_scanf_type_is_ver = mBleDeviceTools.get_device_theme_scanning_mode();

        //模拟测试
//        device_width = 128;
//        device_height = 220;
//        device_shape = 0;
//        it_bin_size = 800;
//        device_is_heart = false;

//        device_width = 240;
//        device_height = 240;
//        device_shape = 2;
//        it_bin_size = 800;
//        device_is_heart = true;


        if (device_width == 128 && device_height == 220) {
            UiType = 1;
        } else {
            UiType = 2;
        }
    }

    void initSetAdapter() {
        mClockDialMarketListAdapter = new ClockDialMarketListAdapter(mContext, UiType);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 初始化广播
     */
    private void initBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastTools.ACTION_GATT_DISCONNECTED);
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_READY);
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_BLOCK_END);
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_REPAIR_HEAD);
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_RESPONSE_SN);
        filter.addAction(BroadcastTools.ACTION_THEME_THEME_RESULT_SUCCESS_SN);
        filter.addAction(BroadcastTools.ACTION_DOWN_CLOCK_FILE_STATE_SUCCESS);
        filter.addAction(BroadcastTools.ACTION_THEME_SUSPENSION_FAIL);
        filter.addAction(BroadcastTools.ACTION_THEME_SUSPENSION_INTERVAL);
        filter.setPriority(1000);
        registerReceiver(broadcastReceiver, filter);
    }


    protected void onDestroy() {
        if (broadcastReceiver != null) {
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {
            }
        }

        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
        }

        if (TimerTwoHandler != null) {
            TimerTwoHandler.removeCallbacksAndMessages(null);
        }

        if (TimerThreeHandler != null) {
            TimerThreeHandler.removeCallbacksAndMessages(null);
        }

        if (TimerFourHandler != null) {
            TimerFourHandler.removeCallbacksAndMessages(null);
        }

        if (TimerFiveHandler != null) {
            TimerFiveHandler.removeCallbacksAndMessages(null);
        }

        super.onDestroy();
    }

    /**
     * 广播监听
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @SuppressWarnings({"unused", "unused"})
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case BroadcastTools.ACTION_GATT_DISCONNECTED:
                    MyLog.i(TAG, "主题数据 蓝牙断开了");
                    handFailState(true);
                    break;

                //准备就绪
                case BroadcastTools.ACTION_THEME_RECEIVE_READY:
                    MyLog.i(TAG, "主题数据 接收广播 准备就绪");
                    BleReady();
                    break;

                case BroadcastTools.ACTION_THEME_RECEIVE_BLOCK_END:
                    MyLog.i(TAG, "主题数据 接收广播 = 当前块结束");
                    FourNowCount = 0;
                    sendDataToNor();
                    break;

                case BroadcastTools.ACTION_THEME_RECEIVE_REPAIR_HEAD:
                    MyLog.i(TAG, "主题数据 接收广播 = 补发头");
                    break;

                case BroadcastTools.ACTION_THEME_RECEIVE_RESPONSE_SN:
                    MyLog.i(TAG, "主题数据 接收广播 = 补发数据");

                    Bundle budle = intent.getExtras();
                    replacmentSnList1 = budle.getIntegerArrayList(BroadcastTools.INTENT_PUT_THEME_REPONSE_SN_NUM_LIST);
                    replacmentSnList2 = new ArrayList<>(replacmentSnList1);
                    MyLog.i(TAG, "主题数据 接收广播 = 补发数据 replacmentSnList1 = " + replacmentSnList1);

                    FourNowCount = 0;

                    TimerThreeStart();

                    break;

                case BroadcastTools.ACTION_THEME_THEME_RESULT_SUCCESS_SN:
                    MyLog.i(TAG, "主题数据 接收广播 = 补发完成的SN");
                    Bundle budle2 = intent.getExtras();
                    int reslt_sn = budle2.getInt(BroadcastTools.INTENT_PUT_THEME_RESULT_SUCCESS_SN);
                    replacmentSnList2.remove((Object) (reslt_sn));

                    MyLog.i(TAG, "主题数据 接收广播 = 补发完成的SN reslt_sn = " + reslt_sn);
                    MyLog.i(TAG, "主题数据 接收广播 = 补发完成的SN replacmentSnList2 = " + replacmentSnList2.toString());

                    int FiveNowCount = 0;

                    //不需要第二轮补发提前结束
                    if (replacmentSnList2.size() == 0) {
                        MyLog.i(TAG, "主题数据 传输 不需要第二轮补发");
                        MyLog.i(TAG, "补发2 传输状态 = " + is_send_data);
                        if (is_send_data) {
                            sendBlockVerfication();
                        }
                        TimerFiveStop();
                    } else {
                        MyLog.i(TAG, "主题数据 传输 需要补发=等待定时器执行");
                    }
                    break;

                case BroadcastTools.ACTION_DOWN_CLOCK_FILE_STATE_SUCCESS:
                    if (!ThemeUtils.checkFileExistenceByMd5(Constants.DOWN_THEME_FILE, CLOCK_FILE_NAME, CLOCK_THEME_MD5)) {
                        MyLog.i(TAG, "数据大小 Bytes = 文件不存在");
//                        new UpdateInfoService(MyThemeActivity.this).downLoadNewFile(CLOCK_FILE_URL, CLOCK_FILE_NAME, Constants.DOWN_THEME_FILE);
                    } else {
                        startSendThemeData(mBleDeviceTools, ThemeUtils.getBytes(Constants.DOWN_THEME_FILE + CLOCK_FILE_NAME));
                    }
                    break;

                case BroadcastTools.ACTION_THEME_SUSPENSION_FAIL:
                    MyLog.i(TAG, "主题数据 接收广播 发送失败-中断");

                    Bundle budle3 = intent.getExtras();
                    int fail_code = budle3.getInt(BroadcastTools.INTENT_PUT_SUSPENSION_FAIL_FIAL_CODE);

                    MyLog.i(TAG, "主题数据 接收广播 发送失败-中断 fail_code = " + fail_code);
                    handFailStateFialCode(fail_code);
                    break;


                case BroadcastTools.ACTION_THEME_SUSPENSION_INTERVAL:
                    MyLog.i(TAG, "主题数据 接收广播 连接间隔");

                    Bundle budle4 = intent.getExtras();
                    int interval_code = budle4.getInt(BroadcastTools.INTENT_PUT_SUSPENSION_INTERVAL_INTERVAL_CODE);

                    MyLog.i(TAG, "主题数据 接收广播 连接间隔 interval_code = " + interval_code);

                    if (interval_code == 1) {
                        handFailState(false);
                    } else if (interval_code == 2) {
                        FourNowCount = 0;
                    }

                    break;

            }
        }
    };


    void initView() {

        findViewById(R.id.public_head_back).setOnClickListener(this);

        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.my_theme_title));

        gr_my_theme = (GridView) findViewById(R.id.gr_my_theme);
        gr_my_theme.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                ThemeBean.DataBean.ListBean my_data = mClockDialMarketListAdapter.getDevice(arg2);

                if (my_data != null) {
                    MyLog.i(TAG, "pos = " + arg2 + "  my_data = " + my_data.toString());
                    MyLog.i(TAG, "pos = " + arg2 + "  名称 = " + my_data.getAuthorName());
                    MyLog.i(TAG, "pos = " + arg2 + "  开始传输 ");
//                    getThemeFile(my_data.getId(), "ver_0");
                    nowImgUrl = my_data.getThemeImgUrl();

                    if (is_scanf_type_is_ver) {
                        getThemeFile(my_data.getId(), "ver.bin");
                    } else {
                        getThemeFile(my_data.getId(), "hor.bin");
                    }


                }
            }
        });


        if (UiType == 1) {
            gr_my_theme.setNumColumns(3);
        } else {
            gr_my_theme.setNumColumns(2);
        }
    }


    void initData(List<ThemeBean.DataBean.ListBean> my_theme_list) {
        mClockDialMarketListAdapter.setDeviceList(my_theme_list);
        mClockDialMarketListAdapter.notifyDataSetChanged();
        gr_my_theme.setAdapter(mClockDialMarketListAdapter);
    }

    void updateUI() {

    }


    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.public_head_back:
                finish();
                break;

        }
    }


    /**
     * 获取主题列表
     */
    private void getPageList() {

        waitDialog.show(getString(R.string.loading0));


//        if (UiType == 1) {
//            device_width = 128;
//            device_height = 220;
//            device_shape = 0;
//        } else {
//            device_width = 240;
//            device_height = 240;
//            device_shape = 2;
//        }

//        boolean device_is_heart = true;
//        it_bin_size = 600;

        RequestInfo mRequestInfo = RequestJson.getThemePageList(device_width, device_height, device_shape, device_is_heart, it_bin_size);

        MyLog.i(TAG, "请求接口-获取主题列表 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-获取主题列表  result = " + result.toString());

                        ThemeBean mThemeBean = ResultJson.ThemeBean(result);


                        //请求成功
                        if (mThemeBean.isRequestSuccess()) {
                            if (mThemeBean.isUserSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取主题列表 成功");
//                                AppUtils.showToast(mContext, R.string.save_ok);

                                initData(mThemeBean.getData().getList());

                            } else if (mThemeBean.isUserSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取主题列表 无数据");
                                AppUtils.showToast(mContext, R.string.no_data);
                            } else {
                                MyLog.i(TAG, "请求接口-获取主题列表 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取主题列表 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        waitDialog.close();
                        MyLog.i(TAG, "请求接口-获取主题列表 请求失败 = message = " + arg0.getMessage());
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);
                        return;
                    }
                });
    }


    /**
     * 获取主题数据
     */
    private void getThemeFile(int theme_id, String bin_name) {

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.getThemeFile(theme_id, bin_name);
//        RequestInfo mRequestInfo = RequestJson.getThemeFile(5, "0");

        MyLog.i(TAG, "请求接口-获取主题数据 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-获取主题数据  result = " + result.toString());

                        ThemeFileBean mThemeFileBean = ResultJson.ThemeFileBean(result);
                        //请求成功
                        if (mThemeFileBean.isRequestSuccess()) {
                            if (mThemeFileBean.isUserSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取主题数据 成功");
//                                AppUtils.showToast(mContext, R.string.save_ok);

                                if (mThemeFileBean.getData().getBinFileName() != null
                                        && mThemeFileBean.getData().getThemeFileUrl() != null
                                        && mThemeFileBean.getData().getMd5Value() != null
                                        && mThemeFileBean.getData().getThemeId() > 0
                                ) {

                                    downThemeFile(mBleDeviceTools, mThemeFileBean.getData());

                                }

                            } else if (mThemeFileBean.isUserSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取主题数据 无数据");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else {
                                MyLog.i(TAG, "请求接口-获取主题数据 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取主题数据 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        waitDialog.close();
                        MyLog.i(TAG, "请求接口-获取主题数据 请求失败 = message = " + arg0.getMessage());
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);
                        return;
                    }
                });
    }


    //===============传输相关===========


    void startSendThemeData(BleDeviceTools mBleDeviceTools, byte[] bytes) {

        if (mBleDeviceTools.get_device_is_theme_transmission()) {
            if (HomeActivity.ISBlueToothConnect()) {
                if (mBleDeviceTools.get_ble_device_power() >= 50) {
                    sendDataToBle(mBleDeviceTools, bytes);
                } else {
                    AppUtils.showToast(mContext, R.string.send_imge_error_low_power);
                }
            } else {
                AppUtils.showToast(mContext, R.string.no_connection_notification);
            }
        }
    }


    /**
     * 发送蓝牙数据
     */
    void sendDataToBle(BleDeviceTools mBleDeviceTools, byte[] bytes) {

//        mThemeModle = new ThemeModle(bytes, 182);
//        mThemeModle = new ThemeModle(bytes, 240);
        MyLog.i(TAG, "主题传输发送 MTU = " + mBleDeviceTools.get_device_mtu_num());
        mThemeModle = new ThemeModle(bytes, mBleDeviceTools.get_device_mtu_num());

        MyLog.i(TAG, "发送数据 服务不为空");

        waitDialog.show(getString(R.string.loading0));

        startSendData();
        AppsendHead();
        TimerFourStart();
//            TimerOneStart();
    }

    //当前传输的块
    int NowBlock = 0;
    //当前块的第几个包？
    int NowPage = 0;
    int SnNum = 0;
    //当前包的大小
    int NowPageNumber = 0;

    //补发总包数
    int ReplacementMax = 0;
    //当前补发的
    int NowReplacement = 0;


    //当前是否是补发状态
    boolean isReplacement = false;

    //开始传输
    void startSendData() {
        NowBlock = 1;
        NowPage = 0;
        SnNum = 0;
        NowPageNumber = 0;
        NowReplacement = 0;
        isReplacement = false;
        is_send_fial = false;
    }


    //设备准备就绪
    void BleReady() {
        is_send_data = true;
        is_send_fial = false;
        waitDialog.close();
        initLoadingdialog();
        TimerTwoStart();
        FourNowCount = 0;
    }

    void sendDataToNor() {

        MyLog.i(TAG, "正常发1 传输状态 = " + is_send_data);

        if (is_send_data) {

            //不补发
            isReplacement = false;

            //传输结束
            if (mThemeModle.isLastBlock(NowBlock)) {
                MyLog.i(TAG, "传输结束");
                handSuccessState();
            }
            //响应不补发-需要发送下一块
            else {
                MyLog.i(TAG, "响应不补发-需要发送下一块");
                NowBlock++;
                NowPage = 0;
                TimerTwoStart();
            }
        }


    }


    //======== 第1个定时器====================
    //发送头
    void AppsendHead() {
        MyLog.i(TAG, "发送头");
        MyLog.i(TAG, "发送头 getPageDataMax = " + mThemeModle.getPageDataMax());
        MyLog.i(TAG, "发送头 getTotalPageSize = " + mThemeModle.getTotalPageSize());

        //模拟丢包，清零
        testPassDataValue = 0;

        sendThemeHead(mThemeModle);

    }

    //======== 第2个定时器====================发送数据-正常数据
//    int CheckDelayTime = 1000*10;
//    int SendDelayTime = 1000*10;
    int CheckDelayTime = 10;
    int CheckDelayTimeTwo = 50;
    //    int CheckDelayTimeThree = 100;
    int SendDelayTime = 10;

    void sendNorDataToBle() {


        byte[] send_data = mThemeModle.getSendData(NowBlock, NowPage, SnNum);
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
            sendThemeData(SnNum, send_data);
        } else {
            MyLog.i(TAG, "发送数据 数据为空 = " + testPassDataValue);
        }

    }

    //发送块校验
    void sendBlockVerfication() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyLog.i(TAG, "主题数据 传输 发送块校验1");
                sendThemeBlockVerfication();


                FourNowCount = 0;
            }
        }, CheckDelayTime);
    }


    //发送正常数据
    void sendNormalData() {

        testPassDataValue++;
        FourNowCount = 0;
        NowReplacement = 0;
        NowPage++;
        SnNum++;

        //最后一个包
        if (mThemeModle.isLastPage(SnNum)) {
//            MyLog.i(TAG, "主题数据 传输 是最后的包 NowPage = " + NowPage + " SnNum = " + SnNum);

            //最后一个正常数据发送完毕
            TimerTwoStop();
            sendNorDataToBle();
            //发送块校验
            sendBlockVerfication();

        } else {
//            MyLog.i(TAG, "主题数据 传输 不是最后的包 NowPage = " + NowPage + " SnNum = " + SnNum);

            //最后一个正常数据发送完毕
            if (mThemeModle.isNowBlockLastPage(NowPage)) {
                TimerTwoStop();
            }

            sendNorDataToBle();
            //最后一个正常数据发送完毕
            if (mThemeModle.isNowBlockLastPage(NowPage)) {
                sendBlockVerfication();
            }

        }

        send_loading_progressbar.setProgress(SnNum);

        String por_str = ThemeUtils.getPercentageStr(send_loading_progressbar.getMax(), SnNum);

//        MyLog.i(TAG, "表盘传输进度条 = por_str = " + por_str);

        end_loading_text.setText(por_str);
    }

    private boolean TimerTwoIsStop = false;
    private Handler TimerTwoHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

                    MyLog.i(TAG, "定时器2=TimerTwoIsStop = " + TimerTwoIsStop);

                    // 添加更新ui的代码
                    if (!TimerTwoIsStop) {
                        sendNormalData();
                        TimerTwoHandler.sendEmptyMessageDelayed(1, SendDelayTime);
                    }
                    break;
                case 0:
                    break;
            }
        }
    };

    private void TimerTwoStart() {
        MyLog.i(TAG, "定时器2=打开");
        TimerTwoHandler.sendEmptyMessage(1);
        TimerTwoIsStop = false;
    }

    private void TimerTwoStop() {
        MyLog.i(TAG, "定时器2=关闭");
        TimerTwoHandler.sendEmptyMessage(0);
        TimerTwoIsStop = true;
    }

    //======== 第3个定时器====================

    ArrayList<Integer> replacmentSnList1;
    ArrayList<Integer> replacmentSnList2;

    //补发数据间隔
    int ReplacementDelayTime = 10;

    void sendRepDataToBle(int sn_number) {

        MyLog.i(TAG, "发送数据 补发 = 编码 = sn_number " + sn_number + " getPageDataMax =" + mThemeModle.getPageDataMax());


        int now_page = sn_number % mThemeModle.getPageDataMax();

        if (now_page <= 0) {
            now_page = mThemeModle.getPageDataMax();
        }


        MyLog.i(TAG, "发送数据 补发 = 编码 = NowBlock " + NowBlock + " now_page =" + now_page + " sn_number = " + sn_number);

        byte[] send_data = mThemeModle.getSendData(NowBlock, now_page, sn_number);

        if (send_data != null) {
            MyLog.i(TAG, "发送数据 补发 = " + ThemeUtils.bytes2HexString3(send_data));
            sendThemeData(sn_number, send_data);
        } else {
            MyLog.i(TAG, "发送数据 数据为空 = " + testPassDataValue);
        }
    }


    //发送补发数据
    void sendReplacment() {

        MyLog.i(TAG, "补发1 传输状态 = " + is_send_data);

        if (is_send_data) {

            NowReplacement++;

            MyLog.i(TAG, "主题数据 传输 发送补发数据 NowReplacement = " + NowReplacement + "  data_sn = " + replacmentSnList1.get(NowReplacement - 1));

            sendRepDataToBle(replacmentSnList1.get(NowReplacement - 1));


            //最后一个数据
            if (NowReplacement >= replacmentSnList1.size()) {

                MyLog.i(TAG, "主题数据 传输 发送补发数据  最后一个数据");

                TimerThreeStop();
                TimerFiveStart();


            }
        } else {
            TimerFiveStop();
        }

    }

    void checkReplacmentData() {

        if (replacmentSnList2.size() == 0) {
            MyLog.i(TAG, "主题数据 传输 不需要第二轮补发");
            MyLog.i(TAG, "补发2 传输状态 = " + is_send_data);
            if (is_send_data) {
                sendBlockVerfication();
            }
        } else {
            MyLog.i(TAG, "补发3 传输状态 = " + is_send_data);
            if (is_send_data) {
                MyLog.i(TAG, "主题数据 传输 需要第二轮补发！！");
                replacmentSnList1 = new ArrayList<>(replacmentSnList2);
                TimerThreeStart();
            }
        }
    }

    private boolean TimerThreeIsStop = false;
    private Handler TimerThreeHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

                    MyLog.i(TAG, "定时器3=TimerThreeIsStop = " + TimerThreeIsStop);

                    // 添加更新ui的代码
                    if (!TimerThreeIsStop) {
                        sendReplacment();
                        TimerThreeHandler.sendEmptyMessageDelayed(1, ReplacementDelayTime);
                    }
                    break;
                case 0:
                    break;
            }
        }
    };

    private void TimerThreeStart() {
        isReplacement = true;
        NowReplacement = 0;
        MyLog.i(TAG, "定时器3=打开");
        TimerThreeHandler.sendEmptyMessage(1);
        TimerThreeIsStop = false;
    }

    private void TimerThreeStop() {
        MyLog.i(TAG, "定时器3=关闭");
        TimerThreeHandler.sendEmptyMessage(0);
        TimerThreeIsStop = true;
    }


    //======== 第4个定时器====================判断超时-总超时
    int HeadDelayFourTime = 1000;
    int FourNowCount = 0;
    int FourMaxCount = 10;

    private boolean TimerFourIsStop = false;
    private Handler TimerFourHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

                    MyLog.i(TAG, "定时器4=TimerFourIsStop = " + TimerFourIsStop);

                    // 添加更新ui的代码
                    if (!TimerFourIsStop) {

                        MyLog.i(TAG, "定时器4 FourNowCount = " + FourNowCount);

                        if (FourNowCount > FourMaxCount) {
                            TimerTwoStop();
                            TimerFourStop();
                            TimerThreeStop();
                            handFailState(false);
                        }
                        FourNowCount++;
                        TimerFourHandler.sendEmptyMessageDelayed(1, HeadDelayFourTime);
                    }
                    break;
                case 0:
                    break;
            }
        }
    };

    private void TimerFourStart() {
        MyLog.i(TAG, "定时器4=打开");
        FourNowCount = 0;
        TimerFourIsStop = false;
        TimerFourHandler.sendEmptyMessage(1);
    }

    private void TimerFourStop() {
        MyLog.i(TAG, "定时器4=关闭");
        FourNowCount = 0;
        TimerFourIsStop = true;
        TimerFourHandler.sendEmptyMessage(0);
    }


    //======== 第5个定时器====================判断超时-补发定时器
    int HeadDelayFiveTime = 500;
    int FiveNowCount = 0;
    int FiveMaxCount = 4;

    private boolean TimerFiveIsStop = false;
    private Handler TimerFiveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

                    MyLog.i(TAG, "定时器5=TimerFiveIsStop = " + TimerFiveIsStop);

                    // 添加更新ui的代码
                    if (!TimerFiveIsStop) {

                        MyLog.i(TAG, "定时器5 FiveNowCount = " + FiveNowCount);

                        if (FiveNowCount > FiveMaxCount) {
                            TimerFiveStop();
                            MyLog.i(TAG, "定时器5 检查补发数据是否发完");
                            checkReplacmentData();
                        }
                        FiveNowCount++;
                        TimerFiveHandler.sendEmptyMessageDelayed(1, HeadDelayFiveTime);
                    }
                    break;
                case 0:
                    break;
            }
        }
    };

    private void TimerFiveStart() {
        MyLog.i(TAG, "定时器5=打开");
        FiveNowCount = 0;
        TimerFiveIsStop = false;
        TimerFiveHandler.sendEmptyMessage(1);
    }

    private void TimerFiveStop() {
        MyLog.i(TAG, "定时器5=关闭");
        FiveNowCount = 0;
        TimerFiveIsStop = true;
        TimerFiveHandler.sendEmptyMessage(0);
    }

    private AlertDialog loading_dialog;
    private ProgressBar send_loading_progressbar;
    private RelativeLayout rl_theme_type1;
    private RelativeLayout rl_theme_type2;
    private ImageView send_loading_img_bg_type1, send_loading_img_text_type1;
    private ImageView send_loading_img_bg_type2, send_loading_img_text_type2, send_loading_cover_img_type2;
    private TextView end_loading_text;

    //发送数据提示框
    void initLoadingdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_send_loading_view, null);
        send_loading_progressbar = (ProgressBar) view.findViewById(R.id.send_loading_progressbar);
        end_loading_text = (TextView) view.findViewById(R.id.end_loading_text);
        rl_theme_type1 = (RelativeLayout) view.findViewById(R.id.rl_theme_type1);
        rl_theme_type2 = (RelativeLayout) view.findViewById(R.id.rl_theme_type2);
        send_loading_img_bg_type1 = (ImageView) view.findViewById(R.id.send_loading_img_bg_type1);
        send_loading_img_text_type1 = (ImageView) view.findViewById(R.id.send_loading_img_text_type1);
        send_loading_img_bg_type2 = (ImageView) view.findViewById(R.id.send_loading_img_bg_type2);
        send_loading_img_text_type2 = (ImageView) view.findViewById(R.id.send_loading_img_text_type2);
        send_loading_cover_img_type2 = (ImageView) view.findViewById(R.id.send_loading_cover_img_type2);
        send_loading_progressbar.setMax(mThemeModle.getTotalPageSize());

        if (mBleDeviceTools.get_device_theme_shape() == 2) {
            send_loading_cover_img_type2.setVisibility(View.VISIBLE);
        } else {
            send_loading_cover_img_type2.setVisibility(View.GONE);
        }

        if (UiType == 1) {
            rl_theme_type1.setVisibility(View.VISIBLE);
            rl_theme_type2.setVisibility(View.GONE);
//            if (isCustom) {
//                send_loading_img_bg_type1.setImageBitmap(NowBgBitmap);
//                send_loading_img_text_type1.setVisibility(View.VISIBLE);
//                send_loading_img_text_type1.setImageBitmap(NowTextBitmap);
//
//            } else {
            new BitmapUtils(mContext).display(send_loading_img_bg_type1, nowImgUrl);
            send_loading_img_text_type1.setVisibility(View.GONE);
            send_loading_img_text_type1.setImageBitmap(null);
//            }

        } else {
            rl_theme_type1.setVisibility(View.GONE);
            rl_theme_type2.setVisibility(View.VISIBLE);
//            if (isCustom) {
//                send_loading_img_bg_type2.setImageBitmap(NowBgBitmap);
//                send_loading_img_text_type2.setVisibility(View.VISIBLE);
//                send_loading_img_text_type2.setImageBitmap(NowTextBitmap);
//            } else {
            new BitmapUtils(mContext).display(send_loading_img_bg_type2, nowImgUrl);
            send_loading_img_text_type2.setVisibility(View.GONE);
            send_loading_img_text_type2.setImageBitmap(null);
//            }

        }

        builder.setView(view);
//        builder.setTitle(getString(R.string.send_loading));
        loading_dialog = builder.show();
        loading_dialog.setCancelable(false);

    }

    //=================升级相关=====================

    byte[] Bytes;

    private void downThemeFile(BleDeviceTools mBleDeviceTools, ThemeFileBean.DataBean mDataBean) {

//        String base64Data = mThemeBinBean.getData().getThemeFile();
//        MyLog.i(TAG, "请求接口-获取主题数据 成功 文件大小 = base64Data = " + base64Data.length());
//        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
//        startSendThemeData(bytes);

//        String url = mDataBean.getThemeFileUrl();
        int theme_id = mDataBean.getThemeId();
        CLOCK_THEME_MD5 = mDataBean.getMd5Value();
        CLOCK_FILE_NAME = String.valueOf(theme_id) + "_" + CLOCK_THEME_MD5;
        CLOCK_FILE_URL = mDataBean.getThemeFileUrl();


//        FileName = mDataBean.getSongName();

        MyLog.i(TAG, "下载表盘文件 CLOCK_FILE_URL = " + CLOCK_FILE_URL);
        MyLog.i(TAG, "下载表盘文件 CLOCK_THEME_MD5 = " + CLOCK_THEME_MD5);
        MyLog.i(TAG, "下载表盘文件 CLOCK_FILE_NAME = " + CLOCK_FILE_NAME);

        if (AuthorityManagement.verifyStoragePermissions(MyThemeActivity.this)) {
            MyLog.i(TAG, "SD卡权限 已获取");

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                MyLog.i(TAG, "SD卡权限 支持");

                if (!ThemeUtils.checkFileExistenceByMd5(Constants.DOWN_THEME_FILE, CLOCK_FILE_NAME, CLOCK_THEME_MD5)) {
                    MyLog.i(TAG, "数据大小 Bytes = 文件不存在");
                    Dialog progressDialogDownFile;
                    progressDialogDownFile = DialogUtils.BaseDialogShowProgress(context,
                            context.getResources().getString(R.string.download_title),
                            context.getResources().getString(R.string.loading0),
                            context.getDrawable(R.drawable.black_corner_bg)
                    );
                    new UpdateInfoService(MyThemeActivity.this).downLoadNewFile(CLOCK_FILE_URL, CLOCK_FILE_NAME, Constants.DOWN_THEME_FILE, progressDialogDownFile);
                } else {
                    startSendThemeData(mBleDeviceTools, ThemeUtils.getBytes(Constants.DOWN_THEME_FILE + CLOCK_FILE_NAME));
                }
            } else {
                MyLog.i(TAG, "SD卡权限 不支持");
                AppUtils.showToast(MyThemeActivity.this, R.string.sd_card);
            }

        } else {
            MyLog.i(TAG, "SD卡权限 未获取");
            AppUtils.showToast(MyThemeActivity.this, R.string.sd_card);
        }

    }


    ///////////////////////


    //==================成功处理
    void handSuccessState() {

        is_send_data = false;

        TimerTwoStop();
        TimerThreeStop();
        TimerFourStop();
        TimerFiveStop();
        if (loading_dialog != null && loading_dialog.isShowing()) {
            loading_dialog.dismiss();
            Toast.makeText(MyThemeActivity.this, getText(R.string.send_success), Toast.LENGTH_SHORT).show();
        }
    }

    //==================失败处理
    void handFailState(boolean is_finish) {

        is_send_data = false;

        waitDialog.close();
        TimerTwoStop();
        TimerThreeStop();
        TimerFourStop();
        TimerFiveStop();
        if (is_finish) {
            AppUtils.showToast(mContext, R.string.no_connection_notification);
            if (loading_dialog != null && loading_dialog.isShowing()) {
                loading_dialog.dismiss();
            }
            finish();
        } else {
            AppUtils.showToast(mContext, R.string.send_fail);
            if (loading_dialog != null && loading_dialog.isShowing()) {
                loading_dialog.dismiss();
            }
        }
    }

    //==================失败处理
    void handFailStateFialCode(int fail_code) {
        is_send_data = false;

        waitDialog.close();
        TimerTwoStop();
        TimerThreeStop();
        TimerFourStop();
        TimerFiveStop();

        if (loading_dialog != null && loading_dialog.isShowing()) {
            loading_dialog.dismiss();
        }

        if (!is_send_fial) {
            AppUtils.showToast(mContext, R.string.send_fail);
        }

        is_send_fial = true;
    }

    /**
     * 获得指定文件的byte数组
     */
    public static byte[] getAssetBytes(Context context, String fileName) {
        byte[] buffer = null;
        try {

            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            buffer = new byte[size];
            is.read(buffer);
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return buffer;
    }
}
