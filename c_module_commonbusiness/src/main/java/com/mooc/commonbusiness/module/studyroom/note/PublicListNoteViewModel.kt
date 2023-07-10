package com.mooc.commonbusiness.module.studyroom.note

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.home.NoteBean
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.global.GlobalsUserManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class PublicListNoteViewModel(var folderId : String="") : BaseListViewModel<NoteBean>() {
    var userId = ""

    override suspend fun getData(): Deferred<List<NoteBean>?> {

        val mId = if(GlobalsUserManager.uid == userId) userId else ""
        val userId = if(GlobalsUserManager.uid == userId) "" else userId

        return viewModelScope.async {
            val await = HttpService.studyRoomApi.getPublicResourceList(
                folderId,
                ResourceTypeConstans.TYPE_NOTE.toString(),
                mId,
                userId,
                offset,
                limit
            ).await().data
            await.folder.note?.items
        }
    }
}