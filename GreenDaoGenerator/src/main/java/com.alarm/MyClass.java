package com.alarm;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyClass {


    public MyClass() throws IOException {
    }

    public static void main(String args[]) throws Exception {

        DaoGenerator generator = new DaoGenerator();
        Schema schema = new Schema(3, "com.pw.box.core.db");

        Entity dataBean = schema.addEntity("DbBean");
        dataBean.setJavaPackage("com.pw.box.core.db.bean");

        dataBean.addLongProperty("id").primaryKey();
        dataBean.addByteArrayProperty("data");


        Entity searchHistoryBean = schema.addEntity("SearchHistory");
        searchHistoryBean.setJavaPackage("com.pw.box.core.db.bean");

        searchHistoryBean.addIdProperty();
        searchHistoryBean.addStringProperty("content");
        searchHistoryBean.addIntProperty("type");
        searchHistoryBean.addLongProperty("time");

        // recent item
        Entity recentItem = schema.addEntity("RecentItem");
        recentItem.setJavaPackage("com.pw.box.core.db.bean");

        recentItem.addIdProperty();
        recentItem.addIntProperty("from");
        recentItem.addLongProperty("time");

        // kvs item
        Entity Kvs = schema.addEntity("Kvs");
        Kvs.setJavaPackage("com.pw.box.core.db.bean");

        Kvs.addStringProperty("k").primaryKey();
        Kvs.addByteArrayProperty("v");


        // offlineItem
        Entity offlineItem = schema.addEntity("OfflineItem");
        offlineItem.setJavaPackage("com.pw.box.core.db.bean");

        offlineItem.addIdProperty();
        offlineItem.addStringProperty("account");
        offlineItem.addStringProperty("desc");
        offlineItem.addByteArrayProperty("content");
        offlineItem.addByteArrayProperty("salt");
        offlineItem.addBooleanProperty("havePassword").columnName("have_password");


        generator.generateAll(schema, "../app/src/main/java");
    }
}
