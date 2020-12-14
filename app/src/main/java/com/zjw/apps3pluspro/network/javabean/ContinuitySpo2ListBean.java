package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.sql.dbmanager.ContinuitySpo2InfoUtils;
import com.zjw.apps3pluspro.sql.entity.ContinuitySpo2Info;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.List;

/**
 * Created by zjw on 2018/3/19.
 */

public class ContinuitySpo2ListBean {
    private static final String TAG = ContinuitySpo2ListBean.class.getSimpleName();
    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : [{"spoData":"0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0","spoDate":"2020-04-06","userId":779987}]
     * codeMsg : 操作成功！
     */

    private int result;
    private String msg;
    private String code;
    private String codeMsg;
    private List<DataBean> data;



    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeMsg() {
        return codeMsg;
    }

    public void setCodeMsg(String codeMsg) {
        this.codeMsg = codeMsg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * spoData : 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
         * spoDate : 2020-04-06
         * userId : 779987
         */

        private String spoData;
        private String spoDate;
        private int userId;

        public String getSpoData() {
            return spoData;
        }

        public void setSpoData(String spoData) {
            this.spoData = spoData;
        }

        public String getSpoDate() {
            return spoDate;
        }

        public void setSpoDate(String spoDate) {
            this.spoDate = spoDate;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
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
     * 获取整点数据
     *
     * @return
     */
    public int isGetSuccess() {
        //获取成功
        if (ResultJson.Code_operation_success.equals(getCode())) {
            return 1;
        }
        //获取失败
        else if (ResultJson.Code_operation_fail.equals(getCode())) {
            return 0;
        }
        //无数据
        else if (ResultJson.Code_no_data.equals(getCode())) {
            return 2;
        } else {
            return -1;
        }
    }

    public static ContinuitySpo2Info getInfo(DataBean mDataBean) {

        MyLog.i(TAG, "请求接口-获取心率数据 getUserId = " + mDataBean.getUserId());
        MyLog.i(TAG, "请求接口-获取心率数据 getSpoDate = " + mDataBean.getSpoDate());
        MyLog.i(TAG, "请求接口-获取心率数据 getSpoData = " + mDataBean.getSpoData());

        String user_id = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getUserId())) ? String.valueOf(mDataBean.getUserId()) : "";
        String heart_data = !JavaUtil.checkIsNull(mDataBean.getSpoData()) ? mDataBean.getSpoData() : "";
        String heart_date = !JavaUtil.checkIsNull(mDataBean.getSpoDate()) ? mDataBean.getSpoDate() : "";

        ContinuitySpo2Info mContinuitySpo2Info = new ContinuitySpo2Info();
        mContinuitySpo2Info.setUser_id(user_id);
        mContinuitySpo2Info.setData(heart_data);
        mContinuitySpo2Info.setDate(heart_date);
        mContinuitySpo2Info.setSync_state("1");

        return mContinuitySpo2Info;

    }


    /**
     * 获取空数据
     *
     * @param date      日期
     * @return
     */
    static ContinuitySpo2Info getNullgetDataInfo(String date) {

        String heart_data = "0";

        ContinuitySpo2Info mContinuitySpo2Info = new ContinuitySpo2Info();
        mContinuitySpo2Info.setUser_id(BaseApplication.getUserId());
        mContinuitySpo2Info.setDate(date);
        mContinuitySpo2Info.setData(heart_data);
        mContinuitySpo2Info.setSync_state("1");

        return mContinuitySpo2Info;

    }

    /**
     * 根据数据列表，插入空数据-为了处理，在后台查询不到数据做的。
     *
     * @param date
     */
    public static void insertNullData(ContinuitySpo2InfoUtils mContinuitySpo2InfoUtils, String date) {

        MyLog.i(TAG, "待处理 date = " + date);

        ContinuitySpo2Info mContinuitySpo2Info = getNullgetDataInfo(date);

        boolean isSuccess = mContinuitySpo2InfoUtils.MyUpdateData(mContinuitySpo2Info);

        if (isSuccess) {
            MyLog.i(TAG, "插入单条连续血氧成功！");
        } else {
            MyLog.i(TAG, "插入单条连续血氧失败！");
        }


    }

    public ContinuitySpo2Info getContinuitySpo2Info(ContinuitySpo2ListBean.DataBean mDataBean) {


        MyLog.i(TAG, "请求接口-获取连续血氧数据 getUserId = " + mDataBean.getUserId());
        MyLog.i(TAG, "请求接口-获取连续血氧数据 getSpoData = " + mDataBean.getSpoData());
        MyLog.i(TAG, "请求接口-获取连续血氧数据 getSpoDate = " + mDataBean.getSpoDate());

        String user_id = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getUserId())) ? String.valueOf(mDataBean.getUserId()) : "";
        String date = !JavaUtil.checkIsNull(mDataBean.getSpoDate()) ? mDataBean.getSpoDate() : "";
        String data = !JavaUtil.checkIsNull(mDataBean.getSpoData()) ? mDataBean.getSpoData() : ResultJson.Duflet_continuity_spo2_data;

        ContinuitySpo2Info mContinuitySpo2Info = new ContinuitySpo2Info();
        mContinuitySpo2Info.setUser_id(user_id);
        mContinuitySpo2Info.setData(data);
        mContinuitySpo2Info.setDate(date);
        mContinuitySpo2Info.setSync_state("1");
        return mContinuitySpo2Info;


    }
}
