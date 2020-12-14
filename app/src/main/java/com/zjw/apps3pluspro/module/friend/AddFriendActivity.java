package com.zjw.apps3pluspro.module.friend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
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
import com.zjw.apps3pluspro.network.javabean.SearchFriendInfoBean;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;


/**
 * 添加好友
 */
public class AddFriendActivity extends BaseActivity implements OnClickListener {
    private final String TAG = AddFriendActivity.class.getSimpleName();
    private Context mContext;

    private WaitDialog waitDialog;
    private ImageView ci_mines_head;
    private TextView tv_user_id, tv_user_name;
    private Button button_add_friend;

    private String search_user_id = "";
    private String search_user_name = "";
    private String search_user_head_url = "";
    private String search_user_state = "";

    @Override
    protected void initViews() {
        super.initViews();
        mContext = AddFriendActivity.this;
        waitDialog = new WaitDialog(mContext);
        initView();
        initData();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_add_friend;
    }

    public void onResume() {
        super.onResume();
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

        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.detailed_info));
        findViewById(R.id.public_head_back).setOnClickListener(this);

        ci_mines_head = (ImageView) findViewById(R.id.ci_mines_head);
        tv_user_id = (TextView) findViewById(R.id.tv_user_id);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        button_add_friend = (Button) findViewById(R.id.button_add_friend);
        button_add_friend.setOnClickListener(this);
    }

    private void initData() {

        Intent intent = getIntent();

        if (!JavaUtil.checkIsNull(intent.getStringExtra(IntentConstants.FriendFId))) {
            search_user_id = intent.getStringExtra(IntentConstants.FriendFId);
        }

        if (!JavaUtil.checkIsNull(intent.getStringExtra(IntentConstants.FriendUserName))) {
            search_user_name = intent.getStringExtra(IntentConstants.FriendUserName);
        }

        if (!JavaUtil.checkIsNull(intent.getStringExtra(IntentConstants.FriendUserHeadUrl))) {
            search_user_head_url = intent.getStringExtra(IntentConstants.FriendUserHeadUrl);
        }

        if (intent.getStringExtra(IntentConstants.FriendState) != null && !intent.getStringExtra(IntentConstants.FriendState).equals("")) {
            search_user_state = intent.getStringExtra(IntentConstants.FriendState);
        }

        if (!JavaUtil.checkIsNull(search_user_head_url)) {

            BitmapUtils bitmapUtils = new BitmapUtils(mContext);
            bitmapUtils.display(ci_mines_head, search_user_head_url);

        } else {
            ci_mines_head.setImageResource(R.drawable.default_header);
        }

        if (!JavaUtil.checkIsNull(search_user_name)) {
            tv_user_name.setText(search_user_name);
        }
        if (!JavaUtil.checkIsNull(search_user_id)) {
            tv_user_id.setText(MyUtils.encryptionUid(search_user_id));
        }

        MyLog.i(TAG, "search_user_id = " + search_user_id);
        MyLog.i(TAG, "search_user_name = " + search_user_name);
        MyLog.i(TAG, "search_user_head_url = " + search_user_head_url);
        MyLog.i(TAG, "search_user_state = " + search_user_state);


        if (!search_user_state.equals("")) {

            switch (search_user_state) {
                //已是好友
                case "0":
                    button_add_friend.setEnabled(false);
                    button_add_friend.setBackgroundResource(R.drawable.my_button_bg_no_enable);
                    button_add_friend.setText(getText(R.string.already_added));
                    break;
                //未申请
                case "1":
                    button_add_friend.setEnabled(true);
                    button_add_friend.setBackgroundResource(R.drawable.my_button1_selector);
                    button_add_friend.setText(getText(R.string.add));
                    break;
                //已申请
                case "2":
                    button_add_friend.setEnabled(false);
                    button_add_friend.setBackgroundResource(R.drawable.my_button_bg_no_enable);
                    button_add_friend.setText(getText(R.string.already_issued));
                    break;
//                //未处理
//                case "3":
//                    button_add_friend.setEnabled(false);
//                    button_add_friend.setBackgroundResource(R.drawable.my_button_bg_no_enable);
//                    button_add_friend.setText("未处理");
//                    break;
//                //添加过，已删除，重新添加
////                case "4":
////                    button_add_friend.setEnabled(true);
////                    button_add_friend.setBackgroundResource(R.drawable.my_button1_selector);
////                    button_add_friend.setText("已添加，恢复");
////                    break;
            }


        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.public_head_back:
                finish();
                break;

            //发出好友请求
            case R.id.button_add_friend:
                MyLog.i(TAG, "触发按钮 search_user_state = " + search_user_state);
                //未申请-发出请求
                if (search_user_state.equals("1")) {
                    addFriend();
                }
                break;


        }
    }


    /**
     * 请求-添加好友
     */
    void addFriend() {

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.addFriends(search_user_id, getString(R.string.new_friend_tip));

        MyLog.i(TAG, "请求接口-添加好友 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

            @Override
            public void onMySuccess(JSONObject result) {
                // TODO Auto-generated method stub

                MyLog.i(TAG, "请求接口-添加好友 请求成功 = result = " + result.toString());

                waitDialog.close();

                SearchFriendInfoBean mSearchFriendInfoBean = ResultJson.SearchFriendInfoBean(result);

                //请求成功
                if (mSearchFriendInfoBean.isRequestSuccess()) {

                    if (mSearchFriendInfoBean.isSearchFirendSuccess() == 1) {
                        AppUtils.showToast(mContext, R.string.already_issued);
                        MyLog.i(TAG, "请求接口-添加好友 成功");
                        finish();
                    } else if (mSearchFriendInfoBean.isSearchFirendSuccess() == 0) {
                        MyLog.i(TAG, "请求接口-添加好友 失败");
                        AppUtils.showToast(mContext, R.string.data_try_again_code1);
                    } else if (mSearchFriendInfoBean.isSearchFirendSuccess() == 2) {
                        MyLog.i(TAG, "请求接口-添加好友 无数据");
                        AppUtils.showToast(mContext, R.string.data_try_again_code1);
                    } else {
                        MyLog.i(TAG, "请求接口-添加好友 请求异常(1)");
                        AppUtils.showToast(mContext, R.string.data_try_again_code1);
                    }

                    //请求失败
                } else {
                    MyLog.i(TAG, "请求接口-添加好友 请求异常(0)");
                    AppUtils.showToast(mContext, R.string.server_try_again_code0);
                }

            }

            @Override
            public void onMyError(VolleyError arg0) {
                // TODO Auto-generated method stub
                waitDialog.close();
                MyLog.i(TAG, "请求接口-添加好友 请求失败 = message = " + arg0.getMessage());
                AppUtils.showToast(mContext, R.string.net_worse_try_again);
                return;
            }
        });

    }


}
