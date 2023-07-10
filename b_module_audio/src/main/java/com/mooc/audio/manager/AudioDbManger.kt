package com.mooc.audio.manager

import com.mooc.audio.db.AudioDatabase
import com.mooc.commonbusiness.model.audio.AlbumListResponse
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.model.db.AlbumDB
import com.mooc.commonbusiness.model.db.TrackDB
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.download.db.DownloadDatabase

class AudioDbManger {

    companion object {

        /**
         * 专辑数据表中
         * 更新上次在听
         */
        fun updateLastListenAudio(albumId: String, listenAudioId: String) {
            val findAlbumById = findAlbum(albumId)
            if (findAlbumById != null) {
                findAlbumById.lastPlayAudioId = listenAudioId
                AudioDatabase.DATABASE?.getAlbumDao()?.update(findAlbumById)
            }
        }

        /**
         * 专辑数据表中
         * 更新有下载音频
         */
        fun updateHaveDownload(albumId: String,have:Boolean){
            val findAlbumById = findAlbum(albumId)
            if (findAlbumById != null) {
                findAlbumById.haveDownload = have
                AudioDatabase.DATABASE?.getAlbumDao()?.update(findAlbumById)
            }
        }

        /**
         * 专辑数据表中
         * 插入音频课纪录
         */
        fun insertAlbumResponse(album: AlbumListResponse) {
            val albumDB = AlbumDB()
            albumDB.id = album.id.toLong()
            albumDB.data =GsonManager.getInstance().toJson(album)
            AudioDatabase.DATABASE?.getAlbumDao()?.insert(albumDB)
        }

        /**
         *根据id查找专辑
         */
        fun findAlbum(albumId:String) : AlbumDB?{
            return AudioDatabase.DATABASE?.getAlbumDao()?.findAlbumById(albumId)
        }

        /***
         * 获取所有的下载专辑
         */
        fun findAllDownloadAlbum() : List<AlbumDB>?{
            return AudioDatabase.DATABASE?.getAlbumDao()?.findDownloadAlbumList()
        }


        /**
         * 缓存音频列表插入一条数据
         */
        fun insertDownloadAudio(trackDB: TrackDB){
            AudioDatabase.DATABASE?.getAudioDao()?.insert(trackDB)
        }

        /**
         * 删除专辑下的音频数据
         */
        fun deleteAllAudioByAlbum(albumDB: AlbumDB){
            val findAllDownloadAudioByAlbumId = findAllDownloadAudioByAlbumId(albumDB.id.toString())
            findAllDownloadAudioByAlbumId?.let {
                //获取下载id
                val map = it.map {
                    GsonManager.getInstance().convert(it.data, TrackBean::class.java)
                        .generateDownloadDBId()
                }
                //根据下载id删除，缓存数据
                map.forEach {
                    val downloadModle = DownloadDatabase.database?.getDownloadDao()?.getDownloadModle(it)
                    downloadModle?.let { it1 ->
                        DownloadDatabase.database?.getDownloadDao()?.delete(
                            it1
                        )
                    }
                }


                AudioDatabase.DATABASE?.getAudioDao()?.delete(it)
            }

            updateHaveDownload(albumDB.id.toString(),false)

        }
        /**
         * 查找该专辑id下的所有音频
         */
        fun findAllDownloadAudioByAlbumId(albumId: String) : List<TrackDB>?{
            return AudioDatabase.DATABASE?.getAudioDao()?.findDownloadTrackList(albumId)
        }

        fun findAudioById(audioId:String) : TrackBean{
            val findDownloadTackById = AudioDatabase.DATABASE?.getAudioDao()?.findDownloadTackById(audioId)
            val convert =
                GsonManager.getInstance().convert(findDownloadTackById?.data, TrackBean::class.java)
            return convert
        }


    }
}