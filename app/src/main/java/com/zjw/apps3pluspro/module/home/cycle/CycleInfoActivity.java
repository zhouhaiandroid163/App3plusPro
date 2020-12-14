package com.zjw.apps3pluspro.module.home.cycle;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.base.BaseActivity;


/**
 * 设置生理周期介绍
 */
public class CycleInfoActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "CycleInfoActivity";
    private Context mContext;


    @Override
    protected int setLayoutId() {
        isTextDark = false;
        bgColor = R.color.title_bg_cycle;
        return R.layout.activity_cycle_info;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = CycleInfoActivity.this;
        initView();
    }
    
    private void initView() {
        // TODO Auto-generated method stub
        ((TextView) (findViewById(R.id.public_head_title))).setText(getText(R.string.cycle_info_tile));
        findViewById(R.id.layoutCalendar).setVisibility(View.GONE);
        findViewById(R.id.layoutTitle).setBackgroundColor(getResources().getColor(R.color.title_bg_cycle));
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

}
