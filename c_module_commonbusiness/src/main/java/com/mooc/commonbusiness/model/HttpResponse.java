package com.mooc.commonbusiness.model;

import android.text.TextUtils;

import com.mooc.commonbusiness.net.network.DataState;

/**
 * 接口返回的基本数据类型
 */
public class HttpResponse<T> {
    private String msg;
    private String message;
    private boolean success;
    private T data;    //泛型T来表示object，可能是数组，也可能是对象
    private T results;    //泛型T来表示object，可能是数组，也可能是对象,有的接口是results字段
    private T result;    //泛型T来表示object，可能是数组，也可能是对象,有的接口是results字段
    private int code;
    private int status;
    public DataState dataState = null;
    public Throwable error = null;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        if (TextUtils.isEmpty(msg)) {
            return message;
        }
        return msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        if (TextUtils.isEmpty(message)) {
            return msg;
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "msg='" + msg + '\'' +
                ", success=" + success +
                ", data=" + data +
                ", code=" + code +
                '}';
    }
}
