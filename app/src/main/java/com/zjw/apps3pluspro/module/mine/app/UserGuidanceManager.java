package com.zjw.apps3pluspro.module.mine.app;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.eventbus.GetHtmlUrlEvent;
import com.zjw.apps3pluspro.eventbus.GetModuleEvent;
import com.zjw.apps3pluspro.eventbus.GetSupportLanguageEvent;
import com.zjw.apps3pluspro.network.JsonUtils;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.HtmlUrlBean;
import com.zjw.apps3pluspro.network.javabean.LanguageBean;
import com.zjw.apps3pluspro.network.javabean.ModuleBean;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UserGuidanceManager {
    private static final String TAG = UserGuidanceManager.class.getSimpleName();
    private static UserGuidanceManager userGuidanceManager;

    public static UserGuidanceManager getInstance() {
        if (userGuidanceManager == null) {
            userGuidanceManager = new UserGuidanceManager();
        }
        return userGuidanceManager;
    }

    private UserGuidanceManager() {
    }

    List<LanguageBean> languageBeanList;
    List<ModuleBean> moduleBeanList;
    List<HtmlUrlBean> htmlUrlBeanList;

    public void getSupportLanguage(Context mContext) {
        RequestInfo mRequestInfo = RequestJson.getSupportLanguage();
        Log.i(TAG, "getSupportLanguage=" + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
            @Override
            public void onMySuccess(JSONObject result) {
                try {
                    Log.i(TAG, "getSupportLanguage=" + result);
                    String resultString = result.optString("code");
                    if (resultString.equalsIgnoreCase(ResultJson.Code_operation_success)) {
                        JSONObject jsonobject = result.optJSONObject("data");
                        JSONArray jsonArray = jsonobject.getJSONArray("list");
                        languageBeanList = JsonUtils.jsonToBeanList(jsonArray.toString(), LanguageBean.class);
                        EventBus.getDefault().post(new GetSupportLanguageEvent());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyError(VolleyError arg0) {

            }
        });
    }


    public void getModuleList(Context mContext, String languageCode) {
        RequestInfo mRequestInfo = RequestJson.getModuleList(languageCode);
        Log.i(TAG, "getModuleList=" + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
            @Override
            public void onMySuccess(JSONObject result) {
                try {
                    Log.i(TAG, "getModuleList=" + result);
                    String resultString = result.optString("code");
                    if (resultString.equalsIgnoreCase(ResultJson.Code_operation_success)) {
                        JSONObject jsonobject = result.optJSONObject("data");
                        JSONArray jsonArray = jsonobject.getJSONArray("list");
                        moduleBeanList = JsonUtils.jsonToBeanList(jsonArray.toString(), ModuleBean.class);
                        EventBus.getDefault().post(new GetModuleEvent());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyError(VolleyError arg0) {
            }
        });
    }

    public void getHtmlUrl(Context mContext, String languageCode, int moduleId) {
        RequestInfo mRequestInfo = RequestJson.getHtmlUrl(languageCode, moduleId);
        Log.i(TAG, "getHtmlUrl=" + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
            @Override
            public void onMySuccess(JSONObject result) {
                try {
                    Log.i(TAG, "getHtmlUrl=" + result);
                    String resultString = result.optString("code");
                    if (resultString.equalsIgnoreCase(ResultJson.Code_operation_success)) {
                        JSONObject jsonobject = result.optJSONObject("data");
                        JSONArray jsonArray = jsonobject.getJSONArray("list");
                        htmlUrlBeanList = JsonUtils.jsonToBeanList(jsonArray.toString(), HtmlUrlBean.class);
                        EventBus.getDefault().post(new GetHtmlUrlEvent());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyError(VolleyError arg0) {
            }
        });
    }
}
