package com.mooc.studyroom.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.studyroom.FolderItem
import com.mooc.studyroom.R

/**
 * 学习清单移动适配器
 */
class StudyListMoveAdatper(list: ArrayList<FolderItem>?)
    : BaseQuickAdapter<FolderItem,BaseViewHolder>(R.layout.studyroom_item_studylist_move) {

    override fun convert(holder: BaseViewHolder, item: FolderItem) {
        //根据子文件夹数显示或隐藏，进入下一级
        holder.setGone(R.id.tvNextFolder,item.count<=0)
        holder.setText(R.id.tvName,item.name)
    }
}