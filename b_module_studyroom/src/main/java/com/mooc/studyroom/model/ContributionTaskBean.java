package com.mooc.studyroom.model;


import android.text.TextUtils;

import com.mooc.commonbusiness.constants.IntentParamsConstants;
import com.mooc.commonbusiness.interfaces.BaseResourceInterface;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ContributionTaskBean implements BaseResourceInterface {

    //今日任务
    public String task_type;  //任务类型名称，分为今日任务1，持续任务2
    public Boolean is_checkin;
    public String button;
    public String name;
    public String short_name; //任务id标识
    public String show_word = "";    //任务进度（昨日有7条动态被置顶）
    public String show_word_short; //任务提示（置顶结果将于发表的第二天12点前通知）
    public String count; //具体数量


    //如果配置资源需要的字段
    public String resource_id;
    public int resource_type;
//    public String resource_url; //自己的资源 (新版接口不区分)
    public String link;    //外部资源的链接
    public int resource_way;     //1的时候是外部资源链接，0是自有资源


    @NotNull
    @Override
    public String get_resourceId() {
        return resource_id;
    }

    @Override
    public int get_resourceType() {
        return resource_type;
    }

    @Nullable
    @Override
    public Map<String, String> get_other() {
        HashMap<String,String> hashMap = new HashMap<>();
        if(!TextUtils.isEmpty(link)){
            hashMap.put(IntentParamsConstants.WEB_PARAMS_URL,link);
        }
        return hashMap;
    }

    @Override
    public int get_resourceStatus() {
        return 0;
    }
}


