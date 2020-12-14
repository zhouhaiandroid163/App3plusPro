package com.zjw.apps3pluspro.utils;

import com.zjw.apps3pluspro.sharedpreferences.UserSetTools;
import com.zjw.apps3pluspro.utils.log.MyLog;

public class CalibrationUtils {

    public static void handleCalibrationDialogGrade(String TAG, UserSetTools mUserSetTools, int CalibrationGrade) {
        MyLog.i(TAG, "测量校准 = 等级校准 = CalibrationGrade = " + CalibrationGrade);

        mUserSetTools.set_blood_grade(CalibrationGrade);

        switch (CalibrationGrade) {
            case 0:
                mUserSetTools.set_calibration_systolic(DefaultVale.USER_SYSTOLIC_STATE0);
                mUserSetTools.set_calibration_diastolic(DefaultVale.USER_DIASTOLIC_STATE0);
                break;
            case 1:
                mUserSetTools.set_calibration_systolic(DefaultVale.USER_SYSTOLIC_STATE1);
                mUserSetTools.set_calibration_diastolic(DefaultVale.USER_DIASTOLIC_STATE1);
                break;
            case 2:
                mUserSetTools.set_calibration_systolic(DefaultVale.USER_SYSTOLIC_STATE2);
                mUserSetTools.set_calibration_diastolic(DefaultVale.USER_DIASTOLIC_STATE2);
                break;
            case 3:
                mUserSetTools.set_calibration_systolic(DefaultVale.USER_SYSTOLIC_STATE3);
                mUserSetTools.set_calibration_diastolic(DefaultVale.USER_DIASTOLIC_STATE3);
                break;
            case 4:
                mUserSetTools.set_calibration_systolic(DefaultVale.USER_SYSTOLIC_STATE4);
                mUserSetTools.set_calibration_diastolic(DefaultVale.USER_DIASTOLIC_STATE4);
                break;
        }

//        gotoPpgMeasure(false);

    }
}
