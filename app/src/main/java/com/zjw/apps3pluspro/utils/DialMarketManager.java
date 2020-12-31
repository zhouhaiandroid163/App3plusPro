package com.zjw.apps3pluspro.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Environment;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.bleservice.UpdateInfoService;
import com.zjw.apps3pluspro.module.device.entity.ThemeDetails;
import com.zjw.apps3pluspro.module.device.entity.ThemeMarketItem;
import com.zjw.apps3pluspro.network.JsonUtils;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.ThemeBean;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by android
 * on 2020/5/14.
 */
public class DialMarketManager {
    private static final String TAG = DialMarketManager.class.getSimpleName();
    private static DialMarketManager dialMarketManager;
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    public static DialMarketManager getInstance() {
        if (dialMarketManager == null) {
            dialMarketManager = new DialMarketManager();
        }
        return dialMarketManager;
    }

    private DialMarketManager() {
    }

    public List<ThemeBean.DataBean.ListBean> dialMarketList = new ArrayList<>();

    int device_width = 0;
    int device_height = 0;
    int device_shape = 0;
    int it_bin_size = 0;
    boolean device_is_heart;
    boolean is_scanf_type_is_ver = false;

    boolean is_send_data = false;
    boolean is_send_fial = false;

    public void clearList() {
        dialMarketList.clear();
    }

    public void getPageList(final GetListOnFinishListen getListOnFinishListen) {
        device_width = mBleDeviceTools.get_device_theme_resolving_power_width();
        device_height = mBleDeviceTools.get_device_theme_resolving_power_height();
        device_shape = mBleDeviceTools.get_device_theme_shape();
        it_bin_size = mBleDeviceTools.get_device_theme_available_space();
        device_is_heart = mBleDeviceTools.get_device_theme_is_support_heart();
        is_scanf_type_is_ver = mBleDeviceTools.get_device_theme_scanning_mode();

        RequestInfo mRequestInfo = RequestJson.getThemePageList(device_width, device_height, device_shape, device_is_heart, it_bin_size);

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(BaseApplication.getmContext(), VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject result) {
                        MyLog.i(TAG, "请求接口-获取主题列表  result = " + result.toString());
                        ThemeBean mThemeBean = ResultJson.ThemeBean(result);
                        //请求成功
                        if (mThemeBean.isRequestSuccess()) {
                            if (mThemeBean.isUserSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取主题列表 成功");

                                dialMarketList.addAll(mThemeBean.getData().getList());
                                if (getListOnFinishListen != null) {
                                    getListOnFinishListen.success();
                                }
                            } else if (mThemeBean.isUserSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取主题列表 无数据");
//                                AppUtils.showToast(mContext, R.string.no_data);
                            } else {
                                MyLog.i(TAG, "请求接口-获取主题列表 请求异常(1)");
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取主题列表 请求异常(0)");
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                    }
                });
    }

    public interface GetListOnFinishListen {
        void success();

        void fail();

        void error();
    }

    public ArrayList<ThemeMarketItem> themeMarketItems = new ArrayList<>();

    public void getMainDialList(GetListOnFinishListen getListOnFinishListen) {
        RequestInfo mRequestInfo = RequestJson.getMainDialList();
        MyLog.i(TAG, "getMainDialList = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(BaseApplication.getmContext(), VolleyInterface.mListener, VolleyInterface.mErrorListener) {
            @Override
            public void onMySuccess(JSONObject result) {
                MyLog.i(TAG, "getMainDialList result = " + result);
                try {
                    String code = result.optString("code");
                    if (code.equalsIgnoreCase(ResultJson.Code_operation_success)) {
                        JSONObject jsonobject = result.optJSONObject("data");
                        JSONArray jsonArray = Objects.requireNonNull(jsonobject).getJSONArray("list");

                        if (themeMarketItems != null && themeMarketItems.size() > 0) {
                            themeMarketItems.clear();
                        }
                        ArrayList<ThemeMarketItem> oldThemeMarketItems = JsonUtils.jsonToBeanList(jsonArray.toString(), ThemeMarketItem.class);
                        for (ThemeMarketItem themeMarketItem : oldThemeMarketItems) {
                            if (themeMarketItem.dialList.size() > 0) {
                                themeMarketItems.add(themeMarketItem);
                            }
                        }

                        if (getListOnFinishListen != null) {
                            getListOnFinishListen.success();
                        }
                    } else {
                        if (getListOnFinishListen != null) {
                            getListOnFinishListen.fail();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getListOnFinishListen != null) {
                        getListOnFinishListen.fail();
                    }
                }
            }

            @Override
            public void onMyError(VolleyError arg0) {
                MyLog.i(TAG, "getMainDialList arg0 = " + arg0);
                if (getListOnFinishListen != null) {
                    getListOnFinishListen.error();
                }
            }
        });
    }

    public ArrayList<ThemeMarketItem.DialInfo> dialInfos;

    public void getMoreDialPageList(GetListOnFinishListen getListOnFinishListen, int pageNum, int dialTypeId) {
        if (dialInfos != null && pageNum == 1) {
            dialInfos.clear();
        }
        RequestInfo mRequestInfo = RequestJson.getMoreDialPageList(pageNum, dialTypeId);
        MyLog.i(TAG, "getMoreDialPageList = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(BaseApplication.getmContext(), VolleyInterface.mListener, VolleyInterface.mErrorListener) {
            @Override
            public void onMySuccess(JSONObject result) {
                MyLog.i(TAG, "getMoreDialPageList result = " + result);
                try {
                    String code = result.optString("code");
                    if (code.equalsIgnoreCase(ResultJson.Code_operation_success)) {
                        JSONObject jsonobject = result.optJSONObject("data");
                        JSONArray jsonArray = Objects.requireNonNull(jsonobject).getJSONArray("list");

                        if (dialInfos != null) {
                            dialInfos.addAll(JsonUtils.jsonToBeanList(jsonArray.toString(), ThemeMarketItem.DialInfo.class));
                        } else {
                            dialInfos = JsonUtils.jsonToBeanList(jsonArray.toString(), ThemeMarketItem.DialInfo.class);
                        }
                        if (getListOnFinishListen != null) {
                            getListOnFinishListen.success();
                        }
                    } else {
                        if (getListOnFinishListen != null) {
                            getListOnFinishListen.error();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getListOnFinishListen != null) {
                        getListOnFinishListen.error();
                    }
                }
            }

            @Override
            public void onMyError(VolleyError arg0) {
                MyLog.i(TAG, "getMoreDialPageList arg0 = " + arg0);
                if (getListOnFinishListen != null) {
                    getListOnFinishListen.error();
                }
            }
        });
    }

    public interface GetDialDetailsListen {
        void success(ThemeDetails themeDetails);

        void error(int code);
    }

    public void getDialDetails(long dialId, GetDialDetailsListen listen) {
        RequestInfo mRequestInfo = RequestJson.getDialDetails(dialId);
        MyLog.i(TAG, "getDialDetails = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(BaseApplication.getmContext(), VolleyInterface.mListener, VolleyInterface.mErrorListener) {
            @Override
            public void onMySuccess(JSONObject result) {
                MyLog.i(TAG, "getDialDetails result = " + result);
                try {
                    String code = result.optString("code");
                    if (code.equalsIgnoreCase(ResultJson.Code_operation_success)) {
                        JSONObject json = result.optJSONObject("data");
                        if (listen != null && json != null) {
                            listen.success(JsonUtils.jsonToBean(json.toString(), ThemeDetails.class));
                        }
                    } else {
                        listen.error(Integer.parseInt(code));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    listen.error(-1);
                }
            }

            @Override
            public void onMyError(VolleyError arg0) {
                listen.error(-1);
                MyLog.i(TAG, "getDialDetails arg0 = " + arg0);
            }
        });
    }


    public void downLoadThemeFile(Activity activity, String themeName, String url) {
        if (AuthorityManagement.verifyStoragePermissions(activity)) {
            MyLog.i(TAG, "SD卡权限 已获取");
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                MyLog.i(TAG, "SD卡权限 支持");
                Dialog progressDialogDownFile;
                progressDialogDownFile = DialogUtils.BaseDialogShowProgress(activity,
                        activity.getResources().getString(R.string.download_title),
                        activity.getResources().getString(R.string.loading0),
                        activity.getDrawable(R.drawable.black_corner_bg)
                );
                new UpdateInfoService(activity).downLoadNewFile(url, themeName, Constants.DOWN_THEME_FILE, progressDialogDownFile);
            } else {
                MyLog.i(TAG, "SD卡权限 不支持");
                AppUtils.showToast(activity, R.string.sd_card);
            }
        } else {
            MyLog.i(TAG, "SD卡权限 未获取");
            AppUtils.showToast(activity, R.string.sd_card);
        }
    }

    public static final int uploadDialDownloadRecordingType1_downloading = 1;
    public static final int uploadDialDownloadRecordingType2_transport = 2;
    public static final int uploadDialDownloadRecordingType3_success = 3;
    public static final int uploadDialDownloadRecordingType4_error = 4;

    public void uploadDialDownloadRecording(long dialId, int dataType, Context context) {
        RequestInfo mRequestInfo = RequestJson.uploadDialDownloadRecording(dialId, dataType, context);
        MyLog.i(TAG, "uploadDialDownloadRecording = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(BaseApplication.getmContext(), VolleyInterface.mListener, VolleyInterface.mErrorListener) {
            @Override
            public void onMySuccess(JSONObject result) {
                MyLog.i(TAG, "uploadDialDownloadRecording result = " + result);
            }

            @Override
            public void onMyError(VolleyError arg0) {
                MyLog.i(TAG, "uploadDialDownloadRecording arg0 = " + arg0);
            }
        });
    }
}
