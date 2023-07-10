package com.mooc.studyproject.model;

/**
 * 检测跟读是否超过限制
 */
public class OverLimitBean {

    String timestamp;

    int num;

    int wait_time;

    public void setWait_time(int wait_time) {
        this.wait_time = wait_time;
    }

    public int getWait_time() {
        return wait_time;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
