package com.mooc.studyroom.ui.adapter.mydownload

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.utils.FileMgr
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.model.db.CourseDB
import com.mooc.studyroom.R
import java.io.File

class DownloadCourseAdapter(list:ArrayList<CourseDB>?)
    : BaseQuickAdapter<CourseDB,BaseViewHolder>(R.layout.studyroom_item_downloadcollection_course,list) {

    var editMode = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun convert(holder: BaseViewHolder, item: CourseDB) {
        holder.setText(R.id.tvCollectionName,item.name)

        //根据文件夹，获取下载的数据，和大小
//        var filePath = DownloadConfig.courseLocation + "/" +item.platform + "/" + item.courseId
//        if(item.classRoomID.isNotEmpty()){
//            filePath += "/${item.classRoomID}"
//        }
//
//        val file = File(filePath)
//        if(file.exists() && file.isDirectory){
//            var size = file.listFiles()?.size?:0
//            var fileSize1 = FileMgr.getFileSize(file)
//            fileSize1 += item.totalSize
//            val formatfileSize = FileMgr.FormetFileSize(fileSize1)
//
//            size += item.totalNum
//            holder.setText(R.id.tvCollectionNum,"已下载${size}个视频")
//            holder.setText(R.id.tvCollectionSize,formatfileSize.toString())
//        }else{
//
//        }

        val fileSize = FileMgr.FormetFileSize(item.totalSize)
        holder.setText(R.id.tvCollectionNum,"已下载${item.totalNum}个视频")
        holder.setText(R.id.tvCollectionSize,fileSize.toString())
//        holder.setGone(R.id.tvCollectionNum,true) //如果是电子书，不显示下载个数
        Glide.with(context).load(item.cover).into(holder.getView(R.id.ivCollectionCover))

        holder.setGone(R.id.btnDelete,!editMode)
    }
}