package com.mooc.discover.model;

import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.mooc.commonbusiness.constants.IntentParamsConstants;
import com.mooc.commonbusiness.constants.ResourceTypeConstans;
import com.mooc.commonbusiness.interfaces.BaseResourceInterface;
import com.mooc.commonbusiness.model.search.AlbumBean;
import com.mooc.commonbusiness.model.search.TrackBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 此模型和RecommendColumn一样
 * todo 后期合并
 */
public class RecommendContentBean implements MultiItemEntity {

    private int count;
    private String about;
    private int position_one;
    private int student_num;
    private String title;
    private boolean has_more;
    private int list_tag;
    private boolean is_subscribe;
    private String detail;
    private int parent_id;
    private int tag;        //列表的样式
    private String link;
    private int position_two;
    private String recommend_reason;
    private int type;
    private int id;
    private int filter_status;//是否显示专栏中的筛选框,1展示 2隐藏,默认显示
    private ArrayList<DataBean> data;
    private ShareDataBean share_data;
    private String big_image;

    public void setFilter_status(int filter_status) {
        this.filter_status = filter_status;
    }

    public int getFilter_status() {
        return filter_status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public int getPosition_one() {
        return position_one;
    }

    public void setPosition_one(int position_one) {
        this.position_one = position_one;
    }

    public int getStudent_num() {
        return student_num;
    }

    public void setStudent_num(int student_num) {
        this.student_num = student_num;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHas_more() {
        return has_more;
    }

    public void setHas_more(boolean has_more) {
        this.has_more = has_more;
    }

    public int getList_tag() {
        return list_tag;
    }

    public void setList_tag(int list_tag) {
        this.list_tag = list_tag;
    }

    public boolean is_subscribe() {
        return is_subscribe;
    }

    public void setIs_subscribe(boolean is_subscribe) {
        this.is_subscribe = is_subscribe;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
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

    public int getPosition_two() {
        return position_two;
    }

    public void setPosition_two(int position_two) {
        this.position_two = position_two;
    }

    public String getRecommend_reason() {
        return recommend_reason;
    }

    public void setRecommend_reason(String recommend_reason) {
        this.recommend_reason = recommend_reason;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<DataBean> getData() {
        return data;
    }

    public void setData(ArrayList<DataBean> data) {
        this.data = data;
    }

    public ShareDataBean getShare_data() {
        return share_data;
    }

    public void setShare_data(ShareDataBean share_data) {
        this.share_data = share_data;
    }

    public String getBig_image() {
        return big_image;
    }

    public void setBig_image(String big_image) {
        this.big_image = big_image;
    }


    @Override
    public int getItemType() {
        return 0;
    }

    public static class DataBean implements BaseResourceInterface {

        private int id;
        private String title;
        private int other_type;
        private String big_image;
        private String small_image;
        private String cover_url;
        private int resource_way;
        private String resource_id;
        private String link;
        private int article_type;
        private int type;
        private int item_type;
        private String about;
        private int parent_id;
        private String parent_name;
        private String update_time;
        private String desc;
        private String publish_time;
        private String nullString = "";
        private String detail = "";
        private VideoDataBean video_data;
        private PublicationDataBean periodicals_data;
        private AlbumBean album_data;
        private TrackBean track_data;
        private int student_num;
        private String source;
        private String staff;
        private String org;
        private String start_time;
        private String basic_date_time;
        private float price;
        private String end_time;
        private long word_count;

        private boolean favourite;
        private boolean enrolled;
        private String writer;
        private String press;
        public String platform;
        private String recommend_reason;
        private boolean is_subscribe; //专栏是否订阅
        private boolean is_read;
        private EventTaskBean event_task;
        private MusicBean audio_data;
        private String platform_zh;
        private String basic_title_url;
        private String basic_url;
        private String video_duration;

        private String is_have_exam = "0";
        private String verified_active = "0";
        private String is_free = "0";

        private MasterOrderBean master_info;

        private String plan_start_users;
        private String plan_starttime = "";
        private String plan_endtime = "";
        private String join_start_time = "";
        private String join_end_time = "";
        private String is_free_info;
        private String verified_active_info;
        private String is_have_exam_info;
        private String join_num;//多少人参与
        private String success_score;//奖励积分
        private String task_start_date;//任务时间  开始
        private String task_end_date;//任务时间  结束
        private int classType = -1;
        private String time_mode = "0";//// 时间是否是永久 1是永久
        private String identity_name;// 推荐模板右上角角标文案，只有合集和课程类型取这个字段

        private ResourceInfo resource_info;
        private TaskDetailsBean task_data;
        //微知识相关
        public String pic = ""; //图片
        public String click_num = "";//学习人数
        public String exam_pass_num = "";//测试通过人数
        public String like_num = "";//点赞数

        public void setResource_info(ResourceInfo resource_info) {
            this.resource_info = resource_info;
        }

        public ResourceInfo getResource_info() {
            return resource_info;
        }

        public void setTask_data(TaskDetailsBean task_data) {
            this.task_data = task_data;
        }

        public TaskDetailsBean getTask_data() {
            return task_data;
        }

        public void setCover_url(String cover_url) {
            this.cover_url = cover_url;
        }

        public String getCover_url() {
            return cover_url;
        }

        public void setClassType(int classType) {
            this.classType = classType;
        }

        public int getClassType() {
            return classType;
        }

        public long getWord_count() {
            return word_count;
        }

        public String getNullString() {
            return nullString;
        }

        public void setNullString(String nullString) {
            this.nullString = nullString;
        }

        public void setWord_count(long word_count) {
            this.word_count = word_count;
        }

        public String getPlan_endtime() {
            return plan_endtime;
        }

        public String getBasic_date_time() {
            return basic_date_time;
        }

        public void setBasic_date_time(String basic_date_time) {
            this.basic_date_time = basic_date_time;
        }


        public String getPress() {
            return press;
        }

        public void setPress(String press) {
            this.press = press;
        }

        public void setPlan_endtime(String plan_endtime) {
            this.plan_endtime = plan_endtime;
        }

        public String getJoin_start_time() {
            return join_start_time;
        }

        public void setJoin_start_time(String join_start_time) {
            this.join_start_time = join_start_time;
        }


        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public MasterOrderBean getMaster_info() {
            return master_info;
        }

        public void setMaster_info(MasterOrderBean master_info) {
            this.master_info = master_info;
        }

        public String getJoin_end_time() {
            return join_end_time;
        }

        public void setJoin_end_time(String join_end_time) {
            this.join_end_time = join_end_time;
        }

        public String getPlan_start_users() {
            return plan_start_users;
        }

        public void setPlan_start_users(String plan_start_users) {
            this.plan_start_users = plan_start_users;
        }

        public String getPlan_starttime() {
            return plan_starttime;
        }

        public String getPlanDurition() {
            return plan_starttime + "-" + plan_endtime;
        }

        public void setPlan_starttime(String plan_starttime) {
            this.plan_starttime = plan_starttime;
        }

        public String getIs_have_exam() {
            return is_have_exam;
        }

        public void setIs_have_exam(String is_have_exam) {
            this.is_have_exam = is_have_exam;
        }

        public String isVerified_active() {
            return verified_active;
        }

        public String getVerified_active() {
            return verified_active;
        }

        public void setVerified_active(String verified_active) {
            this.verified_active = verified_active;
        }


        public String getIs_free() {
            return is_free;
        }

        public void setIs_free(String is_free) {
            this.is_free = is_free;
        }

        public String getIs_free_info() {
            return is_free_info;
        }

        public void setIs_free_info(String is_free_info) {
            this.is_free_info = is_free_info;
        }

        public String getVerified_active_info() {
            return verified_active_info;
        }

        public void setVerified_active_info(String verified_active_info) {
            this.verified_active_info = verified_active_info;
        }

        public String getIs_have_exam_info() {
            return is_have_exam_info;
        }

        public void setIs_have_exam_info(String is_have_exam_info) {
            this.is_have_exam_info = is_have_exam_info;
        }

        public String getParent_name() {
            return parent_name;
        }

        public void setParent_name(String parent_name) {
            this.parent_name = parent_name;
        }

        public String getPlatform_zh() {
            return platform_zh;
        }

        public void setPlatform_zh(String platform_zh) {
            this.platform_zh = platform_zh;
        }

        public AlbumBean getAlbum_data() {
            return album_data;
        }

        public void setAlbum_data(AlbumBean album_data) {
            this.album_data = album_data;
        }

        public TrackBean getTrack_data() {
            return track_data;
        }

        public void setTrack_data(TrackBean track_data) {
            this.track_data = track_data;
        }

        public EventTaskBean getEvent_task() {
            return event_task;
        }

        public void setEvent_task(EventTaskBean event_task) {
            this.event_task = event_task;
        }

        public boolean is_read() {
            return is_read;
        }

        public void setIs_read(boolean is_read) {
            this.is_read = is_read;
        }

        public boolean is_subscribe() {
            return is_subscribe;
        }

        public void setIs_subscribe(boolean is_subscribe) {
            this.is_subscribe = is_subscribe;
        }

        public String getRecommend_reason() {
            return recommend_reason;
        }

        public void setRecommend_reason(String recommend_reason) {
            this.recommend_reason = recommend_reason;
        }

        public String getWriter() {
            return writer;
        }

        public void setWriter(String writer) {
            this.writer = writer;
        }

        public String getPlateform() {
            return platform;
        }

        public void setPlateform(String plateform) {
            this.platform = plateform;
        }

        public int getItem_type() {
            return item_type;
        }

        public void setItem_type(int item_type) {
            this.item_type = item_type;
        }

        public int getOther_type() {
            return other_type;
        }

        public void setOther_type(int other_type) {
            this.other_type = other_type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBig_image() {
            return big_image;
        }

        public void setBig_image(String big_image) {
            this.big_image = big_image;
        }

        public String getSmall_image() {
            return small_image;
        }

        public void setSmall_image(String small_image) {
            this.small_image = small_image;
        }

        public int getResource_way() {
            return resource_way;
        }

        public void setResource_way(int resource_way) {
            this.resource_way = resource_way;
        }

        public String getResource_id() {
            return resource_id;
        }

        public void setResource_id(String resource_id) {
            this.resource_id = resource_id;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getArticle_type() {
            return article_type;
        }

        public void setArticle_type(int article_type) {
            this.article_type = article_type;
        }

        public String getAbout() {
            return about;
        }

        public void setAbout(String about) {
            this.about = about;
        }

        public int getParent_id() {
            return parent_id;
        }

        public void setParent_id(int parent_id) {
            this.parent_id = parent_id;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public String getPlatform() {
            return platform;
        }

        public boolean isIs_subscribe() {
            return is_subscribe;
        }

        public boolean isIs_read() {
            return is_read;
        }

        public String getJoin_num() {
            return join_num;
        }

        public String getSuccess_score() {
            return success_score;
        }

        public void setJoin_num(String join_num) {
            this.join_num = join_num;
        }

        public void setSuccess_score(String success_score) {
            this.success_score = success_score;
        }

        public String getTask_start_date() {
            return task_start_date;
        }

        public void setTask_start_date(String task_start_date) {
            this.task_start_date = task_start_date;
        }

        public String getTask_end_date() {
            return task_end_date;
        }

        public void setTask_end_date(String task_end_date) {
            this.task_end_date = task_end_date;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getPublish_time() {
            return publish_time;
        }

        public void setPublish_time(String publish_time) {
            this.publish_time = publish_time;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public VideoDataBean getVideo_data() {
            return video_data;
        }


        public PublicationDataBean getPeriodicals_data() {
            return periodicals_data;
        }

        public void setVideo_data(VideoDataBean video_data) {
            this.video_data = video_data;
        }

        public int getStudent_num() {
            return student_num;
        }

        public void setStudent_num(int student_num) {
            this.student_num = student_num;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getStaff() {
            if (!TextUtils.isEmpty(staff)) {
                String[] strs = staff.split(",");
                if (strs.length > 1) {
                    staff = strs[0] + "等";
                }
            }
            return staff;
        }

        public void setStaff(String staff) {
            this.staff = staff;
        }

        public String getOrg() {
            return org;
        }

        public void setOrg(String org) {
            this.org = org;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public boolean isFavourite() {
            return favourite;
        }

        public void setFavourite(boolean favourite) {
            this.favourite = favourite;
        }

        public boolean isEnrolled() {
            return enrolled;
        }

        public void setEnrolled(boolean enrolled) {
            this.enrolled = enrolled;
        }

        public MusicBean getAudio_data() {
            return audio_data;
        }

        public void setAudio_data(MusicBean audio_data) {
            this.audio_data = audio_data;
        }

        public String getBasic_title_url() {
            return basic_title_url;
        }

        public void setBasic_title_url(String basic_title_url) {
            this.basic_title_url = basic_title_url;
        }

        public String getBasic_url() {
            return basic_url;
        }

        public void setBasic_url(String basic_url) {
            this.basic_url = basic_url;
        }

        public String getVideo_duration() {
            return video_duration;
        }

        public void setVideo_duration(String video_duration) {
            this.video_duration = video_duration;
        }

        public String getTime_mode() {
            return time_mode;
        }

        public void setTime_mode(String time_mode) {
            this.time_mode = time_mode;
        }

        public String getIdentity_name() {
            return identity_name;
        }

        public void setIdentity_name(String identity_name) {
            this.identity_name = identity_name;
        }

        //        @Override
//        public int getItemType() {
//            //这样写是因为，这个字段是复用的有的模型使用的type，有的使用classType，当不为-1的时候证明取classType
//            if(classType == -1) return type;
//            return classType;
//        }


        @NotNull
        @Override
        public String get_resourceId() {
            if ("0".equals(resource_id)) {
                return id + "";
            }
            return resource_id;
        }


        @Override
        public int get_resourceType() {
            return type;
        }


        @Nullable
        @Override
        public Map<String, String> get_other() {
            String loadUrl = link;

            HashMap<String, String> stringStringHashMap = new HashMap<>();
            stringStringHashMap.put(IntentParamsConstants.WEB_PARAMS_TITLE, title);
            stringStringHashMap.put(IntentParamsConstants.WEB_PARAMS_URL, loadUrl);


            if (type == ResourceTypeConstans.TYPE_PERIODICAL && !TextUtils.isEmpty(basic_url)) {
                //如果是期刊资源，需要传递baseurl
                stringStringHashMap.put(IntentParamsConstants.PERIODICAL_PARAMS_BASICURL, basic_url);
            }
            return stringStringHashMap;
        }

        @Override
        public int get_resourceStatus() {
            return 0;
        }

        public static class VideoDataBean {

            private String video_title = "";
            private String video_link = "";
            private String video_source = "";


            public String getVideo_title() {
                return video_title;
            }

            public void setVideo_title(String video_title) {
                this.video_title = video_title;
            }

            public String getVideo_link() {
                return video_link;
            }

            public void setVideo_link(String video_link) {
                this.video_link = video_link;
            }

            public String getVideo_source() {
                return video_source;
            }

            public void setVideo_source(String video_source) {
                this.video_source = video_source;
            }


        }

    }

}
