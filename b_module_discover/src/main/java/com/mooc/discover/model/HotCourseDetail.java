package com.mooc.discover.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.mooc.commonbusiness.constants.IntentParamsConstants;
import com.mooc.commonbusiness.constants.ResourceTypeConstans;
import com.mooc.commonbusiness.interfaces.BaseResourceInterface;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 最热课程使用的数据模型
 */
public class HotCourseDetail implements BaseResourceInterface {

    private int id;
    private String org;
    private String title;
    private String platform_zh;
    private int is_have_exam;
    private String is_have_exam_info;
    private int is_free;
    private int verified_active;
    private String picture;
    private String verified_active_info;
    private String is_free_info;

    public String getVerified_active_info() {
        return verified_active_info;
    }

    public void setVerified_active_info(String verified_active_info) {
        this.verified_active_info = verified_active_info;
    }

    public String getIs_free_info() {
        return is_free_info;
    }

    public void setIs_free_info(String is_free_info) {
        this.is_free_info = is_free_info;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlatform_zh() {
        return platform_zh;
    }

    public void setPlatform_zh(String platform_zh) {
        this.platform_zh = platform_zh;
    }

    public int getIs_have_exam() {
        return is_have_exam;
    }

    public void setIs_have_exam(int is_have_exam) {
        this.is_have_exam = is_have_exam;
    }

    public String getIs_have_exam_info() {
        return is_have_exam_info;
    }

    public void setIs_have_exam_info(String is_have_exam_info) {
        this.is_have_exam_info = is_have_exam_info;
    }

    public int getIs_free() {
        return is_free;
    }

    public void setIs_free(int is_free) {
        this.is_free = is_free;
    }

    public int getVerified_active() {
        return verified_active;
    }

    public void setVerified_active(int verified_active) {
        this.verified_active = verified_active;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @NotNull
    @Override
    public String get_resourceId() {
        return String.valueOf(id);
    }

    @Override
    public int get_resourceType() {
        return ResourceTypeConstans.TYPE_COURSE;
    }

    @Nullable
    @Override
    public Map<String, String> get_other() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put(IntentParamsConstants.WEB_PARAMS_TITLE,title);
        return stringStringHashMap;
    }

    @Override
    public int get_resourceStatus() {
        return 0;
    }

}
