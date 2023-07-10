package com.mooc.studyroom.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.studyroom.StudyRoomApi
import com.mooc.studyroom.model.InteractionMsgBean
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

/**

 * @Author limeng
 * @Date 2021/3/4-2:44 PM
 */
class InteractionMsgViewModel : BaseListViewModel<InteractionMsgBean>() {
    var deleteRespose=MutableLiveData<HttpResponse<Any>>()
    override suspend fun getData(): Deferred<List<InteractionMsgBean>?> {
        val data = viewModelScope.async {
            val bean = ApiService.getRetrofit().create(StudyRoomApi::class.java).getInteractionMsg(limit, offset).await()
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