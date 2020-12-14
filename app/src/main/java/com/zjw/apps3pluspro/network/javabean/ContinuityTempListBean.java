package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.sql.dbmanager.ContinuityTempInfoUtils;
import com.zjw.apps3pluspro.sql.entity.ContinuityTempInfo;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.List;

/**
 * Created by zjw on 2018/3/19.
 */

public class ContinuityTempListBean {
    private static final String TAG = ContinuityTempListBean.class.getSimpleName();
    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : [{"temperatureData":"0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0","temperatureDate":"2020-04-06","temperatureDifference":"","userId":779987}]
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
         * temperatureData : 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
         * temperatureDate : 2020-04-06
         * temperatureDifference :
         * userId : 779987
         */

        private String temperatureData;
        private String temperatureDate;
        private String temperatureDifference;
        private int userId;

        public String getTemperatureData() {
            return temperatureData;
        }

        public void setTemperatureData(String temperatureData) {
            this.temperatureData = temperatureData;
        }

        public String getTemperatureDate() {
            return temperatureDate;
        }

        public void setTemperatureDate(String temperatureDate) {
            this.temperatureDate = temperatureDate;
        }

        public String getTemperatureDifference() {
            return temperatureDifference;
        }

        public void setTemperatureDifference(String temperatureDifference) {
            this.temperatureDifference = temperatureDifference;
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

    public static ContinuityTempInfo getInfo(DataBean mDataBean) {

        MyLog.i(TAG, "请求接口-获取心率数据 getUserId = " + mDataBean.getUserId());
        MyLog.i(TAG, "请求接口-获取心率数据 getTemperatureDate = " + mDataBean.getTemperatureDate());
        MyLog.i(TAG, "请求接口-获取心率数据 getTemperatureData = " + mDataBean.getTemperatureData());

        String user_id = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getUserId())) ? String.valueOf(mDataBean.getUserId()) : "";
        String temp_data = !JavaUtil.checkIsNull(mDataBean.getTemperatureData()) ? mDataBean.getTemperatureData() : "";
        String temp_date = !JavaUtil.checkIsNull(mDataBean.getTemperatureDate()) ? mDataBean.getTemperatureDate() : "";
        String temp_difference = !JavaUtil.checkIsNull(mDataBean.getTemperatureDifference()) ? mDataBean.getTemperatureDifference() : "0";

        ContinuityTempInfo mContinuityTempInfo = new ContinuityTempInfo();
        mContinuityTempInfo.setUser_id(user_id);
        mContinuityTempInfo.setData(temp_data);
        mContinuityTempInfo.setDate(temp_date);
        mContinuityTempInfo.setTemp_difference(temp_difference);
        mContinuityTempInfo.setSync_state("1");

        return mContinuityTempInfo;

    }


    /**
     * 获取空数据
     *
     * @param date 日期
     * @return
     */
    static ContinuityTempInfo getNullgetDataInfo(String date) {

        String temp_data = "0";
        String temp_difference = "0";

        ContinuityTempInfo mContinuityTempInfo = new ContinuityTempInfo();
        mContinuityTempInfo.setUser_id(BaseApplication.getUserId());
        mContinuityTempInfo.setDate(date);
        mContinuityTempInfo.setData(temp_data);
        mContinuityTempInfo.setTemp_difference(temp_difference);
        mContinuityTempInfo.setSync_state("1");

        return mContinuityTempInfo;

    }

    /**
     * 根据数据列表，插入空数据-为了处理，在后台查询不到数据做的。
     *
     * @param date
     */
    public static void insertNullData(ContinuityTempInfoUtils mContinuityTempInfoUtils, String date) {

        MyLog.i(TAG, "待处理 date = " + date);

        ContinuityTempInfo mContinuityTempInfo = getNullgetDataInfo(date);

        boolean isSuccess = mContinuityTempInfoUtils.MyUpdateData(mContinuityTempInfo);

        if (isSuccess) {
            MyLog.i(TAG, "插入单条连续体温成功！");
        } else {
            MyLog.i(TAG, "插入单条连续体温失败！");
        }

    }

    public ContinuityTempInfo getContinuityTempInfo(ContinuityTempListBean.DataBean mDataBean) {

        MyLog.i(TAG, "请求接口-获取连续体温数据 getUserId = " + mDataBean.getUserId());
        MyLog.i(TAG, "请求接口-获取连续体温数据 getTemperatureData = " + mDataBean.getTemperatureData());
        MyLog.i(TAG, "请求接口-获取连续体温数据 getTemperatureDate = " + mDataBean.getTemperatureDate());
        MyLog.i(TAG, "请求接口-获取连续体温数据 getTemperatureDifference = " + mDataBean.getTemperatureDifference());

        String user_id = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getUserId())) ? String.valueOf(mDataBean.getUserId()) : "";
        String date = !JavaUtil.checkIsNull(mDataBean.getTemperatureDate()) ? mDataBean.getTemperatureDate() : "";
        String data = !JavaUtil.checkIsNull(mDataBean.getTemperatureData()) ? mDataBean.getTemperatureData() : ResultJson.Duflet_continuity_temp_data;
        String difference = !JavaUtil.checkIsNull(mDataBean.getTemperatureDifference()) ? mDataBean.getTemperatureDifference() : "0";

        ContinuityTempInfo mContinuityTempInfo = new ContinuityTempInfo();
        mContinuityTempInfo.setUser_id(user_id);
        mContinuityTempInfo.setData(data);
        mContinuityTempInfo.setDate(date);
        mContinuityTempInfo.setTemp_difference(difference);
        mContinuityTempInfo.setSync_state("1");
        return mContinuityTempInfo;


    }
}
