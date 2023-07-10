package com.mooc.home.ui.hornowall.activityrank

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.home.R
import com.mooc.home.model.ActivityRank
import com.mooc.resource.widget.Space120FootView

class HonorActivityRankListAdapter(list:ArrayList<ActivityRank>) : BaseQuickAdapter<ActivityRank,BaseViewHolder>(
        R.layout.home_item_honor_activity_rank,list
){
    override fun convert(holder: BaseViewHolder, item: ActivityRank) {
        holder.setText(R.id.tvTitle,item.name)
        Glide.with(context).load(item.img).override(48.dp2px(),48.dp2px()).into(holder.getView(R.id.ivCover))
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        setFooterView(Space120FootView(context))
    }
}