package com.zjw.apps3pluspro.module.friend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.lidroid.xutils.BitmapUtils;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.NewFriendInfoBean;
import com.zjw.apps3pluspro.utils.DialogUtils;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.CircleImageView;

import org.json.JSONObject;


/**
 * 好友的详情界面
 */
public class FriendInfoHandleActivity extends BaseActivity implements OnClickListener {
    private final String TAG = FriendInfoHandleActivity.class.getSimpleName();
    private Context mContext;

    private WaitDialog waitDialog;
    private CircleImageView iv_handle_friend_head;

//    public static final int FriendRemarksNameCode = 100;//好友备注

    private String FriendDataId;//数据ID
    private String FriendUid;//好友ID
    private String FriendNickName;//好友昵称
    private String FriendRemarksName;//好友备注
    private String friendHearUrl;//好友头像

    private TextView tv_handle_fullname;
    private TextView tv_handle_nickname;
    private TextView tv_handle_fid;
    private TextView tv_handle_remarks;

    @Override
    protected void initViews() {
        super.initViews();
        mContext = FriendInfoHandleActivity.this;
        waitDialog = new WaitDialog(mContext);

        initView();
        initData();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_handle_friend_info;
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
        ((TextView) (findViewById(R.id.public_head_title))).setText(getText(R.string.care_info));
        iv_handle_friend_head = (CircleImageView) findViewById(R.id.iv_handle_friend_head);

        tv_handle_fullname = (TextView) findViewById(R.id.tv_handle_fullname);
        tv_handle_nickname = (TextView) findViewById(R.id.tv_handle_nickname);
        tv_handle_fid = (TextView) findViewById(R.id.tv_handle_fid);
        tv_handle_remarks = (TextView) findViewById(R.id.tv_handle_remarks);

        findViewById(R.id.ll_handle_delete_friend).setOnClickListener(this);
        findViewById(R.id.rl_handle_change_remarks).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            //返回
            case R.id.public_head_back:
                Intent intent = new Intent(mContext, FriendRankInfoActivity.class);
                intent.putExtra(IntentConstants.FriendRemarksName, FriendRemarksName);
                setResult(FriendRankInfoActivity.FriendRemarksNameCode, intent);
                finish();
                break;

            //跳转修改备注
            case R.id.rl_handle_change_remarks:
                Intent mIntent = new Intent(mContext, FriendInfoRemarksActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(IntentConstants.FriendDataId, FriendDataId);
                bundle.putString(IntentConstants.FriendFId, FriendUid);
                bundle.putString(IntentConstants.FriendUserName, FriendNickName);
                bundle.putString(IntentConstants.FriendRemarksName, FriendRemarksName);
                bundle.putString(IntentConstants.FriendUserHeadUrl, friendHearUrl);
                mIntent.putExtras(bundle);
//                startActivity(mIntent);
                startActivityForResult(mIntent, FriendRankInfoActivity.FriendRemarksNameCode);
                break;

            //删除好友
            case R.id.ll_handle_delete_friend:
                showDeleteFirendDialog();
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

        if (friendHearUrl != null) {
            BitmapUtils bitmapUtils = new BitmapUtils(mContext);
            bitmapUtils.display(iv_handle_friend_head, friendHearUrl);
        }

        updateUi();


    }

    void updateUi() {
        if (!JavaUtil.checkIsNull(FriendNickName)) {
            tv_handle_fullname.setText(FriendNickName);
            if (!JavaUtil.checkIsNull(FriendRemarksName)) {
                tv_handle_fullname.setText(FriendRemarksName);
            } else {
                tv_handle_fullname.setText(FriendNickName);
            }
        } else {
            tv_handle_fullname.setText("");
        }

        if (!JavaUtil.checkIsNull(FriendNickName)) {
            tv_handle_nickname.setText(FriendNickName);
        } else {
            tv_handle_nickname.setText("");
        }

        if (!JavaUtil.checkIsNull(FriendRemarksName)) {
            tv_handle_remarks.setText(FriendRemarksName);
        } else {
            tv_handle_remarks.setText("");
        }

        if (!JavaUtil.checkIsNull(FriendUid)) {
            tv_handle_fid.setText(MyUtils.encryptionUid(FriendUid));
        } else {
            tv_handle_fid.setText("");
        }
    }


    /**
     * 删除好友对话框
     */
    void showDeleteFirendDialog() {
        DialogUtils.BaseDialog(mContext, mContext.getResources().getString(R.string.dialog_prompt),
                mContext.getResources().getString(R.string.delete_friend_content),
                mContext.getDrawable(R.drawable.black_corner_bg), new DialogUtils.DialogClickListener() {
                    @Override
                    public void OnOK() {
                        deleteFriend();
                    }
                    @Override
                    public void OnCancel() {
                    }
                });

    }


    /**
     * 删除好友
     */
    private void deleteFriend() {

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.handleFriend(FriendDataId, FriendUid, "3");

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

                                Intent intent = new Intent(mContext, FriendRankInfoActivity.class);
                                intent.putExtra(FriendRankInfoActivity.DeleteTag, FriendRankInfoActivity.DeleteTag_YES);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //添加闹钟
        if (resultCode == FriendRankInfoActivity.FriendRemarksNameCode) {

            FriendRemarksName = data.getStringExtra(IntentConstants.FriendRemarksName);

            updateUi();


        }

    }


}
