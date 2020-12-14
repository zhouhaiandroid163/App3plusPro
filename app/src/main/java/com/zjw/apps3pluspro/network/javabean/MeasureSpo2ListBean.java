package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.sql.dbmanager.MeasureSpo2InfoUtils;
import com.zjw.apps3pluspro.sql.entity.MeasureSpo2Info;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjw on 2018/3/19.
 */

public class MeasureSpo2ListBean {
    private static final String TAG = MeasureSpo2ListBean.class.getSimpleName();
    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : {"pageCount":1,"spoMeasureList":[{"spoMeasureData":"89","spoMeasureTime":"2020-04-07 16:46:30.0","userId":779987}]}
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
         * spoMeasureList : [{"spoMeasureData":"89","spoMeasureTime":"2020-04-07 16:46:30.0","userId":779987}]
         */

        private int pageCount;
        private List<SpoMeasureListBean> spoMeasureList;

        public int getPageCount() {
            return pageCount;
        }

        public void setPageCount(int pageCount) {
            this.pageCount = pageCount;
        }

        public List<SpoMeasureListBean> getSpoMeasureList() {
            return spoMeasureList;
        }

        public void setSpoMeasureList(List<SpoMeasureListBean> spoMeasureList) {
            this.spoMeasureList = spoMeasureList;
        }

        public static class SpoMeasureListBean {
            /**
             * spoMeasureData : 89
             * spoMeasureTime : 2020-04-07 16:46:30.0
             * userId : 779987
             */

            private String spoMeasureData;
            private String spoMeasureTime;
            private int userId;

            public String getSpoMeasureData() {
                return spoMeasureData;
            }

            public void setSpoMeasureData(String spoMeasureData) {
                this.spoMeasureData = spoMeasureData;
            }

            public String getSpoMeasureTime() {
                return spoMeasureTime;
            }

            public void setSpoMeasureTime(String spoMeasureTime) {
                this.spoMeasureTime = spoMeasureTime;
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
     * @param mMeasureSpo2InfoUtils
     * @param date
     */
    public static void insertNullData(MeasureSpo2InfoUtils mMeasureSpo2InfoUtils, String date) {

        MeasureSpo2Info mMeasureSpo2Info = new MeasureSpo2Info();
        mMeasureSpo2Info.setUser_id(BaseApplication.getUserId());
        mMeasureSpo2Info.setMeasure_time(date + " 00:00:01");
        mMeasureSpo2Info.setMeasure_spo2("");
        mMeasureSpo2Info.setSync_state("1");

        MyLog.i(TAG, "插入健康表 =  mMeasureSpo2Info = " + mMeasureSpo2Info.toString());
        boolean isSuccess = mMeasureSpo2InfoUtils.MyUpdateData(mMeasureSpo2Info);
        if (isSuccess) {
            MyLog.i(TAG, "插入健康表成功！");
        } else {
            MyLog.i(TAG, "插入健康表失败！");
        }

    }

    public List<MeasureSpo2Info> getInfoList(List<MeasureSpo2ListBean.DataBean.SpoMeasureListBean> mSpoMeasureList) {

        List<MeasureSpo2Info> ResultData = new ArrayList<>();

        if (mSpoMeasureList.size() > 0) {

            for (int i = 0; i < mSpoMeasureList.size(); i++) {

                ResultData.add(getInfo(mSpoMeasureList.get(i)));
            }
        }


        return ResultData;
    }


    public MeasureSpo2Info getInfo(MeasureSpo2ListBean.DataBean.SpoMeasureListBean mDataBean) {


        String date = !JavaUtil.checkIsNull(mDataBean.getSpoMeasureTime()) ? mDataBean.getSpoMeasureTime() : "0";
        String spo2_value = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getSpoMeasureData())) ? String.valueOf(mDataBean.getSpoMeasureData()) : "0";
        String user_id = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getUserId())) ? String.valueOf(mDataBean.getUserId()) : "0";


        MeasureSpo2Info mMeasureSpo2Info = new MeasureSpo2Info();
        mMeasureSpo2Info.setUser_id(user_id);
        mMeasureSpo2Info.setMeasure_time(date);
        mMeasureSpo2Info.setMeasure_spo2(spo2_value);
        mMeasureSpo2Info.setSync_state("1");

        return mMeasureSpo2Info;


    }



}
