package com.zjw.apps3pluspro.module.mine.user;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.lidroid.xutils.BitmapUtils;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.bleservice.BroadcastTools;
import com.zjw.apps3pluspro.module.device.SkinColourActivity;
import com.zjw.apps3pluspro.module.home.cycle.CycleInitActivity;
import com.zjw.apps3pluspro.module.home.ecg.EcgMeasureActivity;
import com.zjw.apps3pluspro.module.home.ppg.PpgMeasureActivity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.entity.UserData;
import com.zjw.apps3pluspro.network.javabean.UserBean;
import com.zjw.apps3pluspro.network.okhttp.MyOkHttpClient;
import com.zjw.apps3pluspro.sharedpreferences.BleDeviceTools;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.AuthorityManagement;
import com.zjw.apps3pluspro.utils.BmpUtils;
import com.zjw.apps3pluspro.utils.CalibrationUtils;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.FileUtil;
import com.zjw.apps3pluspro.utils.GZIPUtil;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.SysUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.PickerView;
import com.zjw.apps3pluspro.view.PickerView.onSelectListener;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.SimpleFormatter;


/**
 * 个人信息修改
 */
public class ProfileActivity extends BaseActivity implements OnClickListener {
    private final String TAG = ProfileActivity.class.getSimpleName();
    private Context mContext;
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private BleDeviceTools mBleDeviceTools = BaseApplication.getBleDeviceTools();

    private MyActivityManager manager;
    private WaitDialog waitDialog;

    // 弹框的信息
    private Dialog dialog;

    private String UserName;
    private String heightValue;
    private String weightValue;
    private String birthdayValue;
    private String upBirthdayValue;
    private String sexValue;
    private int year, month, day;

    private TextView tv_profile_name, tv_profile_birthday, tv_profile_height, tv_profile_weight, tv_profile_sex;
    private ImageView ci_mines_head;
    private ImageView img_profile_skin_colour_bg;

//    private RelativeLayout rl_change_password;
//    private View view_change_password;

    private TextView text_map_google, text_map_gaode;
    private TextView tv_profile_bp_calibration;

    //拍照相关
    private Uri imageUri;
    private File tempFile;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_profile;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = ProfileActivity.this;
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        waitDialog = new WaitDialog(mContext);
        imageUri = Uri.parse(Constants.IMAGE_FILE_LOCATION);
        initView();
        initData();
        setPersonalData();
        if (mUserSetTools.get_map_enable()) {
            updateUnitUi(text_map_google, text_map_gaode, mUserSetTools.get_is_google_map());
        } else {
            if (AppUtils.isZh(mContext)) {
                updateUnitUi(text_map_google, text_map_gaode, false);
            } else {
                updateUnitUi(text_map_google, text_map_gaode, true);
            }
        }

        // 创建
        SysUtils.makeRootDirectory(Constants.HEAD_IMG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updaSkinUi();
        updateUi();
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    // 初始化控件
    private void initView() {

        ((TextView) findViewById(R.id.public_head_title)).setText(R.string.personal_settings);
        findViewById(R.id.public_head_back).setOnClickListener(this);

        // 生日，身高，体重，性别，手机号码,昵称
        tv_profile_birthday = (TextView) findViewById(R.id.tv_profile_birthday);
        tv_profile_height = (TextView) findViewById(R.id.tv_profile_height);
        tv_profile_weight = (TextView) findViewById(R.id.tv_profile_weight);
        tv_profile_sex = (TextView) findViewById(R.id.tv_profile_sex);
        tv_profile_name = (TextView) findViewById(R.id.tv_profile_name);

        //修改密码，暂时屏蔽
//        rl_change_password = (RelativeLayout) findViewById(R.id.rl_change_password);
//        rl_change_password.setOnClickListener(this);
//        view_change_password = (View) findViewById(R.id.view_change_password);

        //肤色选择
        img_profile_skin_colour_bg = (ImageView) findViewById(R.id.img_profile_skin_colour_bg);

        findViewById(R.id.bt_profile_exit).setOnClickListener(this);
        findViewById(R.id.rl_profile_skin_colour).setOnClickListener(this);
        findViewById(R.id.rl_profile_name).setOnClickListener(this);
        findViewById(R.id.rl_profile_height).setOnClickListener(this);
        findViewById(R.id.rl_profile_weight).setOnClickListener(this);
        findViewById(R.id.rl_profile_birthday).setOnClickListener(this);
        findViewById(R.id.rl_profile_sex).setOnClickListener(this);

        ci_mines_head = (ImageView) findViewById(R.id.ci_mines_head);

        text_map_google = (TextView) findViewById(R.id.text_map_google);
        text_map_gaode = (TextView) findViewById(R.id.text_map_gaode);
        text_map_google.setOnClickListener(this);
        text_map_gaode.setOnClickListener(this);

        tv_profile_bp_calibration = (TextView) findViewById(R.id.tv_profile_bp_calibration);
        findViewById(R.id.rl_profile_bp_jiaozhun).setOnClickListener(this);
        findViewById(R.id.rl_profile_head).setOnClickListener(this);

    }

    // 设置数据
    private void initData() {

        sexValue = String.valueOf(mUserSetTools.get_user_sex());
        UserName = mUserSetTools.get_user_nickname();
        weightValue = String.valueOf(mUserSetTools.get_user_weight());

        heightValue = String.valueOf(mUserSetTools.get_user_height());
        int in = MyUtils.CmToInInt(heightValue);
        pvFt = in / 12;
        pvIn = in % 12;

        birthdayValue = !JavaUtil.checkIsNull(mUserSetTools.get_user_birthday()) ? mUserSetTools.get_user_birthday() : DefaultVale.USER_BIRTHDAY;

        //修改密码，暂时屏蔽
//        if (mUserSetTools.get_user_password().equals("")) {
//            rl_change_password.setVisibility(View.GONE);
//            view_change_password.setVisibility(View.GONE);
//        } else {
//            rl_change_password.setVisibility(View.VISIBLE);
//            view_change_password.setVisibility(View.VISIBLE);
//        }

        updateUnit(mUserSetTools.get_user_unit_type());


        if (!JavaUtil.checkIsNull(mUserSetTools.get_uesr_head_bast64())) {

            MyLog.i(TAG, "显示头像 Bast64 ");

            Bitmap bitmap = FileUtil.base64ToBitmap(mUserSetTools.get_uesr_head_bast64());
            ci_mines_head.setImageBitmap(bitmap);

        } else if (!JavaUtil.checkIsNull(mUserSetTools.get_user_head_url())) {

            MyLog.i(TAG, "显示头像 url ");

            String head_url = mUserSetTools.get_user_head_url();
            BitmapUtils bitmapUtils = new BitmapUtils(mContext);
            bitmapUtils.display(ci_mines_head, head_url);


        } else {
            MyLog.i(TAG, "显示头像 url ");

            ci_mines_head.setImageResource(R.drawable.default_header);
        }

    }


    /**
     * 设置下面的个人信息
     */
    private void setPersonalData() {


        if (!TextUtils.isEmpty(UserName)) {
            tv_profile_name.setText(UserName);
        }


        if (!JavaUtil.checkIsNull(heightValue)) {
            if (mUserSetTools.get_user_unit_type()) {
                tv_profile_height.setText(heightValue + getString(R.string.centimeter));
            } else {
                tv_profile_height.setText(String.format("%1$2d'%2$2d\"", pvFt, pvIn));
            }
        }

        if (!JavaUtil.checkIsNull(weightValue)) {
            if (mUserSetTools.get_user_unit_type()) {
                tv_profile_weight.setText(weightValue + getString(R.string.kg));
            } else {
                tv_profile_weight.setText(MyUtils.KGToLBString(weightValue, mContext) + getString(R.string.unit_lb));
            }
        }


        if (sexValue.equals("0")) {
            tv_profile_sex.setText(R.string.boy);
        } else {
            tv_profile_sex.setText(R.string.girl);
        }

        if (!TextUtils.isEmpty(birthdayValue)) {
            if (AppUtils.isZh(mContext)) {
                tv_profile_birthday.setText(birthdayValue);
            } else {
                String[] time = birthdayValue.split("-");
                tv_profile_birthday.setText(time[1] + "/" + time[2] + "/" + time[0]);
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //返回
            case R.id.public_head_back:
                manager.popOneActivity(this);
                break;

            //生日
            case R.id.rl_profile_birthday:
                showBirthdayDialog();
                break;

            //身高
            case R.id.rl_profile_height:
                showHeightDialog();
                break;

            //体重
            case R.id.rl_profile_weight:
                showWeightDialog();
                break;

            //性别
            case R.id.rl_profile_sex:
                showSexDialog();
                break;

            //肤色选择
            case R.id.rl_profile_skin_colour:
                Intent mIntent = new Intent(mContext, SkinColourActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(IntentConstants.IntentSkinColur, IntentConstants.IntentSkinColurTypeSet);
                mIntent.putExtras(bundle);
                startActivity(mIntent);
                break;

//            //修改密码
//            case R.id.rl_change_password:
//                startActivity(new Intent(mContext, UpdatePasswordActivity.class));
//                break;

            //退出登录
            case R.id.bt_profile_exit:
                QuitUserDialog();
                break;

            case R.id.rl_profile_name:
                showNameDialog();
                break;

            //切换Google地图
            case R.id.text_map_google:
                int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
                if (code == ConnectionResult.SUCCESS) {
                    updateUnitUi(text_map_google, text_map_gaode, true);
                    mUserSetTools.set_map_enable(true);
                    mUserSetTools.set_is_google_map(true);
                } else {
                    AppUtils.showToast(mContext, R.string.my_mail_list_temporary_support);
                }
                break;
            //切换高德额地图
            case R.id.text_map_gaode:
                updateUnitUi(text_map_google, text_map_gaode, false);
                mUserSetTools.set_map_enable(true);
                mUserSetTools.set_is_google_map(false);
                break;

            //校准
            case R.id.rl_profile_bp_jiaozhun:
                if (HomeActivity.ISBlueToothConnect()) {
                    showCalibrationdialog();
                } else {
                    AppUtils.showToast(mContext, R.string.no_connection_notification);
                }
                break;

            //头像相关
            case R.id.rl_profile_head:
                if (AuthorityManagement.verifyStoragePermissions(ProfileActivity.this)) {
                    MyLog.i(TAG, "SD卡权限 已获取");
                    showHeadDialog();
                } else {
                    MyLog.i(TAG, "SD卡权限 未获取");
                }
                break;


        }
    }


    /**
     * 性别的选择
     */
    private void showSexDialog() {
        // TODO Auto-generated method stub
        View view = getLayoutInflater().inflate(R.layout.dialog_sex, null);
        dialog = new Dialog(mContext, R.style.shareStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = LayoutParams.MATCH_PARENT;
        wl.height = LayoutParams.WRAP_CONTENT;

        PickerView pv_sex = (PickerView) view.findViewById(R.id.pv_sex);

        if (sexValue == null || sexValue.equals("")) {
            sexValue = "0";
        }

        List<String> dataSex = new ArrayList<String>();

        dataSex.add(getString(R.string.boy));
        dataSex.add(getString(R.string.girl));


        if (sexValue.equals("0")) {
            pv_sex.setData(dataSex, 0);
        } else {
            pv_sex.setData(dataSex, 1);
        }

        pv_sex.setOnSelectListener(new onSelectListener() {

            @Override
            public void onSelect(String text) {

                MyLog.i(TAG, "性别弹框 = text = " + text);
                if (text.equals(getString(R.string.boy))) {
                    sexValue = "0";
                } else {
                    sexValue = "1";
                }

            }
        });


        view.findViewById(R.id.tv_sex_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        view.findViewById(R.id.tv_sex_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                //如果支持周期的话
                if (mBleDeviceTools.get_device_is_cycle()) {

                    //没设置过-第一次
                    if (mUserSetTools.get_device_is_one_cycle()) {
                        MyLog.i(TAG, "生理周期性别设置 = 没设置过");
                        //sex =1 = 女
                        if (sexValue.equals("1")) {
                            MyLog.i(TAG, "生理周期性别设置 = 切换成=女");
                            setUserSex();
                            showCangeSexNvDilog();
                        } else {
                            MyLog.i(TAG, "生理周期性别设置 = 切换成=男");
                            setUserSex();
                        }

                    } else {
                        MyLog.i(TAG, "生理周期性别设置 = 设置过的");
                        //sex =1 = 女
                        if (sexValue.equals("1")) {
                            MyLog.i(TAG, "生理周期性别设置 = 切换成=女");
                            setUserSex();
                        } else {
                            MyLog.i(TAG, "生理周期性别设置 = 切换成=男");
                            dialog.cancel();
                            showCangeSexNanDilog();
                        }
                    }

                } else {
                    setUserSex();
                }

            }
        });

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


    }


    /**
     * 生日的dialog
     */
    private void showBirthdayDialog() {

        if (!TextUtils.isEmpty(birthdayValue)) {

            if (birthdayValue.contains("-")) {
                year = Integer.parseInt(birthdayValue.split("-")[0]);
                month = Integer.parseInt(birthdayValue.split("-")[1]);
                day = Integer.parseInt(birthdayValue.split("-")[2]);
                upBirthdayValue = year + "-" + month + "-" + day;


            } else {
                year = Integer.parseInt(birthdayValue.split(".")[0]);
                month = Integer.parseInt(birthdayValue.split(".")[1]);
                day = Integer.parseInt(birthdayValue.split(".")[2]);
                upBirthdayValue = year + "-" + month + "-" + day;

            }
        } else {
            upBirthdayValue = MyTime.getTime();
        }


        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                // 当用户选择日期的时候会触发
//                      2013年-4月-5日
                year = arg1;
                month = arg2 + 1;
                day = arg3;
                upBirthdayValue = year + "-" + month + "-" + day;
                MyLog.i(TAG, "生日 = " + upBirthdayValue);
                birthdayValue = upBirthdayValue;
                saveUserBirthday();

            }
        }, year, month - 1, day);


        datePickerDialog.show();

    }


    /**
     * 体重的dialog
     */
    private void showWeightDialog() {
        MyLog.i(TAG, "公制问题 体重 类型 = " + mUserSetTools.get_user_unit_type());
        View view = getLayoutInflater().inflate(R.layout.dialog_weight, null);
        dialog = new Dialog(mContext, R.style.shareStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = LayoutParams.MATCH_PARENT;
        wl.height = LayoutParams.WRAP_CONTENT;

        PickerView pv_weight = (PickerView) view.findViewById(R.id.pv_weight);
        TextView tv_weigh_unit_dialog = (TextView) view.findViewById(R.id.tv_weigh_unit_dialog);

        List<String> data = new ArrayList<String>();

        if (mUserSetTools.get_user_unit_type()) {
            tv_weigh_unit_dialog.setText(getString(R.string.kg));


            for (int i = 20; i < 251; i++) {
                data.add("" + i);
            }


            if (TextUtils.isEmpty(weightValue) || Integer.valueOf(weightValue) < 20 || Integer.valueOf(weightValue) > 250) {
                pv_weight.setData(data, 45);
                weightValue = "65";
            } else {
                int index = (int) Float.parseFloat(weightValue) - 20;
                pv_weight.setData(data, index);
            }
            pv_weight.setOnSelectListener(new onSelectListener() {

                @Override
                public void onSelect(String text) {
                    weightValue = text;
                }
            });
        } else {

            tv_weigh_unit_dialog.setText(getString(R.string.unit_lb));

            for (int i = 44; i < 553; i++) {
                data.add("" + i);
            }


            MyLog.i(TAG, "公制问题 体重 公制 weightValue2= " + weightValue);


            if (TextUtils.isEmpty(weightValue) || MyUtils.KGToLBInt(weightValue, this) < 44 || MyUtils.KGToLBInt(weightValue, this) > 552) {
                pv_weight.setData(data, 99);
                weightValue = "65";
            } else {
                int index = MyUtils.KGToLBInt(weightValue, this) - 44;
                pv_weight.setData(data, index);
            }
            pv_weight.setOnSelectListener(new onSelectListener() {

                @Override
                public void onSelect(String text) {

                    mUserSetTools.set_weight_disparity(MyUtils.getWeightDisparity(Integer.valueOf(text)));
                    weightValue = MyUtils.LBToKGString(text);
                    MyLog.i(TAG, "公制问题 体重  text = " + text);
                    MyLog.i(TAG, "公制问题 体重  weightValue = " + weightValue);
                    MyLog.i(TAG, "公制问题 体重  text222 = " + MyUtils.getWeightDisparity(Integer.valueOf(text)));
                }
            });

        }

        view.findViewById(R.id.tv_weight_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        view.findViewById(R.id.tv_weight_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserWeight();

            }
        });


        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }


    /**
     * 身高的dialog
     */
    int pvFt = 0;
    int pvIn = 0;

    private void showHeightDialog() {
        // TODO Auto-generated method stub
        MyLog.i(TAG, "公制问题 身高 = 类型 " + mUserSetTools.get_user_unit_type());
        View view = getLayoutInflater().inflate(R.layout.dialog_height, null);
        dialog = new Dialog(mContext, R.style.shareStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = LayoutParams.MATCH_PARENT;
        wl.height = LayoutParams.WRAP_CONTENT;

        PickerView pv_height = (PickerView) view.findViewById(R.id.pv_height);
        PickerView pv_height_ft = (PickerView) view.findViewById(R.id.pv_height_ft);
        PickerView pv_height_in = (PickerView) view.findViewById(R.id.pv_height_in);
        TextView tv_height_unit_dialog = (TextView) view.findViewById(R.id.tv_height_unit_dialog);
        RelativeLayout layout1 = view.findViewById(R.id.layout1);
        LinearLayout layout2 = view.findViewById(R.id.layout2);

        List<String> dataHeight = new ArrayList<String>();
        if (mUserSetTools.get_user_unit_type()) {


            tv_height_unit_dialog.setText(getString(R.string.centimeter));


            for (int i = 70; i < 251; i++) {
                dataHeight.add("" + i);
            }
            if (TextUtils.isEmpty(heightValue) || Integer.valueOf(heightValue) < 70 || Integer.valueOf(heightValue) > 250) {
                heightValue = "170";
                pv_height.setData(dataHeight, 100);
                MyLog.i(TAG, "公制问题 公制 身高 = heightValue = " + heightValue);
            } else {
                int index = (int) Float.parseFloat(heightValue) - 70;
                pv_height.setData(dataHeight, index);
            }
            pv_height.setOnSelectListener(new onSelectListener() {

                @Override
                public void onSelect(String text) {
                    heightValue = text;
                }
            });

        } else {
            layout1.setVisibility(View.GONE);
            layout2.setVisibility(View.VISIBLE);

            List<String> dataHeight1 = new ArrayList<String>();
            List<String> dataHeight2 = new ArrayList<String>();
            for (int i = 2; i < 9; i++) {
                dataHeight1.add("" + i);
            }
            for (int i = 0; i < 12; i++) {
                dataHeight2.add("" + i);
            }

            if (TextUtils.isEmpty(heightValue) || MyUtils.CmToInInt(heightValue) < 24 || MyUtils.CmToInInt(heightValue) > 107) {
                heightValue = "170";
            }
            int in = MyUtils.CmToInInt(heightValue);
            pv_height_ft.setData(dataHeight1, (in / 12 - 2));
            pv_height_in.setData(dataHeight2, in % 12);

            pvFt = in / 12;
            pvIn = in % 12;
            pv_height_ft.setOnSelectListener(text -> {
                pvFt = Integer.parseInt(text);
            });
            pv_height_in.setOnSelectListener(text -> {
                pvIn = Integer.parseInt(text);
            });
        }

        view.findViewById(R.id.tv_height_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        view.findViewById(R.id.tv_height_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                heightValue = String.valueOf((int) (pvFt * 30.48 + pvIn * 2.54));
                saveUserHeight();
            }
        });

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    /**
     * 昵称的dialog
     */

    private void showNameDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_nickname, null);
        dialog = new Dialog(mContext, R.style.shareStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = LayoutParams.MATCH_PARENT;
        wl.height = LayoutParams.WRAP_CONTENT;


        final EditText et_nick_name = (EditText) view.findViewById(R.id.et_nick_name);
        if (!tv_profile_name.getText().toString().trim().equals("")) {
            UserName = tv_profile_name.getText().toString().trim();
        }
        et_nick_name.setText(UserName);
        et_nick_name.setSelection(et_nick_name.getText().length());

        view.findViewById(R.id.tv_nick_name_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_nick_name_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                saveUserName(et_nick_name.getText().toString());

            }
        });


        dialog.show();


    }


    void updateUnit(boolean isture) {
        MyLog.i(TAG, "公制问题 33333 = ");

        mUserSetTools.set_user_unit_type(isture);

        if (isture) {


            MyLog.i(TAG, "个人信息设置 = heightValue = " + heightValue);
            MyLog.i(TAG, "个人信息设置 = heightUnit_en = " + getString(R.string.centimeter));

            if (!TextUtils.isEmpty(heightValue)) {
                tv_profile_height.setText(heightValue + getString(R.string.centimeter));
            }

            if (!TextUtils.isEmpty(weightValue)) {
                tv_profile_weight.setText(weightValue + getString(R.string.kg));
            }
        } else {


//                heightValue = "165.0";

            //这个时候要转换一下,身高转化成英寸，体重转换成磅
            MyLog.i(TAG, "个人信息设置 = heightValue = " + heightValue);
            MyLog.i(TAG, "个人信息设置 = heightUnit_en = " + getString(R.string.unit_in));


            if (!TextUtils.isEmpty(heightValue)) {
                tv_profile_height.setText(String.format("%1$2d'%2$2d\"", pvFt, pvIn));
            }

            if (!TextUtils.isEmpty(weightValue)) {
                tv_profile_weight.setText(MyUtils.KGToLBString(weightValue, this) + getString(R.string.unit_lb));
            }
        }

    }


    /**
     * 保存用户名
     *
     * @param user_name
     */
    void saveUserName(String user_name) {

        dialog.cancel();

        if (JavaUtil.checkIsNull(user_name)) {
            AppUtils.showToast(mContext, R.string.enter_your_nickname);
        } else if (JavaUtil.containsEmoji(user_name)) {
            AppUtils.showToastStr(mContext, getString(R.string.enter_your_enjoy));
        } else if (UserName.equals(user_name)) {
            AppUtils.showToast(mContext, R.string.nick_not_same);
        } else {
            // 这个里面做上传操作。
            tv_profile_name.setText(user_name);
            mUserSetTools.set_user_nickname(user_name);
            UserData mUserData = new UserData();
            mUserData.setNikname(mUserSetTools.get_user_nickname());
            uploadUserInfo(mUserData);

        }
    }

    /**
     * 保存用户身高
     */
    void saveUserHeight() {
        dialog.cancel();
        if (!JavaUtil.checkIsNull(heightValue)) {
            if (mUserSetTools.get_user_unit_type()) {
                tv_profile_height.setText(heightValue + getString(R.string.centimeter));
            } else {
                tv_profile_height.setText(String.format("%1$2d'%2$2d\"", pvFt, pvIn));
            }
            BroadcastTools.sendBleUserinfoData(mContext);
            mUserSetTools.set_user_height(Integer.valueOf(heightValue));
            UserData mUserData = new UserData();
            mUserData.setHeight(String.valueOf(mUserSetTools.get_user_height()));
            uploadUserInfo(mUserData);
        }
    }

    /**
     * 保存用户体重
     */
    void saveUserWeight() {
        dialog.cancel();

        if (!JavaUtil.checkIsNull(weightValue)) {

            if (mUserSetTools.get_user_unit_type()) {
                tv_profile_weight.setText(weightValue + getString(R.string.kg));
            } else {
                tv_profile_weight.setText(MyUtils.KGToLBString(weightValue, mContext) + getString(R.string.unit_lb));
            }

            mUserSetTools.set_user_weight(Integer.valueOf(weightValue));
            UserData mUserData = new UserData();
            mUserData.setWeight(String.valueOf(mUserSetTools.get_user_weight()));
            uploadUserInfo(mUserData);
            BroadcastTools.sendBleUserinfoData(mContext);
        }

    }

    /**
     * 保存用户生日
     */
    void saveUserBirthday() {


        if (MyTime.getIsOldTime(upBirthdayValue)) {
            if (AppUtils.isZh(mContext)) {
                tv_profile_birthday.setText(upBirthdayValue);
            } else {
                String[] time = upBirthdayValue.split("-");
                tv_profile_birthday.setText(time[1] + "/" + time[2] + "/" + time[0]);
            }
            mUserSetTools.set_user_birthday(upBirthdayValue);
            UserData mUserData = new UserData();
            mUserData.setBirthday(mUserSetTools.get_user_birthday());
            uploadUserInfo(mUserData);
            BroadcastTools.sendBleUserinfoData(mContext);
        } else {
            AppUtils.showToast(mContext, R.string.birthday_error);
        }

    }

    /**
     * 保存用户性别
     */
    void setUserSex() {

        if (dialog != null) {
            dialog.cancel();
        }

        if (sexValue != null && !sexValue.equals("")) {
            if (sexValue.equals("0")) {
                tv_profile_sex.setText(R.string.boy);
            } else {
                tv_profile_sex.setText(R.string.girl);
            }
            mUserSetTools.set_user_sex(Integer.valueOf(sexValue));
            UserData mUserData = new UserData();
            mUserData.setSex(String.valueOf(mUserSetTools.get_user_sex()));
            uploadUserInfo(mUserData);
            BroadcastTools.sendBleUserinfoData(mContext);
            BroadcastTools.sendBleCycleData(mContext);
        }

    }


    /**
     * 上传用户信息
     */
    private void uploadUserInfo(UserData mUserData) {

        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.modifyUserInfo(mContext, mUserData, true);

        MyLog.i(TAG, "请求接口-修改个人信息 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-修改个人信息 请求成功 = result = " + result.toString());

                        UserBean mUserBean = ResultJson.UserBean(result);

                        //请求成功
                        if (mUserBean.isRequestSuccess()) {

                            if (mUserBean.uploadUserSuccess() == 1) {

                                MyLog.i(TAG, "请求接口-修改个人信息 成功");
                                AppUtils.showToast(mContext, R.string.change_ok);

                            } else if (mUserBean.uploadUserSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-修改个人信息 失败");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);

                            } else {
                                MyLog.i(TAG, "请求接口-修改个人信息 请求异常(1)");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);
                            }
                            //请求失败
                        } else {
                            MyLog.i(TAG, "请求接口-修改个人信息 请求异常(0)");
                            AppUtils.showToast(mContext, R.string.server_try_again_code0);
                        }
                    }

                    @Override
                    public void onMyError(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-修改个人信息 请求失败 = message = " + arg0.getMessage());

                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });

    }


    /**
     * 换头像的Dialog
     */
    private void showHeadDialog() {
        // TODO Auto-generated method stub
        View view = getLayoutInflater().inflate(
                R.layout.photo_choose_dialog, null);
        dialog = new Dialog(mContext, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = LayoutParams.MATCH_PARENT;
        wl.height = LayoutParams.WRAP_CONTENT;

        view.findViewById(R.id.photograph).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();


                if (AuthorityManagement.verifyStoragePermissions(ProfileActivity.this)) {
                    MyLog.i(TAG, "SD卡权限 已获取");
                } else {
                    MyLog.i(TAG, "SD卡权限 未获取");
                }

                if (AuthorityManagement.verifyPhotogrAuthority(ProfileActivity.this)) {
                    MyLog.i(TAG, "拍照权限 已获取");
                    TakingPictures();
                } else {
                    MyLog.i(TAG, "拍照权限 未获取");
                }

            }
        });
        view.findViewById(R.id.albums).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                PhotoAlbum();
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

    /**
     * 拍照
     */
    void TakingPictures() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Constants.HEAD_IMG, "head.png")));
        startActivityForResult(intent, Constants.TakingTag);// 采用ForResult打开
    }

    /**
     * 相册
     */
    void PhotoAlbum() {
        Intent intent2 = new Intent(Intent.ACTION_PICK, null);
        intent2.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent2, Constants.PhotoTag);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.PhotoTag:
                MyLog.i(TAG, "回调 裁剪图片");
                if (resultCode == RESULT_OK) {
//                    cropPhoto(data.getData());// 裁剪图片
                    try {
                        startCropIntent(data.getData());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Constants.TakingTag:
                MyLog.i(TAG, "回调 拍摄照片");
                if (resultCode == RESULT_OK) {
//                    File temp = new File(Constants.HeadFilePath + "/head.png");
//                    cropPhoto(Uri.fromFile(temp));// 裁剪图片


                    tempFile = new File(Constants.HEAD_IMG + "head.png");
                    try {
                        startCropIntent(Uri.fromFile(tempFile));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case Constants.TailoringResult:
                MyLog.i(TAG, "回调 裁剪完成 imageUri = " + imageUri);
                if (resultCode == RESULT_OK) {
                    if (imageUri != null) {
                        try {
                            Bitmap head = BmpUtils.decodeUriAsBitmap(mContext, imageUri);
                            if (head != null) {
                                setPicToView(head);// 保存在SD卡中
                                ci_mines_head.setImageBitmap(head);// 用ImageView显示出来
                                uploadImage(Constants.HEAD_IMG + "head" + BaseApplication.getUserId() + ".png");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                break;


        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 上传头像
     *
     * @param path
     */
    private void uploadImage(String path) {


        byte[] data_src = null;
        try {
            data_src = FileUtil.FileToByte(path);
        } catch (Exception e1) {
            // Log.e("------------------BX", "图片转换失败");
            e1.printStackTrace();
        }
        String head_loca_data = new String(Base64.encode(data_src, Base64.DEFAULT));
        mUserSetTools.set_user_head_bast64(head_loca_data);

        byte[] data = GZIPUtil.Compress(data_src);
        String head_image_data = new String(Base64.encode(data, Base64.DEFAULT));

        RequestInfo mRequestInfo = RequestJson.uploadUserInfoHead(head_image_data);

        MyLog.i(TAG, "请求接口-上传头像 mRequestInfo = " + mRequestInfo.toString());


        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub


                        MyLog.i(TAG, "请求接口-上传头像 = result = " + result.toString());

                        UserBean mUserBean = ResultJson.UserBean(result);

                        //请求成功
                        if (mUserBean.isRequestSuccess()) {

                            if (mUserBean.uploadUserSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-上传头像 成功");
                                AppUtils.showToast(mContext, R.string.set_head_ok);
                            } else if (mUserBean.uploadUserSuccess() == 0) {

                                MyLog.i(TAG, "请求接口-上传头像 失败");
                                AppUtils.showToast(mContext, R.string.set_head_defeat);

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

                        MyLog.i(TAG, "请求接口-上传头像 请求失败 = message = " + arg0.getMessage());
                        AppUtils.showToast(mContext, R.string.net_worse_try_again);

                        return;
                    }
                });


    }


    /**
     * 裁剪图片
     *
     * @param uri
     * @throws FileNotFoundException
     */
    private void startCropIntent(Uri uri) throws FileNotFoundException {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 140);
        intent.putExtra("aspectY", 140);
        intent.putExtra("outputX", 140);
        intent.putExtra("outputY", 140);
        intent.putExtra("return-data", false);
        // 上面设为false的时候将MediaStore.EXTRA_OUTPUT关联一个Uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, Constants.TailoringResult);
    }

    private void setPicToView(Bitmap mBitmap) {

        String sdStatus = Environment.getExternalStorageState();
        String fileName = "";
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用

            AppUtils.showToast(mContext, R.string.sd_card);
            return;
        }

        // 创建
        SysUtils.makeRootDirectory(Constants.HEAD_IMG);

        FileOutputStream b = null;
        fileName = Constants.HEAD_IMG + "head" + BaseApplication.getUserId() + ".png";// 图片名字

        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 50, b);// 把数据写入文件

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 弹出对话框
     */
    void QuitUserDialog() {
        new android.app.AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.dialog_prompt))
                .setMessage(getString(R.string.exite_account))
                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        disconnect();
                        MyOkHttpClient.getInstance().quitApp(ProfileActivity.this);
                    }
                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }

        }).show();//在按键响应事件中显示此对话框
    }


    void updateUnitUi(TextView left_text, TextView right_text, boolean is_left) {
        if (is_left) {
            left_text.setBackgroundResource(R.drawable.my_unit_left_on);
            left_text.setTextColor(this.getResources().getColor(R.color.my_unit_color_off));

            right_text.setBackgroundResource(R.drawable.my_unit_right_off);
            right_text.setTextColor(this.getResources().getColor(R.color.code_color));

        } else {
            left_text.setBackgroundResource(R.drawable.my_unit_left_off);
            left_text.setTextColor(this.getResources().getColor(R.color.my_unit_color_on));

            right_text.setBackgroundResource(R.drawable.my_unit_right_on);
            right_text.setTextColor(this.getResources().getColor(R.color.my_unit_color_off));
        }

    }

    void updateUi() {

        int grade = mUserSetTools.get_blood_grade();
        int sbp = mUserSetTools.get_calibration_systolic();
        int dbp = mUserSetTools.get_calibration_diastolic();

        MyLog.i(TAG, "测量校准 = grade = " + grade + "  sbp = " + sbp + "  dbp = " + dbp);

        if (grade >= 0 && grade <= 4) {

            switch (grade) {
                case 0:
                    tv_profile_bp_calibration.setText(getString(R.string.user_par_state0));
                    break;
                case 1:
                    tv_profile_bp_calibration.setText(getString(R.string.user_par_state1));

                    break;
                case 2:
                    tv_profile_bp_calibration.setText(getString(R.string.user_par_state2));

                    break;
                case 3:
                    tv_profile_bp_calibration.setText(getString(R.string.user_par_state3));

                    break;

                case 4:
                    tv_profile_bp_calibration.setText(getString(R.string.user_par_state4));

                    break;
            }

        } else {

            tv_profile_bp_calibration.setText(String.valueOf(sbp) + "/" + String.valueOf(dbp) + getString(R.string.mmHg));
        }

    }

    /**
     * 更新肤色UI
     */
    void updaSkinUi() {

        if (mBleDeviceTools.get_skin_colour() < 0) {
            mBleDeviceTools.set_skin_colour(0);
        } else if (mBleDeviceTools.get_skin_colour() >= IntentConstants.SkinColor.length) {
            mBleDeviceTools.set_skin_colour(IntentConstants.SkinColor.length - 1);
        }
        img_profile_skin_colour_bg.setBackgroundResource(IntentConstants.SkinColor[mBleDeviceTools.get_skin_colour()]);


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
        new android.app.AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.dialog_prompt))//设置对话框标题
                .setMessage(title)//设置显示的内容
                .setPositiveButton(getString(R.string.setting_dialog_setting), new DialogInterface.OnClickListener() {//添加确定按钮

                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);

                    }

                }).setNegativeButton(getString(R.string.setting_dialog_cancel), new DialogInterface.OnClickListener() {//添加返回按钮


            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件

                // TODO Auto-generated method stub


            }

        }).show();//在按键响应事件中显示此对话框

    }

    //==================校准对话框=========================

    boolean IsCalibrationGrade = true;
    int CalibrationGrade = 2;

    /**
     * 测量校准对话框
     */
    void showCalibrationdialog() {


        CalibrationGrade = mUserSetTools.get_blood_grade();


        int systilic = mUserSetTools.get_calibration_systolic();
        int diastolic = mUserSetTools.get_calibration_diastolic();
//
        final LayoutInflater factory = LayoutInflater.from(mContext);
        final View textEntryView = factory.inflate(R.layout.dialog_calibration, null);


        final TextView dialog_calibration_0_max = (TextView) textEntryView.findViewById(R.id.dialog_calibration_0_max);
        final TextView dialog_calibration_0_min = (TextView) textEntryView.findViewById(R.id.dialog_calibration_0_min);
        final TextView dialog_calibration_1_max = (TextView) textEntryView.findViewById(R.id.dialog_calibration_1_max);
        final TextView dialog_calibration_1_min = (TextView) textEntryView.findViewById(R.id.dialog_calibration_1_min);
        final TextView dialog_calibration_2_max = (TextView) textEntryView.findViewById(R.id.dialog_calibration_2_max);
        final TextView dialog_calibration_2_min = (TextView) textEntryView.findViewById(R.id.dialog_calibration_2_min);
        final TextView dialog_calibration_3_max = (TextView) textEntryView.findViewById(R.id.dialog_calibration_3_max);
        final TextView dialog_calibration_3_min = (TextView) textEntryView.findViewById(R.id.dialog_calibration_3_min);
        final TextView dialog_calibration_4_max = (TextView) textEntryView.findViewById(R.id.dialog_calibration_4_max);
        final TextView dialog_calibration_4_min = (TextView) textEntryView.findViewById(R.id.dialog_calibration_4_min);

        final Button dialog_btn_calibration_grade = (Button) textEntryView.findViewById(R.id.dialog_btn_calibration_grade);
        final Button dialog_btn_calibration_value = (Button) textEntryView.findViewById(R.id.dialog_btn_calibration_value);


        final LinearLayout dialog_ll_calibration_grade = (LinearLayout) textEntryView.findViewById(R.id.dialog_ll_calibration_grade);
        final LinearLayout dialog_ll_calibration_value = (LinearLayout) textEntryView.findViewById(R.id.dialog_ll_calibration_value);

        final EditText dialog_edit_calibration_sbp = (EditText) textEntryView.findViewById(R.id.dialog_edit_calibration_sbp);
        final EditText dialog_edit_calibration_dbp = (EditText) textEntryView.findViewById(R.id.dialog_edit_calibration_dbp);

        final SeekBar dialog_calibration_grade_seekbar = (SeekBar) textEntryView.findViewById(R.id.dialog_calibration_grade_seekbar);


        //等级校准
        if (CalibrationGrade < 0 || CalibrationGrade > 4) {
            CalibrationGrade = 2;

            IsCalibrationGrade = false;
            dialog_ll_calibration_grade.setVisibility(View.GONE);
            dialog_ll_calibration_value.setVisibility(View.VISIBLE);

            dialog_btn_calibration_grade.setBackgroundResource(R.drawable.dailog_calibration_choice_off);
            dialog_btn_calibration_grade.setTextColor(getResources().getColor(R.color.public_text_color1));
            dialog_btn_calibration_value.setBackgroundResource(R.drawable.dailog_calibration_choice_on);
            dialog_btn_calibration_value.setTextColor(getResources().getColor(R.color.public_text_color_white));
        }
        //精准值校准
        else {

            IsCalibrationGrade = true;
            dialog_ll_calibration_grade.setVisibility(View.VISIBLE);
            dialog_ll_calibration_value.setVisibility(View.GONE);

            dialog_btn_calibration_grade.setBackgroundResource(R.drawable.dailog_calibration_choice_on);
            dialog_btn_calibration_grade.setTextColor(getResources().getColor(R.color.public_text_color_white));
            dialog_btn_calibration_value.setBackgroundResource(R.drawable.dailog_calibration_choice_off);
            dialog_btn_calibration_value.setTextColor(getResources().getColor(R.color.public_text_color1));

        }


        if (systilic < DefaultVale.USER_SYSTOLIC_MIN || systilic > DefaultVale.USER_SYSTOLIC_MAX) {
            systilic = DefaultVale.USER_SYSTOLIC;
        }

        if (diastolic < DefaultVale.USER_DIASTOLIC_MIN || diastolic > DefaultVale.USER_DIASTOLIC_MAX) {
            diastolic = DefaultVale.USER_DIASTOLIC;
        }

        dialog_edit_calibration_sbp.setText(String.valueOf(systilic));
        dialog_edit_calibration_dbp.setText(String.valueOf(diastolic));


        //等级校准
        textEntryView.findViewById(R.id.dialog_btn_calibration_grade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IsCalibrationGrade = true;
                dialog_ll_calibration_grade.setVisibility(View.VISIBLE);
                dialog_ll_calibration_value.setVisibility(View.GONE);

                dialog_btn_calibration_grade.setBackgroundResource(R.drawable.dailog_calibration_choice_on);
                dialog_btn_calibration_grade.setTextColor(getResources().getColor(R.color.public_text_color_white));
                dialog_btn_calibration_value.setBackgroundResource(R.drawable.dailog_calibration_choice_off);
                dialog_btn_calibration_value.setTextColor(getResources().getColor(R.color.public_text_color1));

            }
        });


        //精准值校准
        textEntryView.findViewById(R.id.dialog_btn_calibration_value).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IsCalibrationGrade = false;
                dialog_ll_calibration_grade.setVisibility(View.GONE);
                dialog_ll_calibration_value.setVisibility(View.VISIBLE);

                dialog_btn_calibration_grade.setBackgroundResource(R.drawable.dailog_calibration_choice_off);
                dialog_btn_calibration_grade.setTextColor(getResources().getColor(R.color.public_text_color1));
                dialog_btn_calibration_value.setBackgroundResource(R.drawable.dailog_calibration_choice_on);
                dialog_btn_calibration_value.setTextColor(getResources().getColor(R.color.public_text_color_white));

            }
        });


        dialog_calibration_0_max.setVisibility(View.GONE);
        dialog_calibration_1_max.setVisibility(View.GONE);
        dialog_calibration_2_max.setVisibility(View.GONE);
        dialog_calibration_3_max.setVisibility(View.GONE);
        dialog_calibration_4_max.setVisibility(View.GONE);

        dialog_calibration_0_min.setVisibility(View.VISIBLE);
        dialog_calibration_1_min.setVisibility(View.VISIBLE);
        dialog_calibration_2_min.setVisibility(View.VISIBLE);
        dialog_calibration_3_min.setVisibility(View.VISIBLE);
        dialog_calibration_4_min.setVisibility(View.VISIBLE);


        switch (CalibrationGrade) {
            case 0:
                dialog_calibration_0_max.setVisibility(View.VISIBLE);
                dialog_calibration_0_min.setVisibility(View.GONE);
                break;
            case 1:
                dialog_calibration_1_max.setVisibility(View.VISIBLE);
                dialog_calibration_1_min.setVisibility(View.GONE);
                break;
            case 2:
                dialog_calibration_2_max.setVisibility(View.VISIBLE);
                dialog_calibration_2_min.setVisibility(View.GONE);
                break;
            case 3:
                dialog_calibration_3_max.setVisibility(View.VISIBLE);
                dialog_calibration_3_min.setVisibility(View.GONE);
                break;
            case 4:
                dialog_calibration_4_max.setVisibility(View.VISIBLE);
                dialog_calibration_4_min.setVisibility(View.GONE);
                break;
        }


        dialog_calibration_grade_seekbar.setProgress(CalibrationGrade);

        dialog_calibration_grade_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                CalibrationGrade = progress;

                dialog_calibration_0_max.setVisibility(View.GONE);
                dialog_calibration_1_max.setVisibility(View.GONE);
                dialog_calibration_2_max.setVisibility(View.GONE);
                dialog_calibration_3_max.setVisibility(View.GONE);
                dialog_calibration_4_max.setVisibility(View.GONE);

                dialog_calibration_0_min.setVisibility(View.VISIBLE);
                dialog_calibration_1_min.setVisibility(View.VISIBLE);
                dialog_calibration_2_min.setVisibility(View.VISIBLE);
                dialog_calibration_3_min.setVisibility(View.VISIBLE);
                dialog_calibration_4_min.setVisibility(View.VISIBLE);

                switch (CalibrationGrade) {
                    case 0:
                        dialog_calibration_0_max.setVisibility(View.VISIBLE);
                        dialog_calibration_0_min.setVisibility(View.GONE);
                        break;
                    case 1:
                        dialog_calibration_1_max.setVisibility(View.VISIBLE);
                        dialog_calibration_1_min.setVisibility(View.GONE);
                        break;
                    case 2:
                        dialog_calibration_2_max.setVisibility(View.VISIBLE);
                        dialog_calibration_2_min.setVisibility(View.GONE);
                        break;
                    case 3:
                        dialog_calibration_3_max.setVisibility(View.VISIBLE);
                        dialog_calibration_3_min.setVisibility(View.GONE);
                        break;
                    case 4:
                        dialog_calibration_4_max.setVisibility(View.VISIBLE);
                        dialog_calibration_4_min.setVisibility(View.GONE);
                        break;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final AlertDialog dialog = new AlertDialog.Builder(mContext).setView(textEntryView).
                setPositiveButton(getString(R.string.dialog_yes), null)
                .setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.setTitle(getString(R.string.dialog_prompt));
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyLog.i(TAG, "测量校准 = CalibrationGrade = " + CalibrationGrade);


                //等级比较准
                if (IsCalibrationGrade) {

                    mUserSetTools.set_blood_grade(CalibrationGrade);
                    CalibrationUtils.handleCalibrationDialogGrade(TAG, mUserSetTools, CalibrationGrade);
                    gotoMeasure(false);

                }
                //精准值校准
                else {

                    mUserSetTools.set_blood_grade(DefaultVale.USER_BP_LEVEL);
                    String calibration_sbp = dialog_edit_calibration_sbp.getText().toString().trim();
                    String calibration_dbp = dialog_edit_calibration_dbp.getText().toString().trim();

                    handleCalibrationDialogValue(calibration_sbp, calibration_dbp);
                }

                updateUi();
                dialog.dismiss();
            }
        });


    }


    void handleCalibrationDialogValue(String calibration_sbp, String calibration_dbp) {

        MyLog.i(TAG, "测量校准 = 精准值校准 = calibration_sbp = " + calibration_sbp);
        MyLog.i(TAG, "测量校准 = 精准值校准 = calibration_dbp = " + calibration_dbp);


        if (TextUtils.isEmpty(calibration_sbp) || TextUtils.isEmpty(calibration_dbp)) {
            AppUtils.showToast(mContext, R.string.jiaozhun_dailog_error);
            return;
        } else if (MyUtils.checkInputNumber(calibration_sbp) && MyUtils.checkInputNumber(calibration_dbp)) {

            int sbp = Integer.valueOf(calibration_sbp);
            int dbp = Integer.valueOf(calibration_dbp);

            if (sbp < DefaultVale.USER_SYSTOLIC_MIN || sbp > DefaultVale.USER_SYSTOLIC_MAX
                    || dbp < DefaultVale.USER_DIASTOLIC_MIN || dbp > DefaultVale.USER_DIASTOLIC_MAX) {
                AppUtils.showToast(mContext, R.string.jiaozhun_dailog_error);
                return;
            } else {
                mUserSetTools.set_calibration_systolic(sbp);
                mUserSetTools.set_calibration_diastolic(dbp);
                mUserSetTools.set_blood_grade(-1);
                gotoMeasure(false);
            }
        } else {
            AppUtils.showToast(mContext, R.string.jiaozhun_dailog_error);
            return;
        }
    }


    /**
     * 是否校准过
     *
     * @param isCalibration
     */
    void gotoMeasure(boolean isCalibration) {

        //支持ECG-显示健康布局
        if (mBleDeviceTools.get_is_support_ecg() == 1) {
            Intent intent = null;
            //校准过直接进入测量
            if (isCalibration) {
                intent = new Intent(mContext, EcgMeasureActivity.class);
                intent.putExtra(IntentConstants.MesureType, IntentConstants.MesureType_Measure);
                startActivity(intent);
            }
            //没校准过，进行校准
            else {
                intent = new Intent(mContext, EcgMeasureActivity.class);
                intent.putExtra(IntentConstants.MesureType, IntentConstants.MesureType_Calibration);
                startActivity(intent);
            }

            //不支持ECG-显示运动布局
        } else {
            Intent intent = null;
            //校准过直接进入测量
            if (isCalibration) {
                intent = new Intent(mContext, PpgMeasureActivity.class);
                intent.putExtra(IntentConstants.MesureType, IntentConstants.MesureType_Measure);
                startActivity(intent);
            }
            //没校准过，进行校准
            else {
                intent = new Intent(mContext, PpgMeasureActivity.class);
                intent.putExtra(IntentConstants.MesureType, IntentConstants.MesureType_Calibration);
                startActivity(intent);
            }

        }

    }

    //===========校准相关===================


    //切换成性别男的弹框
    void showCangeSexNanDilog() {

        new android.app.AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.dialog_prompt))
                .setMessage(R.string.cycle_init_tip3)
                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {//添加确定按钮

                    public void onClick(DialogInterface dialog, int which) {
                        if (sexValue.equals("0")) {
                            mUserSetTools.set_device_is_one_cycle(true);
                            mUserSetTools.set_nv_start_date("");
                            mUserSetTools.set_nv_cycle(28);
                            mUserSetTools.set_nv_lenght(5);
                            mUserSetTools.set_nv_device_switch(false);
                        }
                        setUserSex();
                    }

                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {


            @Override

            public void onClick(DialogInterface dialog, int which) {

                // TODO Auto-generated method stub

            }

        }).show();


    }


    //切换成性别女且没设置过的的弹框
    void showCangeSexNvDilog() {

        new android.app.AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.dialog_prompt))
                .setMessage(R.string.cycle_init_tip4)
                .setPositiveButton(getString(R.string.setting_dialog_setting), new DialogInterface.OnClickListener() {//添加确定按钮

                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(ProfileActivity.this, CycleInitActivity.class);
                        intent.putExtra(CycleInitActivity.CYCLE_INIT_TAG_TYPE, CycleInitActivity.CYCLE_INIT_TAG_C);
                        startActivity(intent);
                        manager.popOneActivity(ProfileActivity.this);

                    }

                }).setNegativeButton(getString(R.string.setting_dialog_cancel), new DialogInterface.OnClickListener() {


            @Override

            public void onClick(DialogInterface dialog, int which) {

                // TODO Auto-generated method stub

            }

        }).show();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
