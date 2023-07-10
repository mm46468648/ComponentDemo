package com.mooc.commonbusiness.constants

/**
 * 分享的数据类型常量类
 */
class ShareTypeConstants {

    companion object{
        const val TYPE_APP = "share_app"            //我的页面，分享app
        const val TYPE_NOTE = "share_note"              //分享笔记
        const val TYPE_ALBUM = "share_album"        //专辑分享
        const val TYPE_COLUMN = "share_column"         //专栏页面
        const val TYPE_COURSE = "share_course"        //课程详情
        const val TYPE_READ = "share_read"           //每日一读
        const val TYPE_STUDYPLAN = "share_studyplan"
        const val TYPE_TRACK = "share_track"
        const val TYPE_CHECKIN = "share_checkin"
        const val TYPE_PUBLICATION = "share_kanwu"
        const val TYPE_EBOOK = "share_ebook"
        const val TYPE_MEDAL = "share_medal"
        const val TYPE_DATA = "share_data"
        const val TYPE_ARTICAL = "share_article"
        const val TYPE_PERIODICAL = "share_periodical"
        const val TYPE_BAIKE = "share_baike"
        const val TYPE_MICRO_COURSE = "share_micro_course"
        const val TYPE_QUESTIONNAIRE = "share_questionnaire"

        const val TYPE_SCORE_BOARD = 205 //数据面板



        //获取带拉新分享地址传递的类型
        // *   (6,  u'分享资源邀请类型'),          支持的资源包括 电子书，自建音频，期刊，刊物，音频，单条，课程，问卷，测试卷，文章，专栏
        //         (7,  u'分享学习项目邀请类型'),
        //         (2,  u'分享App邀请类型'),
        //         (3,  u'分享勋章邀请类型'),
        //         (4,  u'分享个人数据邀请类型')
        //     */
        const val SHARE_TYPE_RESOURCE = "6"
        const val SHARE_TYPE_STUDYPROJECT = "7"
        const val SHARE_TYPE_APP = "2"
        const val SHARE_TYPE_MEDAL = "3"
        const val SHARE_TYPE_USERDATA = "4"
    }
}