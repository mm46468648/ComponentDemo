package com.mooc.studyroom.ui.adapter

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.utils.DateUtil
import com.mooc.studyroom.R
import com.mooc.studyroom.model.StudyProject

class StudyProjectAdapter(list: ArrayList<StudyProject>) :
    BaseQuickAdapter<StudyProject, BaseViewHolder>(R.layout.studyroom_item_studyproject, list) {
    @SuppressLint("ResourceAsColor")
    override fun convert(holder: BaseViewHolder, item: StudyProject) {
        //设置标题
        holder.setText(R.id.tvTitle, item.plan_name)
        //设置时间
        var str = "${item.plan_starttime}-${item.plan_endtime}"
        val oldValue = "年"
        val newValue = "."
        val output = str.replace(oldValue, newValue)
        val oldValue1 = "月"
        val newValue1 = "."
        val output1 = output.replace(oldValue1, newValue1)
        val oldValue2 = "日"
        val newValue2 = ""
        val output2 = output1.replace(oldValue2, newValue2)

        if (item.time_mode == 1) {//时间永久
            holder.setText(
                R.id.tvTime,
                context.resources.getString(R.string.study_time_permanent_opening)
            )
        } else {
            holder.setText(R.id.tvTime, output2)
        }


        val endTime: Long
        endTime = if (item.plan_endtime_ts != null) {
            item.plan_endtime_ts * 1000
        } else {
            0
        }
        //设置退出的颜色，不能退出的灰色，能推出的蓝色
        if (DateUtil.getCurrentTime() >= endTime || DateUtil.getCurrentTime() >= item.compute_time) {
            holder.setTextColor(R.id.tvEixt, ContextCompat.getColor(context, R.color.color_ADADAD))

        } else {
            holder.setTextColor(R.id.tvEixt, ContextCompat.getColor(context, R.color.color_6C88BE))
        }

        //设置进度
        val tvTip = holder.getView<TextView>(R.id.tvTip)
        when (item.is_success) {
            1 -> {
                tvTip.setText("项目成功")
                tvTip.setTextColor(ContextCompat.getColor(context, R.color.color_10955B))
//                holder.setTextColor(R.id.tvEixt,ContextCompat.getColor(context,R.color.color_10955B))
            }
            2 -> {
                tvTip.setText("项目失败")
                tvTip.setTextColor(ContextCompat.getColor(context, R.color.color_5D5D5D))
//                holder.setTextColor(R.id.tvEixt,ContextCompat.getColor(context,R.color.color_5D5D5D))

            }
            3 -> {
                val tipStr = "完成度: ${item.user_do_num}/${item.source_num}"
                tvTip.setTextColor(ContextCompat.getColor(context, R.color.color_5D5D5D))
//                holder.setTextColor(R.id.tvEixt,ContextCompat.getColor(context,R.color.color_5D5D5D))
                tvTip.setText(tipStr)
            }
        }
    }
}