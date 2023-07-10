package com.mooc.commonbusiness.module.studyroom.publication

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.search.PublicationBean
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class PublicationViewModel(var folderId : String?="") : BaseListViewModel<PublicationBean>() {
    override suspend fun getData(): Deferred<List<PublicationBean>?> {

        //如果不为空，查询的是学习清单子目录中刊物资源
        if(folderId?.isNotEmpty() == true){
            return viewModelScope.async {
//                这里接口要改成刊物的求情
                val await = HttpService.studyRoomApi.getFolderContentWithId(folderId, ResourceTypeConstans.TYPE_PUBLICATION.toString(),offset,limit).await()
                await.kanwu?.items
            }
        }
        return viewModelScope.async {
            val await = HttpService.studyRoomApi.getUserStudyData(ResourceTypeConstans.TYPE_PUBLICATION.toString(),offset,limit).await()
            await.kanwu?.items
        }
    }


}