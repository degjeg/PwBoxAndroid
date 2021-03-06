package com.pw.box.core.db.bean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "RECENT_ITEM".
 */
public class RecentItem {

    private Long id;
    private Integer from;
    private Long time;

    public RecentItem() {
    }

    public RecentItem(Long id) {
        this.id = id;
    }

    public RecentItem(Long id, Integer from, Long time) {
        this.id = id;
        this.from = from;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

}
