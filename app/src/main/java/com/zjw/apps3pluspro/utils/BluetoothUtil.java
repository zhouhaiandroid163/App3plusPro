package com.zjw.apps3pluspro.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

public class BluetoothUtil {

    public static boolean isBluetoothOpened() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) return false;
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * 开启蓝牙，需要在 Activity 的 onActivityResult() 方法中监听开启结果
     *
     * @param activity 传入当前所在的 Activity
     * @return 是否成功调用开启方法，这里返回 true 不代表开启成功
     */
    public static boolean enableBluetooth(Activity activity, int requestCode) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }
        // 如果本地蓝牙没有开启，则开启
        if (!mBluetoothAdapter.isEnabled()) {
            // 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，
            // 那么将会收到RESULT_OK的结果，
            // 如果RESULT_CANCELED则代表用户不愿意开启蓝牙
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(mIntent, requestCode);
            //用enable()方法来开启，无需询问用户(无声息的开启蓝牙设备),这时就需要用到android.permission.BLUETOOTH_ADMIN权限。
            //mBluetoothAdapter.enable();
            // mBluetoothAdapter.disable();//关闭蓝牙
        }
        return true;
    }

    /**
     * 关闭蓝牙 需要BLUETOOTH_ADMIN权限
     */
    public static boolean closeBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }
        return mBluetoothAdapter.disable();
    }


    public static void initBluetoothStatusDetect(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(mReceiver, filter);
    }


    private static BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            if (TextUtils.equals(intent.getAction(), BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                if (_listener != null) {
                    _listener.onBlueToothStatusChanged(blueState);
                }
            }
        }
    };

    private static BluetoothCloseOpenListener _listener;

    public static void setBluetoothCloseOpenListener(BluetoothCloseOpenListener listener) {
        _listener = listener;
    }

    public interface BluetoothCloseOpenListener {
        /**
         * @param status BluetoothAdapter.STATE_TURNING_ON、BluetoothAdapter.STATE_ON 等
         */
        void onBlueToothStatusChanged(int status);
    }
}
