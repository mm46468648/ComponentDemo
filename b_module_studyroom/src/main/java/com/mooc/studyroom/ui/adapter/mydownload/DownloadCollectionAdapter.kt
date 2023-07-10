package com.mooc.studyroom.ui.adapter.mydownload

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.utils.format.StringUtils
import com.mooc.download.util.DownloadConstants
import com.mooc.studyroom.R
import com.mooc.studyroom.model.DownloadCollectionInfo

/**
 * 下载合集适配器，课程，音频课，电子书通用
 */
class DownloadCollectionAdapter(list : ArrayList<DownloadCollectionInfo>)
    : BaseQuickAdapter<DownloadCollectionInfo,BaseViewHolder>(R.layout.studyroom_item_downloadcollection_course,list) {

    var editMode = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun convert(holder: BaseViewHolder, item: DownloadCollectionInfo) {
        holder.setText(R.id.tvCollectionName,item.name)

        val folderSize = if(item.type == DownloadConstants.TYPE_EBOOK)
            item.ebookSize else StringUtils.formatFileSize(item.size)
        holder.setText(R.id.tvCollectionSize,folderSize)
        holder.setText(R.id.tvCollectionNum,"已下载${item.num}个视频")
        holder.setGone(R.id.tvCollectionNum,item.type == DownloadConstants.TYPE_EBOOK) //如果是电子书，不显示下载个数
        Glide.with(context).load(item.cover).into(holder.getView(R.id.ivCollectionCover))

        holder.setGone(R.id.btnDelete,!editMode)
    }
}