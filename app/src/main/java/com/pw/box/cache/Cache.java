package com.pw.box.cache;

import android.text.TextUtils;

import com.pw.box.App;
import com.pw.box.bean.protobuf.Data;
import com.pw.box.bean.protobuf.Group;
import com.pw.box.core.Cm;
import com.pw.box.core.bean.Pack;
import com.pw.box.core.cmds.DataSyncTask;
import com.pw.box.core.cmds.LoginTask;
import com.pw.box.core.db.Db;
import com.pw.box.net.ConnectionListener;
import com.pw.box.utils.L;
import com.pw.box.utils.PrefUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 密码的数据缓存
 * Created by danger on 16/9/3.
 */
public class Cache implements ConnectionListener {
    public static final int LOGIN_STATUS_INIT = 0;
    public static final int LOGIN_STATUS_LOGGING = 1;
    public static final int LOGIN_STATUS_LOGGIN = 2;
    private static final java.lang.String TAG = "DataManager";
    public static boolean checkVerSuccessed = false;
    private static Cache cache;
    final List<Data> datas = new ArrayList<>();
    private final List<UserStatusChangeListener> listeners = new ArrayList<>();
    User user = new User();
    int dataVersion = 0;
    private int loginStatus = LOGIN_STATUS_INIT;
    private PatternUtil patternUtil;
    private long stopTime = 0;

    public Cache() {
        dataVersion = PrefUtil.getInt(App.getContext(), Constants.PREF_KEY_DATA_VERSION, 0);

        datas.clear();
        datas.addAll(Db.loadAllData());
        patternUtil = new PatternUtil();
        patternUtil.init();
        // getMaxDataId()
    }

    public static Cache get() {
        if (cache == null) {
            synchronized (Cache.class) {
                cache = new Cache();
                Cm.get().addConnectionListener(cache);
            }
        }
        return cache;
    }

    public void clear() { // 只有在注销登录时使用
        cache = null;

        // context = null;
        // version = 0;
        clearAllData();
        patternUtil.clearLockPattern();
        GroupManager.get().setGps(null);
        user = null;
    }

    public User getUser() {
        // User user = new User();
        // user.setAccount("134");
        // Random random = new Random();
        // // user.setPassword("password");
        // user.setPwFilledLogin(new byte[32]);
        // user.setPwFilledRawKey(new byte[32]);
        //
        // random.nextBytes(user.getPwFilledLogin());
        // random.nextBytes(user.getPwFilledRawKey());

        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /*public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }*/

    public boolean isLogin() {
        return loginStatus == LOGIN_STATUS_LOGGIN;
    }

    public int getLoginStatus() {
        return loginStatus;
    }

    public List<Group> getAllTypes() {
        HashMap<Integer, Group> types = new HashMap<>();
        Group outGroup = null; //  = new Group(0, "", "");

        synchronized (datas) {
            for (Data data : datas) {
                boolean find = false;

                if (data.gpid == null || data.gpid == 0) {
                    if (outGroup == null) {
                        outGroup = new Group(0, "", "");
                    }
                } else if (!types.containsKey(data.gpid)) {
                    Group gp = GroupManager.get().getGroup(data.gpid);
                    if (gp != null) {
                        types.put(data.gpid, gp);
                    }
                }
            }
        }

        List<Group> gps = new ArrayList<>(types.values());
        if (outGroup != null) {
            gps.add(outGroup);
        }
        GroupManager.get().sort(gps);
        return gps;
    }

    public void addData(Data data, int newDataVer) {
        synchronized (datas) {
            // data = Data.newBuilder(data).setId(System.currentTimeMillis()).build();
            Db.insertOrUpdate(data);
            datas.add(data);

            setDataVersion(newDataVer);
        }
    }

    public void addDatas(List<Data> itemsList) {
        // datas.clear();
        synchronized (datas) {
            datas.addAll(itemsList);
        }

    }

    public void updateData(Data data, int newDataVer) {
        synchronized (datas) {
            Db.insertOrUpdate(data);
            for (int i = 0; i < datas.size(); i++) {
                if (data.id.equals(datas.get(i).id)) {
                    datas.set(i, data);
                    break;
                }
            }

            setDataVersion(newDataVer);
        }
    }

    public void clearAllTable() {
        Db.clearAllTable();
    }

    public void clearAllData() {
        synchronized (datas) {
            datas.clear();
        }

        Db.clearAllData();
        PrefUtil.setInt(App.getContext(), Constants.PREF_KEY_DATA_VERSION, 0);
        dataVersion = 0;
    }

    public List<Data> getDataList() {
        return datas;
    }

    public List<Data> getDataListByType(Integer type) {
        List<Data> dataList = new ArrayList<>();
        synchronized (datas) {

            for (Data data : datas) {
                if ((data.gpid != null && data.gpid.equals(type))
                        || ((data.gpid == null || data.gpid == 0) && (type == null || type == 0))) {
                    dataList.add(data);
                }
            }
        }
        return dataList;
    }

    public int getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(int dataVersion) {
        this.dataVersion = dataVersion;

        PrefUtil.setInt(App.getContext(), Constants.PREF_KEY_DATA_VERSION, dataVersion);
    }

    public long getMaxDataId() {
        if (datas == null || datas.isEmpty()) {
            return 0;
        }
        return datas.get(datas.size() - 1).id;
    }

    public int getDataCount() {
        return datas.size();
    }

    public Data getData(long id) {
        synchronized (datas) {
            for (Data data : datas) {
                if (data.id == id) {
                    return data;
                }
            }
        }
        return null;
    }

    public PatternUtil getPatternUtil() {
        return patternUtil;
    }

    public void deleteData(Data item, int newDataVer) {
        datas.remove(item);
        Db.getSession().getDbBeanDao().deleteByKey(item.id);

        setDataVersion(newDataVer);
    }


    @Override
    public void onIdle() {

    }

    @Override
    public void onConnected() {
        User user = getUser();

        // 不满足自动登录的条件
        if (TextUtils.isEmpty(user.getAccount()) || null == user.getPwFilledLogin()) {
            return;
        }

        LoginTask loginTask = new LoginTask(user.getAccount(),
                user.getPwFilledLogin(),
                user.getPwFilledRawKey(), null);

        loginTask.execute();
    }

    @Override
    public void onClosed(Exception e) {
        if (L.E) L.get().e(TAG, "onClosed");
        loginStatus = LOGIN_STATUS_INIT;
        synchronized (listeners) {
            for (UserStatusChangeListener listener : listeners) {
                try {
                    listener.onConnectionClosed();
                } catch (Exception e1) {
                    // e.printStackTrace();
                    if (L.E) L.get().e(TAG, "", e1);
                }
            }
        }
    }

    @Override
    public void onReceive(Pack buffer) {

    }

    public void addUserStatusChangeListener(UserStatusChangeListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
            listeners.add(listener);

            if (getLoginStatus() == LOGIN_STATUS_LOGGIN) {
                listener.onUserLoginSuccess();
            }
        }
    }

    public void removeUserStatusChangeListener(UserStatusChangeListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void syncData() {
        if (L.E) L.get().e(TAG, "syncData start");
        DataSyncTask task = new DataSyncTask();
        task.execute();
        synchronized (listeners) {
            for (UserStatusChangeListener listener : listeners) {
                try {
                    listener.onUserSyncDataStart();
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        }
    }

    public void onSyncDataSuccess() {
        if (L.E) L.get().e(TAG, "syncData Success");
        synchronized (listeners) {
            for (UserStatusChangeListener listener : listeners) {
                try {
                    listener.onUserSyncDataSuccess();
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        }
    }

    public void onSyncDataFail() {
        if (L.E) L.get().e(TAG, "syncData fai");
        synchronized (listeners) {
            for (UserStatusChangeListener listener : listeners) {
                try {
                    listener.onUserSyncDataFail();
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        }
    }

    //    public void onLoginStart() {
    //        if(L.E) L.get().e(TAG, "onLoginStart");
    //
    //        synchronized (listeners) {
    //            for (UserStatusChangeListener listener : listeners) {
    //                try {
    //                    listener.onUserLoginStart();
    //                } catch (Exception e) {
    //                    // e.printStackTrace();
    //                }
    //            }
    //        }
    //    }

    public void onLoginSuccess() {
        if (L.E) L.get().e(TAG, "onLoginSuccess");

        loginStatus = LOGIN_STATUS_LOGGIN;
        synchronized (listeners) {
            for (UserStatusChangeListener listener : listeners) {
                try {
                    listener.onUserLoginSuccess();
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        }
    }

    public void onLoginFail() {
        if (L.E) L.get().e(TAG, "onLoginFail");
        loginStatus = LOGIN_STATUS_INIT;
        user = new User();
        synchronized (listeners) {
            for (UserStatusChangeListener listener : listeners) {
                try {
                    listener.onUserLoginFail();
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        }
    }

    public int getDataCountOfGroup(Integer id) {
        int cnt = 0;
        for (int i = 0; i < datas.size(); i++) {
            Data data = datas.get(i);
            if ((data.gpid != null && data.gpid.equals(id))
                    || ((data.gpid == null || data.gpid == 0) && (id == null || id == 0))) {
                ++cnt;
            }
        }
        return cnt;
    }
}

