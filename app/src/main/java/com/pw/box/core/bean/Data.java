//package com.pw.box.core.bean;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by danger on 16/9/16.
// */
//public class Data {
//    private int type; // 银行卡,im,身份证。。。
//    private String subType; // 银行卡又分为招行,建行....
//    private String tag; // 当type为自定义时启用tag
//    private List<Item> itemList = new ArrayList<>();
//
//
//    public String getSubType() {
//        return subType;
//    }
//
//    public void setSubType(String subType) {
//        this.subType = subType;
//    }
//
//    public int getType() {
//        return type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//
//    public String getTag() {
//        return tag;
//    }
//
//    public void setTag(String tag) {
//        this.tag = tag;
//    }
//
//    public List<Item> getItemList() {
//        return itemList;
//    }
//
//    public void setItemList(List<Item> itemList) {
//        this.itemList = itemList;
//    }
//
//    public void add(Item item) {
//        itemList.add(item);
//    }
//}
