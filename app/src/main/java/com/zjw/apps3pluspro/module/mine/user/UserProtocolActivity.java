package com.zjw.apps3pluspro.module.mine.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.MyActivityManager;


/**
 * 隐私协议
 */
public class UserProtocolActivity extends Activity implements View.OnClickListener {
    private final String TAG = UserProtocolActivity.class.getSimpleName();
    private Context mContext;

    private MyActivityManager manager;

    WebView wv_privacy_protocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setStatusBarMode(this, false, R.color.base_activity_bg);
        setContentView(R.layout.activity_user_protocol);
        mContext = UserProtocolActivity.this;
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initView() {
        ((TextView) findViewById(R.id.public_head_title)).setText(getText(R.string.user_protocol_title));
        findViewById(R.id.public_head_back).setOnClickListener(this);

        wv_privacy_protocol = (WebView) findViewById(R.id.wv_privacy_protocol);

        wv_privacy_protocol.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

//                 MyLog.i(TAG, "处理数据 = url = " + url);

//                view.loadUrl(url);
//                return super.shouldOverrideUrlLoading(view, url);

                if (url == null) return false;

                try {
                    if (url.startsWith("tel:")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return true;//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
                }

                //处理http和https开头的url
                wv_privacy_protocol.loadUrl(url);


                return true;

            }
        });

    }


    private void initData() {

        ////        //获取webSettings
        WebSettings settings = wv_privacy_protocol.getSettings();
//        //让webView支持JS
        settings.setJavaScriptEnabled(true);
//        //加载百度网页
        settings.setTextZoom(100);

//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

//        wv.loadUrl("http://www.qq.com//");
//        wv_privacy_protocol.loadUrl("http://www.baidu.com/");
//        wv_privacy_protocol.loadUrl("http://www.wearheart.cn/yinsi_FFit_en.html");

//        if (AppUtils.isZh(mContext)) {
//            wv_privacy_protocol.loadUrl("http://www.wearheart.cn/user_3+PRO_zh.html");
//        } else {
            wv_privacy_protocol.loadUrl("http://www.wearheart.cn/user_3+PRO_en.html");
//        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.public_head_back:
                finish();
                break;
        }
    }
}


