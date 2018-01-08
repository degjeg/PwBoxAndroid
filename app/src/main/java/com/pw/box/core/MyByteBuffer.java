//package com.pw.box.core;
//
///**
// * Created by danger on 16/8/19.
// */
//public class MyByteBuffer {
//
//    private byte[] data;
//    private int position = 0;
//
//    public MyByteBuffer(int size) {
//        data = new byte[size];
//    }
//
//    public int getDataCnt() {
//        return position;
//    }
//
//    public int getCapacity() {
//        return data.length;
//    }
//
//    /**
//     * 返回缓冲区的剩余空间字节数据
//     *
//     * @return
//     */
//    public int getRemaingSpace() {
//        return data.length - position;
//    }
//
//    public byte[] get() {
//        return data;
//    }
//
//    public byte[] get(int bytesCount) {
//        return get(bytesCount, 0);
//    }
//
//    public byte[] get(int bytesCount, int offset) {
//        bytesCount = Math.min(getDataCnt() - offset, bytesCount);
//        if (bytesCount <= 0) {
//            return null;
//        }
//
//        byte[] d = new byte[bytesCount];
//        System.arraycopy(data, offset, d, 0, bytesCount);
//        return d;
//    }
//
//    public void remove(int bytesCount) {
//        bytesCount = Math.min(getDataCnt(), bytesCount);
//        if (getDataCnt() - bytesCount > 0) { // 删完后还有剩余的话,就拷贝
//            System.arraycopy(data, bytesCount, data, 0, getDataCnt() - bytesCount);
//        }
//        position -= bytesCount;
//    }
//
//    public void clear() {
//        position = 0;
//    }
//
//    public int put(byte[] d) {
//        if (d == null) {
//            return -1;
//        }
//        if (d.length > getRemaingSpace()) { // 空间不足
//            return -2;
//        }
//
//        System.arraycopy(d, 0, data, position, d.length);
//        position += d.length;
//        return 0;
//    }
//}
