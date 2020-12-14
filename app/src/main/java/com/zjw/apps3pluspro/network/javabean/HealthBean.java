package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.sql.dbmanager.HealthInfoUtils;
import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.NewTimeUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjw on 2018/4/2.
 */

public class HealthBean {
    private static final String TAG = HealthBean.class.getSimpleName();

    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : {"healthList":[{"bodyLoad":67,"bodyQuality":72,"cardiacFunction":75,"diastolic":136,"fatigueIndex":65,"healthIndex":72,"healthMeasuringTime":"2019-05-16 12:00:00","heart":113,"hrvResult":2,"id":101,"systolic":87,"userId":3},{"bodyLoad":65,"bodyQuality":73,"cardiacFunction":75,"diastolic":128,"fatigueIndex":58,"healthIndex":73,"healthMeasuringTime":"2019-05-16 02:32:30","heart":97,"hrvResult":0,"id":102,"systolic":82,"userId":3}],"pageCount":2}
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
         * healthList : [{"bodyLoad":67,"bodyQuality":72,"cardiacFunction":75,"diastolic":136,"fatigueIndex":65,"healthIndex":72,"healthMeasuringTime":"2019-05-16 12:00:00","heart":113,"hrvResult":2,"id":101,"systolic":87,"userId":3},{"bodyLoad":65,"bodyQuality":73,"cardiacFunction":75,"diastolic":128,"fatigueIndex":58,"healthIndex":73,"healthMeasuringTime":"2019-05-16 02:32:30","heart":97,"hrvResult":0,"id":102,"systolic":82,"userId":3}]
         * pageCount : 2
         */

        private int pageCount;
        private List<HealthListBean> healthList;

        public int getPageCount() {
            return pageCount;
        }

        public void setPageCount(int pageCount) {
            this.pageCount = pageCount;
        }

        public List<HealthListBean> getHealthList() {
            return healthList;
        }

        public void setHealthList(List<HealthListBean> healthList) {
            this.healthList = healthList;
        }

        public static class HealthListBean {
            /**
             * bodyLoad : 67
             * bodyQuality : 72
             * cardiacFunction : 75
             * diastolic : 136
             * fatigueIndex : 65
             * healthIndex : 72
             * healthMeasuringTime : 2019-05-16 12:00:00
             * heart : 113
             * hrvResult : 2
             * id : 101
             * systolic : 87
             * deviceSensorType : 1
             * bpStatus : 0
             * userId : 3
             */

            private int bodyLoad;
            private int bodyQuality;
            private int cardiacFunction;
            private int diastolic;
            private int fatigueIndex;
            private int healthIndex;
            private String healthMeasuringTime;
            private int heart;
            private int hrvResult;
            private int id;
            private int systolic;
            private int deviceSensorType;
            private int bpStatus;
            private int userId;

            public int getBodyLoad() {
                return bodyLoad;
            }

            public void setBodyLoad(int bodyLoad) {
                this.bodyLoad = bodyLoad;
            }

            public int getBodyQuality() {
                return bodyQuality;
            }

            public void setBodyQuality(int bodyQuality) {
                this.bodyQuality = bodyQuality;
            }

            public int getCardiacFunction() {
                return cardiacFunction;
            }

            public void setCardiacFunction(int cardiacFunction) {
                this.cardiacFunction = cardiacFunction;
            }

            public int getDiastolic() {
                return diastolic;
            }

            public void setDiastolic(int diastolic) {
                this.diastolic = diastolic;
            }

            public int getFatigueIndex() {
                return fatigueIndex;
            }

            public void setFatigueIndex(int fatigueIndex) {
                this.fatigueIndex = fatigueIndex;
            }

            public int getHealthIndex() {
                return healthIndex;
            }

            public void setHealthIndex(int healthIndex) {
                this.healthIndex = healthIndex;
            }

            public String getHealthMeasuringTime() {
                return healthMeasuringTime;
            }

            public void setHealthMeasuringTime(String healthMeasuringTime) {
                this.healthMeasuringTime = healthMeasuringTime;
            }

            public int getHeart() {
                return heart;
            }

            public void setHeart(int heart) {
                this.heart = heart;
            }

            public int getHrvResult() {
                return hrvResult;
            }

            public void setHrvResult(int hrvResult) {
                this.hrvResult = hrvResult;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getSystolic() {
                return systolic;
            }

            public void setSystolic(int systolic) {
                this.systolic = systolic;
            }

            public int getDeviceSensorType() {
                return deviceSensorType;
            }

            public void setDeviceSensorType(int deviceSensorType) {
                this.deviceSensorType = deviceSensorType;
            }

            public int getBpStatus() {
                return bpStatus;
            }

            public void setBpStatus(int bpStatus) {
                this.bpStatus = bpStatus;
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
     * 上传整点心率数据
     *
     * @return
     */
    public int isUploadHealthSuccess() {
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
     * 获取健康数据
     *
     * @return
     */
    public int isGetHealthSuccess() {
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


    public HealthInfo getHealthInfo(DataBean.HealthListBean mDataBean) {


        String health_date = !JavaUtil.checkIsNull(mDataBean.getHealthMeasuringTime()) ? mDataBean.getHealthMeasuringTime() : "0";
        String data_id = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getId())) ? String.valueOf(mDataBean.getId()) : "0";
        String health_heart = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getHeart())) ? String.valueOf(mDataBean.getHeart()) : "0";
        String health_systolic = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getSystolic())) ? String.valueOf(mDataBean.getSystolic()) : "0";
        String health_diastolic = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getDiastolic())) ? String.valueOf(mDataBean.getDiastolic()) : "0";
        String health_report = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getHrvResult())) ? String.valueOf(mDataBean.getHrvResult()) : "0";
//        String ecg_data = !JavaUtil.checkIsNull(mDataBean.getHealthEcgData()) ? mDataBean.getHealthEcgData() : "";
//        String ppg_data = !JavaUtil.checkIsNull(mDataBean.getHealthPpgData()) ? mDataBean.getHealthPpgData() : "";
        String index_health = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getHealthIndex())) ? String.valueOf(mDataBean.getHealthIndex()) : "";
        String index_quality = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getBodyQuality())) ? String.valueOf(mDataBean.getBodyQuality()) : "";
        String index_load = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getBodyLoad())) ? String.valueOf(mDataBean.getBodyLoad()) : "";
        String index_fatigue = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getFatigueIndex())) ? String.valueOf(mDataBean.getFatigueIndex()) : "";
        String index_heart = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getCardiacFunction())) ? String.valueOf(mDataBean.getCardiacFunction()) : "";

        String sensor_type = !JavaUtil.checkIsNullnoZero(String.valueOf(mDataBean.getDeviceSensorType())) ? String.valueOf(mDataBean.getDeviceSensorType()) : "1";
        String is_support_bp = !JavaUtil.checkIsNullnoZero(String.valueOf(mDataBean.getBpStatus())) ? String.valueOf(mDataBean.getBpStatus()) : "0";

        HealthInfo mHealthInfo = new HealthInfo();
        mHealthInfo.setUser_id(BaseApplication.getUserId());
        mHealthInfo.setMeasure_time(health_date);
        mHealthInfo.setData_id(data_id);
        mHealthInfo.setHealth_heart(health_heart);
        mHealthInfo.setHealth_systolic(health_systolic);
        mHealthInfo.setHealth_diastolic(health_diastolic);
        mHealthInfo.setHealth_ecg_report(health_report);
//        mHealthInfo.setEcg_data(ecg_data);
//        mHealthInfo.setPpg_data(ppg_data);
        mHealthInfo.setIndex_health_index(index_health);
        mHealthInfo.setIndex_body_quality(index_quality);
        mHealthInfo.setIndex_body_load(index_load);
        mHealthInfo.setIndex_fatigue_index(index_fatigue);
        mHealthInfo.setIndex_cardiac_function(index_heart);

        mHealthInfo.setSensor_type(sensor_type);
        mHealthInfo.setIs_suppor_bp(is_support_bp);

        mHealthInfo.setSync_state("1");

        return mHealthInfo;


    }


    /**
     * 获取一条空的健康数据，村本地，避免重复向后台拿,DataID = -1
     *
     * @return
     */
    public static HealthInfo getEcgHealthNullInfo(String health_date) {

        HealthInfo mHealthInfo = new HealthInfo();
        mHealthInfo.setUser_id(BaseApplication.getUserId());
        mHealthInfo.setMeasure_time(health_date + " 00:00:01");
        mHealthInfo.setData_id(ResultJson.Duflet_health_data_id);
        mHealthInfo.setHealth_heart("");
        mHealthInfo.setHealth_systolic("");
        mHealthInfo.setHealth_diastolic("");
        mHealthInfo.setHealth_ecg_report("");
        mHealthInfo.setEcg_data("");
        mHealthInfo.setPpg_data("");
        mHealthInfo.setIndex_health_index("");
        mHealthInfo.setIndex_body_quality("");
        mHealthInfo.setIndex_body_load("");
        mHealthInfo.setIndex_fatigue_index("");
        mHealthInfo.setIndex_cardiac_function("");
        mHealthInfo.setSensor_type("1");//支持ECG+PPG
        mHealthInfo.setIs_suppor_bp("1");//不支持血压
        mHealthInfo.setSync_state("1");

        return mHealthInfo;


    }

    /**
     * 获取一条空的健康数据，村本地，避免重复向后台拿,DataID = -1
     *
     * @return
     */
    public static HealthInfo getPpgHealthNullInfo(String health_date) {

        HealthInfo mHealthInfo = new HealthInfo();
        mHealthInfo.setUser_id(BaseApplication.getUserId());
        mHealthInfo.setMeasure_time(health_date + " 00:00:01");
        mHealthInfo.setData_id(ResultJson.Duflet_health_data_id);
        mHealthInfo.setHealth_heart("");
        mHealthInfo.setHealth_systolic("");
        mHealthInfo.setHealth_diastolic("");
        mHealthInfo.setHealth_ecg_report("");
        mHealthInfo.setEcg_data("");
        mHealthInfo.setPpg_data("");
        mHealthInfo.setIndex_health_index("");
        mHealthInfo.setIndex_body_quality("");
        mHealthInfo.setIndex_body_load("");
        mHealthInfo.setIndex_fatigue_index("");
        mHealthInfo.setIndex_cardiac_function("");
        mHealthInfo.setSensor_type("2");//支持PPG
        mHealthInfo.setIs_suppor_bp("1");//不支持血压
        mHealthInfo.setSync_state("1");

        return mHealthInfo;

    }


    /**
     * 插入一条空数据
     *
     * @param mHealthInfoUtils
     * @param date
     * @param is_support_ecg
     */
    public static void insertNullHealth(HealthInfoUtils mHealthInfoUtils, String date, boolean is_support_ecg) {


        HealthInfo mHealthInfo;
        if (is_support_ecg) {
            mHealthInfo = HealthBean.getEcgHealthNullInfo(date);
        } else {
            mHealthInfo = HealthBean.getPpgHealthNullInfo(date);
        }
        MyLog.i(TAG, "插入健康表 =  mHealthInfo = " + mHealthInfo.toString());
        boolean isSuccess_health = mHealthInfoUtils.MyUpdateData(mHealthInfo);
        if (isSuccess_health) {
            MyLog.i(TAG, "插入健康表成功！");
        } else {
            MyLog.i(TAG, "插入健康表失败！");
        }

    }

    /**
     * 插入多条空数据
     *
     * @param mHealthInfoUtils
     * @param my_date_list
     * @param is_support_ecg
     */
    public static void insertNullHealthList(HealthInfoUtils mHealthInfoUtils, ArrayList<String> my_date_list, boolean is_support_ecg) {


        List<HealthInfo> healthInfo_list = new ArrayList<>();

        if (my_date_list.size() > 0) {
            for (int i = 0; i < my_date_list.size(); i++) {
                if (is_support_ecg) {
                    healthInfo_list.add(getEcgHealthNullInfo(my_date_list.get(i)));
                } else {
                    healthInfo_list.add(getPpgHealthNullInfo(my_date_list.get(i)));
                }

            }
        }

        for (HealthInfo mHealthInfo : healthInfo_list) {
            MyLog.i(TAG, "解析数组 = healthInfo_list = " + mHealthInfo.toString());
        }

        boolean isSuccess = mHealthInfoUtils.insertInfoList(healthInfo_list);
        if (isSuccess) {
            MyLog.i(TAG, "插入多条运动表成功！");
        } else {
            MyLog.i(TAG, "插入多条运动表失败！");
        }

    }


    public List<HealthInfo> getHealthInfoList(List<DataBean.HealthListBean> mDataBeanList) {

        List<HealthInfo> ResultData = new ArrayList<>();

        if (mDataBeanList.size() > 0) {

            for (int i = 0; i < mDataBeanList.size(); i++) {

                ResultData.add(getHealthInfo(mDataBeanList.get(i)));
            }
        }


        return ResultData;
    }

    public List<HealthInfo> getHealthInfoWeekList(ArrayList<String> my_date_list, List<DataBean.HealthListBean> mDataBeanList, boolean is_support_ecg) {


        List<HealthInfo> ResultData = new ArrayList<>();

        if (mDataBeanList.size() > 0) {
            for (int i = 0; i < mDataBeanList.size(); i++) {
                ResultData.add(getHealthInfo(mDataBeanList.get(i)));
            }
        }


        ArrayList<String> new_list = new ArrayList<>();

        for (int i = 0; i < my_date_list.size(); i++) {

            boolean isYes = false;
            for (int j = 0; j < ResultData.size(); j++) {


                if (is_support_ecg) {
                    if (my_date_list.get(i).equals(NewTimeUtils.getTimeFormatDate(ResultData.get(j).getMeasure_time()))) {
                        if (ResultData.get(j).isEcgDevice()) {
                            isYes = true;
                            break;
                        }

                    }
                } else {
                    if (my_date_list.get(i).equals(NewTimeUtils.getTimeFormatDate(ResultData.get(j).getMeasure_time()))) {
                        if (ResultData.get(j).isSportDevice()) {
                            isYes = true;
                            break;
                        }
                        break;
                    }
                }


            }

            if (!isYes) {
                new_list.add(my_date_list.get(i));
            }
        }


        if (new_list.size() > 0) {
            for (int i = 0; i < new_list.size(); i++) {
                if (is_support_ecg) {
                    ResultData.add(getEcgHealthNullInfo(new_list.get(i)));
                } else {

                    ResultData.add(getPpgHealthNullInfo(new_list.get(i)));
                }


            }
        }


        return ResultData;

    }


}
