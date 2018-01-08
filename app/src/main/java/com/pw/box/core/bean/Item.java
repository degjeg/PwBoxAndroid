package com.pw.box.core.bean;

import android.text.TextUtils;

import com.pw.box.App;

import java.util.Arrays;
import java.util.List;

/**
 * 密码数据的类型item
 * Created by danger on 16/9/17.
 */
public class Item implements Cloneable {
    private int type;
    private String name;
    private String value;

    public Item() {
    }

    public Item(int type) {
        this.type = type;
    }

    public Item(int type, int name) {
        this(type, name > 0 ? App.getContext().getString(name) : null, null);
    }

    public Item(int type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public static String[] getNamesArray(List<Item> allTypes) {
        String[] s = new String[allTypes.size()];
        for (int i = 0; i < allTypes.size(); i++) {
            s[i] = allTypes.get(i).getName();
        }
        return s;
    }

    public static String[] getNamesArray(Item[] allTypes) {
        return getNamesArray(Arrays.asList(allTypes));
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName(int type) {
        if (!TextUtils.isEmpty(name)) return name;
        return ItemTypes.getItemNameByType(App.getContext(), type, this.type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //    public static List<Item> combine(List<Item> allTypes, Item[] items) {
    //        List<Item> types = new ArrayList<>(allTypes);
    //
    //        for (int i = 0; i < items.length; i++) {
    //            boolean find = false;
    //            for (int j = 0; j < types.size(); j++) {
    //                if (TextUtils.equals(types.get(j).getName(), items[i].getName())) {
    //                    find = true;
    //                    break;
    //                }
    //            }
    //            if (!find) {
    //                types.add(items[i]);
    //            }
    //        }
    //
    //        //        String[] ts = new String[types.size()];
    //        //        for (int i = 0; i < types.size(); i++) {
    //        //            ts[i] = types.get(i).name;
    //        //        }
    //
    //        return types;
    //    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Item clone() {
        Item stu = null;
        try {
            stu = (Item) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return stu;
    }
}
