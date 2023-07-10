package com.mooc.discover.adapter

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.discover.R
import com.mooc.discover.model.TaskDetailsBean

/**
 * 互斥任务必选任务适配器
 */
class MutualTaskMustAdapter(list : ArrayList<TaskDetailsBean>) : BaseQuickAdapter<TaskDetailsBean,BaseViewHolder>(
    R.layout.item_mutual_task_must,list){

    override fun convert(holder: BaseViewHolder, taskBean: TaskDetailsBean) {
        holder.setText(R.id.tvTaskTitle,taskBean.title)

        //任务时间
        var taskTime = "${taskBean.task_start_date} - ${taskBean.task_end_date}"
        if (TextUtils.isEmpty(taskBean.task_start_date) && TextUtils.isEmpty(taskBean.task_end_date)) {
            taskTime = "永久开放"
        }
        holder.setText(R.id.tvTaskTime, "任务时间: ${taskTime}")

        val tvAward = holder.getView<TextView>(R.id.tvAward)
        val tvAwardScore = holder.getView<TextView>(R.id.tvAwardScore)
        val tvFailScore = holder.getView<TextView>(R.id.tvFailScore)
        //奖励积分
        if (taskBean.success_score > 0) {
            tvAward.visibility = View.VISIBLE
            tvAwardScore.visibility = View.VISIBLE
            tvAwardScore.setText(taskBean.success_score.toString())
        } else {
            tvAward.visibility = View.GONE
            tvAwardScore.visibility = View.GONE
        }

        //失败扣除积分
        if (taskBean.fail_score > 0) {
            tvFailScore.visibility = View.VISIBLE
            tvFailScore.setText("(失败-${taskBean.fail_score})")
        } else {
            tvFailScore.visibility = View.GONE
        }
    }
}