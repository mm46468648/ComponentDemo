package com.mooc.battle.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.battle.GameConstant
import com.mooc.battle.R
import com.mooc.battle.model.SkillInfo
import com.mooc.commonbusiness.utils.format.TimeFormatUtil

class SkillOtherCreateAdapter(list: ArrayList<SkillInfo>) :
    BaseQuickAdapter<SkillInfo, BaseViewHolder>(R.layout.item_skill_other_create, list),
    LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: SkillInfo) {
        holder.setText(R.id.tvTitle, item.title)
        val stratTime = TimeFormatUtil.formatDate(
            (item.start_time_stamp * 1000).toLong(),
            TimeFormatUtil.yyyydMMddd
        )
        val endTime = TimeFormatUtil.formatDate(
            (item.end_time_stamp * 1000).toLong(),
            TimeFormatUtil.yyyydMMddd
        )
        holder.setText(R.id.tvTime, "${stratTime}~${endTime}")
        val creator = GameConstant.getFormatName(item.creator_name, 15)
        holder.setText(R.id.tvCreator, "此比武由 ${creator} 发起")
        holder.setGone(R.id.tvLook, item.is_enroll == 0)
    }
}