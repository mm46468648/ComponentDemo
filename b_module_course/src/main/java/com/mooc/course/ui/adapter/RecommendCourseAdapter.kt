package com.mooc.course.ui.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.course.R

class RecommendCourseAdapter(data: MutableList<CourseBean>?) : BaseQuickAdapter<CourseBean, BaseViewHolder>(R.layout.item_recommend_course, data) {


    override fun convert(holder: BaseViewHolder, item: CourseBean) {

        Glide.with(context).load(item.picture).placeholder(R.mipmap.common_bg_cover_default).into(holder.getView(R.id.cover))
        holder.setText(R.id.title, item.title)
        holder.setText(R.id.org, item.platform_zh)
    }
}