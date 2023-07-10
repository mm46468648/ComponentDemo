package com.mooc.studyroom.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.studyroom.FolderItem
import com.mooc.studyroom.R

/**
 * 学习清单排序适配器
 */
class StudyListSortAdapter(list: ArrayList<FolderItem>?)
    : BaseQuickAdapter<FolderItem,BaseViewHolder>(R.layout.studyroom_item_studylist_sort,list),DraggableModule{

    var mViewHolder : BaseViewHolder? = null
    override fun convert(holder: BaseViewHolder, item: FolderItem) {

        mViewHolder = holder
        holder.setText(R.id.tvName,item.name)
    }
}