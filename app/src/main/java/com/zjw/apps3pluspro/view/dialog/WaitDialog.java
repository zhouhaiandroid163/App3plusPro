package com.zjw.apps3pluspro.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;

/**
 * 等待对话框
 */
public class WaitDialog {

    private Context mActivity = null;
//    private Handler mHandler = null;

    private Dialog mDialog = null;

//    private Vector<HashMap<String, Object>> mCommands = new Vector<HashMap<String, Object>>();


//    private static class HandlerClass extends Handler {
//
//        SoftReference<WaitDialog> mWaitDialog;
//
//        public HandlerClass(WaitDialog wd) {
//            mWaitDialog = new SoftReference<WaitDialog>(wd);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            WaitDialog wd = mWaitDialog.get();
//            if (msg.what == 0) {
//                wd.closeDialog();
//                return;
//            }
//            if (msg.what == 1) {
//                HashMap<String, Object> map = wd.mCommands.get(0);
//                String prompt = (String) map.get("prompt");
//                wd.openDialog(prompt);
//                return;
//            }
//        }
//
//    }


    /**
     * 构造函数
     */
    private Handler mHandler;

    public WaitDialog(Context mContext) {
        mActivity = mContext;
//        mHandler = new HandlerClass(this);
        mHandler = new Handler();
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
            }
        }
    }

    /**
     * 关闭等待对话框
     */
    public void close() {
        try {
            if (mHandler == null) {
                mHandler = new Handler();
            }
            if (mDialog != null && mDialog.isShowing()) {
                mHandler.postDelayed(() -> {
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                        mDialog = null;
                    }
                }, 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close(int time) {
        try {
            if (mHandler == null) {
                mHandler = new Handler();
            }
            if (mDialog != null && mDialog.isShowing()) {
                mHandler.postDelayed(() -> {
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                        mDialog = null;
                    }
                }, time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示等待对话框
     */
    public void show(String prompt) {
        try {
            if (mDialog == null) {
                mDialog = new Dialog(mActivity);
                mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                mDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                mDialog.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                //布局位于状态栏下方
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                //全屏
                                View.SYSTEM_UI_FLAG_FULLSCREEN;
                        //隐藏导航栏
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        uiOptions |= 0x00001000;
                        mDialog.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
                    }
                });

                mDialog.setContentView(R.layout.popup_waitdialog);
                TextView tv = (TextView) mDialog.findViewById(R.id.waitdialog_text);
                if (TextUtils.isEmpty(prompt))
                    tv.setVisibility(View.GONE);
                else {
                    tv.setText(prompt);
                }

                mDialog.setCancelable(true);
                mDialog.setCanceledOnTouchOutside(false);
            }

            TextView tv = (TextView) mDialog.findViewById(R.id.waitdialog_text);
            if (TextUtils.isEmpty(prompt))
                tv.setVisibility(View.GONE);
            else {
                tv.setText(prompt);
            }
            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
