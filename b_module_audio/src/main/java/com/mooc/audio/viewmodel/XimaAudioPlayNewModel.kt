package com.mooc.audio.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.audio.AlbumRepository
import com.mooc.audio.manager.AudioDbManger
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.manager.studylog.StudyLogManager
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.model.audio.AlbumListResponse
import com.mooc.commonbusiness.model.audio.BaseAudioModle
import com.mooc.download.db.DownloadDatabase
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel
import com.mooc.newdowload.*
import com.ximalaya.ting.android.opensdk.model.PlayableModel
import com.ximalaya.ting.android.opensdk.model.track.Track
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.json.JSONObject

class XimaAudioPlayNewModel : BaseViewModel() {

    var trackId: String = ""
    var offlineModel: Boolean = false
    val mRepository = AlbumRepository()
    var has_permission = ""
    val trackInfoLiveData: MutableLiveData<TrackBean> by lazy {
        MutableLiveData<TrackBean>()
    }

    val resourceShareDetaildata: MutableLiveData<ShareDetailModel> by lazy {
        MutableLiveData<ShareDetailModel>()
    }


    //加载音频课列表状态
//    var loadAlbumState = MutableLiveData<Int>()

    val albumListLiveData: MutableLiveData<ArrayList<TrackBean>> by lazy {
        val mutableLiveData = MutableLiveData<ArrayList<TrackBean>>()
//        mutableLiveData.value = AlbumAudioListManager.currentList
        mutableLiveData.value = arrayListOf()
        mutableLiveData
    }


    fun getTrackInfo(trackId: String) {
        val find = albumListLiveData.value?.find {
            it.id == trackId
        }

        if (find != null) {
            trackInfoLiveData.postValue(find)
            this.trackId = trackId
//            postTrackInfo(trackId)
            getShareInfo(trackId)
        } else {
            //如果专辑列表没找到从接口请求
            getTrackFromServer(trackId)
        }
    }

    var albumId = ""


    fun getTrackFromServer(trackId: String) {
        launchUI {
            flow<TrackBean> {
                val trackInfo = mRepository.getTrackInfo(trackId, false)
                emit(trackInfo)
                getShareInfo(trackId)
            }.catch {
                loge(this.toString())
            }.collect {
                has_permission = it.has_permission
                trackInfoLiveData.postValue(it)
                albumId = it.subordinated_album?.id ?: ""
//                //将此音频先插入播放列表
                //加载专辑列表
            }
        }
    }

    /**
     * 本地查询音频信息
     */
    fun getTracFromLocal(trackId: String) {
        launchUI {
            flow<TrackBean> {
                val trackInfo = AudioDbManger.findAudioById(trackId)
                emit(trackInfo)
            }.collect {
                trackInfoLiveData.postValue(it)
                albumId = it.subordinated_album?.id ?: ""
//                //将此音频先插入播放列表
                //加载专辑列表
            }
        }
    }

    /**
     * 获取专辑列表详情
     *
     * 如果没有传递专辑id，先获取音频详情后再获取专辑id
     */
    suspend fun loadAlbumList(albumId: String): Flow<List<TrackBean>> {
        val sort = viewModelScope.async { getAlbumSort(albumId) }.await()
        loge("专辑排序 ${sort}")
        return mRepository.getAlbumAllTracks(albumId, sort, offlineModel)
    }

    /**
     * 获取专辑的排序
     * 默认正序
     * 离线模式，默认正序
     */
    suspend fun getAlbumSort(albumId: String) : String{
        if(offlineModel) return "1"
        try {
            return mRepository.getAlbumDetail(albumId,"").data.params?.sort?:"1"
        }catch (e:Exception){
            return "1"
        }
    }

    /**
     * 获取分享和加入学习室信息
     */
    fun getShareInfo(trackId: String) {
        launchUI {
            val shareDetailData = HttpService.commonApi.getShareDetailDataNew(ShareTypeConstants.SHARE_TYPE_RESOURCE,ResourceTypeConstans.TYPE_TRACK.toString(), trackId)
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