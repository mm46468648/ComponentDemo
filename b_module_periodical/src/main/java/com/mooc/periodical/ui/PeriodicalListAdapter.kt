package com.mooc.periodical.ui

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.search.PeriodicalBean
import com.mooc.periodical.R

class PeriodicalListAdapter(list:ArrayList<PeriodicalBean>?) : BaseQuickAdapter<PeriodicalBean,BaseViewHolder>(
    R.layout.periodical_item_list,list) ,LoadMoreModule{
    override fun convert(holder: BaseViewHolder, item: PeriodicalBean) {

        holder.setText(R.id.tvTitle,item.basic_title)
        holder.setText(R.id.tvAuthor,item.basic_creator)

        //分割线
        holder.setVisible(R.id.viewLine,holder.adapterPosition != data.size - 1)
    }
}