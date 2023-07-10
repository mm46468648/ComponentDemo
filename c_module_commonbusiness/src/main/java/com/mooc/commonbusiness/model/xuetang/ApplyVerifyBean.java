package com.mooc.commonbusiness.model.xuetang;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ApplyVerifyBean {


    /**
     * display_name : JAVA程序设计进阶（自主模式）
     * honor_type : 普通证书
     * credential_flow_status : none
     * user_info : {"name_zh_cn":"test","verified":false,"name_en_us":"test"}
     * study_end :
     * honor_org : ["学堂在线"]
     * credential_status : paper
     * download_url : http://xuetangx.tunnel.xuetangx.com:8080/download_credential/pKCxqoXtT1E.pdf
     * credential_id : pKCxqoXtT1E
     * course_type : 1
     * src_url : http://storage.xuetangx.com/public_assets/xuetangx/credential/thumbnail/pKCxqoXtT1E_1080.jpg
     * honor_date : 2019-10-25
     * course_id : course-v1:TsinghuaX+30240332+sp
     * study_start : 2017-12-18T00:00:00Z
     * alt :
     * thumbnail : http://storage.xuetangx.com/public/verify_certificate/mobile_credential_template_thumbnail_vertical.jpg
     * electric_applied : true
     */

    private String display_name;
    private String honor_type;
    private String credential_flow_status;
    private UserInfoBean user_info;
    private String study_end;
    private String credential_status;
    private String download_url;
    private String credential_id;
    private int course_type;
    private String src_url;
    private String honor_date;
    private String course_id;
    private String study_start;
    private String alt;
    private String thumbnail;
    private boolean electric_applied;
    private List<String> honor_org;

    public static ApplyVerifyBean objectFromData(String str) {

        return new Gson().fromJson(str, ApplyVerifyBean.class);
    }

    public static List<ApplyVerifyBean> arrayApplyVerifyBeanFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<ApplyVerifyBean>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getHonor_type() {
        return honor_type;
    }

    public void setHonor_type(String honor_type) {
        this.honor_type = honor_type;
    }

    public String getCredential_flow_status() {
        return credential_flow_status;
    }

    public void setCredential_flow_status(String credential_flow_status) {
        this.credential_flow_status = credential_flow_status;
    }

    public UserInfoBean getUser_info() {
        return user_info;
    }

    public void setUser_info(UserInfoBean user_info) {
        this.user_info = user_info;
    }

    public String getStudy_end() {
        return study_end;
    }

    public void setStudy_end(String study_end) {
        this.study_end = study_end;
    }

    public String getCredential_status() {
        return credential_status;
    }

    public void setCredential_status(String credential_status) {
        this.credential_status = credential_status;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getCredential_id() {
        return credential_id;
    }

    public void setCredential_id(String credential_id) {
        this.credential_id = credential_id;
    }

    public int getCourse_type() {
        return course_type;
    }

    public void setCourse_type(int course_type) {
        this.course_type = course_type;
    }

    public String getSrc_url() {
        return src_url;
    }

    public void setSrc_url(String src_url) {
        this.src_url = src_url;
    }

    public String getHonor_date() {
        return honor_date;
    }

    public void setHonor_date(String honor_date) {
        this.honor_date = honor_date;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getStudy_start() {
        return study_start;
    }

    public void setStudy_start(String study_start) {
        this.study_start = study_start;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isElectric_applied() {
        return electric_applied;
    }

    public void setElectric_applied(boolean electric_applied) {
        this.electric_applied = electric_applied;
    }

    public List<String> getHonor_org() {
        return honor_org;
    }

    public void setHonor_org(List<String> honor_org) {
        this.honor_org = honor_org;
    }

    public static class UserInfoBean {
        /**
         * name_zh_cn : test
         * verified : false
         * name_en_us : test
         */

        private String name_zh_cn;
        private boolean verified;
        private String name_en_us;

        public static UserInfoBean objectFromData(String str) {

            return new Gson().fromJson(str, UserInfoBean.class);
        }

        public static List<UserInfoBean> arrayUserInfoBeanFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType = new TypeToken<ArrayList<UserInfoBean>>() {
                }.getType();

                return new Gson().fromJson(jsonObject.getString(str), listType);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }

        public String getName_zh_cn() {
            return name_zh_cn;
        }

        public void setName_zh_cn(String name_zh_cn) {
            this.name_zh_cn = name_zh_cn;
        }

        public boolean isVerified() {
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }

        public String getName_en_us() {
            return name_en_us;
        }

        public void setName_en_us(String name_en_us) {
            this.name_en_us = name_en_us;
        }
    }
}
