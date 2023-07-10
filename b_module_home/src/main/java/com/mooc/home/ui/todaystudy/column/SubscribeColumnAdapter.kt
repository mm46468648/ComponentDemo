package com.mooc.home.ui.todaystudy.column

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.home.R
import com.mooc.home.model.todaystudy.TodaySuscribe

class SubscribeColumnAdapter(list: ArrayList<TodaySuscribe>)
    : BaseQuickAdapter<TodaySuscribe, BaseViewHolder>(R.layout.home_item_todaystudy_subscribe, list), LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: TodaySuscribe) {
        holder.setText(R.id.tvTitle, item.colum_name)
        holder.setText(R.id.tvDesc, item.title)
        holder.setText(R.id.tvTime, item.publish_time)

        val adapterPosition = holder.adapterPosition
        if(adapterPosition>0){
            //如果当前和上一个标题相同则隐藏标题栏
            val lastColumnName = data.get(adapterPosition - 1).colum_name
            val currentColumnName = data.get(adapterPosition).colum_name
            holder.setGone(R.id.tvTitle,lastColumnName == currentColumnName)
        }else{
            holder.setGone(R.id.tvTitle,false)
        }
    }
}