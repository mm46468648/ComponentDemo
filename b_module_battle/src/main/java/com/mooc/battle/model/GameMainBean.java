package com.mooc.battle.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GameMainBean {
    @SerializedName("count")
    private Integer count;
    @SerializedName("results")
    private List<GameMain> results;


    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<GameMain> getResults() {
        return results;
    }

    public void setResults(List<GameMain> results) {
        this.results = results;
    }
}
