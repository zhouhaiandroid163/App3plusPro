package com.zjw.apps3pluspro.bleservice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;


public class BroadcastTools {

    public static final String APP_NAME = "com.zjw.apps3pluspro";
    //======================蓝牙服务发出===================
    //设备已连接广播
    public final static String ACTION_GATT_CONNECTED = APP_NAME + "_" + "ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_CONNECTING = APP_NAME + "_" + "ACTION_GATT_CONNECTING";
    public final static String ACTION_GATT_CONNECT_TIME_OUT = APP_NAME + "_" + "ACTION_GATT_TIME_OUT";
    public final static String ACTION_GATT_CONNECT_DISCOVER_SERVICES = APP_NAME + "_" + "ACTION_GATT_CONNECT_DISCOVER_SERVICES";
    public final static String ACTION_GATT_CONNECT_BIND_SUCCESS = APP_NAME + "_" + "ACTION_GATT_CONNECT_BIND_SUCCESS";
    public final static String ACTION_GATT_CONNECT_BIND_ERROR = APP_NAME + "_" + "ACTION_GATT_CONNECT_BIND_ERROR";
    //设备断开广播
    public final static String ACTION_GATT_DISCONNECTED = APP_NAME + "_" + "ACTION_GATT_DISCONNECTED";
    //设备断开广播
    //同步时间之后，数据回调完毕广播0x04
    public static final String ACTION_GATT_SYNC_COMPLETE = APP_NAME + "_" + "ACTION_GATT_SYNC_COMPLETE";
    // 同步超时
    public static final String ACTION_SYNC_LOADING = APP_NAME + "_" + "ACTION_SYNC_LOADING";
    // 同步超时
    public static final String ACTION_SYNC_TIME_OUT = APP_NAME + "_" + "ACTION_SYNC_TIME_OUT";
    //设备信息接收完毕广播0x08
    public final static String ACTION_GATT_DEVICE_COMPLETE = APP_NAME + "_" + "ACTION_GATT_DEVICE_COMPLETE";
    //发送ECG数据
    public final static String ACTION_GATT_ECG_DATA = APP_NAME + "_" + "ACTION_GATT_ECG_DATA";
    public final static String INTENT_PUT_ECG_DATA = APP_NAME + "_" + "INTENT_PUT_ECG_DATA";
    public final static String INTENT_PUT_ECG_TUOLUO = APP_NAME + "_" + "INTENT_PUT_ECG_TUOLUO";

    //离线心电传输开始
    public final static String ACTION_GATT_OFF_LINE_ECG_START = APP_NAME + "_" + "ACTION_GATT_OFF_LINE_ECG_START";
    //离线心电传输结束
    public final static String ACTION_GATT_OFF_LINE_ECG_END = APP_NAME + "_" + "ACTION_GATT_OFF_LINE_ECG_END";
    //离线心电质量差
    public final static String ACTION_GATT_OFF_LINE_ECG_NO_OK = APP_NAME + "_" + "ACTION_GATT_OFF_LINE_ECG_NO_OK";

    //发送PPG数据
    public static final String ACTION_GATT_PPG_DATA = APP_NAME + "_" + "ACTION_GATT_PPG_DATA";
    public static final String INTENT_PUT_PPG_DATA = APP_NAME + "_" + "INTENT_PUT_PPG_DATA";

    //一键测量返回心率
    public static final String ACCTION_GATT_HEART_DATA = APP_NAME + "_" + "ACCTION_GATT_HEART_DATA";
    public static final String INTENT_PUT_HEART_DATA = APP_NAME + "_" + "INTENT_PUT_HEART_DATA";

    //屏保传输成功
    public static final String ACTION_GATT_SET_SCREENSAVER_SUCCESS = APP_NAME + "_" + "ACTION_GATT_SET_SCREENSAVER_SUCCESS";
    //屏保传输失败
    public static final String ACTION_GATT_SET_SCREENSAVER_FAIL = APP_NAME + "_" + "ACTION_GATT_SET_SCREENSAVER_FAIL";

    //发送拍照广播，和相机库需要保持一致，不可以修改！
    public static final String TAG_SEND_PHOTO_ACTION = "TAG_SEND_PHOTO_ACTION";
    public static final String TAG_CLOSE_PHOTO_ACTION = "TAG_CLOSE_PHOTO_ACTION";

    //同步时间之后，数据回调完毕广播0x24
    public static final String ACTION_GATT_CALL_DEVICE_INFO = APP_NAME + "_" + "ACTION_GATT_CALL_DEVICE_INFO";

    //表盘信息回调完成
    public final static String ACTION_GATT_DIAL_COMPLETE = APP_NAME + "_" + "ACTION_GATT_DIAL_COMPLETE";

    //======================蓝牙服务接收=====================
    //消息推送
    public static final String ACTION_NOTIFICATION_SEND_DATA = APP_NAME + "_" + "ACTION_NOTIFICATION_SEND_DATA";
    public static final String INTENT_PUT_MSG = APP_NAME + "_" + "INTENT_PUT_MSG";
    //个人信息
    public static final String ACTION_NOTIFICATION_SEND_SET_USER_INFO = APP_NAME + "_" + "ACTION_NOTIFICATION_SEND_SET_USER_INFO";
    //目标步数
    public static final String ACTION_NOTIFICATION_SEND_SET_TARGET_STEP = APP_NAME + "_" + "ACTION_NOTIFICATION_SEND_SET_TARGET_STEP";
    //主题选择
    public static final String ACTION_NOTIFICATION_SEND_SET_DEVICE_THEME = APP_NAME + "_" + "ACTION_NOTIFICATION_SEND_SET_DEVICE_THEME";
    //肤色选择
    public static final String ACTION_NOTIFICATION_SEND_SET_SKIN_COLOUR = APP_NAME + "_" + "ACTION_NOTIFICATION_SEND_SET_SKIN_COLOUR";
    //亮度等级
    public static final String ACTION_NOTIFICATION_SEND_SET_SCREEN_BRIGHESS = APP_NAME + "_" + "ACTION_NOTIFICATION_SEND_SET_SCREEN_BRIGHESS";
    //亮屏时间
    public static final String ACTION_NOTIFICATION_SEND_SET_BRIHNESS_TIME = APP_NAME + "_" + "ACTION_NOTIFICATION_SEND_SET_BRIHNESS_TIME";
    //生理周期
    public static final String ACTION_NOTIFICATION_SEND_SET_CYCLE = APP_NAME + "_" + "ACTION_NOTIFICATION_SEND_SET_CYCLE";


    //======================蓝牙服务-主题=====================
    //B1-准备就绪
    public static final String ACTION_THEME_RECEIVE_READY = APP_NAME + "_" + "ACTION_THEME_RECEIVE_READY";
    //B2-当前块结束
    public static final String ACTION_THEME_RECEIVE_BLOCK_END = APP_NAME + "_" + "ACTION_THEME_RECEIVE_BLOCK_END";
    //B3-补发头
    public static final String ACTION_THEME_RECEIVE_REPAIR_HEAD = APP_NAME + "_" + "ACTION_THEME_RECEIVE_REPAIR_HEAD";
    //    public static final String INTENT_PUT_THEME_REPAIR_SN_LEN = APP_NAME+"_"+"INTENT_PUT_THEME_REPAIR_SN_LEN";
    //B5-响应SN
    public static final String ACTION_THEME_RECEIVE_RESPONSE_SN = APP_NAME + "_" + "ACTION_THEME_RECEIVE_RESPONSE_SN";
    public static final String INTENT_PUT_THEME_REPONSE_SN_NUM_LIST = APP_NAME + "_" + "INTENT_PUT_THEME_REPONSE_SN_NUM_LIST";

    public static final String ACTION_THEME_THEME_RESULT_SUCCESS_SN = APP_NAME + "_" + "ACTION_THEME_THEME_RESULT_SUCCESS_SN";
    public static final String INTENT_PUT_THEME_RESULT_SUCCESS_SN = APP_NAME + "_" + "INTENT_PUT_THEME_RESULT_SUCCESS_SN";

    //B7-通讯录-发送成功
    public static final String ACTION_THEME_RECEIVE_SUCCESS = APP_NAME + "_" + "ACTION_THEME_RECEIVE_SUCCESS";

    //B8-通讯录-发送失败
    public static final String ACTION_THEME_RECEIVE_FAIL = APP_NAME + "_" + "ACTION_THEME_RECEIVE_FAIL";

    //表盘传输失败-中止
    public static final String ACTION_THEME_SUSPENSION_FAIL = APP_NAME + "_" + "ACTION_THEME_SUSPENSION_FAIL";
    public static final String INTENT_PUT_SUSPENSION_FAIL_FIAL_CODE = APP_NAME + "_" + "INTENT_PUT_SUSPENSION_FAIL_FIAL_CODE";

    //表盘传输失败-中止
    public static final String ACTION_THEME_SUSPENSION_INTERVAL = APP_NAME + "_" + "ACTION_THEME_SUSPENSION_INTERVAL";
    public static final String INTENT_PUT_SUSPENSION_INTERVAL_INTERVAL_CODE = APP_NAME + "_" + "INTENT_PUT_SUSPENSION_INTERVAL_INTERVAL_CODE";

    //======================酷狗音乐名称=====================
    //酷狗播放器-歌曲名
    public static final String ACTION_NOTIFICATION_SEND_KUGOU_MUSIC = APP_NAME + "_" + "ACTION_NOTIFICATION_SEND_KUGOU_MUSIC";
    public static final String INTENT_PUT_KUGOU_MUSIC = APP_NAME + "_" + "INTENT_PUT_KUGOU_MUSIC";


    //搜索蓝牙发送设备
    public static final String ACTION_RESULT_BLE_DEVICE_ACTION = APP_NAME + "_" + "TAG_RESULT_BLE_DEVICE_ACTION";
    public static final String BLE_DEVICE = APP_NAME + "_" + "device";

    //DFU升级成功广播
    public static final String ACTION_UPDATE_DEVICE_FILE_STATE_SUCCESS = APP_NAME + "_" + "TAG_UPDATE_DEVICE_FILE_STATE_SUCCESS";

    //LTO升级成功广播
    public static final String ACTION_UPDATE_LTO_SUCCESS = APP_NAME + "_" + "ACTION_UPDATE_LTO_SUCCESS";

    //表盘文件下载成功广播
    public static final String ACTION_DOWN_CLOCK_FILE_STATE_SUCCESS = APP_NAME + "_" + "TAG_DOWN_Clock_FILE_STATE_SUCCESS";

    //系统蓝牙状态发生改变
    public final static String ACTION_BLUE_STATE_CHANGED = APP_NAME + "_" + "ACTION_BLUE_STATE_CHANGED";

    public static final String ACTION_GATT_PROTOSPORT = APP_NAME + "_" + "ACTION_GATT_PROTOSPORT";


    public static final String ACTION_CMD_APP_START = APP_NAME + "_" + "ACTION_CMD_START";
    public static final String ACTION_CMD_DEVICE_CONFIRM = APP_NAME + "_" + "ACTION_CMD_DEVICE_CONFIRM";
    public static final String ACTION_CMD_DEVICE_START = APP_NAME + "_" + "ACTION_CMD_DEVICE_START";
    public static final String ACTION_CMD_APP_CONFIRM = APP_NAME + "_" + "ACTION_CMD_APP_CONFIRM";
    public static final String ACTION_CMD_GET_SPORT = APP_NAME + "_" + "ACTION_CMD_GET_SPORT";
    public static final String ACTION_CMD_DEVICE_TRANSMISSION_DATA = APP_NAME + "_" + "ACTION_CMD_DEVICE_TRANSMISSION_DATA";


    /**
     * 消息推送
     *
     * @param context
     * @param send_data
     */
    public static void sendBleAppMsgData(Context context, byte[] send_data) {

        final Intent intent = new Intent();
        intent.setAction(ACTION_NOTIFICATION_SEND_DATA);
        intent.putExtra(INTENT_PUT_MSG, send_data);
        context.sendBroadcast(intent);
    }


    //用户信息
    public static void sendBleUserinfoData(Context conext) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_NOTIFICATION_SEND_SET_USER_INFO);
        conext.sendBroadcast(intent);
    }

    /**
     * 目标步数
     *
     * @param context
     */
    public static void sendBleTargetStepData(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_NOTIFICATION_SEND_SET_TARGET_STEP);
        context.sendBroadcast(intent);
    }

    /**
     * 主题选择
     *
     * @param context
     */
    public static void sendBleThemeData(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_NOTIFICATION_SEND_SET_DEVICE_THEME);
        context.sendBroadcast(intent);
    }

    /**
     * 发送肤色指令给蓝牙
     */
    public static void sendBleSkinData(Context conetxt) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_NOTIFICATION_SEND_SET_SKIN_COLOUR);
        conetxt.sendBroadcast(intent);
    }

    /**
     * 亮度等级
     */
    public static void sendBleScrenBrighessData(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_NOTIFICATION_SEND_SET_SCREEN_BRIGHESS);
        context.sendBroadcast(intent);
    }

    /**
     * 亮屏时间
     */
    public static void sendBleBrighessTimeData(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_NOTIFICATION_SEND_SET_BRIHNESS_TIME);
        context.sendBroadcast(intent);
    }

    /**
     * 生理周期
     */
    public static void sendBleCycleData(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_NOTIFICATION_SEND_SET_CYCLE);
        context.sendBroadcast(intent);
    }


    public static void broadcastDeviceConnecting(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_CONNECTING);
        context.sendBroadcast(intent);
    }

    /**
     * 连接超时
     *
     * @param context
     */
    public static void broadcastDeviceConnectTimeout(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_CONNECT_TIME_OUT);
        context.sendBroadcast(intent);
    }

    /**
     * 设备已连接
     */
    public static void broadcastDeviceConnected(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_CONNECTED);
        context.sendBroadcast(intent);
    }

    public static void broadcastDeviceConnectedDISCOVERSERVICES(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_CONNECT_DISCOVER_SERVICES);
        context.sendBroadcast(intent);
    }

    public static void broadcastDeviceConnectedBIND_SUCCESS(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_CONNECT_BIND_SUCCESS);
        context.sendBroadcast(intent);
    }

    public static void broadcastDeviceConnectedBIND_ERROR(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_CONNECT_BIND_ERROR);
        context.sendBroadcast(intent);
    }

    /**
     * 设备已断开
     */
    public static void broadcastDeviceDisconnected(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_DISCONNECTED);
        context.sendBroadcast(intent);
    }

    /**
     * 同步数据完成
     */
    public static void broadcastSyncComplete(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_SYNC_COMPLETE);
        context.sendBroadcast(intent);
    }
    public static void broadcastSyncProtoSport(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_PROTOSPORT);
        context.sendBroadcast(intent);
    }

    /**
     * 同步中
     *
     * @param context
     */
    public static void broadcastSyncLoading(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_SYNC_LOADING);
        context.sendBroadcast(intent);
    }

    /**
     * 同步超时
     *
     * @param context
     */
    public static void broadcastSyncTimeOut(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_SYNC_TIME_OUT);
        context.sendBroadcast(intent);
    }

    /**
     * 设备信息回调完成
     */
    public static void broadcastDeviceComplete(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_DEVICE_COMPLETE);
        context.sendBroadcast(intent);
    }


    /**
     * ECG数据
     *
     * @param context
     * @param ecg
     * @param tuoluo
     */
    public static void broadcastEcgData(Context context, int[] ecg, int tuoluo) {
        Intent intent = new Intent();
        intent.setAction(ACTION_GATT_ECG_DATA);
        Bundle b = new Bundle();
        b.putIntArray(INTENT_PUT_ECG_DATA, ecg);
        b.putInt(INTENT_PUT_ECG_TUOLUO, tuoluo);
        intent.putExtras(b);
        context.sendBroadcast(intent);
    }

    /**
     * 离线心电开始传输
     */
    public static void broadcastOffLineEcgStart(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_OFF_LINE_ECG_START);
        context.sendBroadcast(intent);
    }


    /**
     * 离线心电结束传输
     */
    public static void broadcastOffLineEcgEnd(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_OFF_LINE_ECG_END);
        context.sendBroadcast(intent);
    }

    /**
     * 离线心电质量差
     */
    public static void broadcastOffLineEcgNoOk(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_OFF_LINE_ECG_NO_OK);
        context.sendBroadcast(intent);
    }


    /**
     * PPG数据
     *
     * @param context
     * @param ppg
     */
    public static void broadcastPpgData(Context context, int[] ppg) {
        Intent intent = new Intent();
        intent.setAction(ACTION_GATT_PPG_DATA);
        Bundle b = new Bundle();
        b.putIntArray(INTENT_PUT_PPG_DATA, ppg);
        intent.putExtras(b);
        context.sendBroadcast(intent);
    }

    /**
     * 一键测量心率
     *
     * @param context
     * @param heart
     */
    public static void broadcastHeartData(Context context, int heart) {
        Intent intent = new Intent();
        intent.setAction(ACCTION_GATT_HEART_DATA);
        Bundle b = new Bundle();
        b.putInt(INTENT_PUT_HEART_DATA, heart);
        intent.putExtras(b);
        context.sendBroadcast(intent);
    }

    /**
     * 屏保传输成功
     */
    public static void broadcastSetScreensaverSuccess(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_SET_SCREENSAVER_SUCCESS);
        context.sendBroadcast(intent);
    }

    /**
     * 屏保传输成功
     */
    public static void broadcastSetScreensaverFail(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_SET_SCREENSAVER_FAIL);
        context.sendBroadcast(intent);
    }

    /**
     * 摇一摇拍照
     */
    public static void broadcastDevicePhoto(Context context) {
        final Intent intent = new Intent();
        intent.setAction(TAG_SEND_PHOTO_ACTION);
        context.sendBroadcast(intent);
    }


    public static void broadcastThemeReady(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_THEME_RECEIVE_READY);
        Bundle b = new Bundle();
        intent.putExtras(b);
        context.sendBroadcast(intent);
    }

    public static void broadcastThemeBlockEnd(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_THEME_RECEIVE_BLOCK_END);
        Bundle b = new Bundle();
        intent.putExtras(b);
        context.sendBroadcast(intent);
    }

//    public static void broadcastThemeRepairHead(Context context,int miss_sn_size) {
//        Intent intent = new Intent();
//        intent.setAction(ACTION_THEME_RECEIVE_REPAIR_HEAD);
//        Bundle b = new Bundle();
//        b.putInt(INTENT_PUT_THEME_REPAIR_SN_LEN, miss_sn_size);
//        intent.putExtras(b);
//        context.sendBroadcast(intent);
//    }

    public static void broadcastThemeRepairHead(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_THEME_RECEIVE_REPAIR_HEAD);
        Bundle b = new Bundle();
        intent.putExtras(b);
        context.sendBroadcast(intent);
    }

    public static void broadcastThemeResponseSnList(Context context, ArrayList<Integer> sn_list) {
        Intent intent = new Intent();
        intent.setAction(ACTION_THEME_RECEIVE_RESPONSE_SN);
        Bundle b = new Bundle();
        b.putIntegerArrayList(INTENT_PUT_THEME_REPONSE_SN_NUM_LIST, sn_list);
        intent.putExtras(b);
        context.sendBroadcast(intent);
    }

    public static void broadcastThemeResultSuccessSn(Context context, int result_sn_num) {
        Intent intent = new Intent();
        intent.setAction(ACTION_THEME_THEME_RESULT_SUCCESS_SN);
        Bundle b = new Bundle();
        b.putInt(INTENT_PUT_THEME_RESULT_SUCCESS_SN, result_sn_num);
        intent.putExtras(b);
        context.sendBroadcast(intent);
    }

    public static void broadcastThemeRepairSuccess(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_THEME_RECEIVE_SUCCESS);
        Bundle b = new Bundle();
        intent.putExtras(b);
        context.sendBroadcast(intent);
    }

    public static void broadcastThemeRepairFail(Context context) {
        Intent intent = new Intent();
        intent.setAction(ACTION_THEME_RECEIVE_FAIL);
        Bundle b = new Bundle();
        intent.putExtras(b);
        context.sendBroadcast(intent);
    }

    /**
     * @param context
     * @param fial_code 0=未知错误 1=电量低 2=忙碌 3=丢包严重
     */
    public static void broadcastThemeSuspension(Context context, int fial_code) {
        Intent intent = new Intent();
        intent.setAction(ACTION_THEME_SUSPENSION_FAIL);
        Bundle b = new Bundle();
        b.putInt(INTENT_PUT_SUSPENSION_FAIL_FIAL_CODE, fial_code);
        intent.putExtras(b);
        context.sendBroadcast(intent);
    }


    /**
     * @param context
     * @param interval_code 0=成功 1=失败 2=等待
     */
    public static void broadcastThemeInterval(Context context, int interval_code) {
        Intent intent = new Intent();
        intent.setAction(ACTION_THEME_SUSPENSION_INTERVAL);
        Bundle b = new Bundle();
        b.putInt(INTENT_PUT_SUSPENSION_INTERVAL_INTERVAL_CODE, interval_code);
        intent.putExtras(b);
        context.sendBroadcast(intent);
    }

    /**
     * 通讯录-来电设备回调。
     */
    public static void broadcastCallDeviceInfo(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_CALL_DEVICE_INFO);
        context.sendBroadcast(intent);
    }


    /**
     * 表盘信息回调完成
     */
    public static void broadcastDialComplete(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_GATT_DIAL_COMPLETE);
        context.sendBroadcast(intent);
    }


    /**
     * 消息推送
     *
     * @param context
     * @param music_name
     */
    public static void sendKuGouMusicName(Context context, String music_name) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_NOTIFICATION_SEND_KUGOU_MUSIC);
        intent.putExtra(INTENT_PUT_KUGOU_MUSIC, music_name);
        context.sendBroadcast(intent);
    }

}
