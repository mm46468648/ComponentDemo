package com.mooc.commonbusiness.model.studyproject;

import java.io.Serializable;

/**
 * 加入学习项目（学习计划）状态
 */
public class JoinStudyState implements Serializable {


    /**
     * resource_show_status : -1
     * about : 随着年龄的增加，了解的事情越多，保持好奇就变的越来越不容易。
     * resource_name :
     * study_plan_id : -1
     * set_resource_show_time : 0
     * study_plan_name :
     * resource_source_introduction :
     * is_join_studyplan : 0
     * detail_title : 如何保持好奇心？这里有7个建议
     * study_plan_end_time : 0
     * set_is_listen_test : -1
     */

    public int resource_show_status;   //没加入学习项目是否显示该资源 0 不显示 1显示
    public String about;
    public String resource_name;
    public String study_plan_id;
    public String set_resource_show_time;
    public String study_plan_name;
    public String resource_source_introduction;
    public int is_join_studyplan;       //是否加入学习项目 0 没有加入 1 已加入
    public String detail_title;
    public long study_plan_end_time;
    public String resource_type;
    public int set_is_listen_test;
    
}
