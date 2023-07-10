package com.mooc.discover.viewmodel

import androidx.lifecycle.viewModelScope
import com.mooc.discover.model.ResultBean
import com.mooc.discover.httpserver.HttpService
import com.mooc.commonbusiness.base.BaseListViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

/**

 * @Author limeng
 * @Date 2020/11/17-10:51 AM
 */
class NewLineViewModel : BaseListViewModel<ResultBean>() {
    override suspend fun getData(): Deferred<ArrayList<ResultBean>?> {
        val asyn = viewModelScope.async {
            val value = HttpService.discoverApi.getNewOnline(limit, offset).await()
            value.result
        }
        return asyn
    }
}