package com.zjw.apps3pluspro.module.device;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.adapter.MyMailListAdapter;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.device.entity.ThemeModle;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.sql.dbmanager.PhoneInfoUtils;
import com.zjw.apps3pluspro.sql.entity.PhoneInfo;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.AuthorityManagement;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.PhoneUtil;
import com.zjw.apps3pluspro.utils.ThemeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;


/**
 * 常用联系人
 */

public class ContactListActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = ContactListActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    //数据库存储
    private PhoneInfoUtils mPhoneInfoUtils = BaseApplication.getPhoneInfoUtils();

    public static final int MyMailListResultAdd = 300;


    private Handler myHandler;
    private ThemeModle mThemeModle;
    private boolean is_send_fial = false;

    private WaitDialog waitDialog;


    //列表
    private ListView my_mail_list_listview;
    private MyMailListAdapter mMyMailListAdapter;

    private LinearLayout public_no_bg_head_add;

    private TextView alarm_clock_size;
    private TextView alarm_clock_delete;
    private LinearLayout lin_alarm_clock;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_my_mail_list;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = ContactListActivity.this;
        myHandler = new Handler();
        waitDialog = new WaitDialog(mContext);
        initBroadcast();
        initView();
        initSetAdapter();
        updateUi();

        enableNotifacationThemeRead();
        saveMyMailListSettoBle();
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
//        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_REPAIR_HEAD);
//        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_RESPONSE_SN);
//        filter.addAction(BroadcastTools.ACTION_THEME_THEME_RESULT_SUCCESS_SN);
//        filter.addAction(BroadcastTools.ACTION_DOWN_CLOCK_FILE_STATE_SUCCESS);
        filter.addAction(BroadcastTools.ACTION_THEME_SUSPENSION_FAIL);
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_SUCCESS);
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_FAIL);
        filter.setPriority(1000);
        registerReceiver(broadcastReceiver, filter);
    }


    protected void onDestroy() {
        waitDialog.dismiss();
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
//                    TimerFourStop();
                    FourNowCount = 0;
                    sendDataToNor();
                    break;

//                case BroadcastTools.ACTION_THEME_RECEIVE_REPAIR_HEAD:
//                    MyLog.i(TAG, "主题数据 接收广播 = 补发头");
//                    break;

//                case BroadcastTools.ACTION_THEME_RECEIVE_RESPONSE_SN:
////                    MyLog.i(TAG, "主题数据 接收广播 = 补发数据");
//                    Bundle budle = intent.getExtras();
//                    replacmentSnList1 = budle.getIntegerArrayList(BroadcastTools.INTENT_PUT_THEME_REPONSE_SN_NUM_LIST);
//                    replacmentSnList2 = new ArrayList<>(replacmentSnList1);
//                    MyLog.i(TAG, "主题数据 接收广播 = 补发数据 replacmentSnList1 = " + replacmentSnList1);
//                    TimerThreeStart();
//
//                    break;

//                case BroadcastTools.ACTION_THEME_THEME_RESULT_SUCCESS_SN:
////                    MyLog.i(TAG, "主题数据 接收广播 = 补发完成的SN");
//                    Bundle budle2 = intent.getExtras();
//                    int reslt_sn = budle2.getInt(BroadcastTools.INTENT_PUT_THEME_RESULT_SUCCESS_SN);
//                    replacmentSnList2.remove((Object) (reslt_sn));
//                    MyLog.i(TAG, "主题数据 接收广播 = 补发完成的SN reslt_sn = " + reslt_sn);
//                    MyLog.i(TAG, "主题数据 接收广播 = 补发完成的SN replacmentSnList2 = " + replacmentSnList2.toString());
//                    break;

                case BroadcastTools.ACTION_THEME_SUSPENSION_FAIL:
                    MyLog.i(TAG, "主题数据 接收广播 发送失败-中断");

                    Bundle budle3 = intent.getExtras();
                    int fail_code = budle3.getInt(BroadcastTools.INTENT_PUT_SUSPENSION_FAIL_FIAL_CODE);

                    MyLog.i(TAG, "主题数据 接收广播 发送失败-中断 fail_code = " + fail_code);
                    handFailStateFialCode(fail_code);
                    break;

                case BroadcastTools.ACTION_THEME_RECEIVE_SUCCESS:
                    MyLog.i(TAG, "主题数据 接收广播 = 发送成功");
//                    Toast.makeText(MyMailListActivity.this, "发送成功！111", Toast.LENGTH_SHORT).show();
                    TimerFourStop();
                    FourNowCount = 0;
                    if (loading_dialog != null && loading_dialog.isShowing()) {
                        loading_dialog.dismiss();
                        Toast.makeText(ContactListActivity.this, getText(R.string.send_success), Toast.LENGTH_SHORT).show();
                    }
                    break;


                case BroadcastTools.ACTION_THEME_RECEIVE_FAIL:
                    MyLog.i(TAG, "主题数据 接收广播 = 发送失败");
//                    Toast.makeText(MyMailListActivity.this, "发送失败！111", Toast.LENGTH_SHORT).show();
                    handFailState(false);
                    break;


            }
        }
    };


    void initView() {

        findViewById(R.id.public_no_bg_head_back).setOnClickListener(this);
        lin_alarm_clock = (LinearLayout) findViewById(R.id.lin_alarm_clock);
//        ((TextView) findViewById(R.id.public_head_title)).setText("常用联系人");

        alarm_clock_delete = (TextView) findViewById(R.id.alarm_clock_delete);
        alarm_clock_size = (TextView) findViewById(R.id.alarm_clock_size);
        public_no_bg_head_add = (LinearLayout) findViewById(R.id.public_no_bg_head_add);
        public_no_bg_head_add.setOnClickListener(this);


        my_mail_list_listview = (ListView) findViewById(R.id.my_mail_list_listview);

        findViewById(R.id.send_data).setOnClickListener(this);

    }

    void initSetAdapter() {

        mMyMailListAdapter = new MyMailListAdapter(mContext);
        my_mail_list_listview.setAdapter(mMyMailListAdapter);

        //listView长按事件
        my_mail_list_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                MyLog.i(TAG, "长按 = getDevice = " + mMyMailListAdapter.getDevice(position).toString());

                deleteMailList(mMyMailListAdapter.getDevice(position));

                return true;
            }
        });

    }

    void updateUi() {

        List<PhoneInfo> mPhoneInfoList = mPhoneInfoUtils.MyQueryToDate(BaseApplication.getUserId());
        if (mPhoneInfoList != null) {
            MyLog.i(TAG, "mPhoneInfoList = " + mPhoneInfoList.toString());
        } else {
            MyLog.i(TAG, "mPhoneInfoList = null");
        }

        if (mPhoneInfoList != null && mPhoneInfoList.size() > 0) {

            MyLog.i(TAG, "mPhoneInfoList = size = " + mPhoneInfoList.size());

            alarm_clock_size.setText(String.valueOf((mPhoneInfoList.size())));

            if (mPhoneInfoList.size() >= 40) {
                public_no_bg_head_add.setVisibility(View.GONE);
            } else {
                public_no_bg_head_add.setVisibility(View.VISIBLE);
            }

            lin_alarm_clock.setVisibility(View.VISIBLE);
            alarm_clock_delete.setVisibility(View.VISIBLE);


            my_mail_list_listview.setVisibility(View.VISIBLE);
            mMyMailListAdapter.clear();
            mMyMailListAdapter.setmPhoneInfoList(mPhoneInfoList);
            mMyMailListAdapter.notifyDataSetChanged();

        } else {

            MyLog.i(TAG, "mPhoneInfoList = null");

            public_no_bg_head_add.setVisibility(View.VISIBLE);
            lin_alarm_clock.setVisibility(View.GONE);
            alarm_clock_delete.setVisibility(View.GONE);
            my_mail_list_listview.setVisibility(View.GONE);

//            mMyMailListAdapter.clear();
//            mMyMailListAdapter.setmPhoneInfoList(mPhoneInfoList);
//            mMyMailListAdapter.notifyDataSetChanged();
        }

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.public_no_bg_head_back:
                finish();
                break;
            case R.id.send_data:
                saveMyMailListSettoBle();

                break;

            case R.id.public_no_bg_head_add:

                if (AuthorityManagement.verifyMailList(ContactListActivity.this)) {
                    MyLog.i(TAG, "读取联系人 已获取");
                    PhoneUtil.getContactNameFromPhoneBook(this, "123456");
                    //                if (mMyMailListAdapter.getCount() >= 10) {
//                    Toast.makeText(MyMailListActivity.this, "多可设置10个常用联系人,请删除后再添加", Toast.LENGTH_SHORT).show();
//                } else {
                    Intent intent = new Intent(mContext, AddMyMailListActivity.class);
                    startActivityForResult(intent, MyMailListResultAdd);
//                }

                } else {
                    MyLog.i(TAG, "读取联系人 未授权");
                    Toast.makeText(this, getResources().getString(R.string.read_contacts_permission), Toast.LENGTH_SHORT).show();
                }
                if (AuthorityManagement.verifyPhoneState(ContactListActivity.this)) {
                    MyLog.i(TAG, "读取来电状态 已获取");
                } else {
                    MyLog.i(TAG, "读取来电状态 已获取");
                }


                break;

        }
    }


    //===============传输相关===========


    void startSendThemeData(BleDeviceTools mBleDeviceTools, byte[] bytes) {

        if (HomeActivity.ISBlueToothConnect()) {
//            if (mBleDeviceTools.get_ble_device_power() >= 25) {
            sendDataToBle(mBleDeviceTools, bytes);
//            } else {
//                AppUtils.showToast(mContext, R.string.send_imge_error_low_power);
//            }
        } else {
            AppUtils.showToast(mContext, R.string.no_connection_notification);
        }

    }


    /**
     * 发送蓝牙数据
     */
    void sendDataToBle(BleDeviceTools mBleDeviceTools, byte[] bytes) {

//        mThemeModle = new ThemeModle(bytes, 182);
//        mThemeModle = new ThemeModle(bytes, 240);
//        mThemeModle = new ThemeModle(bytes, 20);
//        mThemeModle = new ThemeModle(bytes, 20);
        MyLog.i(TAG, "主题传输发送 MTU = " + mBleDeviceTools.get_device_mtu_num());
        mThemeModle = new ThemeModle(bytes, mBleDeviceTools.get_device_mtu_num());

        waitDialog.show(getString(R.string.loading0));
        startSendData();
        AppsendHead();
        TimerFourStart();

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
        waitDialog.close();
        initLoadingdialog();
//        TimerOneStop();
        TimerTwoStart();
//        TimerFourStop();
        FourNowCount = 0;
        is_send_fial = false;
    }

    void sendDataToNor() {

        //不补发
        isReplacement = false;

        //传输结束
//        if (mThemeModle.isLastBlock(NowBlock) || NowBlock == 6) {
        if (mThemeModle.isLastBlock(NowBlock)) {
            MyLog.i(TAG, "传输结束");
            TimerFourStop();
            if (loading_dialog != null && loading_dialog.isShowing()) {
                loading_dialog.dismiss();
                Toast.makeText(ContactListActivity.this, getText(R.string.send_success), Toast.LENGTH_SHORT).show();
            }
        }
        //响应不补发-需要发送下一块
        else {
            NowBlock++;
            NowPage = 0;
            TimerTwoStart();
        }
    }


    //
    //发送头
    private void AppsendHead() {
        MyLog.i(TAG, "定时器发送头");
        sendMailListHead(mThemeModle, 0);
    }

    //======== 第2个定时器====================
    int CheckDelayTime = 10;
    int SendDelayTime = 10;
//    int CheckDelayTime = 2;
//    int SendDelayTime = 2;

    void sendNorDataToBle() {
        byte[] send_data = mThemeModle.getSendData(NowBlock, NowPage, SnNum);

        if (send_data != null) {
            sendThemeData(SnNum, send_data);
        } else {
            MyLog.i(TAG, "发送数据 数据为空");
        }
    }

    void sendBlockVerfication() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyLog.i(TAG, "主题数据 传输 发送块校验");
                sendThemeBlockVerfication();
                FourNowCount = 0;
            }
        }, CheckDelayTime);
    }

    //发送正常数据
    void sendNormalData() {

        FourNowCount = 0;
        NowReplacement = 0;
        NowPage++;
        SnNum++;

        //最后一个包
        if (mThemeModle.isLastPage(SnNum)) {

//            MyLog.i(TAG, "主题数据 传输 最后一个包 NowPage = " + NowPage);
            //最后一个正常数据发送完毕
            TimerTwoStop();

//            MyLog.i(TAG, "主题数据 传输 SnNumber = " + SnNumber + " start_pos = " + start_pos + "  send_data.len = " + send_data.length);
            sendNorDataToBle();
            //发送块校验
            sendBlockVerfication();
        } else {

//            MyLog.i(TAG, "主题数据 传输 不是最后的包 NowPage = " + NowPage);


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

//        float bili = (float) (SnNum) / (float) send_loading_progressbar.getMax() * 100;
//
//        String str = String.valueOf(bili);
//
//        if (bili > 10) {
//            if (str.length() >= 5) {
//                str = str.substring(0, 5);
//            }
//        } else {
//            if (str.length() >= 4) {
//                str = str.substring(0, 4);
//            }
//        }

//        end_loading_text.setText(str);

        String por_str = ThemeUtils.getPercentageStr(send_loading_progressbar.getMax(), SnNum);

        MyLog.i(TAG, "表盘传输进度条 = por_str = " + por_str);
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
        int now_page = sn_number % mThemeModle.getPageDataMax();
        byte[] send_data = mThemeModle.getSendData(NowBlock, now_page, sn_number);
        if (send_data != null) {
            sendThemeData(sn_number, send_data);
        } else {
            MyLog.i(TAG, "发送数据 数据为空");
        }
    }


    //发送补发数据
    void sendReplacment() {
        NowReplacement++;

        MyLog.i(TAG, "主题数据 传输 发送补发数据 NowReplacement = " + NowReplacement + "  data_sn = " + replacmentSnList1.get(NowReplacement - 1));

        sendRepDataToBle(replacmentSnList1.get(NowReplacement - 1));

        //最后一个数据
        if (NowReplacement >= replacmentSnList1.size()) {

            MyLog.i(TAG, "主题数据 传输 发送补发数据  最后一个数据");


            TimerThreeStop();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (replacmentSnList2.size() == 0) {

                        MyLog.i(TAG, "主题数据 传输 不需要第二轮补发");

                        sendBlockVerfication();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                sendBlockVerfication();
                            }
                        }, 500);

                    } else {

                        MyLog.i(TAG, "主题数据 传输 需要第二轮补发！！");

                        replacmentSnList1 = new ArrayList<>(replacmentSnList2);
                        TimerThreeStart();


                    }
                }
            }, 100);


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


    //======== 第4个定时器====================判断超时
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

    private AlertDialog loading_dialog;
    private ProgressBar send_loading_progressbar;
    private RelativeLayout rl_theme_type1;
    private RelativeLayout rl_theme_type2;
    private ImageView send_loading_img_bg_type1, send_loading_img_text_type1;
    private ImageView send_loading_img_bg_type2, send_loading_img_text_type2;
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
        send_loading_progressbar.setMax(mThemeModle.getTotalPageSize());

        rl_theme_type1.setVisibility(View.GONE);
        rl_theme_type2.setVisibility(View.GONE);

        builder.setView(view);
        builder.setTitle(getString(R.string.send_loading));
        loading_dialog = builder.show();
        loading_dialog.setCancelable(false);


    }


    byte[] Bytes;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        MyLog.i(TAG, "通讯录显示界面=========onActivityResult");

        //添加联系人
        if (resultCode == MyMailListResultAdd) {

            MyLog.i(TAG, "通讯录显示界面 300 添加");

            PhoneInfo mPhoneInfo = data.getParcelableExtra(IntentConstants.Intent_PhoneInfo);

            if (mPhoneInfo != null) {
                MyLog.i(TAG, "通讯录显示界面 300 添加 mPhoneInfo = " + mPhoneInfo.toString());
                mPhoneInfoUtils.insertData(mPhoneInfo);
                updateUi();
            } else {
                MyLog.i(TAG, "通讯录显示界面 300 添加 mPhoneInfo = null");
            }

            saveMyMailListSettoBle();

        }

    }

    void saveMyMailListSettoBle() {

        MyLog.i(TAG, "saveMyMailListSettoBle()");

        List<PhoneInfo> mPhoneInfoList = mPhoneInfoUtils.MyQueryToDate(BaseApplication.getUserId());
        if (mPhoneInfoList != null) {
            MyLog.i(TAG, "mPhoneInfoList = " + mPhoneInfoList.toString());
            List<PhoneInfo> my_list = mMyMailListAdapter.getmPhoneInfoList();
            if (my_list != null && my_list.size() > 0) {

                ArrayList<String> name_list = new ArrayList<>();
                ArrayList<String> phone_list = new ArrayList<>();

                for (int i = 0; i < my_list.size(); i++) {
                    name_list.add(my_list.get(i).getPhone_name());
                    phone_list.add(my_list.get(i).getPhone_number());
                }

                MyLog.i(TAG, "数据处理 name_list = " + name_list.toString());
                MyLog.i(TAG, "数据处理 phone_list = " + phone_list.toString());

                Bytes = ThemeUtils.getPhoneList(name_list, phone_list);

                MyLog.i(TAG, "数据处理 Bytes = " + Bytes.length);

                MyLog.i(TAG, "数据处理 str = " + ThemeUtils.bytes2HexString2(Bytes));
//                MyLog.i(TAG, "数据处理 str = " + BleTools.bytes2HexString(Bytes));
//
                startSendThemeData(mBleDeviceTools, Bytes);
            } else {
                cleanMailList();
            }
        } else {
            MyLog.i(TAG, "mPhoneInfoList = null");
            cleanMailList();
        }


    }

    /**
     * 弹出设置对话框
     *
     * @param mPhoneInfo
     */
    void deleteMailList(final PhoneInfo mPhoneInfo) {
        new android.app.AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.my_mail_list_dialog_delete_title))//设置对话框标题
                .setMessage(getString(R.string.my_mail_list_dialog_name) + ":" + mPhoneInfo.getPhone_name()
                        + "\n" + getString(R.string.my_mail_list_dialog_phone) + ":" + mPhoneInfo.getPhone_number()
                )//设置显示的内容
                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {//添加确定按钮

                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                        if (mPhoneInfo != null) {
                            MyLog.i(TAG, "通讯录显示界面 删除 mPhoneInfo = " + mPhoneInfo.toString());
                            mPhoneInfoUtils.deleteData(mPhoneInfo);
                            updateUi();
                        } else {
                            MyLog.i(TAG, "通讯录显示界面 mPhoneInfo = null");
                        }

                        saveMyMailListSettoBle();


                    }

                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {//添加返回按钮


            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件

                // TODO Auto-generated method stub


            }

        }).show();//在按键响应事件中显示此对话框

    }

    //==================失败处理

    void handFailState(boolean is_finish) {


        waitDialog.close();
        TimerTwoStop();
        TimerThreeStop();
        TimerFourStop();


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
    private void handFailStateFialCode(int fail_code) {
        waitDialog.close();
        TimerTwoStop();
        TimerThreeStop();
        TimerFourStop();


        if (loading_dialog != null && loading_dialog.isShowing()) {
            loading_dialog.dismiss();
        }

        if (!is_send_fial) {
            AppUtils.showToast(mContext, R.string.send_fail);
        }

        is_send_fial = true;
    }
}
