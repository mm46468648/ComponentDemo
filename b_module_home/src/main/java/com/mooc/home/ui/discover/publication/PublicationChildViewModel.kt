package com.mooc.home.ui.discover.publication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.commonbusiness.model.search.PublicationBean
import com.mooc.home.HttpService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class PublicationChildViewModel : BaseListViewModel<PublicationBean>() {

    var map: HashMap<String, String> = hashMapOf()
    var listCount = MutableLiveData<String?>()   //搜索列表数目
    var lastRequestPage = 1

    override suspend fun getData(): Deferred<List<PublicationBean>?> {
//        val page = (if (offset == 0) 1 else (offset / limit) + 1).toString()
//        map["page"] = page    //page,从1，开始，并且以此加1
        if (offset == 0) {
            lastRequestPage = 1
        }
        map["page"] = lastRequestPage.toString()
        map["page_size"] = limit.toString()
        return viewModelScope.async {
            val course = HttpService.homeApi.getSearchResult(map).kanwu
            if (course?.count != null) {
                lastRequestPage++
                listCount.postValue(course.count.toString())
            }
            course?.items
        }
    }


    override fun getLimitStart(): Int = 30

    override fun getOffSetStart(): Int = 0
}