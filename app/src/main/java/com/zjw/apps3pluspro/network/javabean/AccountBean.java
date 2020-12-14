package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.okhttp.MyOkHttpClient;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.log.MyLog;

/**
 * Created by zjw on 2018/3/14.
 */
public class AccountBean {
    private static final String TAG = AccountBean.class.getSimpleName();

    /**
     * data : {"registerTime":"2019-05-05 16:22:51","userId":779981}
     * * result : 1
     * code : 0000
     * msg : 请求成功
     * codeMsg : 操作成功！
     */

    private DataBean data;
    private int result;
    private String code;
    private String msg;
    private String codeMsg;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCodeMsg() {
        return codeMsg;
    }

    public void setCodeMsg(String codeMsg) {
        this.codeMsg = codeMsg;
    }

    public static class DataBean {
        /**
         * registerTime : 2019-05-05 16:22:51
         * userId : 779981
         */
        private String authorization;
        private String registerTime;
        private int userId;

        public String getRegisterTime() {
            return registerTime;
        }

        public void setRegisterTime(String registerTime) {
            this.registerTime = registerTime;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getAuthorization() {
            return authorization;
        }

        public void setAuthorization(String authorization) {
            this.authorization = authorization;
        }
    }

    /**
     * 是否请求成功
     *
     * @return
     */
    public boolean isRequestSuccess() {
        if (getResult() == ResultJson.Result_success) {
            return true;
        } else {
            return false;
        }

    }


    /**
     * 是否已注册
     *
     * @return
     */
    public int checkRegister() {
        //已注册
        if (ResultJson.Code_is_register_yes.equals(getCode())) {
            return 1;
            //未注册
        } else if (ResultJson.Code_is_register_no.equals(getCode())) {
            return 0;
            //异常
        } else {
            return -1;
        }
    }


    /**
     * 是否注册成功
     *
     * @return
     */
    public int isRegisterSuccess() {
        //注册成功
        if (ResultJson.Code_operation_success.equals(getCode())) {
            return 1;

        }
        //注册失败
        else if (ResultJson.Code_operation_fail.equals(getCode())) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * 是否登录成功
     *
     * @return
     */
    public int isLoginSuccess() {
        //登录成功
        if (ResultJson.Code_operation_success.equals(getCode())) {
            return 1;

        }
        //密码错误
        else if (ResultJson.Code_password_fail.equals(getCode())) {
            return 0;

        }
        //未注册
        else if (ResultJson.Code_is_register_no.equals(getCode())) {
            return 2;
        } else {
            return -1;
        }
    }

    /**
     * 第三方APP是否登录成功
     *
     * @return
     */
    public int isMoreLoginSuccess() {
        //登录成功
        if (ResultJson.Code_operation_success.equals(getCode())) {
            return 1;
        } else {
            return -1;
        }
    }


    /**
     * 验证码是否发送成功
     *
     * @return
     */
    public int isSendCodeSuccess() {
        //已注册
        if (ResultJson.Code_operation_success.equals(getCode())) {
            return 1;
        }
        //验证码下发失败
        else if (ResultJson.Code_getcode_fail.equals(getCode())) {
            return 0;
        }
        //验证码获取频繁
        else if (ResultJson.Code_getcode_frequently.equals(getCode())) {
            return 2;
        }
        //异常
        else {
            return -1;
        }
    }


    /**
     * 验证验证码并修改密码
     *
     * @return
     */
    public int isForgetPassword() {

        //操作成功
        if (ResultJson.Code_operation_success.equals(getCode())) {
            return 1;
        }
        //修改密码失败
        else if (ResultJson.Code_operation_fail.equals(getCode())) {
            return 0;
        }
        //未注册
        else if (ResultJson.Code_is_register_no.equals(getCode())) {
            return 2;
        }
        //缓存不存在，有可能是过期了，也有可能未获取
        else if (ResultJson.Code_verificationcode_overdue.equals(getCode())) {
            return 3;
        }
        //验证码错误
        else if (ResultJson.Code_verificationcode_fail.equals(getCode())) {
            return 4;
        }
        //异常
        else {
            return -1;
        }
    }


    /**
     * 修改密码是否成功
     *
     * @return
     */
    public int isChangePassword() {
        //修改成功
        if (ResultJson.Code_operation_success.equals(getCode())) {
            return 1;
        }
        //修改失败
        else if (ResultJson.Code_operation_fail.equals(getCode())) {
            return 0;
        }
        //未注册
        else if (ResultJson.Code_is_register_no.equals(getCode())) {
            return 2;
        }
        //密码错误
        else if (ResultJson.Code_password_fail.equals(getCode())) {
            return 3;
        }
        //异常
        else {
            return -1;
        }
    }


    //========================存储数据==================================

    public static void saveAccount(String user_id, String account, String password, String register_time, String type, String authorizationCode) {
        //轻量级存储
        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();


        MyLog.i(TAG, "请求回调-用户 = 解析 = 用户ID = " + user_id);
        MyLog.i(TAG, "请求回调-用户 = 解析 = 账号 = " + account);
        MyLog.i(TAG, "请求回调-用户 = 解析 = 密码 = " + password);
        MyLog.i(TAG, "请求回调-用户 = 解析 = 注册时间= " + register_time);
        MyLog.i(TAG, "请求回调-用户 = 解析 = 登录类型= " + type);

        //存储用户ID
        BaseApplication.setUserId(user_id);
        //存储账号
        mUserSetTools.set_user_account(account);
        //存储密码
        mUserSetTools.set_user_password(password);
        //存储注册时间
        mUserSetTools.set_user_register_time(register_time);
        //登录类型
        mUserSetTools.set_user_login_type(type);
        //登录类型
        mUserSetTools.setUserAuthorizationCode(authorizationCode);
        // 初始化，刷新authorizationCode
        MyOkHttpClient.getInstance().initMyOkHttpClient();
    }

}





