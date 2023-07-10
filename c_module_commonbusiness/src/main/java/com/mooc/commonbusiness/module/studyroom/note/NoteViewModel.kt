package com.mooc.commonbusiness.module.studyroom.note

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.home.NoteBean
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class NoteViewModel(var folderId : String?="") : BaseListViewModel<NoteBean>() {
    override suspend fun getData(): Deferred<List<NoteBean>?> {
        return viewModelScope.async {

            val await = if(folderId?.isNotEmpty() == true){
                HttpService.studyRoomApi.getFolderContentWithId(folderId,ResourceTypeConstans.TYPE_NOTE.toString(),offset,limit).await()
            }else{
                HttpService.studyRoomApi.getUserStudyData(ResourceTypeConstans.TYPE_NOTE.toString(),offset,limit).await()
            }
//            val await = HttpService.studyRoomApi.getUserStudyData(ResourceTypeConstans.TYPE_NOTE.toString()).await()
            await.note?.items
        }
    }
}