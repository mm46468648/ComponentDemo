package com.mooc.battle.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CompetitionHistoryRank {


    @SerializedName("count")
    private Integer count;
    @SerializedName("results")
    private List<Rank> results;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Rank> getResults() {
        return results;
    }

    public void setResults(List<Rank> results) {
        this.results = results;
    }
}
