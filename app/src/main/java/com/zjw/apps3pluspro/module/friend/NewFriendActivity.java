package com.zjw.apps3pluspro.module.friend;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.adapter.NewFriendListAdapter;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.NewFriendInfoBean;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

import java.util.List;


/**
 * 别人的好友请求列表
 */
public class NewFriendActivity extends BaseActivity implements OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "NewFriendActivity";
    private Context mContext;

    private WaitDialog waitDialog;
    //    //下拉刷新
    private SwipeRefreshLayout swipe_new_friend;
    private ListView new_friend_list;
    private LinearLayout ll_new_fiend_tip;
    private NewFriendListAdapter mNewFriendListAdapter;
    private LinearLayout include_no_data;
    private ImageView public_no_data_img;
    private TextView public_no_data_text;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_new_friend;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = NewFriendActivity.this;
        waitDialog = new WaitDialog(mContext);

        initView();
        initSetAdapter();
        initData();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }

    @Override
    protected void onDestroy() {
        waitDialog.dismiss();
        super.onDestroy();
    }

    private void initView() {

        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.care_accept_frend));
        findViewById(R.id.public_head_back).setOnClickListener(this);

        swipe_new_friend = (SwipeRefreshLayout) findViewById(R.id.swipe_new_friend);
        swipe_new_friend.setColorSchemeColors(Color.GRAY, Color.GREEN);
        swipe_new_friend.setOnRefreshListener(this);
        new_friend_list = (ListView) findViewById(R.id.new_friend_list);
        ll_new_fiend_tip = (LinearLayout) findViewById(R.id.ll_new_fiend_tip);
        include_no_data = (LinearLayout) findViewById(R.id.include_no_data);
        public_no_data_img = (ImageView) findViewById(R.id.public_no_data_img);
        public_no_data_text = (TextView) findViewById(R.id.public_no_data_text);
        public_no_data_img.setBackgroundResource(R.drawable.my_icon_no_network);
        public_no_data_text.setText(getString(R.string.rank_no_friend));


    }

    private void initData() {

        getNewFriendListData();
    }

    void initSetAdapter() {

        mNewFriendListAdapter = new NewFriendListAdapter(mContext, mListener_one, mListener_two);
        new_friend_list.setAdapter(mNewFriendListAdapter);

    }


    /**
     * 请求-获取好友请求列表
     */
    private void getNewFriendListData() {


        waitDialog.show(getString(R.string.loading0));


        RequestInfo mRequestInfo = RequestJson.requestFriendsList();

        MyLog.i(TAG, "请求接口-好友添加申请列表 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub


                        closeSwipeRefresh();

                        MyLog.i(TAG, "请求接口-好友申请列表 result = " + result.toString());

                        NewFriendInfoBean mNewFriendInfoBean = ResultJson.NewFriendInfoBean(result);

                        //请求成功
                        if (mNewFriendInfoBean.isRequestSuccess()) {

                            if (mNewFriendInfoBean.isNewFriendSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-搜索好友 成功");
                                ResultNewFriendDataParsing(mNewFriendInfoBean.getData());
                            } else if (mNewFriendInfoBean.isNewFriendSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-搜索好友 失败");
                                updateUi(0);
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else if (mNewFriendInfoBean.isNewFriendSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-搜索好友 无数据");
                                updateUi(0);
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else {
                                MyLog.i(TAG, "请求接口-搜索好友 请求异常(1)");
                                updateUi(0);
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

                        MyLog.i(TAG, "请求接口-好友添加申请列表 请求失败 = message = " + arg0.getMessage());
                        closeSwipeRefresh();
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });


    }

    private void ResultNewFriendDataParsing(List<NewFriendInfoBean.DataBean> mDataBean) {


        MyLog.i(TAG, "请求接口-添加列表 = 解析 = msg = " + mDataBean.toString());

        if (mDataBean != null && (mDataBean.size() > 0)) {


            mNewFriendListAdapter.clear();
            mNewFriendListAdapter.setmDataBean(mDataBean);
            mNewFriendListAdapter.notifyDataSetChanged();

            updateUi(1);

        } else {

            updateUi(0);

            MyLog.i(TAG, "请求接口-添加列表 = 解析 = data = null");
        }


    }

    /**
     * @param type 1 = 有数据，0=无数据。
     */
    void updateUi(int type) {

        if (type == 1) {
            new_friend_list.setVisibility(View.VISIBLE);
            ll_new_fiend_tip.setVisibility(View.VISIBLE);
            include_no_data.setVisibility(View.GONE);
        } else {
            new_friend_list.setVisibility(View.GONE);
            ll_new_fiend_tip.setVisibility(View.GONE);
            include_no_data.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 处理新好友数据
     *
     * @param data_id
     * @param friend_id
     * @param state
     */
    public void HandleFriendData(String data_id, String friend_id, final String state) {


        RequestInfo mRequestInfo = RequestJson.handleFriend(data_id, friend_id, state);


        MyLog.i(TAG, "请求接口 处理新好友 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub


                        closeSwipeRefresh();

                        MyLog.i(TAG, "请求接口-处理新好友数据 result = " + result.toString());

                        NewFriendInfoBean mNewFriendInfoBean = ResultJson.NewFriendInfoBean(result);

                        //请求成功
                        if (mNewFriendInfoBean.isRequestSuccess()) {

                            if (mNewFriendInfoBean.isNewFriendSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-处理新好友数据 成功");
                                BaseApplication.isAddFriend = true;
                                if (state.equals("1")) {
                                    AppUtils.showToast(mContext, R.string.already_added);
                                } else {
                                    AppUtils.showToast(mContext, R.string.care_ignored);
                                }
                                finish();
                            } else if (mNewFriendInfoBean.isNewFriendSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-处理新好友数据 失败");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else if (mNewFriendInfoBean.isNewFriendSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-处理新好友数据 无数据");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else {
                                MyLog.i(TAG, "请求接口-处理新好友数据 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }

                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-处理新好友数据 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }


                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "请求接口-接受好友 请求失败 = message = " + arg0.getMessage());

                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });


    }


    //下拉触发
    public void onRefresh() {
        getNewFriendListData();

    }

    //关闭刷新
    private void closeSwipeRefresh() {

        waitDialog.close();

        if (swipe_new_friend != null
                && swipe_new_friend.isRefreshing()) {
            swipe_new_friend.setRefreshing(false);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.public_head_back:
                finish();
                break;


        }
    }





    /**
     * 同意
     */
    private NewFriendListAdapter.MyClickListenerOne mListener_one = new NewFriendListAdapter.MyClickListenerOne() {
        @Override
        public void myOnClick(int position, View v) {


            MyLog.i(TAG, "同意 点击事件 = getReqUserId = " + String.valueOf(mNewFriendListAdapter.getDevice(position).getReqUserId()));

            HandleFriendData(String.valueOf(mNewFriendListAdapter.getDevice(position).getId()),
                    String.valueOf(mNewFriendListAdapter.getDevice(position).getReqUserId())
                    , "1"
            );

        }
    };

    /**
     * 拒绝
     */
    private NewFriendListAdapter.MyClickListenerTwo mListener_two = new NewFriendListAdapter.MyClickListenerTwo() {
        @Override
        public void myOnClick(int position, View v) {


            MyLog.i(TAG, "拒绝 点击事件 = getReqUserId = " + String.valueOf(mNewFriendListAdapter.getDevice(position).getReqUserId()));

            HandleFriendData(String.valueOf(mNewFriendListAdapter.getDevice(position).getId()),
                    String.valueOf(mNewFriendListAdapter.getDevice(position).getReqUserId())
                    , "2"
            );

        }
    };

}
