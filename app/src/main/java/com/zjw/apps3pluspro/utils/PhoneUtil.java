package com.zjw.apps3pluspro.utils;

/**
 * Created by zjw on 2017/6/15.
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import androidx.core.app.ActivityCompat;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.zjw.apps3pluspro.module.device.entity.PhoneDtoModel;

public class PhoneUtil {

    private static final String TAG = PhoneUtil.class.getSimpleName();

    /**
     * 挂电话
     * 挂断电话
     *
     * @param context
     */
    public static void endCall(Context context) {
        try {
            SysUtils.logContentW(TAG, "SDK=" + android.os.Build.VERSION.SDK_INT);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                if (tm != null) {
                    boolean success = tm.endCall();
                    SysUtils.logContentW(TAG, "endCall success=" + success);
                }
            } else {
                Object telephonyObject = getTelephonyObject(context);
                if (null != telephonyObject) {
                    Class telephonyClass = telephonyObject.getClass();

                    Method endCallMethod = telephonyClass.getMethod("endCall");
                    endCallMethod.setAccessible(true);

                    endCallMethod.invoke(telephonyObject);
                }
            }
        } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            SysUtils.logContentW(TAG, "endCall=" + e);
            e.printStackTrace();
        }

    }

    private static Object getTelephonyObject(Context context) {
        Object telephonyObject = null;
        try {
            // 初始化iTelephony
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // Will be used to invoke hidden methods with reflection
            // Get the current object implementing ITelephony interface
            Class telManager = telephonyManager.getClass();
            @SuppressLint("SoonBlockedPrivateApi") Method getITelephony = telManager.getDeclaredMethod("getITelephony");
            getITelephony.setAccessible(true);
            telephonyObject = getITelephony.invoke(telephonyManager);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return telephonyObject;
    }


    /**
     * 通过反射调用的方法，接听电话，该方法只在android 2.3之前的系统上有效。
     *
     * @param context
     */
    private static void answerRingingCallWithReflect(Context context) {
        try {
            Object telephonyObject = getTelephonyObject(context);
            if (null != telephonyObject) {
                Class telephonyClass = telephonyObject.getClass();
                Method endCallMethod = telephonyClass.getMethod("answerRingingCall");
                endCallMethod.setAccessible(true);

                endCallMethod.invoke(telephonyObject);
                // ITelephony iTelephony = (ITelephony) telephonyObject;
                // iTelephony.answerRingingCall();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    /**
     * 伪造一个有线耳机插入，并按接听键的广播，让系统开始接听电话。
     *
     * @param context
     */
    private static void answerRingingCallWithBroadcast(Context context) {
        AudioManager localAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //判断是否插上了耳机
        boolean isWiredHeadsetOn = localAudioManager.isWiredHeadsetOn();
        if (!isWiredHeadsetOn) {
            Intent headsetPluggedIntent = new Intent(Intent.ACTION_HEADSET_PLUG);
            headsetPluggedIntent.putExtra("state", 1);
            headsetPluggedIntent.putExtra("microphone", 0);
            headsetPluggedIntent.putExtra("name", "");
            context.sendBroadcast(headsetPluggedIntent);

            Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
            meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
            context.sendOrderedBroadcast(meidaButtonIntent, null);

            Intent headsetUnpluggedIntent = new Intent(Intent.ACTION_HEADSET_PLUG);
            headsetUnpluggedIntent.putExtra("state", 0);
            headsetUnpluggedIntent.putExtra("microphone", 0);
            headsetUnpluggedIntent.putExtra("name", "");
            context.sendBroadcast(headsetUnpluggedIntent);

        } else {
            Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
            meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
            context.sendOrderedBroadcast(meidaButtonIntent, null);
        }
    }

    /**
     * 接听电话
     *
     * @param context
     */
    public static void answerRingingCall(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {  //2.3或2.3以上系统
            answerRingingCallWithBroadcast(context);
        } else {
            answerRingingCallWithReflect(context);
        }
    }

    /**
     * 打电话
     *
     * @param context
     * @param phoneNumber
     */
    @SuppressLint("MissingPermission")
    public static void callPhone(Context context, String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                context.startActivity(callIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 拨电话
     *
     * @param context
     * @param phoneNumber
     */
    public static void dialPhone(Context context, String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                context.startActivity(callIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //静音模式 = true=开，false=关
    public static void SetMuteMode(Context mContext, int state) {
        try {
            AudioManager audioManager = (AudioManager)
                    mContext.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.setRingerMode(state);

                audioManager.getStreamVolume(
                        AudioManager.STREAM_RING);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //静音模式 = true=开，false=关
    public static int getMuteMode(Context mContext) {

        int result = 2;

        try {
            AudioManager audioManager = (AudioManager)
                    mContext.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {

                result = audioManager.getRingerMode();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    public static String contacNameByNumber(Context context, String number) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return number;
        }
        String name = number;
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + number);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"display_name"}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                name = cursor.getString(PHONES_DISPLAY_NAME_INDEX);
                return name;
            }
            cursor.close();
            cursor = null;
        }
        return name;
    }

    public static String getContactNameFromPhoneBook(Context context, String phoneNum) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return phoneNum;
        }

        String contactName = "";
        if (phoneNum.length() == 0)
            return contactName;

        ContentResolver cr = context.getContentResolver();
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.NUMBER};
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNum));

        Cursor pCur = cr.query(uri, projection,
                null, null, null);
        if (pCur != null && pCur.getCount() > 0) {
            if (pCur.moveToFirst()) {
                contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                pCur.close();
            }
        }
        return contactName;
    }


    // 号码
    public final static String NUM = ContactsContract.CommonDataKinds.Phone.NUMBER;
    // 联系人姓名
    public final static String NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

    public static Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

//    //上下文对象
//    private Context context;
//    public PhoneUtil(Context context){
//        this.context = context;
//    }

    //获取所有联系人
    public static List<PhoneDtoModel> getPhone(Context context) {

        //联系人提供者的uri


        List<PhoneDtoModel> PhoneDtoModelList = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(phoneUri, new String[]{NUM, NAME}, null, null, null);
        while (cursor.moveToNext()) {
            PhoneDtoModel phoneDtoModel = new PhoneDtoModel(cursor.getString(cursor.getColumnIndex(NAME)), cursor.getString(cursor.getColumnIndex(NUM)));
            PhoneDtoModelList.add(phoneDtoModel);
        }
        return PhoneDtoModelList;
    }


}
