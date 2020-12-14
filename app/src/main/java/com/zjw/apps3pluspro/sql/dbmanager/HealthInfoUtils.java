package com.zjw.apps3pluspro.sql.dbmanager;

import android.content.Context;


import com.zjw.apps3pluspro.sql.entity.HealthInfo;
import com.zjw.apps3pluspro.sql.gen.HealthInfoDao;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjw on 2017/12/6.
 */

public class HealthInfoUtils {
    private final String TAG = HealthInfoUtils.class.getSimpleName();
    private DaoManager mManager;

    public HealthInfoUtils(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }


    /**
     * 插入一条数据
     *
     * @param info
     * @return boolean
     */
    public boolean MyUpdateData(HealthInfo info) {
        boolean flag = false;

//        List<HealthInfo> info_list = queryToTime(info.getUser_id(), info.getMeasure_time());
        List<HealthInfo> info_list = queryToTimeToSensorType(info.getUser_id(), info.getMeasure_time(), info.getSensor_type());

        if (info_list.size() >= 1) {
            MyLog.i(TAG, "有，更新数据");
            if (info_list.get(0).getHealth_heart().equals(info.getHealth_heart())
                    && info_list.get(0).getHealth_systolic().equals(info.getHealth_systolic())
                    && info_list.get(0).getHealth_diastolic().equals(info.getHealth_diastolic())
                    && info_list.get(0).getSensor_type().equals(info.getSensor_type())
                    && info_list.get(0).getIs_suppor_bp().equals(info.getIs_suppor_bp())
            ) {
                MyLog.i(TAG, "有，数据重复不处理");
            } else {
//                MyLog.i(TAG, "有，数据不重复，更新数据");
//                info.set_id(info_list.get(0).get_id());
//                flag = updateData(info);
            }
        } else {
            MyLog.i(TAG, "没有，插入一条");
            flag = insertData(info);
        }
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     *
     * @param infoList
     * @return
     */
    public boolean insertInfoList(final List<HealthInfo> infoList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (HealthInfo info : infoList) {
                        MyLog.i(TAG, "插入多条数据 = " + info.toString());
                        MyUpdateData(info);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 根据时间点查询健康数据
     *
     * @param time
     * @return boolean
     */
    public HealthInfo MyQueryToTime(String user_id, String time) {

        HealthInfo info = new HealthInfo();

        List<HealthInfo> info_list = queryToTime(user_id, time);

        if (info_list.size() >= 1) {
            info = info_list.get(0);
            return info;
        } else {

            return null;
        }

    }

    /**
     * 查询某一天最新的一条数据
     *
     * @param user_id
     * @return
     */
    public HealthInfo MyQueryToDate(String user_id, String date, boolean is_support_ecg) {

        List<HealthInfo> info_list = queryToDate(user_id, date, is_support_ecg);

        if (info_list.size() >= 1) {
            MyLog.i(TAG, "有，更新数据");
            HealthInfo info = info_list.get(0);
            return info;
        } else {

            return null;
        }
    }


    /**
     * 查询某一天的所有数据
     *
     * @param user_id
     * @return
     */
    public List<HealthInfo> queryToDate(String user_id, String date, boolean is_supprt_ecg) {

        QueryBuilder<HealthInfo> queryBuilder = mManager.getDaoSession().queryBuilder(HealthInfo.class);

        if (is_supprt_ecg) {
            return queryBuilder
                    .where(HealthInfoDao.Properties.User_id.eq(user_id))
                    //类型等于0或者等于1或者等于4或者等于5
                    .whereOr(HealthInfoDao.Properties.Sensor_type.eq("0"), HealthInfoDao.Properties.Sensor_type.eq("1"),
                            HealthInfoDao.Properties.Sensor_type.eq("4"))
                    //大于等于
                    .where(HealthInfoDao.Properties.Measure_time.ge(date + " 00:00:00"))
                    //小于等于
                    .where(HealthInfoDao.Properties.Measure_time.le(date + " 23:59:59"))
                    //降序
                    .orderDesc(HealthInfoDao.Properties.Measure_time)
                    .list();
        } else {
            return queryBuilder
                    .where(HealthInfoDao.Properties.User_id.eq(user_id))
                    //类型
                    .whereOr(HealthInfoDao.Properties.Sensor_type.eq("2"), HealthInfoDao.Properties.Sensor_type.eq("6"))
                    //大于等于
                    .where(HealthInfoDao.Properties.Measure_time.ge(date + " 00:00:00"))
                    //小于等于
                    .where(HealthInfoDao.Properties.Measure_time.le(date + " 23:59:59"))
                    //降序
                    .orderDesc(HealthInfoDao.Properties.Measure_time)
                    .list();
        }
    }

    /**
     * 查询某时间段的所有数据
     *
     * @param user_id
     * @return
     */
    public List<HealthInfo> queryToDateList(String user_id, String start_date, String end_date, boolean is_supprt_ecg) {

        QueryBuilder<HealthInfo> queryBuilder = mManager.getDaoSession().queryBuilder(HealthInfo.class);

        if (is_supprt_ecg) {
            return queryBuilder
                    .where(HealthInfoDao.Properties.User_id.eq(user_id))
                    //类型等于0或者等于1或者等于4或者等于5
                    .whereOr(HealthInfoDao.Properties.Sensor_type.eq("0"), HealthInfoDao.Properties.Sensor_type.eq("1"),
                            HealthInfoDao.Properties.Sensor_type.eq("4"))
                    //大于等于
                    .where(HealthInfoDao.Properties.Measure_time.ge(start_date + " 00:00:00"))
                    //小于等于
                    .where(HealthInfoDao.Properties.Measure_time.le(end_date + " 23:59:59"))
                    //降序
                    .orderDesc(HealthInfoDao.Properties.Measure_time)
                    .list();
        } else {
            return queryBuilder
                    .where(HealthInfoDao.Properties.User_id.eq(user_id))
                    //类型
                    .whereOr(HealthInfoDao.Properties.Sensor_type.eq("2"), HealthInfoDao.Properties.Sensor_type.eq("6"))
                    //大于等于
                    .where(HealthInfoDao.Properties.Measure_time.ge(start_date + " 00:00:00"))
                    //小于等于
                    .where(HealthInfoDao.Properties.Measure_time.le(end_date + " 23:59:59"))
                    //降序
                    .orderDesc(HealthInfoDao.Properties.Measure_time)
                    .list();
        }


    }


    /**
     * 查询每一天最新的一条
     *
     * @return
     */
    public List<HealthInfo> queryToDayLastDateList(String user_id, ArrayList<String> week_list, boolean is_support_ecg) {

        List<HealthInfo> health_list = new ArrayList<>();
        for (int i = 0; i < week_list.size(); i++) {

            HealthInfo mHealthInfo = MyQueryToDate(user_id, week_list.get(i), is_support_ecg);

            if (mHealthInfo != null) {
                health_list.add(mHealthInfo);
            }

        }
        return health_list;

    }


    /**
     * 更新一条数据-修改EcgData和PpgData
     *
     * @param user_id
     * @param measure_time
     * @param ecg_data
     * @param ppg_data
     * @return
     */
    public boolean MyUpdateEcgPpgData(String user_id, String measure_time, String ecg_data, String ppg_data) {
        boolean flag = false;

        List<HealthInfo> info_list = queryToTime(user_id, measure_time);

        if (info_list.size() >= 1) {
            MyLog.i(TAG, "有，更新数据");

            HealthInfo info = info_list.get(0);
            info.setEcg_data(ecg_data);
            info.setPpg_data(ppg_data);
            flag = updateData(info);

        }
        return flag;
    }


    //==================修改后台状态==============

    /**
     * 根据用户ID来查询 服务状态为0的前多少条数据
     *
     * @param user_id
     * @return List<HealthInfo>
     */
    public List<HealthInfo> MyQueryToSync(String user_id, int count) {

        QueryBuilder<HealthInfo> queryBuilder = mManager.getDaoSession().queryBuilder(HealthInfo.class);


        List<HealthInfo> movement_list = queryBuilder.where(HealthInfoDao.Properties.User_id.eq(user_id), HealthInfoDao.Properties.Sync_state.eq("0"))
                .orderDesc(HealthInfoDao.Properties.Measure_time)//降序
                .list();


        List<HealthInfo> result_list = new ArrayList<>();

        if (movement_list.size() > count) {

            for (int i = 0; i < count; i++) {
                result_list.add(movement_list.get(i));
            }
        } else {
            result_list = movement_list;
        }

        return result_list;

    }


    /**
     * 根据用户ID来查询 服务状态为0的所有数据
     *
     * @param user_id
     * @return List<HealthInfo>
     */
    public List<HealthInfo> MyQueryToSyncAll(String user_id) {
        QueryBuilder<HealthInfo> queryBuilder = mManager.getDaoSession().queryBuilder(HealthInfo.class);
        List<HealthInfo> info_list = queryBuilder.where(HealthInfoDao.Properties.User_id.eq(user_id), HealthInfoDao.Properties.Sync_state.eq("0"))
                .limit(10)
                .list();
        return info_list;
    }

//    public boolean MyupdateToSyncDataId(String user_id, String measure_time, String data_id) {
//        boolean flag = true;
//        List<HealthInfo> info_list = queryToTime(user_id, measure_time);
//        if (info_list.size() > 0) {
//            for (HealthInfo info : info_list) {
//                info.setData_id(data_id);
//                info.setSync_state("1");
//            }
//            try {
//                mManager.getDaoSession().getHealthInfoDao().updateInTx(info_list);
//            } catch (Exception e) {
//                e.printStackTrace();
//                flag = false;
//            }
//        }
//        return flag;
//    }

    /**
     * 修改某一条的网络状态为1
     *
     * @param info
     * @return
     */

    public boolean MyUpdateToSyncOne(HealthInfo info) {
        boolean flag = false;
        try {
            info.setSync_state("1");
            mManager.getDaoSession().update(info);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;

    }

    /**
     * 修改列表的网络状态为1
     *
     * @param infoList
     * @return
     */

    public boolean MyUpdateToSync(final List<HealthInfo> infoList) {
        boolean flag = true;
        if (infoList != null && infoList.size() > 0) {
            try {
                for (HealthInfo info : infoList) {
                    info.setSync_state("1");
                }
                mManager.getDaoSession().getHealthInfoDao().updateInTx(infoList);
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
            }
        }
        return flag;
    }


    //==================基础方法==============

    /**
     * 插入数据
     *
     * @param info
     * @return
     */
    public boolean insertData(HealthInfo info) {
        info.setWarehousing_time(String.valueOf(System.currentTimeMillis()));
        MyLog.i(TAG, "插入数据 = info = " + info.toString());
        boolean flag = mManager.getDaoSession().getHealthInfoDao().insert(info) == -1 ? false : true;
        return flag;
    }


    /**
     * 更新一条数据（系统ID）
     *
     * @param info
     * @return
     */
    public boolean updateData(HealthInfo info) {
        info.setWarehousing_time(String.valueOf(System.currentTimeMillis()));
        boolean flag = false;
        try {
            mManager.getDaoSession().update(info);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 根据用户ID 和 时间点
     *
     * @param user_id
     * @return
     */
    public List<HealthInfo> queryToTime(String user_id, String time) {

        QueryBuilder<HealthInfo> queryBuilder = mManager.getDaoSession().queryBuilder(HealthInfo.class);
        return queryBuilder.where(HealthInfoDao.Properties.User_id.eq(user_id), HealthInfoDao.Properties.Measure_time.eq(time)).list();

    }

    /**
     * 根据用户ID 和 时间点
     *
     * @param user_id
     * @return
     */
    public List<HealthInfo> queryToTimeToSensorType(String user_id, String time, String sensor_type) {
        QueryBuilder<HealthInfo> queryBuilder = mManager.getDaoSession().queryBuilder(HealthInfo.class);
        return queryBuilder.where(
                HealthInfoDao.Properties.User_id.eq(user_id),
                HealthInfoDao.Properties.Sensor_type.eq(sensor_type),
                HealthInfoDao.Properties.Measure_time.eq(time))
                .list();
    }


    //==================暂时不用==============

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<HealthInfo> queryAllData() {
        return mManager.getDaoSession().loadAll(HealthInfo.class);
    }

    /**
     * 删除所有记录
     *
     * @return
     */
    public boolean deleteAllData() {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().deleteAll(HealthInfo.class);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 删除一条记录
     *
     * @return
     */
    public boolean deleteData(HealthInfo info) {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(info);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 插入多条数据，在子线程操作
     *
     * @param infoList
     * @return
     */
    public boolean MyInsertOherData(final List<HealthInfo> infoList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (HealthInfo info : infoList) {
                        MyLog.i(TAG, "插入多条数据 = " + info.toString());
                        MyUpdateData(info);

                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}