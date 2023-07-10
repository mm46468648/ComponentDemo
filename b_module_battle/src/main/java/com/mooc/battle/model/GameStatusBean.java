package com.mooc.battle.model;

/**
 * 检测对局状态
 * 该题还剩多少时间
 */
public class GameStatusBean {

    public int question_index;    //应该答第几题
    public int question_remain_time = -1; //该题还剩多少秒作答,(如果后端没有返回是-1)
}
