package com.zjw.apps3pluspro.module.device.entity;


import com.zjw.apps3pluspro.utils.MyUtils;

/**
 * 吃药提醒
 */
public class DurgModel {


    public static final String KEY_PERIOD = "medical_period";
    public static final String KEY_STARTH = "medical_starth";
    public static final String KEY_STARTM = "medical_startm";
    public static final String KEY_ENDH = "medical_endh";
    public static final String KEY_ENDM = "medical_endm";
    public static final String KEY_MEDICAL = "medical";


    int MedicineStartHourTime;
    int MedicineStartMinTime;
    int MedicineEndHourTime;
    int MedicineEndMinTime;
    int MedicineCycleTime;
    boolean MedicineEnable;


    public int getMedicineStartHourTime() {
        return MedicineStartHourTime;
    }

    public void setMedicineStartHourTime(int medicineStartHourTime) {
        MedicineStartHourTime = medicineStartHourTime;
    }

    public int getMedicineStartMinTime() {
        return MedicineStartMinTime;
    }

    public void setMedicineStartMinTime(int medicineStartMinTime) {
        MedicineStartMinTime = medicineStartMinTime;
    }

    public int getMedicineEndHourTime() {
        return MedicineEndHourTime;
    }

    public void setMedicineEndHourTime(int medicineEndHourTime) {
        MedicineEndHourTime = medicineEndHourTime;
    }

    public int getMedicineEndMinTime() {
        return MedicineEndMinTime;
    }

    public void setMedicineEndMinTime(int medicineEndMinTime) {
        MedicineEndMinTime = medicineEndMinTime;
    }

    public int getMedicineCycleTime() {
        return MedicineCycleTime;
    }

    public void setMedicineCycleTime(int medicineCycleTime) {
        MedicineCycleTime = medicineCycleTime;
    }

    public boolean isMedicineEnable() {
        return MedicineEnable;
    }

    public void setMedicineEnable(boolean medicineEnable) {
        MedicineEnable = medicineEnable;
    }


    @Override
    public String toString() {
        return "DurgModel{" +
                "MedicineStartHourTime=" + MedicineStartHourTime +
                ", MedicineStartMinTime=" + MedicineStartMinTime +
                ", MedicineEndHourTime=" + MedicineEndHourTime +
                ", MedicineEndMinTime=" + MedicineEndMinTime +
                ", MedicineCycleTime=" + MedicineCycleTime +
                ", MedicineEnable=" + MedicineEnable +
                '}';
    }

    public DurgModel() {
        super();
    }

    public DurgModel(int start_h_time, int start_m_time, int end_h_time, int end_m_time, int cycle_time, boolean enabnle) {

        super();
        setMedicineStartHourTime(start_h_time);
        setMedicineStartMinTime(start_m_time);
        setMedicineEndHourTime(end_h_time);
        setMedicineEndMinTime(end_m_time);
        setMedicineCycleTime(cycle_time);
        setMedicineEnable(enabnle);

    }

    public String getStartTime() {
        return MyUtils.MyFormatTime(MedicineStartHourTime) + ":" + MyUtils.MyFormatTime(MedicineStartMinTime);
    }

    public String getEndTime() {
        return MyUtils.MyFormatTime(MedicineEndHourTime) + ":" + MyUtils.MyFormatTime(MedicineEndMinTime);
    }


    /**
     * 提醒设置，比较时间大小
     */
    public boolean isOldTime() {

        boolean result = false;

        if (MedicineStartHourTime > MedicineEndHourTime) {
            return true;
        } else if (MedicineStartHourTime == MedicineEndHourTime && MedicineStartMinTime > MedicineEndMinTime) {
            return true;
        }

        return result;
    }


}
