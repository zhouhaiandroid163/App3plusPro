package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.network.ResultJson;

public class ThemeFileBean {


    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : {"binFileName":"hor.bin","id":1712,"md5Value":"379f8d1d53c2348595d5fba19a9dd2d8","themeFileUrl":"http://file.genius.liuqingzhi.com/upload/20191116/fa546098e69840589186f5faee4d76b1.bin","themeId":45}
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
         * binFileName : hor.bin
         * id : 1712
         * md5Value : 379f8d1d53c2348595d5fba19a9dd2d8
         * themeFileUrl : http://file.genius.liuqingzhi.com/upload/20191116/fa546098e69840589186f5faee4d76b1.bin
         * themeId : 45
         */

        private String binFileName;
        private int id;
        private String md5Value;
        private String themeFileUrl;
        private int themeId;

        public String getBinFileName() {
            return binFileName;
        }

        public void setBinFileName(String binFileName) {
            this.binFileName = binFileName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMd5Value() {
            return md5Value;
        }

        public void setMd5Value(String md5Value) {
            this.md5Value = md5Value;
        }

        public String getThemeFileUrl() {
            return themeFileUrl;
        }

        public void setThemeFileUrl(String themeFileUrl) {
            this.themeFileUrl = themeFileUrl;
        }

        public int getThemeId() {
            return themeId;
        }

        public void setThemeId(int themeId) {
            this.themeId = themeId;
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
     * 个人信息获取是否成功
     *
     * @return
     */
    public int isUserSuccess() {
        //获取成功
        if (ResultJson.Code_operation_success.equals(getCode())) {
            return 1;
        }
        //没有数据
        else if (ResultJson.Code_no_data.equals(getCode())) {
            return 0;

        } else {
            return -1;
        }
    }


}





