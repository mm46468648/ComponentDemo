package com.mooc.studyroom.ui.adapter.mydownload

import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.utils.FileMgr
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.model.audio.AlbumListResponse
import com.mooc.commonbusiness.model.db.AlbumDB
import com.mooc.commonbusiness.route.routeservice.AudioDownloadService
import com.mooc.studyroom.R
import java.io.File

class DownloadAlbumAdapter(list:ArrayList<AlbumDB>?)
    : BaseQuickAdapter<AlbumDB,BaseViewHolder>(R.layout.studyroom_item_downloadcollection_course,list) {

    var editMode = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    val audioDownloadService by lazy {
        ARouter.getInstance().navigation(AudioDownloadService::class.java)
    }
    override fun convert(holder: BaseViewHolder, item: AlbumDB) {
        val albumListResponse = GsonManager.getInstance().convert(item.data, AlbumListResponse::class.java)
        holder.setText(R.id.tvCollectionName,albumListResponse.album_title)

        //根据文件夹，获取下载的数据，和大小
//        val filePath = DownloadConfig.audioLocation + "/" +item.id
//        val file = File(filePath)
//        if(file.exists() && file.isDirectory){
////            var size = file.listFiles().size
//            //下载数量从数据库中已下载数量获取
//
//            var size = audioDownloadService.getDownloadAudioNum(item.id.toString())
//            var fileSize1 = FileMgr.getFileSize(file)
//            size += albumListResponse.oldDownloadTrackCount
//            fileSize1 += albumListResponse.oldDownloadTrackSize
//            val fileSize = FileMgr.FormetFileSize(fileSize1)
//            holder.setText(R.id.tvCollectionNum,"已下载${size}个音频")
//            holder.setText(R.id.tvCollectionSize,fileSize.toString())
//        }else{
//
//
//            holder.setText(R.id.tvCollectionNum,"已下载${albumListResponse.oldDownloadTrackCount}个音频")
//            holder.setText(R.id.tvCollectionSize,FileMgr.FormetFileSize(albumListResponse.oldDownloadTrackSize))
//        }

        val num = item.fileNum +  albumListResponse.oldDownloadTrackCount
        val size = item.fileSize + albumListResponse.oldDownloadTrackSize

        holder.setText(R.id.tvCollectionNum,"已下载${num}个音频")
        holder.setText(R.id.tvCollectionSize,FileMgr.FormetFileSize(size))

        holder.setGone(R.id.tvCollectionNum,false)
        Glide.with(context).load(albumListResponse.cover_url_large).into(holder.getView(R.id.ivCollectionCover))

        holder.setGone(R.id.btnDelete,!editMode)
    }
}