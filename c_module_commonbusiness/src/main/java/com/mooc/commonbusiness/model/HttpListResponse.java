package com.mooc.commonbusiness.model;


import com.mooc.commonbusiness.net.network.DataState;

/**
 * 接口返回的
 * 列表数据类型
 * 数据模型
 */
public class HttpListResponse<T> {

    private int count;
    private String next;
    private String previous;
    private T results;    //泛型T来表示object，可能是数组，也可能是对象,有的接口是results字段
    public DataState dataState = null;
    public Throwable error = null;


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

    public DataState getDataState() {
        return dataState;
    }

    public void setDataState(DataState dataState) {
        this.dataState = dataState;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }




    @Override
    public String toString() {
        return "HttpResponse{" +
                "count='" + count + '\'' +
                ", next=" + next +
                ", previous=" + previous +
                '}';
    }
}
