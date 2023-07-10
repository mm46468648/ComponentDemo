package com.mooc.battle.model;


import java.util.List;

/**
 * 比武问题信息
 */
public class SkillQuestionInfo {

    public String title;
    public int total_answer_limit_time;
    public int total_question_limit_num;
    public List<GameQuestion> questions;

}
