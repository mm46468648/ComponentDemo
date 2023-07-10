package com.mooc.commonbusiness.constants

import com.mooc.commonbusiness.net.ApiService


/**
 * url相关常量类
 */
class UrlConstants {

    companion object{
        //更新日志

        val UPDATE_LOG_URL = "${ApiService.BASE_URL}/publishment/article/2891/"
        //意见反馈
        @JvmField
        val FEEDBACK_URL = "${ApiService.BASE_URL}/publishment/article/9491/"
         //平台操作指南帮助文档
        const val HELP_DOCUMENT_URL = "https://mp.weixin.qq.com/s/n18RwLufcdyCGQZZlZfTpw"
        //用户服务协议
        @JvmField
        val USER_SERVICE_AGREDDMENT_URL = "${ApiService.BASE_URL}/agreement.html"
        @JvmField
        val PRIVACY_POLICY_URL = "${ApiService.BASE_URL}/privacy.html"

        //分享相关参数
        const val SHARE_FOOT = "?is_share=1"
        const val SHARE_FOOT_MASTER = "&is_master=0"
        @JvmField
        val COLUMN_SHARE_URL_HEADER: String = ApiService.BASE_URL + "/weixin/official/openapp/column/"
        //音频分享
        val ALBUM_SHARE_URL: String = ApiService.BASE_URL + "/weixin/official/openapp/xima/album/"
        val TRACK_SHARE_URL: String = ApiService.BASE_URL + "/weixin/official/openapp/xima/audio/"
        //课程分享学友圈
        val COURSE_SHARE_URL_HEADER: String = ApiService.BASE_URL + "/weixin/official/openapp/coursedetail/"
        val NEWXT_COURSE_SHARE_URL_HEADER: String = ApiService.BASE_URL + "/weixin/official/menu/coursedetail/%s?platform=45"
        //分享logo地址
        const val SHARE_LOGO_URL = "https://static.learning.mil.cn/ucloud/moocnd/img/iv_share.png"
        //apk下载地址
        val APK_DOWNLOAD_PATH: String = ApiService.BASE_URL + "/weixin/official/openapp/appdownload"
        //超星
        const val CHAOXING_BASE_URL = "http://process.learning.mil.cn/http://www.zhizhen.com"
        //知识点
        const val XUETANG_KNOWLEDGE_URL = ApiService.XT_ROOT_URL +  "/mobile/hybrid/fragment/%s?client=android"
        //apk下载
        val APK_DOWNLOAD: String = ApiService.XT_ROOT_URL + "/weixin/official/openapp/appdownload"
        //中国大学mooc下载html
        const val CHINA_MOOC_APK_DOWNLOAD_PAGE = "http://www.icourse163.org/mobile.htm#/mobile"

    }
}