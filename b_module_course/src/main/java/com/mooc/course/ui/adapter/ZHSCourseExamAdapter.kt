package com.mooc.course.ui.adapter

import android.widget.Button
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.course.R
import com.mooc.course.model.ZHSExamData

class ZHSCourseExamAdapter(list : ArrayList<ZHSExamData>)
    : BaseQuickAdapter<ZHSExamData,BaseViewHolder>(R.layout.course_item_zhs_exam,list),LoadMoreModule {
    override fun convert(helper: BaseViewHolder, item: ZHSExamData) {

        //设置章节标题和子标题
        helper.setText(R.id.chapter_title, item.examName)
        helper.setText(R.id.sub_title, item.examName)

        //设置得分数据
        var scoreStr = if("0".equals(item.isSub)) "总分: ${item.totalScore}" else "最终得分${item.score} （总分${item.totalScore}）"
        helper.setText(R.id.tvScore,scoreStr)


        val btEnter = helper.getView<Button>(R.id.btEnter)
        //设置是进入考试，还是查看成绩
        if("0".equals(item.isSub)){
            btEnter.setBackgroundResource(R.drawable.shape_radius20_color_primary)
            btEnter.setText("进入")
            btEnter.setTextColor(ContextCompat.getColor(context,R.color.color_white))
        }else{
            btEnter.setBackgroundResource(R.drawable.shape_radius20_stoke1primary_solidwhite)
            btEnter.setText("查看")
            btEnter.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
        }

    }
}