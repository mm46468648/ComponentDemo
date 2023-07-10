package com.mooc.studyroom.ui.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.setDrawLeft
import com.mooc.commonbusiness.model.studyroom.FolderItem
import com.mooc.studyroom.R

class MoveToStudyZonePopAdapter(list: ArrayList<FolderItem>?)
    : BaseQuickAdapter<FolderItem, BaseViewHolder>(R.layout.studyroom_item_studylist_move) {

    override fun convert(holder: BaseViewHolder, item: FolderItem) {
        //根据子文件夹数显示或隐藏，进入下一级
        holder.setGone(R.id.tvNextFolder,item.count<=0)
        holder.setText(R.id.tvName,item.name)

        //设置左边icon
        val drawLeftRes = if("0" == item.id)R.mipmap.studyroom_ic_rootfold_left_pop else R.mipmap.studyroom_ic_childfolder_left_pop
        holder.getView<TextView>(R.id.tvName).setDrawLeft(drawLeftRes)
    }
}