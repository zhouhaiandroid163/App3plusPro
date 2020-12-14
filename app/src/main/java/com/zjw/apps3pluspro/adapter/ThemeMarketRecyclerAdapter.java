package com.zjw.apps3pluspro.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lidroid.xutils.BitmapUtils;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.module.device.clockdial.ThemeMarketStyleActivity;
import com.zjw.apps3pluspro.module.device.clockdial.ThemeUploadActivity;
import com.zjw.apps3pluspro.module.device.entity.ThemeMarketItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by android
 * on 2020/10/7
 */
public class ThemeMarketRecyclerAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private List<ThemeMarketItem> mList;
    private ThemeMarketRecyclerAdapter.Callback mCallback;
    private Context context;
    private BitmapUtils bitmapUtils;

    public ThemeMarketRecyclerAdapter(Context context, List<ThemeMarketItem> mList) {
        this.mList = mList;
        this.context = context;
        bitmapUtils = new BitmapUtils(context);
    }

    public void setmCallback(ThemeMarketRecyclerAdapter.Callback mCallback) {
        this.mCallback = mCallback;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.theme_market_item, parent, false);
        return new ThemeMarketRecyclerAdapter.ItemHolder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final ThemeMarketRecyclerAdapter.ItemHolder itemHolder = (ThemeMarketRecyclerAdapter.ItemHolder) holder;

        ThemeMarketItem item = mList.get(position);

        itemHolder.layout1.setVisibility(View.INVISIBLE);
        itemHolder.layout2.setVisibility(View.INVISIBLE);
        itemHolder.layout3.setVisibility(View.INVISIBLE);
        itemHolder.layoutMoreTheme.setVisibility(View.INVISIBLE);

        if (item.dialList.size() >= 1) {
            itemHolder.layout1.setVisibility(View.VISIBLE);
            bitmapUtils.display(itemHolder.iv1, item.dialList.get(0).effectImgUrl);
            itemHolder.tv1.setText(item.dialList.get(0).dialName);

            startActivityByPostion(itemHolder.iv1,item,0);
        }

        if (item.dialList.size() >= 2) {
            itemHolder.layout2.setVisibility(View.VISIBLE);
            bitmapUtils.display(itemHolder.iv2, item.dialList.get(1).effectImgUrl);
            itemHolder.tv2.setText(item.dialList.get(1).dialName);

            startActivityByPostion(itemHolder.iv2,item,1);
        }

        if (item.dialList.size() >= 3) {
            itemHolder.layout3.setVisibility(View.VISIBLE);
            bitmapUtils.display(itemHolder.iv3, item.dialList.get(2).effectImgUrl);
            itemHolder.tv3.setText(item.dialList.get(2).dialName);

            startActivityByPostion(itemHolder.iv3,item,2);

            itemHolder.layoutMoreTheme.setVisibility(View.VISIBLE);
        }

        itemHolder.layoutMoreTheme.setOnClickListener(v -> {
            Intent mIntent = new Intent(context, ThemeMarketStyleActivity.class);
            mIntent.putExtra("dialTypeId", item.dialTypeId);
            mIntent.putExtra("dialTypeName", item.dialTypeName);
            context.startActivity(mIntent);
        });

        itemHolder.tvThemeTitle.setText(item.dialTypeName);

    }

    private void startActivityByPostion(ImageView iv, ThemeMarketItem item, int i) {
        iv.setOnClickListener(v -> {
            Intent mIntent = new Intent(context, ThemeUploadActivity.class);
            mIntent.putExtra("DialInfo", item.dialList.get(i));
            context.startActivity(mIntent);
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvThemeTitle)
        TextView tvThemeTitle;
        @BindView(R.id.layoutMoreTheme)
        LinearLayout layoutMoreTheme;
        @BindView(R.id.layout1)
        LinearLayout layout1;
        @BindView(R.id.layout2)
        LinearLayout layout2;
        @BindView(R.id.layout3)
        LinearLayout layout3;


        @BindView(R.id.iv1)
        ImageView iv1;
        @BindView(R.id.iv2)
        ImageView iv2;
        @BindView(R.id.iv3)
        ImageView iv3;

        @BindView(R.id.tv1)
        TextView tv1;
        @BindView(R.id.tv2)
        TextView tv2;
        @BindView(R.id.tv3)
        TextView tv3;

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
}