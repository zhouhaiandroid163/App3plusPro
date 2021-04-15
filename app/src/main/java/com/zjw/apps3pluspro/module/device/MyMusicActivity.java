package com.zjw.apps3pluspro.module.device;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
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

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.adapter.MyMusicListAdapter;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.device.entity.ThemeModle;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.MusicBean;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.bleservice.UpdateInfoService;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.DialogUtils;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.AuthorityManagement;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.ThemeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 我的主题
 */

public class MyMusicActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = MyMusicActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private WaitDialog waitDialog;
    private ListView gr_my_theme;
    private MyMusicListAdapter mMyMusicListAdapter;
    private Bitmap nowImgBitmap = null;
    private Handler myHandler;
    private ThemeModle mThemeModle;

    byte[] Bytes;
    String FileName;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_my_music;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = MyMusicActivity.this;
        myHandler = new Handler();
        waitDialog = new WaitDialog(mContext);
        initBroadcast();
        initView();
        initSetAdapter();
        updateUI();
        getPageList();
        enableNotifacationThemeRead();
    }

    void initSetAdapter() {
        mMyMusicListAdapter = new MyMusicListAdapter(mContext);
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
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_READY);
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_BLOCK_END);
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_REPAIR_HEAD);
        filter.addAction(BroadcastTools.ACTION_THEME_RECEIVE_RESPONSE_SN);
        filter.addAction(BroadcastTools.ACTION_THEME_THEME_RESULT_SUCCESS_SN);
        filter.setPriority(1000);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
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

        if (TimerOneHandler != null) {
            TimerOneHandler.removeCallbacksAndMessages(null);
        }

        if (TimerTwoHandler != null) {
            TimerTwoHandler.removeCallbacksAndMessages(null);
        }

        if (TimerThreeHandler != null) {
            TimerThreeHandler.removeCallbacksAndMessages(null);
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
                //设备信息回调完成
                case BroadcastTools.ACTION_THEME_RECEIVE_READY:
                    MyLog.i(TAG, "主题数据 接收广播 准备就绪");
                    BleReady();
                    break;

                case BroadcastTools.ACTION_THEME_RECEIVE_BLOCK_END:
                    MyLog.i(TAG, "主题数据 接收广播 = 当前块结束");
                    sendDataToNor();
                    break;

                case BroadcastTools.ACTION_THEME_RECEIVE_REPAIR_HEAD:
                    MyLog.i(TAG, "主题数据 接收广播 = 补发头");
                    break;

                case BroadcastTools.ACTION_THEME_RECEIVE_RESPONSE_SN:
//                    MyLog.i(TAG, "主题数据 接收广播 = 补发数据");
                    Bundle budle = intent.getExtras();
                    replacmentSnList1 = budle.getIntegerArrayList(BroadcastTools.INTENT_PUT_THEME_REPONSE_SN_NUM_LIST);
                    replacmentSnList2 = new ArrayList<>(replacmentSnList1);
                    MyLog.i(TAG, "主题数据 接收广播 = 补发数据 replacmentSnList1 = " + replacmentSnList1);
                    TimerThreeStart();

                    break;

                case BroadcastTools.ACTION_THEME_THEME_RESULT_SUCCESS_SN:
//                    MyLog.i(TAG, "主题数据 接收广播 = 补发完成的SN");

                    Bundle budle2 = intent.getExtras();
                    int reslt_sn = budle2.getInt(BroadcastTools.INTENT_PUT_THEME_RESULT_SUCCESS_SN);
                    replacmentSnList2.remove((Object) (reslt_sn));
                    MyLog.i(TAG, "主题数据 接收广播 = 补发完成的SN reslt_sn = " + reslt_sn);
                    MyLog.i(TAG, "主题数据 接收广播 = 补发完成的SN replacmentSnList2 = " + replacmentSnList2.toString());
                    break;

            }
        }
    };


    void initView() {

        findViewById(R.id.public_head_back).setOnClickListener(this);

        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.my_music_title));

        gr_my_theme = (ListView) findViewById(R.id.gr_my_theme);
        gr_my_theme.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                MusicBean.DataBean.ListBean my_data = mMyMusicListAdapter.getDevice(arg2);


                if (my_data != null) {

                    MyLog.i(TAG, "pos = " + arg2 + "  my_data = " + my_data.toString() + " 歌曲名 = " + my_data.getSongName());

                    downMusic(my_data);

                }


            }
        });

    }


    void initData(List<MusicBean.DataBean.ListBean> my_theme_list) {
        mMyMusicListAdapter.setDeviceList(my_theme_list);
        mMyMusicListAdapter.notifyDataSetChanged();
        gr_my_theme.setAdapter(mMyMusicListAdapter);
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
     * 获取音频列表
     */
    private void getPageList() {

        waitDialog.show(getString(R.string.loading0));

//        int device_width = 128;
//        int device_height = 220;
//        int device_shape = 1;
//        boolean device_is_heart = true;
//        int it_bin_size = 600;

        RequestInfo mRequestInfo = RequestJson.getMusicPageList();

        MyLog.i(TAG, "请求接口-获取音频列表 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-获取音频列表  result = " + result.toString());

                        MusicBean mMusicBean = ResultJson.MusicBean(result);

                        MyLog.i(TAG, "请求接口-获取音频列表  mMusicBean = " + mMusicBean.toString());
                        //请求成功
                        if (mMusicBean.isRequestSuccess()) {
                            if (mMusicBean.isUserSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取音频列表 成功");
//                                AppUtils.showToast(mContext, R.string.save_ok);
                                initData(mMusicBean.getData().getList());

                            } else if (mMusicBean.isUserSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取音频列表 无数据");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else {
                                MyLog.i(TAG, "请求接口-获取音频列表 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取音频列表 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        waitDialog.close();
                        MyLog.i(TAG, "请求接口-获取音频列表 请求失败 = message = " + arg0.getMessage());
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);
                        return;
                    }
                });
    }


    //===============传输相关===========


    /**
     * 发送蓝牙数据
     */
    void sendDataToBle(byte[] bytes) {

        mThemeModle = new ThemeModle(bytes, 182);


        MyLog.i(TAG, "发送数据 服务不为空");

        waitDialog.show(getString(R.string.loading0));

        startSendData();
        TimerOneStart();
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
    }


    //设备准备就绪
    void BleReady() {
        waitDialog.close();
        initLoadingdialog();
        TimerOneStop();
        TimerTwoStart();
    }

    void sendDataToNor() {

        //不补发
        isReplacement = false;

        //传输结束
        if (mThemeModle.isLastBlock(NowBlock)) {
            MyLog.i(TAG, "传输结束");
            if (loading_dialog != null && loading_dialog.isShowing()) {
                loading_dialog.dismiss();
            }
        }
        //响应不补发-需要发送下一块
        else {
            NowBlock++;
            NowPage = 0;
            TimerTwoStart();
        }
    }


    //======== 第1个定时器====================
    //头发送的间隔
    int HeadDelayTime = 500;

    //发送头
    void AppsendHead() {
        MyLog.i(TAG, "定时器发送头");
        sendMusicHead(mThemeModle, FileName);
    }

    private boolean TimerOneIsStop = false;
    private Handler TimerOneHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 添加更新ui的代码
                    if (!TimerOneIsStop) {
                        AppsendHead();
                        TimerOneHandler.sendEmptyMessageDelayed(1, HeadDelayTime);
                    }
                    break;
                case 0:
                    break;
            }
        }

    };

    private void TimerOneStart() {
        MyLog.i(TAG, "定时器1=打开");
        TimerOneHandler.sendEmptyMessage(1);
        TimerOneIsStop = false;
    }

    private void TimerOneStop() {
        MyLog.i(TAG, "定时器1=关闭");
        TimerOneHandler.sendEmptyMessage(0);
        TimerOneIsStop = true;
    }

    //======== 第2个定时器====================
    int CheckDelayTime = 10;
    int SendDelayTime = 10;

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
                sendThemeBlockVerfication();
            }
        }, CheckDelayTime);
    }

    //发送正常数据
    void sendNormalData() {

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
        end_loading_text.setText(ThemeUtils.getPercentageStr(send_loading_progressbar.getMax(), SnNum));


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
    int ReplacementDelayTime = 1;

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


        builder.setView(view);
        builder.setTitle(getString(R.string.send_loading));
        loading_dialog = builder.show();
        loading_dialog.setCancelable(false);

    }


    //=================升级相关=====================


    private void downMusic(MusicBean.DataBean.ListBean mDataBean) {

        String url = mDataBean.getUrl();
        String file_name = mDataBean.getId() + ".mp3";
        FileName = mDataBean.getSongName();

        MyLog.i(TAG, "获取APP版本号 url = " + url);
        MyLog.i(TAG, "获取APP版本号 file_name = " + file_name);

        if (AuthorityManagement.verifyStoragePermissions(MyMusicActivity.this)) {
            MyLog.i(TAG, "SD卡权限 已获取");

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                if (!ThemeUtils.isFileExistence(Constants.DOWN_MUSIC_FILE, file_name)) {

                    Dialog progressDialogDownFile;
                    progressDialogDownFile = DialogUtils.BaseDialogShowProgress(context,
                            context.getResources().getString(R.string.download_title),
                            context.getResources().getString(R.string.loading0),
                            context.getDrawable(R.drawable.black_corner_bg)
                    );

                    new UpdateInfoService(MyMusicActivity.this).downLoadNewFile(url, file_name, Constants.DOWN_MUSIC_FILE, progressDialogDownFile);
                } else {


                    Bytes = ThemeUtils.getBytes(Constants.DOWN_MUSIC_FILE + file_name);
                    MyLog.i(TAG, "数据大小 Bytes = " + Bytes.length + " 文件已存在？");
                    sendDataToBle(Bytes);


                }

            } else {
                AppUtils.showToast(MyMusicActivity.this, R.string.sd_card);
            }

        } else {
            MyLog.i(TAG, "SD卡权限 未获取");
            AppUtils.showToast(MyMusicActivity.this, R.string.sd_card);
        }

    }


}
