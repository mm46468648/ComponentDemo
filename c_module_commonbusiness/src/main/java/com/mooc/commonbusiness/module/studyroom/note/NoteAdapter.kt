package com.mooc.commonbusiness.module.studyroom.note

import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.interfaces.BaseEditableAdapter
import com.mooc.commonbusiness.model.home.NoteBean

class NoteAdapter(list: ArrayList<NoteBean>,needEdit:Boolean = true)
    : BaseEditableAdapter<NoteBean>(R.layout.home_item_studyroom_note, list, needEdit),LoadMoreModule{
    override fun convert(holder: BaseViewHolder, item: NoteBean) {
        holder.setText(R.id.tvNoteTitle, item.other_resource_title)
        holder.setText(R.id.tvNoteContent, item.content)
        if (item.recommend_title.isNullOrEmpty()) {
            holder.setGone(R.id.tvNoteBelong,true)
        } else {
            holder.setGone(R.id.tvNoteBelong,false)
            holder.setText(R.id.tvNoteBelong,"|  " + item.recommend_title)
        }
        holder.setText(R.id.tvNoteTime, item.create_time)
    }
}