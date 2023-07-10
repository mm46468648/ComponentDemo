package com.mooc.home.ui.discover.microknow

import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.commonbusiness.model.microknowledge.MicroKnowBean
import com.mooc.discover.httpserver.HttpService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MicroKnowChildViewModel : BaseListViewModel2<MicroKnowBean>() {
    override suspend fun getData(): Flow<List<MicroKnowBean>?> {
        return flow {
            val recommendBaseResponse = HttpService.discoverApi.getMicroKnowledge(offset, limit)
            emit(recommendBaseResponse.results)
        }
    }

}