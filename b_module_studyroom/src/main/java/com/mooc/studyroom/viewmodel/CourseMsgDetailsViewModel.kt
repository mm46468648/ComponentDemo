package com.mooc.studyroom.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.net.ApiService
import com.mooc.studyroom.StudyRoomApi
import com.mooc.studyroom.model.CourseMsgDetailBean
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

/**
课程消息详情的viewmodel
 * @Author limeng
 * @Date 2021/3/4-2:44 PM
 */
class CourseMsgDetailsViewModel : BaseListViewModel<CourseMsgDetailBean>() {
    var deleteRespose = MutableLiveData<HttpResponse<Any>>()
    var course_id: String? =null
    var message_type: String? =  "5"//写死的吖
    override suspend fun getData(): Deferred<List<CourseMsgDetailBean>?> {
        val data = viewModelScope.async {
            val bean = ApiService.getRetrofit().create(StudyRoomApi::class.java).getCourseDetailMsg(course_id, message_type, limit, offset).await()
            bean.results
        }
        return data
    }

    fun deleteMessage(id: String) {
        launchUI {
            var bean = ApiService.getRetrofit().create(StudyRoomApi::class.java).delInteractionMsg(id).await()
            deleteRespose.postValue(bean)
        }
    }
}