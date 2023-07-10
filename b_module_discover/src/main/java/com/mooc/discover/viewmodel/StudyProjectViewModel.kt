package com.mooc.discover.viewmodel

import androidx.lifecycle.viewModelScope
import com.mooc.discover.httpserver.HttpService
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.studyproject.StudyProject
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.base.BaseListViewModel2
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StudyProjectViewModel : BaseListViewModel2<StudyProject>() {
    override suspend fun getData(): Flow<List<StudyProject>?> {
        return flow {
            val recommendBaseResponse = HttpService.discoverApi.getDiscoverStudyPlan(offset, limit)
            emit(recommendBaseResponse.results)
        }
    }
}