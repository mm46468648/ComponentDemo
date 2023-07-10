package com.mooc.studyroom.viewmodel

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.net.ApiService
import com.mooc.studyroom.StudyRoomApi
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class ContributeTaskViewModel : BaseListViewModel<com.mooc.studyroom.model.ContributionTaskBean>() {

    override suspend fun getData(): Deferred<List<com.mooc.studyroom.model.ContributionTaskBean>> {
//        val contributeTasks = arrayListOf<ContributionTaskBean>()
        return viewModelScope.async {
            val honorResult = ApiService.getRetrofit().create(StudyRoomApi::class.java).getContributionTask()
//            val myCertificateResult = ApiService.getRetrofit().create(HomeApi::class.java).getContributionTask(2)
//            contributeTasks.addAll(honorResult.await().data)
//            contributeTasks.addAll(myCertificateResult.await().data)
            honorResult.await().data
        }
    }
}