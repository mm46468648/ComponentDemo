package com.mooc.commonbusiness.module.studyroom.publication

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.search.PublicationBean
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

/**
 * @param folderId 文件夹id，""，默认是根目录
 */
class PublicListPublicationViewModel(var folderId:String="") : BaseListViewModel<PublicationBean>() {

    var userId = ""
    override suspend fun getData(): Deferred<List<PublicationBean>?> {

        val mId = if(GlobalsUserManager.uid == userId) userId else ""
        val userId = if(GlobalsUserManager.uid == userId) "" else userId

        return viewModelScope.async {
            val await = HttpService.studyRoomApi.getPublicResourceList(
                folderId,
                ResourceTypeConstans.TYPE_PUBLICATION.toString(),
                mId,
                userId,
                offset,
                limit
            ).await().data
            await.folder.kanwu?.items
        }
    }
}