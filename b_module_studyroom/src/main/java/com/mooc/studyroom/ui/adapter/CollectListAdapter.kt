package com.mooc.studyroom.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.getColorRes
import com.mooc.studyroom.R
import com.mooc.studyroom.model.CollectList

class CollectListAdapter(list:ArrayList<CollectList>) : BaseQuickAdapter<CollectList,BaseViewHolder>(
    R.layout.studyroom_item_collect_studylist,list) ,LoadMoreModule{

    override fun convert(holder: BaseViewHolder, item: CollectList) {

        holder.setText(R.id.tvCreateName,item.name)
        holder.setText(R.id.tvNum,"共${item.resource_count}个学习资源")
        val nameStr = if(item.username.length>8)
            item.username.replaceRange( 8,item.username.length,"...") else item.username

        if(item.is_admin){
            val fromStr = "来自 军职在线 公开的学习清单"
            val value = spannableString {
                str = fromStr
                colorSpan {
                    start = fromStr.indexOf("军职在线")
                    end = fromStr.indexOf("军职在线") + 4
                    color = context.getColorRes(R.color.colorPrimary)
                }
            }
            holder.setText(R.id.tvFrom, value)
        }else{
            holder.setText(R.id.tvFrom,"来自 ${nameStr} 公开的学习清单")
        }
    }
}