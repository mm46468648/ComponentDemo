package com.mooc.studyroom.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.mooc.commonbusiness.constants.ResourceTypeConstans;

import java.io.Serializable;
import java.sql.Types;

public class IntegralRecordListBean implements Serializable, MultiItemEntity {

    private int id;
    private int user;
    private ScorePrize score_prize;
    private int reduce_score;
    private int prize_status;
    private long created_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public ScorePrize getScore_prize() {
        return score_prize;
    }

    public void setScore_prize(ScorePrize score_prize) {
        this.score_prize = score_prize;
    }

    public int getReduce_score() {
        return reduce_score;
    }

    public void setReduce_score(int reduce_score) {
        this.reduce_score = reduce_score;
    }

    public int getPrize_status() {
        return prize_status;
    }

    public void setPrize_status(int prize_status) {
        this.prize_status = prize_status;
    }

    public long getCreated_time() {
        return created_time * 1000;
    }

    public void setCreated_time(long created_time) {
        this.created_time = created_time;
    }

    @Override
    public String toString() {
        return "IntegralRecordListBean{" +
                "id=" + id +
                ", user=" + user +
                ", score_prize=" + score_prize +
                ", reduce_score=" + reduce_score +
                ", prize_status=" + prize_status +
                ", created_time=" + created_time +
                '}';
    }

    @Override
    public int getItemType() {
        return ResourceTypeConstans.TYPE_INTEGRAL_RECORD;
    }

    public static class ScorePrize implements Serializable {
        private int id;
        private String prize_title;
        private String prize_content;
        private long prize_start_time;
        private long prize_end_time;
        private int prize_score;
        private int prize_nums;
        private int prize_show;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPrize_title() {
            return prize_title;
        }

        public void setPrize_title(String prize_title) {
            this.prize_title = prize_title;
        }

        public String getPrize_content() {
            return prize_content;
        }

        public void setPrize_content(String prize_content) {
            this.prize_content = prize_content;
        }

        public long getPrize_start_time() {
            return prize_start_time * 1000;
        }

        public void setPrize_start_time(long prize_start_time) {
            this.prize_start_time = prize_start_time;
        }

        public long getPrize_end_time() {
            return prize_end_time * 1000;
        }

        public void setPrize_end_time(long prize_end_time) {
            this.prize_end_time = prize_end_time;
        }

        public int getPrize_score() {
            return prize_score;
        }

        public void setPrize_score(int prize_score) {
            this.prize_score = prize_score;
        }

        public int getPrize_nums() {
            return prize_nums;
        }

        public void setPrize_nums(int prize_nums) {
            this.prize_nums = prize_nums;
        }

        public int getPrize_show() {
            return prize_show;
        }

        public void setPrize_show(int prize_show) {
            this.prize_show = prize_show;
        }

        @Override
        public String toString() {
            return "ScorePrize{" +
                    "id=" + id +
                    ", prize_title='" + prize_title + '\'' +
                    ", prize_content='" + prize_content + '\'' +
                    ", prize_start_time=" + prize_start_time +
                    ", prize_end_time=" + prize_end_time +
                    ", prize_score=" + prize_score +
                    ", prize_nums=" + prize_nums +
                    ", prize_show=" + prize_show +
                    '}';
        }
    }

}
