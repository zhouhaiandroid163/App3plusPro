package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.sql.dbmanager.HeartInfoUtils;
import com.zjw.apps3pluspro.sql.entity.HeartInfo;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjw on 2018/3/19.
 */

public class HeartBean {
    private static final String TAG = HeartBean.class.getSimpleName();
    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : [{"heartRate24rawdata":"0,0,0,0,0,0,0,0,0,0,69,87,76,79,77,0,0,0,0,0,0,0,0,0","heartRateRawDate":"2019-05-13","id":1,"userId":3}]
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
         * heartRate24rawdata : 0,0,0,0,0,0,0,0,0,0,69,87,76,79,77,0,0,0,0,0,0,0,0,0
         * heartRateRawDate : 2019-05-13
         * id : 1
         * userId : 3
         * status : 1
         */

        private String heartRateRawDate;
        private String heartRateRawdata;
        private String heartRateType;
        private int id;
        private int userId;
        private int status;


        public String getHeartRateRawDate() {
            return heartRateRawDate;
        }

        public void setHeartRateRawDate(String heartRateRawDate) {
            this.heartRateRawDate = heartRateRawDate;
        }

        public String getHeartRateRawdata() {
            return heartRateRawdata;
        }

        public void setHeartRateRawdata(String heartRateRawdata) {
            this.heartRateRawdata = heartRateRawdata;
        }

        public String getHeartRateType() {
            return heartRateType;
        }

        public void setHeartRateType(String heartRateType) {
            this.heartRateType = heartRateType;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "heartRateRawDate='" + heartRateRawDate + '\'' +
                    ", heartRateRawdata='" + heartRateRawdata + '\'' +
                    ", heartRateType='" + heartRateType + '\'' +
                    ", id=" + id +
                    ", userId=" + userId +
                    ", status=" + status +
                    '}';
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
     * 上传整点心率数据
     *
     * @return
     */
    public int isUploadHeartSuccess() {
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

    /**
     * 获取整点数据
     *
     * @return
     */
    public int isGetHeartSuccess() {
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

    public static HeartInfo getHeartInfo(HeartBean.DataBean mDataBean) {

        MyLog.i(TAG, "请求接口-获取心率数据 getUserId = " + mDataBean.getUserId());
        MyLog.i(TAG, "请求接口-获取心率数据 getHeartRateRawDate = " + mDataBean.getHeartRateRawDate());
        MyLog.i(TAG, "请求接口-获取心率数据 getHeartRateRawdata = " + mDataBean.getHeartRateRawdata());

        String user_id = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getUserId())) ? String.valueOf(mDataBean.getUserId()) : "";
        String heart_date = !JavaUtil.checkIsNull(mDataBean.getHeartRateRawDate()) ? mDataBean.getHeartRateRawDate() : "";
        String heart_data = !JavaUtil.checkIsNull(mDataBean.getHeartRateRawdata()) ? mDataBean.getHeartRateRawdata() : "";
        String poheart_type = !JavaUtil.checkIsNull(mDataBean.getHeartRateType()) ? mDataBean.getHeartRateType() : "0";

        HeartInfo mHeartInfo = new HeartInfo();
        mHeartInfo.setUser_id(user_id);
        mHeartInfo.setData(heart_data);
        mHeartInfo.setDate(heart_date);
        mHeartInfo.setData_type(poheart_type);
        mHeartInfo.setSync_state("1");

        return mHeartInfo;


    }


    /**
     * 获取空数据
     *
     * @param date      日期
     * @param data_type 数据类型
     * @return
     */
    static HeartInfo getNullgetHeartInfo(String date, String data_type) {

        String heart_data = "0";

        if (data_type.equals("0")) {
            heart_data = ResultJson.Duflet_poheart_data;
        } else {
            heart_data = ResultJson.Duflet_woheart_data;
        }

        HeartInfo mHeartInfo = new HeartInfo();
        mHeartInfo.setUser_id(BaseApplication.getUserId());
        mHeartInfo.setDate(date);
        mHeartInfo.setData(heart_data);
        mHeartInfo.setData_type(data_type);
        mHeartInfo.setSync_state("1");

        return mHeartInfo;

    }

    /**
     * 根据数据列表，插入空数据-为了处理，在后台查询不到数据做的。
     *
     * @param date
     */
    public static void insertPoNullData(HeartInfoUtils mHeartInfoUtils, String date) {

        MyLog.i(TAG, "待处理 date = " + date);

        HeartInfo mHeartInfo = getNullgetHeartInfo(date, "0");

        boolean isSuccess = mHeartInfoUtils.MyUpdateData(mHeartInfo);

        if (isSuccess) {
            MyLog.i(TAG, "插入单条整点心率成功！");
        } else {
            MyLog.i(TAG, "插入单条整点心率失败！");
        }


    }

    /**
     * 根据数据列表，插入空数据-为了处理，在后台查询不到数据做的。
     *
     * @param date
     */
    public static void insertWoNullData(HeartInfoUtils mHeartInfoUtils, String date) {

        MyLog.i(TAG, "待处理 date = " + date);

        HeartInfo mHeartInfo = getNullgetHeartInfo(date, "1");

        boolean isSuccess = mHeartInfoUtils.MyUpdateData(mHeartInfo);

        if (isSuccess) {
            MyLog.i(TAG, "插入单条整点心率成功！");
        } else {
            MyLog.i(TAG, "插入单条整点心率失败！");
        }


    }


    public static List<HeartInfo> getPoHeartList(ArrayList<String> my_date_list, List<HeartBean.DataBean> data_list) {


        List<HeartInfo> heart_list = new ArrayList<>();

        if (data_list.size() > 0) {
            for (int i = 0; i < data_list.size(); i++) {
                heart_list.add(getHeartInfo(data_list.get(i)));
            }
        }


        ArrayList<String> new_list = new ArrayList<>();

        for (int i = 0; i < my_date_list.size(); i++) {

            boolean isYes = false;
            for (int j = 0; j < heart_list.size(); j++) {

                if (my_date_list.get(i).equals(heart_list.get(j).getDate())) {
                    isYes = true;
                    break;
                }
            }

            if (!isYes) {
                new_list.add(my_date_list.get(i));
            }
        }

        if (new_list.size() > 0) {
            for (int i = 0; i < new_list.size(); i++) {
                heart_list.add(getNullgetHeartInfo(new_list.get(i), "0"));
            }
        }


        return heart_list;

    }

    public List<HeartInfo> getWoHeartList(ArrayList<String> my_date_list, List<HeartBean.DataBean> data_list) {


        List<HeartInfo> heart_list = new ArrayList<>();

        if (data_list.size() > 0) {
            for (int i = 0; i < data_list.size(); i++) {
                heart_list.add(getHeartInfo(data_list.get(i)));
            }
        }


        ArrayList<String> new_list = new ArrayList<>();

        for (int i = 0; i < my_date_list.size(); i++) {

            boolean isYes = false;
            for (int j = 0; j < heart_list.size(); j++) {

                if (my_date_list.get(i).equals(heart_list.get(j).getDate())) {
                    isYes = true;
                    break;
                }
            }

            if (!isYes) {
                new_list.add(my_date_list.get(i));
            }
        }

        if (new_list.size() > 0) {
            for (int i = 0; i < new_list.size(); i++) {
                heart_list.add(getNullgetHeartInfo(new_list.get(i), "1"));
            }
        }


        return heart_list;

    }

    /**
     * 根据数据列表，插入空数据-为了处理，在后台查询不到数据做的。
     *
     * @param mHeartInfoUtils
     * @param my_date_list
     */
    public static void insertPoHeartNullListData(HeartInfoUtils mHeartInfoUtils, ArrayList<String> my_date_list) {

        MyLog.i(TAG, "待处理 日期数组 = " + my_date_list);

        List<HeartInfo> heartInfo_list = new ArrayList<>();

        if (my_date_list.size() > 0) {
            for (int i = 0; i < my_date_list.size(); i++) {
                heartInfo_list.add(getNullgetHeartInfo(my_date_list.get(i), "0"));
            }
        }

        for (HeartInfo mHeartInfo : heartInfo_list) {
            MyLog.i(TAG, "解析数组 = sleepInfo_list = " + mHeartInfo.toString());
        }

        boolean isSuccess = mHeartInfoUtils.insertInfoList(heartInfo_list);
        if (isSuccess) {
            MyLog.i(TAG, "插入多条运动表成功！");
        } else {
            MyLog.i(TAG, "插入多条运动表失败！");
        }


    }

    /**
     * 根据数据列表，插入空数据-为了处理，在后台查询不到数据做的。
     *
     * @param mHeartInfoUtils
     * @param my_date_list
     */
    public static void insertWoHeartNullListData(HeartInfoUtils mHeartInfoUtils, ArrayList<String> my_date_list) {

        MyLog.i(TAG, "待处理 日期数组 = " + my_date_list);

        List<HeartInfo> heartInfo_list = new ArrayList<>();

        if (my_date_list.size() > 0) {
            for (int i = 0; i < my_date_list.size(); i++) {
                heartInfo_list.add(getNullgetHeartInfo(my_date_list.get(i), "1"));
            }
        }

        for (HeartInfo mHeartInfo : heartInfo_list) {
            MyLog.i(TAG, "解析数组 = heartInfo_list = " + mHeartInfo.toString());
        }

        boolean isSuccess = mHeartInfoUtils.insertInfoList(heartInfo_list);
        if (isSuccess) {
            MyLog.i(TAG, "插入多条运动表成功！");
        } else {
            MyLog.i(TAG, "插入多条运动表失败！");
        }


    }
}
