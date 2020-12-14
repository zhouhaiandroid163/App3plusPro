package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;


public class UserBean {
    private static final String TAG = UserBean.class.getSimpleName();

    /**
     * data : {"id":3,"birthday":"1974-09-12","sex":1,"weight":"105","height":"188","userId":3,"nikname":"Grandpa","head":"http://www.wearheart.cn:8089/img/heads/2018-07-31/3_101811935.png"}
     * result : 1
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
         * id : 3
         * birthday : 1974-09-12
         * sex : 1
         * weight : 105
         * height : 188
         * userId : 3
         * nikname : Grandpa
         * head : http://www.wearheart.cn:8089/img/heads/2018-07-31/3_101811935.png
         */

        private int id;
        private String birthday;
        private int sex;
        private String weight;
        private String height;
        private int userId;
        private String nikname;
        private String head;
        private String skinColor = "0";

        public String getSkinColor() {
            return skinColor;
        }

        public void setSkinColor(String skinColor) {
            this.skinColor = skinColor;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getNikname() {
            return nikname;
        }

        public void setNikname(String nikname) {
            this.nikname = nikname;
        }

        public String getHead() {
            return head;
        }

        public void setHead(String head) {
            this.head = head;
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
     * 个人信息获取是否成功
     *
     * @return
     */
    public int isUserSuccess() {
        //获取成功
        if (ResultJson.Code_operation_success.equals(getCode())) {
            return 1;
        }
        //没有数据
        else if (ResultJson.Code_no_data.equals(getCode())) {
            return 0;

        } else {
            return -1;
        }
    }




    /**
     * 修改个人信息
     *
     * @return
     */
    public int uploadUserSuccess() {
        //修改成功
        if (ResultJson.Code_operation_success.equals(getCode())) {
            return 1;
        }
        //修改失败
        else if (ResultJson.Code_operation_fail.equals(getCode())) {
            return 0;
        } else {
            return -1;
        }
    }


//    //========================存储数据==================================


    public static void saveUserInfo(DataBean mDataBean) {


        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();

        MyLog.i(TAG, "请求回调-用户 = 解析 = mDataBean = " + mDataBean.toString());


        MyLog.i(TAG, "请求回调-用户 = 解析 = 昵称= " + mDataBean.getNikname());
        MyLog.i(TAG, "请求回调-用户 = 解析 = 身高 = " + mDataBean.getHeight());
        MyLog.i(TAG, "请求回调-用户 = 解析 = 体重= " + mDataBean.getWeight());
        MyLog.i(TAG, "请求回调-用户 = 解析 = 生日= " + mDataBean.getBirthday());
        MyLog.i(TAG, "请求回调-用户 = 解析 = 性别= " + mDataBean.getSex());
        MyLog.i(TAG, "请求回调-用户 = 解析 = 头像= " + mDataBean.getHead());



        String nickname = (mDataBean.getNikname() != null && !mDataBean.getNikname().equals("")) ? mDataBean.getNikname() : "";
        String height = !JavaUtil.checkIsNull(mDataBean.getHeight()) ? mDataBean.getHeight() : String.valueOf(DefaultVale.USER_HEIGHT);
        String weight = !JavaUtil.checkIsNull(mDataBean.getWeight()) ? mDataBean.getWeight() : String.valueOf(DefaultVale.USER_WEIGHT);
        String birthday = !JavaUtil.checkIsNull(mDataBean.getBirthday()) ? mDataBean.getBirthday() : DefaultVale.USER_BIRTHDAY;
        int sex = (mDataBean.getSex() == 0 || mDataBean.getSex() == 1) ? mDataBean.getSex() : DefaultVale.USER_SEX;
        String head_url = !JavaUtil.checkIsNull(mDataBean.getHead()) ? mDataBean.getHead() : "";


        mUserSetTools.set_user_nickname(nickname);//昵称


        try {
            mUserSetTools.set_user_height(Integer.valueOf(height));//身高
        } catch (Exception e) {
            MyLog.i(TAG, "请求回调-用户 = 解析 = 身高异常");
            mUserSetTools.set_user_height(DefaultVale.USER_HEIGHT);//身高
        }

        try {
            mUserSetTools.set_user_weight(Integer.valueOf(weight));//体重
        } catch (Exception e) {
            MyLog.i(TAG, "请求回调-用户 = 解析 = 体重异常");
            mUserSetTools.set_user_weight(DefaultVale.USER_WEIGHT);//体重
        }


        mUserSetTools.set_user_birthday(birthday);//生日
        mUserSetTools.set_user_sex(sex);//性别
        mUserSetTools.set_user_head_url(head_url);//头像

        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

        try {
            mBleDeviceTools.set_skin_colour(Integer.parseInt(mDataBean.getSkinColor()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            mBleDeviceTools.set_skin_colour(1);
        }
    }


}





