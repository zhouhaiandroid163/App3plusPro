package com.zjw.apps3pluspro.network;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.utils.network.AESUtils;

import org.json.JSONObject;

public class VolleyRequest {

    private Context context;
    public static JsonObjectRequest objectRequest;
    public static String AESContent = "";
    private static JSONObject postObject;

    public static void RequestPost(Context mContext, String url, String tag, JSONObject jsonObject, VolleyInterface vif) {

        MyLog.i("aaa", "登陆 = 1111 = " + jsonObject.toString());

        try {
            AESContent = AESUtils.encrypt(jsonObject.toString(), "wo.szzhkjyxgs.20");
            MyLog.i("aaa", "登陆 = 4444 = " + AESContent);
            postObject = new JSONObject();
            postObject.put("body", AESContent.replace("\n", ""));
//			postObject.put("data", AESContent.replace("\n", ""));
            MyLog.i("aaa", "登陆 = 55555 = " + postObject.toString());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        objectRequest = new JsonObjectRequest(Method.POST, url, postObject,vif.loadingListener(), vif.errorListener());
        objectRequest.setTag(tag);
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 0, 1.0f));
        BaseApplication.getHttpQueue().add(objectRequest);

//        vif.loadingListener();
//        vif.errorListener();
//        MyOkHttpClient.getInstance().asynCall(postObject, vif, url);
    }
}
