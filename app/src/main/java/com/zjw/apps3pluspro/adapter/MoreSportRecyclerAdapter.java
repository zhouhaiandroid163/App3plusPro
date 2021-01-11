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
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.module.home.sport.SportModleUtils;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sql.entity.SportModleInfo;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.NewTimeUtils;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MoreSportRecyclerAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
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

    @SuppressLint({"ClickableViewAccessibility", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final ItemHolder itemHolder = (ItemHolder) holder;

        SportModleInfo mSportModleInfo = mList.get(position);

        itemHolder.layoutData1.setVisibility(View.VISIBLE);
        itemHolder.layoutData4.setVisibility(View.GONE);
        itemHolder.layoutData5.setVisibility(View.GONE);
        itemHolder.layoutDataUI0.setVisibility(View.GONE);
        itemHolder.layoutDataUI1.setVisibility(View.GONE);

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
        String step = "0";
        String kcal = "0";
        String duration = "0";
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
            itemHolder.layoutData1.setVisibility(View.GONE);
            switch (ui_type) {
                case "0":
                    itemHolder.layoutDataUI0.setVisibility(View.VISIBLE);
                    itemHolder.tvStepsUI0.setText(step + "");
                    itemHolder.tvDurationUI0.setText(NewTimeUtils.getTimeString(Long.parseLong(duration)));
                    break;
                case "1":
                    itemHolder.layoutDataUI1.setVisibility(View.VISIBLE);
                    itemHolder.tvCalUI1.setText(kcal + "");
                    itemHolder.tvDurationUI1.setText(NewTimeUtils.getTimeString(Long.parseLong(duration)));
                    break;
                case "4":
                case "5":
                    if (ui_type.equalsIgnoreCase("4")) {
                        itemHolder.layoutSpeed.setVisibility(View.GONE);
                        itemHolder.layoutData4.setVisibility(View.VISIBLE);
                        itemHolder.layoutData5.setVisibility(View.GONE);
                    } else if (ui_type.equalsIgnoreCase("5")) {
                        itemHolder.layoutData4.setVisibility(View.GONE);
                        itemHolder.layoutData5.setVisibility(View.VISIBLE);
                    }

                    itemHolder.tvDuration.setText(NewTimeUtils.getTimeString(Long.parseLong(duration)));
                    itemHolder.tvSteps.setText(step);
                    itemHolder.tvCal.setText(kcal);

                    itemHolder.tvDuration5.setText(NewTimeUtils.getTimeString(Long.parseLong(duration)));
                    itemHolder.tvCal5.setText(kcal);

                    String my_distance = mSportModleInfo.getDisance();
                    if (my_distance != null && !my_distance.equals("")) {
                        my_distance = my_distance.replace(",", ".");

                        DecimalFormat decimalFormat = new DecimalFormat(",##0.00");

                        long duration11 = Long.parseLong(duration);
                        long distance = Long.parseLong(my_distance);

                        double pace = 0;
                        if (distance != 0) {
                            pace = duration11 / (distance / 1000f);
                        }
                        int minute = (int) (pace / 60);
                        int second = (int) (pace % 60);
                        String formatPace = "";
                        if ((minute * 60 + second) > (50 * 60 + 58)) {
                            formatPace = String.format("%1$02d'%2$02d\"", 0, 0);
                        } else {
                            if (mBleDeviceTools.get_device_unit() == 1) {
                                formatPace = String.format("%1$02d'%2$02d\"", (int) (pace / 60), (int) (pace % 60));
                            } else {
                                pace = duration11 / (distance / 1000f / 1.61f);
                                formatPace = String.format("%1$02d'%2$02d\"", (int) (pace / 60), (int) (pace % 60));
                            }
                        }

                        itemHolder.tvPace.setText(formatPace);
                        itemHolder.tvPace5.setText(formatPace);

                        float distanceK = 0f;
                        if (mBleDeviceTools.get_device_unit() == 1) {
                            distanceK = Float.parseFloat(my_distance) / 1000;

                            itemHolder.tvDistanceUnit.setText(context.getResources().getString(R.string.sport_distance_unit));
                            itemHolder.tvSpeedUnit.setText("km/h");

                            itemHolder.tvDistanceUnit5.setText(context.getResources().getString(R.string.sport_distance_unit));
                            itemHolder.tvSpeedUnit5.setText("km/h");
                        } else {
                            distanceK = Float.parseFloat(my_distance) / 1000 / 1.61f;

                            itemHolder.tvDistanceUnit.setText(context.getResources().getString(R.string.unit_mi));
                            itemHolder.tvSpeedUnit.setText("mi/h");

                            itemHolder.tvDistanceUnit5.setText(context.getResources().getString(R.string.unit_mi));
                            itemHolder.tvSpeedUnit5.setText("mi/h");
                        }

                        itemHolder.tvDistance.setText(decimalFormat.format(distanceK));
                        itemHolder.tvDistance5.setText(decimalFormat.format(distanceK));

                        itemHolder.tvSpeed.setText(decimalFormat.format(distanceK / (duration11 / 3600f)));
                        itemHolder.tvSpeed5.setText(decimalFormat.format(distanceK / (duration11 / 3600f)));
                    }

                    break;
                default:
                    itemHolder.tvValue1.setText(kcal + "");
                    itemHolder.tvValue2.setText(context.getResources().getString(R.string.big_calory) + "，" + context.getResources().getString(R.string.sport_time) + " " + NewTimeUtils.getTimeString(Long.parseLong(duration)));
                    break;
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
        @BindView(R.id.layoutSpeed)
        LinearLayout layoutSpeed;

        @BindView(R.id.layoutData1)
        ConstraintLayout layoutData1;
        @BindView(R.id.layoutData4)
        ConstraintLayout layoutData4;
        @BindView(R.id.layoutData5)
        ConstraintLayout layoutData5;
        @BindView(R.id.layoutDataUI0)
        ConstraintLayout layoutDataUI0;
        @BindView(R.id.layoutDataUI1)
        ConstraintLayout layoutDataUI1;

        @BindView(R.id.tvDuration)
        TextView tvDuration;
        @BindView(R.id.tvSteps)
        TextView tvSteps;
        @BindView(R.id.tvCal)
        TextView tvCal;
        @BindView(R.id.tvDistance)
        TextView tvDistance;
        @BindView(R.id.tvDistanceUnit)
        TextView tvDistanceUnit;
        @BindView(R.id.tvPace)
        TextView tvPace;
        @BindView(R.id.tvSpeed)
        TextView tvSpeed;
        @BindView(R.id.tvSpeedUnit)
        TextView tvSpeedUnit;

        @BindView(R.id.tvDuration5)
        TextView tvDuration5;
        @BindView(R.id.tvCal5)
        TextView tvCal5;
        @BindView(R.id.tvDistance5)
        TextView tvDistance5;
        @BindView(R.id.tvDistanceUnit5)
        TextView tvDistanceUnit5;
        @BindView(R.id.tvPace5)
        TextView tvPace5;
        @BindView(R.id.tvSpeed5)
        TextView tvSpeed5;
        @BindView(R.id.tvSpeedUnit5)
        TextView tvSpeedUnit5;

        @BindView(R.id.tvStepsUI0)
        TextView tvStepsUI0;
        @BindView(R.id.tvDurationUI0)
        TextView tvDurationUI0;

        @BindView(R.id.tvCalUI1)
        TextView tvCalUI1;
        @BindView(R.id.tvDurationUI1)
        TextView tvDurationUI1;


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