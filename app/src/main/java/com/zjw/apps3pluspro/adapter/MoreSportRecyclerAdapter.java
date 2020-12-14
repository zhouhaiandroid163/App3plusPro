package com.zjw.apps3pluspro.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.module.home.sport.SportModleUtils;
import com.zjw.apps3pluspro.sql.entity.SportModleInfo;
import com.zjw.apps3pluspro.utils.NewTimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MoreSportRecyclerAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private List<SportModleInfo> mList;
    private Callback mCallback;
    private Context context;

    public MoreSportRecyclerAdapter(Context context, List<SportModleInfo> mList) {
        this.mList = mList;
        this.context = context;
    }

    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.more_sport_item, parent, false);
        return new ItemHolder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;

        SportModleInfo mSportModleInfo = mList.get(position);

        if (mSportModleInfo.getDataSourceType() == 0) {
            itemHolder.tvSportName.setText(SportModleUtils.getSportTypeStr(context, mSportModleInfo.getSport_type()));
            Drawable imageDrawable = SportModleUtils.getSportTypeImg(context, mSportModleInfo.getSport_type());
            itemHolder.ivSportIcon.setBackground(imageDrawable);
        } else if (mSportModleInfo.getDataSourceType() == 1) {
            itemHolder.tvSportName.setText(SportModleUtils.getDeviceSportTypeStr(context, mSportModleInfo.getRecordPointSportType()));
            Drawable imageDrawable = SportModleUtils.getDeviceSportTypeImg(context, mSportModleInfo.getRecordPointSportType());
            itemHolder.ivSportIcon.setBackground(imageDrawable);
            switch (mSportModleInfo.getRecordPointSportType()) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    itemHolder.tvValue1.setText(mSportModleInfo.getReportTotalStep() + "");
                    itemHolder.tvValue2.setText(context.getResources().getString(R.string.steps) + "，" + context.getResources().getString(R.string.sport_time) + " " +
                            NewTimeUtils.getTimeString(mSportModleInfo.getReportDuration()));
                    break;
                default:
                    itemHolder.tvValue1.setText(mSportModleInfo.getReportCal() + "");
                    itemHolder.tvValue2.setText(context.getResources().getString(R.string.big_calory) + "，" + context.getResources().getString(R.string.sport_time) + " " +
                            NewTimeUtils.getTimeString(mSportModleInfo.getReportDuration()));
                    break;
            }
        }
        itemHolder.tvSportTime.setText(NewTimeUtils.getStringDate(mSportModleInfo.getRecordPointIdTime(), NewTimeUtils.HHMMSS));

        String sport_type = "100";
        String ui_type = "100";
        String step = "";
        String kcal = "";
        String duration = "";
        String speed = "";
        String date = "";
        if (mSportModleInfo.getUi_type() != null && !mSportModleInfo.getUi_type().equals("")) {
            ui_type = mSportModleInfo.getUi_type();
        }
        if (mSportModleInfo.getSport_type() != null && !mSportModleInfo.getSport_type().equals("")) {
            sport_type = mSportModleInfo.getSport_type();
        }

        if (mSportModleInfo.getUi_type() != null && !mSportModleInfo.getUi_type().equals("")) {
            ui_type = mSportModleInfo.getUi_type();
        }

        if (mSportModleInfo.getTotal_step() != null && !mSportModleInfo.getTotal_step().equals("")) {
            step = mSportModleInfo.getTotal_step();
        }

        if (mSportModleInfo.getCalorie() != null && !mSportModleInfo.getCalorie().equals("")) {
            kcal = mSportModleInfo.getCalorie();
        }

        if (mSportModleInfo.getSport_duration() != null && !mSportModleInfo.getSport_duration().equals("")) {
            duration = mSportModleInfo.getSport_duration();
        }

        if (mSportModleInfo.getSpeed() != null && !mSportModleInfo.getSpeed().equals("")) {
            speed = mSportModleInfo.getSpeed();
        }

        if (mSportModleInfo.getTime() != null && !mSportModleInfo.getTime().equals("")) {
            date = mSportModleInfo.getTime();
        }

        if (mSportModleInfo.getDataSourceType() == 0) {
            if (ui_type.equals("0")) {
                itemHolder.tvValue1.setText(step + "");
                itemHolder.tvValue2.setText(context.getResources().getString(R.string.steps) + "，" + context.getResources().getString(R.string.sport_time) + " " +
                        NewTimeUtils.getTimeString(Long.parseLong(duration)));
            } else if (ui_type.equals("1")) {

                itemHolder.tvValue1.setText(kcal + "");
                itemHolder.tvValue2.setText(context.getResources().getString(R.string.big_calory) + "，" + context.getResources().getString(R.string.sport_time) + " " +
                        NewTimeUtils.getTimeString(Long.parseLong(duration)));
            } else {
                itemHolder.tvValue1.setText(kcal + "");
                itemHolder.tvValue2.setText(context.getResources().getString(R.string.big_calory) + "，" + context.getResources().getString(R.string.sport_time) + " " +
                        NewTimeUtils.getTimeString(Long.parseLong(duration)));
            }
        }
        itemHolder.layoutParent.setOnClickListener(v -> {
            mCallback.onClick(itemHolder.layoutParent, position);
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvSportName)
        TextView tvSportName;
        @BindView(R.id.tvSportTime)
        TextView tvSportTime;
        @BindView(R.id.layoutParent)
        LinearLayout layoutParent;
        @BindView(R.id.ivSportIcon)
        ImageView ivSportIcon;
        @BindView(R.id.tvValue1)
        TextView tvValue1;
        @BindView(R.id.tvValue2)
        TextView tvValue2;


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