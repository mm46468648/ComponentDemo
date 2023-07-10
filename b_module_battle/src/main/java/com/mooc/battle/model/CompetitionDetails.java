package com.mooc.battle.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CompetitionDetails {


    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("season_info")
    private SeasonInfo seasonInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SeasonInfo getSeasonInfo() {
        return seasonInfo;
    }

    public void setSeasonInfo(SeasonInfo seasonInfo) {
        this.seasonInfo = seasonInfo;
    }

    public static class SeasonInfo {
        private String id;
        @SerializedName("title")
        private String title;
        @SerializedName("state")
        private int state;
        @SerializedName("start_time")
        private String startTime;
        @SerializedName("end_time")
        private String endTime;
        @SerializedName("start_time_stamp")
        private Long startTimeStamp;
        @SerializedName("end_time_stamp")
        private Long endTimeStamp;
        @SerializedName("nickname")
        private String nickname;
        @SerializedName("cover")
        private String cover;
        @SerializedName("conclusion")
        private String conclusion;
        @SerializedName("self_rank_num")
        private String selfRankNum;
        @SerializedName("level")
        private int level;
        @SerializedName("stars")
        private int stars;
        @SerializedName("level_title")
        private String levelTitle;
        @SerializedName("level_image_small_icon")
        private String levelImageSmallIcon;
        @SerializedName("level_image_big_icon")
        private String levelImageBigIcon;
        @SerializedName("schedule")
        private List<Schedule> schedule;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public Long getStartTimeStamp() {
            return startTimeStamp;
        }

        public void setStartTimeStamp(Long startTimeStamp) {
            this.startTimeStamp = startTimeStamp;
        }

        public Long getEndTimeStamp() {
            return endTimeStamp;
        }

        public void setEndTimeStamp(Long endTimeStamp) {
            this.endTimeStamp = endTimeStamp;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getConclusion() {
            return conclusion;
        }

        public void setConclusion(String conclusion) {
            this.conclusion = conclusion;
        }

        public String getSelfRankNum() {
            return selfRankNum;
        }

        public void setSelfRankNum(String selfRankNum) {
            this.selfRankNum = selfRankNum;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getStars() {
            return stars;
        }

        public void setStars(int stars) {
            this.stars = stars;
        }

        public String getLevelTitle() {
            return levelTitle;
        }

        public void setLevelTitle(String levelTitle) {
            this.levelTitle = levelTitle;
        }

        public String getLevelImageSmallIcon() {
            return levelImageSmallIcon;
        }

        public void setLevelImageSmallIcon(String levelImageSmallIcon) {
            this.levelImageSmallIcon = levelImageSmallIcon;
        }

        public String getLevelImageBigIcon() {
            return levelImageBigIcon;
        }

        public void setLevelImageBigIcon(String levelImageBigIcon) {
            this.levelImageBigIcon = levelImageBigIcon;
        }

        public List<Schedule> getSchedule() {
            return schedule;
        }

        public void setSchedule(List<Schedule> schedule) {
            this.schedule = schedule;
        }

        public static class Schedule {
            @SerializedName("level")
            private int level;
            @SerializedName("level_title")
            private String levelTitle;
            @SerializedName("upgrade_stars")
            private int upgradeStars;
            @SerializedName("level_image_small_icon")
            private String levelImageSmallIcon;
            @SerializedName("level_image_big_icon")
            private String levelImageBigIcon;
            @SerializedName("stars")
            private int stars;
            @SerializedName("is_enter")
            private int isEnter;
            @SerializedName("is_complete")
            private int isComplete;

            public int getLevel() {
                return level;
            }

            public void setLevel(int level) {
                this.level = level;
            }

            public String getLevelTitle() {
                return levelTitle;
            }

            public void setLevelTitle(String levelTitle) {
                this.levelTitle = levelTitle;
            }

            public int getUpgradeStars() {
                return upgradeStars;
            }

            public void setUpgradeStars(int upgradeStars) {
                this.upgradeStars = upgradeStars;
            }

            public String getLevelImageSmallIcon() {
                return levelImageSmallIcon;
            }

            public void setLevelImageSmallIcon(String levelImageSmallIcon) {
                this.levelImageSmallIcon = levelImageSmallIcon;
            }

            public String getLevelImageBigIcon() {
                return levelImageBigIcon;
            }

            public void setLevelImageBigIcon(String levelImageBigIcon) {
                this.levelImageBigIcon = levelImageBigIcon;
            }

            public int getStars() {
                return stars;
            }

            public void setStars(int stars) {
                this.stars = stars;
            }

            public int getIsEnter() {
                return isEnter;
            }

            public void setIsEnter(int isEnter) {
                this.isEnter = isEnter;
            }

            public int getIsComplete() {
                return isComplete;
            }

            public void setIsComplete(int isComplete) {
                this.isComplete = isComplete;
            }
        }
    }
}
