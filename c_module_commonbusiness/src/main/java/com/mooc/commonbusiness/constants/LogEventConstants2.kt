package com.mooc.commonbusiness.constants

class LogEventConstants2 {

    companion object {
        //EVENT
        const val E_CLICK = "onClick"     //点击
        const val E_LOAD = "onLoad"     //页面载入

        //TO
        const val T_BAIKE = "BAIKE"
        const val T_PERIODICAL = "PERIODICAL"
        const val T_ALBUM = "ALBUM"
        const val T_EBOOK = "EBOOK"
        const val T_KNOWLEDGEPOINT = "KNOWLEDGEPOINT"
        const val T_FOLDER = "FOLDER"
        const val T_ARTICLE = "ARTICLE"
        const val T_AUDIO = "AUDIO"
        const val T_AUDIOALBUM = "AUDIOALBUM"
        const val T_COURSE = "COURSE"

        const val F_LAUNCH = "launch"    //启动图
        const val F_QUICK = "quick_entry"    //快捷入口
        const val F_WINDOW = "window"    //弹窗
        const val F_BANNER = "banner"    //banner
        //ETYPE
        //1. 针对具体资源的点击，跟资源类型对应（比如2是课程 5是电子书之类的）
        //2. 当点击的是页面的一些筛选项时，用约定的字符串填写，入快捷入口qs、最顶上的导航tab、下面的分类cate，banner页ban，icon独立图标，qd学习清单；

        const val ET_NAV = "nav"
        const val ET_TAB = "tab"
        const val ET_ICON = "icon"
        const val ET_QS = "qs"
        const val ET_CATE = "cate"
        const val ET_BAN = "ban"
        const val ET_QD = "qd"
        const val ET_CHECK = "check"
        const val ET_ALL = "all"
        const val ET_POP= "pop"

        //PAGE
        const val P_HOME = "HOMEPAGE"
        const val P_DISCOVER = "DISCOVER"
        const val P_TODAY = "TODAY"
        const val P_ROOM = "ROOM"
        const val P_HONOUR = "HONOUR"
        const val P_MY = "MY"
        const val P_SEARCH = "SEARCH"
        const val P_EBOOK = "EBOOK"
        const val P_SPECIAL = "ZHUANTI"
        const val P_COLUME = "ZHUANLAN"
        const val P_DAILYREAD = "DAILYREAD"
        const val P_STUDYPLAN = "STUDYPLAN"
        const val P_RMKC = "RMKC"          //热门课程
        const val P_XXSX = "XXSX"          //新鲜上线
        const val P_SIM = "SIM"          //相似资源推荐
        const val P_ADV = "ADV"          //splash启动页
        const val P_POP = "POP"          //首页消息弹窗


        //首页底部导航对应自负窗
        val NavStringMap = hashMapOf<Int, String>(
            0 to "发现",
            1 to "今日学习",
            2 to "学习室",
            3 to "荣誉墙",
            4 to "我的",
        )

        /**
         * 资源对应的日志点位
         */
        val typeLogPointMap = hashMapOf<Int, String>(
            ResourceTypeConstans.TYPE_COURSE to LogPageConstants.PID_COURSE,
            ResourceTypeConstans.TYPE_E_BOOK to LogPageConstants.PID_EBOOK,
            ResourceTypeConstans.TYPE_BAIKE to LogPageConstants.PID_BAIKE,
            ResourceTypeConstans.TYPE_PERIODICAL to LogPageConstants.PID_PERIODICAL,
            ResourceTypeConstans.TYPE_KNOWLEDGE to LogPageConstants.PID_KNOWLEDGEPOINT,
            ResourceTypeConstans.TYPE_ARTICLE to LogPageConstants.PID_ARTICLE,
            ResourceTypeConstans.TYPE_OTHER_COURSE to LogPageConstants.PID_COURSE,
            ResourceTypeConstans.TYPE_SPECIAL to LogPageConstants.PID_SPECIALSUBJECT,
            ResourceTypeConstans.TYPE_COLUMN to LogPageConstants.PID_COLUMN_DETAIL,
            ResourceTypeConstans.TYPE_ACTIVITY to LogPageConstants.PID_ARTICLE,
            ResourceTypeConstans.TYPE_COLUMN_ARTICLE to LogPageConstants.PID_ARTICLE,
            ResourceTypeConstans.TYPE_STUDY_PLAN to LogPageConstants.PID_STUDYPLAN,
            ResourceTypeConstans.TYPE_PUBLICATION to LogPageConstants.PID_PUBLICATION,
            ResourceTypeConstans.TYPE_TRACK to LogPageConstants.PID_AUDIO,
            ResourceTypeConstans.TYPE_ALBUM to LogPageConstants.PID_ALBUM,
            ResourceTypeConstans.TYPE_ONESELF_TRACK to LogPageConstants.PID_OWNBUILD_AUDIO,
            ResourceTypeConstans.TYPE_MICRO_LESSON to LogPageConstants.PID_MICROLESSON,
            ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL to LogPageConstants.PID_MICRO_PROFESSION,
        )

        /**
         * 分享位置的日志点位
         */
        val typeSharePointMap = hashMapOf<Int, String>(
            0 to "WX",
            1 to "PYQ",
            2 to "XYQ",
        )
    }
}