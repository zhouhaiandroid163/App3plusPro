package com.zjw.apps3pluspro.bleservice;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RemoteController;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.SysUtils;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

import android.service.notification.NotificationListenerService;

/**
 * 推送服务类
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MyNotificationsListenerService extends NotificationListenerService /*implements RemoteController.OnClientUpdateListener*/ {
    private final String TAG = MyNotificationsListenerService.class.getSimpleName();
    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    //==========音乐控制================
    public static RemoteController mRemoteController;
    private RCBinder mBinder = new RCBinder();
    public static RemoteController.OnClientUpdateListener mExternalClientUpdateListener;
    //==========音乐控制================

    public static Context context;

    //    public static final String INTENT_PUT_MSG = "MyNotificationsListenerService_INTENT_PUT_MSG";
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    private static final String PACKAGE_SKYPE1 = "com.skype.raider";
    private static final String PACKAGE_SKYPE2 = "com.skype.rover";
    private static final String PACKAGE_SKYPE3 = "com.skype.insiders";
    private static final String PACKAGE_WHATSAPP = "com.whatsapp";
    private static final String PACKAGE_FACEBOOK1 = "com.facebook.katana";
    private static final String PACKAGE_FACEBOOK2 = "com.facebook.orca";
    private static final String PACKAGE_WECHAT1 = "com.tencent.mm";
    private static final String PACKAGE_WECHAT2 = "com.weipin1.mm";
    private static final String PACKAGE_WECHAT3 = "com.weipin2.mm";
    private static final String PACKAGE_QQ = "com.tencent.mobileqq";
    public static final String PACKAGE_MMS = "com.android.mms";
    public static final String PACKAGE_MMS_VIVO = "com.android.mms.service";
    public static final String PACKAGE_MMS_ONEPLUS = "com.oneplus.mms";
    public static final String PACKAGE_MMS_SAMSUNG = "com.samsung.android.messaging";
    public static final String PACKAGE_MMS_NUBIA = "cn.nubia.mms";
    public static final String PACKAGE_MMS_NOKIA = "com.google.android.apps.messaging";
    private static final String PACKAGE_LINKEDIN = "com.linkedin.android";
    private static final String PACKAGE_TWITTER = "com.twitter.android";
    private static final String PACKAGE_VIBER = "com.viber.voip";
    private static final String PACKAGE_LINE = "jp.naver.line.android";

    private static final String PACKAGE_GMAIL = "com.google.android.gm";
    private static final String PACKAGE_OUTLOOK = "com.microsoft.office.outlook";
    private static final String PACKAGE_INSTAGRAM = "com.instagram.android";
    private static final String PACKAGE_SNAPCHAT = "com.snapchat.android";

    private static final String PACKAGE_ZALO = "com.zing.zalo";
    private static final String PACKAGE_TELEGRAM = "org.telegram.messenger";
    private static final String PACKAGE_YOUTUBE = "com.google.android.youtube";
    private static final String PACKAGE_KAKAOTALK = "com.kakao.talk";
    private static final String PACKAGE_VK = "com.vkontakte.android";
    private static final String PACKAGE_OK = "ru.ok.android";
    private static final String PACKAGE_ICQ = "com.icq.mobile.client";


    public MyNotificationsListenerService() {
    }

    public IBinder onBind(Intent intent) {
        SysUtils.logContentI(TAG, "音乐控制 onBind()");
        // TODO: Return the communication channel to the service.
        return super.onBind(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        SysUtils.logContentI(TAG, "onCreate()");
        startService(new Intent(this, BleService.class));
//        Intent dialogIntent = new Intent(getBaseContext(), HomeActivity.class);
//        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        getApplication().startActivity(dialogIntent);

//        registerRemoteController();
        super.onCreate();
        context = this;
    }

    @Override
    public void onDestroy() {
        SysUtils.logContentI(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        SysUtils.logContentI(TAG, "onLowMemory()");
        super.onLowMemory();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        SysUtils.logContentI(TAG, "onTaskRemoved()");
        super.onTaskRemoved(rootIntent);
    }

    public static boolean isEnabled(Context context) {
        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void openNotificationAccess(Context context) {
        final String ACTION_NOTIFICATION_LISTENER_SETTINGS;
        if (Build.VERSION.SDK_INT >= 22) {
            ACTION_NOTIFICATION_LISTENER_SETTINGS = Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
        } else {
            ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
        }
        context.startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
    }

    private int OutTime = 15 * 1000;
    private long OldSendTime = 0;
    private String OldPageName = "";
    private String oldMessage = "";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Notification notification = sbn.getNotification();
        if (notification == null) {
            return;
        }

        String notificationTitle = notification.extras.getString(Notification.EXTRA_TITLE);
        CharSequence notificationText = notification.extras.getCharSequence(Notification.EXTRA_TEXT);

        String packageName = sbn.getPackageName();

        SysUtils.logContentI(TAG, "package name=" + packageName);
        SysUtils.logContentI(TAG, "notificationTitle=" + notificationTitle);
        SysUtils.logContentI(TAG, "notificationText=" + notificationText);

        if (packageName.equals(PACKAGE_YOUTUBE)) {
            if (notificationTitle == null || notificationTitle.equals("")) {
                notificationTitle = "YouTube";
            }
        }

        if (notification.tickerText != null && notification.tickerText.length() != 0) {
            SysUtils.logContentI(TAG, "tickerText=" + notification.tickerText);
        }

        String postMessage1 = "";
        String postMessage2 = "";

        if (notification.tickerText != null && notification.tickerText.length() != 0) {
            SysUtils.logContentI(TAG, "tickerText=" + notification.tickerText);
            postMessage1 = notification.tickerText.toString();
        } else {
            postMessage1 = notificationTitle + ":" + notificationText;
        }

        postMessage2 = notificationTitle + ":" + notificationText;

        String postMessageReplace1 = postMessage1.replace(":", "");
        postMessageReplace1 = postMessageReplace1.replace("null", "");

        if (packageName.equals(PACKAGE_SKYPE1) ||
                packageName.equals(PACKAGE_SKYPE2) ||
                packageName.equals(PACKAGE_SKYPE3) ||
                packageName.equals(PACKAGE_GMAIL)) {
            if (notificationTitle == null || notificationTitle.equals("") || notificationText == null || notificationText.equals("")) {
                return;
            }
        } else {
            if (postMessageReplace1.equals("") || postMessageReplace1.equals("null") || postMessageReplace1.equals("nullnull") || postMessageReplace1.equals("null:null")) {
                return;
            }
        }

        long curTime = System.currentTimeMillis();


        if (OldPageName.equalsIgnoreCase(packageName) && oldMessage.equalsIgnoreCase(postMessage1)) {
            if (curTime - OldSendTime < OutTime) {
                SysUtils.logContentI(TAG, "retuen 15 S repeat message=" + (curTime - OldSendTime) + "ms");
                return;
            }
        }


        if (packageName.equals(PACKAGE_SKYPE1) || packageName.equals(PACKAGE_SKYPE2) || packageName.equals(PACKAGE_SKYPE3)) {
            if (CheckSkypeNotice(notificationTitle + ":" + notificationText)) {
                SysUtils.logContentI(TAG, "ignore skype message =" + notificationTitle + ":" + notificationText);
                return;
            }
            boolean isOpen = mBleDeviceTools.get_reminde_skype();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "skype message =" + postMessage2);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage2, BleConstant.NotifaceMsgSkype));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_WHATSAPP)) {
            boolean isOpen = mBleDeviceTools.get_reminde_whatsapp();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "whatsapp message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceMsgWhatsapp));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_FACEBOOK1) || packageName.equals(PACKAGE_FACEBOOK2)) {
            if (CheckFaceBookNotice(notificationTitle + ":" + notificationText)) {
                SysUtils.logContentI(TAG, "ignore facebook message =" + postMessage1);
                return;
            }
            boolean isOpen = mBleDeviceTools.get_reminde_facebook();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "facebook message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceMsgFacebook));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_WECHAT1) || packageName.equals(PACKAGE_WECHAT2) || packageName.equals(PACKAGE_WECHAT3)) {
            if (CheckSkypeWX(String.valueOf(notificationText)) || CheckSkypeWX(postMessage1)) {
                SysUtils.logContentI(TAG, "ignore wx message =" + notificationText);
                return;
            }
            boolean isOpen = mBleDeviceTools.get_reminde_wx();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "wx message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceMsgWx));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_QQ)) {
            if (CheckSkypeQQ(String.valueOf(notificationText))) {
                SysUtils.logContentI(TAG, "ignore qq message =" + notificationText);
                return;
            }
            boolean isOpen = mBleDeviceTools.get_reminde_qq();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "qq message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceMsgQq));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_MMS) || packageName.equals(PACKAGE_MMS_VIVO) || packageName.equals(PACKAGE_MMS_ONEPLUS)
                || packageName.equals(PACKAGE_MMS_SAMSUNG) || packageName.equals(PACKAGE_MMS_NUBIA) || packageName.equals(PACKAGE_MMS_NOKIA)) {
            if (CheckSkypeMMs(String.valueOf(notificationTitle)) || CheckSkypeMMs(postMessage1) || CheckSkypeMMs(String.valueOf(notificationText))) {
                SysUtils.logContentI(TAG, "ignore mms message =" + notificationText);
                return;
            }
            boolean isOpen = mBleDeviceTools.get_reminde_mms();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }

            if (isOpen) {
                if (notificationTitle != null && !notificationTitle.equals("") && notificationText != null && !notificationText.equals("")) {
                    try {
                        SysUtils.logContentI(TAG, "mms message 2 =" + postMessage2);
                        sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage2, BleConstant.NotifaceMsgMsg));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        SysUtils.logContentI(TAG, "mms message 1 =" + postMessage1);
                        sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceMsgMsg));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (packageName.equals(PACKAGE_LINKEDIN)) {
            boolean isOpen = mBleDeviceTools.get_reminde_linkedin();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "linkedin message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceMsgLink));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_TWITTER)) {
            boolean isOpen = mBleDeviceTools.get_reminde_twitter();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "twitter message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceMsgTwitter));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_VIBER)) {
            boolean isOpen = mBleDeviceTools.get_reminde_viber();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "viber message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceMsgViber));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_LINE)) {
            boolean isOpen = mBleDeviceTools.get_reminde_line();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "line message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceMsgLine));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_GMAIL)) {
            boolean isOpen = mBleDeviceTools.get_reminde_gmail();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "gmail message =" + postMessage2);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage2, BleConstant.NotifaceGmail));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_OUTLOOK)) {
            boolean isOpen = mBleDeviceTools.get_reminde_outlook();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "outlook message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceOutLook));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_INSTAGRAM)) {
            boolean isOpen = mBleDeviceTools.get_reminde_instagram();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "instagram message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceInstagram));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_SNAPCHAT)) {
            boolean isOpen = mBleDeviceTools.get_reminde_snapchat();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                notificationTitle = notification.extras.getString(Notification.EXTRA_TITLE);
                notificationText = notification.extras.getCharSequence(Notification.EXTRA_TEXT);

                String notificationTextStr = "";

                if (notificationText != null) {
                    notificationTextStr = notificationText.toString();
                }

                if (notification.tickerText != null && notification.tickerText.length() != 0) {
                    postMessage1 = notification.tickerText.toString();
                } else {
                    if (!JavaUtil.checkIsNullnoZero(notificationTitle) && !JavaUtil.checkIsNullnoZero(notificationTextStr)) {
                        postMessage1 = notificationTitle + ":" + notificationText;
                    } else if (!JavaUtil.checkIsNullnoZero(notificationTitle) && JavaUtil.checkIsNullnoZero(notificationTextStr)) {
                        postMessage1 = notificationTitle;
                    } else if (JavaUtil.checkIsNullnoZero(notificationTitle) && !JavaUtil.checkIsNullnoZero(notificationTextStr)) {
                        postMessage1 = notificationTextStr;
                    }
                }

                if (CheckSnapchatNotice(postMessage1)) {
                    SysUtils.logContentI(TAG, "ignore snapchat message =" + postMessage1);
                    return;
                }

                SysUtils.logContentI(TAG, "postMessage1 =" + postMessage1);
                if (JavaUtil.checkIsNullnoZero(postMessage1)) {
                    return;
                }

                try {
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceSnapchat));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_ZALO)) {
            boolean isOpen = mBleDeviceTools.get_reminde_zalo();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "zalo message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceZalo));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_TELEGRAM)) {
            boolean isOpen = mBleDeviceTools.get_reminde_telegram();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "telegram message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceTelegram));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_YOUTUBE)) {
            boolean isOpen = mBleDeviceTools.get_reminde_youtube();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "youtube message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceYouTube));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_KAKAOTALK)) {
            boolean isOpen = mBleDeviceTools.get_reminde_kakao_talk();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "kakaotalk message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceKakaoTalk));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_VK)) {
            boolean isOpen = mBleDeviceTools.get_reminde_vk();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "vk message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceVK));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (packageName.equals(PACKAGE_OK)) {
            boolean isOpen = mBleDeviceTools.get_reminde_ok();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "ok message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceOK));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        } else if (packageName.equals(PACKAGE_ICQ)) {
            boolean isOpen = mBleDeviceTools.get_reminde_icq();
            if (mBleDeviceTools.getMessagePushType() == 1) {
                isOpen = mBleDeviceTools.getOtherMessage(packageName);
            }
            if (isOpen) {
                try {
                    SysUtils.logContentI(TAG, "icq message =" + postMessage1);
                    sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceICQ));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            switch (mBleDeviceTools.getMessagePushType()) {
                case 0:
                    break;
                case 1:
                    if (mBleDeviceTools.getOtherMessage(packageName)) {
                        SysUtils.logContentI(TAG, "other message =" + postMessage1);
                        try {
                            sendBleData(BtSerializeation.notifyMsg(mBleDeviceTools, postMessage1, BleConstant.NotifaceOther));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        OldPageName = packageName;
        oldMessage = postMessage1;
        OldSendTime = System.currentTimeMillis();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        SysUtils.logContentI(TAG, "onNotificationRemoved");
    }


    void sendBleData(byte[] send_data) {
//        BroadcastTools.sendBleAppMsgData(this, send_data);

        Bundle bundle = new Bundle();
        bundle.putString("cmd", "send");
        bundle.putByteArray("params", send_data);
        Intent gattServiceIntent = new Intent(this, BleService.class);
        gattServiceIntent.putExtras(bundle);
        gattServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName name = startService(gattServiceIntent);
    }

    boolean CheckSkypeWX(String count) {
        boolean result = false;
        if (count.contains(getString(R.string.notiface_wx_no_prompt1))
                || count.contains(getString(R.string.notiface_wx_no_prompt2))
                || count.contains(getString(R.string.notiface_wx_no_prompt3))
                || count.contains("WeChat is running")
        ) {
            result = true;
        }
        return result;
    }

    boolean CheckSkypeQQ(String count) {
        boolean result = false;
        if (count.contains(getString(R.string.notiface_no_prompt1))
                || count.contains(getString(R.string.notiface_no_prompt2))
        ) {
            result = true;
        }
        return result;
    }

    boolean CheckSkypeNotice(String count) {
        boolean result = false;
        if (count.equals(getString(R.string.notiface_skype_no_prompt1))
                || count.equals(getString(R.string.notiface_skype_no_prompt2))
                || count.equals(getString(R.string.notiface_skype_no_prompt3))
                || count.equals(getString(R.string.notiface_skype_no_prompt4))
                || count.equals(getString(R.string.notiface_skype_no_prompt5))
                || count.equals(getString(R.string.notiface_skype_no_prompt6))
        ) {
            result = true;
        }
        return result;
    }

    boolean CheckFaceBookNotice(String count) {
        boolean result = false;
        if (count.contains(getString(R.string.notiface_facebook_no_prompt1))
                || count.contains(getString(R.string.notiface_facebook_no_prompt2))
                || count.contains(getString(R.string.notiface_facebook_no_prompt3))
        ) {
            result = true;
        }
        return result;
    }

    boolean CheckSnapchatNotice(String count) {
        boolean result = false;
        if (count.equals(getString(R.string.notiface_snapchat_no_prompt1))
                || count.equals(getString(R.string.notiface_snapchat_no_prompt2))

        ) {
            result = true;
        }
        return result;
    }

    //==============音乐控制===============
    public static void setExternalClientUpdateListener(RemoteController.OnClientUpdateListener externalClientUpdateListener) {
        mExternalClientUpdateListener = externalClientUpdateListener;
    }

//    public void registerRemoteController() {
//
//        SysUtils.logContentI(TAG, "音乐控制 注册服务");
//        mRemoteController = new RemoteController(this, this);
//        boolean registered;
//        try {
//            registered = ((AudioManager) Objects.requireNonNull(getSystemService(AUDIO_SERVICE))).registerRemoteController(mRemoteController);
//        } catch (NullPointerException | SecurityException e) {
//            registered = false;
//        }
//        if (registered) {
//            try {
////                mRemoteController.setArtworkConfiguration(100, 100);
//                mRemoteController.setSynchronizationMode(RemoteController.POSITION_SYNCHRONIZATION_CHECK);
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            }
//        }
////        ((AudioManager) Objects.requireNonNull(getSystemService(AUDIO_SERVICE))).getm
//    }


    public void unregisterRemoteController() {
//        mRemoteController = new RemoteController(this, this);

        if (mRemoteController != null) {

            try {
                ((AudioManager) Objects.requireNonNull(getSystemService(AUDIO_SERVICE))).unregisterRemoteController(mRemoteController);

            } catch (NullPointerException | SecurityException e) {
            }
        }

//        if (mExternalClientUpdateListener != null) {
//            mExternalClientUpdateListener = null;
//        }
    }

//    @Override
//    public void onClientChange(boolean clearing) {
//        if (mExternalClientUpdateListener != null) {
//            mExternalClientUpdateListener.onClientChange(clearing);
//        }
//    }
//
//    @Override
//    public void onClientPlaybackStateUpdate(int state) {
//        if (mExternalClientUpdateListener != null) {
//            mExternalClientUpdateListener.onClientPlaybackStateUpdate(state);
//        }
//    }
//
//    @Override
//    public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs, float speed) {
//        if (mExternalClientUpdateListener != null) {
//            mExternalClientUpdateListener.onClientPlaybackStateUpdate(state, stateChangeTimeMs, currentPosMs, speed);
////           SysUtils.logContentI(TAG, "音乐控制 = stateChangeTimeMs = " + stateChangeTimeMs);
////           SysUtils.logContentI(TAG, "音乐控制 = currentPosMs = " + currentPosMs);
////           SysUtils.logContentI(TAG, "音乐控制 = speed = " + speed);
//        }
//    }
//
//    @Override
//    public void onClientTransportControlUpdate(int transportControlFlags) {
//        if (mExternalClientUpdateListener != null) {
//            mExternalClientUpdateListener.onClientTransportControlUpdate(transportControlFlags);
//        }
//    }
//
//    @Override
//    public void onClientMetadataUpdate(RemoteController.MetadataEditor metadataEditor) {
//        if (mExternalClientUpdateListener != null) {
//            mExternalClientUpdateListener.onClientMetadataUpdate(metadataEditor);
////           SysUtils.logContentI(TAG, "音乐控制 = metadataEditor = " + metadataEditor.toString());
//        }
//    }

    public static boolean sendMusicKeyEvent(int keyCode) {
        if (mRemoteController != null) {
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
            boolean down = mRemoteController.sendMediaKeyEvent(keyEvent);
            keyEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
            boolean up = mRemoteController.sendMediaKeyEvent(keyEvent);
            return down && up;
        }
        return false;
    }

    public class RCBinder extends Binder {
        public MyNotificationsListenerService getService() {
            return MyNotificationsListenerService.this;
        }
    }

    boolean CheckSkypeMMs(String count) {
        boolean result = false;
        if (count == null) {
            result = true;
        }
        if (count.contains("“短信”正在运行") || count.contains("null")
        ) {
            result = true;
        }
        return result;
    }

}
