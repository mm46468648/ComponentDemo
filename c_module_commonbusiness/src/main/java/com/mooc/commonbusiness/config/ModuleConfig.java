package com.mooc.commonbusiness.config;

public interface ModuleConfig {
    //APP壳工程
    String APP = "com.mooc.MoocApplication";

    //主页页面
    String HOME = "com.mooc.home.HomeApplication";
    //
    String AUDIO = "com.mooc.audio.AudioApplication";

    //WEB页面
    String WEB = "com.mooc.webview.WebviewApplication";

    //搜索页面
    String SEARCH = "com.mooc.search.SearchApplication";

    //我的页面
    String MY = "com.mooc.my.MyApplication";

    //登录页面
    String LOGIN = "com.mooc.login.LoginApplication";

    //电子书
    String EBOOK = "com.mooc.ebook.EbookApplication";
    //音频播放
    String MUSICE = "com.mooc.music.MusicApplication";
    //学习计划
    String STUDY_PROJECT = "com.mooc.studyproject.StudyProjectApplication";

    //需要处理attach事件的模块
    String[] attachModules = {EBOOK};
    String[] modules = {
            APP, HOME, WEB, SEARCH, MY, LOGIN, STUDY_PROJECT, EBOOK,AUDIO
    };

}
