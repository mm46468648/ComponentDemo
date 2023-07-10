package com.mooc.discover.adapter

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.setDrawLeft
import com.mooc.common.ktextends.setDrawRight
import com.mooc.discover.R
import com.mooc.discover.model.TaskDetailsBean

/**
 * @param showArrow 是否显示向右箭头
 * 互斥任务选择任务组的时候不需要显示箭头
 */
class MutualTaskChooseChildAdapter(list : ArrayList<TaskDetailsBean>,var showArrow:Boolean = true)
    : BaseQuickAdapter<TaskDetailsBean,BaseViewHolder>(R.layout.item_mutual_task_choose_child,list){
    override fun convert(holder: BaseViewHolder, taskBean: TaskDetailsBean) {

        holder.setText(R.id.tvTaskTitle,taskBean.title)
        val tvTaskTitle = holder.getView<TextView>(R.id.tvTaskTitle)

//        val drawableLeft = ContextCompat.getDrawable(this.context, R.drawable.shape_oval_black_radius_5)
        val drawableRight = ContextCompat.getDrawable(this.context, R.mipmap.ic_right_arrow_gray)
        if(showArrow){
            tvTaskTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null)
        }else{
            tvTaskTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
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

        //隐藏最后一条的分割线
        holder.setGone(R.id.viewBottomLine,holder.layoutPosition == data.size-1)
    }
}