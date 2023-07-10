package com.mooc.course.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.course.R
import com.mooc.course.model.CourseNotice
import com.mooc.resource.widget.expandabletextview.ExpandableTextView

class CourseNoticeAdapter(list:ArrayList<CourseNotice>)
    : BaseQuickAdapter<CourseNotice,BaseViewHolder>(
        R.layout.course_item_play_notice,list) {
    override fun convert(holder: BaseViewHolder, item: CourseNotice) {
//        holder.setText(R.id.tvContent,item.content)
        val view = holder.getView<com.mooc.resource.widget.expandabletextview.ExpandableTextView>(R.id.tvContent)
        view.setContent(item.content)
        holder.setText(R.id.tvDate,item.date)
    }
}