package com.zjw.apps3pluspro.module.home.sport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sql.entity.SportModleInfo;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.MyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 运动详情
 */
public class SportPatternDetailsActivity extends Activity implements View.OnClickListener {

    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    private Context context;


    SportModleInfo mSportModleInfo = null;

    private TextView public_no_bg_head_title;

    private ImageView new_locus_img_type;

    private LinearLayout new_locus_lin_step;
    private TextView new_locus_text_step;

    private LinearLayout new_locus_lin_kcal;
    private TextView new_locus_text_kacal;

    private LinearLayout new_locus_lin_distance;
    private TextView new_locus_text_distance;
    private TextView new_locus_text_distance_unit;

    private TextView new_locus_text_duration;
    private TextView new_locus_text_start_time;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_pattern_details);
        context = SportPatternDetailsActivity.this;
        initView();
        initData();
    }

    void initView() {
        findViewById(R.id.public_no_bg_head_back).setOnClickListener(this);

        public_no_bg_head_title = (TextView) findViewById(R.id.public_no_bg_head_title);

        new_locus_img_type = (ImageView) findViewById(R.id.new_locus_img_type);

        new_locus_lin_step = (LinearLayout) findViewById(R.id.new_locus_lin_step);
        new_locus_text_step = (TextView) findViewById(R.id.new_locus_text_step);

        new_locus_lin_kcal = (LinearLayout) findViewById(R.id.new_locus_lin_kcal);
        new_locus_text_distance = (TextView) findViewById(R.id.new_locus_text_distance);
        new_locus_text_distance_unit = (TextView) findViewById(R.id.new_locus_text_distance_unit);

        new_locus_lin_distance = (LinearLayout) findViewById(R.id.new_locus_lin_distance);
        new_locus_text_kacal = (TextView) findViewById(R.id.new_locus_text_kacal);

        new_locus_text_duration = (TextView) findViewById(R.id.new_locus_text_duration);
        new_locus_text_start_time = (TextView) findViewById(R.id.new_locus_text_start_time);

    }

    void initData() {

        Intent intent = getIntent();

//        mSportModleInfo = intent.getParcelableExtra(IntentConstants.SportModleInfo);
        mSportModleInfo = MoreSportActivity.Companion.getSportModleInfo();

        String my_ui_type = "";
        String my_sport_type = "";
        String my_step = "";
        String my_kcal = "";
        String my_distance = "";
        String my_duration = "";
        String my_start_time = "";

        if (mSportModleInfo != null) {
            my_ui_type = mSportModleInfo.getUi_type();
            my_sport_type = mSportModleInfo.getSport_type();
            my_step = mSportModleInfo.getTotal_step();
            my_kcal = mSportModleInfo.getCalorie();
            my_distance = mSportModleInfo.getDisance();
            my_duration = mSportModleInfo.getSport_duration();
            my_start_time = mSportModleInfo.getTime();
        }


        new_locus_lin_step.setVisibility(View.GONE);
        new_locus_lin_kcal.setVisibility(View.GONE);
        new_locus_lin_distance.setVisibility(View.GONE);

        if (my_ui_type != null && !my_ui_type.equals("")) {


            if (my_sport_type != null && !my_sport_type.equals("")) {

                Drawable imageDrawable = SportModleUtils.getSportTypeTopImg(context, my_sport_type);
                new_locus_img_type.setBackgroundDrawable(imageDrawable);

            }


            if (my_ui_type.equals("0")) {
                new_locus_lin_step.setVisibility(View.VISIBLE);
                if (my_step != null && !my_step.equals("")) {
                    new_locus_text_step.setText(my_step);
                }
            } else if (my_ui_type.equals("1")) {
                new_locus_lin_kcal.setVisibility(View.VISIBLE);
                if (my_kcal != null && !my_kcal.equals("")) {
                    new_locus_text_kacal.setText(my_kcal);
                }
            } else if (my_ui_type.equals("2")) {

                new_locus_lin_step.setVisibility(View.VISIBLE);
                if (my_step != null && !my_step.equals("")) {
                    new_locus_text_step.setText(my_step);
                }

                new_locus_lin_kcal.setVisibility(View.VISIBLE);
                if (my_kcal != null && !my_kcal.equals("")) {
                    new_locus_text_kacal.setText(my_kcal);
                }

                new_locus_lin_distance.setVisibility(View.VISIBLE);
                if (my_distance != null && !my_distance.equals("")) {


                    my_distance = my_distance.replace(",", ".");

                    if (mBleDeviceTools.get_device_unit() == 1) {
                        my_distance = AppUtils.GetFormat(2, Float.valueOf(my_distance) / 1000 + 0.005f);
                        new_locus_text_distance_unit.setText(getString(R.string.sport_distance_unit));
                    } else {
                        my_distance = AppUtils.GetFormat(2, Float.valueOf(my_distance) / 1000 / 1.61f);
                        new_locus_text_distance_unit.setText(getString(R.string.unit_mi));
                    }

                    my_distance = my_distance.replace(",", ".");

                    new_locus_text_distance.setText(my_distance);

                }


            }

            if (my_duration != null && !my_duration.equals("")) {
//                new_locus_text_duration.setText(my_duration);
                String sport_time_used = SportModleUtils.getcueDate(Integer.valueOf(my_duration) * 1000);
                new_locus_text_duration.setText(sport_time_used);

            }

            if (my_start_time != null && !my_start_time.equals("")) {
                new_locus_text_start_time.setText(MyTime.getMyData(my_start_time));
                public_no_bg_head_title.setText(getMyLocusDate(my_start_time));
            }

        }

    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.public_no_bg_head_back:
                finish();
                break;


        }
    }


    public static String getMyLocusDate(String time) {
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        result = format2.format(date);

        return result;

    }

}
