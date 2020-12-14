package com.zjw.apps3pluspro.module.device.reminde;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.utils.MyActivityManager;

/**
 * 心率预警
 */
public class HeartWarnActivity extends Activity implements View.OnClickListener {
    private final String TAG = HeartWarnActivity.class.getSimpleName();
    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    private Context mContext;
    private MyActivityManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disturb);
        mContext = HeartWarnActivity.this;
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        initView();
        initData();

    }

    private void initView() {
        ((TextView) findViewById(R.id.public_head_title)).setText(getText(R.string.heart_warning_tip));
        findViewById(R.id.public_head_back).setOnClickListener(this);
    }


    private void initData() {

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.public_head_back:
                finish();
                break;

        }
    }

}


