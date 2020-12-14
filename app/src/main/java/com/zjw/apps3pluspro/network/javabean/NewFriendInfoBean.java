package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.network.ResultJson;

import java.util.List;

/**
 * Created by zjw on 2018/4/2.
 */

public class NewFriendInfoBean {
    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : [{"id":1,"reqMsg":"\u201c手机号\u201d想请求加你为好友！","reqTime":"2019-05-27 10:43:46","reqUserId":3}]
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
         * id : 1
         * reqMsg : “手机号”想请求加你为好友！
         * reqTime : 2019-05-27 10:43:46
         * reqUserId : 3
         */

        private int id;
        private String reqMsg;
        private String reqTime;
        private int reqUserId;
        private String nickName;
        private String iconUrl;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getReqMsg() {
            return reqMsg;
        }

        public void setReqMsg(String reqMsg) {
            this.reqMsg = reqMsg;
        }

        public String getReqTime() {
            return reqTime;
        }

        public void setReqTime(String reqTime) {
            this.reqTime = reqTime;
        }

        public int getReqUserId() {
            return reqUserId;
        }

        public void setReqUserId(int reqUserId) {
            this.reqUserId = reqUserId;
        }


        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "id=" + id +
                    ", reqMsg='" + reqMsg + '\'' +
                    ", reqTime='" + reqTime + '\'' +
                    ", reqUserId=" + reqUserId +
                    ", nickName='" + nickName + '\'' +
                    ", iconUrl='" + iconUrl + '\'' +
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
     * 查询新好友
     *
     * @return
     */
    public int isNewFriendSuccess() {
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


}
