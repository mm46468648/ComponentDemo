package com.mooc.studyroom.ui.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.studyroom.R
import com.mooc.studyroom.model.FriendRank
import com.mooc.studyroom.model.ScoreDetail

class FriendScoreAdapter(list:ArrayList<FriendRank>)
    : BaseQuickAdapter<FriendRank,BaseViewHolder>(R.layout.studyroom_item_friend_score,list),LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: FriendRank) {
        val view = holder.getView<ImageView>(R.id.ivHead)
        Glide.with(view).load(item.avatar).transform(CircleCrop()).into(view)

        holder.setText(R.id.tvName,item.user_name)
        holder.setText(R.id.tvScore,item.score)

        when(item.rank){
            1->{
                holder.setGone(R.id.ivRank,false)
                holder.setGone(R.id.tvRank,true)
                holder.setImageResource(R.id.ivRank,R.mipmap.studyroom_ic_friend_rank_one)
            }
            2->{
                holder.setGone(R.id.ivRank,false)
                holder.setGone(R.id.tvRank,true)
                holder.setImageResource(R.id.ivRank,R.mipmap.studyroom_ic_friend_rank_two)
            }
            3->{
                holder.setGone(R.id.ivRank,false)
                holder.setGone(R.id.tvRank,true)
                holder.setImageResource(R.id.ivRank,R.mipmap.studyroom_ic_friend_rank_three)
            }
            else->{
                holder.setGone(R.id.ivRank,true)
                holder.setGone(R.id.tvRank,false)
                holder.setText(R.id.tvRank,item.rank.toString())
            }
        }

    }
}