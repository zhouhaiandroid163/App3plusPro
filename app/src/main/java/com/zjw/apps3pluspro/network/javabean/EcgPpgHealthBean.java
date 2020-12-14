package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.network.ResultJson;

import java.util.List;

/**
 * Created by zjw on 2018/4/2.
 */

public class EcgPpgHealthBean {


    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : {"bodyLoad":66,"bodyQuality":73,"bpStatus":0,"cardiacFunction":75,"deviceMac":"DF:0E:5A:47:2E:CC","deviceSensorType":2,"diastolic":91,"ecgList":[{"ecg":"","groupNum":1,"healthId":161,"id":144}],"fatigueIndex":65,"healthIndex":73,"healthMeasuringTime":"2019-06-11 16:02:27","heart":151,"hrvResult":2,"id":161,"ppgList":[{"groupNum":1,"healthId":161,"id":144,"ppg":""}],"systolic":142,"userId":779988}
     * codeMsg : 操作成功！
     */

    private int result;
    private String msg;
    private String code;
    private DataBean data;
    private String codeMsg;

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
         * bodyLoad : 66
         * bodyQuality : 73
         * bpStatus : 0
         * cardiacFunction : 75
         * deviceMac : DF:0E:5A:47:2E:CC
         * deviceSensorType : 2
         * diastolic : 91
         * ecgList : [{"ecg":"","groupNum":1,"healthId":161,"id":144}]
         * fatigueIndex : 65
         * healthIndex : 73
         * healthMeasuringTime : 2019-06-11 16:02:27
         * heart : 151
         * hrvResult : 2
         * id : 161
         * ppgList : [{"groupNum":1,"healthId":161,"id":144,"ppg":""}]
         * systolic : 142
         * userId : 779988
         */

        private int bodyLoad;
        private int bodyQuality;
        private int bpStatus;
        private int cardiacFunction;
        private String deviceMac;
        private int deviceSensorType;
        private int diastolic;
        private int fatigueIndex;
        private int healthIndex;
        private String healthMeasuringTime;
        private int heart;
        private int hrvResult;
        private int id;
        private int systolic;
        private int userId;
        private List<EcgListBean> ecgList;
        private List<PpgListBean> ppgList;

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

        public int getBpStatus() {
            return bpStatus;
        }

        public void setBpStatus(int bpStatus) {
            this.bpStatus = bpStatus;
        }

        public int getCardiacFunction() {
            return cardiacFunction;
        }

        public void setCardiacFunction(int cardiacFunction) {
            this.cardiacFunction = cardiacFunction;
        }

        public String getDeviceMac() {
            return deviceMac;
        }

        public void setDeviceMac(String deviceMac) {
            this.deviceMac = deviceMac;
        }

        public int getDeviceSensorType() {
            return deviceSensorType;
        }

        public void setDeviceSensorType(int deviceSensorType) {
            this.deviceSensorType = deviceSensorType;
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

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public List<EcgListBean> getEcgList() {
            return ecgList;
        }

        public void setEcgList(List<EcgListBean> ecgList) {
            this.ecgList = ecgList;
        }

        public List<PpgListBean> getPpgList() {
            return ppgList;
        }

        public void setPpgList(List<PpgListBean> ppgList) {
            this.ppgList = ppgList;
        }

        public static class EcgListBean {
            /**
             * ecg :
             * groupNum : 1
             * healthId : 161
             * id : 144
             */

            private String ecg;
            private int groupNum;
            private int healthId;
            private int id;

            public String getEcg() {
                return ecg;
            }

            public void setEcg(String ecg) {
                this.ecg = ecg;
            }

            public int getGroupNum() {
                return groupNum;
            }

            public void setGroupNum(int groupNum) {
                this.groupNum = groupNum;
            }

            public int getHealthId() {
                return healthId;
            }

            public void setHealthId(int healthId) {
                this.healthId = healthId;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }
        }

        public static class PpgListBean {
            /**
             * groupNum : 1
             * healthId : 161
             * id : 144
             * ppg :
             */

            private int groupNum;
            private int healthId;
            private int id;
            private String ppg;

            public int getGroupNum() {
                return groupNum;
            }

            public void setGroupNum(int groupNum) {
                this.groupNum = groupNum;
            }

            public int getHealthId() {
                return healthId;
            }

            public void setHealthId(int healthId) {
                this.healthId = healthId;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getPpg() {
                return ppg;
            }

            public void setPpg(String ppg) {
                this.ppg = ppg;
            }
        }
    }
}
