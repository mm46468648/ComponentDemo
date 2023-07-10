package com.mooc.column.ui.columnall

import androidx.lifecycle.viewModelScope
import com.mooc.discover.httpserver.HttpService
import com.mooc.discover.model.RecommendColumn
import com.mooc.commonbusiness.base.BaseListViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class ColumnAllViewModel : BaseListViewModel<RecommendColumn>() {


    override fun getLimitStart(): Int {
        return 4
    }


    override suspend fun getData(): Deferred<ArrayList<RecommendColumn>?> {
        val asyn = viewModelScope.async {
            val value = HttpService.discoverApi.getRecommendColumn(offset, limit).await()
            val columnlist = arrayListOf<RecommendColumn>()
            columnlist.addAll(value?.results!!.toTypedArray())
            columnlist
        }
        return asyn

    }


}