package com.zjw.apps3pluspro.network.javabean;

import android.content.Context;

import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.utils.MyUtils;

/**
 * Created by zjw on 2018/3/22.
 */

public class AppVersionBean {


    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : {"appDownloadUrl":"http://file.genius.liuqingzhi.com/upload/20191106/5f4d91dedcde41058ddfa64571c572eb.apk","appName":"F Fit","appVersion":"1.0.7","appVersionCode":7,"id":100,"mustUpdate":1,"remark":"测试0001"}
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
         * appDownloadUrl : http://file.genius.liuqingzhi.com/upload/20191106/5f4d91dedcde41058ddfa64571c572eb.apk
         * appName : F Fit
         * appVersion : 1.0.7
         * appVersionCode : 7
         * id : 100
         * mustUpdate : 1
         * remark : 测试0001
         */

        private String appDownloadUrl;
        private String appName;
        private String appVersion;
        private int appVersionCode;
        private int id;
        private int mustUpdate;
        private String remark;

        public String getAppDownloadUrl() {
            return appDownloadUrl;
        }

        public void setAppDownloadUrl(String appDownloadUrl) {
            this.appDownloadUrl = appDownloadUrl;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }

        public int getAppVersionCode() {
            return appVersionCode;
        }

        public void setAppVersionCode(int appVersionCode) {
            this.appVersionCode = appVersionCode;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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
     * 版本号
     *
     * @return
     */
    public int isgetAPPVersionSuccess() {
        //获取成功
        if (ResultJson.Code_operation_success.equals(getCode())) {
            return 1;
        }
        //获取失败
        else if (ResultJson.Code_operation_fail.equals(getCode())) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * 会否需要升级
     *
     * @return
     */
    public boolean isAppUpdate(Context context) {
        if (getData().getAppVersionCode() > 0 && getData().getAppDownloadUrl() != null && !getData().getAppDownloadUrl().equals("")) {
            int version_code = MyUtils.getVersionCode();
            int service_code = getData().getAppVersionCode();
            if (service_code > version_code) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

}
