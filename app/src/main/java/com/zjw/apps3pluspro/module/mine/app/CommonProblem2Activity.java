package com.zjw.apps3pluspro.module.mine.app;

import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.eventbus.GetModuleEvent;
import com.zjw.apps3pluspro.eventbus.tools.EventTools;
import com.zjw.apps3pluspro.network.javabean.ModuleBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

public class CommonProblem2Activity extends BaseActivity {

    private final String TAG = CommonProblem2Activity.class.getSimpleName();


    @BindView(R.id.layoutParent)
    LinearLayout layoutParent;

    @Override
    protected int setLayoutId() {
        return R.layout.common_problem2_activity;
    }

    String languageCode;

    @Override
    protected void initViews() {
        super.initViews();
        EventTools.SafeRegisterEventBus(this);
        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.common_problem));
        mLayountInflater = LayoutInflater.from(this);
        languageCode = getIntent().getStringExtra("languageCode");
        UserGuidanceManager.getInstance().getModuleList(this, languageCode);
    }

    @Override
    protected void onDestroy() {
        EventTools.SafeUnregisterEventBus(this);
        super.onDestroy();
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }

    private LayoutInflater mLayountInflater;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getModuleEvent(GetModuleEvent event) {
        if (UserGuidanceManager.getInstance().moduleBeanList != null) {
            layoutParent.removeAllViews();
            for (int i = 0; i < UserGuidanceManager.getInstance().moduleBeanList.size(); i++) {
                LinearLayout mLinearLayout = (LinearLayout) mLayountInflater.inflate(R.layout.commonproblem_item2_layout, null);
                findViewById(mLinearLayout, i, UserGuidanceManager.getInstance().moduleBeanList.get(i));
                layoutParent.addView(mLinearLayout);
            }
        }
    }

    private void findViewById(LinearLayout mLinearLayout, int pos, ModuleBean moduleBean) {
        pos = pos + 1;
        TextView tvItemName = mLinearLayout.findViewById(R.id.tvItemName);
        tvItemName.setText(pos + "." + moduleBean.moduleName);
        mLinearLayout.findViewById(R.id.layoutName).setOnClickListener(v -> {
            Intent intent = new Intent(CommonProblem2Activity.this, CommonProblem3Activity.class);
            intent.putExtra("moduleBeanId", moduleBean.id);
            intent.putExtra("languageCode", languageCode);
            intent.putExtra("titleName", moduleBean.moduleName);
            startActivity(intent);
        });
    }
}
