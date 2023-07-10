package com.mooc.battle.model;

import java.util.List;

public class GameNoticeBean {
    int count;

    List<GameNotice> results;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<GameNotice> getResults() {
        return results;
    }

    public void setResults(List<GameNotice> results) {
        this.results = results;
    }
}
