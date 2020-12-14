package com.zjw.apps3pluspro.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.module.home.PageManagementActivity;
import com.zjw.apps3pluspro.module.home.entity.PageItem;
import com.zjw.apps3pluspro.utils.PageManager;
import com.zjw.apps3pluspro.utils.PageReportListCallback;


import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 可拖拽列表的适配器，
 * 1.需要实现OnlineReportListCallback.ItemTouchMoveListener
 * 2.持有一个接口用于传递position
 */
public class PageManageRecyclerAdapter extends RecyclerView.Adapter implements PageReportListCallback.ItemTouchMoveListener, View.OnClickListener {

    private List<PageItem> mList;
    private Callback mCallback;
    private Context context;

    public PageManageRecyclerAdapter(Context context, List<PageItem> mList) {
        this.mList = mList;
        this.context = context;
    }

    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.page_item, parent, false);
        return new ItemHolder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;

        PageItem pageItem = mList.get(position);

        //设置holder.img 的OnTouchListener方法
        itemHolder.ivDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //dragListener 是适配器内部接口
                    if (dragListener != null) {
                        dragListener.onDrag(itemHolder);
                    }
                }
                return false;
            }
        });

        if (pageItem.index != PageManager.PAGE_HIDE) {
            itemHolder.layoutPage.setBackground(context.getResources().getDrawable(R.drawable.white_corner_bg));
            itemHolder.ivIcon.setVisibility(View.VISIBLE);
            itemHolder.tvName.setVisibility(View.VISIBLE);
            itemHolder.tvDelete.setVisibility(View.GONE);
            itemHolder.layoutHide.setVisibility(View.GONE);

            if (PageManagementActivity.type == 0) {
                if (PageManager.getInstance().isHideApp(pageItem.index)) {
                    showGreyView(itemHolder, true);

                    itemHolder.ivIcon.setBackground(context.getResources().getDrawable(PageManager.getInstance().getPicture(pageItem.index, true)));
                } else {
                    showGreyView(itemHolder, false);

                    itemHolder.ivIcon.setBackground(context.getResources().getDrawable(PageManager.getInstance().getPicture(pageItem.index, false)));
                }
            } else if (PageManagementActivity.type == 1) {
                if (PageManager.getInstance().isHideDevice(pageItem.index)) {

                    showGreyView(itemHolder, true);
                    itemHolder.ivIcon.setBackground(context.getResources().getDrawable(PageManager.getInstance().getDevicePicture(pageItem.index, true)));
                } else {
                    showGreyView(itemHolder, false);
                    itemHolder.ivIcon.setBackground(context.getResources().getDrawable(PageManager.getInstance().getDevicePicture(pageItem.index, false)));
                }
            }

        } else {
            itemHolder.layoutPage.setBackground(null);
            itemHolder.ivIcon.setVisibility(View.GONE);
            itemHolder.tvName.setVisibility(View.GONE);
            itemHolder.tvDelete.setVisibility(View.VISIBLE);

            boolean isHideLayoutHide = PageManager.getInstance().getIndexPosition(PageManager.PAGE_HIDE) == PageManager.getInstance().getPageAppList().size() - 1;
            if(PageManagementActivity.type == 1){
                isHideLayoutHide = PageManager.getInstance().getIndexPositionPageDevice(PageManager.PAGE_HIDE) == PageManager.getInstance().pageDeviceList.size() - 1;
            }

            if (isHideLayoutHide) {
                itemHolder.layoutHide.setVisibility(View.VISIBLE);
            } else {
                itemHolder.layoutHide.setVisibility(View.GONE);
            }

        }
        if (pageItem.isMark) {
            itemHolder.ivDrag.setVisibility(View.GONE);
        } else {
            itemHolder.ivDrag.setVisibility(View.VISIBLE);
        }

        String name = "";

        if (PageManagementActivity.type == 0) {
            switch (pageItem.index) {
                case PageManager.PAGE_HIDE:
                    break;
                case PageManager.PAGE_APP_ECG:
                    name = context.getResources().getString(R.string.ecg_measure_ecg);
                    break;
                case PageManager.PAGE_APP_EXERCISE:
                    name = context.getResources().getString(R.string.page_exercise_title);
                    break;
                case PageManager.PAGE_APP_HEART:
                    name = context.getResources().getString(R.string.heart);
                    break;
                case PageManager.PAGE_APP_SLEEP:
                    name = context.getResources().getString(R.string.title_sleep);
                    break;
                case PageManager.PAGE_APP_GPS_SPORT:
                    name = context.getResources().getString(R.string.data_sport);
                    break;
                case PageManager.PAGE_APP_BLOOD_PRESSURE:
                    name = context.getResources().getString(R.string.blood_pressure);
                    break;
                case PageManager.PAGE_APP_MENSTRUAL_PERIOD:
                    name = context.getResources().getString(R.string.cycle_tile);
                    break;
                case PageManager.PAGE_APP_BLOOD_OXYGEN:
                    name = context.getResources().getString(R.string.spo2_str);
                    break;
                case PageManager.PAGE_APP_TEMPERATURE:
                    name = context.getResources().getString(R.string.temp_body);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + pageItem.index);
            }
        } else if (PageManagementActivity.type == 1) {
            name = PageManager.getInstance().getDeviceName(pageItem.index, context);
        }

        itemHolder.tvName.setText(name);

        itemHolder.index = pageItem.index;
    }

    private void showGreyView(ItemHolder itemHolder, boolean isShow) {
        if (isShow) {
            itemHolder.layoutPage.setBackground(context.getResources().getDrawable(R.drawable.card_grey_bg));
            itemHolder.ivArrow.setBackground(context.getResources().getDrawable(R.mipmap.drag_arrow));
        } else {
            itemHolder.layoutPage.setBackground(context.getResources().getDrawable(R.drawable.white_corner_bg));
            itemHolder.ivArrow.setBackground(context.getResources().getDrawable(R.mipmap.drag_arrow));
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvDelete)
        TextView tvDelete;
        @BindView(R.id.layoutPage)
        public
        ConstraintLayout layoutPage;
        @BindView(R.id.layoutDrag)
        LinearLayout ivDrag;
        @BindView(R.id.ivIcon)
        public
        ImageView ivIcon;
        @BindView(R.id.ivArrow)
        public
        ImageView ivArrow;
        @BindView(R.id.layoutHide)
        public
        LinearLayout layoutHide;

        public int index;

        ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onClick(View v) {
        mCallback.onClick(v, (int) v.getTag());
    }

    public interface Callback {
        void onClick(View v, int position);
    }


    public interface DragListener {
        /**
         * 使用接口回调的方式将ViewHolder返回
         *
         * @param itemHolder
         */
        void onDrag(ItemHolder itemHolder);
    }

    private DragListener dragListener;

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }
}