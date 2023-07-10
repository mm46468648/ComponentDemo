package com.mooc.studyroom.viewmodel

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.telephony.mbms.DownloadStatusListener
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.reflect.TypeToken
import com.mooc.common.global.AppGlobals
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.commonbusiness.model.audio.AlbumListResponse
import com.mooc.commonbusiness.model.db.AlbumDB
import com.mooc.commonbusiness.model.db.TrackDB
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.commonbusiness.route.routeservice.AudioDownloadService
import com.mooc.download.db.DownloadDatabase
import com.mooc.newdowload.DownloadInfo
import com.mooc.newdowload.DownloadManager
import com.mooc.newdowload.State
import com.mooc.studyroom.db.OldDbSearchManager
import com.mooc.studyroom.db.OldDownloadTrackDatabaseHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class AlbumDownloadViewModel : BaseListViewModel2<AlbumDB>() {
    override suspend fun getData(): Flow<List<AlbumDB>?> {
       return flow {
           queryAllDwonloadTrackData()
           val navigation = ARouter.getInstance().navigation(AudioDownloadService::class.java)
           val albums = navigation.findDownloadAlbum()
           //获取每个专辑下的文件数量，和文件大小
            albums?.forEach { album->
                val audioDownloadService = ARouter.getInstance().navigation(AudioDownloadService::class.java)
                val findDownloadAblumDetail = audioDownloadService.findDownloadAblumDetail(album.id.toString())

                var fileNum = 0
                var fileSize = 0L
                for (track in findDownloadAblumDetail?: arrayListOf()){
                    val convert =
                        GsonManager.getInstance().convert(track.data, TrackBean::class.java)
                    val downloadModle = DownloadDatabase.database?.getDownloadDao()
                        ?.getDownloadModle(convert.generateDownloadDBId())

                    if(downloadModle?.state == State.DOWNLOAD_COMPLETED){
                        val size = downloadModle.size
                        fileNum++
                        fileSize+=size
                    }
                }

                album.fileSize = fileSize
                album.fileNum = fileNum
            }

           emit(albums)
       }
    }

    val albumDBHashMap = HashMap<String, AlbumDB>()

    val navigation by lazy {
        ARouter.getInstance().navigation(AudioDownloadService::class.java)
    }

    fun queryAllDwonloadTrackData() {
        val trackHelper = OldDownloadTrackDatabaseHelper(AppGlobals.getApplication())
        var cursor: Cursor? = null
        try {
            val db: SQLiteDatabase = trackHelper.getReadableDatabase()
            // 查询所有数据
            cursor = db.query(
                OldDownloadTrackDatabaseHelper.TABLE_TRACK_DOWNLOAD_NAME,
                null,
                null,
                null,
                null,
                null,
                null
            )
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    parseTrackDownloadBean(cursor)
                }

                //将专辑一个一个插入到专辑列表中
                albumDBHashMap.values.forEach {
                    //同时向音频课表中更新此音频课有缓存内容
                    navigation.insertAlbum(it)
                }

                //同步完毕，删除原来的数据库
                val sql = "DROP TABLE IF EXISTS \"" + OldDownloadTrackDatabaseHelper.TABLE_TRACK_DOWNLOAD_NAME +"\""
                db.execSQL(sql)


            }
        } catch (e: Exception) {
            Log.e(OldDbSearchManager.TAG, e.toString())
        } finally {
            cursor?.close()
        }



    }

    private fun parseTrackDownloadBean(cursor: Cursor): AlbumDB?{
        val albumid = cursor.getInt(cursor.getColumnIndex("albumid"))
        val albumtitle = cursor.getString(cursor.getColumnIndex("albumtitle"))
        val coverurllarge = cursor.getString(cursor.getColumnIndex("album_coverurlmiddle"))
        val downloadstatus = cursor.getInt(cursor.getColumnIndex("downloadstatus"))
        val downloadedsize = cursor.getLong(cursor.getColumnIndex("downloadedsize"))

        if (2 != downloadstatus) return null     //音频2，代表完成

        //构建专辑数据
        val albumDB = AlbumDB()
        val albumListResponse = AlbumListResponse()
        albumDB.id = albumid.toLong()
        albumListResponse.cover_url_large = coverurllarge
        albumListResponse.id = albumid.toString()
        albumListResponse.album_title = albumtitle

        albumDB.haveDownload = true

        if (!albumDBHashMap.containsKey("$albumid")) {
            albumListResponse.oldDownloadTrackCount = 1
            albumListResponse.oldDownloadTrackSize = downloadedsize
            albumDB.data = GsonManager.getInstance().toJson(albumListResponse)
            albumDBHashMap["$albumid"] = albumDB
        } else {
            val albumDB1 = albumDBHashMap["$albumid"]
            if (albumDB1 != null) {
                val albumListResponse1 = GsonManager.getInstance().fromJson<AlbumListResponse>(albumDB1.data,
                    object : TypeToken<AlbumListResponse>(){}.type)
                val count1 = albumListResponse1.oldDownloadTrackCount + 1
                val downloadedsize1 = albumListResponse1.oldDownloadTrackSize + downloadedsize
                albumListResponse1.oldDownloadTrackCount = count1
                albumListResponse1.oldDownloadTrackSize = downloadedsize1
                albumDB1.data = GsonManager.getInstance().toJson(albumListResponse1)
            }
        }

        //构建音频数据
        val trackDB = TrackDB()
        val dataid = cursor.getInt(cursor.getColumnIndex("dataid"))
        val tracktitle = cursor.getString(cursor.getColumnIndex("tracktitle"))
        val duration = cursor.getLong(cursor.getColumnIndex("duration"))
        trackDB.id = dataid.toLong()
        trackDB.albumId = albumid.toString()

        val trackBean = TrackBean(track_title = tracktitle, duration = duration)
        trackBean.id = dataid.toString()
        trackBean.albumId = albumid.toString()
        trackBean.asyncFromXimaData = true
        trackDB.data = GsonManager.getInstance().toJson(trackBean)


        //缓存音频表中插入一条记录
        navigation.insertTrack(trackDB)



        //构建下载模型
        val downloadedsavefilepath = cursor.getString(cursor.getColumnIndex("downloadedsavefilepath"))
        val downloadInfo = DownloadInfo()
        downloadInfo.id = trackBean.generateDownloadDBId()
        downloadInfo.state = 5
        downloadInfo.fileName = tracktitle
        downloadInfo.downloadPath = downloadedsavefilepath
        DownloadDatabase.database?.getDownloadDao()?.insert(downloadInfo)


        Log.e(OldDbSearchManager.TAG, "albumid: " + albumid + "albumtitle: " + albumtitle)
        return albumDB
    }

}