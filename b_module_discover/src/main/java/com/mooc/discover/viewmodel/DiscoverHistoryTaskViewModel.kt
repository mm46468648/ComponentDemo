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
/**
 *历史任务model
 * @author limeng
 * @date 2022/6/13
 */
class DiscoverHistoryTaskViewModel : BaseListViewModel<DiscoverTaskBean>() {

    private val repository = HomeDiscoverRepository()

    val mTaskStatusBean: MutableLiveData<HttpResponse<Any>> by lazy {
        MutableLiveData<HttpResponse<Any>>()
    }

    var map: HashMap<String, String> = hashMapOf()
    override suspend fun getData(): Deferred<List<DiscoverTaskBean>?> {
        return viewModelScope.async {
            val task = HttpService.discoverApi.getMyDiscoverTaskResult(2,limit, offset).await()
            task.data.results
        }
    }



}
