package com.zjw.apps3pluspro.network;


import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.okhttp.MyOkHttpClient;
import com.zjw.apps3pluspro.utils.network.AESUtils;

import org.json.JSONObject;

public class NewVolleyRequest {


    public static void RequestPost(RequestInfo mRequestInfo, String tag, VolleyInterface vif) {
        JSONObject postObject = new JSONObject();

        try {
            String AESContent = AESUtils.encrypt(mRequestInfo.getRequestJson().toString(), "wo.szzhkjyxgs.20");
            postObject.put("data", AESContent.replace("\n", ""));


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        JsonObjectRequest objectRequest = new JsonObjectRequest(Method.POST, mRequestInfo.getRequestUrl(), postObject,
//                vif.loadingListener(), vif.errorListener());
//        objectRequest.setTag(tag);
//        objectRequest.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 0, 1.0f));
//        BaseApplication.getHttpQueue().add(objectRequest);

        vif.loadingListener();
        vif.errorListener();
        MyOkHttpClient.getInstance().asynPostCall(postObject, vif, mRequestInfo.getRequestUrl());
    }

    public static void RequestGet(RequestInfo mRequestInfo, String tag, VolleyInterface vif) {
        vif.loadingListener();
        vif.errorListener();
        MyOkHttpClient.getInstance().asynGetCall(vif, mRequestInfo.getRequestUrl());
    }
}
