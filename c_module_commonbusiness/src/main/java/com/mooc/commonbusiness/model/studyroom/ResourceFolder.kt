package com.mooc.commonbusiness.model.studyroom

import com.mooc.commonbusiness.constants.ResourceTypeConstans

/**
 * 资源文件夹（目录）
 */
data class ResourceFolder(
        var folder: FolderItems,
        var property: Property,
)

data class FolderItems(
        var folder: FolderItems,
        var items: ArrayList<FolderItem>?
)

/**
 * 学习清单属性
 * 是否公开，公开数量，有效资源数
 */
data class Property(
        var show_count: Int,//公开的清单数量
        var folder_name: String,//学习清单名称
        var is_show: Boolean, //公开状态
        var is_like: Boolean, //是否点赞
        var is_collect: Boolean, //是否收藏
        var resource_count: Int,  //有效资源数
        var pid:Int    // 不是0：子文件夹   0：根文件夹
)

data class FolderItem(
        var id: String,
        var name: String,      //文件夹名字
        var count: Int = 0,    //子文件夹数
        var folder_id:String="",

) : com.mooc.commonbusiness.interfaces.StudyResourceEditable {

    var is_show: Boolean = false  // 是否公开

    override var resourceId: String
        get() = id
        set(value) {}
    override var sourceType: String
        get() = ResourceTypeConstans.TYPE_FOLDER.toString()
        set(value) {}
}