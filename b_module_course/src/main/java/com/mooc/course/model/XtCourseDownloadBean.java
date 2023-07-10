package com.mooc.course.model;

import java.util.List;

/**
 * 学堂课程下载数据模型
 */
public class XtCourseDownloadBean {

    public String name;
    public String id;
    public List<XtCourseDownloadBean> section_list;    //每一章的 章节列表
    public List<XtCourseDownloadBean> leaf_list;    //单元列表

    //为了列表展示添加的字段，不是接口反的
    public boolean isChapter;    //是否是章，false代表讲
    public boolean spaceShow;    //是否显示分割条

    private long progress = 0;     //下载进度
    private int downloadState = 0; //下载状态


    public long getProgress() {
        return progress;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }


}
