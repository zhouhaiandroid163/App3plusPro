package com.zjw.apps3pluspro.module.device.clockdial;

import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.adapter.ClockDialMarketListAdapter;
import com.zjw.apps3pluspro.base.BaseFragment;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.ThemeBean;
import com.zjw.apps3pluspro.network.javabean.ThemeFileBean;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

import java.util.List;

/**
 * 表盘市场
 */
public class ClockDialMarketFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = ClockDialMarketFragment.class.getSimpleName();
    private ClockDialActivity mClockDialActivity;

    private WaitDialog waitDialog;
    private ClockDialMarketListAdapter mClockDialMarketListAdapter;

    private GridView gvClockDialMarket;
    private SwipeRefreshLayout sfClockDialMarket;


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        closeSwipeRefresh();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public View initView() {
        view = View.inflate(context, R.layout.fragment_clock_dial_market, null);

        gvClockDialMarket = (GridView) view.findViewById(R.id.gvClockDialMarket);
        gvClockDialMarket.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                ThemeBean.DataBean.ListBean my_data = mClockDialMarketListAdapter.getDevice(arg2);

                if (my_data != null) {
                    MyLog.i(TAG, "pos = " + arg2 + "  my_data = " + my_data.toString());
                    MyLog.i(TAG, "pos = " + arg2 + "  名称 = " + my_data.getAuthorName());
                    MyLog.i(TAG, "pos = " + arg2 + "  开始传输 ");

                    getThemeFile(my_data.getId(), my_data.getThemeImgUrl());

                }
            }
        });

        sfClockDialMarket = (SwipeRefreshLayout) view.findViewById(R.id.sfClockDialMarket);
        sfClockDialMarket.setColorSchemeColors(Color.GRAY, Color.GREEN);
        sfClockDialMarket.setOnRefreshListener(this);

        return this.view;

    }

    private void closeSwipeRefresh() {
        if (sfClockDialMarket != null && sfClockDialMarket.isRefreshing()) {
            sfClockDialMarket.setRefreshing(false);
        }
    }

    @Override
    public void initData() {
        waitDialog = new WaitDialog(context);

        if (this.getActivity() instanceof ClockDialActivity) {
            mClockDialActivity = (ClockDialActivity) getActivity();
        }
        if (mClockDialActivity.UiType == 1) {
            gvClockDialMarket.setNumColumns(3);
        } else {
            gvClockDialMarket.setNumColumns(2);
        }
        mClockDialMarketListAdapter = new ClockDialMarketListAdapter(context, mClockDialActivity.UiType);
        getPageList();

    }

    private void initData(List<ThemeBean.DataBean.ListBean> my_theme_list) {
        mClockDialMarketListAdapter.setDeviceList(my_theme_list);
        mClockDialMarketListAdapter.notifyDataSetChanged();
        gvClockDialMarket.setAdapter(mClockDialMarketListAdapter);
    }


    /**
     * 获取主题列表
     */
    private void getPageList() {

        RequestInfo mRequestInfo = RequestJson.getThemePageList(
                mClockDialActivity.deviceWidth,
                mClockDialActivity.deviceHeight,
                mClockDialActivity.deviceShape,
                mClockDialActivity.deviceIsHeart,
                mClockDialActivity.deviceClockDialDataSize);

        MyLog.i(TAG, "请求接口-获取主题列表 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {

                        closeSwipeRefresh();

                        MyLog.i(TAG, "请求接口-获取主题列表  result = " + result.toString());

                        ThemeBean mThemeBean = ResultJson.ThemeBean(result);

                        //请求成功
                        if (mThemeBean.isRequestSuccess()) {
                            if (mThemeBean.isUserSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取主题列表 成功");
                                initData(mThemeBean.getData().getList());

                            } else if (mThemeBean.isUserSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取主题列表 无数据");
                                AppUtils.showToast(mContext, R.string.no_data);
                            } else {
                                MyLog.i(TAG, "请求接口-获取主题列表 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取主题列表 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        closeSwipeRefresh();
                        MyLog.i(TAG, "请求接口-获取主题列表 请求失败 = message = " + arg0.getMessage());
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);
                        return;
                    }
                });
    }


    public String nowImgUrl = "";

    /**
     * 获取主题数据
     */
    public void getThemeFile(int theme_id, String img_url) {

        nowImgUrl = img_url;

        String bin_name = mClockDialActivity.deviceScanfTypeIsVer ? "ver.bin" : "hor.bin";

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.getThemeFile(theme_id, bin_name);
//        RequestInfo mRequestInfo = RequestJson.getThemeFile(5, "0");

        MyLog.i(TAG, "请求接口-获取主题数据 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-获取主题数据  result = " + result.toString());

                        ThemeFileBean mThemeFileBean = ResultJson.ThemeFileBean(result);
                        //请求成功
                        if (mThemeFileBean.isRequestSuccess()) {
                            if (mThemeFileBean.isUserSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取主题数据 成功");

                                if (mThemeFileBean.getData().getBinFileName() != null
                                        && mThemeFileBean.getData().getThemeFileUrl() != null
                                        && mThemeFileBean.getData().getMd5Value() != null
                                        && mThemeFileBean.getData().getThemeId() > 0
                                ) {

                                    mClockDialActivity.downThemeFile(mThemeFileBean.getData());

                                }

                            } else if (mThemeFileBean.isUserSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取主题数据 无数据");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            } else {
                                MyLog.i(TAG, "请求接口-获取主题数据 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取主题数据 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        waitDialog.close();
                        MyLog.i(TAG, "请求接口-获取主题数据 请求失败 = message = " + arg0.getMessage());
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);
                        return;
                    }
                });
    }

    @Override
    public void onRefresh() {
        getPageList();
    }
}
