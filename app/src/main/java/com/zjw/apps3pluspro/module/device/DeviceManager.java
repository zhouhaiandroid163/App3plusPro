package com.zjw.apps3pluspro.module.device;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.bleservice.BleConstant;
import com.zjw.apps3pluspro.bleservice.BleService;
import com.zjw.apps3pluspro.bleservice.requestServerTools;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.DialMarketManager;
import com.zjw.apps3pluspro.utils.PageManager;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONObject;

/**
 * Created by android
 * on 2021/1/27
 */
public class DeviceManager {

    private static final String TAG = DeviceManager.class.getSimpleName();
    private static DeviceManager deviceManager;

    public static DeviceManager getInstance() {
        if (deviceManager == null) {
            deviceManager = new DeviceManager();
        }
        return deviceManager;
    }

    private DeviceManager() {
    }

    public interface DeviceManagerListen {
        void onSuccess();

        void onError();
    }

    public void upLoadBindDeviceInfo(String deviceMac, String deviceName, DeviceManagerListen uploadBindDeviceInfoListen) {
        RequestInfo mRequestInfo = RequestJson.uploadBindDeviceInfo(deviceMac, deviceName);
        MyLog.i(TAG, "upLoadBindDeviceInfo = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(BaseApplication.getmContext(), VolleyInterface.mListener, VolleyInterface.mErrorListener) {
            @Override
            public void onMySuccess(JSONObject result) {
                MyLog.i(TAG, "upLoadBindDeviceInfo result = " + result);
                try {
                    String code = result.optString("code");
                    if (code.equalsIgnoreCase(ResultJson.Code_operation_success)) {
                        uploadBindDeviceInfoListen.onSuccess();
                    } else {
                        uploadBindDeviceInfoListen.onError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    uploadBindDeviceInfoListen.onError();
                }
            }

            @Override
            public void onMyError(VolleyError arg0) {
                MyLog.i(TAG, "upLoadBindDeviceInfo arg0 = " + arg0);
                uploadBindDeviceInfoListen.onError();
            }
        });
    }

    public void unBindDeviceInfo(String deviceMac, DeviceManagerListen deviceManagerListen) {
        RequestInfo mRequestInfo = RequestJson.unBindDeviceInfo(deviceMac);
        MyLog.i(TAG, "unBindDeviceInfo = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(BaseApplication.getmContext(), VolleyInterface.mListener, VolleyInterface.mErrorListener) {
            @Override
            public void onMySuccess(JSONObject result) {
                MyLog.i(TAG, "unBindDeviceInfo result = " + result);
                try {
                    String code = result.optString("code");
                    if (code.equalsIgnoreCase(ResultJson.Code_operation_success)) {
                        deviceManagerListen.onSuccess();
                    } else {
                        deviceManagerListen.onError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    deviceManagerListen.onError();
                }
            }

            @Override
            public void onMyError(VolleyError arg0) {
                MyLog.i(TAG, "unBindDeviceInfo arg0 = " + arg0);
                deviceManagerListen.onError();
            }
        });
    }

    public void downLoadBindDevice(DeviceManagerListen deviceManagerListen) {
        RequestInfo mRequestInfo = RequestJson.downLoadBindDevice();
        MyLog.i(TAG, "downLoadBindDevice = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(BaseApplication.getmContext(), VolleyInterface.mListener, VolleyInterface.mErrorListener) {
            @Override
            public void onMySuccess(JSONObject result) {
                MyLog.i(TAG, "downLoadBindDevice result = " + result);
                try {
                    String code = result.optString("code");
                    if (code.equalsIgnoreCase(ResultJson.Code_operation_success)) {
                        JSONObject data = result.getJSONObject("data");
                        int connectionStatus = data.optInt("connectionStatus");
                        String deviceMac = data.optString("deviceMac");
                        String deviceName = data.optString("deviceName");

                        if (connectionStatus == 0 && !TextUtils.isEmpty(deviceMac) && !TextUtils.isEmpty(deviceName)) {
                            BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
                            mBleDeviceTools.set_ble_name(deviceName);
                            mBleDeviceTools.set_ble_mac(deviceMac);

                            BleService.curBleName = deviceName;
                            BleService.curBleAddress = deviceMac;
                            deviceManagerListen.onSuccess();

                            SysUtils.logAppRunning(TAG, "curBleName = " + deviceName + " curBleAddress = " + deviceMac);
                        } else {
                            deviceManagerListen.onError();
                        }
                    } else {
                        deviceManagerListen.onError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    deviceManagerListen.onError();
                }
            }

            @Override
            public void onMyError(VolleyError arg0) {
                MyLog.i(TAG, "downLoadBindDevice arg0 = " + arg0);
                deviceManagerListen.onError();
            }
        });
    }

    public void unBind(Context context, DeviceManagerListen deviceManagerListen) {
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        if (deviceManagerListen == null) {
            deleteDeviceInfo(context);
        } else {
            if (!TextUtils.isEmpty(mBleDeviceTools.get_ble_mac())) {
                unBindDeviceInfo(mBleDeviceTools.get_ble_mac(), deviceManagerListen);
            } else {
                deviceManagerListen.onSuccess();
            }
        }
    }

    private void deleteDeviceInfo(Context context) {
        DialMarketManager.getInstance().themeVersion = "-1";
        UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
        BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
        mBleDeviceTools.setWeatherSyncTime(0);
        mBleDeviceTools.setLastUploadDataServiceTime(0);
        mBleDeviceTools.setLastSyncTime(0);
        mBleDeviceTools.setLastDeviceSportSyncTime(0);
        mBleDeviceTools.set_ble_mac("");
        mBleDeviceTools.set_call_ble_mac("");
        mBleDeviceTools.set_ble_name("");
        mBleDeviceTools.set_call_ble_name("");
        mUserSetTools.set_service_upload_device_info("");
        mBleDeviceTools.set_device_theme_resolving_power_height(0);
        mBleDeviceTools.set_device_theme_resolving_power_width(0);
        BleService.setBlueToothStatus(BleConstant.STATE_DISCONNECTED);
        DialMarketManager.getInstance().clearList();
        PageManager.getInstance().cleanList();
    }
}
