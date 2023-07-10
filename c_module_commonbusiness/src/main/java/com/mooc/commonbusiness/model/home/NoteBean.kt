package com.mooc.commonbusiness.model.home

import android.os.Parcelable
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.interfaces.StudyResourceEditable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoteBean(
        var id: String = "",
        val user_id: String = "",
        val other_resource_type: Int = 0,
        val other_resource_id: Int = 0,
        val other_resource_url: String = "",
        val status: Int = 0,
        val create_time: String = "",
        val update_time: String = "",
        val other_resource_title: String = "",
        val recommend_id: String = "",
        val recommend_title: String = "",
        val recommend_type: String = "",
        val content: String = "",
        val is_active: Boolean = false,
        val in_guidang: Boolean = false,
        val is_enrolled: Boolean = false,
        val canDel: Boolean = false,
        val basic_title_url: String = "",
        val basic_url: String = "",
        var is_allowed_move:String          //所在学习清单是否公开 1未公开可编辑，0
): StudyResourceEditable,BaseResourceInterface, Parcelable{
    override var _resourceId: String
        get() = id
        set(value) {}
    override var _resourceType: Int
        get() = ResourceTypeConstans.TYPE_NOTE
        set(value) {}
    override var _other: Map<String, String>?
        get() = null
        set(value) {}
    override var resourceId: String
    get() = id.toString()
    set(value) {}
    override var sourceType: String
    get() = ResourceTypeConstans.TYPE_NOTE.toString()
    set(value) {}

}