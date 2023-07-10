package com.mooc.studyroom.model;

import java.io.Serializable;
import java.util.ArrayList;

public class IntegralBean implements Serializable{

    private int count;
    private String next;
    private String previous;
    private ArrayList<IntegralListBean> results;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public ArrayList<IntegralListBean> getResults() {
        return results;
    }

    public void setResults(ArrayList<IntegralListBean> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "IntegralBean{" +
                "count=" + count +
                ", next='" + next + '\'' +
                ", previous='" + previous + '\'' +
                ", results=" + results +
                '}';
    }
}
