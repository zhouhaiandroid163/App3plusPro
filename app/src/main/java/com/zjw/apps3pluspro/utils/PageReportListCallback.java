package com.zjw.apps3pluspro.utils;

import android.app.Service;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Vibrator;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.zjw.apps3pluspro.adapter.PageManageRecyclerAdapter;
import com.zjw.apps3pluspro.module.home.entity.PageItem;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 用来完成RecyclerView长按拖拽的关键接口
 * 1.getMovementFlags里面表示设置为上下拖动
 * 2.onSelectedChanged里面表示拖动状态下改变背景色，拖动完成后恢复背景色
 * 3.拖动完成的时候viewHolder的值为空！！！所以要用srcHolder
 */
public class PageReportListCallback extends ItemTouchHelper.Callback {

    private ColorDrawable drawable;
    private RecyclerView.ViewHolder srcHolder;
    private Context context;

    private ArrayList<PageItem> pageItemArrayListem;
    private PageManageRecyclerAdapter pageManageRecyclerAdapter;

    public interface ItemTouchMoveListener {
        boolean onItemMove(int fromPosition, int toPosition);
    }

    private ItemTouchMoveListener moveListener;

    public PageReportListCallback(ItemTouchMoveListener moveListener, Context mContext, ArrayList<PageItem> pageItemArrayListem, PageManageRecyclerAdapter pageManageRecyclerAdapter) {
        this.moveListener = moveListener;
        this.context = mContext;
        this.pageItemArrayListem = pageItemArrayListem;
        this.pageManageRecyclerAdapter = pageManageRecyclerAdapter;

        int rgb = Color.rgb(0xff, 0xff, 0xff);
        drawable = new ColorDrawable(rgb);

    }
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, ItemTouchHelper.ACTION_STATE_IDLE);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {

        int fromPosition = srcHolder.getAdapterPosition();//得到拖动ViewHolder的position
        int toPosition = targetHolder.getAdapterPosition();//得到目标ViewHolder的position

        PageItem pageItem =  pageItemArrayListem.get(fromPosition);
        if(pageItem.isMark){
            return false;
        }
        if (fromPosition < toPosition) {
            //分别把中间所有的item的位置重新交换
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(pageItemArrayListem, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(pageItemArrayListem, i, i - 1);
            }
        }
        pageManageRecyclerAdapter.notifyItemMoved(fromPosition, toPosition);
        //返回true表示执行拖动
        return true;
//
//        this.srcHolder = srcHolder;
//        return srcHolder.getItemViewType() == targetHolder.getItemViewType() && moveListener.onItemMove(srcHolder.getAdapterPosition(), targetHolder.getAdapterPosition());
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    /**
     * @param viewHolder  操作的ViewHolder对象
     * @param actionState 当前的状态， ItemTouchHelper.ACTION_STATE_IDLE，闲置状态
     *                    ItemTouchHelper.ACTION_STATE_SWIPE，开始滑动状态
     *                    ItemTouchHelper.ACTION_STATE_DRAG，开始拖拽
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        //如果不是闲置状态
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            //获取系统震动服务
            Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
            if (vib != null) {
                vib.vibrate(70);
            }

            if (viewHolder instanceof PageManageRecyclerAdapter.ItemHolder) {
                PageManageRecyclerAdapter.ItemHolder myViewHolder = (PageManageRecyclerAdapter.ItemHolder) viewHolder;
                //设置字体为白色
//                myViewHolder.tvName.setTextColor(Color.WHITE);
                //设置背景颜色为蓝色
//                myViewHolder.layoutPage.setBackgroundColor(viewHolder.itemView.getResources().getColor(android.R.color.holo_blue_light));
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 还原状态
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof PageManageRecyclerAdapter.ItemHolder) {
            PageManageRecyclerAdapter.ItemHolder itemHolder = (PageManageRecyclerAdapter.ItemHolder) viewHolder;

        }
        super.clearView(recyclerView, viewHolder);
        try {
            pageManageRecyclerAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
