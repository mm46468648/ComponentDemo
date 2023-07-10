package com.mooc.home.ui.discover

import com.mooc.commonbusiness.base.BaseRepository
import com.mooc.commonbusiness.model.studyroom.DiscoverTab
import com.mooc.commonbusiness.net.network.StateLiveData
import com.mooc.home.HttpService

class DiscoverRepository : BaseRepository() {

    suspend fun getDiscoverTabSort(tabLiveData: StateLiveData<List<DiscoverTab>>){
        requestHttp(
            {
                HttpService.homeApi.getDiscoverTabSort()
            }
            ,tabLiveData)
    }
}