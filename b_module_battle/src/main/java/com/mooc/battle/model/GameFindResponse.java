package com.mooc.battle.model;

import java.util.ArrayList;

public class GameFindResponse {

    public String match_uuid; // 对局唯一unqie值

    public GameUserInfo self_user_info;
    public GameUserInfo pk_user_info;

    public int view_answer_request_intervals; //每次查询答案间隔时间
    public ArrayList<GameQuestion> questions;

}
