package com.mooc.commonbusiness.constants


/**
 * SP常量类
 */
class SpConstants {

    companion object {
        const val HAS_AGREE_MENT = "has_agree_ment"    //是否点击过同意隐私政策
        const val KEY_LAST_BOOT = "last_boot"    //上次启动时间，用于判断是否是第一次启动
        const val XT_TOKEN = "xt_token"
        const val IGNORE_UPDATE_APK_VERSION = "ignore_update_apk_version" //点击忽略更新的版本，首页启动检查是否忽略
        const val DEFAULT_DOWNLOAD_LOCATION = "default_download_location" //默认下载位置
        const val STUDY_ROOM_FIRST = "study_room_first" //是否设置学习室页面为首页
        const val SHAKE_FEEDBACK = "intent_shake_setting"   //是否开启摇一摇（默认关闭）
        const val ONLY_WIFI_DOWNLOAD = "intent_wifi" //仅wifi下载（默认开启）

        const val DEFAULT_DOWNLOAD_LOCATION_JSON = "storage_key"    //默认下载位置存储路径 (存储格式是一个json {"storageBeans":[{"checked":true,"path":"/storage/emulated/0","title":"手机储存器"},{"checked":false,"path":"/storage/emulated/0/1","title":"自定义目录"}]})
        const val TODAY_READPOP_LASTTIME = "readTimeLast"    //今日阅读弹窗上次弹出时间

        const val DAILY_READING_JSON = "daily_reading_json" //每日一读缓存内容
        const val DAILY_READING_FIRST_ENTER_TIP = "daily_reading_first_enter_tip"//每日一读第一次进入提示
        const val CHECK_TIME = "checkTime"//检查时间
        const val TODAY_STUDY_SORT_TIP = "today_study_sort_tip"//今日学习排序提醒（true，false）

        const val KEY_LOCATION = "location"//外部跳转App使用
        const val SP_UUID = "uuid"
        const val SP_SEQ_NUM = "firstUpload"
        const val SP_CURRENT_TIME = "currentTime"
        const val SP_LOG_DIR_PATH = "logDirPath"
        const val SP_TEST_ACCOUNT_ARRAY = "SP_TEST_ACCOUNT_ARRAY" //测试账号数组

        //存储后台评测结果，是否弹出过提示，json存储评测数组
        const val SP_SERVER_FOLLOWUP_RESULT = "SP_SERVER_FOLLOWUP_RESULT"

        //存储跟读内容是否上传成功
        const val SP_FOLLOWUP_UPLAOD_STATE = "SP_FOLLOWUP_UPLAOD_STATE"

        //version
        const val SP_PRIVACY_VERSION = "SP_PRIVACY_VERSION"

        const val SP_HOME_STORE_PERMISSION_REQUEST = "SP_HOME_STORE_PERMISSION_REQUEST" //是否在首页申请过存储权限

        const val SP_TASK_FIRST_GUIDE = "SP_TASK_FIRST_GUIDE" //是否显示第一次的任务引导
        const val SP_SKIN_Suffix = "SP_SKIN_Suffix" //皮肤后缀名

        const val SP_RANK_REFRESH_TIP = "SP_RANK_REFRESH_TIP" //是否提示过排行榜10分钟一刷新


        const val SP_LAST_TEST_RESOURCE = "SP_LAST_TEST_RESOURCE" //上次存的测试资源

    }
}