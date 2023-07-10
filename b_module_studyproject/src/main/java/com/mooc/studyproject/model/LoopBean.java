package com.mooc.studyproject.model;

public class LoopBean {

    String wait_time;
    String status; //1 排队， 2评测

    int percent;

    float all_time;

    public float getAll_time() {
        return all_time;
    }

    public void setAll_time(float all_time) {
        this.all_time = all_time;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getPercent() {
        return percent;
    }

    public boolean success = false;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getWait_time() {
        return wait_time;
    }

    public void setWait_time(String wait_time) {
        this.wait_time = wait_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
