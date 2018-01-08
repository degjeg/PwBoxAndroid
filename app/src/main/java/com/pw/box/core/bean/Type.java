//package com.pw.box.core.bean;
//
//import android.support.annotation.NonNull;
//import android.text.TextUtils;
//
//import com.pw.box.cache.Cache;
//
//import java.util.List;
//
///**
// * Created by danger on 16/9/17.
// */
//public class Type implements CharSequence {
//    int type;
//    String value;
//
//    public Type(int type, String value) {
//        this.type = type;
//        this.value = value;
//    }
//
//    @Override
//    public int length() {
//        return value.length();
//    }
//
//    @Override
//    public char charAt(int index) {
//        return value.charAt(index);
//    }
//
//    @Override
//    public CharSequence subSequence(int start, int end) {
//        return value;
//    }
//
//    @NonNull
//    @Override
//    public String toString() {
//        return value;
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
//    public String getValue() {
//        return value;
//    }
//
//    public void setValue(String value) {
//        this.value = value;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        return TextUtils.equals(value, ((Type) o).getValue());
//    }
//
//    public static Type[] combine(List<Type> types1, Type[] types2) {
//        // List<Type> types = Cache.get().getAllTypes();
//        int i = 0;
//        for (Type type1 : types2) {
//            if (!types1.contains(type1)) {
//                types1.add(type1);
//            }
//        }
//
//        Type[] ts = new Type[types1.size()];
//        for (i = 0; i < types1.size(); i++) {
//            ts[i] = types1.get(i);
//        }
//
//        return ts;
//    }
//}
