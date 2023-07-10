package com.mooc.battle.model;

/**
 * 游戏赛季
 */
public class GameSeason {

    public String id;         //赛季id
    public String title;       //赛季名称
    public int is_enroll;       //是否报名 0 未报名
    public long start_time_stamp;       //开始时间 秒值
    public long end_time_stamp;       //结束时间
}
