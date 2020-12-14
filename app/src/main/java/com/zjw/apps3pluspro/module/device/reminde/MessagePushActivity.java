package com.zjw.apps3pluspro.module.device.reminde;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.bleservice.MyNotificationsListenerService;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.AuthorityManagement;
import com.zjw.apps3pluspro.utils.DialogUtils;
import com.zjw.apps3pluspro.utils.PhoneUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;


/**
 * APP提醒
 */
public class MessagePushActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private final String TAG = MessagePushActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    private SwitchCompat sb_notice_qq, sb_notice_wechat, sb_notice_whatsapp, sb_notice_skype, sb_notice_facebook;
    private SwitchCompat sb_notice_linkedin, sb_notice_twitter, sb_notice_viber, sb_notice_line, sbPhone, sbSms;
    private SwitchCompat sb_notice_gmail, sb_notice_outlook, sb_notice_instagrem, sb_notice_snapchat, sb_notice_iosmail;
    private SwitchCompat sb_notice_zalo, sb_notice_telegram, sb_notice_youtube, sb_notice_kakao_talk, sb_notice_vk, sb_notice_ok, sb_notice_icq;

    private LinearLayout notiface_lin_type1;
    private LinearLayout notiface_lin_type3;

    @Override
    protected int setLayoutId() {
        return R.layout.message_push_activity;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = MessagePushActivity.this;
        initView();
        initData();
        updateUi();
        if (!MyNotificationsListenerService.isEnabled(mContext)) {
            String msg = mContext.getString(R.string.allow_notification_authority_tip_left)
                    + mContext.getString(R.string.app_name)
                    + mContext.getString(R.string.allow_notification_authority_tip_right);
            DialogUtils.BaseDialog(mContext, this.getString(R.string.dialog_prompt), msg,mContext.getDrawable(R.drawable.black_corner_bg),new DialogUtils.DialogClickListener() {
                @Override
                public void OnOK() {
                    MyNotificationsListenerService.openNotificationAccess(mContext);
                }

                @Override
                public void OnCancel() {

                }
            });
        }
    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.device_message_notification_title));
        findViewById(R.id.public_head_back).setOnClickListener(this);
        sb_notice_qq = (SwitchCompat) findViewById(R.id.sb_notice_qq);
        sb_notice_wechat = (SwitchCompat) findViewById(R.id.sb_notice_wechat);
        sb_notice_whatsapp = (SwitchCompat) findViewById(R.id.sb_notice_whatsapp);
        sb_notice_skype = (SwitchCompat) findViewById(R.id.sb_notice_skype);
        sb_notice_facebook = (SwitchCompat) findViewById(R.id.sb_notice_facebook);
        sb_notice_linkedin = (SwitchCompat) findViewById(R.id.sb_notice_linkedin);
        sb_notice_twitter = (SwitchCompat) findViewById(R.id.sb_notice_twitter);
        sb_notice_viber = (SwitchCompat) findViewById(R.id.sb_notice_viber);
        sb_notice_line = (SwitchCompat) findViewById(R.id.sb_notice_line);

        sb_notice_gmail = (SwitchCompat) findViewById(R.id.sb_notice_gmail);
        sb_notice_outlook = (SwitchCompat) findViewById(R.id.sb_notice_outlook);
        sb_notice_instagrem = (SwitchCompat) findViewById(R.id.sb_notice_instagrem);
        sb_notice_snapchat = (SwitchCompat) findViewById(R.id.sb_notice_snapchat);
        sb_notice_iosmail = (SwitchCompat) findViewById(R.id.sb_notice_iosmail);

        sb_notice_zalo = (SwitchCompat) findViewById(R.id.sb_notice_zalo);
        sb_notice_telegram = (SwitchCompat) findViewById(R.id.sb_notice_telegram);
        sb_notice_youtube = (SwitchCompat) findViewById(R.id.sb_notice_youtube);
        sb_notice_kakao_talk = (SwitchCompat) findViewById(R.id.sb_notice_kakao_talk);
        sb_notice_vk = (SwitchCompat) findViewById(R.id.sb_notice_vk);
        sb_notice_ok = (SwitchCompat) findViewById(R.id.sb_notice_ok);
        sb_notice_icq = (SwitchCompat) findViewById(R.id.sb_notice_icq);
        sbSms = (SwitchCompat) findViewById(R.id.sbSms);
        sbPhone = (SwitchCompat) findViewById(R.id.sbPhone);

        notiface_lin_type1 = (LinearLayout) findViewById(R.id.notiface_lin_type1);
        notiface_lin_type3 = (LinearLayout) findViewById(R.id.notiface_lin_type3);

    }

    private void initData() {
        sb_notice_qq.setChecked(mBleDeviceTools.get_reminde_qq());
        sb_notice_wechat.setChecked(mBleDeviceTools.get_reminde_wx());
        sb_notice_skype.setChecked(mBleDeviceTools.get_reminde_skype());
        sb_notice_facebook.setChecked(mBleDeviceTools.get_reminde_facebook());
        sb_notice_whatsapp.setChecked(mBleDeviceTools.get_reminde_whatsapp());
        sb_notice_linkedin.setChecked(mBleDeviceTools.get_reminde_linkedin());
        sb_notice_twitter.setChecked(mBleDeviceTools.get_reminde_twitter());
        sb_notice_viber.setChecked(mBleDeviceTools.get_reminde_viber());
        sb_notice_line.setChecked(mBleDeviceTools.get_reminde_line());

        sb_notice_gmail.setChecked(mBleDeviceTools.get_reminde_gmail());
        sb_notice_outlook.setChecked(mBleDeviceTools.get_reminde_outlook());
        sb_notice_instagrem.setChecked(mBleDeviceTools.get_reminde_instagram());
        sb_notice_snapchat.setChecked(mBleDeviceTools.get_reminde_snapchat());
        sb_notice_iosmail.setChecked(mBleDeviceTools.get_reminde_iosmail());


        sb_notice_zalo.setChecked(mBleDeviceTools.get_reminde_zalo());
        sb_notice_telegram.setChecked(mBleDeviceTools.get_reminde_telegram());
        sb_notice_youtube.setChecked(mBleDeviceTools.get_reminde_youtube());
        sb_notice_kakao_talk.setChecked(mBleDeviceTools.get_reminde_kakao_talk());
        sb_notice_vk.setChecked(mBleDeviceTools.get_reminde_vk());
        sb_notice_ok.setChecked(mBleDeviceTools.get_reminde_ok());
        sb_notice_icq.setChecked(mBleDeviceTools.get_reminde_icq());
        sbSms.setChecked(mBleDeviceTools.get_reminde_mms());
        sbPhone.setChecked(mBleDeviceTools.get_reminde_call());


        sb_notice_qq.setOnCheckedChangeListener(this);
        sb_notice_wechat.setOnCheckedChangeListener(this);
        sb_notice_whatsapp.setOnCheckedChangeListener(this);
        sb_notice_skype.setOnCheckedChangeListener(this);
        sb_notice_facebook.setOnCheckedChangeListener(this);
        sb_notice_linkedin.setOnCheckedChangeListener(this);
        sb_notice_twitter.setOnCheckedChangeListener(this);
        sb_notice_viber.setOnCheckedChangeListener(this);
        sb_notice_line.setOnCheckedChangeListener(this);

        sb_notice_gmail.setOnCheckedChangeListener(this);
        sb_notice_outlook.setOnCheckedChangeListener(this);
        sb_notice_instagrem.setOnCheckedChangeListener(this);
        sb_notice_snapchat.setOnCheckedChangeListener(this);
        sb_notice_iosmail.setOnCheckedChangeListener(this);

        sb_notice_zalo.setOnCheckedChangeListener(this);
        sb_notice_telegram.setOnCheckedChangeListener(this);
        sb_notice_youtube.setOnCheckedChangeListener(this);
        sb_notice_kakao_talk.setOnCheckedChangeListener(this);
        sb_notice_vk.setOnCheckedChangeListener(this);
        sb_notice_ok.setOnCheckedChangeListener(this);
        sb_notice_icq.setOnCheckedChangeListener(this);
        sbSms.setOnCheckedChangeListener(this);
        sbPhone.setOnCheckedChangeListener(this);


    }

    void updateUi() {

        notiface_lin_type1.setVisibility(View.GONE);
        notiface_lin_type3.setVisibility(View.GONE);

        if (mBleDeviceTools.get_device_notice_type() == 1) {
            notiface_lin_type1.setVisibility(View.VISIBLE);
        } else if (mBleDeviceTools.get_device_notice_type() == 2) {
            notiface_lin_type1.setVisibility(View.VISIBLE);
        } else if (mBleDeviceTools.get_device_notice_type() == 3) {
            notiface_lin_type1.setVisibility(View.VISIBLE);
            notiface_lin_type3.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.public_head_back:
                finish();
                break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {

            case R.id.sb_notice_qq:
                mBleDeviceTools.set_reminde_qq(isChecked);
                break;

            case R.id.sb_notice_wechat:
                mBleDeviceTools.set_reminde_wx(isChecked);
                break;

            case R.id.sb_notice_whatsapp:
                mBleDeviceTools.set_reminde_whatsapp(isChecked);
                break;

            case R.id.sb_notice_skype:
                mBleDeviceTools.set_reminde_skype(isChecked);
                break;

            case R.id.sb_notice_facebook:
                mBleDeviceTools.set_facebook(isChecked);
                break;

            case R.id.sb_notice_linkedin:
                mBleDeviceTools.set_reminde_linkedin(isChecked);
                break;

            case R.id.sb_notice_twitter:
                mBleDeviceTools.set_reminde_twitter(isChecked);
                break;

            case R.id.sb_notice_viber:
                mBleDeviceTools.set_reminde_viber(isChecked);
                break;

            case R.id.sb_notice_line:
                mBleDeviceTools.set_reminde_line(isChecked);
                break;

            case R.id.sb_notice_gmail:

                mBleDeviceTools.set_reminde_gmail(isChecked);

                break;
            case R.id.sb_notice_outlook:

                mBleDeviceTools.set_reminde_outlook(isChecked);

                break;
            case R.id.sb_notice_instagrem:

                mBleDeviceTools.set_reminde_instagram(isChecked);

                break;
            case R.id.sb_notice_snapchat:

                mBleDeviceTools.set_reminde_snapchat(isChecked);

                break;
            case R.id.sb_notice_iosmail:

                mBleDeviceTools.set_reminde_iosmail(isChecked);

                break;

            case R.id.sb_notice_zalo:

                mBleDeviceTools.set_reminde_zalo(isChecked);

                break;
            case R.id.sb_notice_telegram:

                mBleDeviceTools.set_reminde_telegram(isChecked);

                break;
            case R.id.sb_notice_youtube:

                mBleDeviceTools.set_reminde_youtube(isChecked);

                break;
            case R.id.sb_notice_kakao_talk:

                mBleDeviceTools.set_reminde_kakao_talk(isChecked);

                break;
            case R.id.sb_notice_vk:

                mBleDeviceTools.set_reminde_vk(isChecked);

                break;
            case R.id.sb_notice_ok:

                mBleDeviceTools.set_reminde_ok(isChecked);

                break;
            case R.id.sb_notice_icq:
                mBleDeviceTools.set_reminde_icq(isChecked);
                break;
            case R.id.sbSms:
                mBleDeviceTools.set_reminde_mms(isChecked);
                break;
            case R.id.sbPhone:
                mBleDeviceTools.set_reminde_call(isChecked);
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    // 低版本部分手机触发询问
                    if (isChecked) {
                        PhoneUtil.endCall(this);
                    }
                }
                if (isChecked) {
                    if (AuthorityManagement.verifyMailList(MessagePushActivity.this)) {
                        PhoneUtil.getContactNameFromPhoneBook(this, "123456");
                    }
                    if (AuthorityManagement.verifyPhoneState(MessagePushActivity.this)) {
                        MyLog.i(TAG, "verify Phone State is true");
                    }
                }
                break;
        }
    }
}
