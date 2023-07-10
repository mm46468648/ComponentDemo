package com.mooc.studyroom.viewmodel

import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.commonbusiness.route.routeservice.AudioDownloadService

class AlbumDownladDetailViewModel : BaseViewModel(){

    var audioListData = MutableLiveData<List<TrackBean>>()


    fun getAudioList(albumId:String){
        launchUI {
            val navigation = ARouter.getInstance().navigation(AudioDownloadService::class.java)
            val findDownloadAblumDetail = navigation.findDownloadAblumDetail(albumId)
            val map = findDownloadAblumDetail?.map {
                GsonManager.getInstance().convert(it.data, TrackBean::class.java)
            }
            audioListData.postValue(map)
        }
    }

}