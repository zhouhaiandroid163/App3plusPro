package com.zjw.apps3pluspro.module.mine.user;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zjw.apps3pluspro.HomeActivity;
import com.zjw.apps3pluspro.R;
import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.base.BaseActivity;
import com.zjw.apps3pluspro.module.device.SkinColourActivity;
import com.zjw.apps3pluspro.network.NewVolleyRequest;
import com.zjw.apps3pluspro.network.RequestJson;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.network.VolleyInterface;
import com.zjw.apps3pluspro.network.entity.RequestInfo;
import com.zjw.apps3pluspro.network.entity.UserData;
import com.zjw.apps3pluspro.network.javabean.UserBean;
import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.Constants;
import com.zjw.apps3pluspro.view.dialog.WaitDialog;
import com.zjw.apps3pluspro.utils.AppUtils;
import com.zjw.apps3pluspro.utils.DefaultVale;
import com.zjw.apps3pluspro.utils.IntentConstants;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.MyActivityManager;
import com.zjw.apps3pluspro.utils.MyTime;
import com.zjw.apps3pluspro.utils.MyUtils;
import com.zjw.apps3pluspro.utils.log.MyLog;
import com.zjw.apps3pluspro.view.PickerView;
import com.zjw.apps3pluspro.view.PickerView.onSelectListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 第一次输入个人信息
 */
public class ProfileInitActivity extends BaseActivity implements OnClickListener {
    public static final String IntentName = "intent_name";
    private final String TAG = ProfileInitActivity.class.getSimpleName();
    //轻量级存储
    private UserSetTools mUserSetTools = BaseApplication.getUserSetTools();
    private Context mContext;
    private MyActivityManager manager;
    private WaitDialog waitDialog;

    private TextView tv_profile_init_birthday, tv_profile_init_height, tv_profile_init_weight, tv_profile_init_sex, tv_profile_init_unit;
    private EditText edit_profile_int_name;


    // 退出当前账号。
    private String UserName;
    private String heightValue;
    private String weightValue;
    private String birthdayValue;
    private String sexID;//0=男，1=女
    private String unitTag;//0=公制，1=英制
    private int year, month, day;
    // 弹框的信息
    private Dialog dialog;

    @Override
    protected int setLayoutId() {
        isTextDark = false;
        bgColor = R.color.top_bar_base;
        return R.layout.activity_profile_init;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mContext = ProfileInitActivity.this;
        manager = MyActivityManager.getInstance();
        manager.pushOneActivity(this);
        waitDialog = new WaitDialog(mContext);
        initView();
        initData();
    }

    @OnClick({R.id.tvRightText})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvRightText:
                saveUserInfo();
                break;
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (BaseApplication.getHttpQueue() != null) {
            BaseApplication.getHttpQueue().cancelAll(TAG);
        }
    }

    // 初始化控件
    private void initView() {

        ((TextView) findViewById(R.id.public_head_title)).setText(R.string.personal_settings);
        findViewById(R.id.public_head_back).setVisibility(View.GONE);
        findViewById(R.id.tvRightText).setVisibility(View.VISIBLE);

        edit_profile_int_name = (EditText) findViewById(R.id.edit_profile_int_name);

        // 生日，身高，体重，性别，手机号码,昵称
        tv_profile_init_birthday = (TextView) findViewById(R.id.tv_profile_init_birthday);
        tv_profile_init_height = (TextView) findViewById(R.id.tv_profile_init_height);
        tv_profile_init_weight = (TextView) findViewById(R.id.tv_profile_init_weight);
        tv_profile_init_sex = (TextView) findViewById(R.id.tv_profile_init_sex);
        tv_profile_init_unit = (TextView) findViewById(R.id.tv_profile_init_unit);

        findViewById(R.id.rl_profile_init_height).setOnClickListener(this);
        findViewById(R.id.rl_profile_init_weight).setOnClickListener(this);
        findViewById(R.id.rl_profile_init_birthday).setOnClickListener(this);
        findViewById(R.id.rl_profile_init_sex).setOnClickListener(this);
        findViewById(R.id.rl_profile_init_unit).setOnClickListener(this);

    }

    // 设置数据
    private void initData() {

        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        if (bundle != null && bundle.getString(IntentName) != null && !bundle.getString(IntentName).equals("")) {
            UserName = bundle.getString(IntentName);
            MyLog.i(TAG, "UserName = " + UserName);
//            tv_profile_int_name.setText(UserName);
        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //生日
            case R.id.rl_profile_init_birthday:
                showBirthdayDialog();
                break;

            //身高
            case R.id.rl_profile_init_height:
                showHeightDialog();
                break;

            //体重
            case R.id.rl_profile_init_weight:
                showWeightDialog();
                break;

            //性别
            case R.id.rl_profile_init_sex:
                showSexDialog();
                break;

            //单位
            case R.id.rl_profile_init_unit:
                showUnitDialog();
                break;
        }
    }

    /**
     * 保存用户信息
     */
    void saveUserInfo() {
        UserName = edit_profile_int_name.getText().toString().trim();

        MyLog.i(TAG, "UserName = " + UserName);
        MyLog.i(TAG, "sexID = " + sexID);
        MyLog.i(TAG, "birthdayValue = " + birthdayValue);
        MyLog.i(TAG, "unitTag = " + unitTag);
        MyLog.i(TAG, "heightValue = " + heightValue);
        MyLog.i(TAG, "weightValue = " + weightValue);

        //昵称为空
        if (JavaUtil.checkIsNull(UserName)) {
            AppUtils.showToastStr(mContext, getString(R.string.enter_your_nickname));
            return;
        }

        //包含表情
        if (JavaUtil.containsEmoji(UserName)) {
            AppUtils.showToastStr(mContext, getString(R.string.enter_your_enjoy));
            return;
        }

        //性别为空
        if (sexID == null || sexID.equals("")) {
            AppUtils.showToastStr(mContext, getString(R.string.enter_sex));
            return;
        }

        //生日为空
        if (JavaUtil.checkIsNull(birthdayValue)) {
            AppUtils.showToastStr(mContext, getString(R.string.enter_birthday));
            return;
        }

        //单位为空
        if (unitTag == null || unitTag.equals("")) {
            AppUtils.showToastStr(mContext, getString(R.string.enter_user_unit));
            return;
        }


        //身高为空
        if (JavaUtil.checkIsNull(heightValue)) {
            AppUtils.showToastStr(mContext, getString(R.string.enter_user_height));
            return;
        }

        //体重为空
        if (JavaUtil.checkIsNull(weightValue)) {
            AppUtils.showToastStr(mContext, getString(R.string.enter_user_weight));
            return;
        }


        if (unitTag.equals("0")) {
            mUserSetTools.set_user_unit_type(true); //公英制
        } else {
            mUserSetTools.set_user_unit_type(false); //公英制
        }
        mUserSetTools.set_user_nickname(UserName);//昵称
        mUserSetTools.set_user_height(Integer.valueOf(heightValue));//身高
        mUserSetTools.set_user_weight(Integer.valueOf(weightValue));//体重
        mUserSetTools.set_user_birthday(birthdayValue);//生日
        mUserSetTools.set_user_sex(Integer.valueOf(sexID));//性别

        UserData mUserData = new UserData();
        mUserData.setNikname(mUserSetTools.get_user_nickname());
        mUserData.setHeight(String.valueOf(mUserSetTools.get_user_height()));
        mUserData.setWeight(String.valueOf(mUserSetTools.get_user_weight()));
        mUserData.setBirthday(mUserSetTools.get_user_birthday());
        mUserData.setSex(String.valueOf(mUserSetTools.get_user_sex()));

        waitDialog.show(getString(R.string.loading0));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //上传用户信息
                uploadUserInfo(mUserData);
            }
        }, Constants.serviceHandTime);

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

        if (sexID == null || sexID.equals("")) {
//            sexID = "0";
        }

        final String sexIDInit = sexID;

        List<String> dataSex = new ArrayList<String>();

        dataSex.add(getString(R.string.boy));
        dataSex.add(getString(R.string.girl));


        if (sexID != null) {
            if (sexID.equals("0")) {
                pv_sex.setData(dataSex, 0);
            } else {
                pv_sex.setData(dataSex, 1);
            }
        } else {
            pv_sex.setData(dataSex, 0);
        }


        pv_sex.setOnSelectListener(new onSelectListener() {

            @Override
            public void onSelect(String text) {

                MyLog.i(TAG, "性别弹框 = text = " + text);
                if (text.equals(getString(R.string.boy))) {
                    sexID = "0";
                } else {
                    sexID = "1";
                }

            }
        });


        view.findViewById(R.id.tv_sex_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sexID = sexIDInit;
                dialog.cancel();
            }
        });
        view.findViewById(R.id.tv_sex_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sexID != null) {
                    if (sexID.equals("0")) {
                        tv_profile_init_sex.setText(R.string.boy);
                    } else {
                        tv_profile_init_sex.setText(R.string.girl);
                    }
                } else {
                    sexID = "0";
                    tv_profile_init_sex.setText(R.string.boy);
                }

                dialog.cancel();
                MyLog.i(TAG, "确定  sexID = " + sexID);

            }
        });

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


    }


    /**
     * 公英制的选择
     */
    private void showUnitDialog() {
        // TODO Auto-generated method stub
        View view = getLayoutInflater().inflate(R.layout.dialog_unit, null);
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

        PickerView pv_unit = (PickerView) view.findViewById(R.id.pv_unit);


        if (unitTag == null || unitTag.equals("")) {
//            unitTag = "0";
        }
        final String unitTagINit = unitTag;


        List<String> dataSex = new ArrayList<String>();

        dataSex.add(getString(R.string.Metric));
        dataSex.add(getString(R.string.Inch));


        if (unitTag != null) {
            if (unitTag.equals("0")) {
                pv_unit.setData(dataSex, 0);
            } else {
                pv_unit.setData(dataSex, 1);
            }
        } else {
            pv_unit.setData(dataSex, 0);
        }


        pv_unit.setOnSelectListener(new onSelectListener() {

            @Override
            public void onSelect(String text) {
                MyLog.i(TAG, "性别弹框 = text = " + text);
                if (text.equals(getString(R.string.Metric))) {
                    unitTag = "0";
                } else {
                    unitTag = "1";
                }
            }
        });


        view.findViewById(R.id.tv_unit_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                unitTag = unitTagINit;
                dialog.cancel();
            }
        });
        view.findViewById(R.id.tv_unit_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unitTag == null) {
                    unitTag = "0";
                }
                updateUnit();
                dialog.cancel();
                MyLog.i(TAG, "确定  unitTag = " + unitTag);
            }
        });

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


    }


    void updateUnit() {

        if (unitTag.equals("0")) {
            mUserSetTools.set_user_unit_type(true);
            tv_profile_init_unit.setText(R.string.Metric);

            if (!TextUtils.isEmpty(heightValue)) {
                tv_profile_init_height.setText(heightValue + getString(R.string.centimeter));
            }

            if (!TextUtils.isEmpty(weightValue)) {
                tv_profile_init_weight.setText(weightValue + getString(R.string.kg));
            }

        } else {

            mUserSetTools.set_user_unit_type(false);
            tv_profile_init_unit.setText(R.string.Inch);

            if (!TextUtils.isEmpty(heightValue)) {

                tv_profile_init_height.setText(MyUtils.CmToInString(heightValue) + getString(R.string.unit_in));
            }

            if (!TextUtils.isEmpty(weightValue)) {
                tv_profile_init_weight.setText(MyUtils.KGToLBString(weightValue, this) + getString(R.string.unit_lb));
            }


        }


    }

    /**
     * 生日的dialog
     */
    private void showBirthdayDialog() {


        //当前生日如果为空-赋默认值
        if (birthdayValue == null || birthdayValue.equals("")) {
//            birthdayValue = DefaultVale.USER_BIRTHDAY;
        }


        if (!TextUtils.isEmpty(birthdayValue)) {
            if (birthdayValue.contains("-")) {
                year = Integer.parseInt(birthdayValue.split("-")[0]);
                month = Integer.parseInt(birthdayValue.split("-")[1]);
                day = Integer.parseInt(birthdayValue.split("-")[2]);
                birthdayValue = year + "-" + month + "-" + day;
            } else {
                year = Integer.parseInt(birthdayValue.split(".")[0]);
                month = Integer.parseInt(birthdayValue.split(".")[1]);
                day = Integer.parseInt(birthdayValue.split(".")[2]);
                birthdayValue = year + "-" + month + "-" + day;
            }
        } else {
//            birthdayValue = MyTime.getTime();
            year = Integer.parseInt(DefaultVale.USER_BIRTHDAY.split("-")[0]);
            month = Integer.parseInt(DefaultVale.USER_BIRTHDAY.split("-")[1]);
            day = Integer.parseInt(DefaultVale.USER_BIRTHDAY.split("-")[2]);
        }


        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                // 当用户选择日期的时候会触发
//                      2013年-4月-5日
                int myear = arg1;
                int mmonth = arg2 + 1;
                int mday = arg3;
                String mbirthdayValue = myear + "-" + mmonth + "-" + mday;
                MyLog.i(TAG, "生日 = " + mbirthdayValue);

                if (MyTime.getIsOldTime(mbirthdayValue)) {
                    year = arg1;
                    month = arg2 + 1;
                    day = arg3;
                    birthdayValue = year + "-" + month + "-" + day;

                    String[] time = birthdayValue.split("-");
                    tv_profile_init_birthday.setText(time[1] + "/" + time[2] + "/" + time[0]);
//                    dialog.cancel();
                } else {
                    AppUtils.showToast(mContext, R.string.birthday_error);
                }

            }
        }, year, month - 1, day);


        datePickerDialog.show();


    }


    /**
     * 体重的dialog
     */
    private void showWeightDialog() {
        MyLog.i(TAG, "公英制类型 = " + mUserSetTools.get_user_unit_type());

        final String weightValueInit = weightValue;

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
//                weightValue = String.valueOf(DefaultVale.USER_WEIGHT);
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


            if (TextUtils.isEmpty(weightValue) || MyUtils.KGToLBInt(weightValue, this) < 44 || MyUtils.KGToLBInt(weightValue, this) > 552) {
                pv_weight.setData(data, 99);
//                weightValue = String.valueOf(DefaultVale.USER_WEIGHT);
            } else {
                int index = MyUtils.KGToLBInt(weightValue, this) - 44;
                pv_weight.setData(data, index);
            }
            pv_weight.setOnSelectListener(new onSelectListener() {

                @Override
                public void onSelect(String text) {
                    mUserSetTools.set_weight_disparity(MyUtils.getWeightDisparity(Integer.valueOf(text)));
                    weightValue = MyUtils.LBToKGString(text);

                }
            });

        }

        view.findViewById(R.id.tv_weight_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                weightValue = weightValueInit;
                dialog.cancel();
            }
        });
        view.findViewById(R.id.tv_weight_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weightValue == null) {
                    weightValue = String.valueOf(DefaultVale.USER_WEIGHT);
                }
                if (mUserSetTools.get_user_unit_type()) {
                    tv_profile_init_weight.setText(weightValue + getString(R.string.kg));
                } else {
                    tv_profile_init_weight.setText(MyUtils.KGToLBString(weightValue, mContext) + getString(R.string.unit_lb));
                }

                dialog.cancel();

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

        final String heightValueInit = heightValue;

        MyLog.i(TAG, "公英制类型 = " + mUserSetTools.get_user_unit_type());

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
        TextView tv_height_unit_dialog = (TextView) view.findViewById(R.id.tv_height_unit_dialog);

        PickerView pv_height_ft = (PickerView) view.findViewById(R.id.pv_height_ft);
        PickerView pv_height_in = (PickerView) view.findViewById(R.id.pv_height_in);
        RelativeLayout layout1 = view.findViewById(R.id.layout1);
        LinearLayout layout2 = view.findViewById(R.id.layout2);

        List<String> dataHeight = new ArrayList<String>();

        if (mUserSetTools.get_user_unit_type()) {

            tv_height_unit_dialog.setText(getString(R.string.centimeter));

            for (int i = 70; i < 251; i++) {
                dataHeight.add("" + i);
            }
            if (TextUtils.isEmpty(heightValue) || Integer.valueOf(heightValue) < 70 || Integer.valueOf(heightValue) > 250) {
//                heightValue = String.valueOf(DefaultVale.USER_HEIGHT);
                pv_height.setData(dataHeight, 100);

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

            String height = "0";
            if (TextUtils.isEmpty(heightValue) || MyUtils.CmToInInt(heightValue) < 24 || MyUtils.CmToInInt(heightValue) > 107) {
                height = "170";
            } else {
                height = heightValue;
            }
            int in = MyUtils.CmToInInt(height);
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
                heightValue = heightValueInit;
                dialog.cancel();
            }
        });
        view.findViewById(R.id.tv_height_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (heightValue == null) {
                    heightValue = String.valueOf(DefaultVale.USER_HEIGHT);
                }
                if (mUserSetTools.get_user_unit_type()) {
                    tv_profile_init_height.setText(heightValue + getString(R.string.centimeter));
                } else {
                    int in = MyUtils.CmToInInt(heightValue);
                    pvFt = in / 12;
                    pvIn = in % 12;
                    tv_profile_init_height.setText(String.format("%1$2d'%2$2d\"", pvFt, pvIn));
                }
                dialog.cancel();

            }
        });


        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;

        } else {
            return super.onKeyDown(keyCode, event);
        }

    }


    /**
     * 上传用户到服务器
     */
    private void uploadUserInfo(UserData mUserData) {

//        waitDialog.show(getString(R.string.loading0));

        RequestInfo mRequestInfo = RequestJson.modifyUserInfo(mContext,mUserData,false);

        MyLog.i(TAG, "请求接口-修改个人信息 mRequestInfo = " + mRequestInfo.toString());

        NewVolleyRequest.RequestPost(mRequestInfo, TAG,
                new VolleyInterface(mContext, VolleyInterface.mListener, VolleyInterface.mErrorListener) {

                    @Override
                    public void onMySuccess(JSONObject result) {
                        // TODO Auto-generated method stub

                        waitDialog.close();

                        MyLog.i(TAG, "请求接口-修改个人信息  result = " + result.toString());

                        UserBean mUserBean = ResultJson.UserBean(result);


                        //请求成功
                        if (mUserBean.isRequestSuccess()) {

                            //提交成功
                            if (mUserBean.uploadUserSuccess() == 1) {
                                MyLog.i(TAG, "请求接口-修改个人信息 成功");
                                GoToSkinColour();

                            }
                            //提交失败
                            else if (mUserBean.uploadUserSuccess() == 0) {
                                MyLog.i(TAG, "请求接口-修改个人信息 失败");
                                AppUtils.showToast(mContext, R.string.data_try_again_code1);

                            }
                            //
                            else {
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
     * 跳转到肤色选择
     */
    void GoToSkinColour() {
//        Intent mIntent = new Intent(mContext, SkinColourActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString(IntentConstants.IntentSkinColur, IntentConstants.IntentSkinColurTypeIntput);
//        mIntent.putExtras(bundle);
//        startActivity(mIntent);
        mUserSetTools.set_user_login(true);//登录状态
        startActivity(new Intent(mContext, HomeActivity.class));
        manager.finishAllActivity();
    }


}
