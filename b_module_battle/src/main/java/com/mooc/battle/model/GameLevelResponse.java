package com.mooc.battle.model;

import java.util.ArrayList;

public class GameLevelResponse {
    public GameSeason season_info;
    public ArrayList<LevelInfo> my_rank_info;

    public static class LevelInfo{
        public int level;
        public int upgrade_stars; //当前等级升级需要几颗星星
        public int stars; // 用户获得星星
        public int is_enter; // 是否可以进入 1 可以 0 不行     (0的时候,未已解锁和已完成)
        public int is_complete;  // 是否完成 1 完成 0 未完成     (0的时候,未已解锁和已完成)
        public String level_title;
        public String level_image;  //军衔图片
    }
}
