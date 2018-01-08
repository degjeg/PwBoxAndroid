package com.pw.box.core.db;

import android.database.sqlite.SQLiteDatabase;

import com.pw.box.App;
import com.pw.box.bean.protobuf.Data;
import com.pw.box.core.db.bean.DbBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库的工具类
 * Created by danger on 16/9/25.
 */

public class Db {

    private static DaoMaster daoMaster = new DaoMaster(new DaoMaster.OpenHelper(App.getContext(), "pd.b", null) {
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // do nothing
            DaoMaster.createAllTables(db, true);
            /*if (oldVersion == 1) {
                KvsDao.createTable(db, true);
            }*/
        }
    }.getWritableDatabase());

    private static DaoSession session = daoMaster.newSession();

    public static List<Data> loadAllData() {
        List<DbBean> list = session.getDbBeanDao().loadAll();
        List<Data> list1 = new ArrayList<>();

        for (DbBean d : list) {
            try {
                list1.add(Data.ADAPTER.decode(d.getData()));
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }

        return list1;
    }

    public static void clearAllData() {
        session.getDbBeanDao().deleteAll();
    }

    public static void clearAllTable() {
        session.getDbBeanDao().deleteAll();
        session.getRecentItemDao().deleteAll();
        session.getSearchHistoryDao().deleteAll();
    }

    public static DaoSession getSession() {
        return session;
    }

    public static void insertOrUpdate(Data data) {
        DbBean dbBean = new DbBean();
        dbBean.setId(data.id);
        dbBean.setData(data.encode());

        Db.getSession().getDbBeanDao().insertOrReplace(dbBean);
    }
}
