package com.zjw.apps3pluspro.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
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
    public WaitDialog(Context mContext) {
        mActivity = mContext;
//        mHandler = new HandlerClass(this);
    }

    /**
     * 关闭等待对话框
     */
    public void close() {
//        mHandler.sendEmptyMessage(0);
        closeDialog();
    }

    private void closeDialog() {
        try {
            if (mDialog != null) {
                if(mDialog.isShowing()){
                    mDialog.dismiss();
                    mDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示等待对话框
     */
    public void show(String prompt) {
//        HashMap<String, Object> map = new HashMap<String, Object>();
//        mCommands.clear();
//        map.put("prompt", prompt);
//        mCommands.add(map);
//        mHandler.sendEmptyMessage(1);
        openDialog(prompt);
    }
    
    /**
     * 显示等待对话框
     */
    public boolean isshow() {

        if (mDialog != null) {
            return mDialog.isShowing();
        } else {
            return false;
        }


    }

    private void openDialog(String prompt) {
        try {
            closeDialog();
            mDialog = new Dialog(mActivity, android.R.style.Theme_Light_NoTitleBar);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
            mDialog.setContentView(R.layout.popup_waitdialog);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();

            TextView tv = (TextView) mDialog.findViewById(R.id.waitdialog_text);
            if (prompt == null || prompt.length() == 0)
                tv.setVisibility(View.GONE);
            else
                tv.setText(prompt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
