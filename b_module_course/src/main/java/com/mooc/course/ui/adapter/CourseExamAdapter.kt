package com.mooc.course.ui.adapter

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.course.R
import com.mooc.course.model.SequentialBean
import com.mooc.common.dsl.spannableString
import com.mooc.resource.ktextention.setDrawRight

class CourseExamAdapter(list : ArrayList<SequentialBean>)
    : BaseQuickAdapter<SequentialBean,BaseViewHolder>(R.layout.course_item_play_exam,list) {
    override fun convert(holder: BaseViewHolder, item: SequentialBean) {
        holder.setText(R.id.tvExamName,item.display_name)

        //如果有得分展示得分，没有显示去答题icon
        val tvScore = holder.getView<TextView>(R.id.tvScore)
        if(item.has_submitted){
            val tvScoreStr = "${item.gained_point} / ${item.total_point}"
            val spannableString = spannableString {
                str = tvScoreStr
                colorSpan {
                    color = Color.parseColor("#1EAA50")
                    start = 0
                    end = item.gained_point.toString().length
                }
                colorSpan {
                    color = Color.parseColor("#666666")
                    start = tvScoreStr.lastIndexOf(item.total_point.toString())
                    end = tvScoreStr.length
                }
            }
            tvScore.setText(spannableString)
            tvScore.setDrawRight(null)
        }else{
            tvScore.text = ""
            tvScore.setDrawRight(R.mipmap.common_ic_right_arrow_gray)
        }

        val tvDueTime = holder.getView<TextView>(R.id.tvDueTime)
        //有习题的章节,并且时间不为空，显示习题时长
        if(item.has_problem && item.due_time?.isNotEmpty() == true){
            tvDueTime.visibility = View.VISIBLE
            val formatDate = TimeFormatUtil.formatDateFromString(item.due_time,TimeFormatUtil.yyyy_MM_dd_HH_mm_ss_Tzone, TimeFormatUtil.yyyy_MM_dd_HH_mm_ss)
            holder.setText(R.id.tvDueTime,"${formatDate}截止")
        }else{
            tvDueTime.visibility = View.GONE
        }

    }
}