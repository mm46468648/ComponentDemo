package com.mooc.battle.model;

import com.google.gson.annotations.SerializedName;


public class Rank {
    @SerializedName("id")
    private Integer id;
    @SerializedName("level")
    private Integer level;
    @SerializedName("stars")
    private Integer stars;
    @SerializedName("title")
    private String title;
    @SerializedName("level_title")
    private String levelTitle;
    @SerializedName("level_image")
    private String levelImage;
    @SerializedName("start_time_stamp")
    private Long startTime;
    @SerializedName("end_time_stamp")
    private Long endTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public String getLevelTitle() {
        return levelTitle;
    }

    public void setLevelTitle(String levelTitle) {
        this.levelTitle = levelTitle;
    }

    public String getLevelImage() {
        return levelImage;
    }

    public void setLevelImage(String levelImage) {
        this.levelImage = levelImage;
    }

}
