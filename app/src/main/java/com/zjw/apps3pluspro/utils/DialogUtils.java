package com.zjw.apps3pluspro.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.bleservice.MyNotificationsListenerService;
import com.zjw.apps3pluspro.module.device.ScanDeviceActivity;
import com.zjw.apps3pluspro.network.okhttp.MyOkHttpClient;

public class DialogUtils {

    public static void showSystemDialog(final Context mContext, String title, String message, String positive, String negative, final ClickListener clickListener) {
        new android.app.AlertDialog.Builder(mContext)
                .setTitle(title)//设置对话框标题
                .setMessage(message)//设置显示的内容
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        clickListener.positive();
                    }
                })
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int which) {
                        clickListener.negative();
                    }

                }).show();//
    }

    public interface ClickListener {
        void positive();

        void negative();
    }


    /**
     * 显示设置Gps的对话框
     */
    public static void showSettingGps(final Context context) {
        new android.app.AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_prompt))
                .setMessage(context.getString(R.string.open_gps))
                .setPositiveButton(context.getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                }).setNegativeButton(context.getString(R.string.dialog_no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }

        }).show();
    }

    /**
     *
     */
    public static void OpenContinuityDialog(Context context) {
        BaseDialog(context,
                context.getResources().getString(R.string.reminder),
                context.getResources().getString(R.string.device_continuity_measure_tip),
                context.getDrawable(R.drawable.black_corner_bg),
                null, false
        );
    }

    public interface DialogClickListener {
        void OnOK();

        void OnCancel();
    }

    public static Dialog BaseDialog(Context context, String title, String content, Drawable bg, DialogClickListener dialogClickListener) {
       return showBaseDialog(context, title, content, bg, dialogClickListener, true, false, null);
    }

    public static void BaseDialog(Context context, String title, String content, Drawable bg, DialogClickListener dialogClickListener, String okString) {
        showBaseDialog(context, title, content, bg, dialogClickListener, true, false, okString);
    }

    private static void BaseDialog(Context context, String title, String content, Drawable bg, DialogClickListener dialogClickListener, boolean isShowCancel) {
        showBaseDialog(context, title, content, bg, dialogClickListener, isShowCancel, false, null);
    }

    public static Dialog BaseDialogShowProgress(Context context, String title, String content, Drawable bg) {
        return showBaseDialog(context, title, content, bg, null, false, true, null);
    }

    public static Dialog showBaseDialog(Context context, String title, String content, Drawable bg, DialogClickListener dialogClickListener, boolean isShowCancel, boolean isShowProgress, String okString) {
        Dialog baseDialog = new Dialog(context, R.style.progress_dialog);
        baseDialog.setContentView(R.layout.base_dialog_layout);
        baseDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tvTitle = baseDialog.findViewById(R.id.tvTitle);
        TextView tvContent = baseDialog.findViewById(R.id.tvContent);
        TextView tvCancel = baseDialog.findViewById(R.id.tvCancel);
        TextView tvOk = baseDialog.findViewById(R.id.tvOk);
        ProgressBar progress = baseDialog.findViewById(R.id.progress);

        if (!isShowCancel) {
            tvCancel.setVisibility(View.INVISIBLE);
        }
        if (isShowProgress) {
            progress.setVisibility(View.VISIBLE);
            tvCancel.setVisibility(View.GONE);
            tvOk.setVisibility(View.GONE);
            baseDialog.setCancelable(false);
        }

        if (okString != null) {
            tvOk.setText(okString);
        }

        ConstraintLayout mConstraintLayout = (androidx.constraintlayout.widget.ConstraintLayout) tvContent.getParent();
        mConstraintLayout.setBackground(bg);

        tvTitle.setText(title);
        tvContent.setText(content);
        baseDialog.findViewById(R.id.tvOk).setOnClickListener(v -> {
            baseDialog.dismiss();
            if (dialogClickListener != null) {
                dialogClickListener.OnOK();
            }

        });
        tvCancel.setOnClickListener(v -> {
            baseDialog.dismiss();
            if (dialogClickListener != null) {
                dialogClickListener.OnCancel();
            }
        });

        baseDialog.show();
        return baseDialog;
    }

    public static void showSettingGps(Activity activity) {
        showBaseDialog(activity, activity.getResources().getString(R.string.dialog_prompt), activity.getResources().getString(R.string.open_gps),
                activity.getDrawable(R.drawable.black_corner_bg), new DialogClickListener() {
                    @Override
                    public void OnOK() {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivity(intent); // 设置完成后返回到原来的界面
                    }

                    @Override
                    public void OnCancel() {

                    }
                }, true, false, activity.getResources().getString(R.string.setting_dialog_setting));
    }
}
