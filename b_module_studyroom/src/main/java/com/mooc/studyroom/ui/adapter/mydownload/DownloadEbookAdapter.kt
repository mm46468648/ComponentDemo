package com.mooc.studyroom.ui.adapter.mydownload

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.studyroom.R

class DownloadEbookAdapter(list:ArrayList<EBookBean>?)
    : BaseQuickAdapter<EBookBean,BaseViewHolder>(R.layout.studyroom_item_downloadcollection_ebook,list) {

    var editMode = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun convert(holder: BaseViewHolder, item: EBookBean) {
        holder.setText(R.id.tvCollectionName,item.title)

//        val folderSize = if(item.type == DownloadConstants.TYPE_EBOOK)
//            item.ebookSize else StringUtils.formatFileSize(item.size)
        holder.setText(R.id.tvCollectionSize,item.filesize)
//        holder.setText(R.id.tvCollectionNum,"已下载${0}个视频")
        holder.setGone(R.id.tvCollectionNum,true) //如果是电子书，不显示下载个数
        Glide.with(context).load(item.picture).into(holder.getView(R.id.ivCollectionCover))

        holder.setGone(R.id.btnDelete,!editMode)


    }
}