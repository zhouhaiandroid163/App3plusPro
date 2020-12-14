package com.zjw.apps3pluspro.sql.dbmanager;

import android.content.Context;

import com.zjw.apps3pluspro.sql.entity.MeasureSpo2Info;
import com.zjw.apps3pluspro.sql.gen.MeasureSpo2InfoDao;
import com.zjw.apps3pluspro.utils.log.MyLog;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjw on 2017/12/6.
 */

public class MeasureSpo2InfoUtils {
    private final String TAG = MeasureSpo2InfoUtils.class.getSimpleName();
    private DaoManager mManager;

    public MeasureSpo2InfoUtils(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }


    /**
     * 插入一条数据
     *
     * @param info
     * @return boolean
     */
    public boolean MyUpdateData(MeasureSpo2Info info) {
        boolean flag = false;

        List<MeasureSpo2Info> info_list = queryToTime(info.getUser_id(), info.getMeasure_time());

        if (info_list.size() >= 1) {
            MyLog.i(TAG, "有，更新数据");
            if (info_list.get(0).getMeasure_spo2().equals(info.getMeasure_spo2())
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
    public boolean insertInfoList(final List<MeasureSpo2Info> infoList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (MeasureSpo2Info info : infoList) {
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
    public MeasureSpo2Info MyQueryToTime(String user_id, String time) {

        MeasureSpo2Info info = new MeasureSpo2Info();

        List<MeasureSpo2Info> info_list = queryToTime(user_id, time);

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
    public MeasureSpo2Info MyQueryToDate(String user_id, String date) {

        List<MeasureSpo2Info> info_list = queryToDate(user_id, date);

        if (info_list.size() >= 1) {
            MyLog.i(TAG, "有，更新数据");
            MeasureSpo2Info info = info_list.get(0);
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
    public List<MeasureSpo2Info> queryToDate(String user_id, String date) {
        QueryBuilder<MeasureSpo2Info> queryBuilder = mManager.getDaoSession().queryBuilder(MeasureSpo2Info.class);
        return queryBuilder
                .where(MeasureSpo2InfoDao.Properties.User_id.eq(user_id))
                //大于等于
                .where(MeasureSpo2InfoDao.Properties.Measure_time.ge(date + " 00:00:00"))
                //小于等于
                .where(MeasureSpo2InfoDao.Properties.Measure_time.le(date + " 23:59:59"))
                //降序
                .orderDesc(MeasureSpo2InfoDao.Properties.Measure_time).list();
    }

    /**
     * 查询某时间段的所有数据
     *
     * @param user_id
     * @return
     */
    public List<MeasureSpo2Info> queryToDateList(String user_id, String start_date, String end_date) {

        QueryBuilder<MeasureSpo2Info> queryBuilder = mManager.getDaoSession().queryBuilder(MeasureSpo2Info.class);

        return queryBuilder
                .where(MeasureSpo2InfoDao.Properties.User_id.eq(user_id))
                //大于等于
                .where(MeasureSpo2InfoDao.Properties.Measure_time.ge(start_date + " 00:00:00"))
                //小于等于
                .where(MeasureSpo2InfoDao.Properties.Measure_time.le(end_date + " 23:59:59"))
                //降序
                .orderDesc(MeasureSpo2InfoDao.Properties.Measure_time)
                .list();


    }


    /**
     * 查询每一天最新的一条
     *
     * @return
     */
    public List<MeasureSpo2Info> queryToDayLastDateList(String user_id, ArrayList<String> week_list) {

        List<MeasureSpo2Info> health_list = new ArrayList<>();
        for (int i = 0; i < week_list.size(); i++) {

            MeasureSpo2Info mMeasureSpo2Info = MyQueryToDate(user_id, week_list.get(i));

            if (mMeasureSpo2Info != null) {
                health_list.add(mMeasureSpo2Info);
            }

        }
        return health_list;

    }


    //==================修改后台状态==============

    /**
     * 根据用户ID来查询 服务状态为0的前多少条数据
     *
     * @param user_id
     * @return List<MeasureSpo2Info>
     */
    public List<MeasureSpo2Info> MyQueryToSync(String user_id, int count) {

        QueryBuilder<MeasureSpo2Info> queryBuilder = mManager.getDaoSession().queryBuilder(MeasureSpo2Info.class);


        List<MeasureSpo2Info> movement_list = queryBuilder.where(MeasureSpo2InfoDao.Properties.User_id.eq(user_id), MeasureSpo2InfoDao.Properties.Sync_state.eq("0"))
                .orderDesc(MeasureSpo2InfoDao.Properties.Measure_time)//降序
                .list();

        List<MeasureSpo2Info> result_list = new ArrayList<>();

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
     * @return List<MeasureSpo2Info>
     */
    public List<MeasureSpo2Info> MyQueryToSyncAll(String user_id) {
        QueryBuilder<MeasureSpo2Info> queryBuilder = mManager.getDaoSession().queryBuilder(MeasureSpo2Info.class);
        List<MeasureSpo2Info> info_list = queryBuilder.where(MeasureSpo2InfoDao.Properties.User_id.eq(user_id))
                .whereOr(MeasureSpo2InfoDao.Properties.Sync_state.isNull(), MeasureSpo2InfoDao.Properties.Sync_state.eq("0"))
                .limit(20)
                .list();
        return info_list;
    }

    public boolean MyupdateToSyncDataId(String user_id, String measure_time) {
        List<MeasureSpo2Info> info_list = queryToTime(user_id, measure_time);
        return MyUpdateToSync(info_list);
    }

    /**
     * 修改列表的网络状态为1
     *
     * @param infoList
     * @return
     */

    public boolean MyUpdateToSync(final List<MeasureSpo2Info> infoList) {
        boolean flag = true;
        if (infoList != null && infoList.size() > 0) {
            for (MeasureSpo2Info info : infoList) {
                info.setSync_state("1");
            }
            try {
                mManager.getDaoSession().getMeasureSpo2InfoDao().updateInTx(infoList);
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
    public boolean insertData(MeasureSpo2Info info) {
        info.setWarehousing_time(String.valueOf(System.currentTimeMillis()));
        MyLog.i(TAG, "插入数据 = info = " + info.toString());
        boolean flag = mManager.getDaoSession().getMeasureSpo2InfoDao().insert(info) == -1 ? false : true;
        return flag;
    }


    /**
     * 更新一条数据（系统ID）
     *
     * @param info
     * @return
     */
    public boolean updateData(MeasureSpo2Info info) {
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
    public List<MeasureSpo2Info> queryToTime(String user_id, String time) {

        QueryBuilder<MeasureSpo2Info> queryBuilder = mManager.getDaoSession().queryBuilder(MeasureSpo2Info.class);
        return queryBuilder.where(MeasureSpo2InfoDao.Properties.User_id.eq(user_id), MeasureSpo2InfoDao.Properties.Measure_time.eq(time)).list();

    }


    //==================暂时不用==============

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<MeasureSpo2Info> queryAllData() {
        return mManager.getDaoSession().loadAll(MeasureSpo2Info.class);
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
            mManager.getDaoSession().deleteAll(MeasureSpo2Info.class);
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
    public boolean deleteData(MeasureSpo2Info info) {
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
    public boolean MyInsertOherData(final List<MeasureSpo2Info> infoList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (MeasureSpo2Info info : infoList) {
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