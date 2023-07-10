package com.mooc.audio

import com.mooc.audio.db.AudioDatabase
import com.mooc.audio.manager.AudioDbManger
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.model.search.TrackBean
//import com.mooc.audio.model.TrackBean
import com.mooc.commonbusiness.base.BaseRepository
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.audio.AlbumListResponse
import com.mooc.commonbusiness.model.studyproject.MusicBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.download.db.DownloadDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AlbumRepository : BaseRepository() {

    suspend fun getAlbumDetail(albumId: String,sort:String) : HttpResponse<AlbumListResponse>{
        return request {
            ApiService.getRetrofit().create(AudioApi::class.java).getAlbumDetail(albumId,sort).await()
        }
    }

    suspend fun getAlbumDetailNew(albumId: String,sort:String,trackId: String) : HttpResponse<AlbumListResponse>{
        return request {
            ApiService.getRetrofit().create(AudioApi::class.java).getAlbumDetailNew(albumId,sort,trackId).await()
        }
    }
    suspend fun getAlbumList(albumId: String, pageOffset: Int, sort: String = "1"): AlbumListResponse {
        return request {
            ApiService.getRetrofit().create(AudioApi::class.java).getTrackList(albumId, pageOffset, sort, 10).await()
        }
    }

    /**
     * 请求所有的音频列表
     */
    suspend fun getAlbumAllList(albumId: String, sort: String = "1"): Flow<AlbumListResponse> {
        return flow<AlbumListResponse> {
            emit(ApiService.getRetrofit().create(AudioApi::class.java).getAllTrackList(albumId, 1,sort))
        }
    }

    /**
     * 请求所有的音频列表
     */
    suspend fun getAlbumAllTracks(albumId: String, sort: String = "1",offline: Boolean,pageOffset: Int = 1): Flow<List<TrackBean>> {
        return flow<List<TrackBean>> {
            val value = if(offline){
                    getOfflineTrackList(albumId)
                }else{
                    ApiService.getRetrofit().create(AudioApi::class.java)
                        .getAllTrackList(albumId, pageOffset, sort).tracks
                }

            emit(value?: arrayListOf())
        }
    }

    suspend fun getOfflineTrackList(albumId: String) : List<TrackBean>?{
        val map = AudioDatabase.DATABASE?.getAudioDao()?.findDownloadTrackList(albumId)?.map {
            GsonManager.getInstance().convert(it.data, TrackBean::class.java)
        }
        map?.forEach {
            val downloadModle =
                DownloadDatabase.database?.getDownloadDao()?.getDownloadModle(it.generateDownloadDBId())
            if(it.asyncFromXimaData){  //如果是从喜马拉雅同步过来的下载数据，直接使用下载路径为播放地址
                it.play_url_32 = "${downloadModle?.downloadPath}"
            }else{
                it.play_url_32 = "${downloadModle?.downloadPath}/${downloadModle?.fileName}"
            }
        }
        return map
    }

    suspend fun getTrackInfo(trackId: String,offline:Boolean = false): TrackBean {
        if(offline){
            //1.获取数据库音频模型
            val findAudioById = AudioDbManger.findAudioById(trackId)
            //2.获取下载地址
            val downloadModle =
                DownloadDatabase.database?.getDownloadDao()?.getDownloadModle(findAudioById.generateDownloadDBId())
            findAudioById.play_url_32 = "${downloadModle?.downloadPath}/${downloadModle?.fileName}"
            return TrackBean()
        }
        return ApiService.getRetrofit().create(AudioApi::class.java).getTrackInfo(trackId).await()
    }

    suspend fun getOwnBuildTrackInfo(trackId: String): MusicBean {
        return request {
            ApiService.getRetrofit().create(AudioApi::class.java).getOwnBuildTrackInfo(trackId).await()
        }
    }



}