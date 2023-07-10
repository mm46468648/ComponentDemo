package com.mooc.commonbusiness.constants

/**
 * ！！！！！！！！！！
 * 此类仅存放，模块与模块间跳转需要传递的参数常量
 * 模块内传递的参数请定义在需要接收此参数的类中，避免冗余
 * ！！！！！！！！！！
 */
class IntentParamsConstants {

    companion object {

        val WEB_PARAMS_TASK_FINISH = "params_web_task_finish"
        val WEB_PARAMS_IS_TASK = "params_web_is_task"
        val WEB_PARAMS_TASK_COUNT_DOWN = "params_task_count_down"
        val STUDY_ROOM_CHILD_FOLDER_ID = "params_child_folder_id"

        //H5
        const val WEB_PARAMS_TITLE = "params_title"
        const val WEB_PARAMS_URL = "params_url"

        //课程
        const val COURSE_PARAMS_ID = "params_course_id"
        const val COURSE_PARAMS_TITLE = "params_course_title"    //课程标题
        const val COURSE_XT_PARAMS_ID = "params_xt_course_id"    //学堂接口中的课程id，和自己的课程资源id不要混淆(现统一传递course_id)
        const val COURSE_PARAMS_DATA = "params_course_data"    //课程信息
        const val COURSE_PARAMS_CLASSROOM_ID = "params_course_classroom_id"    //课程班级信息
        const val COURSE_PARAMS_PLATFORM = "course_params_platform"    //课程来源

        //电子书
        const val EBOOK_PARAMS_ID = "params_ebook_id"


        //专栏
        const val COLUMN_PARAMS_ID = "column_params_id"
        const val INTENT_COLUMN_DATA = "column_item_data"      //单个条目专栏数据

        //音频课
        const val ALBUM_PARAMS_ID = "album_params_id"    //音频课id
        const val ALBUM_PARAMS_LIST_SORT = "album_params_list_sort"   //列表排序

        //音频id
        const val AUDIO_PARAMS_ID = "audio_params_id"    //音频id
        const val AUDIO_PARAMS_OFFLINE = "audio_params_offline"    //音频离线模式
        const val AUDIO_PARAMS_FROM_BOTTOM_FLOAD = "audio_params_from_bottom_fload"    //音频从底部浮窗进入

        //期刊
        const val PERIODICAL_PARAMS_BASICURL = "periodical_params_basicurl"    //期刊专属的baseurl 参数

        //百科
        const val BAIKE_PARAMS_SUMMARY = "baike_params_summary" //百科中用于分享描述的字段

        //学习室
        const val STUDYROOM_FROM_FOLDER_ID = "params_from_folder_id"
        const val STUDYROOM_FOLDER_ID = "params_folder_id"
        const val STUDYROOM_FOLDER_NAME = "params_folder_name"
        const val STUDYROOM_STUDYLIST_PRISED = "params_studylist_prised" //学习清单是否点赞
        const val STUDYROOM_STUDYLIST_FORM_RECOMMEND = "params_studylist_from_recommend" //学习清单是否是来自运营推荐
        const val STUDYROOM_STUDYLIST_FORM_TASK = "params_studylist_from_task" //学习清单是否是从任务进入
        const val STUDYROOM_STUDYLIST_FORM_TASK_ID = "params_studylist_from_task_id" //学习清单从任务进入的id
        const val STUDYROOM_COLLECT_TABLIST = "studyroom_collect_tablist" //学习室收藏tablist

        //资源
        const val PARAMS_TASK_ID = "params_task_id"
        const val PARAMS_RESOURCE_ID = "params_resource_id"
        const val PARAMS_RESOURCE_TYPE = "params_resource_type"
        const val PARAMS_RESOURCE_URL = "params_resource_url"      //拦截器中的参数用，用于查询是否是学习计划中的课，todo 后期是否只针对课程单独设置，不必每次都传
        const val PARAMS_RESOURCE_TITLE = "params_resource_title"

        //资源外壳
        const val PARAMS_PARENT_ID = "params_parent_id"       //资源外壳的id
        const val PARAMS_PARENT_TYPE = "params_parent_type"       //资源外壳的id

        //学习项目
        const val STUDYPROJECT_PARAMS_ID = "params_studyproject_id"

        const val INTENT_PLAN_DYNAMIC_ID = "intent_plan_dynamic_id"
        const val INTENT_PLAN_DYNAMIC_IS_INITIATOR = "intent_plan_dynamic_is_initiator"
        const val INTENT_COMMENT_ID = "intent_comment_id"
        const val INTENT_PLANDETAILS_DATA = "intent_plandetails_data"
        const val INTENT_IS_CAN_COMMENT = "intent_is_can_comment"

        const val INTENT_KEY_JOIN = "intent_key_join"
        const val INTENT_CERTIFICATE_ID = "certificate_id"
        const val PLAN_INTRO_UI = "certificate_id"
        const val INTENT_IS_JOIN = "intent_is_join"
        const val INTENT_IS_BIND_TESTPAPER = "intent_is_bind_testpaper"
        const val INTENT_IS_FINISHED_INTELLIGENT_TEST = "intent_is_finished_intelligent_test"
        const val INTENT_BIND_TEST_ID = "intent_bind_test_id"
        const val INTENT_BIND_TEST_LINK = "intent_bind_test_link"
        const val INTENT_BIND_TEST_TITLE = "intent_bind_test_title"


        //web
        const val WEB_RESOURCE_TXT = "resource_txt"
        const val WEB_RESOURCE_TYPE = "resource_type"

        //set
        const val SET_CUSTOM_PATH = "set_custom_path"       //自定义的路径
        const val SET_CUSTOM_CHOOSE = "set_custom_choose"    //是否选择自定义路径

        // 我的
        const val Follow_Fans = "follow_fans"       //粉丝或者关注
        const val MY_USER_ID = "user_id"       //粉丝或者关注

        //图片
        const val INTENT_IMAGE_URL = "intent_image_url"

        //笔记
        const val INTENT_NODE_ID = "intent_node_id"
        const val HOME_UNDERSTAND_TYPE = "understand_type"

        //search book
        const val INTENT_BOOKS_PARAMS = "intent_books"   //已选中的书籍列表集合

        //common
        const val COMMON_IMAGE_PREVIEW_LIST = "common_image_preview_list" //预览图片集合
        const val COMMON_IMAGE_PREVIEW_POSITION = "common_image_preview_position" //当前预览图片位置
        const val COMMON_IMAGE_PREVIEW_FLAG = "common_image_preview_flag" //是否分享  1不分享
        const val INTENT_KEY_COURSE_ID = "intent_key_course_id"//外部跳转App使用


        //home
        const val HOME_SELECT_POSITION = "home_select_position"
        const val HOME_SELECT_CHILD_POSITION = "home_select_child_position"

        //act
        const val ACT_FROM_TYPE = "act_from_type" //活动从哪里进入的类型（launch,window,banner）
        //battle
        const val TOURNAMENT_ID = "tournament_id"

    }
}