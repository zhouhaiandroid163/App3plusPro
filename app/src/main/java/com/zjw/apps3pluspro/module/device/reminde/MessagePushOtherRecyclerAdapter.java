package com.zjw.apps3pluspro.module.device.reminde;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;

import java.util.ArrayList;

/**
 * Created by android
 * on 2021/5/13
 */
public class MessagePushOtherRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = MessagePushOtherRecyclerAdapter.class.getSimpleName();
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.other_app_item_layout, parent, false);
        return new NormalHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NormalHolder normalHolder = (NormalHolder) holder;

        OtherApp otherApp = mDatas.get(position);
        if (otherApp != null) {
            normalHolder.tvName.setText(otherApp.appName);
            normalHolder.ivIcon.setImageDrawable(otherApp.icon);
            if (mBleDeviceTools.getOtherMessage(otherApp.packageName)) {
                normalHolder.mSwitchCompat.setChecked(true);
            } else {
                normalHolder.mSwitchCompat.setChecked(false);
            }

            normalHolder.mSwitchCompat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (normalHolder.mSwitchCompat.isChecked()) {
                        mBleDeviceTools.setOtherMessage(otherApp.packageName, true);
                        Log.i(TAG, "open = " + otherApp.packageName);
                    } else {
                        mBleDeviceTools.setOtherMessage(otherApp.packageName, false);
                        Log.i(TAG, "close = " + otherApp.packageName);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    private Context mContext;
    private ArrayList<OtherApp> mDatas;

    public MessagePushOtherRecyclerAdapter(Context context, ArrayList<OtherApp> datas) {
        mContext = context;
        mDatas = datas;
    }

    public static class OtherApp {
        Drawable icon;
        String packageName;
        String appName;
    }


    public static class NormalHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivIcon;
        SwitchCompat mSwitchCompat;

        NormalHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            mSwitchCompat = itemView.findViewById(R.id.mSwitchCompat);

        }
    }

}
