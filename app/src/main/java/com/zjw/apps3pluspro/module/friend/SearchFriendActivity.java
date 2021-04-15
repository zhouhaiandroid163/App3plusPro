package com.zjw.apps3pluspro.module.friend;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

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
import com.zjw.apps3pluspro.view.CircleImageView;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

import butterknife.BindView;


/**
 * 搜索好友
 */
public class SearchFriendActivity extends BaseActivity implements OnClickListener {
    private final String TAG = SearchFriendActivity.class.getSimpleName();
    private Context mContext;

    private WaitDialog waitDialog;

    private EditText add_edit_search;

    private LinearLayout lin_search;
    @BindView(R.id.layoutInfo)
    RelativeLayout layoutInfo;
    private String search_user_id;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_search_friend;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = SearchFriendActivity.this;
        waitDialog = new WaitDialog(mContext);
        initView();
        initData();
        MyLog.i(TAG, "测试ID = " + MyUtils.encryptionUid("163226"));
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

    @Override
    protected void onDestroy() {
        waitDialog.dismiss();
        super.onDestroy();
    }

    private void initView() {

        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.title_add_friend));
        findViewById(R.id.public_head_back).setOnClickListener(this);

        add_edit_search = (EditText) findViewById(R.id.add_edit_search);

        lin_search = (LinearLayout) findViewById(R.id.lin_search);
        lin_search.setOnClickListener(this);


    }

    private void initData() {


        // 搜索好友
        add_edit_search.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {


                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    /* 隐藏软键盘 */
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }

                    String searchContent = add_edit_search.getText()
                            .toString().trim();

                    if (!JavaUtil.checkIsNull(searchContent)) {
                        MyLog.i(TAG, "搜索ID");
                        searchUser(searchContent);
                    }

                    return true;
                }

                return false;
            }

        });

        add_edit_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                MyLog.i(TAG, " str1 = " + charSequence.toString().length());

                if (charSequence != null && charSequence.toString() != null && !charSequence.toString().equals("") && charSequence.toString().length() > 0) {
                    lin_search.setVisibility(View.VISIBLE);
                } else {
                    lin_search.setVisibility(View.GONE);
                }
                // 输入的内容变化的监听
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 输入前的监听
//                MyLog.i(TAG, " str2 =  " + charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 输入后的监听

            }
        });


    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.public_head_back:
                finish();
                break;

            case R.id.lin_search:
                String searchContent = add_edit_search.getText().toString().trim();
                if (!JavaUtil.checkIsNull(searchContent)) {
                    MyLog.i(TAG, "搜索ID");
                    searchUser(searchContent);
                }
                break;


        }
    }


    /**
     * 根据搜索关键字查询好友
     *
     * @param searchContent
     */
    private void searchUser(String searchContent) {

        MyLog.i(TAG, "请求接口-搜索好友 关键字 = " + searchContent);

        //判断是否是数字
        boolean isNumber = JavaUtil.isNumeric(searchContent);
        MyLog.i(TAG, "请求接口-搜索好友 是否是数字 = " + isNumber);

        String friend_user_id = MyUtils.decryptionUid(searchContent);
        MyLog.i(TAG, "请求接口-搜索好友 解密后，用户ID = " + friend_user_id);

        if (isNumber && !JavaUtil.checkIsNull(friend_user_id)) {
            if (!friend_user_id.equals(BaseApplication.getUserId())) {
                reestSearchFriend(friend_user_id);
            } else {
                AppUtils.showToast(mContext, R.string.no_add_oneself);
            }


        } else {
            AppUtils.showToast(mContext, R.string.the_account_is_not_registered);
        }

    }

    void reestSearchFriend(String friend_user_id) {
        waitDialog.show(getString(R.string.loading0));

        layoutInfo.setVisibility(View.GONE);

        RequestInfo mRequestInfo = RequestJson.searchFriends(friend_user_id);

        MyLog.i(TAG, "请求接口-搜索好友 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "请求接口-搜索好友 请求成功 = result = " + result.toString());

                        waitDialog.close();

                        SearchFriendInfoBean mSearchFriendInfoBean = ResultJson.SearchFriendInfoBean(result);

                        //请求成功
                        if (mSearchFriendInfoBean.isRequestSuccess()) {

                            if (mSearchFriendInfoBean.isSearchFirendSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-搜索好友 成功");
                                ResultSearchFriendDataParsing(mSearchFriendInfoBean.getData());
                            } else if (mSearchFriendInfoBean.isSearchFirendSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-搜索好友 失败");
                                AppUtils.showToast(mContext, R.string.server_try_again_code0);
                            } else if (mSearchFriendInfoBean.isSearchFirendSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-搜索好友 无数据");
                                AppUtils.showToast(mContext, R.string.the_account_is_not_registered);
                            } else {
                                MyLog.i(TAG, "请求接口-搜索好友 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.server_try_again_code0);
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

                        MyLog.i(TAG, "请求接口-搜索好友 请求失败 = message = " + arg0.getMessage());
                        waitDialog.close();

                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });
    }


    /**
     * 解析服务器返回的数据
     */
    @BindView(R.id.tv_user_name)
    TextView tv_user_name;
    @BindView(R.id.tv_user_id)
    TextView tv_user_id;
    @BindView(R.id.ci_mines_head)
    CircleImageView ci_mines_head;
    @BindView(R.id.button_add_friend)
    Button button_add_friend;
    private void ResultSearchFriendDataParsing(SearchFriendInfoBean.DataBean DataBean) {

        MyLog.i(TAG, "解析 搜索好友 getFriendUserId = " + DataBean.getFriendUserId());
        MyLog.i(TAG, "解析 搜索好友 getNickName = " + DataBean.getNickName());
        MyLog.i(TAG, "解析 搜索好友 getIconUrl = " + DataBean.getIconUrl());
        MyLog.i(TAG, "解析 搜索好友 getReqStatus = " + DataBean.getReqStatus());

        layoutInfo.setVisibility(View.VISIBLE);
        tv_user_name.setText(DataBean.getNickName());
        search_user_id = String.valueOf(DataBean.getFriendUserId());

        if (!JavaUtil.checkIsNull(search_user_id)) {
            tv_user_id.setText(MyUtils.encryptionUid(search_user_id));
        }

        String search_user_head_url = DataBean.getIconUrl();
        if (!JavaUtil.checkIsNull(search_user_head_url)) {

            BitmapUtils bitmapUtils = new BitmapUtils(mContext);
            bitmapUtils.display(ci_mines_head, search_user_head_url);

        } else {
            ci_mines_head.setImageResource(R.drawable.default_header);
        }

        String search_user_state = String.valueOf(DataBean.getReqStatus());
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
                    button_add_friend.setBackgroundResource(R.drawable.bt_gradient_bg);
                    button_add_friend.setText(getText(R.string.add));
                    break;
                //已申请
                case "2":
                    button_add_friend.setEnabled(false);
                    button_add_friend.setBackgroundResource(R.drawable.my_button_bg_no_enable);
                    button_add_friend.setText(getText(R.string.already_issued));
                    break;
            }


        }
        button_add_friend.setOnClickListener(v -> {
            if (search_user_state.equals("1")) {
                addFriend();
            }
        });
    }
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
