package com.mooc.discover.viewmodel

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.discover.httpserver.HttpService
import com.mooc.discover.model.RecommendContentBean
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RecommendLookMoreViewModel : BaseListViewModel<RecommendContentBean.DataBean>() {
    var resId = ""
    override suspend fun getData(): Deferred<List<RecommendContentBean.DataBean>?> {
        return viewModelScope.async {
            HttpService.discoverApi.getRecommendLookMoreList(resId,offset,limit).await().data
        }
    }

    override fun getOffSetStart(): Int {
        return 1
    }

    override fun getLimitStart(): Int {
        return 20
    }
}