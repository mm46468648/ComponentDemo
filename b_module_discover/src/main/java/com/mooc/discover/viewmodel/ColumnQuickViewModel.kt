package com.mooc.column.ui.columnall

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.discover.httpserver.HttpService
import com.mooc.discover.model.RecommendColumn
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class ColumnQuickViewModel : BaseListViewModel<RecommendColumn>() {

    var quickId: String = ""
    override fun getLimitStart(): Int {
        return 4
    }

    override suspend fun getData(): Deferred<ArrayList<RecommendColumn>?> {
        val asyn = viewModelScope.async {
            val value = HttpService.discoverApi.getQuickColumn(quickId, offset, limit).await()
            val columnlist = arrayListOf<RecommendColumn>()
            columnlist.addAll(value.data.results!!.toTypedArray())
            columnlist
        }
        return asyn

    }


}