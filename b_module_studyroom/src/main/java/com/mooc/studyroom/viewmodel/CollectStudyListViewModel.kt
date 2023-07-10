package com.mooc.studyroom.viewmodel

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.studyroom.HttpService
import com.mooc.studyroom.model.CollectList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CollectStudyListViewModel : BaseListViewModel<CollectList>() {
    override suspend fun getData(): Deferred<List<CollectList>> {
        return viewModelScope.async {
            HttpService.studyRoomApi.getCollectStudyList(offset,limit).await().data.results
        }
    }

    /**
     * 删除收藏
     */
    fun deleteCollect(folderId:String){
        launchUI {
            HttpService.studyRoomApi.deleteCollectList(folderId)
        }
    }
}