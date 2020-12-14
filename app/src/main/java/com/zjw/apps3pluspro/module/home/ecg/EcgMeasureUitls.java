package com.zjw.apps3pluspro.module.home.ecg;

import android.widget.TextView;

import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.DefaultVale;

/**
 * 测量工具类
 */
public class EcgMeasureUitls {


    public static boolean isDataiDentical(int[] data) {
        boolean result = false;

        if (data != null && data.length >= 9) {
            if (data[0] == data[1] &&
                    data[0] == data[2] &&
                    data[0] == data[3] &&
                    data[0] == data[4] &&
                    data[0] == data[5] &&
                    data[0] == data[6] &&
                    data[0] == data[7] &&
                    data[0] == data[8]) {
                result = true;
            } else {
                result = false;
            }
        }
        return result;

    }

    //================健康指数建议================

    /**
     * 越高越不好 疲劳指数和身心负荷
     *
     * @param textView
     * @param grade1
     * @param grade2
     * @param str1
     * @param str2
     * @param str3
     * @param Number
     */
    public static void updateTextStateUpdate(TextView textView, int grade1, int grade2, String str1, String str2, String str3, int Number) {
        if (Number < grade1) {
            textView.setText(str3);
        } else if (Number >= grade1 && Number < grade2) {
            textView.setText(str2);
        } else if (Number >= grade2) {
            textView.setText(str1);
        }
    }

    /**
     * 越高越好 身体素质，心脏功能
     *
     * @param textView
     * @param grade1
     * @param grade2
     * @param str1
     * @param str2
     * @param str3
     * @param Number
     */
    public static void updateTextStateDown(TextView textView, int grade1, int grade2, String str1, String str2, String str3, int Number) {
        if (Number < grade1) {
            textView.setText(str1);
        } else if (Number >= grade1 && Number < grade2) {
            textView.setText(str2);
        } else if (Number >= grade2) {
            textView.setText(str3);
        }
    }

    public static int getMeasureSBP(UserSetTools mUserSetTools, int measure_heart) {


        int calibration_heart = (mUserSetTools.get_calibration_heart() > 0) ? mUserSetTools.get_calibration_heart() : DefaultVale.USER_HEART;
        int calibration_sbp = (mUserSetTools.get_calibration_systolic() > 0) ? mUserSetTools.get_calibration_systolic() : DefaultVale.USER_SYSTOLIC;

        int now_gaoya = 0;
        int user_gaoya = 0;
        int result_gaoya = 0;


        if (measure_heart == 0) {
            return 0;
        }


        now_gaoya = (int) ((0.0318 * (measure_heart) + 5.12) / (0.133 * 0.44));
        user_gaoya = (int) ((0.0318 * (calibration_heart) + 5.12) / (0.133 * 0.44));
        result_gaoya = now_gaoya - user_gaoya + calibration_sbp;


        return result_gaoya;
    }


    public static int getMeasureDBP(UserSetTools mUserSetTools, int measure_heart) {

        int calibration_heart = (mUserSetTools.get_calibration_heart() > 0) ? mUserSetTools.get_calibration_heart() : DefaultVale.USER_HEART;
        int calibration_sbp = (mUserSetTools.get_calibration_systolic() > 0) ? mUserSetTools.get_calibration_systolic() : DefaultVale.USER_SYSTOLIC;

        int now_gaoya = 0;
        int user_gaoya = 0;
        int result_gaoya = 0;
        int result_diya = 0;


        if (measure_heart == 0) {
            return 0;
        }


        now_gaoya = (int) ((0.0318 * (measure_heart) + 5.12) / (0.133 * 0.44));
        user_gaoya = (int) ((0.0318 * (calibration_heart) + 5.12) / (0.133 * 0.44));
        result_gaoya = now_gaoya - user_gaoya + calibration_sbp;

        result_diya = (int) (result_gaoya * 0.4 / 0.62);
        return result_diya;
    }



}
