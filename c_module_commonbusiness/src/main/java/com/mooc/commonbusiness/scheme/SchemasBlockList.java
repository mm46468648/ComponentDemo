package com.mooc.commonbusiness.scheme;

import java.util.HashMap;

public class SchemasBlockList {
    public final static String HREF_DEFAULT = "default"; //首页
    public final static String HREF_RECOMMEND = "recommend"; //资源
    public final static String HREF_STUDY_ROOM = "studyspace"; //学习室
    public final static String HREF_MY = "my"; //我的
    public final static String HREF_RESOURCE = "resource"; //资源
    public final static String HREF_COURSE_ALL = "allcourse";//全部课程
    public final static String HREF_COURSE = "courseintroduce";//课程及外部课程
    public final static String HREF_DOWNLOAD = "download";//我的--下载
    public final static String HREF_MY_MESSAGE = "mymessage";//我的--消息
    public final static String HREF_FEEDBACK = "feedback";//我的--意见反馈
    public final static String HREF_USER_SIGN = "usersign";//我的--打卡
    public final static String HREF_COLUMN_ARTICLE = "columnarticle";//专栏文章详情
    public final static String HREF_HTML = "html";//知识点、期刊、文章、活动等


    public final static String TAG_HOME_SEARCH = "tag_home_search";//home搜索fragment tag
    public final static String TAG_HOME_FIND = "tag_home_find";//home发现fragment tag
    public final static String TAG_HOME_STUDYROOM = "tag_home_studyroom";//home学习室fragment tag
    public final static String TAG_HOME_HORNER = "tag_home_horner";//home荣誉墙fragment tag
    public final static String TAG_HOME_MY = "tag_home_my";//home我的fragment tag


    public final static int TYPE_DEFAULT = HREF_DEFAULT.hashCode(); //首页
    public final static int TYPE_RECOMMEND = HREF_RECOMMEND.hashCode(); //首页-资源
    public final static int TYPE_STUDY_ROOM = HREF_STUDY_ROOM.hashCode(); //首页-学习室
    public final static int TYPE_MY = HREF_MY.hashCode(); //首页-我的
    public final static int TYPE_RESOURCE = HREF_RESOURCE.hashCode(); //首页-分类
    public final static int TYPE_COURSE_ALL = HREF_COURSE_ALL.hashCode();//全部课程
    public final static int TYPE_COURSE = HREF_COURSE.hashCode();//课程
    public final static int TYPE_DOWNLOAD = HREF_DOWNLOAD.hashCode();//我的--下载
    public final static int TYPE_MY_MESSAGE = HREF_MY_MESSAGE.hashCode();//我的--消息
    public final static int TYPE_FEEDBACK = HREF_FEEDBACK.hashCode();//我的--意见反馈
    public final static int TYPE_USER_SIGN = HREF_USER_SIGN.hashCode();//我的--打卡
    public final static int TYPE_COLUMN_ARTICLE = HREF_COLUMN_ARTICLE.hashCode();//专栏文章详情
    public final static int TYPE_HTML = HREF_HTML.hashCode();//知识点、期刊、文章、活动等

    private final static HashMap<String, Boolean> blockListMap;

    static {
        blockListMap = new HashMap<>();
        blockListMap.put(HREF_RESOURCE, true);
        blockListMap.put(HREF_DEFAULT, true);
        blockListMap.put(HREF_STUDY_ROOM, true);
        blockListMap.put(HREF_MY, true);
        blockListMap.put(HREF_RECOMMEND, true);
        blockListMap.put(HREF_COURSE_ALL, true);
        blockListMap.put(HREF_COURSE, true);
        blockListMap.put(HREF_DOWNLOAD, true);
        blockListMap.put(HREF_MY_MESSAGE, true);
        blockListMap.put(HREF_FEEDBACK, true);
        blockListMap.put(HREF_USER_SIGN, true);
        blockListMap.put(HREF_COLUMN_ARTICLE, true);
        blockListMap.put(HREF_HTML, true);
    }

    /**
     * 是否是要单独处理的协议.
     *
     * @param schemas
     * @return
     */
    public static boolean isBlockSchemas(String schemas) {
        return blockListMap.get(schemas) != null;

    }

    /**
     * 是否是Http或者Https协议.
     *
     * @param schemas
     * @return
     */
    public static boolean isHttpSchemas(String schemas) {
        return "http".equals(schemas) || "https".equals(schemas);
    }
}
