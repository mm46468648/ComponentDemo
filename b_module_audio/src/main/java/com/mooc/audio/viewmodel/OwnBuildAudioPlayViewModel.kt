package com.mooc.audio.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.audio.AlbumRepository
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.model.studyproject.MusicBean
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel

class OwnBuildAudioPlayViewModel : BaseViewModel() {
    var trackId: String = ""
    val mRepository  = AlbumRepository()

    val trackInfoLiveData =  MutableLiveData<MusicBean>()

    val resourceShareDetaildata by lazy {
        MutableLiveData<ShareDetailModel>().also {
            getShareInfo(trackId)
        }
    }

    fun getShareDetail(): ShareDetailModel? {
        val value = resourceShareDetaildata.value
        if (value == null) {
            launchUI { getShareInfo(trackId) }
        }
        return value

    }


    fun loadAudioInfo(){
        launchUI {
            val ownBuildTrackInfo = mRepository.getOwnBuildTrackInfo(trackId)
//            ownBuildTrackInfo.resource_link = "http://aod.cos.tx.xmcdn.com/storages/aba9-audiofreehighqps/BF/93/CKwRIMAEMgZyAMGRPACWju5H.mp3"
//            TrackPlayManger.createMediaMetadataCompats(ownBuildTrackInfo.id, arrayListOf(ownBuildTrackInfo))
            trackInfoLiveData.postValue(ownBuildTrackInfo)
        }
    }

    /**
     * 获取分享和加入学习室信息
     */
    private fun getShareInfo(trackId: String) {
        launchUI {
            val shareDetailData = HttpService.commonApi.getShareDetailDataNew(ShareTypeConstants.SHARE_TYPE_RESOURCE,ResourceTypeConstans.TYPE_ONESELF_TRACK.toString(), trackId)
            if (shareDetailData.data != null)
                resourceShareDetaildata.postValue(shareDetailData.data)
        }
    }
}