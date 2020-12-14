package com.zjw.apps3pluspro.module.mine.app;

import android.annotation.SuppressLint;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.base.BaseActivity;

import butterknife.BindView;

public class HtmlContentActivity extends BaseActivity {
    @BindView(R.id.wvContent)
    WebView wvContent;
    @Override
    protected int setLayoutId() {
        return R.layout.html_content_activity;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initViews() {
        super.initViews();
        String url = getIntent().getStringExtra("notesURL");
        WebSettings settings = wvContent.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setTextZoom(100);
        wvContent.loadUrl(url);

        String titleName = getIntent().getStringExtra("titleName");
        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.common_problem));
    }
}
