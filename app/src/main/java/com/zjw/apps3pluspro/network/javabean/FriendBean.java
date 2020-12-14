package com.zjw.apps3pluspro.network.javabean;

/**
 * Created by zjw on 2018/3/31.
 */

public class FriendBean {

    /**
     * result : 1
     * msg : 请求成功
     * data : {"requstFriendState":"0","userHeadImage":"http://www.xxx.cn:8089/img/heads/2018-03-29/45830_164300141.png","userId":"45830","userName":"手机号22"}
     */

    private int result;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * requstFriendState : 0
         * userHeadImage : http://www.xxx.cn:8089/img/heads/2018-03-29/45830_164300141.png
         * userId : 45830
         * userName : 手机号22
         */

        private String requstFriendState;
        private String headImageUrl;
        private String userId;
        private String userName;

        public String getRequstFriendState() {
            return requstFriendState;
        }

        public void setRequstFriendState(String requstFriendState) {
            this.requstFriendState = requstFriendState;
        }

        public String getheadImageUrl() {
            return headImageUrl;
        }

        public void setheadImageUrl(String headImageUrl) {
            this.headImageUrl = headImageUrl;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
