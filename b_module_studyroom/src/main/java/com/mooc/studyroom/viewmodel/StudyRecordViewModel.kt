package com.mooc.studyroom.viewmodel

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.HttpListResponse
import com.mooc.commonbusiness.net.ApiService
import com.mooc.studyroom.StudyRoomApi
import com.mooc.studyroom.model.StudyRecordBean
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class StudyRecordViewModel : BaseListViewModel<StudyRecordBean>() {

//    override fun needGetData2(): Boolean {
//        return true
//    }
//    override suspend fun getData2(): Deferred<HttpListResponse<List<StudyRecordBean>>> {
//        return  ApiService.getRetrofit().create(StudyRoomApi::class.java).getStudyLearnDetail(offset,limit)
//    }

    override suspend fun getData(): Deferred<List<StudyRecordBean>?> {
        val async = viewModelScope.async {
            val activityList = ApiService.getRetrofit().create(StudyRoomApi::class.java).getStudyLearnDetail(offset,limit).await()
            activityList.results
        }
        return async
    }

    fun cleatStudyRecordData(){
        getPageData().value?.clear()
    }
}