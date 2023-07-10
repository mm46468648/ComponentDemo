package com.mooc.discover.adapter

import android.graphics.Color
import android.text.TextUtils
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.getColorRes
import com.mooc.common.ktextends.setDrawLeft
import com.mooc.discover.R
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.discover.model.TaskFinishResource

/**
 * 任务完成资源适配器
 */
class TaskCompleteResourceAdapter(list: ArrayList<TaskFinishResource>) :
    BaseQuickAdapter<TaskFinishResource, BaseViewHolder>(
        R.layout.discover_item_task_finish_resource,
        list
    ) {

    var type = -1           //代表任务类型

    fun setTaskType(taskType: Int) {
        type = taskType
    }


    override fun convert(holder: BaseViewHolder, item: TaskFinishResource) {
        holder.setText(R.id.tvTitle, item.title)
        holder.setText(R.id.tvSubTitle, item.sub_title)
        holder.setGone(R.id.tvSubTitle, TextUtils.isEmpty(item.sub_title))


        val tvStatus = holder.getView<TextView>(R.id.tvStatus)
        when (item.status) {
            1 -> {
                tvStatus.text = "已完成"
                tvStatus.setTextColor(context.getColorRes(R.color.colorPrimary))
                tvStatus.setDrawLeft(R.mipmap.icon_task_resource_finish)
            }
            2 -> {
                tvStatus.text = "未完成"
                tvStatus.setTextColor(Color.parseColor("#4f4f4f"))
                tvStatus.setDrawLeft(R.mipmap.icon_task_resource_fail)
            }
            3 -> {
                tvStatus.text = "未开始"
                tvStatus.setTextColor(Color.parseColor("#4f4f4f"))
                tvStatus.setDrawLeft(null)
            }
            else -> {
                tvStatus.text = "待完成"
                tvStatus.setTextColor(Color.parseColor("#4f4f4f"))
                tvStatus.setDrawLeft(R.drawable.shape_task_resource_uncomplete)
            }
        }

        //如果指定了资源,显示去完成
        //未指定资源显示待完成
        val toDoVisible = item.status == 0 && item.resource_id != 0
        holder.setGone(R.id.tvToDo, !toDoVisible)
        holder.setGone(R.id.tvStatus, toDoVisible)
        //如果是早起未完成
        if (item.status == 0 && type == TaskConstants.TASK_TYPE_CHECKIN_MORNING) {
            holder.setGone(R.id.tvToDo, false)
            holder.setGone(R.id.tvStatus, true)
        }


    }

}