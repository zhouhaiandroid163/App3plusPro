package com.zjw.apps3pluspro.module.device.reminde;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.bleservice.MyNotificationsListenerService;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MessagePushOtherActivity extends BaseActivity {

    private static final String TAG = MessagePushOtherActivity.class.getSimpleName();
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected int setLayoutId() {
        return R.layout.message_push_other_activity;
    }

    private ArrayList<MessagePushOtherRecyclerAdapter.OtherApp> mDatas = new ArrayList<>();
    private WaitDialog waitDialog;

    @Override
    protected void initViews() {
        super.initViews();
        setTvTitle(R.string.device_message_notification_title);
        waitDialog = new WaitDialog(context);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MessagePushOtherRecyclerAdapter(this, mDatas);
    }

    private MessagePushOtherRecyclerAdapter adapter;

    @Override
    protected void initDatas() {
        super.initDatas();
        waitDialog.show(getResources().getString(R.string.ignored));
//        PackageManager pm = this.getPackageManager();
//        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
//        for (PackageInfo mPackageInfo : packages) {
//            String appname = mPackageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
////            tmpInfo.icon = mPackageInfo.applicationInfo.loadIcon(getPackageManager());
//            Log.i("xxx", "   " + mPackageInfo.packageName + "----" + appname);
//        }

        new Thread(() -> {
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveinfoList = getPackageManager().queryIntentActivities(resolveIntent, 0);
            for (ResolveInfo resolveInfo : resolveinfoList) {
                Log.i(TAG, "   " + resolveInfo.activityInfo.packageName + "----" + resolveInfo.loadLabel(getPackageManager()).toString());
                if (resolveInfo.activityInfo != null && resolveInfo.activityInfo.packageName != null) {
                    if (!resolveInfo.activityInfo.packageName.equals(MyNotificationsListenerService.PACKAGE_MMS)
                            && !resolveInfo.activityInfo.packageName.equals(MyNotificationsListenerService.PACKAGE_MMS_VIVO)
                            && !resolveInfo.activityInfo.packageName.equals(MyNotificationsListenerService.PACKAGE_MMS_ONEPLUS)
                            && !resolveInfo.activityInfo.packageName.equals(MyNotificationsListenerService.PACKAGE_MMS_SAMSUNG)
                            && !resolveInfo.activityInfo.packageName.equals(MyNotificationsListenerService.PACKAGE_MMS_NUBIA)
                            && !resolveInfo.activityInfo.packageName.equals(MyNotificationsListenerService.PACKAGE_MMS_NOKIA)
                            && !resolveInfo.activityInfo.packageName.equals("com.android.contacts")
                    ) {
                        MessagePushOtherRecyclerAdapter.OtherApp mOtherApp = new MessagePushOtherRecyclerAdapter.OtherApp();
                        mOtherApp.appName = resolveInfo.loadLabel(getPackageManager()).toString();
                        mOtherApp.packageName = resolveInfo.activityInfo.packageName;
                        mOtherApp.icon = resolveInfo.activityInfo.loadIcon(getPackageManager());
                        mDatas.add(mOtherApp);
                    }
                }
            }
            runOnUiThread(() -> {
                waitDialog.close(300);
                mRecyclerView.setAdapter(adapter);
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        waitDialog.dismiss();
        super.onDestroy();
    }
}
