package com.mooc.battle.model;

import java.util.ArrayList;

public class GameReviewResponse {
    public String title;
    public String right_answer;      //正确答案
    public String answer;      //比武中,我选择的答案
    public int type;      //答题类型1单选,2判断
    public ArrayList<GameOptions> options;
    public Answer self_answer;   //自己答案
    public Answer pk_answer;//对手答案
}
