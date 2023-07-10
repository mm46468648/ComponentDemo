package com.mooc.studyroom.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.net.ApiService
import com.mooc.studyroom.StudyRoomApi
import com.mooc.studyroom.model.CourseMsgBean
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

/**

 * @Author limeng
 * @Date 2021/3/4-2:44 PM
 */
class CourseMsgViewModel : BaseListViewModel<CourseMsgBean>() {
    var deleteRespose= MutableLiveData<HttpResponse<Any>>()


    override suspend fun getData(): Deferred<List<CourseMsgBean>?> {
        val data = viewModelScope.async {
            val bean = ApiService.getRetrofit().create(StudyRoomApi::class.java).getCourseMsg(limit, offset).await()
            bean.results
        }
        return data
    }
    fun deleteMessage(id: String) {
        launchUI {
            val bean = ApiService.getRetrofit().create(StudyRoomApi::class.java).delCourseMsg(id).await()
            deleteRespose.postValue(bean)
        }
    }
}