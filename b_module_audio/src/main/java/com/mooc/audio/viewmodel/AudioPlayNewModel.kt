package com.mooc.audio.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.audio.AlbumRepository
import com.mooc.audio.manager.AudioDbManger
import com.mooc.audio.manager.PrePlayListManager
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.model.audio.AlbumListResponse
import com.mooc.download.db.DownloadDatabase
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel
import com.mooc.newdowload.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class AudioPlayNewModel : BaseViewModel() {

    var trackId: String = ""
    var offlineModel: Boolean = false
    var albumId = ""

    var startPage = 1

    val mRepository = AlbumRepository()
    var has_permission = ""
    val trackInfoLiveData: MutableLiveData<TrackBean> by lazy {
        MutableLiveData<TrackBean>()
    }

    val resourceShareDetaildata: MutableLiveData<ShareDetailModel> by lazy {
        MutableLiveData<ShareDetailModel>()
    }

    /**
     * 加载音频数据
     * @param needPlay 查询完是否需要播放
     */
    fun getTrackInfo(trackId: String,needPlay:Boolean = true) {
        if(offlineModel){
            getTracFromLocal(trackId,needPlay)
        }else{
            //线上环境每次都要请求接口,因为音频播放地址会变
            getTrackFromServer(trackId,needPlay)
        }
    }




    fun getTrackFromServer(trackId: String,needPlay:Boolean = true) {
        launchUI {
            flow<TrackBean> {
                val trackInfo = mRepository.getTrackInfo(trackId, false)
                emit(trackInfo)
                getShareInfo(trackId)
            }.collect {
                has_permission = it.has_permission
//                trackInfoLiveData.postValue(it)
                albumId = it.subordinated_album?.id ?: ""
                trackInfoLiveData.postValue(it)

                if(!needPlay) return@collect
                //如果预播放列表有这个音频,直接播放
                if(!PrePlayListManager.getInstance().checkContainTrack(trackId)){
                    loadOnlineAlbumList(trackId, albumId)
                }else{
                    PrePlayListManager.getInstance().playTrackId(trackId)
                }
            }
        }
    }


    /**
     * 获取本地专辑播放列表
     * 直接塞到播放队列
     */
    suspend fun loadLocalAlbumList(){
        mRepository.getAlbumAllTracks(albumId, "1", true,1)
            .catch {
                loge(this.toString())
            }
            .collect {
                var findIndex = 0
                //将这个专辑后面的插入到播放列表中
                it.forEachIndexed { index, trackBean ->
                    trackBean.albumId = trackBean.subordinated_album?.id ?: ""
                    if (trackBean.id == trackId) {
                        findIndex = index
                    }
                }

                PrePlayListManager.getInstance().setLocalPreList(it,albumId,findIndex)


            }
    }

    /**
     * 获取在线状态专辑列表详情,
     * 添加到预播放队列,不直接添加到播放队列
     */
    suspend fun loadOnlineAlbumList(trackId: String, albumId: String) {
        val sort = viewModelScope.async { getAlbumSort(albumId,trackId) }.await()
        loge("专辑排序 ${sort}")
        mRepository.getAlbumAllTracks(albumId, sort, offlineModel,startPage)
            .catch {
                loge(this.toString())
            }
            .collect {
                var findIndex = 0
                //将这个专辑后面的插入到播放列表中
                it.forEachIndexed { index, trackBean ->
                    trackBean.albumId = trackBean.subordinated_album?.id ?: ""
                    if (trackBean.id == trackId) {
                        findIndex = index
                    }
                }

                loge("列表中找到位置:${ findIndex}")
                PrePlayListManager.getInstance().setPreList(it, albumId, findIndex)
            }
    }

    /**
     * 本地查询音频信息
     */
    fun getTracFromLocal(trackId: String,needPlay:Boolean = true) {
        launchUI {
            flow<TrackBean> {
                val trackInfo = AudioDbManger.findAudioById(trackId)
                emit(trackInfo)
            }.collect {
                trackInfoLiveData.postValue(it)
                albumId = it.subordinated_album?.id ?: ""

                if(!needPlay) return@collect
                //如果预播放列表有这个音频,直接播放
                if(!PrePlayListManager.getInstance().checkContainTrack(trackId)){
                    loadLocalAlbumList()
                }else{
                    PrePlayListManager.getInstance().playTrackId(trackId)
                }
            }
        }
    }

    /**
     * 获取专辑的排序
     * 默认正序
     * 离线模式，默认正序
     */
    suspend fun getAlbumSort(albumId: String, trackId: String): String {
        if (offlineModel) return "1"
        try {
//            return mRepository.getAlbumDetail(albumId, "").data.params?.sort ?: "1"
            val albumDetailNew = mRepository.getAlbumDetailNew(albumId, "", trackId)

            //page从第0页开始,以10页未一个单位查询
            startPage = (albumDetailNew.data.latest_page / 10) + 1

            loge("startPage:${startPage}}")
            return albumDetailNew.data.params?.sort ?: "1"
        } catch (e: Exception) {
            return "1"
        }
    }

    /**
     * 获取分享和加入学习室信息
     */
    fun getShareInfo(trackId: String) {
        launchUI {
            val shareDetailData = HttpService.commonApi.getShareDetailDataNew(
                ShareTypeConstants.SHARE_TYPE_RESOURCE,
                ResourceTypeConstans.TYPE_TRACK.toString(),
                trackId
            )
            if (shareDetailData.data != null)
                resourceShareDetaildata.postValue(shareDetailData.data)
        }
    }

    fun getShareDetail(): ShareDetailModel? {
        val value = resourceShareDetaildata.value
        if (value == null) {
            launchUI { getShareInfo() }
        }

        return value

    }

    suspend fun getShareInfo(): ShareDetailModel {
        val shareDetailData = HttpService.commonApi.getShareDetailData(
            ResourceTypeConstans.TYPE_TRACK.toString(),
            trackId
        )
        return shareDetailData.await().data

    }

    /**
     * 下载单条音频
     * 并提示相应状态
     */
    fun donwloadTrack() {
        val curreantTrack = trackInfoLiveData.value ?: return

        var downLoadInfo: DownloadInfo? =
            DownloadManager.DOWNLOAD_INFO_HASHMAP.get(curreantTrack.generateDownloadDBId())
        if (downLoadInfo != null) {
            //如果是这两种状态，提示toast
            if (downLoadInfo.state == State.DOWNLOADING || downLoadInfo.state == State.DOWNLOAD_COMPLETED) {
                when (downLoadInfo.state) {
                    State.DOWNLOADING -> toast("下载中")
                    State.DOWNLOAD_COMPLETED -> toast("已下载")
                }
                return
            }
        }



        downLoadInfo = DownloadDatabase.database?.getDownloadDao()
            ?.getDownloadModle(curreantTrack.generateDownloadDBId())

        if (downLoadInfo == null) {
            downLoadInfo = createDownloadInfo(curreantTrack)
        }
        DownloadManager.DOWNLOAD_INFO_HASHMAP.put(downLoadInfo.id, downLoadInfo)
        DownloadManager.getInstance().addObserve(object : DownloadTaskObserve {
            override fun update(id: Long) {
                if (id == curreantTrack.generateDownloadDBId()) {
                    val get = DownloadManager.DOWNLOAD_INFO_HASHMAP.get(id)
                    if (get?.state == State.DOWNLOAD_COMPLETED) {
                        //如果数据库里没有对应的专辑，还需要插入一个专辑记录
                        trackInfoLiveData.value?.subordinated_album?.let { album ->
                            val albumListResponse = AlbumListResponse()
                            albumListResponse.id = album.id
                            albumListResponse.album_title = album.album_title
                            albumListResponse.cover_url_large = album.cover_url_large
                            AudioDbManger.insertAlbumResponse(albumListResponse)
                        }

                        //缓存音频表中插入一条记录
                        AudioDbManger.insertDownloadAudio(curreantTrack.generateTrackDB())
                        //同时向音频课表中更新此音频课有缓存内容
                        AudioDbManger.updateHaveDownload(curreantTrack.albumId, true)
                        toast("已下载")
                        DownloadManager.getInstance().removeObserve(this)
                    }
                }
            }

            override fun getDownloadId(): Long {
                return curreantTrack.generateDownloadDBId()
            }

        })
        toast("开始下载")
        //执行下载
        DownloadManager.getInstance().handleDownload(downLoadInfo.id)
    }

    private fun createDownloadInfo(curreantTrack: TrackBean): DownloadInfo {
        curreantTrack.albumId = albumId
        return DownloadInfoBuilder()
            .setDownloadID(curreantTrack.generateDownloadDBId())
            .setDownloadUrl(curreantTrack.play_url_32)
            .setFilePath(DownloadConfig.audioLocation + "/" + curreantTrack.albumId)
            .setFileName(curreantTrack.track_title)
            .build()
    }

}