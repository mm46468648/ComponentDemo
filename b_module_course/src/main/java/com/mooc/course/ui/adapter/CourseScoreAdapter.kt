package com.mooc.course.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.widget.StarBar
import com.mooc.course.R
import com.mooc.course.model.CourseScoreBean

/**
课程评分adapter
 * @Author limeng
 * @Date 2022/4/29-3:49 下午
 */
class CourseScoreAdapter(data: ArrayList<CourseScoreBean>) : BaseQuickAdapter<CourseScoreBean, BaseViewHolder>(R.layout.course_item_score, data) {
    var onChangeCallBack: ((position: Int, scoreFloat: Float, score: String?) -> Unit)? = null
    override fun convert(holder: BaseViewHolder, item: CourseScoreBean) {
        holder.setText(R.id.starType, item.name)
        val starBar = holder.getViewOrNull<StarBar>(R.id.starBar)
        starBar?.setAbleChange(true)
        starBar?.setIntegerMark(true)
        starBar?.setOnStarChangeListener {
            onChangeCallBack?.invoke(holder.layoutPosition, it, (it * 2).toString())
        }

        holder.setText(R.id.tvScore, item.score + "分")
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNullOrEmpty().not()) {
            val newScore = payloads[0].toString()
            holder.setText(R.id.tvScore, newScore + "分")
        } else {
            // payloads 为空，整个ViewHolder
            super.onBindViewHolder(holder, position, payloads)
        }

    }

}