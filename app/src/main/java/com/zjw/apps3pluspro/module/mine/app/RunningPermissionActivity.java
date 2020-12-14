package com.zjw.apps3pluspro.module.mine.app;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.base.BaseActivity;

public class RunningPermissionActivity extends BaseActivity {

    @Override
    protected int setLayoutId() {
        return R.layout.running_permission_activity;
    }

    @Override
    protected void initViews() {
        super.initViews();
        setTvTitle(R.string.running_title);
    }
}
