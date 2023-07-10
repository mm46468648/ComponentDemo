package com.mooc.discover.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.TypedValue
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.discover.R
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.discover.model.DiscoverTaskBean


/**
 * 任务列表适配器
 */
class DiscoverTaskAdapter(data: ArrayList<DiscoverTaskBean>) : BaseQuickAdapter<DiscoverTaskBean, BaseViewHolder>(R.layout.item_discover_task_two, data), LoadMoreModule {
    @SuppressLint("UseCompatLoadingForDrawables")
    @Suppress("DEPRECATION")
    override fun convert(holder: BaseViewHolder, item: DiscoverTaskBean) {

        holder.setText(R.id.tvTaskTitle, item.title)

        var peoNum = "${item.join_num}人参与"
        if (item.is_limit_num) { //限制人数
            peoNum = "${item.join_num}/${item.limit_num}人参与"
        }
        holder.setText(R.id.tvPeopleNUm, peoNum)
        Glide.with(context)
                .load(item.base_img)
                .error(R.mipmap.common_bg_cover_default)
                .placeholder(R.mipmap.common_bg_cover_default)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(2f.dp2px())))
                .into(holder.getView(R.id.ivImg))


        if(item.time_mode == 1){
            holder.setText(R.id.tvTaskTime, "任务时间：永久开放")
            holder.setText(R.id.tvTaskGetTime, "领取时间：永久开放")
        }else{
            holder.setText(R.id.tvTaskTime, "任务时间：" + item.task_start_date + "-" + item.task_end_date)
            if (item.start_time.isEmpty() && item.end_time.isEmpty()) {
                holder.setText(R.id.tvTaskGetTime, "永久开放")
            } else {
                holder.setText(R.id.tvTaskGetTime, "领取时间：" + item.start_time + "-" + item.end_time)
            }
        }

        if(!TextUtils.isEmpty(item.score?.success_score)){
            holder.setGone(R.id.jiangli, false)
            holder.setGone(R.id.tvTaskRewardScore, false)
            holder.setText(R.id.tvTaskRewardScore, item.score?.success_score)
        }else{
            holder.setGone(R.id.jiangli, true)
            holder.setGone(R.id.tvTaskRewardScore, true)
        }

        holder.setBackgroundResource(R.id.tvTaskStatus, R.drawable.shape_button_look_details_white)
        val buttonText = holder.getViewOrNull<TextView>(R.id.tvTaskStatus)
        buttonText?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15F)

        holder.getViewOrNull<TextView>(R.id.tvTaskStatus)?.setCompoundDrawables(null, null, null, null)


        // 1进行中
//            2已完成
//            3失败
//            4未领取已过期
//            5 未开始领取
//            6 任务已报名未开始 （新增）
        when (item.status) {
            TaskConstants.TASK_STATUS_DOING -> {//进行中
                holder.setText(R.id.tvTaskStatus, "进行中")
                holder.setTextColor(R.id.tvTaskStatus, context.resources.getColor(R.color.color_10955B))

            }
            TaskConstants.TASK_STATUS_SUCCESS -> {// 任务成功
                holder.setText(R.id.tvTaskStatus, "已完成")// 加了一个图片
                holder.setTextColor(R.id.tvTaskStatus, context.resources.getColor(R.color.color_white))
                val drawable: Drawable = context.resources.getDrawable(R.mipmap.common_icon_task_finish)
                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                holder.getViewOrNull<TextView>(R.id.tvTaskStatus)?.setCompoundDrawables(drawable, null, null, null)


            }
            TaskConstants.TASK_STATUS_FAIL -> {// 任务失败
                holder.setText(R.id.tvTaskStatus, "任务失败")
                holder.setTextColor(R.id.tvTaskStatus, context.resources.getColor(R.color.color_F35600))

            }
            TaskConstants.TASK_STATUS_EXPIRED -> {//已过期
                holder.setText(R.id.tvTaskStatus, "已过期")
                holder.setTextColor(R.id.tvTaskStatus, context.resources.getColor(R.color.color_white))
            }
            TaskConstants.TASK_STATUS_CANNOT_GET -> {//未开始领取
                holder.setText(R.id.tvTaskStatus, "领取未开始")
                holder.setTextColor(R.id.tvTaskStatus, context.resources.getColor(R.color.color_white))
            }
            TaskConstants.TASK_STATUS_UNSTART -> {//任务已报名未开始
                holder.setText(R.id.tvTaskStatus, "任务未开始")
                holder.setTextColor(R.id.tvTaskStatus, context.resources.getColor(R.color.color_white))
            }
            else -> {
                holder.setText(R.id.tvTaskStatus, "查看详情")
                holder.setBackgroundResource(R.id.tvTaskStatus, R.drawable.shape_radius15_color_primary)
                holder.setTextColor(R.id.tvTaskStatus, context.resources.getColor(R.color.color_white))
                buttonText?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13F)

            }
        }


    }
}
