package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.network.ResultJson;

/**
 * Created by zjw on 2018/4/2.
 */

public class OldBean {


    /**
     * result : 1
     * msg : 请求成功
     */

    private int result;
    private String msg;

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
}
