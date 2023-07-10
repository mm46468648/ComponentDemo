package com.mooc.battle.ui.adapter

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.battle.R
import com.mooc.battle.model.GameRankResponse

class GameRankAdapter(list: ArrayList<GameRankResponse.RankInfo>?) :
    BaseQuickAdapter<GameRankResponse.RankInfo, BaseViewHolder>(R.layout.item_game_rank, list),
    LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: GameRankResponse.RankInfo) {

        holder.setText(R.id.tvName, item.nickname)
        holder.setText(R.id.tvRank, item.rank_num.toString())
        holder.setText(R.id.tvStar, item.stars)
        holder.setText(R.id.tvLevel, item.level_title)
        Glide.with(holder.itemView).load(item.cover)
            .error(R.mipmap.common_ic_user_head_default)
            .placeholder(R.mipmap.common_ic_user_head_default)
            .transform(CircleCrop()).into(holder.getView(R.id.ivHead))
    }


}