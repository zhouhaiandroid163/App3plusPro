package com.zjw.apps3pluspro.module.friend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
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
import com.zjw.apps3pluspro.network.javabean.NewFriendInfoBean;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;


/**
 * 好友的详情界面
 */
public class FriendInfoRemarksActivity extends BaseActivity implements OnClickListener {
    private final String TAG = FriendInfoRemarksActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    private WaitDialog waitDialog;

    private String FriendDataId;//数据ID
    private String FriendUid;//好友ID
    private String FriendNickName;//好友昵称
    private String FriendRemarksName;//好友备注
    private String friendHearUrl;//好友头像

    private EditText et_remarks_remarks;

    @Override
    protected void initViews() {
        super.initViews();
        mContext = FriendInfoRemarksActivity.this;
        waitDialog = new WaitDialog(mContext);

        initView();
        initData();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_friend_info_remarks;
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
        ((TextView) (findViewById(R.id.public_head_title))).setText(getText(R.string.care_remarks));
        et_remarks_remarks = (EditText) findViewById(R.id.et_remarks_remarks);

        // 修改备注
        findViewById(R.id.btn_remarks_save).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent;
        switch (v.getId()) {
            case R.id.public_head_back:
                finish();
                break;

            case R.id.btn_remarks_save:

                String remarks = et_remarks_remarks.getText().toString().trim();

                if (JavaUtil.containsEmoji(remarks)) {
                    AppUtils.showToastStr(mContext, getString(R.string.enter_your_enjoy));
                } else {
                    ChangeRemarks(remarks);
                }


                break;

        }
    }

    private void initData() {

        Intent intent = getIntent();
        if (!JavaUtil.checkIsNull(intent.getStringExtra(IntentConstants.FriendDataId))) {
            FriendDataId = intent.getStringExtra(IntentConstants.FriendDataId);
        }

        if (!JavaUtil.checkIsNull(intent.getStringExtra(IntentConstants.FriendFId))) {
            FriendUid = intent.getStringExtra(IntentConstants.FriendFId);
        }

        if (!JavaUtil.checkIsNull(intent.getStringExtra(IntentConstants.FriendUserName))) {
            FriendNickName = intent.getStringExtra(IntentConstants.FriendUserName);
        }
        if (!JavaUtil.checkIsNull(intent.getStringExtra(IntentConstants.FriendRemarksName))) {
            FriendRemarksName = intent.getStringExtra(IntentConstants.FriendRemarksName);
        }

        if (!JavaUtil.checkIsNull(intent.getStringExtra(IntentConstants.FriendUserHeadUrl))) {
            friendHearUrl = intent.getStringExtra(IntentConstants.FriendUserHeadUrl);
        }


        if (!JavaUtil.checkIsNull(FriendRemarksName)) {
            et_remarks_remarks.setText(FriendRemarksName);

        } else {
            et_remarks_remarks.setText("");
        }


    }


    /**
     * 修改备注
     */
    private void ChangeRemarks(final String remarks) {

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.handleFriendToRemarks(FriendDataId, FriendUid, remarks);

        MyLog.i(TAG, "请求接口-删除好友 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "请求接口-删除好友 result = " + result);

                        waitDialog.close();

                        NewFriendInfoBean mNewFriendInfoBean = ResultJson.NewFriendInfoBean(result);

                        //请求成功
                        if (mNewFriendInfoBean.isRequestSuccess()) {

                            if (mNewFriendInfoBean.isNewFriendSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-搜索好友 成功");
                                BaseApplication.isAddFriend = true;


                                Intent intent = new Intent(mContext, FriendInfoHandleActivity.class);
                                intent.putExtra(IntentConstants.FriendRemarksName, remarks);
                                setResult(FriendRankInfoActivity.FriendRemarksNameCode, intent);

                                finish();

                            } else if (mNewFriendInfoBean.isNewFriendSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-搜索好友 失败");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else if (mNewFriendInfoBean.isNewFriendSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-搜索好友 无数据");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else {
                                MyLog.i(TAG, "请求接口-搜索好友 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }

                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-搜索好友 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }


                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "请求接口-删除好友 请求失败 = message = " + arg0.getMessage());
                        waitDialog.close();
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);


                        return;
                    }
                });


    }


}
