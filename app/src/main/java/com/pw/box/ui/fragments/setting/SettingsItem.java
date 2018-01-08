package com.pw.box.ui.fragments.setting;

/**
 * 设置界面的item
 * Created by danger on 16/9/16.
 */
public class SettingsItem {
    public int type;
    public int name;
    public String value;

    public SettingsItem(int type, int name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
