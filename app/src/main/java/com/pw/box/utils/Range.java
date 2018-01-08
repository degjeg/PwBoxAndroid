package com.pw.box.utils;

/**
 * 范围工具类
 * Created by danger on 16/10/11.
 */

public class Range<T> {
    T start;
    T end;

    public Range(T start, T end) {
        this.start = start;
        this.end = end;
    }

    public T getStart() {
        return start;
    }

    public void setStart(T start) {
        this.start = start;
    }

    public T getEnd() {
        return end;
    }

    public void setEnd(T end) {
        this.end = end;
    }

    @Override
    public String toString() {
        // return // String.format("%d-%d", start=)
        return "[" + start + "," + end + "]";
    }
}
