package com.zjw.apps3pluspro.module.mine.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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
 * 手机号-忘记密码
 */
public class ForgetPswActivity extends BaseActivity implements OnClickListener {
    private final String TAG = ForgetPswActivity.class.getSimpleName();
    private Context mContext;
    private MyActivityManager manager;

    //倒计时
    int smsTime_phone = 120;
    //网络加载圈
    private WaitDialog waitDialog;


    //忘记密码
    private EditText et_forget_account, et_forget_account_getcode, et_account_forget_password1, et_account_forget_password2;
    private Button btn_forget_account_getcode;
    private CheckBox cb_account_forget_again1, cb_account_forget_again2;

    // 手机号保存的信息。
    String SavePassword, RegisterAccount, AccountCode;

    @Override
    protected int setLayoutId() {
        isTextDark = false;
        bgColor = R.color.top_bar_base;
        return R.layout.activity_forgetpsw;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = ForgetPswActivity.this;
        waitDialog = new WaitDialog(mContext);
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        initView();

        AppUtils.initEditTextFocusChange(et_forget_account, view1, "#57FFB5", "#1Effffff");
        AppUtils.initEditTextFocusChange(et_forget_account_getcode, view2, "#57FFB5", "#1Effffff");
        AppUtils.initEditTextFocusChange(et_account_forget_password1, view3, "#57FFB5", "#1Effffff");
        AppUtils.initEditTextFocusChange(et_account_forget_password2, view4, "#57FFB5", "#1Effffff");
    }
    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.view2)
    View view2;
    @BindView(R.id.view3)
    View view3;
    @BindView(R.id.view4)
    View view4;

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
        if (handler_phone != null) {
            handler_phone.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    /**
     * 定义一个handler用来发送消息。
     */
    Handler handler_phone = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {

                btn_forget_account_getcode.setText(getString(R.string.resend) + "(" + smsTime_phone + ")");


            } else if (msg.what == 2) {
                btn_forget_account_getcode.setText(R.string.send_code);
                btn_forget_account_getcode.setClickable(true);
                smsTime_phone = 120;
            }
        }

    };

    /**
     * 把这个验证码的框变成多少秒以后重新发送。
     */
    private void ButtonCodeChange() {

        btn_forget_account_getcode.setClickable(false);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                for (; smsTime_phone > 0; smsTime_phone--) {

                    handler_phone.sendEmptyMessage(1);
                    if (smsTime_phone <= 0) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                handler_phone.sendEmptyMessage(2);
            }
        }).start();
    }


    private void initView() {

        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.title_forget_pas));
        findViewById(R.id.public_head_back).setOnClickListener(this);

        et_forget_account = (EditText) findViewById(R.id.et_forget_account);// 账号
        et_account_forget_password1 = (EditText) findViewById(R.id.et_account_forget_password1);// 密码
        et_account_forget_password2 = (EditText) findViewById(R.id.et_account_forget_password2);// 确认密码
        et_forget_account_getcode = (EditText) findViewById(R.id.et_forget_account_getcode);// 验证码输入框
        btn_forget_account_getcode = (Button) findViewById(R.id.btn_forget_account_getcode);// 获取验证码
        btn_forget_account_getcode.setOnClickListener(this);

        cb_account_forget_again1 = (CheckBox) findViewById(R.id.cb_account_forget_again1);//密码是否可见开关
        cb_account_forget_again2 = (CheckBox) findViewById(R.id.cb_account_forget_again2);//密码是否可见开关

        et_account_forget_password1.setTypeface(Typeface.DEFAULT);
        et_account_forget_password2.setTypeface(Typeface.DEFAULT);

        cb_account_forget_again1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //选择状态 显示明文--设置为可见的密码
                    et_account_forget_password1.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    et_account_forget_password1.setTypeface(Typeface.DEFAULT);
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    et_account_forget_password1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    et_account_forget_password1.setTypeface(Typeface.DEFAULT);
                }
            }
        });

        cb_account_forget_again2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //选择状态 显示明文--设置为可见的密码
                    et_account_forget_password2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    et_account_forget_password2.setTypeface(Typeface.DEFAULT);
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    et_account_forget_password2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    et_account_forget_password2.setTypeface(Typeface.DEFAULT);
                }
            }
        });


        findViewById(R.id.btn_account_forget_ok).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //返回
            case R.id.public_head_back:
                manager.popOneActivity(this);
                break;

            //获取验证码
            case R.id.btn_forget_account_getcode:
                checkInputAccount(et_forget_account.getText().toString().trim());
                break;

            //完成
            case R.id.btn_account_forget_ok:
                RegisterAccount = et_forget_account.getText().toString().trim();
                checkAccountInputAll(et_forget_account.getText().toString().trim(), et_forget_account_getcode.getText().toString().trim()
                        , et_account_forget_password1.getText().toString().trim(), et_account_forget_password2.getText().toString().trim());
                break;


        }
    }


    //=============找回密码===============


    /**
     * 检查输入的账号
     *
     * @param input_account
     */
    void checkInputAccount(String input_account) {

        String account_type = "1";


        //国内
        if (AppUtils.isZh(mContext)) {
            //输入为空
            if (TextUtils.isEmpty(input_account)) {
                AppUtils.showToast(mContext, R.string.input_your_mobile_phone);
                MyUtils.setFocusable(et_forget_account);
                return;
            }
            //等于手机号且不包含空格
            else if (JavaUtil.isMobileNO(input_account) && !JavaUtil.isKongGe(input_account)) {
                account_type = "1";
            }
            //等于邮箱且不包含空格
            else if (JavaUtil.isEmail(input_account) && !JavaUtil.isKongGe(input_account)) {
                account_type = "2";
            }
            //输入格式错误
            else {
                AppUtils.showToast(mContext, R.string.wrong_input_your_mobile_phone);
                MyUtils.setFocusable(et_forget_account);
                return;
            }
            //国外
        } else {
            //输入为空
            if (TextUtils.isEmpty(input_account)) {
                AppUtils.showToast(mContext, R.string.input_your_email);
                MyUtils.setFocusable(et_forget_account);
                return;
            }
            //等于邮箱，且不包含空格
            else if (JavaUtil.isEmail(input_account) && !JavaUtil.isKongGe(input_account)) {
                account_type = "2";
            }
            //输入格式错误
            else {
                AppUtils.showToast(mContext, R.string.wrong_input_format_email);
                MyUtils.setFocusable(et_forget_account);
                return;
            }
        }


        RegisterAccount = input_account;
        // 是否注册过
        requestCheckMobile(account_type);


    }

    /**
     * 请求 - 检查手机号是否注册过
     */
    public void requestCheckMobile(final String account_type) {


        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.checkUserRegistered(RegisterAccount);

        MyLog.i(TAG, "请求接口-账号是否注册过 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
                        waitDialog.close();


                        MyLog.i(TAG, "请求接口-账号是否注册过 result = " + result.toString());

                        AccountBean mAccountBean = ResultJson.AccountBean(result);

                        //请求成功
                        if (mAccountBean.isRequestSuccess()) {

                            //已注册
                            if (mAccountBean.checkRegister() == 1) {
                                MyLog.i(TAG, "请求接口-手机号是否注册过 已注册");
                                //发送验证码
                                requestSendCode(account_type);

                                //未注册
                            } else if (mAccountBean.checkRegister() == 0) {

                                MyLog.i(TAG, "请求接口-账号是否注册过 未注册");
                                AppUtils.showToast(mContext, R.string.the_account_is_not_registered);
                                MyUtils.setFocusable(et_forget_account);

                            } else {

                                MyLog.i(TAG, "请求接口-账号是否注册过 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.server_try_again_code0);
                            }


                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-账号是否注册过 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);

                        }


                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-账号是否注册过 请求失败 = message = " + arg0.getMessage());

                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });
    }


    /**
     * 请求-发送手机验证码
     */
    private void requestSendCode(final String account_type) {

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.getValidationCode(RegisterAccount, Integer.valueOf(account_type), 2);

        MyLog.i(TAG, "请求接口-发送验证码 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        waitDialog.close();


                        MyLog.i(TAG, "请求接口-发送验证码 result = " + result.toString());

                        AccountBean mAccountBean = ResultJson.AccountBean(result);


                        //请求成功
                        if (mAccountBean.isRequestSuccess()) {

                            //发送成功
                            if (mAccountBean.isSendCodeSuccess() == 1) {

                                MyLog.i(TAG, "请求接口-发送验证码 成功");

                                //改变按钮状态
                                ButtonCodeChange();

                                //验证码发送失败
                            } else if (mAccountBean.isSendCodeSuccess() == 0) {

                                MyLog.i(TAG, "请求接口-发送验证码 失败");
                                AppUtils.showToast(mContext, R.string.code_get_wrong);


                                //获取验证码频繁
                            } else if (mAccountBean.isSendCodeSuccess() == 2) {
                                MyLog.i(TAG, "请求接口-发送验证码 频繁");
                                AppUtils.showToast(mContext, R.string.code_get_frequently);

                            } else {
                                MyLog.i(TAG, "请求接口-发送验证码 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }


                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-发送验证码 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);

                        }

                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub

                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-发送验证码 请求失败 = message = " + arg0.getMessage());

                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });


    }

    /**
     * 检测账号所有内容是否合法
     *
     * @param input_account
     * @param input_code
     * @param input_password1
     * @param input_password2
     */
    void checkAccountInputAll(String input_account, String input_code, String input_password1, String input_password2) {
        String account_type = "1";

        //国内
        if (AppUtils.isZh(mContext)) {
            //输入为空
            if (TextUtils.isEmpty(input_account)) {
                AppUtils.showToast(mContext, R.string.input_your_mobile_phone);
                MyUtils.setFocusable(et_forget_account);
                return;
            }
            //等于手机号且不包含空格
            else if (JavaUtil.isMobileNO(input_account) && !JavaUtil.isKongGe(input_account)) {
                account_type = "1";
            }
            //等于邮箱且不包含空格
            else if (JavaUtil.isEmail(input_account) && !JavaUtil.isKongGe(input_account)) {
                account_type = "2";
            }
            //输入格式错误
            else {
                AppUtils.showToast(mContext, R.string.wrong_input_your_mobile_phone);
                MyUtils.setFocusable(et_forget_account);
                return;
            }
            //国外
        } else {
            //输入为空
            if (TextUtils.isEmpty(input_account)) {
                AppUtils.showToast(mContext, R.string.input_your_email);
                MyUtils.setFocusable(et_forget_account);
                return;
            }
            //等于邮箱，且不包含空格
            else if (JavaUtil.isEmail(input_account) && !JavaUtil.isKongGe(input_account)) {
                account_type = "2";
            }
            //输入格式错误
            else {
                AppUtils.showToast(mContext, R.string.wrong_input_format_email);
                MyUtils.setFocusable(et_forget_account);
                return;
            }
        }


        //验证码是否输入为空
        if (TextUtils.isEmpty(input_code)) {
            AppUtils.showToast(mContext, R.string.input_code);
            MyUtils.setFocusable(et_forget_account_getcode);
            return;
        } else {
            AccountCode = input_code;
        }


        //密码输入为空
        if (TextUtils.isEmpty(input_password1)) {
            AppUtils.showToast(mContext, R.string.input_your_pas);
            MyUtils.setFocusable(et_account_forget_password1);
            return;
        }
        //密码输入是否合法-不合法
        else if (!JavaUtil.isPassword(input_password1)) {
            AppUtils.showToast(mContext, R.string.password_size_wrong);
            MyUtils.setFocusable(et_account_forget_password1);
            return;
        }
        //密码2是否输入为空
        else if (TextUtils.isEmpty(input_password2)) {
            AppUtils.showToast(mContext, R.string.input_your_pas);
            MyUtils.setFocusable(et_account_forget_password2);
            return;
        } //密码2，是否合法-不合法
        else if (!JavaUtil.isPassword(input_password2)) {
            AppUtils.showToast(mContext, R.string.password_size_wrong);
            MyUtils.setFocusable(et_account_forget_password2);
            return;
        }
        //密码是否一致-不一致
        else if (!input_password1.equals(input_password2)) {
            AppUtils.showToast(mContext, R.string.password_not_same);
            MyUtils.setFocusable(et_account_forget_password2);
            return;
        } else {
            SavePassword = input_password1;
        }


        // 校验手机验证码。
        requestCheckCode();


    }

    /**
     * 请求-校验验证码
     */
    public void requestCheckCode() {

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.checkValidationCode(mContext, RegisterAccount, AccountCode, SavePassword);

        MyLog.i(TAG, "请求接口-验证验证码 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-验证验证码 result = " + result.toString());

                        AccountBean mAccountBean = ResultJson.AccountBean(result);


                        //请求成功
                        if (mAccountBean.isRequestSuccess()) {

                            //获取验证码成功
                            if (mAccountBean.isForgetPassword() == 1) {

                                MyLog.i(TAG, "请求接口-验证验证码 成功");
                                startActivity(new Intent(mContext, LoginActivity.class));
                                manager.popOneActivity(ForgetPswActivity.this);

                                //验证码获取失败
                            } else if (mAccountBean.isForgetPassword() == 0) {
                                MyLog.i(TAG, "请求接口-验证验证码 失败");
                                AppUtils.showToast(mContext, R.string.code_get_wrong);

                                //账号未注册
                            } else if (mAccountBean.isForgetPassword() == 2) {

                                MyLog.i(TAG, "请求接口-验证验证码 账号未注册");

                                AppUtils.showToast(mContext, R.string.the_account_is_not_registered);
                                MyUtils.setFocusable(et_forget_account);


                            }
                            //验证码已过期
                            else if (mAccountBean.isForgetPassword() == 3) {

                                MyLog.i(TAG, "请求接口-验证验证码 验证码已过期");
                                AppUtils.showToast(mContext, R.string.code_get_is_old);
                                MyUtils.setFocusable(et_forget_account_getcode);

                                //验证码错误
                            } else if (mAccountBean.isForgetPassword() == 4) {

                                MyLog.i(TAG, "请求接口-验证验证码 验证码错误");
                                AppUtils.showToast(mContext, R.string.code_wrong_try_again);
                                MyUtils.setFocusable(et_forget_account_getcode);

                            } else {
                                MyLog.i(TAG, "请求接口-验证验证码 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }

                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-验证验证码 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);

                        }


                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-验证验证码 请求失败 = message = " + arg0.getMessage());

                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });

    }


}
