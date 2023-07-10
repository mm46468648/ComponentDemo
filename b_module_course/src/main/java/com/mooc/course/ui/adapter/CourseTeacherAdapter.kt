package com.mooc.course.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.course.R
import com.mooc.course.model.StaffInfo
import com.mooc.resource.widget.MoocImageView

class CourseTeacherAdapter(list:ArrayList<StaffInfo>) : BaseQuickAdapter<StaffInfo,BaseViewHolder>(
        R.layout.course_item_teacher,list
){
    override fun convert(holder: BaseViewHolder, item: StaffInfo) {
        holder.setText(R.id.tvTeacherName,item.name)
        holder.setText(R.id.tvTeacherDesc,item.about)
        holder.getView<MoocImageView>(R.id.ivTeacherAvater).setImageUrl(item.avatar,true)

    }
}