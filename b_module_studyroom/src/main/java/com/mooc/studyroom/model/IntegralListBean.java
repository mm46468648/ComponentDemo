package com.mooc.studyroom.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.mooc.commonbusiness.constants.ResourceTypeConstans;

import java.io.Serializable;

public class IntegralListBean implements Serializable, MultiItemEntity {

    private int id;
    private String prize_title;
    private String prize_content;
    private long prize_start_time;
    private long prize_end_time;
    private int prize_score;
    private int prize_nums;
    private int is_expire;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrize_title() {
        return prize_title;
    }

    public void setPrize_title(String prize_title) {
        this.prize_title = prize_title;
    }

    public String getPrize_content() {
        return prize_content;
    }

    public void setPrize_content(String prize_content) {
        this.prize_content = prize_content;
    }

    public long getPrize_start_time() {
        return prize_start_time*1000;
    }

    public void setPrize_start_time(long prize_start_time) {
        this.prize_start_time = prize_start_time;
    }

    public long getPrize_end_time() {
        return prize_end_time*1000;
    }

    public void setPrize_end_time(long prize_end_time) {
        this.prize_end_time = prize_end_time;
    }

    public int getPrize_score() {
        return prize_score;
    }

    public void setPrize_score(int prize_score) {
        this.prize_score = prize_score;
    }

    public int getPrize_nums() {
        return prize_nums;
    }

    public void setPrize_nums(int prize_nums) {
        this.prize_nums = prize_nums;
    }

    public int getIs_expire() {
        return is_expire;
    }

    public void setIs_expire(int is_expire) {
        this.is_expire = is_expire;
    }

    @Override
    public int getItemType() {
        return ResourceTypeConstans.TYPE_INTEGRAL;
    }
}
