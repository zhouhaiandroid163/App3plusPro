package com.zjw.apps3pluspro.module.mine.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.device.DeviceManager;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.AccountBean;
import com.zjw.apps3pluspro.network.javabean.CalibrationBean;
import com.zjw.apps3pluspro.network.javabean.UserBean;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

import butterknife.BindView;

/**
 * 登录页面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = LoginActivity.class.getSimpleName();
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    //数据库存储
    private Context mContext;
    private MyActivityManager manager;
    //登陆相关
    private EditText edit_login_username, edit_login_password;
    private CheckBox cb_login_checkbox;
    private LinearLayout ll_more_login;//第三方登录
    private String LoginType = "0";


    //加载框
    private WaitDialog waitDialog;

    @Override
    protected int setLayoutId() {
        bgColor = R.color.top_bar;
        isTextDark = false;
        return R.layout.activity_login;
    }

    @BindView(R.id.loginView)
    View loginView;
    @BindView(R.id.passwordView)
    View passwordView;

    @Override
    protected void initViews() {
        super.initViews();
        mContext = LoginActivity.this;
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        waitDialog = new WaitDialog(mContext);
        initView();

        edit_login_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edit_login_password.setTypeface(Typeface.DEFAULT);

        AppUtils.initEditTextFocusChange(edit_login_username, loginView, "#ffffff", "#1Effffff");
        AppUtils.initEditTextFocusChange(edit_login_password, passwordView, "#ffffff", "#1Effffff");
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
        super.onDestroy();
        UMShareAPI.get(this).release();
    }


    // 初始化控件
    public void initView() {

        edit_login_username = (EditText) findViewById(R.id.edit_login_username);
        edit_login_password = (EditText) findViewById(R.id.edit_login_password);
        cb_login_checkbox = (CheckBox) findViewById(R.id.cb_login_checkbox);
        ll_more_login = (LinearLayout) findViewById(R.id.ll_more_login);

        edit_login_password.setTypeface(Typeface.DEFAULT);

        //中文
        if (AppUtils.isZh(mContext)) {
//            ll_more_login.setVisibility(View.VISIBLE);
            edit_login_username.setHint(getString(R.string.input_your_mobile_phone));
        }
        //非中文
        else {
            ll_more_login.setVisibility(View.GONE);
            edit_login_username.setHint(getString(R.string.input_your_email));
        }

        cb_login_checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //选择状态 显示明文--设置为可见的密码
                edit_login_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                edit_login_password.setTypeface(Typeface.DEFAULT);
            } else {
                //默认状态显示密码--设置文本 要一起写才能i起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                edit_login_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                edit_login_password.setTypeface(Typeface.DEFAULT);
            }
        });

        findViewById(R.id.tv_login_regist).setOnClickListener(this);
        findViewById(R.id.rl_forget_paswd).setOnClickListener(this);
        findViewById(R.id.btn_login_app).setOnClickListener(this);
        findViewById(R.id.img_login_wx).setOnClickListener(this);
        findViewById(R.id.img_login_qq).setOnClickListener(this);
        findViewById(R.id.tvTourist).setOnClickListener(this);

        findViewById(R.id.tv_user).setOnClickListener(this);
        findViewById(R.id.tv_privacy).setOnClickListener(this);

        uploadRunningInfo();
    }

    private void uploadRunningInfo() {
        try {
            new Handler().postDelayed(() -> {
                RequestInfo mRequestInfo = RequestJson.uploadAppInfo(mContext);
                Log.i(TAG, "uploadAppInfo=" + mRequestInfo.toString());
                NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject result) {
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                    }
                });
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查输入的内容，
     *
     * @param input_username
     * @param input_pass
     */
    void checkInputrStr(String input_username, String input_pass) {

        String user_account = "";
        String user_password = "";
        String login_type = "1";
        //国内
        if (AppUtils.isZh(mContext)) {

            //账号为空
            if (TextUtils.isEmpty(input_username)) {
                AppUtils.showToast(mContext, R.string.input_your_mobile_phone);
                MyUtils.setFocusable(edit_login_username);
                return;
            }
            //手机号
            else if (JavaUtil.isMobileNO(input_username)) {
                user_account = input_username;
                login_type = "1";

            }
            //邮箱
            else if (JavaUtil.isEmail(input_username)) {
                user_account = input_username;
                login_type = "2";

            }
            //账号格式错误
            else {
                AppUtils.showToast(mContext, R.string.wrong_input_your_mobile_phone);
                MyUtils.setFocusable(edit_login_username);
                return;
            }

            //国外
        } else {
            //邮箱输入为空
            if (TextUtils.isEmpty(input_username)) {
                AppUtils.showToast(mContext, R.string.input_your_email);
                MyUtils.setFocusable(edit_login_username);
                return;
            }
            //邮箱
            else if (JavaUtil.isEmail(input_username)) {
                user_account = input_username;
                login_type = "2";
            }
            //邮箱格式错误
            else {
                AppUtils.showToast(mContext, R.string.wrong_input_format_email);
                MyUtils.setFocusable(edit_login_username);
                return;
            }
        }


        //密码输入为空
        if (TextUtils.isEmpty(input_pass)) {
            AppUtils.showToast(mContext, R.string.input_your_pas);
            MyUtils.setFocusable(edit_login_password);
            return;
        } else {
            user_password = input_pass;
        }

        requestLogin(user_account, user_password, login_type);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            // 登录
            case R.id.btn_login_app:
                checkInputrStr(edit_login_username.getText().toString().trim(), edit_login_password.getText().toString().trim());
                break;

            //忘记密码
            case R.id.rl_forget_paswd:
                startActivity(new Intent(LoginActivity.this, ForgetPswActivity.class));
                break;

            //注册
            case R.id.tv_login_regist:
                intent = new Intent(mContext, RegistActivity.class);
                startActivity(intent);
                break;

            //微信登录
            case R.id.img_login_wx:
                MyLog.i(TAG, "第三方登录 = 微信");
                MorePlatForm(SHARE_MEDIA.WEIXIN);
                break;
            //QQ登录
            case R.id.img_login_qq:
                MyLog.i(TAG, "第三方登录 = QQ");
                MorePlatForm(SHARE_MEDIA.QQ);
                break;
            case R.id.tvTourist:
                try {
                    String uuid = mUserSetTools.getTouristUUid();
                    if (uuid.length() == 0) {
                        uuid = System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
                        mUserSetTools.setTouristUUid(uuid);
                    }
                    LoginType = "5";
                    YMLogin(uuid, uuid, "", "", MyTime.getAllTime(), LoginType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_user:
                intent = new Intent(LoginActivity.this, UserProtocolActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_privacy:
                intent = new Intent(LoginActivity.this, PrivacyProtocolActivity.class);
                startActivity(intent);
                break;


        }
    }


    /**
     * 请求登录接口
     */
    void requestLogin(final String account, final String password, final String type) {

        waitDialog.show(getString(R.string.loading0));


        RequestInfo mRequestInfo = RequestJson.userLogin(mContext, account, password);


        MyLog.i(TAG, "请求接口-登录 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-登录 result = " + result.toString());


                        AccountBean mAccountBean = ResultJson.AccountBean(result);

                        //请求成功
                        if (mAccountBean.isRequestSuccess()) {

                            //登录成功
                            if (mAccountBean.isLoginSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-登录 成功");
                                //解析数据
                                ResultLoginDataParsing(mAccountBean, account, password, type);
                            }
                            //密码错误
                            else if (mAccountBean.isLoginSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-登录 失败-密码错误");
                                AppUtils.showToast(mContext, R.string.pasword_verification_fail);
                                MyUtils.setFocusable(edit_login_password);
                            }
                            //未注册
                            else if (mAccountBean.isLoginSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-登录 失败-未注册");
                                AppUtils.showToast(mContext, R.string.the_account_is_not_registered);
                                MyUtils.setFocusable(edit_login_username);
                            }
                            //异常
                            else {
                                MyLog.i(TAG, "请求接口-登录 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-登录 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);

                        }

                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-登录 请求失败 = message = " + arg0.getMessage());
                        waitDialog.close();
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);
                    }
                });

    }

    private void YMLogin(String openid, String uid, String name, String iconurl, String registerTime, final String LoginType) {

        RequestInfo mRequestInfo = RequestJson.thirdPartyLogin(mContext, openid, uid, name, iconurl, registerTime, LoginType);

        MyLog.i(TAG, "请求接口-第三方登录 mRequestInfo = " + mRequestInfo.toString());

        waitDialog.show(getString(R.string.loading0));

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-第三方登录 请求成功 = result = " + result.toString());
                        MyLog.i(TAG, "请求接口-第三方登录 result = " + result.toString());

                        AccountBean mAccountBean = ResultJson.AccountBean(result);

                        //请求成功
                        if (mAccountBean.isRequestSuccess()) {

                            //登录成功
                            if (mAccountBean.isMoreLoginSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-第三方登录 成功");
                                ResultLoginDataParsing(mAccountBean, "", "", LoginType);
                            }
                            //登录失败
                            else {
                                MyLog.i(TAG, "请求接口-第三方登录 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }

                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-第三方登录 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);

                        }

                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "请求接口-第三方登录 请求失败 = message = " + arg0.getMessage());

                        waitDialog.close();

                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });

    }

    /**
     * 解析服务器返回的数据
     */
    private void ResultLoginDataParsing(AccountBean mAccountBean, String account, String password, String type) {


        MyLog.i(TAG, "请求接口-登录 = 解析 = getMsg = " + mAccountBean.getMsg());
        MyLog.i(TAG, "请求接口-登录 = 解析 = getCodeMsg = " + mAccountBean.getCodeMsg());
        MyLog.i(TAG, "请求接口-登录 = 解析 = getData = " + mAccountBean.getData().toString());

        String user_id = String.valueOf(mAccountBean.getData().getUserId());
        String register_time = mAccountBean.getData().getRegisterTime();

        MyLog.i(TAG, "请求接口-登录 = 解析 = user_id = " + user_id);
        MyLog.i(TAG, "请求接口-登录 = 解析 = register_time = " + register_time);


        String authorizationCode = mAccountBean.getData().getAuthorization();
        MyLog.i(TAG, "login Authorization code" + authorizationCode);
        AccountBean.saveAccount(user_id, account, password, register_time, type, authorizationCode);

        getUserInfo();


    }

    /**
     * 请求-获取当前用户基本数据
     */
    private void getUserInfo() {

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.getUserInfo();

        MyLog.i(TAG, "请求接口-获取个人信息 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-获取个人信息 = result = " + result.toString());

                        UserBean mUserBean = ResultJson.UserBean(result);

                        //请求成功
                        if (mUserBean.isRequestSuccess()) {

                            //获取数据成功
                            if (mUserBean.isUserSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取个人信息 成功-数据不为空");
                                ResultUserInfoDataParsing(mUserBean);
                            } else if (mUserBean.isUserSuccess() == 0) {
                                //数据为空
                                MyLog.i(TAG, "请求接口-获取个人信息 成功-数据为空");
                                GotoProfileInit();
                            }
                            //获取失败
                            else {
                                MyLog.i(TAG, "请求接口-获取个人信息 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取个人信息 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);

                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "请求接口-获取个人信息 请求失败 = message = " + arg0.getMessage());

                        waitDialog.close();

                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });


    }

    /**
     * 解析用户信息数据
     */
    private void ResultUserInfoDataParsing(UserBean mUserBean) {

        MyLog.i(TAG, "请求接口-获取个人信息 = 解析 = getMsg = " + mUserBean.getMsg());
        MyLog.i(TAG, "请求接口-获取个人信息 = 解析 = getCodeMsg = " + mUserBean.getCodeMsg());
        MyLog.i(TAG, "请求接口-获取个人信息 = 解析 = getData = " + mUserBean.getData().toString());

        //模拟用户
//        mUserBean.getData().setHeight("");
        if (JavaUtil.checkIsNull(mUserBean.getData().getHeight())) {
            GotoProfileInit();
        } else {
            //存储用户信息
            MyLog.i(TAG, "请求接口-获取个人信息 = 身高不为空");
            UserBean.saveUserInfo(mUserBean.getData());
            GotoMain();
//            getCalibrationInfo();
        }
    }

    /**
     * 请求-获取当前用户的校准信息
     */
    private void getCalibrationInfo() {

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.getUserCalibrationInfo();

        MyLog.i(TAG, "请求接口-获取校准信息 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-获取校准信息 = result = " + result.toString());

                        CalibrationBean mCalibrationBean = ResultJson.CalibrationBean(result);

                        //请求成功
                        if (mCalibrationBean.isRequestSuccess()) {

                            //获取数据UC恒公
                            if (mCalibrationBean.isUserSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取校准信息 成功-数据不为空");
                                resultCalibrationInfoDataParsing(mCalibrationBean);
                            }
                            //数据为空
                            else if (mCalibrationBean.isUserSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取校准信息 成功-数据为空");
                                GotoProfileInit();
                            }
                            //获取数据失败
                            else {
                                MyLog.i(TAG, "请求接口-获取校准信息 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取校准信息 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);

                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "请求接口-获取校准信息 请求失败 = message = " + arg0.getMessage());
                        waitDialog.close();
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });


    }

    /**
     * 解析服务器返回的数据
     */
    private void resultCalibrationInfoDataParsing(CalibrationBean mCalibrationBean) {

        MyLog.i(TAG, "请求接口-获取校准信息 = 解析 = getMsg = " + mCalibrationBean.getMsg());
        MyLog.i(TAG, "请求接口-获取校准信息 = 解析 = getCodeMsg = " + mCalibrationBean.getCodeMsg());
        MyLog.i(TAG, "请求接口-获取校准信息 = 解析 = getData = " + mCalibrationBean.getData().toString());

        //模拟用户
//        mCalibrationBean.getData().setSportTarget(0);

        //运动目标不为空
        if (JavaUtil.checkIsNull(String.valueOf(mCalibrationBean.getData().getSportTarget()))) {
            GotoProfileInit();
        } else {
            //存储用户信息
            MyLog.i(TAG, "请求接口-获取校准信息 = 运动目标不为空");
            CalibrationBean.saveCalibrationInfo(mCalibrationBean.getData());
            GotoMain();
        }
    }

    /**
     * 跳转到信息录入
     */
    void GotoProfileInit() {
        Intent mIntent = new Intent(mContext, ProfileInitActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ProfileInitActivity.IntentName, "");
        mIntent.putExtras(bundle);
        startActivity(mIntent);
        manager.finishAllActivity();
    }

    /**
     * 跳转到主页
     */
    void GotoMain() {
        //标志位-已登录
        DeviceManager.getInstance().downLoadBindDevice(new DeviceManager.DeviceManagerListen() {
            @Override
            public void onSuccess() {
                mUserSetTools.set_user_login(true);
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                manager.finishAllActivity();
            }

            @Override
            public void onError() {
                mUserSetTools.set_user_login(true);
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                manager.finishAllActivity();
            }
        });
    }

    //===============第三方登录=====================
    void MorePlatForm(SHARE_MEDIA share_media) {
        UMShareAPI.get(LoginActivity.this).getPlatformInfo(LoginActivity.this, share_media, authListener);
    }

    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {

            waitDialog.show(getString(R.string.loading0));

            MyLog.i(TAG, "第三方登录 = onStart");

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            MyLog.i(TAG, "监听方法 = onComplete platform = " + platform.toString());

            String Um_platform = platform.toString();
            String openid = "";
            String Um_uid = "";
            String Um_token = "";
            String Um_head_name = "";
            String Um_head_url = "";
            String regiset_time = "";
            for (String key : data.keySet()) {

                MyLog.i(TAG, "key = " + key + " = " + data.get(key));

                if (key.equals("openid")) {
                    openid = data.get(key);
                } else if (key.equals("uid")) {
                    Um_uid = data.get(key);
                } else if (key.equals("access_token") || key.equals("accessToken")) {
                    Um_token = data.get(key);
                } else if (key.equals("name")) {
                    Um_head_name = data.get(key);
                } else if (key.equals("iconurl")) {
                    Um_head_url = data.get(key);
                }
            }


            openid = (openid == null) ? "" : openid;
            Um_uid = (Um_uid == null) ? "" : Um_uid;
            Um_token = (Um_token == null) ? "" : Um_token;
            Um_head_name = (Um_head_name == null) ? "" : Um_head_name;
            Um_head_url = (Um_head_url == null) ? "" : Um_head_url;


            MyLog.i(TAG, "监听方法 = Um_platform = " + Um_platform);
            MyLog.i(TAG, "监听方法 = openid = " + openid);
            MyLog.i(TAG, "监听方法 = Um_uid = " + Um_uid);
            MyLog.i(TAG, "监听方法 = Um_token = " + Um_token);
            MyLog.i(TAG, "监听方法 = Um_head_name = " + Um_head_name);
            MyLog.i(TAG, "监听方法 = Um_head_url = " + Um_head_url);

            //防止其中一个为空，去另外一个ID
            if (openid.equals("")) {
                openid = Um_uid;
            }

            if (Um_uid.equals("")) {
                Um_uid = openid;
            }

            regiset_time = MyTime.getAllTime();


            switch (platform) {
                case QQ:
                    LoginType = "4";
                    YMLogin(openid, Um_uid, Um_head_name, Um_head_url, regiset_time, LoginType);
                    break;

                case WEIXIN:
                    LoginType = "3";
                    YMLogin(openid, Um_uid, Um_head_name, Um_head_url, regiset_time, LoginType);
                    break;

            }

        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            MyLog.i(TAG, "监听方法 = onError = 授权失败");
            waitDialog.close();
            AppUtils.showToast(mContext, R.string.privilege_grant_failed);

        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            MyLog.i(TAG, "监听方法 = onCancel = 授权取消");
            waitDialog.close();
            AppUtils.showToast(mContext, R.string.authorization_cancel);


        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


}
