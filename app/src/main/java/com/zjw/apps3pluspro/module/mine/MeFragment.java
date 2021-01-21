package com.zjw.apps3pluspro.module.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.lidroid.xutils.BitmapUtils;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseFragment;
import com.zjw.apps3pluspro.bleservice.BleTools;
import com.zjw.apps3pluspro.module.device.BleConnectProblemActivity;
import com.zjw.apps3pluspro.module.mine.app.AboutActivity;
import com.zjw.apps3pluspro.module.mine.app.CommonProblemActivity;
import com.zjw.apps3pluspro.module.mine.user.ProfileActivity;
import com.zjw.apps3pluspro.module.mine.user.TargetSettingActivity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.javabean.UserBean;
import com.zjw.apps3pluspro.network.okhttp.MyOkHttpClient;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.DialogUtils;
import com.zjw.apps3pluspro.utils.FileUtil;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.CircleImageView;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by android
 * on 2020/5/11.
 */
public class MeFragment extends BaseFragment {

    private static final String TAG = MeFragment.class.getSimpleName();
    @BindView(R.id.ivMyHead)
    CircleImageView ivMyHead;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvUserId)
    TextView tvUserId;
    @BindView(R.id.layoutCommonProblem)
    LinearLayout layoutCommonProblem;
    @BindView(R.id.layoutMore)
    LinearLayout layoutMore;
    @BindView(R.id.layoutPen)
    LinearLayout layoutPen;
    private HomeActivity homeActivity;
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();

    private int xxxx = 0;

    @Override
    public View initView() {
        homeActivity = (HomeActivity) this.getActivity();
        view = View.inflate(context, R.layout.me_fragment_layout, null);

        return view;
    }

    @Override
    public void initData() {
        questGetUserInfo();
    }

    @OnClick({R.id.ivMyHead, R.id.tvUserName, R.id.tvUserId, R.id.layoutCommonProblem, R.id.layoutMore, R.id.layoutCommonProblem1, R.id.layoutMore1, R.id.layoutPen, R.id.bt_profile_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layoutPen:
                startActivity(new Intent(context, ProfileActivity.class));
//                Intent intent = new Intent(context, BleConnectProblemActivity.class);
//                startActivity(intent);
                break;
            case R.id.layoutCommonProblem:
            case R.id.layoutCommonProblem1:
                if (!MyOkHttpClient.getInstance().isConnect(getActivity())) {
                    AppUtils.showToast(getActivity(), R.string.no_net_work);
                    return;
                }
                startActivity(new Intent(context, CommonProblemActivity.class));
                break;
            case R.id.layoutMore:
            case R.id.layoutMore1:
                startActivity(new Intent(context, AboutActivity.class));
                break;
            case R.id.bt_profile_exit:
                quitUserDialog();
                break;
        }
    }

    @BindView(R.id.tvTitleTop)
    TextView tvTitleTop;

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
        tvUserName.setText(mUserSetTools.get_user_nickname());
        tvUserId.setText(MyUtils.encryptionUid(BaseApplication.getUserId()));

        tvTitleTop.setText(getResources().getString(R.string.title_mine));
        layoutPen.setVisibility(View.VISIBLE);
    }

    private void updateUi() {
        if (!JavaUtil.checkIsNull(mUserSetTools.get_uesr_head_bast64())) {
            MyLog.i(TAG, "显示头像 Bast64 ");
            Bitmap bitmap = FileUtil.base64ToBitmap(mUserSetTools.get_uesr_head_bast64());
            ivMyHead.setImageBitmap(bitmap);
        } else if (!JavaUtil.checkIsNull(mUserSetTools.get_user_head_url())) {
            MyLog.i(TAG, "显示头像 url ");
            String head_url = mUserSetTools.get_user_head_url();
            BitmapUtils bitmapUtils = new BitmapUtils(context);
            bitmapUtils.display(ivMyHead, head_url);
        } else {
            MyLog.i(TAG, "显示头像 url ");
            ivMyHead.setImageResource(R.drawable.default_header);
        }

        String weightValue = String.valueOf(mUserSetTools.get_user_weight());
        String heightValue = String.valueOf(mUserSetTools.get_user_height());
        String birthdayValue = !JavaUtil.checkIsNull(mUserSetTools.get_user_birthday()) ? mUserSetTools.get_user_birthday() : DefaultVale.USER_BIRTHDAY;
        String sexValue = String.valueOf(mUserSetTools.get_user_sex());

        if (sexValue.equals("0")) {
            tv_profile_sex.setText(R.string.boy);
        } else {
            tv_profile_sex.setText(R.string.girl);
        }
        if (!TextUtils.isEmpty(birthdayValue)) {
            if (AppUtils.isZh(homeActivity)) {
                tv_profile_birthday.setText(birthdayValue);
            } else {
                String[] time = birthdayValue.split("-");
                tv_profile_birthday.setText(time[1] + "/" + time[2] + "/" + time[0]);
            }

        }
        if (!JavaUtil.checkIsNull(heightValue)) {
            if (mUserSetTools.get_user_unit_type()) {
                tv_profile_height.setText(heightValue + getString(R.string.centimeter));
            } else {
                int in = MyUtils.CmToInInt(heightValue);
                tv_profile_height.setText(String.format("%1$2d'%2$2d\"", in / 12, in % 12));
            }
        }
        if (!JavaUtil.checkIsNull(weightValue)) {
            if (mUserSetTools.get_user_unit_type()) {
                tv_profile_weight.setText(weightValue + getString(R.string.kg));
            } else {
                tv_profile_weight.setText(MyUtils.KGToLBString(weightValue, context) + getString(R.string.unit_lb));
            }
        }
    }

    @BindView(R.id.tv_profile_sex)
    TextView tv_profile_sex;
    @BindView(R.id.tv_profile_birthday)
    TextView tv_profile_birthday;
    @BindView(R.id.tv_profile_height)
    TextView tv_profile_height;
    @BindView(R.id.tv_profile_weight)
    TextView tv_profile_weight;

    private void questGetUserInfo() {
        RequestInfo mRequestInfo = RequestJson.getUserInfo();
        MyLog.i(TAG, "请求接口-获取个人信息 mRequestInfo = " + mRequestInfo.toString());
        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(context, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-获取个人信息 = result = " + result.toString());
                        UserBean mUserBean = ResultJson.UserBean(result);
                        //请求成功
                        if (mUserBean.isRequestSuccess()) {
                            if (mUserBean.isUserSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-获取个人信息 成功-数据不为空");
                                ResultUserInfoDataParsing(mUserBean);
                            } else if (mUserBean.isUserSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-获取个人信息 成功-数据为空");
                            } else {
                                MyLog.i(TAG, "请求接口-获取个人信息 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-获取个人信息 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);

                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        MyLog.i(TAG, "请求接口-获取个人信息 请求失败 = message = " + arg0.getMessage());
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);
                    }
                });
    }

    /**
     * 解析用户信息数据
     */
    private void ResultUserInfoDataParsing(UserBean mUserBean) {
        MyLog.i(TAG, "请求接口-获取个人信息 = 解析 = getMsg = " + mUserBean.getMsg());
        MyLog.i(TAG, "请求接口-获取个人信息 = 解析 = getCodeMsg = " + mUserBean.getCodeMsg());
        MyLog.i(TAG, "请求接口-获取个人信息 = 解析 = getData = " + mUserBean.getData().toString());
        //存储用户信息
        UserBean.saveUserInfo(mUserBean.getData());
        updateUi();
    }

    private void quitUserDialog() {
        DialogUtils.BaseDialog(context,
                context.getResources().getString(R.string.dialog_prompt),
                context.getResources().getString(R.string.exite_account),
                context.getDrawable(R.drawable.black_corner_bg),
                new DialogUtils.DialogClickListener() {
                    @Override
                    public void OnOK() {
//                        homeActivity.disconnect();
//                        MyOkHttpClient.getInstance().quitApp(context);
                        unBindDevice();
                    }

                    @Override
                    public void OnCancel() {
                    }
                }
        );
    }

    private void unBindDevice() {
        homeActivity.restore_factory();
        Handler mHandler = new Handler();
        mHandler.postDelayed(() -> {
            homeActivity.disconnect();
            BleTools.unBind(context);
            MyOkHttpClient.getInstance().quitApp(context);
        }, Constants.FINISH_ACTIVITY_DELAY_TIME);
    }
}
