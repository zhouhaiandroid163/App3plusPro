package com.zjw.apps3pluspro.utils.log;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import com.zjw.apps3pluspro.utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class TraceErrorLog {
    public static String FileName = "a01";
    private static final String LOG_TAG = "a_ble_heart";
    private static File dir = null;
    private static boolean isLogMode = false;
    private static boolean isInit = false;

    public static void setFileName(String fileName) {
        FileName = fileName;
    }

    private static String s = null;

    public static void init(boolean bCreated) {
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        isInit = true;
        try {
            if (sdCardExist) {
                dir = new File(Constants.DEVICE_ERROR_LOG_FILE
                        + "/"
                        + FileName + ".txt"
                );
//	            		+"ecg_test.log");
                if (!dir.exists()) {
                    if (bCreated) {
                        dir.createNewFile();
                        isLogMode = true;
                    } else {
                        isLogMode = false;
                    }
                } else {
                    isLogMode = true;
                }
            } else if (bCreated) {
                isLogMode = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String msg) {
        try {
            if (dir != null) {
                FileOutputStream fos = new FileOutputStream(dir, true);
                msg = msg + "\r\n";
                byte[] bytes = msg.getBytes();
                fos.write(bytes);
                fos.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void e(String msg) {
        if (!isInit) {
            init(true);
        }
        if (isLogMode) {
            writeFile(s + ",,,," + msg);
            Log.e(LOG_TAG, msg);
        }
    }

    public static void w(String msg) {
        if (!isInit) {
            init(true);
        }

        if (isLogMode) {
            writeFile(s + ",,,," + msg);
            Log.w(LOG_TAG, msg);
        }
    }

    public static void i(String msg) {
        if (Constants.SAVE_DEVICE_LOG) {
            if (!isInit) {
                init(true);
            }

            if (isLogMode) {
                s = InputTime();
                writeFile(s + "\n" + msg);
//			writeFile(msg);
//			Log.i(LOG_TAG, "abc = " + s + msg);
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    private static String InputTime() {
        // TODO Auto-generated method stub
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSSS");
//		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss:SSSS");
        s = dateFormat.format(date);
        return s;
    }

}