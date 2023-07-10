package com.mooc.commonbusiness.module.report

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.model.ReportBean

/**
 * 举报选项适配器
 */
class ReportChoiceAdapter(list: ArrayList<ReportBean>?)
    : BaseQuickAdapter<ReportBean,BaseViewHolder>(R.layout.common_item_report_choice,list) {
    override fun convert(holder: BaseViewHolder, item: ReportBean) {
        holder.setText(R.id.tvChoiceName,item.detail)
    }
}