package com.mooc.studyroom.model;

import java.io.Serializable;

/**
 * Created by huangzuoliang on 2018/1/22.
 */

public class ExchangePointBean implements Serializable {

    /**
     * message : 没有这个兑换的礼物
     * code : 4
     */

    private String message;
    private int code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
