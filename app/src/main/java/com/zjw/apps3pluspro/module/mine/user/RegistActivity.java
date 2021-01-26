package com.zjw.apps3pluspro.module.mine.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.zjw.apps3pluspro.network.javabean.AccountBean;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

import butterknife.BindView;

/**
 * 手机号注册
 */
public class RegistActivity extends BaseActivity implements OnClickListener {
    private final String TAG = RegistActivity.class.getSimpleName();
    private Context mContext;
    private MyActivityManager manager;

    //网络加载圈
    private WaitDialog waitDialog;

    //注册
    private EditText et_register_account, et_register_password1, et_register_password2;
    private CheckBox cb_regist_again1, cb_regist_again2;

    @Override
    protected int setLayoutId() {
        isTextDark = false;
        bgColor = R.color.top_bar_base;
        return R.layout.activity_regist;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = RegistActivity.this;
        waitDialog = new WaitDialog(mContext);
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        initView();
        initData();

        AppUtils.initEditTextFocusChange(et_register_account, view1, "#57FFB5", "#1Effffff");
        AppUtils.initEditTextFocusChange(et_register_password1, view2, "#57FFB5", "#1Effffff");
        AppUtils.initEditTextFocusChange(et_register_password2, view3, "#57FFB5", "#1Effffff");
    }

    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.view2)
    View view2;
    @BindView(R.id.view3)
    View view3;

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
    }


    private void initView() {

        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.btn_regist));
        findViewById(R.id.public_head_back).setOnClickListener(this);

        et_register_account = (EditText) findViewById(R.id.et_register_account);// 账号
        et_register_password1 = (EditText) findViewById(R.id.et_register_password1);// 密码
        et_register_password2 = (EditText) findViewById(R.id.et_register_password2);// 确认密码

        cb_regist_again1 = (CheckBox) findViewById(R.id.cb_regist_again1);
        cb_regist_again2 = (CheckBox) findViewById(R.id.cb_regist_again2);

        et_register_password1.setTypeface(Typeface.DEFAULT);
        et_register_password2.setTypeface(Typeface.DEFAULT);

        cb_regist_again1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //选择状态 显示明文--设置为可见的密码
                    et_register_password1.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    et_register_password1.setTypeface(Typeface.DEFAULT);
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    et_register_password1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    et_register_password1.setTypeface(Typeface.DEFAULT);
                }
            }
        });

        cb_regist_again2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //选择状态 显示明文--设置为可见的密码
                    et_register_password2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    et_register_password2.setTypeface(Typeface.DEFAULT);
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    et_register_password2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    et_register_password2.setTypeface(Typeface.DEFAULT);
                }
            }
        });

        findViewById(R.id.btn_regist_ok).setOnClickListener(this);

        findViewById(R.id.tv_user).setOnClickListener(this);
        findViewById(R.id.tv_privacy).setOnClickListener(this);
        findViewById(R.id.layoutAgree).setOnClickListener(this);
        findViewById(R.id.btAgree).setOnClickListener(this);

        initOkButton();
    }

    void initData() {

        //中文
        if (AppUtils.isZh(mContext)) {
            et_register_account.setHint(getString(R.string.input_your_mobile_phone));
        }
        //非中文
        else {
            et_register_account.setHint(getString(R.string.input_your_email));
        }
    }

    private boolean isAgree = false;
    @BindView(R.id.btAgree)
    Button btAgree;
    @BindView(R.id.btn_regist_ok)
    Button btn_regist_ok;

    private void initOkButton(){
        if (isAgree) {
            btAgree.setBackground(getResources().getDrawable(R.mipmap.agree));
            btn_regist_ok.setBackground(getResources().getDrawable(R.mipmap.white_bt_bg));
            btn_regist_ok.setTextColor(getResources().getColor(R.color.bt_text_color));
        } else {
            btAgree.setBackground(getResources().getDrawable(R.mipmap.no_agree));
            btn_regist_ok.setBackground(getResources().getDrawable(R.mipmap.grey_bt_bg));
            btn_regist_ok.setTextColor(getResources().getColor(R.color.color_71757A));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.public_head_back:
                manager.popOneActivity(this);
                break;
            //注册
            case R.id.btn_regist_ok:
                if (!isAgree) {
                    return;
                }
                checkInputAll(et_register_account.getText().toString().trim(),
                        et_register_password1.getText().toString().trim(),
                        et_register_password2.getText().toString().trim());
                break;
            //手机号注册跳转-隐私协议
            case R.id.tv_user:
                startActivity(new Intent(mContext, UserProtocolActivity.class));
                break;
            case R.id.tv_privacy:
                startActivity(new Intent(mContext, PrivacyProtocolActivity.class));
                break;
            case R.id.layoutAgree:
            case R.id.btAgree:
                isAgree = !isAgree;
                initOkButton();
                break;
        }
    }


    void checkInputAll(String input_account, String input_password1, String input_password2) {


        String register_type = "1";


        //国内
        if (AppUtils.isZh(mContext)) {
            //输入为空
            if (TextUtils.isEmpty(input_account)) {
                AppUtils.showToast(mContext, R.string.input_your_mobile_phone);
                MyUtils.setFocusable(et_register_account);
                return;
            }
            //等于手机号且不包含空格
            else if (JavaUtil.isMobileNO(input_account) && !JavaUtil.isKongGe(input_account)) {
                register_type = "1";
            }
            //等于邮箱且不包含空格
            else if (JavaUtil.isEmail(input_account) && !JavaUtil.isKongGe(input_account)) {
                register_type = "2";
            }
            //输入格式错误
            else {
                AppUtils.showToast(mContext, R.string.wrong_input_your_mobile_phone);
                MyUtils.setFocusable(et_register_account);
                return;
            }
            //国外
        } else {
            //输入为空
            if (TextUtils.isEmpty(input_account)) {
                AppUtils.showToast(mContext, R.string.input_your_email);
                MyUtils.setFocusable(et_register_account);
                return;
            }
            //等于邮箱，且不包含空格
            else if (JavaUtil.isEmail(input_account) && !JavaUtil.isKongGe(input_account)) {
                register_type = "2";
            }
            //输入格式错误
            else {
                AppUtils.showToast(mContext, R.string.wrong_input_format_email);
                MyUtils.setFocusable(et_register_account);
                return;
            }
        }

        //密码1，输入为空
        if (TextUtils.isEmpty(input_password1)) {
            AppUtils.showToast(mContext, R.string.input_your_pas);
            MyUtils.setFocusable(et_register_password1);
            return;
        }
        //密码1，是否合法-不合法
        else if (!JavaUtil.isPassword(input_password1)) {
            AppUtils.showToast(mContext, R.string.password_size_wrong);
            MyUtils.setFocusable(et_register_password1);
            return;
        }
        //密码2，输入为空
        else if (TextUtils.isEmpty(input_password2)) {
            AppUtils.showToast(mContext, R.string.input_your_pas);
            MyUtils.setFocusable(et_register_password2);
            return;
        }
        //密码2，是否合法-不合法
        else if (!JavaUtil.isPassword(input_password2)) {
            AppUtils.showToast(mContext, R.string.password_size_wrong);
            MyUtils.setFocusable(et_register_password2);
            return;
        }
        //密码1和密码2是否一致-不一致
        else if (!input_password1.equals(input_password2)) {
            AppUtils.showToast(mContext, R.string.password_not_same);
            MyUtils.setFocusable(et_register_password2);
            return;
        }

        //检查是否注册过
        requestCheckAccount(input_account, input_password1, register_type);


    }


    /**
     * 判断账号否被注册过
     */
    private void requestCheckAccount(final String account, final String password, final String type) {


        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.checkUserRegistered(account);

        MyLog.i(TAG, "请求接口-是否注册过 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {


                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-是否注册过 result = " + result.toString());

                        AccountBean mAccountBean = ResultJson.AccountBean(result);

                        //请求成功
                        if (mAccountBean.isRequestSuccess()) {

                            //已注册
                            if (mAccountBean.checkRegister() == 1) {

                                MyLog.i(TAG, "请求接口-是否注册过 已注册");
                                MyUtils.setFocusable(et_register_account);
                                AppUtils.showToast(mContext, R.string.user_already_exists);

                                //未注册
                            } else if (mAccountBean.checkRegister() == 0) {

                                MyLog.i(TAG, "请求接口-是否注册过 未注册");
                                //注册
                                requestRegist(account, password, type);
                            } else {
                                MyLog.i(TAG, "请求接口-是否注册过 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }

                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-是否注册过 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);

                        }

                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-是否注册过 请求失败 = message = " + arg0.getMessage());

                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                    }
                });


    }


    /**
     * 请求-注册
     */
    private void requestRegist(final String account, final String password, final String type) {

        waitDialog.show(getString(R.string.loading0));


        RequestInfo mRequestInfo = RequestJson.userRegister(mContext, account, password, type);

        MyLog.i(TAG, "请求接口-注册 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        waitDialog.close();


                        MyLog.i(TAG, "请求接口-注册 result = " + result);

                        AccountBean mAccountBean = ResultJson.AccountBean(result);

                        //请求成功
                        if (mAccountBean.isRequestSuccess()) {

                            //注册成功
                            if (mAccountBean.isRegisterSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-注册 - 成功");
                                MobileRegistParsing(mAccountBean, account, password, type);
                                //注册失败
                            } else if (mAccountBean.isRegisterSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-注册 失败");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                                //请求异常
                            } else {
                                MyLog.i(TAG, "请求接口-注册 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }

                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-注册 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);

                        }


                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub

                        waitDialog.close();
                        MyLog.i(TAG, "请求接口-注册 请求失败 = message = " + arg0.getMessage());
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });


    }


    /**
     * 解析服务器返回的数据
     */
    private void MobileRegistParsing(AccountBean mAccountBean, String account, String password, String type) {


        MyLog.i(TAG, "请求接口-注册 = 解析 = getMsg = " + mAccountBean.getMsg());
        MyLog.i(TAG, "请求接口-注册 = 解析 = getCodeMsg = " + mAccountBean.getCodeMsg());
        MyLog.i(TAG, "请求接口-注册 = 解析 = getData = " + mAccountBean.getData().toString());

        String user_id = String.valueOf(mAccountBean.getData().getUserId());
        String register_time = mAccountBean.getData().getRegisterTime();

        MyLog.i(TAG, "请求接口-注册 = 解析 = user_id = " + user_id);
        MyLog.i(TAG, "请求接口-注册 = 解析 = register_time = " + register_time);

        String authorizationCode = mAccountBean.getData().getAuthorization();
        MyLog.i(TAG, "regist Authorization code" + authorizationCode);
        AccountBean.saveAccount(user_id, account, password, register_time, type, authorizationCode);

        Intent mIntent = new Intent(mContext, ProfileInitActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ProfileInitActivity.IntentName, "");
        mIntent.putExtras(bundle);
        startActivity(mIntent);
        manager.finishAllActivity();

    }


}
