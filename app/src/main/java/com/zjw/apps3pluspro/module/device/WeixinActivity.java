package com.zjw.apps3pluspro.module.device;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.VolleyRequest;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.view.instructions.DepthPageTransformer;
import com.zjw.apps3pluspro.view.instructions.ViewPagerAdatper;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 微信运动
 */
public class WeixinActivity extends Activity implements OnClickListener {
    private final String TAG = WeixinActivity.class.getSimpleName();
    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    private Context mContext;

    private ViewPager in_viewpager;
    private LinearLayout mIn_ll;
    private List<View> mViewList;
    private ImageView mLight_dots;
    private int mDistance;

    private Button button_bangdign_wx;
    private WaitDialog waitDialog;
    private TextView weixin_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixin);
        mContext = WeixinActivity.this;
        waitDialog = new WaitDialog(this);
        initView();
        initData();
        in_viewpager.setAdapter(new ViewPagerAdatper(mViewList));
        addDots();
        moveDots();
        in_viewpager.setPageTransformer(true, new DepthPageTransformer());
        in_viewpager.setOnClickListener(this);
        updateUi();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }


    private void initView() {
        // TODO Auto-generated method stub
        findViewById(R.id.public_head_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.title_weixin));
        findViewById(R.id.weixiin_back).setOnClickListener(this);
        in_viewpager = (ViewPager) findViewById(R.id.in_viewpager);
        mIn_ll = (LinearLayout) findViewById(R.id.in_ll);
        mLight_dots = (ImageView) findViewById(R.id.iv_light_dots);
        button_bangdign_wx = (Button) findViewById(R.id.button_bangdign_wx);
        button_bangdign_wx.setOnClickListener(this);
        weixin_text = (TextView) findViewById(R.id.weixin_text);

    }

    private void initData() {
        mViewList = new ArrayList<View>();

        int[] img_zh = {R.drawable.weixin_zh0, R.drawable.weixin_zh1, R.drawable.weixin_zh2, R.drawable.weixin_zh3, R.drawable.weixin_zh4,
                R.drawable.weixin_zh5, R.drawable.weixin_zh6};

        int[] img_en = {R.drawable.weixin_en0, R.drawable.weixin_en1, R.drawable.weixin_en2, R.drawable.weixin_en3, R.drawable.weixin_en4,
                R.drawable.weixin_en5, R.drawable.weixin_zh6};

        for (int i = 0; i < img_zh.length; i++) {

            LayoutInflater lf = getLayoutInflater().from(mContext);
            View view = lf.inflate(R.layout.weixing_indicator, null);
            ImageView img = (ImageView) view.findViewById(R.id.weixin_img);


            if (AppUtils.isZh(mContext)) {

                img.setImageResource(img_zh[i]);
            } else {
                img.setImageResource(img_en[i]);
            }


            mViewList.add(view);
        }

    }


    private void moveDots() {
        mLight_dots.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //获得两个圆点之间的距离
                mDistance = mIn_ll.getChildAt(1).getLeft() - mIn_ll.getChildAt(0).getLeft();
                mLight_dots.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
            }
        });
        in_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //页面滚动时小白点移动的距离，并通过setLayoutParams(params)不断更新其位置
                float leftMargin = mDistance * (position + positionOffset);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLight_dots.getLayoutParams();
                params.leftMargin = (int) leftMargin;
                mLight_dots.setLayoutParams(params);

//                int[] textSz = {R.string.weixin_step0, R.string.weixin_step1, R.string.weixin_step2, R.string.weixin_step3, R.string.weixin_step4,
//                        R.string.weixin_step5};
//
//                weixin_text.setText(getString(textSz[position]));


            }

            @Override
            public void onPageSelected(int position) {
                //页面跳转时，设置小圆点的margin
                float leftMargin = mDistance * position;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLight_dots.getLayoutParams();
                params.leftMargin = (int) leftMargin;
                mLight_dots.setLayoutParams(params);

                int[] textSz = {R.string.weixin_step0, R.string.weixin_step1, R.string.weixin_step2, R.string.weixin_step3, R.string.weixin_step4,
                        R.string.weixin_step5, R.string.weixin_step6};

                weixin_text.setText(getString(textSz[position]));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    int ViewCount = 6;

    private void addDots() {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 40, 0);


        for (int i = 0; i <= ViewCount; i++) {
            ImageView image = new ImageView(this);
            image.setImageResource(R.drawable.my_dian_gray_dot);
            mIn_ll.addView(image, layoutParams);
        }


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.public_head_back:
                finish();
                break;

            case R.id.weixiin_back:
                finish();
                break;

            case R.id.button_bangdign_wx:
                if (button_bangdign_wx.getText().toString().equals(getString(R.string.weixin_bingding))) {
                    getToken();
                }


                break;


        }
    }



    /**
     * 注册Mac地址
     */
    private void getToken() {


        JSONObject regMacObject = null;


        waitDialog.show(getString(R.string.loading0));


        HashMap<String, String> data = new HashMap<String, String>();
        data.put("grant_type", "client_credential");
        data.put("appid", "wxf43c6f960efc72e1");
        data.put("secret", "8c61474178d5e45d3a7834174593f7b6");

        JSONObject data_json = new JSONObject(data);

        MyLog.i(TAG, "注册MAC 获取Token: data_json = " + data_json.toString());

        regMacObject = new JSONObject();

        try {
            regMacObject.put("c", "ctl000001");
            regMacObject.put("m", "getWeChatToken");
            regMacObject.put("data", data_json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        MyLog.i(TAG, "注册MAC 获取Token: regMacObject 上传 = " + regMacObject.toString());


        VolleyRequest.RequestPost(mContext, RequestJson.WEIXIN_BASE_URL, TAG,
                regMacObject, new VolleyInterface(mContext,
                        VolleyInterface.mListener,
                        VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        MyLog.i(TAG, "注册MAC 获取Token: regMacObject 下发 = " + result.toString());

                        if ("1".equals(result.optString("result"))) {
                            MyLog.i(TAG, "注册MAC 获取Token:" + "true");

                            GetWXId(result.optString("data"));

                        } else {
                            BangDingWxState(false);
                            MyLog.i(TAG, "注册MAC 获取Token:" + "false");
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        BangDingWxState(false);
                        MyLog.i(TAG, "注册MAC 获取Token:" + "error");
                    }
                });
    }

    private void GetWXId(final String token) {

        MyLog.i(TAG, "注册MAC 获取微信ID: token = " + token);

        if (token.equals("")) {
            BangDingWxState(false);
            return;
        }


        String url = "https://api.weixin.qq.com/device/getqrcode?" + "access_token=" + token + "&" + "product_id=7956";
        MyLog.i(TAG, "注册MAC 获取微信ID url = " + url);


        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String str) {

                        MyLog.i(TAG, "注册MAC 获取微信ID: 下发 = " + str);
                        analysisGetWXIdData(token, str);
                    }
                }
                ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        BangDingWxState(false);
                        MyLog.i(TAG, "注册MAC 获取微信ID:" + "error = ");
                    }
                });
        //设置取消取消http请求标签 Activity的生命周期中的onStiop()中调用
        request.setTag(TAG);
        BaseApplication.getHttpQueue().add(request);
//        BaseApplication.getHttpQueue().start();


    }

    private void analysisGetWXIdData(String token, String data) {

        try {
            JSONObject WxInDataObject = new JSONObject(data);
            if (WxInDataObject != null) {

                if (WxInDataObject.getJSONObject("base_resp").getString("errcode").equals("0")) {
                    if (WxInDataObject.has("deviceid")) {

                        MyLog.i(TAG, "注册MAC 获取微信ID: 解析 deviceid = " + WxInDataObject.getString("deviceid"));

                        if (WxInDataObject.getString("deviceid") != null && !WxInDataObject.getString("deviceid").equals("")) {
                            registerMac(token, WxInDataObject.getString("deviceid"));
                        } else {
                            BangDingWxState(false);
                        }

                    } else {
                        BangDingWxState(false);
                    }
                } else {
                    BangDingWxState(false);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void registerMac(String token, String device_id) {

        if (mBleDeviceTools.get_ble_mac() == null || mBleDeviceTools.get_ble_mac().equals("")) {
            return;
        }

        String mac = mBleDeviceTools.get_ble_mac();

        mac = mac.replace(":", "");

        HashMap<String, String> data = new HashMap<String, String>();
        data.put("id", device_id);
        data.put("mac", mac);
        data.put("connect_protocol", "3");
        data.put("auth_key", "");
        data.put("close_strategy", "1");
        data.put("conn_strategy", "1");
        data.put("crypt_method", "0");
        data.put("auth_ver", "0");
        data.put("manu_mac_pos", "-1");
        data.put("ser_mac_pos", "-2");
        data.put("ble_simple_protocol", "1");

        JSONObject data_json = new JSONObject(data);

        MyLog.i(TAG, "注册MAC 注册设备: data_json = " + data_json.toString());


        JSONArray mJSONArray = new JSONArray();
        mJSONArray.put(data_json);

        JSONObject RegisterMacObjcet = new JSONObject();

        try {
            RegisterMacObjcet.put("device_num", "1");
            RegisterMacObjcet.put("op_type", "1");
            RegisterMacObjcet.put("device_list", mJSONArray);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        MyLog.i(TAG, "注册MAC 注册设备: 上传 RegisterMacObjcet = " + RegisterMacObjcet.toString());


        String url = "https://api.weixin.qq.com/device/authorize_device?" + "access_token=" + token;


        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, url, RegisterMacObjcet,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        MyLog.i(TAG, "注册MAC 注册设备: 下发 = " + response.toString());

                        analysisRegisterMacData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                BangDingWxState(false);

                MyLog.i(TAG, "注册MAC 注册设备:" + "error = ");
            }
        });

        jsonRequest.setTag(TAG);
        BaseApplication.getHttpQueue().add(jsonRequest);
//        BaseApplication.getHttpQueue().start();


    }

    private void analysisRegisterMacData(JSONObject json) {

        try {
            if (json != null) {

                if (json.has("resp")) {

                    MyLog.i(TAG, "注册MAC 注册设备: 解析 json = " + json.getString("resp").toString());

                    String health_data = json.getJSONArray("resp").toString();

                    JSONArray arr = new JSONArray(health_data);

                    if (arr.length() >= 1) {

                        JSONObject temp = (JSONObject) arr.get(0);

                        if (temp != null && temp.has("errmsg")) {
                            MyLog.i(TAG, "注册MAC 注册设备: 解析 temp = " + temp.toString());
                            MyLog.i(TAG, "注册MAC 注册设备: 解析 errmsg = " + temp.getString("errmsg"));
                            if (temp.getString("errcode").equals("0")) {
                                BangDingWxState(true);
                            } else {
                                BangDingWxState(false);
                            }

                        } else {
                            BangDingWxState(false);
                        }


                    } else {
                        BangDingWxState(false);
                    }


                } else {
                    BangDingWxState(false);

                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    void BangDingWxState(boolean is_success) {

        waitDialog.close();
        if (is_success) {
            mBleDeviceTools.set_wx_mac_address(mBleDeviceTools.get_ble_mac());
            AppUtils.showToast(mContext, R.string.weixin_bingding_seccuss);
            updateUi();
        } else {
            AppUtils.showToast(mContext, R.string.weixin_bingding_fail);
        }

    }

    void updateUi() {

        if (mBleDeviceTools.get_ble_mac() != null && !mBleDeviceTools.equals("") && mBleDeviceTools.get_wx_mac_address() != null && !mBleDeviceTools.equals("")) {

            if (mBleDeviceTools.get_ble_mac().equals(mBleDeviceTools.get_wx_mac_address())) {
                button_bangdign_wx.setText(getString(R.string.weixin_bingding_ok));
                button_bangdign_wx.setEnabled(false);
                button_bangdign_wx.setAlpha(0.5f);
            } else {
                button_bangdign_wx.setText(getString(R.string.weixin_bingding));
                button_bangdign_wx.setEnabled(true);
                button_bangdign_wx.setAlpha(1);
            }

        } else {
            button_bangdign_wx.setText(getString(R.string.weixin_bingding));
            button_bangdign_wx.setEnabled(true);
            button_bangdign_wx.setAlpha(1);

        }

    }

    @Override
    protected void onDestroy() {


        super.onDestroy();
    }
}
