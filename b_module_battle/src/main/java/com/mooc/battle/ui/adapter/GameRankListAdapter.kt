package com.mooc.battle.ui.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.battle.R
import com.mooc.battle.model.RankDeatilsBean


/**

 * @Author limeng
 * @Date 2022/12/28-10:19 上午
 */
class GameRankListAdapter(data: ArrayList<RankDeatilsBean.RankListBean>) :
    BaseQuickAdapter<RankDeatilsBean.RankListBean, BaseViewHolder>
        (R.layout.battle_item_rank, data),
    LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: RankDeatilsBean.RankListBean) {
        if (holder.layoutPosition == 0) {
           holder.setGone(R.id.ivLineTop,false)
        }else{
            holder.setGone(R.id.ivLineTop,true)
        }
        holder.setText(R.id.tvScore, item.total_score + "分")
        holder.setText(R.id.tvUserName, item.nickname)
        holder.setText(R.id.tvNum, item.rank_num)
        val imageView = holder.getViewOrNull<ImageView>(R.id.ivHead)
        if (imageView != null) {
            Glide.with(context).load(item.cover).circleCrop()
                .placeholder(R.mipmap.common_ic_user_head_default)
                .error(R.mipmap.common_ic_user_head_default).into(imageView)
        }
    }
}