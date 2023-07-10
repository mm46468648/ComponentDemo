package com.mooc.audio

import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.audio.db.AudioDatabase
import com.mooc.audio.manager.AudioDbManger
import com.mooc.commonbusiness.model.db.AlbumDB
import com.mooc.commonbusiness.model.db.TrackDB
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.AudioDownloadService

@Route(path = Paths.SERVICE_AUDIO_DOWNLOAD)
class AudioDownloadImpl : AudioDownloadService {
    override fun findDownloadAlbum(): List<AlbumDB>? {
        return AudioDbManger.findAllDownloadAlbum()
    }

    override fun findDownloadAblumDetail(albumId:String): List<TrackDB>? {
        return AudioDbManger.findAllDownloadAudioByAlbumId(albumId)
    }

    override fun deleteAlbum(album:AlbumDB) {
        //1。先删除所有音频数据库数据
        //2。再更新专辑数据库
//        AudioDatabase.DATABASE?.getAlbumDao()?.delete(album)
        AudioDbManger.deleteAllAudioByAlbum(album)
    }

    override fun deleteTracks(track: List<TrackDB>) {
        AudioDatabase.DATABASE?.getAudioDao()?.delete(track)
    }

    override fun insertTrack(trackDB: TrackDB) {
        AudioDbManger.insertDownloadAudio(trackDB)
    }

    override fun insertAlbum(albumDB: AlbumDB) {
        AudioDatabase.DATABASE?.getAlbumDao()?.insert(albumDB)
    }

    override fun getDownloadAudioNum(albumId: String):Int {
        return AudioDatabase.DATABASE?.getAudioDao()?.getDownloadCount(albumId)?:0
    }
}