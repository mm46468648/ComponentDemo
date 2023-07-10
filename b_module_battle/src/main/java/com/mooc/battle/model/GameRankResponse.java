package com.mooc.battle.model;

import java.util.ArrayList;

public class GameRankResponse {
    public String unique_rank_key; //唯一key,保证请求的分页不会乱
    public GameSeason season_info;
    public String self_rank_num; //本赛季排名 超过10000 后 变成--
    public ArrayList<RankInfo> season_rank_list;

    public static class RankInfo{
        public int rank_num;
        public String level_title;
        public String level_image;
        public String stars;
        public String cover;
        public String nickname;
    }
}
