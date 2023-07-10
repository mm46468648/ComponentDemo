package com.mooc.studyroom.ui.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.studyroom.R
import com.mooc.studyroom.model.MedalDataBean

/**
每个勋章的适配器
 * @Author limeng
 * @Date 2020/9/25-2:34 PM
 */
class MedalAdapter(data: ArrayList<MedalDataBean>, layoutId: Int = R.layout.studyroom_item_medal) :
        BaseQuickAdapter<MedalDataBean, BaseViewHolder>(layoutId, data) {
    var type: Int = 0
    var isAll: Boolean = false
    override fun convert(holder: BaseViewHolder, item: MedalDataBean) {
        holder.setText(R.id.tv_medal_layout, item.title)
        if (item.is_obtain.equals("1")) {
            holder.setGone(R.id.iv_medal_layout, false)
            holder.setGone(R.id.tv_medal_layout, false)
            Glide.with(context)
                    .load(item.after_img)
                    .placeholder(R.mipmap.studyroom_ic_medal_loading)
                    .error(R.mipmap.studyroom_ic_medal_loading)
                    .into(holder.getView(R.id.iv_medal_layout))
            holder.setTextColor(
                    R.id.tv_medal_layout,
                    context.getResources().getColor(R.color.color_3)
            )
            if (item.medal_time?.equals(0) != null && !item.medal_time?.equals(0)!!) {
                holder.setVisible(R.id.tv_medal_time_layout, true)
                holder.setText(
                        R.id.tv_medal_time_layout, String.format(
                        context.getResources().getString(R.string.medal_time_get),
                        StringFormatUtil.timeToString(item.medal_time)
                )
                )
            } else {
                holder.setVisible(R.id.tv_medal_time_layout, false)

            }
        } else {

            Glide.with(context)
                    .load(item.before_img)
                    .placeholder(R.mipmap.studyroom_ic_medal_loading)
                    .override(66.dp2px(), 69.dp2px())
                    .error(R.mipmap.studyroom_ic_medal_loading)
                    .into(holder.getView(R.id.iv_medal_layout))
            holder.setTextColor(
                    R.id.tv_medal_layout,
                    context.getResources().getColor(R.color.color_9)
            )
            holder.setGone(R.id.tv_medal_time_layout, true)

            if (isAll) {//显示
                holder.setGone(R.id.iv_medal_layout, false)
                holder.setGone(R.id.tv_medal_layout, false)
            } else {//不显示
                holder.setGone(R.id.iv_medal_layout, true)
                holder.setGone(R.id.tv_medal_layout, true)
            }
        }
    }
}
