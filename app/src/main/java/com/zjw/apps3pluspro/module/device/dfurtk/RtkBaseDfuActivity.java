/*
 * Copyright (C) 2015 Realsil Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zjw.apps3pluspro.module.device.dfurtk;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.realsil.sdk.core.utility.DataConverter;
import com.realsil.sdk.dfu.DfuConstants;
import com.realsil.sdk.dfu.image.BinIndicator;
import com.realsil.sdk.dfu.model.BinInfo;
import com.realsil.sdk.dfu.model.DfuConfig;
import com.realsil.sdk.dfu.model.OtaDeviceInfo;
import com.realsil.sdk.dfu.model.OtaModeInfo;
import com.realsil.sdk.dfu.utils.DfuAdapter;
import com.realsil.sdk.dfu.utils.DfuUtils;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.utils.log.MyLog;

/**
 * @author bingshanguxue
 */
public abstract class RtkBaseDfuActivity<T extends DfuAdapter> extends Activity {

    private final String TAG = RtkBaseDfuActivity.class.getSimpleName();
    
    //==============相关参数==============
    public String mDeviceAddress = "";
    public String mDeviceName = "";
    public String mFilePath = "";

    public String getmDeviceAddress() {
        return mDeviceAddress;
    }

    public void setmDeviceAddress(String mDeviceAddress) {
        this.mDeviceAddress = mDeviceAddress;
    }

    public String getmDeviceName() {
        return mDeviceName;
    }

    public void setmDeviceName(String mDeviceName) {
        this.mDeviceName = mDeviceName;
    }

    public String getmFilePath() {
        return mFilePath;
    }

    public void setmFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    //额外密钥产品测试已启用
    public static final String EXTRA_KEY_PRODUCT_TEST_ENABLED = "productEnabled";
    //is产品测试
    protected boolean isProductTest = false;
    //初始状态
    public static final int STATE_INIT = 0x0000;
    //OTA处理
    public static final int STATE_OTA_PROCESSING = 0x0400;
    //中止
    public static final int STATE_ABORTED = 0x0800;
    //错误
    public static final int STATE_OTA_ERROR = STATE_ABORTED | 0x02;
    //处理？
    public static final int STATE_OTA_BANKLINK_PROCESSING = STATE_ABORTED | 0x03;
    //当前状态
    protected int mState = STATE_INIT;

    //==============================================

    //通知进程状态已更改
    protected void notifyProcessStateChanged(int state) {
        mState = state;
        sendMessage(mHandle, MSG_PROCESS_STATE_CHANGED);
    }

    //OTA是否加载中
    public boolean isOtaProcessing() {
        return (mState & STATE_OTA_PROCESSING) == STATE_OTA_PROCESSING;
    }

    //MSG_进程状态已更改
    protected static final int MSG_PROCESS_STATE_CHANGED = 0x02;
    //MSG_目标信息已更改
    protected static final int MSG_TARGET_INFO_CHANGED = 0x03;

    /**
     * 发送消息
     *
     * @param handler 处理器
     * @param what    标志位
     */
    protected void sendMessage(Handler handler, int what) {
        if (handler != null) {
            handler.sendMessage(handler.obtainMessage(what));
        }
    }

    /**
     * 处理数据
     */
    protected final Handler mHandle = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //MSG_目标信息已更改
                case MSG_TARGET_INFO_CHANGED:
                    MyLog.i(TAG, "固件升级 父类 = refresh() = 收到消息 = MSG_TARGET_INFO_CHANGED MSG_目标信息已更改");
                    mBinInfo = null;
                    refresh();
                    if (!isOtaProcessing() && mOtaDeviceInfo != null) {
                        selectWorkMode(false);
                    }
                    break;

                //MSG_进程状态已更改
                case MSG_PROCESS_STATE_CHANGED:
                    MyLog.i(TAG, "固件升级 父类 = refresh() = 收到消息 = MSG_PROCESS_STATE_CHANGED MSG_进程状态已更改");
                    refresh();
                    break;


                default:
                    break;
            }

            super.handleMessage(msg);
        }

    };


    protected T mDfuHelper;
    protected DfuConfig mDfuConfig;
    protected OtaDeviceInfo mOtaDeviceInfo;
    protected BinInfo mBinInfo;
    protected int mProcessState;


    protected DfuConfig getDfuConfig() {
        if (mDfuConfig == null) {
            mDfuConfig = new DfuConfig();
        }
        return mDfuConfig;
    }


    //抽象方法
    public abstract T getDfuHelper();


    //=============生命周期===================

    /**
     * 生命周期-创建
     *
     * @param bundle
     */
    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    /**
     * 生命周期-暂停
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * 生命周期-唤醒
     */
    @Override
    public void onResume() {
        super.onResume();
        //确保设备上已启用蓝牙。如果蓝牙当前未启用，
        //启动意图以显示一个对话框，要求用户授予启用该对话框的权限。
        MyLog.i(TAG, "固件升级 父类 = refresh() = onResume()");
        refresh();
    }

    /**
     * 生命周期-摧毁
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 生命周期-返回
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    //===============加载圈相关============

    /**
     * 启动升级服务
     */
    protected void startOtaService() {
        //是否启用上载文件提示
        MyLog.i(TAG, "固件升级 父类 是否启用上载文件提示 = " + RtkConstant.IS_Upload_File_Prompt_Enabled);
        if (!RtkConstant.IS_Upload_File_Prompt_Enabled) {
            //指示器已满？
            getDfuConfig().setFileIndicator(BinIndicator.INDICATOR_FULL);
            //启动升级
            startOtaProcess();
        }
    }

    /**
     * 启动Ota进程
     */
    public void startOtaProcess() {
        //选择设备为空
        if (getmDeviceAddress() == null || getmDeviceAddress().equals("")) {
            showShortToast(R.string.rtk_toast_no_device);
            return;
        }

        //通知进程状态已更改-   //OTA处理
        notifyProcessStateChanged(STATE_OTA_PROCESSING);

        //基本参数设置
        setDfuConfig();

        //启动程序
        boolean ret = getDfuHelper().startOtaProcedure(getDfuConfig());
        //启动失败
        if (!ret) {
            //操作失败
            showShortToast(R.string.rtk_toast_operation_failed);
            //通知进程状态已更改-   //OTA错误
            notifyProcessStateChanged(STATE_OTA_ERROR);
        }

    }

    /**
     * 基本参数设置
     */
    void setDfuConfig() {
        //================基本参数设置=====================

        MyLog.i(TAG, "固件升级 父类 基本参数设置 ================================start");

        String otaServiceUuid = RtkConstant.OTA_UUID;

        //UUID不为空
        if (!TextUtils.isEmpty(otaServiceUuid)) {
            //设置ota服务UUID
            getDfuConfig().setOtaServiceUuid(otaServiceUuid);
        }


        String aesKey = RtkConstant.AES_KEY;

        if (!TextUtils.isEmpty(aesKey)) {
            getDfuConfig().setSecretKey(DataConverter.hex2Bytes(aesKey));
        }

        //重要-设置mac地址
        getDfuConfig().setAddress(getmDeviceAddress());
        MyLog.i(TAG, "固件升级 父类 mSelectedDevice.getAddress() = " + getmDeviceAddress());

        getDfuConfig().setBreakpointResumeEnabled(RtkConstant.IS_Dfu_Breakpoint_Resume_Enabled);
        getDfuConfig().setAutomaticActiveEnabled(RtkConstant.IS_Dfu_Automatic_Active_Enabled);
        getDfuConfig().setBatteryCheckEnabled(RtkConstant.IS_Dfu_Battery_CheckE_nabled);
        getDfuConfig().setLowBatteryThreshold(RtkConstant.Dfu_Low_Battery_Threshold);
        getDfuConfig().setBatteryLevelFormat(RtkConstant.Dfu_Battery_Level_Format);
        getDfuConfig().setVersionCheckEnabled(RtkConstant.IS_Dfu_Version_Check_Enabled);
        getDfuConfig().setIcCheckEnabled(RtkConstant.IS_DfuChip_Type_Check_Enabled);
        getDfuConfig().setSectionSizeCheckEnabled(RtkConstant.IS_Dfu_Image_Section_Size_Check_Enabled);
        getDfuConfig().setThroughputEnabled(RtkConstant.IS_Dfu_Throughput_Enabled);
        getDfuConfig().setMtuUpdateEnabled(RtkConstant.IS_Dfu_Mtu_Update_Enabled);
        getDfuConfig().setWaitActiveCmdAckEnabled(RtkConstant.IS_Dfu_Active_And_Reset_Ack_Enabled);
        getDfuConfig().setConParamUpdateLatencyEnabled(RtkConstant.IS_Dfu_Connection_Parameter_Latency_Enabled);
        getDfuConfig().setFileLocation(RtkConstant.File_Location);
        getDfuConfig().setFileSuffix(RtkConstant.FILE_SUFFIX);

        //是否已启用Dfu错误操作断开连接
        if (RtkConstant.IS_Dfu_Error_Action_Disconnect_Enabled) {
            getDfuConfig().addErrorAction(DfuConfig.ERROR_ACTION_DISCONNECT);
        } else {
            getDfuConfig().removeErrorAction(DfuConfig.ERROR_ACTION_DISCONNECT);
        }

        if (RtkConstant.IS_DfuErrorActionRefreshDeviceEnabled) {
            getDfuConfig().addErrorAction(DfuConfig.ERROR_ACTION_REFRESH_DEVICE);
        } else {
            getDfuConfig().removeErrorAction(DfuConfig.ERROR_ACTION_REFRESH_DEVICE);
        }

        if (RtkConstant.IS_Dfu_Complete_Action_Remove_Bond_Enabled) {
            getDfuConfig().addCompleteAction(DfuConfig.COMPLETE_ACTION_REMOVE_BOND);
        } else {
            getDfuConfig().removeCompleteAction(DfuConfig.COMPLETE_ACTION_REMOVE_BOND);
        }


        //ota设备不等于空
        if (mOtaDeviceInfo != null) {
            getDfuConfig().setProtocolType(mOtaDeviceInfo.getProtocolType());
        } else {
            getDfuConfig().setProtocolType(0);
        }

        getDfuConfig().setLogLevel(RtkConstant.IS_Dfu_Debug_Enabled ? 1 : 0);


//        is产品测试
        if (isProductTest) {
            getDfuConfig().setBufferCheckLevel(DfuConfig.BUFFER_CHECK_ORIGINAL);
        } else {

            int bufferCheckLevel = RtkConstant.Dfu_Buffer_Check_Level;

            if (bufferCheckLevel <= 0) {
                if (mOtaDeviceInfo != null) {
                    getDfuConfig().setBufferCheckLevel(DfuUtils.getRecommendBuffercheckLevel(mOtaDeviceInfo.icType));
                } else {
                    getDfuConfig().setBufferCheckLevel(DfuConfig.BUFFER_CHECK_ORIGINAL);
                }
            } else {
                getDfuConfig().setBufferCheckLevel(bufferCheckLevel);
            }
        }

        //速度
        int speed = DfuUtils.getControlSpeed(RtkConstant.Dfu_Speed_Control_Level);

        if (speed > 0) {
            getDfuConfig().setSpeedControlEnabled(true);
            getDfuConfig().setControlSpeed(speed);
        } else {
            getDfuConfig().setSpeedControlEnabled(false);
            getDfuConfig().setControlSpeed(0);
        }

        MyLog.i(TAG, "固件升级 父类 基本参数设置 ================================end");

        //================基本参数设置=====================
    }

    /**
     * 在挂起的活动映像上
     */
    public void onPendingActiveImage() {

//        new AlertDialog.Builder(this)
//                .setMessage(R.string.rtk_dfu_toast_active_image)
//                .setPositiveButton(R.string.rtkbt_ota_yes, (dialog, which) -> {
//                    new Thread(() -> getDfuHelper().activeImage(true)).start();
//                    dialog.dismiss();
//                })
//                .setNegativeButton(R.string.rtkbt_ota_no, (dialog, which) -> {
//                    new Thread(() -> getDfuHelper().activeImage(false)).start();
//                    dialog.dismiss();
//                })
//                .setCancelable(false)
//                .show();

        new AlertDialog.Builder(this)
                .setTitle(R.string.dfu_file_init_title).
                setMessage(R.string.rtk_dfu_toast_active_image)
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
            }
        }).show();
    }


    /**
     * 修改工作模式
     *
     * @param workMode
     */
    public void changeWorkMode(int workMode) {
        getDfuConfig().setOtaWorkMode(workMode);
    }


    /**
     * 选择工作模式
     *
     * @param promptEnabled 快速启动 = 普通模式
     */
    protected void selectWorkMode(boolean promptEnabled) {
        OtaModeInfo modeInfo = null;
        if (!promptEnabled) {
            boolean work_modle_prompt = RtkConstant.Work_Mode_Prompt_Enabled;
            MyLog.i(TAG, "固件升级 父类 工作模式 = " + RtkConstant.Work_Mode_Prompt_Enabled);
            if (!work_modle_prompt) {
                //OTA模式静音功能?
                modeInfo = getDfuHelper().getPriorityWorkMode(DfuConstants.OTA_MODE_SILENT_FUNCTION);
            }

            if (modeInfo != null) {
                changeWorkMode(modeInfo.getWorkmode());
            }
        }
    }

    //改变UI-需要重写
    public void refresh() {
    }

    //======================

    public void showShortToast(int var1) {
        Toast.makeText(this, getText(var1), Toast.LENGTH_SHORT).show();
    }

}
