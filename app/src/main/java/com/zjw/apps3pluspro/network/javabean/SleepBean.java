package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.sql.dbmanager.SleepInfoUtils;
import com.zjw.apps3pluspro.sql.entity.SleepInfo;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjw on 2018/3/19.
 */

public class SleepBean {
    private static final String TAG = SleepBean.class.getSimpleName();
    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : [{"id":237283,"sleepBeginTime":"","sleepDate":"2019-05-09","sleepEndTime":"","sleepRawdata":"289,611,1122,1636,1730,2755,4130,4547,5090,5539,6274,8771,9826,12611,13154,13380,14882,14917","userId":3},{"id":237284,"sleepBeginTime":"","sleepDate":"2019-05-10","sleepEndTime":"","sleepRawdata":"673,1250,1763,2850,8003,9058,11747,12386,14915,15490,15843,16389","userId":3}]
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
         * id : 237283
         * sleepBeginTime :
         * sleepDate : 2019-05-09
         * sleepEndTime :
         * sleepRawdata : 289,611,1122,1636,1730,2755,4130,4547,5090,5539,6274,8771,9826,12611,13154,13380,14882,14917
         * userId : 3
         * status : 1
         */

        private int id;
        private String sleepBeginTime;
        private String sleepDate;
        private String sleepEndTime;
        private String sleepRawdata;
        private int userId;
        private int status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSleepBeginTime() {
            return sleepBeginTime;
        }

        public void setSleepBeginTime(String sleepBeginTime) {
            this.sleepBeginTime = sleepBeginTime;
        }

        public String getSleepDate() {
            return sleepDate;
        }

        public void setSleepDate(String sleepDate) {
            this.sleepDate = sleepDate;
        }

        public String getSleepEndTime() {
            return sleepEndTime;
        }

        public void setSleepEndTime(String sleepEndTime) {
            this.sleepEndTime = sleepEndTime;
        }

        public String getSleepRawdata() {
            return sleepRawdata;
        }

        public void setSleepRawdata(String sleepRawdata) {
            this.sleepRawdata = sleepRawdata;
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
                    "id=" + id +
                    ", sleepBeginTime='" + sleepBeginTime + '\'' +
                    ", sleepDate='" + sleepDate + '\'' +
                    ", sleepEndTime='" + sleepEndTime + '\'' +
                    ", sleepRawdata='" + sleepRawdata + '\'' +
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
     * 上传睡眠数据
     *
     * @return
     */
    public int isUploadSleepSuccess() {
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
     * 获取睡眠数据
     *
     * @return
     */
    public int isGetSleepSuccess() {
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


    public SleepInfo getSleepInfo(SleepBean.DataBean mDataBean) {


        MyLog.i(TAG, "请求接口-获取睡眠数据 getUserId = " + mDataBean.getUserId());
        MyLog.i(TAG, "请求接口-获取运动数据 getSleepDate = " + mDataBean.getSleepDate());
        MyLog.i(TAG, "请求接口-获取运动数据 getSleepRawdata = " + mDataBean.getSleepRawdata());

        String user_id = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getUserId())) ? String.valueOf(mDataBean.getUserId()) : "";
        String sleep_date = !JavaUtil.checkIsNull(mDataBean.getSleepDate()) ? mDataBean.getSleepDate() : "";
        String sleep_data = !JavaUtil.checkIsNull(mDataBean.getSleepRawdata()) ? mDataBean.getSleepRawdata() : "";

        SleepInfo mSleepInfo = new SleepInfo();
        mSleepInfo.setUser_id(user_id);
        mSleepInfo.setData(sleep_data);
        mSleepInfo.setDate(sleep_date);
        mSleepInfo.setSync_state("1");

        return mSleepInfo;


    }

    public static SleepInfo getNullSleepInfo(String date) {

        String sleep_data = "";

        SleepInfo mSleepInfo = new SleepInfo();
        mSleepInfo.setUser_id(BaseApplication.getUserId());
        mSleepInfo.setData(sleep_data);
        mSleepInfo.setDate(date);
        mSleepInfo.setSync_state("1");

        return mSleepInfo;

    }

    public static void insertNullData(SleepInfoUtils mSleepInfoUtils, String date) {

        SleepInfo mSleepInfo = getNullSleepInfo(date);
        MyLog.i(TAG, "mSleepInfo = " + mSleepInfo.toString());
        boolean isSuccess = mSleepInfoUtils.MyUpdateData(mSleepInfo);
        if (isSuccess) {
            MyLog.i(TAG, "插入睡眠表成功！");
        } else {
            MyLog.i(TAG, "插入运动表失败！");
        }
    }

    public List<SleepInfo> getSleepList(ArrayList<String> my_date_list, List<SleepBean.DataBean> data_list) {


        List<SleepInfo> sleepInfo_list = new ArrayList<>();

        if (data_list.size() > 0) {
            for (int i = 0; i < data_list.size(); i++) {
                sleepInfo_list.add(getSleepInfo(data_list.get(i)));
            }
        }


        ArrayList<String> new_list = new ArrayList<>();

        for (int i = 0; i < my_date_list.size(); i++) {

            boolean isYes = false;
            for (int j = 0; j < sleepInfo_list.size(); j++) {

                if (my_date_list.get(i).equals(sleepInfo_list.get(j).getDate())) {
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
                sleepInfo_list.add(getNullSleepInfo(new_list.get(i)));
            }
        }


        return sleepInfo_list;

    }


    /**
     * 根据数据列表，插入空数据-为了处理，在后台查询不到数据做的。
     *
     * @param mSleepInfoUtils
     * @param my_date_list
     */
    public static void insertNullListData(SleepInfoUtils mSleepInfoUtils, ArrayList<String> my_date_list) {

        MyLog.i(TAG, "待处理 日期数组 = " + my_date_list);

        List<SleepInfo> sleepInfo_list = new ArrayList<>();

        if (my_date_list.size() > 0) {
            for (int i = 0; i < my_date_list.size(); i++) {
                sleepInfo_list.add(SleepBean.getNullSleepInfo(my_date_list.get(i)));
            }
        }

        for (SleepInfo mSleepInfo : sleepInfo_list) {
            MyLog.i(TAG, "解析数组 = sleepInfo_list = " + mSleepInfo.toString());
        }

        boolean isSuccess = mSleepInfoUtils.insertInfoList(sleepInfo_list);
        if (isSuccess) {
            MyLog.i(TAG, "插入多条运动表成功！");
        } else {
            MyLog.i(TAG, "插入多条运动表失败！");
        }


    }

}
