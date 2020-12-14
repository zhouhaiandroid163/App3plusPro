package com.zjw.apps3pluspro.module.device.reminde;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.adapter.AlarmClockAdapter;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.device.entity.AlarmClockModel;
import com.zjw.apps3pluspro.bleservice.BtSerializeation;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.List;

/**
 * 设备闹钟
 *
 * @author Administrator
 */
public class AlarmClockActivity extends BaseActivity implements OnClickListener {
    private final String TAG = AlarmClockActivity.class.getSimpleName();
    private MyActivityManager manager;
    private Context mContext;

    public static final int AlarmClockResultAdd = 300;
    public static final int AlarmClockResultEdit = 400;
    public static final int AlarmClockResultDelete = 450;


    private TextView alarm_clock_size;

    private ListView alarm_clock_list;
    private AlarmClockAdapter mAlarmClockAdapter;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_alarm_clock;
    }
    @Override
    protected void initViews() {
        super.initViews();
        mContext = AlarmClockActivity.this;
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        initView();
        initSetAdapter();
        initData();
    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {

        alarm_clock_size = (TextView) findViewById(R.id.alarm_clock_size);
//        lin_no_alarm_clock = (LinearLayout) findViewById(R.id.lin_no_alarm_clock);
        alarm_clock_list = (ListView) findViewById(R.id.alarm_clock_list);

        findViewById(R.id.btAdd).setOnClickListener(this);
        ((TextView) findViewById(R.id.public_head_title)).setText(R.string.colok_title);
    }

    void initSetAdapter() {

        mAlarmClockAdapter = new AlarmClockAdapter(AlarmClockActivity.this);
        alarm_clock_list.setAdapter(mAlarmClockAdapter);
        alarm_clock_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {


                AlarmClockModel myAlarmClockModel = mAlarmClockAdapter.getDevice(arg2);

                MyLog.i(TAG, "发出去的= arg2 = " + arg2);
                MyLog.i(TAG, "发出去的= myAlarmClockModel = " + myAlarmClockModel.toString());


                if (myAlarmClockModel != null) {
                    Intent intent = new Intent(mContext, AddAlarmClockActivity.class);
                    intent.putExtra(IntentConstants.Intent_AlarmClock, myAlarmClockModel);
                    intent.putExtra(IntentConstants.Intent_AlarmClockType, IntentConstants.Intent_AlarmClockEdit);
                    startActivityForResult(intent, AlarmClockResultEdit);
                }


            }
        });

        //listView长按事件
        alarm_clock_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                showDeleteHistoryData(position);
                return true;
            }
        });


    }

    private void initData() {


        updateUi();
    }


    /**
     * 展示当前用户UI
     */
    void updateUi() {

        List<AlarmClockModel> mAlarmClockModelList = RemindeUtils.getAlarmClock(mContext);

        MyLog.i(TAG, "闹钟个数 = " + mAlarmClockModelList.size());


        mAlarmClockAdapter.clear();
        mAlarmClockAdapter.setDeviceList(mAlarmClockModelList);
        mAlarmClockAdapter.notifyDataSetChanged();

        alarm_clock_size.setText(String.valueOf((mAlarmClockModelList.size())));
        if (mAlarmClockModelList.size() >= 5) {
            findViewById(R.id.btAdd).setVisibility(View.GONE);
            alarm_clock_size.setTextColor(getResources().getColor(R.color.white));
        } else {
            findViewById(R.id.btAdd).setVisibility(View.VISIBLE);
            alarm_clock_size.setTextColor(getResources().getColor(R.color.code_color));
        }
        setListViewHeightBasedOnChildren(alarm_clock_list);
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.public_no_bg_head_back:
                manager.popOneActivity(this);
                break;
            case R.id.btAdd:
                if (mAlarmClockAdapter.getCount() >= 5) {
                    AppUtils.showToast(mContext, R.string.alarm_full);
                } else {
                    Intent intent = new Intent(mContext, AddAlarmClockActivity.class);
                    intent.putExtra(IntentConstants.Intent_AlarmClockType, IntentConstants.Intent_AlarmClockAdd);
                    startActivityForResult(intent, AlarmClockResultAdd);
                }
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyLog.i(TAG, "闹钟显示界面=========onActivityResult");

        //添加闹钟
        if (resultCode == AlarmClockResultAdd) {
            MyLog.i(TAG, "闹钟问题 300 添加闹钟");


            AlarmClockModel mAlarmClockModel = data.getParcelableExtra(IntentConstants.Intent_AlarmClock);

            if (mAlarmClockModel != null) {
                MyLog.i(TAG, "闹钟问题 300 添加闹钟 mAlarmClockModel = " + mAlarmClockModel.toString());

                RemindeUtils.insertAlarmClock(mContext, mAlarmClockModel);
                updateUi();
            }

            saveAlarmClockSettoBle();


            //编辑闹钟
        } else if (resultCode == AlarmClockResultEdit) {
            MyLog.i(TAG, "闹钟问题 400 = 编辑闹钟");

            AlarmClockModel mAlarmClockModel = data.getParcelableExtra(IntentConstants.Intent_AlarmClock);

            if (mAlarmClockModel != null) {
                RemindeUtils.updateClockAlarmClock(mContext, mAlarmClockModel);
                updateUi();
            }

            saveAlarmClockSettoBle();

        } else if (resultCode == AlarmClockResultDelete) {
            MyLog.i(TAG, "闹钟问题 450 = 删除闹钟");

            AlarmClockModel mAlarmClockModel = data.getParcelableExtra(IntentConstants.Intent_AlarmClock);
            MyLog.i(TAG, "闹钟问题 mAlarmClockModel = " + mAlarmClockModel.toString());
            if (mAlarmClockModel != null) {
                RemindeUtils.deleteAlarmClock(mContext, mAlarmClockModel);
            }
            updateUi();
            saveAlarmClockSettoBle();

        }
    }

    public void saveAlarmClockSettoBle() {
        //如果蓝牙已连接
        if (HomeActivity.ISBlueToothConnect()) {
            writeRXCharacteristic(BtSerializeation.setDeviceAlarm(RemindeUtils.getAlarmClock(mContext)));
        } else {
            AppUtils.showToast(mContext, R.string.no_connection_notification);
        }

    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }


    /**
     * 弹出删除对话框
     *
     * @param position
     */
    void showDeleteHistoryData(final int position) {

        new android.app.AlertDialog.Builder(AlarmClockActivity.this)
                .setTitle(getString(R.string.dialog_prompt))//设置对话框标题

                .setMessage(getString(R.string.is_delete_clock))//设置显示的内容

                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {//添加确定按钮

                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                        deleteAlarmClock(position);


                    }

                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {//添加返回按钮


            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件

                // TODO Auto-generated method stub


            }

        }).show();//在按键响应事件中显示此对话框
    }

    void deleteAlarmClock(int position) {
        AlarmClockModel mAlarmClockModel = mAlarmClockAdapter.getDevice(position);
        if (mAlarmClockModel != null) {
            RemindeUtils.deleteAlarmClock(mContext, mAlarmClockModel);
            updateUi();
        }

        saveAlarmClockSettoBle();

    }

}
