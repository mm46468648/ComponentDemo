package com.mooc.studyroom.viewmodel

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.studyroom.StudyRoomApi
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class CompleteCourseViewModel : BaseListViewModel<CourseBean>() {
    override suspend fun getData(): Deferred<List<CourseBean>?> {
        return viewModelScope.async {
            val await = ApiService.getRetrofit().create(StudyRoomApi::class.java).getCompleteCourse(offset, limit).await()
            await.results
        }
    }
}