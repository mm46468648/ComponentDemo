package com.mooc.commonbusiness.route.routeservice

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider
import com.mooc.commonbusiness.model.db.AlbumDB
import com.mooc.commonbusiness.model.db.TrackDB

interface AudioDownloadService : IProvider {
    override fun init(context: Context?) {}

    fun findDownloadAlbum() : List<AlbumDB>?

    fun findDownloadAblumDetail(albumId:String):List<TrackDB>?

    fun deleteAlbum(album:AlbumDB)

    fun deleteTracks(track: List<TrackDB>)

    fun insertTrack(trackDB: TrackDB)

    fun insertAlbum(trackDB: AlbumDB)

    fun getDownloadAudioNum(albumId: String) : Int
}