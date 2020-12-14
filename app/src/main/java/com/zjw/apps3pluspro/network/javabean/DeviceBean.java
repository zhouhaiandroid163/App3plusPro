package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.utils.JavaUtil;

/**
 * Created by zjw on 2018/4/26.
 */

public class DeviceBean {


    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : {"dataStatus":0,"deviceType":"1000","firmwarePlatform":1,"mustUpdate":0,"remark":"这是第一个主题","resType":2,"versionAfter":"11","versionBefore":"10","versionUrl":"http://file.genius.liuqingzhi.com/upload/20191114/cf096436893848aead19d90beff3685d.bin","whiteList":"780003,779988"}
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
         * dataStatus : 0
         * deviceType : 1000
         * firmwarePlatform : 1
         * mustUpdate : 0
         * remark : 这是第一个主题
         * resType : 2
         * versionAfter : 11
         * versionBefore : 10
         * versionUrl : http://file.genius.liuqingzhi.com/upload/20191114/cf096436893848aead19d90beff3685d.bin
         * whiteList : 780003,779988
         */

        private int dataStatus;
        private String deviceType;
        private int firmwarePlatform;
        private int mustUpdate;
        private String remark;
        private int resType;
        private String versionAfter;
        private String versionBefore;
        private String versionUrl;
        private String whiteList;

        public int getDataStatus() {
            return dataStatus;
        }

        public void setDataStatus(int dataStatus) {
            this.dataStatus = dataStatus;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public int getFirmwarePlatform() {
            return firmwarePlatform;
        }

        public void setFirmwarePlatform(int firmwarePlatform) {
            this.firmwarePlatform = firmwarePlatform;
        }

        public int getMustUpdate() {
            return mustUpdate;
        }

        public void setMustUpdate(int mustUpdate) {
            this.mustUpdate = mustUpdate;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getResType() {
            return resType;
        }

        public void setResType(int resType) {
            this.resType = resType;
        }

        public String getVersionAfter() {
            return versionAfter;
        }

        public void setVersionAfter(String versionAfter) {
            this.versionAfter = versionAfter;
        }

        public String getVersionBefore() {
            return versionBefore;
        }

        public void setVersionBefore(String versionBefore) {
            this.versionBefore = versionBefore;
        }

        public String getVersionUrl() {
            return versionUrl;
        }

        public void setVersionUrl(String versionUrl) {
            this.versionUrl = versionUrl;
        }

        public String getWhiteList() {
            return whiteList;
        }

        public void setWhiteList(String whiteList) {
            this.whiteList = whiteList;
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
     * 是否成功
     *
     * @return
     */
    public int isOk() {
        //获取成功
        if (ResultJson.Code_operation_success.equals(getCode())) {
            return 1;
        } else if (ResultJson.Code_no_data.equals(getCode())) {
            return 2;
        } else {
            return -1;
        }
    }


    /**
     * 是否可以升级
     *
     * @return
     */
    public boolean isUpdate(BleDeviceTools mBleDeviceTools) {

        if (getData().getVersionAfter() != null && !getData().getVersionAfter().equals("")
                && getData().getVersionUrl() != null && !getData().getVersionUrl().equals("")
        ) {

            String version = getData().getVersionAfter();
//            version = "27";

            if (JavaUtil.isNumeric(version)) {
                if (mBleDeviceTools.get_ble_device_type() >= 1 &&
                        mBleDeviceTools.get_ble_device_version() >= 1
                        && Integer.valueOf(version) >= 1
                        && Integer.valueOf(version) > mBleDeviceTools.get_ble_device_version()) {

                    return true;


                } else {

                    return false;
                }
            } else {
                return false;
            }


        } else {
            return false;
        }

    }

}
