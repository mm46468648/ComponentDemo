package com.mooc.audio.viewmodel

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.model.audio.AlbumListResponse
import com.mooc.audio.AlbumRepository
import com.mooc.audio.AudioApi
import com.mooc.download.DownloadModel
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.constants.LoadStateConstants
import com.mooc.audio.manager.AudioDbManger
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseResourceViewModel
import com.mooc.commonbusiness.constants.LiveDataBusEventConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.common.bus.LiveDataBus
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.download.db.DownloadDatabase
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.newdowload.DownloadInfo
import com.mooc.newdowload.DownloadInfoBuilder
import com.mooc.newdowload.DownloadManager
import org.json.JSONObject

class AlbumViewModel(var albumId: String) :
    BaseResourceViewModel(albumId, ResourceTypeConstans.TYPE_ALBUM) {

    val mRepository = AlbumRepository()

    var pageOffset = 1    //从第一页开始加载

    //    var nextPageOffset = 1
    var beforePageOffset = 1
    var sort = ""   //1正序，0倒叙 默认""，未知排序
    var total_page = 0 //总叶码

    val pageState = MutableLiveData<Int>()   //页面状态1，全部加载完毕，2.加载中，3。加载错误
    var albumList = ArrayList<TrackBean>()

    val albumListLiveData: MutableLiveData<ArrayList<TrackBean>> by lazy {
        val mutableLiveData = MutableLiveData<ArrayList<TrackBean>>()
        mutableLiveData
    }

    val albumDetailLiveDta: MutableLiveData<AlbumListResponse> by lazy {
        MutableLiveData<AlbumListResponse>()
    }

    init {
        albumListLiveData.value = albumList
    }


    /**
     * 获取音频课信息
     */
    fun loadAlbumInfo(s: String = "",errorCallBack:((it:Exception)->Unit)? = null) {
        //获取加入学习室和分享数据
//        getShareDetailData(ResourceTypeConstans.TYPE_ALBUM.toString(), albumId)
        launchUI({
            val albumInfo = mRepository.getAlbumDetail(albumId, s).data
            albumDetailLiveDta.postValue(albumInfo)


            //根据上次请求的页码，和排序加载数据
            pageOffset = albumInfo.latest_page
            sort = albumInfo.params?.sort ?: "1"
            total_page = albumInfo.total_page
            beforePageOffset = pageOffset - 1


            getAlbumList()
            //在音频课表中插入一条音频课记录
            AudioDbManger.insertAlbumResponse(albumInfo)
        }, {
            errorCallBack?.invoke(it)
        })
    }

    /**
     * 改变顺序，并重置
     * @param  sort正序1，倒叙0
     */
    fun getAlbumList() {
        launchUI({
            val albumListResponse =
                mRepository.getAlbumList(albumId, pageOffset, this@AlbumViewModel.sort)
            pageState.postValue(LoadStateConstants.STATE_REFRESH_COMPLETE)
            //音频课列表
            if (albumListResponse.tracks?.isNotEmpty() == true) {
                albumListLiveData.value?.clear()
                //同步查询相应的下载信息数据
                albumListResponse.tracks?.forEach {
                    it.albumId = albumListResponse.id

                    initDownloadInfo(it)
                }
                pageOffset++
                albumListLiveData.value?.addAll(albumListResponse.tracks!!)
                albumListLiveData.postValue(albumListLiveData.value)

//                AlbumAudioListManager.setList(albumId, albumListResponse.tracks!!)

                //接口中返回上次在听
                if (albumDetailLiveDta.value?.latest_track_id?.isNotEmpty() == true && albumDetailLiveDta.value?.latest_track_id ?: "0" != "0") {
                    LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_LISTEN_TRACK_ID)
                        .postValue(albumDetailLiveDta.value?.latest_track_id)
                }
            }else{
                pageState.postValue(LoadStateConstants.STATE_DATA_EMPTY)
            }
        }, {
            loge(it.toString())
            pageState.postValue(LoadStateConstants.STATE_ERROR)
        })
    }

    //下拉刷新，请求上一页
    fun loadBefore() {
        if (beforePageOffset < 1) {
            pageState.postValue(LoadStateConstants.STATE_REFRESH_COMPLETE)
            return
        }
        launchUI({
            val albumListResponse =
                mRepository.getAlbumList(albumId, beforePageOffset, this@AlbumViewModel.sort)
            pageState.postValue(LoadStateConstants.STATE_REFRESH_COMPLETE)


            //音频课列表
            if (albumListResponse.tracks?.isNotEmpty() == true) {
                //同步查询相应的下载信息数据
                albumListResponse.tracks?.forEach {
                    it.albumId = albumListResponse.id
                    initDownloadInfo(it)
                }
                beforePageOffset--
                albumListLiveData.value?.addAll(0, albumListResponse.tracks!!)
                albumListLiveData.postValue(albumListLiveData.value)

                //记录音频课数据
//                AlbumAudioListManager.addList(0, albumId, albumListResponse.tracks!!)


            }


        }, {
            loge(it.toString())
            pageState.postValue(LoadStateConstants.STATE_ERROR)
        })

    }

    fun loadMore() {
        if (pageOffset > total_page) {
            pageState.postValue(LoadStateConstants.STATE_ALL_COMPLETE)
            return
        }
        launchUI({
            val albumListResponse =
                mRepository.getAlbumList(albumId, pageOffset, this@AlbumViewModel.sort)
            pageState.postValue(LoadStateConstants.STATE_CURRENT_COMPLETE)

            //通知所有加载完毕
            if (albumListResponse.tracks?.isEmpty() == true && pageOffset > 1) {
                pageState.postValue(LoadStateConstants.STATE_ALL_COMPLETE)
            }

            //音频课列表
            if (albumListResponse.tracks?.isNotEmpty() == true) {
                if (pageOffset >= total_page) {
                    pageState.postValue(LoadStateConstants.STATE_ALL_COMPLETE)
                }
                //同步查询相应的下载信息数据
                albumListResponse.tracks?.forEach {
                    it.albumId = albumListResponse.id
                    initDownloadInfo(it)
                }
                pageOffset++


                albumListLiveData.value?.addAll(albumListResponse.tracks!!)
                albumListLiveData.postValue(albumListLiveData.value)

                //添加音频课数据
//                AlbumAudioListManager.addList(
//                    AlbumAudioListManager.currentList.size,
//                    albumId,
//                    albumListResponse.tracks!!
//                )

            }


        }, {
            loge(it.toString())
            pageState.postValue(LoadStateConstants.STATE_ERROR)
        })
    }


    /**
     * 获取分享信息
     */
    fun getShareInfo(): LiveData<ShareDetailModel> {
        val mutableLiveData = MutableLiveData<ShareDetailModel>()
        launchUI {
            val shareDetailData = HttpService.commonApi.getShareDetailDataNew(
                ShareTypeConstants.SHARE_TYPE_RESOURCE,
                ResourceTypeConstans.TYPE_ALBUM.toString(),
                albumId
            )
            mutableLiveData.postValue(shareDetailData.data)
        }
        return mutableLiveData
    }

    /**
     * 屏蔽专辑资源
     * 专辑资源固定传"2"
     */
    fun postSheildAblum(){
        val requestData = JSONObject()
        requestData.put("type", "2")
        requestData.put("pk", albumId)
//        requestData.put("duration", audioPointManager.currentMusicData?.progress?.div(1000) ?: 0)
        ApiService.getRetrofit().create(AudioApi::class.java)
            .postResourceShield(RequestBodyUtil.fromJson(requestData))
            .compose(RxUtils.applySchedulers())
            .subscribe ({
                if(it!=null){
                    toast(it.msg)
                }
            },{})
    }

    fun initDownloadInfo(track: TrackBean) {
        val key = track.generateDownloadDBId()
        if (DownloadManager.DOWNLOAD_INFO_HASHMAP.get(key) != null) return

        var downLoadInfo = loadFromDB(track)
        if (downLoadInfo == null) {
            downLoadInfo = createDownloadInfo(track)
        }

        //添加到缓存
        downLoadInfo?.let {
            DownloadManager.DOWNLOAD_INFO_HASHMAP.put(key, it)
        }
    }

    /**
     * 内存没有从数据库加载
     */
    private fun loadFromDB(trackbean: TrackBean): DownloadInfo? {
        val downloadModle =
            DownloadDatabase.database?.getDownloadDao()
                ?.getDownloadModle(trackbean.generateDownloadDBId())


        if (downloadModle?.state == DownloadModel.STATUS_DOWNLOADING || downloadModle?.state == DownloadModel.STATUS_WAIT) {
            downloadModle.state = DownloadModel.STATUS_PAUSED
        }
        return downloadModle
    }

    private fun createDownloadInfo(item: TrackBean): DownloadInfo? {
        if (TextUtils.isEmpty(item.play_url_32)) return null
        return DownloadInfoBuilder()
            .setDownloadID(item.generateDownloadDBId())
            .setDownloadUrl(item.play_url_32)
            .setFilePath(DownloadConfig.audioLocation + "/" + item.albumId)
            .setFileName(item.track_title)
            .build()
    }
}