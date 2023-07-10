package com.mooc.studyproject.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.studyproject.R
import com.mooc.studyproject.model.BetPoint

/**

 * @author limeng
 * @date 2021/3/12
 */
class BetPointAdapter(data: ArrayList<BetPoint>?) : BaseQuickAdapter<BetPoint, BaseViewHolder>(R.layout.studyproject_item_bet_point, data) {
    override fun convert(holder: BaseViewHolder, item: BetPoint) {


        if (item.selected) {
            holder.setGone(R.id.selectedImg, false)
            holder.setBackgroundResource(R.id.bgRl, R.drawable.studyproject_colorprimary_width2_radius7)
        } else {
            holder.setGone(R.id.selectedImg, true)
            holder.setBackgroundResource(R.id.bgRl, R.drawable.studyproject_color79_width2_radius7)

        }

        if (item.key.equals("0")) {
            holder.setGone(R.id.betRl,true)
            holder.setGone(R.id.noJonTv,false)
        } else {
            holder.setGone(R.id.betRl,false)
            holder.setGone(R.id.noJonTv,true)
            holder.setText(R.id.betKey,item.key)
            holder.setText(R.id.betValue,item.value)
        }

    }


}