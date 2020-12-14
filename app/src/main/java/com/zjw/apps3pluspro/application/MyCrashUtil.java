package com.zjw.apps3pluspro.application;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * Created by zhangzida haha1998@qq.com on 2016-11-15 .
 */

public class MyCrashUtil implements Thread.UncaughtExceptionHandler {
    public static final String TAG = MyCrashUtil.class.getSimpleName();

    //TODO
    private static int MAX_FILE_NUM = 100;//本地最大的存储crash日志文件数量

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashUtil实例
    private static MyCrashUtil INSTANCE = new MyCrashUtil();
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private JSONObject infos = new JSONObject();

    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private String crashToastString;

    private String crashLogFileDir;

    private boolean exited = false;

    /**
     * 保证只有一个CrashUtil实例
     */
    private MyCrashUtil() {
    }

    /**
     * 获取CrashUtil实例 ,单例模式
     */
    public static MyCrashUtil getInstance() {
        return INSTANCE;
    }

    /**
     * * 初始化
     *
     * @param context
     * @param crashLogFileDir  crash的目录位置，传入前确保文件夹已创建
     * @param crashToastString 空的话则不toast
     */
    public void init(Context context, @NonNull String crashLogFileDir, @Nullable String crashToastString) {
        mContext = context;
        this.crashLogFileDir = crashLogFileDir;
        this.crashToastString = crashToastString;

        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashUtil为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);

        //限制文件数量在MAX_FILE_NUM内
        List<File> crashFileList = listFilesInDirWithFilter(new File(crashLogFileDir), new FileFilter() {
            @Override
            public boolean accept(File file) {
                return true;
            }
        }, false);
        if (crashFileList.size() > MAX_FILE_NUM) {
            Collections.sort(crashFileList, new Comparator<File>() {
                @Override
                public int compare(File file, File t1) {
                    if (file.lastModified() > t1.lastModified()) return 1;
                    if (file.lastModified() < t1.lastModified()) return -1;
                    return 0;
                }
            });

            for (int i = 0; i < crashFileList.size() - MAX_FILE_NUM; i++) {
                crashFileList.get(i).delete();
            }
        }
    }

    public static List<File> listFilesInDirWithFilter(final File dir,
                                                      final FileFilter filter,
                                                      final boolean isRecursive) {
        List<File> list = new ArrayList<>();
        if (dir != null && dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length != 0) {
                for (File file : files) {
                    if (filter.accept(file)) {
                        list.add(file);
                    }
                    if (isRecursive && file.isDirectory()) {
                        //noinspection ConstantConditions
                        list.addAll(listFilesInDirWithFilter(file, filter, true));
                    }
                }
            }
        }
        return list;
    }


    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        synchronized (this) {
            if (ex != null) {
//            Log.e("error", Log.getStackTraceString(ex));
            }

            if (exited) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                return;
            }

            if (!handleException(ex) && mDefaultHandler != null) {
                //如果用户没有处理则让系统默认的异常处理器来处理
                mDefaultHandler.uncaughtException(thread, ex);
                return;
            }

            exitApp();
        }
    }

    public void exitApp() {
        //退出程序
        exited = true;
        MyActivityManager.getInstance().finishAllActivity();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //打印出错信息
        MyLog.logError(ex);
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                if (!TextUtils.isEmpty(crashToastString))
                    Toast.makeText(mContext, crashToastString, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        //收集设备参数信息
        collectDeviceInfo(mContext, ex);
        //保存日志文件
        saveCrashInfo2File(ex);

        saveCrashInfo2Server();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx, Throwable ex) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                if (Constants.isBast) {
                    versionName = versionName + Constants.BastVersion;
                }
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            printWriter.close();
            String detail = writer.toString();
            infos.put("error_detail", detail);
            infos.put("error_level", "0");

            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            }

        } catch (Exception e) {
            MyLog.logError(e);
//            Log.e(TAG, "an error occured when collect package info", e);
        }


    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    public void saveCrashInfo2File(Throwable ex) {
        try {
            StringBuilder sb = new StringBuilder();
            String time = formatter.format(new Date());
            sb.append("\r\n\r\n" + time + "\r\n");
            Iterator<String> it = infos.keys();
            while (it.hasNext()) {
                String key = it.next();
                Log.e(TAG, infos.getString(key));
                sb.append(key + "=" + infos.getString(key) + "\n");
            }

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            String result = writer.toString();
            sb.append(result);

            String fileName = time;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                SysUtils.makeRootDirectory(crashLogFileDir);
                SysUtils.logContentE("Crash", "app is crashing");
                FileOutputStream fos = new FileOutputStream(crashLogFileDir + fileName + ".txt");
                fos.write(sb.toString().getBytes());
                fos.close();
            }
        } catch (Exception e) {
            MyLog.logError(e);
        }
    }


    private static List<String> errorMap = new ArrayList<String>();

    //保存错误信息到服务器
    private void saveCrashInfo2Server() {
        try {
            if (errorMap != null && errorMap.size() > 0 && errorMap.contains(SysUtils.stringToMD5(infos.getString("error_detail")))) {
                Log.i(TAG, "same error , not push to service");
            } else {
                Log.i(TAG, "saveCrashInfo2Server else");
                Thread t = new Thread(runnable);
                t.start();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                String errorText = infos.getString("error_detail");
                if (errorText.contains(" com.android.mycamera")) {
                    return;
                }
                RequestInfo mRequestInfo = RequestJson.uploadErrorInfo(mContext, errorText);
                Log.i(TAG, "uploadAppInfo=" + mRequestInfo.toString());
                NewVolleyRequest.RequestPost(mRequestInfo, TAG, new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject result) {
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


}
