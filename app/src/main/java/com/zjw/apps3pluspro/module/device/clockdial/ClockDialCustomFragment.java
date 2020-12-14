package com.zjw.apps3pluspro.module.device.clockdial;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.adapter.ClockDialCustomListAdapter;
import com.zjw.apps3pluspro.base.BaseFragment;
import com.zjw.apps3pluspro.module.device.entity.ClockDialCustomModel;

/**
 * 自定义表盘
 */
public class ClockDialCustomFragment extends BaseFragment {
    private final String TAG = ClockDialCustomFragment.class.getSimpleName();
    private ClockDialActivity mClockDialActivity;
    public GridView gvClockDialCustom;
    public ClockDialCustomListAdapter mClockDialCustomListAdapter;
    public static final int RESULT_CODE_CUSTOM = 600;

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }


    @Override
    public View initView() {
        view = View.inflate(context, R.layout.fragment_clock_dial_custom, null);
        gvClockDialCustom = (GridView) view.findViewById(R.id.gvClockDialCustom);
        gvClockDialCustom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                ClockDialCustomModel mClockDialCustomModel = mClockDialCustomListAdapter.getDevice(arg2);
                mClockDialActivity.customToClockDialSendActivity(mClockDialCustomModel);
            }
        });
        return this.view;
    }

    @Override
    public void initData() {

        if (this.getActivity() instanceof ClockDialActivity) {
            mClockDialActivity = (ClockDialActivity) getActivity();
        }

        if (mClockDialActivity.UiType == 1) {
            gvClockDialCustom.setNumColumns(3);
        } else {
            gvClockDialCustom.setNumColumns(2);
        }

        mClockDialCustomListAdapter = new ClockDialCustomListAdapter(context, mClockDialActivity.UiType);

        initListData();
    }


    void initListData() {

        if (mClockDialActivity.deviceShape == 0) {
            mClockDialCustomListAdapter.setDeviceList(
                    ClockDiaConstants.SQUARE_240X240_BIN_NAME,
                    ClockDiaConstants.SQUARE_240X240_BG_NAME,
                    ClockDiaConstants.SQUARE_240X240_TEXT_NAME);
        } else if (mClockDialActivity.deviceShape == 2) {
            mClockDialCustomListAdapter.setDeviceList(
                    ClockDiaConstants.CIRCULAR_240X240_BIN_NAME,
                    ClockDiaConstants.CIRCULAR_240X240_BG_NAME,
                    ClockDiaConstants.SCIRCULAR_240X240_TEXT_NAME);
        }

        mClockDialCustomListAdapter.notifyDataSetChanged();
        gvClockDialCustom.setAdapter(mClockDialCustomListAdapter);
    }


}
