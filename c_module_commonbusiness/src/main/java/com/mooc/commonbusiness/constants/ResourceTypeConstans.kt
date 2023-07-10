package com.mooc.commonbusiness.constants

/**
 * 资源类型
 */
class ResourceTypeConstans {

    companion object {
        const val TYPE_UNDEFINE = -1    //未定义
        const val TYPE_BLOCK = 0         //推荐查看更多
        const val TYPE_COURSE = 2    //课程
        const val TYPE_E_BOOK = 5 //电子书
        const val TYPE_ACTIVITY = 9 //活动
        const val TYPE_BAIKE = 10   //百科
        const val TYPE_PERIODICAL = 11 //期刊
        const val TYPE_KNOWLEDGE = 12
        const val TYPE_TU_LING = 13 //图灵
        const val TYPE_ARTICLE = 14 //文章
        const val TYPE_SPECIAL = 15 //专题（合集）
        const val TYPE_OTHER_COURSE = 16
        const val TYPE_COLUMN = 17 //专栏
        const val TYPE_COLUMN_ARTICLE = 18 //专栏文章
        const val TYPE_RECOMMEND_OUT_LINK = 19 //友情链接
        const val TYPE_STUDY_PLAN = 20 //学习项目
        const val TYPE_ALBUM = 21 //音频课
        const val TYPE_TRACK = 22 //音频
        const val TYPE_ACTIVITY_TASK = 24 //活动任务
        const val TYPE_MASTER_TALK = 25;  //大师课
        const val TYPE_NOTE = 26 //笔记
        const val TYPE_TEST_VOLUME = 27 //测试卷
        const val TYPE_FOLLOWUP_RESOURCE = 28 //跟读资源
        const val TYPE_STUDY_PLAN_DYNAMIC = 30  //学习项目动态
        const val TYPE_ONESELF_TRACK = 31 //自建音频
        const val TYPE_QUESTIONNAIRE = 32 //问卷
        const val TYPE_MICRO_LESSON = 33 //国开微课
        const val TYPE_ARTICLE_NOIMAGE = 34 //无图文章
        const val TYPE_MICRO_KNOWLEDGE = 40 //微知识
        const val TYPE_PUBLICATION = 51 //刊物
        const val TYPE_SOURCE_FOLDER = 77 //学习清单对应的资源类型
        const val TYPE_CUSTOMER_SERVICE = 80 //客服
        const val TYPE_WX_PROGRAM = 100 //微信小程序
        const val TYPE_FOLDER = 100  //学习清单文件夹,用来移动的时候传递 (接口定义不可乱改)
        const val TYPE_TASK = 124  //任务详情类型
        const val TYPE_PUSH_HTML = 202 //推送到html
        const val TYPE_WE_CHAT = 203 //外链文章
        const val TYPE_STUDY_DATA = 205 //数据面板
        const val TYPE_BATTLE = 400 //对战
        const val TYPE_OTHER = -110 //其他类型
        const val TYPE_DEFAULT = -111 //默认类型
        const val TYPE_MICRO_PROFESSIONAL = 901 //微专业
        const val TYPE_ROOM_TAB_OTHER = 999 //其他
        const val TYPE_ROOM_TAB_EMPTY = -999 //空tab
        const val TYPE_ZY_READER = 2387 //掌阅
        const val TYPE_NO_IMAGE = 2388 //无图
        const val TYPE_RECOMMEND_USER = 2389 //推荐相似用户
        const val TYPE_STUDY_FOLDER = "folder" //学习清单文件夹
        const val TYPE_REPORT_NUM_USER = 500 //举报用户类型
        const val TYPE_INTEGRAL = 726 //
        const val TYPE_INTEGRAL_RECORD = 727 //



        //资源类型对应标题
        val typeStringMap = hashMapOf<Int, String>(
            TYPE_COURSE to "课程",
            TYPE_ALBUM to "音频课",
            TYPE_TRACK to "音频",
            TYPE_KNOWLEDGE to "知识点",
            TYPE_MICRO_LESSON to "微课",
            TYPE_PERIODICAL to "期刊",
            TYPE_COLUMN_ARTICLE to "文章",
            TYPE_ARTICLE to "文章",
            TYPE_COLUMN to "专栏",
            TYPE_E_BOOK to "电子书",
            TYPE_STUDY_PLAN to "学习项目",
            TYPE_ACTIVITY to "活动",
            TYPE_ACTIVITY_TASK to "活动任务",
            TYPE_RECOMMEND_OUT_LINK to "友情链接",
            TYPE_SPECIAL to "合集",   //专题
            TYPE_BAIKE to "百科",
            TYPE_TEST_VOLUME to "测试卷",
            TYPE_QUESTIONNAIRE to "问卷",
            TYPE_ONESELF_TRACK to "自建音频",
            TYPE_WX_PROGRAM to "小程序",
            TYPE_BATTLE to "对战",
            TYPE_PUBLICATION to "刊物",
            TYPE_MICRO_PROFESSIONAL to "微专业",
            TYPE_NOTE to "笔记",
            TYPE_TU_LING to "知识库",
            TYPE_MICRO_KNOWLEDGE to "微知识",
            TYPE_FOLLOWUP_RESOURCE to "跟读",
            TYPE_TASK to "任务",
            TYPE_CUSTOMER_SERVICE to "客服"
        )

        //资源类型别名
        val typeAliasName = hashMapOf<Int, String>(
            TYPE_COURSE to "course",
            TYPE_TRACK to "track",
            TYPE_ONESELF_TRACK to "audio",
            TYPE_KNOWLEDGE to "knowledge",
            TYPE_MICRO_LESSON to "micro_course",
            TYPE_PERIODICAL to "periodical",
            TYPE_ARTICLE to "article",
            TYPE_COLUMN_ARTICLE to "article",
            TYPE_E_BOOK to "ebook",
            TYPE_BAIKE to "baike"
        )

        //资源类型别名转换成资源类型
        val typeAliasToResource = hashMapOf<String, Int>(
            "course" to TYPE_COURSE,
            "album" to TYPE_ALBUM,
            "study_plan" to TYPE_STUDY_PLAN,
            "folder" to TYPE_FOLDER,
            "track" to TYPE_TRACK,
            "audio" to TYPE_ONESELF_TRACK,
            "knowledge" to TYPE_KNOWLEDGE,
            "micro_course" to TYPE_MICRO_LESSON,
            "chaoxing" to TYPE_PERIODICAL,
            "article" to TYPE_ARTICLE,
            "ebook" to TYPE_E_BOOK,
            "baike" to TYPE_BAIKE,
            "tuling" to TYPE_TU_LING,
            "kanwu" to TYPE_PUBLICATION,
            "micro_knowledge" to TYPE_MICRO_KNOWLEDGE
        )

        val typeAliasToName = hashMapOf<String, String>(
            "course" to "课程",
            "album" to "音频课",
            "study_plan" to "学习项目",
            "folder" to "学习清单",
            "track" to "音频",
            "audio" to "自建音频",
            "knowledge" to "知识点",
            "micro_course" to "微课",
            "chaoxing" to "期刊",
            "article" to "文章",
            "ebook" to "电子书",
            "baike" to "百科",
            "tuling" to "知识库",
            "kanwu" to "刊物",
            "micro_knowledge" to "微知识"
        )

        //搜索内容加载的顺序(总共13中)
        val searchTypeSort = arrayListOf<String>(
            "micro_knowledge",
            "study_plan",
            "course",
            "ebook",
            "album",
            "track",
            "article",
            "chaoxing",
            "micro_course",
            "folder",
            "kanwu",
            "baike",
            "tuling"
        )

        //工信部点击不进入课程详情页面，需要弹框的课程id
        val allCourseDialogId = arrayListOf<String>(
            "25924",
            "25925",
            "25926",
            "25927",
            "25928",
            "25929",
            "26188",
            "26189",
            "26190",
            "28901",
            "28902",
            "29216",
            "30446",
            "30447",
            "30448",
            "30449",
            "30450",
            "30451",
            "30452",
            "30453",
            "12627",
            "12631"
        )

    }
}