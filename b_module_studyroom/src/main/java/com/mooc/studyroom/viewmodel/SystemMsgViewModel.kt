package com.mooc.studyroom.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.net.ApiService
import com.mooc.studyroom.StudyRoomApi
import com.mooc.studyroom.model.SystemMsgBean
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

/**

 * @Author limeng
 * @Date 2021/3/4-2:44 PM
 */
class SystemMsgViewModel :BaseListViewModel<SystemMsgBean>() {
    var deleteRespose= MutableLiveData<HttpResponse<Any>>()

    override suspend fun getData(): Deferred<ArrayList<SystemMsgBean>?> {
       return viewModelScope.async {
            val bean = ApiService.getRetrofit().create(StudyRoomApi::class.java).getSystemMessage(limit, offset).await()
            bean.results
        }
    }
    fun deleteMessage(id: String) {
        launchUI {
            var bean = ApiService.getRetrofit().create(StudyRoomApi::class.java).delSystemMsg(id).await()
            deleteRespose.postValue(bean)
        }
    }
}