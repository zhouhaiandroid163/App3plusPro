package com.zjw.apps3pluspro.module.mine.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.OldBean;
import com.zjw.apps3pluspro.network.okhttp.RequestParams;
import com.zjw.apps3pluspro.network.okhttp.UploadProgressListener;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.DialogUtils;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;


/**
 * 意见反馈
 */
public class FeedBackActivity extends BaseActivity implements OnClickListener {
    private final String TAG = FeedBackActivity.class.getSimpleName();
    private Context mContext;
    private WaitDialog waitDialog;
    private EditText ed_feedback_advice, et_feedback_email;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = FeedBackActivity.this;
        waitDialog = new WaitDialog(mContext);
        initView();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }

    private void initView() {
        // TODO Auto-generated method stub
        findViewById(R.id.public_head_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.title_feedback));
        ed_feedback_advice = (EditText) findViewById(R.id.ed_feedback_advice);
        et_feedback_email = (EditText) findViewById(R.id.et_feedback_email);
        findViewById(R.id.bt_feedback_submit).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.public_head_back:
                finish();
                break;

            case R.id.bt_feedback_submit:
                String advice = ed_feedback_advice.getText().toString().trim();
                String email = et_feedback_email.getText().toString().trim();
//                mHandleNetworkListener.FeedBackToServer(TAG, mContext, advice, email);
                checkFeedback(advice, email);
                break;
        }
    }

    void checkFeedback(String advice, String email) {


        if (TextUtils.isEmpty(advice)) {
            AppUtils.showToast(mContext, R.string.feedback_null);
            return;
        }

        if (TextUtils.isEmpty(email)) {
            AppUtils.showToast(mContext, R.string.input_your_email);
            return;
        }

        if (!JavaUtil.isEmail(email)) {

            AppUtils.showToast(mContext, R.string.wrong_input_format_email);
            return;
        }

        FeedBackToServer(advice, email);
    }

    /**
     * 提交意见到服务器
     */
    private Dialog progressDialogDownFile;
    private void FeedBackToServer(String advice, String email) {
        waitDialog.show(getString(R.string.loading0));

//        Handler handler = new Handler();
//        progressDialogDownFile = DialogUtils.BaseDialogShowProgress(context,
//                context.getResources().getString(R.string.download_title),
//                context.getResources().getString(R.string.loading0),
//                context.getDrawable(R.drawable.black_corner_bg)
//        );
//        ProgressBar progressBar = progressDialogDownFile.findViewById(R.id.progress);
//        try {
//            File mFile = new File(Constants.P_LOG_PATH + Constants.P_LOG_BLE_FILENAME);
//            RequestParams params = new RequestParams();
//            params.put("file", mFile);
//            params.put("userId", BaseApplication.getUserId());
//            params.put("feedbackContent", advice);
//            params.put("feedbackEmail", email);
//            params.put("phoneModel", "Android");
//            params.put("appMsg", MyUtils.getAppName() + "_" + MyUtils.getAppInfo());
//            params.put("phoneSystem", MyUtils.getPhoneModel());
//            params.put("appId", "02");
////            params.put("feekBackType", 3);
//            NewVolleyRequest.RequestMultiPostRequest(params, new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
//                @Override
//                public void onMySuccess(JSONObject result) {
//                    if (waitDialog != null) {
//                        waitDialog.close();
//                    }
//                    MyLog.i(TAG, "请求接口-意见反馈 result = " + result);
//                    AppUtils.showToast(mContext, R.string.conmit_ok);
//                    finish();
//                }
//
//                @Override
//                public void onMyError(VolleyError arg0) {
//                    if (waitDialog != null) {
//                        waitDialog.close();
//                    }
//                    MyLog.i(TAG, "onMyError result = " + arg0);
//                }
//            }, RequestJson.feedbackUrl2, new UploadProgressListener() {
//                @Override
//                public void onProgress(long contentLength, int mCurrentLength) {
//                    handler.post(() -> {
//                        progressBar.setMax((int) contentLength);
//                        progressBar.setProgress(mCurrentLength);
//                    });
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        RequestInfo mRequestInfo = RequestJson.feedback(mContext, advice, email, 0);
        MyLog.i(TAG, "请求接口-意见反馈 mRequestInfo = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-意见反馈 result = " + result);
                        waitDialog.close();
                        OldBean mOldBean = ResultJson.OldBean(result);
                        if (mOldBean.isRequestSuccess()) {
                            MyLog.i(TAG, "请求接口-意见反馈 成功");
                            AppUtils.showToast(mContext, R.string.conmit_ok);
                            finish();
                        } else {
                            MyLog.i(TAG, "请求接口-意见反馈 失败");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }


                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub


                        MyLog.i(TAG, "请求接口-意见反馈 请求失败 = message = " + arg0.getMessage());
                        waitDialog.close();
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });
    }
}
