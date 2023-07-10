package com.mooc.course.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.course.R
import com.mooc.course.model.LessonInfo

/**
 * 智慧树课程章节适配器
 */
class ZHSCourseWareAdapter(list: ArrayList<LessonInfo>) :
    BaseQuickAdapter<LessonInfo, BaseViewHolder>(R.layout.course_item_zhs_chapter, list),
    LoadMoreModule {

    //当前选中位置
    var currentPlay = -1
        set(value) {
            //避免索引越界
            if (value in data.indices) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun convert(holder: BaseViewHolder, item: LessonInfo) {
        when (item.level) {
            0 -> {
                holder.setGone(R.id.tvFirstName, false)
                holder.setGone(R.id.ViewDividerFirst, false)
                holder.setGone(R.id.tvSecondName, true)
                holder.setGone(R.id.ViewDividerSecond, true)
                holder.setGone(R.id.clThird, true)
                holder.setGone(R.id.ViewDividerBottom, true)

                holder.setText(R.id.tvFirstName, item.name)

            }
            1 -> {
                holder.setGone(R.id.tvFirstName, true)
                holder.setGone(R.id.ViewDividerFirst, true)
                holder.setGone(R.id.tvSecondName, false)
                holder.setGone(R.id.ViewDividerSecond, false)
                holder.setGone(R.id.clThird, true)
                holder.setGone(R.id.ViewDividerBottom, true)

                holder.setText(R.id.tvSecondName, item.name)
            }
            2 -> {

                holder.setGone(R.id.tvFirstName, true)
                holder.setGone(R.id.ViewDividerFirst, true)
                holder.setGone(R.id.tvSecondName, true)
                holder.setGone(R.id.ViewDividerSecond, true)
                holder.setGone(R.id.clThird, false)
                holder.setGone(R.id.ViewDividerBottom, false)

                holder.setText(R.id.tvCourseName, item.name)

                //格式化时间
                val formatAudioPlayTime =
                    TimeFormatUtil.formatAudioPlayTime(item.videoLength * 1000)
                holder.setText(R.id.tvLength, formatAudioPlayTime)

                val leftIcon =
                    if (holder.layoutPosition == currentPlay) R.mipmap.course_ic_zhs_pause_gray else R.mipmap.course_ic_zhs_play_gray
                holder.setImageResource(R.id.ivPlay, leftIcon)

                //除了最后一条，如果当前和下一个level相同，隐藏底部分割线
//                val adapterPosition = holder.adapterPosition
//                if (adapterPosition < data.size - 1) {
//                    val nextItem = data.get(adapterPosition + 1)
//                    holder.setGone(R.id.ViewDividerBottom, item.level == nextItem.level)
//                }else{
//                    holder.setGone(R.id.ViewDividerBottom,true)
//                }

                //第二级下面如果是0就显示,否则隐藏
                val adapterPosition = holder.adapterPosition
                if (adapterPosition < data.size - 1) {
                    val nextItem = data.get(adapterPosition + 1)
                    holder.setGone(R.id.ViewDividerBottom, 0 != nextItem.level)
                }else{
                    holder.setGone(R.id.ViewDividerBottom,true)
                }
            }
        }
//        //格式化时间
//        val formatAudioPlayTime = TimeFormatUtil.formatAudioPlayTime(item.videoLength * 1000)
//        holder.setText(R.id.tvLength, formatAudioPlayTime)
//
//        val leftIcon = if (holder.layoutPosition == currentPlay) R.mipmap.course_ic_zhs_pause_gray else R.mipmap.course_ic_zhs_play_gray
//        holder.setImageResource(R.id.ivPlay, leftIcon)
//
//
//        holder.setText(R.id.tvFirstName, item.chapterName)
//        holder.setText(R.id.tvSecondName, item.lessonName)
//
//        val adapterPosition = holder.adapterPosition
//        //如果当前和上一个parentFatherName相同，则隐藏
//        val currentItem = data.get(adapterPosition)
//
//        //空直接隐藏
//        holder.setGone(R.id.tvFirstName, currentItem.chapterName?.isEmpty() == true)
//        holder.setGone(R.id.tvSecondName, currentItem.lessonName?.isEmpty() ==true)
//        holder.setGone(R.id.ViewDividerFirst, currentItem.chapterName?.isEmpty() == true)
//        holder.setGone(R.id.ViewDividerSecond, currentItem.lessonName?.isEmpty() == true)
//
//        if (currentItem.chapterName?.isNotEmpty() ==true && adapterPosition > 0) {
//            val lastItem = data.get(adapterPosition - 1)
//            holder.setGone(R.id.tvFirstName, currentItem.chapterName == lastItem.chapterName)
//            holder.setGone(R.id.ViewDividerFirst, currentItem.chapterName == lastItem.chapterName)
//        }
//        //如果当前和上一个parentName相同，则隐藏
//        if(adapterPosition>0){
//            val lastItem = data.get(adapterPosition - 1)
//            holder.setGone(R.id.tvSecondName, currentItem.lessonName == lastItem.lessonName)
//            holder.setGone(R.id.ViewDividerSecond, currentItem.lessonName == lastItem.lessonName)
//        }
//        //除了最后一条，如果当前和下一个parentName相同，隐藏底部分割线
//        if (adapterPosition < data.size - 1) {
//            val nextItem = data.get(adapterPosition + 1)
//            holder.setGone(R.id.ViewDividerBottom, currentItem.lessonName == nextItem.lessonName)
//        }
    }
}