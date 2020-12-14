package com.zjw.apps3pluspro.module.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.adapter.PageManageRecyclerAdapter;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.eventbus.PageDeviceSetEvent;
import com.zjw.apps3pluspro.eventbus.PageDeviceSyncEvent;
import com.zjw.apps3pluspro.eventbus.PageDeviceSyncOverEvent;
import com.zjw.apps3pluspro.eventbus.tools.EventTools;
import com.zjw.apps3pluspro.module.home.entity.PageItem;
import com.zjw.apps3pluspro.utils.PageManager;
import com.zjw.apps3pluspro.utils.PageReportListCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;

public class PageManagementActivity extends BaseActivity {
    @BindView(R.id.rvPage)
    RecyclerView rvPage;
    @BindView(R.id.layoutNoMove)
    LinearLayout layoutNoMove;
    private ItemTouchHelper itemTouchHelper;

    public static final String PAGE_TYPE = "PAGE_TYpe";
    public static final int PAGE_APP = 0;
    public static final int PAGE_DEVICE = 1;

    public static int type = 0;

    @Override
    protected int setLayoutId() {
        return R.layout.page_management_activity;
    }

    @Override
    protected void initViews() {
        super.initViews();
        setTvTitle(R.string.data_card_sorting);
        setFinishListener(() -> {
        });

        EventTools.SafeRegisterEventBus(this);
        Context mContext = this;
        type = getIntent().getIntExtra(PAGE_TYPE, PAGE_APP);
        //设置RecyclerView的布局
        rvPage.setLayoutManager(new LinearLayoutManager(mContext));
        if (type == PAGE_APP) {
            ArrayList<PageItem> pageItemArrayList = PageManager.getInstance().getPageAppList();
            initPage(pageItemArrayList);
        } else if (type == PAGE_DEVICE) {
            PageManager.getInstance().cleanAllPageDeviceList();
            ArrayList<PageItem> pageItemArrayList = PageManager.getInstance().getPageDeviceList();
            if (pageItemArrayList.size() > 1) {
                initPage(pageItemArrayList);
            } else {
                EventBus.getDefault().post(new PageDeviceSyncEvent());
            }
        }
    }

    private LayoutInflater mLayountInflater;

    @SuppressLint("InflateParams")
    private void initPage(ArrayList<PageItem> pageItemArrayList) {
        PageManageRecyclerAdapter pageManageRecyclerAdapter = new PageManageRecyclerAdapter(context, pageItemArrayList);
        rvPage.setAdapter(pageManageRecyclerAdapter);

        pageManageRecyclerAdapter.notifyDataSetChanged();

        PageReportListCallback callback = new PageReportListCallback(pageManageRecyclerAdapter, this, pageItemArrayList, pageManageRecyclerAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvPage);

        pageManageRecyclerAdapter.setDragListener(holder -> {
            // 手动的调用ItemTouchHelper的开始拖拽方法
            itemTouchHelper.startDrag(holder);
        });

        mLayountInflater = LayoutInflater.from(context);
        if (type == PAGE_DEVICE) {
            layoutNoMove.removeAllViews();
            LinearLayout mLinearLayout;
            if (PageManager.getInstance().pageDeviceNoMove.size() > 0) {
                for (int i = 0; i < PageManager.getInstance().pageDeviceNoMove.size(); i++) {
                    PageItem pageItem = PageManager.getInstance().pageDeviceNoMove.get(i);
                    mLinearLayout = (LinearLayout) mLayountInflater.inflate(R.layout.page_item, null);

                    ImageView ivIcon = mLinearLayout.findViewById(R.id.ivIcon);
                    TextView tvName = mLinearLayout.findViewById(R.id.tvName);
                    TextView tvDelete = mLinearLayout.findViewById(R.id.tvDelete);
                    LinearLayout layoutHide = mLinearLayout.findViewById(R.id.layoutHide);
                    LinearLayout layoutDrag = mLinearLayout.findViewById(R.id.layoutDrag);

                    ivIcon.setVisibility(View.VISIBLE);
                    tvName.setVisibility(View.VISIBLE);
                    tvDelete.setVisibility(View.GONE);
                    layoutHide.setVisibility(View.GONE);
                    layoutDrag.setVisibility(View.INVISIBLE);

                    ivIcon.setBackground(context.getResources().getDrawable(PageManager.getInstance().getDevicePicture(pageItem.index, false)));
                    tvName.setText(PageManager.getInstance().getDeviceName(pageItem.index, context));
                    layoutNoMove.addView(mLinearLayout);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (type == PAGE_APP) {
            setResult(RESULT_OK);
            PageManager.getInstance().setCardJson();
        } else if (type == PAGE_DEVICE) {
            ArrayList<PageItem> pageItemArrayList = PageManager.getInstance().getPageDeviceList();
            if(pageItemArrayList.size() > 0){
                EventBus.getDefault().post(new PageDeviceSetEvent());
            }
        }
        EventTools.SafeUnregisterEventBus(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pageDeviceSyncOverEvent(PageDeviceSyncOverEvent event) {
        ArrayList<PageItem> pageItemArrayList = PageManager.getInstance().getPageDeviceList();
        initPage(pageItemArrayList);
    }
}
