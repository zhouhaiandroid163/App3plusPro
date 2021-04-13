package com.zjw.apps3pluspro.module.mine.app;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcel;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;
import com.yanzhenjie.album.api.widget.Widget.ButtonStyle;
import com.yanzhenjie.album.util.AlbumUtils;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.mine.user.ProfileActivity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.okhttp.MyOkHttpClient;
import com.zjw.apps3pluspro.network.okhttp.RequestParams;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.AuthorityManagement;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.DialogUtils;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 意见反馈
 */
public class FeedBackActivity extends BaseActivity implements OnClickListener {
    private static final int ACTIVITY_REQUEST_SELECT_PHOTO = 0x01;
    private final String TAG = FeedBackActivity.class.getSimpleName();
    private Context mContext;
    private WaitDialog waitDialog;
    private EditText ed_feedback_advice, et_feedback_email;

    @BindView(R.id.layoutPicture)
    LinearLayout layoutPicture;
    @BindView(R.id.btAgree)
    TextView btAgree;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Bitmap> listBitmaps = new ArrayList<>();
    private ArrayList<String> pathList = new ArrayList<>();

    @Override
    protected int setLayoutId() {
        return R.layout.activity_feedback;
    }

    private boolean isAgree = true;

    @OnClick({R.id.layoutAgree})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layoutAgree:
                isAgree = !isAgree;
                if (isAgree) {
                    btAgree.setBackground(getResources().getDrawable(R.mipmap.agree));
                } else
                    btAgree.setBackground(getResources().getDrawable(R.mipmap.no_agree));
                break;
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = FeedBackActivity.this;
        waitDialog = new WaitDialog(mContext);
        initView();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }

    private void initView() {
        // TODO Auto-generated method stub
        findViewById(R.id.public_head_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.public_head_title)).setText(getString(R.string.title_feedback));
        ed_feedback_advice = (EditText) findViewById(R.id.ed_feedback_advice);
        et_feedback_email = (EditText) findViewById(R.id.et_feedback_email);
        findViewById(R.id.bt_feedback_submit).setOnClickListener(this);

        mLayoutInflater = LayoutInflater.from(this);
        initPictureView();
//        ButtonStyle.Builder xx = ButtonStyle.Builder.setButtonSelector(ContextCompat.getColor(FeedBackActivity.this, R.color.base_activity_bg), ContextCompat.getColor(FeedBackActivity.this, R.color.base_activity_bg));
    }

    private ArrayList<AlbumFile> mAlbumFiles;

    private void initPictureView() {
        layoutPicture.removeAllViews();
        for (int i = 0; i < listBitmaps.size(); i++) {
            RelativeLayout mLinearLayout = (RelativeLayout) mLayoutInflater.inflate(R.layout.picture_layout, null);
            RelativeLayout layoutDelete = mLinearLayout.findViewById(R.id.layoutDelete);
            ImageView ivPicture = mLinearLayout.findViewById(R.id.ivPicture);
            ivPicture.setImageBitmap(listBitmaps.get(i));
            int finalI = i;
            layoutDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listBitmaps.remove(finalI);
                    pathList.remove(finalI);
                    initPictureView();
                }
            });
            layoutPicture.addView(mLinearLayout);
        }
        if (listBitmaps.size() < 4) {
            LinearLayout mLinearLayout = (LinearLayout) mLayoutInflater.inflate(R.layout.picture_add, null);
            ImageView ivAddPicture = mLinearLayout.findViewById(R.id.ivAddPicture);
            ivAddPicture.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AuthorityManagement.verifyStoragePermissions(FeedBackActivity.this)) {
                        MyLog.i(TAG, "SD卡权限 已获取");
                        if (AuthorityManagement.verifyStoragePermissions(FeedBackActivity.this)) {
                            MyLog.i(TAG, "SD卡权限 已获取");
                        } else {
                            MyLog.i(TAG, "SD卡权限 未获取");
                        }

                        if (AuthorityManagement.verifyPhotogrAuthority(FeedBackActivity.this)) {
                            MyLog.i(TAG, "拍照权限 已获取");
                            //  showDialog();
                            Album.image(FeedBackActivity.this)
                                    .multipleChoice()
                                    .camera(true)
                                    .columnCount(4)
                                    .selectCount(4 - listBitmaps.size())
//                                    .checkedList(mAlbumFiles)
                                    .widget(Widget.newDarkBuilder(context)
                                            .navigationBarColor(ContextCompat.getColor(FeedBackActivity.this, R.color.base_activity_bg))
                                            .statusBarColor(ContextCompat.getColor(FeedBackActivity.this, R.color.base_activity_bg))
                                            .toolBarColor(ContextCompat.getColor(FeedBackActivity.this, R.color.base_activity_bg))
                                            .title(getResources().getString(R.string.select_picture))
                                            .build()
                                    )
                                    .onResult(new Action<ArrayList<AlbumFile>>() {
                                        @Override
                                        public void onAction(@NonNull ArrayList<AlbumFile> result) {
                                            mAlbumFiles = result;
                                            List<String> paths = new ArrayList<>();
                                            for (int i = 0; i < mAlbumFiles.size(); i++) {
                                                paths.add(mAlbumFiles.get(i).getPath());
                                            }
                                            if (paths.size() == 0) {
                                                return;
                                            }
                                            for (int i = 0; i < paths.size(); i++) {
                                                Bitmap bitmap = BitmapFactory.decodeFile(paths.get(i), getBitmapOption(2)); //将图片的长和宽缩小味原来的1/2
                                                listBitmaps.add(bitmap);
                                                pathList.add(paths.get(i));
                                            }
                                            initPictureView();
                                        }
                                    })
                                    .onCancel(new Action<String>() {
                                        @Override
                                        public void onAction(@NonNull String result) {
//                                            Toast.makeText(context, "cancel", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .start();
                        } else {
                            MyLog.i(TAG, "拍照权限 未获取");
                        }
                    } else {
                        MyLog.i(TAG, "SD卡权限 未获取");
                    }
                }
            });
            layoutPicture.addView(mLinearLayout);
        }
    }

    //================权限相关===================
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AuthorityManagement.REQUEST_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.i(TAG, "SD卡权限 回调允许");
                } else {
                    MyLog.i(TAG, "SD卡权限 回调拒绝");
                    showSettingDialog(getString(R.string.setting_dialog_storage));
                }
            }
            break;
            case AuthorityManagement.REQUEST_EXTERNAL_CALL_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.i(TAG, "拍照权限 回调允许");
//                    TakingPictures();
                } else {
                    MyLog.i(TAG, "拍照权限 回调拒绝");
                    showSettingDialog(getString(R.string.setting_dialog_call_camera));
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    void showSettingDialog(String title) {
        DialogUtils.showBaseDialog(mContext, mContext.getResources().getString(R.string.dialog_prompt), title,
                mContext.getDrawable(R.drawable.black_corner_bg), new DialogUtils.DialogClickListener() {
                    @Override
                    public void OnOK() {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }

                    @Override
                    public void OnCancel() {

                    }
                }, true, false, getResources().getString(R.string.setting_dialog_setting));
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.public_head_back:
                finish();
                break;
            case R.id.bt_feedback_submit:
                String advice = ed_feedback_advice.getText().toString().trim();
                String email = et_feedback_email.getText().toString().trim();
//                mHandleNetworkListener.FeedBackToServer(TAG, mContext, advice, email);
                checkFeedback(advice, email);
                break;
        }
    }

    void checkFeedback(String advice, String email) {
        if (TextUtils.isEmpty(advice)) {
            AppUtils.showToast(mContext, R.string.feedback_null);
            return;
        }
        if (TextUtils.isEmpty(email)) {
            AppUtils.showToast(mContext, R.string.input_your_email);
            return;
        }
        if (!JavaUtil.isEmail(email)) {
            AppUtils.showToast(mContext, R.string.wrong_input_format_email);
            return;
        }
        if (MyOkHttpClient.getInstance().isConnect(context)) {
            FeedBackToServer(advice, email);
        } else
            AppUtils.showToast(this, R.string.no_net_work);
    }

    private Dialog dialog;

    private void showDialog() {
        View view = getLayoutInflater().inflate(
                R.layout.photo_choose_dialog, null);
        dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        ((Window) window).setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        wl.width = LinearLayout.LayoutParams.MATCH_PARENT;
        wl.height = LinearLayout.LayoutParams.WRAP_CONTENT;

        view.findViewById(R.id.photograph).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.albums).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Album.startAlbum(FeedBackActivity.this, ACTIVITY_REQUEST_SELECT_PHOTO
//                        , 4 - listBitmaps.size()
//                        , ContextCompat.getColor(FeedBackActivity.this, R.color.base_activity_bg)
//                        , ContextCompat.getColor(FeedBackActivity.this, R.color.base_activity_bg));
//                dialog.dismiss();
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        if (listBitmaps != null && listBitmaps.size() > 0) {
            listBitmaps.clear();
        }
        super.onDestroy();
    }

    private BitmapFactory.Options getBitmapOption(int inSampleSize) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    /**
     * 提交意见到服务器
     */
    private Dialog progressDialogDownFile;

    private void FeedBackToServer(String advice, String email) {
        Handler handler = new Handler();
        progressDialogDownFile = DialogUtils.BaseDialogShowProgress(context,
                context.getResources().getString(R.string.loading0),
                context.getResources().getString(R.string.loading0),
                context.getDrawable(R.drawable.black_corner_bg)
        );
        ProgressBar progressBar = progressDialogDownFile.findViewById(R.id.progress);
        try {
            File mFile = new File(Constants.P_LOG_PATH + Constants.P_LOG_APP_RUNNING);
            RequestParams params = new RequestParams();
            if (isAgree) {
                params.put("file", mFile);
            }
            for (int i = 1; i < pathList.size() + 1; i++) {
                File img = new File(pathList.get(i - 1));
                if (img.exists() && img.length() > 0) {
                    params.put("img" + i, img);
                }
            }
            params.put("userId", BaseApplication.getUserId());
            params.put("feedbackContent", advice);
            params.put("feedbackEmail", email);
            params.put("phoneModel", "Android");
            params.put("appMsg", MyUtils.getAppName() + "_" + MyUtils.getAppInfo());
            params.put("phoneSystem", MyUtils.getPhoneModel());
            params.put("appId", "02");
//            params.put("feekBackType", 3);
            NewVolleyRequest.RequestMultiPostRequest(params, new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                @Override
                public void onMySuccess(JSONObject result) {
                    if (waitDialog != null) {
                        waitDialog.close();
                    }
                    MyLog.i(TAG, "请求接口-意见反馈 result = " + result);
                    AppUtils.showToast(mContext, R.string.conmit_ok);
                    if (progressDialogDownFile.isShowing()) {
                        progressDialogDownFile.dismiss();
                    }
                    String strFilePath = Constants.P_LOG_PATH + Constants.P_LOG_APP_RUNNING;
                    File file = new File(strFilePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    finish();
                }

                @Override
                public void onMyError(VolleyError arg0) {
                    if (waitDialog != null) {
                        waitDialog.close();
                    }
                    MyLog.i(TAG, "onMyError result = " + arg0);
                    finish();
                }
            }, RequestJson.feedbackUrl2, (contentLength, mCurrentLength) -> handler.post(() -> {
                progressBar.setMax((int) contentLength);
                progressBar.setProgress(mCurrentLength);
                if (mCurrentLength >= contentLength) {
                    if (progressDialogDownFile.isShowing()) {
                        progressDialogDownFile.dismiss();
                    }
                    waitDialog.show(getString(R.string.loading0));
                }
            }));

        } catch (Exception e) {
            e.printStackTrace();
        }

//        RequestInfo mRequestInfo = RequestJson.feedback(mContext, advice, email, 0);
//        MyLog.i(TAG, "请求接口-意见反馈 mRequestInfo = " + mRequestInfo.toString());
//        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
//                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
//                    @Override
//                    public void onMySuccess(JSONObject result) {
//                        // TODO Auto-generated method stub
//                        MyLog.i(TAG, "请求接口-意见反馈 result = " + result);
//                        waitDialog.close();
//                        OldBean mOldBean = ResultJson.OldBean(result);
//                        if (mOldBean.isRequestSuccess()) {
//                            MyLog.i(TAG, "请求接口-意见反馈 成功");
//                            AppUtils.showToast(mContext, R.string.conmit_ok);
//                            finish();
//                        } else {
//                            MyLog.i(TAG, "请求接口-意见反馈 失败");
//                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onMyError(VolleyError arg0) {
//                        // TODO Auto-generated method stub
//
//
//                        MyLog.i(TAG, "请求接口-意见反馈 请求失败 = message = " + arg0.getMessage());
//                        waitDialog.close();
//                        AppUtils.showToast(mContext, R.string.net_worse_try_again);
//
//                        return;
//                    }
//                });
    }
}
