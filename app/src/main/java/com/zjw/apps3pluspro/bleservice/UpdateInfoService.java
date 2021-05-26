package com.zjw.apps3pluspro.bleservice;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

public class UpdateInfoService {
    private final String TAG = UpdateInfoService.class.getSimpleName();

    Dialog progressDialog;
    Handler handler;
    Context context;

    public UpdateInfoService(Context context) {
        this.context = context;
    }

//    public boolean isNeedUpdate(String netVersion) {
//        String new_version = netVersion; // 最新版本的版本号
//        // 获取当前版本号
//        String now_version = "";
//        try {
//            PackageManager packageManager = context.getPackageManager();
//            PackageInfo packageInfo = packageManager.getPackageInfo(
//                    context.getPackageName(), 0);
//            now_version = packageInfo.versionName;
//        } catch (NameNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        MyLog.i(TAG, "新版本和老版本-----------" + new_version + "-----" + now_version);
//        MyLog.i(TAG, "新版本和老版本长度-----------" + new_version.trim().split("\\.").length + "-----" + now_version.trim().split("\\.").length);
//
//        if (TextUtils.isEmpty(new_version) && TextUtils.isEmpty(now_version)) {
//            MyLog.i(TAG, "版本-----------kong");
//            return false;
//        } else if (new_version.split("\\.").length == now_version.split("\\.").length) {
//            for (int i = 0; i < new_version.split("\\.").length; i++) {
//                if (Integer.parseInt(new_version.split("\\.")[i]) > Integer
//                        .parseInt(now_version.split("\\.")[i])) {
//                    MyLog.i(TAG, "版本校验-----------true");
//                    return true;
//                } else if (Integer.parseInt(new_version.split("\\.")[i]) < Integer
//                        .parseInt(now_version.split("\\.")[i])) {
//                    MyLog.i(TAG, "版本校验-----------false");
//                    return false;
//                }
//            }
//        }
//        MyLog.i(TAG, "版本校验-----------false");
//        return false;
//
//
//    }

    public void downLoadFile(final String url, final Dialog pDialog,
                             Handler h) {
        ProgressBar progressBar = pDialog.findViewById(R.id.progress);
        handler = h;
        new Thread() {
            public void run() {

                HttpURLConnection httpURLConnection = null;
                try {

                    URL connectUrl = new URL(url);
                    httpURLConnection = (HttpURLConnection) connectUrl.openConnection();
                    int length = httpURLConnection.getContentLength(); // 获取文件大小
                    progressBar.setMax(length); // 设置进度条的总长度
                    InputStream is = httpURLConnection.getInputStream();
                    FileOutputStream fileOutputStream = null;
                    DecimalFormat format = new DecimalFormat("0.0");
                    if (is != null) {
                        //DFU需要这里下载路径
                        SysUtils.makeRootDirectory(Constants.APK_DIR);
                        File file = new File(Constants.APK_DIR, context.getString(R.string.app_name) + "_update.apk");
                        fileOutputStream = new FileOutputStream(file);
                        // 这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一下就下载完了,
                        // 看不出progressbar的效果。
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            process += ch;
                            progressBar.setProgress(process); // 这里就是关键的实时更新进度了！
                            float all = (float) ((length / 1024.0) / 1024);
                            float percent = (float) ((process / 1024.0) / 1024);
//                            progressDialog.setProgressNumberFormat(String.format("%.1fM/%.1fM", percent, all));
                        }

                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }

                    handler.post(() -> {
                        try {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.cancel();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        checkIsAndroidO();
                    });

                } catch (Exception e) {
                    e.printStackTrace();

                    try {
                        handler.post(() -> {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.cancel();
                            }
                            AppUtils.showToast(context, R.string.net_worse_try_again);
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }
        }.start();

    }

    public static final int INSTALL_PACKAGES_REQUESTCODE = 0xFE;
    public static final int GET_UNKNOWN_APP_SOURCES = 0xFF;
    public boolean checkIsAndroidOneTime;

    void checkIsAndroidO() {
        MyLog.e(TAG, "updateApk check android O");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean b = false;
            try {
                b = context.getPackageManager().canRequestPackageInstalls();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (b) {
                installApk26Plus();
            } else {
                if (checkIsAndroidOneTime) {
                    return;
                }
                checkIsAndroidOneTime = true;
                // 请求安装未知应用来源的权限
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, INSTALL_PACKAGES_REQUESTCODE);
            }
        } else {
            installApk26();
        }
    }

    public void installApk26Plus() {
        MyLog.e(TAG, "installApk26Plus");
        File apkFile = new File(Constants.APK_DIR, context.getString(R.string.app_name) + "_update.apk");
        Uri apkOutputUri = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                apkFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(apkOutputUri, "application/vnd.android.package-archive");
        // 查询所有符合 intent 跳转目标应用类型的应用，注意此方法必须放置在 setDataAndType 方法之后
        List<ResolveInfo> resolveLists = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        // 然后全部授权
        for (ResolveInfo resolveInfo : resolveLists) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, apkOutputUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        context.startActivity(intent);
    }

    public void handlePermissionsResult(int requestCode, int[] grantResults) {
        if (requestCode == INSTALL_PACKAGES_REQUESTCODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                installApk26Plus();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                ((Activity) context).startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES);
            }
        }
    }

    public void handleActivityResult(int requestCode) {
        switch (requestCode) {
            case GET_UNKNOWN_APP_SOURCES:
                checkIsAndroidO();
                break;
        }
    }

    private void installApk26() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            installApk26Plus();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(Constants.APK_DIR, context.getString(R.string.app_name) + "_update.apk")), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    public void downLoadFile2(final String url, final Dialog pDialog,
                              Handler h, final String file_name) {
        ProgressBar progressBar = pDialog.findViewById(R.id.progress);
        handler = h;
        new Thread() {
            public void run() {

                HttpURLConnection httpURLConnection = null;
                try {

                    URL connectUrl = new URL(url);
                    httpURLConnection = (HttpURLConnection) connectUrl.openConnection();
                    int length = httpURLConnection.getContentLength(); // 获取文件大小
                    progressBar.setMax(length); // 设置进度条的总长度
//					InputStream is = entity.getContent();
                    InputStream is = httpURLConnection.getInputStream();
                    FileOutputStream fileOutputStream = null;
                    DecimalFormat format = new DecimalFormat("0.0");
                    if (is != null) {
                        //DFU需要这里下载路径
                        File file = new File(Constants.UPDATE_DEVICE_FILE, file_name);

                        fileOutputStream = new FileOutputStream(file);
                        // 这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一下就下载完了,
                        // 看不出progressbar的效果。
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            process += ch;
                            progressBar.setProgress(process); // 这里就是关键的实时更新进度了！
                            float all = (float) ((length / 1024.0) / 1024);
                            float percent = (float) ((process / 1024.0) / 1024);
//                            progressDialog.setProgressNumberFormat(String.format("%.1fK/%.1fK", percent / 10, all / 10));

                        }

                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
//					down();
                    pDialog.dismiss();
                    Intent intent = new Intent();
                    intent.setAction(BroadcastTools.ACTION_UPDATE_DEVICE_FILE_STATE_SUCCESS);
                    context.sendBroadcast(intent);
                    MyLog.i(TAG, "DFU 下好了");

                }  catch (Exception e) {
                    e.printStackTrace();
                    try {
                        pDialog.dismiss();
                        Intent intent = new Intent();
                        intent.setAction(BroadcastTools.ACTION_UPDATE_DEVICE_FILE_STATE_ERROR);
                        context.sendBroadcast(intent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        }.start();
    }

    public void downLoadNewFile(final String url, final String file_name, final String file_path, final Dialog pDialog) {

        ProgressBar progressBar = pDialog.findViewById(R.id.progress);
        //创建文件夹
        File destDir = new File(file_path);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        new Thread() {
            public void run() {

                HttpURLConnection httpURLConnection = null;
                try {
                    URL connectUrl = new URL(url);
                    httpURLConnection = (HttpURLConnection) connectUrl.openConnection();
                    int length = httpURLConnection.getContentLength(); // 获取文件大小
                    progressBar.setMax(length); // 设置进度条的总长度
                    InputStream is = httpURLConnection.getInputStream();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        //DFU需要这里下载路径
                        File file = new File(file_path, file_name);
                        fileOutputStream = new FileOutputStream(file);
                        // 这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一下就下载完了,
                        // 看不出progressbar的效果。
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            process += ch;
                            progressBar.setProgress(process); // 这里就是关键的实时更新进度了！
                            float all = (float) ((length / 1024.0) / 1024);
                            float percent = (float) ((process / 1024.0) / 1024);
//                            mProgressDialog.setProgressNumberFormat(String.format("%.1fM/%.1fM", percent, all));
                        }
                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
//                    down();
                    pDialog.dismiss();
                    Intent intent = new Intent();
                    intent.setAction(BroadcastTools.ACTION_DOWN_CLOCK_FILE_STATE_SUCCESS);
                    context.sendBroadcast(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        pDialog.dismiss();
                        Intent intent = new Intent();
                        intent.setAction(BroadcastTools.ACTION_DOWN_CLOCK_FILE_STATE_ERROR);
                        context.sendBroadcast(intent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        }.start();
    }

    public void downLoadFileBase(final String url, final Dialog pDialog, Handler h, final String file_name, String action) {
        ProgressBar progressBar = pDialog.findViewById(R.id.progress);
        handler = h;
        new Thread() {
            public void run() {

                HttpURLConnection httpURLConnection = null;
                try {

                    URL connectUrl = new URL(url);
                    httpURLConnection = (HttpURLConnection) connectUrl.openConnection();
                    int length = httpURLConnection.getContentLength(); // 获取文件大小
                    progressBar.setMax(length); // 设置进度条的总长度
//					InputStream is = entity.getContent();
                    InputStream is = httpURLConnection.getInputStream();
                    FileOutputStream fileOutputStream = null;
                    DecimalFormat format = new DecimalFormat("0.0");
                    if (is != null) {
                        //DFU需要这里下载路径
                        File file = new File(Constants.UPDATE_DEVICE_FILE, file_name);

                        fileOutputStream = new FileOutputStream(file);
                        // 这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一下就下载完了,
                        // 看不出progressbar的效果。
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            process += ch;
                            progressBar.setProgress(process); // 这里就是关键的实时更新进度了！
                            float all = (float) ((length / 1024.0) / 1024);
                            float percent = (float) ((process / 1024.0) / 1024);
//                            progressDialog.setProgressNumberFormat(String.format("%.1fK/%.1fK", percent / 10, all / 10));

                        }

                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
//					down();
                    pDialog.dismiss();
                    Intent intent = new Intent();
                    intent.setAction(action);
                    context.sendBroadcast(intent);
                    MyLog.i(TAG, "文件下载 下好了");

                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        pDialog.dismiss();
                        Intent intent = new Intent();
                        intent.setAction(BroadcastTools.ACTION_DOWN_CLOCK_FILE_STATE_ERROR);
                        context.sendBroadcast(intent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }

        }.start();

    }

}
