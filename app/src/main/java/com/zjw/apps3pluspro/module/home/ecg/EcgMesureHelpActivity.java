package com.zjw.apps3pluspro.module.home.ecg;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;


public class EcgMesureHelpActivity extends Activity implements OnClickListener {
    private String TAG = "EcgMesureHelpActivity";


    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg_mesure_help);
        initView();
        initData();
    }


    private void initView() {
        findViewById(R.id.public_head_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.public_head_title)).setText(getText(R.string.measure_help));

    }

    void initData() {

    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.public_head_back:
                finish();
                break;
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();


    }


}
