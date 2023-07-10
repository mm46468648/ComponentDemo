package com.mooc.home.ui.discover.micrlprofession

import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.commonbusiness.model.MicroProfession
import com.mooc.commonbusiness.model.microknowledge.MicroKnowBean
import com.mooc.discover.httpserver.HttpService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MicroKnowProfessionViewModel : BaseListViewModel2<MicroProfession>() {
    override suspend fun getData(): Flow<List<MicroProfession>> {
        return flow {
            val recommendBaseResponse = HttpService.discoverApi.getMicroProfessions(limit, offset)
            emit(recommendBaseResponse.results)
        }
    }

}