package com.mooc.discover.adapter

import android.graphics.Typeface
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.getColorRes
import com.mooc.common.ktextends.getDrawableRes
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.discover.R
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.discover.model.TaskDetailsBean
import com.mooc.discover.view.TaskStartDayCutdownView

/**
 * 互斥任务子任务适配器
 * @param success 总任务是否成功
 */
class TaskMutualChildAdapter(list: ArrayList<TaskDetailsBean>?, val success: Boolean) :
    BaseQuickAdapter<TaskDetailsBean, BaseViewHolder>(
        R.layout.item_task_mutual_child_detail,
        list
    ) {
    override fun convert(holder: BaseViewHolder, item: TaskDetailsBean) {

        //如果成功,并且是第一个,展示任务成功提示
        val rlContainer = holder.getView<View>(R.id.rlContainer)
        val ivSuccessPrise = holder.getView<View>(R.id.ivSuccessPrise)
        val tvGroupTitle = holder.getView<TextView>(R.id.tvGroupTitle)
        if (holder.layoutPosition == 0 && success) {
            ivSuccessPrise.visibility = View.VISIBLE //第一个显示成功
        } else if (holder.layoutPosition == 0 && !success) {
            ivSuccessPrise.visibility = View.INVISIBLE        //第一个不是成功,但是要占据位置
        } else { //其他隐藏
            ivSuccessPrise.visibility = View.GONE
        }

        //如果下面的分组和上面一样,则隐藏分组名字
        if (holder.layoutPosition > 0) {
            val lastGroupTitle = holder.layoutPosition - 1
            val b = data[lastGroupTitle].groupName == item.groupName
            tvGroupTitle.visibility = if (b) View.GONE else View.VISIBLE
        } else {
            tvGroupTitle.visibility = View.VISIBLE
        }

        tvGroupTitle.text = item.groupName

        //分组名称,和成功提示同时隐藏的时候,rlContaier才隐藏
        if (tvGroupTitle.visibility == View.GONE && ivSuccessPrise.visibility == View.GONE) {
            rlContainer.visibility = View.GONE
        } else {
            rlContainer.visibility = View.VISIBLE
        }
        holder.setText(R.id.tvTaskTitle, "${item.title}")

        //任务时间
        var taskTime = "${item.task_start_date} - ${item.task_end_date}"
        if (TextUtils.isEmpty(item.task_start_date) && TextUtils.isEmpty(item.task_end_date)) {
            taskTime = "永久开放"
        }

        if (item.time_mode == 1) {
            holder.setText(R.id.tvTaskTime, "任务时间: 永久开放")
        } else {
            holder.setText(R.id.tvTaskTime, "任务时间: ${taskTime}")
        }
        val progressBar = holder.getView<ProgressBar>(R.id.progressBar)
        val tvTaskStatus = holder.getView<TextView>(R.id.tvTaskStatus)

        if (item.status == TaskConstants.TASK_STATUS_SUCCESS) {
            tvTaskStatus.setTextColor(context.getColorRes(R.color.colorPrimary))
            tvTaskStatus.setText("任务成功")
            tvTaskStatus.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))

            progressBar.progressDrawable =
                context.getDrawableRes(R.drawable.bg_task_detail_progressbar)

        } else if (item.status == TaskConstants.TASK_STATUS_FAIL) {
            tvTaskStatus.setTextColor(context.getColorRes(R.color.color_2))
            tvTaskStatus.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
            tvTaskStatus.setText("任务失败")
            progressBar.progressDrawable =
                context.getDrawableRes(R.drawable.bg_task_detail_progressbar_fail)

        } else if (item.status == TaskConstants.TASK_STATUS_CHILD_NOT_COMPLETE) {
            tvTaskStatus.setTextColor(context.getColorRes(R.color.color_2))
            tvTaskStatus.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
            tvTaskStatus.setText("任务未完成")
            progressBar.progressDrawable =
                context.getDrawableRes(R.drawable.bg_task_detail_progressbar_fail)

        } else if (item.status == TaskConstants.TASK_STATUS_DOING) {
            tvTaskStatus.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
            tvTaskStatus.setTextColor(context.getColorRes(R.color.color_2))

            //距离任务截止
            val finishDay = item.finish_data?.remain_days
            val tvRemainDays = "距任务截止还有 ${finishDay} 天"
            val spannableString = spannableString {
                str = tvRemainDays
                colorSpan {
                    color = context.getColorRes(R.color.colorPrimary)
                    start = tvRemainDays.indexOf("${finishDay}")
                    end = tvRemainDays.indexOf("${finishDay}") + "$finishDay".length
                }
            }
            tvTaskStatus.setText(spannableString)

            progressBar.progressDrawable =
                context.getDrawableRes(R.drawable.bg_task_detail_progressbar)


        } else {
            tvTaskStatus.setTextColor(context.getColorRes(R.color.color_2))
            tvTaskStatus.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
            tvTaskStatus.setText("")
        }


        //进度条
        var max = item.finish_data?.total_day_count ?: 0
        var progress = item.finish_data?.finish_day_count ?: 0
        //普通任务,或组合任务显示完成个数
        if (item.calculate_type == TaskConstants.TASK_CACULATE_TYPE_NOMAL || item.calculate_type == TaskConstants.TASK_CACULATE_TYPE_COMBINE) {
            max = item.finish_data?.total_count ?: 0
            progress = item.finish_data?.completed_count ?: 0
        }

        progressBar.max = max
        progressBar.progress = progress

        val unit = getProgressStr(item)
        val remainStr = "${progress}/${max}${unit}"

        val remainStrSpannableString = spannableString {
            str = remainStr
            colorSpan {
                color = context.getColorRes(R.color.colorPrimary)
                start = remainStr.indexOf("${progress}")
                end = remainStr.indexOf("${progress}") + "$progress".length
            }
        }
        holder.setText(R.id.tvProcess, remainStrSpannableString)

        //距离任务开始
        holder.setGone(R.id.llProgressContainer, item.status == TaskConstants.TASK_STATUS_UNSTART)

        //任务状态提示
        if (item.status == TaskConstants.TASK_STATUS_DOING) {
            //正在进行中的任务,要看是不是永久任务,永久任务没有截止时间
            holder.setGone(R.id.tvTaskStatus, item.time_mode == 1)
        } else {
            holder.setGone(R.id.tvTaskStatus, item.status == TaskConstants.TASK_STATUS_UNSTART)
        }
        val taskCutdownView = holder.getView<TaskStartDayCutdownView>(R.id.taskCutdownView)
        taskCutdownView.bindCutDownTime(item.finish_data?.timedelta_remain ?: "")
        if (item.status != TaskConstants.TASK_STATUS_UNSTART) {
            taskCutdownView.visibility = View.GONE
        } else {
            taskCutdownView.visibility = View.VISIBLE
        }

    }

    fun getProgressStr(item: TaskDetailsBean): String {


        return when {
            item.calculate_type == TaskConstants.TASK_CACULATE_TYPE_CONTINUE || item.calculate_type == TaskConstants.TASK_CACULATE_TYPE_SUM -> {
                "天"
            }
            item.type == TaskConstants.TASK_TYPE_CHECKIN || item.type == TaskConstants.TASK_TYPE_CHECKIN_MORNING -> {
                "天"
            }
            item.type == TaskConstants.TASK_TYPE_STUDY_RESOURCE && (item.task_resource_type == ResourceTypeConstans.TYPE_E_BOOK) -> {
                "本"
            }
            item.type == TaskConstants.TASK_TYPE_STUDY_RESOURCE && (item.task_resource_type == ResourceTypeConstans.TYPE_ARTICLE) && item.is_bind_folder -> {
                "篇"
            }
            item.type == TaskConstants.TASK_TYPE_STUDY_PROJECT -> "个"

            else -> ""
        }

    }
}