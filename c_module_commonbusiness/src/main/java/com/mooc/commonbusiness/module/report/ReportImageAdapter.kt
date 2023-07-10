package com.mooc.commonbusiness.module.report

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.R
import com.ypx.imagepicker.bean.ImageItem


class ReportImageAdapter(list : ArrayList<String>) : BaseQuickAdapter<String,BaseViewHolder>(
    R.layout.common_item_report_image,list) {
    override fun convert(holder: BaseViewHolder, item: String) {
        Glide.with(context).load(item).into(holder.getView(R.id.ivReport))
    }
}