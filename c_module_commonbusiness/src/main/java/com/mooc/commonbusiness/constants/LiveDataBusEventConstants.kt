package com.mooc.commonbusiness.constants

/**
 * LiveData事件总线的常量
 */
class LiveDataBusEventConstants {

    companion object{
        const val EVENT_USER_LOGIN = "event_user_login"    //用户登录事件
        const val EVENT_DISCOVER_TAB_CHANGE = "event_discover_tab_change"    //发现页Tab切换事件
        const val EVENT_DISCOVER_TAB_CHILD_ID = "event_discover_tab_child_id"    //发现页Tab切换事件，二级分类id
        const val EVENT_USERINFO_CHANGE = "event_userinfo_change"   //用户信息改变

        const val EVENT_LISTEN_TRACK_ID = "event_discover_child_list_count" //正在听的音频id

//        const val EVENT_MOVE_RESOURCE_ID = "event_move_resource_id" //学习室移动资源id
//        const val EVENT_MOVE_FOLDER_ID = "event_move_folder_id" //学习室移动文件夹id

        const val EVENT_CHILDE_FRAGMENT_SELECTPOSITION = "event_childe_fragment_selectposition" //子fragment选中位置

        const val STUDYLIST_PUBLIC_STATUS = "studylist_public_status" //当前学习清单公开状态
    }
}