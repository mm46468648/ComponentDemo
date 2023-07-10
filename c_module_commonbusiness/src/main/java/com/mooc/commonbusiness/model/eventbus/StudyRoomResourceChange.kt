package com.mooc.commonbusiness.model.eventbus

/**
 * 学习室资源发生改变事件
 *@param moveType 0 文件夹(删除，移动，添加)，1 资源（删除移动）
 *@param resourceId 如果是移动资源，需要传递移动的资源id
 */
class StudyRoomResourceChange(var moveType : Int, var resourceId : String = ""){


    companion object{
        const val TYPE_FOLODER = 0
        const val TYPE_RESOURCE = 1
    }



}