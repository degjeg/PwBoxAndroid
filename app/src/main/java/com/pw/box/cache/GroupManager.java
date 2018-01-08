package com.pw.box.cache;


import com.pw.box.App;
import com.pw.box.bean.protobuf.GetGroupListResponse;
import com.pw.box.bean.protobuf.Group;
import com.pw.box.utils.FileUtils;
import com.pw.box.utils.L;

import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 分组管理
 * Created by Administrator on 2017/3/9.
 */

public class GroupManager {
    public static final String ACTION_GROUP_CHANGE = "com.pwbx.gp.ch";

    public static final String TAG = "GroupManager-";

    private static final GroupManager instance = new GroupManager();

    private final List<Group> gps = new ArrayList<>();

    final private List<Reference<GroupChangeListener>> listeners = new ArrayList<>();

    private GroupManager() {
        load();
    }

    public static GroupManager get() {
        return instance;
    }

    public boolean canDelete(Group group) {
        // Cache.get().getDataList()
        return true;
    }

    public void deleteGroup(Group group) {
        gps.remove(group);
        save();
    }

    public List<Group> getPgs() {
        if (L.E) L.get().e(TAG, "getPgs:" + Arrays.toString(getOrders().toArray()));
        return gps;
    }

    public void setGps(List<Group> gps) {
        this.gps.clear();
        if (gps != null) {
            this.gps.addAll(gps);
        }
        if (L.E)
            L.get().e(TAG, "setGps:" + Arrays.toString(getOrders().toArray()), new Exception());
        save();
    }

    public List<Integer> getOrders() {
        return getOrders(this.gps);
    }

    public void setOrders(final List<Integer> orders) {
        // this.orders.clear();
        // this.orders.addAll(orders);

        sort(gps, orders);
        save();
    }

    public List<Integer> getOrders(List<Group> gps) {
        List<Integer> orders = new ArrayList<>();
        for (int i = 0; i < gps.size(); i++) {
            orders.add(gps.get(i).id);
        }
        return orders;
    }

    public List<Integer> getOrdersIfChanged(List<Group> gps) {
        List<Integer> ordersOld = getOrders();
        List<Integer> ordersNew = getOrders(gps);
        if (!ordersNew.equals(ordersOld)) {
            return ordersNew;
        }
        return null;
    }

    public void sort(List<Group> gps) {
        sort(gps, getOrders());
    }

    public void sort(List<Group> gps, final List<Integer> orders) {

        Collections.sort(gps, new Comparator<Group>() {
            @Override
            public int compare(Group o1, Group o2) {
                int index1 = orders.indexOf(o1.id);
                int index2 = orders.indexOf(o2.id);
                return index1 - index2;
            }
        });
    }

    private void load() {
        File f = new File(App.getContext().getFilesDir(), "gps.dat");
        byte[] rawData = FileUtils.readFile(f);
        if (rawData != null && rawData.length > 0) {
            try {
                GetGroupListResponse response = GetGroupListResponse.ADAPTER.decode(rawData);
                this.gps.clear();
                this.gps.addAll(response.gps);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void save() {
        GetGroupListResponse response = new GetGroupListResponse.Builder().gps(gps).build();
        File f = new File(App.getContext().getFilesDir(), "gps.dat");
        byte[] rawData = response.encode();
        FileUtils.writeFile(f, rawData);
    }

    public void updateGroup(Group group1) {
        for (int i = 0; i < gps.size(); i++) {
            if (gps.get(i).id.equals(group1.id)) {
                gps.set(i, group1);
                save();
                return;
            }
        }
        gps.add(group1);
        save();
    }

    public int getDataCount(Group item) {

        return Cache.get().getDataCountOfGroup(item.id);
    }

    public Group getGroup(Integer gpid) {
        for (int i = 0; i < gps.size(); i++) {
            if (gps.get(i).id.equals(gpid)) {
                return gps.get(i);
            }
        }
        return null;
    }

    public String getGroupName(Integer gpid, String def) {
        Group group = getGroup(gpid);
        if (group != null) {
            return group.name;
        }

        if (def != null) {
            return def;
        }
        return "";
    }

    public void notifyGpsChange() {
        for (int i = 0; i < listeners.size(); i++) {
            Reference<GroupChangeListener> reference = listeners.get(i);
            if (reference.get() != null) {
                reference.get().onGroupChanged(gps);
            } else {
                listeners.remove(i--);
            }
        }
    }

    public void addListener(GroupChangeListener listener) {
        Reference reference = new MyReference<>(listener);
        listeners.remove(reference);
        listeners.add(reference);
    }

    public void removeListener(GroupChangeListener listener) {
        Reference reference = new MyReference<>(listener);
        listeners.remove(reference);
    }

    public interface GroupChangeListener {
        void onGroupChanged(List<Group> gps);
    }

    class MyReference<T> extends WeakReference<T> {

        public MyReference(T referent) {
            super(referent);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Reference)) return false;
            MyReference other = (MyReference) obj;

            if ((get() == null) != (other.get() == null)) {
                return false;
            }
            return ((get() == null) && (other.get() == null))
                    || (get().equals(other.get()));
        }
    }
}
