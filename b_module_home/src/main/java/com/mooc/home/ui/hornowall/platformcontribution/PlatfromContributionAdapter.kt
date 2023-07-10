package com.mooc.home.ui.hornowall.platformcontribution

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.home.R
import com.mooc.home.model.honorwall.PlatformContributionBean
import com.mooc.resource.widget.MoocImageView


class PlatfromContributionAdapter(data: MutableList<PlatformContributionBean>?, layoutResId: Int = R.layout.home_item_platform_contribution) : BaseQuickAdapter<PlatformContributionBean, BaseViewHolder>(layoutResId, data) , LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: PlatformContributionBean) {

        holder.setText(R.id.tvName, nameStr(item.name))

        holder.setText(R.id.tvID, "ID:" + item.user_id)
        holder.setText(R.id.tvScore, item.devote)
        holder.getView<MoocImageView>(R.id.mivAvatar).setImageUrl(item.avatar, true)
        holder.setVisible(R.id.tvRank, false)
        holder.setVisible(R.id.ranking_img, false)
        if (0 == holder.adapterPosition){
            holder.setImageResource(R.id.ranking_img, R.mipmap.home_ic_honor_one)
            holder.setVisible(R.id.ranking_img, true)
        }else if (1 == holder.adapterPosition){
            holder.setImageResource(R.id.ranking_img, R.mipmap.home_ic_honor_two)
            holder.setVisible(R.id.ranking_img, true)
        }else if (2 == holder.adapterPosition){
            holder.setImageResource(R.id.ranking_img, R.mipmap.home_ic_honor_three)
            holder.setVisible(R.id.ranking_img, true)
        }else{
            holder.setText(R.id.tvRank, (holder.adapterPosition + 1).toString())
            holder.setVisible(R.id.tvRank, true)
        }
    }

}

fun nameStr(mobiles: String): String {
    return if (mobiles.length> 5){
        mobiles.replace("(.{3}).*(.{2})".toRegex(), "$1****$2")
    }else mobiles
}