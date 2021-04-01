package com.zjw.apps3pluspro.module.friend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.lidroid.xutils.BitmapUtils;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.adapter.FriendListAdapter;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseFragment;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.FriendListBean;
import com.zjw.apps3pluspro.network.javabean.NewFriendInfoBean;
import com.zjw.apps3pluspro.network.okhttp.MyOkHttpClient;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.CircleImageView;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;


/**
 * 好友主界面
 */
public class FriendRankFragment extends BaseFragment implements OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = FriendRankFragment.class.getSimpleName();


    private TextView public_head_title;

    //刷新
    private SwipeRefreshLayout swipe_friend;

    //好友为空
    private LinearLayout ll_frgment_friend_no_data;

    //好友列表
    private ListView friend_listview;
    private FriendListAdapter mFriendListAdapter;

    //新好友
    private LinearLayout friend_is_new;
    private CircleImageView ci_friend_new_head;
    private TextView text_new_friend_nickname;
    private TextView text_new_friend_request_str;
    private TextView text_new_friend_number;

    @BindView(R.id.tvErrorTip)
    TextView tvErrorTip;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //为了自动刷新，不判断，每次唤醒都刷新
//        if (BaseApplication.isAddFriend) {
//            BaseApplication.isAddFriend = false;
        try {
            getFriendList();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }


    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
        closeSwipeRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @BindView(R.id.tvTitleTop)
    TextView tvTitleTop;
    @BindView(R.id.btAdd)
    Button btAdd;
    @BindView(R.id.btn_fragment_friend_add)
    Button btn_fragment_friend_add;

    @Override
    public View initView() {
        view = View.inflate(context, R.layout.fragment_friend, null);

        friend_listview = (ListView) view.findViewById(R.id.friend_listview);
        friend_is_new = (LinearLayout) view.findViewById(R.id.friend_is_new);

        ci_friend_new_head = (CircleImageView) view.findViewById(R.id.ci_friend_new_head);
        text_new_friend_nickname = (TextView) view.findViewById(R.id.text_new_friend_nickname);
        text_new_friend_request_str = (TextView) view.findViewById(R.id.text_new_friend_request_str);
        text_new_friend_number = (TextView) view.findViewById(R.id.text_new_friend_number);
        ll_frgment_friend_no_data = (LinearLayout) view.findViewById(R.id.ll_frgment_friend_no_data);

        swipe_friend = (SwipeRefreshLayout) view.findViewById(R.id.swipe_friend);
        swipe_friend.setColorSchemeColors(Color.GRAY, Color.GREEN);
        swipe_friend.setOnRefreshListener(this);

        friend_is_new.setOnClickListener(this);

        view.findViewById(R.id.btn_fragment_friend_add).setOnClickListener(this);


        return view;
    }

    void initSetAdapter() {
        mFriendListAdapter = new FriendListAdapter(context);
        friend_listview.setAdapter(mFriendListAdapter);
        friend_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                MyLog.i(TAG, "点到了 = getCount = " + friend_listview.getCount());
                MyLog.i(TAG, "点到了 = getDevice = " + mFriendListAdapter.getDevice(arg2).toString());

                Intent mIntent = new Intent(context, FriendRankInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(IntentConstants.FriendDataId, String.valueOf(mFriendListAdapter.getDevice(arg2).getId()));
                bundle.putString(IntentConstants.FriendFId, String.valueOf(mFriendListAdapter.getDevice(arg2).getFriendUserId()));
                bundle.putString(IntentConstants.FriendUserName, mFriendListAdapter.getDevice(arg2).getNickName());
                bundle.putString(IntentConstants.FriendRemarksName, mFriendListAdapter.getDevice(arg2).getNickNameRename());
                bundle.putString(IntentConstants.FriendUserHeadUrl, mFriendListAdapter.getDevice(arg2).getIconUrl());
                mIntent.putExtras(bundle);
                startActivity(mIntent);

            }
        });


    }

    @Override
    public void initData() {
        initSetAdapter();
        swipe_friend.setRefreshing(true);
//        BaseApplication.isAddFriend = false;
//        getFriendList();

        tvTitleTop.setText(context.getString(R.string.title_care));
        btAdd.setOnClickListener(this);
        btn_fragment_friend_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            // 跳转到添加搜索好友界面
            case R.id.public_friend:
                startActivity(new Intent(context, SearchFriendActivity.class));
                break;

            //新好友
            case R.id.friend_is_new:
                startActivity(new Intent(context, NewFriendActivity.class));
                break;

            // 跳转到添加搜索好友界面
            case R.id.btn_fragment_friend_add:
            case R.id.btAdd:
                startActivity(new Intent(context, SearchFriendActivity.class));
                break;

        }
    }


    @Override
    public void onRefresh() {
        MyLog.i(TAG, "onRefresh()");
        try {
            getFriendList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 请求-获取好友列表
     */
    private void getFriendList() {
        MyLog.i(TAG, "getFriendList()");


        RequestInfo mRequestInfo = RequestJson.getFriendsList();

        MyLog.i(TAG, "请求接口-好友列表 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "请求接口-好友列表 请求成功 = result = " + result.toString());

                        FriendListBean mFriendListBean = ResultJson.FriendListBean(result);

                        //请求成功
                        if (mFriendListBean.isRequestSuccess()) {

                            if (mFriendListBean.isFriendListSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-搜索好友 成功");
                                ResultFriendListDataParsing(mFriendListBean.getData());
                            } else if (mFriendListBean.isFriendListSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-搜索好友 失败");
                                updateUi(0);
                            } else if (mFriendListBean.isFriendListSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-搜索好友 无数据");
                                updateUi(0);
                            } else {
                                MyLog.i(TAG, "请求接口-搜索好友 请求异常(1)");
                                updateUi(0);
                            }

                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-搜索好友 请求异常(0)");

                        }
                        getNewFriendListData();
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        closeSwipeRefresh();

                        updateUi(1);

                        MyLog.i(TAG, "请求接口-好友列表 请求失败 = message = " + arg0.getMessage());

//                        BengXinUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });
    }


    /**
     * 解析服务器返回的数据
     */
    private void ResultFriendListDataParsing(List<FriendListBean.DataBean> mDataBean) {

        MyLog.i(TAG, "请求接口-好友列表 = 解析 = mDataBean = " + mDataBean.toString());

        if (mDataBean != null && (mDataBean.size() > 0)) {
//            MyLog.i(TAG, "请求接口-好友列表 = 解析 = data = " + mFriendListBean.getData().toString());
            updateUi(2);
            mFriendListAdapter.clear();
            mFriendListAdapter.setmDataBean(mDataBean);
            mFriendListAdapter.notifyDataSetChanged();


        } else {

            MyLog.i(TAG, "请求接口-好友列表 = 解析 = data = null");
            updateUi(0);
        }


    }

    /**
     * 请求-获取好友请求列表
     */
    private void getNewFriendListData() {

        MyLog.i(TAG, "getNewFriendListData()");

        RequestInfo mRequestInfo = RequestJson.requestFriendsList();

        MyLog.i(TAG, "请求接口-好友申请列表 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

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
                                friend_is_new.setVisibility(View.GONE);
                            } else if (mNewFriendInfoBean.isNewFriendSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-搜索好友 无数据");
                                friend_is_new.setVisibility(View.GONE);
                            } else {
                                MyLog.i(TAG, "请求接口-搜索好友 请求异常(1)");
                                friend_is_new.setVisibility(View.GONE);
                            }

                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-搜索好友 请求异常(0)");
                        }

                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "请求接口-好友添加申请列表 请求失败 = message = " + arg0.getMessage());
                        closeSwipeRefresh();
//                        BengXinUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });


    }


    /**
     * 解析新好友列表
     *
     * @param mDataBean
     */
    private void ResultNewFriendDataParsing(List<NewFriendInfoBean.DataBean> mDataBean) {

        MyLog.i(TAG, "请求接口-添加列表 = 解析 = msg = " + mDataBean.toString());

        if (mDataBean != null && (mDataBean.size() > 0)) {

            NewFriendInfoBean.DataBean my_data_bean = mDataBean.get(0);

            // 设置头像
            if (!TextUtils.isEmpty(my_data_bean.getIconUrl())) {
                new BitmapUtils(context).display(ci_friend_new_head, my_data_bean.getIconUrl());
            }

            if (!JavaUtil.checkIsNull(my_data_bean.getNickName())) {
                text_new_friend_nickname.setText(my_data_bean.getNickName());
            } else {
                text_new_friend_nickname.setText("");
            }

            if (!JavaUtil.checkIsNull(my_data_bean.getReqMsg())) {
                text_new_friend_request_str.setText(my_data_bean.getReqMsg());
            } else {
                text_new_friend_request_str.setText("");
            }

            friend_is_new.setVisibility(View.VISIBLE);
            btAdd.setVisibility(View.VISIBLE);
            ll_frgment_friend_no_data.setVisibility(View.GONE);
            text_new_friend_number.setText(String.valueOf(mDataBean.size()));

        } else {
            friend_is_new.setVisibility(View.GONE);
            btAdd.setVisibility(View.GONE);
            MyLog.i(TAG, "请求接口-添加列表 = 解析 = data = null");
        }

    }


    /**
     * @param state 0 = 没有好友 1=没网络 2=有好友
     */
    void updateUi(int state) {
        try {
            tvErrorTip.setText(getResources().getString(R.string.no_friend_tip_title));
            switch (state) {
                case 0:
                    friend_listview.setVisibility(View.GONE);
                    btAdd.setVisibility(View.GONE);
                    ll_frgment_friend_no_data.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    friend_listview.setVisibility(View.GONE);
                    btAdd.setVisibility(View.GONE);
                    ll_frgment_friend_no_data.setVisibility(View.VISIBLE);
                    if (!MyOkHttpClient.getInstance().isConnect(getActivity())) {
                        tvErrorTip.setText(getResources().getString(R.string.no_net_work));
                        tvErrorTip.setVisibility(View.VISIBLE);
                    } else {
                        tvErrorTip.setVisibility(View.GONE);
                    }
                    break;
                case 2:
                    friend_listview.setVisibility(View.VISIBLE);
                    btAdd.setVisibility(View.VISIBLE);
                    ll_frgment_friend_no_data.setVisibility(View.GONE);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void closeSwipeRefresh() {

        MyLog.i(TAG, "closeSwipeRefresh()");

        if (swipe_friend != null && swipe_friend.isRefreshing()) {
            swipe_friend.setRefreshing(false);
        }
    }


}
