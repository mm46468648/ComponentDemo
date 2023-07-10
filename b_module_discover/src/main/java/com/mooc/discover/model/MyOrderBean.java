package com.mooc.discover.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mooc.commonbusiness.constants.IntentParamsConstants;
import com.mooc.commonbusiness.constants.ResourceTypeConstans;
import com.mooc.commonbusiness.interfaces.BaseResourceInterface;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrderBean {

    private int count;
    private List<ResultsBean> results;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean implements BaseResourceInterface {

        private int student_num;
        private int list_tag;
        private int tag;
        private String link;
//        private ShareDataBean share_data;
        private String id;
        private int count;
        private String big_image;
        private String about;
        private String title;
        private boolean has_more;
        private String small_image;
        private String cover_url;//资源图片地址
        ;
        private String detail;
        private int parent_id;
        private int parent_type;
        private int position_two;
        private String recommend_reason;
        public String resource_id;
        private int type;
        private List<RecommendColumn> data;

        public void setCover_url(String cover_url) {
            this.cover_url = cover_url;
        }

        public String getCover_url() {
            return cover_url;
        }

        public int getParent_id() {
            return parent_id;
        }

        public void setParent_id(int parent_id) {
            this.parent_id = parent_id;
        }

        public int getParent_type() {
            return parent_type;
        }

        public void setParent_type(int parent_type) {
            this.parent_type = parent_type;
        }

        public int getTag() {
            return tag;
        }

        public void setTag(int tag) {
            this.tag = tag;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getBig_image() {
            return big_image;
        }

        public void setBig_image(String big_image) {
            this.big_image = big_image;
        }

        public String getAbout() {
            return about;
        }

        public void setAbout(String about) {
            this.about = about;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }


        public String getSmall_image() {
            return small_image;
        }

        public void setSmall_image(String small_image) {
            this.small_image = small_image;
        }


        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<RecommendColumn> getData() {
            return data;
        }

        public void setData(List<RecommendColumn> data) {
            this.data = data;
        }

        @NotNull
        @Override
        public String get_resourceId() {
            if(!data.isEmpty()){
                if(data.get(0) instanceof BaseResourceInterface){
                    return data.get(0).get_resourceId();
                }
            }
            return id;
        }

        @Override
        public int get_resourceType() {
            if(data!=null && !data.isEmpty()){
                if(data.get(0) instanceof BaseResourceInterface){
                    return data.get(0).get_resourceType();
                }
            }
            return type;
        }

        @Nullable
        @Override
        public Map<String, String> get_other() {
            HashMap<String, String> map = new HashMap<>();
            if(!data.isEmpty()){
                RecommendColumn recommendColumn = data.get(0);
                if(recommendColumn != null){
                    map.put(IntentParamsConstants.WEB_PARAMS_TITLE,recommendColumn.getTitle());
                    map.put(IntentParamsConstants.WEB_PARAMS_URL, recommendColumn.getLink());

                    if(type == ResourceTypeConstans.TYPE_PERIODICAL && !TextUtils.isEmpty(recommendColumn.getBasic_url())){
                        //如果是期刊资源，需要传递baseurl
                        map.put(IntentParamsConstants.PERIODICAL_PARAMS_BASICURL,recommendColumn.getBasic_url());
                    }
                }
            }
            return map;
        }

        @Override
        public int get_resourceStatus() {
            return 0;
        }
    }
}
