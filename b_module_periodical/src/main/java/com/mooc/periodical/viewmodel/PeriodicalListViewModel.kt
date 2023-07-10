package com.mooc.periodical.viewmodel

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.search.PeriodicalBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.periodical.PeriodicalApi
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class PeriodicalListViewModel : BaseListViewModel<PeriodicalBean>() {

    var pId:String = ""
    var year : Int = -1
    var term:String = ""

    override suspend fun getData(): Deferred<List<PeriodicalBean>> {
        return viewModelScope.async {
            val bean = ApiService.getRetrofit().create(PeriodicalApi::class.java)
                .getPeriodicalList(pId,year,term,offset,limit).await()
            bean.data.results?: arrayListOf()
        }
    }
}