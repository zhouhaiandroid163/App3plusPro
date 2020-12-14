package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.network.ResultJson;


public class CurrencyBean {


    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * codeMsg : 操作成功！
     */

    private int result;
    private String msg;
    private String code;
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
     * 上传信息
     *
     * @return
     */
    public int uploadSuccess() {
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
}





