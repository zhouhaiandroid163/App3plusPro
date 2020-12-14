package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.network.ResultJson;

/**
 * Created by zjw on 2018/4/2.
 */

public class SearchFriendInfoBean {
    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : {"friendUserId":707490,"nickName":"邮箱","reqStatus":1}
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
         * friendUserId : 707490
         * nickName : 邮箱
         * reqStatus : 1
         */

        private int friendUserId;
        private String nickName;
        private int reqStatus;
        private String iconUrl;

        public int getFriendUserId() {
            return friendUserId;
        }

        public void setFriendUserId(int friendUserId) {
            this.friendUserId = friendUserId;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getReqStatus() {
            return reqStatus;
        }

        public void setReqStatus(int reqStatus) {
            this.reqStatus = reqStatus;
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
                    "friendUserId=" + friendUserId +
                    ", nickName='" + nickName + '\'' +
                    ", reqStatus=" + reqStatus +
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
     * 搜索好友
     *
     * @return
     */
    public int isSearchFirendSuccess() {
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
