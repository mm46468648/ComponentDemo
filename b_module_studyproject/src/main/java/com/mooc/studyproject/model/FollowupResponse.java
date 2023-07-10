package com.mooc.studyproject.model;

/**
 * 跟读资源，详情
 * 查询是否绑定电子书，
 * ，绑定电子书进度
 */

public class FollowupResponse {

    public FollowupDetail results;

    public static class FollowupDetail {
        public String is_bind_source; //是否绑定电子书    1绑定，0未绑定
        public String bind_source_id; //绑定资源的id
        public int bind_source_type; //绑定资源的类型
        public float read_process = 0f;   //电子书需达标进度浮点型 1为100%
        public float ebook_process = 0f;   //电子书需达标进度
        public int repeat_context_count = 0;   //共几篇文稿
        public String name;     //标题
        public String introduction;  //简介

    }
}

