package com.mooc.discover.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.getColorRes
import com.mooc.common.ktextends.getDrawableRes
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.discover.R
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.discover.model.DiscoverTaskBean


/**
 * 任务列表适配器
 */
class DiscoverTaskHistoryAdapter(data: ArrayList<DiscoverTaskBean>) : BaseQuickAdapter<DiscoverTaskBean, BaseViewHolder>(R.layout.item_task_history, data), LoadMoreModule {
    @Suppress("DEPRECATION")
    override fun convert(holder: BaseViewHolder, item: DiscoverTaskBean) {

        holder.setText(R.id.tvTaskTitle, item.title)
        Glide.with(context)
                .load(item.base_img)
                .error(R.mipmap.common_bg_cover_default)
                .placeholder(R.mipmap.common_bg_cover_default)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(2f.dp2px())))
                .into(holder.getView(R.id.ivImg))

        if(item.time_mode == 1){
            holder.setText(R.id.tvTaskTime, "任务时间：永久开放")
        }else{
            holder.setText(R.id.tvTaskTime, "任务时间：" + item.task_start_date + "-" + item.task_end_date)
        }
        val progressBar = holder.getViewOrNull<ProgressBar>(R.id.progressBar)
        progressBar?.progressDrawable = context.getDrawableRes(R.drawable.bg_shape_task_progressbar)


        holder.setGone(R.id.tvTaskTime, true)
        holder.setGone(R.id.llProgressContainer, false)

        var max = item.finish_data?.total_day_count ?: 0
        var progress = item.finish_data?.finish_day_count ?: 0
        if (item.calculate_type == TaskConstants.TASK_CACULATE_TYPE_NOMAL || item.calculate_type == TaskConstants.TASK_CACULATE_TYPE_COMBINE) {
            max = item.finish_data?.total_count ?: 0
            progress = item.finish_data?.completed_count ?: 0
        }

        //青年读书会定制任务
        if(item.type == TaskConstants.TASK_TYPE_REED_BOOK){
            max = item.finish_data?.total_count ?: 0
            progress = item.finish_data?.completed_count ?: 0
        }

        progressBar?.max = max
        progressBar?.progress = progress

        val temp = "${progress}/${max}"

        val spannableString = spannableString {
            str = "${temp}${getTaskUnit(item)}"
            colorSpan {
                color = context.getColorRes(R.color.color_02D97B)
                start = 0
                end = progress.toString().length
            }
        }
        holder.setText(R.id.tvTaskProDay, spannableString)
        if (TaskConstants.TASK_TYPE_STUDY_RESOURCE == item.type && ResourceTypeConstans.TYPE_ARTICLE == item.task_resource_type
                && item.is_bind_folder) {
            // 是资源类型任务 是文章是否绑定学习清单
            holder.setText(R.id.tvTaskDetails, "通过阅读主题文章完成任务计数")
            //进行中,成功或失败再提示
            if (item.status == TaskConstants.TASK_STATUS_DOING || item.status == TaskConstants.TASK_STATUS_SUCCESS || item.status == TaskConstants.TASK_STATUS_FAIL) {
                holder.setGone(R.id.tvTaskTip, false)
            } else {
                holder.setGone(R.id.tvTaskTip, true)
            }
        } else if (TaskConstants.TASK_TYPE_REED_BOOK == item.type
            && item.is_bind_folder) {
            // 青年读书会定制任务,指定完成89本书
            holder.setText(R.id.tvTaskDetails, "通过完成指定电子书阅读完成任务")
            holder.setGone(R.id.tvTaskTip, true)

//            //进行中,成功或失败再提示
//            if (item.status == TaskConstants.TASK_STATUS_DOING || item.status == TaskConstants.TASK_STATUS_SUCCESS || item.status == TaskConstants.TASK_STATUS_FAIL) {
//                holder.setGone(R.id.tvTaskTip, false)
//            } else {
//                holder.setGone(R.id.tvTaskTip, true)
//            }
        }else {
            holder.setGone(R.id.tvTaskTip, true)
            val tvTaskDetails = holder.getView<TextView>(R.id.tvTaskDetails)
            tvTaskDetails.text = getTaskDes(item)
        }

        holder.setGone(R.id.tvTaskStatus, false)
        holder.getViewOrNull<TextView>(R.id.tvTaskStatus)?.setCompoundDrawables(null, null, null, null)
        when (item.status) {//6 任务已报名未开始 （新增）
            TaskConstants.TASK_STATUS_DOING -> {//进行中
                holder.setText(R.id.tvTaskStatus, "进行中")
                holder.setTextColor(R.id.tvTaskStatus, context.resources.getColor(R.color.color_02D97B))
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
                holder.setTextColor(R.id.tvTaskStatus, Color.parseColor("#fff35600"))
                progressBar?.progressDrawable = context.getDrawableRes(R.drawable.bg_shape_task_fail_progressbar)
            }
            TaskConstants.TASK_STATUS_UNSTART -> {//领取未开始
                holder.setText(R.id.tvTaskStatus, "未开始")
                holder.setTextColor(R.id.tvTaskStatus, context.resources.getColor(R.color.color_white))

                holder.setGone(R.id.tvTaskTime, false)
                holder.setGone(R.id.llProgressContainer, true)
            }
            else -> {
                holder.setGone(R.id.tvTaskStatus, true)//未开始，进行中，已过期 隐藏
            }
        }


    }

    /**
     * 获取任务描述
     */
    private fun getTaskDes(item: DiscoverTaskBean): String {
        return when {
            item.type == TaskConstants.TASK_TYPE_CHECKIN -> {
                "通过每日打卡完成任务计数"
            }
            item.type == TaskConstants.TASK_TYPE_CHECKIN_MORNING -> {//原有早起打卡任务废除，改为现在的文案
                "通过每日指定时间进行任务打卡完成计数"
            }
            item.type == TaskConstants.TASK_TYPE_STUDY_PROJECT -> {
                "通过完成学习项目完成任务计数"
            }
            item.type == TaskConstants.TASK_TYPE_STUDY_RESOURCE && (item.task_resource_type == ResourceTypeConstans.TYPE_E_BOOK) -> {
                "通过阅读电子书完成任务计数"
            }
            item.calculate_type == TaskConstants.TASK_CACULATE_TYPE_COMBINE -> {
                "通过完成子任务完成任务计数"
            }
            else -> {
                ""
            }
        }
    }

    /**
     * 获取任务完成的单位
     */
    fun getTaskUnit(item: DiscoverTaskBean): String {
        return when {
            item.calculate_type == TaskConstants.TASK_CACULATE_TYPE_SUM || item.calculate_type == TaskConstants.TASK_CACULATE_TYPE_CONTINUE -> {
                "天"
            }
            item.type == TaskConstants.TASK_TYPE_STUDY_RESOURCE && (item.task_resource_type == ResourceTypeConstans.TYPE_E_BOOK) -> {
                "本"
            }
            item.type == TaskConstants.TASK_TYPE_REED_BOOK -> {
                "本"
            }
            item.type == TaskConstants.TASK_TYPE_STUDY_RESOURCE && (item.task_resource_type == ResourceTypeConstans.TYPE_ARTICLE) && item.is_bind_folder -> {
                "篇"
            }
            item.type == TaskConstants.TASK_TYPE_STUDY_PROJECT || item.calculate_type == TaskConstants.TASK_CACULATE_TYPE_COMBINE -> "个"

            else -> ""
        }

    }
}
