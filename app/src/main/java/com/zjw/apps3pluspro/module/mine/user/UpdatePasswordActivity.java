package com.zjw.apps3pluspro.module.mine.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

/**
 * 修改密码
 */
public class UpdatePasswordActivity extends BaseActivity implements OnClickListener {
    private final String TAG = UpdatePasswordActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();

    private EditText mine_modefine_number_old, mine_modefine_number_new, mine_modefine_number_newagain;
    private String oldPassword, newPassword, newPassword2;

    private WaitDialog waitDialog;

    private MyActivityManager manager;

    private CheckBox cb_update_pass_old, cb_update_pass_new, cb_update_pass_again;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_update_psw;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = UpdatePasswordActivity.this;
        waitDialog = new WaitDialog(mContext);
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        initView();
    }

    @Override
    protected void onResume() {
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
    public void onDestroy() {
        waitDialog.dismiss();
        super.onDestroy();
    }

    private void initView() {

        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.modefy_password));
        findViewById(R.id.public_head_back).setOnClickListener(this);

        mine_modefine_number_old = (EditText) findViewById(R.id.mine_modefine_number_old);
        mine_modefine_number_new = (EditText) findViewById(R.id.mine_modefine_number_new);
        mine_modefine_number_newagain = (EditText) findViewById(R.id.mine_modefine_number_newagain);
        cb_update_pass_old = (CheckBox) findViewById(R.id.cb_update_pass_old);
        cb_update_pass_new = (CheckBox) findViewById(R.id.cb_update_pass_new);
        cb_update_pass_again = (CheckBox) findViewById(R.id.cb_update_pass_again);

        mine_modefine_number_old.setTypeface(Typeface.DEFAULT);
        mine_modefine_number_new.setTypeface(Typeface.DEFAULT);
        mine_modefine_number_newagain.setTypeface(Typeface.DEFAULT);

        cb_update_pass_old.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //选择状态 显示明文--设置为可见的密码
                    mine_modefine_number_old.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mine_modefine_number_old.setTypeface(Typeface.DEFAULT);
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    mine_modefine_number_old.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mine_modefine_number_old.setTypeface(Typeface.DEFAULT);
                }
            }
        });

        cb_update_pass_new.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //选择状态 显示明文--设置为可见的密码
                    mine_modefine_number_new.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mine_modefine_number_new.setTypeface(Typeface.DEFAULT);
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    mine_modefine_number_new.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mine_modefine_number_new.setTypeface(Typeface.DEFAULT);
                }
            }
        });

        cb_update_pass_again.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //选择状态 显示明文--设置为可见的密码
                    mine_modefine_number_newagain.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mine_modefine_number_newagain.setTypeface(Typeface.DEFAULT);
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    mine_modefine_number_newagain.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mine_modefine_number_newagain.setTypeface(Typeface.DEFAULT);
                }
            }
        });

        findViewById(R.id.bt_mine_modefy_pasw_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.public_head_back:

                manager.popOneActivity(this);
                break;
            case R.id.bt_mine_modefy_pasw_ok:// 提交
                checkAll();
                break;
        }
    }

    void checkAll() {
        // 在这里进行修改密码的操作。
        // 获取输入框
        oldPassword = mine_modefine_number_old.getText().toString().trim();
        newPassword = mine_modefine_number_new.getText().toString().trim();
        newPassword2 = mine_modefine_number_newagain.getText().toString().trim();
//        String password = mUserSetTools.get_user_password();
//        MyLog.i(TAG, "old_password = " + password);


        if (TextUtils.isEmpty(oldPassword)) {
            AppUtils.showToast(mContext, R.string.input_old_pass);
            MyUtils.setFocusable(mine_modefine_number_old);
            return;
        }
        //密码输入是否合法-不合法
        else if (!JavaUtil.isPassword(oldPassword)) {
            AppUtils.showToast(mContext, R.string.password_size_wrong);
            MyUtils.setFocusable(mine_modefine_number_old);
            return;
        }


        if (TextUtils.isEmpty(newPassword)) {
            AppUtils.showToast(mContext, R.string.new_password_null);
            MyUtils.setFocusable(mine_modefine_number_new);
            return;
        } else if (!JavaUtil.isPassword(oldPassword)) {
            AppUtils.showToast(mContext, R.string.password_size_wrong);
            MyUtils.setFocusable(mine_modefine_number_new);
            return;
        }


        if (!newPassword.equals(newPassword2)) {
            AppUtils.showToast(mContext, R.string.password_not_same);
            MyUtils.setFocusable(mine_modefine_number_new);
            return;
        }


        if (newPassword.equals(oldPassword)) {
            AppUtils.showToast(mContext, R.string.password_old_to_new);
            MyUtils.setFocusable(mine_modefine_number_old);
            return;
        }


        ChangePasswordDialog();

    }


    /**
     * 确认修改密码对话框
     */
    void ChangePasswordDialog() {
        new android.app.AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.dialog_prompt))
                .setMessage(getString(R.string.is_change_password))
                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {//添加确定按钮
                    public void onClick(DialogInterface dialog, int which) {
                        requesUpoladPassword();
                    }

                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {//添加返回按钮

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        }).show();
    }

    /**
     * 请求修改密码
     */
    void requesUpoladPassword() {

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.modifyUserPassword(mContext, oldPassword, newPassword);

        MyLog.i(TAG, "请求接口-修改密码 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-修改密码 result = " + result.toString());

                        AccountBean mAccountBean = ResultJson.AccountBean(result);

                        //请求成功
                        if (mAccountBean.isRequestSuccess()) {

                            if (mAccountBean.isChangePassword() == 1) {

                                MyLog.i(TAG, "请求接口-修改密码 成功");
                                AppUtils.showToast(mContext, R.string.change_ok);
                                quitApp();
                            } else if (mAccountBean.isChangePassword() == 0) {
                                MyLog.i(TAG, "请求接口-修改密码 失败");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else if (mAccountBean.isChangePassword() == 2) {
                                MyLog.i(TAG, "请求接口-修改密码 未注册");
                                AppUtils.showToast(mContext, R.string.the_account_is_not_registered);
                            } else if (mAccountBean.isChangePassword() == 3) {
                                MyLog.i(TAG, "请求接口-修改密码 密码错误");
                                AppUtils.showToast(mContext, R.string.pasword_verification_no);
                            } else {
                                MyLog.i(TAG, "请求接口-修改密码 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-修改密码 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        waitDialog.close();
                        MyLog.i(TAG, "请求接口-修改密码 请求失败 = message = " + arg0.getMessage());
                        return;
                    }
                });
    }

    /**
     * 对出当前账号
     */
    void quitApp() {

        mUserSetTools.set_user_login(false);
        disconnect();

        mUserSetTools.set_user_id("");
        mUserSetTools.set_user_nickname("");//用户名
        mUserSetTools.set_user_password("");//密码
        mUserSetTools.set_user_sex(DefaultVale.USER_SEX);//性别
        mUserSetTools.set_user_birthday("");//生日
        mUserSetTools.set_user_register_time("");//注册时间
        mUserSetTools.set_user_head_url("");//头像url
        mUserSetTools.set_user_phone("");//手机号
        mUserSetTools.set_user_email("");//邮箱
        mUserSetTools.set_user_height(0);//身高
        mUserSetTools.set_user_weight(0);//体重
        mUserSetTools.set_user_head_bast64("");
        mUserSetTools.set_is_par(0);//校准标志位-设置成未校准
        mUserSetTools.set_user_exercise_target(String.valueOf(DefaultVale.USER_SPORT_TARGET));//运动目标
        mUserSetTools.set_user_sleep_target(String.valueOf(DefaultVale.USER_SLEEP_TARGET));//睡眠目标
        mUserSetTools.set_blood_grade(DefaultVale.USER_BP_LEVEL);//血压等级
        mUserSetTools.set_calibration_heart(DefaultVale.USER_HEART);//校准心率
        mUserSetTools.set_calibration_systolic(DefaultVale.USER_SYSTOLIC);//校准高压-收缩压
        mUserSetTools.set_calibration_diastolic(DefaultVale.USER_DIASTOLIC);//校准低压-舒张压
        mUserSetTools.set_user_wear_way(DefaultVale.USER_WEARWAY);//穿戴方式
        startActivity(new Intent(mContext, LoginActivity.class));
        manager.finishAllActivity();

    }
}
