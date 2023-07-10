package com.mooc.home.ui.hornowall.talent

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.home.HttpService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TalentDetailViewModel : BaseListViewModel2<Any>(){
    var planId : String = ""
    override suspend fun getData(): Flow<List<Any>> {
       return flow {
           val studyPlanHonorDetial = HttpService.homeApi.getStudyPlanHonorDetial(planId, offset, limit)
           emit(studyPlanHonorDetial.user_complate_info)
//            studyPlanHonorDetial.user_complate_info
       }
    }
//    override suspend fun getData(): Deferred<List<Any>?> {
//        return viewModelScope.async {
//            val studyPlanHonorDetial = HttpService.homeApi.getStudyPlanHonorDetial(planId, offset, limit).await()
//            studyPlanHonorDetial.user_complate_info
//        }
//    }
}