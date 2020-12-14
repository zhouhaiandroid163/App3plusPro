package com.zjw.apps3pluspro.network.okhttp;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.module.mine.user.LoginActivity;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.utils.network.AESUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyOkHttpClient {
    private static final String TAG = MyOkHttpClient.class.getSimpleName();
    private static final int TIME_OUT = 15;// 超时时间
    private volatile static MyOkHttpClient mClient = null;
    private static OkHttpClient mOkHttpClient;



    public static MyOkHttpClient getInstance() {
        if (null == mClient) {
            synchronized (MyOkHttpClient.class) {
                if (null == mClient) {
                    mClient = new MyOkHttpClient();
                }
            }
        }
        return mClient;
    }

    public void initMyOkHttpClient() {
        mClient = null;
    }

    private MyOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cookieJar(new SimpleCookieJar())
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .followRedirects(true)
                .hostnameVerifier(new ZhHostnameVerifier())
                .addInterceptor(new Interceptor() {

                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("content-type", "application/json")
                                .addHeader("cache-control", "no-cache")
                                .addHeader("User-Agent", MyOkHttpUitls.getUserAgent())
                                .addHeader("TimeStamp", String.valueOf(System.currentTimeMillis()))
                                .addHeader("Authorization", BaseApplication.getUserSetTools().getUserAuthorizationCode())
                                .build();

//                        MyLog.i(TAG, "MyOkHttpClient request = " + request.headers().toString());

                        return chain.proceed(request);
                    }
                });
        builder.sslSocketFactory(HTTPSTrustManager.getSslSocketFactory());// client信任所有证书
        if (Constants.NO_PROXY) {
            builder.proxy(Proxy.NO_PROXY); // 设置反代理
        }
        mOkHttpClient = builder.build();
    }

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static CommonJsonCallback jsonCallback;

    public void asynCall(JSONObject postObject, VolleyInterface vif, String url) {
        RequestBody body = RequestBody.create(JSON, postObject.toString());

        Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .url(url).post(body).build();

        Call call = mOkHttpClient.newCall(request);
        jsonCallback = new CommonJsonCallback(vif);
        call.enqueue(jsonCallback);
    }

    public static class CommonJsonCallback implements Callback {

        protected final String RESULT_CODE = "code";
        protected final String ERROR_MSG = "ERROR_MSG";
        protected final String EMPTY_MSG = "EMPTY_MSG";
        protected final String COOKIE_STORE = "Set-Cookie";

        /**
         * the java layer exception, do not same to the logic error
         */
        protected final int NETWORK_ERROR = -1;// the network relative error
        protected final int JSON_ERROR = -2;// the JSON relative error
        protected final int OTHER_ERROR = -3;// the unknow error

        /**
         * 将其它线程的数据转发到UI线程
         */
        private Handler mDeliveryHandler;
        private com.android.volley.Response.Listener<JSONObject> mListener;
        private final com.android.volley.Response.ErrorListener mErrorListener;
        private Class<?> mClass;
        private boolean retry = true;

        public CommonJsonCallback(VolleyInterface handle) {
            this.mListener = handle.mListener;
            this.mErrorListener = handle.mErrorListener;
            this.mDeliveryHandler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void onFailure(final Call call, final IOException ioexception) {
            final String ioException = ioexception.toString();
            mDeliveryHandler.post(new Runnable() {// 此时还在非UI线程，因此要转发
                @Override
                public void run() {
                    mErrorListener.onErrorResponse(new VolleyError(ioException));
                }
            });
        }

        @Override
        public void onResponse(final Call call, final Response response) throws IOException {
            final String result = response.body().string();
            MyLog.e(TAG, "onResponse =" + result);
            try {
                MyLog.i(TAG, "result=" + new JSONObject(AESUtils.decrypt((new JSONObject(result)).optString("result"), "wo.szzhkjyxgs.20")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            final ArrayList<String> cookieLists = handleCookie(response.headers());
            mDeliveryHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mListener.onResponse(new JSONObject(result));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private ArrayList<String> handleCookie(Headers headers) {
            ArrayList<String> tempList = new ArrayList<String>();
            for (int i = 0; i < headers.size(); i++) {
                if (headers.name(i).equalsIgnoreCase(COOKIE_STORE)) {
                    tempList.add(headers.value(i));
                }
            }
            return tempList;
        }
    }


    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    public void quitApp(Context context) {
        mUserSetTools.set_user_login(false);
        mUserSetTools.set_user_id("");
        mUserSetTools.set_user_nickname("");//用户名
        mUserSetTools.set_user_password("");//密码
        mUserSetTools.set_user_sex(DefaultVale.USER_SEX);//性别
        mUserSetTools.set_user_birthday("");//生日
        mUserSetTools.set_user_register_time("");//注册时间
        mUserSetTools.set_user_head_url("");//头像url
        mUserSetTools.set_user_phone("");//手机号
        mUserSetTools.set_user_email("");//邮箱
        mUserSetTools.set_user_height(0);//身高
        mUserSetTools.set_user_weight(0);//体重
        mUserSetTools.set_user_head_bast64("");
        mUserSetTools.set_is_par(0);//校准标志位-设置成未校准
        mUserSetTools.set_user_exercise_target(String.valueOf(DefaultVale.USER_SPORT_TARGET));//运动目标
        mUserSetTools.set_user_sleep_target(String.valueOf(DefaultVale.USER_SLEEP_TARGET));//睡眠目标
        mUserSetTools.set_blood_grade(DefaultVale.USER_BP_LEVEL);//血压等级
        mUserSetTools.set_calibration_heart(DefaultVale.USER_HEART);//校准心率
        mUserSetTools.set_calibration_systolic(DefaultVale.USER_SYSTOLIC);//校准高压-收缩压
        mUserSetTools.set_calibration_diastolic(DefaultVale.USER_DIASTOLIC);//校准低压-舒张压
        mUserSetTools.set_user_wear_way(DefaultVale.USER_WEARWAY);//穿戴方式
        //清空生理周期信息
//        mUserSetTools.set_device_is_one_cycle(true);
//        mUserSetTools.set_nv_start_date("");
//        mUserSetTools.set_nv_cycle(28);
//        mUserSetTools.set_nv_lenght(5);
//        mUserSetTools.set_nv_device_switch(false);

        context.startActivity(new Intent(context, LoginActivity.class));
        MyActivityManager.getInstance().finishAllActivity();
        MyOkHttpClient.getInstance().initMyOkHttpClient();
    }

    /**
     * 当前手机是否有可用网络 (所有网络类型)获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
     *
     * @param context
     * @return
     */
    public boolean isConnect(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        if (info.isAvailable()) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.v("error", e.toString());
            return false;
        }
        return false;
    }
}