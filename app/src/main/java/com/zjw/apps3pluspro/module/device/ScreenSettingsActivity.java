package com.zjw.apps3pluspro.module.device;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.utils.log.MyLog;

/**
 * 屏幕相关设置
 */
public class ScreenSettingsActivity extends Activity implements View.OnClickListener {
    private final String TAG = ScreenSettingsActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    private TextView tv_screen_brightness;
    private TextView tv_brightness_time;
    private int screen_brightess_number = 0;
    private int brightness_time_number = 0;

    private SeekBar seek_screen_brightness;
    private SeekBar seek_brightness_time;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_settings);
        mContext = ScreenSettingsActivity.this;
        mHandler = new Handler();
        initView();

        initScreenBirghthessData();
        initBirghthessTimeData();


    }

    private void initView() {


        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.screen_settings_title));
        findViewById(R.id.public_head_back).setOnClickListener(this);

        tv_screen_brightness = (TextView) findViewById(R.id.tv_screen_brightness);
        tv_brightness_time = (TextView) findViewById(R.id.tv_brightness_time);

        seek_brightness_time = (SeekBar) findViewById(R.id.seek_brightness_time);
        seek_screen_brightness = (SeekBar) findViewById(R.id.seek_screen_brightness);

        findViewById(R.id.button_screen_save).setOnClickListener(this);


    }


    private void initScreenBirghthessData() {

        screen_brightess_number = mBleDeviceTools.get_screen_brightness();

        MyLog.i(TAG, "亮度等级1 = " + screen_brightess_number);

        if (screen_brightess_number < 1) {
            screen_brightess_number = 1;
        }
        if (screen_brightess_number > 5) {
            screen_brightess_number = 5;
        }

        MyLog.i(TAG, "亮度等级2 = " + screen_brightess_number);

        seek_screen_brightness.setMax(4);
        tv_screen_brightness.setText(String.valueOf(screen_brightess_number));
        seek_screen_brightness.setProgress(screen_brightess_number - 1);

        MyLog.i(TAG, "亮度等级3 = " + screen_brightess_number);

        seek_screen_brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                MyLog.i(TAG, "屏幕设置 亮度等级 拖动结束 = screen_brightess_number = " + screen_brightess_number);

                mBleDeviceTools.set_screen_brightness(screen_brightess_number);
                BroadcastTools.sendBleScrenBrighessData(mContext);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                MyLog.i(TAG, "屏幕设置 亮度等级 = progress = " + progress);

                screen_brightess_number = progress + 1;
                tv_screen_brightness.setText(String.valueOf(screen_brightess_number));


            }
        });


    }

    private void initBirghthessTimeData() {


        brightness_time_number = mBleDeviceTools.get_brightness_time();


        MyLog.i(TAG, "亮屏时间1 = " + brightness_time_number);

        if (brightness_time_number < 5) {
            brightness_time_number = 5;
        }

        if (brightness_time_number > 15) {
            brightness_time_number = 15;
        }

        MyLog.i(TAG, "亮屏时间2 = " + brightness_time_number);

        seek_brightness_time.setMax(10);

        tv_brightness_time.setText(String.valueOf(brightness_time_number));
        seek_brightness_time.setProgress(brightness_time_number - 5);

        MyLog.i(TAG, "亮屏时间3 = " + brightness_time_number);
        seek_brightness_time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                MyLog.i(TAG, "屏幕设置 亮屏时间 拖动结束 = brightness_time_number = " + brightness_time_number);

                mBleDeviceTools.set_brightness_time(brightness_time_number);

                BroadcastTools.sendBleBrighessTimeData(mContext);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                MyLog.i(TAG, "屏幕设置 亮屏时间 = progress = " + progress);

                brightness_time_number = progress + 5;
                tv_brightness_time.setText(String.valueOf(brightness_time_number));


            }
        });

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.public_head_back:
                finish();
                break;

            case R.id.button_screen_save:

//                mBleDeviceTools.set_screen_brightness(screen_brightess_number);
//                mBleDeviceTools.set_brightness_time(brightness_time_number);
//                AppUtils.showToast(mContext, R.string.save_ok);
//                sendBleData();

                break;


        }
    }





    protected void onDestroy() {

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        super.onDestroy();
    }


}


