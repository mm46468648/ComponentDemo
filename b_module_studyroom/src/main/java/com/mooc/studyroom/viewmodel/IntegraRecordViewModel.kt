package com.mooc.studyroom.viewmodel

import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.commonbusiness.net.ApiService
import com.mooc.studyroom.IntegralApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class IntegraRecordViewModel:BaseListViewModel2<Any>() {
    override suspend fun getData(): Flow<List<Any>?> {
        return flow {
            val await = ApiService.getRetrofit().create(IntegralApi::class.java).getPrizeRecordList( limit, offset).await()
            emit(await.results)
        }
    }


}