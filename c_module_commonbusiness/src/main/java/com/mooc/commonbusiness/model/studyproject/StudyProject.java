package com.mooc.commonbusiness.model.studyproject;

import com.mooc.commonbusiness.constants.IntentParamsConstants;
import com.mooc.commonbusiness.constants.ResourceTypeConstans;
import com.mooc.commonbusiness.interfaces.BaseResourceInterface;
import com.mooc.commonbusiness.model.UserInfo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyProject implements BaseResourceInterface {

    private int id;
    private String plan_name;
    private String plan_subtitle;
    private String plan_starttime;
    private String plan_endtime;
    private String plan_rule;
    private String plan_img;
    private int plan_order;
    private int plan_status;
    private String plan_num = "";
    private int is_continus_checkin;
    private String plan_start_users_introduction;
    private int is_bet;
    private String bet_rules;
    private int need_score;
    private int limit_num;
    private String join_start_time;
    private String join_end_time;
    private int is_review_checkin;
    private int is_medal;
    private String medal_link;
    private String medal_default_link;
    private int space_time;
    private int share_status;
    private String share_title;
    private String share_desc;
    private String share_picture;
    private int comment_like_status;
    private Object set_time;
    private int plan_mode_status;
    private int need_score_status;
    private int pop_window_status;
    private String pop_desc;
    private int activity_status;
    private Object set_activity_time;
    private int num_activity_limit;
    private String bet_introduction;
    private String set_resource_end_time;
    private String plan_master_speaker;
    private String plan_master_content;
    private String plan_master_join_method;
    private int plan_master_is_code;
    private int plan_master_code_num;
    private Object plan_master_code_starttime;
    private Object plan_master_code_endtime;
    private int plan_master_relation;
    private int set_join_send_score;
    private int checkin_activity_status;
    private String is_join;
    private String is_bind_testpaper;
    private String finished_intelligent_test;
    private IntelligentBindInfo bind_info;
    private int before_resource_check_status;
    private int time_mode;//时间是否是永久 1是永久
    private List<UserInfo> plan_start_users;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public String getPlan_subtitle() {
        return plan_subtitle;
    }

    public void setPlan_subtitle(String plan_subtitle) {
        this.plan_subtitle = plan_subtitle;
    }

    public String getPlan_starttime() {
        return plan_starttime;
    }

    public void setPlan_starttime(String plan_starttime) {
        this.plan_starttime = plan_starttime;
    }

    public String getPlan_endtime() {
        return plan_endtime;
    }

    public void setPlan_endtime(String plan_endtime) {
        this.plan_endtime = plan_endtime;
    }

    public String getPlan_rule() {
        return plan_rule;
    }

    public void setPlan_rule(String plan_rule) {
        this.plan_rule = plan_rule;
    }

    public String getPlan_img() {
        return plan_img;
    }

    public void setPlan_img(String plan_img) {
        this.plan_img = plan_img;
    }

    public int getPlan_order() {
        return plan_order;
    }

    public void setPlan_order(int plan_order) {
        this.plan_order = plan_order;
    }

    public int getPlan_status() {
        return plan_status;
    }

    public void setPlan_status(int plan_status) {
        this.plan_status = plan_status;
    }

    public String getPlan_num() {
        return plan_num;
    }

    public void setPlan_num(String plan_num) {
        this.plan_num = plan_num;
    }

    public int getIs_continus_checkin() {
        return is_continus_checkin;
    }

    public void setIs_continus_checkin(int is_continus_checkin) {
        this.is_continus_checkin = is_continus_checkin;
    }

    public String getPlan_start_users_introduction() {
        return plan_start_users_introduction;
    }

    public void setPlan_start_users_introduction(String plan_start_users_introduction) {
        this.plan_start_users_introduction = plan_start_users_introduction;
    }

    public int getIs_bet() {
        return is_bet;
    }

    public void setIs_bet(int is_bet) {
        this.is_bet = is_bet;
    }

    public String getBet_rules() {
        return bet_rules;
    }

    public void setBet_rules(String bet_rules) {
        this.bet_rules = bet_rules;
    }

    public int getNeed_score() {
        return need_score;
    }

    public void setNeed_score(int need_score) {
        this.need_score = need_score;
    }

    public int getLimit_num() {
        return limit_num;
    }

    public void setLimit_num(int limit_num) {
        this.limit_num = limit_num;
    }

    public String getJoin_start_time() {
        return join_start_time;
    }

    public void setJoin_start_time(String join_start_time) {
        this.join_start_time = join_start_time;
    }

    public String getJoin_end_time() {
        return join_end_time;
    }

    public void setJoin_end_time(String join_end_time) {
        this.join_end_time = join_end_time;
    }

    public int getIs_review_checkin() {
        return is_review_checkin;
    }

    public void setIs_review_checkin(int is_review_checkin) {
        this.is_review_checkin = is_review_checkin;
    }

    public int getIs_medal() {
        return is_medal;
    }

    public void setIs_medal(int is_medal) {
        this.is_medal = is_medal;
    }

    public String getMedal_link() {
        return medal_link;
    }

    public void setMedal_link(String medal_link) {
        this.medal_link = medal_link;
    }

    public String getMedal_default_link() {
        return medal_default_link;
    }

    public void setMedal_default_link(String medal_default_link) {
        this.medal_default_link = medal_default_link;
    }

    public int getSpace_time() {
        return space_time;
    }

    public void setSpace_time(int space_time) {
        this.space_time = space_time;
    }

    public int getShare_status() {
        return share_status;
    }

    public void setShare_status(int share_status) {
        this.share_status = share_status;
    }

    public String getShare_title() {
        return share_title;
    }

    public void setShare_title(String share_title) {
        this.share_title = share_title;
    }

    public String getShare_desc() {
        return share_desc;
    }

    public void setShare_desc(String share_desc) {
        this.share_desc = share_desc;
    }

    public String getShare_picture() {
        return share_picture;
    }

    public void setShare_picture(String share_picture) {
        this.share_picture = share_picture;
    }

    public int getComment_like_status() {
        return comment_like_status;
    }

    public void setComment_like_status(int comment_like_status) {
        this.comment_like_status = comment_like_status;
    }

    public Object getSet_time() {
        return set_time;
    }

    public void setSet_time(Object set_time) {
        this.set_time = set_time;
    }

    public int getPlan_mode_status() {
        return plan_mode_status;
    }

    public void setPlan_mode_status(int plan_mode_status) {
        this.plan_mode_status = plan_mode_status;
    }

    public int getNeed_score_status() {
        return need_score_status;
    }

    public void setNeed_score_status(int need_score_status) {
        this.need_score_status = need_score_status;
    }

    public int getPop_window_status() {
        return pop_window_status;
    }

    public void setPop_window_status(int pop_window_status) {
        this.pop_window_status = pop_window_status;
    }

    public String getPop_desc() {
        return pop_desc;
    }

    public void setPop_desc(String pop_desc) {
        this.pop_desc = pop_desc;
    }

    public int getActivity_status() {
        return activity_status;
    }

    public void setActivity_status(int activity_status) {
        this.activity_status = activity_status;
    }

    public Object getSet_activity_time() {
        return set_activity_time;
    }

    public void setSet_activity_time(Object set_activity_time) {
        this.set_activity_time = set_activity_time;
    }

    public int getNum_activity_limit() {
        return num_activity_limit;
    }

    public void setNum_activity_limit(int num_activity_limit) {
        this.num_activity_limit = num_activity_limit;
    }

    public String getBet_introduction() {
        return bet_introduction;
    }

    public void setBet_introduction(String bet_introduction) {
        this.bet_introduction = bet_introduction;
    }

    public String getSet_resource_end_time() {
        return set_resource_end_time;
    }

    public void setSet_resource_end_time(String set_resource_end_time) {
        this.set_resource_end_time = set_resource_end_time;
    }

    public String getPlan_master_speaker() {
        return plan_master_speaker;
    }

    public void setPlan_master_speaker(String plan_master_speaker) {
        this.plan_master_speaker = plan_master_speaker;
    }

    public String getPlan_master_content() {
        return plan_master_content;
    }

    public void setPlan_master_content(String plan_master_content) {
        this.plan_master_content = plan_master_content;
    }

    public String getPlan_master_join_method() {
        return plan_master_join_method;
    }

    public void setPlan_master_join_method(String plan_master_join_method) {
        this.plan_master_join_method = plan_master_join_method;
    }

    public int getPlan_master_is_code() {
        return plan_master_is_code;
    }

    public void setPlan_master_is_code(int plan_master_is_code) {
        this.plan_master_is_code = plan_master_is_code;
    }

    public int getPlan_master_code_num() {
        return plan_master_code_num;
    }

    public void setPlan_master_code_num(int plan_master_code_num) {
        this.plan_master_code_num = plan_master_code_num;
    }

    public Object getPlan_master_code_starttime() {
        return plan_master_code_starttime;
    }

    public void setPlan_master_code_starttime(Object plan_master_code_starttime) {
        this.plan_master_code_starttime = plan_master_code_starttime;
    }

    public Object getPlan_master_code_endtime() {
        return plan_master_code_endtime;
    }

    public void setPlan_master_code_endtime(Object plan_master_code_endtime) {
        this.plan_master_code_endtime = plan_master_code_endtime;
    }

    public int getPlan_master_relation() {
        return plan_master_relation;
    }

    public void setPlan_master_relation(int plan_master_relation) {
        this.plan_master_relation = plan_master_relation;
    }

    public int getSet_join_send_score() {
        return set_join_send_score;
    }

    public void setSet_join_send_score(int set_join_send_score) {
        this.set_join_send_score = set_join_send_score;
    }

    public int getCheckin_activity_status() {
        return checkin_activity_status;
    }

    public void setCheckin_activity_status(int checkin_activity_status) {
        this.checkin_activity_status = checkin_activity_status;
    }

    public int getBefore_resource_check_status() {
        return before_resource_check_status;
    }

    public void setBefore_resource_check_status(int before_resource_check_status) {
        this.before_resource_check_status = before_resource_check_status;
    }

    public int getTime_mode() {
        return time_mode;
    }

    public void setTime_mode(int time_mode) {
        this.time_mode = time_mode;
    }

    public List<UserInfo> getPlan_start_users() {
        return plan_start_users;
    }

    public void setPlan_start_users(List<UserInfo> plan_start_users) {
        this.plan_start_users = plan_start_users;
    }

    @NotNull
    @Override
    public String get_resourceId() {
        return String.valueOf(id);
    }

    @Override
    public int get_resourceType() {
        return ResourceTypeConstans.TYPE_STUDY_PLAN;
    }

    @Nullable
    @Override
    public Map<String, String> get_other() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put(IntentParamsConstants.INTENT_IS_JOIN,is_join);
        stringStringHashMap.put(IntentParamsConstants.INTENT_IS_BIND_TESTPAPER,is_bind_testpaper);
        stringStringHashMap.put(IntentParamsConstants.INTENT_IS_FINISHED_INTELLIGENT_TEST,finished_intelligent_test);
        if (bind_info != null) {
            stringStringHashMap.put(IntentParamsConstants.INTENT_BIND_TEST_ID,bind_info.getBind_test_paper_id());
            stringStringHashMap.put(IntentParamsConstants.INTENT_BIND_TEST_LINK,bind_info.getTest_paper_link());
            stringStringHashMap.put(IntentParamsConstants.INTENT_BIND_TEST_TITLE,bind_info.getTitle());
        }

        return stringStringHashMap;
    }

    @Override
    public int get_resourceStatus() {
        return 0;
    }
}
