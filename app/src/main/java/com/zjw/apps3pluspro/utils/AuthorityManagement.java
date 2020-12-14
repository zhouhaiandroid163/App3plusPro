package com.zjw.apps3pluspro.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

/**
 * Created by zjw on 2018/5/22.
 */

public class AuthorityManagement {


    public static final int REQUEST_EXTERNAL_LOCATION = 101;//获取定位权限
    public static final int REQUEST_EXTERNAL_STORAGE = 102;//获取读写SD卡权限
    public static final int REQUEST_EXTERNAL_CALL_CAMERA = 103;//获取调用相机权限
    public static final int REQUEST_EXTERNAL_MAIL_LIST = 104;//获取联系人权限
    public static final int REQUEST_EXTERNAL_PHONE_STATE = 105;//获取来电状态权限
    public static final int REQUEST_EXTERNAL_UNKNOWN_INSTALL = 106;//获取未知来源
    public static final int REQUEST_EXTERNAL_ALL = 107;//所有权限

    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION//Android10.0的定位权限
    };

    /**
     * 获取定位
     *
     * @param activity
     * @return
     */
    public static boolean verifyLocation(Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) /*******below android 6.0*******/ {
            return true;
        }
        int permissionCheck = 0;


        for (int i = 0; i < PERMISSIONS_LOCATION.length; i++) {
            //大于等于Android10.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissionCheck += activity.checkSelfPermission(PERMISSIONS_LOCATION[i]);
            } else {
                //不等于Android10.0的特殊权限
                if (!Manifest.permission.ACCESS_BACKGROUND_LOCATION.equals(PERMISSIONS_LOCATION[i])) {
                    permissionCheck += activity.checkSelfPermission(PERMISSIONS_LOCATION[i]);
                }
            }
        }

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //注册权限0
            ActivityCompat.requestPermissions(activity, PERMISSIONS_LOCATION, REQUEST_EXTERNAL_LOCATION);
            return false;
        } else {//已获得过权限
            //进行蓝牙设备搜索操作
            return true;
        }
    }


    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * 读写SD卡权限
     *
     * @param activity
     * @return
     */
    public static boolean verifyStoragePermissions(Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) /*******below android 6.0*******/ {
            return true;
        }

        int permissionCheck = 0;
        for (int i = 0; i < PERMISSIONS_STORAGE.length; i++) {
            permissionCheck += activity.checkSelfPermission(PERMISSIONS_STORAGE[i]);
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            return false;
        } else {
            return true;
        }
    }


    private static String[] PERMISSIONS_PHOTO = {
            Manifest.permission.CAMERA};

    /**
     * 拍照权限
     *
     * @param activity
     * @return
     */
    public static boolean verifyPhotogrAuthority(Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) /*******below android 6.0*******/ {
            return true;
        }

        int permissionCheck = 0;
        for (int i = 0; i < PERMISSIONS_PHOTO.length; i++) {
            permissionCheck += activity.checkSelfPermission(PERMISSIONS_PHOTO[i]);
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //如果没有授权，则请求授权
            ActivityCompat.requestPermissions(activity, PERMISSIONS_PHOTO, REQUEST_EXTERNAL_CALL_CAMERA);
            return false;
        } else {
            //有授权，直接开启摄像头
            return true;
        }
    }


    private static String[] PERMISSIONS_MAIL_LIST = {
            Manifest.permission.READ_CONTACTS,
//            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS};

    /**
     * 联系人权限
     *
     * @param activity
     * @return
     */
    public static boolean verifyMailList(Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) /*******below android 6.0*******/ {
            return true;
        }
        int permissionCheck = 0;
        for (int i = 0; i < PERMISSIONS_MAIL_LIST.length; i++) {
            permissionCheck += activity.checkSelfPermission(PERMISSIONS_MAIL_LIST[i]);
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //申请权限  第二个参数是一个 数组 说明可以同时申请多个权限
            ActivityCompat.requestPermissions(activity, PERMISSIONS_MAIL_LIST, REQUEST_EXTERNAL_MAIL_LIST);
            return false;

        } else {//已授权
            return true;
        }
    }

    private static String[] PERMISSIONS_PHONE = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.ACTIVITY_RECOGNITION
//            Manifest.permission.WRITE_CALL_LOG,
//            Manifest.permission.USE_SIP,
//            Manifest.permission.PROCESS_OUTGOING_CALLS
    };

    /**
     * 读取通话状态权限
     *
     * @param activity
     * @return
     */
    public static boolean verifyPhoneState(Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) /*******below android 6.0*******/ {
            return true;
        }
        int permissionCheck = 0;
        for (int i = 0; i < PERMISSIONS_PHONE.length; i++) {
            permissionCheck += activity.checkSelfPermission(PERMISSIONS_PHONE[i]);
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //申请权限  第二个参数是一个 数组 说明可以同时申请多个权限
            ActivityCompat.requestPermissions(activity, PERMISSIONS_PHONE, REQUEST_EXTERNAL_PHONE_STATE);
            return false;

        } else {//已授权
            return true;

        }
    }


    private static String[] PERMISSIONS_UNKNOWN_INSTALL = {
            Manifest.permission.REQUEST_INSTALL_PACKAGES};

    /**
     * 读取未知来源安装权限
     *
     * @param activity
     * @return
     */
    public static boolean verifyUnknownInstall(Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) /*******below android 6.0*******/ {
            return true;
        }
        int permissionCheck = 0;
        for (int i = 0; i < PERMISSIONS_PHONE.length; i++) {
            permissionCheck += activity.checkSelfPermission(PERMISSIONS_PHONE[0]);
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //申请权限  第二个参数是一个 数组 说明可以同时申请多个权限
            ActivityCompat.requestPermissions(activity, PERMISSIONS_UNKNOWN_INSTALL, REQUEST_EXTERNAL_UNKNOWN_INSTALL);
            return false;

        } else {//已授权
            return true;

        }

    }


    private static String[] PERMISSIONS_All = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,//Android10.0的定位权限
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.ACTIVITY_RECOGNITION
//            Manifest.permission.WRITE_CALL_LOG,
//            Manifest.permission.USE_SIP,
//            Manifest.permission.PROCESS_OUTGOING_CALLS
    };


    /**
     * 获取所有权限
     *
     * @param activity
     * @return
     */
    public static boolean verifyAll(Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) /*******below android 6.0*******/ {
            return true;
        }
        int permissionCheck = 0;

        for (int i = 0; i < PERMISSIONS_All.length; i++) {
            //大于Android10.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissionCheck += activity.checkSelfPermission(PERMISSIONS_All[i]);
            } else {
                //不等于Android10.0的特殊权限
                if (!Manifest.permission.ACCESS_BACKGROUND_LOCATION.equals(PERMISSIONS_All[i])) {
                    permissionCheck += activity.checkSelfPermission(PERMISSIONS_All[i]);
                }
            }
        }

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //注册权限0
            ActivityCompat.requestPermissions(activity, PERMISSIONS_All, REQUEST_EXTERNAL_ALL);
            return false;
        } else {//已获得过权限
            //进行蓝牙设备搜索操作
            return true;
        }
    }
    public static void permissionSTORAGE(Activity activity, int type) {
        permission11(Permission.Group.STORAGE, activity, type);
    }
    public static void permissionLOCATION(Activity activity) {
        permission11(Permission.Group.LOCATION, activity, -1);
    }
    public static void permission11(String[] permission, Activity activity, int type) {
        XXPermissions.with(activity)
                .permission(permission)
                .request(new OnPermission() {

                    @Override
                    public void hasPermission(List<String> granted, boolean all) {
                        if (all) {
                            Log.i("Permission", "verifyAll11 permission 获取权限成功");
                            if(type == 0){
                                SysUtils.makeRootDirectory(Constants.HOME_DIR);
                                SysUtils.makeRootDirectory(Constants.P_LOG_PATH);
                            }
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean never) {
                        Log.i("Permission", "  denied = " + denied);
                        if (never) {
                            Log.i("Permission", "verifyAll11 permission 被永久拒绝授权，");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                            XXPermissions.gotoPermissionSettings(activity);
                        } else {
                            Log.i("Permission", "verifyAll11 permission 获取权限失败");
                        }
                    }
                });
    }


}
