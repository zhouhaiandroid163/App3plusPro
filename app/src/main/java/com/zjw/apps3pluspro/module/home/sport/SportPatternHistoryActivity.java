package com.zjw.apps3pluspro.module.home.sport;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.adapter.SportPatternAdapter;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.home.sport.amap.AmapGpsSportActivity;
import com.zjw.apps3pluspro.module.home.sport.amap.AmapLocusActivity;
import com.zjw.apps3pluspro.module.home.sport.google.GoogleGpsSportActivity;
import com.zjw.apps3pluspro.module.home.sport.google.GoogleLocusActivity;
import com.zjw.apps3pluspro.sql.dbmanager.SportModleInfoUtils;
import com.zjw.apps3pluspro.sql.entity.SportModleInfo;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.List;

/**
 * 运动历史列表
 */
public class SportPatternHistoryActivity extends BaseActivity implements OnClickListener {
    private final String TAG = SportPatternHistoryActivity.class.getSimpleName();
    private Context mContext;
    //数据库
    private SportModleInfoUtils mSportModleInfoUtils = BaseApplication.getSportModleInfoUtils();

    private LinearLayout lin_heart_record_nodata;

    private ListView list_sport_pattern_history;
    private SportPatternAdapter mSportPatternAdapter;

    private TextView tv_sport_modle_total_time, tv_sport_modle_total_count;

    @Override
    protected int setLayoutId() {
        bgColor = R.color.color_278DFD;
        isTextDark = false;
        return R.layout.activity_sport_pattern_history;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = SportPatternHistoryActivity.this;
        initView();
        //模拟 清空多运动模式表
//        mSportModleInfoUtils.deleteAllData();
        initSetAdapter();
//        initData();


        //模拟数据
//        String pointData = "113.834757,22.631997;113.834732,22.631986;113.834711,22.631983;113.834686,22.631972;113.834667,22.631963;113.834650,22.631950;113.834634,22.631935;113.834636,22.631911;113.834645,22.631893;113.834651,22.631869;113.834687,22.631537;113.834690,22.631468;113.834746,22.631308;113.834709,22.631391;113.834767,22.631282;113.834842,22.631222;113.834967,22.631092;113.834897,22.631195;113.834886,22.631142;113.834903,22.631104;113.834915,22.631015;113.834941,22.631013;113.835024,22.630946;113.834967,22.631025;113.834927,22.631064;113.834941,22.631031;113.834967,22.631013;113.834882,22.631054;113.834949,22.631009;113.835017,22.630923;113.834980,22.630950;113.835088,22.630810;113.835120,22.630761;113.835159,22.630695;113.835220,22.630656;113.835277,22.630574;113.835486,22.630531;113.835459,22.630542;113.835532,22.630580;113.835506,22.630450;113.835524,22.630383;113.835453,22.630393;113.835463,22.630350;113.835463,22.630309;113.835512,22.630198;113.835509,22.630230;113.835592,22.630178;113.835588,22.630037;113.835539,22.629730;113.835611,22.629804;113.835724,22.629869;113.835864,22.629817;113.835678,22.629572;113.835655,22.629508;113.835672,22.629583;113.835691,22.629476;113.835694,22.629505;113.835711,22.629395;113.835797,22.629364;113.835785,22.629239;113.835791,22.629147;113.835702,22.629442;113.835942,22.629158;113.836031,22.628984;113.836060,22.628894;113.836228,22.628751;113.835980,22.628840;113.836054,22.628793;113.836109,22.628775;113.836162,22.628660;113.836541,22.628779;113.836523,22.628752;113.836351,22.628477;113.836327,22.628483;113.836253,22.628305;113.836216,22.628315;113.836138,22.628310;113.836315,22.628143;113.836450,22.627811;113.836241,22.628009;113.836532,22.627791;113.836717,22.627854;113.836904,22.627651;113.837103,22.627501;113.837090,22.627439;113.837107,22.627363;113.837153,22.627447;113.837132,22.627328;113.837128,22.627304;113.837156,22.627317;113.837185,22.627295;113.837171,22.627269;113.837166,22.627234;113.837151,22.627298;113.837234,22.627204;113.837314,22.627003;113.837413,22.626858;113.837601,22.626627;113.837627,22.626526;113.837560,22.626507;113.837643,22.626416;113.837728,22.626357;113.837788,22.626348;113.837809,22.626338;113.837809,22.626358;113.837791,22.626349;113.837846,22.626214;113.837689,22.625982;113.837567,22.625861;113.837567,22.625976;113.837591,22.626087;113.837601,22.626124;113.837492,22.626085;113.837417,22.626026;113.837386,22.626022;113.837448,22.626026;113.837358,22.626103;113.837391,22.626065;113.837456,22.626088;113.837526,22.626085;113.837552,22.626113;113.837518,22.626043;113.837517,22.626012;113.837486,22.625980;113.837494,22.625952";
//        //模拟插入多运动-健走
//        SportModleInfo mSportModleInfo = new SportModleInfo();
//        mSportModleInfo.setUser_id(BaseApplication.getUserId());
//        mSportModleInfo.setTime("2019-10-25 16:36:15");
//        mSportModleInfo.setSport_duration(String.valueOf("100"));
//        mSportModleInfo.setSport_type("100");
//        mSportModleInfo.setUi_type("100");
//        mSportModleInfo.setSpeed("100");
//        mSportModleInfo.setDisance("100");
//        mSportModleInfo.setMap_data(pointData);
//        mSportModleInfo.setCalorie("100");
//        //高德
//        Intent intent = new Intent(mContext, AmapLocusActivity.class);
//        intent.putExtra(IntentConstants.SportModleInfo, mSportModleInfo);
//        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
//        list_record = (DeleteListView) findViewById(R.id.list_record);
        lin_heart_record_nodata = (LinearLayout) findViewById(R.id.lin_heart_record_nodata);

        findViewById(R.id.public_no_bg_head_back).setOnClickListener(this);
        findViewById(R.id.public_no_bg_head_back_img).setBackgroundResource(R.drawable.my_icon_back_white);
        ((TextView) (findViewById(R.id.public_no_bg_head_title))).setText(getString(R.string.movement_history));
        ((TextView) (findViewById(R.id.public_no_bg_head_title))).setTextColor(getResources().getColor(R.color.public_text_color_white));

        tv_sport_modle_total_time = (TextView) findViewById(R.id.tv_sport_modle_total_time);
        tv_sport_modle_total_count = (TextView) findViewById(R.id.tv_sport_modle_total_count);

        findViewById(R.id.start_sport).setOnClickListener(this);

        list_sport_pattern_history = (ListView) findViewById(R.id.list_sport_pattern_history);

    }

    void initSetAdapter() {
        mSportPatternAdapter = new SportPatternAdapter(SportPatternHistoryActivity.this);
        list_sport_pattern_history.setAdapter(mSportPatternAdapter);
        list_sport_pattern_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {


                SportModleInfo mSportModleInfo = mSportPatternAdapter.getDevice(arg2);


                if (mSportModleInfo != null) {
                    if (mSportModleInfo.getUi_type().equals("100")) {
                        if (MyUtils.isGoogle(mContext)) {
                            //谷歌
                            Intent intent = new Intent(mContext, GoogleLocusActivity.class);
                            intent.putExtra(IntentConstants.SportModleInfo, mSportModleInfo);
                            startActivity(intent);
                        } else {
                            //高德
                            Intent intent = new Intent(mContext, AmapLocusActivity.class);
                            intent.putExtra(IntentConstants.SportModleInfo, mSportModleInfo);
                            startActivity(intent);
                        }


                    } else {
//                        Intent intent = new Intent(mContext, SportPatternDetailsActivity.class);
//                        intent.putExtra(IntentConstants.SportModleInfo, mSportModleInfo);
//                        startActivity(intent);
                    }

                }

            }
        });

        //listView长按事件
        list_sport_pattern_history.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                showDeleteHistoryData(position);

                return true;
            }
        });


    }

    private void initData() {
        getSportModleData();
    }

    /**
     * 获取多运动模式数据
     */
    void getSportModleData() {

        MyLog.i(TAG, "getSportModleData()");

        List<SportModleInfo> my_sport_list = mSportModleInfoUtils.queryAllTime(BaseApplication.getUserId());

        MyLog.i(TAG, "my_sport_list = size = " + my_sport_list.size());
        MyLog.i(TAG, "my_sport_list = toString = " + my_sport_list.toString());

        updateUi(my_sport_list);

    }

    /**
     * 展示当前用户UI
     *
     * @param my_sport_list
     */
    void updateUi(List<SportModleInfo> my_sport_list) {

        mSportPatternAdapter.clear();
        mSportPatternAdapter.setDevice(my_sport_list);
        mSportPatternAdapter.notifyDataSetChanged();
        int duration = 0;
        int total_duration = 0;

        if (my_sport_list.size() > 0) {

            lin_heart_record_nodata.setVisibility(View.GONE);


            for (int i = 0; i < my_sport_list.size(); i++) {

                duration = 0;

                try {
                    duration = Integer.valueOf(my_sport_list.get(i).getSport_duration());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                MyLog.i(TAG, "计算总时长 i = " + i + "  duration = " + duration);

                total_duration += duration;
            }

            tv_sport_modle_total_time.setText(SportModleUtils.getMyMinute(String.valueOf(total_duration)));
            tv_sport_modle_total_count.setText(String.valueOf(my_sport_list.size()));

        } else {
//            showEmptyData();
            tv_sport_modle_total_time.setText(SportModleUtils.getMyMinute(String.valueOf(total_duration)));
            tv_sport_modle_total_count.setText(String.valueOf(my_sport_list.size()));
            lin_heart_record_nodata.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.public_no_bg_head_back:
                finish();
                break;


            case R.id.start_sport:

                if (MyUtils.isGPSOpen(this)) {
                    Intent intent;
                    if (MyUtils.isGoogle(SportPatternHistoryActivity.this)) {
                        //谷歌
                        intent = new Intent(this, GoogleGpsSportActivity.class);
                        startActivity(intent);
                    } else {
                        //高德
                        intent = new Intent(this, AmapGpsSportActivity.class);
                        startActivity(intent);
                    }
                } else {
                    showSettingGps();
                }

                break;
        }
    }


    /**
     * 显示设置Gps的对话框
     */
    private void showSettingGps() {
        new android.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_prompt))//设置对话框标题

                .setMessage(getString(R.string.open_gps))//设置显示的内容

                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {//添加确定按钮

                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent); // 设置完成后返回到原来的界面

                    }

                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {//添加返回按钮


            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件

                // TODO Auto-generated method stub


            }

        }).show();//在按键响应事件中显示此对话框
    }

    /**
     * 弹出删除对话框
     *
     * @param position
     */
    void showDeleteHistoryData(final int position) {

        new android.app.AlertDialog.Builder(SportPatternHistoryActivity.this)
                .setTitle(getString(R.string.dialog_prompt))//设置对话框标题

                .setMessage(getString(R.string.delete_sport_tip))//设置显示的内容

                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {//添加确定按钮

                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                        deleteSportItem(position);


                    }

                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {//添加返回按钮


            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件

                // TODO Auto-generated method stub


            }

        }).show();//在按键响应事件中显示此对话框
    }

    void deleteSportItem(int position) {
        SportModleInfo mSportModleInfo = mSportPatternAdapter.getDevice(position);
        if (mSportModleInfo != null) {
            mSportModleInfoUtils.deleteDataToDate(mSportModleInfo);
            initData();
        }


    }


}
