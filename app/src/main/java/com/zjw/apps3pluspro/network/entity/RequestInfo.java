package com.zjw.apps3pluspro.network.entity;

import org.json.JSONObject;


/**
 * 请求后台的模型
 * Created by zjw on 2018/3/7.
 */

public class RequestInfo {
    JSONObject RequestJson;
    String RequestUrl;

    public RequestInfo(JSONObject json, String url) {
        setRequestJson(json);
        setRequestUrl(url);
    }

    public JSONObject getRequestJson() {
        return RequestJson;
    }

    public void setRequestJson(JSONObject requestJson) {
        RequestJson = requestJson;
    }

    public String getRequestUrl() {
        return RequestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        RequestUrl = requestUrl;
    }

    @Override
    public String toString() {
        return "url=" + RequestUrl + " json='" + RequestJson;
    }
}
