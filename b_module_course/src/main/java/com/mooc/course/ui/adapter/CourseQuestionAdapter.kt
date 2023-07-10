package com.mooc.course.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.course.R
import com.mooc.course.model.Question

class CourseQuestionAdapter(list:ArrayList<Question>) : BaseQuickAdapter<Question,BaseViewHolder>
        (R.layout.course_item_question,list) {
    override fun convert(holder: BaseViewHolder, item: Question) {
        holder.setText(R.id.tvQuestion,item.question)
        holder.setText(R.id.tvAnswer,item.answer)
    }
}