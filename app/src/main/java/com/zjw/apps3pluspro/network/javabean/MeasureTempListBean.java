package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.sql.dbmanager.MeasureTempInfoUtils;
import com.zjw.apps3pluspro.sql.entity.MeasureTempInfo;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjw on 2018/3/19.
 */

public class MeasureTempListBean {
    private static final String TAG = MeasureTempListBean.class.getSimpleName();
    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : {"pageCount":1,"temperatureMeasureList":[{"temperatureDifference":"0","temperatureMeasureData":"83","temperatureMeasureTime":"2020-04-06 21:12:24","userId":779987}]}
     * codeMsg : 操作成功！
     */

    private int result;
    private String msg;
    private String code;
    private DataBean data;
    private String codeMsg;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getCodeMsg() {
        return codeMsg;
    }

    public void setCodeMsg(String codeMsg) {
        this.codeMsg = codeMsg;
    }

    public static class DataBean {
        /**
         * pageCount : 1
         * temperatureMeasureList : [{"temperatureDifference":"0","temperatureMeasureData":"83","temperatureMeasureTime":"2020-04-06 21:12:24","userId":779987}]
         */

        private int pageCount;
        private List<TemperatureMeasureListBean> temperatureMeasureList;

        public int getPageCount() {
            return pageCount;
        }

        public void setPageCount(int pageCount) {
            this.pageCount = pageCount;
        }

        public List<TemperatureMeasureListBean> getTemperatureMeasureList() {
            return temperatureMeasureList;
        }

        public void setTemperatureMeasureList(List<TemperatureMeasureListBean> temperatureMeasureList) {
            this.temperatureMeasureList = temperatureMeasureList;
        }

        public static class TemperatureMeasureListBean {
            /**
             * temperatureDifference : 0
             * temperatureMeasureData : 83
             * temperatureMeasureTime : 2020-04-06 21:12:24
             * userId : 779987
             */

            private String temperatureDifference;
            private String temperatureMeasureData;
            private String temperatureMeasureTime;
            private int userId;

            public String getTemperatureDifference() {
                return temperatureDifference;
            }

            public void setTemperatureDifference(String temperatureDifference) {
                this.temperatureDifference = temperatureDifference;
            }

            public String getTemperatureMeasureData() {
                return temperatureMeasureData;
            }

            public void setTemperatureMeasureData(String temperatureMeasureData) {
                this.temperatureMeasureData = temperatureMeasureData;
            }

            public String getTemperatureMeasureTime() {
                return temperatureMeasureTime;
            }

            public void setTemperatureMeasureTime(String temperatureMeasureTime) {
                this.temperatureMeasureTime = temperatureMeasureTime;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }
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

    /**
     * 插入一条空数据
     *
     * @param mMeasureTempInfoUtils
     * @param date
     */
    public static void insertNullData(MeasureTempInfoUtils mMeasureTempInfoUtils, String date) {

        MeasureTempInfo mMeasureTempInfo = new MeasureTempInfo();
        mMeasureTempInfo.setUser_id(BaseApplication.getUserId());
        mMeasureTempInfo.setMeasure_time(date + " 00:00:01");
        mMeasureTempInfo.setMeasure_wrist_temp("");
        mMeasureTempInfo.setMeasure_temp_difference("");
        mMeasureTempInfo.setSync_state("1");

        MyLog.i(TAG, "插入体温表 =  mMeasureTempInfo = " + mMeasureTempInfo.toString());

        boolean isSuccess = mMeasureTempInfoUtils.MyUpdateData(mMeasureTempInfo);
        if (isSuccess) {
            MyLog.i(TAG, "插入体温表成功！");
        } else {
            MyLog.i(TAG, "插入体温表失败！");
        }

    }

    public List<MeasureTempInfo> getInfoList(List<MeasureTempListBean.DataBean.TemperatureMeasureListBean> mTempMeasureList) {

        List<MeasureTempInfo> ResultData = new ArrayList<>();

        if (mTempMeasureList.size() > 0) {

            for (int i = 0; i < mTempMeasureList.size(); i++) {

                ResultData.add(getInfo(mTempMeasureList.get(i)));
            }
        }


        return ResultData;
    }


    public MeasureTempInfo getInfo(MeasureTempListBean.DataBean.TemperatureMeasureListBean mDataBean) {


        String date = !JavaUtil.checkIsNull(mDataBean.getTemperatureMeasureTime()) ? mDataBean.getTemperatureMeasureTime() : "0";
        String wrist_value = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getTemperatureMeasureData())) ? String.valueOf(mDataBean.getTemperatureMeasureData()) : "0";
        String difference_value = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getTemperatureDifference())) ? String.valueOf(mDataBean.getTemperatureDifference()) : "0";
        String user_id = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getUserId())) ? String.valueOf(mDataBean.getUserId()) : "0";


        MeasureTempInfo mMeasureTempInfo = new MeasureTempInfo();
        mMeasureTempInfo.setUser_id(user_id);
        mMeasureTempInfo.setMeasure_time(date);
        mMeasureTempInfo.setMeasure_wrist_temp(wrist_value);
        mMeasureTempInfo.setMeasure_temp_difference(difference_value);
        mMeasureTempInfo.setSync_state("1");

        return mMeasureTempInfo;


    }



}
