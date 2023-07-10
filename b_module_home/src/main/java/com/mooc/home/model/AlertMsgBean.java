package com.mooc.home.model;

import com.mooc.commonbusiness.constants.IntentParamsConstants;
import com.mooc.commonbusiness.constants.LogEventConstants2;
import com.mooc.commonbusiness.interfaces.BaseResourceInterface;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 弹窗消息
 * 和每日一读模型复用
 */
public class AlertMsgBean implements BaseResourceInterface {
    public int id;
    public String system_message_id;
    public String alert_img = "";
    public String alert_desc = "";
    public String alert_title = "";

    //跳转资源相关字段
    public String link = "";
    public String title = "";
    public int resource_id = -1;
    public int resource_type = -1;

    public void setSystem_message_id(String system_message_id) {
        this.system_message_id = system_message_id;
    }

    public String getSystem_message_id() {
        return system_message_id;
    }

    @NotNull
    @Override
    public String get_resourceId() {
        return String.valueOf(resource_id);
    }

    @Override
    public int get_resourceType() {
        return resource_type;
    }

    @Nullable
    @Override
    public Map<String, String> get_other() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put(IntentParamsConstants.WEB_PARAMS_URL, link);
        stringStringHashMap.put(IntentParamsConstants.WEB_PARAMS_TITLE, title);
        stringStringHashMap.put(IntentParamsConstants.ACT_FROM_TYPE, LogEventConstants2.F_WINDOW);
        return stringStringHashMap;
    }

    @Override
    public int get_resourceStatus() {
        return 0;
    }
}
