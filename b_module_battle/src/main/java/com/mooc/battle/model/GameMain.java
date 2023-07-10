package com.mooc.battle.model;

import com.google.gson.annotations.SerializedName;

public class GameMain {
    @SerializedName("id")
    private Integer id;
    @SerializedName("title")
    private String title;
    @SerializedName("season_info")
    private SeasonInfo seasonInfo;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SeasonInfo getSeasonInfo() {
        return seasonInfo;
    }

    public void setSeasonInfo(SeasonInfo seasonInfo) {
        this.seasonInfo = seasonInfo;
    }
}
