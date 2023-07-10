package com.mooc.battle.model;

import java.util.ArrayList;

/**
 * 游戏问题
 */
public class GameQuestion {
    public String id;      //题目id
    public String title;
    public ArrayList<GameOptions> options;
    public int question_time_limit = 30; //答题时间 默认30s
    public int type = 1; //题目类型1,单选,2判断

}
