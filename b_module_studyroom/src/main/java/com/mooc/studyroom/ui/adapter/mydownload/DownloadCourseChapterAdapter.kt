package com.mooc.studyroom.ui.adapter.mydownload

import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.course.BaseChapter
import com.mooc.studyroom.R

class DownloadCourseChapterAdapter(list:ArrayList<BaseChapter>?)
    : BaseQuickAdapter<BaseChapter,BaseViewHolder>(R.layout.download_item_course_chapter,list){

    var editMode = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectChapters = arrayListOf<BaseChapter>()

    override fun convert(holder: BaseViewHolder, item: BaseChapter) {
        holder.setText(R.id.tvChapterName,item.name)
        holder.setGone(R.id.itemDownloadChxSelect,!editMode)

        val checkBox = holder.getView<CheckBox>(R.id.itemDownloadChxSelect)
        checkBox.isChecked = item in selectChapters
    }

}