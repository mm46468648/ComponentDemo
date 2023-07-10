package com.mooc.battle.model;

/**
 * 竞赛管理对象
 */
public class CompetitionManageData {
    private String id;//id
    private String title;//竞赛标题
    private String status;//1 显示 0 隐藏
    private String content;//竞赛规则

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
