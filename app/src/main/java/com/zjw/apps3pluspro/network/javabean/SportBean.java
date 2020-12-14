package com.zjw.apps3pluspro.network.javabean;

import com.zjw.apps3pluspro.application.BaseApplication;
import com.zjw.apps3pluspro.network.ResultJson;
import com.zjw.apps3pluspro.sql.dbmanager.MovementInfoUtils;
import com.zjw.apps3pluspro.sql.entity.MovementInfo;
import com.zjw.apps3pluspro.utils.JavaUtil;
import com.zjw.apps3pluspro.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjw on 2018/3/19.
 */

public class SportBean {
    private static final String TAG = SportBean.class.getSimpleName();
    /**
     * result : 1
     * msg : 请求成功
     * code : 0000
     * data : [{"calorie":2,"distance":0.03,"id":3,"sportDate":"2019-05-08","sportRawdata":"0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,27,0,23,0,0,0,0","step":50,"userId":3},{"calorie":1,"distance":0.02,"id":4,"sportDate":"2019-05-09","sportRawdata":"0,0,0,0,0,0,0,0,0,31,0,0,0,0,0,0,0,0,0,0,0,0,0,0","step":31,"userId":3}]
     * codeMsg : 操作成功！
     */

    private int result;
    private String msg;
    private String code;
    private String codeMsg;
    private List<DataBean> data;


    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeMsg() {
        return codeMsg;
    }

    public void setCodeMsg(String codeMsg) {
        this.codeMsg = codeMsg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }


    public static class DataBean {
        /**
         * calorie : 2
         * distance : 0.03
         * id : 3
         * sportDate : 2019-05-08//通用
         * sportRawdata : 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,27,0,23,0,0,0,0
         * step : 50
         * status:1//上传数据用到了
         * userId : 3//通用
         */

        private int calorie;
        private double distance;
        private int id;
        private String sportDate;
        private String sportRawdata;
        private int step;
        private int status;
        private int userId;

        public int getCalorie() {
            return calorie;
        }

        public void setCalorie(int calorie) {
            this.calorie = calorie;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSportDate() {
            return sportDate;
        }

        public void setSportDate(String sportDate) {
            this.sportDate = sportDate;
        }

        public String getSportRawdata() {
            return sportRawdata;
        }

        public void setSportRawdata(String sportRawdata) {
            this.sportRawdata = sportRawdata;
        }

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }

    /**
     * 是否请求成功
     *
     * @return
     */
    public boolean isRequestSuccess() {
        if (getResult() == ResultJson.Result_success) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 上传运动数据
     *
     * @return
     */
    public int isUploadSportSuccess() {
        //修改成功
        if (ResultJson.Code_operation_success.equals(getCode())) {
            return 1;
        }
        //修改失败
        else if (ResultJson.Code_operation_fail.equals(getCode())) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * 获取运动数据
     *
     * @return
     */
    public int isGetSportSuccess() {
        //获取成功
        if (ResultJson.Code_operation_success.equals(getCode())) {
            return 1;
        }
        //获取失败
        else if (ResultJson.Code_operation_fail.equals(getCode())) {
            return 0;
        }
        //无数据
        else if (ResultJson.Code_no_data.equals(getCode())) {
            return 2;
        } else {
            return -1;
        }
    }


    public MovementInfo getMovementInfo(DataBean mDataBean) {


        MyLog.i(TAG, "请求接口-获取运动数据 getUserId = " + mDataBean.getUserId());
        MyLog.i(TAG, "请求接口-获取运动数据 getDate = " + mDataBean.getSportDate());
        MyLog.i(TAG, "请求接口-获取运动数据 getSportRawdata = " + mDataBean.getSportRawdata());
        MyLog.i(TAG, "请求接口-获取运动数据 getStep = " + mDataBean.getStep());
        MyLog.i(TAG, "请求接口-获取运动数据 getSportCalorie = " + mDataBean.getCalorie());
        MyLog.i(TAG, "请求接口-获取运动数据 getSportDistance = " + mDataBean.getDistance());

        String user_id = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getUserId())) ? String.valueOf(mDataBean.getUserId()) : "";
        String sport_date = !JavaUtil.checkIsNull(mDataBean.getSportDate()) ? mDataBean.getSportDate() : "";
        String sport_data = !JavaUtil.checkIsNull(mDataBean.getSportRawdata()) ? mDataBean.getSportRawdata() : ResultJson.Duflet_sport_data;
        String sport_step = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getStep())) ? String.valueOf(mDataBean.getStep()) : "0";
        String sport_calory = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getCalorie())) ? String.valueOf(mDataBean.getCalorie()) : "0";
        String sport_distance = !JavaUtil.checkIsNull(String.valueOf(mDataBean.getDistance())) ? String.valueOf(mDataBean.getDistance()) : "0";

        MovementInfo mMovementInfo = new MovementInfo();
        mMovementInfo.setUser_id(user_id);
        mMovementInfo.setData(sport_data);
        mMovementInfo.setTotal_step(sport_step);
        mMovementInfo.setCalorie(sport_calory);
        mMovementInfo.setDisance(sport_distance);
        mMovementInfo.setDate(sport_date);
        mMovementInfo.setSync_state("1");
        return mMovementInfo;


    }

    public static MovementInfo getNullMovementInfo(String date) {

        String sport_data = ResultJson.Duflet_sport_data;
        String sport_step = "0";
        String sport_calory = "0";
        String sport_distance = "0";

        MovementInfo mMovementInfo = new MovementInfo();
        mMovementInfo.setUser_id(BaseApplication.getUserId());
        mMovementInfo.setData(sport_data);
        mMovementInfo.setTotal_step(sport_step);
        mMovementInfo.setCalorie(sport_calory);
        mMovementInfo.setDisance(sport_distance);
        mMovementInfo.setDate(date);
        mMovementInfo.setSync_state("1");

        return mMovementInfo;

    }

    public List<MovementInfo> getMovementList(ArrayList<String> my_date_list, List<DataBean> data_list) {


        List<MovementInfo> movementInfo_list = new ArrayList<>();

        if (data_list.size() > 0) {
            for (int i = 0; i < data_list.size(); i++) {
                movementInfo_list.add(getMovementInfo(data_list.get(i)));
            }
        }


        ArrayList<String> new_list = new ArrayList<>();

        for (int i = 0; i < my_date_list.size(); i++) {

            boolean isYes = false;
            for (int j = 0; j < movementInfo_list.size(); j++) {

                if (my_date_list.get(i).equals(movementInfo_list.get(j).getDate())) {
                    isYes = true;
                    break;
                }
            }

            if (!isYes) {
                new_list.add(my_date_list.get(i));
            }
        }

        if (new_list.size() > 0) {
            for (int i = 0; i < new_list.size(); i++) {
                movementInfo_list.add(getNullMovementInfo(new_list.get(i)));
            }
        }


        return movementInfo_list;

    }


    public static void insertNullData(MovementInfoUtils mMovementInfoUtils, String date) {

        MovementInfo mMovementInfo = getNullMovementInfo(date);
        MyLog.i(TAG, "getNullmMovementInfo = " + mMovementInfo.toString());
        boolean isSuccess = mMovementInfoUtils.MyUpdateData(mMovementInfo);
        if (isSuccess) {
            MyLog.i(TAG, "插入运动表成功！");
        } else {
            MyLog.i(TAG, "插入运动表失败！");
        }
    }


    /**
     * 根据数据列表，插入空数据-为了处理，在后台查询不到数据做的。
     *
     * @param mMovementInfoUtils
     * @param my_date_list
     */
    public static void insertNullListData(MovementInfoUtils mMovementInfoUtils, ArrayList<String> my_date_list) {

        MyLog.i(TAG, "待处理 日期数组 = " + my_date_list);


        List<MovementInfo> movementInfo_list = new ArrayList<>();


        if (my_date_list.size() > 0) {
            for (int i = 0; i < my_date_list.size(); i++) {
                movementInfo_list.add(SportBean.getNullMovementInfo(my_date_list.get(i)));
            }
        }

        for (MovementInfo mMovementInfo : movementInfo_list) {
            MyLog.i(TAG, "解析数组 = movementInfo_list = " + mMovementInfo.toString());
        }

        boolean isSuccess = mMovementInfoUtils.insertInfoList(movementInfo_list);
        if (isSuccess) {
            MyLog.i(TAG, "插入多条运动表成功！");
        } else {
            MyLog.i(TAG, "插入多条运动表失败！");
        }


    }


}
