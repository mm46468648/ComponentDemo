package com.mooc.studyroom.model

import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.StudyResourceEditable

class CollectList : StudyResourceEditable {
    var id :String = ""
    var folder_id :String = "" //学习清单id
    var name :String = "" //"学习清单名称"
    var resource_count :Int = 0//学习资源数量
    var like_count :String = ""//点赞数
    var username :String = ""      //来自哪位用户的清单
    var from_folder_id :String = ""      //原始的学习清单id
    var is_admin : Boolean = false

    override val resourceId: String
        get() = folder_id
    override val sourceType: String
        get() = ResourceTypeConstans.TYPE_FOLDER.toString()
}