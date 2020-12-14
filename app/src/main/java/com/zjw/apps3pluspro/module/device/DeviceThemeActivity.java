package com.zjw.apps3pluspro.module.device;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.adapter.DeviceThemeListAdapter;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 表盘中心
 */
public class DeviceThemeActivity extends Activity implements View.OnClickListener {
    private final String TAG = DeviceThemeActivity.class.getSimpleName();
    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    private Context mContext;
    private DeviceThemeListAdapter mDeviceThemeListAdapter;


    private ListView device_theme_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_theme);
        mContext = DeviceThemeActivity.this;

        initView();
        initSetAdapter();
        initData();
    }

    private void initView() {

        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.device_theme_title));
        findViewById(R.id.public_head_back).setOnClickListener(this);
        device_theme_list = (ListView) findViewById(R.id.device_theme_list);

    }

    int payType;

    void initSetAdapter() {


        mDeviceThemeListAdapter = new DeviceThemeListAdapter(mContext);

        //自定义回调函数
        mDeviceThemeListAdapter.setOncheckChanged(new DeviceThemeListAdapter.OnMyCheckChangedListener() {

            public void setSelectID(int selectID) {

                MyLog.i(TAG, "点击 单选 selectID = " + selectID);
                mDeviceThemeListAdapter.setSelectID(selectID);                //选中位置
                mDeviceThemeListAdapter.notifyDataSetChanged();        //刷新适配器
                mBleDeviceTools.set_device_theme(selectID);
                BroadcastTools.sendBleThemeData(mContext);
            }
        });

        device_theme_list.setAdapter(mDeviceThemeListAdapter);
        device_theme_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);//如果不使用这个设置，选项中的radiobutton无法响应选中事件
        device_theme_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {


//                MyLog.i(TAG, "点击 列表 arg2 = " + position);
//
                mDeviceThemeListAdapter.setSelectID(position);                //选中位置
                mDeviceThemeListAdapter.notifyDataSetChanged();        //刷新适配器

                mBleDeviceTools.set_device_theme(position);
                BroadcastTools.sendBleThemeData(mContext);


            }
        });


    }

    private void initData() {


        int count = mBleDeviceTools.get_theme_count();
        BroadcastTools.sendBleThemeData(mContext);

        if (mBleDeviceTools.get_device_theme() >= mBleDeviceTools.get_theme_count()) {

            mBleDeviceTools.set_device_theme(0);
        }

        if (count >= 2) {

            List<String> mData = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                if (i == 0) {
                    mData.add(getString(R.string.defult_theme));
                } else {
                    mData.add(getString(R.string.device_theme) + String.valueOf(i));
                }
            }

            mDeviceThemeListAdapter.setDeviceList(mData);
            mDeviceThemeListAdapter.setSelectID(mBleDeviceTools.get_device_theme());                //选中位置
            mDeviceThemeListAdapter.notifyDataSetChanged();


//            mDeviceThemeListAdapter.notifyDataSetChanged();
        }


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.public_head_back:
                finish();
                break;


        }
    }

//    void sendBleData() {
//        final Intent intent = new Intent();
//        intent.setAction(BleConstant.ACTION_NOTIFICATION_SEND_SET_DEVICE_THEME);
//        sendBroadcast(intent);
//    }


}


