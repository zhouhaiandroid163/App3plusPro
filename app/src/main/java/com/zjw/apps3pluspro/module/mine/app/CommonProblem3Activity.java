package com.zjw.apps3pluspro.module.mine.app;

import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.eventbus.GetHtmlUrlEvent;
import com.zjw.apps3pluspro.eventbus.tools.EventTools;
import com.zjw.apps3pluspro.network.javabean.HtmlUrlBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

public class CommonProblem3Activity extends BaseActivity {
    @BindView(R.id.layoutParent)
    LinearLayout layoutParent;
    private String languageCode;
    private int moduleBeanId;

    @Override
    protected int setLayoutId() {
        return R.layout.common_problem3_activity;
    }

    @Override
    protected void onDestroy() {
        EventTools.SafeUnregisterEventBus(this);
        super.onDestroy();
    }

    @Override
    protected void initViews() {
        super.initViews();
        EventTools.SafeRegisterEventBus(this);
        mLayountInflater = LayoutInflater.from(this);
        languageCode = getIntent().getStringExtra("languageCode");
        moduleBeanId = getIntent().getIntExtra("moduleBeanId", 0);
        String titleName = getIntent().getStringExtra("titleName");
        ((TextView) findViewById(R.id.public_head_title)).setText(titleName);
        UserGuidanceManager.getInstance().getHtmlUrl(this, languageCode, moduleBeanId);
    }

    private LayoutInflater mLayountInflater;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getHtmlUrlEvent(GetHtmlUrlEvent event) {
        if (UserGuidanceManager.getInstance().htmlUrlBeanList != null) {
            layoutParent.removeAllViews();
            for (int i = 0; i < UserGuidanceManager.getInstance().htmlUrlBeanList.size(); i++) {
                LinearLayout mLinearLayout = (LinearLayout) mLayountInflater.inflate(R.layout.commonproblem_item3_layout, null);
                findViewById(mLinearLayout, i, UserGuidanceManager.getInstance().htmlUrlBeanList.get(i));
                layoutParent.addView(mLinearLayout);
            }
        }
    }

    private void findViewById(LinearLayout mLinearLayout, int pos, HtmlUrlBean htmlUrlBean) {
        pos = pos + 1;
        TextView tvItemName = mLinearLayout.findViewById(R.id.tvItemName);
        tvItemName.setText(pos + "." + htmlUrlBean.titleName);
        mLinearLayout.findViewById(R.id.layoutName).setOnClickListener(v -> {
            Intent intent = new Intent(CommonProblem3Activity.this, HtmlContentActivity.class);
            intent.putExtra("notesURL", htmlUrlBean.notesURL);
            intent.putExtra("titleName", htmlUrlBean.titleName);
            startActivity(intent);
        });
    }
}
