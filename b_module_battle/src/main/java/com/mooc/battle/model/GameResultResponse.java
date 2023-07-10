package com.mooc.battle.model;

import java.io.Serializable;

/**
 * 对战结果统计
 */
public class GameResultResponse implements Serializable {

    public GameSummary self_summary;
    public GameSummary pk_summary;

    //为了方便传递值,在结果页中使用,自己加的参数
    public GameUserInfo pk_user_info;
    public static class GameSummary implements Serializable{

        public int total_score;
        public int result;//结果 1 赢了 -1 输了  2平局  -2 逃跑
        public int answer_streak;//答题连对记录 其中=1 不展示
        public int current_win_streak;//连胜记录 其中=1 不展示
        public int exp;//经验
    }
}
