package com.mooc.course.viewmodel

import androidx.lifecycle.viewModelScope
import com.mooc.course.CourseApi
import com.mooc.course.model.CourseNotice
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.net.ApiService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class CourseNoticeViewModel : BaseListViewModel<CourseNotice>() {
    var xtCourseId = ""
    override suspend fun getData(): Deferred<List<CourseNotice>> {
        return viewModelScope.async {
            ApiService.xtRetrofit.create(CourseApi::class.java).getCourseNotice(xtCourseId).await().updates
        }
    }
}