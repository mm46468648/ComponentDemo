package com.mooc.course.ui.adapter

import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.course.R
import com.mooc.course.model.ChaptersBean

//import com.mooc.commonbusiness.model.search.ChaptersBean

/**
 * 课程大纲适配器
 */
class CourseOutLineAdapter(var list: ArrayList<ChaptersBean>)
    : BaseQuickAdapter<ChaptersBean, BaseViewHolder>(R.layout.course_item_chapters_multi_level, list) {

    override fun convert(@NonNull helper: BaseViewHolder, item: ChaptersBean) {

        val level: Int = item.level

        //标题，老学堂使用title字段，新学堂使用name字段
        val title: String = if (item.title?.isNotEmpty() == true) item.title?:"" else item.name?: ""
//        val title: String = item.name?:""
        val view: ImageView = helper.getView(R.id.viewIcon)
        val tvChapterName: TextView = helper.getView(R.id.tvChapterName)
        when (level) {
            0 -> {
                view.setImageResource(R.mipmap.course_ic_chapter_spot)
                tvChapterName.textSize = 16f
                tvChapterName.setTextColor(ContextCompat.getColor(context, R.color.black))
                tvChapterName.setTypeface(Typeface.DEFAULT_BOLD)
            }
            1 -> {
                tvChapterName.textSize = 13f
                view.setImageResource(R.drawable.course_shape_strok1_979797_solidwhite)
                tvChapterName.setTextColor(ContextCompat.getColor(context, R.color.color_6))
                tvChapterName.setTypeface(Typeface.DEFAULT)
            }
            else -> {
                tvChapterName.textSize = 12f
                view.setImageResource(R.drawable.course_shape_solid_d8d8d8_size5)
                tvChapterName.setTextColor(ContextCompat.getColor(context, R.color.color_6))
                tvChapterName.setTypeface(Typeface.DEFAULT)
            }
        }
        tvChapterName.text = title

        //第一个top ,最后一个bottom竖线的显示隐藏
        helper.setGone(R.id.line_top, helper.adapterPosition == 0)
        helper.setGone(R.id.line_bottom, helper.adapterPosition == data.size - 1)
    }
}