package com.zjw.apps3pluspro.network;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.eventbus.AuthorizationStateEvent;
import com.zjw.apps3pluspro.utils.network.AESUtils;

import android.content.Context;

public abstract class VolleyInterface {

    private static final String TAG = VolleyInterface.class.getSimpleName();
    public Context mContext;
    public static Listener<JSONObject> mListener;
    public static ErrorListener mErrorListener;

    public VolleyInterface(Context context, Listener<JSONObject> listener,
                           ErrorListener errorListener) {
        this.mContext = context;
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    public Listener<JSONObject> loadingListener() {
        mListener = new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                // TODO Auto-generated method stub
                try {
                    JSONObject result = new JSONObject(AESUtils.decrypt(arg0.optString("result"), "wo.szzhkjyxgs.20"));
                    String code = result.optString("code");
                    if (code.equalsIgnoreCase(ResultJson.AUTHORIZATION_CODE_FAILURE) || code.equalsIgnoreCase(ResultJson.AUTHORIZATION_CODE_NULL)) {
                        EventBus.getDefault().post(new AuthorizationStateEvent(code));
                    } else {
                        onMySuccess(result);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        };

        return mListener;

    }


    public ErrorListener errorListener() {
        mErrorListener = new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                onMyError(arg0);
            }
        };
        return mErrorListener;

    }

    public abstract void onMySuccess(JSONObject result);

    public abstract void onMyError(VolleyError arg0);
}
