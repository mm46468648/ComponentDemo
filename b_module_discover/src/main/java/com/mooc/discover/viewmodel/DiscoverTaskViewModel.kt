package com.mooc.discover.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.discover.httpserver.HttpService
import com.mooc.discover.model.DiscoverTaskBean
import com.mooc.discover.request.HomeDiscoverRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import okhttp3.RequestBody

class DiscoverTaskViewModel : BaseListViewModel<DiscoverTaskBean>() {

    private val repository = HomeDiscoverRepository()

    val mTaskStatusBean: MutableLiveData<HttpResponse<Any>> by lazy {
        MutableLiveData<HttpResponse<Any>>()
    }

    var map: HashMap<String, String> = hashMapOf()
    override suspend fun getData(): Deferred<List<DiscoverTaskBean>?> {
        return viewModelScope.async {
            val task = HttpService.discoverApi.getDiscoverTaskResult(limit, offset).await()
            task.data.results
        }
    }

    fun postTaskToReceive(body: RequestBody) {

        launchUI {
            val bean = repository.postTask(body)
            mTaskStatusBean.postValue(bean)
        }
    }


}
