package com.zjw.apps3pluspro.broadcastreceiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * 来电广播监听
 */

public class PhoneReceiver extends BroadcastReceiver {

    private OnPhoneListener onPhoneListener;

    public PhoneReceiver() {
    }

    public PhoneReceiver(OnPhoneListener onPhoneListener) {
        this.onPhoneListener = onPhoneListener;
    }

    /**
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {

        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    public interface OnPhoneListener {


        void onCallState(int state, String incomingNumber);
    }


    PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            //方法必须写在super方法后面，否则incomingNumber无法获取到值。
            super.onCallStateChanged(state, incomingNumber);

            if (onPhoneListener != null) {
                onPhoneListener.onCallState(state, incomingNumber);
            }
        }
    };


}