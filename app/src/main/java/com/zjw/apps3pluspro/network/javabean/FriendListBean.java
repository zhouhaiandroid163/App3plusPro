package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.network.ResultJson;

import java.util.List;

/**
 * Created by zjw on 2018/4/2.
 */

public class FriendListBean {


    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : [{"friendUserId":707490,"iconUrl":"file.genius.liuqingzhi.com/upload/icon/20190525/2878e29cb5674a679c8d69c6764ba37f.png","id":1,"nickName":"邮箱"}]
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
         * friendUserId : 707490
         * iconUrl : file.genius.liuqingzhi.com/upload/icon/20190525/2878e29cb5674a679c8d69c6764ba37f.png
         * id : 1
         * nickName : 邮箱
         */

        private int friendUserId;
        private String iconUrl;
        private int id;
        private String nickName;
        private String nickNameRename;

        public int getFriendUserId() {
            return friendUserId;
        }

        public void setFriendUserId(int friendUserId) {
            this.friendUserId = friendUserId;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getNickNameRename() {
            return nickNameRename;
        }

        public void setNickNameRename(String nickNameRename) {
            this.nickNameRename = nickNameRename;
        }


        @Override
        public String toString() {
            return "DataBean{" +
                    "friendUserId=" + friendUserId +
                    ", iconUrl='" + iconUrl + '\'' +
                    ", id=" + id +
                    ", nickName='" + nickName + '\'' +
                    ", nickNameRename='" + nickNameRename + '\'' +
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
     * 查询好友列表
     *
     * @return
     */
    public int isFriendListSuccess() {
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
