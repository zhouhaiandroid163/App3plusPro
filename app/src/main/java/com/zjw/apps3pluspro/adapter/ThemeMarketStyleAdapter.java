package com.zjw.apps3pluspro.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.module.device.entity.ThemeMarketItem;

import java.util.ArrayList;

/**
 * Created by android
 * on 2020/10/8
 */
public class ThemeMarketStyleAdapter extends BaseAdapter {

    private final BitmapUtils bitmapUtils;
    private LayoutInflater mInflator;
    private ArrayList<ThemeMarketItem.DialInfo> dialInfos;

    public ThemeMarketStyleAdapter(Context context, ArrayList<ThemeMarketItem.DialInfo> dialInfos) {
        super();
        this.mInflator = LayoutInflater.from(context);
        this.dialInfos = dialInfos;
        bitmapUtils = new BitmapUtils(context);
    }

    @Override
    public int getCount() {
        return dialInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return dialInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView tvName;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ThemeMarketStyleAdapter.ViewHolder viewHolder;

        if (view == null) {
            view = mInflator.inflate(R.layout.theme_market_stytle_item, null);

            viewHolder = new ThemeMarketStyleAdapter.ViewHolder();
            viewHolder.tvName = view.findViewById(R.id.tvName);
            viewHolder.imageView = view.findViewById(R.id.imageView);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ThemeMarketStyleAdapter.ViewHolder) view.getTag();
        }

        ThemeMarketItem.DialInfo dialInfo = dialInfos.get(position);

        bitmapUtils.display(viewHolder.imageView, dialInfo.effectImgUrl);
        viewHolder.tvName.setText(dialInfo.dialName);

        return view;
    }


}
